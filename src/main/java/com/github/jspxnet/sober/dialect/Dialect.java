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
import com.github.jspxnet.boot.environment.Placeholder;
import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.util.DataMap;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-6
 * Time: 18:05:33
 * 通用SQL转换器
 */
@Slf4j
public abstract class Dialect extends HashMap<String,String> {
    final protected static Placeholder PLACEHOLDER = EnvFactory.getPlaceholder();
    final protected static Placeholder SQL_PLACEHOLDER = EnvFactory.getSqlPlaceholder();


    public static final String ORACLE_CREATE_SEQUENCE = "oracle_create_sequence";
    public static final String ORACLE_CREATE_SEQ_TIGGER = "oracle_create_seq_tigger";
    public static final String ORACLE_HAVE_SEQ = "oracle_have_seq";

    public static final String KEY_DATABASE_NAME = "database_name";
    public static final String KEY_TABLE_NAME = "table_name";
    public static final String KEY_TABLE_CAPTION = "table_caption";

    public static final String KEY_FIELD_NAME = "field_name";
    public static final String KEY_FIELD_SERIAL = "field_serial";

    public static final String KEY_SEQUENCE_RESTART = "field_sequence_restart";

    public static final String KEY_INDEX_NAME = "index_name";
    public static final String KEY_INDEX_FIELD = "index_field";
    //是否为唯一索引
    public static final String KEY_IS_UNIQUE = "isUnique";


    public static final String FIELD_QUOTE = "_quote";
    public static final String KEY_FIELD_VALUE = "field_value";
    public static final String KEY_FIELD_LIST = "field_list";
    public static final String KEY_FIELD_COUNT = "field_count";
    public static final String KEY_TERM = "field_term";
    public static final String KEY_FIELD_GROUPBY = "field_groupby";
    public static final String KEY_FIELD_ORDERBY = "field_orderby";
    public static final String KEY_COLUMN_LIST = "column_list";
    public static final String KEY_FIELD_PROJECTION = "field_projection";
    public static final String KEY_PRIMARY_KEY = "primary_key";
    public static final String KEY_PRIMARY_VALUE = "primary_key_value";
    public static final String KEY_IS_DROP = "sql_is_drop";
    public static final String SERIAL_NAME = "serial_name";


    public static final String SQL_QUERY_ONE_FIELD = "sql_query_one_field";
    public static final String SQL_INSERT = "sql_insert";
    public static final String SQL_DELETE = "sql_delete";
    public static final String SQL_UPDATE = "sql_update";
    //public static final String SQL_UPDATE_FIELD = "sql_update_field";

    public static final String SQL_RESULT_BEGIN_ROW = "sql_result_begin_row";
    public static final String SQL_RESULT_END_ROW = "sql_result_end_row";

    public static final String SQL_HAVE = "sql_have";
    public static final String SQL_DELETE_IN = "sql_delete_in";
    public static final String SQL_CRITERIA_QUERY = "sql_criteria_query";
    public static final String SQL_CRITERIA_UNIQUERESULT = "sql_criteria_uniqueresult";
    public static final String SQL_CRITERIA_DELETE = "sql_criteria_delete";
    public static final String SQL_CRITERIA_UPDATE = "sql_criteria_updata";
    public static final String SQL_CREATE_TABLE = "sql_create_table";
    public static final String SQL_CREATE_TABLE_INDEX = "sql_create_table_index";
    public static final String SQL_DROP_TABLE = "sql_drop_table";
    public static final String SQL_COMMENT = "sql_comment";
    public static final String SQL_TABLE_COMMENT = "sql_table_comment";

    public static final String COLUMN_NAME = "column_name";
    public static final String COLUMN_LENGTH = "column_length";
    public static final String COLUMN_NOT_NULL = "column_not_null";
    public static final String COLUMN_DEFAULT = "column_default";
    public static final String COLUMN_CAPTION = "column_caption";

    public static final String FUN_TABLE_EXISTS = "fun_table_exists";

