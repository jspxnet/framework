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
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.criteria.projection.Criterion;
import com.github.jspxnet.sober.util.JdbcUtil;

import com.github.jspxnet.utils.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: chenYuan
 * date: 12-4-9
 * Time: 下午6:09
 * 符合条件查询方式
 * 模糊查询功能，这里有几个特殊的处理方式
 * <pre>{@code
 *
 * 1.默认查询使用like方式，如果数据里边没有通配符号,默认在尾部加入%
 * 2.如果是日期等类型，存在区间查询的，使用 & 符号来切分 例如  date BETWEEN data1 and data2
 * 3.如果字段名称为 spelling，表示快速搜索字段，里边保存的是全拼，首字母大写区分， 将支持全拼和简拼查询 查询语句 例如:SH    spelling like 'S%H%'
 * 4.如果字段名称为 tags，表示为搜索关键字，  tags like '%tag %'
 *
 * }</pre>
 */
public class FindExpression implements Criterion {

    final static private String find_wildcard = "%";
    final static private String field_spelling = "spelling"; //简拼和全拼搜索
    final static private String field_tags = "tags"; //搜索关键字
    final static private String field_title = "title"; //支持空格 or 搜索
    final static private String field_name = "name"; //支持空格 or 搜索
    //final static private String field_organize = "organize"; //支持空格 or 搜索

    private String[] propertyName;
    private Object[] value;
    //OR 方式
    private boolean or = false;
    //模糊方式,默认采用模糊方式
    //数字采用精确查询, 有特殊符号的采用特殊符号表述查询,不能确定的采用此配置来分配
    private boolean vague = true;
    //包含特殊字符的符号
    public final static char[] incertitudeSQLChars = {
            '\\', '\'', '\"', '&', '#', '!', '=', '<', '>'
    };

    /**
     * @param str 字符串
     * @return 安全过滤
     */
    public static String safeFilter(String str) {
        if (str == null) {
            return StringUtil.empty;
        }
        for (char c : incertitudeSQLChars) {
            str = StringUtil.replace(str, c + "", "");
        }
        return str;
    }


    public FindExpression(String[] propertyName, Object[] value) {
        initExpression(propertyName, value, false, true);
    }

    public FindExpression(String[] propertyName, Object[] value, boolean or) {
        initExpression(propertyName, value, or, true);
    }

    public FindExpression(String[] propertyName, Object[] value, boolean or, boolean vague) {
        initExpression(propertyName, value, or, vague);
    }

