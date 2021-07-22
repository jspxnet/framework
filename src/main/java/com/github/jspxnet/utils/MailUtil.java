/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.utils;

import lombok.extern.slf4j.Slf4j;



import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2005-8-4
 * Time: 15:48:05
 */

public class MailUtil {
    public static final int MAX_MESSAGES_PER_TRANSPORT = 100;

    private MailUtil() {// prevent instantiation
    }

    /**
     * @param mail 邮箱
     * @return 发送的邮箱地址列表
     */
    public static String[] getMails(String mail) {
        if (mail == null) {
            mail = StringUtil.empty;
        }
        mail = mail.trim();// very important
        mail = mail.replace(',', ';');
        //replace all occurrence of ',' transfer ';'
        StringTokenizer t = new StringTokenizer(mail, StringUtil.SEMICOLON);
        String[] ret = new String[t.countTokens()];
        int index = 0;
        while (t.hasMoreTokens()) {
            String mails = StringUtil.trim(t.nextToken());
            if (!ValidUtil.isMail(mails)) {
                continue;
            }
            ret[index] = mails;
            index++;
        }
        return ret;
    }

    public static String[] getMails(String to, String cc, String bcc) {
        String[] toMail = getMails(to);
        String[] ccMail = getMails(cc);
        String[] bccMail = getMails(bcc);
        String[] ret = new String[toMail.length + ccMail.length + bccMail.length];
        int index = 0;
        for (String aToMail : toMail) {
            ret[index] = aToMail;
            index++;
        }
        for (String aCcMail : ccMail) {
            ret[index] = aCcMail;
            index++;
        }
        for (String aBccMail : bccMail) {
            ret[index] = aBccMail;
            index++;
        }
        return ret;
    }
}