    public static final String ALTER_SEQUENCE_RESTART = "alter_sequence_restart";

    public static final String TABLE_MAX_ID = "table_max_id";

    public static final String DATABASE_SIZE = "database_size";

    public static final String SEQUENCE_NAME = "sequence_name";

    //得到表的名称
    public static final String SQL_TABLE_NAMES = "sql_table_names";

    public static final String SQL_CRITERIA_GROUP_QUERY = "sql_criteria_group_query";

//    public final Map<String, String> standard_SQL = new HashMap<>(100);

    public Dialect() {
        put(SQL_QUERY_ONE_FIELD, "SELECT * FROM ${" + KEY_TABLE_NAME + "} WHERE ${" + KEY_FIELD_NAME + "}=?");
        put(SQL_INSERT, "INSERT INTO ${" + KEY_TABLE_NAME + "} (<#list field=" + KEY_FIELD_LIST + ">${field}<#if where=field_has_next>,</#if></#list>) VALUES (<#list x=1.." + KEY_FIELD_COUNT + ">?<#if x_has_next>,</#if></#list>)");
        put(SQL_DELETE, "DELETE FROM ${" + KEY_TABLE_NAME + "} WHERE ${" + KEY_FIELD_NAME + "}=<#if where=" + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>${" + KEY_FIELD_VALUE + "}<#if where= " + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>");
        put(SQL_DELETE_IN, "DELETE FROM ${" + KEY_TABLE_NAME + "} WHERE ${" + KEY_FIELD_NAME + "} IN (<#list fvalue=" + KEY_FIELD_VALUE + ">'${fvalue}'<#if where=fvalue_has_next>,</#if></#list>)");
        put(SQL_UPDATE, "UPDATE ${" + KEY_TABLE_NAME + "} SET <#list field=" + KEY_FIELD_LIST + ">${field}=?<#if where=field_has_next>,</#if></#list> WHERE ${" + KEY_FIELD_NAME + "}=<#if where=" + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>${" + KEY_FIELD_VALUE + "}<#if where=" + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>");
        put(SQL_HAVE, "SELECT COUNT(1) FROM ${" + KEY_TABLE_NAME + "} WHERE ${" + KEY_FIELD_NAME + "}=<#if where=" + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>${" + KEY_FIELD_VALUE + "}<#if where=" + KEY_FIELD_NAME + FIELD_QUOTE + ">'</#if>");
        put(SQL_CRITERIA_UNIQUERESULT, "SELECT ${" + KEY_FIELD_PROJECTION + "} FROM ${" + KEY_TABLE_NAME + "} <#if where=" + KEY_TERM + "!=''>WHERE ${" + KEY_TERM + "}</#if><#if where=" + KEY_FIELD_GROUPBY + "!=''> GROUP BY ${" + KEY_FIELD_GROUPBY + "}</#if><#if where=" + KEY_FIELD_ORDERBY + "!=''> ORDER BY ${" + KEY_FIELD_ORDERBY + "}</#if>");
        put(SQL_CRITERIA_QUERY, "SELECT * FROM ${" + KEY_TABLE_NAME + "} <#if where=" + KEY_TERM + "!=''>WHERE ${" + KEY_TERM + "}</#if><#if where=" + KEY_FIELD_GROUPBY + "!=''> GROUP BY ${" + KEY_FIELD_GROUPBY + "}</#if><#if where=" + KEY_FIELD_ORDERBY + "!=''> ORDER BY ${" + KEY_FIELD_ORDERBY + "}</#if>");

        put(SQL_CRITERIA_GROUP_QUERY, "SELECT ${" + KEY_FIELD_GROUPBY + "} FROM ${" + KEY_TABLE_NAME + "} <#if where=" + KEY_TERM + "!=''>WHERE ${" + KEY_TERM + "}</#if><#if where=" + KEY_FIELD_GROUPBY + "!=''> GROUP BY ${" + KEY_FIELD_GROUPBY + "}</#if><#if where=" + KEY_FIELD_ORDERBY + "!=''> ORDER BY ${" + KEY_FIELD_ORDERBY + "}</#if>");

        put(SQL_CRITERIA_DELETE, "DELETE FROM ${" + KEY_TABLE_NAME + "} <#if where=" + KEY_TERM + "!=''>WHERE ${" + KEY_TERM + "}</#if>");
        put(SQL_CRITERIA_UPDATE, "UPDATE ${" + KEY_TABLE_NAME + "} SET <#list field=" + KEY_FIELD_LIST + ">${field}=?<#if where=field_has_next>,</#if></#list> <#if where=" + KEY_TERM + "!=''>WHERE ${" + KEY_TERM + "}</#if>");

        put(FUN_TABLE_EXISTS, "desc ${" + KEY_TABLE_NAME + "}");
        put(SQL_DROP_TABLE, "DROP TABLE IF EXISTS ${" + KEY_TABLE_NAME + "}");
        put(SQL_CREATE_TABLE, "CREATE TABLE ${" + KEY_TABLE_NAME + "} \n(" +
                " <#list column=" + KEY_COLUMN_LIST + ">${column}<#if column_has_next>,\n</#if></#list>" +
                "  PRIMARY KEY  (${" + KEY_PRIMARY_KEY + "})\n)");

        put(String.class.getName(), "${" + COLUMN_NAME + "} <#if where=" + COLUMN_LENGTH + "&gt;255 >text<#else>varchar(${" + COLUMN_LENGTH + "})</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default '${" + COLUMN_DEFAULT + "}'");

        put(Integer.class.getName(), "${" + COLUMN_NAME + "} integer <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default ${" + COLUMN_DEFAULT + "}");
        put(Boolean.class.getName(), "${" + COLUMN_NAME + "} int(1) <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default ${" + COLUMN_DEFAULT + "}");
        put(boolean.class.getName(), "${" + COLUMN_NAME + "} int(1) <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default '${" + COLUMN_DEFAULT + "}'");
        put(Long.class.getName(), "${" + COLUMN_NAME + "} <#if where=" + COLUMN_LENGTH + "&gt;16>bigint(${" + COLUMN_LENGTH + "})<#else>bigint(16)</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default ${" + COLUMN_DEFAULT + "}");
        put(Double.class.getName(), "${" + COLUMN_NAME + "} <#if where=" + COLUMN_LENGTH + "&gt;15>double(${" + COLUMN_LENGTH + "},3)<#else>double(15,3)</#else></#if> <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default ${" + COLUMN_DEFAULT + "}");
        put(Float.class.getName(), "${" + COLUMN_NAME + "} <#if where=" + COLUMN_LENGTH + "&gt;9>float(${" + COLUMN_LENGTH + "},3)<#else>float(9,3)</#if></#else><#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default ${" + COLUMN_DEFAULT + "}");
        put(Date.class.getName(), "${" + COLUMN_NAME + "} datetime NOT NULL default '0000-00-00 00:00:00'");
        put(Time.class.getName(), "${" + COLUMN_NAME + "} time DEFAULT <#if where=" + COLUMN_NOT_NULL + ">NOT NULL</#if> default '${" + COLUMN_DEFAULT + "}'");
        put(byte[].class.getName(), "${" + COLUMN_NAME + "} blob");
        put(InputStream.class.getName(), "${" + COLUMN_NAME + "} blob");
        put(char.class.getName(), "${" + COLUMN_NAME + "} char(2) NOT NULL default ''");
        put(ALTER_SEQUENCE_RESTART, "ALTER SEQUENCE serial RESTART WITH ${" + KEY_SEQUENCE_RESTART + "}");
        put(TABLE_MAX_ID, "SELECT MAX(${" + KEY_PRIMARY_KEY + "}) AS maxId FROM ${" + KEY_TABLE_NAME + "}");
        put(SQL_CREATE_TABLE_INDEX, "ALTER TABLE ${" + KEY_TABLE_NAME + "} ADD <#if where=" + KEY_IS_UNIQUE + ">unique</#if> INDEX ${"+KEY_INDEX_NAME+"}(${"+KEY_INDEX_FIELD+"})");




    }

    public abstract boolean supportsSequenceName();

    /**
     * @return 是否支持翻页查询
     */
    public abstract boolean supportsLimit();

    /**
     * pgsql 需要补充sql才能设置 注释,mysql不需要
     *
     * @return 创建后是否需要注释补充语句
     */
    public abstract boolean commentPatch();


    /**
     * @param sql        sql
     * @param begin      开始
     * @param end        结束
     * @param soberTable 表信息
     * @return 返回sql
     */
    public abstract String getLimitString(String sql, int begin, int end, TableModels soberTable);


    abstract public boolean supportsConcurReadOnly();


    public String processSql(String sql, Map<String, Object> valueMap) {
        try {
            if (sql.contains("<#"))
            {
                return PLACEHOLDER.processTemplate(valueMap, sql);
            }
            return SQL_PLACEHOLDER.processTemplate(valueMap, sql);
        } catch (Exception e) {
            log.error(sql + "  " + MapUtil.toString(valueMap), e);
            throw e;
        }
    }

    public String processTemplate(String sqlKey, Map<String, Object> valueMap) {
        try {
            return PLACEHOLDER.processTemplate(valueMap, get(sqlKey));
        } catch (Throwable e) {
            log.error("sql:{},keys:{},Throwable:{}", sqlKey, get(sqlKey), e.getMessage());
            for (String key : valueMap.keySet()) {
                log.error(key + StringUtil.EQUAL + valueMap.get(key));
            }
        }
        return null;
    }


    public void setPreparedStatementValue(PreparedStatement ps, int parameterIndex, Object obj) throws Exception {
        if (ps == null) {
            return;
        }
        if (obj == null) {
            ps.setNull(parameterIndex, Types.NULL);
            return;
        }
        if (obj instanceof String) {
            ps.setString(parameterIndex, (String) obj);
            return;
        }
        if (obj instanceof Clob) {
            ps.setClob(parameterIndex, (Clob) obj);
            return;
        }
        if (obj instanceof java.sql.Time) {
            ps.setTime(parameterIndex, (java.sql.Time) obj);
            return;
        }
        if (obj instanceof java.sql.Date) {
            ps.setDate(parameterIndex, (java.sql.Date) obj);
            return;
        }
        if (obj instanceof java.util.Date) {
            java.util.Date d = (java.util.Date) obj;
            ps.setTimestamp(parameterIndex, new Timestamp(d.getTime()));
            return;
        }
        if (obj instanceof byte[]) {
            byte[] d = (byte[]) obj;
            ps.setBytes(parameterIndex, d);
            return;
        }
        if (obj instanceof InputStream) {
            InputStream b = (InputStream) obj;
            ps.setBinaryStream(parameterIndex, b, b.available());
            return;
        }
        if (obj instanceof Blob) {
            ps.setBlob(parameterIndex, (Blob) obj);
            return;
        }
        if (obj instanceof Integer) {
            ps.setInt(parameterIndex, (Integer) obj);
            return;
        }
        if (obj instanceof Boolean) {
            ps.setBoolean(parameterIndex, ObjectUtil.toBoolean(obj));
            return;
        }
        if (obj instanceof Float) {
            ps.setFloat(parameterIndex, (Float) obj);
            return;
        }
        if (obj instanceof Double) {
            ps.setDouble(parameterIndex, (Double) obj);
            return;
        }
        if (obj instanceof Array) {
            ps.setArray(parameterIndex, (Array) obj);
            return;
        }
        if (obj instanceof Short) {
            ps.setShort(parameterIndex, (Short) obj);
            return;
        }
        if (obj instanceof Long) {
            ps.setLong(parameterIndex, (Long) obj);
            return;
        }
        if (obj instanceof Ref) {
            ps.setRef(parameterIndex, (Ref) obj);
            return;
        }
        ps.setObject(parameterIndex, obj);
    }


    /**
     *
     * @param rs    数据
     * @param index 索引
     * @return 返回查询结果
     * @throws SQLException 异常
     */
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
            if (("int".equals(typeName) && colSize < 4) || "short".equals(typeName) || "smallint".equals(typeName) || "int2".equals(typeName) || "tinyint".equals(typeName) || ("fixed".equals(typeName) && colSize < 4)) {
                return rs.getShort(index);
            }

            ///////长整型
            if ("bigserial".equals(typeName) || "long".equals(typeName) || "bigint".equals(typeName) || "int8".equals(typeName) || ("fixed".equals(typeName) && colSize > 18)) {
                return rs.getLong(index);
            }

            //////////整型
            if ("integer".equals(typeName) || "serial".equals(typeName) || typeName.toLowerCase().contains("int") || ("number".equals(typeName) && colSize < 8) || ("fixed".equals(typeName) && colSize < 19)) {
                return rs.getInt(index);
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
            if ("date".equals(typeName) || "datetime".equals(typeName)) {
                Date t = rs.getDate(index);
                if (t == null) {
                    return null;
                }
                return new java.util.Date(t.getTime());
            }

            ///////日期时间java.sql.Timestamp
            if (typeName.toLowerCase().contains("timestamp")) {
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
            if ("char".equals(typeName) || "nvarchar".equals(typeName) || "varchar".equals(typeName) || "varchar2".equals(typeName) || "tinyblob".equals(typeName)) {
                return rs.getString(index);
            }

            //|| typeName.equals("long varchar")
            ////////////大文本类型
            if ("clob".equals(typeName) || "mediumtext".equals(typeName)
                    || "ntext".equals(typeName) || "text".equals(typeName) || "long raw".equals(typeName)) {

                Clob clob = rs.getClob(index);
                if (clob == null) {
                    return StringUtil.empty;
                }
                Reader bodyReader = clob.getCharacterStream();
                StringWriter out = new StringWriter();
                try {
                    char[] buf = new char[256];
                    int i;
                    while ((i = bodyReader.read(buf)) != -1) {
                        out.write(buf, 0, i);
                    }
                    out.close();
                    bodyReader.close();
                } catch (Exception e) {
                    log.error(typeName + StringUtil.EQUAL + index, e);
                }
                return out.toString();
            }

            ///////二进制类型 文件类型
            if ("bytea".equalsIgnoreCase(typeName) || "blob".equalsIgnoreCase(typeName) || "mediumblob".equalsIgnoreCase(typeName)
                    || "longblob".equalsIgnoreCase(typeName) || "dbclob".equalsIgnoreCase(typeName)
                    || "image".equalsIgnoreCase(typeName) || "long byte".equalsIgnoreCase(typeName)
                    || "varbinary".equals(typeName) || "binary".equals(typeName) || "long byte".equals(typeName)) {

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

    /**
     * @param rs   数据
     * @param name 索引
     * @return 返回查询结果
     * @throws SQLException 异常
     */
    public Object getResultSetValue(ResultSet rs, String name) throws SQLException {
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        int index = 0;
        if (name != null) {
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                if (name.equalsIgnoreCase(resultSetMetaData.getColumnName(i)) || name.equalsIgnoreCase(resultSetMetaData.getColumnLabel(i))) {
                    index = i;
                }
            }
        }
        if (index == 0) {
            return null;
        }
        return getResultSetValue(rs, index);
    }
    //能够自动获取的配置数据begin



    /**
     * @param map    数据
     * @return 返回查询结果
     *
     * {
     * 		"numPrecRadix": 10,
     * 		"sourceDataType": 0,
     * 		"isAutoincrement": "NO",
     * 		"nullable": 0,
     * 		"dataType": 12,
     * 		"tableCat": null,
     * 		"typeName": "varchar",
     * 		"ordinalPosition": 5,
     * 		"scopeTable": null,
     * 		"tableName": "jbbs_journey",
     * 		"scopeCatalog": null,
     * 		"tableSchem": "public",
     * 		"sqlDataType": 0,
     * 		"charOctetLength": "200",
     * 		"sqlDatetimeSub": 0,
     * 		"columnSize": 200,
     * 		"isNullable": "NO",
     * 		"bufferLength": null,
     * 		"isGeneratedcolumn": "",
     * 		"decimalDigits": 0,
     * 		"columnDef": null,
     * 		"scopeSchema": null,
     * 		"remarks": "城市",
     * 		"columnName": "city"
     *  }
     */
    public SoberColumn getJavaType(Map<String,Object> map)
    {
        if (ObjectUtil.isEmpty(map)) {
            return null;
        }
        DataMap<String,Object> rs = new DataMap<>();
        rs.putAll(map);
        SoberColumn column = new SoberColumn();
        String typeName = StringUtil.toLowerCase(rs.getValue("typeName"));

        int colSize = rs.getInt("columnSize");
        column.setLength(colSize);
        column.setCaption(rs.getValue("remarks"));
        column.setName(rs.getValue("columnName"));
        column.setNotNull(!ObjectUtil.toBoolean(rs.getValue("isNullable")));

        //短断整型
        if (("int".equals(typeName) && colSize < 4) || "short".equals(typeName) || "smallint".equals(typeName) || "int2".equals(typeName) || "tinyint".equals(typeName) || ("fixed".equals(typeName) && colSize < 4)) {
            column.setClassType(Short.class);
            return column;
        }

        ///////长整型
        if ("bigserial".equals(typeName) || "long".equals(typeName) || "bigint".equals(typeName) || "int8".equals(typeName) || ("fixed".equals(typeName) && colSize > 18)) {
            column.setClassType(Long.class);
            return column;
        }

        //////////整型
        if ("integer".equals(typeName) || "serial".equals(typeName) || typeName.toLowerCase().contains("int") || ("number".equals(typeName) && colSize < 8) || ("fixed".equals(typeName) && colSize < 19)) {
            column.setClassType(Integer.class);
            return column;
        }

        ///////单精度
        if ("money".equals(typeName) || "float".equals(typeName) || "real".equals(typeName) || "binary_float".equals(typeName)) {
            column.setClassType(Float.class);
            return column;
        }
        ///////大数值
        if ("decimal".equals(typeName)) {
            column.setClassType(BigDecimal.class);
            return column;
        }
        ///////双精度
        if ("double".equals(typeName) || "double precision".equals(typeName) || "binary_double".equals(typeName)) {
            column.setClassType(Double.class);
            return column;
        }

        ///////日期
        if (typeName.contains("date")) {
            column.setClassType(java.util.Date.class);
            return column;
        }

        ///////日期时间java.sql.Timestamp
        if (typeName.contains("timestamp")) {
            column.setClassType(java.util.Date.class);
            return column;
        }

        ////////////时间
        if ("time".equals(typeName)) {
            column.setClassType(Time.class);
            return column;
        }
        column.setClassType(String.class);
        return column;
    }


    private boolean supportsSavePoints = false;

    public void setSupportsSavePoints(boolean supportsSavePoints) {
        this.supportsSavePoints = supportsSavePoints;
    }

    public boolean isSupportsSavePoints() {
        return supportsSavePoints;
    }

    /**
     * 是否支持自动生成key并且能够返回key
     */
    private boolean supportsGetGeneratedKeys = false;

    public boolean isSupportsGetGeneratedKeys() {
        return supportsGetGeneratedKeys;
    }

    public void setSupportsGetGeneratedKeys(boolean supportsGetGeneratedKeys) {
        this.supportsGetGeneratedKeys = supportsGetGeneratedKeys;
    }
    //能够自动获取的配置数据end
}