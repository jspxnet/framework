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
 * date: 2007-1-10
 * Time: 8:55:17
 */
public class CountProjection extends AggregateProjection {

    private boolean distinct;

    protected CountProjection(String prop) {
        super(Expression.KEY_COUNT, prop);
    }

    @Override
    public String toString() {
        if (distinct) {
            return Expression.KEY_DISTINCT + super.toString();
        } else {
            return super.toString();
        }
    }


    @Override
    public String toSqlString(String databaseName) {
        if (distinct) {
            return Expression.KEY_DISTINCT + super.toString();
        } else {
            return super.toString();
        }
    }

    public CountProjection setDistinct() {
        distinct = true;
        return this;
    }

}