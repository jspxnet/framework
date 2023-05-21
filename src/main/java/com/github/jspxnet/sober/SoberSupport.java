/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober;

import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.sober.table.SqlMapConf;
import com.github.jspxnet.txweb.table.meta.BaseBillType;
import com.github.jspxnet.txweb.table.meta.OperatePlug;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.dialect.Dialect;
import com.github.jspxnet.sober.exception.ValidException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.sql.ResultSet;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-9
 * Time: 23:20:44
 */
public interface SoberSupport extends Serializable {
    /**
     * 简化自动生成一个sql 执行ID 名称,
     * 名称格式为 className.MethodName
     * @return 得到当前执行的方法名称
     */
    String getClassMethodName();
    /**
     *
     * @return sql 适配器
     */
    Dialect getDialect();
    /**
     *
     * @param soberFactory 数据工厂
     */
    void setSoberFactory(SoberFactory soberFactory);
    /**
     *
     * @return 得到数据工程对象
     */
    SoberFactory getSoberFactory();
    /**
     *
     * @param cla 类
     * @return 表结构模型
     */
    TableModels getSoberTable(Class<?> cla);
    /**
     *
     * @param tableName  类
     * @return 表结构模型
     */
    TableModels getTableModels(String tableName);
    /**
     *
     * @param cla  类对象
     * @param fieldName  字段名称
     * @return  返回枚举
     */
    JSONArray getFieldEnumType(Class<?> cla, String fieldName);
    /**
     *
     * @param tableName 表明
     * @param fieldName  字段名称
     * @return 返回枚举
     */
    JSONArray getFieldEnumType(String tableName, String fieldName);

    /**
     *
     * @param dto 是否包含DTO
     * @param extend  0:所有;1:可扩展;2:不可扩展
     * @return  得到所有表结构的模型
     */
    Map<String,TableModels> getAllTableModels(boolean dto,int extend);

    /**
     *
     * @param cla 类对象
     * @return 表明
     */
    String getTableName(Class<?> cla);
    /**
     *
     * @return 默认的最大返回行数
     */
    int getMaxRows();

    /**
     * @param aClass       类
     * @param <T> 类型
     * @param field        字段名称
     * @param serializable 字段值
     * @param loadChild    是否载入关联
     * @return 返回对象，如果为空就创建对象，不会有null 返回
     */
    <T> T load(Class<T> aClass, Serializable field, Serializable serializable, boolean loadChild);

    /**
     *  load from id
     * @param aClass class 类对象
     * @param serializable id
     * @param <T> 类型
     * @return query a object 返回对象
     */
    <T> T get(Class<T> aClass, Serializable serializable);

    /**
     * ID 得到对象
     * load from id and map bean
     * @param aClass 类
     * @param serializable Id
     * @param loadChild 载入子对象
     * @param <T> 类型
     * @return Object 得到对象
     */
    <T> T get(Class<T> aClass, Serializable serializable, boolean loadChild);
    /**
     * 查询字段返回一个对象,不从缓存中起，但是查询后放入换成
     * 如果为空，就返回空，不创建对象，load方式会是用缓存来减少查询，也会创建null对象返回
     *
     * @param aClass       类
     * @param <T> 类型
     * @param field        字段
     * @param serializable 字段值
     * @param loadChild 载入子对象
     * @return Object 得到对象
     */
    <T> T get(Class<T> aClass, Serializable field, Serializable serializable, boolean loadChild);
    /**
     * @param aClass       类
     * @param serializable 字段值
     * @param <T>          类对象
     * @return 返回对象，如果为空就创建对象，不会有null 返回
     */
    <T> T load(Class<T> aClass, Serializable serializable);
    /**
     * @param aClass       类
     * @param <T> 类型
     * @param serializable 字段值
     * @param loadChild    是否载入关联
     * @return 返回对象，如果为空就创建对象，不会有null 返回
     */
    <T> T load(Class<T> aClass, Serializable serializable, boolean loadChild);
    /**
     *
     * @param aClass 返回实体
     * @param serializables 字段值
     * @param <T> 类型
     * @return 查询返回
     */
    <T> List<T> load(Class<T> aClass, Serializable[] serializables);

    /**
     *
     * @param aClass 返回实体
     * @param values 字段值
     * @param loadChild 是否载入映射
     * @param <T> 类型
     * @return 返回列表
     */
    <T> List<T> load(Class<T> aClass, Collection<?> values, boolean loadChild);

    /**
     *
     * @param aClass 返回实体
     * @param field 查询字段
     * @param values 字段值
     * @param loadChild 是否载入映射
     * @param <T> 类型
     * @return 返回列表
     */
    <T> List<T> load(Class<T> aClass, String field, Collection<?> values, boolean loadChild);

