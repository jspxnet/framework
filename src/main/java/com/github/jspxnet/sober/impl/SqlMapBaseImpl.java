package com.github.jspxnet.sober.impl;

import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.scriptmark.util.ScriptMarkUtil;
import com.github.jspxnet.sioc.util.TypeUtil;
import com.github.jspxnet.sober.*;
import com.github.jspxnet.sober.annotation.SqlMap;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.dialect.Dialect;
import com.github.jspxnet.sober.enums.ExecuteEnumType;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.table.SqlMapConf;
import com.github.jspxnet.sober.util.AnnotationUtil;
import com.github.jspxnet.sober.util.DataMap;
import com.github.jspxnet.sober.util.JdbcUtil;
import com.github.jspxnet.sober.util.SoberUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将以前的 SqlMapClientImpl 分离出来,为了实现拦截器方式
 */
@Slf4j
public class SqlMapBaseImpl implements SqlMapBase {

    private final SoberFactory soberFactory;
    private final JdbcOperations jdbcOperations;

    public SqlMapBaseImpl(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
        this.soberFactory = jdbcOperations.getSoberFactory();

    }

    /**
     *
     * @return jdbc查询器
     */
    @Override
    public SoberSupport getSoberSupport() {
        return jdbcOperations;
    }

    /**
     *
     * @return 数据库方言
     */
    @Override
    public Dialect getDialect() {
        return soberFactory.getDialect();
    }


    /**
     *
     * @param namespace  命名空间
     * @param exeId 执行id
     * @param executeEnumType 执行方式
     * @param sqlMap 配置
     * @return sqlMap配置
     * @throws Exception 异常
     */
    @Override
    public SqlMapConf getSqlMapConf(String namespace, String exeId, ExecuteEnumType executeEnumType, SqlMap sqlMap) throws Exception {
        return SoberUtil.getSqlMapConf(soberFactory, namespace, exeId, executeEnumType,sqlMap);
    }


    /**
     * @param o bean对象
     * @return 对象转换为Map提供查询参数
     */
    @Override
    public Map<String, Object> getValueMap(Object o) {
        Map<String, Object> valueMap = ObjectUtil.getMap(o);
        if (o != null) {
            TableModels soberTable = jdbcOperations.getSoberTable(o.getClass());
            valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
            valueMap.put(Dialect.KEY_PRIMARY_KEY, soberTable.getPrimary());
        }
        return valueMap;
    }


    /**
     * @param sqlMapConf sql配置
     * @param o          参数对象
     * @return 返回单一对象
     */
    @Override
    public Object getUniqueResult(SqlMapConf sqlMapConf, Object o) {
        Map<String, Object> valueMap = getValueMap(o);
        return getUniqueResult(sqlMapConf, valueMap);
    }


    /**
     * 单个对象查询返回
     *
     * @param sqlMapConf sql配置
     * @param valueMap   查询参数
     * @return Object   返回对象
     */
    @Override
    public Object getUniqueResult(SqlMapConf sqlMapConf, Map<String, Object> valueMap) {

        Dialect dialect = soberFactory.getDialect();
        return jdbcOperations.getUniqueResult(dialect.processSql(sqlMapConf.getContext(), valueMap));
    }


    /**
     * batis 方式查询返回
     *
     * @param sqlMapConf  sql配置
     * @param o           查询对象条件
     * @param currentPage 当前页数
     * @param totalCount  最大行数
     * @param rollRows    是否滚动
     * @param <T>         返回类型
     * @return 返回列表
     * @throws Exception 异常
     */
    @Override
    public <T> List<T> query(SqlMapConf sqlMapConf, Object o, int currentPage, int totalCount, boolean rollRows) throws Exception {
        Map<String, Object> valueMap = getValueMap(o);
        return query(sqlMapConf, valueMap, currentPage, totalCount,  rollRows);
    }

    /**
     * @param sqlMapConf sql配置
     * @param valueMap   参数对象
     * @param <T>        类
     * @return 返回查询列表
     * @throws Exception 异常
     */
    @Override
    public <T> List<T> query(SqlMapConf sqlMapConf, Map<String, Object> valueMap) throws Exception {
        return query(sqlMapConf, valueMap, 1, jdbcOperations.getMaxRows(), false, null);
    }

    /**
     * @param sqlMapConf sql配置
     * @param valueMap   参数对象
     * @param cls        类
     * @param <T>        类型
     * @return 返回查询列表
     * @throws Exception 异常
     */
    @Override
    public <T> List<T> query(SqlMapConf sqlMapConf, Map<String, Object> valueMap, Class<T> cls) throws Exception {
        return query(sqlMapConf, valueMap, 1, jdbcOperations.getMaxRows(), false, cls);
    }


