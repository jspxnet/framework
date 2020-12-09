/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.impl;

import com.github.jspxnet.sober.SoberEnv;
import com.github.jspxnet.sober.SoberFactory;
import com.github.jspxnet.sober.SqlMapClient;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.config.SqlMapConfig;
import com.github.jspxnet.sober.config.SQLRoom;
import com.github.jspxnet.sober.dialect.Dialect;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.util.JdbcUtil;
import com.github.jspxnet.sober.util.SoberUtil;
import com.github.jspxnet.txweb.AssertException;
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
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-9
 * Time: 20:41:00
 * Ibatis 的 SqlMapClient 方式扩展
 */
@Slf4j
public class SqlMapClientImpl implements SqlMapClient {


    private final SoberFactory soberFactory;
    private final JdbcOperations jdbcOperations;

    public SqlMapClientImpl(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
        this.soberFactory = jdbcOperations.getSoberFactory();
    }


    /**
     * @param o bean对象
     * @return 对象转换为Map提供查询参数
     */
    private Map<String, Object> getValueMap(Object o) {
        Map<String, Object> valueMap = ObjectUtil.getMap(o);
        if (o != null) {
            TableModels soberTable = jdbcOperations.getSoberTable(o.getClass());
            valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
            valueMap.put(Dialect.KEY_PRIMARY_KEY, soberTable.getPrimary());
        }
        return valueMap;
    }

    /**
     * @param namespace 命名空间
     * @param exeId     sqlId
     * @param o         参数对象
     * @return 返回单一对象
     */
    @Override
    public Object getUniqueResult(String namespace, String exeId, Object o) {
        Map<String, Object> valueMap = getValueMap(o);
        return getUniqueResult(namespace, exeId, valueMap);
    }

    /**
     * 单个对象查询返回
     *
     * @param namespace 命名空间
     * @param exeId     sql ID
     * @param valueMap  查询参数
     * @return Object   返回对象
     */
    @Override
    public Object getUniqueResult(String namespace, String exeId, Map<String, Object> valueMap) {
        SQLRoom sqlRoom = soberFactory.getSqlRoom(namespace);
        SqlMapConfig mapSql = sqlRoom.getQueryMapSql(exeId,soberFactory.getDatabaseName());
        if (mapSql == null) {
            return null;
        }
        Dialect dialect = soberFactory.getDialect();
        String sql = dialect.processSQL(mapSql.getContext(), valueMap);
        return jdbcOperations.getUniqueResult(sql);
    }


    /**
     *  batis 方式查询返回
     * @param namespace   命名空间
     * @param exeId       查询语句ID
     * @param o           查询对象条件
     * @param currentPage 当前页数
     * @param totalCount  最大行数
     * @param loadChild   是否载入
     * @param rollRows    是否滚动
     * @param <T> 返回类型
     * @return 返回列表
     * @throws Exception 异常
     */
    @Override
    public <T> List<T> query(String namespace, String exeId, Object o, int currentPage, int totalCount, boolean loadChild, boolean rollRows) throws Exception {
        Map<String, Object> valueMap = getValueMap(o);
        return query(namespace, exeId, valueMap, currentPage, totalCount, loadChild, rollRows);
    }

    /**
     *
     * @param namespace 命名空间
     * @param exeId 查询语句ID
     * @param valueMap 参数对象
     * @param <T> 类
     * @return 返回查询列表
     * @throws Exception 异常
     */
    @Override
    public <T> List<T> query(String namespace, String exeId, Map<String, Object> valueMap) throws Exception {
        return query( namespace,  exeId,valueMap, 1, jdbcOperations.getMaxRows(), false,false);
    }

    /**
     *
     * @param namespace 命名空间
     * @param exeId 查询语句ID
     * @param valueMap 参数对象
     * @param cls 类
     * @param <T> 类型
     * @return 返回查询列表
     * @throws Exception 异常
     */
    @Override
    public <T> List<T> query(String namespace, String exeId, Map<String, Object> valueMap, Class<T> cls) throws Exception {
        return query( namespace,  exeId,valueMap, 1, jdbcOperations.getMaxRows(), false,false,cls);
    }

    /**
     *
     * @param namespace 命名空间
     * @param exeId 查询语句ID
     * @param valueMap 参数对象
     * @param currentPage 页数
     * @param totalCount 返回行数
     * @param loadChild 载入子对象
     * @param cls 返回类型
     * @param <T> 类
     * @return 返回查询列表
     * @throws Exception 异常
     */
    @Override
    public <T> List<T> query(String namespace, String exeId, Map<String, Object> valueMap, int currentPage, int totalCount,boolean loadChild, Class<T> cls) throws Exception {
        return query( namespace,  exeId,valueMap, currentPage, totalCount, loadChild,false,cls);
    }

