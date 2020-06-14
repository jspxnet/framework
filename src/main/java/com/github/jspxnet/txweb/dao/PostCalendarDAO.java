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

import com.github.jspxnet.txweb.table.PostCalendar;
import com.github.jspxnet.sober.SoberSupport;

import java.util.List;
import java.util.Date;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2007-10-6
 * Time: 15:08:34
 */
public interface PostCalendarDAO extends SoberSupport {
    String getOrganizeId();

    void setOrganizeId(String organizeId);

    void setNamespace(String namespace);

    List<PostCalendar> getBetweenList(Date beginDate, Date endDate);

    PostCalendar getPostCalendar(Date date);

    boolean updatePostCalendar(Date date, long count) throws Exception;

    Map<String, Integer> getMonthListCountMap(int year);

    boolean updatePostCalendar() throws Exception;

}