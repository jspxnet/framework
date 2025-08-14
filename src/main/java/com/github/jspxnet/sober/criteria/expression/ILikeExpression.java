package com.github.jspxnet.sober.criteria.expression;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.OperatorEnumType;
import com.github.jspxnet.sober.criteria.projection.Criterion;
import com.github.jspxnet.sober.enums.DatabaseEnumType;
import com.github.jspxnet.sober.util.JdbcUtil;

public class ILikeExpression implements Criterion {

    private final String propertyName;
    private final Object value;

    public ILikeExpression(String propertyName,Object value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    public ILikeExpression(JSONObject json) {
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
        return propertyName + " " + OperatorEnumType.ILIKE.getSql() + " ?";
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {
        return JdbcUtil.appendArray(null, value);
    }

    @Override
    public String toString() {
        return propertyName + " " + OperatorEnumType.ILIKE.getSql() + " " + value;
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
        json.put(JsonExpression.JSON_OPERATOR,OperatorEnumType.ILIKE.getKey());
        json.put(JsonExpression.JSON_VALUE,value);
        return json;
    }

}