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
import com.github.jspxnet.sober.util.JdbcUtil;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.ObjectUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-8
 * Time: 23:37:18
 */
public class LogicalExpression implements Criterion {

    private List<Criterion> list;
    private String op = "AND";

    public LogicalExpression(List<Criterion> list, String op) {
        this.list = list;
        this.op = op;
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        sb.append("(");

        for (int i = 0; i < list.size(); i++) {
            Criterion criterion = list.get(i);
            sb.append(criterion.toSqlString(soberTable, databaseName));
            if (i < list.size() - 1) {
                sb.append(" ").append(getOp()).append(" ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public String getOp() {
        return op;
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {
        if (list == null || list.isEmpty()) {
            return new Object[0];
        }
        Object[] paramArray = null;
        for (Criterion criterion : list) {
            paramArray = JdbcUtil.appendArray(paramArray, criterion.getParameter(soberTable));
        }
        return paramArray;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < list.size(); i++) {
            Criterion criterion = list.get(i);
            sb.append(criterion.toString());
            if (list.size() - 1 != i) {
                sb.append(" ").append(getOp()).append(" ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String[] getFields() {
        if (ObjectUtil.isEmpty(list))
        {
            return null;
        }
        String[] fields = null;
        for (Criterion criterion : list) {
            if (criterion == null) {
                continue;
            }
            fields = ArrayUtil.join(fields, criterion.getFields());
        }
        return fields;
    }

    @Override
    public String termString() {
        return toString();
    }
}