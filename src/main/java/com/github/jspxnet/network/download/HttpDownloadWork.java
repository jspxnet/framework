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

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-8-5
 * Time: 0:17:37
 */
public interface HttpDownloadWork {
    void setProxyServer(String proxy, String proxyPort);

    void put(String keyId, HttpDownloadThread td);

    HttpDownloadThread get(String keyId);

    Map<String, HttpDownloadThread> getGroup();

    HttpDownloadThread remove(String keyId);

    boolean containsKey(String key);

    void clearFailAndFinish(long hour, String namespace);

    void closeAll();
}