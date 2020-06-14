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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * Created with IntelliJ IDEA.
 * User: yuan
 * date: 12-12-4
 * Time: 下午9:33
 * 回复可见
 */
public class ReplyFilter extends HTMLFilter {
    private boolean reply = false;
    private String replyTip = "回复可见";

    public ReplyFilter(String s) {
        super(s);
    }

    public ReplyFilter() {

    }


    public boolean isReply() {
        return reply;
    }

    public void setReply(boolean reply) {
        this.reply = reply;
    }

    public String getReplyTip() {
        return replyTip;
    }

    public void setReplyTip(String replyTip) {
        this.replyTip = replyTip;
    }

    @Override
    public String convertString() {
        return hideConverter();
    }

    public String hideConverter() {
        Pattern pattern = compile("(\\[reply\\])(.+?)(\\[\\/reply\\])", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        StringBuffer stringbuffer = new StringBuffer();
        for (boolean flag = matcher.find(); flag; flag = matcher.find()) {
            if (reply) {
                matcher.appendReplacement(stringbuffer, matcher.group(2));
            } else {
                matcher.appendReplacement(stringbuffer, replyTip);
            }
        }
        matcher.appendTail(stringbuffer);
        return stringbuffer.toString();
    }
}