    /**
     *
     * @param aClass 返回实体
     * @param field 查询字段
     * @param serializables 字段值
     * @param loadChild 是否载入映射
     * @param <T> 类型
     * @return 查询返回
     */
    <T> List<T> load(Class<T> aClass, String field, Serializable[] serializables, boolean loadChild);

    /**
     *
     * @param tableName 表名称
     * @return 单据类型列表
     */
    BaseBillType getDefaultBaseBillType(String tableName);
    /**
     * 判断是否存在单号
     * @param tableName 表名
     * @param billNo  单号
     * @return 是否存在,int类型,为数量
     */
    int existBillNo(String tableName, String billNo);

    /**
     *
     * @param object 实体对象
     * @return 保存对象
     * @throws Exception 异常
     */
    int save(Object object) throws Exception;
    /**
     * @param object 保存对象
     * @param child  保持子对象
     * @return 保存一个对象
     * @throws Exception 异常
     */
    int save(Object object, boolean child) throws Exception;
    /**
     * @param collection 保存一个列表
     * @return 返回保持数量
     * @throws Exception      验证错误
     * @throws ValidException 其他错误
     */
    int save(Collection<?> collection) throws Exception;
    /**
     *
     * @param collection 保存一个列表
     * @param child 子对象
     * @return  返回保持数量
     * @throws Exception 验证错误
     */
    int save(Collection<?> collection, boolean child) throws Exception;

    /**
     *
     * @param collection  批量快速保持 集合
     * @return 更新数量,如果错误 返回 负数
     * @throws Exception 异常
     */
    int batchSave(Collection<?> collection) throws Exception;

    /**
     * 删除对象
     *
     * @param o 对象
     * @return boolean
     */
    int delete(Object o);
    /**
     * @param aClass       类
     * @param serializable id
     * @return 删除对象
     */
    int delete(Class<?> aClass, Serializable serializable);
    /**
     * 更具字段删除一个对象,或一组对象
     *
     * @param aClass       类
     * @param field        字段
     * @param serializable 字段值
     * @return 是否成功
     */
    int delete(Class<?> aClass, String field, Serializable serializable);
    /**
     * 级联删除,不删除ManyToOne,只删除OneToOne 和 OneToMany
     *
     * @param o        对象
     * @param delChild 是否删除子对象
     * @return int 是否成功
     * @throws Exception 异常
     */
    int delete(Object o, boolean delChild) throws Exception;
    /**
     * @param aClass   类
     * @param ids      id 列表
     * @param delChild 删除关联
     * @return 删除
     */
    int delete(Class<?> aClass, Object[] ids, boolean delChild);
    /**
     * 级联删除,不删除ManyToOne,只删除OneToOne 和 OneToMany
     *
     * @param aClass       删除的类
     * @param serializable 值
     * @param delChild     是否删除映射对象
     * @return boolean 是否成功
     */
    int delete(Class<?> aClass, Serializable serializable, boolean delChild);
    /**
     * 级联方式删除对象,只删除一层
     *
     * @param aClass       删除对象
     * @param field        删除字段
     * @param serializable 字段值
     * @param term 条件
     * @param delChild     删除映射对象
     * @return int 是否成功
     */

    int delete(Class<?> aClass, String field, Serializable serializable, String term, boolean delChild);
    /**
     * 删除映射关系的对象 ManyToOne 关系不删除
     *
     * @param o 对象
     * @return int 是否成功
     */
    int deleteNexus(Object o) ;

    /**
     *  删除一堆对象
     * @param collection 删除激活
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteAll(Collection<?> collection) throws Exception;
    /**
     * 单个对象查询返回
     *
     * @param sql      sql
     * @param valueMap map参数
     * @return Object 单个对象
     */
    Object getUniqueResult(String sql, Map<String, Object> valueMap);

    /**
     *
     * @param collection 集合
     * @return 是否成功
     * @throws Exception 异常
     */
    int update(Collection<Object> collection) throws Exception;
    /**
     * 更具ID更新一个对象
     *
     * @param object 对象
     * @return boolean
     * @throws Exception 异常
     */
    int update(Object object) throws Exception;
    /**
     * @param object      查询对象
     * @param updateFiled 更新一个字段
     * @return 指定更新字段, 特殊不验证了
     * @throws Exception 异常
     */
    int update(Object object, String[] updateFiled) throws Exception;
    /**
     *
     * @param sql 简单sql
     * @return sql执行更新
     * @throws Exception 异常
     */
    int update(String sql) throws Exception;
    /**
     * @param sqlText 使用sql直接更新,参数 ？ 的jdbc原生形式
     * @param params  参数
     * @return 更新数量
     * @throws Exception 异常
     */
    int update(String sqlText, Object[] params) throws Exception;
    /**
     *
     * @param sqlText 简单的sql
     * @return 执行一个 execute
     * @throws Exception 异常
     */
    boolean execute(String sqlText) throws Exception;
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
    boolean execute(String sqlText, Object[] params) throws Exception;


