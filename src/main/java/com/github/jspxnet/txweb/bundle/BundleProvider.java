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

import com.github.jspxnet.io.StringOutputStream;
import com.github.jspxnet.json.JSONException;
import com.github.jspxnet.scriptmark.util.ScriptConverter;
import com.github.jspxnet.sober.util.AnnotationUtil;
import com.github.jspxnet.txweb.bundle.table.BundleTable;
import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.txweb.model.dto.SoberColumnDto;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.SystemUtil;
import com.github.jspxnet.utils.StringUtil;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.beans.XMLEncoder;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;



/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-27
 * Time: 11:35:25
 */
@Slf4j
public abstract class BundleProvider implements Bundle, Serializable {

    @Setter
    protected String namespace = TXWeb.global;
    @Setter
    protected String dataType = StringUtil.empty;
    protected String encode = SystemUtil.encode;
    final protected Map<String, String> cache = new HashMap<>();


    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public String getDataType() {
        return dataType;
    }

    @Override
    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        if (StringUtil.isNull(encode)) {
            return;
        }
        this.encode = encode;
    }

    @Override
    public String getString(String key) {
        return getValue(key);
    }

    @Override
    public String[] getArray(String key) {
        String v = getValue(key);
        return StringUtil.split(StringUtil.replace(v, ",", ";"), ";");
    }

    @Override
    public String get(String key) {
        return get(key, "");
    }

    @Override
    public String get(String key, String defVar) {
        String temp = getValue(key);
        if (StringUtil.isNull(temp)) {
            return defVar;
        }
        return temp;
    }

    @Override
    public String getString(String key, String defVar) {
        return get(key, defVar);
    }

    @Override
    public String getLang(String key) {
        String temp = getValue(key);
        if (StringUtil.isNull(temp)) {
            return key;
        }
        return temp;
    }

    @Override
    public String getLang(String key, String def) {
        return getString(key, def);
    }

    @Override
    public boolean save(String key, String value, int encrypt) throws Exception {
        BundleTable bundleTable = new BundleTable();
        bundleTable.setId(0);
        bundleTable.setIdx(key);
        bundleTable.setContext(value);
        bundleTable.setDataType(dataType);
        bundleTable.setNamespace(namespace);
        bundleTable.setEncrypt(encrypt);
        return save(bundleTable);
    }

    @Override
    public boolean getBoolean(String key) {
        return StringUtil.toBoolean(get(key));
    }

    @Override
    public int getInt(String key, int defVar) {
        int result = getInt(key);
        return result == 0 ? defVar : result;
    }


    public SoberColumnDto getSoberColumn(final String keys)
    {
        BundleTable bundleTable = getBundleTable(keys);
        if (bundleTable==null)
        {
            return null;
        }
        SoberColumnDto dto = new SoberColumnDto();
        dto.setName(bundleTable.getIdx());
        dto.setCaption(bundleTable.getCaption());
        dto.setDefaultValue(bundleTable.getContext());
        dto.setInput("text");
        dto.setClassType(String.class);
        dto.setTableName(AnnotationUtil.getTableName(BundleTable.class));
        dto.setLength(bundleTable.getContext()==null?200:bundleTable.getContext().length());
        dto.setDataType(bundleTable.getDataType());
        return dto;
    }

    @Override
    public int getInt(String key) {
        return ObjectUtil.toInt(getValue(key));
    }

    @Override
    public long getLong(String key) {
        return ObjectUtil.toLong(getValue(key));
    }

    @Override
    public double getDouble(String key) {
        return ObjectUtil.toDouble(getValue(key));
    }

    @Override
    public float getFloat(String key) {
        return ObjectUtil.toFloat(getValue(key));
    }

    @Override
    public abstract void flush() throws Exception;

    @Override
    public abstract List<BundleTable> getList();

    public abstract String getValue(String key);

    @Override
    public abstract void loadMap();

    @Override
    public abstract boolean remove(String key);

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public Map<String, String> toMap() {
        loadMap();
        return cache;
    }

    public String toString(String key) {
        return getValue(key);
    }

    @Override
    public String toJson() throws JSONException {
        return ScriptConverter.toJson(cache);
    }

    @Override
    public String toXml() throws IOException {
        OutputStream sw = new StringOutputStream();
        try {
            java.beans.XMLEncoder en = new XMLEncoder(sw);
            en.writeObject(getList());
            en.flush();
            en.close();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        } finally {
            sw.flush();
            sw.close();
        }
        return sw.toString();
    }
}