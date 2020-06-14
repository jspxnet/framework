/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.postalcode;

import com.github.jspxnet.sober.SoberSupport;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-3-14
 * Time: 17:33:48
 */
public interface AreaCityDAO extends SoberSupport {
    List<AreaCity> getAreaCityList();

    boolean createAreaCity(String xmlString);

    List<AreaCity>  getProvinceList() throws Exception;

    long getCount();
}