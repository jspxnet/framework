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
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 13:25:01
 */
public final class ReadFileUtil {
    final private static int BUFFER_SIZE = 2048;

    private ReadFileUtil() {

    }

    /**
     * jdk 1.7 以上才支持，目前测试下来，性能io,nio基本差不多
     *
     * @param file 文件
     * @return 文件类容
     * @throws IOException 异常
     */
    public static String readToStringNio(File file) throws IOException {
        // 获取源文件和目标文件的输入输出流
        FileInputStream fin = new FileInputStream(file);
        ByteArrayOutputStream fout = new ByteArrayOutputStream();
        // 获取输入输出通道
        FileChannel fcin = fin.getChannel();
        // 创建缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        while (true) {
            // clear方法重设缓冲区，使它可以接受读入的数据
            buffer.clear();
            // 从输入通道中将数据读到缓冲区
            int r = fcin.read(buffer);
            // read方法返回读取的字节数，可能为零，如果该通道已到达流的末尾，则返回-1
            if (r == -1) {
                break;
            }
            // flip方法让缓冲区可以将新读入的数据写入另一个通道
            buffer.flip();
            // 从输出通道中将数据写入缓冲区
            fout.write(buffer.array());
        }
        fin.close();
        return fout.toString( "UTF-8");
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