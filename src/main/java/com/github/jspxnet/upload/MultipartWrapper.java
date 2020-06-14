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

import java.io.*;
import java.util.*;
import javax.servlet.http.*;

public class MultipartWrapper extends HttpServletRequestWrapper {

    MultipartRequest mreq = null;

    public MultipartWrapper(HttpServletRequest req, String dir)
            throws IOException {
        super(req);
        mreq = new MultipartRequest(req, dir);
    }

    @Override
    public Enumeration getParameterNames() {
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
        Map<String, String[]> map = new HashMap<String, String[]>();
        Enumeration enums = getParameterNames();
        while (enums.hasMoreElements()) {
            String name = (String) enums.nextElement();
            map.put(name, mreq.getParameterValues(name));
        }
        return map;
    }
}