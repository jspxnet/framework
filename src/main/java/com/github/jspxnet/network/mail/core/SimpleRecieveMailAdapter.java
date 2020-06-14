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

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-9-18
 * Time: 16:01:12
 * GmailRecieveMailAdapter
 */
public class SimpleRecieveMailAdapter extends AbstractRecivevMail {
    public SimpleRecieveMailAdapter() {
        port = 110;
    }


    public Message[] getMessages() {

        Properties props = new Properties();
        props.setProperty("mail.pop3.socketFactory.fallback", "false");
        props.setProperty("mail.pop3.port", NumberUtil.toString(port));
        props.setProperty("mail.pop3.socketFactory.port", NumberUtil.toString(port));
        props.setProperty("mail.pop3.rsetbeforequit", "true");
        props.setProperty(POP_HOST, popHost);
        props.setProperty(POP_AUTH, "true");

        Session mysession = Session.getDefaultInstance(props, null);
        Store store = null;
        Folder folder = null;
        try {
            store = mysession.getStore("pop3");
            store.connect(popHost, user, password);
            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.FLAGS);
            fp.add("X-Mailer");
            folder.fetch(folder.getMessages(), fp);
            log.debug("recieve mail succeed");
            return folder.getMessages();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (folder != null) {
                    folder.close(false);
                }
                if (store != null) {
                    store.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}