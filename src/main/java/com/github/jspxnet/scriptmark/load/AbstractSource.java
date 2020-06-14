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

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.io.UnicodeReader;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 13:32:00
 */
public abstract class AbstractSource extends Source implements java.io.Serializable {

    static final String UNICODE_START1 = "utf";
    static final String UNICODE_START2 = "unicode";

    private final String name;
    protected final String encoding;

    public AbstractSource(String name, String encoding) {
        this.name = name;
        this.encoding = encoding;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public Reader getReader() throws IOException {
        if (encoding != null) {
            if (encoding.toLowerCase().startsWith(UNICODE_START1) || encoding.toLowerCase().startsWith(UNICODE_START2)) {
                return new UnicodeReader(getInputStream(), encoding);
            } else {
                return new InputStreamReader(getInputStream(), encoding);
            }
        } else {
            return new InputStreamReader(getInputStream(), Environment.defaultEncode);
        }
    }

    protected abstract InputStream getInputStream() throws IOException;

}