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
import com.github.jspxnet.sober.criteria.OperatorEnumType;
import com.github.jspxnet.sober.criteria.projection.Criterion;
import com.github.jspxnet.sober.enums.DatabaseEnumType;
import com.github.jspxnet.sober.util.JdbcUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-8
 * Time: 11:10:16
 */
public class LikeExpression implements Criterion {

    private final String propertyName;
    private final Object value;

    public LikeExpression(String propertyName,Object value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    public LikeExpression(JSONObject json) {
        propertyName = json.getString(JsonExpression.JSON_FIELD);
        value = json.get(JsonExpression.JSON_VALUE);
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        if (DatabaseEnumType.DM.equals(DatabaseEnumType.find(databaseName)))
        {
            return propertyName + " " + OperatorEnumType.LIKE.getSql() + " ?";
        }
        if (DatabaseEnumType.POSTGRESQL.equals(DatabaseEnumType.find(databaseName)))
        {
            return propertyName + " " + OperatorEnumType.ILIKE.getSql() + " ?";
        }
        return propertyName + " " + OperatorEnumType.LIKE.getSql() + " ?";
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {
        return JdbcUtil.appendArray(null, value);
    }

    @Override
    public String toString() {
        return propertyName + " " + OperatorEnumType.LIKE.getSql() + " " + value;
    }

    @Override
    public String[] getFields() {
        return new String[]{propertyName};
    }

    @Override
    public OperatorEnumType getOperatorEnumType() {
        return OperatorEnumType.LIKE;
    }

    @Override
    public JSONObject getJson()
    {
        JSONObject json = new JSONObject();
        json.put(JsonExpression.JSON_FIELD,propertyName);
        json.put(JsonExpression.JSON_OPERATOR,OperatorEnumType.LIKE.getKey());
        json.put(JsonExpression.JSON_VALUE,value);
        return json;
    }

}