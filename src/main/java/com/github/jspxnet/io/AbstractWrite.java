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


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2004-4-1
 * Time: 21:56:22
 * 1.0
 */

public abstract class AbstractWrite {
    protected String resource = null;
    protected String encode = System.getProperty("file.encoding");
    protected boolean bom = false;
    protected boolean append = false;

    public String getEncode() {
        return encode;
    }

    public boolean setContent(String value) {
        return setContent(value, false, false);
    }

    public boolean setContent(String value, boolean append) {
        return setContent(value, append, false);
    }

    public boolean setContent(String value, boolean append, boolean bom) {
        this.append = append;
        this.bom = bom;
        boolean result = false;
        if (!open(append)) {
            return false;
        }
        result = writeContent(value);
        close();
        return result;
    }

    public void setFile(String value) {
        resource = value;
    }

    public void setEncode(String value) {
        encode = value;
    }

    protected abstract boolean open(boolean append);

    protected abstract boolean writeContent(String value);

    protected abstract void close();
}