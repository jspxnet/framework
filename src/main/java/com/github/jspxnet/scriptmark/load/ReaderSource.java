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
import java.io.FileNotFoundException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 13:34:55
 */
public class ReaderSource extends Source {

    private final Reader reader;

    private final long lastModified;

    private final String name;

    private final String encoding;

    public ReaderSource(Reader reader, long lastModified, String name, String encoding) {
        this.reader = reader;
        this.lastModified = lastModified;
        this.name = name;
        this.encoding = encoding;
    }

    @Override
    public Reader getReader() throws FileNotFoundException {
        return reader;
    }

    @Override
    public long getLastModified() {
        return lastModified;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

}