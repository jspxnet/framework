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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2005-12-30
 * Time: 15:37:52
 */
public class JspxNetContextListener implements ServletContextListener {

    public JspxNetContextListener() {

    }

    @Override
    public void contextInitialized(ServletContextEvent evt) {
        if (!JspxNetApplication.checkRun()) {
            JspxNetApplication.autoRun();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent evt) {
        JspxNetApplication.destroy();
    }
}