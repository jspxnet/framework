/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober;

import com.github.jspxnet.sober.config.SoberColumn;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-10-26
 * Time: 下午11:00
 */
public interface TableModels extends Serializable {

    boolean isEmpty();

    String getIdx();

    String getName();

    String getCaption();

    String getDatabaseName();

    void setDatabaseName(String databaseName);

    boolean isCreate();

    boolean isUseCache();

    Class<?> getEntity();

    String getPrimaryKey();

    void setPrimaryKey(String primary);

    boolean isAutoId();

    String getIdType();

    Date getLastDate();

    boolean isSerial();

    SoberColumn getColumn(String keys);

    String[] getFullFieldArray();

    String[] getFieldArray();

    void setAutoId(boolean autoId);

    List<SoberColumn> getColumns();

    void setColumns(List<SoberColumn> columns);

    void addColumns(SoberColumn column);

    boolean containsField(String keys);

    boolean isCanExtend();

    void setCanExtend(boolean canExtend);

    boolean isAutoCleanCache();

    long getId();

}