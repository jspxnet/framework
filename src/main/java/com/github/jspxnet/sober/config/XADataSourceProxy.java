/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.config;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-8-7
 * Time: 10:43:22
 */
public class XADataSourceProxy implements DataSource, Serializable {

    private XADataSource dataSource;

    public XADataSourceProxy(XADataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getXAConnection().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return dataSource.getXAConnection().getConnection();
    }

    @Override
    public java.io.PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(java.io.PrintWriter out) throws SQLException {
        dataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        dataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }


    @Override
    public Logger getParentLogger() {
        return Logger.getLogger(XADataSourceProxy.class.getName());
    }

    @Override
    public <T> T unwrap(java.lang.Class<T> iface) {
        return null;
    }

    @Override
    public boolean isWrapperFor(java.lang.Class<?> iface) {
        return false;
    }
}