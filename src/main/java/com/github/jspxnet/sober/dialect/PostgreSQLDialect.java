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

import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.utils.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.utils.StringUtil;
import java.io.*;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-10
 * Time: 11:21:54
 * Postgre数据库 SQL匹配
 */
@Slf4j
public class PostgreSQLDialect extends Dialect {


    public PostgreSQLDialect() {
        put(SQL_CREATE_TABLE, "CREATE TABLE IF NOT EXISTS ${" + KEY_TABLE_NAME + "} \n(\n" +
                " <#list column=" + KEY_COLUMN_LIST + ">${column},\n</#list>" +
                " \nCONSTRAINT \"${" + KEY_TABLE_NAME + "}_key\" PRIMARY KEY  (${" + KEY_PRIMARY_KEY + "})\n)");


        //oracle 和 pgsql 设置注释方式begin
        put(SQL_COMMENT, "COMMENT ON COLUMN ${" + KEY_TABLE_NAME + "}.${" + COLUMN_NAME + "} IS '${" + COLUMN_CAPTION + "}'");
        put(SQL_TABLE_COMMENT, "COMMENT ON TABLE ${" + KEY_TABLE_NAME + "} IS '${" + SQL_TABLE_COMMENT + "}'");
        //oracle 和 pgsql 设置注释方式end

        put(Boolean.class.getName(), "${" + COLUMN_NAME + "} boolean <#if where=\"" + COLUMN_NOT_NULL + "\">NOT NULL</#if> <#if where=" + COLUMN_DEFAULT + ">default ${" + COLUMN_DEFAULT + ".toBoolean()}</#if>");
        put(boolean.class.getName(), "${" + COLUMN_NAME + "} boolean <#if where=\"" + COLUMN_NOT_NULL + "\">NOT NULL</#if> <#if where=" + COLUMN_DEFAULT + ">default ${" + COLUMN_DEFAULT + ".toBoolean()}</#if>");

        put(String.class.getName(), "${" + COLUMN_NAME + "} <#if where=\"" + COLUMN_LENGTH + "&lt;255\">varchar(${" + COLUMN_LENGTH + "})<#else>text</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=" + COLUMN_DEFAULT + ">default '${" + COLUMN_DEFAULT + "}'</#if>");

        put(Integer.class.getName(), "${" + COLUMN_NAME + "} <#if where=\"" + KEY_FIELD_SERIAL + "\">SERIAL<#else>integer</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=!" + KEY_FIELD_SERIAL + " >default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#if>");
        put("int", "${" + COLUMN_NAME + "} <#if where=\"" + KEY_FIELD_SERIAL + "\">SERIAL<#else>integer</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=!" + KEY_FIELD_SERIAL + " >default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#if>");

        put(Long.class.getName(), "${" + COLUMN_NAME + "} <#if where=\"" + KEY_FIELD_SERIAL + "\">BIGSERIAL<#else>BIGINT</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=!" + KEY_FIELD_SERIAL + " >default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#if>");
        put("long", "${" + COLUMN_NAME + "} <#if where=\"" + KEY_FIELD_SERIAL + "\">BIGSERIAL<#else>BIGINT</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=!" + KEY_FIELD_SERIAL + " >default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#if>");

        put(Double.class.getName(), "${" + COLUMN_NAME + "} double precision <#if where=\"" + COLUMN_NOT_NULL + "\">NOT NULL</#if> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");
        put("double", "${" + COLUMN_NAME + "} double precision <#if where=\"" + COLUMN_NOT_NULL + "\">NOT NULL</#if> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");

        put(Float.class.getName(), "${" + COLUMN_NAME + "} real <#if where=\"" + COLUMN_NOT_NULL + "\">NOT NULL</#if> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");
        put("float", "${" + COLUMN_NAME + "} real <#if where=\"" + COLUMN_NOT_NULL + "\">NOT NULL</#if> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");

        put(BigDecimal.class.getName(), "${" + COLUMN_NAME + "} double precision <#if where=\"" + COLUMN_NOT_NULL + "\">NOT NULL</#if> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");
        put("BigDecimal", "${" + COLUMN_NAME + "} double precision <#if where=\"" + COLUMN_NOT_NULL + "\">NOT NULL</#if> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");

        put(Date.class.getName(), "${" + COLUMN_NAME + "} timestamp <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default now()");

        put(Time.class.getName(), "${" + COLUMN_NAME + "} time <#if where=" + COLUMN_NOT_NULL + ">NOT NULL DEFAULT '${" + COLUMN_DEFAULT + "}'</#if>");

        put(byte[].class.getName(), "${" + COLUMN_NAME + "} bytea");
        put(InputStream.class.getName(), "${" + COLUMN_NAME + "} bytea");
        put(char.class.getName(), "${" + COLUMN_NAME + "} char(2) NOT NULL default ''");

        put(SQL_DROP_TABLE, "DROP TABLE ${" + KEY_TABLE_NAME + "}");

        put(FUN_TABLE_EXISTS, "SELECT (count(*)>0) FROM pg_class WHERE relname ILIKE '${" + KEY_TABLE_NAME + "}'");

        //修改系列开始
        put(ALTER_SEQUENCE_RESTART, "ALTER SEQUENCE ${" + SERIAL_NAME + "} RESTART WITH ${" + KEY_SEQUENCE_RESTART + "}");

        put(SQL_TABLE_NAMES, "SELECT tablename FROM pg_tables WHERE tablename NOT LIKE 'pg%' AND tablename NOT LIKE 'sql_%' ORDER   BY tablename");

        put(DATABASE_SIZE, "SELECT pg_database_size('${" + KEY_TABLE_NAME + "}'");

        put(SEQUENCE_NAME, "SELECT adsrc FROM pg_attrdef WHERE adsrc like 'nextval(_${" + KEY_TABLE_NAME + "}_${" + KEY_PRIMARY_KEY + "}_seq%::regclass)'");

        put(SQL_CREATE_TABLE_INDEX, "CREATE <#if where=" + KEY_IS_UNIQUE + ">unique</#if> INDEX IF NOT EXISTS ${"+KEY_INDEX_NAME+"} ON ${" + KEY_TABLE_NAME + "} (${" + KEY_INDEX_FIELD +"})");
    }

