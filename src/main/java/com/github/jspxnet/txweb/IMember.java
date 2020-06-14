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

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.txweb.table.MemberRole;
import com.github.jspxnet.txweb.table.Role;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-7-27
 * Time: 14:47:52
 */
public interface IMember {
    long getId();

    String getName();

    void setName(String name);

    String getNickname();

    String getMail();

    String getPhone();

    String getKid();

    String getSpelling();

    String getPassword();

    String getFaceImage();

    String getSex();

    Date getBirthday();

    int getPrestige();

    int getFascination();

    int getCredit();

    String getMedals();

    int getExtcredits1();

    int getExtcredits2();

    int getPoints();

    int getActivity();

    double getStoreMoney();

    String getDiscountGroup();

    int getHideInfo();

    long getPid();

    int getLoginTimes();

    Date getLoginDate();

    int getCongealType();

    Date getCongealDate();

    Date getAutoCongealDate();

    Date getConfineDate();

    String getSkin();

    String getQq();

    String getCountry();

    String getProvince();

    String getCity();

    String getRemark();

    String getSignature();

    Map<String, IRole> getRoles();

    String getPutName();

    long getPutUid();

    String getIp();

    Date getCreateDate();

    Role getRole(String softName);

    void setMemberRoles(List<MemberRole> memberRoles);

    int getMaxUserType();

    JSONObject toJson();

    void setRole(Role role);

    boolean isFastPay(double amount,int points);

    String getProperty(String key);

}