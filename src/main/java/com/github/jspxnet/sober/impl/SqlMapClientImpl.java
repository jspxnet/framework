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

import com.github.jspxnet.sober.*;
import com.github.jspxnet.sober.dialect.Dialect;
import com.github.jspxnet.sober.enums.ExecuteEnumType;
import com.github.jspxnet.sober.enums.QueryModelEnumType;
import com.github.jspxnet.sober.proxy.InterceptorProxy;
import com.github.jspxnet.sober.table.SqlMapConf;
import com.github.jspxnet.sober.util.SoberUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
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

    private final SqlMapBase sqlMapBase;

    public SqlMapClientImpl(SqlMapBase sqlMapBase) {
        this.sqlMapBase = sqlMapBase;
    }

    /**
     *
     * @return 数据库支持
     */
    @Override
    public SoberSupport getSoberSupport() {
        return sqlMapBase.getSoberSupport();
    }

    /**
     *
     * @return sql 方言
     */
    @Override
    public Dialect getDialect() {
        return sqlMapBase.getDialect();
    }

    /**
     * sqlMap配置
     * @param namespace 命名空间
     * @param exeId 执行名称
     * @param executeEnumType 执行类型
     * @return 返回配置
     * @throws Exception 异常
     */
    @Override
    public SqlMapConf getSqlMapConf(String namespace, String exeId, ExecuteEnumType executeEnumType) throws Exception {
        return sqlMapBase.getSqlMapConf(namespace,exeId,executeEnumType,null);
    }

    /**
     *
     * @param o 对象
     * @return 返回变量数组
     */
    @Override
    public Map<String, Object> getValueMap(Object o) {
        return sqlMapBase.getValueMap(o);
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
        SqlMapConf mapSql;
        try {
            mapSql = getSqlMapConf(namespace,exeId,ExecuteEnumType.QUERY);
            return SoberUtil.invokeSqlMapInvocation(getSoberSupport(),mapSql,valueMap) ;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(0);
        }
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
        return query( namespace,  exeId,valueMap, 1, getSoberSupport().getMaxRows(), false,false);
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
        return query( namespace,  exeId,valueMap, 1, getSoberSupport().getMaxRows(), false,false,cls);
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
     * @param count  一页的行数
     * @param loadChild   是否载入映射对象
     * @param rollRows    是否让程序执行滚动,如果不让程序执行滚动，那么在程序里边要自己判断滚动
     * @param cls 类型
     * @param <T> 类型
     * @return 返回查询列表
     * @throws Exception 异常
     */
    @Override
    public <T> List<T> query(String namespace, String exeId, Map<String, Object> valueMap, int currentPage, int count, boolean loadChild, boolean rollRows, Class<T> cls) throws Exception {
        valueMap.put(InterceptorProxy.KEY_LOAD_CHILD, loadChild);
        valueMap.put(InterceptorProxy.KEY_ROLL_ROWS, rollRows);
        valueMap.put(InterceptorProxy.KEY_NAMESPACE, namespace);
        valueMap.put(InterceptorProxy.KEY_CURRENT_PAGE, currentPage);
        valueMap.put(InterceptorProxy.KEY_COUNT, count);
        valueMap.put(InterceptorProxy.KEY_RETURN_CLASS, cls);
        SqlMapConf mapSql = sqlMapBase.getSqlMapConf(namespace,exeId,ExecuteEnumType.QUERY,null);
        mapSql.setQueryModel(QueryModelEnumType.LIST.getValue());
        return (List<T>)SoberUtil.invokeSqlMapInvocation(getSoberSupport(),mapSql,valueMap);
    }

    /**
     * 用来避免写两次SQL 来得到翻页的总数,这里映入查询,就自动封装得到行数
     * 这个方法不会执行拦截器
     * @param namespace 命名空间
     * @param exeId     查询ID,是列表的id  不用在写一边查询总数的sql
     * @param valueMap  参数
     * @return 这里 exeId 是列表的id,
     */
    @Override
    public long queryCount(String namespace, String exeId, Map<String, Object> valueMap)  {
        SqlMapConf sqlMapConf;
        try {
            sqlMapConf = sqlMapBase.getSqlMapConf(namespace,exeId,ExecuteEnumType.QUERY,null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return sqlMapBase.queryCount(sqlMapConf,valueMap);
        //放入cache
    }

    /**
     * @param namespace 命名空间
     * @param exeId     执行id
     * @param o         对象参数
     * @return 是否执行成功
     */
    @Override
    public boolean execute(String namespace, String exeId, Object o) throws Exception {
        Map<String, Object> valueMap = getValueMap(o);
        return execute(namespace, exeId, valueMap);
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
        SqlMapConf sqlMapConf = getSqlMapConf(namespace,exeId,ExecuteEnumType.EXECUTE);
        Object result = SoberUtil.invokeSqlMapInvocation(getSoberSupport(),sqlMapConf,valueMap);
        return ObjectUtil.toBoolean(result);

    }

    /**
     * @param namespace 命名空间
     * @param exeId     执行id
     * @param o         对象参数
     * @return 更新是否成功
     * @throws Exception 异常
     */
    @Override
    public int update(String namespace, String exeId, Object o) throws Exception {
        Map<String, Object> valueMap = getValueMap(o);
        return update(namespace, exeId, valueMap);
    }

    /**
     * @param namespace 命名空间
     * @param exeId     执行id
     * @param valueMap  参数msp
     * @return 更新是否成功
     *  @throws Exception 异常
     */
    @Override
    public int update(String namespace, String exeId, Map<String, Object> valueMap) throws Exception {
        SqlMapConf sqlMapConf = getSqlMapConf(namespace,exeId,ExecuteEnumType.UPDATE);
        Object result = SoberUtil.invokeSqlMapInvocation(getSoberSupport(),sqlMapConf,valueMap);
        return ObjectUtil.toInt(result);
    }
}