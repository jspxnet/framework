/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.io.zip;

/**
 * Copyright: Copyright (c) 2002-2003
 * Company: JavaResearch(http://www.javaresearch.org)
 * 最后更新日期:2003年3月3日
 *
 * @author Cherami, Barney, Brain
 * @version 0.8
 * 陈原修改版
 */

import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.*;
import java.util.zip.*;

/**
 * 压缩文件的一个记录项的抽象。
 * 根据区域设置其时间以及文件大小的表示方式，记录的属性用一个HashMap维护，这样便于进行属性的扩展。
 * 这个类实际上是一个工具类，将一个ZipEntry的信息进行解析。
 */

public class ZipFileRecord {
    public static final int NAME = 0;
    public static final int SIZE = 1;
    public static final int PACK = 2;
    public static final int SCALE = 3;
    public static final int TIME = 4;
    public static final int TYPE = 5;
    public static final int PATH = 6;
    public final ZipEntry entry;

    private Map properties;
    private static final int COUNT = 7;
    private long size = 0;
    private long pack = 0;
    private float scale = 0.0f;
    private Date modifyTime;

    public static final String[] columnNames = new String[COUNT];
    public static final Class[] columnClasses = new Class[COUNT];

    static {
        columnNames[NAME] = "name";
        columnNames[SIZE] = "size";
        columnNames[PACK] = "pack";
        columnNames[SCALE] = "scale";
        columnNames[TIME] = "time";
        columnNames[TYPE] = "type";
        columnNames[PATH] = "path";
        columnClasses[NAME] = String.class;
        columnClasses[SIZE] = Long.class;
        columnClasses[PACK] = Long.class;
        columnClasses[SCALE] = Float.class;
        columnClasses[TIME] = Date.class;
        columnClasses[TYPE] = String.class;
        columnClasses[PATH] = String.class;
    }

    /**
     * 构造方法，在完成初始化的同时读取记录的所有属性。
     *
     * @param entry 原始信息
     */
    public ZipFileRecord(ZipEntry entry) {
        this.entry = entry;
        properties = new HashMap();
        getFileProperties();
        getDateTimeProperties();
        getSizeProperties();
    }

    /**
     * 记录的字符串表示。
     *
     * @return 记录的字符串表示
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ZipFileRecord:[");
        sb.append(properties.get("name")).append(",");
        sb.append(properties.get("size")).append(",");
        sb.append(properties.get("pack")).append(",");
        sb.append(properties.get("scale")).append(",");
        sb.append(properties.get("time")).append(",");
        sb.append(properties.get("type")).append(",");
        sb.append(properties.get("path"));
        sb.append("]");
        return sb.toString();
    }

    /**
     * 解析文件相关的属性：文件名，扩展名，根目录以及路径。
     */
    @SuppressWarnings("unchecked")
    private void getFileProperties() {
        String fileName = entry.getName();
        properties.put(columnNames[NAME], FileUtil.getNamePart(fileName));
        properties.put(columnNames[PATH], FileUtil.getPathPart(fileName));
        properties.put(columnNames[TYPE], FileUtil.getTypePart(fileName));
    }

    /**
     * 解析最后一次修改时间。
     */
    @SuppressWarnings("unchecked")
    private void getDateTimeProperties() {
        modifyTime = new Date(entry.getTime());
        properties.put(columnNames[TIME], modifyTime);
    }

    /**
     * 解析大小属性。
     */
    @SuppressWarnings("unchecked")
    private void getSizeProperties() {
        size = entry.getSize();
        properties.put(columnNames[SIZE], size);
        pack = entry.getCompressedSize();
        properties.put(columnNames[PACK], pack);
        if (size == -1 || pack == -1) {
            properties.put(columnNames[SCALE], Float.NaN);
        } else {
            if (size == 0) {
                scale = 1.0f;
            } else {
                scale = (float) pack / (float) size;
            }
            properties.put(columnNames[SCALE], scale);
        }
    }

    /**
     * 得到原始的大小。
     *
     * @return 记录的大小
     */
    public long getSize() {
        return size;
    }

    /**
     * 得到压缩后的大小。
     *
     * @return 压缩后的大小
     */
    public long getCompressedSize() {
        return pack;
    }

    /**
     * 得到压缩比。
     *
     * @return 压缩比
     */
    public float getScale() {
        return scale;
    }

