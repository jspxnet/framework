/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.datasource;

import com.github.jspxnet.sober.SoberEnv;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.RandomUtil;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 12-1-2
 * Time: 下午3:16
 * 支持读写分离的连接池,sober已经支持，配置 后可以负债均衡，读写分离
 * <p>
 * [bean id="readDataSource" class="com.gibhub.jspxnet.datasource.JspxDataSource" destroy="close" singleton="true"]
 * [string name="driverClass"]${driverClassName}[/string]
 * [string name="jdbcUrl"][![CDATA[jdbc:postgresql://192.168.0.201:5433/jspxnet]]][/string]
 * [string name="user"]${username}[/string]
 * [string name="password"][![CDATA[${password}]]][/string]
 * [int name="maxPoolSize"]${maxPoolSize}[/int]
 * [int name="readWrite"]1[/int]
 * [/bean]
 * <p>
 * [bean id="writeDataSource" class="com.github.jspxnet.datasource.JspxDataSource" destroy="close" singleton="true"]
 * [string name="driverClass"]${driverClassName}[/string]
 * [string name="jdbcUrl"][![CDATA[jdbc:postgresql://192.168.0.201:5432/jspxnet]]][/string]
 * [string name="user"]${username}[/string]
 * [string name="password"][![CDATA[${password}]]][/string]
 * [int name="maxPoolSize"]${maxPoolSize}[/int]
 * [int name="readWrite"]0[/int]
 * [/bean]
 * <p>
 * [bean id="jspxDataSource" class="com.github.jspxnet.datasource.JRWPoolDataSource" singleton="true"]
 * [list name="dataSources" class="ref"]
 * [value]readDataSource[/value]
 * [value]readDataSource1[/value]
 * [value]writeDataSource[/value]
 * [/list]
 * [/bean]
 * 只有 getConnection(int type) 有 意义
 */
public class JRWPoolDataSource implements DataSource, Serializable {

    private int[] readOnlyIds = null;
    private int[] writeIds = null;
    private List<DataSource> dataSources;

    public List<DataSource> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<DataSource> sources) {
        this.dataSources = sources;
        for (int i = 0; i < dataSources.size(); i++) {
            Object ds = dataSources.get(i);
            if (ds instanceof com.github.jspxnet.datasource.ReadWriteDataSource) {
                com.github.jspxnet.datasource.ReadWriteDataSource rwDataSource = (com.github.jspxnet.datasource.ReadWriteDataSource) ds;
                if (SoberEnv.READ_ONLY == rwDataSource.getReadWrite()) {
                    readOnlyIds = ArrayUtil.add(readOnlyIds, i);
                } else if (SoberEnv.WRITE_ONLY == rwDataSource.getReadWrite() || SoberEnv.READ_WRITE == rwDataSource.getReadWrite()) {
                    writeIds = ArrayUtil.add(writeIds, i);
                }
            }
        }
    }

    /**
     * 读写分离，支持负载均衡
     * final public static int ReadWrite = 0;
     * <p>
     * final public static int ReadOnly = 1;
     * <p>
     * final public static int WriteOnly = 2;
     * //读写分离，支持负载均衡  end
     *
     * @param type 连接类型
     * @return 连接
     * @throws SQLException 异常
     */
    public Connection getConnection(int type) throws SQLException {
        if (SoberEnv.READ_ONLY == type) {
            int x = readOnlyIds[RandomUtil.getRandomInt(0, readOnlyIds.length - 1)];
            Connection connection = dataSources.get(x).getConnection();
            if (readOnlyIds.length > 1 && connection == null) {
                readOnlyIds = ArrayUtil.remove(readOnlyIds, x);
                x = readOnlyIds[RandomUtil.getRandomInt(0, readOnlyIds.length - 1)];
                return dataSources.get(x).getConnection();
            } else {
                connection.setReadOnly(true);
                return connection;
            }
        }
        if (SoberEnv.WRITE_ONLY == type || SoberEnv.READ_WRITE == type) {
            int x = writeIds[RandomUtil.getRandomInt(0, writeIds.length - 1)];
            Connection connection = dataSources.get(x).getConnection();
            if (writeIds.length > 1 && connection != null) {
                writeIds = ArrayUtil.remove(writeIds, x);
                x = writeIds[RandomUtil.getRandomInt(0, readOnlyIds.length - 1)];
                return dataSources.get(x).getConnection();
            } else {
                return connection;
            }
        }
        throw new SQLException("read write dataSource not find,读写数据源配置错误，不能够找到数据源");
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(0);
    }

    @Override
    public Connection getConnection(String user, String password) throws SQLException {
        for (DataSource dataSource : dataSources) {
            if (dataSource instanceof com.github.jspxnet.datasource.ReadWriteDataSource) {
                com.github.jspxnet.datasource.ReadWriteDataSource rwDataSource = (com.github.jspxnet.datasource.ReadWriteDataSource) dataSource;
                if (SoberEnv.WRITE_ONLY == rwDataSource.getReadWrite() || SoberEnv.READ_WRITE == rwDataSource.getReadWrite()) {
                    return rwDataSource.getConnection(user, password);
                }
            }
        }
        return getConnection(0);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        for (DataSource dataSource : dataSources) {
            if (dataSource instanceof com.github.jspxnet.datasource.ReadWriteDataSource) {
                com.github.jspxnet.datasource.ReadWriteDataSource rwDataSource = (com.github.jspxnet.datasource.ReadWriteDataSource) dataSource;
                if (SoberEnv.WRITE_ONLY == rwDataSource.getReadWrite() || SoberEnv.READ_WRITE == rwDataSource.getReadWrite()) {
                    return rwDataSource.getLogWriter();
                }
            }
        }
        return dataSources.get(0).getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        for (DataSource dataSource : dataSources) {
            if (dataSource instanceof com.github.jspxnet.datasource.ReadWriteDataSource) {
                com.github.jspxnet.datasource.ReadWriteDataSource rwDataSource = (com.github.jspxnet.datasource.ReadWriteDataSource) dataSource;
                if (SoberEnv.WRITE_ONLY == rwDataSource.getReadWrite() || SoberEnv.READ_WRITE == rwDataSource.getReadWrite()) {
                    rwDataSource.setLogWriter(out);
                }
            }
        }
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        for (DataSource dataSource : dataSources) {
            if (dataSource instanceof com.github.jspxnet.datasource.ReadWriteDataSource) {
                com.github.jspxnet.datasource.ReadWriteDataSource rwDataSource = (com.github.jspxnet.datasource.ReadWriteDataSource) dataSource;
                if (SoberEnv.WRITE_ONLY == rwDataSource.getReadWrite() || SoberEnv.READ_WRITE == rwDataSource.getReadWrite()) {
                    rwDataSource.setLoginTimeout(seconds);
                }
            }
        }
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        for (DataSource dataSource : dataSources) {
            if (dataSource instanceof com.github.jspxnet.datasource.ReadWriteDataSource) {
                com.github.jspxnet.datasource.ReadWriteDataSource rwDataSource = (com.github.jspxnet.datasource.ReadWriteDataSource) dataSource;
                if (SoberEnv.WRITE_ONLY == rwDataSource.getReadWrite() || SoberEnv.READ_WRITE == rwDataSource.getReadWrite()) {
                    return rwDataSource.getLoginTimeout();
                }
            }
        }
        return dataSources.get(0).getLoginTimeout();
    }

    /**
     * Return the parent Logger of all the Loggers used by this data source. This
     * should be the Logger farthest from the root Logger that is
     * still an ancestor of all of the Loggers used by this data source. Configuring
     * this Logger will affect all of the log messages generated by the data source.
     * In the worst case, this may be the root Logger.
     *
     * @return the parent Logger for this data source
     */
    @Override
    public Logger getParentLogger() {
        for (DataSource dataSource : dataSources) {
            if (dataSource instanceof com.github.jspxnet.datasource.ReadWriteDataSource) {
                com.github.jspxnet.datasource.ReadWriteDataSource rwDataSource = (com.github.jspxnet.datasource.ReadWriteDataSource) dataSource;
                if (SoberEnv.WRITE_ONLY == rwDataSource.getReadWrite() || SoberEnv.READ_WRITE == rwDataSource.getReadWrite()) {
                    return (Logger) BeanUtil.getProperty(rwDataSource, "parentLogger");
                }
            }
        }
        return (Logger) BeanUtil.getProperty(dataSources.get(0), "parentLogger");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        for (DataSource dataSource : dataSources) {
            if (dataSource instanceof com.github.jspxnet.datasource.ReadWriteDataSource) {
                com.github.jspxnet.datasource.ReadWriteDataSource rwDataSource = (com.github.jspxnet.datasource.ReadWriteDataSource) dataSource;
                if (SoberEnv.WRITE_ONLY == rwDataSource.getReadWrite() || SoberEnv.READ_WRITE == rwDataSource.getReadWrite()) {
                    return rwDataSource.unwrap(iface);
                }
            }
        }
        return dataSources.get(0).unwrap(iface);

    }

    @Override
    public boolean isWrapperFor(Class<?> iFace) throws SQLException {
        for (DataSource dataSource : dataSources) {
            if (dataSource instanceof com.github.jspxnet.datasource.ReadWriteDataSource) {
                com.github.jspxnet.datasource.ReadWriteDataSource rwDataSource = (ReadWriteDataSource) dataSource;
                if (SoberEnv.WRITE_ONLY == rwDataSource.getReadWrite() || SoberEnv.READ_WRITE == rwDataSource.getReadWrite()) {
                    return rwDataSource.isWrapperFor(iFace);
                }
            }
        }
        return dataSources.get(0).isWrapperFor(iFace);
    }


}