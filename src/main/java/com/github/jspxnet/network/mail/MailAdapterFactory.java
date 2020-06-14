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

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.network.mail.core.SendEmailAdapter;
import com.github.jspxnet.txweb.bundle.Bundle;

import com.github.jspxnet.utils.StringUtil;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 12-5-22
 * Time: 下午11:55
 */
public class MailAdapterFactory {
    private MailAdapterFactory() {

    }

    public static SendEmailAdapter getSendEmailAdapter(Bundle config) {
        SendEmailAdapter mailAdapter = new SendEmailAdapter();
        mailAdapter.setSmtpHost(config.get(Environment.mailSmtp));
        String from = config.get(Environment.mailUser);
        if (!StringUtil.isNull(from) && !from.contains("@") && config.get(Environment.mailSmtp).startsWith("smtp.")) {
            from = from + "@" + StringUtil.substringAfter(config.get(Environment.mailSmtp), "smtp.");
        }
        mailAdapter.setFrom(config.get(Environment.mailUser));
        mailAdapter.setUser(config.get(Environment.mailUser));
        mailAdapter.setPassword(config.get(Environment.mailPassword));
        mailAdapter.setSendTo(config.get(Environment.manageMail));
        return mailAdapter;
    }
    // SendEmailAdapter
}