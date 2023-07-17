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


import com.github.jspxnet.sober.criteria.FilterLogicEnumType;
import com.github.jspxnet.sober.criteria.OperatorEnumType;
import com.github.jspxnet.sober.criteria.projection.Criterion;
import com.github.jspxnet.utils.ArrayUtil;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-9
 * Time: 15:01:56
 */
public final class Expression {

    private Expression() {

    }

    public static final String KEY_COUNT = "COUNT";
    public static final String KEY_DISTINCT = "DISTINCT";
    public static final String KEY_SUM = "SUM";
    public static final String KEY_NOT = "NOT";


    /**
     * Apply an "equal" constraint transfer the named property
     *
     * @param propertyName 属性名称
     * @param value        变量
     * @return Criterion
     */
    public static SimpleExpression eq(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, OperatorEnumType.EQ);
    }

    /**
     * Apply a "not equal" constraint transfer the named property
     *
     * @param propertyName 属性名称
     * @param value        变量
     * @return Criterion
     */
    public static SimpleExpression ne(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, OperatorEnumType.NE);
    }

    /**
     * Apply a "like" constraint transfer the named property
     *
     * @param propertyName 属性名称
     * @param value        变量
     * @return Criterion
     */
    public static Criterion like(String propertyName, Object value) {
        return new LikeExpression(propertyName, value);
    }


    /**
     * Apply a "like" constraint transfer the named property
     *
     * @param propertyName 属性名称
     * @param value        变量
     * @return Criterion
     */
    public static Criterion find(String[] propertyName, Object[] value) {
        return new FindExpression(propertyName, value);
    }

    /**
     * @param propertyName 字段
     * @param value        值
     * @param or           是否or关系
     * @return 查询对象
     */
    public static Criterion find(String[] propertyName, Object[] value, boolean or) {
        return new FindExpression(propertyName, value, or);
    }

    /**
     * Apply a "greater than" constraint transfer the named property
     *
     * @param propertyName 属性名称
     * @param value        变量
     * @return Criterion
     */
    public static Criterion gt(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, OperatorEnumType.GT);
    }

    /**
     * Apply a "less than" constraint transfer the named property
     *
     * @param propertyName 属性名称
     * @param value        变量
     * @return Criterion
     */
    public static Criterion lt(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, OperatorEnumType.LE);
    }

    /**
     * Apply a "less than or equal" constraint transfer the named property
     *
     * @param propertyName 属性名称
     * @param value        变量
     * @return Criterion
     */
    public static Criterion le(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, OperatorEnumType.LE);
    }

    /**
     * Apply a "greater than or equal" constraint transfer the named property
     *
     * @param propertyName 属性名称
     * @param value        变量
     * @return Criterion
     */
    public static Criterion ge(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, OperatorEnumType.GE);
    }



    /**
     * Apply a "between" constraint transfer the named property
     *
     * @param propertyName 属性名称
     * @param lo           value
     * @param hi           value
     * @return Criterion
     */
    public static Criterion between(String propertyName, Object lo, Object hi) {
        return new BetweenExpression(propertyName, lo, hi);
    }

    /**
     * Apply an "in" constraint transfer the named property
     *
     * @param propertyName 属性名称
     * @param values       变量
     * @return Criterion
     */
    public static Criterion in(String propertyName, Object[] values) {
        return new InExpression(propertyName, values);
    }

    public static Criterion in(String propertyName, long[] values) {
        return new InExpression(propertyName, ArrayUtil.getLongObjectArray(values));
    }

    public static Criterion in(String propertyName, int[] values) {
        return new InExpression(propertyName, ArrayUtil.getIntegerArray(values));
    }

    public static Criterion in(String propertyName, float[] values) {
        return new InExpression(propertyName, ArrayUtil.getFloatObjectArray(values));
    }

    public static Criterion in(String propertyName, double[] values) {
        return new InExpression(propertyName, ArrayUtil.getDoubleObjectArray(values));
    }

    public static Criterion in(String propertyName, BigDecimal[] values) {
        return new InExpression(propertyName, values);
    }

    /**
     * Apply an "in" constraint transfer the named property
     *
     * @param propertyName 属性名称
     * @param values       变量
     * @return Criterion
     */
    public static Criterion in(String propertyName, Collection<?> values) {
        return new InExpression(propertyName, values.toArray());
    }


    /**
     * not in
     * @param propertyName  属性名称
     * @param values  变量
     * @return Criterion
     */
    public static Criterion notIn(String propertyName, Collection<?> values) {
        return new NotInExpression(propertyName, values.toArray());
    }

    public static Criterion notIn(String propertyName, Object[] values) {
        return new NotInExpression(propertyName, values);
    }

    public static Criterion notIn(String propertyName, long[] values) {
        return new NotInExpression(propertyName, ArrayUtil.getLongObjectArray(values));
    }

    public static Criterion notIn(String propertyName, int[] values) {
        return new NotInExpression(propertyName, ArrayUtil.getIntegerArray(values));
    }

    public static Criterion notIn(String propertyName, float[] values) {
        return new NotInExpression(propertyName, ArrayUtil.getFloatObjectArray(values));
    }

    public static Criterion notIn(String propertyName, double[] values) {
        return new NotInExpression(propertyName, ArrayUtil.getDoubleObjectArray(values));
    }

    public static Criterion notIn(String propertyName, BigDecimal[] values) {
        return new NotInExpression(propertyName, values);
    }



    public static Criterion inSql(String propertyName, String values) {
        return new InSqlExpression(propertyName, values);
    }


    /**
     * Apply an "is null" constraint transfer the named property
     *
     * @param propertyName 字段
     * @return 是否为空
     */
    public static Criterion isNull(String propertyName) {
        return new IsNullExpression(propertyName);
    }


    /**
     * Apply an "is not null" constraint transfer the named property
     *
     * @param propertyName 字段
     * @return 不为空
     */
    public static Criterion isNotNull(String propertyName) {
        return new NotNullExpression(propertyName);
    }

    /**
     * Return the conjuction of two expressions
     *
     * @param lhs 表达式1
     * @param rhs 表达式2
     * @return 数据处理对象
     */
    public static Criterion and(Criterion lhs, Criterion rhs) {
        List<Criterion> list = new LinkedList<Criterion>();
        list.add(lhs);
        list.add(rhs);
        return new LogicalExpression(list, FilterLogicEnumType.AND);
    }

    /**
     * Return the disjuction of two expressions
     *
     * @param lhs 表达式1
     * @param rhs 表达式2
     * @return Criterion
     */
    public static LogicalExpression or(Criterion lhs, Criterion rhs) {
        List<Criterion> list = new LinkedList<>();
        list.add(lhs);
        list.add(rhs);
        return new LogicalExpression(list, FilterLogicEnumType.OR);
    }


    public static LogicalExpression or(List<Criterion> list) {
        return new LogicalExpression(list, FilterLogicEnumType.OR);
    }


    public static Criterion field(String field1, String compare, String field2) {
        return new FieldExpression(field1, compare, field2);
    }

    public static Criterion not(Criterion criterion) {
        return new NotExpression(criterion);
    }

}