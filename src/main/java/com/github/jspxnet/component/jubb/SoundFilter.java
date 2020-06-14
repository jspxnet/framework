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
public class SoundFilter extends HTMLFilter {
    public SoundFilter(String s) {
        super(s);
    }

    public SoundFilter() {
    }

    @Override
    public String convertString() {
        return soundConverter();
    }

    public String soundConverter() {
        Pattern pattern = compile("(\\[sound=([^\\[]*)\\])([^\\[]*)(\\[\\/sound\\])", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        StringBuffer stringbuffer = new StringBuffer();
        for (boolean flag = matcher.find(); flag; flag = matcher.find()) {
            String soundFile = getSafetyUrl(matcher.group(2));
            if (!StringUtil.isNull(soundFile)) {
                String str = "<audio controls=\"controls\" autoplay=\"autoplay\" controls=\"controls\" loop=\"loop\">\n" +
                        "<source src=\"" + soundFile + "\" type=\"audio/mpeg\" />" +
                        matcher.group(3) + "</audio> ";
                matcher.appendReplacement(stringbuffer, str);
            }
        }
        matcher.appendTail(stringbuffer);
        return stringbuffer.toString();
    }
}