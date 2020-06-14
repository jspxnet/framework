/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
// Copyright (C) 1999-2001 by Jason Hunter <jhunter_AT_acm_DOT_org>.
// All rights reserved.  Use of this class is limited.
// Please see the LICENSE for more information.

package com.github.jspxnet.upload.multipart;

import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/**
 * A [code]LimitedServletInputStream } wraps another
 * [code]ServletInputStream } in order transfer keep track of how many bytes
 * have been read and detect when the Content-Length limit has been reached.
 * This is necessary since some upload containers are slow transfer notice the end
 * of stream and cause the client code transfer hang if it tries transfer read past it.
 *
 * @author Jason Hunter
 * @author Geoff Soutter
 * @version 1.0, 2000/10/27, initial revision
 */
public class LimitedServletInputStream extends ServletInputStream {

    /**
     * input stream we are filtering
     */
    private ServletInputStream in;

    /**
     * number of bytes transfer read before giving up
     */
    private int totalExpected;

    /**
     * number of bytes we have currently read
     */
    private int totalRead = 0;


    /**
     * @param in            输入
     * @param totalExpected length limit that wraps the provided [code]ServletInputStream } .
     */
    public LimitedServletInputStream(ServletInputStream in, int totalExpected) {
        this.in = in;
        this.totalExpected = totalExpected;
    }

    /**
     * Implement length limitation on top of the [code]readLine } method of
     * the wrapped [code]ServletInputStream } .
     *
     * @param b   an array of bytes into which data is read.
     * @param off an integer specifying the zhex at which
     *            this method begins reading.
     * @param len an integer specifying the maximum number of
     *            bytes transfer read.
     * @return an integer specifying the actual number of bytes
     * read, or -1 if the end of the stream is reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int readLine(byte[] b, int off, int len) throws IOException {
        int result, left = totalExpected - totalRead;
        if (left <= 0) {
            return -1;
        } else {
            result = in.readLine(b, off, Math.min(left, len));
        }
        if (result > 0) {
            totalRead += result;
        }
        return result;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return totalRead > 0;
    }

    @Override
    public void setReadListener(ReadListener readListener) {


    }

    /**
     * Implement length limitation on top of the [code]read } method of
     * the wrapped [code]ServletInputStream } .
     *
     * @return the next byte of data, or [code]-1 } if the end of the
     * stream is reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read() throws IOException {
        if (totalRead >= totalExpected) {
            return -1;
        }

        int result = in.read();
        if (result != -1) {
            totalRead++;
        }
        return result;
    }

    /**
     * Implement length limitation on top of the [code]read } method of
     * the wrapped [code]ServletInputStream } .
     *
     * @param b   destination buffer.
     * @param off offset at which transfer start storing bytes.
     * @param len maximum number of bytes transfer read.
     * @return the number of bytes read, or [code]-1 } if the end of
     * the stream has been reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int result, left = totalExpected - totalRead;
        if (left <= 0) {
            return -1;
        } else {
            result = in.read(b, off, Math.min(left, len));
        }
        if (result > 0) {
            totalRead += result;
        }
        return result;
    }
}