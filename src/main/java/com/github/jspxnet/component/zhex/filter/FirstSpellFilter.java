/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.zhex.filter;

import com.github.jspxnet.component.zhex.WordFilter;
import com.github.jspxnet.component.zhex.spell.ChineseUtil;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-1-30
 * Time: 下午1:43
 */
public class FirstSpellFilter extends AbstractWordFilter {

    private static WordFilter instance = new FirstSpellFilter();

    public static WordFilter getInstance() {
        return instance;
    }

    private FirstSpellFilter() {

    }

    @Override
    public String doFilter(String str, String to) {
        return ChineseUtil.getFirstSpell(str, to);
    }

    public static void main(String[] args) {
        WordFilter wordFilter = FirstSpellFilter.getInstance();
        try {
            System.out.println(wordFilter.doFilter(args[0], ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}