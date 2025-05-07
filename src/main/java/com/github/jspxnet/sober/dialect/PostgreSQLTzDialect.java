package com.github.jspxnet.sober.dialect;

/**
 * 代时区
 */
public class PostgreSQLTzDialect  extends  PostgreSQLDialect{
    public PostgreSQLTzDialect() {
        put(java.util.Date.class.getName(), "${" + COLUMN_NAME + "} timestamptz <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default now()");
        put(java.sql.Date.class.getName(), "${" + COLUMN_NAME + "} timestamptz <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default now()");
    }
}
