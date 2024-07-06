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


import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.cache.*;
import com.github.jspxnet.enums.BoolEnumType;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sober.*;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.NullClass;
import com.github.jspxnet.sober.table.SoberFieldEnum;
import com.github.jspxnet.sober.table.SqlMapConf;
import com.github.jspxnet.txweb.table.OptionBundle;
import com.github.jspxnet.txweb.table.meta.BaseBillType;
import com.github.jspxnet.txweb.table.meta.OperatePlug;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.projection.Projections;
import com.github.jspxnet.sober.dialect.Dialect;
import com.github.jspxnet.sober.exception.ValidException;
import com.github.jspxnet.sober.impl.CriteriaImpl;
import com.github.jspxnet.sober.impl.SqlMapBaseImpl;
import com.github.jspxnet.sober.impl.SqlMapClientImpl;
import com.github.jspxnet.sober.ssql.SSqlExpression;
import com.github.jspxnet.sober.util.JdbcUtil;
import com.github.jspxnet.sober.util.LockUtil;
import com.github.jspxnet.sober.util.SoberUtil;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.io.Serializable;
import java.lang.reflect.Field;
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

    private SqlMapClient sqlMapClient = null;
    private SqlMapBase sqlMapBase = null;
    private SoberFactory soberFactory;

    public JdbcOperations() {

    }
    /**
     * 简化自动生成一个sql 执行ID 名称,
     * 名称格式为 className.MethodName
     * @return 得到当前执行的方法名称
     */
    @Override
    public String getClassMethodName() {
        return ClassUtil.getClassMethodName(Thread.currentThread().getStackTrace());
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
     * @return sql 适配器
     */
    @Override
    public Dialect getDialect() {
        return this.soberFactory.getDialect();
    }
    /**
     *
     * @param soberFactory 数据工厂
     */
    @Override
    public void setSoberFactory(SoberFactory soberFactory) {
        this.soberFactory = soberFactory;
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
     * @param tableName  类
     * @return 表结构模型
     */
    @Override
    public TableModels getTableModels(String tableName) {
        return soberFactory.getTableModels(tableName, this);
    }

    /**
     *
     * @param cla  类对象
     * @param fieldName  字段名称
     * @return  返回枚举
     */
    @Override
    public JSONArray getFieldEnumType(Class<?> cla, String fieldName)
    {
        TableModels tableModels = soberFactory.getTableModels(cla, this);
        if (tableModels==null)
        {
            return null;
        }
        return getFieldEnumType(tableModels.getName(), fieldName);
    }
    /**
     *
     * @param tableName 表明
     * @param fieldName  字段名称
     * @return 返回枚举
     */
    @Override
    public JSONArray getFieldEnumType(String tableName, String fieldName)
    {
        TableModels tableModels = soberFactory.getTableModels(tableName, this);
        if (tableModels==null)
        {
            return null;
        }
        Class<?> cla = tableModels.getEntity();
        if (cla==null)
        {
            return null;
        }

        Field field = ClassUtil.getDeclaredField(cla,fieldName);
        if (field==null)
        {
            return null;
        }
        Column column = field.getAnnotation(Column.class);
        if (column==null)
        {
            return null;
        }
        //1:小;2:中;3:大
        if (!NullClass.class.getName().equals(column.enumType().getName()))
        {
            Object[] enumObj = column.enumType().getEnumConstants();
            return new JSONArray(enumObj);
        }
        //文本方式
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
                //这里有两种格式,先判断是不是简写的
                if (column.option()!=null && !column.option().contains(StringUtil.COLON))
                {
                    //简写方式  男;女
                    String[] dataArray = StringUtil.split(column.option(),StringUtil.SEMICOLON);
                    if (!ObjectUtil.isEmpty(dataArray))
                    {
                        for (String value:dataArray)
                        {
                            JSONObject json = new JSONObject();
                            json.put("value",value);
                            json.put("name",value);
                            temp.add(json);
                        }
                    }
                }
                else {
                    //标准格式
                    StringMap<String,String> stringMap = new StringMap<>();
                    stringMap.setKeySplit(StringUtil.COLON);
                    stringMap.setLineSplit(StringUtil.SEMICOLON);
                    stringMap.setString(column.option());
                    for (String key:stringMap.keySet())
                    {
                        JSONObject json = new JSONObject();
                        json.put("value",key);
                        json.put("name",stringMap.getString(key));
                        temp.add(json);
                    }
                }
            }
            return new JSONArray(temp);
        }

        SoberColumn soberColumn = tableModels.getColumn(fieldName);
        if (soberColumn!=null&&soberColumn.isConfEnum())
        {
            //数据库绑定方式
            //未了实现低耦合，这里还是,在去查询数据库
            SoberFieldEnum soberFieldEnum = JdbcUtil.getSoberFieldEnum(this,tableName,fieldName);
            if (soberFieldEnum==null)
            {
                return null;
            }

            List<OptionBundle> optionBundles = JdbcUtil.getOptionBundleList(this,soberFieldEnum.getGroupCode(),soberFieldEnum.getNamespace());
            if (ObjectUtil.isEmpty(optionBundles))
            {
                return null;
            }
            List<JSONObject> temp = new ArrayList<>();
            for (OptionBundle bundle:optionBundles)
            {
                JSONObject json = new JSONObject();
                json.put("value",bundle.getCode());
                json.put("name",bundle.getName());
                json.put("selected",bundle.getSelected());
                json.put("parentCode",bundle.getParentCode());
                json.put("groupCode",bundle.getGroupCode());
                json.put("namespace",bundle.getNamespace());
                temp.add(json);
            }
            return new JSONArray(temp);
        }
        return null;
    }
    /**
     * 并且根据数据模型自动创建缓存
     * @param dto 是否包含DTO
     * @param extend  类型,0:所有
     * @return  得到所有表结构的模型
     */
    @Override
    public Map<String,TableModels> getAllTableModels(boolean dto,int extend) {
        return JdbcUtil.getAllTableModels(soberFactory,dto,extend);
    }
    /**
     *
     * @param cla 类对象
     * @return 表明
     */
    @Override
    public String getTableName(Class<?> cla) {
        return JdbcUtil.getTableName(this,cla);
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
    public <T> T loadColumnsValue(Class<T> tClass, ResultSet resultSet) throws Exception {
        return JdbcUtil.loadColumnsValue(this, getDialect(),tClass,resultSet);
    }

    /**
     *  计算合计,这个标签会占用大量的CPU计算资源，谨慎使用
     * @param soberTable 结果关系表
     * @param inObj 对象
     * @return 计算结果
     */
    @Override
    public Object calcUnique(TableModels soberTable, Object inObj)  {
        return JdbcUtil.calcUnique(this, getDialect(),soberTable,inObj);
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
        JdbcUtil.loadNexusList(this, soberTable, list);
    }
    /**
     * @param soberTable 结构
     * @param result     对象
     */
    @Override
    public void loadNexusValue(TableModels soberTable, Object result) {
        JdbcUtil.loadNexusValue(this,soberTable,result);
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
        return JdbcUtil.load(this,aClass,field,serializable,loadChild);
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
    public <T> T get(Class<T> aClass, Serializable field, Serializable serializable, boolean loadChild)
    {
        return JdbcUtil.get(this, getDialect(),aClass,field,serializable,loadChild);
    }


    /**
     *
     * @param aClass 返回实体
     * @param serializable 字段值
     * @param <T> 类型
     * @return 查询返回
     */
    @Override
    public <T> List<T> load(Class<T> aClass, Serializable[] serializable)
    {
        TableModels soberTable = getSoberTable(aClass);
        String field = soberTable.getPrimary();
        return load(aClass,  field, serializable, true);
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
     * @param serializable 字段值
     * @param loadChild 是否载入映射
     * @param <T> 类型
     * @return 查询返回
     */
    @Deprecated
    @Override
    public <T> List<T> load(Class<T> aClass, String field, Serializable[] serializable, boolean loadChild)
    {
        //载入一个ID列表
        Criteria criteria = createCriteria(aClass);
        criteria = criteria.add(Expression.in(field, serializable));
        criteria = criteria.setCurrentPage(1).setTotalCount(getMaxRows());
        return criteria.list(loadChild);
    }
    /**
     *
     * @param tableName 表名称
     * @return 单据类型列表
     */
    @Override
    public BaseBillType getDefaultBaseBillType(String tableName)
    {
        //载入一个ID列表
        Criteria criteria = createCriteria(BaseBillType.class);
        criteria = criteria.add(Expression.eq("tableName", tableName));
        criteria = criteria.add(Expression.eq("defType", BoolEnumType.YES.getValue()));
        criteria = criteria.setCurrentPage(1).setTotalCount(1);
        List<BaseBillType> list = criteria.list(false);
        if (ObjectUtil.isEmpty(list))
        {
            return null;
        }
        return list.get(0);
    }

    /**
     * 判断是否存在单号
     * @param tableName 表名
     * @param billNo  单号
     * @return 是否存在,int类型,为数量
     */
    @Override
    public int existBillNo(String tableName, String billNo)
    {
        //载入一个ID列表
        String SQL = "SELECT count(1) as num FROM "+tableName+" where billNo=" + StringUtil.quoteSql(billNo);
        return ObjectUtil.toInt(getUniqueResult(SQL));
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
      return JdbcUtil.save(this,getDialect(),object,child);
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
            return 0;
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
     *
     * @param collection  批量快速保持 集合
     * @return 更新数量,如果错误 返回 负数
     * @throws Exception 异常
     */
    @Override
    public int batchSave(Collection<?> collection) throws Exception {
        return JdbcUtil.batchSave(this,collection);
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
        return JdbcUtil.delete(this,aClass,field,serializable);

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
        return JdbcUtil.delete(this,aClass,field,serializable,term,delChild);
    }

    /**
     * 删除映射关系的对象 ManyToOne 关系不删除
     *
     * @param o 对象
     * @return boolean 是否成功
     */
    @Override
    public int deleteNexus(Object o) {
       return JdbcUtil.deleteNexus(this,o);
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
       return JdbcUtil.update(this,getDialect(),object);
    }

    /**
     * @param object      查询对象
     * @param updateFiled 更新一个字段
     * @return 指定更新字段, 特殊不验证了
     * @throws Exception 异常
     */
    @Override
    public int update(Object object, String[] updateFiled) throws Exception {
       return JdbcUtil.update(this,getDialect(),object,updateFiled);
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
        return JdbcUtil.update(this,getDialect(),sqlText,params);
    }




    /**
     * 批量更新，这个方法主要为了提高速度
     * @param sqlMapConf  sql 配置
     * @param valueMap  变量
     * @return 执行结果
     * @throws SQLException 异常
     */
    @Override
    public int[] batchUpdate(SqlMapConf sqlMapConf, Map<String, Object> valueMap) throws SQLException
    {
        return JdbcUtil.batchUpdate(this,sqlMapConf,valueMap) ;
    }

    /**
     * 批量更新
     * @param template sql模版
     * @param paramList 参数对象
     * @return 执行结果
     * @throws SQLException 异常
     */
    @Override
    public int[] batchUpdate(String template, List<?> paramList) throws SQLException
    {
        return JdbcUtil.batchUpdate(this,template,paramList) ;
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
        return JdbcUtil.execute(this,getDialect(),cla,sqlText,params);
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
        return JdbcUtil.execute(this,sqlText,params);
    }


    /**
     * 先判断是否存在,存在就使用更新,否则增加
     * @param object 对象
     * @return 保存是否成功
     * @throws Exception 异常
     */
    @Override
    public int saveOrUpdate(Object object) throws Exception {
        return JdbcUtil.saveOrUpdate(this,getDialect(),object);
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
    public <T> List<T> query(Class<T> cla, String sql, Object[] param, int currentPage, int totalCount, boolean loadChild) {
        return JdbcUtil.query(this,getDialect(),cla,sql,param,currentPage,totalCount,loadChild);
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
    public <T> List<T> query(Class<T> cla, String sql, Object[] param) {
        return JdbcUtil.query(this,getDialect(),cla,sql,param);
    }

    /**
     * @param sqlText     sql
     * @param param       参数数组
     * @param currentPage 页数
     * @param totalCount  返回行数
     * @return List  查询返回列表
     */
    @Override
    public List<?> query(String sqlText, Object[] param, int currentPage, long totalCount) {
        return JdbcUtil.query(this,getDialect(),sqlText,param,currentPage,totalCount);
    }

    @Override
    public List<?> query(String sqlText, Object[] param, int currentPage, int totalCount) {
        return JdbcUtil.query(this,getDialect(),sqlText,param,currentPage,totalCount);
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
        return JdbcUtil.getUniqueResult(this,getDialect(),sqlText,param);
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
       return JdbcUtil.getUniqueResult(this,getDialect(),sql,valueMap);
    }

    /**
     * 删除一堆对象
     * @param collection 删除激活
     * @throws Exception 异常
     */
    @Override
    public boolean deleteAll(Collection<?> collection) throws Exception {
         return JdbcUtil.deleteAll(this,getDialect(),collection);
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
        return execute(getDialect().processTemplate(Dialect.SQL_DROP_TABLE, valueMap), null);
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
        Object o = getUniqueResult(getDialect().processTemplate(Dialect.FUN_TABLE_EXISTS, valueMap));
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
        return ObjectUtil.toLong(getUniqueResult(getDialect().processTemplate(Dialect.TABLE_MAX_ID, valueMap)));
    }

    /**
     * @param databaseName 数据库名称
     * @return 得到数据库大小
     */
    @Override
    public long getDataBaseSize(String databaseName) {
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, databaseName);
        return ObjectUtil.toLong(getUniqueResult(getDialect().processTemplate(Dialect.DATABASE_SIZE, valueMap)));
    }

    /**
     * @return 返回表名称数组
     */
    @Override
    public String[] getTableNames() {
        return SoberUtil.getTableNames(this,getDialect());
    }

    /**
     * @param cla 类对象
     * @return 得到数据库序列名称
     */
    @Override
    public String getSequenceName(Class<?> cla) {
        if (!getDialect().supportsSequenceName()) {
            return null;
        }
        TableModels soberTable = getSoberTable(cla);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_PRIMARY_KEY, soberTable.getPrimary());
        Object o = getUniqueResult(getDialect().processTemplate(Dialect.SEQUENCE_NAME, valueMap));
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
       return SoberUtil.alterSequenceStart(this,cla,start);
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
        return JdbcUtil.prepareExecute(this,sqlText,param);
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
        return JdbcUtil.prepareUpdate(this,getDialect(),sqlText,param);
    }

    /**
     * @param sqlText sql
     * @param param   参数
     * @return 返回动态封装的对象列表
     */
    @Override
    public List<?> prepareQuery(String sqlText, Object[] param) {
        return JdbcUtil.prepareQuery(this,getDialect(),sqlText, param);
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
        JdbcUtil.validator(this,obj);
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
     * 添加字段
     * @param cls 实体类
     * @param soberColumn 列对象
     * @return 是否成功
     * @throws Exception 异常
     */
    @Override
    public boolean addColumn(Class<?> cls,SoberColumn soberColumn) throws Exception {
        return SoberUtil.addColumn(this,cls,soberColumn);
    }

    /**
     *  修改字段
     * @param cls 实体类
     * @param soberColumn 列对象
     * @return 是否成功
     * @throws Exception 异常
     */
    @Override
    public boolean modifyColumn(Class<?> cls,SoberColumn soberColumn) throws Exception {
        return SoberUtil.modifyColumn(this,cls,soberColumn);
    }

    /**
     *  删除字段
     * @param cls 实体类
     * @param soberColumn 列对象
     * @return 是否成功
     * @throws Exception 异常
     */
    @Override
    public boolean dropColumn(Class<?> cls,SoberColumn soberColumn) throws Exception {
        return SoberUtil.dropColumn(this,cls,soberColumn);
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
    public boolean createIndex(String tableName, String name, String field) throws Exception {
       return SoberUtil.createIndex(this,getDialect(),tableName,name,field);
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


    /**
     * 通过sql 得到字段信息
     * @param sql sql
     * @return 字段信息
     */
    @Override
    public  List<SoberColumn>  getSqlColumns(String sql) {
        return JdbcUtil.getSqlColumns(this,sql);
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
     * @param cla 类模型
     */
    @Override
    public void evictTableModels(Class<?> cla) {
        soberFactory.evictTableModels(cla);
        JSCacheManager.queryRemove(DefaultCache.class, Environment.KEY_SOBER_TABLE_CACHE+"_*");
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

    /**
     *
     * @param tableMeta 表单类
     * @return 插件列表
     */
    @Override
    public List<OperatePlug> getOperatePlugList(Class<?> tableMeta)
    {
        return SoberUtil.getOperatePlugList(this,tableMeta);
    }
    //----------------锁定
    @Override
    public boolean lock(Object obj) throws Exception {
        return LockUtil.lock(this,obj);
    }
    @Override
    public boolean isLock(Object obj) {
        return LockUtil.isLock(this,obj);
    }

    @Override
    public boolean unLock(Object obj) {
        return LockUtil.unLock(this,obj);
    }
}
