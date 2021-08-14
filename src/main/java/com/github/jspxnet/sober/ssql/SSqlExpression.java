/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.ssql;


import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.sober.criteria.projection.Criterion;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.Order;
import com.github.jspxnet.sober.Criteria;
import java.util.Date;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-9
 * Time: 17:08:29
 * com.github.jspxnet.sober.ssql.SSqlExpression
 * eg: field:like['']
 * images:is[H]
 *
 * @author chenYuan
 */
public class SSqlExpression {
    private SSqlExpression() {

    }

    /**
     * @param typeClass 类型
     * @param value     值对象
     * @return Object 查询数据类型自动转换
     */
    public static Object getTypeObject(Class<?> typeClass, String value) {
        if (Integer.class == typeClass || "int".equals(typeClass.toString())) {
            return StringUtil.toInt(value);
        } else if (Float.class == typeClass || typeClass.toString().endsWith("java.lang.Float")) {
            return StringUtil.toFloat(value);
        } else if (Double.class == typeClass || typeClass.toString().endsWith("java.lang.Double")) {
            return StringUtil.toDouble(value);
        } else if (Long.class == typeClass || typeClass.toString().endsWith("java.lang.Long")) {
            return StringUtil.toLong(value);
        } else if (Date.class == typeClass || typeClass.toString().endsWith("java.util.Date")) {
            return StringUtil.getDate(value);
        } else if (Byte.class == typeClass || typeClass.toString().endsWith("java.lang.Byte")) {
            return new Byte(value);
        }
        return value;
    }

    /**
     * OR 条件表达式
     *
     * @param term          条件
     * @param methodsNameMa 方法
     * @return Criterion
     */
    public static Criterion getOrExpression(String term, Map<String, Class<?>> methodsNameMa) {
        if (term == null) {
            return null;
        }
        if (term.toUpperCase().startsWith("O") || term.toUpperCase().startsWith("OR")) {
            term = StringUtil.substringBeforeLast(StringUtil.substringAfter(term, "["), "]");
        }
        String termString = term;
        int p = termString.indexOf("/");
        if (p == -1) {
            return null;
        }
        String frist = termString.substring(0, p).trim();
        String second = StringUtil.substringAfter(term, "/").trim();
        Criterion criterion1;
        String field = StringUtil.substringBefore(frist, ":");
        if (frist.toUpperCase().startsWith("O") || frist.toUpperCase().startsWith("OR")) {
            criterion1 = getOrExpression(frist, methodsNameMa);
        } else {
            criterion1 = getExpression(frist, methodsNameMa.get(field));
        }

        field = StringUtil.substringBefore(second, ":");
        Criterion criterion2;
        if (second.toUpperCase().startsWith("O") || second.toUpperCase().startsWith("OR")) {
            criterion2 = getOrExpression(second, methodsNameMa);
        } else {
            criterion2 = getExpression(second, methodsNameMa.get(field));
        }
        return Expression.or(criterion1, criterion2);
    }

