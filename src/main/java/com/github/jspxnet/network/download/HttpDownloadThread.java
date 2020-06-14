/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.network.download;

import com.github.jspxnet.network.TransmitListener;

import java.util.Date;
import java.net.URL;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-6-17
 * Time: 23:50:55
 */
public interface HttpDownloadThread {
    void registerListener(TransmitListener listener);

    Date getCreateDate();

    void setBufferSize(int size);

    int getStateType();

    void setURL(URL url);

    URL getURL();

    void setSaveFile(File file);

    File getSaveFile();

    long getCompleted();

    //void startDownload();
    void start();

    void setQuit(boolean flagQuit);

    boolean getQuit();

    String getPercent();

    int getSplitter();

    void setSplitter(int splitter);

    void setDownStateId(String downStateId);

    String getDownStateId();

    String get(String k);

    void put(String k, String v);

    String getNamespace();

    void setNamespace(String namespace);
}