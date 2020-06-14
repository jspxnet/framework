/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.network.download.impl;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-8-4
 * Time: 21:15:19
 */

import java.io.*;


public class FileAccess {
    private RandomAccessFile oSavedFile;
    private boolean close = false;

    FileAccess() throws IOException {

    }


    public FileAccess(File file, long nPos) throws IOException {
        oSavedFile = new RandomAccessFile(file, "rw");
        oSavedFile.seek(nPos);
    }


    public synchronized int write(byte[] b, int nStart, int nLen) {
        if (close) {
            return -1;
        }
        int n = -1;
        try {
            oSavedFile.write(b, nStart, nLen);
            n = nLen;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return n;
    }


    public void close() {
        close = true;
        if (oSavedFile != null) {
            try {
                oSavedFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}