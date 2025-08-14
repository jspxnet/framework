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
 * User: chenYuan
 * date: 2004-4-21
 * Time: 20:06:34
 * 颜色设置
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorFilter extends HTMLFilter {
    private static final String REGEX = "(\\[color=(.[^\\[]*)\\])(.+?)(\\[\\/color\\])";
    public ColorFilter(String s) {
        super(s);
    }

    public ColorFilter() {
    }

    @Override
    public String convertString() {
        return colorConverter();
    }

    public String colorConverter() {
        //([^\\]]*)

        Pattern pattern = Pattern.compile(REGEX, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        StringBuffer stringbuffer = new StringBuffer();
        for (boolean flag = matcher.find(); flag; flag = matcher.find()) {
            if (matcher.group(2).length() > 7) {
                matcher.appendReplacement(stringbuffer, "<font>" + matcher.group(3) + "</font>");
            } else {
                matcher.appendReplacement(stringbuffer, "<font color=" + matcher.group(2) + ">" + matcher.group(3) + "</font>");
            }
        }
        matcher.appendTail(stringbuffer);
        return stringbuffer.toString();
    }
}