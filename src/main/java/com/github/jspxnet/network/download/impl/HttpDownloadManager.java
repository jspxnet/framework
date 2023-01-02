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

import java.util.Date;
import java.util.Map;
import java.util.Hashtable;

import com.github.jspxnet.network.download.HttpDownloadWork;
import com.github.jspxnet.network.download.HttpDownloadThread;
import com.github.jspxnet.boot.sign.DownStateType;
import com.github.jspxnet.utils.DateUtil;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-8-5
 * Time: 0:16:26
 * com.github.jspxnet.network.download.impl.HttpDownloadManager
 */
public class HttpDownloadManager implements HttpDownloadWork {
    final private static HttpDownloadWork INSTANCE = new HttpDownloadManager();

    final private static Map<String, HttpDownloadThread> DOWN_MAP = new Hashtable<>();

    static public HttpDownloadWork getInstance() {
        return INSTANCE;
    }

    @Override
    public void put(String keyId, HttpDownloadThread td) {
        DOWN_MAP.put(keyId, td);
    }

    @Override
    public HttpDownloadThread get(String keyId) {
        return DOWN_MAP.get(keyId);
    }

    @Override
    public Map<String, HttpDownloadThread> getGroup() {
        return DOWN_MAP;
    }

    @Override
    public HttpDownloadThread remove(String keyId) {
        return DOWN_MAP.remove(keyId);
    }

    @Override
    public boolean containsKey(String key) {
        return DOWN_MAP.containsKey(key);
    }

    /**
     * 设置代理服务器
     *
     * @param proxy     String
     * @param proxyPort String
     */
    @Override
    public void setProxyServer(String proxy, String proxyPort) {
        //设置代理服务器
        System.getProperties().put("proxySet", "true");
        System.getProperties().put("proxyHost", proxy);
        System.getProperties().put("proxyPort", proxyPort);

    }


    /**
     * @param hour      按照小时判断
     * @param namespace 命名空间
     */
    @Override
    public void clearFailAndFinish(long hour, String namespace) {
        synchronized (this) {
            for (HttpDownloadThread httpDownload : DOWN_MAP.values()) {
                if (!httpDownload.getNamespace().equalsIgnoreCase(namespace)) {
                    continue;
                }
                if (DownStateType.ERROR == httpDownload.getStateType() || DownStateType.FINISH == httpDownload.getStateType()
                        || DownStateType.DOWNLOADING != httpDownload.getStateType()) {
                    if (DateUtil.compareHour(new Date(), httpDownload.getCreateDate()) > hour) {
                        DOWN_MAP.remove(httpDownload.getDownStateId());
                    }
                }
            }
        }
    }

    @Override
    public void closeAll() {
        synchronized (DOWN_MAP) {
            for (HttpDownloadThread httpDownload : DOWN_MAP.values()) {
                httpDownload.setQuit(true);
                DOWN_MAP.remove(httpDownload.getDownStateId());
            }
        }
    }
}