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

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.Placeholder;
import com.github.jspxnet.cache.*;
import com.github.jspxnet.datasource.ConnectionProxy;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.scriptmark.ScriptRunner;
import com.github.jspxnet.scriptmark.core.script.TemplateScriptEngine;
import com.github.jspxnet.sober.*;
import com.github.jspxnet.sober.annotation.IDType;
import com.github.jspxnet.sober.config.SoberCalcUnique;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.config.SoberNexus;
import com.github.jspxnet.sober.config.SoberTable;
import com.github.jspxnet.sober.criteria.Order;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.dialect.Dialect;
import com.github.jspxnet.sober.dialect.GeneralDialect;
import com.github.jspxnet.sober.dialect.OracleDialect;
import com.github.jspxnet.sober.enums.DatabaseEnumType;
import com.github.jspxnet.sober.enums.MappingType;
import com.github.jspxnet.sober.exception.ValidException;
import com.github.jspxnet.sober.model.container.PropertyContainer;
import com.github.jspxnet.sober.proxy.InterceptorProxy;
import com.github.jspxnet.sober.ssql.SSqlExpression;
import com.github.jspxnet.sober.table.SoberFieldEnum;
import com.github.jspxnet.sober.table.SoberTableModel;
import com.github.jspxnet.sober.table.SqlMapConf;
import com.github.jspxnet.txweb.table.OptionBundle;
import com.github.jspxnet.util.FieldWordUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.util.*;



/**
 * Created by IntelliJ IDEA.
 *
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-6
 * Time: 18:09:10
 */
@Slf4j
public final class JdbcUtil {