    /**
     *
     * @param namespace 命名空间
     * @param exeId 查询语句ID
     * @param valueMap 参数对象
     * @param currentPage 页数
     * @param totalCount 页数
     * @param loadChild 返回行数
     * @param rollRows 是否行滚
     * @param <T> 类
     * @return 返回查询列表
     * @throws Exception 异常
     */
    @Override
    public <T> List<T> query(String namespace, String exeId, Map<String, Object> valueMap, int currentPage, int totalCount, boolean loadChild, boolean rollRows) throws Exception {
        return query( namespace,  exeId,  valueMap,  currentPage,  totalCount,  loadChild,  rollRows, null);
    }

    /**
     *
     * @param namespace 命名空间
     * @param exeId 查询语句ID
     * @param valueMap 参数对象
     * @param currentPage 页数
     * @param totalCount 返回行数
     * @param <T> 类
     * @return 返回查询列表
     * @throws Exception 异常
     */
    @Override
    public <T> List<T> query(String namespace, String exeId, Map<String, Object> valueMap, int currentPage, int totalCount) throws Exception {
        return query( namespace,  exeId,valueMap, currentPage, totalCount, false,false,null);
    }
    /**
     * 模版方式查询
     * 如果不想缓存数据，在数据使用后clear就可以了
     * @param namespace   命名空间
     * @param exeId       查询ID
     * @param valueMap    参数MAP
     * @param currentPage 第几页
     * @param totalCount  一页的行数
     * @param loadChild   是否载入映射对象
     * @param rollRows    是否让程序执行滚动,如果不让程序执行滚动，那么在程序里边要自己判断滚动
     * @param cls 类型
     * @param <T> 类型
     * @return 返回查询列表
     * @throws Exception 异常
     */
    @Override
    public <T> List<T> query(String namespace, String exeId, Map<String, Object> valueMap, int currentPage, int totalCount, boolean loadChild, boolean rollRows, Class<T> cls) throws Exception {
        if (totalCount > jdbcOperations.getMaxRows()) {
            totalCount = jdbcOperations.getMaxRows();
        }
        if (totalCount<1)
        {
            totalCount = 1;
        }
        if (currentPage <= 0) {
            currentPage = 1;
        }
        int beginRow = currentPage * totalCount - totalCount;
        if (beginRow < 0) {
            beginRow = 0;
        }
        int endRow = beginRow + totalCount;

        SQLRoom sqlRoom = soberFactory.getSqlRoom(namespace);
        if (sqlRoom == null) {
            log.error("ERROR:not get sql map namespace " + namespace + ",sql映射中不能够得到相应的命名空间,检查你的sql配置");
            return new ArrayList<>();
        }
        Dialect dialect = soberFactory.getDialect();
        SqlMapConfig mapSql = sqlRoom.getQueryMapSql(exeId,soberFactory.getDatabaseName());
        if (mapSql == null) {
             log.error("ERROR:not get sql map namespace " + namespace + " query id " + exeId + ",此命名空间中不能够找到sql,检查你的sql配置 ");
            return new ArrayList<>();
        }
        valueMap.put("currentPage", currentPage);
        valueMap.put("totalCount", totalCount);
        valueMap.put("loadChild", loadChild);
        valueMap.put("rollRows", rollRows);
        valueMap.put("beginRow", beginRow);
        valueMap.put("endRow", endRow);
        valueMap.put("namespace", namespace);
        String sqlText = StringUtil.empty;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<T> list = new ArrayList<>();
        try {
            sqlText = dialect.processSQL(mapSql.getContext(), valueMap);
            if (StringUtil.isNull(sqlText)) {
                throw new Exception("ERROR SQL IS NULL");
            }

            jdbcOperations.debugPrint(sqlText);
            conn = jdbcOperations.getConnection(SoberEnv.READ_ONLY);
            //结果集的游标只能向下滚动  并且为只读模式
            if (!dialect.supportsConcurReadOnly()) {
                preparedStatement = conn.prepareStatement(sqlText);
            } else {
                preparedStatement = conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }

            preparedStatement.setMaxRows(endRow);
            resultSet = preparedStatement.executeQuery();

            if (cls==null && !StringUtil.isNull(mapSql.getResultClass()))
            {
                cls = (Class<T>) Class.forName(mapSql.getResultClass());
            }

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            if (rollRows && beginRow > 0) {
                resultSet.absolute(beginRow);
            }
            while (resultSet.next()) {
                if (cls==null)
                {
                    Map<String, Object> map = SoberUtil.getDataHashMap(resultSetMetaData, dialect, resultSet);
                    //名称位_转换位驼峰命名方式
                    Map<String, Object> beanMap = new HashMap<>();
                    for (String key : map.keySet()) {
                        Object value = map.get(key);
                        String field = StringUtil.underlineToCamel(key);
                        beanMap.put(field, value);
                    }
                    list.add((T) ReflectUtil.createDynamicBean(beanMap));
                }
                else
                {
                    if (ClassUtil.isStandardProperty(cls))
                    {
                        list.add(BeanUtil.getTypeValue(dialect.getResultSetValue(resultSet,1),cls));
                    } else
                    {
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
            log.error("error SQL:{},info:{}",sqlText, e.getMessage());
            e.printStackTrace();
            throw new Exception("SQL:" + sqlText);
        } finally {
            JdbcUtil.closeResultSet(resultSet);
            JdbcUtil.closeStatement(preparedStatement);
            JdbcUtil.closeConnection(conn);
        }
        //放入cache
        return list;
    }

    /**
     * 用来避免写两次SQL 来得到翻页的总数,这里映入查询,就自动封装得到行数
     *
     * @param namespace 命名空间
     * @param exeId     查询ID,是列表的id  不用在写一边查询总数的sql
     * @param valueMap  参数
     * @return 这里 exeId 是列表的id,
     */
    @Override
    public long queryCount(String namespace, String exeId, Map<String, Object> valueMap)  {
        SQLRoom sqlRoom = soberFactory.getSqlRoom(namespace);
        if (sqlRoom == null) {
            log.error("ERROR:not get sql map namespace " + namespace + ",sql映射中不能够得到相应的命名空间");
            return 0;
        }
        Dialect dialect = soberFactory.getDialect();
        SqlMapConfig mapSql = sqlRoom.getQueryMapSql(exeId,soberFactory.getDatabaseName());
        if (mapSql == null) {
            log.error("ERROR:not get sql map namespace " + namespace + " query id " + exeId + ",此命名空间中不能够找到sql");
            return 0;
        }
        valueMap.put("databaseName", soberFactory.getDatabaseName());
        valueMap.put("currentPage", 1);
        valueMap.put("totalCount", 1);
        valueMap.put("loadChild", false);
        valueMap.put("rollRows", false);
        valueMap.put("namespace", namespace);
        String sqlText = StringUtil.empty;
        sqlText = dialect.processSQL(mapSql.getContext(), valueMap);
        if (StringUtil.isNull(sqlText)) {
            throw new IllegalArgumentException("ERROR SQL IS NULL:" + sqlText);
        }
        sqlText = StringUtil.removeOrders(sqlText);
        sqlText = "SELECT count(1) as countNum FROM (" + sqlText + ") queryCount";
        jdbcOperations.debugPrint(sqlText);
        return ObjectUtil.toLong(jdbcOperations.getUniqueResult(sqlText));
        //放入cache

    }

    /**
     * @param namespace 命名空间
     * @param exeid     执行id
     * @param o         对象参数
     * @return 是否执行成功
     */
    @Override
    public boolean execute(String namespace, String exeid, Object o) throws Exception {
        Map<String, Object> valueMap = getValueMap(o);
        return execute(namespace, exeid, valueMap);
    }

    /**
     * ibatis remote execute
     *
     * @param namespace 命名空间
     * @param exeId     sql  Id
     * @param valueMap  参数map
     * @return boolean
     */
    @Override
    public boolean execute(String namespace, String exeId, Map<String, Object> valueMap) throws Exception {
        SQLRoom sqlRoom = soberFactory.getSqlRoom(namespace);
        Dialect dialect = soberFactory.getDialect();
        SqlMapConfig mapSql = sqlRoom.getExecuteMapSql(exeId,soberFactory.getDatabaseName());
        if (mapSql == null) {
            return false;
        }
        return jdbcOperations.execute(dialect.processSQL(mapSql.getContext(), valueMap));

    }

    /**
     * @param namespace 命名空间
     * @param exeid     执行id
     * @param o         对象参数
     * @return 更新是否成功
     * @throws Exception 异常
     */
    @Override
    public int update(String namespace, String exeid, Object o) throws Exception {
        Map<String, Object> valueMap = getValueMap(o);
        return update(namespace, exeid, valueMap);
    }

    /**
     * @param namespace 命名空间
     * @param exeId     执行id
     * @param valueMap  参数msp
     * @return 更新是否成功
     */
    @Override
    public int update(String namespace, String exeId, Map<String, Object> valueMap) throws Exception {
        SQLRoom sqlRoom = soberFactory.getSqlRoom(namespace);
        Dialect dialect = soberFactory.getDialect();
        SqlMapConfig mapSql = sqlRoom.getUpdateMapSql(exeId,soberFactory.getDatabaseName());
        if (mapSql == null) {
            log.error("ERROR SQL map not config SQL update id :" + exeId + "  namespace:" + namespace);
            return -3;
        }
        return jdbcOperations.update(dialect.processSQL(mapSql.getContext(), valueMap));

    }
}