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

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-9
 * Time: 20:42:20
 */
public interface SqlMapClient {
    /**
     * @param namespace 命名空间
     * @param exeId     sqlId
     * @param o         参数对象
     * @return 返回单一对象
     */
    Object getUniqueResult(String namespace, String exeId, Object o);
    /**
     * 单个对象查询返回
     *
     * @param namespace 命名空间
     * @param exeId     sql ID
     * @param valueMap  查询参数
     * @return Object   返回对象
     */
    Object getUniqueResult(String namespace, String exeId, Map<String, Object> valueMap);
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
    <T> List<T> query(String namespace, String exeId, Object o, int currentPage, int totalCount, boolean loadChild, boolean rollRows) throws Exception;
    /**
     *
     * @param namespace 命名空间
     * @param exeId 查询语句ID
     * @param valueMap 参数对象
     * @param <T> 类
     * @return 返回查询列表
     * @throws Exception 异常
     */
    <T> List<T> query(String namespace, String exeId, Map<String, Object> valueMap) throws Exception;
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
    <T> List<T> query(String namespace, String exeId, Map<String, Object> valueMap, int currentPage, int totalCount) throws Exception;
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
    <T> List<T> query(String namespace, String exeId, Map<String, Object> valueMap, int currentPage, int totalCount, boolean loadChild, boolean rollRows) throws Exception;
    /**
     * @param namespace 命名空间
     * @param exeid     执行id
     * @param o         对象参数
     * @return 是否执行成功
     * @throws Exception 异常
     */
    boolean execute(String namespace, String exeid, Object o) throws Exception;
    /**
     * ibatis remote execute
     *
     * @param namespace 命名空间
     * @param exeId     sql  Id
     * @param valueMap  参数map
     * @return boolean
     * @throws Exception 异常
     */
    boolean execute(String namespace, String exeId, Map<String, Object> valueMap) throws Exception;
    /**
     * @param namespace 命名空间
     * @param exeid     执行id
     * @param o         对象参数
     * @return 更新是否成功
     */
    int update(String namespace, String exeid, Object o) throws Exception;

    /**
     * @param namespace 命名空间
     * @param exeId     执行id
     * @param valueMap  参数msp
     * @return 更新是否成功
     */
    int update(String namespace, String exeId, Map<String, Object> valueMap) throws Exception;

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
    <T> List<T> query(String namespace, String exeId, Map<String, Object> valueMap, Class<T> cls) throws Exception;

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
    <T> List<T> query(String namespace, String exeId, Map<String, Object> valueMap, int currentPage, int totalCount,boolean loadChild, Class<T> cls) throws Exception;


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
    <T> List<T> query(String namespace, String exeId, Map<String, Object> valueMap, int currentPage, int totalCount, boolean loadChild, boolean rollRows, Class<T> cls) throws Exception;

    /**
     * 用来避免写两次SQL 来得到翻页的总数,这里映入查询,就自动封装得到行数
     *
     * @param namespace 命名空间
     * @param exeId     查询ID,是列表的id  不用在写一边查询总数的sql
     * @param valueMap  参数
     * @return 这里 exeId 是列表的id,
     * @throws Exception 异常
     */
    long queryCount(String namespace, String exeId, Map<String, Object> valueMap) throws Exception;

}