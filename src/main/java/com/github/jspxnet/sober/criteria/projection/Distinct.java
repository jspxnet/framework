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

import com.github.jspxnet.sober.criteria.expression.Expression;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-9
 * Time: 23:35:35
 */
public class Distinct implements Projection {

    private final Projection projection;

    public Distinct(Projection proj) {
        this.projection = proj;
    }

    @Override
    public String toSqlString(String databaseName) {

        return Expression.KEY_DISTINCT + " " + projection.toSqlString(databaseName);
    }

    @Override
    public String toString() {
        return Expression.KEY_DISTINCT + " " + projection.toString();
    }
}