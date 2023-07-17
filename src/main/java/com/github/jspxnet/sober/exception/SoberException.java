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

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-8-4
 * Time: 14:09:24
 */
public class SoberException extends Exception {
    private String msg;
    private String sql;

    public SoberException(String s) {
        super(s);
    }


    public SoberException(Exception e, String msg) {
        super(msg, e);
        this.msg = msg;

    }

    public SoberException(String msg, Exception e, String sql) {
        super(msg + " " + sql, e);
        this.msg = msg;
        this.sql = sql;
    }


    @Override
    public StackTraceElement[] getStackTrace() {
        StackTraceElement[] ses = super.getStackTrace();
        StackTraceElement[] newses = new StackTraceElement[ses.length + 2];
        System.arraycopy(ses, 0, newses, 0, ses.length);
        StackTraceElement st1 = new StackTraceElement("Sober", "msg", msg, 1);
        newses[ses.length + 1] = st1;
        StackTraceElement st2 = new StackTraceElement("Sober", "sql", sql, 2);
        newses[ses.length + 2] = st2;
        return newses;
    }

    /**
     * @return Get the actual SQL
     */
    public String getSQL() {
        return sql;
    }

}