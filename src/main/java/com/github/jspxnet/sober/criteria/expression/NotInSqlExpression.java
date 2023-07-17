package com.github.jspxnet.sober.criteria.expression;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.OperatorEnumType;
import com.github.jspxnet.sober.criteria.projection.Criterion;
import com.github.jspxnet.sober.enums.DatabaseEnumType;
import com.github.jspxnet.utils.StringUtil;

public class NotInSqlExpression implements Criterion {
    private final String propertyName;
    private String sql = StringUtil.empty;


    public NotInSqlExpression(JSONObject json) {
        propertyName = json.getString(JsonExpression.JSON_FIELD);
        sql = json.getString(JsonExpression.JSON_SQL);
    }

    public NotInSqlExpression(String propertyName, String values) {
        this.propertyName = propertyName;
        this.sql = values;
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {
        return new Object[]{};
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        if (DatabaseEnumType.DM.equals(DatabaseEnumType.find(databaseName)))
        {
            return StringUtil.quote(propertyName,true) + " "+OperatorEnumType.NINSQL.getSql()+" (" + sql + ") ";
        }
        return propertyName + " "+OperatorEnumType.NINSQL.getSql()+" (" + sql + ") ";
    }

    @Override
    public String toString() {
        if (sql == null) {
            return propertyName + " "+OperatorEnumType.NINSQL.getSql()+" ('')";
        }
        return propertyName + " " + OperatorEnumType.NINSQL.getSql() + " (" + sql + ") ";
    }

    @Override
    public String[] getFields() {
        return new String[]{propertyName};
    }

    @Override
    public OperatorEnumType getOperatorEnumType() {
        return OperatorEnumType.NINSQL;
    }

    @Override
    public JSONObject getJson()
    {
        JSONObject json = new JSONObject();
        json.put(JsonExpression.JSON_FIELD,propertyName);
        json.put(JsonExpression.JSON_OPERATOR,OperatorEnumType.NINSQL.getKey());
        json.put(JsonExpression.JSON_SQL,sql);
        return json;
    }
}