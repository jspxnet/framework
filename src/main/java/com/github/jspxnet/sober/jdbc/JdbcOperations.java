/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.jdbc;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.Placeholder;
import com.github.jspxnet.cache.DefaultCache;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.scriptmark.ScriptRunner;
import com.github.jspxnet.scriptmark.core.script.TemplateScriptEngine;
import com.github.jspxnet.sober.*;
import com.github.jspxnet.sober.config.SoberCalcUnique;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.config.SoberNexus;
import com.github.jspxnet.sober.config.SoberTable;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.projection.Projections;
import com.github.jspxnet.sober.dialect.Dialect;
import com.github.jspxnet.sober.dialect.OracleDialect;
import com.github.jspxnet.sober.enums.DatabaseEnumType;
import com.github.jspxnet.sober.enums.MappingType;
import com.github.jspxnet.sober.exception.ValidException;
import com.github.jspxnet.sober.impl.CriteriaImpl;
import com.github.jspxnet.sober.impl.SqlMapBaseImpl;
import com.github.jspxnet.sober.impl.SqlMapClientImpl;
import com.github.jspxnet.sober.ssql.SSqlExpression;
import com.github.jspxnet.sober.util.AnnotationUtil;
import com.github.jspxnet.sober.util.JdbcUtil;
import com.github.jspxnet.sober.util.SoberUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-6
 * Time: 15:19:38
 * JDBC 数据库操作
 * date: 2019-9-10 数据尽量采用泛型方式
 */
@Slf4j
public abstract class JdbcOperations implements SoberSupport {
    private Dialect dialect = null;
    private SqlMapClient sqlMapClient = null;
    private SqlMapBase sqlMapBase = null;
    private SoberFactory soberFactory;

    /**
     * 简化自动生成一个sql 执行ID 名称,
     * 名称格式为 className.MethodName
     * @return 得到当前执行的方法名称
     */
    @Override
    public String getClassMethodName() {
        StackTraceElement[] stackTraceElementArray = Thread.currentThread().getStackTrace();
        StackTraceElement stackTraceElement = stackTraceElementArray[2];
        if (stackTraceElement == null) {
            return null;
        }
        String className = stackTraceElement.getClassName();
        if (StringUtil.isEmpty(className)) {
            return null;
        }
        className = ClassUtil.getClassName(className);
        for (StackTraceElement stackTrace :stackTraceElementArray)
        {
            if (stackTrace.getClassName().equals(className))
            {
                stackTraceElement = stackTrace;
            }
        }
        Class<?> cls;
        try {
            cls = ClassUtil.loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.error(className + " not found", e);
            return null;
        }
        Class<?> iClass = ClassUtil.getImplements(cls);
        if (iClass == null) {
            iClass = cls;
        }
        return iClass.getName() + StringUtil.DOT + stackTraceElement.getMethodName();
    }

    public JdbcOperations() {

    }

    /**
     *
     * @return 默认的最大返回行数
     */
    @Override
    public int getMaxRows() {
        return this.soberFactory.getMaxRows();
    }

    /**
     *
     * @return 得到数据工程对象
     */
    @Override
    public SoberFactory getSoberFactory() {
        return soberFactory;
    }

    /**
     *
     * @param soberFactory 数据工厂
     */
    @Override
    public void setSoberFactory(SoberFactory soberFactory) {
        this.soberFactory = soberFactory;
        dialect = this.soberFactory.getDialect();
    }

    /**
     *
     * @param cla 类
     * @return 表结构模型
     */
    @Override
    public TableModels getSoberTable(Class<?> cla) {
        return soberFactory.getTableModels(cla, this);
    }


    /**
     *
     * @param dto 是否包含DTO
     * @return 得到所有表结构的模型
     */
    @Override
    public Map<String,TableModels> getAllTableModels(boolean dto)
    {
        String cacheKey = Environment.KEY_SOBER_TABLE_CACHE + "_"+ObjectUtil.toInt(dto);
        Map<String,TableModels>  result = (Map<String,TableModels>)JSCacheManager.get(DefaultCache.class,cacheKey);
        if (!ObjectUtil.isEmpty(result))
        {
            return result;
        }
        result = new HashMap<>();
        List<SoberTable> list = SoberUtil.getScanTableAnnotationList(dto);
        for (SoberTable table:list)
        {
            result.put(table.getId(),table);
        }
        JSCacheManager.put(DefaultCache.class,cacheKey,result);
        return result;
    }
    /**
     *
     * @param cla 类对象
     * @return 表明
     */
    @Override
    public String getTableName(Class<?> cla) {
        TableModels tableModels = soberFactory.getTableModels(cla, this);
        if (tableModels!=null)
        {
            return tableModels.getName();
        }
        return AnnotationUtil.getTableName(cla);
    }

    /**
     * @param cla   类对象
     * @param field 字段名称
     * @return 判断是否存在此字段
     */
    @Override
    public boolean containsField(Class<?> cla, String field) {
        if (!StringUtil.hasLength(field) || cla == null) {
            return false;
        }
        TableModels tableModels = getSoberTable(cla);
        return tableModels.containsField(field);
    }

    /**
     *
     * @param cla 类对象
     * @return 将表头数据返回给前端
     */
    @Override
    public List<SoberColumn> getColumnModels(Class<?> cla)  {
        TableModels soberTable = getSoberTable(cla);
        List<SoberColumn> list = new ArrayList<>();
        String[] fieldArray = soberTable.getFieldArray();
        for (String field : fieldArray) {
            SoberColumn soberColumn = soberTable.getColumn(field);
            list.add(BeanUtil.copy(soberColumn,SoberColumn.class));
        }
        return list;
    }