    @Override
    public String getFieldType(SoberColumn soberColumn) {

        if (ClassUtil.isNumberType(soberColumn.getClassType()))
        {
            if (soberColumn.getClassType()==int.class || soberColumn.getClassType()==Integer.class)
            {
                if (soberColumn.getLength()<3)
                {
                    return "tinyint("+soberColumn.getLength()+")";
                }
                return "integer";
            }

            if (soberColumn.getClassType()==long.class || soberColumn.getClassType()==Long.class)
            {
                if (soberColumn.getLength()>8)
                {
                    return "bigint("+soberColumn.getLength()+")";
                }
                return "bigint(16)";
            }

            if (soberColumn.getClassType()==float.class || soberColumn.getClassType()==Float.class)
            {
                return "real";
            }

            if (soberColumn.getClassType()==double.class || soberColumn.getClassType()==Double.class)
            {
                return "double precision";
            }
        }
        if (soberColumn.getClassType()==boolean.class || soberColumn.getClassType()==Boolean.class)
        {
            return "boolean";
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
            return "timestamp";
        }

        if (soberColumn.getClassType()==Time.class)
        {
            return "time";
        }

        if (soberColumn.getClassType()==InputStream.class)
        {
            return "bytea";
        }

        if (soberColumn.getClassType()==char.class)
        {
            return "char("+soberColumn.getLength()+")";
        }
        return "varchar(512)";
    }

