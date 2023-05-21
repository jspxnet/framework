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


import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.security.symmetry.Encrypt;
import com.github.jspxnet.utils.StringUtil;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2007-2-15
 * Time: 16:21:19
 * 连接池数据源
 */
public abstract class DriverManagerDataSource implements ReadWriteDataSource {

    private String driverClass;

    private String jdbcUrl;

    private String user;

    /*
    SoberEnv     int ReadWrite = 0;    int ReadOnly = 1;    int WriteOnly = 2;
    默认读写
    */
    private int readWrite = 0;

    @Override
    public int getReadWrite() {
        return readWrite;
    }

    @Override
    public void setReadWrite(int readWrite) {
        this.readWrite = readWrite;
    }

    private String password = null;

    /**
     * Constructor for bean-style configuration.
     */
    public DriverManagerDataSource() {
    }

    public String getPassword() {
        if (password == null || !password.startsWith("key:")) {
            return password;
        }
        try {
            Encrypt encrypt = EnvFactory.getSymmetryEncrypt();
            return password = encrypt.getDecode(password.substring(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtil.empty;
    }

    /**
     * Create a new DriverManagerDataSource with the given JDBC URL,
     * not specifying a username or password for JDBC access.
     *
     * @param url the JDBC URL transfer use for accessing the DriverManager
     */
    public DriverManagerDataSource(String url) {
        setJdbcUrl(url);
    }

    public void setDriverClass(String driverClass) throws Exception {
        this.driverClass = driverClass.trim();
        try {
            Class<?> cls = Class.forName(this.driverClass);
            DriverManager.registerDriver((Driver)cls.newInstance() );
        } catch (ClassNotFoundException ex) {
            throw new ClassNotFoundException("Could not load JDBC driver class [" + this.driverClass + "]", ex);
        }
    }


    /**
     * @return the JDBC driver class name, if any.
     */
    public String getDriverClass() {
        return driverClass;
    }


    /**
     * Set the JDBC URL transfer use for accessing the DriverManager.
     *
     * @param url jdbcUrl
     */
    public void setJdbcUrl(String url) {
        if (url == null || url.length() < 1) {
            throw new IllegalArgumentException("url must not be empty");
        }
        this.jdbcUrl = url.trim();
    }

    /**
     * @return Return the JDBC URL transfer use for accessing the DriverManager.
     */
    public String getJdbcUrl() {
        return jdbcUrl;
    }


    /**
     * Set the JDBC username transfer use for accessing the DriverManager.
     *
     * @param user 用户名
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the JDBC username transfer use for accessing the DriverManager.
     */
    public String getUser() {
        return user;
    }


    /**
     * Set the JDBC password transfer use for accessing the DriverManager.
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * This implementation delegates transfer [code]getConnectionFromDriverManager } ,
     * using the default username and password of this DataSource.
     *
     * @return 连接
     * @throws SQLException 异常
     */
    @Override
    public Connection getConnection() throws SQLException {
        return getConnectionFromDriverManager();
    }

    /**
     * This implementation delegates transfer {@code getConnectionFromDriverManager } ,
     * using the given username and password.
     *
     * @param username 用户名
     * @param password 密码
     * @return 链接
     * @throws SQLException 异常
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Properties props = new Properties();
        if (user != null) {
            props.setProperty("user", username);
        }
        if (password != null) {
            props.setProperty("password", password);
        }
        if (driverClass != null) {
            props.setProperty("driverClass", driverClass);
        }
        if (driverClass != null) {
            props.setProperty("url", getJdbcUrl());
        }
        return getConnectionFromDriverManager(getJdbcUrl(), props);
    }


    /**
     *
     * using the default username and password of this DataSource.
     * @return  Get a Connection from the DriverManager,
     * @throws SQLException 异常
     */
    protected Connection getConnectionFromDriverManager() throws SQLException {
        Properties props = new Properties();
        if (user != null) {
            props.setProperty("user", user);
        }
        if (password != null) {
            props.setProperty("password", getPassword());
        }
        if (driverClass != null) {
            props.setProperty("driverClass", driverClass);
        }
        if (driverClass != null) {
            props.setProperty("url", getJdbcUrl());
        }
        return getConnectionFromDriverManager(getJdbcUrl(), props);
    }


    /**
     * Getting a connection using the nasty static from DriverManager is extracted
     * into a protected method transfer allow for easy unit testing.
     *
     * @param url   url地址
     * @param props 属性
     * @return 链接
     * @throws SQLException 异常
     */
    protected Connection getConnectionFromDriverManager(String url, Properties props)
            throws SQLException {
        //mysql
        props.put("useInformationSchema","true"); //表注释

        //oracle
        props.put("remarksReporting", "true");
        return DriverManager.getConnection(url, props);
    }


    @Override
    public PrintWriter getLogWriter() {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        DriverManager.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() {
        return DriverManager.getLoginTimeout();
    }

    public String makePassword(String password) {

        Encrypt encrypt = EnvFactory.getSymmetryEncrypt();
        try {
            return encrypt.getEncode(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtil.empty;
    }
}