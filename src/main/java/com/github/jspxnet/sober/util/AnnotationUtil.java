/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.util;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.enums.DocumentFormatType;
import com.github.jspxnet.io.jar.ClassScannerUtils;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.annotation.*;
import com.github.jspxnet.sober.config.SoberCalcUnique;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.config.SoberNexus;
import com.github.jspxnet.sober.config.SoberTable;
import com.github.jspxnet.sober.dialect.Dialect;
import com.github.jspxnet.sober.enums.DatabaseEnumType;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.model.container.PropertyContainer;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-5
 * Time: 23:49:08
 */
@Slf4j
public final class AnnotationUtil {

    private AnnotationUtil() {

    }


    /**
     * postgre 数据库seq修复
     *
     * @param soberTable     模型
     * @param jdbcOperations jdbc
     */
    public static void postgreSqlFixSeqId(TableModels soberTable, JdbcOperations jdbcOperations) {
        try {
            jdbcOperations.update(" SELECT setval('" + soberTable.getName() + "_id_seq', (SELECT MAX(id) FROM " + soberTable.getName() + ")+1)");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("注意：{}你的字段类型，关键字配置", soberTable.getName());
        }
    }

    public static void fixIdCacheMax(TableModels soberTable, Object object, JdbcOperations jdbcOperations) {
        if (jdbcOperations == null || object == null) {
            return;
        }

        Field field = ClassUtil.getDeclaredField(object.getClass(), soberTable.getPrimary());
        if (field == null) {
            return;
        }

        Id idf = field.getAnnotation(Id.class);
        if (idf == null || !idf.auto()) {
            return;
        }

        if (IDType.uuid.equals(idf.type())) {
            return;
        }

        Object maxId = jdbcOperations.getUniqueResult("SELECT max(" + soberTable.getPrimary() + ") FROM " + soberTable.getName());
        long value = 0;
        if (maxId instanceof Number) {
            value = ((Number) maxId).longValue();
        } else if (maxId instanceof String) {
            String strId = (String) maxId;
            if (idf.dateStart() && !StringUtil.isEmpty(idf.dateFormat()) && (strId.length() > idf.dateFormat().length())) {
                strId = strId.substring(idf.dateFormat().length());
            }
            if (StringUtil.isStandardNumber(strId)) {
                value = ObjectUtil.toLong(NumberUtil.getNumberStdFormat(strId));
            }
        } else {
            if (DatabaseEnumType.DM.equals(DatabaseEnumType.find(soberTable.getDatabaseName()))) {
                value = ObjectUtil.toLong(jdbcOperations.getUniqueResult("SELECT count(1) FROM " + soberTable.getDatabaseName() + StringUtil.DOT + soberTable.getName()));
            } else {
                value = ObjectUtil.toLong(jdbcOperations.getUniqueResult("SELECT count(1) FROM " + soberTable.getName()));
            }
        }
        SequenceFactory sequenceFactory = EnvFactory.getBeanFactory().getBean(SequenceFactory.class);
        sequenceFactory.fixCache(object.getClass().getName(), value);
    }

