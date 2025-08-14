/*
 * Copyright (c) 2013. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.sober.dialect;

import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.utils.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.InputStream;
import java.sql.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 13-2-19
 * Time: 上午10:43
 * NULL: NULL value
 * Integer: 值是signed integer 类型，大小可以是1,2,3,4,6,8bytes
 * REAL:  浮点类型
 * TEXT: 以UTF-8, UTF-16BE or UTF-16LE编码存储的字符类型
 * BLOB: 二进制数据
 * [id] integer PRIMARY KEY ASC AUTOINCREMENT NOT NULL DEFAULT 0
 * ,[idx] bigint NOT NULL DEFAULT 0
 * ,[context] TEXT
 * ,[dataType] TEXT
 * ,[namespace] varchar(20)
 * , UNIQUE([id])
 */
@Slf4j
public class SqliteDialect extends Dialect {
    public SqliteDialect() {
        put(SQL_CREATE_TABLE, "<#assign primary_length=" + KEY_PRIMARY_KEY + ".length />" +
                "CREATE TABLE ${" + KEY_TABLE_NAME + "} \n(\n" +
                " <#list column=" + KEY_COLUMN_LIST + ">${column} <#if where=column_has_next>,</#if>\n</#list>" +
                " \n)");

        put(SQL_INSERT, "INSERT INTO ${" + KEY_TABLE_NAME + "} (<#list field=" + KEY_FIELD_LIST + ">${field}<#if where=field_has_next>,</#if></#list>) VALUES (<#list x=1.." + KEY_FIELD_COUNT + ">?<#if x_has_next>,</#if></#list>)");
        put(SQL_DELETE, "DELETE FROM ${" + KEY_TABLE_NAME + "} WHERE ${" + KEY_FIELD_NAME + "}=<#if where=" + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>${" + KEY_FIELD_VALUE + "}<#if where=" + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>");
        put(SQL_DELETE_IN, "DELETE FROM ${" + KEY_TABLE_NAME + "} WHERE ${" + KEY_FIELD_NAME + "} IN (<#list fvalue=" + KEY_FIELD_VALUE + ">'${fvalue}'<#if where=fvalue_has_next>,</#if></#list>)");
        put(SQL_UPDATE, "UPDATE ${" + KEY_TABLE_NAME + "} SET <#list field=" + KEY_FIELD_LIST + ">${field}=?<#if where=field_has_next>,</#if></#list> WHERE ${" + KEY_FIELD_NAME + "}=<#if where=" + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>${" + KEY_FIELD_VALUE + "}<#if where=" + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>");

        put(String.class.getName(), "${" + COLUMN_NAME + "} <#if where=" + COLUMN_LENGTH + "&gt;255>text<#else>varchar(${" + COLUMN_LENGTH + "})</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=" + COLUMN_DEFAULT + ">default '${" + COLUMN_DEFAULT + "}'</#if>");

        put(Integer.class.getName(), "${" + COLUMN_NAME + "} integer <#if where=" + KEY_FIELD_SERIAL + ">PRIMARY KEY ASC AUTOINCREMENT</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + ">'0'<#else>'${" + COLUMN_DEFAULT + "}'</#else></#if></#else></#if>");
        put("int", "${" + COLUMN_NAME + "} integer <#if where=" + KEY_FIELD_SERIAL + ">PRIMARY KEY ASC AUTOINCREMENT</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + ">'0'<#else>'${" + COLUMN_DEFAULT + "}'</#else></#if></#else></#if>");
        put(Long.class.getName(), "${" + COLUMN_NAME + "} integer <#if where=" + KEY_FIELD_SERIAL + ">PRIMARY KEY ASC AUTOINCREMENT</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else> default <#if where=!" + COLUMN_DEFAULT + ">'0'<#else>'${" + COLUMN_DEFAULT + "}'</#else></#if></#else></#if>");
        put("long", "${" + COLUMN_NAME + "} integer <#if where=" + KEY_FIELD_SERIAL + ">PRIMARY KEY ASC AUTOINCREMENT</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else>  default <#if where=!" + COLUMN_DEFAULT + ">'0'<#else>'${" + COLUMN_DEFAULT + "}'</#else></#if></#else></#if>");

        put(Double.class.getName(), "${" + COLUMN_NAME + "} REAL default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");
        put("double", "${" + COLUMN_NAME + "} REAL default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");
        put(Float.class.getName(), "${" + COLUMN_NAME + "} REAL <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default <#if where=!" + COLUMN_DEFAULT + ">'0'<#else>'${" + COLUMN_DEFAULT + "}'</#else></#if>");
        put("float", "${" + COLUMN_NAME + "} REAL <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default <#if where=!" + COLUMN_DEFAULT + ">'0'<#else>'${" + COLUMN_DEFAULT + "}'</#else></#if>");

        put(Boolean.class.getName(), "${" + COLUMN_NAME + "} integer <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=" + COLUMN_DEFAULT + ">default ${" + COLUMN_DEFAULT + ".toInt()}</#if>");
        put(boolean.class.getName(), "${" + COLUMN_NAME + "} integer <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=" + COLUMN_DEFAULT + ">default ${" + COLUMN_DEFAULT + ".toInt()}</#if>");

        put(Date.class.getName(), "${" + COLUMN_NAME + "} datetime DEFAULT (datetime(CURRENT_TIMESTAMP,'localtime'))");
        put(byte[].class.getName(), "${" + COLUMN_NAME + "} integer");
        put(InputStream.class.getName(), "${" + COLUMN_NAME + "} blob");
        put(char.class.getName(), "${" + COLUMN_NAME + "} char <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=" + COLUMN_DEFAULT + ">default '${" + COLUMN_DEFAULT + "}'</#if>");

        put(SQL_TABLE_NAMES, "SELECT tbl_name FROM sqlite_master WHERE type = 'table'");

        put(SQL_DROP_TABLE, "DROP TABLE ${" + KEY_TABLE_NAME + "}");

        put(FUN_TABLE_EXISTS, "SELECT COUNT(1) AS NUM FROM sqlite_master WHERE type='table' AND name='${" + KEY_TABLE_NAME + "}'");

        //创建索引
        put(SQL_CREATE_TABLE_INDEX, "CREATE INDEX ${"+KEY_INDEX_NAME+"} ON ${" + KEY_TABLE_NAME + "}(${"+KEY_INDEX_FIELD+"})");

        put(SQL_ADD_COLUMN, "ALTER TABLE ${" + KEY_TABLE_NAME + "} ADD COLUMN ${" + COLUMN_NAME + "} ${"+COLUMN_TYPE+"} <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=" + COLUMN_DEFAULT + ">default '${" + COLUMN_DEFAULT + "}'</#if>");

        //sqlite 不能删除字段,不能修复字段

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
                return "integer";
            }

            if (soberColumn.getClassType()==float.class || soberColumn.getClassType()==Float.class)
            {

                return "REAL";
            }
            if (soberColumn.getClassType()==double.class || soberColumn.getClassType()==Double.class)
            {

                return "REAL";
            }
        }
        if (soberColumn.getClassType()==boolean.class || soberColumn.getClassType()==Boolean.class)
        {
            return "integer";
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
            return "blob";
        }

        if (soberColumn.getClassType()==char.class)
        {
            return "char("+soberColumn.getLength()+")";
        }
        return "varchar(512)";
    }

    @Override
    public String getLimitString(String sql, int begin, int end, TableModels soberTable) {
        int length = end - begin;
        if (length < 0) {
            length = 0;
        }
        return sql + " limit " + begin + "," + length;
    }

    @Override
    public void setPreparedStatementValue(PreparedStatement pstmt, int parameterIndex, Object obj) throws Exception {
        //支持boolean 类型
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
        return false;
    }

    @Override
    public boolean commentPatch() {
        return false;
    }


    /**
     * @param rs    数据
     * @param index 索引
     * @return 返回查询结果
     * @throws java.sql.SQLException 异常
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

            if ("tinyint".equalsIgnoreCase(typeName)) {
                return rs.getBoolean(index);
            }
            //短断整型
            if (("int".equals(typeName) && colSize < 4) || "short".equals(typeName) || "smallint".equals(typeName) || "int2".equals(typeName) || ("fixed".equalsIgnoreCase(typeName) && colSize < 4)) {
                return rs.getShort(index);
            }

            ///////长整型
            if ("bigserial".equals(typeName) || "long".equals(typeName) || "bigint".equals(typeName) || "int8".equals(typeName) || ("fixed".equals(typeName) && colSize > 18)) {
                return rs.getInt(index);
            }

            //////////整型
            if ("integer".equals(typeName) || "serial".equals(typeName) || typeName.contains("int") || ("number".equals(typeName) && colSize < 8) || ("fixed".equals(typeName) && colSize < 19)) {
                return rs.getInt(index);
            }


            ///////单精度
            if ("money".equals(typeName) || "float".equals(typeName) || "real".equals(typeName) || "binary_float".equals(typeName)) {
                return rs.getFloat(index);
            }
            ///////大数值
            if (typeName.contains("decimal")) {
                return rs.getBigDecimal(index);
            }
            ///////双精度
            if ("double".equals(typeName) || "double precision".equals(typeName) || "binary_double".equals(typeName)) {
                return rs.getDouble(index);
            }

            ///////日期
            if ("date".equals(typeName)) {
                java.sql.Date t = rs.getDate(index);
                if (t == null) {
                    return null;
                }
                return new java.util.Date(t.getTime());
            }

            ///////日期时间java.sql.Timestamp
            if (typeName.contains("timestamp") || "datetime".equals(typeName)) {
                Object obj = rs.getObject(index);

                if (obj instanceof Long) {
                    return new java.util.Date((Long) obj);
                } else if (obj instanceof String) {
                    try {
                        return StringUtil.getDate((String) obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (obj instanceof Timestamp) {
                    Timestamp t = rs.getTimestamp(index);
                    return new java.util.Date(t.getTime());
                } else if (obj instanceof Date) {
                    return rs.getDate(index);
                }
            }

            ////////////时间
            if ("time".equalsIgnoreCase(typeName)) {
                return rs.getTime(index);
            }

            ///////短字符串
            if ("char".equals(typeName) || "text".equals(typeName) || "nvarchar".equals(typeName) || "varchar".equals(typeName) || "varchar2".equals(typeName)) {
                return rs.getString(index);
            }


            ///////二进制类型 文件类型
            if ("bytea".equals(typeName) || typeName.contains("blob") || "image".equalsIgnoreCase(typeName) || "long byte".equalsIgnoreCase(typeName)
                    || "varbinary".equalsIgnoreCase(typeName) || "binary".equalsIgnoreCase(typeName)) {

                Blob blob = rs.getBlob(index);
                return blob.getBinaryStream();
            }
            return rs.getObject(index);
        } catch (SQLException e) {
            log.error("typeName=" + typeName + " size=" + colSize + " columnName=" + rs.getMetaData().getColumnName(index), e);
        }
        return null;
    }
    @Override
    public String fieldQuerySql(String sql) {
        return "SELECT * FROM (" + sql + ") zs limit 1";
    }

}