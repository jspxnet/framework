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

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 12-2-4
 * Time: 下午10:54
 */
public interface GenericDAO extends SoberSupport {


    <T> List<T> getList(
            T cls,
            String field,
            String find,
            String term,
            long uid,
            String sortString,
            int page, int count, boolean load);

    int getCount(
            Class T,
            String field,
            String find,
            String term,
            long uid
    ) throws Exception;
}