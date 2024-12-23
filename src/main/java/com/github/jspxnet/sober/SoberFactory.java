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

import com.github.jspxnet.sober.dialect.Dialect;
import com.github.jspxnet.sober.config.SQLRoom;
import com.github.jspxnet.sober.transaction.AbstractTransaction;
import javax.sql.DataSource;
import java.sql.Connection;
import java.io.Serializable;
import java.sql.SQLException;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-6
 * Time: 23:36:39
 */
public interface SoberFactory extends Serializable {
    int getMaxRows();

    void setMaxRows(int maxRows);

    String getDatabaseType();

    String getDatabaseName();

    Dialect getDialect();

    DataSource getDataSource();


    String getTransactionId();

    /**
     *  创建一个事务 创建事务
     * @return 事务对象
     * @throws SQLException 异常
     */
    AbstractTransaction createTransaction() throws SQLException;

    /**
     * @param type 读写分离 0 ReadWrite 1 ReadOnly 2 WriteOnly
     * @param tid  事务连接ID
     * @return 通过各种方式得到连接，包括XA连接
     * @throws SQLException sql异常
     */
    Connection getConnection(int type, String tid) throws SQLException;

    /**
     *
     * @param conn 关闭连接
     * @param release 是否彻底关闭
     */
    void closeConnection(Connection conn, boolean release);
    /**
     * 得到表结构,如果数据库中不存在表，就创建表
     *
     * @param cla          类
     * @param soberSupport 支持对象
     * @return 得到表结构
     */
    TableModels getTableModels(Class<?> cla, final SoberSupport soberSupport);

    /**
     *
     * @return 调试开启显示sql
     */
    boolean isShowsql();

    /**
     *
     * @return 是否自动提交
     */
    boolean isAutoCommit();

    int getTransactionIsolation();

    void setTransactionIsolation(int transactionIsolation);
    /**
     * 同时这里将初始化并创建索引
     * @param namespace 得到命名空间的SQL
     * @return sql空间
     */
    SQLRoom getSqlRoom(String namespace);

    void setMappingResources(String[] strings) throws Exception;

    boolean isValid();

    void setValid(boolean valid);

    TableModels getTableModels(String tableName, SoberSupport soberSupport);

    void evictTableModels(Class<?> cla);

    void clear();

    /**
     * @return 事务超时
     */
    int getTransactionTimeout();

    /**
     * @return 使用jta 分布式事务
     */
    boolean isJta();

    /**
     * @return 得到默认缓存名称
     */
    String getCacheName();

    /**
     * @return 是否使用缓存
     */
    boolean isUseCache();


}