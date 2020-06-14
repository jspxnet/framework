/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.exception;


import com.github.jspxnet.txweb.annotation.Transaction;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-8-22
 * Time: 10:02:13
 * 事务异常
 */
public class TransactionException extends Exception {
    private Transaction transaction;
    public TransactionException(String msg) {
        super(msg);
    }

    public TransactionException(Throwable e,Transaction transaction,String msg) {
        super(msg, e);
        this.transaction = transaction;
    }

    public TransactionException(Throwable e, Transaction transaction) {
        super(e);
        this.transaction = transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}