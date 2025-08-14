/*
 * Copyright (c) 2013. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.sober.criteria.expression;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.OperatorEnumType;
import com.github.jspxnet.sober.criteria.projection.Criterion;
import com.github.jspxnet.sober.enums.DatabaseEnumType;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created with IntelliJ IDEA.
 * User: chenYuan
 * date: 13-4-11
 * Time: 下午8:23
 * 特殊 in(sql)
 */
public class InSqlExpression implements Criterion {
    private final String propertyName;
    private String sql = StringUtil.empty;

    public InSqlExpression(String propertyName, String values) {
        this.propertyName = propertyName;
        this.sql = values;
    }
    public InSqlExpression(JSONObject json) {
        propertyName = json.getString(JsonExpression.JSON_FIELD);
        sql = json.getString(JsonExpression.JSON_VALUE,sql);
    }
    @Override
    public Object[] getParameter(TableModels soberTable) {
        return new Object[]{};
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        if (DatabaseEnumType.DM.equals(DatabaseEnumType.find(databaseName)))
        {
            return StringUtil.quote(propertyName,true) + " "+OperatorEnumType.INSQL.getSql()+" (" + sql + ") ";
        }
        return propertyName + " "+OperatorEnumType.INSQL.getSql()+" (" + sql + ") ";
    }

    @Override
    public String toString() {
        if (sql == null) {
            return propertyName + " IN ('')";
        }
        return propertyName + " IN (" + sql + ") ";
    }

    @Override
    public String[] getFields() {
        return new String[]{propertyName};
    }


    @Override
    public OperatorEnumType getOperatorEnumType() {
        return OperatorEnumType.INSQL;
    }

    @Override
    public JSONObject getJson()
    {
        JSONObject json = new JSONObject();
        json.put(JsonExpression.JSON_FIELD,propertyName);
        json.put(JsonExpression.JSON_OPERATOR,OperatorEnumType.INSQL.getKey());
        json.put(JsonExpression.JSON_VALUE,sql);
        return json;
    }
}