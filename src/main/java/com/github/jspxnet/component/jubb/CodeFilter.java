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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.*;

/**
 * Created by IntelliJ IDEA.
 * User:
 * date: 2004-4-22
 * Time: 10:59:35
 * SyntaxHighlighter
 */

public class CodeFilter extends HTMLFilter {
    private String[] codeType = {"c", "c#", "css", "delphi", "java", "js", "php", "python", "ruby", "sql", "vb", "xml"};

    public CodeFilter(String s) {
        super(s);
    }

    public CodeFilter() {
    }

    @Override
    public String convertString() {
        return getTextFilter();
    }

    public String getTextFilter() {
        return codeConverter();
    }

    public String codeConverter() {
        Pattern pattern = compile("(\\[code=(.[^\\[]*)\\])(.([^\\[]|\\n|\\r)*)(\\[\\/code\\])", DOTALL);
        Matcher matcher = pattern.matcher(s);
        StringBuffer stringbuffer = new StringBuffer();
        for (boolean flag = matcher.find(); flag; flag = matcher.find()) {
            String lan = matcher.group(2);
            if (StringUtil.isNull(lan)) {
                lan = "java";
            }
            lan = lan.toLowerCase().replace("\"", "").trim();
            if (!ArrayUtil.inArray(codeType, lan, true)) {
                lan = "java";
            }
            matcher.appendReplacement(stringbuffer, "<textarea name=\"code\" class=\"" + lan + "\" rows=\"15\" cols=\"100\">\r\n" + matcher.group(3) + "\r\n</textarea>");
        }
        matcher.appendTail(stringbuffer);
        pattern = compile("(\\[code\\])(.([^\\[]|\\n|\\r)*)(\\[\\/code\\])", DOTALL);
        matcher = pattern.matcher(stringbuffer.toString());
        stringbuffer.setLength(0);
        for (boolean flag = matcher.find(); flag; flag = matcher.find()) {
            matcher.appendReplacement(stringbuffer, "<textarea name=\"code\" class=\"java\" rows=\"15\" cols=\"100\">\r\n" + matcher.group(2) + "\r\n</textarea>");
        }
        matcher.appendTail(stringbuffer);
        return stringbuffer.toString();
    }
}