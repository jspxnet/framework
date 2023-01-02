/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.dialect;

import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.DateUtil;

import java.io.*;
import java.sql.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-31
 * Time: 0:34:39
 */
public class SmallDBDialect extends Dialect {
    public SmallDBDialect() {

        put(SQL_CREATE_TABLE, "CREATE TABLE ${" + KEY_TABLE_NAME + "} \n(\n" +
                " <#list column=" + KEY_COLUMN_LIST + ">${column},\n</#list>" +
                " \nPRIMARY KEY  (${" + KEY_PRIMARY_KEY + "})\n)");
        put(SQL_CRITERIA_QUERY, "SELECT top ${" + SQL_RESULT_END_ROW + "} * FROM ${" + KEY_TABLE_NAME + "} <#if where=" + KEY_TERM + "!=''>WHERE ${" + KEY_TERM + "}</#if><#if where=" + KEY_FIELD_GROUPBY + "!=''> GROUP BY ${" + KEY_FIELD_GROUPBY + "}</#if><#if where=" + KEY_FIELD_ORDERBY + "!=''> ORDER BY ${" + KEY_FIELD_ORDERBY + "}</#if>");
        put(String.class.getName(), "${" + COLUMN_NAME + "} <#if where=" + COLUMN_LENGTH + "&gt;255>text<#else>varchar(${" + COLUMN_LENGTH + "})</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default '${" + COLUMN_DEFAULT + "}'");
        put(Boolean.class.getName(), "${" + COLUMN_NAME + "} smallint <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default <#if where=" + COLUMN_DEFAULT + "==''>0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");
        put(boolean.class.getName(), "${" + COLUMN_NAME + "} smallint <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default <#if where=" + COLUMN_DEFAULT + "==''>0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");
        put(Integer.class.getName(), "${" + COLUMN_NAME + "} <#if where=" + KEY_FIELD_SERIAL + ">COUNTER<#else>integer</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=" + COLUMN_DEFAULT + "==''>'0'<#else>'${" + COLUMN_DEFAULT + "}'</#else></#if></#else></#if>");
        put("int", "${" + COLUMN_NAME + "} <#if where=" + KEY_FIELD_SERIAL + ">COUNTER<#else>integer</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=" + COLUMN_DEFAULT + "==''>'0'<#else>'${" + COLUMN_DEFAULT + "}'</#else></#if></#else></#if>");
        put(Long.class.getName(), "${" + COLUMN_NAME + "} <#if where=" + KEY_FIELD_SERIAL + ">COUNTER<#else>bigint</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else>  default <#if where=" + COLUMN_DEFAULT + "==''>'0'<#else>'${" + COLUMN_DEFAULT + "}'</#else></#if></#else></#if>");
        put("long", "${" + COLUMN_NAME + "} <#if where=" + KEY_FIELD_SERIAL + ">COUNTER<#else>bigint</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else>  default <#if where=" + COLUMN_DEFAULT + "==''>'0'<#else>'${" + COLUMN_DEFAULT + "}'</#else></#if></#else></#if>");
        put(Double.class.getName(), "${" + COLUMN_NAME + "} double <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default <#if where=" + COLUMN_DEFAULT + "==''>'0'<#else>'${" + COLUMN_DEFAULT + "}'</#else></#if>");
        put("double", "${" + COLUMN_NAME + "} double <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default <#if where=" + COLUMN_DEFAULT + "==''>'0'<#else>'${" + COLUMN_DEFAULT + "}'</#else></#if>");
        put(Float.class.getName(), "${" + COLUMN_NAME + "} float <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default <#if where=" + COLUMN_DEFAULT + "==''>'0'<#else>'${" + COLUMN_DEFAULT + "}'</#else></#if>");
        put("float", "${" + COLUMN_NAME + "} float <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default <#if where=" + COLUMN_DEFAULT + "==''>'0'<#else>'${" + COLUMN_DEFAULT + "}'</#else></#if>");
        put(Date.class.getName(), "${" + COLUMN_NAME + "} datetime NOT NULL default now()");
        put(byte[].class.getName(), "${" + COLUMN_NAME + "} LONGvarbinary");
        put(InputStream.class.getName(), "${" + COLUMN_NAME + "} LONGvarbinary");
        put(char.class.getName(), "${" + COLUMN_NAME + "} char(2) NOT NULL default ''");
        put(SQL_DROP_TABLE, "DROP TABLE ${" + KEY_TABLE_NAME + "}");
        put(FUN_TABLE_EXISTS, "exists table ${" + KEY_TABLE_NAME + "}");
    }

