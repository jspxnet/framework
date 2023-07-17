package com.github.jspxnet.sober.criteria.expression;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.OperatorEnumType;
import com.github.jspxnet.sober.criteria.projection.Criterion;
import com.github.jspxnet.sober.util.SoberUtil;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class NotInExpression  implements Criterion {
    private final String propertyName;
    private final List<Object> values = new LinkedList<>();

    public NotInExpression(JSONObject json) {
        propertyName = json.getString(JsonExpression.JSON_FIELD);
        List<Object> valuesList = json.getIgnoreList(JsonExpression.JSON_VALUE);
        if (valuesList!=null)
        {
            values.addAll(valuesList);
        }
    }


    public NotInExpression(String propertyName, Object[] values) {
        this.propertyName = propertyName;
        if (!ArrayUtil.isEmpty(values)) {
            this.values.addAll(Arrays.asList(values));
        }
    }

    public NotInExpression(String propertyName, int[] values) {
        this.propertyName = propertyName;
        if (!ArrayUtil.isEmpty(values)) {
            this.values.addAll(Arrays.asList(values));
        }
    }

    public NotInExpression(String propertyName, long[] values) {
        this.propertyName = propertyName;
        if (!ArrayUtil.isEmpty(values)) {
            this.values.addAll(Arrays.asList(values));
        }
    }

    public NotInExpression(String propertyName, float[] values) {
        this.propertyName = propertyName;
        if (!ArrayUtil.isEmpty(values)) {
            this.values.addAll(Arrays.asList(values));
        }
    }

    public NotInExpression(String propertyName, double[] values) {
        this.propertyName = propertyName;
        if (!ArrayUtil.isEmpty(values)) {
            this.values.addAll(Arrays.asList(values));
        }
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {
        return values.toArray();
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        StringBuilder sb = new StringBuilder();
        if (!ObjectUtil.isEmpty(values))
        {
            for (int i = 0; i < values.size(); i++) {
                sb.append("?");
                if (i != values.size() - 1) {
                    sb.append(",");
                }
            }
        }
        if (!StringUtil.hasLength(sb.toString())) {
            return propertyName + " "+OperatorEnumType.NOT_IN.getSql()+" ('')";
        }
        return propertyName + " "+OperatorEnumType.NOT_IN.getSql()+" (" + sb + ") ";
    }

    @Override
    public String toString() {
        if (ObjectUtil.isEmpty(values)) {
            return propertyName + " "+OperatorEnumType.NOT_IN.getSql()+" ('')";
        }
        Object[] objects = values.toArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objects.length; i++) {
            sb.append(SoberUtil.toTypeString(objects[i]));
            if (i != values.size() - 1) {
                sb.append(",");
            }
        }
        if (!StringUtil.hasLength(sb.toString())) {
            return propertyName + " "+OperatorEnumType.NOT_IN.getSql()+" ('')";
        }
        return propertyName + " "+OperatorEnumType.NOT_IN.getSql()+" (" + sb + ") ";
    }

    @Override
    public String[] getFields() {
        return new String[]{propertyName};
    }


    @Override
    public OperatorEnumType getOperatorEnumType() {
        return OperatorEnumType.NOT_IN;
    }


    @Override
    public JSONObject getJson()
    {
        JSONObject json = new JSONObject();
        json.put(JsonExpression.JSON_FIELD,propertyName);
        json.put(JsonExpression.JSON_OPERATOR,OperatorEnumType.NOT_IN.getKey());
        json.put(JsonExpression.JSON_VALUE,values);
        return json;
    }

}