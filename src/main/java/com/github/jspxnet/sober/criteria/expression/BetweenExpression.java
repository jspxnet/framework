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
import com.github.jspxnet.sober.criteria.FilterLogicEnumType;
import com.github.jspxnet.sober.criteria.OperatorEnumType;
import com.github.jspxnet.sober.criteria.projection.Criterion;
import com.github.jspxnet.sober.enums.DatabaseEnumType;
import com.github.jspxnet.sober.util.JdbcUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-8
 * Time: 11:06:21
 */
public class BetweenExpression implements Criterion {
    private final Object field;
    private final Object low;
    private final Object high;

    public BetweenExpression(Object field, Object lo, Object hi) {
        this.field = field;
        this.low = lo;
        this.high = hi;
    }


    public BetweenExpression(JSONObject json) {
        field = json.getString(JsonExpression.JSON_FIELD);
        low = json.get(JsonExpression.JSON_LOW);
        high = json.get(JsonExpression.JSON_HIGH);
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        if (DatabaseEnumType.DM.equals(DatabaseEnumType.find(databaseName))) {
            if (field instanceof String)
            {
                return StringUtil.quote((String)field, true) + " " + OperatorEnumType.BETWEEN.getSql() + " ? " + FilterLogicEnumType.AND.getKey() + " ? ";
            }
        }
        return field + " " + OperatorEnumType.BETWEEN.getKey() + " ? " + FilterLogicEnumType.AND.getKey() + " ? ";
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {
        Object[] result = JdbcUtil.appendArray(null, low);
        return JdbcUtil.appendArray(result, high);
    }

    @Override
    public String toString() {
        return field + " " + OperatorEnumType.BETWEEN.getSql() + " " + low + " " + FilterLogicEnumType.AND.getKey() + " " + high;
    }

    @Override
    public String[] getFields() {
        if (field instanceof String)
        {
            return new String[]{(String)field};
        }

        if (low instanceof String && high instanceof String)
        {
            return new String[]{(String)low, (String)high};
        }
        return new String[]{(String)field};
    }


    /**
     * @return 扩展对
     */
    @Override
    public OperatorEnumType getOperatorEnumType() {
        return OperatorEnumType.BETWEEN;
    }


    @Override
    public JSONObject getJson() {
        JSONObject json = new JSONObject();
        json.put(JsonExpression.JSON_FIELD, field);
        json.put(JsonExpression.JSON_OPERATOR, OperatorEnumType.BETWEEN.getKey());
        json.put(JsonExpression.JSON_LOW, low);
        json.put(JsonExpression.JSON_HIGH, high);
        return json;
    }
}