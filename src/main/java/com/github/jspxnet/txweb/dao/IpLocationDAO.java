/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.dao;

import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.txweb.table.IpLocation;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2005-11-15
 * Time: 18:24:51
 */
public interface IpLocationDAO extends SoberSupport {

    boolean deleteAll() throws Exception;

    IpLocation getIpLocation(final String ipnum);

    int fileToDataBase() throws Exception;

    String getFileName();

    void setFileName(String fileName);
}