    private void initExpression(String[] propertyName, Object[] value, boolean or, boolean vague) {
        this.or = or;
        this.vague = vague;
        //计算交叉关系,一般情况为有字段，却没用值
        if (ArrayUtil.isEmpty(propertyName) || ArrayUtil.isEmpty(value)) {
            this.propertyName = null;
            this.value = null;
        } else {
            int min = NumberUtil.getMin(new int[]{propertyName.length, value.length});
            for (int i = 0; i < min; i++) {
                if (StringUtil.isNull(propertyName[i])) {
                    continue;
                }
                if (value[i] == null || !StringUtil.hasLength(StringUtil.trim((String) value[i]))) {
                    continue;
                }
                this.propertyName = ArrayUtil.add(this.propertyName, propertyName[i]);
                this.value = ArrayUtil.add(this.value, StringUtil.trim(safeFilter((String) value[i])));
            }
        }
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        if (ArrayUtil.isEmpty(propertyName) || ArrayUtil.isEmpty(value)) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        int min = NumberUtil.getMin(new int[]{propertyName.length, value.length});
        for (int i = 0; i < min; i++) {
            String field = propertyName[i];
            if (!soberTable.containsField(field)) {
                continue;
            }
            String find = StringUtil.trim(((String) value[i]));
            SoberColumn column = soberTable.getColumn(field);
            if (find.contains(find_wildcard) || find.contains("_")) {
                sb.append(field);
                if (or) {
                    sb.append(" LIKE ? OR ");
                } else {
                    sb.append(" LIKE ? AND ");
                }
            } else if (field_title.equals(field) || field_name.equals(field) ) {
                String[] findArray = StringUtil.split(StringUtil.trim(StringUtil.replace(find, "  ", " ")), " ");
                sb.append("(");
                if (soberTable.containsField(field_tags)) {
                    sb.append("(");
                }
                for (int j = 0; j < findArray.length && j < 3; j++) {
                    sb.append(field).append(" LIKE ? OR ");
                }
                if (sb.toString().endsWith("OR ")) {
                    sb.setLength(sb.length() - 3);
                }
                sb.append(")");
                if (sb.toString().endsWith("()")) {
                    sb.setLength(sb.length() - 3);
                }

                if (soberTable.containsField(field_tags)) {
                    sb.append("OR (");
                    for (int j = 0; j < findArray.length && j < 3; j++) {
                        sb.append(field_tags).append(" LIKE ? OR ");
                    }
                    if (sb.toString().endsWith("OR ")) {
                        sb.setLength(sb.length() - 3);
                    }
                    sb.append(") ");
                    if (sb.toString().endsWith("() ")) {
                        sb.setLength(sb.length() - 3);
                    }
                    sb.append(")");
                }

                if (sb.toString().endsWith("() ")) {
                    sb.setLength(sb.length() - 3);
                }
                if (sb.toString().endsWith("()")) {
                    sb.setLength(sb.length() - 2);
                }

                if (or) {
                    sb.append(" OR ");
                } else {
                    sb.append(" AND ");
                }

            } else if (field_tags.equals(field)) {   // && find.contains(" ")
                String[] findArray = StringUtil.split(StringUtil.trim(StringUtil.replace(find, "  ", " ")), " ");
                sb.append("(");
                for (int j = 0; j < findArray.length && j < 3; j++) {
                    sb.append(field).append(" LIKE ? OR ");
                }
                if (sb.toString().endsWith("OR ")) {
                    sb.setLength(sb.length() - 3);
                }
                sb.append(") ");
                if (sb.toString().endsWith("() ")) {
                    sb.setLength(sb.length() - 3);
                }
                if (soberTable.containsField(field_title)) {
                    sb.append("OR (");
                    for (int j = 0; j < findArray.length && j < 3; j++) {
                        sb.append(field_title).append(" LIKE ? OR ");
                    }
                    if (sb.toString().endsWith("OR ")) {
                        sb.setLength(sb.length() - 3);
                    }
                    sb.append(") ");
                    if (sb.toString().endsWith("() ")) {
                        sb.setLength(sb.length() - 3);
                    }
                } else if (soberTable.containsField(field_name)) {
                    sb.append("OR (");
                    for (int j = 0; j < findArray.length && j < 3; j++) {
                        sb.append(field_name).append(" LIKE ? OR ");
                    }
                    if (sb.toString().endsWith("OR ")) {
                        sb.setLength(sb.length() - 3);
                    }
                    sb.append(")");
                    if (sb.toString().endsWith("() ")) {
                        sb.setLength(sb.length() - 3);
                    }
                }
                if (sb.toString().endsWith("() ")) {
                    sb.setLength(sb.length() - 3);
                }
                if (or) {
                    sb.append(" OR ");
                } else {
                    sb.append(" AND ");
                }

            } else if (field_spelling.equalsIgnoreCase(field)) {
                //拼音搜索
                sb.append(field_spelling);
                if (or) {
                    sb.append(" LIKE ? OR ");
                } else {
                    sb.append(" LIKE ? AND ");
                }
            } else if (find.contains("~") && !find.startsWith("~") && !find.endsWith("~")) {
                sb.append("(").append(field);
                if (or) {
                    sb.append(" BETWEEN ? AND ?) OR ");
                } else {
                    sb.append(" BETWEEN ? AND ?) AND ");
                }
            } else if (JdbcUtil.isQuote(soberTable, field)) {
                sb.append(field);
                if (find.contains(find_wildcard) || find.contains("_")) {
                    if (or) {
                        sb.append(" LIKE ? OR ");
                    } else {
                        sb.append(" LIKE ? AND ");
                    }
                } else {
                    if (vague) {  //模糊方式
                        if (or) {
                            sb.append(" LIKE ? OR ");
                        } else {
                            sb.append(" LIKE ? AND ");
                        }
                    } else {
                        if (or) {
                            sb.append("=? OR ");
                        } else {
                            sb.append("=? AND ");
                        }
                    }
                }
            } else {
                //不能确定的数据
                if (ClassUtil.isNumberType(column.getClassType()) && !StringUtil.isStandardNumber(find)) {
                    //数字字段
                    sb.append(1);
                    if (or) {
                        sb.append("=1 OR ");
                    } else {
                        sb.append("=1 AND ");
                    }
                } else {
                    sb.append(field);
                    if (or) {
                        sb.append("=? OR ");
                    } else {
                        sb.append("=? AND ");
                    }
                }
            }
        }

        if (or && sb.toString().endsWith("OR ")) {
            sb.setLength(sb.length() - 3);
        }
        if (!or && sb.toString().endsWith("AND ")) {
            sb.setLength(sb.length() - 4);
        }

        if (sb.toString().length() > 4) {
            return "(" + sb.toString().trim() + ")";
        }
        return sb.toString();
    }

