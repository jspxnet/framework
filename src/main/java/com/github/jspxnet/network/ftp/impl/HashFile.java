/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.network.ftp.impl;

import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-9-24
 * Time: 21:04:26
 */
public class HashFile {

    private String name = StringUtil.empty;

    private long dateTime = 0;

    private String hash = StringUtil.empty;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}