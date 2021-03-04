/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.config;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.cache.DefaultCache;
import com.github.jspxnet.datasource.JRWPoolDataSource;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sober.SoberEnv;
import com.github.jspxnet.sober.SoberFactory;
import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.dialect.Dialect;
import com.github.jspxnet.sober.dialect.DialectFactory;
import com.github.jspxnet.sober.transaction.AbstractTransaction;
import com.github.jspxnet.sober.transaction.JDBCTransaction;
import com.github.jspxnet.sober.transaction.JTATransaction;
import com.github.jspxnet.sober.transaction.TransactionManager;
import com.github.jspxnet.sober.util.JdbcUtil;
import com.github.jspxnet.sober.util.SoberUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.UserTransaction;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-12
 * Time: 9:40:31
 * com.github.jspxnet.sober.config.SoberMappingBean
 * 如果使用JTA事务调用,需要注意几点:
 * 1.使用XA数据源,一般数据库驱动里边有例如,postgresql 为 org.postgresql.xa.PGXADataSource
 * 2.数据源使用JNDI 数据源
 * 3.事务也使用JNDI 事务,userTransaction
 */

@Slf4j
public class SoberMappingBean implements SoberFactory {
    //事务管理器
    private static final TransactionManager TRANSACTION_MANAGER = TransactionManager.getInstance();
    //初始化表 用于比较是否重复
    private final static Map<Class<?>,SqlMapConfig> INIT_TABLE_MAP = new HashMap<>();
    //sql映射表
    private static final  Map<String, SQLRoom> SQL_MAP = new HashMap<>();
    //表结果映射
    private static final Map<Class<?>, TableModels> TABLE_MAP = new HashMap<>();
    //整合服务器JNDI接口
    private Context context = null;
    //一次最多查询行数，避免out memory 
    private int maxRows = 20000;
    //数据源
    private DataSource dataSource;
    //显示SQL
    private boolean showsql = true;
    //自动提交
    private boolean autoCommit = true;
    //事务级别
    private int transactionIsolation = Connection.TRANSACTION_NONE;
    //超时设置
    private int transactionTimeout = DateUtil.MINUTE*2; //保存大量数据可能会很长时间
    //JTA 事务调用
    private String userTransaction = null;
    //jdni数据源绑定
    private String dataSourceLoop = null;
    //Sioc数据库名
    private String databaseName;
    //保存的时候是否要验证
    private boolean valid = false;
    //判断是否为JTA方式事务
    private boolean jta = false;
    //事务线程池,满足多线程多事务高并发使用
    private boolean useCache = true;

