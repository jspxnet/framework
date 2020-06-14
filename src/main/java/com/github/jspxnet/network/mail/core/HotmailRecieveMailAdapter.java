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

import javax.mail.*;
import java.util.Properties;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-9-19
 * Time: 11:45:54
 */
public class HotmailRecieveMailAdapter extends AbstractRecivevMail {
    final static String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    public HotmailRecieveMailAdapter() {
        port = 995;
    }

    public Message[] getMessages() {
        try {
            Properties prop = new Properties();
            Session ses = Session.getInstance(prop);
            Store store = ses.getStore("jdavmail");
            store.connect(null, user, password);
            if (store.isConnected()) {
                Folder inbox = store.getFolder("INBOX");
                return inbox.getMessages();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}