/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.jubb;



import com.github.jspxnet.utils.ValidUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2004-4-21
 * Time: 19:57:44
 * JUBB
 */
public class MailFilter extends HTMLFilter {
    public MailFilter() {
    }

    public MailFilter(String s) {
        super(s);
    }

    @Override
    public String convertString() {
        return getMailFilter();
    }

    public String getMailFilter() {
        s = mailConverter("mail");
        return mailConverter("mail");
    }

    public String mailConverter(String st) {
        Pattern pattern = null;
        Matcher matcher = null;
        if ("email".equals(st)) {
            pattern = compile("\\[email=(.[^\\[]*)\\](.*)(\\[\\/email\\])|(\\[email\\])(.[^\\[]*)(\\[\\/email\\])", Pattern.DOTALL);
            matcher = pattern.matcher(s);
        }
        if ("mail".equals(st)) {
            pattern = compile("\\[mail=(.[^\\[]*)\\](.*)(\\[\\/mail\\])|(\\[mail\\])(.[^\\[]*)(\\[\\/mail\\])", Pattern.DOTALL);
            matcher = pattern.matcher(s);
        }
        StringBuffer stringbuffer = new StringBuffer();
        if (matcher != null) {
            for (boolean flag = matcher.find(); flag && matcher.group(0) != null; flag = matcher.find()) {
                if (matcher.group(1) != null) {
                    String mail = matcher.group(1);
                    if (ValidUtil.isMail(mail)) {
                        matcher.appendReplacement(stringbuffer, "<a href=\"mailto:" + mail + "\">" + matcher.group(2) + "</a>");
                    }
                    continue;
                }
                if (matcher.group(4) != null) {
                    String mail = matcher.group(5);
                    if (ValidUtil.isMail(mail)) {
                        matcher.appendReplacement(stringbuffer, "<a href=\"mailto:" + mail + "\">" + mail + "</a>");
                    }
                }
            }
            matcher.appendTail(stringbuffer);
        }
        return stringbuffer.toString();
    }
}