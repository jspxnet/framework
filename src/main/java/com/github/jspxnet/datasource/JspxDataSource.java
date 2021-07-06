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


import com.github.jspxnet.network.mail.core.SendEmailAdapter;
import com.github.jspxnet.sioc.annotation.Destroy;
import com.github.jspxnet.sioc.annotation.Scheduled;
import com.github.jspxnet.sober.util.JdbcUtil;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2007-2-15  last 2011-07-26
 * Time: 16:20:54
 * 版本 3.0
 * 2.0 修改容器和锁定方式,比1.0性能提升30%,性能和比dbpc,c3p0都好 和bonecp相当
 * 3.0 比2.0性能提升10%,性能和比dbpc,c3p0 bonecp 0.7 都好
 * test.test.modoo
 * <pre>
 *     {@code
 * <bean id="jspxDataSource" class="com.github.jspxnet.datasource.JspxDataSource" >
 * <property name="driverClass">
 * <value>${driverClass}</value>
 * </property>
 * <property name="url">
 * <value><![CDATA[${jdbcUrl}]]></value>
 * </property>
 * <property name="user">
 * <value>${username}</value>
 * </property>
 * <property name="password">
 * <value>${password}</value>
 * </property>
 * <property name="suppressClose">
 * <value>true</value>
 * </property>
 * </bean>
 * jspx.test.sioc
 * <bean id="jspxDataSource" class="com.github.jspxnet.datasource.JspxDataSource" destroy="close" singleton="true">
 * <string name="driverClass">${driverClassName}&gt;/string&lg;
 * <string name="jdbcUrl"><![CDATA[${jdbcUrl}]]>&gt;/string&lg;
 * <string name="user">${username}&gt;/string&lg;
 * <string name="password"><![CDATA[${password}]]>&gt;/string&lg;
 * <int name="maxPoolSize">${maxPoolSize}</int>
 * </bean>
 * }
 * </pre>
 * 比较中规中矩的一个连接池，性能不算高，但稳定 ReentrantReadWriteLock 就不要了，在高并发的时候会死锁
 * 最后一个作为备用
 */
@Slf4j
public class JspxDataSource extends DriverManagerDataSource {

    private int maxPoolSize = 8;
    private transient ConnectionProxy[] connectionPool = new ConnectionProxy[(maxPoolSize)];
    private int maxConnectionTime = DateUtil.MINUTE*10;  //1小时 超时关闭
    private String checkSql = "SELECT 1";
    private boolean mailTips = false;
    private int mailSendTimes = 0;
    private int minPoolSize = 3;
    private String smtp = ""; //mail.gzec.com.cn
    private String mailFrom = ""; //public@gzec.com.cn
    private String mailUser = "";
    private String mailPassword = ""; //111111
    private String mailSendTo = ""; //39793751@qq.com
    private boolean isRun = true;

    public JspxDataSource() {
    }


    public boolean isMailTips() {
        return mailTips;
    }

    public void setMailTips(boolean mailTips) {
        this.mailTips = mailTips;
    }

    public String getSmtp() {
        return smtp;
    }

