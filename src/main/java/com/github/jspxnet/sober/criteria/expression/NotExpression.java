/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.criteria.expression;

import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.projection.Criterion;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-8
 * Time: 11:24:19
 */
public class NotExpression implements Criterion {

    private final Criterion criterion;

    public NotExpression(Criterion criterion) {
        this.criterion = criterion;
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        return Restrictions.KEY_NOT + "(" + criterion.toSqlString(soberTable, databaseName) + ")";
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {
        return criterion.getParameter(soberTable);
    }

    @Override
    public String toString() {
        return Restrictions.KEY_NOT + "(" + criterion.toString() + ")";
    }

    @Override
    public String[] getFields() {
        return null;
    }

    @Override
    public String termString() {
        return toString();
    }

}