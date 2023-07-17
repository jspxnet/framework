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
import lombok.extern.slf4j.Slf4j;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-8
 * Time: 23:27:49
 */

@Slf4j
public class SimpleExpression implements Criterion {

    private final String propertyName;
    private final Object value;
    private final OperatorEnumType op;

    public SimpleExpression(JSONObject json)  {
        propertyName = json.getString(JsonExpression.JSON_FIELD);
        value = json.get(JsonExpression.JSON_VALUE);
        String opStr = json.getIgnoreString(JsonExpression.JSON_OPERATOR);
        op = OperatorEnumType.find(opStr);
        if (op.equals(OperatorEnumType.UNKNOWN))
        {
            try {
                throw  new Exception("表达式异常op不能识别:"+json.toString(4));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public SimpleExpression(String propertyName, Object value, OperatorEnumType op) {
        this.propertyName = propertyName;
        this.value = value;
        this.op = op;
        if (op.equals(OperatorEnumType.UNKNOWN))
        {
            log.debug("表达式异常op不能识别:{}",op);
        }
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        StringBuilder sb = new StringBuilder();
        if (DatabaseEnumType.DM.equals(DatabaseEnumType.find(databaseName)))
        {
            sb.append(StringUtil.quote(propertyName,true)).append(op.getSql()).append("? ");
        }
        else
        {
            sb.append(propertyName).append(op.getSql()).append("? ");
        }
        return sb.toString();
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {
        return JdbcUtil.appendArray(null, value);
    }

    @Override
    public String toString() {
        return propertyName + op.getSql() + value;
    }

    @Override
    public String[] getFields() {
        return new String[]{propertyName};
    }

    @Override
    public OperatorEnumType getOperatorEnumType() {
        return op;
    }

    @Override
    public JSONObject getJson()
    {
        OperatorEnumType  operatorEnumType = getOperatorEnumType();
        JSONObject json = new JSONObject();
        json.put(JsonExpression.JSON_OPERATOR,operatorEnumType.getKey());
        json.put(JsonExpression.JSON_FIELD,propertyName);
        json.put(JsonExpression.JSON_VALUE,value);
        return json;
    }
}