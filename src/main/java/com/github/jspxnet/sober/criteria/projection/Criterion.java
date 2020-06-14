/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.criteria.projection;

import com.github.jspxnet.sober.TableModels;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-8
 * Time: 10:12:03
 */
public interface Criterion extends Serializable {

    String[] getFields();

    String toSqlString(TableModels soberTable, String databaseName);

    Object[] getParameter(TableModels soberTable);

    String termString();


}