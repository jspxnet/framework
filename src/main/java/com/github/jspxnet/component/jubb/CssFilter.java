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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CssFilter extends HTMLFilter {
    public CssFilter(String s) {
        super(s);
    }

    public CssFilter() {
    }

    @Override
    public String convertString() {
        return getTextFilter();
    }

    public String getTextFilter() {
        s = cssConverter("fly");
        s = cssConverter("move");
        return s;
    }

    public String cssConverter(String s) {

        String result = this.s;
        String s1 = "\\[" + s + "\\](.*)\\[\\/" + s + "\\]";
        Pattern pattern = Pattern.compile(s1, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(result);
        while (matcher != null && matcher.find()) {
            if ("fly".equals(s)) {
                result = matcher.replaceAll("<marquee width=96% behavior=alternate scrollamount=3>$1</marquee>");
            } else {
                result = matcher.replaceAll("<marquee width=96% scrollamount=3 >$1</marquee>");
            }
            matcher = pattern.matcher(result);
        }
        return result;
    }
}