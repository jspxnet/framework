package com.github.jspxnet.sober.dialect;

import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.*;
import java.util.Date;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/7/31 2:17
 * description: 达梦数据库支持
 **/
public class DmDialect extends Dialect {

    public DmDialect() {                                                                              //number(1)
        put(SQL_CREATE_TABLE, "CREATE TABLE \"${"+ KEY_DATABASE_NAME +"}\".\"${" + KEY_TABLE_NAME + "}\" \n(\n" +
                " <#list column=" + KEY_COLUMN_LIST + ">${column},\n</#list>" +
                " \nCONSTRAINT \"${" + KEY_TABLE_NAME + "}_key\" PRIMARY KEY  (\"${" + KEY_PRIMARY_KEY + "}\")\n)");


        //oracle , pgsql ,db2,dm一样
        //oracle , pgsql ,db2,dm设置注释方式begin
        put(SQL_COMMENT, "COMMENT ON COLUMN \"${"+ KEY_DATABASE_NAME +"}\".\"${" + KEY_TABLE_NAME + "}\".\"${" + COLUMN_NAME + "}\" IS '${" + COLUMN_CAPTION + "}'");
        put(SQL_TABLE_COMMENT, "COMMENT ON TABLE \"${"+ KEY_DATABASE_NAME +"}\".\"${" + KEY_TABLE_NAME + "}\" IS '${" + SQL_TABLE_COMMENT + "}'");
        //oracle 和 pgsql 设置注释方式end

        //oracle
        put(ORACLE_CREATE_SEQUENCE,"create sequence \"${"+ KEY_DATABASE_NAME +"}\".\"${" + KEY_TABLE_NAME + ".toUpperCase()}_SEQ\" minvalue 1 maxvalue 99999999 increment by 1  start with 1");
        put(ORACLE_CREATE_SEQ_TIGGER,"create or replace trigger ${" + KEY_TABLE_NAME + ".toUpperCase()}_SEQ_TIGGER\n" +
                "before insert on \"${"+ KEY_DATABASE_NAME +".toUpperCase()}\".\"${" + KEY_TABLE_NAME + ".toUpperCase()}\"\n" +
                "for each row\r\n" +
                "begin if (:new.${" + KEY_PRIMARY_KEY + ".toUpperCase()} is null or :new.${" + KEY_PRIMARY_KEY + ".toUpperCase()}=0) then select ${"+ KEY_DATABASE_NAME +"}.${" + KEY_TABLE_NAME + ".toUpperCase()}_SEQ.nextval into :new.${" + KEY_PRIMARY_KEY + ".toUpperCase()} from dual; end if; end;");

        put(ORACLE_HAVE_SEQ,"select count(1) as num from user_sequences where sequence_name=upper('${"+ KEY_DATABASE_NAME +".toUpperCase()}.${" + KEY_TABLE_NAME + ".toUpperCase()}_SEQ')");


        put(SQL_QUERY_ONE_FIELD, "SELECT * FROM \"${"+ KEY_DATABASE_NAME +"}\".\"${" + KEY_TABLE_NAME + "}\" WHERE ${" + KEY_FIELD_NAME + "}=?");
        put(SQL_INSERT, "INSERT INTO \"${"+ KEY_DATABASE_NAME +"}\".\"${" + KEY_TABLE_NAME + "}\" (<#list field=" + KEY_FIELD_LIST + ">\"${field}\"<#if where=field_has_next>,</#if></#list>) VALUES (<#list x=1.." + KEY_FIELD_COUNT + ">?<#if x_has_next>,</#if></#list>)");
        put(SQL_DELETE, "DELETE FROM \"${"+ KEY_DATABASE_NAME +"}\".${" + KEY_TABLE_NAME + "}\" WHERE \"${" + KEY_FIELD_NAME + "}\"=<#if where=" + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>${" + KEY_FIELD_VALUE + "}<#if where= " + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>");
        put(SQL_DELETE_IN, "DELETE FROM \"${"+ KEY_DATABASE_NAME +"}\".\"${" + KEY_TABLE_NAME + "}\" WHERE \"${" + KEY_FIELD_NAME + "}\" IN (<#list fvalue=" + KEY_FIELD_VALUE + ">'${fvalue}'<#if where=fvalue_has_next>,</#if></#list>)");
        put(SQL_UPDATE, "UPDATE \"${"+ KEY_DATABASE_NAME +"}\".\"${" + KEY_TABLE_NAME + "}\" SET <#list field=" + KEY_FIELD_LIST + ">\"${field}\"=?<#if where=field_has_next>,</#if></#list> WHERE \"${" + KEY_FIELD_NAME + "}\"=<#if where=" + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>${" + KEY_FIELD_VALUE + "}<#if where=" + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>");
        put(SQL_HAVE, "SELECT count(1) FROM \"${"+ KEY_DATABASE_NAME +"}\".\"${" + KEY_TABLE_NAME + "}\" WHERE ${" + KEY_FIELD_NAME + "}=<#if where=" + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>${" + KEY_FIELD_VALUE + "}<#if where=" + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>");
        put(SQL_CRITERIA_UNIQUERESULT, "SELECT ${" + KEY_FIELD_PROJECTION + "} FROM \"${"+ KEY_DATABASE_NAME +"}\".\"${" + KEY_TABLE_NAME + "}\" <#if where=" + KEY_TERM + "!=''>WHERE ${" + KEY_TERM + "}</#if><#if where=" + KEY_FIELD_GROUPBY + "!=''> GROUP BY ${" + KEY_FIELD_GROUPBY + "}</#if><#if where=" + KEY_FIELD_ORDERBY + "!=''> ORDER BY ${" + KEY_FIELD_ORDERBY + "}</#if>");
        put(SQL_CRITERIA_QUERY, "SELECT * FROM \"${"+ KEY_DATABASE_NAME +"}\".\"${" + KEY_TABLE_NAME + "}\" <#if where=" + KEY_TERM + "!=''>WHERE ${" + KEY_TERM + "}</#if><#if where=" + KEY_FIELD_GROUPBY + "!=''> GROUP BY ${" + KEY_FIELD_GROUPBY + "}</#if><#if where=" + KEY_FIELD_ORDERBY + "!=''> ORDER BY ${" + KEY_FIELD_ORDERBY + "}</#if>");

        put(SQL_CRITERIA_GROUP_QUERY, "SELECT ${" + KEY_FIELD_GROUPBY + "} FROM \"${"+ KEY_DATABASE_NAME +"}\".\"${" + KEY_TABLE_NAME + "}\" <#if where=" + KEY_TERM + "!=''>WHERE ${" + KEY_TERM + "}</#if><#if where=" + KEY_FIELD_GROUPBY + "!=''> GROUP BY ${" + KEY_FIELD_GROUPBY + "}</#if><#if where=" + KEY_FIELD_ORDERBY + "!=''> ORDER BY ${" + KEY_FIELD_ORDERBY + "}</#if>");

        put(SQL_CRITERIA_DELETE, "DELETE FROM \"${"+ KEY_DATABASE_NAME +"}\".\"${" + KEY_TABLE_NAME + "}\" <#if where=" + KEY_TERM + "!=''>WHERE ${" + KEY_TERM + "}</#if>");
        put(SQL_CRITERIA_UPDATE, "UPDATE \"${"+ KEY_DATABASE_NAME +"}\".${" + KEY_TABLE_NAME + "}\" SET <#list field=" + KEY_FIELD_LIST + ">\"${field}\"=?<#if where=field_has_next>,</#if></#list> <#if where=" + KEY_TERM + "!=''>WHERE ${" + KEY_TERM + "}</#if>");


        put(Boolean.class.getName(), "\"${" + COLUMN_NAME + "}\" number(1) default <#if where=!" + COLUMN_DEFAULT + " >0<#else>1</#else></#if>");
        put(boolean.class.getName(), "\"${" + COLUMN_NAME + "}\" number(1) default <#if where=!" + COLUMN_DEFAULT + " >0<#else>1</#else></#if>");
        put(String.class.getName(), "\"${" + COLUMN_NAME + "}\" <#if where=" + COLUMN_LENGTH + "&gt;4000>long<#else>varchar2(${" + COLUMN_LENGTH + "})</#else></#if> <#if where=" + COLUMN_DEFAULT + ">default '${" + COLUMN_DEFAULT + "}'</#if>");
        put(Integer.class.getName(), "\"${" + COLUMN_NAME + "}\" NUMBER(10) <#if where=!" + KEY_FIELD_SERIAL + " >default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#if>");
        put("int", "\"${" + COLUMN_NAME + "}\" <#if where=" + KEY_FIELD_SERIAL + ">SERIAL<#else>NUMBER(10)</#else></#if> <#if where=!" + KEY_FIELD_SERIAL + " >default <#if where=!" + COLUMN_DEFAULT + " >0<#else>${" + COLUMN_DEFAULT + "}</#else></#if></#if>");

        put(Long.class.getName(), "\"${" + COLUMN_NAME + "}\" <#if where=" + COLUMN_LENGTH + "&gt;16>NUMBER(${" + COLUMN_LENGTH + "})<#else>NUMBER(16)</#else></#if> default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");
        put("long", "\"${" + COLUMN_NAME + "}\" <#if where=" + COLUMN_LENGTH + "&gt;16>NUMBER(${" + COLUMN_LENGTH + "})<#else>NUMBER(16)</#else></#if> default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");

        put(Double.class.getName(), "\"${" + COLUMN_NAME + "}\" BINARY_DOUBLE default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");
        put("double", "\"${" + COLUMN_NAME + "}\" BINARY_DOUBLE default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");

        put(Float.class.getName(), "\"${" + COLUMN_NAME + "}\" BINARY_FLOAT default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");
        put("float", "\"${" + COLUMN_NAME + "}\" BINARY_FLOAT default <#if where=!" + COLUMN_DEFAULT + ">0<#else>${" + COLUMN_DEFAULT + "}</#else></#if>");

        put(Date.class.getName(), "\"${" + COLUMN_NAME + "}\" TIMESTAMP default SYSDATE");
        put(byte[].class.getName(), "\"${" + COLUMN_NAME + "}\" blob");
        put(InputStream.class.getName(), "\"${" + COLUMN_NAME + "}\" blob");
        put(char.class.getName(), "\"${" + COLUMN_NAME + "}\" char(2) NOT NULL default ''");
        put(SQL_DROP_TABLE, "DROP TABLE  \"${"+ KEY_DATABASE_NAME +"}\".\"${" + KEY_TABLE_NAME + "}\"");
        put(FUN_TABLE_EXISTS, "SELECT COUNT(1)  FROM all_tables WHERE OWNER='${"+KEY_DATABASE_NAME+"}' AND TABLE_NAME='${" + KEY_TABLE_NAME + "}'");
    }

    @Override
    public String getFieldType(SoberColumn soberColumn) {

        if (ClassUtil.isNumberType(soberColumn.getClassType()))
        {
            if (soberColumn.getClassType()==int.class || soberColumn.getClassType()==Integer.class)
            {
                if (soberColumn.getLength()<3)
                {
                    return "NUMBER("+soberColumn.getLength()+")";
                }
                return "NUMBER(10)";
            }

            if (soberColumn.getClassType()==long.class || soberColumn.getClassType()==Long.class)
            {
                return "NUMBER(16)";
            }

            if (soberColumn.getClassType()==float.class || soberColumn.getClassType()==Float.class)
            {
                return "BINARY_FLOAT";
            }
            if (soberColumn.getClassType()==double.class || soberColumn.getClassType()==Double.class)
            {
                return "BINARY_DOUBLE";
            }
        }
        if (soberColumn.getClassType()==boolean.class || soberColumn.getClassType()==Boolean.class)
        {
            return "number(1)";
        }
        if (soberColumn.getClassType()==String.class)
        {
            if (soberColumn.getLength()<2000)
            {
                return "varchar2("+soberColumn.getLength()+")";
            }
            return "long";
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
        return "varchar2(512)";
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
    public boolean supportsConcurReadOnly() {
        return true;
    }

    @Override
    public void setPreparedStatementValue(PreparedStatement pstmt, int parameterIndex, Object obj) throws Exception {
        if (obj instanceof String) {
            pstmt.setString(parameterIndex, (String) obj);
            return;
        }
        if (obj instanceof Boolean) {
            pstmt.setInt(parameterIndex, ObjectUtil.toInt(ObjectUtil.toBoolean(obj)));
            return;
        }
        if (obj instanceof Clob) {
            pstmt.setClob(parameterIndex, (Clob) obj);
            return;
        }
        if (obj instanceof java.sql.Time) {
            pstmt.setTime(parameterIndex, (java.sql.Time) obj);
            return;
        }
        if (obj instanceof java.sql.Date) {
            pstmt.setDate(parameterIndex, (java.sql.Date) obj);
            return;
        }
        if (obj instanceof java.util.Date) {
            java.util.Date d = (java.util.Date) obj;
            pstmt.setTimestamp(parameterIndex, new Timestamp(d.getTime()));
            return;
        }
        if (obj instanceof InputStream) {
            InputStream b = (InputStream) obj;
            pstmt.setBinaryStream(parameterIndex, b, b.available());
            return;
        }
        if (obj instanceof Blob) {
            pstmt.setBlob(parameterIndex, (Blob) obj);
            return;
        }
        if (obj instanceof Integer) {
            pstmt.setInt(parameterIndex, (Integer) obj);
            return;
        }
        if (obj instanceof Float) {
            pstmt.setFloat(parameterIndex, (Float) obj);
            return;
        }
        if (obj instanceof Double) {
            pstmt.setDouble(parameterIndex, (Double) obj);
            return;
        }
        if (obj instanceof Array) {
            pstmt.setArray(parameterIndex, (Array) obj);
            return;
        }
        if (obj instanceof Short) {
            pstmt.setShort(parameterIndex, (Short) obj);
            return;
        }
        if (obj instanceof Long) {
            pstmt.setLong(parameterIndex, (Long) obj);
            return;
        }
        if (obj instanceof Ref) {
            pstmt.setRef(parameterIndex, (Ref) obj);
            return;
        }

        pstmt.setObject(parameterIndex, obj);
    }


    /**
     * @param rs    返回对象
     * @param index 索引
     * @return 返回查询结果
     * @throws SQLException 异常
     */
    @Override
    public Object getResultSetValue(ResultSet rs, int index) throws SQLException {
        String typeName = rs.getMetaData().getColumnTypeName(index).toLowerCase();
        int colSize = rs.getMetaData().getColumnDisplaySize(index);
        ///////大数值
        if ("ROWID".equalsIgnoreCase(typeName)) {
            return rs.getString(index);
        }

        //短断整型
        if (("int".equals(typeName) && colSize < 4) || "short".equals(typeName) || "smallint".equals(typeName) || "int2".equals(typeName) || "tinyint".equals(typeName) || ("fixed".equals(typeName) && colSize < 4)) {
            return rs.getShort(index);
        }
        //////////整型
        if ("int".equals(typeName) || "integer".equals(typeName) || "int4".equals(typeName) || ("number".equals(typeName) && colSize < 8) || ("fixed".equals(typeName) && colSize < 19)) {
            return rs.getInt(index);
        }

        ///////长整型
        if ("number".equals(typeName) || "bigint".equals(typeName) || "int8".equals(typeName) || ("fixed".equals(typeName))) {
            return rs.getLong(index);
        }

        ///////单精度
        if ("money".equals(typeName) || "float".equals(typeName) || "real".equals(typeName) || "binary_float".equals(typeName)) {
            return rs.getFloat(index);
        }
        ///////大数值
        if ("decimal".equals(typeName)) {
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

        ///////日期时间
        if (typeName.contains("timestamp") || "datetime".equals(typeName)) {
            Timestamp t = rs.getTimestamp(index);
            if (t == null) {
                return null;
            }
            return new java.util.Date(t.getTime());
        }


        ////////////时间
        if ("time".equals(typeName)) {
            return rs.getTime(index);
        }

        ///////短字符串
        if ("char".equals(typeName) || "nvarchar".equals(typeName) || "varchar".equals(typeName)|| "long".equals(typeName) || "varchar2".equals(typeName) || "tinyblob".equals(typeName)) {
            return rs.getString(index);
        }

        ////////////大文本类型
        if ("CLOB".equals(typeName) || "mediumtext".equals(typeName) || " long varchar".equals(typeName)
                || "ntext".equals(typeName) || "text".equals(typeName) || "long raw".equals(typeName)) {
            //oracle.sql.CLOB clob = (oracle.sql.CLOB) rs.getClob(index);
            Clob clob = rs.getClob(index);
            if (clob == null) {
                return StringUtil.empty;
            }
            Reader bodyReader = clob.getCharacterStream();

            StringWriter out = new StringWriter(255);
            try {
                char[] buf = new char[256];
                int i;
                while ((i = bodyReader.read(buf)) != -1) {
                    out.write(buf, 0, i);
                }
                out.close();
                bodyReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return out.toString();
        }

        ///////二进制类型 文件类型
        if ("image".equals(typeName) || "blob".equals(typeName) || "mediumblob".equals(typeName)
                || "longblob".equals(typeName) || "dbclob".equals(typeName)
                || "long byte".equals(typeName)
                || "varbinary".equals(typeName) || "binary".equals(typeName)) {
            // oracle.sql.BLOB blob = (oracle.sql.BLOB) rs.getBlob(index);
            // Clob clob = rs.getClob(index);
            // return clob.getBinaryStream();
            return rs.getAsciiStream(index);
        }
        return rs.getObject(index);
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
        return true;
    }

}