    public void setSmtp(String smtp) {
        this.smtp = smtp;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getMailUser() {
        return mailUser;
    }

    public void setMailUser(String mailUser) {
        this.mailUser = mailUser;
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }

    public String getMailSendTo() {
        return mailSendTo;
    }

    public void setMailSendTo(String mailSendTo) {
        this.mailSendTo = mailSendTo;
    }


    public int getPoolSize() {
        int result = 0;
        for (ConnectionProxy conn : connectionPool) {
            if (conn != null) {
                result++;
            }
        }
        return result;
    }

    public int getMaxPoolSize() {
        return maxPoolSize + 1;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        if (this.maxPoolSize == maxPoolSize) {
            return;
        }
        this.maxPoolSize = maxPoolSize;
        if (this.maxPoolSize < 3) {
            this.maxPoolSize = 3;
        }
        synchronized (this) {
            close();
            connectionPool = new ConnectionProxy[(this.maxPoolSize)];
        }
    }

    //兼容c3p0
    public void setMinPoolSize(int minPoolSize) {
        //不需要，系统默认保持最优
        this.minPoolSize = minPoolSize;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public int getMaxConnectionTime() {
        return maxConnectionTime;
    }

    public String getCheckSql() {
        return checkSql;
    }

    public void setCheckSql(String checkSql) {
        this.checkSql = checkSql;
    }

    public void setMaxConnectionTime(int maxConnectionTime) {
        this.maxConnectionTime = Math.max(maxConnectionTime, DateUtil.MINUTE*5);
    }

    @Override
    public ConnectionProxy getConnection() {
        try {
            for (int i = 0; i < connectionPool.length; i++) {
                ConnectionProxy conn = connectionPool[i];
                if (conn == null) {
                    return connectionPool[i] = createConnectionProxy();
                }
                if (conn.isClosed() && conn.open()) {
                    return conn;
                }
                if (conn.isOvertime() && conn.isConnect() && conn.open()) {
                    return conn;
                }
            }
        } catch (SQLException e) {
            close();
            log.error("连接发生异常,当前最大连接数为:" + maxPoolSize + "当前连接数:" + getPoolSize() + ",已经不能分配连接," + System.getenv("user.dir"), e);
        }
        //留一个作为备用链接begin
        int outI = connectionPool.length - 1;

        //留一个作为备用链接end
        if (connectionPool[outI].open()) {
            return connectionPool[outI];
        }
        log.error("连接发生异常,不能创建连接,请检查数据库连接配置是否正确:" + getJdbcUrl() + " " + System.getenv("user.dir"));
        return null;
    }


    /**
     * 创建一个连接
     *
     * @param userName 用户名
     * @param password 密码
     */
    @Override
    public Connection getConnection(String userName, String password) {
        setUser(userName);
        setPassword(password);
        return getConnection();
    }

    /**
     * 关闭连接
     */
    public void close() {
        for (int i = 0; i < connectionPool.length; i++) {
            if (connectionPool[i] != null) {
                connectionPool[i].release();
            }
            connectionPool[i] = null;
        }
    }

    @Destroy
    public void shutdown() {
        isRun = false;
        close();
    }

    /**
     * @return ConnectionProxy  创建代理链接
     */
    private ConnectionProxy createConnectionProxy() {
        try {
            ConnectionProxy connectionProxy = (ConnectionProxy) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(),
                    new Class[]{ConnectionProxy.class}, new ConnectionInvocationHandler(getConnectionFromDriverManager()));
            connectionProxy.setCheckSql(checkSql);
            connectionProxy.setMaxConnectionTime(maxConnectionTime);
            return connectionProxy;
        } catch (SQLException e) {
            e.printStackTrace();
            if (mailTips && mailSendTimes < 3) {
                SendEmailAdapter theMail = new SendEmailAdapter();
                theMail.setSmtpHost(smtp);
                theMail.setFrom(mailFrom);
                theMail.setUser(mailUser);
                theMail.setPassword(mailPassword);
                theMail.setSendTo(mailSendTo);
                theMail.setSubject("连接池创建链接错误|jdbc create connection error info");
                theMail.setBody("jdbcUrl=" + getJdbcUrl() + "<br>" + com.github.jspxnet.utils.StringUtil.toBrLine(e.getLocalizedMessage()));
                theMail.sendMail();
                mailSendTimes++;
            }
        }
        return null;
    }

    @Override
    public <T> T unwrap(java.lang.Class<T> iface) {
        return null;
    }

    @Override
    public boolean isWrapperFor
            (java.lang.Class<?> iface) {
        return false;
    }

    /**
     * @return the parent Logger for this data source
     * @since 1.7
     */
    @Override
    public Logger getParentLogger() {
        return Logger.getLogger(JspxDataSource.class.getName());
    }

    @Scheduled(force = true)
    public void run() {
        if (!isRun || ArrayUtil.isEmpty(connectionPool)) {
            return;
        }

        int poolSize = getPoolSize();
        try {
            for (int i = 0; i < connectionPool.length; i++) {
                ConnectionProxy conn = connectionPool[i];
                if (conn == null) {
                    if (poolSize < minPoolSize) {
                        connectionPool[i] = createConnectionProxy();
                        poolSize++;
                        continue;
                    }
                }

                if (conn != null) {
                    if (!conn.isConnect()||conn.isClosed()&&conn.isOvertime()) {
                        //周期比较长,有就直接关闭
                        JdbcUtil.closeConnection(conn, true);
                        connectionPool[i] = null;
                        continue;
                    }
                    if (!conn.isClosed()&&conn.isOvertime()) {
                        conn.close();
                        Thread.sleep(300);
                        JdbcUtil.closeConnection(conn, true);
                        connectionPool[i] = null;
                    }
                }
            }
            log.debug("minPoolSize:{},连接池有效长度:{}", minPoolSize, poolSize);
        } catch (Exception e) {
            log.error("连接池线程异常", e);
        }
    }
}