    @Override
    public boolean isUseCache() {
        if (TRANSACTION_MANAGER.containsKey(Integer.toString(hashCode())))
        {
            return false;
        }
        else
        {
            return useCache;
        }
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    //载入cache
    private Class<?> cacheName = DefaultCache.class;

    @Override
    public String getCacheName() {
        return cacheName.getName();
    }

    public void setCacheName(String cacheName) {

        try {
            this.cacheName = ClassUtil.loadClass(cacheName);
            BeanFactory beanFactory = EnvFactory.getBeanFactory();
            Object o = beanFactory.getBean(this.cacheName.getName(), Environment.CACHE);
            if (o == null) {
                log.error("cacheName Cache " + cacheName + " not find");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("cacheName Cache 初始化错误", e);
        }

    }

    public SoberMappingBean() {
        try {
            context = new InitialContext();
        } catch (NamingException e) {
            log.error("JNDI 初始化错误", e);
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    @Override
    public boolean isJta() {
        return jta;
    }

    @Override
    public int getMaxRows() {
        return maxRows;
    }

    @Override
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    @Override
    public int getTransactionTimeout() {
        return transactionTimeout;
    }

    public void setTransactionTimeout(int transactionTimeout) {
        this.transactionTimeout = transactionTimeout;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public int getTransactionIsolation() {
        return transactionIsolation;
    }

    @Override
    public void setTransactionIsolation(int transactionIsolation) {
        this.transactionIsolation = transactionIsolation;
    }

    @Override
    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    @Override
    public boolean isShowsql() {
        return showsql;
    }

    public void setShowsql(boolean showsql) {
        this.showsql = showsql;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public Dialect getDialect() {
        return dialect;
    }

    private Dialect dialect = null;

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * JTA方式的事务接口,JNDI 方式得到到，必须在服务器里边先配置好。
     *
     * @param userTransaction 设置事务方式
     */
    public void setUserTransaction(String userTransaction) {
        this.userTransaction = userTransaction;
        try {
            UserTransaction userTransaction1 = (UserTransaction) context.lookup(userTransaction);
            jta = userTransaction1 != null;
        } catch (Exception ex) {
            log.info("DataSource Rollback is JTATransaction", ex);
            jta = false;
        }
        if (jta) {
            log.info("DataSource Rollback is JTATransaction");
        } else {
            log.info("DataSource Rollback is JDBCTransaction");
        }
    }

    /**
     * @param dataSourceLoop JNDI 设置数源，方便和服务器整合
     * @throws Exception 异常
     */
    public void setDataSource(String dataSourceLoop) throws Exception {
        this.dataSourceLoop = dataSourceLoop;
        checkConnection(getConnection(SoberEnv.READ_WRITE, SoberEnv.notTransaction));
    }

    /**
     * @param dataSource 普通数据源
     * @throws Exception 异常
     */
    public void setDataSource(DataSource dataSource) throws Exception {
        jta = false;
        this.dataSource = dataSource;
        checkConnection(getConnection(SoberEnv.READ_WRITE, SoberEnv.notTransaction));
    }

    /**
     * @param dataSource 直接使用XA数据源,一般要配合JTA事务
     * @throws Exception 异常
     */
    public void setDataSource(XADataSource dataSource) throws Exception {
        jta = true;
        this.dataSource = new XADataSourceProxy(dataSource);
        checkConnection(getConnection(SoberEnv.READ_WRITE, SoberEnv.notTransaction));
    }

    /**
     * @param soberSupport 创建一个事务，如果事务已经存在那么就返回已经存在的事务
     * @return 事务对象
     * @throws SQLException sql 异常
     */
    @Override
    public AbstractTransaction createTransaction(SoberSupport soberSupport) throws SQLException
    {
        String  transactionId = Integer.toString(soberSupport.getSoberFactory().hashCode())+ Thread.currentThread().getId();
        AbstractTransaction trans = TRANSACTION_MANAGER.get(transactionId);
        if (trans != null && !trans.isClosed()) {
            return trans;
        }
        Connection connection = getConnection(SoberEnv.READ_WRITE, transactionId);
        if (connection != null && transactionIsolation > Connection.TRANSACTION_NONE) {
            connection.setTransactionIsolation(transactionIsolation);
        }
        if (jta) {
            JTATransaction transaction = new JTATransaction(soberSupport);
            try {
                transaction.setUserTransaction((UserTransaction) context.lookup(userTransaction));
            } catch (NamingException e) {
                log.error("create transaction error:", e);
                e.printStackTrace();
            }
            transaction.setTransactionId(transactionId);

            transaction.setTimeout(transactionTimeout);
            transaction.setConnection(connection);
            TRANSACTION_MANAGER.add(transaction);
            return transaction;
        } else {
            JDBCTransaction transaction = new JDBCTransaction(soberSupport);
            transaction.setTransactionId(transactionId);
            transaction.setSupportsSavePoints(dialect.isSupportsSavePoints());

            transaction.setTimeout(transactionTimeout);
            transaction.setConnection(connection);
            TRANSACTION_MANAGER.add(transaction);
            return transaction;
        }

    }

    /**
     * 检测连接池配置是否正确,初始化使用，只运行一次
     *
     * @param conn 链接
     */
    private void checkConnection(Connection conn) throws Exception {
        log.info("Connection is " + conn);
        try {
            if (conn == null || conn.isClosed())
            {
                log.error("database dataSource not get JDBC Connection，数据库配置错误");
                throw new SQLException("database dataSource not get JDBC Connection,数据库配置错误");
            }
            if (StringUtil.isNull(databaseName) || Environment.auto.equalsIgnoreCase(databaseName)) {
                this.databaseName = JdbcUtil.getDatabaseName(conn);
                this.dialect = DialectFactory.createDialect(databaseName);
            } else {
                this.dialect = DialectFactory.createDialect(databaseName);
            }
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            this.dialect.setSupportsSavePoints(databaseMetaData.supportsSavepoints());
            this.dialect.setSupportsGetGeneratedKeys(databaseMetaData.supportsGetGeneratedKeys());
        } finally {
            closeConnection(conn, false);
        }
    }


    /**
     * @param type 读写分离 0 ReadWrite 1 ReadOnly 2 WriteOnly
     * @param tid  事务连接ID
     * @return 通过各种方式得到连接，包括XA连接
     * @throws SQLException sql异常
     */
    @Override
    public Connection getConnection(final int type,final String tid) throws SQLException
    {
        //默认方式加入当前事务连接
        if (!SoberEnv.notTransaction.equals(tid) && SoberEnv.THREAD_LOCAL!=type) {
            AbstractTransaction trans = TRANSACTION_MANAGER.get(tid);
            if (trans != null && !trans.isClosed() && trans.getConnection() != null) {
                 return trans.getConnection();
            }
        }
        return getConnection(type) ;
    }

    private Connection getConnection(int type) throws SQLException
    {
        //否则重新分配一个连接,新开始一个事务
        Connection conn = null;
        if (dataSource != null) {
            if (dataSource instanceof JRWPoolDataSource) {
                JRWPoolDataSource rwDataSource = (JRWPoolDataSource) dataSource;
                conn = rwDataSource.getConnection(type);
            } else {
                conn = dataSource.getConnection();
            }
        } else {
            Object xDataSource = null;
            try {
                xDataSource = context.lookup(dataSourceLoop);
                if (xDataSource instanceof XADataSource) {
                    XADataSource dataSource = (XADataSource) xDataSource;
                    conn = dataSource.getXAConnection().getConnection();
                } else if (xDataSource instanceof JRWPoolDataSource) {
                    JRWPoolDataSource rwDataSource = (JRWPoolDataSource) xDataSource;
                    conn = rwDataSource.getConnection(type);
                } else if (xDataSource instanceof DataSource) {
                    DataSource dataSource = (DataSource) xDataSource;
                    conn = dataSource.getConnection();
                } else {
                    throw new NamingException("JNDI dataSource Loop " + dataSourceLoop + " is NULL");
                }
            } catch (NamingException e) {
                e.printStackTrace();
                log.error("getConnection transaction error:", e);
            }
        }

        //判断事物是否超时
        if (!TRANSACTION_MANAGER.isEmpty()) {
            TRANSACTION_MANAGER.checkTransactionOvertime();
        }
        return conn;
    }


    /**
     * @param conn 关闭连接
     */
    @Override
    public void closeConnection(Connection conn, boolean release) {
        if (conn == null) {
            return;
        }
        JdbcUtil.closeConnection(conn, release);
    }

    /**
     * 同时这里将初始化并创建索引
     * @param namespace 得到命名空间的SQL
     * @return sql空间
     */
    @Override
    public SQLRoom getSqlRoom(String namespace)
    {
        return SQL_MAP.get(namespace.toLowerCase());
    }

    /**
     * 读取sql配置
     * @param strings 配置路径
     * @throws Exception 异常
     */
    @Override
    public void setMappingResources(String[] strings) throws Exception {
        if (!SQL_MAP.isEmpty()) {
            return;
        }
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        if (strings != null) {
            String[] files = new String[0];
            for (String file : strings) {
                if (file.contains(StringUtil.ASTERISK) || file.contains("?") || file.contains("#")) {
                    List<File> findFiles = FileUtil.getPatternFiles(envTemplate.getString(Environment.defaultPath), file);
                    if (findFiles==null&&!StringUtil.isNull(envTemplate.getString(Environment.sqlXmlPath)))
                    {
                        String sqlXmlPath = envTemplate.getString(Environment.sqlXmlPath);
                        String[] pathList = StringUtil.split(sqlXmlPath,StringUtil.SEMICOLON);
                        for (String path:pathList)
                        {
                            List<File> childFindFiles = FileUtil.getPatternFiles(path, file);
                            if (childFindFiles!=null)
                            {
                                findFiles.addAll(childFindFiles);
                            }
                        }
                    }
                    if (!ObjectUtil.isEmpty(findFiles))
                    {
                        for (File f : findFiles) {
                            if (f.isFile())
                            {
                                files = ArrayUtil.add(files, f.getName());
                            }
                        }
                    }
                } else {
                    files = ArrayUtil.add(files, file);
                }
            }
            String defaultPath = envTemplate.getString(Environment.defaultPath);
            List<String> fileList = new ArrayList<>();
            for (String file : files) {
                if (StringUtil.isNull(file)||fileList.contains(file)) {
                    continue;
                }
                String fileName = file;
                URL url = ClassUtil.getResource(file);
                if (url == null) {
                    url = ClassUtil.getResource(defaultPath + file);
                }
                if (url == null && (file.startsWith("http") || file.startsWith("file") || file.startsWith("ftp"))) {
                    url = new URL(file);
                }
                if (url == null)
                {
                    if (FileUtil.isFileExist(file)) {
                        fileName = file;
                    } else if (FileUtil.isFileExist(defaultPath + file)) {
                        fileName = defaultPath + file;
                    }
                } else {
                    fileName = url.getPath();
                }
                if (!FileUtil.isFileExist(fileName)) {
                    log.error("not find file :{}", file);
                    continue;
                }
                fileList.add(fileName);
                String xmlString = IoUtil.autoReadText(fileName,envTemplate.getString(Environment.encode, Environment.defaultEncode));
                xmlString = StringUtil.trim(xmlString);
                if (!StringUtil.isNull(xmlString))
                {
                    SoberUtil.readSqlMap(xmlString,SQL_MAP,INIT_TABLE_MAP);
                }
            }
            fileList.clear();
        }
    }

    /**
     * 得到表结构,如果数据库中不存在表，就创建表
     *
     * @param cla          类
     * @param soberSupport 支持对象
     * @return 得到表结构
     */
    @Override
    public TableModels getTableModels(Class<?> cla, final SoberSupport soberSupport) {
        if (!INIT_TABLE_MAP.isEmpty())
        {
            SoberUtil.initTable(new ArrayList<>(INIT_TABLE_MAP.values()),soberSupport);
            INIT_TABLE_MAP.clear();
        }
        TableModels soberTable = TABLE_MAP.get(cla);
        if (soberTable != null) {
            return soberTable;
        }
        soberTable = SoberUtil.createTableAndIndex(cla,null,soberSupport);
        if (soberTable!=null)
        {
            TABLE_MAP.put(cla, soberTable);
        } else
        {
            log.error("严重异常,必须尽快排除:{} 表创建失败",cla);
        }

        return soberTable;
    }



    @Override
    public void clear() {
        TABLE_MAP.clear();
    }


}