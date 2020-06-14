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
public class TextStyleFilter extends HTMLFilter {
    public TextStyleFilter(String s) {
        super(s);
    }

    public TextStyleFilter() {
    }

    @Override
    public String convertString() {
        return getTextFilter();
    }

    public String getTextFilter() {
        return textStyleConverter();
    }

    public String textStyleConverter() {
        Pattern p = compile("\\[(b)\\](.[^\\[]*)\\[\\/(b)\\]|\\[(i)\\](.[^\\[]*)\\[\\/(i)\\]|\\[(u)\\](.[^\\[]*)\\[\\/(u)\\]|\\[(center)\\](.[^\\[]*)\\[\\/(center)\\]", Pattern.DOTALL);
        Matcher m = p.matcher(s);
        StringBuffer sb = new StringBuffer();
        for (boolean result = m.find(); result && m.group(0) != null; result = m.find()) {
            if (m.group(1) != null) {
                m.appendReplacement(sb, String.valueOf(String.valueOf((new StringBuffer("<b>")).append(m.group(2)).append("</b>"))));
                continue;
            }
            if (m.group(4) != null) {
                m.appendReplacement(sb, String.valueOf(String.valueOf((new StringBuffer("<i>")).append(m.group(5)).append("</i>"))));
                continue;
            }
            if (m.group(7) != null) {
                m.appendReplacement(sb, String.valueOf(String.valueOf((new StringBuffer("<u>")).append(m.group(8)).append("</u>"))));
                continue;
            }
            if (m.group(10) != null) {
                m.appendReplacement(sb, String.valueOf(String.valueOf((new StringBuffer("<center>")).append(m.group(11)).append("</center>"))));
            }
        }

        m.appendTail(sb);
        return sb.toString();
    }
}