    /**
     * @param object         自动生成ID
     * @param fieldName      字段名称
     * @param jdbcOperations 系列化DAO对象
     *                       验证错误
     */
    public static void autoSetId(Object object, String fieldName, JdbcOperations jdbcOperations) {
        if (object == null || StringUtil.isNull(fieldName)) {
            return;
        }
        if (jdbcOperations == null) {
            log.error("jdbcOperations is null");
            return;
        }
        SequenceFactory sequenceFactory = EnvFactory.getBeanFactory().getBean(SequenceFactory.class);
        try {
            Field field = ClassUtil.getDeclaredField(object.getClass(), fieldName);
            if (field == null) {
                return;
            }

            Id idf = field.getAnnotation(Id.class);
            if (idf == null || !idf.auto()) {
                return;
            }

            Dialect dialect = jdbcOperations.getSoberFactory().getDialect();
            Object oldIdValue = BeanUtil.getProperty(object, field.getName());

            //长整类型
            if ((field.getType().equals(Long.class) || field.getType().equals(long.class)) && ObjectUtil.toInt(oldIdValue) == 0) {
                if (IDType.serial.equalsIgnoreCase(idf.type()) && !dialect.isSupportsGetGeneratedKeys()) {
                    //配置数据库来运行
                    //配置的序列方式，但数据库又不支持，这里就切换到数据库来自动生成，就是seq方式
                    BeanUtil.setFieldValue(object, field.getName(), StringUtil.toLong(sequenceFactory.getNextKey(object.getClass().getName(), idf, field.getType(), jdbcOperations)));
                } else if (IDType.seq.equalsIgnoreCase(idf.type())) {
                    //配置构架来生成
                    BeanUtil.setFieldValue(object, field.getName(), StringUtil.toLong(sequenceFactory.getNextKey(object.getClass().getName(), idf, field.getType(), jdbcOperations)));
                } else if (IDType.uuid.equalsIgnoreCase(idf.type())) {
                    BeanUtil.setFieldValue(object, field.getName(), RandomUtil.getRandomGUID(idf.length()));
                } else {
                    BeanUtil.setFieldValue(object, field.getName(), RandomUtil.getRandomGUID(idf.length()));
                }
            }

            //整数类型
            if ((field.getType().equals(Integer.class) || field.getType().equals(int.class)) && ObjectUtil.toInt(oldIdValue) == 0) {
                if (IDType.serial.equalsIgnoreCase(idf.type()) && !dialect.isSupportsGetGeneratedKeys()) {
                    //配置的序列方式，但数据库又不支持，这里就切换到数据库来自动生成，就是seq方式
                    BeanUtil.setFieldValue(object, field.getName(), StringUtil.toInt(sequenceFactory.getNextKey(object.getClass().getName(), idf, field.getType(), jdbcOperations)));
                } else if (IDType.seq.equalsIgnoreCase(idf.type())) {
                    BeanUtil.setFieldValue(object, field.getName(), StringUtil.toInt(sequenceFactory.getNextKey(object.getClass().getName(), idf, field.getType(), jdbcOperations)));
                } else {
                    BeanUtil.setFieldValue(object, field.getName(), RandomUtil.getRandomGUID(idf.length()));
                }
            }

            //字符串类型
            if (field.getType().equals(String.class) && StringUtil.isNull((String) oldIdValue)) {

                //字符串类型
                if (IDType.serial.equalsIgnoreCase(idf.type()) && !dialect.isSupportsGetGeneratedKeys()) {
                    //配置的序列方式，但数据库又不支持，这里就切换到数据库来自动生成，就是seq方式
                    BeanUtil.setFieldValue(object, field.getName(), sequenceFactory.getNextKey(object.getClass().getName(), idf, field.getType(), jdbcOperations));
                } else if (IDType.seq.equalsIgnoreCase(idf.type())) {
                    BeanUtil.setFieldValue(object, field.getName(), sequenceFactory.getNextKey(object.getClass().getName(), idf, field.getType(), jdbcOperations));
                } else { //uid
                    BeanUtil.setFieldValue(object, field.getName(), RandomUtil.getRandomGUID(idf.length()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param cls 类
     * @return 得到数据库字段关系
     */
    public static List<SoberColumn> getColumnList(Class<?> cls) {
        List<SoberColumn> soberColumns = new LinkedList<>();
        Field[] fields = ClassUtil.getDeclaredFields(cls);//字段
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                SoberColumn soberColumn = new SoberColumn();
                soberColumn.setName(field.getName());
                soberColumn.setClassType(field.getType());
                soberColumn.setCaption(column.caption());
                soberColumn.setLength(column.length());
                soberColumn.setDefaultValue(column.defaultValue());
                soberColumn.setNotNull(column.notNull());
                soberColumn.setDataType(column.dataType());
                soberColumn.setHidden(column.hidden());
                soberColumn.setInput(column.input());
                if (!NullClass.class.equals(column.enumType()))
                {
<<<<<<< HEAD
                    soberColumn.setOption(ObjectUtil.toString(column.enumType().getEnumConstants()));
=======
                    soberColumn.setOption(new JSONArray(column.enumType().getEnumConstants()).toString());
>>>>>>> dev
                } else {
                    soberColumn.setOption(column.option());
                }
                soberColumns.add(soberColumn);
                Table table = cls.getAnnotation(Table.class);
                if (field.getType() == String.class && column.length() < 1 && table != null && table.create()) {
                    log.error("class " + cls.getName() + " field " + field.getName() + " not column length,没有定义字段长度");
                }
                if (table != null) {
                    soberColumn.setTableName(table.name());
                }
            }
        }
        return soberColumns;
    }

    /**
     * @param cls 类
     * @return 得到映射关系
     */
    public static Map<String, SoberNexus> getSoberNexus(Class<?> cls) {
        Map<String, SoberNexus> soberColumns = new LinkedHashMap<>();
        Field[] fields = ClassUtil.getDeclaredFields(cls);//字段
        for (Field field : fields) {
            Nexus nexus = field.getAnnotation(Nexus.class);
            if (nexus != null) {
                SoberNexus soberNexus = new SoberNexus();
                soberNexus.setMapping(nexus.mapping());
                soberNexus.setField(nexus.field());
                soberNexus.setTargetField(nexus.targetField());
                soberNexus.setTargetEntity(nexus.targetEntity());
                soberNexus.setOrderBy(nexus.orderBy());
                soberNexus.setDelete(nexus.delete());
                soberNexus.setUpdate(nexus.update());
                soberNexus.setSave(nexus.save());
                soberNexus.setChain(nexus.chain());
                soberNexus.setWhere(nexus.where());
                soberNexus.setTerm(nexus.term());
                soberNexus.setLength(nexus.length());
                soberColumns.put(field.getName(), soberNexus);
            }
        }
        return soberColumns;
    }


    /**
     * @param cls 类
     * @return 得到映射关系
     */
    public static Map<String, SoberCalcUnique> getSoberCalcUnique(Class<?> cls) {
        Map<String, SoberCalcUnique> soberCalcUniques = new LinkedHashMap<>();
        Field[] fields = ClassUtil.getDeclaredFields(cls);//字段
        for (Field field : fields) {
            CalcUnique calcUnique = field.getAnnotation(CalcUnique.class);
            if (calcUnique != null) {
                SoberCalcUnique soberCalcUnique = new SoberCalcUnique();
                soberCalcUnique.setName(field.getName());
                soberCalcUnique.setCaption(calcUnique.caption());
                soberCalcUnique.setSql(calcUnique.sql());
                soberCalcUnique.setValue(calcUnique.value());
                soberCalcUnique.setEntity(calcUnique.entity());
                soberCalcUniques.put(field.getName(), soberCalcUnique);
            }
        }
        return soberCalcUniques;
    }

    /**
     * 得到映射关系中的表名
     *
     * @param cls 类
     * @return 表名
     */
    public static Table getTable(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        Annotation[] annotation = cls.getAnnotations();
        if (ObjectUtil.isEmpty(annotation)) {
            return null;
        }
        for (Annotation a : annotation) {
            if (a instanceof Table) {
                return (Table) a;
            }
        }
        return null;
    }
    /**
     * @param cls 类
     * @return 得到的显示名称
     */
    public static String getTableCaption(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        Annotation[] annotation = cls.getAnnotations();
        if (ObjectUtil.isEmpty(annotation)) {
            return null;
        }
        for (Annotation anAnnotation : annotation) {
            if (anAnnotation instanceof Table) {
                return ((Table) anAnnotation).caption();
            }
        }
        return StringUtil.empty;
    }

    /**
     * @param cls class对象
     * @return 得到数据库的名
     */
    public static String getTableName(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        Annotation[] annotation = cls.getAnnotations();
        if (ObjectUtil.isEmpty(annotation)) {
            return null;
        }
        for (Annotation anAnnotation : annotation) {
            if (anAnnotation instanceof Table) {
                return ((Table) anAnnotation).name();
            }
        }
        return StringUtil.empty;
    }


    /**
     *
     * @param cls 实体对象
     * @param extend  0:所有;1:可扩展;2:不可扩展
     * @return 生成 SoberTable
     */
    public static SoberTable getSoberTable(Class<?> cls,int extend) {
        if (cls == null) {
            return null;
        }
        SoberTable soberTable = new SoberTable();
        soberTable.setEntity(cls);
        Table table = getTable(cls);
        if (table == null) {
            return null;
        }
        soberTable.setName(table.name());  //得到数据库表名
        soberTable.setTableCaption(table.caption());//表的别名
        soberTable.setUseCache(table.cache()); //是否使用cache 默认使用
        soberTable.setCreate(table.create());
        soberTable.setColumns(getColumnList(cls)); //数据库字段
        soberTable.setCalcUniqueMap(getSoberCalcUnique(cls)); //单个计算
        soberTable.setNexusMap(getSoberNexus(cls)); //映射关系
        Field[] fields = ClassUtil.getDeclaredFields(cls);//字段
        //找到ID
        for (Field field : fields) {
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                soberTable.setPrimary(field.getName());
                soberTable.setAutoId(id.auto());
                soberTable.setIdType(id.type());
            }
        }
        soberTable.setCanExtend(PropertyContainer.class.isAssignableFrom(cls));
        if (extend==0)
        {
            return soberTable;
        }
        if (extend==1 && soberTable.isCanExtend())
        {
            return soberTable;
        }
        if (extend==2 && !soberTable.isCanExtend())
        {
            return soberTable;
        }
        return null;
    }

    static public String getNexusOrderBy(Object obj, String orderBy) {
        if (obj == null || orderBy == null) {
            return StringUtil.empty;
        }
        String[] orderByVar = StringUtil.getFreeMarkerVar(orderBy);
        if (!ArrayUtil.isEmpty(orderByVar)) {
            Map<String, Object> valueMap = new HashMap<>();
            for (String varName : orderByVar) {
                valueMap.put(varName, BeanUtil.getProperty(obj, varName));
            }
            orderBy = EnvFactory.getPlaceholder().processTemplate(valueMap, orderBy);
        }
        return orderBy;
    }

    static public String getNexusTerm(Object obj, String term) {
        if (obj == null || term == null) {
            return StringUtil.empty;
        }
        String[] termVar = StringUtil.getFreeMarkerVar(term);
        if (!ArrayUtil.isEmpty(termVar)) {
            Map<String, Object> valueMap = new HashMap<>();
            for (String varName : termVar) {
                valueMap.put(varName, BeanUtil.getProperty(obj, varName));
            }
            term = EnvFactory.getPlaceholder().processTemplate(valueMap, term);
        }
        return term;
    }

    static public int getNexusLength(Object obj, String length, int defaultLength) {
        if (obj == null || StringUtil.isNull(length)) {
            return defaultLength;
        }
        String[] lengthVar = StringUtil.getFreeMarkerVar(length);
        if (!ArrayUtil.isEmpty(lengthVar)) {
            Map<String, Object> valueMap = new HashMap<>();
            for (String varName : lengthVar) {
                valueMap.put(varName, BeanUtil.getProperty(obj, varName));
            }
            length = EnvFactory.getPlaceholder().processTemplate(valueMap, length);
        }
        int len = StringUtil.toInt(length);
        return len <= 0 ? defaultLength : len;
    }

    /**
     * 得到有Table的class列表
     *
     * @param classPath class路径
     * @return 得到class列表
     * @throws Exception 异常
     */
    static public List<Class<?>> getTableAnnotationClassList(String classPath) throws Exception {
        List<Class<?>> result = new ArrayList<>();
        String[] classList = FileUtil.scanClass(classPath);
        for (String name : classList) {
            Class<?> cls = ClassUtil.loadClass(name);
            Table table = cls.getAnnotation(Table.class);
            if (table != null && table.create()) {
                result.add(cls);
            }
        }


        Set<Class<?>> list = ClassScannerUtils.searchClasses(classPath, EnvFactory.getBaseConfiguration().getDefaultPath());
        for (Class<?> cls : list) {
            if (cls == null) {
                continue;
            }
            Table table = cls.getAnnotation(Table.class);
            if (table != null && table.create() && !result.contains(cls)) {
                result.add(cls);
            }
        }
        return result;
    }

    /**
     *
     * @param cla 查询过滤出枚举类型列表
     * @return 得到枚举列表,包括选项
     */
    public static Map<String,JSONArray> getEnumType(Class<?> cla)
    {
        Map<String,JSONArray> result = new HashMap<>();
        Field[] fields = ClassUtil.getDeclaredFields(cla);
        for (Field field:fields)
        {
            Column column = field.getAnnotation(Column.class);
            if (column==null)
            {
                continue;
            }
            if (StringUtil.isNull(column.option()) && column.enumType()==NullClass.class)
            {
                continue;
            }
            //1:小;2:中;3:大
            if (!NullClass.class.getName().equals(column.enumType().getName()))
            {
                Object[] enumObj = column.enumType().getEnumConstants();
                if (!result.containsKey(column.enumType().getSimpleName()))
                {
                    result.put(StringUtil.uncapitalize(column.enumType().getSimpleName()),new JSONArray(enumObj));
                }
            }
            else if (!StringUtil.isNull(column.option()))
            {
                List<JSONObject> temp = new ArrayList<>();
                String option = column.option();
                if (StringUtil.isJsonArray(option))
                {
                    JSONArray array = new JSONArray(option);
                    for (int i=0;i<array.length();i++)
                    {
                        JSONObject obj = array.getJSONObject(i);
                        temp.add(obj);
                    }
                } else
                {
                    StringMap<String,String> stringMap = new StringMap<>();
                    stringMap.setKeySplit(StringUtil.COLON);
                    stringMap.setLineSplit(StringUtil.EQUAL);
                    stringMap.setString(column.option());
                    for (String key:stringMap.keySet())
                    {
                        JSONObject json = new JSONObject();
                        json.put("value",key);
                        json.put("name",stringMap.getString(key));
                        temp.add(json);
                    }
                }
                result.put(StringUtil.uncapitalize(field.getName())+"EnumType",new JSONArray(temp));
            }
        }
        return result;
    }


}