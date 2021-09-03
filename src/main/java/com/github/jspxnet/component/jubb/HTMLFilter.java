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

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2004-4-21
 * Time: 19:52:40
 */
public abstract class HTMLFilter implements Filter {
    final static String regex = "^javascript\\S*|^javascrip\\S*||^java\\S*|^script\\S*|^cscript\\S*|\\w+background-image.*|\\w+alert.*|\\w+document.*|\\w+group_concat.*|\\w+delete.*|\\w+update.*|\\w+drop.*|\\w+\".*|\\w+'.*|\\w+>.*|w+<\\S*|mailto\\S*|w+\\(\\S*|w+\\)S*|w+\\{S*|w+\\}\\S*|.*\\.js$||.*\\.css";
    final static Pattern safePattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE); //编译一个正则表达式，同时生成Pattern

    protected String s;


    public HTMLFilter(String s1) {
        s = StringUtil.empty;
        s = s1;
    }

    public HTMLFilter() {
        s = StringUtil.empty;
    }

    String empath = "images/smiles/default/";

    public String getEmpath() {
        return empath;
    }

    public void setEmpath(String empath) {
        this.empath = empath;
    }

    @Override
    public void setInputString(String s1) {
        s = s1;
    }

    @Override
    public String getInputString() {
        return s;
    }

    @Override
    public String getFilterString() {
        return convertString();
    }

    public String convertString() {
        return StringUtil.empty;
    }

    /**
     * @param url URL:开头不能为 javascript,mailto,#
     * @return 得到一个安全的URL
     */
    public String getSafetyUrl(String url) {
        if (StringUtil.isNull(url)) {
            return "#";
        }
        try {
            Matcher m = safePattern.matcher(url); //匹配到得用去掉
            return m.replaceAll("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean isSafetyUrl(String url) {
        return !StringUtil.isNull(url) && !url.matches(safePattern.toString());
    }
}