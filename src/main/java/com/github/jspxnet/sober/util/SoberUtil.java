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
import com.github.jspxnet.sober.config.BaseXmlTagNode;
import com.github.jspxnet.sober.config.SQLRoom;
import com.github.jspxnet.sober.config.SqlMapConfig;
import com.github.jspxnet.sober.config.xml.*;
import com.github.jspxnet.sober.dialect.Dialect;
import com.github.jspxnet.utils.XMLUtil;
import lombok.extern.slf4j.Slf4j;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.utils.StringUtil;
import java.io.Serializable;
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
    public static boolean containsFields(TableModels soberTable, String[] fields) {
        if (fields == null || soberTable == null) {
            return true;
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
        StringBuilder sb = new StringBuilder(aClass.getName());
        sb.append(CACHE_TREM_LOAD);
        //满足redis 规范
        sb.append(field).append(CACHE_TREM_EQUALS).append(find).append(CACHE_TREM_CHILD).append(loadChild);
        return sb.toString();
    }


    public static String getListKey(Class<?> aClass, String term,String sort,int begin,int end, boolean loadChild) {
        //满足redis 规范
        StringBuilder sb = new StringBuilder(aClass.getName());
        sb.append(CACHE_TREM_LIST);
        sb.append(EncryptUtil.getMd5(term)).append("_T_").append(sort).append(CACHE_TREM_CHILD).append("_L").append(begin).append("_").append(end).append(loadChild);
        return StringUtil.replace(sb.toString()," ","");
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
    public static Map<String, Object> getDataHashMap(ResultSetMetaData resultSetMetaData, Dialect dialect, ResultSet resultSet) throws SQLException {
        int numColumns = resultSetMetaData.getColumnCount();
        Map<String, Object> resultMap = new HashMap<>();
        for (int c = 1; c <= numColumns; c++) {
            String colName = resultSetMetaData.getColumnLabel(c);
            resultMap.put(colName, dialect.getResultSetValue(resultSet, c));
        }
        return resultMap;
    }

    public static Object getSingleValue(ResultSetMetaData resultSetMetaData, Dialect dialect, ResultSet resultSet) throws SQLException {
        int numColumns = resultSetMetaData.getColumnCount();
        Map<String, Object> resultMap = new HashMap<>();
        for (int c = 1; c <= numColumns; c++) {
            String colName = resultSetMetaData.getColumnLabel(c);
            resultMap.put(colName, dialect.getResultSetValue(resultSet, c));
        }
        return resultMap;
    }

    /**
     *  读取xml sql
     * @param xmlString xml配置
     * @param allSqlMap  总配置表
     * @throws Exception 异常
     */
    public static void readSqlMap(String xmlString,final Map<String, SQLRoom> allSqlMap) throws Exception
    {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(SqlMapXml.TAG_NAME, SqlMapXml.class.getName());
        List<TagNode> list = xmlEngine.getTagNodes(xmlString);
        for (TagNode node : list) {
            SqlMapXml sqlMapXmlEl = (SqlMapXml) node;
            String namespace = sqlMapXmlEl.getNamespace().toLowerCase();
            //读取数据
            readSqlRoom(sqlMapXmlEl.getBody(),namespace,allSqlMap);
            //处理include标签

        }
    }



    /**
     * 读取放入SqlRoom
     * @param xmlString xml配置
     * @param namespace 命名空间
     * @param allSqlMap 总配置表
     * @throws Exception 异常
     */
    public static void readSqlRoom(String xmlString,String namespace,final Map<String, SQLRoom> allSqlMap) throws Exception {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(ExecuteXml.TAG_NAME, ExecuteXml.class.getName());
        xmlEngine.putTag(QueryXml.TAG_NAME, QueryXml.class.getName());
        xmlEngine.putTag(UpdateXml.TAG_NAME, UpdateXml.class.getName());
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
            }
        }
    }


}