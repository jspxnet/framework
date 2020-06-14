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
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-9-26
 * Time: 9:41:55
 */
public class FTPInputStream extends InputStream {
    private final InputStream is;
    private final IFTPClient ftpClient;

    public FTPInputStream(InputStream is, IFTPClient ftpClient) {

        this.is = is;
        this.ftpClient = ftpClient;

    }

    @Override
    public int read() throws IOException {
        return is.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (is != null) {
            return is.read(b);
        }
        return -1;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (is != null) {
            return is.read(b, off, len);
        }
        return -1;
    }

    @Override
    public long skip(long n) throws IOException {
        return is.skip(n);
    }

    @Override
    public void close() throws IOException {

        ftpClient.completePendingCommand();
        if (is != null) {
            is.close();
        }
    }

}