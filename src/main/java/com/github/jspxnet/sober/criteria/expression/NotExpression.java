package com.github.jspxnet.sober.criteria.expression;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.OperatorEnumType;
import com.github.jspxnet.sober.criteria.projection.Criterion;
import com.github.jspxnet.utils.ClassUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenYuan
 * 都提供了单独的not方式
 */
@Slf4j
public class NotExpression implements Criterion {

    private final Criterion criterion;

    public NotExpression(JSONObject json) {
        if (json.containsKey(JsonExpression.JSON_LOGIC))
        {
            criterion = new LogicalExpression(json);
        } else {
            try {
                JSONObject jsonObject = json.getIgnoreJSONObject(JsonExpression.JSON_FIELD);
                String operator = jsonObject.getIgnoreString(JsonExpression.JSON_OPERATOR);
                OperatorEnumType operatorEnumType =  OperatorEnumType.find(operator);
                criterion = (Criterion) ClassUtil.newInstance(operatorEnumType.getClassName(),new Object[]{jsonObject});
            } catch (Exception e) {
                log.error("NotExpression json 解析错误:{}",json,e);
                throw new RuntimeException(e);
            }
        }
    }


    public NotExpression(Criterion criterion) {
        this.criterion = criterion;
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        return Expression.KEY_NOT + "(" + criterion.toSqlString(soberTable, databaseName) + ")";
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {
        return criterion.getParameter(soberTable);
    }

    @Override
    public OperatorEnumType getOperatorEnumType() {
        return OperatorEnumType.NOT;
    }

    @Override
    public String toString() {
        return Expression.KEY_NOT + "(" + criterion.toString() + ")";
    }

    @Override
    public String[] getFields() {
        if (criterion==null)
        {
            return null;
        }
        return criterion.getFields();
    }


    @Override
    public JSONObject getJson()
    {
        JSONObject json = new JSONObject();
        json.put(JsonExpression.JSON_FIELD,criterion.getJson());
        json.put(JsonExpression.JSON_OPERATOR, OperatorEnumType.NOT.getKey());
        return json;
    }

}
