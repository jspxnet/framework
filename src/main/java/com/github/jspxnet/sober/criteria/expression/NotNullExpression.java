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
import com.github.jspxnet.sober.SoberEnv;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-8
 * Time: 11:23:21
 */
public class NotNullExpression implements Criterion {

    private String propertyName;

    public NotNullExpression(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        if (SoberEnv.POSTGRESQL.equalsIgnoreCase(databaseName) || SoberEnv.MYSQL.equalsIgnoreCase(databaseName) || SoberEnv.MSSQL.equalsIgnoreCase(databaseName) || SoberEnv.ORACLE.equalsIgnoreCase(databaseName)) {
            StringBuilder sb = new StringBuilder();
            sb.append("(").append(propertyName).append(" IS NOT NULL AND ").append(propertyName).append("<>'')");
            return sb.toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(propertyName).append(" IS NOT NULL");
        return sb.toString();
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {
        return null;
    }

    @Override
    public String[] getFields() {
        return new String[]{propertyName};
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(propertyName).append(" IS NOT NULL");
        return sb.toString();
    }

    @Override
    public String termString() {
        return toString();
    }
}