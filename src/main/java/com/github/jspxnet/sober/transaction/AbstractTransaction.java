/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.transaction;

import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.sober.Transaction;
import com.github.jspxnet.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-8-18
 * Time: 14:03:08
 */
@Slf4j
public abstract class AbstractTransaction implements Transaction {
    //事务管理器
    final static transient TransactionManager TRANSACTION_MANAGER = TransactionManager.getInstance();
    protected Connection connection = null;
    protected boolean wasRolledBack = false;
    protected boolean wasCommitted = false;
    protected int isActive = 0;
    protected int timeout = DateUtil.MINUTE*2;
    protected boolean supportsSavePoints = false;
    //数据源
    protected DataSource dataSource = null;

    //事务ID,不再拼装
    protected String transactionId;
    //判断超时
    final private long createTimeMillis = System.currentTimeMillis();

    @Override
    public long getCreateTimeMillis() {
        return createTimeMillis;
    }

    public boolean isSupportsSavePoints() {
        return supportsSavePoints;
    }

    public void setSupportsSavePoints(boolean supportsSavePoints) {
        this.supportsSavePoints = supportsSavePoints;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean wasRolledBack() {
        return wasRolledBack;
    }

    @Override
    public boolean wasCommitted() {
        return wasCommitted;
    }

    @Override
    public boolean isActive() {
        return connection != null && isActive != 0;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public int getTimeout() {
        return this.timeout;
    }


    @Override
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public boolean isClosed() {
        try {
            return (wasRolledBack || wasCommitted) || dataSource == null || connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 计数重载
     */
    @Override
    public void reset()
    {
        wasRolledBack = false;
        wasCommitted = false;
        isActive = 0;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("transactionId:").append(transactionId).append("\r\n");
        sb.append("connection:").append(connection).append("\r\n");
        sb.append("wasRolledBack:").append(wasRolledBack).append("\r\n");
        sb.append("wasCommitted:").append(wasCommitted).append("\r\n");
        sb.append("isActive:").append(isActive).append("\r\n");
        sb.append("timeout:").append(timeout).append("\r\n");
        sb.append("supportsSavePoints:").append(supportsSavePoints).append("\r\n");
        sb.append("dataSource:").append(dataSource.hashCode()).append("\r\n");
        return sb.toString();
    }
}