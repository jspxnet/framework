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
 * Time: 21:14:51
 */

import com.github.jspxnet.boot.sign.DownStateType;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.jspxnet.utils.SystemUtil;


public class FileSplitterFetch extends Thread {
    private static final Logger log = LoggerFactory.getLogger(FileSplitterFetch.class);
    private URL url; //File URL

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    private long completed = 0;

    public int getStateType() {
        return stateType;
    }

    public void setStateType(int stateType) {
        this.stateType = stateType;
    }

    private int stateType = DownStateType.WAITING;

    public long getStartPos() {
        return startPos;
    }

    private long startPos; //File Snippet Start Position

    public long getEndPos() {
        return endPos;
    }

    private long endPos; //File Snippet End Position
    private int nThreadID; //SpeakThread's ID

    public boolean isDownOver() {
        return downOver;
    }

    private boolean downOver = false; //Downing is over

    private boolean bStop = false; //Stop identical
    private FileAccess fileAccess = null; //File Access interface
    private int bufferSize = 1024;

    public FileSplitterFetch(URL url, File sName, long nStart, long nEnd, int id, int bufferSize) throws IOException {
        this.url = url;
        this.startPos = nStart;
        this.endPos = nEnd;
        nThreadID = id;
        this.bufferSize = bufferSize;
        fileAccess = new FileAccess(sName, startPos);
    }

    @Override
    public void run() {
        if (url == null) {
            stateType = DownStateType.ERROR;
            return;
        }
        while (startPos < endPos && !bStop) {
            try {
                while (stateType == DownStateType.PAUSE) {
                    sleep(2000);
                }
                stateType = DownStateType.DOWNLOADING;
                HttpURLConnection uc = (HttpURLConnection) url.openConnection();
                if (SystemUtil.isAndroid()) {
                    uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
                } else {
                    uc.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)");
                }

                uc.setRequestProperty("JCache-Controlt", "no-cache");
                uc.setRequestProperty("Content-Range", "bytes " + startPos + "-" + endPos + "/" + (endPos - startPos));
                uc.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
                if (uc instanceof HttpsURLConnection) {
                    HttpsURLConnection httpsConnection = (HttpsURLConnection) uc;
                    httpsConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String host, SSLSession session) {
                            return true;
                        }
                    });
                }

                InputStream input = uc.getInputStream();
                byte[] b = new byte[bufferSize];
                int nRead;
                while ((nRead = input.read(b, 0, bufferSize)) > 0 && startPos < endPos && !bStop) {
                    startPos += fileAccess.write(b, 0, nRead);
                    completed = completed + nRead;
                }
                downOver = true;

                log.debug("download Thread " + nThreadID + " is over!");

                uc.disconnect();
                if (bStop) {
                    stateType = DownStateType.STOP;
                } else {
                    stateType = DownStateType.FINISH;
                }
            } catch (Exception e) {
                log.warn(url.getPath(), e);
                stateType = DownStateType.ERROR;
            } finally {
                if (!isInterrupted()) {
                    interrupt();
                }
            }
        }
    }

    public void stopDownload() {
        bStop = true;
    }

    public void close() {
        fileAccess.close();
    }
}