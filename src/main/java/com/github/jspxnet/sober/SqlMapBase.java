package com.github.jspxnet.sober;

import com.github.jspxnet.sober.annotation.SqlMap;
import com.github.jspxnet.sober.dialect.Dialect;
import com.github.jspxnet.sober.enums.ExecuteEnumType;
import com.github.jspxnet.sober.table.SqlMapConf;

import java.util.List;
import java.util.Map;

public interface SqlMapBase {
    /**
     *
     * @return jdbc查询器
     */
    SoberSupport getSoberSupport();
    /**
     *
     * @return 数据库方言
     */
    Dialect getDialect();
    /**
     *
     * @param namespace  命名空间
     * @param exeId 执行id
     * @param executeEnumType 执行方式
     * @param sqlMap 配置
     * @return sqlMap配置
     * @throws Exception 异常
     */
    SqlMapConf getSqlMapConf(String namespace, String exeId, ExecuteEnumType executeEnumType, SqlMap sqlMap) throws Exception;
    /**
     * @param o bean对象
     * @return 对象转换为Map提供查询参数
     */
    Map<String, Object> getValueMap(Object o);
    /**
     * @param sqlMapConf sql配置
     * @param o          参数对象
     * @return 返回单一对象
     */
    Object getUniqueResult(SqlMapConf sqlMapConf, Object o);
    /**
     * 单个对象查询返回
     *
     * @param sqlMapConf sql配置
     * @param valueMap   查询参数
     * @return Object   返回对象
     */
    Object getUniqueResult(SqlMapConf sqlMapConf, Map<String, Object> valueMap);
    /**
     * batis 方式查询返回
     *
     * @param sqlMapConf  sql配置
     * @param o           查询对象条件
     * @param currentPage 当前页数
     * @param totalCount  最大行数
     * @param loadChild   是否载入
     * @param rollRows    是否滚动
     * @param <T>         返回类型
     * @return 返回列表
     * @throws Exception 异常
     */
    <T> List<T> query(SqlMapConf sqlMapConf, Object o, int currentPage, int totalCount, boolean loadChild, boolean rollRows) throws Exception;
    /**
     * @param sqlMapConf sql配置
     * @param valueMap   参数对象
     * @param <T>        类
     * @return 返回查询列表
     * @throws Exception 异常
     */
    <T> List<T> query(SqlMapConf sqlMapConf, Map<String, Object> valueMap) throws Exception;
    /**
     * @param sqlMapConf sql配置
     * @param valueMap   参数对象
     * @param cls        类
     * @param <T>        类型
     * @return 返回查询列表
     * @throws Exception 异常
     */
    <T> List<T> query(SqlMapConf sqlMapConf, Map<String, Object> valueMap, Class<T> cls) throws Exception;
    /**
     * @param sqlMapConf  sql配置
     * @param valueMap    参数对象
     * @param currentPage 页数
     * @param totalCount  返回行数
     * @param loadChild   载入子对象
     * @param cls         返回类型
     * @param <T>         类
     * @return 返回查询列表
     * @throws Exception 异常
     */
    <T> List<T> query(SqlMapConf sqlMapConf, Map<String, Object> valueMap, int currentPage, int totalCount, boolean loadChild, Class<T> cls) throws Exception;
    /**
     * @param sqlMapConf  sql配置
     * @param valueMap    参数对象
     * @param currentPage 页数
     * @param totalCount  页数
     * @param loadChild   返回行数
     * @param rollRows    是否行滚
     * @param <T>         类
     * @return 返回查询列表
     * @throws Exception 异常
     */
    <T> List<T> query(SqlMapConf sqlMapConf, Map<String, Object> valueMap, int currentPage, int totalCount, boolean loadChild, boolean rollRows) throws Exception;
    /**
     * @param sqlMapConf  sql配置
     * @param valueMap    参数对象
     * @param currentPage 页数
     * @param totalCount  返回行数
     * @param <T>         类
     * @return 返回查询列表
     * @throws Exception 异常
     */
    <T> List<T> query(SqlMapConf sqlMapConf, Map<String, Object> valueMap, int currentPage, int totalCount) throws Exception;
    /**
     * 模版方式查询
     * 如果不想缓存数据，在数据使用后clear就可以了
     *
     * @param sqlMapConf  sql配置
     * @param valueMap    参数MAP
     * @param currentPage 第几页
     * @param totalCount  一页的行数
     * @param loadChild   是否载入映射对象
     * @param rollRows    是否让程序执行滚动,如果不让程序执行滚动，那么在程序里边要自己判断滚动
     * @param cls         类型
     * @param <T>         类型
     * @return 返回查询列表
     * @throws Exception 异常
     */
    <T> List<T> query(SqlMapConf sqlMapConf, Map<String, Object> valueMap, int currentPage, int totalCount, boolean loadChild, boolean rollRows, Class<T> cls) throws Exception;
    /**
     * 用来避免写两次SQL 来得到翻页的总数,这里映入查询,就自动封装得到行数
     *
     * @param sqlMapConf sql配置
     * @param valueMap   参数
     * @return 这里 exeId 是列表的id,
     */
    long queryCount(SqlMapConf sqlMapConf, Map<String, Object> valueMap);
    /**
     * @param sqlMapConf sql配置
     * @param o          对象参数
     * @return 是否执行成功
     * @throws Exception 异常
     */
    boolean execute(SqlMapConf sqlMapConf, Object o) throws Exception;
    /**
     * ibatis remote execute
     *
     * @param sqlMapConf sql配置
     * @param valueMap   参数map
     * @return boolean 执行成功
     * @throws Exception 异常
     */
    boolean execute(SqlMapConf sqlMapConf, Map<String, Object> valueMap) throws Exception;
    /**
     * @param sqlMapConf sql配置
     * @param o          对象参数
     * @return 更新是否成功
     * @throws Exception 异常
     */
    int update(SqlMapConf sqlMapConf, Object o) throws Exception;
    /**
     * @param sqlMapConf sql配置
     * @param valueMap   参数msp
     * @return 更新是否成功
     * @throws Exception 异常
     */
    int update(SqlMapConf sqlMapConf, Map<String, Object> valueMap) throws Exception;
}
