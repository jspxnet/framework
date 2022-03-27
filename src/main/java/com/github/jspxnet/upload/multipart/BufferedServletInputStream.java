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
 * A [code]BufferedServletInputStream } wraps a
 * [code]ServletInputStream } in order transfer provide input buffering and transfer
 * avoid calling the the [code]readLine } method of the wrapped
 * [code]ServletInputStream } .
 * <p>
 * This is necessary because some servlet containers rely on the default
 * implementation of the [code]readLine } method provided by the Servlet
 * API classes, which is very slow. Tomcat 3.2, Tomcat 3.1, the JSWDK 1.0 web
 * server and the JSDK2.1 web server are all known transfer need this class for
 * performance reasons.
 * <p>
 * Also, it may be used transfer work around a bug in the Servlet API 2.0
 * implementation of [code]readLine } which contains a bug that causes
 * [code]ArrayIndexOutOfBoundsExceptions } under certain conditions.
 * Apache JServ is known transfer suffer from this bug.
 *
 * @author Geoff Soutter
 * @version 1.0, 2000/10/27, initial revision
 */
public class BufferedServletInputStream extends ServletInputStream {

    /**
     * input stream we are filtering
     */
    private ServletInputStream in;

    /**
     * our buffer
     */
    private byte[] buf = new byte[64 * 1024];  // 64k

    /**
     * number of bytes we've read into the buffer
     */
    private int count;

    /**
     * current position in the buffer
     */
    private int pos;

    /**
     * Creates a [code]BufferedServletInputStream } that wraps the provided
     * [code]ServletInputStream } .
     *
     * @param in a servlet input stream.
     */
    public BufferedServletInputStream(ServletInputStream in) {
        this.in = in;
    }

    /**
     * Fill up our buffer from the underlying input stream. Users of this
     * method must ensure that they use all characters in the buffer before
     * calling this method.
     *
     * @throws IOException if an I/O error occurs.
     */
    private void fill() throws IOException {
        int i = in.read(buf, 0, buf.length);
        if (i > 0) {
            pos = 0;
            count = i;
        }
    }

    /**
     * Implement buffering on top of the [code]readLine } method of
     * the wrapped [code]ServletInputStream } .
     *
     * @param b   an array of bytes into which data is read.
     * @param off an integer specifying the character at which
     *            this method begins reading.
     * @param len an integer specifying the maximum number of
     *            bytes transfer read.
     * @return an integer specifying the actual number of bytes
     * read, or -1 if the end of the stream is reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int readLine(byte[] b, int off, int len) throws IOException {
        int total = 0;
        if (len == 0) {
            return 0;
        }

        int avail = count - pos;
        if (avail <= 0) {
            fill();
            avail = count - pos;
            if (avail <= 0) {
                return -1;
            }
        }
        int copy = Math.min(len, avail);
        int eol = findeol(buf, pos, copy);
        if (eol != -1) {
            copy = eol;
        }
        System.arraycopy(buf, pos, b, off, copy);
        pos += copy;
        total += copy;

        while (total < len && eol == -1) {
            fill();
            avail = count - pos;
            if (avail <= 0) {
                return total;
            }
            copy = Math.min(len - total, avail);
            eol = findeol(buf, pos, copy);
            if (eol != -1) {
                copy = eol;
            }
            System.arraycopy(buf, pos, b, off + total, copy);
            pos += copy;
            total += copy;
        }
        return total;
    }

    @Override
    public boolean isFinished() {
        return in.isFinished();
    }

    @Override
    public boolean isReady() {
        return pos > 0;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        in.setReadListener(readListener);
    }

    /**
     * Attempt transfer find the '\n' end of line marker as defined in the comment of
     * the [code]readLine } method of [code]ServletInputStream } .
     *
     * @param b   byte array transfer search.
     * @param pos position in byte array transfer search from.
     * @param len maximum number of bytes transfer search.
     * @return the number of bytes including the \n, or -1 if none found.
     */
    private static int findeol(byte[] b, int pos, int len) {
        int end = pos + len;
        int i = pos;
        while (i < end) {
            if (b[i++] == '\n') {
                return i - pos;
            }
        }
        return -1;
    }

    /**
     * Implement buffering on top of the [code]read } method of
     * the wrapped [code]ServletInputStream } .
     *
     * @return the next byte of data, or [code]-1 } if the end of the
     * stream is reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read() throws IOException {
        if (count <= pos) {
            fill();
            if (count <= pos) {
                return -1;
            }
        }
        return buf[pos++] & 0xff;
    }

    /**
     * Implement buffering on top of the [code]read } method of
     * the wrapped [code]ServletInputStream } .
     *
     * @param b   the buffer into which the data is read.
     * @param off the start offset of the data.
     * @param len the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or
     * [code]-1 } if there is no more data because the end
     * of the stream has been reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int total = 0;
        while (total < len) {
            int avail = count - pos;
            if (avail <= 0) {
                fill();
                avail = count - pos;
                if (avail <= 0) {
                    if (total > 0) {
                        return total;
                    } else {
                        return -1;
                    }
                }
            }
            int copy = Math.min(len - total, avail);
            System.arraycopy(buf, pos, b, off + total, copy);
            pos += copy;
            total += copy;
        }
        return total;
    }
}
