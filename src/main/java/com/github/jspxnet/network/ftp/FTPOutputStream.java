/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.network.ftp;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-9-26
 * Time: 9:55:49
 */
public class FTPOutputStream extends OutputStream {
    private final OutputStream out;
    private final IFTPClient ftpClient;

    public FTPOutputStream(OutputStream out, IFTPClient ftpClient) {

        this.out = out;
        this.ftpClient = ftpClient;

    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);

    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    /**
     * Closes this output stream and releases any system com.github.jspxnet.xhtmlrenderer.resources
     * associated with this stream. The general contract of [code]close [/code]
     * is that it closes the output stream. A closed stream cannot perform
     * output operations and cannot be reopened.
     * <p>
     * The [code]close } method of [code]OutputStream } does nothing.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        out.close();
        ftpClient.completePendingCommand();
    }
}