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

import java.io.Reader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 13:43:23
 */
public class StringSource extends Source implements java.io.Serializable {

    private final String source;

    private final long lastModified;

    private static final String STRING_ENCODING = StandardCharsets.UTF_8.name();

    public StringSource(String source) {
        this.source = source;
        this.lastModified = System.currentTimeMillis();
    }

    @Override
    public String getEncoding() {
        return STRING_ENCODING;
    }

    @Override
    public long getLastModified() {
        return lastModified;
    }

    @Override
    public Reader getReader() throws IOException {
        return new StringReader(source);
    }

}