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

import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.config.xml.*;
import com.github.jspxnet.sober.enums.DatabaseEnumType;
import com.github.jspxnet.sober.table.SqlMapConf;
import com.github.jspxnet.sober.table.SqlMapInterceptorConf;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.XMLUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-6
 * Time: 18:24:23
 */
@Slf4j
@Table(caption = "SqlMap配置",cache = true,create = false)
public class SQLRoom implements Serializable {
    //命名空间
    private String namespace;

    //开启数据库配置
    private boolean database = false;

    //拦截器列表
    private final LinkedList<SqlMapInterceptorConf> interceptorList = new LinkedList<>();

    //包含
    private final Map<String,String> includeMap = new HashMap<>();
    //查询SQL表
    private final Map<String,Map<String, SqlMapConf>> queryMap = new HashMap<>();
    //更新SQL表
    private final Map<String, Map<String, SqlMapConf>> updateMap = new HashMap<>();
    //执行SQL表
    private final Map<String, Map<String, SqlMapConf>> executeMap = new HashMap<>();

    /**
     * @return 命名空间
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     *
     * @param namespace 命名空间
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public boolean isDatabase() {
        return database;
    }

    public void setDatabase(boolean database) {
        this.database = database;
    }

    /**
     * 载入 include
     * @param sql sql
     * @return 返回
     */
    public String getReplenish(String sql)  {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(IncludeXml.TAG_NAME, IncludeXml.class.getName());
        List<TagNode> list = null;
        try {
            list = xmlEngine.getTagNodes(sql);
        } catch (Exception e) {
            log.error("sql map 配置中语法存在错误sql:{}",sql);
            e.printStackTrace();
        }
        if (list!=null)
        {
            for (TagNode node : list) {
                BaseXmlTagNode beanEl = (BaseXmlTagNode) node;
                String includeStr = includeMap.get(beanEl.getId());
                sql = StringUtil.replace(sql,beanEl.getSource(),includeStr);
            }
        }
        for (String key:includeMap.keySet())
        {
            sql = StringUtil.replace(sql,key,includeMap.get(key));
        }
        return sql;
    }

    public SqlMapConf getQueryMapSql(String id, String db)
    {
        return getMapSql( queryMap, id,  fixDbName(db));
    }

    public SqlMapConf getUpdateMapSql(String id, String db)
    {
        return getMapSql( updateMap, id,  fixDbName(db));
    }

    public SqlMapConf getExecuteMapSql(String id, String db)
    {
        return getMapSql( executeMap, id,  fixDbName(db));
    }

    public LinkedList<SqlMapInterceptorConf>  getInterceptorList()
    {
        return interceptorList;
    }
    /**
     *
     * @param key map key
     * @param value body
     */
    public void addInclude(String key,String value)
    {
        includeMap.put(key,XMLUtil.xmlCdataDecrypt(value));
    }

    /**
     *
     * @param sqlMapInterceptorConf 拦截器配置
     */
    public void addInterceptor(SqlMapInterceptorConf sqlMapInterceptorConf)
    {
        interceptorList.addLast(sqlMapInterceptorConf);
    }
    /**
     * 添加查询配置
     * @param sqlMapConf 配置文件
     */
    public void addQuery(SqlMapConf sqlMapConf)
    {
        Map<String, SqlMapConf> configMap = queryMap.computeIfAbsent(sqlMapConf.getName(), k -> new HashMap<>());
        configMap.put(StringUtil.toLowerCase(sqlMapConf.getDatabaseType()),sqlMapConf);
    }


    /**
     *  添加跟新配置
     * @param sqlMapConf 配置文件
     */
    public void addUpdate(SqlMapConf sqlMapConf)
    {
        Map<String, SqlMapConf> configMap = updateMap.computeIfAbsent(sqlMapConf.getName(), k -> new HashMap<>());
        configMap.put(StringUtil.toLowerCase(sqlMapConf.getDatabaseType()),sqlMapConf);
    }
    /**
     *  添加跟新执行配置
     * @param sqlMapConf 配置文件
     */
    public void addExecute(SqlMapConf sqlMapConf)
    {
        Map<String, SqlMapConf> configMap = executeMap.computeIfAbsent(sqlMapConf.getName(), k -> new HashMap<>());
        configMap.put(StringUtil.toLowerCase(sqlMapConf.getDatabaseType()),sqlMapConf);
    }


    /**
     * 提供给解析器是用,放入配置信息
     * @param map 配置列表
     * @param sqlMapConf 配置文件
     * @return sql配置,一般不用
     */
    private static SqlMapConf addConfig(Map<String, Map<String, SqlMapConf>> map,SqlMapConf sqlMapConf)
    {
        Map<String, SqlMapConf> configMap = map.computeIfAbsent(sqlMapConf.getName(), k -> new HashMap<>());
        return configMap.put(StringUtil.toLowerCase(sqlMapConf.getDatabaseType()),sqlMapConf);
    }

    /**
     * 修复数据库名称,不区分大小写
     * @param db 数据库名称
     * @return 修复的名称
     */
    private static String fixDbName(String db)
    {
        if (db==null)
        {
            db = DatabaseEnumType.General.getName();
        }
        return db.toLowerCase();
    }

    /**
     * 统一的查询方法
     * @param confMap 配置
     * @param id id
     * @param db 数据库名称
     * @return 得到配置
     */
    private static SqlMapConf getMapSql(Map<String,Map<String, SqlMapConf>> confMap,String id, String db)
    {
        Map<String, SqlMapConf> map = confMap.get(id);
        if (ObjectUtil.isEmpty(map))
        {
            return null;
        }
        SqlMapConf result = null;
        if (map.size()==1)
        {
            result = map.values().iterator().next();
            if (StringUtil.isEmpty(result.getQuote()))
            {
                return result;
            }
            if (!StringUtil.isEmpty(result.getQuote())&&!result.getQuote().equalsIgnoreCase(id))
            {
                return  getMapSql2(confMap,result.getQuote(), db);
            }
        }
        if (result==null)
        {
            result = map.get(db);
        }
        if (result!=null&&!StringUtil.isEmpty(result.getQuote())&&!result.getQuote().equalsIgnoreCase(id))
        {
           return  getMapSql2(confMap,result.getQuote(), db);
        }
        if (ObjectUtil.isEmpty(result))
        {
            result = getDefaultMap(map);
        }
        return result;
    }

    /**
     * 统一的查询方法 这里只是为了避免死循环,引用只有一层
     * @param confMap 配置
     * @param id id
     * @param db 数据库名称
     * @return 得到配置
     */
    private static SqlMapConf getMapSql2(Map<String,Map<String, SqlMapConf>> confMap,String id, String db)
    {
        Map<String, SqlMapConf> map = confMap.get(id);
        if (map==null)
        {
            return null;
        }
        if (map.size()==1)
        {
            return map.values().iterator().next();
        }
        SqlMapConf result = map.get(db);
        if (result==null)
        {
            result = getDefaultMap(map);
        }
        return result;
    }

    /**
     *
     * @param map 配置
     * @return 得到默认配置
     */
    private static SqlMapConf getDefaultMap(Map<String, SqlMapConf> map)
    {
        for (String key:map.keySet())
        {
            if (StringUtil.isNull(key) || DatabaseEnumType.General.getName().equalsIgnoreCase(key))
            {
                return map.get(key);
            }
        }
        return map.values().iterator().next();
    }

}