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

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.JspxNetApplication;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.txweb.evasive.Configuration;
import com.github.jspxnet.txweb.evasive.EvasiveConfiguration;
import com.github.jspxnet.txweb.evasive.EvasiveManager;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.URLUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2007-12-20
 * Time: 12:40:51
 * tomcat get 方法需要设置编码修复
 */
@Slf4j
public class ServletDispatcher extends HttpServlet implements javax.servlet.Servlet {
    public ServletDispatcher() {

    }

    //转发次数
    private static EvasiveManager evasiveManager = null;

    //使用回避拦截功能
    static private boolean useEvasive;

    /**
     * 初始平台
     *
     * @param servletConfig 配置
     */
    @Override
    public void init(ServletConfig servletConfig) {
        ServletContext servletContext = servletConfig.getServletContext();
        Dispatcher.init(servletContext);
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        useEvasive = envTemplate.getBoolean(Environment.useEvasive);
        if (useEvasive) {
            Configuration evasiveConfiguration = EvasiveConfiguration.getInstance();
            evasiveConfiguration.setFileName(envTemplate.getString(Environment.evasive_config));
            evasiveManager = EvasiveManager.getInstance();
        }

    }

    /**
     * @param servletRequest  Request  请求
     * @param servletResponse Response 应答
     */
    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            request.setCharacterEncoding(Dispatcher.getEncode());
            response.setCharacterEncoding(Dispatcher.getEncode());
        } catch (UnsupportedEncodingException e) {
            TXWebUtil.errorPrint("系统编码错误", null, response, HttpStatusType.HTTP_status_403);
            log.debug("系统编码错误", e);
            return;
        }
        if (useEvasive && JspxNetApplication.checkRun() && evasiveManager.execute(request, response)) {
            return;
        }
        String suffix = URLUtil.getFileType(request.getRequestURI());
        if (suffix != null) {
            suffix = suffix.toLowerCase();
        }
        if (Dispatcher.hasSuffix(suffix)) {
            Dispatcher.getInstance().wrapRequest(request, response);
        } else {
            try {
                super.service(servletRequest, servletResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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