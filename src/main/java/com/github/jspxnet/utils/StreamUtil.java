/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.utils;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.io.StringOutputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-9-2
 * Time: 17:05:33
 * 流处理单元
 */

public class StreamUtil {
    private StreamUtil() {

    }


    /**
     * @param in     输入流
     * @param out    输出流
     * @param buffer 缓冲
     * @return 是否成功
     * @throws IOException 异常
     */
    public static boolean copy(InputStream in, OutputStream out, int buffer) throws IOException {
        return copy(in, out, buffer, null);
    }

    /**
     * @param in     输入流
     * @param out    输出流
     * @param buffer 缓冲
     * @param event  事件
     * @return 是否成功
     * @throws IOException 异常
     */
    public static boolean copy(InputStream in, OutputStream out, int buffer, StreamEvent event) throws IOException {
        if (in == null || out == null) {
            return false;
        }
        if (buffer < 1) {
            buffer = 1024;
        }
        byte[] data = new byte[buffer];
        long fullSize = 0;
        int r;
        try {
            while ((r = in.read(data)) >= 0) {
                out.write(data, 0, r);
                fullSize = fullSize + r;
                if (event != null) {
                    event.setSize(fullSize);
                }
            }
            out.flush();
            if (event != null) {
                event.setFullSize(fullSize);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            in.close();
            out.close();
        }
        return true;
    }


    /**
     * @param in       输入流
     * @param startPos 开始位置
     * @param buffer   缓冲
     * @param event    事件
     * @return 是否成功
     * @throws IOException 异常
     */
    public static byte[] copy(InputStream in, int startPos, int buffer, StreamEvent event) throws IOException {
        if (in == null) {
            return new byte[0];
        }
        if (buffer < 1) {
            buffer = 1024;
        }
        byte[] data = new byte[buffer];
        long fullSize = 0;

        try {
            in.skip(startPos);
            if (in.read(data) >= 0) {
                return data;
            }
            if (event != null) {
                event.setFullSize(fullSize);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
        return data;
    }

    /**
     *
     * @param in 读取流
     * @return 读取字符串
     * @throws IOException 异常
     */
    public static String readStreamText(InputStream in) throws IOException
    {
        return readStreamText(in, Environment.defaultEncode);
    }

    public static String readStreamText(InputStream in,String encode) throws IOException {
        StringOutputStream out = new StringOutputStream();
        copy(in,out,1024);
        return out.toString(encode);
    }
}