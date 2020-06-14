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

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2004-4-21
 * Time: 19:57:44
 * JUBB
 */

import com.github.jspxnet.utils.XMLUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoFilter extends HTMLFilter {

    public AutoFilter(String s) {
        super(s);
    }

    public AutoFilter() {
    }

    @Override
    public String convertString() {
        return autoURLConverter();
    }

    public String autoURLConverter() {
        String regex =
                "\\b                         # start at word\n"
                        + "                            # boundary\n"
                        + "(                           # capture to $1\n"
                        + "(https?|telnet|gopher|wais|ftp) : \n"
                        + "                            # resource and colon\n"
                        + "[\\w/\\#~:.?+=&%@!\\-] +?   # one or more valid\n"
                        + "                            # characters\n"
                        + "                            # but take as little\n"
                        + "                            # as possible\n"
                        + ")\n"
                        + "(?=                         # lookahead\n"
                        + "[.:?\\-] *                  # for possible punc\n"
                        + "(?: [^\\w/\\#~:.?+=&%@!\\-] # invalid character\n"
                        + "| $ )                       # or end of string\n"
                        + ")";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE + Pattern.COMMENTS);
        Matcher matcher = pattern.matcher(s);
        StringBuffer stringbuffer = new StringBuffer();
        for (boolean flag = matcher.find(); flag && matcher.group(0) != null; flag = matcher.find()) {
            matcher.appendReplacement(stringbuffer, "[url]" + XMLUtil.escapeDecrypt(matcher.group(0)) + "[/url]");
        }
        matcher.appendTail(stringbuffer);
        return stringbuffer.toString();
    }

}