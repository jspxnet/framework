/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sioc.util;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sioc.type.*;
import com.github.jspxnet.sioc.tag.*;
import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-3-22
 * Time: 20:17:48
 * 负责类的比较，转换
 */
@Slf4j
public final class TypeUtil {

    private TypeUtil() {
    }

    final private static Map<String, TypeSerializer> TYPE_MAP = new HashMap<>();

    public static final String[] BASE_TYPE = {"int", "integer", "BigInteger", "long", "bool", "boolean", "float",  "BigDecimal", "date", "double", "string", "ref", "map"};


    final public static Map<String,String> CODE_TYPE_MAP = new HashMap<>();
    static{
        CODE_TYPE_MAP.put("int","int");
        CODE_TYPE_MAP.put("integer","integer");
        CODE_TYPE_MAP.put("BigInteger","BigInteger");
        CODE_TYPE_MAP.put("long","long");
        CODE_TYPE_MAP.put("bool","boolean");
        CODE_TYPE_MAP.put("float","float");
        CODE_TYPE_MAP.put("BigDecimal","BigDecimal");
        CODE_TYPE_MAP.put("bigDecimal","BigDecimal");
        CODE_TYPE_MAP.put("date","Date");
        CODE_TYPE_MAP.put("double","double");
        CODE_TYPE_MAP.put("string","String");
        CODE_TYPE_MAP.put("map","Map");
    }


    static {

        TypeSerializer typeSerializer = new BooleanXmlType();
        TYPE_MAP.put(boolean.class.getName(), typeSerializer);
        TYPE_MAP.put(Boolean.class.getName(), typeSerializer);
        TYPE_MAP.put("bool", typeSerializer);
        TYPE_MAP.put("boolean", typeSerializer);

        typeSerializer = new IntXmlType();
        TYPE_MAP.put(int.class.getName(), typeSerializer);
        TYPE_MAP.put(Integer.class.getName(), typeSerializer);
        TYPE_MAP.put("int", typeSerializer);

        typeSerializer = new BigIntegerXmlType();
        TYPE_MAP.put(BigInteger.class.getName(), typeSerializer);
        TYPE_MAP.put("BigInteger", typeSerializer);

        typeSerializer = new LongXmlType();
        TYPE_MAP.put(long.class.getName(), typeSerializer);
        TYPE_MAP.put(Long.class.getName(), typeSerializer);
        TYPE_MAP.put("long", typeSerializer);

        typeSerializer = new FloatXmlType();
        TYPE_MAP.put(float.class.getName(), typeSerializer);
        TYPE_MAP.put(Float.class.getName(), typeSerializer);
        TYPE_MAP.put("float", typeSerializer);

        typeSerializer = new DoubleXmlType();
        TYPE_MAP.put(Double.class.getName(), typeSerializer);
        TYPE_MAP.put(Double.class.getName(), typeSerializer);
        TYPE_MAP.put("double", typeSerializer);

        typeSerializer = new BigDecimalXmlType();
        TYPE_MAP.put(BigDecimal.class.getName(), typeSerializer);
        TYPE_MAP.put("BigDecimal", typeSerializer);

        typeSerializer = new StringXmlType();
        TYPE_MAP.put(String.class.getName(), typeSerializer);
        TYPE_MAP.put("String", typeSerializer);

        typeSerializer = new DateXmlType();
        TYPE_MAP.put(Date.class.getName(), typeSerializer);
        TYPE_MAP.put(java.sql.Date.class.getName(), typeSerializer);
        TYPE_MAP.put("Date", typeSerializer);

        typeSerializer = new DateXmlType();
        TYPE_MAP.put(java.sql.Timestamp.class.getName(), typeSerializer);
        TYPE_MAP.put("Date", typeSerializer);


        TYPE_MAP.put(Object[].class.getName(), new ArrayXmlType());

        TYPE_MAP.put(int[].class.getName(), new IntArrayXmlType());
        TYPE_MAP.put(Integer[].class.getName(), new IntegerArrayXmlType());

        TYPE_MAP.put(String[].class.getName(), new StringArrayXmlType());

        typeSerializer = new LongArrayXmlType();
        TYPE_MAP.put(long[].class.getName(), typeSerializer);
        TYPE_MAP.put(Long[].class.getName(), typeSerializer);

        typeSerializer = new FloatArrayXmlType();
        TYPE_MAP.put(float[].class.getName(), typeSerializer);
        TYPE_MAP.put(Float[].class.getName(), typeSerializer);
        TYPE_MAP.put("float[]", typeSerializer);

        typeSerializer = new DoubleArrayXmlType();
        TYPE_MAP.put(double[].class.getName(), typeSerializer);
        TYPE_MAP.put(double[].class.getName(), typeSerializer);
        TYPE_MAP.put("double[]", typeSerializer);

        typeSerializer = new BooleanArrayXmlType();
        TYPE_MAP.put(boolean[].class.getName(), typeSerializer);
        TYPE_MAP.put(Boolean[].class.getName(), typeSerializer);
        TYPE_MAP.put("Boolean[]", typeSerializer);

        typeSerializer = new DoubleArrayXmlType();
        TYPE_MAP.put(double[].class.getName(), typeSerializer);
        TYPE_MAP.put(Double[].class.getName(), typeSerializer);
        TYPE_MAP.put("Double[]", typeSerializer);

        TYPE_MAP.put(Date[].class.getName(), new DateArrayXmlType());
        TYPE_MAP.put("Date[]", typeSerializer);

        typeSerializer = new ListXmlType();
        TYPE_MAP.put(List.class.getName(), typeSerializer);
        TYPE_MAP.put(LinkedList.class.getName(), typeSerializer);
        TYPE_MAP.put(ArrayList.class.getName(), typeSerializer);
        TYPE_MAP.put("list", typeSerializer);

        typeSerializer = new MapXmlType();
        TYPE_MAP.put(HashMap.class.getName(), typeSerializer);
        TYPE_MAP.put(Hashtable.class.getName(), typeSerializer);
        TYPE_MAP.put(LinkedHashMap.class.getName(), typeSerializer);
        TYPE_MAP.put("map", typeSerializer);
    }

