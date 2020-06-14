/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.bundle;

import com.github.jspxnet.txweb.bundle.table.BundleTable;
import com.github.jspxnet.sober.SoberSupport;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-27
 * Time: 12:00:12
 */
public interface BundleDAO extends SoberSupport {
    BundleTable getBundleTable(final String keys, final String dataType, final String namespace);

    List getList(String dataType, String namespace);

    boolean deleteAll(String dataType, String namespace);
}