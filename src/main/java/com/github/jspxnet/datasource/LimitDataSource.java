package com.github.jspxnet.datasource;

import com.github.jspxnet.network.mail.core.SendEmailAdapter;
import com.github.jspxnet.sober.util.JdbcUtil;
import com.github.jspxnet.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by yuan on 14-4-17.
 * 压力测试通过，不要随便调整
 */
@Slf4j
public class LimitDataSource extends DriverManagerDataSource {

    final private transient com.github.jspxnet.datasource.ConnectionProxy[] connectionPool = new com.github.jspxnet.datasource.ConnectionProxy[8];
    final private int maxPoolSize = connectionPool.length;
    private int maxConnectionTime = DateUtil.MINUTE;  //8小时
    private String checkSql = "SELECT 1";
    private boolean mailTips = false;
    private int mailSendTimes = 0;
    private int minPoolSize = 3;
    private int sleepSecond = 1;
    private String smtp = "mail.gzec.com.cn:26";
    private String mailFrom = "public@gzec.com.cn";
    private String mailUser = "public@gzec.com.cn";
    private String mailPassword = "111111";
    private String mailSendTo = "39793751@qq.com";
    private int current = 0;

    //是否允许高并发的时候使用不安全连接 false 表示使用 true表示不使用
    public LimitDataSource() {


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
        return 2;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getSleepSecond() {
        return sleepSecond;
    }

    public void setSleepSecond(int sleepSecond) {
        this.sleepSecond = sleepSecond;
    }

    public void setMaxPoolSize(int maxPoolSize) {

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
        if (maxConnectionTime < 1000) {
            this.maxConnectionTime = 1000;
        } else {
            this.maxConnectionTime = maxConnectionTime;
        }
    }

    @Override
    public com.github.jspxnet.datasource.ConnectionProxy getConnection() throws SQLException {
        try {
            for (int i = 0; i < connectionPool.length; i++) {
                com.github.jspxnet.datasource.ConnectionProxy conn = connectionPool[i];
                if (conn == null) {
                    connectionPool[i] = createConnectionProxy();
                } else if (conn.isOvertime() || !conn.isConnect()) {
                    JdbcUtil.closeConnection(conn,true);
                    connectionPool[i] = createConnectionProxy();
                }
            }
        } catch (Exception e) {
            close();
            log.error("连接发生异常", e);
        }
        current++;
        if (current >= maxPoolSize) {
            current = 0;
        }
        com.github.jspxnet.datasource.ConnectionProxy conn = connectionPool[current];
        conn.open();
        return conn;
    }


    /**
     * 创建一个连接
     *
     * @param userName 用户名
     * @param password 密码
     * @return Connection
     * @throws SQLException 异常
     */
    @Override
    public Connection getConnection(String userName, String password) throws SQLException {
        setUser(userName);
        setPassword(password);
        return getConnection();
    }

    /**
     * 关闭连接
     */
    public void close() {
        for (com.github.jspxnet.datasource.ConnectionProxy connectionA : connectionPool) {
            if (connectionA != null) {
                connectionA.release();
            }
        }
    }

    /**
     * @return ConnectionProxy  创建代理链接
     */
    private com.github.jspxnet.datasource.ConnectionProxy createConnectionProxy() {
        try {
            com.github.jspxnet.datasource.ConnectionProxy connectionProxy = (com.github.jspxnet.datasource.ConnectionProxy) Proxy.newProxyInstance(com.github.jspxnet.datasource.ConnectionProxy.class.getClassLoader(),
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
    public <T> T unwrap
            (java.lang.Class<T> iface) {
        return null;
    }

    @Override
    public boolean isWrapperFor
            (java.lang.Class<?> iface) {
        return false;
    }

    /**
     * @return the parent Logger for this data source
     */
    @Override
    public Logger getParentLogger() {
        return Logger.getLogger(JspxDataSource.class.getName());
    }


}