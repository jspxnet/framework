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

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.criteria.FilterLogicEnumType;
import com.github.jspxnet.sober.criteria.OperatorEnumType;
import com.github.jspxnet.sober.criteria.projection.Criterion;
import com.github.jspxnet.sober.enums.DatabaseEnumType;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-8
 * Time: 11:23:21
 */
public class NotNullExpression implements Criterion {

    private final DatabaseEnumType[] noNullDb = new DatabaseEnumType[]{
            DatabaseEnumType.POSTGRESQL,DatabaseEnumType.MYSQL,DatabaseEnumType.MSSQL,DatabaseEnumType.ORACLE
    };

    private final String propertyName;

    public NotNullExpression(String propertyName) {
        this.propertyName = propertyName;
    }

    public NotNullExpression(JSONObject json) {
        propertyName = json.getString(JsonExpression.JSON_FIELD);
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        SoberColumn soberColumn = soberTable.getColumn(propertyName);
        if (soberColumn!=null&&"String".equalsIgnoreCase(soberColumn.getTypeString()))
        {
            if (DatabaseEnumType.inArray(noNullDb,databaseName))
            {
                if (DatabaseEnumType.DM.equals(DatabaseEnumType.find(databaseName)))
                {
                    return "(" + StringUtil.quote(propertyName, true) +
                            " " + OperatorEnumType.NOT_NULL.getSql() + " "+ FilterLogicEnumType.AND.getKey()+" " +
                            StringUtil.quote(propertyName, true) + "<>'')";
                } else
                {
                    return "(" + propertyName + " " + OperatorEnumType.NOT_NULL.getSql()+" "+FilterLogicEnumType.AND.getKey()
                            +" " + propertyName + "<>'')";
                }
            }
        }
        return propertyName + " " + OperatorEnumType.NOT_NULL.getSql();
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
        return propertyName + " " + OperatorEnumType.NOT_NULL.getSql();
    }


    @Override
    public OperatorEnumType getOperatorEnumType() {
        return OperatorEnumType.NOT_NULL;
    }

    @Override
    public JSONObject getJson()
    {
        JSONObject json = new JSONObject();
        json.put(JsonExpression.JSON_FIELD,propertyName);
        json.put(JsonExpression.JSON_OPERATOR,OperatorEnumType.NOT_NULL.getKey());
        return json;
    }
}