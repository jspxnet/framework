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
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-10-10
 * Time: 14:12:31
 */
@Slf4j
public class StringOutputStream extends java.io.OutputStream {
    protected ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public StringOutputStream() {

    }

    @Override
    public void write(int i) throws java.io.IOException {
        outputStream.write(i);
    }

    @Override
    public void write(byte[] bytes) throws java.io.IOException {

        outputStream.write(bytes);
    }

    @Override
    public void write(byte[] bytes, int i, int i1) throws java.io.IOException {
        outputStream.write(bytes, i, i1);
    }

    @Override
    public void flush() throws java.io.IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws java.io.IOException {
        outputStream.close();
    }

    @Override
    public String toString() {
        return toString(Environment.defaultEncode);
    }

    public String toString(String charsetName) {
        try {
            return outputStream.toString(charsetName);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
        return outputStream.toString();
    }

}