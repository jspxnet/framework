/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.network.mail.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-9-18
 * Time: 15:44:15
 * 接收邮件
 */
public abstract class AbstractRecivevMail {
    protected static final Logger log = LoggerFactory.getLogger(AbstractRecivevMail.class);
    static final public String POP_HOST = "pop3.smtp.host";
    static final public String POP_AUTH = "pop3.smtp.auth";
    protected String popHost = "mail.gzec.com.cn";
    protected String user = "public";
    protected String password = "111111";
    protected int port = 25;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPopHost() {
        return popHost;
    }

    public void setPopHost(String popHost) {
        this.popHost = popHost;
    }

}