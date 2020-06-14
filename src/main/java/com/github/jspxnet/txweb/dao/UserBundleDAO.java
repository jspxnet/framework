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
import com.github.jspxnet.txweb.table.MemberBundle;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 12-3-28
 * Time: 下午4:41
 */
public interface UserBundleDAO extends SoberSupport {

    String getNamespace();

    void setNamespace(String namespace);

    MemberBundle getUserBundle(String key, long uid) ;

    List<MemberBundle> getList(long uid);

    boolean save(MemberBundle bundleTable) throws Exception;
}