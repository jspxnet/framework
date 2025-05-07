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

import lombok.extern.slf4j.Slf4j;
import com.github.jspxnet.utils.URLUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Hashtable;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-9-27
 * Time: 9:44:01
 * com.github.jspxnet.dispatcher.JspxSupportServlet
 */
@Slf4j
public class JspxFilterDispatcher implements Filter {
    final static private Map<String, Filter> FILTER_MAP = new Hashtable<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Enumeration<String> enumeration = filterConfig.getInitParameterNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                String className =  enumeration.nextElement();
                String param = filterConfig.getInitParameter(className);
                try {
                    Filter childFilter = (Filter) ClassUtil.newInstance(className);
                    childFilter.init(filterConfig);
                    String[] fileNamType = StringUtil.split(param, "/");
                    for (String type : fileNamType) {
                        FILTER_MAP.put(type.toLowerCase(), childFilter);
                    }
                } catch (Exception e) {
                    log.error(className, e);
                }
            }
        }
    }

    private String getFileType(ServletRequest servletRequest) {
        //////////////////////////////////////
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        return URLUtil.getFileType(httpRequest.getRequestURL().toString()).toLowerCase();
        ////////////////////////////////////
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        String fileType = getFileType(servletRequest);
        Filter childFilter = FILTER_MAP.get(fileType);
        if (childFilter != null) {
            childFilter.doFilter(servletRequest, servletResponse, filterChain);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        for (Filter childFilter : FILTER_MAP.values()) {
            if (childFilter != null) {
                childFilter.destroy();
            }
        }
    }
}