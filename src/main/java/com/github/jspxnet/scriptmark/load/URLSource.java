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

import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 13:31:14
 */
public class URLSource extends AbstractSource {

    private final URL url;

    public URLSource(URL url, String name, String encoding) throws MalformedURLException {
        super(name, encoding);
        this.url = url;
    }

    @Override
    public long getLastModified() {
        try {
            return url.openConnection().getLastModified();
        } catch (IOException e) {
            return UNKOWN_MODIFIED;
        }
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(14000);
        conn.setReadTimeout(14000);
        return conn.getInputStream();
    }
    @Override
    public boolean isFile()
    {
        return StringUtil.isHttp(url.getPath());
    }
}