    private JdbcUtil() {

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
                return !ClassUtil.isNumberType(column.getClassType()) && !column.getClassType().getTypeName().contains("bool") && !column.getClassType().getTypeName().contains("int") && !column.getClassType().getTypeName().contains("long");
            }
        }
        return true;
    }

    /**
     * @param con 连接
     * @return String 得到数据库类型,枚举和适配器对应
     */
    public static DatabaseEnumType getDatabaseType(Connection con) {

        String databaseType;
        String driverName;
        try {
            DatabaseMetaData metaData = con.getMetaData();
            databaseType = metaData.getDatabaseProductName().toLowerCase();
            driverName = metaData.getDriverName().toLowerCase();
        } catch (Exception e) {
            log.error("con={}", con, e);
            return DatabaseEnumType.General;
        }
        if (driverName.contains(DatabaseEnumType.ORACLE.getName().toLowerCase())) {
            return DatabaseEnumType.ORACLE;
        }
        if (driverName.contains(DatabaseEnumType.DM.getName().toLowerCase())) {
            return DatabaseEnumType.DM;
        }
        if (driverName.contains(DatabaseEnumType.POSTGRESQL.getName().toLowerCase()) || databaseType.contains("pql") || databaseType.contains("pgql")) {
            return DatabaseEnumType.POSTGRESQL;
        }
        if (driverName.contains(DatabaseEnumType.INTERBASE.getName().toLowerCase())) {
            return DatabaseEnumType.INTERBASE;
        }
        if (driverName.contains("sqlserver") || driverName.contains("sql server")) {
            return DatabaseEnumType.MSSQL;
        }
        if (driverName.contains("db2") || databaseType.contains("db2")) {
            return DatabaseEnumType.DB2;
        }
        if (driverName.contains("firebird") || databaseType.contains("firebird")) {
            return DatabaseEnumType.FIREBIRD;
        }
        if (driverName.contains(DatabaseEnumType.SQLITE.getName().toLowerCase())) {
            return DatabaseEnumType.SQLITE;
        }
        if (driverName.contains("smalldb") || databaseType.contains("smalldb") || databaseType.contains("smallsql")) {
            return DatabaseEnumType.SMALLDB;
        }
        return DatabaseEnumType.MYSQL;
    }

    /**
     * @param conn             链接
     * @param dialect          适配器
     * @param databaseEnumType 数据库类型
     * @return 得到数据库名
     */
    public static String getCurrentDatabaseName(Connection conn, Dialect dialect, DatabaseEnumType databaseEnumType) {

        try {
            String databaseName = conn.getCatalog();
            if (StringUtil.isNull(databaseName)) {
                if (DatabaseEnumType.ORACLE.equals(databaseEnumType) || DatabaseEnumType.DM.equals(databaseEnumType)) {
                    DatabaseMetaData databaseMetaData = conn.getMetaData();
                    //oracle 一般就是用户名,oracle 的库概念不同
                    databaseName = databaseMetaData.getUserName().toUpperCase();
                } else if (DatabaseEnumType.POSTGRESQL.equals(databaseEnumType)) {
                    ResultSet resultSet = conn.createStatement().executeQuery(Dialect.POSTGRESQL_CURRENT_DATABASE);
                    if (resultSet.next()) {
                        databaseName = ObjectUtil.toString(dialect.getResultSetValue(resultSet, 1));
                    }
                    resultSet.close();
                } else if (DatabaseEnumType.MYSQL.equals(databaseEnumType)) {
                    ResultSet resultSet = conn.createStatement().executeQuery(Dialect.MYSQL_CURRENT_DATABASE);
                    if (resultSet.next()) {
                        databaseName = ObjectUtil.toString(dialect.getResultSetValue(resultSet, 1));
                    }
                    resultSet.close();
                } else if (DatabaseEnumType.MSSQL.equals(databaseEnumType)) {
                    ResultSet resultSet = conn.createStatement().executeQuery(Dialect.MSSQL_CURRENT_DATABASE);
                    if (resultSet.next()) {
                        databaseName = ObjectUtil.toString(dialect.getResultSetValue(resultSet, 1));
                    }
                    resultSet.close();
                }
            }
            return databaseName;
        } catch (SQLException e) {
            closeConnection(conn, true);
            log.error("获取数据库名称失败", e);
            throw new RuntimeException(e);
        }
        //oracle会是空的
    }

    /**
     * 判断是否未mssql高版本,2012后 就为高版本，能够支持分页
     *
     * @param conn 连接
     * @return 是否
     */
    public static boolean isMsSqlHeightVersion(Connection conn) {
        ResultSet resultSet = null;
        try {
            resultSet = conn.createStatement().executeQuery(Dialect.MSSQL_CURRENT_VESION);
            if (resultSet.next()) {
                String checkVersion = resultSet.getString(1);
                if (checkVersion != null && (checkVersion.contains("2004") || checkVersion.contains("2005")
                        || checkVersion.contains("2006") || checkVersion.contains("2007") || checkVersion.contains("2008")
                        || checkVersion.contains("2009") || checkVersion.contains("2010") || checkVersion.contains("2011"))) {
                    return false;
                }
            }
            resultSet.close();
        } catch (SQLException e) {
            return false;

        }
        return true;
    }

    /**
     * request 从参数传入Bean对象  MultipartRequest HttpServletRequest 两种情况
     *
     * @param rs      请求
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
        if (cla == null || cla.isAssignableFrom(List.class) || cla.isAssignableFrom(String.class) || cla.isAssignableFrom(Map.class)) {
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            Map<String, Object> result = new HashMap<>(resultSetMetaData.getColumnCount() + 1);
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                String name = resultSetMetaData.getColumnLabel(i);
                String field = StringUtil.underlineToCamel(name);
                if (!result.containsKey(field))
                {
                    Object value = dialect.getResultSetValue(rs, name);
                    result.put(field, value);
                }
            }
            return (T) result;
        }

        T result = cla.newInstance();
        Field[] fields = ClassUtil.getDeclaredFields(cla);
        for (Field field : fields) {
            if (Modifier.isFinal(field.getModifiers()) || field.getModifiers() == (Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL)
                    || field.getModifiers() == (Modifier.PRIVATE | Modifier.FINAL)) {
                continue;
            }
            String propertyName = field.getName();
            Object value = null;
            try {
                value = dialect.getResultSetValue(rs, propertyName);
            } catch (Exception e) {
                log.error("错误的字段 getResultSetValue:" + propertyName, e);
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
    public static List<SoberColumn> getTableColumns(SoberSupport jdbcOperations, String table) {
        if (StringUtil.isNull(table)) {
            return new ArrayList<>(0);
        }
        List<SoberColumn> columnList = new LinkedList<>();
        Dialect dialect = jdbcOperations.getDialect();
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_ONLY);
            //查询出表结构 begin
            if (DatabaseEnumType.find(jdbcOperations.getSoberFactory().getDatabaseType()).equals(DatabaseEnumType.ORACLE)
                    || DatabaseEnumType.find(jdbcOperations.getSoberFactory().getDatabaseType()).equals(DatabaseEnumType.DB2)) {
                rs = conn.getMetaData().getColumns(conn.getCatalog(), getSchema(conn), table.toUpperCase(), "%");
            } else {
                rs = conn.getMetaData().getColumns(conn.getCatalog(), "%", table, "%");
            }
            //查询出表结构 end
            while (rs.next()) {
                columnList.add(dialect.getJavaType(jdbcOperations.loadColumnsValue(Map.class, rs)));
            }
        } catch (Exception e) {
            log.error("getTableColumns table={}", table, e);
        } finally {
            closeResultSet(rs);
            closeConnection(conn);
        }
        return columnList;
    }

    /**
     * @param jdbcOperations jdbc 操作对象
     * @param sql            sql查询
     * @return 通过sql得到数据结构
     */
    public static List<SoberColumn> getSqlColumns(SoberSupport jdbcOperations, String sql) {
        return getSqlColumns(jdbcOperations, sql, false);
    }

    /**
     * @param jdbcOperations jdbc 操作对象
     * @param sql            sql查询
     * @param fixFieldName   修复字段明
     * @return 通过sql得到数据结构
     */
    public static List<SoberColumn> getSqlColumns(SoberSupport jdbcOperations, String sql, boolean fixFieldName) {
        if (StringUtil.isNull(sql)) {
            return new ArrayList<>(0);
        }
        Connection conn = null;
        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_ONLY);
            return getSoberColumnList(conn, sql, fixFieldName);
        } catch (Exception e) {
            log.error("getSqlColumns sql={}", sql, e);
            return null;
        } finally {
            closeConnection(conn);
        }

    }

    /**
     * @param jdbcOperations 数据操作
     * @param sql            sql
     * @param fixFieldName   修复字段名称
     * @return 通过sql得到表结构
     */
    public static SoberTable getSoberTable(SoberSupport jdbcOperations, String sql, boolean fixFieldName) {
        if (StringUtil.isNull(sql)) {
            return null;
        }
        SoberTable soberTable = new SoberTable();
        soberTable.setAutoId(false);
        soberTable.setIdType(IDType.serial);
        soberTable.setEntity(HashMap.class);
        Connection conn = null;
        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_ONLY);
            List<SoberColumn> columnList = getSoberColumnList(conn, sql, fixFieldName);
            if (!columnList.isEmpty()) {
                SoberColumn soberColumn = columnList.get(0);
                DatabaseMetaData dbmd = conn.getMetaData();
                ResultSet primaryKeys = dbmd.getPrimaryKeys(null, null, soberColumn.getTableName());
                List<String> primaryKeyColumns = new ArrayList<>();
                // 遍历主键信息
                while (primaryKeys.next()) {
                    String columnName = primaryKeys.getString("COLUMN_NAME");
                    // 这里可以将主键列名存储起来，以便后续判断
                    if (!StringUtil.isNull(columnName)) {
                        primaryKeyColumns.add(columnName);
                    }
                }
                if (!primaryKeyColumns.isEmpty()) {
                    for (SoberColumn column : columnList) {
                        if (primaryKeyColumns.contains(column.getName())) {
                            soberTable.setPrimary(column.getName());
                            break;
                        }
                    }
                }
                closeResultSet(primaryKeys);
            }
            soberTable.setColumns(columnList);
            soberTable.setDatabaseName(jdbcOperations.getSoberFactory().getDatabaseName());
        } catch (Exception e) {
            log.error("getSqlColumns sql={}", sql, e);
        } finally {
            closeConnection(conn);
        }
        return soberTable;
    }


    /**
     * 目的只是，合并相同的代码,
     *
     * @param conn         链接
     * @param sql          sql
     * @param fixFieldName 是否修复名称
     * @return 的都字段列表
     * @throws Exception 异常
     */
    private static List<SoberColumn> getSoberColumnList(Connection conn, String sql, boolean fixFieldName) throws Exception {



        List<SoberColumn> result = new LinkedList<>();
        List<String> checkList = new ArrayList<>();
        //查询出表结构 begin
        ResultSet rs = conn.createStatement().executeQuery(sql);
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
            SoberColumn soberColumn = new SoberColumn();
            String className = resultSetMetaData.getColumnClassName(i);

            int scale = resultSetMetaData.getScale(i);
            //是数据库类型
            String typeName = resultSetMetaData.getColumnTypeName(i);
            if (typeName == null) {
                typeName = StringUtil.empty;
            }
            String name = resultSetMetaData.getColumnName(i);

            soberColumn.setName(name);
            if (!StringUtil.isNull(className) && className.contains(".")) {
                if (className.contains("BigDecimal") && scale == 0) {
                    soberColumn.setClassType(long.class);
                    soberColumn.setLength(0);
                } else if ("NUMBER".equalsIgnoreCase(typeName)) {
                    soberColumn.setClassType(Double.class);
                    soberColumn.setLength(0);
                } else if (className.equalsIgnoreCase(java.sql.Date.class.getName())
                        ||className.equalsIgnoreCase(java.sql.Timestamp.class.getName())) {
                    //时间日期用java.util.Date
                    soberColumn.setClassType(java.util.Date.class);
                    soberColumn.setLength(0);
                }
                else {
                    soberColumn.setClassType(ClassUtil.loadClass(className));
                }
            } else {
                soberColumn.setClassType(ClassUtil.loadClass(className));
            }

            soberColumn.setCaption(resultSetMetaData.getColumnLabel(i));
            soberColumn.setLength(resultSetMetaData.getColumnDisplaySize(i));
            if (resultSetMetaData.isNullable(i) == ResultSetMetaData.columnNoNulls &&soberColumn.getLength()<250 )
            {
                //识别并不准确，text 类型 默认都为非必填
                soberColumn.setNotNull(true);
            }
            soberColumn.setAutoincrement(resultSetMetaData.isAutoIncrement(i));
            soberColumn.setTableName(resultSetMetaData.getTableName(i));
            if (fixFieldName) {
                String fileName = FieldWordUtil.getFiledName(soberColumn.getCaption(), soberColumn.getName());
                soberColumn.setName(fileName);
            }
            if (!checkList.contains(soberColumn.getName()))
            {
                checkList.add(soberColumn.getName());
                result.add(soberColumn);
            }
        }
        closeResultSet(rs);
        checkList.clear();
        //查询出表结构 end
        return  result;
    }

    //其他数据库不需要这个方法 oracle和db2需要
    private static String getSchema(Connection conn) throws Exception {
        String schema = conn.getMetaData().getUserName();
        if ((schema == null) || (schema.isEmpty())) {
            throw new Exception("ORACLE数据库模式不允许为空");
        }
        return schema.toUpperCase();
    }

    static public void setFetchSize(PreparedStatement preparedStatement, int count) {
        if (preparedStatement == null) {
            return;
        }
        int size = 20;
        if (count >= 30 && count < 60) {
            size = 50;
        } else if (count >= 60 && count < 100) {
            size = 80;
        } else if (count >= 100 && count < 200) {
            size = 100;
        } else if (count >= 200 && count < 300) {
            size = 150;
        } else if (count >= 300 && count < 500) {
            size = 200;
        } else if (count >= 500) {
            size = 250;
        }
        try {
            preparedStatement.setFetchSize(size);
        } catch (SQLException e) {
            log.error("setFetchSize count={}", count, e);
        }
    }

    //-----------------
    public static Map<String, TableModels> getAllTableModels(SoberFactory soberFactory, boolean dto, int extend) {

        String cacheKey = SoberUtil.getListKey(SoberTableModel.class, Environment.KEY_SOBER_TABLE_CACHE + "_" + ObjectUtil.toInt(dto) + extend, StringUtil.empty, 1, 1, false);
        Map<String, TableModels> result = null;
        if (soberFactory.isUseCache()) {
            result = (Map<String, TableModels>) JSCacheManager.get(SoberTableModel.class, cacheKey);
            if (!ObjectUtil.isEmpty(result)) {
                return result;
            }
        }
        result = new HashMap<>();
        List<SoberTable> list = SoberUtil.getScanTableAnnotationList(dto, extend);
        for (SoberTable table : list) {
            if (StringUtil.isNull(table.getDatabaseName())) {
                table.setDatabaseName(soberFactory.getDatabaseName());
            }
            result.put(table.getId(), table);
            CacheManager cacheManager = JSCacheManager.getCacheManager();
            if (table.isUseCache() && cacheManager.containsKey(table.getClassName())) {
                Cache cache = cacheManager.getCache(SoberTableModel.class);
                Map<String, String> configMap = new HashMap<>();
                configMap.put("name", table.getClassName());
                configMap.put("keepTime", ObjectUtil.toString(cache.getSecond()));
                configMap.put("maxElements", ObjectUtil.toString(cache.getMaxElements()));
                configMap.put("eternal", ObjectUtil.toString(cache.isEternal()));
                configMap.put("diskStorePath", null);
                IStore store = (IStore) EnvFactory.getBeanFactory().getBean(Environment.DEFAULT_STORE, Environment.CACHE);
                JSCacheManager.getCacheManager().createCache(store, configMap);
            }
        }

        if (soberFactory.isUseCache()) {
            JSCacheManager.put(SoberTableModel.class, cacheKey, result);
        }
        return result;
    }


    public static String getTableName(SoberSupport jdbcOperations, Class<?> cla) {
        TableModels tableModels = jdbcOperations.getSoberFactory().getTableModels(cla, jdbcOperations);
        if (tableModels != null) {
            return tableModels.getName();
        }
        final String FLAG = "notTableConf";
        //查询过的做标识，以后就不要查询了
        //扩展的实体结构begin
        String key = SoberUtil.getLoadKey(SoberTableModel.class, FLAG, SoberTableModel.class.getName(), false);
        int hasConf = (int) JSCacheManager.get(SoberTableModel.class, key);
        if (hasConf != YesNoEnumType.YES.getValue()) {
            SoberTableModel soberTableModel = jdbcOperations.load(SoberTableModel.class, "entityClass", cla.getName(), false);
            if (soberTableModel != null && soberTableModel.getId() > 0) {
                return soberTableModel.getTableName();
            } else {
                JSCacheManager.put(SoberTableModel.class, key, YesNoEnumType.YES.getValue());
            }
        }

        //扩展的实体结构end
        return AnnotationUtil.getTableName(cla);
    }

    public static <T> T loadColumnsValue(SoberSupport jdbcOperations, Dialect dialect, Class<T> tClass, ResultSet resultSet) throws Exception {
        T result;
        if (!PropertyContainer.class.isAssignableFrom(tClass)
                && (Map.class.isAssignableFrom(tClass) || HashMap.class.isAssignableFrom(tClass) || List.class.isAssignableFrom(tClass))) {
            //载入表结构定义
            ResultSetMetaData metaData = resultSet.getMetaData();
            DataMap<String, Object> beanMap = new DataMap<>();
            for (int n = 1; n <= metaData.getColumnCount(); n++) {
                String field = metaData.getColumnLabel(n);
                if (!beanMap.containsKey(field))
                {
                    Object value = dialect.getResultSetValue(resultSet, n);
                    beanMap.put(StringUtil.underlineToCamel(field.toLowerCase()), value);
                }
            }
            result = (T) beanMap;
        } else {
            //载入查询数据
            TableModels soberTable = jdbcOperations.getSoberTable(tClass);
            result = tClass.newInstance();
            ResultSetMetaData metaData = resultSet.getMetaData();

            int count = metaData.getColumnCount();
            //count 里边的数据有可能会出现重复，这里需要排除重复的字段名称
            Map<String,Integer> fieldNameList = new HashMap<>();
            for (int i = 1; i <= count; i++) {
                String field = metaData.getColumnLabel(i);
                if (field!=null&&!fieldNameList.containsKey(field))
                {
                    fieldNameList.put(field,i);
                }
            }
            for (String dbFiled:fieldNameList.keySet()) {
                int row = fieldNameList.get(dbFiled);
                SoberColumn soberColumn = soberTable.getColumn(dbFiled);
                if (soberColumn != null) {
                    Object obj = dialect.getResultSetValue(resultSet, row);
                    BeanUtil.setFieldValue(result, soberColumn.getName(), obj);
                } else if (ClassUtil.getDeclaredField(result.getClass(), dbFiled, true) != null) {
                    BeanUtil.setFieldValue(result, dbFiled, dialect.getResultSetValue(resultSet, row));
                }

            }
        }
        if (String.class.isAssignableFrom(tClass)) {
            return (T) ObjectUtil.getJson(result);
        }
        return result;
    }

    public static void loadNexusValue(SoberSupport jdbcOperations, TableModels soberTable, Object result) {
        if (result == null) {
            return;
        }
        try {
            Map<String, SoberNexus> nexus = soberTable.getNexusMap();
            for (String colName : nexus.keySet()) {
                SoberNexus soberNexus = nexus.get(colName);
                Placeholder placeholder = EnvFactory.getPlaceholder();
                if ((MappingType.OneToOne.equalsIgnoreCase(soberNexus.getMapping()) || MappingType.ManyToOne.equalsIgnoreCase(soberNexus.getMapping()))
                        && (StringUtil.isNull(soberNexus.getWhere()) || !StringUtil.isNull(soberNexus.getWhere()) && ObjectUtil.toBoolean(placeholder.processTemplate(ObjectUtil.getMap(result), soberNexus.getWhere())))) {

                    Object findValue = BeanUtil.getProperty(result, soberNexus.getField());
                    String term = AnnotationUtil.getNexusTerm(result, soberNexus.getTerm());

                    TableModels targetModels = jdbcOperations.getSoberTable(soberNexus.getTargetEntity());
                    SoberColumn soberColumn = targetModels.getColumn(soberNexus.getTargetField());
                    if (soberColumn == null) {
                        continue;
                    }
                    Class<?> classType = soberColumn.getClassType();
                    //id 为 0 的时候不查询
                    if ((classType == Long.class || classType == long.class || classType == Integer.class || classType == int.class) && ObjectUtil.toInt(findValue) == 0) {
                        continue;
                    }
                    List<?> childList = getFindFieldList(jdbcOperations, soberNexus.getTargetEntity(), soberNexus.getTargetField(), (Serializable) findValue, term, AnnotationUtil.getNexusOrderBy(result, soberNexus.getOrderBy()), false, 1);
                    if (!childList.isEmpty()) {
                        Object chainObj = childList.get(0);
                        if (soberNexus.isChain() && chainObj != null) {
                            TableModels cSoberTable = jdbcOperations.getSoberTable(chainObj.getClass());
                            loadNexusValue(jdbcOperations, cSoberTable, chainObj);
                        }
                        BeanUtil.setSimpleProperty(result, colName, chainObj);
                    }
                } else if (MappingType.OneToMany.equalsIgnoreCase(soberNexus.getMapping())) {
                    //对应id对象
                    Object findValue = BeanUtil.getProperty(result, soberNexus.getField());
                    //条件
                    String term = AnnotationUtil.getNexusTerm(result, soberNexus.getTerm());
                    //数据个数
                    int length = AnnotationUtil.getNexusLength(result, soberNexus.getLength(), jdbcOperations.getMaxRows());
                    //查询得到列表
                    List<?> childList = getFindFieldList(jdbcOperations, soberNexus.getTargetEntity(), soberNexus.getTargetField(), (Serializable) findValue, term, AnnotationUtil.getNexusOrderBy(result, soberNexus.getOrderBy()), soberNexus.isChain(), length);

                    if (!childList.isEmpty()) {
                        Object chainObj = childList.get(0);
                        if (soberNexus.isChain() && chainObj != null) {
                            TableModels cSoberTable = jdbcOperations.getSoberTable(chainObj.getClass());
                            for (Object childObj : childList) {
                                if (childObj == null || ClassUtil.isStandardType(childObj.getClass())) {
                                    continue;
                                }
                                loadNexusValue(jdbcOperations, cSoberTable, childObj);
                            }
                        }
                    }
                    BeanUtil.setSimpleProperty(result, colName, childList);
                }

            }
        } catch (Exception e) {
            log.error("载入关系表错误" + soberTable.toString() + "对象" + result, e);
        }
    }

    /**
     * @param jdbcOperations jdbc操作类
     * @param aClass         返回实体
     * @param field          查询字段
     * @param serializable   字段值
     * @param term           条件
     * @param orderBy        排序
     * @param loadChild      是否载入映射
     * @param max            最大行数
     * @param <T>            查询返回
     * @return 查询返回列表
     */
    public static <T> List<T> getFindFieldList(SoberSupport jdbcOperations, Class<T> aClass, String field, Serializable serializable, String term, String orderBy, boolean loadChild, int max) {
        Criteria criteria = jdbcOperations.createCriteria(aClass);
        criteria = criteria.add(Expression.eq(field, serializable));
        if (!StringUtil.isNull(term)) {
            criteria = SSqlExpression.getTermExpression(criteria, term);
        }
        if (!StringUtil.isNull(orderBy)) {
            criteria = SSqlExpression.getSortOrder(criteria, orderBy);
        }
        criteria = criteria.setCurrentPage(1).setTotalCount(max);
        return criteria.list(loadChild);
    }

    /**
     * @param jdbcOperations jdbc操作类
     * @param dialect        sql是配置
     * @param soberTable     数据模型
     * @param inObj          进入对象
     * @return 计算结果
     */
    public static Object calcUnique(SoberSupport jdbcOperations, Dialect dialect, TableModels soberTable, Object inObj) {
        if (inObj == null || soberTable == null) {
            return inObj;
        }
        Map<String, SoberCalcUnique> calcUniqueMap = soberTable.getCalcUniqueMap();
        if (ObjectUtil.isEmpty(calcUniqueMap)) {
            return inObj;
        }
        Object obj = inObj;
        if (inObj instanceof String) {
            obj = new JSONObject((String) inObj).parseObject(soberTable.getEntity());
        }
        ////////////////////////////CalcUnique
        for (String colName : calcUniqueMap.keySet()) {
            SoberCalcUnique soberCalcUnique = calcUniqueMap.get(colName);
            Map<String, Object> valueMap = new HashMap<>();
            Class<?>[] classArray = soberCalcUnique.getEntity();
            if (classArray != null) {
                for (Class<?> aClassArray : classArray) {

                    String tableName = StringUtil.uncapitalize(aClassArray.getSimpleName());
                    if (!StringUtil.hasLength(tableName)) {
                        tableName = aClassArray.getName();
                    }
                    valueMap.put(tableName, jdbcOperations.getTableName(aClassArray));
                }
            }
            String sqlText = dialect.processSql(soberCalcUnique.getSql(), valueMap);
            try {
                Object[] param = null;
                if (!ArrayUtil.isEmpty(soberCalcUnique.getValue())) {
                    for (String key : soberCalcUnique.getValue()) {
                        param = ArrayUtil.add(param, BeanUtil.getProperty(obj, key));
                    }
                }
                if (obj instanceof Map) {
                    Map<String, Object> temp = (Map<String, Object>) obj;
                    temp.put(colName, jdbcOperations.getUniqueResult(sqlText, param));
                } else {
                    BeanUtil.setSimpleProperty(obj, colName, jdbcOperations.getUniqueResult(sqlText, param));
                }
            } catch (Exception e) {
                log.error(soberTable.getName() + ":" + sqlText, e);
            }
        }
        ////////////////////////////
        if (inObj instanceof String) {
            return new JSONObject(obj).toString();
        }
        return obj;
    }


    /**
     * 单个对象查询返回
     *
     * @param jdbcOperations jdbc操作类
     * @param dialect        sql适配
     * @param sql            sql
     * @param valueMap       map参数
     * @return Object
     */
    public static Object getUniqueResult(SoberSupport jdbcOperations, Dialect dialect, String sql, Map<String, Object> valueMap) {
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String sqlText = StringUtil.empty;
        try {
            sqlText = dialect.processSql(sql, valueMap);
            jdbcOperations.debugPrint(sqlText);
            conn = jdbcOperations.getConnection(SoberEnv.READ_ONLY);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.createStatement();
            } else {
                statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }
            resultSet = statement.executeQuery(sqlText);
            if (resultSet.next()) {
                return dialect.getResultSetValue(resultSet, 1);
            }
        } catch (Exception e) {
            log.error("SQL:" + sqlText, e);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            closeConnection(conn);
        }
        return null;
    }

    /**
     * @param jdbcOperations jdbc操作类
     * @param dialect        sql 适配
     * @param sqlText        sql语句
     * @param param          参数数组
     * @return 单一返回对象
     */
    public static Object getUniqueResult(SoberSupport jdbcOperations, Dialect dialect, String sqlText, Object[] param) {
        Object result = null;
        Connection conn = null;
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_ONLY);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }

            jdbcOperations.debugPrint(sqlText);
            if (!ArrayUtil.isEmpty(param)) {
                for (int i = 0; i < param.length; i++) {
                    jdbcOperations.debugPrint("prepared[" + (i + 1) + "]=" + param[i]);
                    dialect.setPreparedStatementValue(statement, i + 1, param[i]);
                }
            }
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getObject(1);
            }
        } catch (Exception e) {
            log.error("SQL:" + sqlText, e);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            closeConnection(conn);
        }
        return result;
    }

    /**
     * @param jdbcOperations jdbc操作类
     * @param aClass         类
     * @param field          字段
     * @param serializable   调整
     * @param loadChild      载入子类
     * @param <T>            类型
     * @return 载入对象
     */
    public static <T> T load(SoberSupport jdbcOperations, Class<T> aClass, Serializable field, Serializable serializable, boolean loadChild) {
        if (aClass == null) {
            return null;
        }
        if ((serializable == null || field == null && (ClassUtil.isNumberType(serializable.getClass())
                && ObjectUtil.toLong(serializable) == 0)) || ObjectUtil.isEmpty(serializable)) {
            try {
                return aClass.newInstance();
            } catch (Exception e) {
                log.error("load field={},serializable:{}", field, serializable, e);
                return null;
            }
        }
        //安全防范，如果有  or and where 这种关键字的，直接返回 空
        TableModels soberTable = jdbcOperations.getSoberTable(aClass);
        if (field == null) {
            field = soberTable.getPrimary();
        }

        if (!StringUtil.hasLength((String) field)) {
            log.error(aClass + " SQL Primary is NULL,配置表没设置ID");
            return null;
        }

        T result;
        //取出cache
        String cacheKey = null;
        boolean useCache = jdbcOperations.getSoberFactory().isUseCache() && soberTable.isUseCache();
        if (useCache) {
            cacheKey = SoberUtil.getLoadKey(aClass, field, serializable, loadChild);
            result = (T) JSCacheManager.get(aClass, cacheKey);
            if (!ObjectUtil.isEmpty(result) && aClass.equals(result.getClass())) {
                return result;
            }
        }

        result = get(jdbcOperations, jdbcOperations.getDialect(), aClass, field, serializable, loadChild);
        //放入cache
        if (useCache && result != null) {
            JSCacheManager.put(aClass, cacheKey, result);
        }
        if (result == null) {
            try {
                return aClass.newInstance();
            } catch (Exception e) {
                log.error(aClass + " newInstance error", e);
            }
        }
        return result;
    }

    /**
     * 载入映射对象
     *
     * @param jdbcOperations jdbc操作类
     * @param soberTable     mapping
     * @param list           list
     */
    public static void loadNexusList(SoberSupport jdbcOperations, TableModels soberTable, List<?> list) {
        if (ObjectUtil.isEmpty(list)) {
            return;
        }
        Map<String, SoberNexus> nexus = soberTable.getNexusMap();
        Placeholder placeholder = EnvFactory.getPlaceholder();
        for (String colName : nexus.keySet()) {
            SoberNexus soberNexus = nexus.get(colName);
            if ((MappingType.OneToOne.equalsIgnoreCase(soberNexus.getMapping()) || MappingType.ManyToOne.equalsIgnoreCase(soberNexus.getMapping()))
                    && (StringUtil.isNull(soberNexus.getWhere()) || !StringUtil.isNull(soberNexus.getWhere())
                    && ObjectUtil.toBoolean(placeholder.processTemplate(ObjectUtil.getMap(list.get(0)), soberNexus.getWhere())))) {
                List<Object> idList = BeanUtil.copyFieldList(list, soberNexus.getField());

                //合并相同的id begin
                Map<Object, Object> tempMap = new HashMap<>();
                for (Object obj : idList) {
                    tempMap.put(obj,obj);
                }
                List<Object> mergedList = new ArrayList<>();
                mergedList.addAll(tempMap.values());
                //合并相同的id end
                if (mergedList.isEmpty())
                {
                    continue;
                }
                if (mergedList.size()==1)
                {
                    if (mergedList.get(0) instanceof Number)
                    {
                        Number num = (Number)mergedList.get(0);
                        if (0==num.intValue())
                        {
                            continue;
                        }
                    }
                }

                Criteria criteria = jdbcOperations.createCriteria(soberNexus.getTargetEntity());
                criteria = criteria.add(Expression.in(soberNexus.getTargetField(), mergedList));
                if (!StringUtil.isNull(soberNexus.getTerm())) {
                    String term = soberNexus.getTerm();
                    term = AnnotationUtil.getNexusTerm(list.get(0), term);
                    criteria = SSqlExpression.getTermExpression(criteria, term);
                }
                criteria = criteria.setCurrentPage(1).setTotalCount(idList.size());
                List<Object> loadObjectList = criteria.list(soberNexus.isChain());
                if (ObjectUtil.isEmpty(loadObjectList)) {
                    continue;
                }
                for (Object obj : list) {
                    Object objField = BeanUtil.getProperty(obj, soberNexus.getField());
                    if (objField == null) {
                        continue;
                    }
                    for (Object loadObj : loadObjectList) {
                        if (objField.equals(BeanUtil.getProperty(loadObj, soberNexus.getTargetField()))) {
                            BeanUtil.setFieldValue(obj, colName, loadObj);
                        }
                    }
                }
            } else if (MappingType.OneToMany.equalsIgnoreCase(soberNexus.getMapping())) {
                List<Object> idList = BeanUtil.copyFieldList(list, soberNexus.getField());
                for (Object obj : list) {
                    Criteria criteria = jdbcOperations.createCriteria(soberNexus.getTargetEntity());
                    criteria = criteria.add(Expression.in(soberNexus.getTargetField(), idList));
                    if (!StringUtil.isNull(soberNexus.getTerm())) {
                        String term = soberNexus.getTerm();
                        term = AnnotationUtil.getNexusTerm(obj, term);
                        criteria = SSqlExpression.getTermExpression(criteria, term);
                    }
                    criteria = criteria.setCurrentPage(1).setTotalCount(jdbcOperations.getMaxRows());
                    List<Object> loadObjectList = criteria.list(soberNexus.isChain());
                    List<Object> valueLstCache = new ArrayList<>();
                    for (Object loadObj : loadObjectList) {
                        if (loadObj == null) {
                            continue;
                        }
                        //对应id对象
                        Object objField = BeanUtil.getFieldValue(obj, soberNexus.getField(), false);
                        if (objField == null || ((objField instanceof Long || objField instanceof Integer)&&0==ObjectUtil.toLong(objField))) {
                            continue;
                        }
                        Object keyField = BeanUtil.getFieldValue(loadObj, soberNexus.getTargetField(), false);
                        if (objField.equals(keyField)) {
                            valueLstCache.add(loadObj);
                        }
                        BeanUtil.setSimpleProperty(obj, colName, valueLstCache);
                    }
                }
            }
        }
    }


    /**
     * 使用jdbc完成,比较浪费资源
     *
     * @param jdbcOperations jdbc操作类
     * @param cla            类型
     * @param sql            sql
     * @param param          参数
     * @param currentPage    页数
     * @param totalCount     每页行数
     * @param loadChild      载入子对象
     * @param <T>            类型
     * @return 查询返回列表
     */
    public static <T> List<T> query(SoberSupport jdbcOperations,  Class<T> cla, String sql, Object[] param, int currentPage, int totalCount, boolean loadChild) {
        if (totalCount > jdbcOperations.getMaxRows()) {
            totalCount = jdbcOperations.getMaxRows();
        }
        if (currentPage <= 0) {
            currentPage = 1;
        }
        int iEnd = currentPage * totalCount;
        if (iEnd < 0) {
            iEnd = 0;
        }

        int iBegin = iEnd - totalCount;
        if (iBegin < 0) {
            iBegin = 0;
        }

        TableModels soberTable = jdbcOperations.getSoberTable(cla);
        if (soberTable == null)
        {
            try {
                throw new Exception("此方法不支持Map返回");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        Dialect dialect = jdbcOperations.getDialect();
        SoberFactory soberFactory = jdbcOperations.getSoberFactory();
        List<T> result;
        //取出cache  begin
        String cacheKey = null;
        if (soberFactory.isUseCache() && soberTable.isUseCache()) {
            StringBuilder termKey = new StringBuilder();
            termKey.append(sql);
            termKey.append("_").append("_p_").append(soberFactory.getDatabaseType()).append("_");
            if (param != null) {
                for (Object po : param) {
                    termKey.append(ObjectUtil.toString(po));
                }
            }
            cacheKey = SoberUtil.getListKey(cla, StringUtil.replace(termKey.toString(), StringUtil.EQUAL, "_"), StringUtil.empty, iBegin, iEnd, loadChild);
            result = (List<T>) JSCacheManager.get(cla, cacheKey);
            if (!ObjectUtil.isEmpty(result)) {
                return result;
            }
        }
        //取出cache  end
        result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        Map<String, Object> valueMap = new HashMap<>(5);
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_PRIMARY_KEY, soberTable.getPrimary());
        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_ONLY);
            sql = dialect.processSql(sql, valueMap);
            jdbcOperations.debugPrint(sql);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sql);
            } else {
                statement = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            }
            setFetchSize(statement, iEnd);
            statement.setMaxRows(iEnd);
            if (param != null) {
                for (int i = 0; i < param.length; i++) {
                    jdbcOperations.debugPrint("prepared[" + (i + 1) + "]=" + param[i]);
                    dialect.setPreparedStatementValue(statement, i + 1, param[i]);
                }
            }
            resultSet = statement.executeQuery();
            if (iBegin > 0) {
                resultSet.absolute(iBegin);
            }
            while (resultSet.next()) {
                T tempObj = loadColumnsValue(jdbcOperations, dialect, cla, resultSet);
                //载入计算数据
                calcUnique(jdbcOperations, dialect, soberTable, tempObj);
                result.add(tempObj);
                if (result.size() > totalCount) {
                    break;
                }
            }
            if (loadChild) {
                loadNexusList(jdbcOperations, soberTable, result);
            }
        } catch (Exception e) {
            log.error("query soberTable={},sql={}", soberTable, sql, e);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            closeConnection(conn);
            valueMap.clear();
            if (soberFactory.isUseCache() && soberTable.isUseCache()) {
                JSCacheManager.put(cla, cacheKey, result);
            }
        }
        return result;
    }

    /**
     * 验证数据对象
     *
     * @param jdbcOperations jdbc操作类
     * @param obj            对象
     * @throws Exception 异常
     */
    public static void validator(SoberSupport jdbcOperations, Object obj) throws Exception {
        Map<String, String> result = new HashMap<>();
        ScriptRunner scriptRunner = new TemplateScriptEngine();
        TableModels soberTable = jdbcOperations.getSoberTable(obj.getClass());
        try {
            for (SoberColumn soberColumn : soberTable.getColumns()) {
                if (!soberColumn.isNotNull()) {
                    continue;
                }
                String dataType = soberColumn.getDataType();
                if (StringUtil.isNull(dataType)) {
                    continue;
                }
                Object value = BeanUtil.getProperty(obj, soberColumn.getName());
                if (value instanceof InputStream) {
                    continue;
                }
                boolean isNeed = soberColumn.isNotNull();
                if (value != null) {
                    isNeed = true;
                }
                if (!isNeed) {
                    continue;
                }
                String expression = dataType;
                if (expression.startsWith(" ")) {
                    expression = expression.trim();
                } else {
                    expression = soberColumn.getName() + StringUtil.DOT + expression;
                }
                if (!expression.endsWith(")")) {
                    expression = expression + "()";
                }
                if (value == null) {
                    value = StringUtil.empty;
                }
                scriptRunner.put(soberColumn.getName(), value);
                if (!ObjectUtil.toBoolean(scriptRunner.eval(expression, 0))) {
                    result.put(soberColumn.getName(), "value:" + value + " dataType:" + dataType);
                }
                scriptRunner.put(soberColumn.getName(), null);
            }
            if (!result.isEmpty()) {
                throw new ValidException(result, obj);
            }
        } finally {
            scriptRunner.exit();
        }
    }

    /**
     * 根据字段删除一个对象,或一组对象,快速删除
     *
     * @param jdbcOperations jdbc操作类
     * @param aClass         类
     * @param field          字段
     * @param serializable   字段值
     * @return 是否成功
     */
    public static int delete(SoberSupport jdbcOperations, Class<?> aClass, String field, Serializable serializable) {
        TableModels soberTable = jdbcOperations.getSoberTable(aClass);
        if (soberTable == null) {
            return -2;
        }
        if (!soberTable.containsField(field)) {
            return -1;
        }
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_FIELD_NAME, field);
        valueMap.put(Dialect.KEY_FIELD_NAME + Dialect.FIELD_QUOTE, isQuote(soberTable, field));
        valueMap.put(Dialect.KEY_FIELD_VALUE, serializable);
        Dialect dialect = jdbcOperations.getSoberFactory().getDialect();
        String sqlText = dialect.processTemplate(Dialect.SQL_DELETE, valueMap);
        try {
            int x = jdbcOperations.update(sqlText);
            if (x >= 0 && soberTable.isAutoCleanCache()) {
                jdbcOperations.evict(aClass);
            }
            return x;
        } catch (Exception e) {
            log.error("delete aClass={},field={},serializable={}", aClass, field, serializable);
        }
        return -2;
    }

    /**
     * @param jdbcOperations jdbc操作类
     * @param dialect        sql适配器
     * @param sqlText        使用sql直接更新,参数 ？ 的jdbc原生形式
     * @param params         参数
     * @return 更新数量
     * @throws Exception 异常
     */
    public static int update(SoberSupport jdbcOperations, Dialect dialect, String sqlText, Object[] params) throws Exception {
        if (StringUtil.isEmpty(sqlText)) {
            return -2;
        }
        int result;
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_WRITE);
            jdbcOperations.debugPrint(sqlText);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            }
            if (!ArrayUtil.isEmpty(params)) {
                for (int i = 0; i < params.length; i++) {
                    jdbcOperations.debugPrint("prepared[" + (i + 1) + "]=" + params[i]);
                    dialect.setPreparedStatementValue(statement, i + 1, params[i]);
                }
            }
            result = statement.executeUpdate();
        } catch (Exception e) {
            log.error("update sql={},params=", sqlText, e);
            throw e;
        } finally {
            closeStatement(statement);
            closeConnection(conn);
        }
        return result;
    }


    /**
     * 删除一堆对象
     *
     * @param jdbcOperations jdbc操作类
     * @param dialect        sql适配器
     * @param collection     删除列表
     * @return 是否成功删除
     * @throws Exception 异常
     */
    public static boolean deleteAll(SoberSupport jdbcOperations, Dialect dialect, Collection<?> collection) throws Exception {
        if (ObjectUtil.isEmpty(collection)) {
            return true;
        }
        PreparedStatement statement = null;
        Connection conn = null;
        ResultSet resultSet = null;
        TableModels soberTable = null;
        List<Object> deleteId = new ArrayList<>();
        int i = 0;
        for (Object object : collection) {
            if (i == 0) {
                soberTable = jdbcOperations.getSoberTable(object.getClass());
                if (soberTable == null) {
                    return false;
                }
            }
            deleteId.add(BeanUtil.getProperty(object, soberTable.getPrimary()));
            i++;
        }

        if (soberTable == null) {
            return false;
        }
        String[] fieldArray = soberTable.getFieldArray();
        if (fieldArray == null || fieldArray.length < 1) {
            return false;
        }
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_FIELD_LIST, fieldArray);
        valueMap.put(Dialect.KEY_FIELD_COUNT, fieldArray.length);
        valueMap.put(Dialect.KEY_FIELD_NAME, soberTable.getPrimary());
        valueMap.put(Dialect.KEY_FIELD_NAME + Dialect.FIELD_QUOTE, isQuote(soberTable, soberTable.getPrimary()));
        String sqlText = StringUtil.empty;
        try {
            valueMap.put(Dialect.KEY_FIELD_VALUE, deleteId);
            conn = jdbcOperations.getConnection(SoberEnv.WRITE_ONLY);
            sqlText = dialect.processTemplate(Dialect.SQL_DELETE_IN, valueMap);
            jdbcOperations.debugPrint(sqlText);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            }
            boolean x = statement.execute();
            if (x && soberTable.isAutoCleanCache() && soberTable.getEntity() != null) {
                jdbcOperations.evict(soberTable.getEntity());
            }
            return x;
        } catch (Exception e) {
            log.error("SQL:" + sqlText, e);
            throw e;
        } finally {
            valueMap.clear();
            closeResultSet(resultSet);
            closeStatement(statement);
            closeConnection(conn);
        }
    }

    /**
     * 级联方式删除对象,只删除一层
     *
     * @param jdbcOperations jdbc操作对象
     * @param aClass         删除对象
     * @param field          删除字段
     * @param serializable   字段值
     * @param term           条件
     * @param delChild       删除子映射对象
     * @return 删除数量
     */
    public static int delete(SoberSupport jdbcOperations, Class<?> aClass, String field, Serializable serializable, String term, boolean delChild) {
        Criteria criteria = jdbcOperations.createCriteria(aClass);
        if (serializable.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(serializable);
            Object[] params = new Object[length];
            for (int i = 0; i < length; i++) {
                params[i] = java.lang.reflect.Array.get(serializable, i);
            }
            criteria = criteria.add(Expression.in(field, params));
        } else {
            criteria = criteria.add(Expression.eq(field, serializable));
        }
        if (!StringUtil.isNull(term)) {
            try {
                criteria = SSqlExpression.getTermExpression(criteria, term);
            } catch (Exception e) {
                log.error("delete:" + criteria.toString(), e);
            }
        }
        return criteria.delete(delChild);
    }

    /**
     * @param jdbcOperations jdbc操作对象
     * @param dialect        sql适配器
     * @param aClass         class 类对象
     * @param field          字段
     * @param serializable   id
     * @param loadChild      载入子对象
     * @param <T>            类型
     * @return query a object 返回对象
     */
    public static <T> T get(SoberSupport jdbcOperations, Dialect dialect, Class<T> aClass, Serializable field, Serializable serializable, boolean loadChild) {
        TableModels soberTable = jdbcOperations.getSoberTable(aClass);
        if (field == null) {
            field = soberTable.getPrimary();
        }

        if (!StringUtil.hasLength((String) field)) {
            log.error(aClass + " SQL Primary is NULL,配置表没设置ID");
            return null;
        }

        T result = null;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_FIELD_NAME, field);

        String sqlText = StringUtil.empty;
        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_ONLY);
            if (conn == null) {
                String info = "得到链接为空，数据库连接池不能正常连接,Connection is null";
                SQLException e = new SQLException(info);
                log.error(info, e);
                throw e;
            }
            sqlText = dialect.processTemplate(Dialect.SQL_QUERY_ONE_FIELD, valueMap);
            jdbcOperations.debugPrint(sqlText);
            if (!dialect.supportsConcurReadOnly()) {
                preparedStatement = conn.prepareStatement(sqlText);
            } else {
                preparedStatement = conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }
            preparedStatement.setMaxRows(1);
            jdbcOperations.debugPrint("prepared[1]=" + serializable);
            dialect.setPreparedStatementValue(preparedStatement, 1, serializable);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = loadColumnsValue(jdbcOperations, jdbcOperations.getDialect(), aClass, resultSet);
                //载入映射对象
                if (loadChild) {
                    loadNexusValue(jdbcOperations, soberTable, result);
                }
                //载入计算数据
                result = (T) calcUnique(jdbcOperations, jdbcOperations.getDialect(), soberTable, result);
            }
        } catch (Exception e) {
            log.error("sql:" + sqlText, e);
        } finally {
            closeResultSet(resultSet);
            closeStatement(preparedStatement);
            closeConnection(conn);
            valueMap.clear();
        }
        return result;
    }

    /**
     * 查询返回封装好的列表
     *
     * @param jdbcOperations jdbc操作对象
     * @param cla            要封装返回的对象
     * @param sql            SQL
     * @param param          参数
     * @param <T>            类型
     * @return 封装好的查询对象
     */
    public static <T> List<T> query(SoberSupport jdbcOperations, Class<T> cla, String sql, Object[] param) {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        List<T> result = null;
        //取出cache  begin
        SoberFactory soberFactory = jdbcOperations.getSoberFactory();
        Dialect dialect = jdbcOperations.getDialect();
        TableModels soberTable = null;
        String cacheKey = null;
        if (!cla.getName().equals(Map.class.getName())) {
            soberTable = soberFactory.getTableModels(cla, jdbcOperations);
            if (soberTable != null && soberFactory.isUseCache() && soberTable.isUseCache()) {
                StringBuilder termKey = new StringBuilder();
                termKey.append(sql);
                termKey.append("_").append("_p_").append(soberFactory.getDatabaseType()).append("_");
                if (param != null) {
                    for (Object po : param) {
                        termKey.append(ObjectUtil.toString(po));
                    }
                }
                cacheKey = SoberUtil.getListKey(cla, StringUtil.replace(termKey.toString(), StringUtil.EQUAL, "_"), StringUtil.empty, 1, jdbcOperations.getMaxRows(), false);
                result = (List<T>) JSCacheManager.get(cla, cacheKey);
                if (!ObjectUtil.isEmpty(result)) {
                    return result;
                }
            }
        }

        result = new ArrayList<>();
        //取出cache  end
        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_ONLY);
            jdbcOperations.debugPrint(sql);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sql);
            } else {
                statement = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            }

            if (param != null) {
                for (int i = 0; i < param.length; i++) {
                    jdbcOperations.debugPrint("prepared[" + (i + 1) + "]=" + param[i]);
                    dialect.setPreparedStatementValue(statement, i + 1, param[i]);
                }
            }
            setFetchSize(statement, 500);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                T resultObject;
                if (soberTable != null) {
                    resultObject = loadColumnsValue(jdbcOperations, jdbcOperations.getDialect(), cla, resultSet);
                } else {
                    resultObject = getBean(resultSet, cla, dialect);
                }
                result.add(resultObject);
            }
        } catch (Exception e) {
            log.error("SQL:" + sql, e);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            closeConnection(conn);
            if (soberTable != null && soberFactory.isUseCache() && soberTable.isUseCache()) {
                JSCacheManager.put(cla, cacheKey, result);
            }
        }
        return result;
    }


    /**
     * @param jdbcOperations jdbc操作对象
     * @param sqlText        sql
     * @param param          参数数组
     * @param currentPage    页数
     * @param totalCount     返回行数
     * @return List  查询返回列表
     */
    public static List<?> query(SoberSupport jdbcOperations, String sqlText, Object[] param, int currentPage, long totalCount) {
        return query(jdbcOperations, sqlText, param, currentPage, (int) totalCount);
    }

    /**
     * @param jdbcOperations jdbc操作对象
     * @param sqlText        sql
     * @param param          参数数组
     * @param currentPage    页数
     * @param totalCount     返回行数
     * @return List  查询返回列表
     */
    public static List<?> query(SoberSupport jdbcOperations, String sqlText, Object[] param, int currentPage, int totalCount) {

        if (totalCount > jdbcOperations.getMaxRows()) {
            totalCount = jdbcOperations.getMaxRows();
        }
        if (currentPage <= 0) {
            currentPage = 1;
        }
        int iEnd = currentPage * totalCount;
        if (iEnd < 0) {
            iEnd = 0;
        }
        int iBegin = iEnd - totalCount;
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Dialect dialect = jdbcOperations.getDialect();
        List<Object> result = new LinkedList<>();
        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_ONLY);
            jdbcOperations.debugPrint(sqlText);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            }

            setFetchSize(statement, iEnd);
            statement.setMaxRows(iEnd);

            if (param != null) {
                for (int i = 0; i < param.length; i++) {
                    jdbcOperations.debugPrint("prepared[" + (i + 1) + "]=" + param[i]);
                    dialect.setPreparedStatementValue(statement, i + 1, param[i]);
                }
            }

            resultSet = statement.executeQuery();
            if (iBegin == 0 || resultSet.absolute(iBegin)) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                while (resultSet.next()) {
                    Map<String, Object> beanMap = new HashMap<>();
                    for (int n = 1; n <= metaData.getColumnCount(); n++) {
                        String field = StringUtil.underlineToCamel(metaData.getColumnLabel(n));
                        if (!beanMap.containsKey(field)) {
                            Object value = dialect.getResultSetValue(resultSet, n);
                            beanMap.put(field, value);
                        }
                    }
                    result.add(ReflectUtil.createDynamicBean(beanMap));
                    if (result.size() > totalCount) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("SQL:" + sqlText, e);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            closeConnection(conn);
        }
        return result;
    }

    /**
     * @param jdbcOperations jdbc操作对象
     * @param dialect        sql适配器
     * @param object         查询对象
     * @param updateFiled    更新一个字段
     * @return 指定更新字段, 特殊不验证了
     * @throws Exception 异常
     */
    public static int update(SoberSupport jdbcOperations, Dialect dialect, Object object, String[] updateFiled) throws Exception {
        if (object == null) {
            return -2;
        }
        if (ArrayUtil.isEmpty(updateFiled)) {
            return jdbcOperations.update(object);
        }

        TableModels soberTable = jdbcOperations.getSoberTable(object.getClass());
        SoberFactory soberFactory = jdbcOperations.getSoberFactory();
        Connection conn = null;
        PreparedStatement statement = null;
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_FIELD_LIST, updateFiled);
        valueMap.put(Dialect.KEY_FIELD_COUNT, updateFiled.length);
        valueMap.put(Dialect.KEY_FIELD_NAME, soberTable.getPrimary());
        valueMap.put(Dialect.KEY_FIELD_NAME + Dialect.FIELD_QUOTE, isQuote(soberTable, soberTable.getPrimary()));
        Object value;
        String sqlText = StringUtil.empty;

        try {
            value = BeanUtil.getProperty(object, soberTable.getPrimary());
            valueMap.put(Dialect.KEY_FIELD_VALUE, value);
            if (valueMap.get(Dialect.KEY_FIELD_VALUE) == null) {
                throw new SQLException("SQL Primary Key is NULL");
            }
            conn = jdbcOperations.getConnection(SoberEnv.READ_WRITE);
            sqlText = dialect.processTemplate(Dialect.SQL_UPDATE, valueMap);
            jdbcOperations.debugPrint(sqlText);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            }
            //TYPE_FORWARD_ONLY
            setPreparedStatementValueList(jdbcOperations, dialect, statement, updateFiled, object);
            int x = statement.executeUpdate();
            if (x >= 0 && soberTable.isAutoCleanCache() && soberTable.getEntity() != null) {
                jdbcOperations.evict(soberTable.getEntity());
            }
            return x;
        } catch (Exception e) {
            log.error(sqlText, e);
        } finally {
            valueMap.clear();
            closeStatement(statement);
            closeConnection(conn);
            if (soberFactory.isUseCache() && soberTable.isUseCache()) {
                //同时更新缓存
                String cacheKey = SoberUtil.getLoadKey(soberTable.getEntity(), soberTable.getPrimary(), BeanUtil.getProperty(object, soberTable.getPrimary()));
                JSCacheManager.remove(soberTable.getEntity(), cacheKey);
            }
        }
        return -2;
    }


    /**
     * @param jdbcOperations jdbc操作对象
     * @param dialect        sql适配器
     * @param object         对象
     * @return 是否成功
     * @throws Exception 异常
     */
    public static int update(SoberSupport jdbcOperations, Dialect dialect, Object object) throws Exception {
        if (object == null) {
            return -2;
        }
        //////////配置验证才能够保存 begin
        SoberFactory soberFactory = jdbcOperations.getSoberFactory();
        if (soberFactory.isValid()) {
            validator(jdbcOperations, object);
        }
        //////////配置验证才能够保存 end                ;
        TableModels soberTable = jdbcOperations.getSoberTable(object.getClass());
        if (soberTable == null) {
            return -2;
        }
        if (!soberTable.isCreate()) {
            throw new Exception("注释标签配置为不可建表对象，不允许保存" + object.getClass());
        }
        BusinessFilterUtil.updateFilter(jdbcOperations, object);
        Connection conn = null;
        PreparedStatement statement = null;
        String[] fieldArray = soberTable.getFieldArray();
        if (ArrayUtil.isEmpty(fieldArray)) {
            return -2;
        }
        fieldArray = ArrayUtil.delete(fieldArray, soberTable.getPrimary(), true);

        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_FIELD_LIST, fieldArray);
        valueMap.put(Dialect.KEY_FIELD_COUNT, fieldArray.length);
        valueMap.put(Dialect.KEY_FIELD_NAME, soberTable.getPrimary());
        valueMap.put(Dialect.KEY_FIELD_NAME + Dialect.FIELD_QUOTE, isQuote(soberTable, soberTable.getPrimary()));
        Object value = BeanUtil.getProperty(object, soberTable.getPrimary());
        valueMap.put(Dialect.KEY_FIELD_VALUE, value);
        if (value == null) {
            SQLException e = new SQLException("ERROR:SQL,update primary is null,更新的关键字不能为空!");
            log.error(ObjectUtil.toString(object), e);
            throw e;
        }

        int result;
        String sqlText = StringUtil.empty;
        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_WRITE);
            sqlText = dialect.processTemplate(Dialect.SQL_UPDATE, valueMap);
            jdbcOperations.debugPrint(sqlText);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            }

            setPreparedStatementValueList(jdbcOperations, dialect, statement, fieldArray, object);
            result = statement.executeUpdate();

            ///////////////////处理关联对象begin
            Map<String, SoberNexus> nexus = soberTable.getNexusMap();
            for (String colName : nexus.keySet()) {
                SoberNexus soberNexus = nexus.get(colName);
                if (!soberNexus.isUpdate()) {
                    continue;
                }
                if (MappingType.OneToOne.equalsIgnoreCase(soberNexus.getMapping())) {
                    Object findValue = BeanUtil.getProperty(object, soberNexus.getField());
                    if (findValue == null) {
                        continue;
                    }
                    jdbcOperations.delete(soberNexus.getTargetEntity(), soberNexus.getTargetField(), (Serializable) findValue);
                    Object saveObj = BeanUtil.getProperty(object, colName);
                    if (saveObj != null) {
                        result = result + jdbcOperations.save(saveObj);
                    }
                }
                if (MappingType.OneToMany.equalsIgnoreCase(soberNexus.getMapping())) {
                    Object findValue = BeanUtil.getProperty(object, soberNexus.getField());
                    if (findValue == null) {
                        continue;
                    }
                    Criteria criteria = jdbcOperations.createCriteria(soberNexus.getTargetEntity()).add(Expression.eq(soberNexus.getTargetField(), findValue));
                    SSqlExpression.getTermExpression(criteria, soberNexus.getTerm()).delete(false);
                    Collection<?> saveObjList = (Collection<?>) BeanUtil.getProperty(object, colName);
                    if (saveObjList != null && !saveObjList.isEmpty()) {
                        for (Object child : saveObjList) {
                            BeanUtil.setSimpleProperty(child, soberNexus.getTargetField(), findValue);
                        }
                        result = result + jdbcOperations.save(saveObjList);
                        jdbcOperations.evict(soberNexus.getTargetEntity());
                    }
                }
            }
            ///////////////////处理关联对象end
            if (soberTable.isAutoCleanCache() && soberTable.getEntity() != null) {
                jdbcOperations.evict(soberTable.getEntity());
            }
        } catch (Exception e) {
            log.error(sqlText, e);
            throw e;
        } finally {
            closeStatement(statement);
            closeConnection(conn);
            valueMap.clear();
        }
        return result;
    }

    /**
     * 先判断是否存在,存在就使用更新,否则增加
     *
     * @param jdbcOperations jdbc操作对象
     * @param dialect        sql适配器
     * @param object         对象
     * @return 保存是否成功
     * @throws Exception 异常
     */
    public static int saveOrUpdate(SoberSupport jdbcOperations, Dialect dialect, Object object) throws Exception {
        if (object == null) {
            return -2;
        }

        if (object instanceof Collection) {
            return saveOrUpdateAll(jdbcOperations, (Collection<?>) object);
        }
        SoberFactory soberFactory = jdbcOperations.getSoberFactory();
        //////////配置验证才能够保存 begin
        if (soberFactory.isValid()) {
            validator(jdbcOperations, object);
        }
        //////////配置验证才能够保存 end

        TableModels soberTable = jdbcOperations.getSoberTable(object.getClass());
        if (!soberTable.isCreate()) {
            throw new Exception("注释标签配置为不可建表对象，不允许保存" + object.getClass());
        }
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String[] fieldArray = soberTable.getFieldArray();
        if (fieldArray == null || fieldArray.length < 1) {
            return -2;
        }
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_FIELD_LIST, fieldArray);
        valueMap.put(Dialect.KEY_FIELD_COUNT, fieldArray.length);
        valueMap.put(Dialect.KEY_FIELD_NAME, soberTable.getPrimary());
        valueMap.put(Dialect.KEY_FIELD_NAME + Dialect.FIELD_QUOTE, isQuote(soberTable, soberTable.getPrimary()));
        valueMap.put(Dialect.KEY_FIELD_VALUE, BeanUtil.getProperty(object, soberTable.getPrimary()));
        if (valueMap.get(Dialect.KEY_FIELD_VALUE) == null) {
            if (dialect.isSupportsGetGeneratedKeys() && soberTable.isAutoId() && soberTable.isSerial()) {
                return jdbcOperations.save(object);
            } else {
                throw new SQLException("SQL Primary is NULL");
            }
        }
        String sqlText = StringUtil.empty;
        Connection conn = null;
        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_WRITE);
            sqlText = dialect.processTemplate(Dialect.SQL_HAVE, valueMap);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            }
            jdbcOperations.debugPrint(sqlText);
            resultSet = statement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                fieldArray = ArrayUtil.delete(fieldArray, soberTable.getPrimary(), true);
                valueMap.put(Dialect.KEY_FIELD_LIST, fieldArray);
                valueMap.put(Dialect.KEY_FIELD_COUNT, fieldArray.length);
                sqlText = dialect.processTemplate(Dialect.SQL_UPDATE, valueMap);
                jdbcOperations.debugPrint(sqlText);
                statement = conn.prepareStatement(sqlText);
                setPreparedStatementValueList(jdbcOperations, dialect, statement, fieldArray, object);
            } else {
                sqlText = dialect.processTemplate(Dialect.SQL_INSERT, valueMap);
                jdbcOperations.debugPrint(sqlText);
                if (!dialect.supportsConcurReadOnly()) {
                    statement = conn.prepareStatement(sqlText);
                } else {
                    statement = conn.prepareStatement(sqlText, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                }
                setPreparedStatementValueList(jdbcOperations, dialect, statement, fieldArray, object);
            }
            int x = statement.executeUpdate();
            if (soberTable.isAutoCleanCache() && soberTable.getEntity() != null) {
                jdbcOperations.evict(soberTable.getEntity());
            }
            return x;
        } catch (Exception e) {
            log.error("SQL:" + sqlText, e);
            throw e;
        } finally {
            valueMap.clear();
            closeResultSet(resultSet);
            closeStatement(statement);
            closeConnection(conn);
        }
    }


    /**
     * 先判断是否存在,存在就使用更新,否则增加 ,处理一个队
     *
     * @param jdbcOperations jdbc操作对象
     * @param collection     更新对象列表
     * @return int 返回是否成功
     * @throws Exception 异常
     */
    public static int saveOrUpdateAll(SoberSupport jdbcOperations, Collection<?> collection) throws Exception {
        if (ObjectUtil.isEmpty(collection)) {
            return -2;
        }

        SoberFactory soberFactory = jdbcOperations.getSoberFactory();
        Dialect dialect = soberFactory.getDialect();

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        TableModels soberTable = null;
        List<Object> deleteId = new ArrayList<>();
        //得到类型
        int i = 0;
        for (Object object : collection) {
            if (i == 0) {
                soberTable = jdbcOperations.getSoberTable(object.getClass());
                if (soberTable == null) {
                    log.error("识别不到表结构模型:{}", collection.iterator().next());
                    return -2;
                }
                if (!soberTable.isCreate()) {
                    throw new Exception("注释标签配置为不可建表对象，不允许保存" + object.getClass());
                }
            }
            deleteId.add(BeanUtil.getProperty(object, soberTable.getPrimary()));
            i++;
            //////////配置验证才能够保存 begin

            if (soberFactory.isValid()) {
                validator(jdbcOperations, object);
            }
            ////////////配置验证才能够保存 end

        }

        assert soberTable != null;
        String[] fieldArray = soberTable.getFieldArray();
        if (ArrayUtil.isEmpty(fieldArray)) {
            return -2;

        }
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_FIELD_LIST, fieldArray);
        valueMap.put(Dialect.KEY_FIELD_COUNT, fieldArray.length);
        valueMap.put(Dialect.KEY_FIELD_NAME, soberTable.getPrimary());
        valueMap.put(Dialect.KEY_FIELD_NAME + Dialect.FIELD_QUOTE, isQuote(soberTable, soberTable.getPrimary()));
        int result = 0;
        String sqlText = StringUtil.empty;
        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_WRITE);

            //////////////删除 begin
            valueMap.put(Dialect.KEY_FIELD_VALUE, deleteId);
            sqlText = dialect.processTemplate(Dialect.SQL_DELETE_IN, valueMap);
            jdbcOperations.debugPrint(sqlText);
            if (!dialect.supportsConcurReadOnly()) {
                conn.prepareStatement(sqlText).execute();
            } else {
                conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE).execute();
            }
            //////////////删除 end

            sqlText = dialect.processTemplate(Dialect.SQL_INSERT, valueMap);
            jdbcOperations.debugPrint(sqlText);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            }
            for (Object object : collection) {
                setPreparedStatementValueList(jdbcOperations, dialect, statement, fieldArray, object);
                int temp = statement.executeUpdate();
                if (temp < 1) {
                    throw new SQLException(sqlText + " object:" + MapUtil.toString(ObjectUtil.getMap(object)));
                } else {
                    result = result + temp;
                }
                statement.clearParameters();
            }

            if (soberTable.isAutoCleanCache() && soberTable.getEntity() != null) {
                jdbcOperations.evict(soberTable.getEntity());
            }
            return result;
        } catch (Exception e) {
            log.error("SQL:" + sqlText, e);
            throw e;
        } finally {
            valueMap.clear();
            closeResultSet(resultSet);
            closeStatement(statement);
            closeConnection(conn);

        }
    }

    /**
     * @param jdbcOperations jdbc操作对象
     * @param dialect        sql适配器
     * @param object         保存对象
     * @param child          保持子对象
     * @return 保存一个对象
     * @throws Exception 异常
     */
    public static int save(SoberSupport jdbcOperations, Dialect dialect, Object object, final boolean child) throws Exception {
        if (object == null) {
            return -2;
        }
        if (object instanceof Collection) {
            return jdbcOperations.save((Collection<?>) object);
        }

        SoberFactory soberFactory = jdbcOperations.getSoberFactory();
        //////////配置验证才能够保存 begin
        if (soberFactory.isValid()) {
            validator(jdbcOperations, object);
        }
        //////////配置验证才能够保存 end
        TableModels soberTable = jdbcOperations.getSoberTable(object.getClass());
        if (soberTable == null) {
            log.error("先确保数据库jdbc连接正常, @Table 标签没有配置:{}", object.getClass());
            throw new Exception("先确保数据库jdbc连接正常, @Table 标签没有配置");
        }
        if (!soberTable.isCreate()) {
            throw new Exception("注释标签配置为不可建表对象，不允许保存" + object.getClass());
        }

        //业务逻辑过滤处理begin
        BusinessFilterUtil.saveFilter(jdbcOperations, object);
        //业务逻辑过滤处理end

        Object idValue = BeanUtil.getFieldValue(object, soberTable.getPrimary(), false);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());

        String sqlText = StringUtil.empty;
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            //判断是否支持自动生成ID，如果支持，字段中就不包含id字段，否则包含id字段,先自动生成ID
            String[] fieldArray;
            if (!soberTable.isAutoId() && dialect.isSupportsSavePoints() && (idValue == null || 0 == ObjectUtil.toLong(idValue))) {
                fieldArray = soberTable.getFieldArray();
            } else {
                //不用支持ID字段，就自动生成ID字段
                AnnotationUtil.autoSetId(object, soberTable.getPrimary(), jdbcOperations);
                fieldArray = soberTable.getFullFieldArray();
            }
            if (fieldArray == null || fieldArray.length < 1) {
                return -2;
            }


            valueMap.put(Dialect.KEY_FIELD_LIST, fieldArray);
            valueMap.put(Dialect.KEY_FIELD_COUNT, fieldArray.length);
            sqlText = dialect.processTemplate(Dialect.SQL_INSERT, valueMap);

            conn = jdbcOperations.getConnection(SoberEnv.READ_WRITE);
            if (StringUtil.isNull(sqlText)) {
                //不破坏连接属性
                return -2;
            }
            if (!soberTable.isAutoId() && dialect.isSupportsSavePoints() && soberTable.isSerial()) {
                statement = conn.prepareStatement(sqlText, Statement.RETURN_GENERATED_KEYS);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            }
            jdbcOperations.debugPrint(sqlText);
            for (int i = 0; i < fieldArray.length; i++) {

                Object value = BeanUtil.getProperty(object, fieldArray[i]);
                SoberColumn soberColumn = soberTable.getColumn(fieldArray[i]);
                if (value == null && soberColumn != null) {
                    //放入默认数据 begin

                    if (!StringUtil.isNull(soberColumn.getDefaultValue())) {
                        value = soberColumn.getDefaultValue();
                    }
                    //放入默认数据 end
                }
                jdbcOperations.debugPrint("prepared[" + (i + 1) + "]=" + value);
                if (soberColumn != null) {
                    dialect.setPreparedStatementValue(statement, i + 1, BeanUtil.getTypeValue(value, soberColumn.getClassType()));
                } else {
                    dialect.setPreparedStatementValue(statement, i + 1, value);
                }
            }
            int result = statement.executeUpdate();
            if (result < 1) {
                return result;
            }
            if (!soberTable.isAutoId() && dialect.isSupportsGetGeneratedKeys() && soberTable.isSerial()) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    BeanUtil.setSimpleProperty(object, soberTable.getPrimary(), dialect.getResultSetValue(rs, 1));
                }
                closeResultSet(rs);
            }
            if (child) {
                Map<String, SoberNexus> nexusMap = soberTable.getNexusMap();
                if (!ObjectUtil.isEmpty(nexusMap)) {
                    // Object keyObj = BeanUtil.getProperty(object, soberTable.getPrimary());
                    /////////////////////////////保存关联对象begin
                    Map<String, SoberNexus> nexus = soberTable.getNexusMap();
                    for (String colName : nexus.keySet()) {
                        SoberNexus soberNexus = nexus.get(colName);
                        if (!soberNexus.isSave()) {
                            continue;
                        }
                        if (MappingType.OneToOne.equalsIgnoreCase(soberNexus.getMapping())) {
                            Object oneToOneObject = BeanUtil.getProperty(object, colName);
                            if (oneToOneObject == null) {
                                continue;
                            }
                            Object oneToOneValue = BeanUtil.getProperty(object, soberNexus.getField());
                            Object v = BeanUtil.getFieldValue(oneToOneObject, soberNexus.getTargetField(), false);
                            if (ObjectUtil.isEmpty(v) || ((v instanceof Number) && ObjectUtil.toLong(v) == 0)) {
                                BeanUtil.setSimpleProperty(oneToOneObject, soberNexus.getTargetField(), oneToOneValue);
                            }
                            result = result + jdbcOperations.save(oneToOneObject, soberNexus.isChain());
                        }
                        if (MappingType.OneToMany.equalsIgnoreCase(soberNexus.getMapping())) {
                            Collection<?> oneToMayObjects = (Collection<?>) BeanUtil.getProperty(object, colName);
                            if (oneToMayObjects == null || oneToMayObjects.isEmpty()) {
                                continue;
                            }
                            Object oneToManyValue = BeanUtil.getProperty(object, soberNexus.getField());
                            for (Object o : oneToMayObjects) {
                                Object v = BeanUtil.getFieldValue(o, soberNexus.getTargetField(), false);
                                if (ObjectUtil.isEmpty(v) || ((v instanceof Number) && ObjectUtil.toLong(v) == 0)) {
                                    BeanUtil.setSimpleProperty(o, soberNexus.getTargetField(), oneToManyValue);
                                }
                            }
                            int s = jdbcOperations.save(oneToMayObjects, soberNexus.isChain());
                            if (s != oneToMayObjects.size()) {
                                return -2;
                            }
                            result = result + oneToMayObjects.size();
                            oneToMayObjects.clear();
                        }
                    }
                }
                //save
                /////////////////////////////保存关联对象end
            }
            if (soberTable.isAutoCleanCache() && soberTable.getEntity() != null) {
                jdbcOperations.evict(soberTable.getEntity());
            }
            return result;
        } catch (Exception e) {
            SoberColumn soberColumn = soberTable.getColumn(soberTable.getPrimary());
            if (soberTable.isAutoId() && soberColumn != null && ClassUtil.isNumberType(soberColumn.getClassType())) {
                String msg = e.getMessage();
                if (msg != null && msg.contains("Duplicate") && msg.contains("PRIMARY")) {
                    //关键字重复了,这些去修复一下
                    AnnotationUtil.fixIdCacheMax(soberTable, object, jdbcOperations);
                    if (DatabaseEnumType.find(soberFactory.getDatabaseType()).equals(DatabaseEnumType.POSTGRESQL) && msg.contains("duplicate key value")) {
                        //手工修改了数据库的seq,这里尝试修复
                        AnnotationUtil.postgresqlFixSeqId(soberTable, jdbcOperations);
                    }
                }
            }
            log.error(sqlText, e);
            //在事务中不能关闭连接,关闭了会滚会失败
            throw e;
        } finally {
            closeStatement(statement);
            closeConnection(conn);
            valueMap.clear();
        }
    }

    /**
     * @param jdbcOperations jdbc操作对象
     * @param collection     批量快速保存 集合
     * @return 更新数量, 如果错误 返回 负数
     * @throws Exception 异常
     */
    public static int batchSave(SoberSupport jdbcOperations, Collection<?> collection) throws Exception {
        if (collection == null || collection.isEmpty()) {
            return -2;
        }
        Dialect dialect = jdbcOperations.getDialect();
        Object checkObj = collection.iterator().next();
        TableModels soberTable = jdbcOperations.getSoberTable(checkObj.getClass());
        if (!soberTable.isCreate()) {
            throw new Exception("注释标签配置为不可建表对象，不允许保存" + checkObj.getClass());
        }
        PreparedStatement statement = null;
        Object idValue = BeanUtil.getProperty(checkObj, soberTable.getPrimary());
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());

        SoberFactory soberFactory = jdbcOperations.getSoberFactory();

        String[] fieldArray;
        if (!soberTable.isAutoId() && dialect.isSupportsGetGeneratedKeys() && (idValue == null || 0 == ObjectUtil.toLong(idValue))) {
            fieldArray = soberTable.getFieldArray();
        } else {
            //不用支持ID字段，就自动生成ID字段
            for (Object object : collection) {
                AnnotationUtil.autoSetId(object, soberTable.getPrimary(), jdbcOperations);
            }
            fieldArray = soberTable.getFullFieldArray();
        }
        if (fieldArray == null || fieldArray.length < 1) {
            return -2;
        }

        valueMap.put(Dialect.KEY_FIELD_LIST, fieldArray);
        valueMap.put(Dialect.KEY_FIELD_COUNT, fieldArray.length);

        int result = 0;
        String sqlText = StringUtil.empty;
        Connection conn = jdbcOperations.getConnection(SoberEnv.WRITE_ONLY);
        boolean oldAutoCommit = conn.getAutoCommit();
        try {
            //先自动生成ID
            conn.setAutoCommit(false);
            sqlText = dialect.processTemplate(Dialect.SQL_INSERT, valueMap);
            jdbcOperations.debugPrint(sqlText);
            if (!soberTable.isAutoId() && dialect.isSupportsGetGeneratedKeys() && soberTable.isSerial()) {
                statement = conn.prepareStatement(sqlText, Statement.RETURN_GENERATED_KEYS);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            }
            int cm = 0;
            for (Object object : collection) {
                cm++;
                //////////配置验证才能够保存 begin
                if (soberFactory.isValid()) {
                    validator(jdbcOperations, object);
                }
                //////////配置验证才能够保存 end

                for (int i = 0; i < fieldArray.length; i++) {
                    jdbcOperations.debugPrint("prepared[" + (i + 1) + "]=" + BeanUtil.getProperty(object, fieldArray[i]));
                    dialect.setPreparedStatementValue(statement, i + 1, BeanUtil.getProperty(object, fieldArray[i]));
                }
                statement.addBatch();
                if (cm % 500 == 0) {
                    result = result + ArrayUtil.sum(statement.executeBatch());
                    conn.commit();
                }
            }
            if (cm % 500 != 0) {
                result = result + ArrayUtil.sum(statement.executeBatch());
                conn.commit();
            }
            if (soberTable.isAutoCleanCache() && soberTable.getEntity() != null) {
                jdbcOperations.evict(soberTable.getEntity());
            }
        } catch (Exception e) {
            log.error("ERROR SQL:{}",sqlText, e);
        } finally {
            conn.setAutoCommit(oldAutoCommit);
            closeStatement(statement);
            closeConnection(conn);
            valueMap.clear();
        }
        return result;
    }


    /**
     * @param jdbcOperations 数据对象
     * @param savaList      方式的数据对象
     * @param soberTable     数据模型
     * @return 执行结果
     * @throws Exception 异常
     */
    public static int batchSave(SoberSupport jdbcOperations, List<Map> savaList, TableModels soberTable) throws Exception {
        if (savaList == null || savaList.isEmpty()) {
            return -2;
        }

        if (!soberTable.isCreate()) {
            throw new Exception("注释标签配置为不可建表对象，不允许保存" + soberTable);
        }
        PreparedStatement statement = null;

        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());


        Dialect dialect = jdbcOperations.getDialect();
        Map map = savaList.get(0);
        Object idValue = map.get(soberTable.getPrimary());
        String[] fieldArray;
        if (!soberTable.isAutoId() && dialect.isSupportsSavePoints() && (idValue == null || 0 == ObjectUtil.toLong(idValue))) {
            fieldArray = soberTable.getFieldArray();
        } else {
            //不用支持ID字段，就自动生成ID字段
            fieldArray = soberTable.getFullFieldArray();
        }
        if (fieldArray == null || fieldArray.length < 1) {
            return -2;
        }

        valueMap.put(Dialect.KEY_FIELD_LIST, fieldArray);
        valueMap.put(Dialect.KEY_FIELD_COUNT, fieldArray.length);

        int result = 0;
        String sqlText = StringUtil.empty;
        Connection conn = jdbcOperations.getConnection(SoberEnv.WRITE_ONLY);
        boolean oldAutoCommit = conn.getAutoCommit();
        try {
            //先自动生成ID
            conn.setAutoCommit(false);
            sqlText = dialect.processTemplate(Dialect.SQL_INSERT, valueMap);
            jdbcOperations.debugPrint(sqlText);
            if (!soberTable.isAutoId() && dialect.isSupportsGetGeneratedKeys() && soberTable.isSerial()) {
                statement = conn.prepareStatement(sqlText, Statement.RETURN_GENERATED_KEYS);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            }
            int cm = 0;
            for (Map object : savaList) {
                TreeMap<String, Object> mapObj = new TreeMap<>(object);
                cm++;
                for (int i = 0; i < fieldArray.length; i++) {
                    jdbcOperations.debugPrint("prepared[" + (i + 1) + "]=" + mapObj.get(fieldArray[i]));
                    dialect.setPreparedStatementValue(statement, i + 1, mapObj.get(fieldArray[i]));
                }
                statement.addBatch();
                if (cm % 500 == 0) {
                    result = result + ArrayUtil.sum(statement.executeBatch());
                    conn.commit();
                }
                mapObj.clear();
            }
            if (cm % 500 != 0) {
                result = result + ArrayUtil.sum(statement.executeBatch());
                conn.commit();
            }
        } catch (Exception e) {
            log.error("ERROR SQL:" + sqlText, e);
            throw e;
        } finally {
            conn.setAutoCommit(oldAutoCommit);
            closeStatement(statement);
            closeConnection(conn);
            valueMap.clear();
        }
        return result;
    }

    /**
     * @param jdbcOperations jdbc操作对象
     * @param sqlMapConf     配置
     * @param valueMap       变量
     * @return 是否更新成功
     * @throws SQLException 异常
     */
    public static int[] batchUpdate(SoberSupport jdbcOperations, SqlMapConf sqlMapConf, Map<String, Object> valueMap) throws SQLException {
        if (ObjectUtil.isEmpty(sqlMapConf) || StringUtil.isNull(sqlMapConf.getContext())) {
            return new int[0];
        }
        String valueName = (String) valueMap.getOrDefault(InterceptorProxy.KEY_BATCH_DATA, "item");
        Collection<Object> collection = (Collection<Object>) valueMap.get(valueName);
        if (ObjectUtil.isEmpty(collection)) {
            return new int[0];
        }
        Dialect dialect = jdbcOperations.getDialect();
        final String template = sqlMapConf.getContext();
        if (StringUtil.isEmpty(StringUtil.trim(template))) {
            return new int[0];
        }
        int[] result = null;
        String sqlText = null;
        Connection conn = jdbcOperations.getConnection(SoberEnv.WRITE_ONLY);
        Statement statement = conn.createStatement();
        try {
            int cm = 0;
            for (Object object : collection) {
                cm++;
                Map<String, Object> vMap = new HashMap<>(valueMap);
                vMap.put(valueName, object);
                sqlText = dialect.processSql(template, vMap);
                vMap.clear();
                if (StringUtil.isNull(StringUtil.trim(sqlText))) {
                    log.error("生产SQL失败,template={},valueMap={}", template, vMap);
                    continue;
                }
                jdbcOperations.debugPrint(sqlText);
                statement.addBatch(sqlText);
                if (cm % 500 == 0) {
                    int[] resultTimes = statement.executeBatch();
                    result = ArrayUtil.join(result, resultTimes);
                }
            }
            if (cm % 500 != 0) {
                int[] resultTimes = statement.executeBatch();
                result = ArrayUtil.join(result, resultTimes);
            }

        } catch (Exception e) {
            log.error("ERROR SQL:" + sqlText, e);
        } finally {
            closeStatement(statement);
            closeConnection(conn);
            valueMap.clear();
        }
        return result;
    }


    /**
     * @param jdbcOperations jdbc操作对象
     * @param template       模版
     * @param paramList      参数对象
     * @return 返回执行结果
     * @throws SQLException 异常
     */
    public static int[] batchUpdate(SoberSupport jdbcOperations, String template, List<?> paramList) throws SQLException {
        if (StringUtil.isEmpty(StringUtil.trim(template))) {
            return new int[0];
        }
        Dialect dialect = jdbcOperations.getDialect();
        int[] result = null;
        String sqlText = null;
        Connection conn = jdbcOperations.getConnection(SoberEnv.WRITE_ONLY);
        Statement statement = conn.createStatement();
        try {
            int cm = 0;
            for (Object object : paramList) {
                cm++;
                Map<String, Object> vMap = ObjectUtil.getMap(object);
                sqlText = dialect.processSql(template, vMap);

                if (StringUtil.isNull(StringUtil.trim(sqlText))) {
                    log.error("生产SQL失败,template={},valueMap={}", template, vMap);
                    continue;
                }
                jdbcOperations.debugPrint(sqlText);
                statement.addBatch(sqlText);
                if (cm % 500 == 0) {
                    int[] resultTimes = statement.executeBatch();
                    result = ArrayUtil.join(result, resultTimes);
                }
            }
            if (cm % 500 != 0) {
                int[] resultTimes = statement.executeBatch();
                result = ArrayUtil.join(result, resultTimes);
            }

        } catch (Exception e) {
            log.error("ERROR SQL:" + sqlText, e);
        } finally {
            closeStatement(statement);
            closeConnection(conn);
        }
        return result;
    }

    /**
     * 删除映射关系的对象 ManyToOne 关系不删除
     *
     * @param jdbcOperations jdbc操作类
     * @param o              对象
     * @return boolean 是否成功
     */
    public static int deleteNexus(SoberSupport jdbcOperations, Object o) {
        int result = 0;
        TableModels soberTable = jdbcOperations.getSoberTable(o.getClass());
        for (SoberNexus soberNexus : soberTable.getNexusMap().values()) {
            if (!soberNexus.isDelete()) {
                continue;
            }
            if (!MappingType.ManyToOne.equalsIgnoreCase(soberNexus.getMapping())) {
                Object selfValue;
                try {
                    selfValue = BeanUtil.getProperty(o, soberNexus.getField());
                    if (selfValue == null) {
                        continue;
                    }
                } catch (Exception e) {
                    log.error("映射关系错误:" + o.getClass().getName() + "  方法:" + soberNexus.getField() + "不存在", e);
                    return -2;
                }
                String term = AnnotationUtil.getNexusTerm(o, soberNexus.getTerm());
                result = result + delete(jdbcOperations, soberNexus.getTargetEntity(), soberNexus.getTargetField(), (Serializable) selfValue, term, soberNexus.isChain());
            }
        }
        if (soberTable.isAutoCleanCache() && soberTable.getEntity() != null) {
            jdbcOperations.evict(soberTable.getEntity());
        }
        return result;
    }

    /**
     * 执行一个sql
     *
     * @param jdbcOperations jdbc操作类
     * @param dialect        sql适配器
     * @param cla            类 对映配置中的map命名空间
     * @param sqlText        sql
     * @param params         支持类型 Object[] or HashMap  String,Object,这里是留给参数对象的,所以params没有类型
     * @return 执行情况
     * @throws Exception 异常
     */
    public static boolean execute(SoberSupport jdbcOperations, Dialect dialect, Class<?> cla, String sqlText, Object params) throws Exception {
        Object[] args = null;
        Map<String, Object> valueMap = null;
        TableModels soberTable = jdbcOperations.getSoberTable(cla);
        if (params instanceof Map) {
            valueMap = (Map<String, Object>) params;
        } else if (params instanceof Collection) {
            Collection coll = (Collection) params;
            args = coll.toArray();
        }
        if (valueMap == null) {
            valueMap = new HashMap<>();
        }
        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_PRIMARY_KEY, soberTable.getPrimary());
        assert params instanceof Object[];

        return execute(jdbcOperations, dialect.processSql(sqlText, valueMap), args);
    }


    /**
     * 执行一个 execute
     * execute 方法应该仅在语句能返回多个 ResultSet 对象、多个更新计数或ResultSet 对象与更新计数的组合时使用。当执行某个已存储过程或动态执行未知 SQL 字符串（即应用程序程序员在编译时未知）时，有可能出现多个结果的情况，尽管这种情况很少见。例如，用户可能执行一个已存储过程（使用 CallableStatement 对象 - 参见第 135 页的 CallableStatement），并且该已存储过程可执行更新，然后执行选择，再进行更新，再进行选择，等等。通常使用已存储过程的人应知道它所返回的内容。
     * 因为方法 execute 处理非常规情况，所以获取其结果需要一些特殊处理并不足为怪。例如，假定已知某个过程返回两个结果集，则在使用方法 execute 执行该过程后，必须调用方法 getResultSet 获得第一个结果集，然后调用适当的getXXX 方法获取其中的值。要获得第二个结果集，需要先调用 getMoreResults方法，然后再调用 getResultSet 方法。如果已知某个过程返回两个更新计数，则首先调用方法 getUpdateCount，然后调用 getMoreResults，并再次调用
     *
     * @param jdbcOperations jdbc操作类
     * @param sqlText        sql
     * @param params         参数
     * @return 执行结果
     * @throws Exception 异常
     */
    public static boolean execute(SoberSupport jdbcOperations, String sqlText, Object[] params) throws Exception {
        if (sqlText == null || sqlText.isEmpty()) {
            return false;
        }
        Connection conn = null;
        jdbcOperations.debugPrint(sqlText);
        SoberFactory soberFactory = jdbcOperations.getSoberFactory();
        Dialect dialect = soberFactory.getDialect();
        //oracle 创建促发器的一个bug
        if ((dialect instanceof OracleDialect) && sqlText.toLowerCase().contains(" trigger ") && ObjectUtil.isEmpty(params)) {
            Statement statement = null;
            try {
                conn = jdbcOperations.getConnection(SoberEnv.READ_WRITE);
                boolean autoCommit = conn.getAutoCommit();
                statement = conn.createStatement();
                statement.execute(sqlText);
                if (!autoCommit) {
                    //如果初始化在实务中，这里不一定会创建
                    conn.commit();
                    conn.setAutoCommit(autoCommit);
                }
                return true;
            } catch (Exception e) {
                log.error("SQL:" + sqlText, e);
                throw e;
            } finally {
                closeStatement(statement);
                closeConnection(conn);
            }
        } else {
            PreparedStatement statement = null;
            try {

                conn = jdbcOperations.getConnection(SoberEnv.READ_WRITE);
                boolean autoCommit = conn.getAutoCommit();
                statement = conn.prepareStatement(sqlText);
                if (!ObjectUtil.isEmpty(params)) {
                    for (int i = 0; i < params.length; i++) {
                        jdbcOperations.debugPrint("prepared[" + (i + 1) + "]=" + params[i]);
                        dialect.setPreparedStatementValue(statement, i + 1, params[i]);
                    }
                }
                //execute 返回很特殊,不代表是否执行成功
                statement.execute();
                if (!autoCommit) {
                    //如果初始化在实务中，这里不一定会创建
                    conn.commit();
                    conn.setAutoCommit(autoCommit);
                }
                return true;
            } catch (Exception e) {
                log.error("SQL:" + sqlText, e);
                throw e;
            } finally {
                closeStatement(statement);
                closeConnection(conn);
            }
        }
    }


    /**
     * 设置参数
     *
     * @param jdbcOperations jdbc操作类
     * @param dialect        sql适配器
     * @param statement      jdbc查询器
     * @param fields         查询字段
     * @param object         查询对象
     * @throws Exception 其他错误
     */
    public static void setPreparedStatementValueList(SoberSupport jdbcOperations, Dialect dialect, PreparedStatement statement, String[] fields, Object object) throws Exception {
        for (int i = 0; i < fields.length; i++) {
            Object value = BeanUtil.getProperty(object, fields[i]);
            jdbcOperations.debugPrint("SetPrepared[" + (i + 1) + "]=" + value);
            dialect.setPreparedStatementValue(statement, i + 1, value);
        }
    }

    /**
     * @param jdbcOperations jdbc操作类
     * @param dialect        sql适配器
     * @param sqlText        sql
     * @param param          参数
     * @return 返回动态封装的对象列表
     */
    public static List<?> prepareQuery(SoberSupport jdbcOperations, Dialect dialect, String sqlText, Object[] param) {
        List<Object> result = new ArrayList<>();
        Connection conn = null;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_ONLY);
            jdbcOperations.debugPrint(sqlText);
            statement = conn.prepareCall(sqlText);
            if (!ArrayUtil.isEmpty(param)) {
                for (int i = 0; i < param.length; i++) {
                    jdbcOperations.debugPrint("prepared[" + (i + 1) + "]=" + param[i]);
                    dialect.setPreparedStatementValue(statement, i + 1, param[i]);
                }
            }
            setFetchSize(statement, 500);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                //名称位_转换位驼峰命名方式
                Map<String, Object> beanMap = new HashMap<>();
                for (int n = 1; n <= metaData.getColumnCount(); n++) {
                    String field = StringUtil.underlineToCamel(metaData.getColumnLabel(n));
                    if (!beanMap.containsKey(field))
                    {
                        beanMap.put(field, dialect.getResultSetValue(resultSet, n));
                    }
                }
                result.add(ReflectUtil.createDynamicBean(beanMap));
            }
        } catch (Exception e) {
            log.error("检查 SQL:" + sqlText, e);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            closeConnection(conn);
        }
        return result;
    }

    /**
     * 更新一个存储过程
     *
     * @param jdbcOperations jdbc操作类
     * @param dialect        sql适配器
     * @param sqlText        sql
     * @param param          参数
     * @return update 返回， jdbc
     */
    public static int prepareUpdate(SoberSupport jdbcOperations, Dialect dialect, String sqlText, Object[] param) {
        Connection conn = null;
        CallableStatement statement = null;
        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_WRITE);
            statement = conn.prepareCall(sqlText);
            for (int i = 0; i < param.length; i++) {
                jdbcOperations.debugPrint("prepared[" + (i + 1) + "]=" + param[i]);
                dialect.setPreparedStatementValue(statement, i + 1, param[i]);
            }
            return statement.executeUpdate();
        } catch (Exception e) {
            log.error("ERROR SQL:" + sqlText, e);
            return -2;
        } finally {
            closeStatement(statement);
            closeConnection(conn);
        }
    }

    /**
     * @param jdbcOperations jdbc操作类
     * @param sqlText        sql
     * @param param          参数
     * @return 执行是否成功
     * @throws Exception 异常
     */
    public static boolean prepareExecute(SoberSupport jdbcOperations, String sqlText, Object[] param) throws Exception {
        Connection conn = null;
        CallableStatement statement = null;
        Dialect dialect = jdbcOperations.getSoberFactory().getDialect();
        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_WRITE);
            statement = conn.prepareCall(sqlText);
            jdbcOperations.debugPrint(sqlText);
            for (int i = 0; i < param.length; i++) {
                jdbcOperations.debugPrint("prepared[" + (i + 1) + "]=" + param[i]);
                dialect.setPreparedStatementValue(statement, i + 1, param[i]);
            }
            return statement.execute();
        } catch (Exception e) {
            log.error("ERROR SQL:" + sqlText, e);
            throw e;
        } finally {
            closeStatement(statement);
            closeConnection(conn);
        }
    }


    /**
     * @param jdbcOperations jdbc操作类
     * @param groupCode      字典表分组
     * @param namespace      字典表命名空间
     * @return 字典表
     */
    public static List<OptionBundle> getOptionBundleList(SoberSupport jdbcOperations, String groupCode, String namespace) {
        if (StringUtil.isNull(groupCode) && StringUtil.isNull(namespace)) {
            return null;
        }
        Criteria criteria = jdbcOperations.createCriteria(OptionBundle.class);
        if (!StringUtil.isNull(groupCode)) {
            criteria = criteria.add(Expression.eq("groupCode", groupCode));
        }
        if (!StringUtil.isNull(namespace)) {
            criteria = criteria.add(Expression.eq("namespace", namespace));
        }
        return SSqlExpression.getSortOrder(criteria, Order.DEFAULT_SORT).setCurrentPage(1).setTotalCount(jdbcOperations.getMaxRows()).list(false);
    }


    /**
     * @param soberSupport jdbc操作类
     * @param tableName    表名称
     * @param name         字段名称
     * @return 查询数据库中配置的枚举
     */
    public static SoberFieldEnum getSoberFieldEnum(SoberSupport soberSupport, String tableName, String name) {
        if (StringUtil.isNull(tableName) || StringUtil.isNull(name)) {
            return null;
        }
        Criteria criteria = soberSupport.createCriteria(SoberFieldEnum.class);
        criteria = criteria.add(Expression.eq("tableName", tableName));
        criteria = criteria.add(Expression.eq("fieldName", name));
        String sortString = "id:D";
        return SSqlExpression.getSortOrder(criteria, sortString).objectUniqueResult(false);
    }


    /**
     * 判断是否有配置的枚举
     *
     * @param soberSupport jdbc操作类
     * @param columnList   配置列表
     */
    public static void isConfFieldEnum(SoberSupport soberSupport, List<SoberColumn> columnList) {
        if (ObjectUtil.isEmpty(columnList)) {
            return;
        }
        List<SoberFieldEnum> list = soberSupport.createCriteria(SoberFieldEnum.class).setCurrentPage(1).setTotalCount(soberSupport.getMaxRows()).list(false);
        if (!ObjectUtil.isEmpty(list)) {
            for (SoberColumn column : columnList) {
                for (SoberFieldEnum soberFieldEnum : list) {
                    if (column.getTableName().equalsIgnoreCase(soberFieldEnum.getTableName()) && column.getName().equalsIgnoreCase(soberFieldEnum.getFieldName())) {
                        column.setConfEnum(true);
                        break;
                    }
                }
            }

        }
    }

    /**
     *
     * @param soberSupport 数据操作
     * @param tableNamePattern 表匹配
     * @return 得到所有表明
     */
    public static List<String> getTables(SoberSupport soberSupport,   String tableNamePattern) {
        return getTables(soberSupport,null,null,tableNamePattern);
    }
    /**
     *
     * @param soberSupport 数据操作
     * @param catalog 库名，可以空
     * @param schemaPattern public
     * @param tableNamePattern 表匹配
     * @return 得到所有表明
     */
    public static List<String> getTables(SoberSupport soberSupport, String catalog, String schemaPattern,  String tableNamePattern) {
        if (StringUtil.isNull(tableNamePattern))
        {
            tableNamePattern = "%";
        }
        List<String> result = new ArrayList<>();
        Connection conn = null;
        try {
            conn = soberSupport.getConnection(SoberEnv.READ_ONLY);
            DatabaseMetaData meta = conn.getMetaData();
            // 获取所有表的结果集
            ResultSet tables = meta.getTables(catalog, schemaPattern, tableNamePattern, new String[] {"TABLE"});
            while (tables.next()) {
                // 打印表名
                result.add(tables.getString("TABLE_NAME"));
            }
        } catch (Exception e) {
            log.error("getTables schemaPattern={}", schemaPattern, e);

        } finally {
            closeConnection(conn);
        }
        return result;
    }
}