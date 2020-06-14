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
import java.util.Map;
import java.util.Date;
import java.io.Serializable;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-4-14
 * Time: 0:41:16
 * DataMap特点，key不区分大小写，提供类型转换功能
 */
public class DataMap<K extends String, V> extends TreeMap<K, V> implements Serializable {
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
     * html 图片
     *
     * @param fname  字段
     * @param width  宽
     * @param height 高
     * @return 输出图片
     */
    public String getImageHtml(String fname, int width, int height) {
        Object o = get(fname);
        if (o == null) {
            return StringUtil.empty;
        }
        String result = (String) o;
        if (StringUtil.isNull(result)) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<img src=").append(result).append(" border=0 ");
        if (width > 0) {
            sb.append("width=").append(NumberUtil.toString(width)).append(" ");
        }

        if (width > 0) {
            sb.append("height=").append(NumberUtil.toString(height)).append(" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * 输出图片
     *
     * @param fname  字段
     * @param width  宽
     * @param height 高
     * @param path   路径
     * @return 输出图片
     */
    public String getImageHtml(String fname, int width, int height, String path) {
        Object o = get(fname);
        if (o == null) {
            return StringUtil.empty;
        }
        String result = (String) o;
        if (StringUtil.isNull(result)) {
            return StringUtil.empty;
        }
        result = path + result;
        StringBuilder sb = new StringBuilder();
        sb.append("<img src=").append(result).append(" border=0 ");
        if (width > 0) {
            sb.append("width=").append(NumberUtil.toString(width)).append(" ");
        }

        if (width > 0) {
            sb.append("height=").append(NumberUtil.toString(height)).append(" ");
        }
        sb.append("/>");

        return sb.toString();
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
        return super.get(key.toLowerCase());
    }

    @Override
    public V put(K k, V v) {
        String key = k.toLowerCase();
        return super.put((K) key, v);
    }

    @Override
    public V remove(java.lang.Object o) {
        String key = (String) o;
        return super.remove(key.toLowerCase());
    }

    @Override
    public void putAll(java.util.Map<? extends K, ? extends V> inmap) {
        Map<K, V> xmap = (Map<K, V>) inmap;
        for (K myk : xmap.keySet()) {
            super.put((K) myk.toLowerCase(), xmap.get(myk));
        }
    }
}