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

import com.github.jspxnet.utils.NumberUtil;

import javax.mail.*;
import java.util.Properties;
import java.security.Security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-9-18
 * Time: 16:35:02
 * YahooRecieveMailAdapter
 */

public class GmailRecieveMailAdapter extends AbstractRecivevMail {
    private static final Logger log = LoggerFactory.getLogger(GmailRecieveMailAdapter.class);
    final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    public GmailRecieveMailAdapter() {
        port = 995;
    }

    public Message[] getMessages() {
        Properties props = new Properties();
        props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.pop3.socketFactory.fallback", "false");
        props.setProperty("mail.pop3.port", NumberUtil.toString(port));
        props.setProperty("mail.pop3.socketFactory.port", NumberUtil.toString(port));
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        // 以下步骤跟一般的JavaMail操作相同
        Session session = Session.getDefaultInstance(props, null);
        //请将红色部分对应替换成你的邮箱帐号和密码
        URLName urln = new URLName("pop3", popHost, port, null, user, password);
        Folder inbox = null;
        Store store = null;
        try {
            store = session.getStore(urln);
            store.connect();
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            return inbox.getMessages();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (inbox != null) {
                try {
                    inbox.close(false);

                } catch (Exception e) {
                    log.error(MessageFormatter.format("host {} port {} user {}", new String[]{popHost, "" + port, user}).getMessage(), e);
                }
            }
            if (store != null) {
                try {
                    store.close();
                } catch (Exception e) {
                    log.error(MessageFormatter.format("host {} port {} user {}", new String[]{popHost, "" + port, user}).getMessage(), e);
                }
            }
        }
        return null;
    }

}