    int[] batchUpdate(SqlMapConf sqlMapConf, Map<String, Object> valueMap) throws SQLException;

    /**
     * 执行一个sql
     *
     * @param cla     类 对映配置中的map命名空间
     * @param sqlText sql
     * @param params  支持类型 Object[] or HashMap  String,Object,这里是留给参数对象的,所以params没有类型
     * @return 执行情况
     * @throws Exception 异常
     */
    boolean execute(Class<?> cla, String sqlText, Object params) throws Exception;

    /**
     * 先判断是否存在,存在就使用更新,否则增加
     * @param object 对象
     * @return 保存是否成功
     * @throws Exception 异常
     */
    int saveOrUpdate(Object object) throws Exception;

    /**
     * 载入映射对象
     * @param soberTable mapping
     * @param list list
     */
    void loadNexusList(TableModels soberTable, List<?> list);
    /**
     * 载入关联列表
     *
     * @param cla  类
     * @param list 对象实体列表
     */
    void loadNexusList(Class<?> cla, List<?> list);
    /**
     *
     * @param cla 类对象
     * @return 将表头数据返回给前端
     */
    List<SoberColumn> getColumnModels(Class<?> cla);
    /**
     * 设置字段数据(无映射关系)
     *
     * @param tClass    类型
     * @param resultSet jdbc数据集合
     * @param <T>       类型
     * @return 载入对应
     * @throws Exception 异常
     */
    <T> T loadColumnsValue(Class<T> tClass, ResultSet resultSet) throws Exception;
    /**
     * @param soberTable 结构
     * @param result     对象
     */
    void loadNexusValue(TableModels soberTable, Object result);

    /**
     *
     * @param sql sql
     * @param param 参数数组
     * @param currentPage 页数
     * @param totalCount 返回行数
     * @return 查询返回列表
     */
    List<?> query(String sql, Object[] param, int currentPage, int totalCount);
    /**
     * 查询返回列表
     * 使用jdbc完成,比较浪费资源
     *
     * @param cla class
     * @param sql  sql
     * @param param 参数
     * @param currentPage page number
     * @param totalCount rows
     * @param loadChild load map object
     * @param <T> 类型
     * @return List object list
     */
    <T> List<T> query(Class<T> cla, String sql, Object[] param, int currentPage, int totalCount, boolean loadChild);
    /**
     * 查询返回封装好的列表
     *
     * @param cla     要封装返回的对象
     * @param sqlText SQL
     * @param param   参数
     * @param <T> 类型
     * @return 封装好的查询对象
     */
    <T> List<T> query(Class<T> cla, String sqlText, Object[] param);

    /**
     * @param sql sql语句
     * @return 单一返回对象
     */
    Object getUniqueResult(String sql);
    /**
     * @param sql sql语句
     * @param params   参数数组
     * @return 单一返回对象
     */
    Object getUniqueResult(String sql, Object[] params);
    /**
     * @param sql sql语句
     * @param o   参数对象
     * @return 单一返回对象
     */
    Object getUniqueResult(String sql, Object o);
    /**
     * @param cla 类
     * @param sql sql
     * @param o   对象
     * @return 返回单一对象
     */
    Object getUniqueResult(Class<?> cla, String sql, Object o);
    /**
     *  计算合计,这个标签会占用大量的CPU计算资源，谨慎使用
     * @param soberTable 结果关系表
     * @param obj 对象
     * @return 计算结果
     */
    Object calcUnique(TableModels soberTable, Object obj);
    /**
     * 创建标准查询
     *
     * @param cla 类对象
     * @return Criteria 查询器
     */
    Criteria createCriteria(Class<?> cla);

    //-----------------------------------------------------------------
    /**
     * 添加字段
     * @param cls 实体类
     * @param soberColumn 列对象
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean addColumn(Class<?> cls, SoberColumn soberColumn) throws Exception;
    /**
     *  修改字段
     * @param cls 实体类
     * @param soberColumn 列对象
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean modifyColumn(Class<?> cls, SoberColumn soberColumn) throws Exception;
    /**
     *  删除字段
     * @param cls 实体类
     * @param soberColumn 列对象
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean dropColumn(Class<?> cls, SoberColumn soberColumn) throws Exception;

    /**
     * 创建索引
     * @param tableName 表名
     * @param name 索引名称
     * @param field 字段
     * @return 是否创建成功
     * @throws Exception 异常
     */
    boolean createIndex(String tableName, String name, String field) throws Exception;