    @Override
    public String[] getFields() {
        return propertyName;
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {

        if (ArrayUtil.isEmpty(propertyName) || ArrayUtil.isEmpty(value)) {
            return null;
        }
        Object[] result = null;
        int min = NumberUtil.getMin(new int[]{propertyName.length, value.length});
        for (int i = 0; i < min; i++) {
            String field = propertyName[i];
            if (!soberTable.containsField(field)) {
                continue;
            }
            String find = ((String) value[i]);
            SoberColumn column = soberTable.getColumn(field);

            if (find.contains(find_wildcard) || find.contains("_")) {
                result = ArrayUtil.add(result, value[i]);
            } else if (field_title.equalsIgnoreCase(field) || field_name.equalsIgnoreCase(field)) {
                //标题方式支持 or
                String[] findArray = StringUtil.split(StringUtil.trim(StringUtil.replace(find, "  ", " ")), " ");
                for (int j = 0; j < findArray.length && j < 3; j++) {
                    StringBuilder value = new StringBuilder();
                    result = ArrayUtil.add(result, value.append(find_wildcard).append(findArray[j]).append(find_wildcard).toString());
                    value.setLength(0);
                }
                if (soberTable.containsField(field_tags)) {
                    for (int j = 0; j < findArray.length && j < 3; j++) {
                        StringBuilder value = new StringBuilder();
                        result = ArrayUtil.add(result, value.append(find_wildcard).append(findArray[j]).append(find_wildcard).toString());
                        value.setLength(0);
                    }
                }
            } else if (field_tags.equalsIgnoreCase(field)) {
                //关键字搜索
                String[] findArray = StringUtil.split(StringUtil.trim(StringUtil.replace(find, "  ", " ")), " ");
                for (int j = 0; j < findArray.length && j < 3; j++) {
                    StringBuilder value = new StringBuilder();
                    result = ArrayUtil.add(result, value.append(find_wildcard).append(findArray[j]).append(find_wildcard).toString());
                    value.setLength(0);
                }
                if (soberTable.containsField(field_title)) {
                    for (int j = 0; j < findArray.length && j < 3; j++) {
                        StringBuilder value = new StringBuilder();
                        result = ArrayUtil.add(result, value.append(find_wildcard).append(findArray[j]).append(find_wildcard).toString());
                        value.setLength(0);
                    }
                } else if (soberTable.containsField(field_name)) {
                    for (int j = 0; j < findArray.length && j < 3; j++) {
                        StringBuilder value = new StringBuilder();
                        result = ArrayUtil.add(result, value.append(find_wildcard).append(findArray[j]).append(find_wildcard).toString());
                        value.setLength(0);
                    }
                }
            } else if (field_spelling.equalsIgnoreCase(field)) {
                //拼音搜索
                if (find.length() < 4) {
                    StringBuilder value = new StringBuilder();
                    for (int p = 0; p < find.length(); p++) {
                        value.append(("" + find.charAt(p)).toUpperCase()).append(find_wildcard);
                    }
                    result = ArrayUtil.add(result, value.toString());
                } else {
                    result = ArrayUtil.add(result, find + find_wildcard);
                }
            } else if (find.contains("~") && !find.startsWith("~") && !find.endsWith("~")) {
                String value1 = StringUtil.substringBefore(find, "~");
                String value2 = StringUtil.substringAfter(find, "~");
                if (column.getClassType().equals(java.sql.Date.class) || column.getClassType().equals(Date.class)) {
                    try {
                        result = ArrayUtil.add(result, StringUtil.getDate(value1));
                        result = ArrayUtil.add(result, StringUtil.getDate(value2));
                    } catch (Exception e) {
                        result = ArrayUtil.add(result, DateUtil.empty);
                        result = ArrayUtil.add(result, new Date());
                    }
                } else if (column.getClassType().equals(long.class) || column.getClassType().equals(Long.class)) {
                    result = ArrayUtil.add(result, ObjectUtil.toLong(value1));
                    result = ArrayUtil.add(result, ObjectUtil.toLong(value2));
                } else if (column.getClassType().equals(int.class) || column.getClassType().equals(Integer.class)) {
                    result = ArrayUtil.add(result, ObjectUtil.toInt(value1));
                    result = ArrayUtil.add(result, ObjectUtil.toInt(value2));
                } else if (column.getClassType().equals(float.class) || column.getClassType().equals(Float.class)) {
                    result = ArrayUtil.add(result, ObjectUtil.toFloat(value1));
                    result = ArrayUtil.add(result, ObjectUtil.toFloat(value2));
                } else if (column.getClassType().equals(double.class) || column.getClassType().equals(Double.class)) {
                    result = ArrayUtil.add(result, ObjectUtil.toDouble(value1));
                    result = ArrayUtil.add(result, ObjectUtil.toDouble(value2));
                } else if (column.getClassType().equals(short.class) || column.getClassType().equals(Short.class)) {
                    result = ArrayUtil.add(result, Short.valueOf(value1));
                    result = ArrayUtil.add(result, Short.valueOf(value2));
                } else {
                    result = ArrayUtil.add(result, value1);
                    result = ArrayUtil.add(result, value2);
                }
            } else if (JdbcUtil.isQuote(soberTable, field)) {
                if (find.contains(find_wildcard) || find.contains("_")) {
                    result = ArrayUtil.add(result, find);
                } else {
                    if (vague) {   //模糊查询，如果没有%加入%
                        result = ArrayUtil.add(result, find_wildcard + find + find_wildcard);
                    } else {
                        result = ArrayUtil.add(result, find);
                    }
                }
            } else {
                if (column.getClassType().equals(boolean.class) || column.getClassType().equals(Boolean.class)) {
                    result = ArrayUtil.add(result, ObjectUtil.toBoolean(find) ? 1 : 0);
                } else if (ClassUtil.isNumberType(column.getClassType())) {
                    if (StringUtil.isStandardNumber(find)) {
                        if (column.getClassType() == long.class || column.getClassType() == Long.class) {
                            result = ArrayUtil.add(result, Long.valueOf(find));
                        } else if (column.getClassType() == int.class || column.getClassType() == Integer.class) {
                            result = ArrayUtil.add(result, Integer.valueOf(find));
                        } else if (column.getClassType() == float.class || column.getClassType() == Float.class) {
                            result = ArrayUtil.add(result, Float.valueOf(find));
                        } else if (column.getClassType() == double.class || column.getClassType() == Double.class) {
                            result = ArrayUtil.add(result, Double.valueOf(find));
                        } else if (column.getClassType() == short.class || column.getClassType() == Short.class) {
                            result = ArrayUtil.add(result, Short.valueOf(find));
                        } else {
                            result = ArrayUtil.add(result, find);
                        }
                    } else if (ClassUtil.isNumberType(column.getClassType()) && !StringUtil.isStandardNumber(find)) {
                        //数字字段
                        result = ArrayUtil.add(result, ObjectUtil.toInt(find));
                    } else {
                        result = ArrayUtil.add(result, find);
                    }
                } else {
                    result = ArrayUtil.add(result, find);
                }
            }
        }
        return result;
    }

    /**
     * @return 转换为SQL字符串
     */
    @Override
    public String toString() {
        if (propertyName == null || value == null) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        int min = NumberUtil.getMin(new int[]{propertyName.length, value.length});
        for (int i = 0; i < min; i++) {
            String find = ((String) value[i]);
            if (find.contains(find_wildcard) || find.contains("_") || find.contains("~")) {
                sb.append(propertyName[i]).append(" LIKE ").append(StringUtil.quote(find, false));
                if (or) {
                    sb.append(" OR ");
                } else {
                    sb.append(" AND ");
                }
            } else
            if (!find.contains(find_wildcard) && !find.contains("_") && !find.contains("~")) {
                sb.append(propertyName[i]).append(" LIKE ").append(StringUtil.quote(find_wildcard + find + find_wildcard, false));

                if (or) {
                    sb.append(" OR ");
                } else {
                    sb.append(" AND ");
                }
            } else if (!find.startsWith("~") && !find.endsWith("~") && find.contains("~")) {
                String value1 = StringUtil.substringBefore(find, "~");
                String value2 = StringUtil.substringAfter(find, "~");
                sb.append(propertyName[i]).append(" BETWEEN ").append(StringUtil.quote(value1, false)).append(" AND ").append(StringUtil.quote(value2, false));
                if (or) {
                    sb.append(" OR ");
                } else {
                    sb.append(" AND ");
                }
            } else {
                if (ClassUtil.isNumberType(value[i].getClass())) {
                    sb.append(propertyName[i]).append("=").append(find);
                    if (or) {
                        sb.append(" OR ");
                    } else {
                        sb.append(" AND ");
                    }
                } else {
                    sb.append(propertyName[i]).append(" LIKE ").append(StringUtil.quote(find + find_wildcard, false));
                    if (or) {
                        sb.append(" OR ");
                    } else {
                        sb.append(" AND ");
                    }
                }
            }
        }
        if (or && sb.toString().endsWith("OR ")) {
            sb.setLength(sb.length() - 3);
        }
        if (!or && sb.toString().endsWith("AND ")) {
            sb.setLength(sb.length() - 4);
        }
        return sb.toString();
    }

    @Override
    public String termString() {
        return toString();
    }

    public static void main(String[] args) {
        FindExpression findExpression = new FindExpression(new String[]{"f1","f2"},new String[]{"%aaa","bbb"});
        System.out.println(findExpression.toString());
    }
}