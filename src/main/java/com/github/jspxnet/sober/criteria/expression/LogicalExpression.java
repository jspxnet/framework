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

import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.FilterLogicEnumType;
import com.github.jspxnet.sober.criteria.OperatorEnumType;
import com.github.jspxnet.sober.criteria.projection.Criterion;
import com.github.jspxnet.sober.util.JdbcUtil;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-8
 * Time: 23:37:18
 */
@Slf4j
public class LogicalExpression implements Criterion {

    //是否使用括号包括 filters
    private boolean bracket = true;

    //字段条件
    private List<Criterion> filters = new LinkedList<>();

    private FilterLogicEnumType logic = FilterLogicEnumType.AND;


    public LogicalExpression(JSONObject json) {

        String logicKey = json.getString(JsonExpression.JSON_LOGIC,logic.getKey());
        logic = FilterLogicEnumType.find(logicKey);
        if (!json.containsKey(JsonExpression.JSON_BRACKET))
        {
            //默认
            bracket = true;
        } else
        {
            bracket = json.getBoolean(JsonExpression.JSON_BRACKET);
        }

        JSONArray array = json.getIgnoreJSONArray(JsonExpression.JSON_FILTERS);
        if (!ObjectUtil.isEmpty(array))
        {
            for (int i=0;i<array.length();i++)
            {
                JSONObject criterionJson = array.getJSONObject(i);
                if (criterionJson.containsKey(JsonExpression.JSON_LOGIC))
                {
                    //逻辑表达式
                    filters.add(new LogicalExpression(criterionJson));
                } else {
                    String operator = criterionJson.getIgnoreString(JsonExpression.JSON_OPERATOR);
                    OperatorEnumType operatorEnumType =  OperatorEnumType.find(operator);
                    if (OperatorEnumType.UNKNOWN.equals(operatorEnumType))
                    {
                        continue;
                    }
                    if (StringUtil.isNull(operatorEnumType.getClassName()))
                    {
                        continue;
                    }
                    try {
                        Criterion criterion = (Criterion)ClassUtil.newInstance(operatorEnumType.getClassName(),new Object[]{criterionJson});
                        filters.add(criterion);
                    } catch (Exception e) {
                        log.error("转换异常:{}",criterionJson,e);
                    }
                }
            }
        }

    }


    public LogicalExpression(List<Criterion> filters, String op) {
        this.filters = filters;
        this.logic = FilterLogicEnumType.find(op);
    }

    public LogicalExpression(List<Criterion> filters, FilterLogicEnumType op) {
        this.filters = filters;
        this.logic = op;
    }

    public LogicalExpression(List<Criterion> filters, FilterLogicEnumType op,boolean bracket) {
        this.filters = filters;
        this.logic = op;
        this.bracket = bracket;
    }

    public FilterLogicEnumType getFilterLogicEnumType() {
        return this.logic;
    }

    @Override
    public OperatorEnumType getOperatorEnumType() {
        return OperatorEnumType.LOGIC;
    }
    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        if (filters == null || filters.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        if (bracket)
        {
            sb.append("(");
        }

        for (int i = 0; i < filters.size(); i++) {
            Criterion criterion = filters.get(i);
            sb.append(criterion.toSqlString(soberTable, databaseName));
            if (i < filters.size() - 1) {
                sb.append(" ").append(this.logic.getKey()).append(" ");
            }
        }
        if (bracket)
        {
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {
        if (filters == null || filters.isEmpty()) {
            return new Object[0];
        }
        Object[] paramArray = null;
        for (Criterion criterion : filters) {
            paramArray = JdbcUtil.appendArray(paramArray, criterion.getParameter(soberTable));
        }
        return paramArray;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (bracket)
        {
            sb.append("(");
        }
        for (int i = 0; i < filters.size(); i++) {
            Criterion criterion = filters.get(i);
            sb.append(criterion.toString());
            if (filters.size() - 1 != i) {
                sb.append(" ").append(this.logic.getKey()).append(" ");
            }
        }
        if (bracket)
        {
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public String[] getFields() {
        if (ObjectUtil.isEmpty(filters))
        {
            return null;
        }
        String[] fields = null;
        for (Criterion criterion : filters) {
            if (criterion == null) {
                continue;
            }
            fields = ArrayUtil.join(fields, criterion.getFields());
        }
        return fields;
    }

    @Override
    public JSONObject getJson()
    {
        JSONObject json = new JSONObject();
        json.put(JsonExpression.JSON_LOGIC,logic.getKey());
        json.put(JsonExpression.JSON_BRACKET,bracket);

        JSONArray array = new JSONArray();
        for (Criterion criterion : filters) {
            if (criterion == null) {
                continue;
            }
            array.add(criterion.getJson());
        }
        json.put(JsonExpression.JSON_FILTERS,array);
        return json;
    }
}