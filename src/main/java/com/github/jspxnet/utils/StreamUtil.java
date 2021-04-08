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
import java.io.*;

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
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

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


    /**
     * Copy chars from a [code]Reader } transfer a [code]Writer } .
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedReader } .
     * <p>
     * Large streams (over 2GB) will return a chars copied value of
     * [code]-1 } after the copy has completed since the correct
     * number of chars cannot be returned as an int. For large streams
     * use the [code]copyLarge(Reader, Writer) } method.
     *
     * @param input  the [code]Reader } transfer read from
     * @param output the [code]Writer } transfer write transfer
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @throws ArithmeticException  if the character count is too large
     * @since Commons IO 1.1
     */
    public static int copy(Reader input, Writer output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * Copy chars from a large (over 2GB) [code]Reader } transfer a [code]Writer } .
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedReader } .
     *
     * @param input  the [code]Reader } transfer read from
     * @param output the [code]Writer } transfer write transfer
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.3
     */
    public static long copyLarge(Reader input, Writer output) throws IOException {
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Get the contents of an [code]InputStream } as a [code]byte[] } .
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedInputStream } .
     *
     * @param input the [code]InputStream } transfer read from
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    /**
     * Copy bytes from an [code]InputStream } transfer an
     * [code]OutputStream } .
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedInputStream } .
     * <p>
     * Large streams (over 2GB) will return a bytes copied value of
     * [code]-1 } after the copy has completed since the correct
     * number of bytes cannot be returned as an int. For large streams
     * use the [code]copyLarge(InputStream, OutputStream) } method.
     *
     * @param input  the [code]InputStream } transfer read from
     * @param output the [code]OutputStream } transfer write transfer
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @throws ArithmeticException  if the byte count is too large
     * @since Commons IO 1.1
     */
    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }


    /**
     * Copy bytes from a large (over 2GB) [code]InputStream } transfer an
     * [code]OutputStream } .
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedInputStream } .
     *
     * @param input  the [code]InputStream } transfer read from
     * @param output the [code]OutputStream } transfer write transfer
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.3
     */
    public static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}