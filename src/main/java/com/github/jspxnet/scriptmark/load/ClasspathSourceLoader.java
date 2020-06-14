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

import java.net.URL;
import java.io.FileNotFoundException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 13:58:22
 */
public class ClasspathSourceLoader extends URLSourceLoader {

    private final ClassLoader classLoader;

    public ClasspathSourceLoader() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    public ClasspathSourceLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    protected URL getURL(String name) throws FileNotFoundException {
        if (name.charAt(0) == '/' || name.charAt(0) == '\\') {
            name = name.substring(1);
        }
        return classLoader.getResource(name);
    }

}