    /**
     * 创建相应的类型数组
     *
     * @param typeString 类型
     * @param length     长度
     * @return Object[]
     */
    public static Object[] createArray(String typeString, int length) {

        Object[] result = null;
        if ("int".equalsIgnoreCase(typeString) || "integer".equalsIgnoreCase(typeString)) {
            result = new Integer[length];
        } else if ("BigInteger".equalsIgnoreCase(typeString)) {
            result = new BigInteger[length];
        } else if ("long".equalsIgnoreCase(typeString)) {
            result = new Long[length];
        } else if ("float".equalsIgnoreCase(typeString)) {
            result = new Float[length];
        } else if ("double".equalsIgnoreCase(typeString)) {
            result = new Double[length];
        } else if ("BigDecimal".equalsIgnoreCase(typeString)) {
            result = new BigDecimal[length];
        } else if ("bool".equalsIgnoreCase(typeString) || "boolean".equalsIgnoreCase(typeString)) {
            result = new Boolean[length];
        } else if ("date".equalsIgnoreCase(typeString)) {
            result = new Date[length];
        } else if ("type".equalsIgnoreCase(typeString)) {
            result = new Type[length];
        } else if ("string".equalsIgnoreCase(typeString)) {
            result = new String[length];
        } else if ("object".equalsIgnoreCase(typeString)) {
            result = new Object[length];
        } else if (!StringUtil.isNull(typeString)) {
            try {
                Class<?> classType = Class.forName(typeString);
                result = (Object[]) Array.newInstance(classType, length);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("XML配置错误,数组类型:" + typeString);
            }
        }
        return result;
    }

    /**
     * 转换得到java 类型
     * @param typeString 类型字符串
     * @return 转换得到java 类型
     */
    public static Type getJavaType(String typeString) {
        TypeSerializer typeSerializer = TYPE_MAP.get(typeString);
        if (typeSerializer==null && CODE_TYPE_MAP.containsKey(typeString))
        {
            typeSerializer = TYPE_MAP.get(CODE_TYPE_MAP.get(typeString));
        }
        if (typeSerializer!=null)
        {
            return typeSerializer.getJavaType();
        }
        if (typeString.contains(StringUtil.DOT))
        {
            try {
                return ClassUtil.loadClass(typeString);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                log.error("loadClass :{}",typeString,e);
            }
        }
        return Object.class;
    }

    /**
     * @param name   方法名称
     * @param object 对象
     * @return 相应对象转化给系列对象
     * @throws Exception 异常
     */
    public static String getTypeSerializer(String name, Object object) throws Exception {
        if (object == null) {
            return StringUtil.empty;
        }
        TypeSerializer typeSerializer = TYPE_MAP.get(object.getClass().getName());
        if (typeSerializer == null) {
            typeSerializer = new BeanXmlType();
        }
        typeSerializer.setName(name);
        typeSerializer.setValue(object);
        return typeSerializer.getXmlString();
    }

