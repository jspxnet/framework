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

import java.io.InputStream;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-11-7
 * Time: 下午11:33
 * To change this template use File | Settings | File Templates.
 * todo 还没调试，不能正常使用
 */
public class InformixDialect extends Dialect {
    public InformixDialect() {
        standard_SQL.put(SQL_CREATE_TABLE, "CREATE TABLE ${" + KEY_TABLE_NAME + "} \n(\n" +
                " <#list column=" + KEY_COLUMN_LIST + ">${column},\n</#list>" +
                " \nPRIMARY KEY (${" + KEY_PRIMARY_KEY + "})\n)");

        standard_SQL.put(String.class.getName(), "${" + COLUMN_NAME + "} <#if where=" + COLUMN_LENGTH + "&gt;255 >\"LONG VARCHAR\"<#else>varchar(${" + COLUMN_LENGTH + "})</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default '${" + COLUMN_DEFAULT + "}'</#else></#if>");

        standard_SQL.put(Boolean.class.getName(), "${" + COLUMN_NAME + "} smallint <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>1</#else></#if></#else></#if>");
        standard_SQL.put(boolean.class.getName(), "${" + COLUMN_NAME + "} smallint <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>1</#else></#if></#else></#if>");

        standard_SQL.put(Integer.class.getName(), "${" + COLUMN_NAME + "} int <#if where=" + KEY_FIELD_SERIAL + ">GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CACHE)</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        standard_SQL.put("int", "${" + COLUMN_NAME + "} int <#if where=" + KEY_FIELD_SERIAL + ">GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CACHE)</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");

        standard_SQL.put(Long.class.getName(), "${" + COLUMN_NAME + "} bigint <#if where=" + KEY_FIELD_SERIAL + ">GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1, NO CACHE )</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        standard_SQL.put("long", "${" + COLUMN_NAME + "} bigint <#if where=" + KEY_FIELD_SERIAL + ">GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1, NO CACHE )</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");

        standard_SQL.put(Float.class.getName(), "${" + COLUMN_NAME + "} real <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        standard_SQL.put("float", "${" + COLUMN_NAME + "} real <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");

        standard_SQL.put(Double.class.getName(), "${" + COLUMN_NAME + "} <#if where=" + COLUMN_LENGTH + "&gt;15>double(${" + COLUMN_LENGTH + "},3)<#else>double(15,3)</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");
        standard_SQL.put("double", "${" + COLUMN_NAME + "} <#if where=" + COLUMN_LENGTH + "&gt;15>double(${" + COLUMN_LENGTH + "},3)<#else>double(15,3)</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#else></#if>");

        standard_SQL.put(Date.class.getName(), "${" + COLUMN_NAME + "} timestamp default current timestamp");
        standard_SQL.put(byte[].class.getName(), "${" + COLUMN_NAME + "} blob");
        standard_SQL.put(InputStream.class.getName(), "${" + COLUMN_NAME + "} blob");
        standard_SQL.put(char.class.getName(), "${" + COLUMN_NAME + "} char(2) NOT NULL default ''");
        standard_SQL.put(SQL_DROP_TABLE, "DROP TABLE ${" + KEY_TABLE_NAME + "}");
        standard_SQL.put(FUN_TABLE_EXISTS, "SELECT count(name) FROM sysibm.systables WHERE name LIKE upper('${" + KEY_TABLE_NAME + "}')");
    }


    @Override
    public String getLimitString(String sql, int begin, int end, TableModels soberTable) {
        int length = end - begin;
        if (length < 0) {
            length = 0;
        }
        //skip 4 first 2
        //select skip 0 first 2 * from test_rowcols where 1=1 order by score;
        StringBuilder sb = new StringBuilder(sql.length() + 20).append(sql);
        int pos = sb.toString().toLowerCase().indexOf("select");
        sb.insert(pos + "select".length(), " skip " + begin + " first " + length);
        return sb.toString();

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
        return false;
    }

}