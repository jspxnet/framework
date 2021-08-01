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

import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sober.SoberEnv;
import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.sober.annotation.IDType;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.config.BaseXmlTagNode;
import com.github.jspxnet.sober.config.SQLRoom;
import com.github.jspxnet.sober.config.SoberTable;
import com.github.jspxnet.sober.config.SqlMapConfig;
import com.github.jspxnet.sober.config.xml.*;
import com.github.jspxnet.sober.dialect.Dialect;
import com.github.jspxnet.sober.dialect.DmDialect;
import com.github.jspxnet.sober.dialect.OracleDialect;
import com.github.jspxnet.sober.enums.DatabaseEnumType;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.XMLUtil;
import lombok.extern.slf4j.Slf4j;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.utils.StringUtil;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ChenYuan
 * date: 12-3-21
 * Time: 下午8:39
 * 换成规则
 */
@Slf4j
public class SoberUtil {

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

    public static String getLoadKey(Class<?> aClass, Serializable field, Object find, boolean loadChild) {
        return aClass.getName() + CACHE_TREM_LOAD +
                //满足redis 规范
                field + CACHE_TREM_EQUALS + find + CACHE_TREM_CHILD + loadChild;
    }


    public static String getListKey(Class<?> aClass, String term,String sort,int begin,int end, boolean loadChild) {
        //满足redis 规范
        String sb = aClass.getName() + CACHE_TREM_LIST +
                EncryptUtil.getMd5(term) + "_T_" + sort + CACHE_TREM_CHILD + "_L" + begin + "_" + end + loadChild;
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
/*

    public static Object getSingleValue(ResultSetMetaData resultSetMetaData, Dialect dialect, ResultSet resultSet) throws SQLException {
        int numColumns = resultSetMetaData.getColumnCount();
        Map<String, Object> resultMap = new HashMap<>();
        for (int c = 1; c <= numColumns; c++) {
            String colName = resultSetMetaData.getColumnLabel(c);
            resultMap.put(colName, dialect.getResultSetValue(resultSet, c));
        }
        return resultMap;
    }
*/


    /**
     * 读取xml sql
     * @param xmlString xml配置
     * @param allSqlMap 总配置表
     * @param tableListMap 初始化表
     * @throws Exception 异常
     */
    public static void readSqlMap(String xmlString,final Map<String, SQLRoom> allSqlMap,Map<Class<?>,SqlMapConfig> tableListMap) throws Exception
    {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(SqlMapXml.TAG_NAME, SqlMapXml.class.getName());
        List<TagNode> list = xmlEngine.getTagNodes(xmlString);
        for (TagNode node : list) {
            SqlMapXml sqlMapXmlEl = (SqlMapXml) node;
            String namespace = sqlMapXmlEl.getNamespace().toLowerCase();
            //读取数据
            readSqlRoom(sqlMapXmlEl.getBody(),namespace,allSqlMap,tableListMap);
            //处理include标签

        }
    }



    /**
     * 读取放入SqlRoom
     * @param xmlString xml配置
     * @param namespace 命名空间
     * @param allSqlMap 总配置表
     * @param tableListMap 初始化表
     * @throws Exception 异常
     */
    public static void readSqlRoom(String xmlString,String namespace,final Map<String, SQLRoom> allSqlMap,Map<Class<?>,SqlMapConfig> tableListMap) throws Exception {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(TableXml.TAG_NAME, TableXml.class.getName());
        xmlEngine.putTag(ExecuteXml.TAG_NAME, ExecuteXml.class.getName());
        xmlEngine.putTag(QueryXml.TAG_NAME, QueryXml.class.getName());
        xmlEngine.putTag(UpdateXml.TAG_NAME, UpdateXml.class.getName());
        xmlEngine.putTag(SqlXml.TAG_NAME, SqlXml.class.getName());

        List<TagNode> list = xmlEngine.getTagNodes(xmlString);
        SQLRoom sqlRoom = allSqlMap.get(namespace.toLowerCase());
        if (sqlRoom == null) {
            sqlRoom = new SQLRoom();
            sqlRoom.setNamespace(namespace.toLowerCase());
            allSqlMap.put(namespace, sqlRoom);
        }
        for (TagNode node : list) {
            BaseXmlTagNode beanEl = (BaseXmlTagNode) node;
            SqlMapConfig config  = new SqlMapConfig();
            config.setId(beanEl.getId());
            config.setDatabase(beanEl.getDatabase());
            config.setResultType(beanEl.getResultType());
            config.setContext(StringUtil.trim(XMLUtil.xmlCdataDecrypt(beanEl.getBody())));
            config.setQuote(beanEl.getQuote());
            if (TableXml.TAG_NAME.equalsIgnoreCase(node.getTagName()))
            {
                config.setIndex(beanEl.getIndex());
                if (StringUtil.isEmpty(config.getContext()))
                {
                    config.setContext(beanEl.getResultType());
                }
                addTable(tableListMap,config,namespace);
            }
            if (ExecuteXml.TAG_NAME.equalsIgnoreCase(node.getTagName()))
            {
                sqlRoom.addExecute(config);
            } else
            if (QueryXml.TAG_NAME.equalsIgnoreCase(node.getTagName()))
            {
                sqlRoom.addQuery(config);
            } else
            if (UpdateXml.TAG_NAME.equalsIgnoreCase(node.getTagName()))
            {
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
     * @param sqlMapConfig  配置
     * @param namespace 命名空间
     */
    static public void addTable(Map<Class<?>,SqlMapConfig> tableListMap,SqlMapConfig sqlMapConfig,String namespace)
    {
        if (StringUtil.isEmpty(sqlMapConfig.getContext()))
        {
            return;
        }

        Class<?> cls;
        try {
            cls = ClassUtil.loadClass(sqlMapConfig.getContext());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.error("检查sqlMap配置文件:" + namespace + "中初始化配置init中的table对象不能找到:{},错误:{}",sqlMapConfig.getContext(),e.getMessage());
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
        tableListMap.put(cls,sqlMapConfig);
    }

    /**
     *
     * @param tableList 初始化表列表
     * @param soberSupport 数据对象
     */
    public static void initTable(List<SqlMapConfig> tableList, SoberSupport soberSupport)
    {
        if (!ObjectUtil.isEmpty(tableList))
        {
            for (SqlMapConfig sqlMapConfig:tableList)
            {
                if (StringUtil.isEmpty(sqlMapConfig.getContext()))
                {
                    continue;
                }

                Class<?> cls;
                try {
                    cls = ClassUtil.loadClass(sqlMapConfig.getContext());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    log.error("检查sqlMap配置,中初始化配置init中的table对象不能找到:{},错误:{}",sqlMapConfig.getContext(),e.getMessage());
                    continue;
                }
                Table table = AnnotationUtil.getTable(cls);
                if (table==null || !table.create())
                {
                    log.info( "检查sqlMap配置,中初始化配置init中的:{},它不是一个合规的数据库表对象必须开启@Table创建属性",cls.getName());
                    continue;
                }
                createTableAndIndex(cls,sqlMapConfig,soberSupport);

            }
        }
    }

    /**
     * 创建索引
     * @param cls 类对象
     * @param sqlMapConfig 索引配置
     * @param soberSupport 数据连接
     */
    public static void createTableIndex(Class<?> cls,SqlMapConfig sqlMapConfig, SoberSupport soberSupport)
    {
        //创建索引

        TableModels tableModels = AnnotationUtil.getSoberTable(cls);
        if (tableModels==null)
        {
            return;
        }
        String indexList = sqlMapConfig.getIndex();
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
     * @param sqlMapConfig 配置
     * @param soberSupport 数据对象
     * @return 表模型对象
     */
    public static TableModels createTableAndIndex(Class<?> cla,SqlMapConfig sqlMapConfig, SoberSupport soberSupport)
    {
        //&& !soberSupport.tableExists(cla)
        SoberTable soberTable = AnnotationUtil.getSoberTable(cla);
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
        DatabaseEnumType[] oracleDatabaseEnumType = new DatabaseEnumType[]{DatabaseEnumType.ORACLE,DatabaseEnumType.DB2,DatabaseEnumType.DM};

        String sql = null;
        try {
            if (soberTable.isCreate()&& !soberSupport.tableExists(soberTable)) {

                sql = soberSupport.getCreateTableSql(cla,soberTable);
                //oracle只能一个; 一个; 的执行
                if (DatabaseEnumType.inArray(oracleDatabaseEnumType,soberSupport.getSoberFactory().getDatabaseType()))
                {
                    String[] sqlLines = StringUtil.split(sql,StringUtil.SEMICOLON);
                    for (String sqlLine:sqlLines)
                    {
                        if (StringUtil.isNull(sqlLine))
                        {
                            continue;
                        }
                        soberSupport.execute(sqlLine);
                    }
                    //创建数据库序列
                    if (IDType.serial.equalsIgnoreCase(soberTable.getIdType()))
                    {
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

                        String tiggerSql = soberSupport.getSoberFactory().getDialect().processTemplate(Dialect.ORACLE_CREATE_SEQ_TIGGER,valueMap);
                        log.info("tiggerSql:\r\n{}",tiggerSql);
                        soberSupport.execute(tiggerSql);
                    }
                }
                else
                {
                    //其他数据库可以一次执行
                    soberSupport.execute(sql);
                }
                if (sqlMapConfig!=null)
                {
                    createTableIndex(cla, sqlMapConfig,  soberSupport);
                }
            }
        } catch (Exception e) {
            log.error("ERROR:auto create table 自动创建表错误:" + sql + " table:" + soberTable.getName() + " 原因:没有得到连接或者和数据库不兼容", e);
            e.printStackTrace();
            return null;
        }
        return soberTable;
    }



}