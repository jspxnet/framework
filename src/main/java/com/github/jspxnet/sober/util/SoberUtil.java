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
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.io.jar.ClassScannerUtils;
import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sioc.IocContext;
import com.github.jspxnet.sioc.config.ConfigureContext;
import com.github.jspxnet.sioc.util.TypeUtil;
import com.github.jspxnet.sober.*;
import com.github.jspxnet.sober.annotation.IDType;
import com.github.jspxnet.sober.annotation.SqlMap;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.config.*;
import com.github.jspxnet.sober.config.xml.*;
import com.github.jspxnet.sober.dao.SqlMapConfDAO;
import com.github.jspxnet.sober.dao.impl.SqlMapConfDAOImpl;
import com.github.jspxnet.sober.dialect.Dialect;
import com.github.jspxnet.sober.enums.DatabaseEnumType;
import com.github.jspxnet.sober.enums.ExecuteEnumType;
import com.github.jspxnet.sober.enums.QueryModelEnumType;
import com.github.jspxnet.sober.proxy.DefaultSqlMapInvocation;
import com.github.jspxnet.sober.proxy.InterceptorProxy;
import com.github.jspxnet.sober.table.SqlMapConf;
import com.github.jspxnet.sober.table.SqlMapInterceptorConf;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ChenYuan
 * date: 12-3-21
 * Time: 下午8:39
 * 换成规则
 * @author ChenYuan
 */
@Slf4j
public final class SoberUtil {

    final public static String CACHE_TREM_LIST = ":list:";
    final public static String CACHE_TREM_LOAD = ":load:";
    final public static String CACHE_TREM_CHILD = ":c:";
    final public static String CACHE_TREM_EQUALS = ":e:";

    private SoberUtil() {

    }



