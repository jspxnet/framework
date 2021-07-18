package com.github.jspxnet.json;


import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.util.TypeReference;
import com.github.jspxnet.utils.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.*;

/*
Information:java: 有关详细信息, 请使用 -Xlint:unchecked 重新编译。
ConcurrentHashMap
 */
public class JSONObject extends LinkedHashMap<String, Object> {
    private static final Logger log = LoggerFactory.getLogger(JSONObject.class);
    final static private String KEY_DATA = "data";
    //final static private String KEY_ROOT = "root";

    static public String FULL_ST_FORMAT = DateUtil.FULL_ST_FORMAT;
    static public String CLASS_NAME = "@class";
    static public String BIN_DATA_START = "data:stream/byte;base64,";


    /**
     * JSONObject.NULL is equivalent transfer the value that JavaScript calls null,
     * whilst Java's null is equivalent transfer the value that JavaScript calls
     * undefined.
     */
    private static final class Null {

        /**
         * There is only intended transfer be a single instance of the NULL object,
         * so the clone method returns itself.
         *
         * @return NULL.
         */
        @Override
        protected final Object clone() {
            return this;
        }


        /**
         * A Null object is equal transfer the null value and transfer itself.
         *
         * @param object An object transfer testaio for nullness.
         * @return true if the object parameter is the JSONObject.NULL object
         * or null.
         */
        @Override
        public boolean equals(Object object) {
            return object == null || object == this;
        }

        /**
         * A Null object is equal transfer the null value and transfer itself.
         *
         * @return always returns 0.
         */
        @Override
        public int hashCode() {
            return 0;
        }

        /**
         * Get the "null" string value.
         *
         * @return The string "null".
         */
        @Override
        public String toString() {
            return "null";
        }
    }

    /**
     * Regular Expression Pattern that matches JSON Numbers. This is primarily used for
     * output transfer guarantee that we are always writing valid JSON.
     */
    //static final Pattern NUMBER_PATTERN = Pattern.compile("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?");

    /**
     * It is sometimes more convenient and less ambiguous transfer have a
     * {@code null  } object than transfer use Java's {@code null  } value.
     * [code]JSONObject.NULL.equals(null) } returns [code]true } .
     * [code]JSONObject.NULL.toString() } returns [code]"null" } .
     */
    public static final Object NULL = new Null();


    /**
     * Construct an empty JSONObject.
     */
    public JSONObject() {

    }


    /**
     * Construct a JSONObject from a subset of another JSONObject.
     * An array of strings is used transfer identify the keys that should be copied.
     * Missing keys are ignored.
     *
     * @param jo    A JSONObject.
     * @param names An array of strings.
     * @throws JSONException If a value is a non-finite number or if a name is duplicated.
     */
    public JSONObject(JSONObject jo, String[] names) throws JSONException {
        for (String name : names) {
            putOnce(name, jo.get(name));
        }
    }


    /**
     * Construct a JSONObject from a JSONTokener.
     *
     * @param x A JSONTokener object containing the source string.
     * @throws JSONException If there is a syntax error in the source string
     *                       or a duplicated key.
     */
    public JSONObject(JSONTokener x) throws JSONException {
        char c;
        String key;

        if (x.nextClean() != '{') {
            throw x.syntaxError("A JSONObject text must begin with '{'");
        }
        for (; ; ) {
            c = x.nextClean();
            switch (c) {
                case 0:
                    throw x.syntaxError("A JSONObject text must end with '}'");
                case '}':
                    return;
                default:
                    x.back();
                    key = x.nextValue().toString();
            }

            /*
             * The key is followed by ':'. We will also tolerate '=' or '=>'.
             */

            c = x.nextClean();
            if (c != ':') {
                throw x.syntaxError("Expected a ':' after a key");
            }
            putOnce(key, x.nextValue());

            /*
             * Pairs are separated by ','. We will also tolerate ';'.
             */

            switch (x.nextClean()) {
                case ';':
                case ',':
                    if (x.nextClean() == '}') {
                        return;
                    }
                    x.back();
                    break;
                case '}':
                    return;
                default:
                    throw x.syntaxError("Expected a ',' or '}'");
            }
        }
    }


    /**
     * Construct a JSONObject from a super.
     *
     * @param map A map object that can be used transfer initialize the contents of
     *            the JSONObject.
     */
    public JSONObject(Map<Object, Object> map) {
        if (map==null||map.isEmpty())
        {
            return;
        }
        for (Object key:map.keySet())
        {
            super.put(ObjectUtil.toString(key),map.get(key));
        }
    }

    /**
     * Construct a JSONObject from a super.
     * <p>
     * Note: Use this constructor when the map contains {@code  <key,bean>}
     *
     * @param map               - A map with Key-Bean data.
     * @param includeSuperClass - Tell whether transfer include the super class properties.
     */
    public JSONObject(Map<String, Object> map, boolean includeSuperClass) {
        if (map != null) {
            for (Map.Entry<String, Object> e : map.entrySet()) {
                if (ClassUtil.isStandardProperty(e.getValue().getClass())) {
                    super.put(e.getKey(), e.getValue());
                } else {
                    super.put(e.getKey(), new JSONObject(e.getValue(), includeSuperClass));
                }
            }
        }
    }


