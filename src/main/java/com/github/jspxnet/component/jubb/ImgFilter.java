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


import com.github.jspxnet.utils.StringUtil;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static java.util.regex.Pattern.compile;

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2004-4-21
 * Time: 19:57:44
 * JUBB
 */
public class ImgFilter extends HTMLFilter {
    public ImgFilter(String s) {
        super(s);
    }

    public ImgFilter() {
    }

    @Override
    public String convertString() {
        return imgConverter();
    }

    public String imgConverter() {
        Pattern pattern = compile("(\\[img=(.[^\\[]*)\\])(.[^\\[]*)(\\[\\/img\\])", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        StringBuffer stringbuffer = new StringBuffer();
        for (boolean flag = matcher.find(); flag; flag = matcher.find()) {
            String wh = matcher.group(2);
            int w = -1, h = -1;
            if (wh != null && wh.contains(",")) {
                w = StringUtil.toInt(StringUtil.substringBefore(wh, ","));
                h = StringUtil.toInt(StringUtil.substringAfterLast(wh, ","));
            } else if (wh != null && !wh.contains(",")) {
                w = StringUtil.toInt(wh);
                if (w > 0) {
                    h = w;
                }
            }
            if (w < 0) {
                w = h;
            }
            if (h < 0) {
                h = w;
            }
            if (w > 700) {
                w = 600;
            }
            if (h > 700) {
                h = 600;
            }
            String fileName = StringUtil.trim(getSafetyUrl(matcher.group(3)));

            StringBuilder sb = new StringBuilder();
            if (!StringUtil.isNull(fileName)) {
                sb.append("<img class=\"localimg\" src=\"").append(fileName).append("\" ");
                if (w > 0) {
                    sb.append("width=\"").append(w).append("\" ");
                }
                if (h > 0) {
                    sb.append("height=\"").append(h).append("\" ");
                }
                sb.append(" border=\"0\" onclick=\"clickImg(this);\" />");
            }
            matcher.appendReplacement(stringbuffer, sb.toString());
        }
        matcher.appendTail(stringbuffer);

        pattern = compile("(\\[img\\])(http://.[^\\[]*)(\\[\\/img\\])", Pattern.DOTALL);
        matcher = pattern.matcher(s);
        stringbuffer = new StringBuffer();
        String s;
        for (; matcher.find(); matcher.appendReplacement(stringbuffer, s)) {
            String fileName = StringUtil.trim(getSafetyUrl(matcher.group(2)));
            s = "<img src=\"" + fileName + "\" border=0 onload=\"javascript:if(screen.width-333<this.width)this.width=screen.width-333;\" onclick=\"clickImg(this);\">";
        }
        matcher.appendTail(stringbuffer);
        return stringbuffer.toString();
    }
}