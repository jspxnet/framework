/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.datasource;

import java.sql.Connection;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2007-3-23
 * Time: 17:02:55
 */
public interface ConnectionProxy extends Connection, Serializable {
    //采用那条sql 检查 链接
    void setCheckSQL(String testSql);

    //最大链接保留时间
    void setMaxConnectionTime(int maxConnectionTime);

    //boolean isConnected();
    //关闭销毁连接
    boolean release();

    //从新切换到链接状态
    boolean isConnect();

    //超时了
    boolean isOvertime();

    boolean open();

}