package com.github.jspxnet.sober.criteria.expression;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.OperatorEnumType;
import com.github.jspxnet.sober.criteria.projection.Criterion;
import com.github.jspxnet.sober.enums.DatabaseEnumType;
import com.github.jspxnet.sober.util.JdbcUtil;
import com.github.jspxnet.utils.StringUtil;

public class NotLikeExpression implements Criterion {

    private final String propertyName;
    private final Object value;

    public NotLikeExpression(String propertyName,Object value) {
        this.propertyName = propertyName;
        this.value = value;
    }
    public NotLikeExpression(JSONObject json) {
        propertyName = json.getString(JsonExpression.JSON_FIELD);
        value = json.get(JsonExpression.JSON_VALUE);
    }
    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        if (DatabaseEnumType.DM.equals(DatabaseEnumType.find(databaseName)))
        {
            return StringUtil.quote(propertyName,true)+ " " + OperatorEnumType.NOT_LIKE.getSql() + " ?";
        }
        return propertyName + " " + OperatorEnumType.NOT_LIKE.getSql() + " ?";
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {
        return JdbcUtil.appendArray(null, value);
    }

    @Override
    public String toString() {
        return propertyName + " " + OperatorEnumType.NOT_LIKE.getSql() + " " + value;
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
        json.put(JsonExpression.JSON_OPERATOR,OperatorEnumType.NOT_LIKE.getKey());
        json.put(JsonExpression.JSON_VALUE,value);
        return json;
    }

}