    /**
     * 只为方便扩展使用
     *
     * @param type 连接类型
     * @return 返回一个连接
     */
    public Connection getConnection(final int type)  {
        String  transactionId = soberFactory.getTransactionId();
        try {
            return soberFactory.getConnection(type,transactionId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            log.error("数据库连接创建失败",throwables);
        }
        return null;
    }

    /**
     * 设置字段数据(无映射关系)
     *
     * @param tClass    类型
     * @param resultSet jdbc数据集合
     * @param <T>       类型
     * @return 载入对应, 一次一个对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T loadColumnsValue(Class<T> tClass, ResultSet resultSet) throws Exception {
        T result;
        if (Map.class.isAssignableFrom(tClass)||HashMap.class.isAssignableFrom(tClass)||List.class.isAssignableFrom(tClass))
        {
            ResultSetMetaData metaData = resultSet.getMetaData();
            Map<String, Object> beanMap = new HashMap<>();
            for (int n = 1; n <= metaData.getColumnCount(); n++) {
                String field = StringUtil.underlineToCamel(metaData.getColumnLabel(n));
                Object value = dialect.getResultSetValue(resultSet, n);
                beanMap.put(field, value);
            }
            result = (T)beanMap;
        } else
        {
            TableModels soberTable = getSoberTable(tClass);
            result = tClass.newInstance();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int count = metaData.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String dbFiled = metaData.getColumnLabel(i);
                SoberColumn soberColumn = soberTable.getColumn(dbFiled);
                if (soberColumn != null) {
                    Object obj = dialect.getResultSetValue(resultSet, i);
                    BeanUtil.setFieldValue(result, soberColumn.getName(), obj);
                } else if (ClassUtil.getDeclaredField(result.getClass(), dbFiled) != null) {
                    BeanUtil.setFieldValue(result, dbFiled, dialect.getResultSetValue(resultSet, i));
                }
            }
        }
        if (String.class.isAssignableFrom(tClass))
        {
            return (T)ObjectUtil.getJson(result);
        }
        return result;
    }

    /**
     *  计算合计,这个标签会占用大量的CPU计算资源，谨慎使用
     * @param soberTable 结果关系表
     * @param inObj 对象
     * @return 计算结果
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object calcUnique(TableModels soberTable, Object inObj)  {
        if (inObj == null || soberTable == null) {
            return inObj;
        }
        Map<String, SoberCalcUnique> calcUniqueMap = soberTable.getCalcUniqueMap();
        if (ObjectUtil.isEmpty(calcUniqueMap))
        {
            return inObj;
        }
        Object obj= inObj;
        if (inObj instanceof String)
        {
            obj = new JSONObject((String)inObj).parseObject(soberTable.getEntity());
        }
        ////////////////////////////CalcUnique

        for (String colName : calcUniqueMap.keySet()) {
            SoberCalcUnique soberCalcUnique = calcUniqueMap.get(colName);
            Map<String, Object> valueMap = new HashMap<>();
            Class<?>[] classArray = soberCalcUnique.getEntity();
            if (classArray != null) {
                for (Class<?> aClassArray : classArray) {

                    String tableName = StringUtil.uncapitalize(aClassArray.getSimpleName());
                    if (!StringUtil.hasLength(tableName)) {
                        tableName = aClassArray.getName();
                    }
                    valueMap.put(tableName, getTableName(aClassArray));
                }
            }
            String sqlText = dialect.processSql(soberCalcUnique.getSql(), valueMap);
            try {
                Object[] param = null;
                if (!ArrayUtil.isEmpty(soberCalcUnique.getValue())) {
                    for (String key : soberCalcUnique.getValue()) {
                        param = ArrayUtil.add(param, BeanUtil.getProperty(obj, key));
                    }
                }
                if (obj instanceof Map)
                {
                    Map<String,Object> temp = (Map<String,Object>)obj;
                    temp.put(colName,getUniqueResult(sqlText, param));
                } else
                {
                    BeanUtil.setSimpleProperty(obj, colName, getUniqueResult(sqlText, param));
                }
            } catch (Exception e) {
                log.error(soberTable.getName() + ":" + sqlText, e);
                e.printStackTrace();
            }
        }
        ////////////////////////////
        if (inObj instanceof String)
        {
            return new JSONObject(obj).toString();
        }
        return obj;
    }

    /**
     * 载入关联列表
     *
     * @param cla  类
     * @param list 对象实体列表
     */
    @Override
    public void loadNexusList(Class<?> cla, List<?> list) {
        loadNexusList(getSoberTable(cla), list);
    }

    /**
     * 载入映射对象
     *
     * @param soberTable mapping
     * @param list       list
     */
    @Override
    public void loadNexusList(TableModels soberTable, List<?> list) {
        if (ObjectUtil.isEmpty(list))
        {
            return;
        }
        Map<String, SoberNexus> nexus = soberTable.getNexusMap();
        Placeholder placeholder = EnvFactory.getPlaceholder();
        for (String colName : nexus.keySet()) {
            SoberNexus soberNexus = nexus.get(colName);
            if ((MappingType.OneToOne.equalsIgnoreCase(soberNexus.getMapping()) || MappingType.ManyToOne.equalsIgnoreCase(soberNexus.getMapping()))
                    && (StringUtil.isNull(soberNexus.getWhere())|| !StringUtil.isNull(soberNexus.getWhere())
                    && ObjectUtil.toBoolean(placeholder.processTemplate(ObjectUtil.getMap(list.get(0)), soberNexus.getWhere()))))
            {
                List<Object> idList = BeanUtil.copyFieldList(list,soberNexus.getField());
                Criteria criteria = createCriteria(soberNexus.getTargetEntity());
                criteria = criteria.add(Expression.in(soberNexus.getTargetField(), idList));
                if (!StringUtil.isNull(soberNexus.getTerm())) {
                    String term = soberNexus.getTerm();
                    if (term.contains("${") && term.contains("}"))
                    {
                        term = placeholder.processTemplate(ObjectUtil.getMap(list.get(0)), term);
                    }
                    criteria = SSqlExpression.getTermExpression(criteria, term);
                }
                criteria = criteria.setCurrentPage(1).setTotalCount(idList.size());
                List<Object> loadObjectList = criteria.list(soberNexus.isChain());
                if (ObjectUtil.isEmpty(loadObjectList))
                {
                    continue;
                }
                for (Object obj:list)
                {
                    Object objField = BeanUtil.getProperty(obj,soberNexus.getField());
                    if (objField==null)
                    {
                        continue;
                    }
                    for (Object loadObj:loadObjectList)
                    {
                        if (objField.equals(BeanUtil.getProperty(loadObj,soberNexus.getTargetField())))
                        {
                            BeanUtil.setFieldValue(obj,colName,loadObj);
                        }
                    }
                }
            }
            else if (MappingType.OneToMany.equalsIgnoreCase(soberNexus.getMapping())) {
                List<Object> idList = BeanUtil.copyFieldList(list,soberNexus.getField());
                Criteria criteria = createCriteria(soberNexus.getTargetEntity());
                criteria = criteria.add(Expression.in(soberNexus.getTargetField(), idList));
                if (!StringUtil.isNull(soberNexus.getTerm())) {
                    criteria = SSqlExpression.getTermExpression(criteria, soberNexus.getTerm());
                }
                criteria = criteria.setCurrentPage(1).setTotalCount(getMaxRows());
                List<Object> loadObjectList = criteria.list(soberNexus.isChain());
                for (Object object : list) {
                    if (object==null)
                    {
                        continue;
                    }
                    //对应id对象
                    Object objField = BeanUtil.getFieldValue(object, soberNexus.getField(),false);
                    if (objField==null)
                    {
                        continue;
                    }
                    List<Object> valueLstCache = new ArrayList<>();
                    for (Object loadObj:loadObjectList)
                    {
                        if (loadObj==null)
                        {
                            continue;
                        }
                        Object keyField = BeanUtil.getFieldValue(loadObj,soberNexus.getTargetField(),false);
                        if (objField.equals(keyField))
                        {
                            valueLstCache.add(loadObj);
                        }
                    }
                    BeanUtil.setSimpleProperty(object, colName, valueLstCache);
                }

            }
        }

    }
    /**
     * @param soberTable 结构
     * @param result     对象
     */
    @Override
    public void loadNexusValue(TableModels soberTable, Object result) {
        if (result == null) {
            return;
        }
        try {
            Map<String, SoberNexus> nexus = soberTable.getNexusMap();
            for (String colName : nexus.keySet()) {
                SoberNexus soberNexus = nexus.get(colName);
                Placeholder placeholder = EnvFactory.getPlaceholder();
                if ((MappingType.OneToOne.equalsIgnoreCase(soberNexus.getMapping()) || MappingType.ManyToOne.equalsIgnoreCase(soberNexus.getMapping()))
                        && (StringUtil.isNull(soberNexus.getWhere())|| !StringUtil.isNull(soberNexus.getWhere()) && ObjectUtil.toBoolean(placeholder.processTemplate(ObjectUtil.getMap(result), soberNexus.getWhere())))) {

                    Object findValue = BeanUtil.getProperty(result, soberNexus.getField());
                    String term = AnnotationUtil.getNexusTerm(result, soberNexus.getTerm());
                    TableModels targetModels = getSoberTable(soberNexus.getTargetEntity());
                    SoberColumn soberColumn = targetModels.getColumn(soberNexus.getTargetField());
                    if (soberColumn == null) {
                        continue;
                    }
                    Class<?> classType = soberColumn.getClassType();
                    //id 为 0 的时候不查询
                    if ((classType == Long.class || classType == long.class || classType == Integer.class || classType == int.class) && ObjectUtil.toInt(findValue) == 0) {
                        continue;
                    }
                    List<?> childList =  getFindFieldList(soberNexus.getTargetEntity(), soberNexus.getTargetField(), (Serializable) findValue, term, AnnotationUtil.getNexusOrderBy(result, soberNexus.getOrderBy()), false, 1);
                    if (!childList.isEmpty()) {
                        Object chainObj = childList.get(0);
                        if (soberNexus.isChain() && chainObj != null) {
                            TableModels cSoberTable = getSoberTable(chainObj.getClass());
                            loadNexusValue(cSoberTable, chainObj);
                        }
                        BeanUtil.setSimpleProperty(result, colName, chainObj);
                    }
                } else if (MappingType.OneToMany.equalsIgnoreCase(soberNexus.getMapping())) {
                    //对应id对象
                    Object findValue = BeanUtil.getProperty(result, soberNexus.getField());
                    //条件
                    String term = AnnotationUtil.getNexusTerm(result, soberNexus.getTerm());
                    //数据个数
                    int length = AnnotationUtil.getNexusLength(result, soberNexus.getLength(), getMaxRows());
                    //查询得到列表
                    List<?> childList = getFindFieldList(soberNexus.getTargetEntity(), soberNexus.getTargetField(), (Serializable) findValue, term, AnnotationUtil.getNexusOrderBy(result, soberNexus.getOrderBy()), soberNexus.isChain(), length);
                    BeanUtil.setSimpleProperty(result, colName, childList);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("载入关系表错误" + soberTable.toString() + "对象" + result, e);
        }
    }


    /**
     * 设置参数
     *
     * @param statement jdbc查询器
     * @param fields    查询字段
     * @param object    查询对象
     * @throws Exception 其他错误
     */
    private void setPreparedStatementValueList(PreparedStatement statement, String[] fields, Object object) throws Exception {
        for (int i = 0; i < fields.length; i++) {
            debugPrint("SetPrepared[" + (i + 1) + "]=" + BeanUtil.getProperty(object, fields[i]));
            dialect.setPreparedStatementValue(statement, i + 1, BeanUtil.getProperty(object, fields[i]));
        }
    }

    /**
     * @param aClass       类
     * @param serializable 字段值
     * @param <T>          类对象
     * @return 返回对象，如果为空就创建对象，不会有null 返回
     */
    @Override
    public <T> T load(Class<T> aClass, Serializable serializable) {
        return load(aClass, null, serializable, false);
    }

    /**
     * @param aClass       类
     * @param <T> 类型
     * @param serializable 字段值
     * @param loadChild    是否载入关联
     * @return 返回对象，如果为空就创建对象，不会有null 返回
     */
    @Override
    public <T> T load(Class<T> aClass, Serializable serializable, boolean loadChild) {
        return load(aClass, null, serializable, loadChild);
    }

    /**
     * @param aClass       类
     * @param <T> 类型
     * @param field        字段名称
     * @param serializable 字段值
     * @param loadChild    是否载入关联
     * @return 返回对象，如果为空就创建对象，不会有null 返回
     */
    @Override
    public <T> T load(Class<T> aClass, Serializable field, Serializable serializable, boolean loadChild) {
        return load(aClass, field, serializable, loadChild, true);
    }
    /**
     * 查询字段返回一个对象,从缓存中起，查询后放入缓存
     * 如果为空，如果为空就创建对象,返回永远不为空
     *
     * @param aClass       类
     * @param <T> 类型
     * @param field        字段
     * @param serializable 字段值
     * @return Object 得到对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T load(Class<T> aClass, Serializable field, Serializable serializable, boolean loadChild, boolean loadUseCache)
    {
        if (aClass==null)
        {
            return null;
        }
        if ((serializable == null || field == null && (ClassUtil.isNumberType(serializable.getClass())
                && ObjectUtil.toLong(serializable) == 0)) || ObjectUtil.isEmpty(serializable)) {
            try {
                return aClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        //安全防范，如果有  or and where 这种关键字的，直接返回 空
        TableModels soberTable = getSoberTable(aClass);
        if (field == null) {
            field = soberTable.getPrimary();
        }

        if (!StringUtil.hasLength((String) field)) {
            log.error(aClass + " SQL Primary is NULL,配置表没设置ID");
            return null;
        }

        T result;
        //取出cache
        String cacheKey = null;
        boolean useCache = loadUseCache && soberFactory.isUseCache() && soberTable.isUseCache();
        if (useCache) {
            cacheKey = SoberUtil.getLoadKey(aClass, field, serializable, loadChild);
            result = (T) JSCacheManager.get(aClass, cacheKey);
            if (!ObjectUtil.isEmpty(result) && aClass.equals(result.getClass())) {
                return result;
            }
        }

        result = get(aClass,  field,  serializable,  loadChild);
        //放入cache
        if (useCache&&result!=null) {
            JSCacheManager.put(aClass, cacheKey, result);
        }

        if (result == null) {
            try {
                return aClass.newInstance();
            } catch (Exception e) {
                log.error(aClass + " newInstance error", e);
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * load from id
     *
     * @param aClass       class 类对象
     * @param <T> 类型
     * @param serializable id
     * @return query a object 返回对象
     */
    @Override
    public <T> T get(Class<T> aClass, Serializable serializable) {
        return get(aClass, null,serializable, false);
    }

    /**
     * ID 得到对象
     * load from id and map bean
     *
     * @param aClass       类
     * @param <T> 类型
     * @param serializable Id
     * @return Object 得到对象
     */
    @Override
    public <T> T get(Class<T> aClass, Serializable serializable, boolean loadChild) {

        return get(aClass, null, serializable, loadChild);
    }

    /**
     * 查询字段返回一个对象,不从缓存中起，但是查询后放入换成
     * 如果为空，就返回空，不创建对象，load方式会是用缓存来减少查询，也会创建null对象返回
     *
     * @param aClass       类
     * @param <T> 类型
     * @param field        字段
     * @param serializable 字段值
     * @return Object 得到对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> aClass, Serializable field, Serializable serializable, boolean loadChild)
    {
        TableModels soberTable = getSoberTable(aClass);
        if (field == null) {
            field = soberTable.getPrimary();
        }

        if (!StringUtil.hasLength((String) field)) {
            log.error(aClass + " SQL Primary is NULL,配置表没设置ID");
            return null;
        }

        T result = null;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_FIELD_NAME, field);

        String sqlText = StringUtil.empty;
        try {
            conn = getConnection(SoberEnv.READ_ONLY);
            if (conn == null) {
                String info = "得到链接为空，数据库连接池不能正常连接,Connection is null";
                SQLException e = new SQLException(info);
                log.error(info, e);
                throw e;
            }
            sqlText = dialect.processTemplate(Dialect.SQL_QUERY_ONE_FIELD, valueMap);
            debugPrint(sqlText);
            if (!dialect.supportsConcurReadOnly()) {
                preparedStatement = conn.prepareStatement(sqlText);
            } else {
                preparedStatement = conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }
            preparedStatement.setMaxRows(1);
            debugPrint("prepared[1]=" + serializable);
            dialect.setPreparedStatementValue(preparedStatement, 1, serializable);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = loadColumnsValue(aClass, resultSet);
                //载入映射对象
                if (loadChild) {
                    loadNexusValue(soberTable, result);
                }
                //载入计算数据
                result = (T)calcUnique(soberTable, result);
            }
        } catch (Exception e) {
            log.error("sql:" + sqlText, e);
            throw new IllegalArgumentException("sql:" + sqlText);
        } finally {
            JdbcUtil.closeResultSet(resultSet);
            JdbcUtil.closeStatement(preparedStatement);
            JdbcUtil.closeConnection(conn);
            valueMap.clear();
        }
        return result;
    }

    /**
     * @param aClass       返回实体
     * @param <T> 类型
     * @param field        查询字段
     * @param serializable 字段值
     * @param term         条件
     * @param orderBy      排序
     * @param loadChild    是否载入映射
     * @param max          最大行数
     * @return 查询返回
     */
    private <T> List<T> getFindFieldList(Class<T> aClass, String field, Serializable serializable, String term, String orderBy, boolean loadChild, int max) {
        Criteria criteria = createCriteria(aClass);
        criteria = criteria.add(Expression.eq(field, serializable));
        if (!StringUtil.isNull(term)) {
            criteria = SSqlExpression.getTermExpression(criteria, term);
        }
        if (!StringUtil.isNull(orderBy)) {
            criteria = SSqlExpression.getSortOrder(criteria, orderBy);
        }
        criteria = criteria.setCurrentPage(1).setTotalCount(max);
        return criteria.list(loadChild);
    }

    /**
     *
     * @param aClass 返回实体
     * @param serializables 字段值
     * @param <T> 类型
     * @return 查询返回
     */
    @Override
    public <T> List<T> load(Class<T> aClass, Serializable[] serializables)
    {
        TableModels soberTable = getSoberTable(aClass);
        String field = soberTable.getPrimary();
        return load(aClass,  field, serializables,  true,  true);
    }

    /**
     *
     * @param aClass 返回实体
     * @param values 字段值
     * @param loadChild 是否载入映射
     * @param <T> 类型
     * @return 返回列表
     */
    @Override
    public <T> List<T> load(Class<T> aClass, Collection<?> values, boolean loadChild) {
        //载入一个ID列表
        TableModels soberTable = getSoberTable(aClass);
        String field = soberTable.getPrimary();
        Criteria criteria = createCriteria(aClass);
        criteria = criteria.add(Expression.in(field, values));
        criteria = criteria.setCurrentPage(1).setTotalCount(getMaxRows());
        return criteria.list(loadChild);
    }

    /**
     *
     * @param aClass 返回实体
     * @param field 查询字段
     * @param values 字段值
     * @param loadChild 是否载入映射
     * @param <T> 类型
     * @return 返回列表
     */
    @Override
    public <T> List<T> load(Class<T> aClass, String field, Collection<?> values, boolean loadChild) {
        //载入一个ID列表
        Criteria criteria = createCriteria(aClass);
        criteria = criteria.add(Expression.in(field, values));
        criteria = criteria.setCurrentPage(1).setTotalCount(getMaxRows());
        return criteria.list(loadChild);
    }
    /**
     *
     * @param aClass 返回实体
     * @param field 查询字段
     * @param serializables 字段值
     * @param loadChild 是否载入映射
     * @param loadUseCache 载入换成
     * @param <T> 类型
     * @return 查询返回
     */
    @Deprecated
    @Override
    public <T> List<T> load(Class<T> aClass, String field, Serializable[] serializables, boolean loadChild, boolean loadUseCache)
    {
        //载入一个ID列表
        Criteria criteria = createCriteria(aClass);
        criteria = criteria.add(Expression.in(field, serializables));
        criteria = criteria.setCurrentPage(1).setTotalCount(getMaxRows());
        return criteria.list(loadChild);
    }

    /**
     *
     * @param object 实体对象
     * @return 保存对象
     * @throws Exception 异常
     */
    @Override
    public int save(Object object) throws Exception {
        return save(object, false);
    }

    /**
     * @param object 保存对象
     * @param child  保持子对象
     * @return 保存一个对象
     * @throws Exception 异常
     */
    @Override
    public int save(Object object, final boolean child) throws Exception {
        if (object == null) {
            return -2;
        }
        if (object instanceof Collection) {
            return save((Collection<?>) object);
        }
        //////////配置验证才能够保存 begin
        if (soberFactory.isValid()) {
            validator(object);
        }
        //////////配置验证才能够保存 end
        TableModels soberTable = getSoberTable(object.getClass());
        if (soberTable==null)
        {
            log.error("@Table 标签没有配置:{}",object.getClass());
            throw  new Exception("@Table 标签没有配置");
        }

        Object idValue = BeanUtil.getFieldValue(object, soberTable.getPrimary(),false);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());

        String sqlText = StringUtil.empty;
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            //判断是否支持自动生成ID，如果支持，字段中就不包含id字段，否则包含id字段,先自动生成ID
            String[] fieldArray;
            if (!soberTable.isAutoId() && dialect.isSupportsSavePoints() && (idValue == null || 0 == ObjectUtil.toLong(idValue))) {
                fieldArray = soberTable.getFieldArray();
            } else {
                //不用支持ID字段，就自动生成ID字段
                AnnotationUtil.autoSetId(object, soberTable.getPrimary(), this);
                fieldArray = soberTable.getFullFieldArray();
            }
            if (fieldArray == null || fieldArray.length < 1) {
                return -2;
            }

            valueMap.put(Dialect.KEY_FIELD_LIST, fieldArray);
            valueMap.put(Dialect.KEY_FIELD_COUNT, fieldArray.length);
            conn = getConnection(SoberEnv.READ_WRITE);
            sqlText = dialect.processTemplate(Dialect.SQL_INSERT, valueMap);
            if (StringUtil.isNull(sqlText)) {
                //不破坏连接属性
                return -2;
            }
            if (!soberTable.isAutoId() && dialect.isSupportsSavePoints() && soberTable.isSerial()) {
                statement = conn.prepareStatement(sqlText, Statement.RETURN_GENERATED_KEYS);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            }
            debugPrint(sqlText);
            for (int i = 0; i < fieldArray.length; i++) {
                debugPrint("prepared[" + (i + 1) + "]=" + BeanUtil.getProperty(object, fieldArray[i]));
                dialect.setPreparedStatementValue(statement, i + 1, BeanUtil.getProperty(object, fieldArray[i]));
            }
            int result = statement.executeUpdate();
            if (result < 1) {
                return result;
            }
            if (!soberTable.isAutoId() && dialect.isSupportsGetGeneratedKeys() && soberTable.isSerial()) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    BeanUtil.setSimpleProperty(object, soberTable.getPrimary(), dialect.getResultSetValue(rs, 1));
                }
                JdbcUtil.closeResultSet(rs);
            }
            if (child) {
                Map<String, SoberNexus>  nexusMap = soberTable.getNexusMap();
                if (!ObjectUtil.isEmpty(nexusMap))
                {
                    // Object keyObj = BeanUtil.getProperty(object, soberTable.getPrimary());
                    /////////////////////////////保存关联对象begin
                    Map<String, SoberNexus> nexus = soberTable.getNexusMap();
                    for (String colName : nexus.keySet()) {
                        SoberNexus soberNexus = nexus.get(colName);
                        if (!soberNexus.isSave()) {
                            continue;
                        }
                        if (MappingType.OneToOne.equalsIgnoreCase(soberNexus.getMapping())) {
                            Object oneToOneObject = BeanUtil.getProperty(object, colName);
                            if (oneToOneObject == null) {
                                continue;
                            }
                            Object oneToOneValue = BeanUtil.getProperty(object, soberNexus.getField());
                            Object v = BeanUtil.getFieldValue(oneToOneObject,soberNexus.getTargetField(),false);
                            if (ObjectUtil.isEmpty(v) || ((v instanceof Number)&& ObjectUtil.toLong(v)==0))
                            {
                                BeanUtil.setSimpleProperty(oneToOneObject, soberNexus.getTargetField(), oneToOneValue);
                            }
                            result = result + save(oneToOneObject);
                        }
                        if (MappingType.OneToMany.equalsIgnoreCase(soberNexus.getMapping())) {
                            Collection<?> oneToMayObjects = (Collection<?>) BeanUtil.getProperty(object, colName);
                            if (oneToMayObjects == null || oneToMayObjects.isEmpty()) {
                                continue;
                            }
                            Object oneToManyValue = BeanUtil.getProperty(object, soberNexus.getField());
                            for (Object o : oneToMayObjects) {
                                Object v = BeanUtil.getFieldValue(o,soberNexus.getTargetField(),false);
                                if (ObjectUtil.isEmpty(v) || ((v instanceof Number)&& ObjectUtil.toLong(v)==0))
                                {
                                    BeanUtil.setSimpleProperty(o, soberNexus.getTargetField(), oneToManyValue);
                                }
                            }
                            int s = save(oneToMayObjects);
                            if (s != oneToMayObjects.size()) {
                                return -2;
                            }
                            result = result + oneToMayObjects.size();
                            oneToMayObjects.clear();
                        }
                    }
                }
                //save
                /////////////////////////////保存关联对象end
            }

            return result;
        } catch (Exception e) {
            SoberColumn soberColumn = soberTable.getColumn(soberTable.getPrimary());
            if (soberTable.isAutoId()&&soberColumn!=null&&ClassUtil.isNumberType(soberColumn.getClassType()))
            {
                String msg = e.getMessage();
                if (msg!=null&&msg.contains("Duplicate")&&msg.contains("PRIMARY"))
                {
                    //关键字重复了,这些去修复一下
                    AnnotationUtil.fixIdCacheMax(soberTable,object,this);
                    if (DatabaseEnumType.find(soberFactory.getDatabaseType()).equals(DatabaseEnumType.POSTGRESQL)&&msg.contains("duplicate key value"))
                    {
                        //手工修改了数据库的seq,这里尝试修复
                        AnnotationUtil.postgreSqlFixSeqId(soberTable,this);
                    }
                }
            }
            log.error(sqlText, e);
            //在事务中不能关闭连接,关闭了会滚会失败
            throw e;
        } finally {
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
            valueMap.clear();
        }
    }

    /**
     * @param collection 保持一个列表
     * @return 返回保持数量
     * @throws Exception      验证错误
     * @throws ValidException 其他错误
     */
    @Override
    public int save(Collection<?> collection) throws Exception
    {
        return save(collection,false);
    }

    /**
     *
     * @param collection 保持一个列表
     * @param child 子对象
     * @return  返回保持数量
     * @throws Exception 验证错误
     */
    @Override
    public int save(Collection<?> collection, boolean child) throws Exception
    {
        if (collection == null || collection.size() < 1) {
            return -2;
        }
        TableModels soberTable = null;
        int result = 0;
        for (Object obj : collection) {
            if (soberTable == null) {
                soberTable = getSoberTable(obj.getClass());
            }
            //////////配置验证才能够保存 begin
            if (soberFactory.isValid()) {
                validator(obj);
            }
            //////////配置验证才能够保存 end
            result = result + save(obj, child);
        }
        return result;
    }

    /**
     * @param collection 批量快速保持
     * @return jdbc 更新返回状态
     * @throws Exception 异常
     */
    @Override
    public int batchSave(Collection<?> collection) throws Exception {
        if (collection == null || collection.size() < 1) {
            return -2;
        }
        Object checkObj = collection.iterator().next();
        TableModels soberTable = getSoberTable(checkObj.getClass());
        PreparedStatement statement = null;

        Object idValue = BeanUtil.getProperty(checkObj, soberTable.getPrimary());
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());

        String[] fieldArray;
        if (!soberTable.isAutoId() && dialect.isSupportsGetGeneratedKeys() && (idValue == null || 0 == ObjectUtil.toLong(idValue))) {
            fieldArray = soberTable.getFieldArray();
        } else {
            //不用支持ID字段，就自动生成ID字段
            for (Object object : collection) {
                AnnotationUtil.autoSetId(object, soberTable.getPrimary(), this);
            }
            fieldArray = soberTable.getFullFieldArray();
        }
        if (fieldArray == null || fieldArray.length < 1) {
            return -2;
        }

        valueMap.put(Dialect.KEY_FIELD_LIST, fieldArray);
        valueMap.put(Dialect.KEY_FIELD_COUNT, fieldArray.length);

        int result = 0;
        String sqlText = StringUtil.empty;
        Connection conn = getConnection(SoberEnv.WRITE_ONLY);
        boolean oldAutoCommit = conn.getAutoCommit();
        try {
            //先自动生成ID
            conn.setAutoCommit(false);
            sqlText = dialect.processTemplate(Dialect.SQL_INSERT, valueMap);
            debugPrint(sqlText);
            if (!soberTable.isAutoId() && dialect.isSupportsGetGeneratedKeys() && soberTable.isSerial()) {
                statement = conn.prepareStatement(sqlText, Statement.RETURN_GENERATED_KEYS);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            }

            int cm = 0;
            for (Object object : collection) {
                cm++;
                //////////配置验证才能够保存 begin
                if (soberFactory.isValid()) {
                    validator(object);
                }
                //////////配置验证才能够保存 end

                for (int i = 0; i < fieldArray.length; i++) {
                    debugPrint("prepared[" + (i + 1) + "]=" + BeanUtil.getProperty(object, fieldArray[i]));
                    dialect.setPreparedStatementValue(statement, i + 1, BeanUtil.getProperty(object, fieldArray[i]));
                }

                statement.addBatch();
                if (cm % 500 == 0) {
                    result = result + ArrayUtil.sum(statement.executeBatch());
                    conn.commit();
                }
            }
            if (cm % 500 != 0) {
                result = result + ArrayUtil.sum(statement.executeBatch());
                conn.commit();
            }
            return result;
        } catch (Exception e) {
            log.error("ERROR SQL:" + sqlText, e);
            e.printStackTrace();
            throw e;
        } finally {
            conn.setAutoCommit(oldAutoCommit);
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
            valueMap.clear();
        }
    }

    /**
     * 级联删除,不删除ManyToOne,只删除OneToOne 和 OneToMany
     *
     * @param o        对象
     * @param delChild 是否删除子对象
     * @return boolean 是否成功
     */
    @Override
    public int delete(Object o, boolean delChild) throws Exception {
        if (o == null) {
            return -2;
        }
        TableModels soberTable = getSoberTable(o.getClass());
        return delete(o.getClass(), soberTable.getPrimary(), (Serializable) BeanUtil.getProperty(o, soberTable.getPrimary()), null, delChild);
    }

    /**
     * @param aClass   类
     * @param ids      id 列表
     * @param delChild 删除关联
     * @return 删除
     */
    @Override
    public int delete(Class<?> aClass, Object[] ids, boolean delChild) {
        if (ids == null) {
            return -2;
        }
        TableModels soberTable = getSoberTable(aClass);
        return createCriteria(aClass).add(Expression.in(soberTable.getPrimary(), ids)).delete(delChild);
    }

    /**
     * 删除对象
     *
     * @param o 对象
     * @return boolean
     */
    @Override
    public int delete(Object o) {
        if (o == null) {
            return -2;
        }
        if (ClassUtil.isStandardProperty(o.getClass())) {
            log.debug("delete 参数错误，必须传入对象{}", o);
        }
        TableModels soberTable = getSoberTable(o.getClass());
        Object key = BeanUtil.getProperty(o, soberTable.getPrimary());
        return delete(o.getClass(), soberTable.getPrimary(), (Serializable) key);
    }

    /**
     * @param aClass       类
     * @param serializable id
     * @return 删除对象
     */
    @Override
    public int delete(Class<?> aClass, Serializable serializable) {
        return delete(aClass, getSoberTable(aClass).getPrimary(), serializable);
    }

    /**
     * 根据字段删除一个对象,或一组对象,快速删除
     *
     * @param aClass       类
     * @param field        字段
     * @param serializable 字段值
     * @return 是否成功
     */
    @Override
    public int delete(Class<?> aClass, String field, Serializable serializable) {
        TableModels soberTable = getSoberTable(aClass);
        if (soberTable == null) {
            return -2;
        }
        if (!soberTable.containsField(field))
        {
            return -1;
        }
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_FIELD_NAME, field);
        valueMap.put(Dialect.KEY_FIELD_NAME + Dialect.FIELD_QUOTE, JdbcUtil.isQuote(soberTable, field));
        valueMap.put(Dialect.KEY_FIELD_VALUE, serializable);
        String sqlText = dialect.processTemplate(Dialect.SQL_DELETE, valueMap);
        try {
            return update(sqlText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -2;
    }

    /**
     * 级联删除,不删除ManyToOne,只删除OneToOne 和 OneToMany
     *
     * @param aClass       删除的类
     * @param serializable 值
     * @param delChild     是否删除映射对象
     * @return boolean 是否成功
     */
    @Override
    public int delete(Class<?> aClass, Serializable serializable, boolean delChild) {
        return delete(aClass, getSoberTable(aClass).getPrimary(), serializable, null, delChild);
    }

    /**
     * 级联方式删除对象,只删除一层
     *
     * @param aClass       删除对象
     * @param field        删除字段
     * @param serializable 字段值
     * @param delChild     删除映射对象
     * @return boolean
     */
    @Override
    public int delete(Class<?> aClass, String field, Serializable serializable, String term, boolean delChild) {
        Criteria criteria = createCriteria(aClass);
        if (serializable.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(serializable);
            Object[] params = new Object[length];
            for (int i = 0; i < length; i++) {
                params[i] = java.lang.reflect.Array.get(serializable, i);
            }
            criteria = criteria.add(Expression.in(field, params));
        } else {
            criteria = criteria.add(Expression.eq(field, serializable));
        }
        if (!StringUtil.isNull(term)) {
            try {
                criteria = SSqlExpression.getTermExpression(criteria, term);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return criteria.delete(delChild);
    }

    /**
     * 删除映射关系的对象 ManyToOne 关系不删除
     *
     * @param o 对象
     * @return boolean 是否成功
     */
    @Override
    public int deleteNexus(Object o) {
        int result = 0;
        TableModels soberTable = getSoberTable(o.getClass());
        for (SoberNexus soberNexus : soberTable.getNexusMap().values()) {
            if (!soberNexus.isDelete()) {
                continue;
            }
            if (!MappingType.ManyToOne.equalsIgnoreCase(soberNexus.getMapping())) {
                Object selfValue;
                try {
                    selfValue = BeanUtil.getProperty(o, soberNexus.getField());
                    if (selfValue == null) {
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("映射关系错误:" + o.getClass().getName() + "  方法:" + soberNexus.getField() + "不存在", e);
                    return -2;
                }
                result = result + delete(soberNexus.getTargetEntity(), soberNexus.getTargetField(), (Serializable) selfValue);
            }
        }
        return result;
    }
    //------------------------------------------------------------------------------------------------------------------

    @Override
    public int update(Collection<Object> collection) throws Exception {
        int result = 0;
        for (Object o : collection) {
            result = result + update(o);
        }
        return result;
    }

    /**
     * 更具ID更新一个对象
     *
     * @param object 对象
     * @return boolean
     * @throws Exception 异常
     */
    @Override
    public int update(Object object) throws Exception {
        if (object == null) {
            return -2;
        }
        //////////配置验证才能够保存 begin

        if (soberFactory.isValid()) {
            validator(object);
        }

        //////////配置验证才能够保存 end                ;
        TableModels soberTable = getSoberTable(object.getClass());
        if (soberTable == null) {
            return -2;
        }
        Connection conn = null;
        PreparedStatement statement = null;
        String[] fieldArray = soberTable.getFieldArray();
        if (ArrayUtil.isEmpty(fieldArray)) {
            return -2;
        }
        fieldArray = ArrayUtil.delete(fieldArray, soberTable.getPrimary(), true);

        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_FIELD_LIST, fieldArray);
        valueMap.put(Dialect.KEY_FIELD_COUNT, fieldArray.length);
        valueMap.put(Dialect.KEY_FIELD_NAME, soberTable.getPrimary());
        valueMap.put(Dialect.KEY_FIELD_NAME + Dialect.FIELD_QUOTE, JdbcUtil.isQuote(soberTable, soberTable.getPrimary()));
        Object value = BeanUtil.getProperty(object, soberTable.getPrimary());
        valueMap.put(Dialect.KEY_FIELD_VALUE, value);
        if (value == null)
        {
            SQLException e = new SQLException("ERROR:SQL,update primary is null,更新的关键字不能为空!");
            log.error(ObjectUtil.toString(object), e);
            e.printStackTrace();
            throw e;
        }

        int result;
        String sqlText = StringUtil.empty;
        try {
            conn = getConnection(SoberEnv.READ_WRITE);
            sqlText = dialect.processTemplate(Dialect.SQL_UPDATE, valueMap);
            debugPrint(sqlText);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            }

            setPreparedStatementValueList(statement, fieldArray, object);
            result = statement.executeUpdate();

            ///////////////////处理关联对象begin
            Map<String, SoberNexus> nexus = soberTable.getNexusMap();
            for (String colName : nexus.keySet()) {
                SoberNexus soberNexus = nexus.get(colName);
                if (!soberNexus.isUpdate()) {
                    continue;
                }
                if (MappingType.OneToOne.equalsIgnoreCase(soberNexus.getMapping())) {
                    Object findValue = BeanUtil.getProperty(object, soberNexus.getField());
                    if (findValue == null) {
                        continue;
                    }
                    delete(soberNexus.getTargetEntity(), soberNexus.getTargetField(), (Serializable) findValue);
                    Object saveObj = BeanUtil.getProperty(object, colName);
                    if (saveObj != null) {
                        result = result + save(saveObj);
                    }
                }
                if (MappingType.OneToMany.equalsIgnoreCase(soberNexus.getMapping())) {
                    Object findValue = BeanUtil.getProperty(object, soberNexus.getField());
                    if (findValue == null) {
                        continue;
                    }
                    Criteria criteria = createCriteria(soberNexus.getTargetEntity()).add(Expression.eq(soberNexus.getTargetField(), findValue));
                    SSqlExpression.getTermExpression(criteria, soberNexus.getTerm()).delete(false);
                    Collection<?> saveObjList = (Collection<?>) BeanUtil.getProperty(object, colName);
                    if (saveObjList != null && !saveObjList.isEmpty()) {
                        for (Object child : saveObjList) {
                            BeanUtil.setSimpleProperty(child, soberNexus.getTargetField(), findValue);
                        }
                        result = result + save(saveObjList);
                        evict(soberNexus.getTargetEntity());
                    }
                }
            }
            ///////////////////处理关联对象end
        } catch (Exception e) {
            log.error(sqlText, e);
            e.printStackTrace();
            throw e;
        } finally {
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
            valueMap.clear();
        }
        return result;
    }

    /**
     * @param object      查询对象
     * @param updateFiled 更新一个字段
     * @return 指定更新字段, 特殊不验证了
     * @throws Exception 异常
     */
    @Override
    public int update(Object object, String[] updateFiled) throws Exception {
        if (object == null) {
            return -2;
        }
        if (ArrayUtil.isEmpty(updateFiled))
        {
            return update(object);
        }
        TableModels soberTable = getSoberTable(object.getClass());
        Connection conn = null;
        PreparedStatement statement = null;
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_FIELD_LIST, updateFiled);
        valueMap.put(Dialect.KEY_FIELD_COUNT, updateFiled.length);
        valueMap.put(Dialect.KEY_FIELD_NAME, soberTable.getPrimary());
        valueMap.put(Dialect.KEY_FIELD_NAME + Dialect.FIELD_QUOTE, JdbcUtil.isQuote(soberTable, soberTable.getPrimary()));
        Object value;
        String sqlText = StringUtil.empty;

        try {
            value = BeanUtil.getProperty(object, soberTable.getPrimary());
            valueMap.put(Dialect.KEY_FIELD_VALUE, value);
            if (valueMap.get(Dialect.KEY_FIELD_VALUE) == null) {
                throw new SQLException("SQL Primary Key is NULL");
            }
            conn = getConnection(SoberEnv.READ_WRITE);
            sqlText = dialect.processTemplate(Dialect.SQL_UPDATE, valueMap);
            debugPrint(sqlText);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            }
            //TYPE_FORWARD_ONLY
            setPreparedStatementValueList(statement, updateFiled, object);
            return statement.executeUpdate();
        } catch (Exception e) {
            log.error(sqlText, e);
            throw e;
        } finally {
            valueMap.clear();
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
            if (soberFactory.isUseCache() && soberTable.isUseCache()) {
                //同时更新缓存
                String cacheKey = SoberUtil.getLoadKey(soberTable.getEntity(), soberTable.getPrimary(), BeanUtil.getProperty(object, soberTable.getPrimary()));
                JSCacheManager.remove(soberTable.getEntity(), cacheKey);
            }
        }

    }

    /**
     *
     * @param sql 简单sql
     * @return sql执行更新
     * @throws Exception 异常
     */
    @Override
    public int update(String sql) throws Exception {
        return update(sql, (Object[]) null);
    }


    /**
     * @param sqlText 使用sql直接更新,参数 ？ 的jdbc原生形式
     * @param params  参数
     * @return 更新数量
     * @throws Exception 异常
     */
    @Override
    public int update(String sqlText, Object[] params) throws Exception {
        if (StringUtil.isEmpty(sqlText)) {
            return -2;
        }
        int result;
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = getConnection(SoberEnv.READ_WRITE);
            debugPrint(sqlText);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            }
            if (!ArrayUtil.isEmpty(params)) {
                for (int i = 0; i < params.length; i++) {
                    debugPrint("prepared[" + (i + 1) + "]=" + params[i]);
                    dialect.setPreparedStatementValue(statement, i + 1, params[i]);
                }
            }
            result = statement.executeUpdate();
        } catch (Exception e) {
            log.error("update sql:" + sqlText, e);
            throw  e;
        } finally {
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
        }
        return result;
    }


    /**
     * 执行一个sql
     *
     * @param cla     类 对映配置中的map命名空间
     * @param sqlText sql
     * @param params  支持类型 Object[] or HashMap  String,Object,这里是留给参数对象的,所以params没有类型
     * @return 执行情况
     * @throws Exception 异常
     */
    @Override
    public boolean execute(Class<?> cla, String sqlText, Object params) throws Exception {
        Object[] args = null;
        Map<String, Object> valueMap = null;
        TableModels soberTable = getSoberTable(cla);
        if (params instanceof Map) {
            valueMap = (Map<String, Object>) params;
        } else if (params instanceof Collection) {
            Collection coll = (Collection) params;
            args = coll.toArray();
        }
        if (valueMap == null) {
            valueMap = new HashMap<>();
        }
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_PRIMARY_KEY, soberTable.getPrimary());
        assert params instanceof Object[];
        return execute(dialect.processSql(sqlText, valueMap), args);
    }

    /**
     *
     * @param sqlText 简单的sql
     * @return 执行一个 execute
     * @throws Exception 异常
     */
    @Override
    public boolean execute(String sqlText) throws Exception {
        return execute(sqlText, null);
    }

    /**
     * 执行一个 execute
     * execute 方法应该仅在语句能返回多个 ResultSet 对象、多个更新计数或ResultSet 对象与更新计数的组合时使用。当执行某个已存储过程或动态执行未知 SQL 字符串（即应用程序程序员在编译时未知）时，有可能出现多个结果的情况，尽管这种情况很少见。例如，用户可能执行一个已存储过程（使用 CallableStatement 对象 - 参见第 135 页的 CallableStatement），并且该已存储过程可执行更新，然后执行选择，再进行更新，再进行选择，等等。通常使用已存储过程的人应知道它所返回的内容。
     * 因为方法 execute 处理非常规情况，所以获取其结果需要一些特殊处理并不足为怪。例如，假定已知某个过程返回两个结果集，则在使用方法 execute 执行该过程后，必须调用方法 getResultSet 获得第一个结果集，然后调用适当的getXXX 方法获取其中的值。要获得第二个结果集，需要先调用 getMoreResults方法，然后再调用 getResultSet 方法。如果已知某个过程返回两个更新计数，则首先调用方法 getUpdateCount，然后调用 getMoreResults，并再次调用
     *
     * @param sqlText sql
     * @param params  参数
     * @return 执行结果
     * @throws Exception 异常
     */
    @Override
    public boolean execute(String sqlText, Object[] params) throws Exception {
        if (sqlText == null || sqlText.length() < 1) {
            return false;
        }
        Connection conn = null;
        debugPrint(sqlText);
        //oracle 创建促发器的一个bug
        if ((dialect instanceof OracleDialect)&&sqlText.contains(" trigger ") && ObjectUtil.isEmpty(params))
        {
            Statement statement =  null;
            try {
                conn = getConnection(SoberEnv.READ_WRITE);
                statement = conn.createStatement();
                return statement.execute(sqlText);
            } catch (Exception e) {
                log.error("SQL:" + sqlText, e);
                throw e;
            } finally {
                JdbcUtil.closeStatement(statement);
                JdbcUtil.closeConnection(conn);
            }
        }
        else
        {
            PreparedStatement statement = null;
            try {
                conn = getConnection(SoberEnv.READ_WRITE);
                statement = conn.prepareStatement(sqlText);
                if (params != null) {
                    for (int i = 0; i < params.length; i++) {
                        debugPrint("prepared[" + (i + 1) + "]=" + params[i]);
                        dialect.setPreparedStatementValue(statement, i + 1, params[i]);
                    }
                }
                return statement.execute();
            } catch (Exception e) {
                log.error("SQL:" + sqlText, e);
                throw e;
            } finally {
                JdbcUtil.closeStatement(statement);
                JdbcUtil.closeConnection(conn);
            }
        }
    }


    /**
     * 先判断是否存在,存在就使用更新,否则增加
     * @param object 对象
     * @return 保存是否成功
     * @throws Exception 异常
     */
    @Override
    public int saveOrUpdate(Object object) throws Exception {
        if (object == null) {
            return -2;
        }
        if (object instanceof Collection) {
            return saveOrUpdateAll((Collection<?>) object);
        }
        //////////配置验证才能够保存 begin
        if (soberFactory.isValid()) {
            validator(object);
        }
        //////////配置验证才能够保存 end

        TableModels soberTable = getSoberTable(object.getClass());
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String[] fieldArray = soberTable.getFieldArray();
        if (fieldArray == null || fieldArray.length < 1) {
            return -2;
        }
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_FIELD_LIST, fieldArray);
        valueMap.put(Dialect.KEY_FIELD_COUNT, fieldArray.length);
        valueMap.put(Dialect.KEY_FIELD_NAME, soberTable.getPrimary());
        valueMap.put(Dialect.KEY_FIELD_NAME + Dialect.FIELD_QUOTE, JdbcUtil.isQuote(soberTable, soberTable.getPrimary()));
        valueMap.put(Dialect.KEY_FIELD_VALUE, BeanUtil.getProperty(object, soberTable.getPrimary()));
        if (valueMap.get(Dialect.KEY_FIELD_VALUE) == null) {
            if (dialect.isSupportsGetGeneratedKeys() && soberTable.isAutoId() && soberTable.isSerial()) {
                return save(object);
            } else {
                throw new SQLException("SQL Primary is NULL");
            }
        }
        String sqlText = StringUtil.empty;
        Connection conn = null;
        try {
            conn = getConnection(SoberEnv.READ_WRITE);
            sqlText = dialect.processTemplate(Dialect.SQL_HAVE, valueMap);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            }
            debugPrint(sqlText);
            resultSet = statement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                fieldArray = ArrayUtil.delete(fieldArray, soberTable.getPrimary(), true);
                valueMap.put(Dialect.KEY_FIELD_LIST, fieldArray);
                valueMap.put(Dialect.KEY_FIELD_COUNT, fieldArray.length);
                sqlText = dialect.processTemplate(Dialect.SQL_UPDATE, valueMap);
                debugPrint(sqlText);
                statement = conn.prepareStatement(sqlText);
                setPreparedStatementValueList(statement, fieldArray, object);
            } else {
                sqlText = dialect.processTemplate(Dialect.SQL_INSERT, valueMap);
                debugPrint(sqlText);
                if (!dialect.supportsConcurReadOnly()) {
                    statement = conn.prepareStatement(sqlText);
                } else {
                    statement = conn.prepareStatement(sqlText, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                }
                setPreparedStatementValueList(statement, fieldArray, object);
            }
            return statement.executeUpdate();
        } catch (Exception e) {
            log.error("SQL:" + sqlText, e);
            throw e;
        } finally {
            valueMap.clear();
            JdbcUtil.closeResultSet(resultSet);
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
        }
    }

    /**
     * 先判断是否存在,存在就使用更新,否则增加 ,处理一个队
     *
     * @param collection 更新对象列表
     * @return boolean 返回是否成功
     * @throws Exception 异常
     */
    private int saveOrUpdateAll(Collection<?> collection) throws Exception {
        if (collection == null || collection.isEmpty()) {
            return -2;
        }
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        TableModels soberTable = null;
        List<Object> deleteId = new ArrayList<>();
        //得到类型
        int i = 0;
        for (Object object : collection) {
            if (i == 0) {
                soberTable = getSoberTable(object.getClass());
                if (soberTable == null) {
                    return -2;
                }
            }
            deleteId.add(BeanUtil.getProperty(object, soberTable.getPrimary()));
            i++;
            //////////配置验证才能够保存 begin

            if (soberFactory.isValid()) {
                validator(object);
            }
            ////////////配置验证才能够保存 end

        }
        String[] fieldArray = soberTable.getFieldArray();
        if (ArrayUtil.isEmpty(fieldArray)) {
            return -2;
        }
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_FIELD_LIST, fieldArray);
        valueMap.put(Dialect.KEY_FIELD_COUNT, fieldArray.length);
        valueMap.put(Dialect.KEY_FIELD_NAME, soberTable.getPrimary());
        valueMap.put(Dialect.KEY_FIELD_NAME + Dialect.FIELD_QUOTE, JdbcUtil.isQuote(soberTable, soberTable.getPrimary()));
        int result = 0;
        String sqlText = StringUtil.empty;
        try {
            conn = getConnection(SoberEnv.READ_WRITE);

            //////////////删除 begin
            valueMap.put(Dialect.KEY_FIELD_VALUE, deleteId);
            sqlText = dialect.processTemplate(Dialect.SQL_DELETE_IN, valueMap);
            debugPrint(sqlText);
            if (!dialect.supportsConcurReadOnly()) {
                conn.prepareStatement(sqlText).execute();
            } else {
                conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE).execute();
            }
            //////////////删除 end

            sqlText = dialect.processTemplate(Dialect.SQL_INSERT, valueMap);
            debugPrint(sqlText);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            }
            for (Object object : collection) {
                setPreparedStatementValueList(statement, fieldArray, object);
                int temp = statement.executeUpdate();
                if (temp < 1) {
                    throw new SQLException(sqlText + " object:" + MapUtil.toString(ObjectUtil.getMap(object)));
                } else {
                    result = result + temp;
                }
                statement.clearParameters();
            }
            return result;
        } catch (Exception e) {
            log.error("SQL:" + sqlText, e);
            throw e;
        } finally {
            valueMap.clear();
            JdbcUtil.closeResultSet(resultSet);
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);

        }
    }

    //------------------------------------------------------------------------------------------------------------------
    /**
     * 查询返回列表
     * 使用jdbc完成,比较浪费资源
     * @param cla class
     * @param sql  sql
     * @param param 参数
     * @param currentPage page number
     * @param totalCount rows
     * @param loadChild load map object
     * @param <T> 类型
     * @return List object list
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> query(Class<T> cla, String sql, Object[] param, int currentPage, int totalCount, boolean loadChild) {
        if (totalCount > getMaxRows()) {
            totalCount = getMaxRows();
        }
        if (currentPage <= 0) {
            currentPage = 1;
        }
        int iEnd = currentPage * totalCount;
        if (iEnd < 0) {
            iEnd = 0;
        }
        int iBegin = iEnd - totalCount;
        if (iBegin < 0) {
            iBegin = 0;
        }

        TableModels soberTable = getSoberTable(cla);

        List<T> result;

        //取出cache  begin
        String cacheKey = null;
        if (soberFactory.isUseCache() && soberTable.isUseCache()) {
            StringBuilder termKey = new StringBuilder();
            termKey.append(sql);
            termKey.append("_").append("_p_").append(soberFactory.getDatabaseType()).append("_");
            if (param != null) {
                for (Object po : param) {
                    termKey.append(ObjectUtil.toString(po));
                }
            }
            cacheKey = SoberUtil.getListKey(cla, StringUtil.replace(termKey.toString(), StringUtil.EQUAL, "_"),StringUtil.empty,iBegin,iEnd, loadChild);
            result = (List<T>)JSCacheManager.get(cla, cacheKey);
            if (!ObjectUtil.isEmpty(result)) {
                return result;
            }
        }
        //取出cache  end
        result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        Map<String, Object> valueMap = new HashMap<>(5);
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_PRIMARY_KEY, soberTable.getPrimary());
        try {
            conn = getConnection(SoberEnv.READ_ONLY);
            sql = dialect.processSql(sql, valueMap);
            debugPrint(sql);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sql);
            } else {
                statement = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            }
            JdbcUtil.setFetchSize(statement,iEnd);
            statement.setMaxRows(iEnd);
            if (param != null) {
                for (int i = 0; i < param.length; i++) {
                    debugPrint("prepared[" + (i + 1) + "]=" + param[i]);
                    dialect.setPreparedStatementValue(statement, i + 1, param[i]);
                }
            }
            resultSet = statement.executeQuery();
            if (iBegin > 0) {
                resultSet.absolute(iBegin);
            }
            while (resultSet.next()) {
                T tempObj = loadColumnsValue(cla, resultSet);
                //载入计算数据
                calcUnique(soberTable, tempObj);
                result.add(tempObj);
                if (result.size() > totalCount) {
                    break;
                }
            }
            if (loadChild) {
                loadNexusList(soberTable, result);
            }
        } catch (Exception e) {
            log.error(soberTable + ",SQL:" + sql, e);
            e.printStackTrace();
            throw new IllegalArgumentException(soberTable + ",SQL:" + sql);
        } finally {
            JdbcUtil.closeResultSet(resultSet);
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
            valueMap.clear();
            if (soberFactory.isUseCache() && soberTable.isUseCache()) {
                JSCacheManager.put(cla, cacheKey,result);
            }

        }
        return result;
    }

    /**
     * 查询返回封装好的列表
     *
     * @param cla     要封装返回的对象
     * @param sql SQL
     * @param param   参数
     * @return 封装好的查询对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> query(Class<T> cla, String sql, Object[] param) {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        List<T> result = null;
        //取出cache  begin
        TableModels soberTable = soberFactory.getTableModels(cla, this);
        String cacheKey = null;
        if (soberTable!=null&&soberFactory.isUseCache() && soberTable.isUseCache()) {
            StringBuilder termKey = new StringBuilder();
            termKey.append(sql);
            termKey.append("_").append("_p_").append(soberFactory.getDatabaseType()).append("_");
            if (param != null) {
                for (Object po : param) {
                    termKey.append(ObjectUtil.toString(po));
                }
            }
            cacheKey = SoberUtil.getListKey(cla, StringUtil.replace(termKey.toString(), StringUtil.EQUAL, "_"),StringUtil.empty,1,getMaxRows(), false);
            result = (List<T>)JSCacheManager.get(cla, cacheKey);
            if (!ObjectUtil.isEmpty(result)) {
                return result;
            }

        }
        result = new ArrayList<>();
        //取出cache  end
        try {
            conn = getConnection(SoberEnv.READ_ONLY);
            debugPrint(sql);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sql);
            } else {
                statement = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            }

            if (param != null) {
                for (int i = 0; i < param.length; i++) {
                    debugPrint("prepared[" + (i + 1) + "]=" + param[i]);
                    dialect.setPreparedStatementValue(statement, i + 1, param[i]);
                }
            }
            JdbcUtil.setFetchSize(statement,500);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                T resultObject;
                if (soberTable != null) {
                    resultObject = loadColumnsValue(cla, resultSet);
                } else {
                    resultObject = JdbcUtil.getBean(resultSet, cla, dialect);
                }
                result.add(resultObject);
            }
        } catch (Exception e) {
            log.error("SQL:" + sql, e);
            e.printStackTrace();
        } finally {
            JdbcUtil.closeResultSet(resultSet);
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
            if (soberTable!=null&&soberFactory.isUseCache() && soberTable.isUseCache()) {
                JSCacheManager.put(cla, cacheKey,result);
            }
        }
        return result;
    }

    /**
     * @param sqlText     sql
     * @param param       参数数组
     * @param currentPage 页数
     * @param totalCount  返回行数
     * @return List  查询返回列表
     */
    @Override
    public List<?> query(String sqlText, Object[] param, int currentPage, int totalCount) {
        if (totalCount > getMaxRows()) {
            totalCount = getMaxRows();
        }
        if (currentPage <= 0) {
            currentPage = 1;
        }
        int iEnd = currentPage * totalCount;
        if (iEnd < 0) {
            iEnd = 0;
        }
        int iBegin = iEnd - totalCount;
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Object> result = new ArrayList<>();
        try {
            conn = getConnection(SoberEnv.READ_ONLY);
            debugPrint(sqlText);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            }

            JdbcUtil.setFetchSize(statement,iEnd);
            statement.setMaxRows(iEnd);

            if (param != null) {
                for (int i = 0; i < param.length; i++) {
                    debugPrint("prepared[" + (i + 1) + "]=" + param[i]);
                    dialect.setPreparedStatementValue(statement, i + 1, param[i]);
                }
            }

            resultSet = statement.executeQuery();
            if (iBegin == 0 || resultSet.absolute(iBegin)) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                while (resultSet.next()) {
                    Map<String, Object> beanMap = new HashMap<>();
                    for (int n = 1; n <= metaData.getColumnCount(); n++) {
                        String field = StringUtil.underlineToCamel(metaData.getColumnLabel(n));
                        Object value = dialect.getResultSetValue(resultSet, n);
                        beanMap.put(field, value);
                    }
                    result.add(ReflectUtil.createDynamicBean(beanMap));
                    if (result.size() > totalCount) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("SQL:" + sqlText, e);
            e.printStackTrace();
        } finally {
            JdbcUtil.closeResultSet(resultSet);
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
        }
        return result;
    }
    //------------------------------------------------------------------------------------------------------------------

    /**
     * @param cla 类
     * @param sql sql
     * @param o   对象
     * @return 返回单一对象
     */
    @Override
    public Object getUniqueResult(Class<?> cla, String sql, Object o) {
        Map<String, Object> valueMap = ObjectUtil.getMap(o);
        TableModels soberTable = getSoberTable(cla);
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_PRIMARY_KEY, soberTable.getPrimary());
        return getUniqueResult(sql, valueMap);
    }

    /**
     * @param sql sql语句
     * @param o   参数对象
     * @return 单一返回对象
     */
    @Override
    public Object getUniqueResult(String sql, Object o)  {
        Map<String, Object> valueMap = null;
        if (o != null) {
            valueMap = ObjectUtil.getMap(o);
            TableModels soberTable = getSoberTable(o.getClass());
            valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
            valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
            valueMap.put(Dialect.KEY_PRIMARY_KEY, soberTable.getPrimary());
        }
        return getUniqueResult(sql, valueMap);
    }

    /**
     * @param sql sql语句
     * @return 单一返回对象
     */
    @Override
    public Object getUniqueResult(String sql) {
        return getUniqueResult(sql, (Object) null);
    }

    /**
     * @param sqlText sql语句
     * @param param   参数数组
     * @return 单一返回对象
     */
    @Override
    public Object getUniqueResult(String sqlText, Object[] param) {
        Object result = null;
        Connection conn = null;
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try {
            conn = getConnection(SoberEnv.READ_ONLY);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }

            debugPrint(sqlText);
            if (!ArrayUtil.isEmpty(param)) {
                for (int i = 0; i < param.length; i++) {
                    debugPrint("prepared[" + (i + 1) + "]=" + param[i]);
                    dialect.setPreparedStatementValue(statement, i + 1, param[i]);
                }
            }
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getObject(1);
            }
        } catch (Exception e) {
            log.error("SQL:" + sqlText, e);
            e.printStackTrace();
        } finally {
            JdbcUtil.closeResultSet(resultSet);
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
        }

        return result;
    }

    /**
     * 单个对象查询返回
     *
     * @param sql      sql
     * @param valueMap map参数
     * @return Object
     */
    @Override
    public Object getUniqueResult(String sql, Map<String, Object> valueMap) {
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String sqlText = StringUtil.empty;
        try {
            sqlText = dialect.processSql(sql, valueMap);
            debugPrint(sqlText);
            conn = getConnection(SoberEnv.READ_ONLY);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.createStatement();
            } else {
                statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }
            resultSet = statement.executeQuery(sqlText);
            if (resultSet.next()) {
                return dialect.getResultSetValue(resultSet, 1);
            }
        } catch (Exception e) {
            log.error("SQL:" + sqlText, e);
            e.printStackTrace();
        } finally {
            JdbcUtil.closeResultSet(resultSet);
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
        }
        return null;
    }


    /**
     * 删除一堆对象
     *
     * @param collection 删除激活
     * @throws Exception 异常
     */
    @Override
    public boolean deleteAll(Collection<?> collection) throws Exception {
        if (ObjectUtil.isEmpty(collection)) {
            return true;
        }
        PreparedStatement statement = null;
        Connection conn = null;
        ResultSet resultSet = null;
        TableModels soberTable = null;
        List<Object> deleteId = new ArrayList<>();
        int i = 0;
        for (Object object : collection) {
            if (i == 0) {
                soberTable = getSoberTable(object.getClass());
                if (soberTable == null) {
                    return false;
                }
            }
            deleteId.add(BeanUtil.getProperty(object, soberTable.getPrimary()));
            i++;
        }

        if (soberTable == null) {
            return false;
        }
        String[] fieldArray = soberTable.getFieldArray();
        if (fieldArray == null || fieldArray.length < 1) {
            return false;
        }
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_FIELD_LIST, fieldArray);
        valueMap.put(Dialect.KEY_FIELD_COUNT, fieldArray.length);
        valueMap.put(Dialect.KEY_FIELD_NAME, soberTable.getPrimary());
        valueMap.put(Dialect.KEY_FIELD_NAME + Dialect.FIELD_QUOTE, JdbcUtil.isQuote(soberTable, soberTable.getPrimary()));
        String sqlText = StringUtil.empty;
        try {
            valueMap.put(Dialect.KEY_FIELD_VALUE, deleteId);
            conn = getConnection(SoberEnv.WRITE_ONLY);
            sqlText = dialect.processTemplate(Dialect.SQL_DELETE_IN, valueMap);
            debugPrint(sqlText);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            }
            return statement.execute();
        } catch (Exception e) {
            log.error("SQL:" + sqlText, e);
            throw  e;
        } finally {
            valueMap.clear();
            JdbcUtil.closeResultSet(resultSet);
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
        }
    }

    /**
     * 得到创建表的SQL
     *
     * @param createClass 生成表创建sql
     */
    @Override
    public String getCreateTableSql(Class<?> createClass, TableModels soberTable) {

        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_TABLE_CAPTION, StringUtil.replace(soberTable.getCaption(),"'",""));

        /////////先创建每一个字段
        String[] columns = null;
        for (SoberColumn soberColumn : soberTable.getColumns()) {
            valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
            if (soberColumn.getName().equalsIgnoreCase(soberTable.getPrimary())) {
                valueMap.put(Dialect.KEY_FIELD_SERIAL, soberTable.isSerial());
            } else {
                valueMap.put(Dialect.KEY_FIELD_SERIAL, false);
            }
            valueMap.put(Dialect.KEY_PRIMARY_KEY, soberTable.getPrimary());
            valueMap.put(Dialect.COLUMN_NAME, soberColumn.getName());

            valueMap.put(Dialect.COLUMN_LENGTH, soberColumn.getLength());

            valueMap.put(Dialect.COLUMN_NOT_NULL, soberColumn.isNotNull());
            if (StringUtil.isEmpty(soberColumn.getDefaultValue()) && ClassUtil.isNumberType(soberColumn.getClassType())) {
                valueMap.put(Dialect.COLUMN_DEFAULT, 0);
            } else {
                valueMap.put(Dialect.COLUMN_DEFAULT, soberColumn.getDefaultValue());
            }
            if (soberColumn.getLength()==0&&soberColumn.getClassType().equals(String.class))
            {
                valueMap.put(Dialect.COLUMN_LENGTH, 32);
                log.error("类对象{}创建表结构字段{},没有设置长度,系统默认设置32",createClass,soberColumn.getName());
            }
            valueMap.put(Dialect.COLUMN_CAPTION, soberColumn.getCaption());
            String columnData = dialect.processTemplate(soberColumn.getClassType().getName(), valueMap);
            if (StringUtil.isEmpty(columnData) || columnData.length() < 4) {
                log.error(soberTable.getName() + StringUtil.DOT + soberColumn.getName() + "表结构定义有异常");
            }
            columns = ArrayUtil.add(columns, columnData);
            valueMap.clear();
        }
        ///修补建表注释主要是pgsql   begin
        StringBuilder commentPatchSql = new StringBuilder();
        if (dialect.commentPatch()) {

            for (SoberColumn soberColumn : soberTable.getColumns()) {
                valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
                valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
                valueMap.put(Dialect.COLUMN_NAME, soberColumn.getName());
                valueMap.put(Dialect.COLUMN_CAPTION, StringUtil.replace(soberColumn.getCaption(),StringUtil.SEMICOLON,"_"));
                commentPatchSql.append(dialect.processTemplate(Dialect.SQL_COMMENT, valueMap)).append(StringUtil.SEMICOLON).append(StringUtil.CRLF);
                valueMap.clear();
            }
            valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
            valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
            valueMap.put(Dialect.SQL_TABLE_COMMENT, StringUtil.replace(soberTable.getCaption(),StringUtil.SEMICOLON,"_"));
            valueMap.put(Dialect.KEY_TABLE_CAPTION, soberTable.getCaption());

            commentPatchSql.append(dialect.processTemplate(Dialect.SQL_TABLE_COMMENT, valueMap)).append(StringUtil.SEMICOLON).append(StringUtil.CRLF);
        }
        ///修补建表注释主要是pgsql   end

        /////////在总体的生成SQL begin
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_TABLE_CAPTION, StringUtil.replace(soberTable.getCaption(),"'",""));
        valueMap.put(Dialect.KEY_COLUMN_LIST, columns);
        valueMap.put(Dialect.KEY_PRIMARY_KEY, soberTable.getPrimary());
        valueMap.put(Dialect.KEY_FIELD_SERIAL, soberTable.isSerial());
        /////////在总体的生成SQL end

        if (dialect.commentPatch() && !StringUtil.isNull(commentPatchSql.toString())) {
            return dialect.processTemplate(Dialect.SQL_CREATE_TABLE, valueMap) + StringUtil.SEMICOLON + StringUtil.CRLF + commentPatchSql;
        }

        return dialect.processTemplate(Dialect.SQL_CREATE_TABLE, valueMap);
    }

    /**
     * 删除表
     *
     * @param cla 删除表
     * @return 是否成功
     */
    @Override
    public boolean dropTable(Class<?> cla) throws Exception {
        if (cla == null) {
            return false;
        }
        TableModels soberTable = getSoberTable(cla);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.COLUMN_NAME, soberTable.getPrimary());
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        if (soberFactory.isUseCache() && soberTable.isUseCache()) {
            JSCacheManager.removeAll(cla);
        }
        return execute(dialect.processTemplate(Dialect.SQL_DROP_TABLE, valueMap), null);
    }

    /**
     * 表是否存在
     *
     * @param soberTable bean对象是否存在表
     * @return 返回是否存在
     */
    @Override
    public boolean tableExists(TableModels soberTable) {
        if (soberTable==null) {
            return false;
        }
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.COLUMN_NAME, soberTable.getPrimary());
        Object o = getUniqueResult(dialect.processTemplate(Dialect.FUN_TABLE_EXISTS, valueMap));
        return o instanceof String && soberTable.getName().equalsIgnoreCase((String) o) || ObjectUtil.toBoolean(o);
    }

    /**
     * @param cla 得到最大ID
     * @return ID数
     */
    @Override
    public long getTableMaxId(Class<?> cla) {
        TableModels soberTable = getSoberTable(cla);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_PRIMARY_KEY, soberTable.getPrimary());
        return ObjectUtil.toLong(getUniqueResult(dialect.processTemplate(Dialect.TABLE_MAX_ID, valueMap)));
    }

    /**
     * @param databaseName 数据库名称
     * @return 得到数据库大小
     */
    @Override
    public long getDataBaseSize(String databaseName) {
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, databaseName);
        return ObjectUtil.toLong(getUniqueResult(dialect.processTemplate(Dialect.DATABASE_SIZE, valueMap)));
    }

    /**
     * @return 返回表名称数组
     */
    @Override
    public String[] getTableNames() {
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String[] result = null;
        //取出cache  begin
        String sqlText = StringUtil.empty;
        try {
            sqlText = dialect.processTemplate(Dialect.SQL_TABLE_NAMES, new HashMap<>());
            debugPrint(sqlText);
            conn = getConnection(SoberEnv.READ_ONLY);

            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.createStatement();
            } else {
                statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }
            resultSet = statement.executeQuery(sqlText);
            while (resultSet.next()) {
                result = ArrayUtil.add(result, (String) dialect.getResultSetValue(resultSet, 1));
            }
        } catch (Exception e) {
            log.error("SQL:" + sqlText, e);
            e.printStackTrace();
        } finally {
            JdbcUtil.closeResultSet(resultSet);
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
        }
        return result;
    }


    /**
     * @param cla 类对象
     * @return 得到数据库序列名称
     */
    @Override
    public String getSequenceName(Class<?> cla) {
        if (!dialect.supportsSequenceName()) {
            return null;
        }
        TableModels soberTable = getSoberTable(cla);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_PRIMARY_KEY, soberTable.getPrimary());
        Object o = getUniqueResult(dialect.processTemplate(Dialect.SEQUENCE_NAME, valueMap));
        return StringUtil.substringBetween((String) o, "'", "'");
    }

    /**
     * @param cla   类对象
     * @param start 序列值
     * @return 设置序列开始值
     * @throws Exception 异常
     */
    @Override
    public boolean alterSequenceStart(Class<?> cla, long start) throws Exception {
        if (start <= 0) {
            return false;
        }
        TableModels soberTable = getSoberTable(cla);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_PRIMARY_KEY, soberTable.getPrimary());
        valueMap.put(Dialect.SERIAL_NAME, getSequenceName(cla));
        valueMap.put(Dialect.KEY_SEQUENCE_RESTART, start);
        return execute(dialect.processTemplate(Dialect.ALTER_SEQUENCE_RESTART, valueMap), null);
    }

    /**
     *
     * @param obj 对象
     * @param field 字段
     * @param num 加的数字
     * @return 是否成功
     * @throws Exception 异常
     */
    @Override
    public boolean updateFieldAddNumber(Object obj,String field, int num) throws Exception {
        if (obj==null)
        {
            return false;
        }
        Class<?> cla = obj.getClass();
        TableModels soberTable = getSoberTable(cla);
        if (soberTable==null)
        {
            return false;
        }
        Object key = BeanUtil.getFieldValue(obj,soberTable.getPrimary(),false);
        SoberColumn soberColumn = soberTable.getColumn(soberTable.getPrimary());
        boolean isNum = ClassUtil.isNumberType(soberColumn.getClassType());
        String sql = "UPDATE " + soberTable.getName() + " SET "+field+StringUtil.EQUAL + field + "+"+ num +" WHERE " + soberTable.getPrimary() + StringUtil.EQUAL+(isNum?key:StringUtil.quoteSql((String)key));
        int x = update(sql);
        if (soberTable.isUseCache())
        {
            evict(cla);
        }
        return x>=0;
    }
    //------------------------------------------------------------------------------------------------------------------

    /**
     * @param sqlText sql
     * @param param   参数
     * @return 执行一个存储过程
     * @throws Exception 异常
     */
    @Override
    public boolean prepareExecute(String sqlText, Object[] param) throws Exception {
        Connection conn = null;
        CallableStatement statement = null;
        try {
            conn = getConnection(SoberEnv.READ_WRITE);
            statement = conn.prepareCall(sqlText);
            debugPrint(sqlText);
            for (int i = 0; i < param.length; i++) {
                debugPrint("prepared[" + (i + 1) + "]=" + param[i]);
                dialect.setPreparedStatementValue(statement, i + 1, param[i]);
            }
            return statement.execute();
        } catch (Exception e) {
            log.error("ERROR SQL:" + sqlText, e);
            e.printStackTrace();
            throw  e;
        } finally {
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
        }
    }

    /**
     * 更新一个存储过程
     *
     * @param sqlText sql
     * @param param   参数
     * @return update 返回， jdbc
     */
    @Override
    public int prepareUpdate(String sqlText, Object[] param) {
        Connection conn = null;
        CallableStatement statement = null;
        try {
            conn = getConnection(SoberEnv.READ_WRITE);
            statement = conn.prepareCall(sqlText);
            for (int i = 0; i < param.length; i++) {
                debugPrint("prepared[" + (i + 1) + "]=" + param[i]);
                dialect.setPreparedStatementValue(statement, i + 1, param[i]);
            }
            return statement.executeUpdate();
        } catch (Exception e) {
            log.error("ERROR SQL:" + sqlText, e);
            return -2;
        } finally {
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
        }
    }

    /**
     * @param sqlText sql
     * @param param   参数
     * @return 返回动态封装的对象列表
     */
    @Override
    public List<?> prepareQuery(String sqlText, Object[] param) {
        List<Object> result = new ArrayList<>();
        Connection conn = null;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        try {
            conn = getConnection(SoberEnv.READ_ONLY);
            debugPrint(sqlText);
            statement = conn.prepareCall(sqlText);
            if (!ArrayUtil.isEmpty(param)) {
                for (int i = 0; i < param.length; i++) {
                    debugPrint("prepared[" + (i + 1) + "]=" + param[i]);
                    dialect.setPreparedStatementValue(statement, i + 1, param[i]);
                }
            }
            JdbcUtil.setFetchSize(statement,500);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                //名称位_转换位驼峰命名方式
                Map<String, Object> beanMap = new HashMap<>();
                for (int n = 1; n <= metaData.getColumnCount(); n++) {
                    String field = StringUtil.underlineToCamel(metaData.getColumnLabel(n));
                    beanMap.put(field, dialect.getResultSetValue(resultSet, n));
                }
                result.add(ReflectUtil.createDynamicBean(beanMap));
            }
        } catch (Exception e) {
            log.error("检查 SQL:" + sqlText, e);
            e.printStackTrace();
        } finally {
            JdbcUtil.closeResultSet(resultSet);
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
        }
        return result;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * 验证bean
     *
     * @param obj 验证的bean
     * @throws Exception      其他错误
     * @throws ValidException 验证错误
     */
    @Override
    public void validator(Object obj) throws Exception {
        Map<String, String> result = new HashMap<>();
        ScriptRunner scriptRunner = new TemplateScriptEngine();
        TableModels soberTable = getSoberTable(obj.getClass());
        try {
            for (SoberColumn soberColumn : soberTable.getColumns()) {
                if (!soberColumn.isNotNull()) {
                    continue;
                }
                String dataType = soberColumn.getDataType();
                if (StringUtil.isNull(dataType)) {
                    continue;
                }
                Object value = BeanUtil.getProperty(obj, soberColumn.getName());
                if (value instanceof InputStream) {
                    continue;
                }
                boolean isNeed = soberColumn.isNotNull();
                if (value != null) {
                    isNeed = true;
                }
                if (!isNeed) {
                    continue;
                }
                String expression = dataType;
                if (expression.startsWith(" ")) {
                    expression = expression.trim();
                } else {
                    expression = soberColumn.getName() + StringUtil.DOT + expression;
                }
                if (!expression.endsWith(")")) {
                    expression = expression + "()";
                }
                if (value == null) {
                    value = StringUtil.empty;
                }
                scriptRunner.put(soberColumn.getName(), value);
                if (!ObjectUtil.toBoolean(scriptRunner.eval(expression, 0))) {
                    result.put(soberColumn.getName(), "value:" + value + " dataType:" + dataType);
                }
                scriptRunner.put(soberColumn.getName(), null);
            }
            if (!result.isEmpty()) {
                throw new ValidException(result, obj);
            }
        } finally {
            scriptRunner.exit();
        }
    }

    /**
     * 简单表达式查询
     * SSqlExpression
     *
     * @param aClass      类
     * @param term        条件
     * @param orderBy     排序
     * @param currentPage 页
     * @param totalCount  一页多少记录
     * @param loadChild   载入映射否
     * @return 执行结果
     */
    @Override
    public List<?> getExpressionList(Class<?>  aClass, String term, String orderBy, int currentPage, int totalCount, boolean loadChild) {
        if (totalCount > getMaxRows()) {
            totalCount = getMaxRows();
        }
        Criteria criteria = createCriteria(aClass);
        if (!StringUtil.isNull(term)) {
            criteria = SSqlExpression.getTermExpression(criteria, term);
        }
        if (!StringUtil.isNull(orderBy)) {
            criteria = SSqlExpression.getSortOrder(criteria, orderBy);
        }
        criteria = criteria.setCurrentPage(currentPage).setTotalCount(totalCount);
        return criteria.list(loadChild);
    }

    /**
     * 用来计算总数很方便
     * SSqlExpression
     * 简单表达式查询得到行数
     *
     * @param aClass 类
     * @param term   条件
     * @return 得到单一的返回
     */
    @Override
    public int getExpressionCount(Class<?> aClass, String term) {
        Criteria criteria = createCriteria(aClass);
        if (!StringUtil.isNull(term)) {
            criteria = SSqlExpression.getTermExpression(criteria, term);
        }
        return criteria.setProjection(Projections.rowCount()).intUniqueResult();
    }

    /**
     * 创建标准查询
     * @param cla 类对象
     * @return Criteria 查询器
     */
    @Override
    public Criteria createCriteria(Class<?> cla) {
        return new CriteriaImpl(cla, this);
    }

    //-----------------------------------------------------------------

    /**
     * 创建索引
     * @param tableName 表名
     * @param name 索引名称
     * @param field 字段
     * @return 是否创建成功
     * @throws Exception 异常
     */
    @Override
    public boolean createIndex(String databaseName, String tableName, String name, String field) throws Exception {
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, databaseName);
        valueMap.put(Dialect.KEY_TABLE_NAME, tableName);
        valueMap.put(Dialect.KEY_INDEX_NAME, name);
        valueMap.put(Dialect.KEY_INDEX_FIELD, field);
        if (name!=null)
        {
            valueMap.put(Dialect.KEY_IS_UNIQUE, name.toLowerCase().contains("_unique_"));
        } else
        {
            valueMap.put(Dialect.KEY_IS_UNIQUE, false);
        }

        String sqlText = dialect.processTemplate(Dialect.SQL_CREATE_TABLE_INDEX, valueMap);
        return execute(sqlText);
    }


    /**
     * 将表对象转换为实体对象，用于辅助代码
     * @param tableName 表名
     * @return 字段列表
     */
    @Override
    public  List<SoberColumn>  getTableColumns(String tableName) {
        return JdbcUtil.getTableColumns(this,tableName);
    }
    //-----------------------------------------------------------------
    /**
     * sql map 查询器,带拦截器等功能
     *
     * @return SqlMapClient
     */
    @Override
    public SqlMapClient buildSqlMap() {
        if (sqlMapClient==null)
        {
            synchronized (this)
            {
                sqlMapClient = new SqlMapClientImpl(getBaseSqlMap());
            }
        }
        return sqlMapClient;
    }

    /**
     *
     * @return 基础的查询器
     */
    @Override
    public SqlMapBase getBaseSqlMap() {
        if (sqlMapBase==null)
        {
            synchronized (this)
            {
                sqlMapBase = new SqlMapBaseImpl(this);
            }
        }
        return sqlMapBase;
    }
    /**
     * @param info 控制台输出SQL
     */
    @Override
    public void debugPrint(String info) {
        if (soberFactory.isShowsql()) {
            System.out.println(info);
        }
    }

    /**
     * 清除缓存所有数据
     * @param cla 类
     */
    @Override
    public void evict(Class<?> cla) {
        if (soberFactory.isUseCache()) {
            JSCacheManager.queryRemove(cla, cla.getName() + StringUtil.ASTERISK);
        }
    }

    /**
     * 清除缓存 中list 相关数据
     *
     * @param cla classes
     */
    @Override
    public void evictList(Class<?> cla) {
        if (soberFactory.isUseCache()) {
            JSCacheManager.queryRemove(cla, cla.getName() + SoberUtil.CACHE_TREM_LIST + StringUtil.ASTERISK);
        }
    }

    /**
     * 清除缓存 中load 相关数据
     * @param cla classes
     */
    @Override
    public void evictLoad(Class<?> cla) {
        if (soberFactory.isUseCache()) {
            JSCacheManager.queryRemove(cla, cla.getName() + SoberUtil.CACHE_TREM_LOAD + StringUtil.ASTERISK);
        }
    }


    /**
     * 清除缓存 中load 相关数据
     * @param cla 类型
     * @param field 字段
     * @param id id
     */
    @Override
    public void evictLoad(Class<?> cla, String field, Serializable id) {
        if (soberFactory.isUseCache()) {
            String cacheKey = SoberUtil.getLoadKey(cla, field, id, true);
            cacheKey = StringUtil.substringBefore(cacheKey,SoberUtil.CACHE_TREM_CHILD) + StringUtil.ASTERISK;
            JSCacheManager.queryRemove(cla, cacheKey);
        }
    }

    /**
     *
     * @param data 更新缓存数据
     * @param loadChild 是否为载入子对象
     */
    @Override
    public void updateLoadCache(Object data,boolean loadChild) {
        if (data==null)
        {
            return;
        }
        if (soberFactory.isUseCache()) {
            Class<?> cla = data.getClass();
            TableModels soberTable = getSoberTable(cla);
            Object id = BeanUtil.getProperty(data,soberTable.getPrimary());
            String cacheKey = SoberUtil.getLoadKey(cla, soberTable.getPrimary(), id, loadChild);
            JSCacheManager.put(cla,cacheKey,data);
        }
    }
}
