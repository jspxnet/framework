/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
// Copyright (C) 2001 by Jason Hunter <jhunter_AT_acm_DOT_org>.
// All rights reserved.  Use of this class is limited.
// Please see the LICENSE for more information.

package com.github.jspxnet.upload;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * A filter for easy semi-automatic handling of multipart/form-data requests
 * (file uploads).  The filter capability requires Servlet API 2.3.
 * <p>
 * See Jason Hunter's June 2001 article in JavaWorld for a full explanation of
 * the class usage.
 *
 * @author [b]Jason Hunter[/B], Copyright &#169; 2001
 * @version 1.0, 2001/06/19
 */
public class MultipartFilter implements Filter {

    private FilterConfig config = null;
    private String dir = null;

    @Override
    public void init(FilterConfig config) throws ServletException {
        this.config = config;

        // Determine the upload directory.  First look for an uploadDir filter
        // init parameter.  Then look for the context tempdir.
        dir = config.getInitParameter("uploadDir");
        if (dir == null) {
            File tempdir = (File) config.getServletContext()
                    .getAttribute("javax.servlet.context.tempdir");
            if (tempdir != null) {
                dir = tempdir.toString();
            } else {
                throw new ServletException(
                        "MultipartFilter: No upload directory found: set an uploadDir " +
                                "init parameter or ensure the javax.servlet.context.tempdir " +
                                "directory is valid");
            }
        }
    }

    @Override
    public void destroy() {
        config = null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String type = req.getHeader("Content-Type");

        // If this is not a multipart/form-data request continue
        if (type == null || !type.startsWith("multipart")) {
            chain.doFilter(request, response);
        } else {
            MultipartWrapper multi = new MultipartWrapper(req, dir);
            chain.doFilter(multi, response);
        }
    }
}