    /**
     * @param soberTable 表对象
     * @param fields     字段
     * @return 字段安全检测 true 表示继续运行，false 表示过滤这个字段条件，应为这个字段不存在
     */
    public static boolean containsField(TableModels soberTable, String[] fields) {
        if (fields == null || soberTable == null) {
            return false;
        }
        for (String field : fields) {
            if (!soberTable.containsField(field)) {
                log.debug(soberTable.getName() + "  " + soberTable.getCaption() + " field:" + field);
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param aClass 类对象
     * @param field 字段
     * @param find 查询
     * @param loadChild 载入子对象
     * @return 返回缓存key
     */
    public static String getLoadKey(Class<?> aClass, Serializable field, Object find, boolean loadChild) {
        return aClass.getName() + CACHE_TREM_LOAD +
                //满足redis 规范
                field + CACHE_TREM_EQUALS + find + CACHE_TREM_CHILD + loadChild;
    }

    public static String getLoadKey(Class<?> aClass, Serializable field, Object find) {
        return aClass.getName() + CACHE_TREM_LOAD +
                //满足redis 规范
                field + CACHE_TREM_EQUALS + find + CACHE_TREM_CHILD + ".*";
    }
    /**
     *
     * @param aClass 类对象
     * @param term 条件
     * @param sort 排序
     * @param begin  开始行
     * @param end  结束行
     * @param loadChild 载入子对象
     * @return 返回缓存key
     */
    public static String getListKey(Class<?> aClass, String term,String sort,int begin,int end, boolean loadChild) {
        //满足redis 规范
        String ck = term;
        if (ck!=null && ck.length()>2)
        {
            ck = ck.substring(0,2);
        }
        String sb = aClass.getName() + CACHE_TREM_LIST +
                ck + EncryptUtil.getMd5(term) + "_T_" + sort + CACHE_TREM_CHILD + "_L" + begin + "_" + end + loadChild;
        return StringUtil.replace(sb," ","");
    }

    public static String toTypeString(Object obj) {
        if (obj == null) {
            return StringUtil.empty;
        }
        if (obj instanceof String) {
            return StringUtil.quote(obj.toString(), false);
        }
        return obj.toString();
    }


    /**
     * @param resultSetMetaData jdbc表头对象
     * @param dialect           sql翻译器
     * @param resultSet         返回数据
     * @return 行数据
     * @throws SQLException 异常
     */
    public static Map<String, Object> getHashMap(ResultSetMetaData resultSetMetaData, Dialect dialect, ResultSet resultSet) throws SQLException {
        int numColumns = resultSetMetaData.getColumnCount();
        Map<String, Object> resultMap = new HashMap<>(numColumns);
        for (int c = 1; c <= numColumns; c++) {
            String colName = resultSetMetaData.getColumnLabel(c);
            resultMap.put(StringUtil.underlineToCamel(colName), dialect.getResultSetValue(resultSet, c));
        }
        return resultMap;
    }

    public static DataMap<String, Object> getDataHashMap(ResultSetMetaData resultSetMetaData, Dialect dialect, ResultSet resultSet) throws SQLException {
        int numColumns = resultSetMetaData.getColumnCount();
        DataMap<String, Object> resultMap = new DataMap<>();
        for (int c = 1; c <= numColumns; c++) {
            String colName = resultSetMetaData.getColumnLabel(c);
            resultMap.put(StringUtil.underlineToCamel(colName), dialect.getResultSetValue(resultSet, c));
        }
        return resultMap;
    }

    /**
     * 读取xml sql
     * @param xmlString xml配置
     * @param allSqlMap 总配置表
     * @param tableListMap 初始化表
     * @throws Exception 异常
     */
    public static void readSqlMap(String xmlString,final Map<String, SQLRoom> allSqlMap,Map<Class<?>,SqlMapConf> tableListMap) throws Exception
    {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(SqlMapXml.TAG_NAME, SqlMapXml.class.getName());
        List<TagNode> list = xmlEngine.getTagNodes(xmlString);
        for (TagNode node : list) {
            if (node==null)
            {
                continue;
            }
            SqlMapXml sqlMapXmlEl = (SqlMapXml) node;
            //读取数据
            readSqlRoom(sqlMapXmlEl.getBody(),sqlMapXmlEl,allSqlMap,tableListMap);
            //处理include标签
        }
    }



    /**
     * 读取放入SqlRoom
     * @param xmlString xml配置
     * @param sqlMapXmlEl 命名空间 和 数据是否载入
     * @param allSqlMap 总配置表
     * @param tableListMap 初始化表
     * @throws Exception 异常
     */
    public static void readSqlRoom(String xmlString,SqlMapXml sqlMapXmlEl ,final Map<String, SQLRoom> allSqlMap,Map<Class<?>,SqlMapConf> tableListMap) throws Exception {

        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(TableXml.TAG_NAME, TableXml.class.getName());
        xmlEngine.putTag(ExecuteXml.TAG_NAME, ExecuteXml.class.getName());
        xmlEngine.putTag(QueryXml.TAG_NAME, QueryXml.class.getName());
        xmlEngine.putTag(UpdateXml.TAG_NAME, UpdateXml.class.getName());
        xmlEngine.putTag(SqlXml.TAG_NAME, SqlXml.class.getName());
        xmlEngine.putTag(InterceptorXml.TAG_NAME, SqlXml.class.getName());


        String namespace = sqlMapXmlEl.getNamespace();
        if (namespace!=null)
        {
            namespace = namespace.toLowerCase();
        }

        List<TagNode> list = xmlEngine.getTagNodes(xmlString);
        SQLRoom sqlRoom = allSqlMap.get(namespace);
        if (sqlRoom == null) {
            sqlRoom = new SQLRoom();
            sqlRoom.setNamespace(namespace);
            sqlRoom.setDatabase(sqlMapXmlEl.getDatabase());
            allSqlMap.put(namespace, sqlRoom);
        }

        for (TagNode node : list) {
            BaseXmlTagNode beanEl = (BaseXmlTagNode) node;
            SqlMapConf config  = new SqlMapConf();
            config.setName(beanEl.getId());
            config.setDatabaseType(beanEl.getDatabase());
            config.setResultType(beanEl.getResultType());
            config.setContext(StringUtil.trim(XMLUtil.xmlCdataDecrypt(beanEl.getBody())));
            config.setQuote(beanEl.getQuote());
            config.setCaption(beanEl.getCaption());
            if (TableXml.TAG_NAME.equalsIgnoreCase(node.getTagName()))
            {
                config.setIndex(beanEl.getIndex());
                if (StringUtil.isEmpty(config.getContext()))
                {
                    config.setContext(beanEl.getResultType());
                }
                addTable(tableListMap,config,namespace);
            }
            if (InterceptorXml.TAG_NAME.equalsIgnoreCase(node.getTagName()))
            {
                SqlMapInterceptorConf sqlMapInterceptorConf = new SqlMapInterceptorConf();
                sqlMapInterceptorConf.setName(beanEl.getId());
                sqlMapInterceptorConf.setCaption(beanEl.getCaption());
                sqlMapInterceptorConf.setTerm(beanEl.getTerm());
                sqlMapInterceptorConf.setEnable(YesNoEnumType.YES.getValue());
                sqlMapInterceptorConf.setNamespace(namespace);
                sqlRoom.addInterceptor(sqlMapInterceptorConf);
            }
            if (ExecuteXml.TAG_NAME.equalsIgnoreCase(node.getTagName()))
            {
                config.setExecuteType(ExecuteEnumType.EXECUTE.getValue());
                sqlRoom.addExecute(config);
            } else
            if (QueryXml.TAG_NAME.equalsIgnoreCase(node.getTagName()))
            {
                config.setExecuteType(ExecuteEnumType.QUERY.getValue());
                sqlRoom.addQuery(config);
            } else
            if (UpdateXml.TAG_NAME.equalsIgnoreCase(node.getTagName()))
            {
                config.setExecuteType(ExecuteEnumType.UPDATE.getValue());
                sqlRoom.addUpdate(config);
            }else
            if (SqlXml.TAG_NAME.equalsIgnoreCase(node.getTagName())&&beanEl.getId()!=null)
            {
               sqlRoom.addInclude(beanEl.getId(),StringUtil.trim(beanEl.getBody()));
            }
        }
    }


    /**
     *  单独放在这里主要是方便提示错误
     * @param tableListMap 表配置映射
     * @param sqlMapConf  配置
     * @param namespace 命名空间
     */
    static public void addTable(Map<Class<?>,SqlMapConf> tableListMap,SqlMapConf sqlMapConf,String namespace)
    {
        if (StringUtil.isEmpty(sqlMapConf.getContext()))
        {
            return;
        }

        Class<?> cls;
        try {
            cls = ClassUtil.loadClass(sqlMapConf.getContext());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.error("检查sqlMap配置文件:" + namespace + "中初始化配置init中的table对象不能找到:{},错误:{}",sqlMapConf.getContext(),e.getMessage());
            return;
        }
        Table table = AnnotationUtil.getTable(cls);
        if (table==null || !table.create())
        {
            log.info( "检查sqlMap配置文件:" + namespace + "中初始化配置init中的:{},它不是一个合规的数据库表对象必须开启@Table创建属性",cls.getName());
            return;
        }
        if (tableListMap.containsKey(cls))
        {
            return;
        }
        tableListMap.put(cls,sqlMapConf);
    }

    /**
     *
     * @param tableList 初始化表列表
     * @param soberSupport 数据对象
     */
    public static void initTable(List<SqlMapConf> tableList, SoberSupport soberSupport)
    {
        if (!ObjectUtil.isEmpty(tableList))
        {
            for (SqlMapConf sqlMapConf:tableList)
            {
                if (StringUtil.isEmpty(sqlMapConf.getContext()))
                {
                    continue;
                }
                Class<?> cls;
                try {
                    cls = ClassUtil.loadClass(sqlMapConf.getContext());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    log.error("检查sqlMap配置,中初始化配置init中的table对象不能找到:{},错误:{}",sqlMapConf.getContext(),e.getMessage());
                    continue;
                }
                Table table = AnnotationUtil.getTable(cls);
                if (table==null || !table.create())
                {
                    log.info( "检查sqlMap配置,中初始化配置init中的:{},它不是一个合规的数据库表对象必须开启@Table创建属性",cls.getName());
                    continue;
                }
                createTableAndIndex(cls,sqlMapConf,soberSupport);

            }
        }
    }

    /**
     * 创建索引
     * @param cls 类对象
     * @param sqlMapConf 索引配置
     * @param soberSupport 数据连接
     */
    public static void createTableIndex(Class<?> cls,SqlMapConf sqlMapConf, SoberSupport soberSupport)
    {
        //创建索引
        TableModels tableModels = AnnotationUtil.getSoberTable(cls);
        if (tableModels==null)
        {
            return;
        }
        String indexList = sqlMapConf.getIndex();
        String[] indexLine = StringUtil.split(indexList,StringUtil.SEMICOLON);
        for (String line:indexLine)
        {
            if (StringUtil.isEmpty(line))
            {
                continue;
            }
            String name;
            String field;
            if (line.contains("(")&&line.contains(")"))
            {
                name = StringUtil.substringBefore(line,"(");
                if (tableModels.containsField(name))
                {
                    //postgresql 不能直接是用字段名称,不然会相互影响冲突,需要带上表明
                    name = StringUtil.camelToUnderline(tableModels.getName(),true) + "_" + name +"_idx";
                }
                field = StringUtil.substringBetween(line,"(",")");
            } else
            {
                name = StringUtil.camelToUnderline(tableModels.getName(),true)+ "_" + line +"_idx";
                field = line;
            }

            if (name==null&&field==null)
            {
                continue;
            }


            if (DatabaseEnumType.find(soberSupport.getSoberFactory().getDatabaseType()).equals(DatabaseEnumType.MSSQL))
            {
                String[] fields = StringUtil.split(field,StringUtil.COMMAS);
                if (ArrayUtil.isEmpty(fields))
                {
                    continue;
                }

                for (int i=0;i<fields.length;i++)
                {
                    if (fields[i].toLowerCase().contains("date"))
                    {
                        fields[i] = "["+fields[i]+"] DESC";
                    } else
                    {
                        fields[i] = "["+fields[i]+"] ASC";
                    }
                }
                field = ArrayUtil.toString(fields,StringUtil.COMMAS);
            }

            try {
                soberSupport.createIndex(tableModels.getDatabaseName(),tableModels.getName(),name,field);
            } catch (Exception e) {
                e.printStackTrace();
                log.info( "检查sqlMap配置,中初始化配置init中索引配置错误:table={},index={},创建索引异常",tableModels.getName(),line);
            }
        }
    }


    /**
     * 创建表结构
     * @param cla 类对象
     * @param sqlMapConf 配置
     * @param soberSupport 数据对象
     * @return 表模型对象
     */
    public static TableModels createTableAndIndex(Class<?> cla,SqlMapConf sqlMapConf, SoberSupport soberSupport)
    {
        //&& !soberSupport.tableExists(cla)
        SoberTable soberTable = AnnotationUtil.getSoberTable(cla);
        if (soberTable==null)
        {
            //DTO 对象
            return null;
        }
        if (StringUtil.isEmpty(soberTable.getDatabaseName()))
        {
            Connection connection = null;
            try {
                connection = soberSupport.getSoberFactory().getConnection(SoberEnv.READ_ONLY,SoberEnv.NOT_TRANSACTION);
                String jdbcDbName = StringUtil.getJdbcUrlDataBaseName(connection.getMetaData().getURL());
                soberTable.setDatabaseName(jdbcDbName);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                JdbcUtil.closeConnection(connection);
            }
        }
        DatabaseEnumType[] oracleDatabaseEnumType = new DatabaseEnumType[]{DatabaseEnumType.ORACLE,DatabaseEnumType.DB2,DatabaseEnumType.DM,DatabaseEnumType.MSSQL};

        String sql = null;
        try {
            if (soberTable.isCreate()&& !soberSupport.tableExists(soberTable)) {

                sql = soberSupport.getCreateTableSql(cla,soberTable);
                //oracle只能一个; 一个; 的执行
                if (DatabaseEnumType.inArray(oracleDatabaseEnumType,soberSupport.getSoberFactory().getDatabaseType()))
                {
                    String[] sqlLines = StringUtil.split(sql,StringUtil.SEMICOLON + StringUtil.CRLF);
                    for (String sqlLine:sqlLines)
                    {
                        if (StringUtil.isNull(sqlLine))
                        {
                            continue;
                        }
                        soberSupport.execute(sqlLine);
                    }
                    //创建数据库序列
                    if (DatabaseEnumType.inArray(new DatabaseEnumType[]{DatabaseEnumType.ORACLE,DatabaseEnumType.DM},soberSupport.getSoberFactory().getDatabaseType())&&
                            IDType.serial.equalsIgnoreCase(soberTable.getIdType()))
                    {
                        //oracle 和 dm数据需要创新序列
                        Map<String, Object> valueMap = new HashMap<>();
                        valueMap.put(Dialect.KEY_DATABASE_NAME, soberTable.getDatabaseName());
                        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
                        valueMap.put(Dialect.KEY_PRIMARY_KEY, soberTable.getPrimary());

                        String haveSeqSql = soberSupport.getSoberFactory().getDialect().processTemplate(Dialect.ORACLE_HAVE_SEQ,valueMap);
                        Object obj = soberSupport.getUniqueResult(haveSeqSql);
                        if (ObjectUtil.toInt(obj)<=0)
                        {
                            String seqSql = soberSupport.getSoberFactory().getDialect().processTemplate(Dialect.ORACLE_CREATE_SEQUENCE,valueMap);
                            soberSupport.execute(seqSql);
                        }

                        String tigSql = soberSupport.getSoberFactory().getDialect().processTemplate(Dialect.ORACLE_CREATE_SEQ_TIGGER,valueMap);
                        log.info("sql:\r\n{}",tigSql);
                        soberSupport.execute(tigSql);
                    }
                }
                else
                {
                    //其他数据库可以一次执行
                    soberSupport.execute(sql);
                }
                if (sqlMapConf!=null)
                {
                    createTableIndex(cla, sqlMapConf,  soberSupport);
                }
            }
        } catch (Exception e) {
            log.error("ERROR:auto create table 自动创建表错误:" + sql + " table:" + soberTable.getName() + " 原因:没有得到连接或者和数据库不兼容", e);
            e.printStackTrace();
            return null;
        }
        return soberTable;
    }

    /**
     * 带拦截器的 sqlMap执行
     * @param soberSupport  数据库支持
     * @param arg 参数
     * @param sqlMap sqlMap配置
     * @param exeMethod  执行方法
     * @return 返回查询接口
     * @throws Exception 异常
     */
    public static Object invokeSqlMapInvocation(SoberSupport soberSupport, Object[] arg, SqlMapConf sqlMap, Method exeMethod) throws Exception {
        DefaultSqlMapInvocation invocation = new DefaultSqlMapInvocation(new InterceptorProxy(soberSupport,arg,sqlMap,exeMethod));
        return invocation.invoke();
    }

    /**
     *
     * @param soberSupport 数据库支持
     * @param sqlMap sqlMap配置
     * @param valueMap 查询参数
     * @return 返回查询接口
     * @throws Exception 异常
     */
    public static Object invokeSqlMapInvocation(SoberSupport soberSupport, SqlMapConf sqlMap,Map<String, Object> valueMap) throws Exception {
        DefaultSqlMapInvocation invocation = new DefaultSqlMapInvocation(new InterceptorProxy(soberSupport,sqlMap,valueMap));
        return invocation.invoke();
    }


    /**
     *
     * @param soberFactory 数据库对象
     * @param namespace 命名空间
     * @param exeName  sql名称
     * @param executeEnumType 查询方式
     * @param sqlMap sqlmap 配置
     * @return 返回sqlmap
     * @throws Exception 异常
     */
    public static SqlMapConf getSqlMapConf(SoberFactory soberFactory, String namespace, String exeName, ExecuteEnumType executeEnumType, SqlMap sqlMap) throws Exception {
        //做个缓存提高查询速度

       String intercept = StringUtil.empty;
       if (sqlMap!=null)
       {
           intercept = ObjectUtil.toString(sqlMap.intercept()) + "_" +sqlMap.mode().getValue();
       }
        //@param sqlMapConfDAO 数据库配置
        String hash = EncryptUtil.getMd5(namespace + "_" + exeName + "_" + intercept);
        String cacheKey = SoberUtil.getLoadKey(SqlMapConf.class,"id",hash + executeEnumType.getValue(),false);
        SqlMapConf sqlMapConf = (SqlMapConf)JSCacheManager.get(SqlMapConf.class,cacheKey);
       if (sqlMapConf!=null)
        {
            return sqlMapConf;
        }

        SQLRoom sqlRoom = soberFactory.getSqlRoom(namespace);
        if (ExecuteEnumType.UPDATE.equals(executeEnumType))
        {
            sqlMapConf = sqlRoom.getUpdateMapSql(exeName,soberFactory.getDatabaseType());
        } else
        if (ExecuteEnumType.EXECUTE.equals(executeEnumType))
        {
            sqlMapConf = sqlRoom.getUpdateMapSql(exeName,soberFactory.getDatabaseType());
        } else
        {
            sqlMapConf = sqlRoom.getQueryMapSql(exeName,soberFactory.getDatabaseType());
        }
        //数据库里边查询得到配置begin
        SqlMapConfDAO sqlMapConfDAO = null;
        if (sqlRoom.isDatabase()&&sqlMapConf == null) {
            BeanFactory beanFactory = EnvFactory.getBeanFactory();
            sqlMapConfDAO = beanFactory.getBean(SqlMapConfDAO.class);
            if (sqlMapConfDAO==null)
            {
                log.error("SqlMapConfDAO 没有注册到ioc 不能是用db sqlMap功能:{}", SqlMapConfDAOImpl.class.getName());
            } else {
                sqlMapConf = sqlMapConfDAO.getSqlMap(namespace,exeName);
            }
        }
        if (sqlMapConf == null) {
            log.error("ERROR SQL map not config SQL id:{},namespace:{}",exeName,namespace);
            throw new Exception("ERROR SQL map not config SQL:"+exeName+",namespace:"+namespace);
        }
        if (StringUtil.isNull(sqlMapConf.getNamespace()))
        {
            sqlMapConf.setNamespace(namespace);
        }
        if (sqlMap!=null)
        {
            if (QueryModelEnumType.COUNT.getValue()==sqlMap.mode().getValue())
            {
                sqlMapConf = BeanUtil.copy(sqlMapConf,SqlMapConf.class);
            }
            sqlMapConf.setQueryModel(sqlMap.mode().getValue());
            sqlMapConf.setInterceptor(ArrayUtil.toString(sqlMap.intercept(),StringUtil.SEMICOLON));
            sqlMapConf.setReplenished(false);
            sqlMapConf.setInterceptorConfList(null);
        }
        if (!sqlMapConf.isReplenished())
        {
            sqlMapConf.setContext(sqlRoom.getReplenish(sqlMapConf.getContext()));
            LinkedList<SqlMapInterceptorConf> interceptorConfList = getInterceptorList( soberFactory,  sqlMapConf,  sqlMapConfDAO);
            sqlMapConf.setInterceptorConfList(interceptorConfList);
            sqlMapConf.setReplenished(true);
        }

        //数据库里边查询得到配置end
        JSCacheManager.put(SqlMapConf.class,cacheKey,sqlMapConf);
        return sqlMapConf;
    }

    /**
     *
     * @param soberFactory  数据库对象
     * @param namespace 命名空间
     * @param exeName sql名称
     * @return  配置的sqlMap
     * @throws Exception 异常
     */
    public static SqlMapConf getSqlMapConf(SoberFactory soberFactory, String namespace, String exeName) throws Exception {
        //做个缓存提高查询速度

        //@param sqlMapConfDAO 数据库配置
        String hash = EncryptUtil.getMd5(namespace + "_" + exeName + "_DB");
        String cacheKey = SoberUtil.getLoadKey(SqlMapConf.class,"id",hash,false);
        SqlMapConf sqlMapConf = (SqlMapConf)JSCacheManager.get(SqlMapConf.class,cacheKey);
        if (sqlMapConf!=null)
        {
            return sqlMapConf;
        }

        //数据库里边查询得到配置begin
        SqlMapConfDAO sqlMapConfDAO = null;
        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        sqlMapConfDAO = beanFactory.getBean(SqlMapConfDAO.class);
        if (sqlMapConfDAO==null)
        {
            log.error("SqlMapConfDAO 没有注册到ioc 不能是用db sqlMap功能:{}", SqlMapConfDAOImpl.class.getName());
        } else {
            sqlMapConf = sqlMapConfDAO.getSqlMap(namespace,exeName);
        }
        if (sqlMapConf == null) {
            log.error("ERROR SQL map not config SQL id:{},namespace:{}",exeName,namespace);
            throw new Exception("ERROR SQL map not config SQL:"+exeName+",namespace:"+namespace);
        }
        if (StringUtil.isNull(sqlMapConf.getNamespace()))
        {
            sqlMapConf.setNamespace(namespace);
        }

        if (!sqlMapConf.isReplenished())
        {
            LinkedList<SqlMapInterceptorConf> interceptorConfList = getInterceptorList( soberFactory,  sqlMapConf,  sqlMapConfDAO);
            sqlMapConf.setInterceptorConfList(interceptorConfList);
            sqlMapConf.setReplenished(true);
        }
        //数据库里边查询得到配置end
        JSCacheManager.put(SqlMapConf.class,cacheKey,sqlMapConf);
        return sqlMapConf;
    }

    /**
     * 构建一个sqlmap对应的配置总表
     * @param soberFactory 数据库对象
     * @param mapSql sqlmap配置
     * @param sqlMapConfDAO 配置DAO
     * @return 得到需要执行的拦截器列表
     */
    public static LinkedList<SqlMapInterceptorConf> getInterceptorList(SoberFactory soberFactory, SqlMapConf mapSql, SqlMapConfDAO sqlMapConfDAO) {
        if (mapSql==null)
        {
            return null;
        }
        if (mapSql.isReplenished())
        {
            return mapSql.getInterceptorConfList();
        }

        String exeName = mapSql.getName();
        LinkedList<SqlMapInterceptorConf> allConfList = new LinkedList<>();
        SQLRoom sqlRoom = soberFactory.getSqlRoom(mapSql.getNamespace());
        if (sqlRoom==null)
        {
            return null;
        }

        LinkedList<SqlMapInterceptorConf> interceptorList = sqlRoom.getInterceptorList();
        if (!ObjectUtil.isEmpty(interceptorList))
        {
            for (SqlMapInterceptorConf conf:interceptorList)
            {
                if (exeName.equalsIgnoreCase(conf.getTerm())||StringUtil.getPatternFind(exeName,conf.getTerm()))
                {
                    allConfList.addLast(conf);
                }
            }
        }
        if (sqlMapConfDAO!=null)
        {
            List<SqlMapInterceptorConf> list = sqlMapConfDAO.getInterceptorMap(mapSql.getNamespace());
            if (!ObjectUtil.isEmpty(list))
            {
                for (SqlMapInterceptorConf conf:list)
                {
                    if (exeName.equalsIgnoreCase(conf.getTerm())||StringUtil.getPatternFind(exeName,conf.getTerm()))
                    {
                        allConfList.addLast(conf);
                    }
                }
            }
        }

        //单独的sqlmap上配置的拦截器
        String[] classNames = mapSql.getInterceptorArray();
        if (!ObjectUtil.isEmpty(classNames))
        {
            for (String className:classNames)
            {
                SqlMapInterceptorConf conf = new SqlMapInterceptorConf();
                conf.setName(className);
                conf.setNamespace(mapSql.getNamespace());
                conf.setCaption(mapSql.getCaption() + "Annotation");
                conf.setTerm(mapSql.getName());
                conf.setSortType(allConfList.size()+1);
                allConfList.addLast(conf);
            }
        }
         return allConfList;
    }

    /**
     *
     * @param resultType 配置的类型字符串
     * @param defClass 默认类型
     * @return 得到返回类型
     */
    public static Class<?> getResultClass(String resultType,Class<?> defClass) {
        if (StringUtil.isNull(resultType)&&defClass!=null &&
                !ClassUtil.isCollection(defClass)
                && !Object.class.getName().equals(defClass.getName())
                && !defClass.getSimpleName().equals("T")&& !defClass.isArray()
                && !defClass.isAnnotation() && !defClass.isInterface()
        )
        {
            return defClass;
        }
        if ("map".equalsIgnoreCase(resultType))
        {
            return DataMap.class;
        }
        if (resultType==null)
        {
            return null;
        }
        return (Class<?>) TypeUtil.getJavaType(resultType);
    }


    /**
     *
     * @param dto 是否包含DTO
     * @return 得到扫描的所有表结构模型
     */
    public static List<SoberTable> getScanTableAnnotationList(boolean dto) {
        IocContext iocContext = ConfigureContext.getInstance();
        List<String> scanPackageList = iocContext.getScanPackageList();
        if (!scanPackageList.contains("com.github.jspxnet.txweb.table"))
        {
            scanPackageList.add("com.github.jspxnet.txweb.table");
        }
        Set<SoberTable> result = new LinkedHashSet<>();
        //扫描目录加载注释的IocBean
        for (String classPath : scanPackageList) {
            List<SoberTable> scanList = getSanTableBean(StringUtil.trim(classPath),dto);
            result.addAll(scanList);
        }
        return new ArrayList<>(result);
    }

    /**
     *
     * @param className 类名称
     * @param dto 是否包含dto
     * @return 扫描Table注释类载入
     */

    public static List<SoberTable> getSanTableBean(String className,boolean dto) {
        List<SoberTable> result = new ArrayList<>();
        Set<Class<?>> list =  ClassScannerUtils.searchAnnotation(className,EnvFactory.getBaseConfiguration().getDefaultPath(), Table.class);
        for (Class<?> cls : list) {
            try {
                if (cls == null) {
                    continue;
                }
                SoberTable soberTable = com.github.jspxnet.sober.util.AnnotationUtil.getSoberTable(cls);
                if (soberTable==null)
                {
                    continue;
                }
                if (dto)
                {
                    result.add(soberTable);
                } else
                if (soberTable.isCreate())
                {
                    result.add(soberTable);
                }
            } catch (Exception e) {
                log.error("ioc scan load class dir error" + cls, e);
            }
        }
        return result;
    }


}