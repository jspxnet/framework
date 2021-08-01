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
 * Time: 23:27:49
 */
public class SimpleExpression implements Criterion {

    private final String propertyName;
    private final Object value;
    private final String op;

    public SimpleExpression(String propertyName, Object value, String op) {
        this.propertyName = propertyName;
        this.value = value;
        this.op = op;
    }

    public SimpleExpression ignoreCase() {
        return this;
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        StringBuilder sb = new StringBuilder();
        if (DatabaseEnumType.DM.equals(DatabaseEnumType.find(databaseName)))
        {
            sb.append(StringUtil.quote(propertyName,true)).append(op).append("? ");
        }
        else
        {
            sb.append(propertyName).append(op).append("? ");
        }
        return sb.toString();
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {
        return JdbcUtil.appendArray(null, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(propertyName).append(getOp()).append(value);
        return sb.toString();
    }

    @Override
    public String[] getFields() {
        return new String[]{propertyName};
    }

    protected final String getOp() {
        return op;
    }

    @Override
    public String termString() {
        return toString();
    }
}