    /**
     * @param sqlMapConf  sql配置
     * @param valueMap    参数对象
     * @param currentPage 页数
     * @param totalCount  页数
     * @param rollRows    是否行滚
     * @param <T>         类
     * @return 返回查询列表
     * @throws Exception 异常
     */
    @Override
    public <T> List<T> query(SqlMapConf sqlMapConf, Map<String, Object> valueMap, int currentPage, int totalCount, boolean rollRows) throws Exception {
        return query(sqlMapConf, valueMap, currentPage, totalCount,  rollRows, null);
    }

    /**
     * @param sqlMapConf  sql配置
     * @param valueMap    参数对象
     * @param currentPage 页数
     * @param totalCount  返回行数
     * @param <T>         类
     * @return 返回查询列表
     * @throws Exception 异常
     */
    @Override
    public <T> List<T> query(SqlMapConf sqlMapConf, Map<String, Object> valueMap, int currentPage, int totalCount) throws Exception {
        return query(sqlMapConf, valueMap, currentPage, totalCount, false, null);
    }

    /**
     * 模版方式查询
     * 如果不想缓存数据，在数据使用后clear就可以了
     *
     * @param sqlMapConf  sql配置
     * @param valueMap    参数MAP
     * @param currentPage 第几页
     * @param totalCount  一页的行数
//     * @param loadChild   是否载入映射对象
     * @param rollRows    是否让程序执行滚动,如果不让程序执行滚动，那么在程序里边要自己判断滚动
     * @param cls         类型
     * @param <T>         类型
     * @return 返回查询列表
     * @throws Exception 异常
     */
    @Override
    public <T> List<T> query(SqlMapConf sqlMapConf, Map<String, Object> valueMap, int currentPage, int totalCount, boolean rollRows, Class<T> cls) throws Exception {
        if (totalCount > jdbcOperations.getMaxRows()) {
            totalCount = jdbcOperations.getMaxRows();
        }
        if (totalCount < 1) {
            totalCount = 1;
        }

        if (currentPage <= 0) {
            currentPage = 1;
        }
        if (valueMap == null) {
            valueMap = new HashMap<>();
        }


        int beginRow = currentPage * totalCount - totalCount;
        if (beginRow < 0) {
            beginRow = 0;
        }
        int endRow = beginRow + totalCount;


        Dialect dialect = soberFactory.getDialect();
        boolean loadChild = ObjectUtil.toBoolean(sqlMapConf.getNexus());
        valueMap.put("currentPage", currentPage);
        valueMap.put("totalCount", totalCount);
        valueMap.put("loadChild", loadChild);
        valueMap.put("rollRows", rollRows);
        valueMap.put("beginRow", beginRow);
        valueMap.put("endRow", endRow);
        valueMap.put("namespace", sqlMapConf.getNamespace());

        //修复变量,避免空异常 begin
        ScriptMarkUtil.fixVarNull(valueMap, sqlMapConf.getContext());
        //修复变量,避免空异常 end

        String sqlText = dialect.processSql(sqlMapConf.getContext(), valueMap);
        if (StringUtil.isNull(sqlText)) {
            throw new Exception("ERROR SQL IS NULL");
        }

        //判断是否是用缓存
        Table table = AnnotationUtil.getTable(cls);
        String cacheKey = null;
        if (table != null && soberFactory.isUseCache() && table.cache()) {
            cacheKey = SoberUtil.getListKey(cls, sqlText, StringUtil.empty, beginRow, endRow, loadChild);
            List<T> resultList = (List<T>) JSCacheManager.get(cls, cacheKey);
            if (!ObjectUtil.isEmpty(resultList)) {
                return resultList;
            }
        }

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<T> list = new ArrayList<>();
        try {

            jdbcOperations.debugPrint(sqlText);
            conn = jdbcOperations.getConnection(SoberEnv.READ_ONLY);
            //结果集的游标只能向下滚动  并且为只读模式
            if (!dialect.supportsConcurReadOnly()) {
                preparedStatement = conn.prepareStatement(sqlText);
            } else {
                preparedStatement = conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }
            if (totalCount > 10 && totalCount < 24) {
                preparedStatement.setFetchSize(30);
            } else if (totalCount >= 24) {
                preparedStatement.setFetchSize(100);
            }
            preparedStatement.setMaxRows(endRow);
            resultSet = preparedStatement.executeQuery();

            if (cls == null && !StringUtil.isNull(sqlMapConf.getResultType())) {
                cls = (Class<T>) TypeUtil.getJavaType(sqlMapConf.getResultType());
            }

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            if (rollRows && beginRow > 0) {
                resultSet.absolute(beginRow);
            }

            while (resultSet.next()) {
                if (cls == null && StringUtil.isEmpty(sqlMapConf.getResultType())) {
                    Map<String, Object> beanMap = SoberUtil.getHashMap(resultSetMetaData, dialect, resultSet);
                    list.add((T) ReflectUtil.createDynamicBean(beanMap));
                } else if (Map.class.isAssignableFrom(cls) || cls.isInstance(Map.class)) {
                    DataMap<String, Object> map = SoberUtil.getDataHashMap(resultSetMetaData, dialect, resultSet);
                    list.add((T) map);
                } else {
                    if (ClassUtil.isStandardProperty(cls)) {
                        list.add(BeanUtil.getTypeValue(dialect.getResultSetValue(resultSet, 1), cls));
                    } else {
                        T resultObject;
                        TableModels soberTable = soberFactory.getTableModels(cls, jdbcOperations);
                        if (soberTable != null) {
                            resultObject = jdbcOperations.loadColumnsValue(cls, resultSet);
                        } else {
                            resultObject = JdbcUtil.getBean(resultSet, cls, dialect);
                        }
                        if (loadChild) {
                            jdbcOperations.loadNexusValue(soberTable, resultObject);
                        }
                        list.add(resultObject);
                    }
                }
                if (list.size() > totalCount) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("error SQL:{},info:{}", sqlText, e.getMessage());
            throw new Exception("SQL:" + sqlText);
        } finally {
            JdbcUtil.closeResultSet(resultSet);
            JdbcUtil.closeStatement(preparedStatement);
            JdbcUtil.closeConnection(conn);
        }
        //放入cache

        if (table != null && soberFactory.isUseCache() && table.cache()) {
            JSCacheManager.put(cls, cacheKey, list);
        }
        return list;
    }

    /**
     * 用来避免写两次SQL 来得到翻页的总数,这里映入查询,就自动封装得到行数
     *
     * @param sqlMapConf sql配置
     * @param valueMap   参数
     * @return 这里 exeId 是列表的id,
     */
    @Override
    public long queryCount(SqlMapConf sqlMapConf, Map<String, Object> valueMap) {

        Dialect dialect = soberFactory.getDialect();
        valueMap.put("databaseType", soberFactory.getDatabaseType());
        valueMap.put("currentPage", 1);
        valueMap.put("totalCount", 1);
        valueMap.put("loadChild", false);
        valueMap.put("rollRows", false);
        valueMap.put("namespace", sqlMapConf.getNamespace());
        valueMap.put("beginRow", 0);
        valueMap.put("endRow", soberFactory.getMaxRows());

        String sqlTxt = sqlMapConf.getContext();
        //修复变量,避免空异常 begin
        ScriptMarkUtil.fixVarNull(valueMap, sqlTxt);
        //修复变量,避免空异常 end

        String sql = dialect.processSql(sqlTxt, valueMap);
        if (StringUtil.isNull(sql)) {
            throw new IllegalArgumentException("ERROR SQL IS NULL:" + sql);
        }
        sql = StringUtil.removeOrders(sql);
        sql = "SELECT count(1) as countNum FROM (" + sql + ") queryCount";
        return ObjectUtil.toLong(jdbcOperations.getUniqueResult(sql));
        //放入cache

    }

    /**
     * @param sqlMapConf sql配置
     * @param o          对象参数
     * @return 是否执行成功
     * @throws Exception 异常
     */
    @Override
    public boolean execute(SqlMapConf sqlMapConf, Object o) throws Exception {
        Map<String, Object> valueMap = getValueMap(o);
        return execute(sqlMapConf, valueMap);
    }

    /**
     * ibatis remote execute
     *
     * @param sqlMapConf sql配置
     * @param valueMap   参数map
     * @return boolean
     * @throws Exception 异常
     */
    @Override
    public boolean execute(SqlMapConf sqlMapConf, Map<String, Object> valueMap) throws Exception {
        Dialect dialect = soberFactory.getDialect();
        return jdbcOperations.execute(dialect.processSql(sqlMapConf.getContext(), valueMap));

    }

    /**
     * @param sqlMapConf sql配置
     * @param o          对象参数
     * @return 更新是否成功
     * @throws Exception 异常
     */
    @Override
    public int update(SqlMapConf sqlMapConf, Object o) throws Exception {
        Map<String, Object> valueMap = getValueMap(o);
        return update(sqlMapConf, valueMap);
    }

    /**
     * @param sqlMapConf sql配置
     * @param valueMap   参数msp
     * @return 更新是否成功
     * @throws Exception 异常
     */
    @Override
    public int update(SqlMapConf sqlMapConf, Map<String, Object> valueMap) throws Exception {
        Dialect dialect = soberFactory.getDialect();
        return jdbcOperations.update(dialect.processSql(sqlMapConf.getContext(), valueMap));
    }
}