/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.bundle;

import com.github.jspxnet.json.JSONException;
import com.github.jspxnet.txweb.bundle.table.BundleTable;
import com.github.jspxnet.txweb.model.dto.SoberColumnDto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.io.IOException;


/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2004-4-5
 * Time: 18:07:03
 */
public interface Bundle extends Serializable {
    String getNamespace();

    String getDataType();

    String getEncode();

    String getString(String key);

    String get(String key);

    String get(String key, String defVar);

    boolean getBoolean(String key);

    double getDouble(String key);

    float getFloat(String key);

    long getLong(String key);

    List<SoberColumnDto> getColumnList();

    int getInt(String key);

    int getInt(String key, int defVar);

    String[] getArray(String key);

    BundleTable getBundleTable(final String keys);

    SoberColumnDto getSoberColumn(final String keys);

    boolean save(BundleTable bundletable) throws Exception;

    boolean save(String key, String value) throws Exception;

    boolean save(String key, String value, int encrypt) throws Exception;

    List<BundleTable> getList();

    //载入数据
    void loadMap();

    Map<String, String> toMap();

    //boolean createForXML(String xmlString);
    //彻底删除数据
    boolean deleteAll();

    //List getList();
    //回存数据
    void flush() throws Exception;

    int size();

    String toXml() throws IOException;

    String toJson() throws JSONException;

    boolean remove(String key);

    String getString(String key, String defVar);

    String getLang(String key);

    String getLang(String key, String def);
}