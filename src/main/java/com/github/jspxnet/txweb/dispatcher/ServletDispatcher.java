/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.dispatcher;

import com.github.jspxnet.boot.JspxNetApplication;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2007-12-20
 * Time: 12:40:51
 * tomcat get 方法需要设置编码修复
 */
public class ServletDispatcher extends HttpServlet implements javax.servlet.Servlet {
    public ServletDispatcher() {

    }

    /**
     * 初始平台
     *
     * @param servletConfig 配置
     */
    @Override
    public void init(ServletConfig servletConfig) {
        ServletContext servletContext = servletConfig.getServletContext();
        Dispatcher.init(servletContext);
    }

    /**
     * @param servletRequest  Request  请求
     * @param servletResponse Response 应答
     */
    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Dispatcher.getInstance().wrapRequest(request, response);
    }

    /**
     * 卸载数据
     */
    @Override
    public void destroy() {
        JspxNetApplication.destroy();
        super.destroy();
    }
}