/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.boot;

import com.github.jspxnet.utils.StringUtil;

import com.github.jspxnet.utils.DateUtil;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-5-25
 * Time: 16:31:31
 */
public class JspxNetApplication {

    final private static JspxCoreListener JSPX_CORE_LISTENER = new JspxCoreListener();

    public static boolean checkRun() {
        return com.github.jspxnet.boot.JspxCoreListener.isRun();
    }

    public static void autoRun() {
        autoRun(null);
    }

    public static void autoRun(String defaultPath) {
        if (JspxCoreListener.isRun()) {
            return;
        }
        if (!StringUtil.isNull(defaultPath)) {
            JSPX_CORE_LISTENER.setDefaultPath(defaultPath);
        }
        JSPX_CORE_LISTENER.contextInitialized(null);

    }

    public static void destroy() {
        JSPX_CORE_LISTENER.contextDestroyed(null);
    }

    public static void restart() {
        JSPX_CORE_LISTENER.contextDestroyed(null);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSPX_CORE_LISTENER.contextInitialized(null);
    }

    //得到服务器运行天数

    public static long runDay() {
        return DateUtil.compareDay(new Date(JspxCoreListener.getStartCurrentTimeMillis()));
    }

    //得到服务器运行天数

    public static Date getRunDate() {
        return new Date(JspxCoreListener.getStartCurrentTimeMillis());
    }
}