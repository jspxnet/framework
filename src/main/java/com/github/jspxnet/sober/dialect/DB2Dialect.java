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
import com.github.jspxnet.utils.NumberUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-10
 * Time: 12:48:51
 * DB2数据库 SQL匹配
 */
public class DB2Dialect extends Dialect {

    public DB2Dialect() {
        put(SQL_CREATE_TABLE, "CREATE TABLE ${" + KEY_TABLE_NAME + "} \n(\n" +
                " <#list column=" + KEY_COLUMN_LIST + ">${column},\n</#list>" +
                " \nPRIMARY KEY (${" + KEY_PRIMARY_KEY + "})\n)");

        //oracle , pgsql ,db2,dm一样
        //oracle , pgsql ,db2,dm设置注释方式begin
        put(SQL_COMMENT, "COMMENT ON COLUMN ${" + KEY_TABLE_NAME + "}.${" + COLUMN_NAME + "} IS '${" + COLUMN_CAPTION + "}'");
        put(SQL_TABLE_COMMENT, "COMMENT ON TABLE ${" + KEY_TABLE_NAME + "} IS '${" + SQL_TABLE_COMMENT + "}'");
        //oracle 和 pgsql 设置注释方式end


        put(String.class.getName(), "${" + COLUMN_NAME + "} <#if where=" + COLUMN_LENGTH + "&gt;255 >\"LONG VARCHAR\"<#else>varchar(${" + COLUMN_LENGTH + "})</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default '${" + COLUMN_DEFAULT + "}'</#else></#if>");

        put(Boolean.class.getName(), "${" + COLUMN_NAME + "} smallint <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>1</#else></#if></#else></#if>");
        put(boolean.class.getName(), "${" + COLUMN_NAME + "} smallint <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>1</#else></#if></#else></#if>");

        put(Integer.class.getName(), "${" + COLUMN_NAME + "} int <#if where=" + KEY_FIELD_SERIAL + ">GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CACHE)</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        put("int", "${" + COLUMN_NAME + "} int <#if where=" + KEY_FIELD_SERIAL + ">GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CACHE)</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");

        put(Long.class.getName(), "${" + COLUMN_NAME + "} bigint <#if where=" + KEY_FIELD_SERIAL + ">GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1, NO CACHE )</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        put("long", "${" + COLUMN_NAME + "} bigint <#if where=" + KEY_FIELD_SERIAL + ">GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1, NO CACHE )</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");

        put(Float.class.getName(), "${" + COLUMN_NAME + "} real <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        put("float", "${" + COLUMN_NAME + "} real <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");

        put(Double.class.getName(), "${" + COLUMN_NAME + "} <#if where=" + COLUMN_LENGTH + "&gt;15>double(${" + COLUMN_LENGTH + "},3)<#else>double(15,3)</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        put("double", "${" + COLUMN_NAME + "} <#if where=" + COLUMN_LENGTH + "&gt;15>double(${" + COLUMN_LENGTH + "},3)<#else>double(15,3)</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");

        put(Date.class.getName(), "${" + COLUMN_NAME + "} timestamp default current timestamp");
        put(Time.class.getName(), "${" + COLUMN_NAME + "} time default current timestamp");
        put(byte[].class.getName(), "${" + COLUMN_NAME + "} blob");
        put(InputStream.class.getName(), "${" + COLUMN_NAME + "} blob");
        put(char.class.getName(), "${" + COLUMN_NAME + "} char(2) NOT NULL default ''");
        put(SQL_DROP_TABLE, "DROP TABLE ${" + KEY_TABLE_NAME + "}");
        put(FUN_TABLE_EXISTS, "SELECT count(name) FROM sysibm.systables WHERE name LIKE upper('${" + KEY_TABLE_NAME + "}')");


        put(SQL_ADD_COLUMN, "ALTER TABLE ${" + KEY_TABLE_NAME + "} ADD COLUMN ${" + COLUMN_NAME + "} ${"+COLUMN_TYPE+"} <#if where=" + COLUMN_DEFAULT + ">default '${" + COLUMN_DEFAULT + "}'</#if>");

        put(SQL_MODIFY_COLUMN, "ALTER TABLE ${" + KEY_TABLE_NAME + "} MODIFY COLUMN ${" + COLUMN_NAME + "} ${"+COLUMN_TYPE+"} <#if where=" + COLUMN_DEFAULT + ">default '${" + COLUMN_DEFAULT + "}'</#if>");

        put(SQL_DROP_COLUMN, "ALTER TABLE ${" + KEY_TABLE_NAME + "} DROP COLUMN ${" + COLUMN_NAME + "}");

    }


    @Override
    public String getFieldType(SoberColumn soberColumn) {

        if (ClassUtil.isNumberType(soberColumn.getClassType()))
        {
            if (soberColumn.getClassType()==int.class || soberColumn.getClassType()==Integer.class)
            {
                if (soberColumn.getLength()<3)
                {
                    return "smallint("+soberColumn.getLength()+")";
                }
                return "int";
            }

            if (soberColumn.getClassType()==long.class || soberColumn.getClassType()==Long.class)
            {
                return "bigint";
            }

            if (soberColumn.getClassType()==float.class || soberColumn.getClassType()==Float.class||soberColumn.getClassType()==double.class || soberColumn.getClassType()==Double.class)
            {
                return "real";
            }
        }
        if (soberColumn.getClassType()==boolean.class || soberColumn.getClassType()==Boolean.class)
        {
            return "smallint";
        }
        if (soberColumn.getClassType()==String.class)
        {
            if (soberColumn.getLength()<1000)
            {
                return "varchar("+soberColumn.getLength()+")";
            }
            return "LONG VARCHAR";
        }

        if (soberColumn.getClassType()==Date.class)
        {
            return "timestamp";
        }

        if (soberColumn.getClassType()== Time.class)
        {
            return "time";
        }

        if (soberColumn.getClassType()==InputStream.class)
        {
            return "blob";
        }
        if (soberColumn.getClassType()==char.class)
        {
            return "char("+soberColumn.getLength()+")";
        }
        return "varchar(512)";
    }

    private String getRowNumber(String sql) {
        StringBuilder row = new StringBuilder(50)
                .append("rownumber() over(");
        int orderByIndex = sql.toLowerCase().indexOf("order by");
        if (orderByIndex > 0 && !hasDistinct(sql)) {
            row.append(sql.substring(orderByIndex));
        }
        row.append(") as rownumber_,");
        return row.toString();
    }


    private static boolean hasDistinct(String sql) {
        return sql.toLowerCase().contains("select distinct");
    }

    private String getTableName(String sql) {
        int fromByIndex = sql.toLowerCase().indexOf("from");
        int whereByIndex = sql.toLowerCase().indexOf("where");
        if (whereByIndex == -1) {
            whereByIndex = sql.length();
        }
        int orderByIndex = sql.toLowerCase().indexOf("order");
        if (orderByIndex == -1) {
            orderByIndex = sql.length();
        }
        int groupByIndex = sql.toLowerCase().indexOf("group");
        if (groupByIndex == -1) {
            groupByIndex = sql.length();
        }
        int end = NumberUtil.getMin(new int[]{whereByIndex, orderByIndex, groupByIndex});
        if (end == -1) {
            end = sql.length();
        }
        return sql.substring(fromByIndex + 5, end).trim();
    }

    private String getWhere(String sql) {
        int whereByIndex = sql.toLowerCase().indexOf("where");
        if (whereByIndex == -1) {
            return StringUtil.empty;
        }
        int orderByIndex = sql.toLowerCase().indexOf("order");
        if (orderByIndex == -1) {
            orderByIndex = sql.length();
        }
        int groupByIndex = sql.toLowerCase().indexOf("group");
        if (groupByIndex == -1) {
            groupByIndex = sql.length();
        }
        int end = NumberUtil.getMin(new int[]{orderByIndex, groupByIndex});
        if (end == -1) {
            end = sql.length();
        }
        return sql.substring(whereByIndex, end).trim();
    }

    private String getGroupBy(String sql) {
        int groupByIndex = sql.toLowerCase().indexOf("group");
        if (groupByIndex == -1) {
            return StringUtil.empty;
        }
        int orderByIndex = sql.toLowerCase().indexOf("order");
        if (orderByIndex == -1) {
            orderByIndex = sql.length();
        }
        return sql.substring(groupByIndex, orderByIndex).trim();
    }

    //////不能使用group by
    //select * from (select tb.*,rownumber() over() as rownumber_,from testBean tb  ) where rownumber_ between 1 and 10
    @Override
    public String getLimitString(String sql, int ibegin, int iend,TableModels soberTable) {
        String table = getTableName(sql);
        StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
        if (hasDistinct(sql)) {
            pagingSelect.append("SELECT * FROM (SELECT DISTINCT ");
        } else {
            pagingSelect.append("SELECT * from (SELECT ");
        }
        pagingSelect.append(getRowNumber(sql));
        pagingSelect.append(table).append(".* FROM ").append(table);
        pagingSelect.append(" ").append(getWhere(sql));
        pagingSelect.append(" ").append(getGroupBy(sql));
        pagingSelect.append(" ) WHERE rownumber_ ");
        pagingSelect.append("BETWEEN ").append((ibegin + 1)).append(" AND ").append(iend);
        return pagingSelect.toString();
    }

    @Override
    public void setPreparedStatementValue(PreparedStatement pstmt, int parameterIndex, Object obj) throws Exception {
        //pgsql 支持boolean 类型
        if (obj instanceof Boolean) {
            pstmt.setInt(parameterIndex, ObjectUtil.toInt(ObjectUtil.toBoolean(obj)));
            return;
        }
        super.setPreparedStatementValue(pstmt, parameterIndex, obj);
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
        return true;
    }

    @Override
    public boolean commentPatch() {
        return true;
    }

    @Override
    public String fieldQuerySql(String sql) {
        return "SELECT * FROM (" + sql + ") zs FETCH FIRST 1 ROWS ONLY";
    }

}