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

import com.github.jspxnet.json.JsonField;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.annotation.*;
import com.github.jspxnet.sober.enums.EntityLevelEnumType;
import com.github.jspxnet.sober.enums.MappingType;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.ArrayUtil;
import lombok.Data;
import lombok.Setter;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2025-1229
 * Time: 23:07:57
 * 将@Table的结构信息保存是组件库中,直接使用数据库的信息动态的生成dto对象,就是SoberTable对象,查询放入.
 * 同时包含创建等信息
 * 是 SoberTable 的保存表
 * {@code
 *  <table class="jspx.jcommon.model.table.ElementPageEctype" index="element_page_unique_index(url,namespace,originId)" caption="页面" />
 *  }
 *
 *
 */
@Data
@Table(name = "jspx_sober_table", caption = "动态表信息",idx = "jspx_sober_name_unique_idx(name)")
public class SoberTable implements TableModels {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Setter
    private boolean empty = false;

    @Override
    public boolean isEmpty() {
        return empty;
    }

    //数据库名 ，这里不是数据库类型
    @Column(caption = "实体类型", length = 50, notNull = true)
    private String databaseName = StringUtil.empty;

    //为了在平铺结构里边找到主表
    @Column(caption = "实体类型")
    private int entityLevel = EntityLevelEnumType.MAIN.getValue();

    @Column(caption = "表名", length = 50, notNull = true)
    private String name = StringUtil.empty;

    @Column(caption = "注释", length = 50)
    private String caption = StringUtil.empty;

    //是否动态创建表
    @Column(caption = "创建表", notNull = true)
    private boolean isCreate = true;

    //是否使用缓存
    @Column(caption = "使用缓存", notNull = true)
    private boolean useCache = true;

    //是否自动清理缓存
    @Column(caption = "自动清理缓存", notNull = true)
    private boolean autoCleanCache = false;

    //实体,具体的类
    @JsonIgnore
    private Class<?> entity;

    //关键字名
    @Column(caption = "关键字", length = 50, notNull = true)
    private String primaryKey = StringUtil.empty;

    //是否自动生成ID
    //是指使用sober构架来生成,false默认数据库自己生成
    @Column(caption = "自动生成ID", notNull = true)
    private boolean autoId = false;

    //是否使用数据库自增
    @Column(caption = "序列号生成方式", enumType = IDType.class, length = 50, notNull = true)
    private String idType = IDType.serial;
    //映射对应关系

    @Nexus(caption = "映射对应关系", mapping = MappingType.OneToMany, field = "name", targetField = "tableName",term = "version:eq[${version}]",  targetEntity = SoberColumn.class,save = true, chain = true,update = true, delete = true)
    private List<SoberColumn> columns = new LinkedList<>();

    //可扩展,系统是否锁定
    @Column(caption = "可扩展", notNull = true)
    private boolean canExtend = false;

    //绑定 类对象
    @Column(caption = "实体对象", length = 200)
    private String entityClass;

    //以此来表示继承关系
    @Column(caption = "父对象")
    private long parentId;

    //最后访问时间
    @Column(caption = "最后修改时间")
    private Date lastDate = null;

    @Column(caption = "版本号")
    private int version = 0;


    //索引 例子: element_page_unique_index(url,namespace,originId)
    @Column(caption = "关键字索性", length = 200)
    private String idx = StringUtil.empty;


    @Column(caption = "创建人", length = 40)
    private String creator = StringUtil.empty;

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

    @Override
    public boolean isSerial() {
        return IDType.serial.equalsIgnoreCase(idType);
    }


    @Override
    public String getPrimaryKey() {
        if (StringUtil.isNull(primaryKey))
        {
            for (SoberColumn column:columns)
            {
                if (column.isAutoincrement())
                {
                    return column.getName();
                }
            }
        }
        return primaryKey;
    }


    @Override
    public void addColumns(SoberColumn column)
    {
        this.columns.add(column);
    }

    @Override
    public SoberColumn getColumn(String keys) {
        for (SoberColumn column : columns) {
            if (column.getField().equalsIgnoreCase(keys) || column.getName().equalsIgnoreCase(keys) || column.getCaption().equalsIgnoreCase(keys)) {
                return column;
            }
        }
        return null;
    }

    @Override
    public boolean containsField(String keys) {
        for (SoberColumn column : columns) {
            if (column.getField().equalsIgnoreCase(keys)) {
                return true;
            }
        }
        return false;
    }
    /**
     * @return 都不包括 映射关系,这里不包括id
     */
    @Override
    public String[] getFieldArray() {
        String[] fieldArray = null;
        for (SoberColumn column : columns) {
            if (StringUtil.isNullOrWhiteSpace(column.getField()) || column.getNexus() != null || column.getCalcUnique() !=null)
            {
                continue;
            }
            if (IDType.serial.equalsIgnoreCase(idType) && column.getField().equals(primaryKey)) {
                continue;
            }
            fieldArray = ArrayUtil.add(fieldArray, column.getField());
        }
        return fieldArray;
    }

    /**
     * @return 都不包括 映射关系,这里包括id
     */
    @Override
    public String[] getFullFieldArray() {

        String[] fieldArray = null;
        for (SoberColumn column : columns) {
            if (StringUtil.isNullOrWhiteSpace(column.getField()) || column.getNexus() != null || column.getCalcUnique() !=null)
            {
                continue;
            }
            fieldArray = ArrayUtil.add(fieldArray, column.getField());
        }
        return fieldArray;
    }

    @Override
    public Date getLastDate() {
        return lastDate;
    }

    public void updateLastDate() {
        this.lastDate = new Date();
    }

    @Override
    public boolean isUseCache() {
        return useCache;
    }

    @Override
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        for (SoberColumn column : columns) {
            if (column==null)
            {
                continue;
            }
            column.setDatabaseName(databaseName);
        }
    }


    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString()
    {
        return ObjectUtil.toString(this);
    }

    @Override
    public boolean equals(Object models)
    {
        if (models==null)
        {
            return false;
        }
        return (toString()).equals(models.toString());
    }

    @JsonField(name="className")
    public String getClassName()
    {
        if (entity==null)
        {
            return StringUtil.empty;
        }
        return entity.getName();
    }

    @JsonField(name="isCanExtend")
    @Override
    public boolean isCanExtend() {
        return canExtend;
    }


    @Override
    public boolean isAutoCleanCache() {
        return autoCleanCache;
    }


}