    /**
     * 得到最后修改日期。
     *
     * @return 最后修改日期
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * 判断两个记录是否是同一个记录。
     *
     * @param obj 要进行比较的另一个对象
     * @return 如果是同一类型并且私有entry成员相等则返回true，其他情况都返回false
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof ZipFileRecord && entry.equals(((ZipFileRecord) obj).entry);
    }

    /**
     * 返回记录的hashcode，主要是为了和equals方法匹配。
     * 如果equals方法返回true，那么本方法返回的hashcode应该相同。
     *
     * @return 记录的hashcode。
     */
    @Override
    public int hashCode() {
        return entry.hashCode();
    }

    /**
     * 得到数据的列数。
     *
     * @return 数据的列数
     */
    public int getCount() {
        return COUNT;
    }

    /**
     * 得到指定列的值。
     *
     * @param column 列
     * @return 指定列的值
     */
    public Object get(int column) {
        if (properties.containsKey(columnNames[column])) {
            return properties.get(columnNames[column]);
        } else {
            return StringUtil.empty;
        }
    }


    /**
     * 得到指定列的列名。
     *
     * @param column 列
     * @return 对应的列名
     */
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /**
     * 得到指定列的索引。
     * 不存在时返回-1。
     *
     * @param columnName 列名
     * @return 对应的索引
     */
    public int getColumnIndex(String columnName) {
        return ArrayUtil.indexOf(columnNames, columnName);
    }

    /**
     * 得到全部列名的数组，按照位置排序。
     *
     * @return 全部列名的数组
     */
    public String[] getColumnNames() {
        return columnNames;
    }

    /**
     * 得到指定列所属的类。
     *
     * @param column 列
     * @return 列所属的类
     */
    public static Class getColumnClass(int column) {
        return columnClasses[column];
    }

    /**
     * 根据比较关键字进行比较
     *
     * @param other 要比较的另一个对象
     * @param key   比较关键字
     * @return 根据比较关键字进行比较的结果，大于时返回值大于0，相等时返回0，小于时返回值小于0
     */
    public int compareTo(Object other, int key) {
        return compareTo((ZipFileRecord) other, key);
    }

    /**
     * 根据比较关键字进行比较
     *
     * @param other 要比较的另一个对象
     * @param key   比较关键字
     * @return 根据比较关键字进行比较的结果，大于时返回值大于0，相等时返回0，小于时返回值小于0
     */

    public int compareTo(ZipFileRecord other, int key) {
        int result = 0;
        switch (key) {
            case NAME:
            case TYPE:
            case PATH:
                result = ((String) get(key)).compareToIgnoreCase((String) other.get(key));
                break;
            case SIZE:
            case PACK:
                result = ((Long) get(key)).compareTo((Long) other.get(key));
                break;
            case SCALE:
                result = ((Float) get(key)).compareTo((Float) other.get(key));
                break;
            case TIME:
                result = ((Date) get(key)).compareTo((Date) other.get(key));
                break;
            default:
                result = 0;
        }
        /*if (result==0) {
          return compareAll(other);
             }*/
        return result;
    }

    /**
     * 以name为比较主键进行比较
     *
     * @param o 另一个对象
     * @return 根据比较关键字进行比较的结果，大于时返回值大于0，相等时返回0，小于时返回值小于0
     */
    public int compareTo(Object o) {
        return compareAll((ZipFileRecord) o);
    }

    /**
     * 以name为比较主键进行比较
     *
     * @param o 另一个对象
     * @return 根据比较关键字进行比较的结果，大于时返回值大于0，相等时返回0，小于时返回值小于0
     */
    public int compareTo(ZipFileRecord o) {
        return compareAll(o);
    }

    private int compareAll(ZipFileRecord o) {
        int result = 0;
        result = ((String) get(NAME)).compareTo((String) o.get(NAME));
        if (result != 0) {
            return result;
        }
        result = ((String) get(SIZE)).compareTo((String) o.get(SIZE));
        if (result != 0) {
            return result;
        }
        result = ((String) get(PACK)).compareTo((String) o.get(PACK));
        if (result != 0) {
            return result;
        }
        result = ((String) get(SCALE)).compareTo((String) o.get(SCALE));
        if (result != 0) {
            return result;
        }
        result = ((String) get(TIME)).compareTo((String) o.get(TIME));
        if (result != 0) {
            return result;
        }
        result = ((String) get(TYPE)).compareTo((String) o.get(TYPE));
        if (result != 0) {
            return result;
        }
        result = ((String) get(PATH)).compareTo((String) o.get(PATH));
        return result;
    }
}