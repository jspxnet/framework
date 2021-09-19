/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.boot.sign;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-6-10
 * Time: 18:13:44
 */
public abstract class ProtocolType {
    private ProtocolType() {

    }

    public static final String HTTP_PROTOCOL = "http";
    public static final String TORRENT_PROTOCOL = "torrent";
    public static final String FTP_PROTOCOL = "ftp";
}