    @Override
    public String getFieldType(SoberColumn soberColumn) {

        if (ClassUtil.isNumberType(soberColumn.getClassType()))
        {
            if (soberColumn.getClassType()==int.class || soberColumn.getClassType()==Integer.class)
            {

                return "integer";
            }

            if (soberColumn.getClassType()==long.class || soberColumn.getClassType()==Long.class)
            {
                return "bigint";
            }

            if (soberColumn.getClassType()==float.class || soberColumn.getClassType()==Float.class)
            {

                return "float";
            }
            if (soberColumn.getClassType()==double.class || soberColumn.getClassType()==Double.class)
            {

                return "double";
            }
        }
        if (soberColumn.getClassType()==boolean.class || soberColumn.getClassType()==Boolean.class)
        {
            return "smallint";
        }
        if (soberColumn.getClassType()==String.class)
        {
            if (soberColumn.getLength()<512)
            {
                return "varchar("+soberColumn.getLength()+")";
            }
            return "text";
        }

        if (soberColumn.getClassType()==Date.class)
        {
            return "datetime";
        }

        if (soberColumn.getClassType()==Time.class)
        {
            return "datetime";
        }

        if (soberColumn.getClassType()==InputStream.class)
        {
            return "LONGvarbinary";
        }

        if (soberColumn.getClassType()==char.class)
        {
            return "char("+soberColumn.getLength()+")";
        }
        return "varchar(512)";
    }
    /**
     * @param rs    数据对象
     * @param index 索引
     * @return 返回查询结果
     * @throws java.sql.SQLException 异常
     */
    @Override
    public Object getResultSetValue(ResultSet rs, int index) throws SQLException {
        if (rs == null || index <= 0) {
            return null;
        }
        String typeName = rs.getMetaData().getColumnTypeName(index);
        int colSize = rs.getMetaData().getColumnDisplaySize(index);

        //短断整型
        if (("int".equalsIgnoreCase(typeName) && colSize < 4) || "short".equalsIgnoreCase(typeName) || "smallint".equalsIgnoreCase(typeName) || "int2".equalsIgnoreCase(typeName) || "tinyint".equalsIgnoreCase(typeName) || ("fixed".equalsIgnoreCase(typeName) && colSize < 4)) {
            return rs.getShort(index);
        }
        //////////整型
        if ("int".equalsIgnoreCase(typeName) || "integer".equalsIgnoreCase(typeName) || "int4".equalsIgnoreCase(typeName) || ("number".equalsIgnoreCase(typeName) && colSize < 8) || ("fixed".equalsIgnoreCase(typeName) && colSize < 19)) {
            return rs.getInt(index);
        }

        ///////长整型
        if ("long".equalsIgnoreCase(typeName) || "bigint".equalsIgnoreCase(typeName) || "int8".equalsIgnoreCase(typeName) || ("fixed".equalsIgnoreCase(typeName) && colSize > 18)) {
            return rs.getLong(index);
        }

        ///////单精度
        if ("money".equalsIgnoreCase(typeName) || "float".equalsIgnoreCase(typeName) || "real".equalsIgnoreCase(typeName) || "binary_float".equalsIgnoreCase(typeName)) {
            return rs.getFloat(index);
        }
        ///////大数值
        if ("decimal".equalsIgnoreCase(typeName)) {
            return rs.getBigDecimal(index);
        }
        ///////双精度
        if ("double".equalsIgnoreCase(typeName) || "double precision".equalsIgnoreCase(typeName) || "binary_double".equalsIgnoreCase(typeName)) {
            return rs.getDouble(index);
        }

        ///////日期
        if ("date".equalsIgnoreCase(typeName)) {
            java.sql.Date t = rs.getDate(index);
            if (t == null) {
                return null;
            }
            return new java.util.Date(t.getTime());
        }

        ///////日期时间
        if ("timestamp".equalsIgnoreCase(typeName) || "datetime".equalsIgnoreCase(typeName)) {
            Timestamp t = rs.getTimestamp(index);
            if (t == null) {
                return null;
            }
            return new java.util.Date(t.getTime());
        }

        ////////////时间
        if ("time".equalsIgnoreCase(typeName)) {
            return rs.getTime(index);
        }

        ///////短字符串
        if ("char".equalsIgnoreCase(typeName) || "nvarchar".equalsIgnoreCase(typeName) || "varchar".equalsIgnoreCase(typeName) || "varchar2".equalsIgnoreCase(typeName) || "tinyblob".equalsIgnoreCase(typeName)) {
            return rs.getString(index);
        }

        ////////////大文本类型
        if ("CLOB".equalsIgnoreCase(typeName) || "mediumtext".equalsIgnoreCase(typeName) || " long varchar".equalsIgnoreCase(typeName)
                || "ntext".equalsIgnoreCase(typeName) || "text".equalsIgnoreCase(typeName) || "long raw".equalsIgnoreCase(typeName)) {
            Clob clob = rs.getClob(index);
            Reader bodyReader = clob.getCharacterStream();
            StringWriter out = new StringWriter(255);
            try {
                char[] buf = new char[256];
                int i;
                while ((i = bodyReader.read(buf)) != -1) {
                    out.write(buf, 0, i);
                }
                out.close();
                bodyReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return out.toString();
        }


        ///////二进制类型 文件类型
        if ("image".equalsIgnoreCase(typeName) || "blob".equalsIgnoreCase(typeName) || "LONGvarbinary".equalsIgnoreCase(typeName)
                || "longblob".equalsIgnoreCase(typeName) || "dbclob".equalsIgnoreCase(typeName)
                || "varbinary".equalsIgnoreCase(typeName) || "binary".equalsIgnoreCase(typeName) || "long byte".equalsIgnoreCase(typeName)) {
            return new ByteArrayInputStream(rs.getBytes(index));
        }
        return rs.getObject(index);
    }


    @Override
    public String getLimitString(String sql, int begin, int end, TableModels soberTable) {
        return sql;
    }

    @Override
    public boolean supportsSequenceName() {
        return false;
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean supportsConcurReadOnly() {
        return false;
    }

    @Override
    public boolean commentPatch() {
        return false;
    }
}