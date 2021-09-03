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


import com.github.jspxnet.utils.XMLUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class URLFilter extends HTMLFilter {
    public URLFilter()
    {

    }

    public URLFilter(String s) {
        super(s);
    }


    @Override
    public String convertString() {
        return URLConverter();
    }

    public String URLConverter() {
        Pattern pattern = compile("(\\[(link)=([^\\[]*)\\])([^\\[]*)(\\[\\/(link)\\])", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(s);
        StringBuffer stringbuffer = new StringBuffer();
        for (boolean flag = matcher.find(); flag; flag = matcher.find()) {
            StringBuilder tempBuilder = new StringBuilder();
            tempBuilder.append("<a href=\"").append(matcher.group(3)).append("\" target=\"_blank\">").append(matcher.group(4)).append("</a>");
            matcher.appendReplacement(stringbuffer, tempBuilder.toString());
        }
        matcher.appendTail(stringbuffer);

        pattern = compile("(\\[(URL)((=(((http://)|(ftp://))?)(.*))?)\\])(.[^\\[]*)(\\[(\\/URL)\\])", Pattern.CASE_INSENSITIVE + Pattern.COMMENTS);
        matcher = pattern.matcher(stringbuffer.toString());
        stringbuffer.setLength(0);

        for (boolean flag = matcher.find(); flag; flag = matcher.find()) {

            if (!"".equals(matcher.group(3))) {
                if (isSafetyUrl(matcher.group(9))) {
                    matcher.appendReplacement(stringbuffer, "<a href=\"");
                    if ("".equals(matcher.group(5))) {
                        if (matcher.group(10) == null) {
                            stringbuffer.append("http://").append(matcher.group(9)).append("\" target=\"_blank\">").append(XMLUtil.escape(matcher.group(9))).append("</a>");
                        } else {
                            stringbuffer.append("http://").append(matcher.group(9)).append("\" target=\"_blank\">").append(XMLUtil.escape(matcher.group(10))).append("</a>");
                        }
                        continue;
                    }
                    if (matcher.group(10) == null) {
                        stringbuffer.append(matcher.group(5)).append(matcher.group(9)).append("\" target=\"_blank\">").append(XMLUtil.escape(matcher.group(5))).append(matcher.group(9)).append("</a>");
                    } else {
                        stringbuffer.append(matcher.group(5)).append(matcher.group(9)).append("\" target=\"_blank\">").append(XMLUtil.escape(matcher.group(10))).append("</a>");
                    }

                } else {
                    matcher.appendReplacement(stringbuffer, "<span class=\"safetyHref\">");
                    stringbuffer.append(matcher.group(5) == null ? "" : matcher.group(5)).append(matcher.group(9) == null ? "" : matcher.group(9)).append("(").append(XMLUtil.escape(matcher.group(10))).append(")").append("</span>");
                }
                continue;
            }
            if (isSafetyUrl(matcher.group(10))) {
                if ("".equals(matcher.group(11))) {
                    matcher.appendReplacement(stringbuffer, "<a href=\"");
                    stringbuffer.append("http://").append(matcher.group(10)).append("\" target=\"_blank\">").append(XMLUtil.escape(matcher.group(10))).append("</a>");
                } else if (!"".equals(matcher.group(11))) {
                    matcher.appendReplacement(stringbuffer, "<a href=\"");
                    stringbuffer.append(matcher.group(10)).append("\" target=\"_blank\">").append(XMLUtil.escape(matcher.group(10))).append("</a>");
                }
            } else {
                matcher.appendReplacement(stringbuffer, "<span class=\"safetyHref\">");
                stringbuffer.append(matcher.group(5) == null ? "" : matcher.group(5)).append(matcher.group(9) == null ? "" : matcher.group(9)).append("(").append(XMLUtil.escape(matcher.group(10))).append(")").append("</span>");
            }
        }
        matcher.appendTail(stringbuffer);
        return stringbuffer.toString();

    }
}