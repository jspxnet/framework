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
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.InputStream;
import java.sql.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-6
 * Time: 22:08:11
 * MySQL数据库 SQL匹配
 */
@Slf4j
public class MySQLDialect extends Dialect {

    public MySQLDialect() {
        //ENGINE=MyISAM
        put(SQL_CREATE_TABLE, "CREATE TABLE `${" + KEY_TABLE_NAME + "}` \n(\n" +
                " <#list column=" + KEY_COLUMN_LIST + ">${column},\n</#list>" +
                " \nPRIMARY KEY  (`${" + KEY_PRIMARY_KEY + "}`)\n) COMMENT = '${"+ KEY_TABLE_CAPTION +"}';"); //DEFAULT CHARSET=UTF8

        put(SQL_INSERT, "INSERT INTO ${" + KEY_TABLE_NAME + "} (<#list field=" + KEY_FIELD_LIST + ">`${field}`<#if where=field_has_next>,</#if></#list>) VALUES (<#list x=1.." + KEY_FIELD_COUNT + ">?<#if x_has_next>,</#if></#list>)");
        put(SQL_DELETE, "DELETE FROM ${" + KEY_TABLE_NAME + "} WHERE `${" + KEY_FIELD_NAME + "}`=<#if where=" + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>${" + KEY_FIELD_VALUE + "}<#ifwhere= " + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>");
        put(SQL_DELETE_IN, "DELETE FROM ${" + KEY_TABLE_NAME + "} WHERE `${" + KEY_FIELD_NAME + "}` IN (<#list fvalue=" + KEY_FIELD_VALUE + ">'${fvalue}'<#if where=fvalue_has_next>,</#if></#list>)");
        put(SQL_UPDATE, "UPDATE ${" + KEY_TABLE_NAME + "} SET <#list field=" + KEY_FIELD_LIST + ">`${field}`=?<#if where=field_has_next>,</#if></#list> WHERE ${" + KEY_FIELD_NAME + "}=<#if where=" + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>${" + KEY_FIELD_VALUE + "}<#if where=" + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>");

        put(String.class.getName(), "`${" + COLUMN_NAME + "}` <#if where=\"" + COLUMN_LENGTH + "&gt;512\"><#if where=\"" + COLUMN_LENGTH + "&lt;30000\" >text<#else>mediumtext</#else></#if><#else>varchar(${" + COLUMN_LENGTH + "})</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=" + COLUMN_DEFAULT + ">default '${" + COLUMN_DEFAULT + "}'</#if> COMMENT '${" + COLUMN_CAPTION + "}'");

        put(Integer.class.getName(), "`${" + COLUMN_NAME + "}` <#if where=\"" + COLUMN_LENGTH + "!=0&&"+ COLUMN_LENGTH +"&lt;4\">tinyint(${" + COLUMN_LENGTH + "})<#else>integer</#else></#if> <#if where=" + KEY_FIELD_SERIAL + ">AUTO_INCREMENT</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else>  default <#if where=!" + COLUMN_DEFAULT + ">0<#else>'${" + COLUMN_DEFAULT + "}'</#else></#if></#else></#if> COMMENT '${" + COLUMN_CAPTION + "}'");
        put("int", "`${" + COLUMN_NAME + "}` <#if where=\"" + COLUMN_LENGTH + "!=0&&"+ COLUMN_LENGTH +"&lt;4\">tinyint(${" + COLUMN_LENGTH + "})<#else>integer</#else></#if> <#if where=" + KEY_FIELD_SERIAL + ">AUTO_INCREMENT</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else>  default <#if where=!" + COLUMN_DEFAULT + ">0<#else>'${" + COLUMN_DEFAULT + "}'</#else></#if></#else></#if> COMMENT '${" + COLUMN_CAPTION + "}'");

        put(Long.class.getName(), "`${" + COLUMN_NAME + "}` <#if where=" + COLUMN_LENGTH + "&gt;16>bigint(${" + COLUMN_LENGTH + "})<#else>bigint(16)</#else></#if> <#if where=" + KEY_FIELD_SERIAL + ">AUTO_INCREMENT</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else>  default <#if where=!" + COLUMN_DEFAULT + ">'0'<#else>'${" + COLUMN_DEFAULT + "}'</#else></#if></#else></#if> COMMENT '${" + COLUMN_CAPTION + "}'");
        put("long", "`${" + COLUMN_NAME + "}` <#if where=" + COLUMN_LENGTH + "&gt;16>bigint(${" + COLUMN_LENGTH + "})<#else>bigint(16)</#else></#if> <#if where=" + KEY_FIELD_SERIAL + ">AUTO_INCREMENT</#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL<#else>  default <#if where=!" + COLUMN_DEFAULT + ">0<#else>'${" + COLUMN_DEFAULT + "}'</#else></#if></#else></#if> COMMENT '${" + COLUMN_CAPTION + "}'");
        put(Double.class.getName(), "`${" + COLUMN_NAME + "}` <#if where=" + COLUMN_LENGTH + "&gt;15>decimal(${" + COLUMN_LENGTH + "},3)<#else>decimal(15,3)</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if> COMMENT '${" + COLUMN_CAPTION + "}'");
        put("double", "`${" + COLUMN_NAME + "}` <#if where=" + COLUMN_LENGTH + "&gt;15>decimal(${" + COLUMN_LENGTH + "},3)<#else>decimal(15,3)</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if> COMMENT '${" + COLUMN_CAPTION + "}'");
        put(Float.class.getName(), "`${" + COLUMN_NAME + "}` <#if where=" + COLUMN_LENGTH + "&gt;9>decimal(${" + COLUMN_LENGTH + "},2)<#else>decimal(10,2)</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if> COMMENT '${" + COLUMN_CAPTION + "}'");
        put("float", "`${" + COLUMN_NAME + "}` <#if where=" + COLUMN_LENGTH + "&gt;9>decimal(${" + COLUMN_LENGTH + "},2)<#else>decimal(9,2)</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if> COMMENT '${" + COLUMN_CAPTION + "}'");

        put(Boolean.class.getName(), "`${" + COLUMN_NAME + "}` int(1) <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=" + COLUMN_DEFAULT + ">default ${" + COLUMN_DEFAULT + ".toInt()}</#if>  COMMENT '${" + COLUMN_CAPTION + "}'");
        put(boolean.class.getName(), "`${" + COLUMN_NAME + "}` int(1) <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=" + COLUMN_DEFAULT + ">default ${" + COLUMN_DEFAULT + ".toInt()}</#if>  COMMENT '${" + COLUMN_CAPTION + "}'");

        put(Date.class.getName(), "`${" + COLUMN_NAME + "}` datetime <#if where=" + COLUMN_NOT_NULL + ">NOT NULL DEFAULT now()</#if> COMMENT '${" + COLUMN_CAPTION + "}'");
        put(Time.class.getName(), "`${" + COLUMN_NAME + "}` time <#if where=" + COLUMN_NOT_NULL + ">NOT NULL DEFAULT '${" + COLUMN_DEFAULT + "}'</#if> COMMENT '${" + COLUMN_CAPTION + "}'");

        put(byte[].class.getName(), "`${" + COLUMN_NAME + "}` LONGBLOB COMMENT '${" + COLUMN_CAPTION + "}'");
        put(InputStream.class.getName(), "`${" + COLUMN_NAME + "}` LONGBLOB COMMENT '${" + COLUMN_CAPTION + "}'");
        put(char.class.getName(), "`${" + COLUMN_NAME + "}` char(2) <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> <#if where=" + COLUMN_DEFAULT + ">default '${" + COLUMN_DEFAULT + "}'</#if> COMMENT '${" + COLUMN_CAPTION + "}'");
        put(SQL_DROP_TABLE, "DROP TABLE IF EXISTS ${" + KEY_TABLE_NAME + "}");
        put(FUN_TABLE_EXISTS, "show tables like '${" + KEY_TABLE_NAME + "}'");


        //修改系列开始
        //ALTER SEQUENCE ${" + SERIAL_NAME + "} RESTART WITH ${" + KEY_SEQUENCE_RESTART + "}
        put(ALTER_SEQUENCE_RESTART, "Alter Table ${" + KEY_TABLE_NAME + "} Auto_increment=${" + KEY_SEQUENCE_RESTART + "}");

        put(SQL_TABLE_NAMES, "SHOW TABLES");

        put(DATABASE_SIZE, "SELECT SUM(data_length) + SUM(index_length) as size FROM information_schema.tables where table_schema = '${" + KEY_TABLE_NAME + "}'");

        put(SEQUENCE_NAME, "SHOW TABLE STATUS LIKE '${" + KEY_TABLE_NAME + "}'");

        //创建索引,一个一个
        put(SQL_CREATE_TABLE_INDEX, "ALTER TABLE `${" + KEY_TABLE_NAME + "}` ADD <#if where=" + KEY_IS_UNIQUE + ">unique</#if> INDEX `${"+KEY_INDEX_NAME+"}`(${"+KEY_INDEX_FIELD+"})");

    }

    @Override
    public String getLimitString(String sql, int begin, int end, TableModels soberTable) {
        int length = end - begin;
        if (length < 0) {
            length = 0;
        }
        return sql + " limit " + begin + "," + length;
    }

/*
    @Override
    public void setPreparedStatementValue(PreparedStatement pstmt, int parameterIndex, Object obj) throws Exception {
        super.setPreparedStatementValue(pstmt, parameterIndex, obj);
    }
*/

    @Override
    public boolean supportsSequenceName() {
        return true;
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    public boolean supportsSavePoints() {
        return true;
    }

    public boolean supportsGetGeneratedKeys() {
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

            //短断整型
            if (("int".equals(typeName) && colSize < 4) || "tinyint".equalsIgnoreCase(typeName) || "short".equals(typeName) || "smallint".equals(typeName) || "int2".equals(typeName) || ("fixed".equalsIgnoreCase(typeName) && colSize < 4)) {
                return rs.getShort(index);
            }

            ///////长整型
            if ("bigserial".equals(typeName) || "long".equals(typeName) || "bigint".equals(typeName) || "int8".equals(typeName) || ("fixed".equals(typeName) && colSize > 18)) {
                return rs.getLong(index);
            }

            //////////整型
            if ("integer".equals(typeName) || "serial".equals(typeName) || typeName.contains("int") || ("number".equals(typeName) && colSize < 8) || ("fixed".equals(typeName))) {
                return rs.getInt(index);
            }

            ///////单精度
            if ("money".equals(typeName) || "float".equals(typeName) || "real".equals(typeName) || "binary_float".equals(typeName)) {
                return rs.getFloat(index);
            }
            ///////大数值
            if (typeName.contains("decimal")) {
                //mysql float 精度有问题,默认使用 decimal
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
                Timestamp t = rs.getTimestamp(index);
                if (t == null) {
                    return null;
                }
                return new java.util.Date(t.getTime());
            }

            ////////////时间
            if ("time".equalsIgnoreCase(typeName)) {
                Time t = rs.getTime(index);
                if (t == null) {
                    return null;
                }
                return t;
            }

            ///////短字符串
            if ("char".equals(typeName) || "text".equals(typeName) || "nvarchar".equals(typeName) || "varchar".equals(typeName) || "varchar2".equals(typeName)) {
                return rs.getString(index);
            }

            ///////二进制类型 文件类型
            if ("bytea".equalsIgnoreCase(typeName) || typeName.contains("blob") || "image".equalsIgnoreCase(typeName) || "long byte".equalsIgnoreCase(typeName)
                    || "varbinary".equalsIgnoreCase(typeName) || "binary".equalsIgnoreCase(typeName)) {

                Blob blob = rs.getBlob(index);
                if (blob == null) {
                    return StringUtil.empty;
                }
                return blob.getBinaryStream();
            }
            return rs.getObject(index);
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("typeName=" + typeName + " size=" + colSize + " columnName=" + rs.getMetaData().getColumnName(index), e);
        }
        return null;
    }

}