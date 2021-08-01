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
 * Time: 12:45:58
 * Interbase 数据库 SQL 匹配
 */
public class InterbaseDialect extends Dialect {
    public InterbaseDialect() {
        put(SQL_CREATE_TABLE, "CREATE TABLE ${" + KEY_TABLE_NAME + "} \n(\n" +
                " <#list column=" + KEY_COLUMN_LIST + ">${column},\n</#list>" +
                " \nPRIMARY KEY (${" + KEY_PRIMARY_KEY + "})\n)");

        put(String.class.getName(), "${" + COLUMN_NAME + "} <#if where=" + COLUMN_LENGTH + "&gt;255>blob sub_type 1<#else>varchar(${" + COLUMN_LENGTH + "})</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default '${" + COLUMN_DEFAULT + "}'</#else></#if>");
        put(Boolean.class.getName(), "${" + COLUMN_NAME + "} smallint <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>1</#else></#if></#else></#if>");
        put(boolean.class.getName(), "${" + COLUMN_NAME + "} smallint <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>1</#else></#if></#else></#if>");
        put(Integer.class.getName(), "${" + COLUMN_NAME + "} integer <#if where=" + KEY_FIELD_SERIAL + ">GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CACHE)</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        put("int", "${" + COLUMN_NAME + "} integer <#if where=" + KEY_FIELD_SERIAL + ">GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CACHE)</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        put(Long.class.getName(), "${" + COLUMN_NAME + "} bigint <#if where=" + KEY_FIELD_SERIAL + ">GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1, NO CACHE )</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        put("long", "${" + COLUMN_NAME + "} bigint <#if where=" + KEY_FIELD_SERIAL + ">GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1, NO CACHE )</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        put(Double.class.getName(), "${" + COLUMN_NAME + "} double precision <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        put("double", "${" + COLUMN_NAME + "} double precision <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        put(Float.class.getName(), "${" + COLUMN_NAME + "} float <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        put("float", "${" + COLUMN_NAME + "} float <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        put(Date.class.getName(), "${" + COLUMN_NAME + "} timestamp default getdate()");
        put(byte[].class.getName(), "${" + COLUMN_NAME + "} blob");
        put(InputStream.class.getName(), "${" + COLUMN_NAME + "} blob");
        put(char.class.getName(), "${" + COLUMN_NAME + "} char(2) NOT NULL default ''");
        put(SQL_DROP_TABLE, "DROP TABLE ${" + KEY_TABLE_NAME + "}");
        put(FUN_TABLE_EXISTS, "if (Exists(Select RDB$RELATION_NAME From RDB$RELATIONS WHERE (RDB$RELATION_NAME='${" + KEY_TABLE_NAME + "}') AND RDB$VIEW_SOURCE IS NULL))");
    }

    @Override
    public String getLimitString(String sql, int ibegin, int iend, TableModels soberTable) {
        return sql + " rows " + (ibegin + 1) + " transfer " + iend;
    }


    @Override
    public boolean supportsConcurReadOnly() {
        return false;
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
    public boolean commentPatch() {
        return false;
    }
}