    /**
     * @param type 类型
     * @return 判断是否为基础类型
     */
    public static boolean isBaseType(String type) {
        for (String keys : BASE_TYPE) {
            if (keys.equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }


    /**
     * @param cla 类
     * @return String   得到类型表示字符
     */
    public static String getTypeString(Class<?> cla) {
        TypeSerializer typeSerializer = TYPE_MAP.get(cla.getName());
        if (typeSerializer == null) {
            typeSerializer = new BeanXmlType();
        }
        return typeSerializer.getTypeString();
    }

    /**
     * 类型转换,只支持基本的java类型
     * @param type  类型
     * @param object XML 对象XML
     * @return Object   对象
     */
    public static Object getTypeValue(String type, Object object) {
        if (object == null || JSONObject.NULL.equals(object)||type.contains("JSONObject$Null")||type.endsWith("$Null")) {
            return null;
        }
        String types = type;
        if (type.contains(StringUtil.DOT))
        {
            types = StringUtil.substringAfterLast(type,StringUtil.DOT);
        }
        for (TypeSerializer typeSerializer : TYPE_MAP.values()) {
            if (typeSerializer.getTypeString().equalsIgnoreCase(types)) {
                typeSerializer.setValue(object);
                try {
                    return typeSerializer.getTypeObject();
                } catch (Exception e) {
                    log.error("类型转换错误 type:{} ,object:{},TypeSerializer:{},message:{}", types, object, typeSerializer, e.getMessage());
                }
            }
        }
        return object;
    }


    /**
     * @param element     元素
     * @param namespace   命名空间
     * @param beanFactory 对象工厂
     * @return 得到参数
     * @throws Exception 异常
     */
    public static List<Object> getListValue(ListElement element, String namespace, BeanFactory beanFactory) throws Exception {

        List<TagNode> valueLists = element.getValueList();
        List<Object> result = new ArrayList<>(valueLists.size());
        for (TagNode valueElement : valueLists) {
            ValueElement value = (ValueElement) valueElement;
            if (beanFactory != null && (Sioc.IocRef.equalsIgnoreCase(element.getClassName()) || value.isRef())) {
                result.add(beanFactory.getBean(value.getValue(), namespace));
            } else {
                result.add(TypeUtil.getTypeValue(element.getClassName(), value.getValue()));
            }
        }
        return result;
    }

    /**
     * @param element     元素
     * @param namespace   命名空间
     * @param beanFactory bean工厂
     * @return 得到Array的参数
     * @throws Exception 异常
     *                   解析错误
     */
    public static Object[] getArrayValue(ArrayElement element, String namespace, BeanFactory beanFactory) throws Exception {
        List<TagNode> valueLists = element.getValueList();
        Object[] result = TypeUtil.createArray(element.getClassName(), valueLists.size());
        for (int i = 0; i < valueLists.size(); i++) {
            ValueElement ve = (ValueElement) valueLists.get(i);
            if (ve.isRef() && beanFactory != null) {
                  result[i] = beanFactory.getBean(ve.getValue(), namespace);
            } else {
                result[i] = ve.getValue();
            }
        }
        return result;
    }

    public static Map<String, Object> getMapValue(MapElement element, String namespace, BeanFactory beanFactory) throws Exception {
        Map<String, Object> result = new LinkedHashMap<>();
        List<TagNode> valueLists = element.getValueList();
        for (TagNode xve : valueLists) {
            ValueElement ve = (ValueElement) xve;
            Object value;
            if (ve.isRef() && beanFactory != null) {
                value = beanFactory.getBean(ve.getValue(), namespace);
            } else {
                value = ve.getValue();
            }
            if (value == null) {
                value = StringUtil.empty;
            }
            result.put(ve.getKey(), value);
        }
        return result;
    }


    public static Map<String, Object> getPropertyValue(List<PropertyElement> propertyElements, String namespace) {
        Map<String, Object> paramMap = new LinkedHashMap<>(propertyElements.size());
        //////////////PropertyElement begin
        for (PropertyElement element : propertyElements) {
            if (Sioc.IocRef.equalsIgnoreCase(element.getTypeName())) {
                paramMap.put(element.getName(), Sioc.IocLoad + element.getValue() + Sioc.IocFen + namespace);

            } else {
                paramMap.put(element.getName(), TypeUtil.getTypeValue(element.getTypeName(), element.getValue()));
            }
        }
        propertyElements.clear();
        //////////////PropertyElement end
        return paramMap;
    }

    public static String getRootNamespace(String namespace) {
        if (namespace == null) {
            return StringUtil.empty;
        }
        if (namespace.contains("/")) {
            return StringUtil.substringBefore(namespace, "/");
        }
        return namespace;
    }
/*
    public static List<Object> getOptionList(String option)
    {
        if (StringUtil.isNull(option))
        {
            return new ArrayList<>(0);
        }
        List<Object> result = new ArrayList<>();
        if (StringUtil.isJsonArray(option))
        {
            JSONArray array = new JSONArray(option);
            for (int i=0;i<array.length();i++)
            {
                Object obj = array.get(i);
                if (obj instanceof Map)
                {
                    result.add(ReflectUtil.createDynamicBean((Map)obj));
                } else
                {
                    result.add(obj);
                }
            }
        } else
        {
            StringMap<String,String> stringMap = new StringMap<>();
            stringMap.setKeySplit(StringUtil.COLON);
            stringMap.setLineSplit(StringUtil.SEMICOLON);
            stringMap.setString(option);
            for (String key:stringMap.keySet())
            {
                JSONObject json = new JSONObject();
                json.put("value",key);
                json.put("name",stringMap.getString(key));
                result.add(ReflectUtil.createDynamicBean(json));
            }
        }
        return result;
    }*/
}