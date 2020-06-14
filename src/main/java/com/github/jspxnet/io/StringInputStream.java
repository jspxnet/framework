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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-10-10
 * Time: 14:04:44
 */
public class StringInputStream extends java.io.InputStream {
    protected InputStream inputStream;

    public StringInputStream(String string) {
        this(string, Environment.defaultEncode);
    }

    public StringInputStream(String string, String encode) {
        if (encode == null) {
            encode = Environment.defaultEncode;
        }
        try {
            inputStream = new ByteArrayInputStream(string.getBytes(encode));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int read() throws java.io.IOException {

        return inputStream.read();
    }


    @Override
    public int read(byte[] bytes) throws java.io.IOException {

        return inputStream.read(bytes);
    }

    @Override
    public int read(byte[] bytes, int i, int i1) throws java.io.IOException {

        return inputStream.read(bytes, i, i1);
    }

    @Override
    public long skip(long l) throws java.io.IOException {

        return inputStream.skip(l);
    }

    @Override
    public int available() throws java.io.IOException {

        return inputStream.available();
    }

    @Override
    public void close() throws java.io.IOException {

        inputStream.close();
    }

    @Override
    public void mark(int i) {
        inputStream.mark(i);
    }

    @Override
    public void reset() throws java.io.IOException {

        inputStream.reset();
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

    @Override
    public String toString() {
        return inputStream.toString();
    }
}