    /**
     * 将表对象转换为实体对象，用于辅助代码
     * @param tableName 表名
     * @return 字段列表
     */
    List<SoberColumn>  getTableColumns(String tableName);

    /**
     * sql map 查询器
     *
     * @return SqlMapClient
     */
    SqlMapClient buildSqlMap();


    /**
     *  有些数据库建表要带上库名
     * @param createClass 生成表创建sql
     * @param soberTable 库名等信息
     * @return 得到创建表的SQL
     */
   // String getCreateTableSql(Class<?> createClass, TableModels soberTable);

    /**
     * 删除表
     *
     * @param cla 删除表
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean dropTable(Class<?> cla) throws Exception;
    /**
     * @param sqlText sql
     * @param param   参数
     * @return 返回动态封装的对象列表
     */
    List<?> prepareQuery(String sqlText, Object[] param);
    /**
     * 更新一个存储过程
     *
     * @param sqlText sql
     * @param param   参数
     * @return update 返回， jdbc
     */
    int prepareUpdate(String sqlText, Object[] param);
    /**
     *
     * @param obj 对象
     * @param field 字段
     * @param num 加的数字
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean updateFieldAddNumber(Object obj, String field, int num) throws Exception;

    /**
     * @param sqlText sql
     * @param param   参数
     * @return 执行一个存储过程
     * @throws Exception 异常
     */
    boolean prepareExecute(String sqlText, Object[] param) throws Exception;
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
    List<?> getExpressionList(Class<?>   aClass, String term, String orderBy, int currentPage, int totalCount, boolean loadChild);
    /**
     * 用来计算总数很方便
     * SSqlExpression
     * 简单表达式查询得到行数
     *
     * @param aClass 类
     * @param term   条件
     * @return 得到单一的返回
     */
    int getExpressionCount(Class<?> aClass, String term);

    //--------------------
    /**
     * 表是否存在
     *
     * @param soberTable bean对象是否存在表
     * @return 返回是否存在
     */
    boolean tableExists(TableModels soberTable);

    /**
     * @param cla 得到最大ID
     * @return ID数
     */
    long getTableMaxId(Class<?> cla);
    /**
     * @return 返回表名称数组
     */
    String[] getTableNames();
    /**
     * @param databaseName 数据库名称
     * @return 得到数据库大小
     */
    long getDataBaseSize(String databaseName);
    /**
     * @param cla   类对象
     * @param start 序列值
     * @return 设置序列开始值
     * @throws Exception 异常
     */
    boolean alterSequenceStart(Class<?> cla, long start) throws Exception;
    /**
     * @param cla 类对象
     * @return 得到数据库序列名称
     */
    String getSequenceName(Class<?> cla);
    /**
     * 验证bean
     *
     * @param obj 验证的bean
     * @throws Exception      其他错误
     * @throws Exception 异常
     */
    void validator(Object obj) throws Exception;
    /**
     * @param cla   类对象
     * @param field 字段名称
     * @return 判断是否存在此字段
     */
    boolean containsField(Class<?> cla, String field);

    /**
     *
     * @return 基础的查询器
     */
    SqlMapBase getBaseSqlMap();

    /**
     * @param info 控制台输出SQL
     */
    void debugPrint(String info);
    /**
     * 清除缓存所有数据
     * @param cla 类
     */
    void evict(Class<?> cla);
    /**
     * 清除缓存 中list 相关数据
     *
     * @param cla classes
     */
    void evictList(Class<?> cla);
    /**
     * 清除缓存 中load 相关数据
     * @param cla classes
     */
    void evictLoad(Class<?> cla);

    /**
     * 清除缓存 中load 相关数据
     * @param cla 类型
     * @param field 字段
     * @param id id
     */
    void evictLoad(Class<?> cla, String field, Serializable id);

    void evictTableModels(Class<?> cla);

    /**
     *
     * @param data 更新缓存数据
     * @param loadChild 是否为载入子对象
     */
    void updateLoadCache(Object data, boolean loadChild);

    //----------------锁定
    /**
     *
     * @param tableMeta 表单类
     * @return 插件列表
     */
    List<OperatePlug> getOperatePlugList(Class<?> tableMeta);

    boolean lock(Object obj) throws Exception;

    boolean isLock(Object obj) throws Exception;

    boolean unLock(Object obj);
}