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

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.OperatorEnumType;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-8
 * Time: 10:12:03
 */
public interface Criterion extends Serializable {


    /**
     *
     * @return 得到涉及的字段
     */
    String[] getFields();

    /**
     *
     * @param soberTable  数据库模型
     * @param databaseType 数据库类型
     * @return 得到sql
     */
    String toSqlString(TableModels soberTable, String databaseType);

    /**
     *
     * @param soberTable 数据库模型
     * @return 得到参数
     */
    Object[] getParameter(TableModels soberTable);

    //String termString();


    OperatorEnumType getOperatorEnumType();

    JSONObject getJson();


}