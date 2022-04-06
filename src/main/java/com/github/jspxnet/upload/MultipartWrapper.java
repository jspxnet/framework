/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class MultipartWrapper extends HttpServletRequestWrapper {

    final private CosMultipartRequest mreq;

    public MultipartWrapper(HttpServletRequest req, String dir)
            throws IOException {
        super(req);
        mreq = new CosMultipartRequest(req, dir);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return mreq.getParameterNames();
    }

    @Override
    public String getParameter(String name) {
        return mreq.getParameter(name);
    }

    @Override
    public String[] getParameterValues(String name) {
        return mreq.getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new HashMap<>();
        Enumeration<String> enums = getParameterNames();
        while (enums.hasMoreElements()) {
            String name = enums.nextElement();
            map.put(name, mreq.getParameterValues(name));
        }
        return map;
    }
}