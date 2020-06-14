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

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-8
 * Time: 11:06:21
 */
public class BetweenExpression implements Criterion {
    private String propertyName;
    private Object lo;
    private Object hi;

    public BetweenExpression(String propertyName, Object lo, Object hi) {
        this.propertyName = propertyName;
        this.lo = lo;
        this.hi = hi;
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        StringBuilder sb = new StringBuilder();
        sb.append(propertyName).append(" ").append(Restrictions.key_between).append(" ? ").append(Restrictions.key_and).append(" ? ");
        return sb.toString();
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {
        Object[] result = JdbcUtil.appendArray(null, lo);
        return JdbcUtil.appendArray(result, hi);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(propertyName).append(" ").append(Restrictions.key_between).append(" ").append(lo).append(" ").append(Restrictions.key_and).append(" ").append(hi);
        return sb.toString();
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