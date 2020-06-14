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


import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.ValidUtil;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2004-4-21
 * Time: 19:57:44
 * JUBB
 */
public class FontFilter extends HTMLFilter {
    public FontFilter() {
    }

    public FontFilter(String s) {
        super(s);
    }

    @Override
    public String convertString() {
        return getTextFilter();
    }

    public String getTextFilter() {
        s = fontConverter("size");
        s = fontConverter("face");
        s = fontConverter("align");
        return s;
    }

    public String fontConverter(String s) {
        String s1 = StringUtil.empty;
        if ("size".equals(s)) {
            s1 = "\\[size=([1-8])\\]((.|\\n|\\r)*)(\\[\\/size\\])";
        } else if ("align".equals(s)) {
            s1 = "\\[align=(.[^\\[]*)\\]((.|\\n|\\r)*)(\\[\\/align\\])";
        } else {
            return this.s;
        }
        Pattern pattern = Pattern.compile(s1, Pattern.UNIX_LINES);
        Matcher matcher = pattern.matcher(this.s);
        StringBuffer stringbuffer = new StringBuffer();
        for (boolean flag = matcher.find(); flag && matcher.group(0) != null; flag = matcher.find()) {
            if (matcher.group(1) == null) {
                continue;
            }
            if ("size".equals(s)) {
                String size = matcher.group(1);
                if (ValidUtil.isNumber(size) && StringUtil.toInt(size) > 0 && StringUtil.toInt(size) <= 42) {
                    matcher.appendReplacement(stringbuffer, "<font size=" + matcher.group(1) + ">" + matcher.group(2) + "</font>");
                }
                continue;
            }
            if ("face".equals(s)) {
                matcher.appendReplacement(stringbuffer, "<font face=" + matcher.group(1) + ">" + matcher.group(2) + "</font>");
                continue;
            }
            if ("align".equals(s)) {
                String align = matcher.group(1);
                if (ArrayUtil.inArray(new String[]{"left", "right", "center", "justify"}, align, true)) {
                    matcher.appendReplacement(stringbuffer, "<div align=" + matcher.group(1) + ">" + matcher.group(2) + "</div>");
                }
            }
        }

        matcher.appendTail(stringbuffer);
        return stringbuffer.toString();
    }
}