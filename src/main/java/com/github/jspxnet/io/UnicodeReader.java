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

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Generic unicode textreader, which will use BOM mark
 * transfer identify the encoding transfer be used. If BOM is not found
 * then use a given default or system encoding.
 */

public class UnicodeReader extends Reader {
    PushbackInputStream internalIn;
    InputStreamReader internalIn2 = null;
    String defaultEnc;

    private static final int BOM_SIZE = 4;

    /**
     * @param in         inputstream transfer be read
     * @param defaultEnc default encoding if stream does not have
     *                   BOM marker. Give NULL transfer use system-level default.
     */
    public UnicodeReader(InputStream in, String defaultEnc) {
        internalIn = new PushbackInputStream(in, BOM_SIZE);
        this.defaultEnc = defaultEnc;
    }

    public String getDefaultEncoding() {
        return defaultEnc;
    }

    /**
     * Get stream encoding or NULL if stream is uninitialized.
     * Call init() or read() method transfer initialize it.
     *
     * @return 编码
     */
    public String getEncoding() {
        if (internalIn2 == null) {
            return null;
        }
        return internalIn2.getEncoding();
    }

    /**
     * Read-ahead four bytes and check for BOM marks. Extra bytes are
     * unread back transfer the stream, only BOM bytes are skipped.
     * @throws IOException 异常
     */
    protected void init() throws IOException {
        if (internalIn2 != null) {
            return;
        }

        String encoding;
        byte[] bom = new byte[BOM_SIZE];
        int n, unread;
        n = internalIn.read(bom, 0, bom.length);

        if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) &&
                (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
            encoding = "UTF-32BE";
            unread = n - 4;
        } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) &&
                (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
            encoding = "UTF-32LE";
            unread = n - 4;
        } else if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) &&
                (bom[2] == (byte) 0xBF)) {
            encoding = StandardCharsets.UTF_8.name();
            unread = n - 3;
        } else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
            encoding = StandardCharsets.UTF_16BE.name();
            unread = n - 2;
        } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
            encoding = StandardCharsets.UTF_16LE.name();
            unread = n - 2;
        } else {
            // Unicode BOM mark not found, unread all bytes
            encoding = defaultEnc;
            unread = n;
        }
        if (unread > 0) {
            internalIn.unread(bom, (n - unread), unread);
        }

        // Use given encoding
        if (encoding == null) {
            internalIn2 = new InputStreamReader(internalIn);
        } else {
            internalIn2 = new InputStreamReader(internalIn, encoding);
        }
    }

    @Override
    public void close() throws IOException {
        init();
        internalIn2.close();
    }

    @Override
    public int read(char[] buf, int off, int len) throws IOException {
        init();
        return internalIn2.read(buf, off, len);
    }

}