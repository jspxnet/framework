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

import com.github.jspxnet.sober.config.SoberCalcUnique;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.config.SoberNexus;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-10-26
 * Time: 下午11:00
 */
public interface TableModels extends Serializable {
    String getName();

    String getCaption();

    boolean isCreate();

    boolean isUseCache();

    Class getEntity();

    String getPrimary();

    boolean isAutoId();

    String getIdType();

    Map<String, SoberNexus> getNexusMap();

    Map<String, SoberCalcUnique> getCalcUniqueMap();

    long getLastDate();

    boolean isSerial();

    SoberColumn getColumn(String keys);

    String[] getFullFieldArray();

    String[] getFieldArray();

    List<SoberColumn> getColumns();

    void setColumns(List<SoberColumn> columns);

    boolean containsField(String keys);


}