    @Override
    public String getLimitString(String sql, int begin, int end, TableModels soberTable) {
        int start = end - begin;
        if (start < 0) {
            start = 0;
        }
        return sql + " limit " + start + " offset " + begin;
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
        String typeName = rs.getMetaData().getColumnTypeName(index).toLowerCase();
        int colSize = rs.getMetaData().getColumnDisplaySize(index);
        //短断整型
        if ((typeName.contains("int") && colSize < 4) || "short".equals(typeName) || "smallint".equals(typeName) || "int2".equals(typeName) || "tinyint".equalsIgnoreCase(typeName) || ("fixed".equals(typeName) && colSize < 4)) {
            return rs.getShort(index);
        }
        //////////整型
        if ("integer".equals(typeName) || "serial".equals(typeName) || (typeName.contains("int4") || "int".equals(typeName) || (typeName.contains("number")) && colSize < 12) || ("fixed".equalsIgnoreCase(typeName) && colSize < 12)) {
            return rs.getInt(index);
        }
        ///////长整型
        if ("bigserial".equals(typeName) || "bigint".equals(typeName) || "fixed".equals(typeName)) {
            return rs.getLong(index);
        }

        ///////单精度
        if ("decimal".equals(typeName) || "money".equals(typeName) || "float".equals(typeName) || "real".equals(typeName) || "binary_float".equals(typeName)) {
            return rs.getFloat(index);
        }

        ///////大数值
        if ("numeric".equals(typeName)) {
            return rs.getBigDecimal(index);
        }

        ///////双精度
        if ("double".equals(typeName) || "double precision".equals(typeName) || "binary_double".equals(typeName)) {
            return rs.getDouble(index);
        }


        //timestamptz  传输有两种方式，字符串和  bytes
        if (typeName.contains("timestamptz")) {
            //目前发现jdbc 有bug，得到的时间，如果是多个timestamptz，后边的得不到时间，只能得到日期，而第一个可以得到
            byte[] t = rs.getBytes(index);
            if (t != null) {
                try {
                    return StringUtil.getDate(new String(t));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }


        ///////日期时间java.sql.Timestamp
        if (typeName.contains("timestamp") || "datetime".equals(typeName)) {
            Timestamp t = rs.getTimestamp(index);
            if (t == null) {
                return null;
            }
            return new java.util.Date(t.getTime());
        }
        ////////////时间

        ///////日期
        if ("date".equals(typeName)) {
            java.sql.Date t = rs.getDate(index);
            if (t == null) {
                return null;
            }
            return new java.util.Date(t.getTime());
        }

        if ("time".equalsIgnoreCase(typeName)) {
            return rs.getTime(index);
        }

        ///////短字符串
        if ("char".equals(typeName) || "nvarchar".equals(typeName) || "varchar".equals(typeName) || "varchar2".equals(typeName) || "text".equals(typeName)) {
            return rs.getString(index);
        }

        ////////////大文本类型
        if ("clob".equals(typeName) || "mediumtext".equals(typeName) || " long varchar".equals(typeName)
                || "ntext".equals(typeName) || "long raw".equals(typeName)) {
            Reader reader = rs.getCharacterStream(index);
            if (reader == null) {
                return StringUtil.empty;
            }
            StringWriter out = new StringWriter();
            try {
                char[] buf = new char[256];
                int i;
                while ((i = reader.read(buf)) != -1) {
                    out.write(buf, 0, i);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("close clob out error,关闭大文件类型读取错误", e);
                }
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("close clob reader error,关闭大文件类型读取错误", e);
                }
            }
            return out.toString();
        }

        ///////二进制类型 文件类型
        if ("bytea".equalsIgnoreCase(typeName) || "bytea[]".equalsIgnoreCase(typeName) || typeName.contains("blob")) {
            return new ByteArrayInputStream(rs.getBytes(index));
        }
        return rs.getObject(index);
    }


    @Override
    public boolean supportsSequenceName() {
        return true;
    }

    @Override
    public boolean supportsConcurReadOnly() {
        return false;
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean commentPatch() {
        return true;
    }

}