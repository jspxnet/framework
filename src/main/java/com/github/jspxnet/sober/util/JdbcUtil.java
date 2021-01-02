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
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.*;
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
public abstract class JdbcUtil {

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
        Map<String, SoberColumn> result = new HashMap<String, SoberColumn>();
        try {
            ResultSetMetaData rsm = resultSet.getMetaData();
            for (int i = 1; i <= rsm.getColumnCount(); i++) {
                SoberColumn soberColumn = new SoberColumn();
                soberColumn.setName(rsm.getColumnName(i));
                result.put(soberColumn.getName(), soberColumn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<String, SoberColumn>(0);
        }
        return result;
    }


    /**
     * @param con 连接
     * @return String 得到数据库名
     * @throws SQLException 连接错误
     */
    public static String getDatabaseName(Connection con) throws SQLException {
        DatabaseMetaData metaData = con.getMetaData();
        String dbName = metaData.getDatabaseProductName().toLowerCase();
        String driverName = metaData.getDriverName().toLowerCase();

        if (driverName.contains("oracle")) {
            return SoberEnv.ORACLE;
        }
        if (driverName.contains("postgresql") || dbName.contains("pql") || dbName.contains("pgql")) {
            return SoberEnv.POSTGRESQL;
        }
        if (driverName.contains("interbase") || dbName.contains("interbase")) {
            return SoberEnv.INTERBASE;
        }
        if (driverName.contains("sqlserver") || driverName.contains("sql server")) {
            return SoberEnv.MSSQL;
        }
        if (driverName.contains("mysql") || dbName.contains("mysql")) {
            return SoberEnv.MYSQL;
        }
        if (driverName.contains("db2") || dbName.contains("db2")) {
            return SoberEnv.DB2;
        }
        if (driverName.contains("firebird") || dbName.contains("firebird")) {
            return SoberEnv.Firebird;
        }
        if (driverName.contains("sqlite")) {
            return SoberEnv.Sqlite;
        }
        if (driverName.contains("smalldb") || dbName.contains("smalldb") || dbName.contains("smallsql")) {
            return SoberEnv.Smalldb;
        }
        return SoberEnv.General;
    }

    /**
     * equest 从参数传入Bean对象  MultipartRequest HttpServletRequest 两种情况
     *
     * @param request 请求
     * @param cla     类
     * @param dialect 数据库适配器
     * @param <T>     泛型
     * @return 实体bean
     * @throws Exception 异常
     */
    public static <T> T getBean(ResultSet request, Class<T> cla, Dialect dialect) throws Exception {
        if (dialect == null) {
            dialect = new GeneralDialect();
        }
        T result = cla.newInstance();
        Field[] fields = ClassUtil.getDeclaredFields(cla);
        for (Field field : fields) {
            String propertyName = field.getName();
            Object value = null;
            try {
                value = dialect.getResultSetValue(request, propertyName);
            } catch (Exception e) {
                log.error("错误的字段getResultSetValue:" + propertyName, e);
            }
            BeanUtil.setFieldValue(result, field.getName(), value);
        }
        return result;
    }
}