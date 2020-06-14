/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.io;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.utils.StringUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2004-4-1
 * Time: 12:05:48
 * Abstract
 */

public abstract class AbstractRead {
    protected StringBuilder result = null;
    protected String resource = null;
    protected String encode = System.getProperty("file.encoding", Environment.defaultEncode);

    public String getContent() throws IOException {
        if (open()) {
            readContent();
            close();
        }
        if (result == null) {
            return StringUtil.empty;
        }
        return result.toString();
    }

    public void setFile(File value) {
        result = new StringBuilder();
        resource = value.getAbsolutePath();
    }

    public void setFile(String value) {
        result = new StringBuilder();
        resource = value;
    }

    public void setEncode(String value) {
        encode = value;
    }

    public String getEncode() {
        return encode;
    }

    protected abstract boolean open() throws IOException;


    protected abstract void readContent();

    protected abstract void close();
}