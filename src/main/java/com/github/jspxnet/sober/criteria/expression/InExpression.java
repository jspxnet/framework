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
import com.github.jspxnet.sober.util.SoberUtil;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-8
 * Time: 11:19:11
 */
public class InExpression implements Criterion {
    private final String propertyName;
    private final List<Object> values = new LinkedList<>();

    public InExpression(JSONObject json) {
        propertyName = json.getString(JsonExpression.JSON_FIELD);
        values.addAll(json.getIgnoreList(JsonExpression.JSON_VALUE));
    }


    public InExpression(String propertyName, Object[] values) {
        this.propertyName = propertyName;
        if (!ArrayUtil.isEmpty(values)) {
            this.values.addAll(Arrays.asList(values));
        }
    }

    public InExpression(String propertyName, int[] values) {
        this.propertyName = propertyName;
        if (!ArrayUtil.isEmpty(values)) {
            this.values.addAll(Collections.singletonList(values));
        }
    }

    public InExpression(String propertyName, long[] values) {
        this.propertyName = propertyName;
        if (!ArrayUtil.isEmpty(values)) {
            this.values.addAll(Collections.singletonList(values));
        }
    }

    public InExpression(String propertyName, float[] values) {
        this.propertyName = propertyName;
        if (!ArrayUtil.isEmpty(values)) {
            this.values.addAll(Collections.singletonList(values));
        }
    }

    public InExpression(String propertyName, double[] values) {
        this.propertyName = propertyName;
        if (!ArrayUtil.isEmpty(values)) {
            this.values.addAll(Collections.singletonList(values));
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
            return propertyName + " IN ('')";
        }
        return propertyName + " IN (" + sb + ") ";
    }

    @Override
    public String toString() {
        if (ObjectUtil.isEmpty(values)) {
            return propertyName + " IN ('')";
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
            return propertyName + " IN ('')";
        }
        return propertyName + " IN (" + sb + ") ";
    }

    @Override
    public String[] getFields() {
        return new String[]{propertyName};
    }



    @Override
    public OperatorEnumType getOperatorEnumType() {
        return OperatorEnumType.IN;
    }


    @Override
    public JSONObject getJson()
    {
        JSONObject json = new JSONObject();
        json.put(JsonExpression.JSON_FIELD,propertyName);
        json.put(JsonExpression.JSON_OPERATOR,OperatorEnumType.IN.getKey());
        json.put(JsonExpression.JSON_VALUE,values);
        return json;
    }
}