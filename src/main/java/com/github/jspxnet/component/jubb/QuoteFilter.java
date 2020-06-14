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
 * Time: 20:01:44
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuoteFilter extends HTMLFilter {
    public QuoteFilter(String s) {
        super(s);
    }

    public QuoteFilter() {
    }

    @Override
    public String convertString() {
        return quoteConverter();
    }

    public String quoteConverter() {

        String regex = "(\\[quote\\])(.+?)(\\[\\/quote\\])";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(this.s);
        StringBuffer stringbuffer = new StringBuffer();
        for (boolean flag = matcher.find(); flag; flag = matcher.find()) {
            if (matcher.group(2) != null) {
                matcher.appendReplacement(stringbuffer, "<div class=\"quote\">" + matcher.group(2) + "</div>");
            }
        }
        matcher.appendTail(stringbuffer);
        return stringbuffer.toString();
    }

    /*
    <div class="topicCitation cB02">
<p class="citationSummary">引用：(&nbsp;原贴由<span>某某某</span>发表于&nbsp;2007-9-10&nbsp;22:00&nbsp;)
引用内容是否需要取某个字数呢？还是全部引用显示呢？
</div>
     */
}