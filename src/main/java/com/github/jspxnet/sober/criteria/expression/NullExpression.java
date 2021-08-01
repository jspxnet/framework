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
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-8
 * Time: 11:22:06
 */
public class NullExpression implements Criterion {
    private final DatabaseEnumType[] noNullDb = new DatabaseEnumType[]{
            DatabaseEnumType.POSTGRESQL,DatabaseEnumType.MYSQL,DatabaseEnumType.MSSQL,DatabaseEnumType.ORACLE
    };
    private final String propertyName;

    public NullExpression(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        if (DatabaseEnumType.inArray(noNullDb,databaseName))
        {
            if (DatabaseEnumType.DM.equals(DatabaseEnumType.find(databaseName)))
            {
                return "(" + StringUtil.quote(propertyName,true) + " IS NULL OR " + StringUtil.quote(propertyName,true) + "='')";
            } else
            {
                return "(" + propertyName + " IS NULL OR " + propertyName + "='')";
            }

        }
        return propertyName + " IS NULL";
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {
        return null;
    }

    @Override
    public String toString() {
        return propertyName + " IS NULL";
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