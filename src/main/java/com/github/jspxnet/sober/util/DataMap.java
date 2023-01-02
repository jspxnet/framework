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


import com.github.jspxnet.utils.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.io.Serializable;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-4-14
 * Time: 0:41:16
 * DataMap特点，key不区分大小写，提供类型转换功能
 */
public class DataMap<K extends String, V> extends HashMap<K, V> implements Serializable {
    private final Map<String, String> keyMap = new HashMap<>();
    public DataMap() {

    }

    /**
     * @param fname 字段名称
     * @return 得到Double类型
     */
    public double getDouble(String fname) {
        Object o = get(fname);
        if (o == null) {
            return 0;
        }
        return (Double) o;
    }


    public float getFloat(String name) {
        Object o = get(name);
        return ObjectUtil.toFloat(o);
    }

    /**
     * @param name 字段名称
     * @param scale 保留几位
     * @return 得到Double类型
     */

    public double getDouble(String name, int scale) {
        return NumberUtil.getRound(getDouble(name), scale).doubleValue();
    }

    /**
     * @param name 字段名称
     * @return 得到Int类型
     */
    public int getInt(String name) {
        Object o = get(name);
        if (o == null) {
            return 0;
        }
        return StringUtil.toInt(o.toString());
    }

    public long getLong(String name) {
        Object o = get(name);
        if (o == null) {
            return 0;
        }
        return StringUtil.toLong(o.toString());
    }


    public String getValue(String fname) {
        Object o = get(fname);
        if (o == null) {
            return StringUtil.empty;
        }
        String s = o.toString();
        if (StringUtil.isNull(s)) {
            return StringUtil.empty;
        }
        return s;
    }

    /**
     * @param fname 字段
     * @return 得到boolean 类型
     */
    public boolean getBoolean(String fname) {
        return StringUtil.toBoolean(getValue(fname));
    }

    /**
     * @param fname  字段
     * @param strue  成立的数据
     * @param sfalse 不成立的数据
     * @return 判断条件输出结果String
     */
    public String getBoolean(String fname, String strue, String sfalse) {
        if (getBoolean(fname)) {
            return strue;
        }
        return sfalse;
    }


    /**
     * @param fname      字段
     * @param dateFormat 日期格式
     * @return 得到Date类型String
     */
    public String getDateString(String fname, String dateFormat) {
        Object o = get(fname);
        if (o == null) {
            return StringUtil.empty;
        }
        Date date = (Date) o;
        return DateUtil.toString(date, dateFormat);
    }

    /**
     * @param fname 字段
     * @return 得到Date类型
     */
    public Date getDate(String fname) {
        Object o = get(fname);
        if (o == null) {
            return null;
        }
        return ObjectUtil.toDate(o);
    }

    public String getValue(String name, int length) {
        return getValue(name, length, false, "");
    }

    public String getValue(String name, int length, boolean nohtml) {
        return getValue(name, length, nohtml, "");
    }

    public String getValueNewlines(String name) {
        return StringUtil.toBrLine(getValue(name));
    }


    /**
     * @param fname     字段名称
     * @param length    长度
     * @param nohtml    是否取出html
     * @param addstring 尾巴
     * @return 值
     */
    public String getValue(String fname, int length, boolean nohtml, String addstring) {
        Object o = get(fname);
        if (o == null) {
            return StringUtil.empty;
        }
        String result = (String) o;
        if (nohtml) {
            result = HtmlUtil.deleteHtml(result);
        }
        if (result.length() > length + 2) {
            result = result.substring(0, length) + addstring;
        }
        return result;
    }

    @Override
    public V get(java.lang.Object o) {
        String key = (String) o;
        if (key == null) {
            return null;
        }
        key = keyMap.get(key.toLowerCase());
        return super.get(key);
    }

    @Override
    public boolean containsKey(Object key) {
        return keyMap.containsKey(((String)key).toLowerCase());
    }


    @Override
    public V put(K k, V v) {
        if (k==null)
        {
           return null;
        }
        String key = k.toLowerCase();
        keyMap.put(key,k);
        return super.put(k, v);
    }

    @Override
    public V remove(java.lang.Object o) {
        String key = (String) o;
        key = keyMap.get(key.toLowerCase());
        return super.remove(key);
    }

    @Override
    public void putAll(java.util.Map<? extends K, ? extends V> inmap) {
        Map<K, V> xmap = (Map<K, V>) inmap;
        for (K myk : xmap.keySet()) {
            put(myk, xmap.get(myk));
        }
    }

    @Override
    public void clear()
    {
        keyMap.clear();
        super.clear();
    }

}