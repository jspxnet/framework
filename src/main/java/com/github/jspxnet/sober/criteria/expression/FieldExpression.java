package com.github.jspxnet.sober.criteria.expression;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.OperatorEnumType;
import com.github.jspxnet.sober.criteria.projection.Criterion;
import com.github.jspxnet.sober.enums.DatabaseEnumType;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by chenyuan on 15-5-5.
 */
public class FieldExpression implements Criterion {
    private final String field1;
    private final String compare;
    private final String field2;

    public FieldExpression(String field1, String compare, String field2) {
        this.field1 = field1;
        this.compare = compare;
        this.field2 = field2;
    }

    public FieldExpression(JSONObject json) {
        field1 = json.getString(JsonExpression.JSON_FIELD1);
        field2 = json.getString(JsonExpression.JSON_FIELD2);
        compare = json.getString(JsonExpression.JSON_OPERATOR);
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        StringBuilder sb = new StringBuilder();
        if (DatabaseEnumType.DM.equals(DatabaseEnumType.find(databaseName)))
        {
            sb.append(StringUtil.quote(field1,true)).append(compare).append(StringUtil.quote(field2,true));
        }
        else
        {
            sb.append(field1).append(compare).append(field2);
        }
        return sb.toString();
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {
        return null;
    }

    @Override
    public String toString() {
        return field1 + compare + field2;
    }

    @Override
    public String[] getFields() {
        return new String[]{field1};
    }

    /**
     *
     * @return 扩展对
     */
    @Override
    public OperatorEnumType getOperatorEnumType() {
        return OperatorEnumType.find(compare);
    }


    @Override
    public JSONObject getJson()
    {
        OperatorEnumType operatorEnumType = getOperatorEnumType();
        JSONObject json = new JSONObject();
        json.put(JsonExpression.JSON_FIELD1,field1);
        json.put(JsonExpression.JSON_OPERATOR,operatorEnumType.getKey());
        json.put(JsonExpression.JSON_FIELD2,field2);
        return json;
    }
}