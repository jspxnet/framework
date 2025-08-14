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

import java.io.InputStream;
import java.sql.Time;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2007-12-3
 * Time: 10:33:27
 */
public class SybaseDialect extends Dialect {
    public SybaseDialect() {

        put(SQL_CREATE_TABLE, "CREATE TABLE ${" + KEY_TABLE_NAME + "} \n(\n" +
                " <#list column=" + KEY_COLUMN_LIST + ">${column},\n</#list>" +
                " \nPRIMARY KEY (${" + KEY_PRIMARY_KEY + "})\n)");

        put(String.class.getName(), "${" + COLUMN_NAME + "} <#if where=" + COLUMN_LENGTH + "&gt;255>text<#else>varchar(${" + COLUMN_LENGTH + "})</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default '${" + COLUMN_DEFAULT + "}'</#else></#if>");

        put(Boolean.class.getName(), "${" + COLUMN_NAME + "} tinyint <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>1</#else></#if></#else></#if>");
        put(boolean.class.getName(), "${" + COLUMN_NAME + "} tinyint <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>1</#else></#if></#else></#if>");

        put(Integer.class.getName(), "${" + COLUMN_NAME + "} int <#if where=" + KEY_FIELD_SERIAL + ">GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CACHE)</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        put("int", "${" + COLUMN_NAME + "} int <#if where=" + KEY_FIELD_SERIAL + ">GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CACHE)</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");

        put(Long.class.getName(), "${" + COLUMN_NAME + "} bigint <#if where=" + KEY_FIELD_SERIAL + ">GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1, NO CACHE )</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        put("long", "${" + COLUMN_NAME + "} bigint <#if where=" + KEY_FIELD_SERIAL + ">GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1, NO CACHE )</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");

        put(Double.class.getName(), "${" + COLUMN_NAME + "} double precision <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        put("double", "${" + COLUMN_NAME + "} double precision <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");

        put(Float.class.getName(), "${" + COLUMN_NAME + "} float <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        put("float", "${" + COLUMN_NAME + "} float <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");

        put(Date.class.getName(), "${" + COLUMN_NAME + "} datetime default getdate()");
        put(byte[].class.getName(), "${" + COLUMN_NAME + "} image");
        put(InputStream.class.getName(), "${" + COLUMN_NAME + "} image");
        put(char.class.getName(), "${" + COLUMN_NAME + "} char(2) NOT NULL default ''");
        put(SQL_DROP_TABLE, "DROP TABLE ${" + KEY_TABLE_NAME + "}");
        put(FUN_TABLE_EXISTS, "select count(1) from sysobjects where name='${" + KEY_TABLE_NAME + "}'");
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
                return "float";
            }
            if (soberColumn.getClassType()==double.class || soberColumn.getClassType()==Double.class)
            {

                return "double precision";
            }
        }
        if (soberColumn.getClassType()==boolean.class || soberColumn.getClassType()==Boolean.class)
        {
            return "tinyint";
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

        if (soberColumn.getClassType()== Time.class)
        {
            return "datetime";
        }

        if (soberColumn.getClassType()==InputStream.class)
        {
            return "image";
        }

        if (soberColumn.getClassType()==char.class)
        {
            return "char("+soberColumn.getLength()+")";
        }
        return "varchar(512)";
    }

    @Override
    public String getLimitString(String sql, int begin, int end, TableModels soberTable) {
        return sql + " first " + begin + " skip " + end;
    }

    @Override
    public boolean supportsConcurReadOnly() {
        return false;
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
    public boolean commentPatch() {
        return false;
    }

    @Override
    public String fieldQuerySql(String sql) {
        return "SELECT TOP 1 * FROM (" + sql + ") zs";
    }

}