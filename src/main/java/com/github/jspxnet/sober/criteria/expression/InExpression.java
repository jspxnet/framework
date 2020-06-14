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

import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.projection.Criterion;
import com.github.jspxnet.sober.util.SoberUtil;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-8
 * Time: 11:19:11
 */
public class InExpression implements Criterion {
    private String propertyName;
    private Set<Object> values = new HashSet<Object>();



    public InExpression(String propertyName, Object[] values) {
        this.propertyName = propertyName;
        if (!ArrayUtil.isEmpty(values)) {
            this.values.addAll(Arrays.asList(values));
        }
    }

    public InExpression(String propertyName, int[] values) {
        this.propertyName = propertyName;
        if (!ArrayUtil.isEmpty(values)) {
            this.values.addAll(Arrays.asList(values));
        }
    }

    public InExpression(String propertyName, long[] values) {
        this.propertyName = propertyName;
        if (!ArrayUtil.isEmpty(values)) {
            this.values.addAll(Arrays.asList(values));
        }
    }

    public InExpression(String propertyName, float[] values) {
        this.propertyName = propertyName;
        if (!ArrayUtil.isEmpty(values)) {
            this.values.addAll(Arrays.asList(values));
        }
    }

    public InExpression(String propertyName, double[] values) {
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
            return propertyName + " IN ('')";
        }
        return propertyName + " IN (" + sb.toString() + ") ";
    }

    @Override
    public String toString() {
        if (values == null) {
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
        return propertyName + " IN (" + sb.toString() + ") ";
    }

    @Override
    public String[] getFields() {
        return new String[]{propertyName};
    }

    @Override
    public String termString() {
        return toString();
    }
}