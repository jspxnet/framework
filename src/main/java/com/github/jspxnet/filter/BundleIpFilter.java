/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.filter;

import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.IpUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2004-5-28
 * Time: 15:41:53
 */
public class BundleIpFilter implements Filter {
    private boolean enabled = true;
    private String errorLink = "nopower.jsp";
    private String expression = StringUtil.ASTERISK;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (!enabled) {
            return;
        }
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String ip = RequestUtil.getRemoteAddr(req);
        boolean result = IpUtil.interiorly(ip, expression);
        if (!result) {
            res.sendRedirect(errorLink);
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig config)
            throws ServletException {
        //是否使用IP过滤
        enabled = StringUtil.toBoolean(config.getInitParameter("enabled"));
        //错误连接页面
        errorLink = config.getInitParameter("errorLink");
        //过滤表达式
        expression = config.getInitParameter("expression");

    }

    @Override
    public void destroy() {

    }

}