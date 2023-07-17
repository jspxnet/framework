package com.github.jspxnet.sober.criteria.projection;

import com.github.jspxnet.sober.criteria.expression.Expression;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/12/14 21:54
 * description: jspx-framework
 **/
public class SumProjection extends AggregateProjection {
    public SumProjection(String propertyName) {
        super(Expression.KEY_SUM, propertyName);
    }
}