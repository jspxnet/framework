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

import java.io.Serializable;
import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-8-5
 * Time: 16:10:20
 * 事务接口
 */
public interface Transaction extends Serializable {
    Connection getConnection();

    /**
     * 为了兼容分布式事务处理
     *
     * @throws Throwable 事务错误
     */
    void begin() throws Throwable;

    void commit() throws Throwable;

    void rollback();

    boolean wasRolledBack();

    boolean wasCommitted();

    boolean isActive();

    String getTransactionId();

    void setTransactionId(String transactionId);

    boolean isClosed();

    int getTimeout();

    long getCreateTimeMillis();

    void reset();
}