    /**
     * Construct a JSONObject from an Object using bean getters.
     * It reflects on all of the public methods of the object.
     * For each of the methods with no parameters and a name starting
     * with [code]"get" } or [code]"is" } followed by an uppercase letter,
     * the method is invoked, and a key and the value returned from the getter method
     * are put into the new JSONObject.
     * <p>
     * The key is formed by removing the [code]"get" } or [code]"is" } prefix. If the second remaining
     * character is not upper case, then the first
     * character is converted transfer lower case.
     * <p>
     * For example, if an object has a method named [code]"getName" } , and
     * if the result of calling [code]object.getName() } is [code]"Larry Fine" } ,
     * then the JSONObject will contain [code]"name": "Larry Fine" } .
     *
     * @param bean An object that has getter methods that should be used
     *             transfer make a JSONObject.
     */
    public JSONObject(Object bean) {
        this(bean, true);
    }

    private boolean isValidMethodName(String name) {
        return !"getClass".equals(name) && !"getDeclaringClass".equals(name);
    }

    /**
     *
     * @param bean 对象
     * @param includeSuperClass 是否包含子对象
     */
    public JSONObject(Object bean,boolean includeSuperClass)
    {
        this(bean,null, includeSuperClass,null);
    }

    public JSONObject(Object bean,JSONObject dataField)
    {
        this(bean,null, true,dataField);
    }
    /**
     *
     * @param bean 对象
     * @param includeSuperClass 是否包含子对象
     * @param dataField 显示字段
     *
     *  {
     *      data:[company,name,addresss,user]
     *      user:[name,sex]
     *  }
     */
    public JSONObject(Object bean,boolean includeSuperClass,JSONObject dataField)
    {
        this(bean,null, includeSuperClass,dataField);
    }
    //保存对象格式化
    private Class lass;
    private JSONObject dataField = null;

