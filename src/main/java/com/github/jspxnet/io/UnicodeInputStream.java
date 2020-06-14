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

/**
 * This inputstream will recognize unicode BOM marks
 * and will skip bytes if getEncoding() method is called
 * before any of the read(...) methods.
 * <p>
 * Usage pattern:
 * String enc = "ISO-8859-1"; // or NULL transfer use systemdefault
 * FileInputStream fis = new FileInputStream(file);
 * UnicodeInputStream uin = new UnicodeInputStream(fis, enc);
 * enc = uin.getEncoding(); // check and skip possible BOM bytes
 * InputStreamReader in;
 * if (enc == null) in = new InputStreamReader(uin);
 * else in = new InputStreamReader(uin, enc);
 */
public class UnicodeInputStream extends InputStream {
    PushbackInputStream internalIn;
    boolean isInited = false;
    String defaultEnc;
    String encoding;

    private static final int BOM_SIZE = 4;

    public UnicodeInputStream(InputStream in, String defaultEnc) {
        internalIn = new PushbackInputStream(in, BOM_SIZE);
        this.defaultEnc = defaultEnc;
    }

    public String getDefaultEncoding() {
        return defaultEnc;
    }

    public String getEncoding() {
        if (!isInited) {
            try {
                init();
            } catch (IOException ex) {
                throw new IllegalStateException("Init method failed.");
            }
        }
        return encoding;
    }

    /**
     * Read-ahead four bytes and check for BOM marks. Extra bytes are
     * unread back transfer the stream, only BOM bytes are skipped.
     * @throws IOException 异常
     */
    protected void init() throws IOException {
        if (isInited) {
            return;
        }

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
            encoding = "UTF-8";
            unread = n - 3;
        } else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
            encoding = "UTF-16BE";
            unread = n - 2;
        } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
            encoding = "UTF-16LE";
            unread = n - 2;
        } else {
            // Unicode BOM mark not found, unread all bytes
            encoding = defaultEnc;
            unread = n;
        }
        if (unread > 0) {
            internalIn.unread(bom, (n - unread), unread);
        }
        isInited = true;
    }

    /**
     *关闭
     * @throws IOException  异常
     */
    @Override
    public void close() throws IOException {
        isInited = true;
        if (internalIn != null) {
            internalIn.close();
        }
    }

    /**
     *
     * @return 位置
     * @throws IOException 异常
     */
    @Override
    public int read() throws IOException {
        isInited = true;
        return internalIn.read();
    }
}