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
import com.github.jspxnet.sober.enums.DatabaseEnumType;
import com.github.jspxnet.sober.util.JdbcUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-8
 * Time: 11:06:21
 */
public class BetweenExpression implements Criterion {
    private final String propertyName;
    private final Object lo;
    private final Object hi;

    public BetweenExpression(String propertyName, Object lo, Object hi) {
        this.propertyName = propertyName;
        this.lo = lo;
        this.hi = hi;
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        if (DatabaseEnumType.DM.equals(DatabaseEnumType.find(databaseName)))
        {
            return StringUtil.quote(propertyName,true) + " " + Restrictions.KEY_BETWEEN + " ? " + Restrictions.KEY_AND + " ? ";
        }
        return propertyName + " " + Restrictions.KEY_BETWEEN + " ? " + Restrictions.KEY_AND + " ? ";
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {
        Object[] result = JdbcUtil.appendArray(null, lo);
        return JdbcUtil.appendArray(result, hi);
    }

    @Override
    public String toString() {
        return propertyName + " " + Restrictions.KEY_BETWEEN + " " + lo + " " + Restrictions.KEY_AND + " " + hi;
    }

    @Override
    public String[] getFields() {
        return new String[]{propertyName};
    }

    @Override
    public String termString() {
        return toString();
    }

}