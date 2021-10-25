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
import com.github.jspxnet.component.zhex.bg2big5.GB2Big5;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-1-30
 * Time: 下午12:13
 */
public class FJFilter extends AbstractWordFilter {

    private static WordFilter instance = null;

    public synchronized static WordFilter getInstance() {
        if (instance==null)
        {
            instance = new FJFilter();
        }
        return instance;
    }

    private FJFilter() {


    }

    @Override
    public String doFilter(String str, String to) {
        if (to == null) {
            to = GB_BIG5;
        }
        try {
            if (GB_BIG5.equalsIgnoreCase(to)) {
                return GB2Big5.getGbkToBig5(str);
            }
            if (BIG5_GB.equalsIgnoreCase(to)) {
                return GB2Big5.getBig5ToGbk(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void main(String[] args) {
        WordFilter wordFilter = FJFilter.getInstance();
        try {
            String out = wordFilter.doFilter("中文三大范德萨阿斯蒂芬", FJFilter.GB_BIG5);
            System.out.println(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}