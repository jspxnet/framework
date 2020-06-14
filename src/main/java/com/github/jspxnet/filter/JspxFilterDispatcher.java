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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class JspxFilterDispatcher implements Filter {
    private static final Logger log = LoggerFactory.getLogger(JspxFilterDispatcher.class);
    private Map<String, Filter> filterMap = new Hashtable<String, Filter>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Enumeration enumeration = filterConfig.getInitParameterNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                String className = (String) enumeration.nextElement();
                String param = filterConfig.getInitParameter(className);
                try {
                    Filter childFilter = (Filter) ClassUtil.newInstance(className);
                    childFilter.init(filterConfig);
                    String[] fileNamType = StringUtil.split(param, "/");
                    for (String type : fileNamType) {
                        filterMap.put(type.toLowerCase(), childFilter);
                    }
                } catch (Exception e) {
                    log.error(className, e);
                    e.printStackTrace();
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
        Filter childFilter = filterMap.get(fileType);
        if (childFilter != null) {
            childFilter.doFilter(servletRequest, servletResponse, filterChain);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        for (Filter childFilter : filterMap.values()) {
            if (childFilter != null) {
                childFilter.destroy();
            }
        }
    }
}