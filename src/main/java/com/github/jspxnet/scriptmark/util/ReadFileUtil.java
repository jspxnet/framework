/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.util;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 13:25:01
 */
public final class ReadFileUtil {
    final public static int BUFFER_SIZE = 2048;

    private ReadFileUtil() {

    }


    public static String readToString(File file) throws IOException {
        return readToString(new FileReader(file));
    }

    public static char[] readToChars(File file) throws IOException {
        return readToChars(new FileReader(file));
    }

    public static byte[] readToBytes(File file) throws IOException {
        return readToBytes(new FileReader(file));
    }

    public static String readToString(InputStream inputStream, String encoding) throws IOException {
        return readToString(new InputStreamReader(inputStream, encoding));
    }

    public static char[] readToChars(InputStream inputStream) throws IOException {
        return readToChars(new InputStreamReader(inputStream));
    }

    public static char[] readToChars(InputStream inputStream, String encoding) throws IOException {
        return readToChars(new InputStreamReader(inputStream, encoding));
    }

    public static byte[] readToBytes(InputStream inputStream) throws IOException {
        return readToBytes(new InputStreamReader(inputStream));
    }

    public static byte[] readToBytes(InputStream inputStream, String encoding) throws IOException {
        return readToBytes(new InputStreamReader(inputStream, encoding));
    }

    public static String readToString(Reader reader) throws IOException {
        StringBuilder buffer = new StringBuilder();
        char[] buf = new char[BUFFER_SIZE];
        int len;
        while ((len = reader.read(buf)) != -1) {
            buffer.append(buf, 0, len);
        }
        reader.close();
        return buffer.toString();
    }

    public static char[] readToChars(Reader reader) throws IOException {
        StringBuilder buffer = new StringBuilder();
        char[] buf = new char[BUFFER_SIZE];
        int len;
        while ((len = reader.read(buf)) != -1) {
            buffer.append(buf, 0, len);
        }
        int length = buffer.length();
        char[] dst = new char[length];
        buffer.getChars(0, length, dst, 0);
        reader.close();
        return dst;
    }

    public static byte[] readToBytes(Reader reader) throws IOException {
        StringBuilder buffer = new StringBuilder();
        char[] buf = new char[BUFFER_SIZE];
        int len;
        while ((len = reader.read(buf)) != -1) {
            buffer.append(buf, 0, len);
        }
        reader.close();
        return buffer.toString().getBytes();
    }
}