/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.util;

import com.github.jspxnet.datasource.ConnectionProxy;
import com.github.jspxnet.sober.SoberEnv;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.dialect.Dialect;
import com.github.jspxnet.sober.dialect.GeneralDialect;
import com.github.jspxnet.sober.enums.DatabaseEnumType;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 *
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-6
 * Time: 18:09:10
 */
@Slf4j
public final class JdbcUtil {
    private JdbcUtil()
    {

    }

    public static Object[] appendArray(Object[] array, Object append) {
        if (array == null) {
            array = new Object[1];
            array[0] = append;
            return array;
        }
        Object[] result = new Object[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = append;
        return result;
    }

    public static Object[] appendArray(Object[] array, Object[] append) {
        if (array == null) {
            return append;
        }
        if (append == null) {
            return array;
        }
        int length = array.length + append.length;
        Object[] result = new Object[length];
        System.arraycopy(array, 0, result, 0, array.length);
        System.arraycopy(append, 0, result, array.length, length - array.length);
        return result;
    }

    /**
     * @param stmt 关闭查询器
     */
    public static void closeStatement(Statement stmt) {
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.clearWarnings();
                stmt.close();
            }
        } catch (Exception ex) {
            log.warn("Could not close JDBC Statement", ex);
        }

    }

    /**
     * @param rs 关闭返回集合
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception ex) {
                log.warn("Could not close JDBC ResultSet", ex);
            }
        }
    }

    /**
     * @param connection 关闭连接
     */
    public static void closeConnection(Connection connection) {
        closeConnection(connection, false);
    }


    /**
     * @param conn  连接
     * @param force 关闭连接
     */
    public static void closeConnection(Connection conn, boolean force) {
        if (conn == null) {
            return;
        }
        try {
            if (force) {
                if (conn instanceof ConnectionProxy) {
                    SQLWarning warning = conn.getWarnings();
                    if (warning != null) {
                        log.info(warning.getMessage());
                    }
                    ConnectionProxy connectionProxy = ((ConnectionProxy) conn);
                    connectionProxy.close();
                    connectionProxy.release();
                }
            } else {
                conn.close();
            }
        } catch (Exception e) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e1) {
                //...
            }
        }
    }

    /**
     * @param soberTable 排序表
     * @param field      字段
     * @return 判断在数据库里边是否需要引号
     */
    public static boolean isQuote(TableModels soberTable, Serializable field) {
        return isQuote(soberTable, (String) field);
    }

    /**
     * @param soberTable 排序表
     * @param field      字段
     * @return 判断在数据库里边是否需要引号
     */
    public static boolean isQuote(TableModels soberTable, String field) {
        return soberTable == null || isQuote(soberTable.getColumns(), field);
    }

    /**
     * @param soberColumns 字段
     * @param field        字段
     * @return 字段是否引用
     */
    public static boolean isQuote(List<SoberColumn> soberColumns, String field) {
        if (soberColumns == null || soberColumns.isEmpty()) {
            return true;
        }
        for (SoberColumn column : soberColumns) {
            if (column.getName().equalsIgnoreCase(field)) {
                return !ClassUtil.isNumberType(column.getClassType()) && !column.getClassType().getName().contains("bool") && !column.getClassType().getName().contains("int") && !column.getClassType().getName().contains("long");
            }
        }
        return true;
    }

    /**
     * @param resultSet 返回结果
     * @return 得到字段信息
     */
    public static Map<String, SoberColumn> getFieldType(final ResultSet resultSet) {
        Map<String, SoberColumn> result = new HashMap<>();
        try {
            ResultSetMetaData rsm = resultSet.getMetaData();
            for (int i = 1; i <= rsm.getColumnCount(); i++) {
                SoberColumn soberColumn = new SoberColumn();
                soberColumn.setName(rsm.getColumnName(i));
                result.put(soberColumn.getName(), soberColumn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>(0);
        }
        return result;
    }


    /**
     *
     * @param con 连接
     * @return String 得到数据库类型,枚举和适配器对应
     */
    public static DatabaseEnumType getDatabaseType(Connection con) {

        String dbName;
        String driverName;
        try {
            DatabaseMetaData metaData = con.getMetaData();
            dbName = metaData.getDatabaseProductName().toLowerCase();
            driverName = metaData.getDriverName().toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return DatabaseEnumType.General;
        }
        if (driverName.contains(DatabaseEnumType.ORACLE.getName().toLowerCase())) {
            return DatabaseEnumType.ORACLE;
        }
        if (driverName.contains(DatabaseEnumType.DM.getName().toLowerCase())) {
            return DatabaseEnumType.DM;
        }
        if (driverName.contains(DatabaseEnumType.POSTGRESQL.getName().toLowerCase()) || dbName.contains("pql") || dbName.contains("pgql")) {
            return DatabaseEnumType.POSTGRESQL;
        }
        if (driverName.contains(DatabaseEnumType.INTERBASE.getName().toLowerCase())) {
            return DatabaseEnumType.INTERBASE;
        }
        if (driverName.contains("sqlserver") || driverName.contains("sql server")) {
            return DatabaseEnumType.MSSQL;
        }
        if (driverName.contains("mysql") || dbName.contains("mysql")) {
            return DatabaseEnumType.MYSQL;
        }
        if (driverName.contains("db2") || dbName.contains("db2")) {
            return DatabaseEnumType.DB2;
        }
        if (driverName.contains("firebird") || dbName.contains("firebird")) {
            return DatabaseEnumType.FIREBIRD;
        }
        if (driverName.contains(DatabaseEnumType.SQLITE.getName().toLowerCase())) {
            return DatabaseEnumType.SQLITE;
        }
        if (driverName.contains("smalldb") || dbName.contains("smalldb") || dbName.contains("smallsql")) {
            return DatabaseEnumType.SMALLDB;
        }
        return DatabaseEnumType.General;
    }

    /**
     * equest 从参数传入Bean对象  MultipartRequest HttpServletRequest 两种情况
     *
     * @param rs 请求
     * @param cla     类
     * @param dialect 数据库适配器
     * @param <T>     泛型
     * @return 实体bean
     * @throws Exception 异常
     */
    public static <T> T getBean(ResultSet rs, Class<T> cla, Dialect dialect) throws Exception {
        if (dialect == null) {
            dialect = new GeneralDialect();
        }
        if (cla==null||cla.isAssignableFrom(List.class)||cla.isAssignableFrom(String.class)||cla.isAssignableFrom(Map.class))
        {

            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            Map<String,Object> result = new HashMap<>(resultSetMetaData.getColumnCount()+1);
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                String name = resultSetMetaData.getColumnLabel(i);
                String field = StringUtil.underlineToCamel(name);
                Object value = dialect.getResultSetValue(rs, name);
                result.put(field,value);
            }
            return (T)result;
        }

        T result = cla.newInstance();
        Field[] fields = ClassUtil.getDeclaredFields(cla);
        for (Field field : fields) {
            if (Modifier.isFinal(field.getModifiers()) || field.getModifiers() == 26 || field.getModifiers() == 18) {
                continue;
            }
            String propertyName = field.getName();
            Object value = null;
            try {
                value = dialect.getResultSetValue(rs, propertyName);
            } catch (Exception e) {
                log.error("错误的字段getResultSetValue:" + propertyName, e);
            }
            BeanUtil.setFieldValue(result, field.getName(), value);
        }
        return result;
    }


    /**
     * 将表对象转换为实体对象，用于辅助代码
     *
     * @param jdbcOperations jdbc 操作对象
     * @param table          表名
     * @return 字段列表
     */
    @SuppressWarnings("unchecked")
    public static List<SoberColumn> getTableColumns(JdbcOperations jdbcOperations, String table) {
        List<SoberColumn> columnList = new ArrayList<>();
        if (StringUtil.isNull(table)) {
            return columnList;
        }
        Dialect dialect = jdbcOperations.getSoberFactory().getDialect();
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_ONLY);
            if (DatabaseEnumType.find(jdbcOperations.getSoberFactory().getDatabaseType()).equals(DatabaseEnumType.ORACLE)||DatabaseEnumType.find(jdbcOperations.getSoberFactory().getDatabaseType()).equals(DatabaseEnumType.DB2))
            {
                rs = conn.getMetaData().getColumns(null, getSchema(conn), table.toUpperCase(), "%");
            }
            else
            {
                rs = conn.getMetaData().getColumns(null, "%", table, "%");
            }
            while (rs.next()) {
                columnList.add(dialect.getJavaType(jdbcOperations.loadColumnsValue(Map.class, rs)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JdbcUtil.closeResultSet(rs);
            JdbcUtil.closeConnection(conn);
        }
        return columnList;
    }

    //其他数据库不需要这个方法 oracle和db2需要
    private static String getSchema(Connection conn) throws Exception {
        String schema = conn.getMetaData().getUserName();
        if ((schema == null) || (schema.length() == 0)) {
            throw new Exception("ORACLE数据库模式不允许为空");
        }
        return schema.toUpperCase();
    }

    static public void setFetchSize(PreparedStatement preparedStatement,int count)
    {
        if (preparedStatement==null)
        {
            return;
        }
        int size = 20;
        if (count>=30 && count<60)
        {
            size = 40;
        } else
        if (count>=60&&count<100)
        {
            size = 60;
        } else
        if (count>=100&&count<200)
        {
            size = 80;
        }
        else if (count>=200&&count<300)
        {
            size = 100;
        } else if (count>=300&&count<500)
        {
            size = 120;
        } else if (count>=500)
        {
            size = 150;
        }
        try {
            preparedStatement.setFetchSize(size);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}