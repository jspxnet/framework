/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.network.mail;

import javax.mail.Message;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-9-18
 * Time: 15:33:39
 */
public interface MailAdapter {
    int getPort();

    void setPort(int port);

    String getHost();

    void setHost(String host);

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    String getPoptype();

    void setPoptype(String poptype);

    Message[] getMessages();

}