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
import com.github.jspxnet.utils.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-10
 * Time: 11:29:08
 * MS SQL 2000 数据库 SQL 匹配
 */
@Slf4j
public class MsSqlDialect extends Dialect {
    public MsSqlDialect() {

        //oracle 和 pgsql 设置注释方式begin
        //字段注释
        put(SQL_COMMENT, "EXEC sp_addextendedproperty 'MS_Description', N'${" + COLUMN_CAPTION + "}','SCHEMA', N'dbo','TABLE', N'${" + KEY_TABLE_NAME + "}','COLUMN', N'${" + COLUMN_NAME + "}'");

        //  put(SQL_COMMENT, "sp_updateextendedproperty 'MS_Description', N'${" + COLUMN_CAPTION + "}','SCHEMA', N'dbo','TABLE', N'${" + KEY_TABLE_NAME + "}','COLUMN', N'${" + COLUMN_NAME + "}'");

        //表注释
        put(SQL_TABLE_COMMENT, "EXEC sp_addextendedproperty 'MS_Description', N'${" + SQL_TABLE_COMMENT + "}','SCHEMA', N'dbo','TABLE', N'${" + KEY_TABLE_NAME + "}'");
        //oracle 和 pgsql 设置注释方式end

        put(SQL_CREATE_TABLE, "<#assign primary_length=" + KEY_PRIMARY_KEY + ".length />" +
                "CREATE TABLE [dbo].[${" + KEY_TABLE_NAME + "}] \n(\n" +
                " <#list column=" + KEY_COLUMN_LIST + ">${column} <#if where=column.substring(1,primary_length)==" + KEY_PRIMARY_KEY + ">PRIMARY KEY</#if><#if where=column_has_next>,</#if>\n</#list>" +
                " \n)");


        put(SQL_INSERT, "INSERT INTO ${" + KEY_TABLE_NAME + "} (<#list field=" + KEY_FIELD_LIST + ">${field}<#if where=field_has_next>,</#if></#list>) VALUES (<#list x=[1.." + KEY_FIELD_COUNT + "]>?<#if where=x_has_next>,</#if></#list>)");

        put(SQL_CRITERIA_QUERY, "SELECT top ${" + SQL_RESULT_END_ROW + "} * FROM ${" + KEY_TABLE_NAME + "} <#if where=" + KEY_TERM + "!=''>WHERE ${" + KEY_TERM + "}</#if><#if where=" + KEY_FIELD_GROUPBY + "!=''> GROUP BY ${" + KEY_FIELD_GROUPBY + "}</#if><#if where=" + KEY_FIELD_ORDERBY + "!=''> ORDER BY ${" + KEY_FIELD_ORDERBY + "}</#if>");

        put(String.class.getName(), "[${" + COLUMN_NAME + "}] <#if where=" + COLUMN_LENGTH + "&gt;1000>[ntext]<#else>nvarchar(${" + COLUMN_LENGTH + "})</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else>NULL</#else></#if> <#if where=" + COLUMN_DEFAULT + ">default '${" + COLUMN_DEFAULT + "}'</#if>");

        put(Boolean.class.getName(), "[${" + COLUMN_NAME + "}] smallint <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=" + COLUMN_DEFAULT + ">default ${" + COLUMN_DEFAULT + ".toInt()}</#if>");

        put(boolean.class.getName(), "[${" + COLUMN_NAME + "}] smallint <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=" + COLUMN_DEFAULT + ">default ${" + COLUMN_DEFAULT + ".toInt()}</#if>");


        put(Integer.class.getName(), "[${" + COLUMN_NAME + "}] int <#if where=" + KEY_FIELD_SERIAL + ">identity(1,1)</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if>  <#if where=!" + KEY_FIELD_SERIAL + " >default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#if>");
        put("int", "[${" + COLUMN_NAME + "}] [int] <#if where=" + KEY_FIELD_SERIAL + ">identity(1,1)</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if>  <#if where=!" + KEY_FIELD_SERIAL + " >default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#if>");

        put(Long.class.getName(), "[${" + COLUMN_NAME + "}] bigint <#if where=" + KEY_FIELD_SERIAL + ">identity(1,1)</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=!" + KEY_FIELD_SERIAL + " >default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#if>");
        put("long", "[${" + COLUMN_NAME + "}] [bigint] <#if where=" + KEY_FIELD_SERIAL + ">identity(1,1)</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=!" + KEY_FIELD_SERIAL + " >default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#if>");

        put(Double.class.getName(), "[${" + COLUMN_NAME + "}] decimal(16,5) <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else>NULL</#else></#if> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");
        put("double", "[${" + COLUMN_NAME + "}] decimal(16,5) <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else>NULL</#else></#if> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");


        put(Float.class.getName(), "[${" + COLUMN_NAME + "}] decimal(10,4) <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else>NULL</#else></#if> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");
        put("float", "[${" + COLUMN_NAME + "}] decimal(10,4) <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else>NULL</#else></#if> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");

        put(Time.class.getName(), "[${" + COLUMN_NAME + "}] time(0) NOT NULL default getdate()");
        put("time", "[${" + COLUMN_NAME + "}] time(0) NOT NULL default getdate()");

        put(Date.class.getName(), "[${" + COLUMN_NAME + "}] datetime NOT NULL default getdate()");
        put("date", "[${" + COLUMN_NAME + "}] datetime NOT NULL default getdate()");

        put(byte[].class.getName(), "[${" + COLUMN_NAME + "}] image");
        put(InputStream.class.getName(), "[${" + COLUMN_NAME + "}] image");
        put(char.class.getName(), "[${" + COLUMN_NAME + "}] char(2) NOT NULL");
        put(SQL_DROP_TABLE, "drop table [${" + KEY_TABLE_NAME + "}]");
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
    public String getFieldType(SoberColumn soberColumn) {

        if (ClassUtil.isNumberType(soberColumn.getClassType()))
        {
            if (soberColumn.getClassType()==int.class || soberColumn.getClassType()==Integer.class)
            {
                return "int";
            }

            if (soberColumn.getClassType()==long.class || soberColumn.getClassType()==Long.class)
            {
                return "bigint";
            }

            if (soberColumn.getClassType()==float.class || soberColumn.getClassType()==Float.class)
            {
                if (soberColumn.getLength()<1)
                {
                    return "decimal(10,4)";
                }
                return "decimal("+soberColumn.getLength()+",4)";
            }

            if (soberColumn.getClassType()==double.class || soberColumn.getClassType()==Double.class)
            {
                if (soberColumn.getLength()<1)
                {
                    return "decimal(16,5)";
                }
                return "decimal("+soberColumn.getLength()+",5)";
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
                return "nvarchar("+soberColumn.getLength()+")";
            }
            return "ntext";
        }

        if (soberColumn.getClassType()==Date.class)
        {
            return "datetime";
        }

        if (soberColumn.getClassType()== Time.class)
        {
            if (soberColumn.getLength()<0)
            {
                return "time(0)";
            }
            return "time("+soberColumn.getLength()+")";
        }

        if (soberColumn.getClassType()==InputStream.class)
        {
            return "image";
        }

        if (soberColumn.getClassType()==char.class)
        {
            return "char("+soberColumn.getLength()+")";
        }
        return "nvarchar(512)";
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
    public String getLimitString(String sql, int begin, int end,TableModels soberTable) {
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


    /**
     * @param rs    数据
     * @param index 索引
     * @return 返回查询结果
     * @throws SQLException 异常
     */
    @Override
    public Object getResultSetValue(ResultSet rs, int index) throws SQLException {
        if (rs == null || index <= 0) {
            return null;
        }
        String typeName = null;
        int colSize = 0;
        try {
            typeName = rs.getMetaData().getColumnTypeName(index).toLowerCase();
            colSize = rs.getMetaData().getColumnDisplaySize(index);
            ///////日期
            if ("date".equals(typeName) || "datetime".equals(typeName)) {
                java.sql.Timestamp t = rs.getTimestamp(index);
                if (t == null) {
                    return null;
                }
                return new java.util.Date(t.getTime());
            }
            //短断整型
            return super.getResultSetValue(rs,index);
        } catch (SQLException e) {
            log.error("typeName=" + typeName + " size=" + colSize + " columnName=" + rs.getMetaData().getColumnName(index), e);
        }
        return null;
    }

    @Override
    public String fieldQuerySql(String sql) {
        return "SELECT top 1 * FROM (" + sql + ") zs";
    }
}