    /**
     * @param termData  一个条件表达式 例如 title:like[%txweb%]
     * @param classType 类型
     * @return Criterion 查询器
     */
    public static Criterion getExpression(String termData, Class<?> classType) {
        if (StringUtil.isNull(termData)) {
            return null;
        }
        String field = StringUtil.substringBefore(termData, ":");
        String term = termData.substring(field.length() + 1);
        String expression = StringUtil.substringBefore(term, "[");
        String value = StringUtil.substringBetween(term, "[", "]");
        if (StringUtil.isNull(field)) {
            return null;
        }
        if (StringUtil.isNull(expression)) {
            return null;
        }
        if (StringUtil.isNull(value)) {
            return null;
        }
        Object fValue = getTypeObject(classType, value);
        if ("is".equalsIgnoreCase(expression)) {
            if ("Null".equalsIgnoreCase(value) || "N".equalsIgnoreCase(value)) {
                return Expression.isNull(field);
            }
            if ("NotNull".equalsIgnoreCase(value) || "H".equalsIgnoreCase(value) || "Have".equalsIgnoreCase(value)) {
                return Expression.isNotNull(field);
            }
        } else if ("between".equalsIgnoreCase(expression) || "bt".equalsIgnoreCase(expression)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                char old = ' ';
                if (i > 0) {
                    old = c;
                }
                if (old != '\\' && c == ',') {
                    break;
                }
                sb.append(c);
            }
            String lastV = value.substring(sb.length() + 1);
            return Expression.between(field, sb.toString(), lastV);
        } else if (StringUtil.EQUAL.equalsIgnoreCase(expression) || "eq".equalsIgnoreCase(expression)) {
            return Expression.eq(field, fValue);
        } else if (">".equalsIgnoreCase(expression) || "gt".equalsIgnoreCase(expression)) {
            return Expression.gt(field, fValue);
        } else if (">=".equalsIgnoreCase(expression) || "ge".equalsIgnoreCase(expression)) {
            return Expression.ge(field, fValue);
        } else if ("<=".equalsIgnoreCase(expression) || "le".equalsIgnoreCase(expression)) {
            return Expression.le(field, fValue);
        } else if ("<".equalsIgnoreCase(expression) || "lt".equalsIgnoreCase(expression)) {
            return Expression.lt(field, fValue);
        } else if ("L".equalsIgnoreCase(expression) || "like".equalsIgnoreCase(expression)) {
            return Expression.like(field, fValue);
        } else if ("<>".equalsIgnoreCase(expression) || "ne".equalsIgnoreCase(expression)) {
            return Expression.ne(field, fValue);
        } else if ("I".equalsIgnoreCase(expression) || "in".equalsIgnoreCase(expression)) {
            String[] valueArray = null;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                char old = ' ';
                if (i > 0) {
                    old = c;
                }
                if (old != '\\' && c == ',') {
                    valueArray = ArrayUtil.add(valueArray, sb.toString());
                    sb.setLength(0);
                } else {
                    sb.append(c);
                }
            }
            if (ArrayUtil.isEmpty(valueArray)) {
                valueArray = new String[]{value};
            }
            Object[] arrayValue = new Object[valueArray.length];
            for (int i = 0; i < valueArray.length; i++) {
                arrayValue[i] = getTypeObject(classType, valueArray[i]);
            }
            return Expression.in(field, arrayValue);
        } else if ("NI".equalsIgnoreCase(expression) || "nin".equalsIgnoreCase(expression)) {
            String[] valueArray = null;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                char old = ' ';
                if (i > 0) {
                    old = c;
                }
                if (old != '\\' && c == ',') {
                    valueArray = ArrayUtil.add(valueArray, sb.toString());
                    sb.setLength(0);
                } else {
                    sb.append(c);
                }
            }
            if (ArrayUtil.isEmpty(valueArray)) {
                valueArray = new String[]{value};
            }
            Object[] arrayValue = new Object[valueArray.length];
            for (int i = 0; i < valueArray.length; i++) {
                arrayValue[i] = getTypeObject(classType, valueArray[i]);
            }
            return Expression.not(Expression.in(field, arrayValue));
        }
        return null;
    }

    /**
     * @param criteria  查询器
     * @param queryTerm 多个条件表达式
     * @return Criteria 查询器
     */
    public static Criteria getTermExpression(Criteria criteria, String queryTerm) {
        if (StringUtil.isNull(queryTerm) || queryTerm.length() < 3) {
            return criteria;
        }
        Map<String, Class<?>> methodsNameMap = ClassUtil.getMethodsNameAndType(criteria.getCriteriaClass(), ClassUtil.METHOD_NAME_SET);
        if (queryTerm.startsWith(StringUtil.SEMICOLON)) {
            queryTerm = queryTerm.substring(1);
        }
        /////////////单个的情况 begin
        queryTerm = queryTerm.trim();
        if (!queryTerm.contains(StringUtil.SEMICOLON)) {
            String field = StringUtil.substringBefore(queryTerm, ":");
            if (methodsNameMap.containsKey(field)) {
                Criterion criterionTmp = getExpression(queryTerm, methodsNameMap.get(field));
                if (criterionTmp != null) {
                    return criteria.add(criterionTmp);
                }
            } else if (queryTerm.toUpperCase().startsWith("O") || queryTerm.toUpperCase().startsWith("OR")) {
                Criterion criterionTmp = getOrExpression(queryTerm, methodsNameMap);
                if (criterionTmp != null) {
                    return criteria.add(criterionTmp);
                }
            }
            return criteria;
        }
        /////////////单个的情况 end

        ////////////////分解 条件 语法begin
        String[] termArray = StringUtil.split(queryTerm, StringUtil.SEMICOLON);
        if (termArray.length < 1) {
            return criteria;
        }
        for (String termData : termArray) {
            termData = termData.trim();
            String field = StringUtil.substringBefore(termData, ":");
            if (methodsNameMap.containsKey(field)) {
                Criterion criterion = getExpression(termData, methodsNameMap.get(field));
                if (criterion != null) {
                    criteria = criteria.add(criterion);
                }
            } else if (termData.toUpperCase().startsWith("O[") || termData.toUpperCase().startsWith("OR[")) {
                Criterion criterionTmp = getOrExpression(termData, methodsNameMap);
                if (criterionTmp != null) {
                    return criteria.add(criterionTmp);
                }
            }

        }
        ////////////////分解 条件 语法end
        return criteria;
    }

    /**
     * 排序
     * eg:sortType:D;sortDate:D;createDate:D
     *
     * @param criteria   查询器
     * @param sortString 排序
     * @return Criteria 查询器
     */
    public static Criteria getSortOrder(Criteria criteria, String sortString) {
        if (StringUtil.isNull(sortString)) {
            return criteria;
        }
        ////////////////分解 排序 语法begin
        String[] sortArray = StringUtil.split(sortString, StringUtil.SEMICOLON);
        for (String sortData : sortArray) {
            if (sortData == null) {
                continue;
            }
            String field = StringUtil.substringBefore(sortData, ":");
            field = field.trim();
            String sort = StringUtil.substringAfter(sortData, ":");
            sort = sort.trim();
            if (!StringUtil.isNull(field)) {
                if (StringUtil.isNull(sort) || "A".equalsIgnoreCase(sort) || "ASC".equalsIgnoreCase(sort) || "0".equalsIgnoreCase(sort)) {
                    criteria = criteria.addOrder(Order.asc(field));
                } else {
                    criteria = criteria.addOrder(Order.desc(field));
                }
            }
        }
        return criteria;
    }

    /**
     * 排序
     * eg:sortType:D;sortDate:D;createDate:D
     * 输出:sortType desc,sortDate desc,createDate desc
     * @param sortString 排序
     * @return Criteria 查询器
     */
    public static String getSortString(String sortString) {
        if (StringUtil.isNull(sortString)) {
            return null;
        }
        ////////////////分解 排序 语法begin
        StringBuilder sb = new StringBuilder();
        String[] sortArray = StringUtil.split(sortString, StringUtil.SEMICOLON);
        for (String sortData : sortArray) {
            if (sortData == null) {
                continue;
            }
            String field = StringUtil.substringBefore(sortData, ":");
            field = field.trim();
            String sort = StringUtil.substringAfter(sortData, ":");
            sort = sort.trim();
            if (!StringUtil.isNull(field)) {
                if (StringUtil.isNull(sort) || "A".equalsIgnoreCase(sort) || "ASC".equalsIgnoreCase(sort) || "0".equalsIgnoreCase(sort)) {
                    sb.append(Order.asc(field)).append(",");
                } else {
                    sb.append(Order.desc(field)).append(",");
                }
            }
        }
        if (sb.toString().endsWith(","))
        {
            sb.setLength(sb.length()-1);
        }
        return sb.toString().trim();
    }
}