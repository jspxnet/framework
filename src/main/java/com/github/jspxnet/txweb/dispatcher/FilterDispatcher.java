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
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.URLUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-12-26
 * Time: 17:42:15
 * tomcat get 方法需要设置编码修复
 * 这个东西会占用 request 读一次，不能正常使用ajax 请求
 */
public class FilterDispatcher implements Filter {

    /**
     * @param servletConfig 配置
     */
    @Override
    public void init(FilterConfig servletConfig)  {
        ServletContext servletContext = servletConfig.getServletContext();
        Dispatcher.init(servletContext);
    }

    /**
     * @param servletRequest  请求
     * @param servletResponse 应答
     * @param filterChain     过滤
     * @throws ServletException 异常
     * @throws IOException      异常
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String urlName = URLUtil.getFileName(request.getRequestURI());

        //确保系统跳转到正确到后缀名begin
        if (!StringUtil.toBoolean(request.getParameter("t")) && (StringUtil.isNull(urlName) || "".equals(urlName) || "/".equals(urlName))) {
            response.sendRedirect(request.getRequestURI() + "index." + Dispatcher.getFilterSuffix() + "?t=true");
            return;
        }
        //确保系统跳转到正确到后缀名end

        String suffix = URLUtil.getFileType(request.getRequestURI());
        if (suffix != null) {
            suffix = suffix.toLowerCase();
        }

        if (ArrayUtil.inArray(new String[]{Dispatcher.getTemplateSuffix(),Dispatcher.getFilterSuffix(),Dispatcher.getMarkdownSuffix()},suffix,true)) {
            Dispatcher.getInstance().wrapRequest(request, response);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    /**
     * 卸载数据
     */
    @Override
    public void destroy() {
        JspxNetApplication.destroy();
    }
}