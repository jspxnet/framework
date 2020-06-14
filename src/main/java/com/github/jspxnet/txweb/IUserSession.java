/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb;

import com.github.jspxnet.txweb.table.Role;
import java.util.Date;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-9-13
 * Time: 21:53:03
 */
public interface IUserSession extends Serializable {
    void setId(String sid);

    String getId();

    //用户id
    void setUid(long uid);

    long getUid();

    String getName();

    String getNickname();

    String getMail();

    String getPhone();

    String getKid();

    String getFaceImage();

    String getSex();

    //角色,参数为软件名称
    IRole getRole(String softName, String organizeId);

    void setRole(Role role);

    //登录时间
    Date getCreateDate();

    void setCreateDate(Date createDate);

    //最后请求时间
    long getLastRequestTime();

    void setLastRequestTime(long lastRequestTime);

    //是否为游客
    boolean isGuest();

    @Override
    String toString();

    String getIp();

    void setIp(String ip);
}