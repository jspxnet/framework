/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.utils;


import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-13
 * Time: 8:50:52
 */
@Slf4j
public final class ObjectUtil {

    private ObjectUtil() {

    }


    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return StringUtil.isEmpty((String) value);
        }
        if (value instanceof Collection) {
            return ((Collection<?>) value).isEmpty();
        }
        if (value instanceof String[]) {
            return ((String[]) value).length == 0;
        }
        if (value.getClass().isArray()) {
            return Array.getLength(value) == 0;
        }
        if (value instanceof Map) {
            return ((Map<?, ?>) value).isEmpty();
        }
        return false;
    }

    public static void free(Object value) {
        if (value == null) {
            return;
        }

        if (value instanceof Collection) {
            ((Collection<?>) value).clear();
        }
        if (value instanceof Map) {
            ((Map<?, ?>) value).clear();
        }
    }

    public static Object deepCopy(Object oldValue) {
        if (oldValue == null) {
            return null;
        }
        Object newValue = null;
        ObjectInputStream ois = null;
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bout);) {
            oos.writeObject(oldValue);
            ois = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
            newValue = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error deepCopy  during serialization and deserialization of value:" + oldValue.getClass(), e);
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (Exception e) {
                log.error("Error closing Stream", e);
            }
        }
        return newValue;

    }

    /**
     * @param obj 对象
     * @return 序列化
     */
    public static long getSerializedSize(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (!(obj instanceof Serializable)) {
            return 0;
        }

        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bout)) {
            oos.writeObject(obj);
            return bout.size();
        } catch (IOException e) {
            //...
        }
        return 0;
    }

    /**
     * @param o 对像
     * @return boolean 逻辑类型
     */
    public static Boolean toBoolean(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Boolean) {
            return (Boolean) o;
        }
        if (o instanceof Number) {
            return ((Number) o).intValue() > 0;
        }
        if (o instanceof String[]) {
            return !ObjectUtil.isEmpty(o);
        }
        if (o instanceof List || o instanceof Map) {
            return !ObjectUtil.isEmpty(o);
        }
        if (o instanceof String) {
            return StringUtil.toBoolean((String) o);
        }
        return true;
    }

    /**
     * @param obj 对象
     * @return 整数
     */
    public static int toInt(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof Boolean) {
            if ((Boolean) obj) {
                return 1;
            } else {
                return 0;
            }
        }
        if (obj.getClass().isAssignableFrom(Integer.class)) {
            return ((Integer) obj);
        }
        if (obj.getClass().isAssignableFrom(Long.class)) {
            return ((Long) obj).intValue();
        }
        if (obj.getClass().isAssignableFrom(Double.class)) {
            return ((Double) obj).intValue();
        }
        if (obj.getClass().isAssignableFrom(Float.class)) {
            return ((Float) obj).intValue();
        }
        if (obj.getClass().isAssignableFrom(BigDecimal.class)) {
            return ((BigDecimal) obj).intValue();
        }
        return StringUtil.toInt(obj.toString());
    }

    /**
     * @param obj 对象
     * @return 长整
     */
    public static long toLong(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj.getClass().isAssignableFrom(Long.class)) {
            return ((Long) obj);
        }
        if (obj.getClass().isAssignableFrom(Double.class)) {
            return ((Double) obj).longValue();
        }
        if (obj.getClass().isAssignableFrom(Integer.class)) {
            return ((Integer) obj).longValue();
        }
        if (obj.getClass().isAssignableFrom(BigDecimal.class)) {
            return ((BigDecimal) obj).longValue();
        }
        if (obj.getClass().isAssignableFrom(Date.class)) {
            Date d = (Date) obj;
            return d.getTime();
        }
        return StringUtil.toLong(obj.toString());
    }

    /**
     * @param obj 对象
     * @return d单进度
     */
    public static float toFloat(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj.getClass().isAssignableFrom(Float.class)) {
            return ((Float) obj);
        }
        if (obj.getClass().isAssignableFrom(Double.class)) {
            return ((Double) obj).floatValue();
        }
        if (obj.getClass().isAssignableFrom(Long.class)) {
            return ((Long) obj).floatValue();
        }
        if (obj.getClass().isAssignableFrom(Integer.class)) {
            return ((Integer) obj).floatValue();
        }
        if (obj.getClass().isAssignableFrom(BigDecimal.class)) {
            return ((BigDecimal) obj).floatValue();
        }
        return StringUtil.toFloat(obj.toString());
    }

    /**
     * @param obj 对象
     * @return 双精度
     */
    public static double toDouble(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj.getClass().isAssignableFrom(Long.class)) {
            return ((Long) obj).doubleValue();
        }
        if (obj.getClass().isAssignableFrom(Integer.class)) {
            return ((Integer) obj).doubleValue();
        }
        if (obj.getClass().isAssignableFrom(Float.class)) {
            return ((Float) obj).doubleValue();
        }
        if (obj.getClass().isAssignableFrom(BigDecimal.class)) {
            return ((BigDecimal) obj).doubleValue();
        }
        return StringUtil.toDouble(obj.toString());
    }

    public static Date toDate(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj.getClass().isAssignableFrom(Date.class)) {
            return (Date) obj;
        }
        if (obj.getClass().isAssignableFrom(Long.class)) {
            return new Date(((Long) obj));
        }
        if (obj.getClass().isAssignableFrom(java.sql.Date.class)) {
            return new Date(((java.sql.Date) obj).getTime());
        }
        try {
            return StringUtil.getDate(obj.toString());
        } catch (Exception e) {
            log.error("toDate", e);
        }
        return null;
    }

    public static java.sql.Date toSqlDate(Object obj) {
        if (obj == null) {
            return new java.sql.Date(DateUtil.empty.getTime());
        }
        if (obj.getClass().isAssignableFrom(java.sql.Date.class)) {
            return ((java.sql.Date) obj);
        }
        if (obj.getClass().isAssignableFrom(Long.class)) {
            return new java.sql.Date(((Long) obj));
        }
        if (obj.getClass().isAssignableFrom(Date.class)) {
            return new java.sql.Date(((Date) obj).getTime());
        }
        try {
            Date date = StringUtil.getDate(obj.toString());
            return new java.sql.Date(date.getTime());
        } catch (Exception e) {
            log.error("toSqlDate", e);
        }
        return new java.sql.Date(DateUtil.empty.getTime());
    }

    public static java.sql.Timestamp toSqlTimestamp(Object obj) {
        if (obj == null) {
            return new java.sql.Timestamp(DateUtil.empty.getTime());
        }

        if (obj.getClass().isAssignableFrom(java.sql.Timestamp.class)) {
            return ((java.sql.Timestamp) obj);
        }
        if (obj.getClass().isAssignableFrom(Long.class)) {
            return new java.sql.Timestamp(((Long) obj));
        }
        if (obj.getClass().isAssignableFrom(Date.class)) {
            return new java.sql.Timestamp(((Date) obj).getTime());
        }
        try {
            Date date = StringUtil.getDate(obj.toString());
            return new java.sql.Timestamp(date.getTime());
        } catch (Exception e) {
            log.error("toSqlTimestamp", e);
        }
        return new java.sql.Timestamp(DateUtil.empty.getTime());
    }

    public static java.sql.Time toSqlTime(Object obj) {
        if (obj == null) {
            return new java.sql.Time(DateUtil.empty.getTime());
        }
        if (obj.getClass().isAssignableFrom(java.sql.Time.class)) {
            return ((java.sql.Time) obj);
        }

        if (obj.getClass().isAssignableFrom(Long.class)) {
            return new java.sql.Time(((Long) obj));
        }
        if (obj.getClass().isAssignableFrom(Date.class)) {
            return new java.sql.Time(((Date) obj).getTime());
        }
        try {
            Date date = StringUtil.getDate(obj.toString());
            return new java.sql.Time(date.getTime());
        } catch (Exception e) {
            log.error("toSqlTime", e);
        }
        return new java.sql.Time(DateUtil.empty.getTime());
    }

    /**
     * 调试用,格式化后的字符串
     *
     * @param obj 对象转字符串
     * @return 字符串
     */
    public static String toFormatString(Object obj) {
        return toString(obj, 4);
    }

    /**
     * 调试用,一行方式
     *
     * @param obj 对象转字符串
     * @return 字符串
     */
    public static String toString(Object obj) {
        return toString(obj, 0);
    }

    /**
     * @param obj 对象转字符串
     * @param tab 格式化间隔
     * @return 字符串
     */
    public static String toString(Object obj, int tab) {
        if (obj == null) {
            return StringUtil.empty;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof Number) {
            return NumberUtil.toString(obj);
        }
        if (obj instanceof InetAddress) {
            return IpUtil.getIp((InetAddress) obj);
        }
        if (obj instanceof SocketAddress) {
            return IpUtil.getIp((SocketAddress) obj);
        }
        if (obj instanceof Object[]) {
            return new JSONArray(obj).toString(tab);
        }
        if (ClassUtil.isStandardProperty(obj.getClass())) {
            if (ClassUtil.isNumberProperty(obj.getClass())) {
                return NumberUtil.getNumberStdFormat(obj + "");
            }
            return obj + "";
        }
        if (obj instanceof JSONArray) {
            return ((JSONArray) obj).toString(tab);
        }
        if (obj instanceof JSONObject) {
            return ((JSONObject) obj).toString(tab);
        }
        if (obj.getClass().isArray() || obj instanceof List) {
            return new JSONArray(obj).toString(tab);
        }
        return new JSONObject(obj).toString(tab);
    }

    /**
     * @param object 置序列化对象
     * @return jdk内置序列化
     */
    public static byte[] getJkdSerialize(Object object) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        ) {
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.error("getJkdSerialize", e);
        }
        return null;
    }

    /**
     * jdk内置序列化 反
     *
     * @param binaryByte 二进制对象
     * @return jdk内置序列化 反
     */
    public static Object getJdkUnSerizlize(byte[] binaryByte) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(binaryByte);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        ) {
            return objectInputStream.readObject();
        } catch (Exception e) {
            log.error("getJdkUnSerizlize", e);
        }
        return null;
    }


    /**
     * jdk  xml 序列化
     *
     * @param aXml Creates object from xml
     * @return object from xml
     * @throws UnsupportedEncodingException 异常
     */
    public static Object getForXml(String aXml) throws UnsupportedEncodingException {
        try (java.beans.XMLDecoder decoder = new java.beans.XMLDecoder(new BufferedInputStream(new ByteArrayInputStream(aXml.getBytes(Environment.defaultEncode))))) {
            return decoder.readObject();
        }
    }

    /**
     * jdk xml 反系列化
     *
     * @param aObject Converts object transfer xml
     * @return representing an object
     */
    public static String getXml(Object aObject) {
        ByteArrayOutputStream baStream = new ByteArrayOutputStream();
        try (java.beans.XMLEncoder encoder = new java.beans.XMLEncoder(new BufferedOutputStream(new ByteArrayOutputStream()));) {
            encoder.writeObject(aObject);
            return baStream.toString(Environment.defaultEncode);
        } catch (UnsupportedEncodingException e) {
            log.error("getXml", e);
        }
        return null;
    }

    /**
     * @param o 对象
     * @return json 的对象转换
     */
    public static String getJson(Object o) {
        if (ClassUtil.isArrayType(o) || ClassUtil.isCollection(o)) {
            return new JSONArray(o).toString();
        }
        return new JSONObject(o).toString();
    }

    /**
     * @param str json字符串
     * @param t   结果对象类型
     * @return json 的对象转换
     */
    public static Object getForJson(String str, Class<?> t) {
        if (StringUtil.isJsonArray(str)) {
            return new JSONArray(str).parseObject(t);
        }
        return new JSONObject(str).parseObject(t);
    }

    /**
     * @param o 对象
     * @return Map 对象的值映射成Map
     */
    public static Map<String, Object> getMap(Object o) {
        if (o == null) {
            return new HashMap<>(0);
        }
        if (o instanceof Class) {
            return new HashMap<>(0);
        }
        if (o instanceof AbstractMap) {
            return (Map<String, Object>) o;
        }
        Map<String, Object> valueMap = new TreeMap<>();
        Field[] fields = ClassUtil.getDeclaredFields(o.getClass());
        if (fields != null) {
            for (Field field : fields) {
                try {
                    Object value = BeanUtil.getFieldValue(o, field.getName(), false);
                    valueMap.put(field.getName(), value);
                } catch (Exception e) {
                    log.error(o + "   method=" + field.getName(), e);
                }
            }
        }
        return valueMap;
    }

    /**
     *
     * 主要用于jxls 转换，jxls只支持map参数
     * @param o 对象
     * @return 完全的map对象
     */
    public static Map<String, Object> getFullMap(Object o) {
        if (o == null) {
            return new HashMap<>(0);
        }
        if (o instanceof Class) {
            return new HashMap<>(0);
        }
        if (o instanceof Map) {
            return (Map<String, Object>) o;
        }
        Map<String, Object> valueMap = new HashMap<>();
        Field[] fields = ClassUtil.getDeclaredFields(o.getClass());
        if (fields != null) {
            for (Field field : fields) {
                try {
                    Object value = BeanUtil.getFieldValue(o, field.getName(), false);
                    if (value==null)
                    {
                        valueMap.put(field.getName(), null);
                    } else
                    if (ClassUtil.isArrayType(value)) {
                        List<Object> list = new ArrayList<>();
                        int len = Array.getLength(value);
                        for (int i = 0; i < len; i++) {
                            Object v = Array.get(value, i);
                            if (!ClassUtil.isStandardType(v.getClass()) && !(v instanceof Map))
                            {
                               list.add(getFullMap(v));
                            } else
                            {
                               list.add(v);
                            }
                        }
                        valueMap.put(field.getName(), list);
                    } else if (ClassUtil.isCollection(value)) {
                        Collection<?> collection = (Collection<?>) value;
                        List<Object> list = new ArrayList<>();
                        for (Object v : collection) {
                            if (!ClassUtil.isStandardType(v.getClass()) && !(v instanceof Map))
                            {
                               list.add(getFullMap(v));
                            } else
                            {
                               list.add(v);
                            }
                        }
                        valueMap.put(field.getName(), list);
                    }
                    else
                    {
                        if (!ClassUtil.isStandardType(value.getClass()) && !(value instanceof Map))
                        {
                            valueMap.put(field.getName(),getFullMap(value));
                        } else
                        {
                            valueMap.put(field.getName(),value);
                        }
                    }
                } catch (Exception e) {
                    log.error(o + "   method=" + field.getName(), e);
                }
            }
        }
        return valueMap;
    }

    /**
     * 比较两个对象相同
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 是否相等
     */
    public static boolean compare(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        }
        if (obj1 == obj2) {
            return true;
        } else if (obj1 instanceof Collection && obj2 instanceof Collection) {
            JSONArray array1 = new JSONArray(obj1);
            JSONArray array2 = new JSONArray(obj2);
            return array1.toString().equals(array2.toString());
        } else if (obj1 instanceof Map && obj2 instanceof Map) {
            JSONObject json1 = new JSONObject(obj1);
            JSONObject json2 = new JSONObject(obj2);
            return json1.toString().equals(json2.toString());
        } else {
            return compareValue(obj1, obj2);
        }
    }


    private static boolean compareValue(Object value1, Object value2) {
        if (!(value1 instanceof Boolean) && !(value2 instanceof Boolean)) {
            if (isEmptyObject(value1) && isEmptyObject(value2)) {
                return true;
            } else if (!isEmptyObject(value1) && !isEmptyObject(value2)) {
                if (value1 instanceof Byte) {
                    return ((Byte) value1).compareTo((Byte) value2) == 0;
                } else if (value1 instanceof Short) {
                    return ((Short) value1).compareTo((Short) value2) == 0;
                } else if (value1 instanceof Integer) {
                    return ((Integer) value1).compareTo((Integer) value2) == 0;
                } else if (value1 instanceof Long) {
                    return ((Long) value1).compareTo((Long) value2) == 0;
                } else if (value1 instanceof Float) {
                    return ((Float) value1).compareTo((Float) value2) == 0;
                } else if (value1 instanceof Double) {
                    return ((Double) value1).compareTo((Double) value2) == 0;
                } else if (value1 instanceof BigDecimal) {
                    return ((BigDecimal) value1).compareTo((BigDecimal) value2) == 0;
                } else if (value1 instanceof String) {
                    return (value1).equals(value2);
                } else if (value1 instanceof Date) {
                    return DateUtil.dayEquals((Date) value1, (Date) value2);
                } else if (value1 instanceof Time) {
                    return ((Time) value1).compareTo((Time) value2) == 0;
                } else if (value1 instanceof Timestamp) {
                    return DateUtil.dayEquals((Date) value1, (Date) value2);
                } else if (value1.getClass().isArray() && value2.getClass().isArray()) {
                    Object[] arrValue1 = (Object[]) value1;
                    Object[] arrValue2 = (Object[]) value2;
                    if (arrValue1.length != arrValue2.length) {
                        return false;
                    } else {
                        boolean retValue = true;
                        for (int i = 0; i < arrValue1.length && retValue; ++i) {
                            retValue = compareValue(arrValue1[i], arrValue2[i]);
                        }

                        return retValue;
                    }
                } else {
                    return value1.equals(value2);
                }
            } else {
                return false;
            }
        } else {
            Boolean bool1 = value1 == null ? Boolean.FALSE : ObjectUtil.toBoolean(value1);
            Boolean bool2 = value2 == null ? Boolean.FALSE : (Boolean) value2;
            return bool1.equals(bool2);
        }
    }

    /**
     *
     * @param s 对象
     * @return 盘点是否为空
     */
    private static boolean isEmptyObject(Object s) {
        if (s instanceof String) {
            return ((String) s).trim().isEmpty();
        } else {
            return s == null || s == JSONObject.NULL;
        }
    }

    /**
     *
     * @param o 对象
     * @return 判断对象是字符串并且是空
     */
    public static boolean isNullOrWhiteSpace(Object o) {
        if (o==null)
        {
            return true;
        }
        if (!(o instanceof String))
        {
            return false;
        }
        return StringUtil.isNullOrWhiteSpace((String)o);
    }


}