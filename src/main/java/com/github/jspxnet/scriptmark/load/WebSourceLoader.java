/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.load;

import javax.servlet.ServletContext;
import java.net.URL;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 13:57:07
 */
public class WebSourceLoader extends URLSourceLoader {

    private ServletContext servletContext;

    public WebSourceLoader() {

    }

    public WebSourceLoader(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    protected URL getURL(String name) throws IOException {
        if (name.charAt(0) != '/') {
            name = "/" + name;
        }
        return servletContext.getResource(name);
    }

}