    /**
     *
     * @param bean 对象
     * @param showField 要显示的字段,设计了让前端传入
     * @param includeSuperClass 是否包含子对象
     * @param dataField 显示字段
     */
    public JSONObject(Object bean, String[] showField,boolean includeSuperClass,JSONObject dataField)
    {
        this.dataField = dataField;
        if (bean == null) {
            return;
        }
        if (bean instanceof Class) {
            super.put("class", ((Class) bean).getName());
            return;
        }

        if (ClassUtil.isProxy(bean.getClass())) {
            bean = ReflectUtil.getValueMap(bean);
        }
        if (bean instanceof Map) {
            Map map = (Map) bean;
            for (Object key : map.keySet()) {
                //判断是否需要返回
                if (!ArrayUtil.isEmpty(showField)&&!ArrayUtil.contains(showField,key))
                {
                    continue;
                }
                Object v = map.get(key);
                if (v==null)
                {
                    super.put((String) key, null);
                } else
                if (ClassUtil.isCollection(v))
                {
                    super.put((String) key, new JSONArray(v,includeSuperClass));
                } else
                if (!ClassUtil.isStandardProperty(v.getClass())&&!(v instanceof JSONObject)) {
                    super.put((String) key, new JSONObject(v, includeSuperClass,dataField));
                } else {
                    super.put((String) key, v);
                }
            }
            return;
        }

        if (bean.getClass().isArray() || bean instanceof Collection) {
            //正常使用不应该使用JSONObject，应该直接使用JSONArray
            super.put(KEY_DATA, new JSONArray(bean,includeSuperClass,KEY_DATA, dataField));
            return;
        }
        lass = bean.getClass();
        if (!isNull(bean)) {
            put(CLASS_NAME, lass.getName());
        }

        Field[] fields = ClassUtil.getDeclaredFields(lass);
        if (!ObjectUtil.isEmpty(fields))
        {
            for (Field field : fields) {
                if (field.getModifiers() > 25) {
                    continue;
                }
                String key = field.getName();
                if (!ArrayUtil.isEmpty(showField)&&!ArrayUtil.contains(showField,key))
                {
                    continue;
                }
                JsonIgnore ignore = field.getAnnotation(JsonIgnore.class);
                if (ignore != null && !ignore.isNull()) {
                    continue;
                }
                String displayKey = key;
                JsonField jsonField = field.getAnnotation(JsonField.class);
                if (jsonField!=null&&!StringUtil.isEmpty(jsonField.name()))
                {
                    displayKey = jsonField.name();
                }
                if (displayKey.startsWith("_"))
                {
                    displayKey = displayKey.substring(1);
                }
                try {
                    field.setAccessible(true);
                    Object result = field.get(bean);
                    if (ignore != null && ignore.isNull()&&result == null) {
                        continue;
                    }

                    if (result == null) {
                        super.put(displayKey, null);
                        continue;
                    }

                    if (result instanceof JSONObject || result instanceof JSONArray) {
                        super.put(displayKey, result);
                        continue;
                    }
                    if (result instanceof Map) {
                        String[] childShowField = null;
                        if (dataField!=null&&!dataField.isEmpty())
                        {
                            JSONArray fieldArray = dataField.getJSONArray(key);
                            if (fieldArray!=null&&!fieldArray.isEmpty())
                            {
                                childShowField = (String[]) fieldArray.toArray(new String[fieldArray.size()]);
                            }
                        }
                        super.put(displayKey, new JSONObject(result, childShowField,includeSuperClass,dataField));
                        continue;
                    }
                    if (result instanceof java.sql.Time) {
                        super.put(displayKey, result.toString());
                        continue;
                    }

                    if (result instanceof java.sql.Timestamp) {
                        result = new Date(((java.sql.Timestamp) result).getTime());
                    }

                    if (result instanceof Date) {
                        //日期格式化

                        if (jsonField != null && !StringUtil.isNull(jsonField.format())) {
                            super.put(displayKey, DateUtil.toString((Date) result, jsonField.format()));
                        } else {
                            super.put(displayKey, DateUtil.toString((Date) result, FULL_ST_FORMAT));
                        }

                    } else if (result.getClass().isArray() || result instanceof Collection) {
                        super.put(key, new JSONArray(result, includeSuperClass,key,dataField));
                    } else if (ClassUtil.isNumberType(result.getClass())) {
                        // 数字格式化
                        super.put(displayKey, result);
                    } else if (ClassUtil.isStandardProperty(result.getClass())) { //Primitives, String and Wrapper
                        super.put(displayKey, result);
                    } else {
                        Package objectPackage = result.getClass().getPackage();
                        String objectPackageName = objectPackage != null ? objectPackage.getName() : StringUtil.empty;
                        if (objectPackageName.equals("java/lang/Package"))
                        {
                            continue;
                        }
                        if (objectPackageName.startsWith("java.")
                                || objectPackageName.startsWith("javax.")
                                || result.getClass().getClassLoader() == null) {
                            super.put(displayKey, result);
                        } else {
                            if (includeSuperClass && !ClassUtil.isStandardProperty(result.getClass())&&result instanceof Serializable) {
                                String[] childShowField = null;
                                if (dataField!=null&&!dataField.isEmpty())
                                {
                                    JSONArray fieldArray = dataField.getJSONArray(key);
                                    if (fieldArray!=null&&!fieldArray.isEmpty())
                                    {
                                        childShowField = (String[]) fieldArray.toArray(new String[fieldArray.size()]);
                                    }
                                }
                                super.put(displayKey, new JSONObject(result, childShowField,includeSuperClass,dataField));
                            } else {
                                super.put(displayKey, result);
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        //-------------------------------方法需要输出的注释

        Method[] methods = ClassUtil.getDeclaredReturnMethods(lass, 0);
        if (methods!=null)
        {
            for (Method method : methods) {
                if (!Modifier.isPublic(method.getModifiers())) {
                    continue;
                }
                if (method.getGenericReturnType().equals(Void.TYPE)) {
                    continue;
                }
                if (method.getParameterTypes().length > 1) {
                    continue;
                }

                JsonField jsonField = method.getAnnotation(JsonField.class);
                if (jsonField == null) {
                    continue;
                }
                String key = jsonField.name();
                if (StringUtil.isNull(key)) {
                    key = ClassUtil.getMethodFiledName(method.getName());
                }
                if (!ArrayUtil.isEmpty(showField)&&!ArrayUtil.contains(showField,key))
                {
                    continue;
                }
                try {
                    Object result = method.invoke(bean, (Object[]) null);

                    if (result == null) {
                        super.put(key, null);
                        continue;
                    }

                    if (result instanceof JSONObject || result instanceof JSONArray) {
                        super.put(key, result);
                        continue;
                    }
                    if (result instanceof Map) {

                        super.put(key, new JSONObject(result, includeSuperClass,dataField));
                        continue;
                    }
                    if (result instanceof java.sql.Time) {
                        super.put(key, result.toString());
                        continue;
                    }

                    if (result instanceof java.sql.Timestamp) {
                        result = new Date(((java.sql.Timestamp) result).getTime());
                    }

                    if (result instanceof Date) {
                        //日期格式化
                        if (!StringUtil.isNull(jsonField.format())) {
                            super.put(key, DateUtil.toString((Date) result, jsonField.format()));
                        } else {
                            super.put(key, DateUtil.toString((Date) result, FULL_ST_FORMAT));
                        }
                    } else if (result.getClass().isArray() || result instanceof Collection) {
                        super.put(key, new JSONArray(result, includeSuperClass,key,dataField));
                    } else if (ClassUtil.isNumberType(result.getClass())) {
                        // 数字格式化
                        super.put(key, result);
                    } else if (ClassUtil.isStandardProperty(result.getClass())) { //Primitives, String and Wrapper
                        super.put(key, result);
                    } else {
                        Package objectPackage = result.getClass().getPackage();
                        String objectPackageName = objectPackage != null ? objectPackage.getName() : "";
                        if (objectPackageName.startsWith("java.")
                                || objectPackageName.startsWith("javax.")
                                || result.getClass().getClassLoader() == null) {
                            super.put(key, result.toString());
                        } else {
                            if (includeSuperClass && !ClassUtil.isStandardProperty(result.getClass())) {
                                String[] childShowField = null;
                                if (dataField!=null&&!dataField.isEmpty())
                                {
                                    JSONArray fieldArray = dataField.getJSONArray(key);
                                    if (fieldArray!=null&&!fieldArray.isEmpty())
                                    {
                                        final  int size = fieldArray.size();
                                        childShowField = (String[]) fieldArray.toArray(new String[size]);
                                    }
                                }
                                super.put(key, new JSONObject(result, childShowField,includeSuperClass,dataField));
                            } else {
                                super.put(key, result);
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Construct a JSONObject from a source JSON text string.
     * This is the most commonly used JSONObject constructor.
     *
     * @param source A string beginning
     *               with [code]{ } &nbsp;<small>(left brace)</small> and ending
     *               with [code]} } &nbsp;<small>(right brace)</small>.
     * @throws JSONException If there is a syntax error in the source
     *                       string or a duplicated key.
     */
    public JSONObject(String source) throws JSONException {
        this(new JSONTokener(source));
    }


    /**
     * Accumulate values under a key. It is similar transfer the put method except
     * that if there is already an object stored under the key then a
     * JSONArray is stored under the key transfer hold all of the accumulated values.
     * If there is already a JSONArray, then the new value is appended transfer it.
     * In contrast, the put method replaces the previous value.
     *
     * @param key   A key string.
     * @param value An object transfer be accumulated under the key.
     * @return this.
     * @throws JSONException If the value is an invalid number
     *                       or if the key is null.
     */
    public JSONObject accumulate(String key, Object value) throws JSONException {
        testValidity(value);
        Object o = get(key);
        if (o == null) {
            super.put(key, value instanceof JSONArray ?
                    new JSONArray().put(value) :
                    value);
        } else if (o instanceof JSONArray) {
            ((JSONArray) o).put(value);
        } else {
            super.put(key, new JSONArray().put(o).put(value));
        }
        return this;
    }


    /**
     * Append values transfer the array under a key. If the key does not exist in the
     * JSONObject, then the key is put in the JSONObject with its value being a
     * JSONArray containing the value parameter. If the key was already
     * associated with a JSONArray, then the value parameter is appended transfer it.
     *
     * @param key   A key string.
     * @param value An object transfer be accumulated under the key.
     * @return this.
     * @throws JSONException If the key is null or if the current value
     *                       associated with the key is not a JSONArray.
     */
    public JSONObject append(String key, Object value) throws JSONException {
        testValidity(value);
        Object o = get(key);
        if (o == null) {
            super.put(key, new JSONArray().put(value));
        } else if (o instanceof JSONArray) {
            super.put(key, ((JSONArray) o).put(value));
        }
        return this;
    }


    /**
     * Get the boolean value associated with a key.
     *
     * @param key A key string.
     * @return The truth.
     * @throws JSONException if the value is not a Boolean or the String "true" or "false".
     */
    public boolean getBoolean(String key) {
        return ObjectUtil.toBoolean(get(key));
    }


    /**
     * Get the double value associated with a key.
     *
     * @param key A key string.
     * @return The numeric value.
     * @throws JSONException if the key is not found or
     *                       if the value is not a Number object and cannot be converted transfer a number.
     */
    public double getDouble(String key) {
        return ObjectUtil.toDouble(get(key));
    }

    public float getFloat(String key) {
        return ObjectUtil.toFloat(get(key));
    }

    /**
     * Get the int value associated with a key. If the number value is too
     * large for an int, it will be clipped.
     *
     * @param key A key string.
     * @return The integer value.
     * @throws JSONException if the key is not found or if the value cannot
     *                       be converted transfer an integer.
     */
    public int getInt(String key) {
        return ObjectUtil.toInt(get(key));
    }


    /**
     * Increment a property of a JSONObject. If there is no such property,
     * create one with a value of 1. If there is such a property, and if it is
     * an Integer, Long, Double, or Float, then add one transfer it.
     *
     * @param key A key string.
     * @return this.
     * @throws JSONException If there is already a property with this name that is not an
     *                       Integer, Long, Double, or Float.
     */
    public JSONObject increment(String key) {
        Object value = get(key);
        if (value == null) {
            this.put(key, 1);
        } else if (value instanceof Short) {
            this.put(key, (Short) value + 1);
        } else if (value instanceof Integer) {
            this.put(key, (Integer) value + 1);
        } else if (value instanceof Long) {
            this.put(key, (Long) value + 1);
        } else if (value instanceof Double) {
            this.put(key, (Double) value + 1);
        } else if (value instanceof Float) {
            this.put(key, (Float) value + 1);
        }
        return this;
    }

    /**
     * Get the JSONArray value associated with a key.
     *
     * @param key A key string.
     * @return A JSONArray which is the value.
     * @throws JSONException if the key is not found or
     *                       if the value is not a JSONArray.
     */
    public JSONArray getJSONArray(String key) {
        Object o = get(key);
        if (o instanceof JSONArray) {
            return (JSONArray) o;
        }
        return null;
    }


    /**
     * Get the JSONObject value associated with a key.
     *
     * @param key A key string.
     * @return A JSONObject which is the value.
     * @throws JSONException if the key is not found or
     *                       if the value is not a JSONObject.
     */
    public JSONObject getJSONObject(String key) {
        Object o = get(key);
        if (o instanceof JSONObject) {
            return (JSONObject) o;
        }
        return null;
    }


    /**
     * Get the long value associated with a key. If the number value is too
     * long for a long, it will be clipped.
     *
     * @param key A key string.
     * @return The long value.
     * @throws JSONException if the key is not found or if the value cannot
     *                       be converted transfer a long.
     */
    public long getLong(String key) {
        return ObjectUtil.toLong(get(key));
    }

    public Date getDate(String key) throws JSONException {
        Object o = get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof JSONObject) {
            JSONObject json = (JSONObject) o;
            long time = json.getLong("time");
            return new Date(time);
        }

        if (o instanceof String && o.toString().contains("{") && o.toString().contains("}")) {
            long time = new JSONObject(o.toString()).getLong("time");
            return new Date(time);
        }
        try {
            return StringUtil.getDate((String) o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Get the string associated with a key.
     *
     * @param key A key string.
     * @return A string which is the value.
     * @throws JSONException if the key is not found.
     */
    public String getString(String key) {
        Object o = get(key);
        if (o==null)
        {
            return null;
        }
        return o.toString();
    }


    /**
     * Determine if the JSONObject contains a specific key.
     *
     * @param key A key string.
     * @return true if the key exists in the JSONObject.
     */
    public boolean has(String key) {
        return super.containsKey(key);
    }


    /**
     * Determine if the value associated with the key is null or if there is
     * no value.
     *
     * @param key A key string.
     * @return true if there is no value associated with the key or if
     * the value is the JSONObject.NULL object.
     */
    public boolean isNull(String key) {
        return JSONObject.NULL.equals(get(key)) || get(key)==null;
    }

    static public boolean isNull(Object bean) {
        return JSONObject.NULL.equals(bean) || bean==null;
    }

    /**
     * Produce a JSONArray containing the names of the elements of this
     * JSONObject.
     *
     * @return A JSONArray containing the key strings, or null if the JSONObject
     * is empty.
     */
    public JSONArray names() {
        JSONArray ja = new JSONArray();
        Set<String> keys = this.keys();
        for (String key : keys) {
            ja.put(key);
        }
        return ja.size() == 0 ? null : ja;
    }

    /**
     * Get an optional boolean associated with a key.
     * It returns the defaultValue if there is no such key, or if it is not
     * a Boolean or the String "true" or "false" (case insensitive).
     *
     * @param key          A key string.
     * @param defaultValue The default.
     * @return The truth.
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            return getBoolean(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public JSONObject put(String key, JSONArray value) {
        super.put(key, value);
        return this;
    }

    /**
     * Put a key/value pair in the JSONObject, where the value will be a
     * JSONArray which is produced from a Collection.
     *
     * @param key   A key string.
     * @param value A Collection value.
     * @return this.
     * @throws JSONException 异常
     */
    public JSONObject put(String key, Collection value) throws JSONException {
        super.put(key, value);
        return this;
    }


    /**
     * Get an optional string associated with a key.
     * It returns the defaultValue if there is no such key.
     *
     * @param key          A key string.
     * @param defaultValue The default.
     * @return A string which is the value.
     */
    public String getString(String key, String defaultValue) {
        Object o = get(key);
        return o != null ? o.toString() : defaultValue;
    }


    /**
     * Put a key/boolean pair in the JSONObject.
     *
     * @param key   A key string.
     * @param value A boolean which is the value.
     * @return this.
     * @throws JSONException If the key is null.
     */
    public JSONObject put(String key, boolean value) {
        super.put(key, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    /**
     * @param key   A key string.
     * @param value A Map value.
     * @return this.
     */
    public JSONObject put(String key, Map value) {
        super.put(key, value);
        return this;
    }


    /**
     * Put a key/value pair in the JSONObject. If the value is null,
     * then the key will be removed from the JSONObject if it is present.
     *
     * @param key   A key string.
     * @param value An object which is the value. It should be of one of these
     *              types: Boolean, Double, Integer, JSONArray, JSONObject, Long, String,
     *              or the JSONObject.NULL object.
     * @return this.
     */
    @Override
    public JSONObject put(String key, Object value) {
        if (key == null) {
            return this;
        }
        if (value != null) {
            try {
                testValidity(value);
                super.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            super.remove(key);
        }
        return this;
    }


    /**
     * Put a key/value pair in the JSONObject, but only if the key and the
     * value are both non-null, and only if there is not already a member
     * with that name.
     *
     * @param key   key
     * @param value 值
     * @return his.
     * @throws JSONException if the key is a duplicate
     */
    public JSONObject putOnce(String key, Object value) throws JSONException {
        if (key != null && value != null) {
            if (get(key) != null) {
                throw new JSONException("Duplicate key \"" + key + "\"");
            }
            super.put(key, value);
        }
        return this;
    }


    /**
     * Put a key/value pair in the JSONObject, but only if the
     * key and the value are both non-null.
     *
     * @param key   A key string.
     * @param value An object which is the value. It should be of one of these
     *              types: Boolean, Double, Integer, JSONArray, JSONObject, Long, String,
     *              or the JSONObject.NULL object.
     * @return this.
     * @throws JSONException If the value is a non-finite number.
     */
    public JSONObject putOpt(String key, Object value) throws JSONException {
        if (key != null && value != null) {
            super.put(key, value);
        }
        return this;
    }

    /**
     * Remove a name and its value, if present.
     *
     * @param key The name transfer be removed.
     * @return The value that was associated with the name,
     * or null if there was no value.
     */
    public Object remove(String key) {
        return super.remove(key);
    }

    /**
     * Get an enumeration of the keys of the JSONObject.
     *
     * @return An iterator of the keys.
     */
   /* public Enumeration<String> keys() {

        return super.keys();
    }*/
    public Set<String> keys() {

        return super.keySet();
    }

    public Map<String,Object> toMap() {
        Map<String, Object> map = new TreeMap<>();
        for (String key : keySet()) {
            if (key.equals(CLASS_NAME)) {
                continue;
            }
            map.put(key, get(key));
        }
        return map;
    }


    /**
     * Try transfer convert a string into a number, boolean, or null. If the string
     * can't be converted, return the string.
     *
     * @param s A String.
     * @return A simple JSON value.
     */
    static public Object stringToValue(String s) {
        if ("".equals(s)) {
            return s;
        }
        if ("true".equalsIgnoreCase(s)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(s)) {
            return Boolean.FALSE;
        }
        if ("null".equalsIgnoreCase(s)) {
            return JSONObject.NULL;
        }

        /*
         * If it might be a number, try converting it. We support the 0- and 0x-
         * conventions. If a number cannot be produced, then the value will just
         * be a string. Note that the 0-, 0x-, plus, and implied string
         * conventions are non-standard. A JSON parser is free transfer accept
         * non-JSON forms as long as it accepts all correct JSON forms.
         */

        char b = s.charAt(0);
        if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
            if (b == '0') {
                if (s.length() > 2 &&
                        (s.charAt(1) == 'x' || s.charAt(1) == 'X')) {
                    try {
                        return Integer.parseInt(s.substring(2), 16);
                    } catch (Exception e) {
                        /* Ignore the error */
                    }
                } else {
                    try {
                        return Integer.parseInt(s, 8);
                    } catch (Exception e) {
                        /* Ignore the error */
                    }
                }
            }
            try {
                if (s.indexOf('.') > -1 || s.indexOf('e') > -1 || s.indexOf('E') > -1) {
                    return Double.valueOf(s);
                } else {
                    Long myLong = new Long(s);
                    if (myLong == myLong.intValue()) {
                        return myLong.intValue();
                    } else {
                        return myLong;
                    }
                }
            } catch (Exception f) {
                /* Ignore the error */
            }
        }
        return s;
    }


    /**
     * Throw an exception if the object is an NaN or infinite number.
     *
     * @param o The object transfer testaio.
     * @throws JSONException If o is a non-finite number.
     */
    static void testValidity(Object o) throws JSONException {
        if (o != null) {
            if (o instanceof Double) {
                if (((Double) o).isInfinite() || ((Double) o).isNaN()) {
                    throw new JSONException("JSON does not allow non-finite numbers.");
                }
            } else if (o instanceof Float) {
                if (((Float) o).isInfinite() || ((Float) o).isNaN()) {
                    throw new JSONException("JSON does not allow non-finite numbers.");
                }
            }
        }
    }


    /**
     * Produce a JSONArray containing the values of the members of this
     * JSONObject.
     *
     * @param names A JSONArray containing a list of key strings. This
     *              determines the sequence of the values in the result.
     * @return A JSONArray of values.
     * @throws JSONException If any of the values are non-finite numbers.
     */
    public JSONArray toJSONArray(JSONArray names) throws JSONException {
        if (names == null || names.isEmpty()) {
            return null;
        }
        JSONArray ja = new JSONArray();
        for (int i = 0; i < names.size(); i += 1) {
            ja.put(get(names.getString(i)));
        }
        return ja;
    }

    /**
     * Make a JSON text of this JSONObject. For compactness, no whitespace
     * is added. If this would not result in a syntactically correct JSON text,
     * then null will be returned instead.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return a printable, displayable, portable, transmittable
     * representation of the object, beginning
     * with [code]{ } &nbsp;<small>(left brace)</small> and ending
     * with [code]} } &nbsp;<small>(right brace)</small>.
     */
    @Override
    public String toString() {
        try {
            return toString(this,lass,0, 0, false,false);
        } catch (Exception e) {
            log.error("解析错误", e);
            return StringUtil.empty;
        }
    }

    public String toSortString() {
        try {
            return toString(this,lass,0, 0, false,true);
        } catch (Exception e) {
            log.error("解析错误", e);
            return StringUtil.empty;
        }
    }

    public String toString(int indentFactor) {
        return toString(this,lass,indentFactor, 0, false,false);
    }

    public String toString(int indentFactor, boolean needClass) {

        return toString(this,lass,indentFactor, 0, needClass,false);
    }

    public String toString(int indentFactor, int indent, boolean needClass)
    {
        return toString(this,lass,indentFactor, indent, needClass,false);
    }


    /**
     * Make a prettyprinted JSON text of this JSONObject.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param indentFactor The number of spaces transfer add transfer each level of
     *                     indentation.
     * @param indent       The indentation of the top level.
     * @return a printable, displayable, transmittable
     * representation of the object, beginning
     * with [code]{ } &nbsp;<small>(left brace)</small> and ending
     * with [code]} } &nbsp;<small>(right brace)</small>.
     */
    static String toString(Map<String,?> valueMap,Class<?> lass,int indentFactor, int indent, boolean needClass,boolean sort) {
        if (ObjectUtil.isEmpty(valueMap)) {
            return "{}";
        }
        Map<String,?>  map;
        if (sort)
        {
            map = MapUtil.sortByKey(valueMap);
        } else
        {
            map = valueMap;
        }

        StringBuilder sb = new StringBuilder("{");
        try {
            int j;
            Iterator<String> keys = map.keySet().iterator();
            Object o;
            Object v;
            if (map.size() == 1) {
                o = keys.next();
                v = map.get(o);
                sb.append(StringUtil.quote(o == null ? "" : o.toString(), true));
                sb.append(StringUtil.COLON);
                if (o==null)
                {
                    sb.append("null");
                } else
                {
                    sb.append(valueToString(getJsonField(lass,o.toString()),v, indentFactor, indent, needClass));
                }
            } else {
                int newIndent = indent + indentFactor;
                while (keys.hasNext()) {
                    o = keys.next();
                    v = map.get(o);
                    if (!needClass && CLASS_NAME.equals(o)) {
                        continue;
                    }
                    if (sb.length() > 1) {
                        sb.append(",");
                        if (indentFactor > 0) {
                            sb.append('\n');
                        }
                    } else {
                        if (indentFactor > 0) {
                            sb.append('\n');
                        }
                    }
                    for (j = 0; j < newIndent; j += 1) {
                        sb.append(' ');
                    }
                    if (isNull(o))
                    {
                        sb.append(o.toString());
                    } else
                    {
                        sb.append(StringUtil.quote(o.toString(), true));
                    }
                    sb.append(StringUtil.COLON);
                    sb.append(valueToString(getJsonField(lass,o.toString()),v, indentFactor, newIndent, needClass));
                }
                if (sb.length() > 1) {
                    if (indentFactor > 0) {
                        sb.append('\n');
                    }
                    for (j = 0; j < indent; j += 1) {
                        sb.append(' ');
                    }
                }
            }
        } catch (Exception f) {
            log.error("解析错误", f);
            return "{}";
        }
        sb.append('}');

        return sb.toString();
    }


    /**
     * Make a JSON text of an Object value. If the object has an
     * value.toJSONString() method, then that method will be used transfer produce
     * the JSON text. The method is required transfer produce a strictly
     * conforming text. If the object does not contain a toJSONString
     * method (which is the most common case), then a text will be
     * produced by other means. If the value is an array or Collection,
     * then a JSONArray will be made from it and its toJSONString method
     * will be called. If the value is a MAP, then a JSONObject will be made
     * from it and its toJSONString method will be called. Otherwise, the
     * value's toString method will be called, and the result will be quoted.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param value The value transfer be serialized.
     * @return a printable, displayable, transmittable
     * representation of the object, beginning
     * with [code]{ } &nbsp;<small>(left brace)</small> and ending
     * with [code]} } &nbsp;<small>(right brace)</small>.
     * @throws JSONException If the value is or contains an invalid number.
     */
    static String valueToString(JsonField jsonField,Object value) throws JSONException {
        return valueToString(jsonField,value, 0, 0, false);
    }

    /**
     * Make a prettyprinted JSON text of an object value.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param value        The value transfer be serialized.
     * @param indentFactor The number of spaces transfer add transfer each level of
     *                     indentation.
     * @param indent       The indentation of the top level.
     * @return a printable, displayable, transmittable
     * representation of the object, beginning
     * with [code]{ } &nbsp;<small>(left brace)</small> and ending
     * with [code]} } &nbsp;<small>(right brace)</small>.
     * @throws JSONException If the object contains an invalid number.
     */
    static String valueToString(JsonField jsonField,Object value, int indentFactor, int indent, boolean classInfo) throws JSONException {
        if (value == null || JSONObject.NULL.equals(value)) {
            return null;
        }

        if (value instanceof String) {
            if (((String) value).startsWith("<script>") && ((String) value).endsWith("</script>")) {
                return StringUtil.substringOutBetween(value.toString(), "<script>", "</script>");
            } else {
                return StringUtil.quote(value.toString(), true);
            }
        }

        if (value instanceof Date) {
            if (DateUtil.empty.equals(value))
            {
                return null;
            }
            if (jsonField!=null&&!StringUtil.isNull(jsonField.format()))
            {
                return StringUtil.quote(DateUtil.toString((Date) value, jsonField.format()), true);
            }
            else
            {
                return StringUtil.quote(DateUtil.toString((Date) value, FULL_ST_FORMAT), true);
            }
        }

        if (value instanceof InetAddress) {
                return StringUtil.quote(IpUtil.getIp((InetAddress)value), true);
        }

        if (value instanceof SocketAddress) {
            return StringUtil.quote(IpUtil.getIp((SocketAddress)value), true);
        }

        if (value instanceof Boolean) {
            return value.toString();
        }

        if (value instanceof Number) {
            if (jsonField!=null&&!StringUtil.isNull(jsonField.format()))
            {
                return NumberUtil.format(value, jsonField.format());
            }
            else
            {
                return NumberUtil.getNumberStdFormat((Number) value);
            }

        }
        if (value instanceof InputStream) {
            InputStream in = (InputStream) value;
            try {
                byte[] data = new byte[in.available()];
                in.read(data, 0, data.length);
                return StringUtil.quote(BIN_DATA_START + EncryptUtil.getBase64Encode(data, EncryptUtil.NO_WRAP), true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (value instanceof JSONString) {
            return ((JSONString) value).toJSONString();
        }
        if (value instanceof List) {
            JSONArray array = new JSONArray(value);
            return array.toString(indentFactor, indent);
        }

        if (value instanceof JSONObject) {

            return ((JSONObject) value).toString(indentFactor, indent, classInfo);
        }

        if (value instanceof Map) {
            return new JSONObject((Map) value).toString(indentFactor, indent, classInfo);
        }

        if (value instanceof Iterable || value.getClass().isArray()) {
            return new JSONArray(value).toString(indentFactor, indent);
        }
        if (value instanceof Serializable && !ClassUtil.isStandardProperty(value.getClass())) {
            return new JSONObject(value).toString(indentFactor, indent, classInfo);
        }

        return StringUtil.quote(value.toString(), true);
    }


    /**
     * Write the contents of the JSONObject as JSON text transfer a writer.
     * For compactness, no whitespace is added.
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param writer 写对象
     * @return The writer.
     * @throws Exception 异常
     */
    public Writer write(Writer writer) throws Exception {
        try {
            boolean b = false;
            Iterator keys = super.keySet().iterator();
            writer.write('{');
            while (keys.hasNext()) {
                if (b) {
                    writer.write(',');
                }
                Object k = keys.next();
                writer.write(StringUtil.quote(k.toString(), true));
                writer.write(':');
                Object v = super.get(k);
                if (v==null)
                {
                    writer.write(valueToString(getJsonField(lass,k.toString()),NULL));
                }
                else
                {
                    writer.write(valueToString(getJsonField(lass,k.toString()),v));
                }
                b = true;
            }
            writer.write('}');
            return writer;
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }

    private static JsonField getJsonField(Class<?> lass,String key)
    {
        if (lass==null)
        {
            return null;
        }

        Field  field = ClassUtil.getDeclaredField(lass,key);
        if (field!=null)
        {
            JsonField jsonField = field.getAnnotation(JsonField.class);
            if (jsonField!=null)
            {
                return jsonField;
            }
        }

        Method method = ClassUtil.getDeclaredMethod(lass,key);
        if (method!=null)
        {
            JsonField jsonField = method.getAnnotation(JsonField.class);
            if (jsonField!=null)
            {
                return jsonField;
            }
        }
        return null;
    }

    /**
     * @param clazz 类对象
     * @param <T>   泛型
     * @return 不能支持泛型
     */
    public <T> T parseObject(Class<T> clazz) {
        return parseObject(this, clazz);
    }


    public static <T> T parseObject(JSONObject json, Class<T> clazz) {
        String className = null;
        //检查内部是否保存了对象名称
        if (clazz == null || ClassUtil.isStandardProperty(clazz) || Object.class.equals(clazz) || Class.class.equals(clazz)) {
            className = json.getString(CLASS_NAME);
        }
        if (clazz.equals(JSONObject.class))
        {
            return (T)json;
        }

        if (clazz != null && StringUtil.isEmpty(className)) {
            className = clazz.getName();
        }

        if (clazz!=null&&(Map.class.isAssignableFrom(clazz)))
        {
            T obj = null;
            try {
                obj = clazz.newInstance();
                ((Map)obj).putAll(json.toMap());
                return obj;
            } catch (Exception e) {
                log.error("创建对象实例错误:{}",clazz);
                return (T)json.toMap();
            }
        }
        if (StringUtil.isEmpty(className) || ClassUtil.isProxy(clazz)) {
            //动态创建返回
            Map<String, Object> valueMap = json.toMap();
            return (T) ReflectUtil.createDynamicBean(valueMap);
        }
        Class<?> cls = null;
        try {
            cls = ClassUtil.loadClass(className);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("create newInstance className " + className, e);
        }
        if (cls == null) {
            return null;
        }

        Gson gson = GsonUtil.createGson();
        return (T) gson.fromJson(json.toString(), cls);
    }

    /**
     * 泛型装置，这里使用了fastjson
     *
     * @param json          json
     * @param typeReference 资源方式
     * @param <T>           泛型
     * @return 泛型装置，这里使用了
     */
    public static <T> T parseObject(JSONObject json, TypeReference<T> typeReference) {
        Gson gson = GsonUtil.createGson();
        return gson.fromJson(json.toString(),typeReference.getType());
    }

    /**
     * 是用例子
     * <pre>{@code RocResponse<List<Matter>> rocResponse2 = jsonObject.parseObject(new TypeReference<RocResponse<List<Matter>>>(){});
     *  }</pre>
     *
     * @param typeReference 资源方式
     * @param <T>           泛型
     * @return 是用例子
     */
    public <T> T parseObject(TypeReference<T> typeReference) {
        return parseObject(this, typeReference);
    }
}