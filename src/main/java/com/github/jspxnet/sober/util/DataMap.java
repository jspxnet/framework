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
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import java.util.Date;



/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-4-14
 * Time: 0:41:16
 * DataMap特点，key不区分大小写，提供类型转换功能
 */
public class DataMap<K extends String, V> extends CaseInsensitiveMap<K, V> {
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
        return ObjectUtil.toDouble(o);
    }


    public float getFloat(String name) {
        Object o = get(name);
        if (o == null) {
            return 0;
        }
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
        return ObjectUtil.toInt(o);
    }

    public long getLong(String name) {
        Object o = get(name);
        if (o == null) {
            return 0;
        }
        return ObjectUtil.toLong(o);
    }


    public String getValue(String fname) {
        Object o = get(fname);
        if (o == null) {
            return StringUtil.empty;
        }
        if (o instanceof String) {
            return (String) o;
        }
        return ObjectUtil.toString(o);
    }

    /**
     * @param name 字段
     * @return 得到boolean 类型
     */
    public boolean getBoolean(String name) {
        return ObjectUtil.toBoolean(get(name));
    }


    /**
     * @param name      字段
     * @param dateFormat 日期格式
     * @return 得到Date类型String
     */
    public String getDateString(String name, String dateFormat) {
        Object o = get(name);
        if (o == null) {
            return StringUtil.empty;
        }
        if (o instanceof Date) {
            Date date = (Date) o;
            return DateUtil.toString(date, dateFormat);
        }
        if (o instanceof String) {
            String dateStr = (String) o;
            return DateUtil.toString(StringUtil.getDate(dateStr), dateFormat);
        }
        return ObjectUtil.toString(o);
    }

    /**
     * @param name 字段
     * @return 得到Date类型
     */
    public Date getDate(String name) {
        Object o = get(name);
        if (o == null) {
            return null;
        }
        return ObjectUtil.toDate(o);
    }

    public String getValue(String name, int length) {
        return getValue(name, length, false, StringUtil.empty);
    }

    public String getValue(String name, int length, boolean nohtml) {
        return getValue(name, length, nohtml, StringUtil.empty);
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
/*
    public static void main(String[] args) {
        CaseInsensitiveMap<String,Object> test =  new CaseInsensitiveMap<>();
        test.put("aaA","123");
        test.put("aBc","456");
        DataMap<String,Object> rs = new DataMap<>();
        rs.putAll(test);

        System.out.println(test.get("aaA"));
        System.out.println(test.get("ABC"));
        System.out.println(rs instanceof Map);

    }*/
}