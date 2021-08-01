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

import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.annotation.IDType;

import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.ArrayUtil;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-6
 * Time: 23:07:57
 */
public class SoberTable implements TableModels {
    //数据库名
    private String databaseName = StringUtil.empty;

    //数据库表名
    private String name = StringUtil.empty;
    //表别名
    private String caption = StringUtil.empty;

    //是否动态创建表
    private boolean create = true;

    //cache
    private boolean useCache = true;

    //实体,具体的类
    @JsonIgnore
    private Class<?> entity;
    //关键字名
    private String primary = StringUtil.empty;
    //是否自动生成ID
    private boolean autoId = true;
    //是否使用数据库自增
    private String idType = StringUtil.empty;
    //映射对应关系
    private Map<String, SoberNexus> nexusMap = new LinkedHashMap<String, SoberNexus>();
    //字段
    private List<SoberColumn> columns = new LinkedList<SoberColumn>();
    //字段
    private Map<String, SoberCalcUnique> calcUniqueMap = new LinkedHashMap<String, SoberCalcUnique>();
    //最后访问时间
    private long lastDate = System.currentTimeMillis();

    /**
     * 得到表名
     *
     * @return String
     */
    @Override
    public String getName() {
        if (StringUtil.isNull(name)) {
            name = entity.getSimpleName();
        }
        return name;
    }

    @Override
    public String getCaption() {
        if (StringUtil.isNull(caption)) {
            return getName();
        }
        return caption;
    }

    public void setTableCaption(String tableCaption) {
        this.caption = tableCaption;
    }

    @Override
    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    @Override
    public boolean isSerial() {
        return IDType.serial.equalsIgnoreCase(idType);
    }

    /**
     * 设置表名
     *
     * @param tableName 表名
     */
    public void setName(String tableName) {
        this.name = tableName;
    }

    @Override
    public Class<?> getEntity() {
        return entity;
    }

    public void setEntity(Class<?> entity) {
        this.entity = entity;
    }

    @Override
    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    @Override
    public boolean isAutoId() {
        return autoId;
    }

    public void setAutoId(boolean autoId) {
        this.autoId = autoId;
    }

    @Override
    public List<SoberColumn> getColumns() {
        return columns;
    }

    @Override
    public void setColumns(List<SoberColumn> columns) {
        this.columns = columns;
    }

    @Override
    public SoberColumn getColumn(String keys) {
        for (SoberColumn column : columns) {
            if (column.getName().equalsIgnoreCase(keys)) {
                return column;
            }
        }
        return null;
    }

    @Override
    public boolean containsField(String keys) {
        for (SoberColumn column : columns) {
            if (column.getName().equalsIgnoreCase(keys)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<String, SoberCalcUnique> getCalcUniqueMap() {
        return calcUniqueMap;
    }

    public void setCalcUniqueMap(Map<String, SoberCalcUnique> calcUniqueMap) {
        this.calcUniqueMap = calcUniqueMap;
    }

    @Override
    public String[] getFieldArray() {
        String[] fieldArray = null;
        for (SoberColumn column : columns) {
            if (IDType.serial.equalsIgnoreCase(idType) && column.getName().equals(primary)) {
                continue;
            }
            fieldArray = ArrayUtil.add(fieldArray, column.getName());
        }
        return fieldArray;
    }

    @Override
    public String[] getFullFieldArray() {

        String[] fieldArray = null;
        for (SoberColumn column : columns) {
            fieldArray = ArrayUtil.add(fieldArray, column.getName());
        }
        return fieldArray;
    }

    @Override
    public Map<String, SoberNexus> getNexusMap() {
        return nexusMap;
    }

    public void setNexusMap(Map<String, SoberNexus> nexusMap) {
        this.nexusMap = nexusMap;
    }

    @Override
    public long getLastDate() {
        return lastDate;
    }

    public void setLastDate(long lastDate) {
        this.lastDate = lastDate;
    }

    public void updateLastDate() {
        this.lastDate = System.currentTimeMillis();
    }

    @Override
    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    @Override
    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<table caption=\"").append(caption).append("\" ");
        sb.append("name=\"").append(name).append("\" ");
        sb.append("create=\"").append(create).append("\" ");
        sb.append("useCache=\"").append(useCache).append("\" ");
        return sb.toString();
    }
}