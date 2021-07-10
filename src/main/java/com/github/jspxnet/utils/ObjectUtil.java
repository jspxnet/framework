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
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-13
 * Time: 8:50:52
 */
@Slf4j
public class ObjectUtil {

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
            return ((Collection)value).isEmpty();
        }
        if (value.getClass().isArray()) {
            return Array.getLength(value) == 0;
        }
        if (value instanceof Map)
        {
            return ((Map)value).isEmpty();
        }
        return false;
    }

    public static void free(Object value) {
        if (value == null) {
            return;
        }

        if (value instanceof Collection) {
             ((Collection)value).clear();
        }

        if (value instanceof Map)
        {
            ((Map)value).clear();
        }

    }

    public static Object deepCopy(Object oldValue) {
        if (oldValue == null) {
            return null;
        }
        Object newValue = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            oos = new ObjectOutputStream(bout);
            oos.writeObject(oldValue);
            ois = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
            newValue = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error deepCopy  during serialization and deserialization of value:" + oldValue.getClass(), e);
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (ois != null) {
                    ois.close();
                }
            } catch (Exception e) {
                log.error("Error closing Stream", e);
            }
        }
        return newValue;

    }

    public static long getSerializedSize(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (!(obj instanceof Serializable)) {
            return 0;
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bout);
            oos.writeObject(obj);
            return bout.size();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        if (o instanceof List||o instanceof Map) {
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
            return DateUtil.empty;
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
            e.printStackTrace();
        }
        return DateUtil.empty;
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return new java.sql.Timestamp(DateUtil.empty.getTime());
    }

    public static String toString(Object obj) {
        if (obj == null) {
            return StringUtil.empty;
        }
        if (obj instanceof String)
        {
            return (String)obj;
        }
        if (obj instanceof InetAddress) {
            return IpUtil.getIp((InetAddress)obj);
        }
        if (obj instanceof SocketAddress) {
            return IpUtil.getIp((SocketAddress)obj);
        }
        if (obj instanceof Object[]) {
            return new JSONArray(obj).toString();
        }
        if (ClassUtil.isStandardProperty(obj.getClass())) {
            if (ClassUtil.isNumberProperty(obj.getClass()))
            {
                return NumberUtil.getNumberStdFormat(obj+"");
            }
            return obj + "";
        }

        if (obj.getClass().isArray() || obj instanceof List) {
            return new JSONArray(obj).toString();
        }
        if (obj instanceof JSONObject||obj instanceof JSONArray) {
            return obj.toString();
        }
        return new JSONObject(obj).toString();
    }

    /**
     * @param object 置序列化对象
     * @return jdk内置序列化
     */
    public static byte[] getJkdSerialize(Object object) {
        ObjectOutputStream objectOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

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
        ObjectInputStream objectInputStream = null;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(binaryByte);
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

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
        java.beans.XMLDecoder decoder = new java.beans.XMLDecoder(new BufferedInputStream(new ByteArrayInputStream(aXml.getBytes(Environment.defaultEncode))));
        Object obj = decoder.readObject();
        decoder.close();
        return obj;
    }


    /**
     * jdk xml 反系列化
     *
     * @param aObject Converts object transfer xml
     * @return representing an object
     */
    public static String getXml(Object aObject) {
        ByteArrayOutputStream baStream = new ByteArrayOutputStream();
        java.beans.XMLEncoder encoder = new java.beans.XMLEncoder(new BufferedOutputStream(baStream));
        encoder.writeObject(aObject);
        encoder.close();
        try {
            return baStream.toString(Environment.defaultEncode);
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param o 对象
     * @return json 的对象转换
     */
    public static String getJson(Object o) {
        if (ClassUtil.isCollection(o)) {
            return new JSONArray(o).toString();
        }
        return new JSONObject(o).toString();
    }

    /**
     * @param str json字符串
     * @param t   结果对象类型
     * @return json 的对象转换
     */
    public static Object getForJson(String str, Class t) {
        if (StringUtil.isJsonArray(str)) {
            return new JSONArray(str).parseObject(t);
        }
        return new JSONObject(str).parseObject(t);
    }


    /**
     * 12:30  2点飞机
     * method.getModifiers()  返回说明
     * <p>
     * PUBLIC: 1
     * PRIVATE: 2
     * PROTECTED: 4
     * STATIC: 8
     * FINAL: 16
     * SYNCHRONIZED: 32
     * VOLATILE: 64
     * TRANSIENT: 128
     * NATIVE: 256
     * INTERFACE: 512
     * ABSTRACT: 1024
     * STRICT: 2048
     *
     * @param o 对象
     * @return Map 对象的值映射成Map
     */
    public static Map<String, Object> getMap(Object o) {
        if (o == null) {
            return new HashMap<>();
        }
        if (o instanceof Class) {
            return new HashMap<>();
        }
        if (o instanceof AbstractMap) {
            return (Map<String, Object>) o;
        }
        Map<String, Object> valueMap = new TreeMap<>();
        Field[] fields = ClassUtil.getDeclaredFields(o.getClass());
        if (fields != null) {
            for (Field field : fields) {
                try {
                    Object value = BeanUtil.getFieldValue(o, field.getName(),false);
                    valueMap.put(field.getName(), value);
                } catch (Exception e) {
                    log.error(o + "   method=" + field.getName(), e);
                }
            }
        }
        return valueMap;
    }


}