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
 * User: chenYuan
 * date: 2008-3-7
 * Time: 14:33:56
 */
public class HideFilter extends HTMLFilter {
    private final static String PATTERN_HIDE_FILTER = "(\\[hide=([^\\[]*)\\])(.+?)(\\[\\/hide\\])";


    private int grade = 0;
    private String gradeTip = "等级以上才能查看";

    public HideFilter(String s) {
        super(s);
    }

    public HideFilter() {

    }


    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }


    public String getGradeTip() {
        return gradeTip;
    }

    public void setGradeTip(String gradeTip) {
        this.gradeTip = gradeTip;
    }


    @Override
    public String convertString() {
        return hideConverter();
    }

    public String hideConverter() {
        Pattern pattern = Pattern.compile(PATTERN_HIDE_FILTER, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        StringBuffer stringbuffer = new StringBuffer();
        for (boolean flag = matcher.find(); flag; flag = matcher.find()) {
            String lan = matcher.group(2);
            if (grade >= StringUtil.toInt(lan)) {
                matcher.appendReplacement(stringbuffer, matcher.group(3));
            } else {
                matcher.appendReplacement(stringbuffer, StringUtil.replace(gradeTip, "{0}", lan));
            }
        }
        matcher.appendTail(stringbuffer);

        return hideConverter2(stringbuffer.toString());
    }

    public String hideConverter2(String s) {
        Pattern pattern = compile("(\\[hide\\])(.+?)(\\[\\/hide\\])", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        StringBuffer stringbuffer = new StringBuffer();
        for (boolean flag = matcher.find(); flag; flag = matcher.find()) {
            if (grade>0) {
                matcher.appendReplacement(stringbuffer, matcher.group(2));
            } else {
                matcher.appendReplacement(stringbuffer, StringUtil.replace(gradeTip, "{0}", "0"));
            }
        }
        matcher.appendTail(stringbuffer);
        return stringbuffer.toString();
    }
}