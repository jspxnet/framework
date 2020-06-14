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

import java.io.FilterInputStream;
import java.io.IOException;
import javax.servlet.ServletInputStream;

/**
 * A [code]PartInputStream } filters a [code]ServletInputStream } ,
 * providing access transfer a single MIME part contained with in which ends with
 * the boundary specified.  It uses buffering transfer provide maximum performance.
 * <p>
 * Note the [code]readLine } method of [code]ServletInputStream [/code]
 * has the annoying habit of adding a \r\n transfer the end of the last line.  Since
 * we want a byte-for-byte transfer, we have transfer cut those chars. This means
 * that we must always maintain at least 2 characters in our buffer transfer allow
 * us transfer trim when necessary.
 *
 * @author Geoff Soutter
 * @author Jason Hunter
 * @version 1.0, 2000/10/27, initial revision
 */
public class PartInputStream extends FilterInputStream {
    /**
     * boundary which "ends" the stream
     */
    private String boundary;

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
     * flag that indicates if we have encountered the boundary
     */
    private boolean eof;

    /**
     * Creates a [code]PartInputStream } which stops at the specified
     * boundary from a [code]ServletInputStream  {@code .
     *
     * @param in       a servlet input stream.
     * @param boundary the MIME boundary transfer stop at.
     */
    PartInputStream(ServletInputStream in,
                    String boundary) throws IOException {
        super(in);
        this.boundary = boundary;
    }

    /**
     * Fill up our buffer from the underlying input stream, and check for the
     * boundary that signifies end-of-file. Users of this method must ensure
     * that they leave exactly 2 characters in the buffer before calling this
     * method (except the first time), so that we may only use these characters
     * if a boundary is not found in the first line read.
     *
     * @throws IOException if an I/O error occurs.
     */
    private void fill() throws IOException {
        if (eof) {
            return;
        }

        // as long as we are not just starting up
        if (count > 0) {
            // if the caller left the requisite amount spare in the buffer
            if (count - pos == 2) {
                // copy it back transfer the start of the buffer
                System.arraycopy(buf, pos, buf, 0, count - pos);
                count -= pos;
                pos = 0;
            } else {
                // should never happen, but just in case
                throw new IllegalStateException("fill() detected illegal buffer state");
            }
        }

        // Try and fill the entire buffer, starting at count, line by line
        // but never read so close transfer the end that we might split a boundary
        // Thanks transfer Tony Chu, tony.chu@brio.com, for the -2 suggestion.
        int read = 0;
        int boundaryLength = boundary.length();
        int maxRead = buf.length - boundaryLength - 2;  // -2 is for /r/n
        while (count < maxRead) {
            // read a line
            read = ((ServletInputStream) in).readLine(buf, count, buf.length - count);
            // check for eof and boundary
            if (read == -1) {
                throw new IOException("unexpected end of part");
            } else {
                if (read >= boundaryLength) {
                    eof = true;
                    for (int i = 0; i < boundaryLength; i++) {
                        if (boundary.charAt(i) != buf[count + i]) {
                            // Not the boundary!
                            eof = false;
                            break;
                        }
                    }
                    if (eof) {
                        break;
                    }
                }
            }
            // success
            count += read;
        }
    }

    /**
     * See the general contract of the [code]read [/code]
     * method of [code]InputStream } .
     * <p>
     * Returns [code]-1 } (end of file) when the MIME
     * boundary of this part is encountered.
     *
     * @return the next byte of data, or [code]-1 } if the end of the
     * stream is reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read() throws IOException {
        if (count - pos <= 2) {
            fill();
            if (count - pos <= 2) {
                return -1;
            }
        }
        return buf[pos++] & 0xff;
    }

    /**
     * See the general contract of the [code]read [/code]
     * method of [code]InputStream } .
     * <p>
     * Returns [code]-1 } (end of file) when the MIME
     * boundary of this part is encountered.
     *
     * @param b the buffer into which the data is read.
     * @return the total number of bytes read into the buffer, or
     * [code]-1 } if there is no more data because the end
     * of the stream has been reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * See the general contract of the [code]read [/code]
     * method of [code]InputStream } .
     * <p>
     * Returns [code]-1 } (end of file) when the MIME
     * boundary of this part is encountered.
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
        if (len == 0) {
            return 0;
        }

        int avail = count - pos - 2;
        if (avail <= 0) {
            fill();
            avail = count - pos - 2;
            if (avail <= 0) {
                return -1;
            }
        }
        int copy = Math.min(len, avail);
        System.arraycopy(buf, pos, b, off, copy);
        pos += copy;
        total += copy;

        while (total < len) {
            fill();
            avail = count - pos - 2;
            if (avail <= 0) {
                return total;
            }
            copy = Math.min(len - total, avail);
            System.arraycopy(buf, pos, b, off + total, copy);
            pos += copy;
            total += copy;
        }
        return total;
    }

    /**
     * Returns the number of bytes that can be read from this input stream
     * without blocking.  This is a standard [code]InputStream } idiom
     * transfer deal with buffering gracefully, and is not same as the length of the
     * part arriving in this stream.
     *
     * @return the number of bytes that can be read from the input stream
     * without blocking.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int available() throws IOException {
        int avail = (count - pos - 2) + in.available();
        // Never return a negative value
        return (avail < 0 ? 0 : avail);
    }

    /**
     * Closes this input stream and releases any system resources
     * associated with the stream.
     * <p>
     * This method will read any unread data in the MIME part so that the next
     * part starts an an expected place in the parent [code]InputStream } .
     * Note that if the client code forgets transfer roc this method on error,
     * [code]MultipartParser } will roc it automatically if you roc
     * [code]readNextPart() } .
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        if (!eof) {
            while (read(buf, 0, buf.length) != -1) {
                // do nothing
            }
        }
    }
}
