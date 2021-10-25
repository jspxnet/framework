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
import com.github.jspxnet.utils.ObjectUtil;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-10
 * Time: 11:29:08
 * MS SQL 2000 数据库 SQL 匹配
 */
public class MsSqlDialect extends Dialect {
    public MsSqlDialect() {
        put(SQL_CREATE_TABLE, "<#assign primary_length=" + KEY_PRIMARY_KEY + ".length />" +
                "CREATE TABLE [dbo].[${" + KEY_TABLE_NAME + "}] \n(\n" +
                " <#list column=" + KEY_COLUMN_LIST + ">${column} <#if where=column.substring(1,primary_length)==" + KEY_PRIMARY_KEY + ">PRIMARY KEY</#if><#if where=column_has_next>,</#if>\n</#list>" +
                " \n)");

        //oracle 和 pgsql 设置注释方式begin
        //字段注释
        put(SQL_COMMENT, "sp_addextendedproperty 'MS_Description', N'${" + COLUMN_CAPTION + "}','SCHEMA', N'dbo','TABLE', N'${" + KEY_TABLE_NAME + "}','COLUMN', N'${" + COLUMN_NAME + "}'");

        //表注释
        put(SQL_TABLE_COMMENT, "sp_addextendedproperty 'MS_Description', N'${" + SQL_TABLE_COMMENT + "}','SCHEMA', N'dbo','TABLE', N'${" + KEY_TABLE_NAME + "}'");
        //oracle 和 pgsql 设置注释方式end





        put(SQL_INSERT, "INSERT INTO ${" + KEY_TABLE_NAME + "} (<#list field=" + KEY_FIELD_LIST + ">${field}<#if where=field_has_next>,</#if></#list>) VALUES (<#list x=[1.." + KEY_FIELD_COUNT + "]>?<#if where=x_has_next>,</#if></#list>)");
        put(SQL_CRITERIA_QUERY, "SELECT top ${" + SQL_RESULT_END_ROW + "} * FROM ${" + KEY_TABLE_NAME + "} <#if where=" + KEY_TERM + "!=''>WHERE ${" + KEY_TERM + "}</#if><#if where=" + KEY_FIELD_GROUPBY + "!=''> GROUP BY ${" + KEY_FIELD_GROUPBY + "}</#if><#if where=" + KEY_FIELD_ORDERBY + "!=''> ORDER BY ${" + KEY_FIELD_ORDERBY + "}</#if>");

        put(String.class.getName(), "[${" + COLUMN_NAME + "}] <#if where=" + COLUMN_LENGTH + "&gt;1000>[ntext]<#else>[nvarchar] (${" + COLUMN_LENGTH + "})</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else>NULL</#else></#if> <#if where=" + COLUMN_DEFAULT + ">default '${" + COLUMN_DEFAULT + "}'</#if>");

        put(Boolean.class.getName(), "[${" + COLUMN_NAME + "}] smallint <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default ${" + COLUMN_DEFAULT + "}");
        put(boolean.class.getName(), "[${" + COLUMN_NAME + "}] smallint <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default ${" + COLUMN_DEFAULT + "}");

        put(Integer.class.getName(), "[${" + COLUMN_NAME + "}] [int] <#if where=" + KEY_FIELD_SERIAL + ">identity(1,1)</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if>  <#if where=!" + KEY_FIELD_SERIAL + " >default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#if>");
        put("int", "[${" + COLUMN_NAME + "}] [int] <#if where=" + KEY_FIELD_SERIAL + ">identity(1,1)</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if>  <#if where=!" + KEY_FIELD_SERIAL + " >default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#if>");

        put(Long.class.getName(), "[${" + COLUMN_NAME + "}] [bigint] <#if where=" + KEY_FIELD_SERIAL + ">identity(1,1)</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=!" + KEY_FIELD_SERIAL + " >default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#if>");
        put("long", "[${" + COLUMN_NAME + "}] [bigint] <#if where=" + KEY_FIELD_SERIAL + ">identity(1,1)</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=!" + KEY_FIELD_SERIAL + " >default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#if>");

        put(Double.class.getName(), "[${" + COLUMN_NAME + "}] [decimal](16,5) <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else>NULL</#else></#if> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");
        put("double", "[${" + COLUMN_NAME + "}] [decimal](16,5) <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else>NULL</#else></#if> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");


        put(Float.class.getName(), "[${" + COLUMN_NAME + "}] [decimal](10,4) <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else>NULL</#else></#if> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");
        put("float", "[${" + COLUMN_NAME + "}] [decimal](10,4) <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else>NULL</#else></#if> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");

        put(Date.class.getName(), "[${" + COLUMN_NAME + "}] datetime NOT NULL default getdate()");
        put(byte[].class.getName(), "[${" + COLUMN_NAME + "}] image");
        put(InputStream.class.getName(), "[${" + COLUMN_NAME + "}] image");
        put(char.class.getName(), "[${" + COLUMN_NAME + "}] char(2) NOT NULL");
        put(SQL_DROP_TABLE, "drop table [dbo].[${" + KEY_TABLE_NAME + "}]");
        put(FUN_TABLE_EXISTS, "SELECT * FROM sysobjects WHERE name='${" + KEY_TABLE_NAME + "}'");


//创建索引,一个一个
/*
CREATE UNIQUE NONCLUSTERED INDEX [name_index] ON [dbo].[jcompany_colony_group] (
  [name] DESC
)
GO

CREATE NONCLUSTERED INDEX [name_data_index] ON [dbo].[jcompany_colony_group] (
  [name] ASC,
  [createDate] DESC
)
GO
*/
        put(SQL_CREATE_TABLE_INDEX, "CREATE  <#if where=" + KEY_IS_UNIQUE + ">UNIQUE</#if> NONCLUSTERED INDEX  [${"+KEY_INDEX_NAME+"}] ON [dbo].[${" + KEY_TABLE_NAME + "}] (${"+KEY_INDEX_FIELD+"})");


    }

    @Override
    public void setPreparedStatementValue(PreparedStatement pstmt, int parameterIndex, Object obj) throws Exception {
        // 支持boolean 类型
        if (obj instanceof Boolean) {
            pstmt.setInt(parameterIndex, ObjectUtil.toInt(ObjectUtil.toBoolean(obj)));
            return;
        }
        super.setPreparedStatementValue(pstmt, parameterIndex, obj);
    }

    @Override
    public String getLimitString(String sql, int begin, int end, TableModels soberTable) {
        return sql;
    }

    @Override
    public boolean supportsConcurReadOnly() {
        return true;
    }

    @Override
    public boolean supportsSequenceName() {
        return false;
    }

    @Override
    public boolean supportsLimit() {
        return false;
    }

    @Override
    public boolean commentPatch() {
        return true;
    }
}