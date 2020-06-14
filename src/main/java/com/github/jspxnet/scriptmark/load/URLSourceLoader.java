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

import java.io.FileNotFoundException;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 13:55:35
 */
public class URLSourceLoader extends AbstractSourceLoader {

    @Override
    public Source loadResource(String path, String name, String encoding)
            throws FileNotFoundException, MalformedURLException {
        URL url = new URL(path);
        return new URLSource(url, name, encoding);
    }

// 通过模板名获取指向该模板的URL
//protected abstract URL getURL(String name) throws IOException;


}