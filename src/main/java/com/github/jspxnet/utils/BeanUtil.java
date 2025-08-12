package com.github.jspxnet.utils;

import com.github.jspxnet.json.GsonUtil;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.util.TypeUtil;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.model.container.PropertyContainer;
import com.github.jspxnet.sober.util.AnnotationUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanMap;

import java.io.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-7
 * Time: 19:04:35
 * com.jspx.jspx.test.utils.BeanUtil
 */
@Slf4j
public final class BeanUtil {
    private BeanUtil() {

    }

    /**
     * 设置属性,性能很差,但容错很好
     *
     * @param object     bean对象
     * @param methodName 方法名称,如果存在set就不加set
     * @param obj        参数
     */
    @SuppressWarnings("unchecked")
    public static void setSimpleProperty(Object object, String methodName, Object obj) {
        if (object == null) {
            throw new NullPointerException(object + methodName + " is NULL");
        }
        if (!StringUtil.hasLength(methodName)) {
            return;
        }
        //map 的

        if (object instanceof Map && !(object instanceof PropertyContainer)) {
            Map<String,Object> map = (Map<String,Object>) object;
            map.put(methodName, obj);
            return;
        }
        if ((object instanceof List) && (obj instanceof List)) {
            List<Object> list = (List<Object>) object;
            list.addAll((List<Object>) obj);
            return;
        }
        if (methodName.contains(StringUtil.DOT)) {
            methodName = methodName.substring(methodName.lastIndexOf(StringUtil.DOT));
        }
        if (methodName.startsWith("void")) {
            methodName = methodName.substring(5).trim();
        } else if (!methodName.startsWith(ClassUtil.METHOD_NAME_SET)) {
            methodName = ClassUtil.METHOD_NAME_SET + StringUtil.capitalize(methodName);
        }

        Class<?> cls = ClassUtil.getClass(object.getClass());
        Method method = ClassUtil.getDeclaredMethod(cls, methodName);
        if (method == null && methodName.startsWith("setIs")) {
            methodName = ClassUtil.METHOD_NAME_SET + StringUtil.capitalize(methodName.substring(5));
            method = ClassUtil.getDeclaredMethod(cls, methodName);
        }
        if (method!=null)
        {
            Type[] types = method.getGenericParameterTypes();
            if (types.length < 1) {
                return;
            }
            Type aType = types[0];
            Object[] pObject = new Object[1];
            try {
                pObject[0] = getTypeValue(obj, aType);
                (new java.beans.Expression(object, methodName, pObject)).execute();
            } catch (Exception e) {
                log.error(object.getClass().getName() + StringUtil.DOT + methodName + " setProperty  type=" + aType + " value=" + obj, e);
            }
        } else
        {
            BeanUtil.setFieldValue(object, methodName, obj);
        }

    }

    @SuppressWarnings("unchecked")
    public static void setFieldValue(Object object, String fieldName, Object obj) {
        if (object == null || !StringUtil.hasLength(fieldName)) {
            return;
        }

        //map 的    if (!(bean instanceof PropertyContainer))

        if (object instanceof Map && !(object instanceof PropertyContainer)) {
            Map map = (Map) object;
            map.put(fieldName, obj);
            return;
        }
        if ((object instanceof List) && (obj instanceof List)) {
            List list = (List) object;
            list.addAll((List) obj);
            return;
        }

        Class<?> cls = ClassUtil.getClass(object.getClass());
        if (ClassUtil.isProxy(cls))
        {
            //代理对象
            fieldName = "$cglib_prop_" + fieldName;
        }
        Field field = ClassUtil.getDeclaredField(cls, fieldName);
        if (field ==null && (object instanceof PropertyContainer)) {
            PropertyContainer container = (PropertyContainer) object;
            container.put(fieldName, obj);
            return;
        }
        if (field == null) {
            log.debug(object.getClass() + " set field {} not find", fieldName);
            return;
        }
        if (Modifier.isFinal(field.getModifiers()) || field.getModifiers() == 26 || field.getModifiers() == 18) {
            return;
        }
        Type aType = field.getType();

        Object pObject;
        try {
            field.setAccessible(true);
            if (obj != null && aType.getTypeName().equals(cls.getTypeName())) {
                field.set(object, obj);
            } else {
                pObject = getTypeValue(obj, aType);
                field.set(object, pObject);
            }
        } catch (Exception e) {
            log.error(object.getClass().getName() + StringUtil.DOT + fieldName + " setValue  type=" + aType + " value=" + obj, e);
        }
    }

    /**
     * @param obj 对象
     * @param cls 类
     * @param <T> 返回类型
     * @param <D> 数据
     * @return 类型转换
     */
    @SuppressWarnings("unchecked")
    public static <T, D> T getTypeValue(D obj, Type cls) {
        return (T) getTypeValueObject(obj, cls);
    }

    /**
     *
     * @param obj 进入数据
     * @param aType 类型
     * @return 类型转换
     */
    @SuppressWarnings("unchecked")
    private static Object getTypeValueObject(Object obj, Type aType) {

        if (aType==null||ClassUtil.isBaseNumberType(aType)&&(obj==null))
        {
            return 0;
        }
        if (obj == null) {
            if (ClassUtil.isNumberType(aType)) {
                return 0;
            }
            return null;
        }
        if (JSONObject.NULL.equals(obj)) {
            return null;
        }

        if (obj.getClass().equals(aType)) {
            return obj;
        }

        if (ClassUtil.isNumberType(obj.getClass()) && aType.equals(String.class)) {
            return NumberUtil.toString(obj);
        }
        if (obj  instanceof JSONArray && aType.equals(String.class))
        {
            JSONArray array = (JSONArray)obj;
            return ObjectUtil.toString(array.get(0));
        }
        if (obj  instanceof JSONArray && aType.equals(Number.class))
        {
            JSONArray array = (JSONArray)obj;
            return ObjectUtil.toInt(array.get(0));
        }
        //如果是泛型
        if (aType instanceof ParameterizedType && obj instanceof JSONArray)
        {
            ParameterizedType ptype = (ParameterizedType)aType;
            Type rawType = ptype.getRawType();
            Type type = ptype.getActualTypeArguments()[0];
            if (rawType.getTypeName().contains("java.util.List")||rawType.getTypeName().contains(".Collection"))
            {
                try {
                    Class<?> cls = Class.forName(type.getTypeName());
                    JSONArray jsonArray = (JSONArray)obj;
                    return  GsonUtil.getList(jsonArray.toString(),cls);
                } catch (ClassNotFoundException e) {
                    log.error("最外层<>前面那个类型 rawType："+rawType.getTypeName(),e);
                }
            }
            if (ClassUtil.isArrayType(aType)) {
                try {
                    Class<?> cls = Class.forName(type.getTypeName());
                    JSONArray jsonArray = (JSONArray) obj;
                    return GsonUtil.getList(jsonArray.toString(), cls).toArray();
                } catch (ClassNotFoundException e) {
                    log.error("aType：{}",aType,e);
                }
            }
         }
        if (aType instanceof ParameterizedType && obj instanceof JSONObject)
        {
            ParameterizedType ptype = (ParameterizedType)aType;
            //Type rawType = ptype.getRawType();
            //System.out.println("最外层<>前面那个类型 rawType："+rawType.getTypeName());
            Type type = ptype.getActualTypeArguments()[0];
            Gson gson = GsonUtil.createGson();
            JSONObject json = (JSONObject)obj;
            try {
                Class<?> cls = Class.forName(type.getTypeName());
                return gson.fromJson(json.toString(),cls);
            } catch (ClassNotFoundException e) {
                 log.error("obj：{}",obj,e);
            }
        }

        if (aType.equals(Map.class) && obj instanceof String) {
            String str = (String) obj;
            return StringUtil.mapStringToMap(str);
        } else if ((aType.toString().contains(".List") || aType.toString().contains(".Collection")) && obj instanceof String) {
            String str = (String) obj;
            List<Object> list = new ArrayList<>();
            String[] array = StringUtil.split(str, ",");
            Collections.addAll(list, array);
            return list;
        }
        else if (ClassUtil.isArrayType(aType) && ((obj.getClass().isArray() || obj instanceof JSONArray))) {
            Object[] vv;
            if (obj instanceof JSONArray) {
                vv = ((JSONArray) obj).toArray();
            } else {
                if (aType.equals(String[].class))
                {
                    vv = ArrayUtil.toStringArray((Object[])obj);
                }
                else
                if (aType.equals(Integer[].class))
                {
                    vv = ArrayUtil.toIntegerArray((Object[])obj);
                }
                else
                if (aType.equals(Float[].class))
                {
                    vv = ArrayUtil.toFloatArray((Object[])obj);
                } else
                if (aType.equals(Double[].class))
                {
                    vv = ArrayUtil.toDoubleArray((Object[])obj);
                }
                else
                {
                    vv = (Object[]) obj;
                }
            }
            if (aType.equals(int[].class)) {
                return ArrayUtil.getIntArray(ArrayUtil.toStringArray(vv));
            } else if (aType.equals(Integer[].class)) {
                return ArrayUtil.getIntegerArray(ArrayUtil.toStringArray(vv));
            } else if (aType.equals(long[].class)) {
                return ArrayUtil.getLongArray(ArrayUtil.toStringArray(vv));
            } else if (aType.equals(Long[].class)) {
                return ArrayUtil.getLongObjectArray(ArrayUtil.toStringArray(vv));
            } else if (aType.equals(float[].class)) {
                return ArrayUtil.getFloatArray(ArrayUtil.toStringArray(vv));
            } else if (aType.equals(Float[].class)) {
                return ArrayUtil.getFloatObjectArray(ArrayUtil.toStringArray(vv));
            } else if (aType.equals(double[].class)) {
                return ArrayUtil.getDoubleArray(ArrayUtil.toStringArray(vv));
            } else if (aType.equals(Double[].class)) {
                return ArrayUtil.getDoubleObjectArray(ArrayUtil.toStringArray(vv));
            } else if (aType.equals(BigDecimal[].class)) {
                return ArrayUtil.getBigDecimalArray(ArrayUtil.toStringArray(vv));
            } else if (aType.equals(String[].class)) {
                return ArrayUtil.toStringArray(vv);
            } else {
                return obj;
            }
        }
        else if (ClassUtil.isArrayType(aType) && (ClassUtil.isStandardType(obj.getClass()))) {
            if (aType.equals(int[].class)||aType.equals(Integer[].class)) {
                int[] vv = new int[1];
                vv[0] = ObjectUtil.toInt(obj);
                return vv;
            } else if (aType.equals(long[].class)||aType.equals(Long[].class))
            {
                long[] vv = new long[1];
                vv[0] = ObjectUtil.toLong(obj);
                return vv;
            }  else if (aType.equals(float[].class)) {
                float[] vv = new float[1];
                vv[0] = ObjectUtil.toFloat(obj);
                return vv;

            } else if (aType.equals(Float[].class)) {
                Float[] vv = new Float[1];
                vv[0] = ObjectUtil.toFloat(obj);
                return vv;
            } else if (aType.equals(double[].class)) {
                double[] vv = new double[1];
                vv[0] = ObjectUtil.toDouble(obj);
                return vv;

            } else if (aType.equals(Double[].class)) {
                Double[] vv = new Double[1];
                vv[0] = ObjectUtil.toDouble(obj);
                return vv;
            } else if (aType.equals(BigDecimal[].class)) {
                BigDecimal[] vv = new BigDecimal[1];
                vv[0] = BigDecimal.valueOf(ObjectUtil.toDouble(obj));
                return vv;
            } else if (aType.equals(String[].class)) {
                if (StringUtil.empty.equals(obj)|| "[]".equals(obj))
                {
                    return new String[0];
                }
                String[] vv = new String[1];
                vv[0] = obj+"";
                return vv;
            } else {
                if (StringUtil.empty.equals(obj) || "[]".equals(obj))
                {
                    return new Object[0];
                }
                Object[] vv = new Object[1];
                vv[0] = obj;
                return vv;
            }
        }
        if (aType.equals(Boolean.class) || aType.equals(boolean.class)) {
            return ObjectUtil.toBoolean(obj);
        } else if (aType.equals(int.class) || aType.equals(Integer.class)) {
            return ObjectUtil.toInt(obj);
        } else if (aType.equals(long.class) || aType.equals(Long.class)) {
            return ObjectUtil.toLong(obj);
        } else if (aType.equals(float.class) || aType.equals(Float.class)) {
            return ObjectUtil.toFloat(obj);
        } else if (aType.equals(double.class) || aType.equals(Double.class)) {
            return ObjectUtil.toDouble(obj);
        } else if (aType.equals(BigDecimal.class))
        {
            return new BigDecimal(ObjectUtil.toString(obj));
        } else if (aType.equals(BigInteger.class))
        {
            return new BigInteger(ObjectUtil.toString(obj));
        } else if (aType.equals(Date.class)) {
            return ObjectUtil.toDate(obj);
        } else if (aType.equals(java.sql.Date.class)) {
            return ObjectUtil.toSqlDate(obj);
        } else if (aType.equals(Timestamp.class)) {
            return ObjectUtil.toSqlTimestamp(obj);
        } else if (aType.equals(Time.class)) {
            return ObjectUtil.toSqlTime(obj);
        }else if (aType.equals(String.class)) {
            if (obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) obj;
                if (jsonObject.isEmpty()) {
                    return "";
                }
            }
            return obj;
        } else if (aType.equals(InputStream.class) && obj instanceof String) {
            String str = (String) obj;
            if (str.startsWith(JSONObject.BIN_DATA_START)) {
                String data = StringUtil.substringAfter(str, JSONObject.BIN_DATA_START);
                byte[] buf = EncryptUtil.getBase64Decode(data, EncryptUtil.NO_WRAP);
                return new ByteArrayInputStream(buf);
            }
            return obj;
        } else if (obj instanceof JSONObject && !ClassUtil.isStandardType(aType)) {
            if (ObjectUtil.isEmpty(obj)) {
                return  null;
            } else
            if (ClassUtil.isArrayType(aType)) {
                Gson gson = GsonUtil.createGson();
                JSONObject json = (JSONObject)obj;
                return gson.fromJson(json.toString(),aType);
            } else {
                JSONObject json = (JSONObject) obj;
                return json.parseObject((Class) aType);
            }
        } else {
            return obj;
        }

    }

    /**
     *
     * @param object bean对象
     * @param name 方法名称
     * @param clazz 类型
     * @return  泛型支持的返回结果
     * @param <T> 返回对象类型
     */
    public static  <T> T getProperty(Object object, String name, Class<T> clazz) {
        Object obj = getProperty(object,name);
        return getTypeValue(obj,clazz);
    }

    /**
     *
     * @param object  bean对象
     * @param name 方法名称
     * @return 返回字段数据
     */
    public static Object getProperty(Object object, String name) {
        return getProperty(object, name, ArrayUtil.NULL, true);
    }

    /**
     * 保留外部显示，返回的有可能是复杂对象
     * @param object    bean对象
     * @param name      方法名称
     * @param parameter 参数列表
     * @param jump      跳过不满足条件的方法,并且不会报错
     * @return 返回对象
     */
    public static Object getProperty(Object object, String name, Object[] parameter, boolean jump)
    {
        if (!StringUtil.hasLength(name)) {
            return null;
        }
        if (object == null || ClassUtil.isStandardProperty(object.getClass())) {
            return object;
        }
        if (object instanceof Map && !(object instanceof  PropertyContainer) && parameter == null) {
            Map<String,Object> map = (Map<String,Object>) object;
            return map.get(name);
        }
        if (object instanceof  PropertyContainer)
        {
            PropertyContainer container = (PropertyContainer)object;
            if (container.containsKey(name))
            {
                return container.get(name);
            }
        }
        //数据库中进入的
        if (ClassUtil.isProxy(object.getClass())&&parameter==null)
        {
            BeanMap beanMap = BeanMap.create(object);
            //代理方式都是小心的
            return beanMap.get(name);
        }

        Method[] methods = ClassUtil.getDeclaredMethods(object.getClass());
        Method testMethod = null;
        for (Method method : methods) {
            if (method.getGenericReturnType().equals(Void.TYPE)) {
                continue;
            }
            if (method.getName().equals(name) || method.getName().equals(ClassUtil.METHOD_NAME_GET + StringUtil.capitalize(name)) || method.getName().equals(ClassUtil.METHOD_NAME_IS + StringUtil.capitalize(name))) {
                //如果parameter == ArrayUtil.NULL 表示只起无参数的函数
                int iParam = method.getGenericParameterTypes().length;
                if (parameter != ArrayUtil.NULL && parameter.length == iParam || parameter == ArrayUtil.NULL && iParam == 0) {
                    testMethod = method;
                    break;
                }
            }
        }
        if (testMethod == null && jump) {
            return null;
        }
        if (testMethod == null) {
            log.error(object.getClass().getName() + " method not find " + name + ",找不到方法名");
            return null;
        }
        try {
            if (parameter == null || testMethod.getParameterTypes().length == 0) {
                return testMethod.invoke(object);
            } else {
                return testMethod.invoke(object, parameter);
            }
        } catch (Throwable e) {
            log.error(object.getClass().getName() + " getProperty=" + testMethod.getName() + "  parameter=" + Arrays.toString(parameter) + ",检查类中方法是否正常执行", e);
        }

        return null;
    }

    /**
     *
     * @param object 对象
     * @param name 字段名称
     * @param anyField 不屈服大小写
     * @param <T> 类型
     * @return 得到字段的值
     */
    public static <T> T getFieldValue(Object object, String name,boolean anyField)
    {
        return getFieldValue(object, name,null,anyField);
    }

    /**
     *
     * @param object 对象
     * @param name 字段名称
     * @param cls 类对象
     * @param anyField 不屈服大小写
     * @param <T> 类型
     * @return 得到字段的值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object object, String name,Class<T> cls,boolean anyField)
    {
        if (!StringUtil.hasLength(name)) {
            return null;
        }

        if (object == null || ClassUtil.isStandardProperty(object.getClass())) {
            if (cls==null)
            {
                return (T)object;
            }
            return getTypeValue(object,cls);
        }
        if (object instanceof Map) {
            Map map = (Map) object;
            if (cls==null)
            {
                return (T)map.get(name);
            }
            return getTypeValue(map.get(name),cls);

        }
        if (ClassUtil.isProxy(object.getClass()))
        {
            BeanMap beanMap = BeanMap.create(object);
            if (anyField)
            {
                for (Object key:beanMap.keySet())
                {
                    if (name.equalsIgnoreCase((String)key))
                    {
                        return (T)beanMap.get(key);
                    }
                }
            }
            return (T)beanMap.get(name);
        }
        Field[] fields = ClassUtil.getDeclaredFields(object.getClass());
        if (fields == null) {
            return null;
        }
        for (Field field : fields) {
            if ((!anyField&&field.getName().equals(name))||(anyField&&field.getName().equalsIgnoreCase(name))) {
                try {
                    field.setAccessible(true);
                    if (cls==null)
                    {
                        return (T)field.get(object);
                    }
                    return getTypeValue(field.get(object),cls);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * @param object 数据对象
     * @param cls    类对象，作为模型创建新的对象
     * @param <T>    VO 拷贝到那个对象
     * @param <D>    数据对象，赋给新的对象
     * @return 拷贝到新的对象
     */
    @SuppressWarnings("unchecked")
    public static <T, D> T copy(D object, Class<T> cls) {
        if (null == cls || null == object) {
            return null;
        }
        if (ClassUtil.isStandardProperty(cls))
        {
            return (T)object;
        }
        if (cls.equals(JSONObject.class)) {
            return (T) new JSONObject(object);
        }
        if (cls.equals(JSONArray.class)) {
            return (T) new JSONArray(object);
        }
        if (cls.equals(String.class)) {
            return (T) ObjectUtil.toString(object);
        }
        T result = null;
        try {
            result = (T)ClassUtil.newInstance(cls.getName());
            if (cls.equals(object.getClass()))
            {
                //相同类型,快速拷贝
                BeanCopier beanCopier = BeanCopier.create(cls, cls,false);
                beanCopier.copy(object, result, null);
                if (object instanceof PropertyContainer && result instanceof PropertyContainer )
                {
                    PropertyContainer propertyContainer = (PropertyContainer)object;
                    PropertyContainer resultPropertyContainer = (PropertyContainer)result;
                    resultPropertyContainer.putAll(propertyContainer.getValues());
                }
            } else
            {
                copyFiledValue(object,result);
            }
        } catch (Exception e) {
            log.error("对象copy失败 class:{},error:{}",cls,e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * 拷贝列表
     *
     * @param list 列表数据
     * @param cls  列表类型
     * @param <T>  列表类型4
     * @param <V>  列表值
     * @return 返回新的列表
     */
    public static <T, V> List<T> copyList(Collection<V> list, Class<T> cls) {
        if (null == list) {
            return new ArrayList<>(0);
        } else {
            List<T> result = new ArrayList<>();
            for (V obj : list) {

                result.add(copy(obj, cls));
            }
            return result;
        }
    }

    /**
     * 拷贝属性,
     *
     * @param out 源bean
     * @param in  得到属性的bean
     */
    @SuppressWarnings("unchecked")
    public static void copyMethodValue(Object out, Object in) {
        if (out instanceof JSONObject) {
            out = ((JSONObject) out).toMap();
        }
        if (out instanceof Map) {
            Map<String,Object> map = (Map) out;
            Class<?> getClass = in.getClass();
            for (Object keyObj : map.keySet()) {
                if (keyObj == null) {
                    continue;
                }
                String key = ObjectUtil.toString(keyObj);
                Method method = ClassUtil.getSetMethod(getClass, key);
                if (method != null) {
                    setSimpleProperty(in, method.getName(), map.get(key));
                }
            }
            return;
        }

        Method[] methodsGet = ClassUtil.getDeclaredMethods(in.getClass());
        Method[] methodsOut = ClassUtil.getDeclaredMethods(out.getClass());
        for (Method outMethod : methodsOut) {
            if (outMethod.getName().startsWith(ClassUtil.METHOD_NAME_SET)) {
                continue;
            }
            if (outMethod.getGenericParameterTypes().length > 0) {
                continue;
            }
            try {
                for (Method method : methodsGet) {
                    if (!method.getName().startsWith(ClassUtil.METHOD_NAME_SET)) {
                        continue;
                    }
                    if (method.getName().substring(3).equals(outMethod.getName().substring(3))) {
                        Object oo = outMethod.invoke(out);
                        method.invoke(in, oo);
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("get " + in + "  copy:" + out + " set methods " + outMethod.getName(), e);
            }
        }
    }


    public final static int[] STOP_MODIFIERS = {18, 25, 26, 28,128};

    /**
     * 拷贝属性, 后边的数据拷贝到前边
     *
     * @param getData  源bean
     * @param newData 得到属性的bean
     */
    @SuppressWarnings("unchecked")
    public static void copyFiledValue(Object getData,Object newData) {

        if (getData instanceof JSONObject) {
            getData = ((JSONObject) getData).toMap();
        }

        if (ClassUtil.isProxy(getData.getClass())) {
            getData = ReflectUtil.getValueMap(getData);
        }

        if (getData instanceof Map) {
            Map<String,Object> map = (Map) getData;

            Class<?> getClass = newData.getClass();
            Map<String,String> fieldMap = new HashMap<>();
            List<SoberColumn> columns = AnnotationUtil.getColumnList(getClass);
            for (SoberColumn column:columns)
            {
                fieldMap.put(column.getCaption(),column.getName());
            }
            for (Object keyObj : map.keySet()) {
                if (keyObj == null) {
                    continue;
                }
                //两个都是key begin
                String key = ObjectUtil.toString(keyObj);
                Field field = ClassUtil.getDeclaredField(getClass, key,true);
                //两个都是key end

                if (field==null)
                {
                    key = fieldMap.get(key);
                    field = ClassUtil.getDeclaredField(getClass, key,true);
                }

                if (field == null&&newData instanceof PropertyContainer) {
                        PropertyContainer propertyContainer = (PropertyContainer)newData;
                        propertyContainer.put(key,map.get(key));
                }
                if (field == null) {
                    continue;
                }
                if (ArrayUtil.indexOf(STOP_MODIFIERS, field.getModifiers()) != -1) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    Object obj1 = null;
                    if (map.containsKey(key))
                    {
                        obj1 = map.get(key);
                    }
                    if (obj1==null && map.containsKey(ObjectUtil.toString(keyObj)))
                    {
                        obj1 = map.get(ObjectUtil.toString(keyObj));
                    }
                    String fieldValue = ObjectUtil.toString(obj1);
                    Object obj = TypeUtil.getTypeValue(field.getType().getName(),fieldValue);
                    field.set(newData,obj);
                } catch (Exception e) {
                    log.error("class={},field={},data={}",getClass,field.getName(),map.get(key), e);
                }
            }
            if (!(getData instanceof PropertyContainer))
            {
                return;
            }
        }

        //保存已经处理过的字段
        List<String> doFields = new ArrayList<>();
        Field[] fieldGet = ClassUtil.getDeclaredFields(newData.getClass());
        Field[] fieldSet = ClassUtil.getDeclaredFields(getData.getClass());
        PropertyContainer propertyContainer = null;
        if (getData instanceof PropertyContainer)
        {
            propertyContainer = (PropertyContainer)getData;
        }

        for (Field setField : fieldSet) {
            for (Field field : fieldGet) {
                if (ArrayUtil.indexOf(STOP_MODIFIERS, field.getModifiers()) != -1) {
                    continue;
                }
                if (field.getName().equals(setField.getName())) {
                    doFields.add(field.getName());
                    putFieldValue( field, setField, getData, newData);
                }
            }
        }
        for (Field field : fieldGet) {
            if (doFields.contains(field.getName()))
            {
                continue;
            }
            if (propertyContainer!=null && propertyContainer.containsKey(field.getName()))
            {
                putFieldValue( field, propertyContainer.get(field.getName()), newData);

            }
        }
        doFields.clear();

    }

    private static void putFieldValue(Field field,Object o,Object newData)
    {
        try {
            field.setAccessible(true);
            if (o==null && ClassUtil.isBaseNumberType(field.getType()))
            {
                field.set(newData, 0);
            } else
            if (o==null && !ClassUtil.isBaseNumberType(field.getType()))
            {
                field.set(newData,null);
            }
            else if (o!=null&&ClassUtil.isNumberType(o.getClass()) && ClassUtil.isNumberType(field.getType())) {
                //todo字符串 和数字类型需要避开
                Object col =field.get(newData);
                if ((col instanceof Collection)&& !ObjectUtil.isEmpty(col))
                {
                    Collection coll = (Collection)col;
                    Object obj = coll.iterator().next();
                    if (obj!=null)
                    {

                        Class<?> type = obj.getClass();
                        field.set(newData, copyList((Collection)o,type));
                    }
                }
                else
                {

                    field.set(newData, o);
                }
            }
            else
            {
                //对象二次拷贝
                String typeModel = field.getGenericType().getTypeName();
                typeModel = StringUtil.substringOutBetween(typeModel,"<",">");
                if (ClassUtil.isCollection(field.getType()) && !StringUtil.isEmpty(typeModel))
                {
                    field.set(newData,copyList((Collection<?>)o,ClassUtil.loadClass(typeModel)));
                }
                else if (field.getType().equals(Map.class))
                {
                    JSONObject json = new JSONObject(newData);
                    field.set(newData,json);
                }
                else if (o!=null&&field.getType().equals(String.class) && ClassUtil.isNumberType(o.getClass()))
                {
                    field.set(newData,ObjectUtil.toString(o));
                }
                else
                {
                    field.set(newData,copy(o, (Class<?>)field.getType()));
                }
            }
        } catch (Exception e) {
            log.error(field.getName() + " Modifiers=" + field.getModifiers(), e);
        }
    }

    private static void putFieldValue(Field field,Field setField,Object getData,Object newData)
    {
        try {
            field.setAccessible(true);
            setField.setAccessible(true);
            Object o = setField.get(getData);
            if (o==null && ClassUtil.isBaseNumberType(field.getType()))
            {
                field.set(newData, 0);
            }
            else if (setField.getGenericType().equals(field.getGenericType()) || ClassUtil.isNumberType(setField.getType()) && ClassUtil.isNumberType(field.getType())) {
                //todo字符串 和数字类型需要避开
                Object col =field.get(newData);
                if ((col instanceof Collection)&& !ObjectUtil.isEmpty(col))
                {
                    Collection coll = (Collection)col;
                    Object obj = coll.iterator().next();
                    if (obj!=null)
                    {

                        Class<?> type = obj.getClass();
                        field.set(newData, copyList((Collection)o,type));
                    }
                }
                else
                {

                    field.set(newData, o);
                }
            }
            else
            {
                //对象二次拷贝
                String typeModel = field.getGenericType().getTypeName();
                typeModel = StringUtil.substringOutBetween(typeModel,"<",">");
                if (ClassUtil.isCollection(field.getType()) && !StringUtil.isEmpty(typeModel))
                {
                    field.set(newData,copyList((Collection<?>)o,ClassUtil.loadClass(typeModel)));
                }
                else if (field.getType().equals(Map.class))
                {
                    JSONObject json = new JSONObject(newData);
                    field.set(newData,json);
                }
                else if (o!=null&&field.getType().equals(String.class) && ClassUtil.isNumberType(o.getClass()))
                {
                    field.set(newData,ObjectUtil.toString(o));
                }
                else
                {
                    field.set(newData,copy(o, (Class<?>)field.getType()));
                }
            }
        } catch (Exception e) {
            log.error("{} Modifiers={}",field.getName(),field.getModifiers(), e);
        }
    }
    /**
     *
     * @param list 列表
     * @param field 字段
     * @param dis 去重
     * @param anyField 不区分大小写
     * @param <T> 泛型类型
     * @return 列表
     */
    static public <T> List<T> copyFieldList(Collection<?> list, String field, boolean dis,boolean anyField)
    {
        return copyFieldList(list,  field, null,dis, anyField);
    }

    /**
     *
     * @param list 列表
     * @param field 字段
     * @param dis 去重
     * @param <T> 泛型类型
     * @return 列表
     */
    static public <T> List<T> copyFieldList(Collection<?> list, String field, boolean dis)
    {
        return copyFieldList(list,  field, null,dis,false);
    }

    /**
     *
     * @param list 列表
     * @param field 字段
     * @param cls 类对象
     * @param <T> 泛型类型
     * @return 列表
     */
    static public <T> List<T> copyFieldList(Collection<?> list, String field,  Class<T> cls)
    {
        return copyFieldList(list,  field, cls,false,false);
    }
    /**
     * 拷贝字段列
     *
     * @param list  列表
     * @param field 字段
     * @param <T>   泛型类型
     * @return 列表
     */
    static public <T> List<T> copyFieldList(Collection<?> list, String field)
    {
        return copyFieldList( list,  field,false,false);
    }

    /**
     *
     * @param list 列表
     * @param field 字段
     * @param cls 类
     * @param dis 是否去重
     * @param anyField 不区分大小写
     * @param <T> 泛型类型
     * @return 列表
     */
    static public <T> List<T> copyFieldList(Collection<?> list, String field, Class<T> cls,boolean dis,boolean anyField)
    {
        if (ObjectUtil.isEmpty(list)) {
            return new ArrayList<>(0);
        }
        List<T> result = new LinkedList<>();
        for (Object bean : list) {
            T v = null;
            if (cls!=null)
            {
                v = getFieldValue(bean, field,cls,anyField);
            } else
            {
                v = getFieldValue(bean, field ,anyField);
            }
            if (dis)
            {
                //是否去重
                if (!result.contains(v))
                {
                    result.add(v);
                }
            } else
            {
                result.add(v);
            }
        }
        return result;
    }
    /**
     * @param f1 方法数组1
     * @param f2 方法数组2
     * @return 合并两个方法数组
     */
    public static Method[] joinMethodArray(Method[] f1, Method[] f2) {
        if (f1 == null) {
            return f2;
        }
        if (f2 == null) {
            return f1;
        }
        Method[] result = new Method[f1.length + f2.length];
        for (int i = 0; i < result.length; i++) {
            if (i < f1.length) {
                result[i] = f1[i];
            } else {
                result[i] = f2[i - f1.length];
            }
        }
        return result;
    }


    public static Method[] appendMethodArray(Method[] f1, Method f2) {
        if (f1 == null && f2 == null) {
            return null;
        }
        if (f2 == null) {
            return f1;
        }
        if (f1 == null) {
            Method[] result = new Method[1];
            result[0] = f2;
            return result;
        }
        Method[] result = new Method[f1.length + 1];
        System.arraycopy(f1, 0, result, 0, f1.length);
        result[f1.length] = f2;
        return result;
    }


    /**
     * 对象转换位字符串，主要为了调试方便
     *
     * @param o 对象
     * @return 对象转换位字符串
     */
    public static String toString(Object o) {
        Map<String,Object> map = ObjectUtil.getMap(o);
        StringBuilder sb = new StringBuilder();
        for (Object obj : map.values()) {
            sb.append(obj).append(" ");
        }
        return sb.toString();
    }


    /**
     * @param object bean 对象
     * @param string 调用一个方法
     * @param args   参数
     * @return 执行返回
     * @throws Exception 运行错误
     */
    public static Object invoke(Object object, String string, Object... args) throws Exception {
        if (object == null || string == null) {
            return null;
        }
        Method method = null;
        if (args==null || args.length==0)
        {
            method = object.getClass().getMethod(string);
            if (Modifier.isStatic(method.getModifiers()))
            {
                return ClassUtil.invokeStaticMethod(object.getClass().getName(),method.getName(),null);
            } else {
                return method.invoke(object);
            }
        }
        method = ClassUtil.getDeclaredMethod(object.getClass(),string,args.length);
        if (Modifier.isStatic(method.getModifiers()))
        {
            return ClassUtil.invokeStaticMethod(object.getClass().getName(),method.getName(),args);
        } else {
            return method.invoke(object,args);
        }
    }


    /**
     * 设置数据对象,如果为空的字符串转换为""
     * @param o 数据对象
     */
    public static void stringNullToEmpty(Object o) {
        if (o==null)
        {
            return;
        }
        if (ClassUtil.isStandardProperty(o.getClass()))
        {
            return;
        }
        if (ClassUtil.isArrayType(o.getClass()) && o.getClass().isArray())
        {
            int length = Array.getLength(o);
            for (int i=0;i<length;i++)
            {
                Object tmp = Array.get(o,i);
                if (tmp instanceof String && StringUtil.isNull((String)tmp))
                {
                    Array.set(o,i,StringUtil.empty);
                }
            }
        } else
        if (ClassUtil.isCollection(o))
        {
            List<Object> tmpList = (List<Object>)o;
            for (int i=0;i<tmpList.size();i++)
            {
                Object tmp = tmpList.get(i);
                if (tmp instanceof String && StringUtil.isNull((String)tmp))
                {
                    tmpList.set(i,StringUtil.empty);
                }
            }

        } else
        if (o instanceof Map)
        {
            Map<Object,Object> map = (Map<Object,Object>)o;
            for (Object key:map.keySet())
            {
                Object obj = map.get(key);
                if (obj instanceof String && StringUtil.isNull((String)obj))
                {
                    map.put(key,StringUtil.empty);
                }
            }
        }
        Field[] fields = ClassUtil.getDeclaredFields(o.getClass());
        if (fields != null) {
            for (Field field : fields) {
                if (ArrayUtil.indexOf(STOP_MODIFIERS, field.getModifiers()) != -1) {
                    continue;
                }
                try {
                    Type aType = field.getGenericType();
                    if (aType.equals(String.class))
                    {
                        field.setAccessible(true);
                        Object v = field.get(o);
                        if (v==null)
                        {
                            field.set(o,StringUtil.empty);
                        }
                    } else if (ClassUtil.isArrayType(aType))
                    {
                        field.setAccessible(true);
                        Object v = field.get(o);
                        if (v instanceof List)
                        {
                            List<Object> tmpList = (List<Object>)v;
                            for (int i=0;i<tmpList.size();i++)
                            {
                                Object tmp = tmpList.get(i);
                                if (tmp instanceof String && StringUtil.isNull((String)tmp))
                                {
                                    tmpList.set(i,StringUtil.empty);
                                }
                            }
                        } else {
                            int length = Array.getLength(v);
                            for (int i=0;i<length;i++)
                            {
                                Object tmp = Array.get(o,i);
                                if (tmp instanceof String && StringUtil.isNull((String)tmp))
                                {
                                    Array.set(v,i,StringUtil.empty);
                                }
                            }
                        }
                    } else
                    if (ClassUtil.isCollection(aType))
                    {
                        field.setAccessible(true);
                        Object v = field.get(o);
                        List<Object> tmpList = (List<Object>)v;
                        for (int i=0;i<tmpList.size();i++)
                        {
                            Object tmp = tmpList.get(i);
                            if (tmp instanceof String && StringUtil.isNull((String)tmp))
                            {
                                tmpList.set(i,StringUtil.empty);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(o + "   method=" + field.getName(), e);
                }
            }
        }
    }

    /**
     *
     * @param o  各种空转化为空白
     */
    public static void stringNullOrWhiteSpaceToEmpty(Object o) {
        if (o==null)
        {
            return;
        }
        if (ClassUtil.isStandardProperty(o.getClass()))
        {
            return;
        } else
        if (ClassUtil.isArrayType(o.getClass()) && o.getClass().isArray())
        {
            int length = Array.getLength(o);
            for (int i=0;i<length;i++)
            {
                Object tmp = Array.get(o,i);
                if (ObjectUtil.isNullOrWhiteSpace(tmp))
                {
                    Array.set(o,i,StringUtil.empty);
                }  else {
                    stringNullOrWhiteSpaceToEmpty(tmp);
                }
            }
        } else
        if (ClassUtil.isCollection(o))
        {
            List<Object> tmpList = (List<Object>)o;
            for (int i=0;i<tmpList.size();i++)
            {
                Object tmp = tmpList.get(i);
                if (ObjectUtil.isNullOrWhiteSpace(tmp))
                {
                    tmpList.set(i,StringUtil.empty);
                } else {
                    stringNullOrWhiteSpaceToEmpty(tmp);
                }
            }
        } else
        if (o instanceof Map)
        {
            Map<Object,Object> map = (Map<Object,Object>)o;
            for (Object key:map.keySet())
            {
                Object obj = map.get(key);
                if (ObjectUtil.isNullOrWhiteSpace(obj) )
                {
                    map.put(key,StringUtil.empty);
                } else {
                    stringNullOrWhiteSpaceToEmpty(obj);
                }
            }
        }
        Field[] fields = ClassUtil.getDeclaredFields(o.getClass());
        if (fields != null) {
            for (Field field : fields) {
                if (ArrayUtil.indexOf(BeanUtil.STOP_MODIFIERS, field.getModifiers()) != -1) {
                    continue;
                }
                try {
                    Type aType = field.getGenericType();
                    if (aType.equals(String.class))
                    {
                        field.setAccessible(true);
                        Object v = field.get(o);
                        if (ObjectUtil.isNullOrWhiteSpace(v))
                        {
                            field.set(o,StringUtil.empty);
                        }
                    } else if (ClassUtil.isArrayType(aType))
                    {
                        field.setAccessible(true);
                        Object v = field.get(o);
                        if (v instanceof List)
                        {
                            List<Object> tmpList = (List<Object>)v;
                            for (int i=0;i<tmpList.size();i++)
                            {
                                Object tmp = tmpList.get(i);
                                if (ObjectUtil.isNullOrWhiteSpace(tmp))
                                {
                                    tmpList.set(i,StringUtil.empty);
                                }  else {
                                    stringNullOrWhiteSpaceToEmpty(tmp);
                                }
                            }
                        } else {
                            int length = Array.getLength(v);
                            for (int i=0;i<length;i++)
                            {
                                Object tmp = Array.get(v,i);
                                if (ObjectUtil.isNullOrWhiteSpace(tmp))
                                {
                                    Array.set(v,i,StringUtil.empty);
                                }  else {
                                    stringNullOrWhiteSpaceToEmpty(tmp);
                                }
                            }
                        }
                    } else
                    if (ClassUtil.isCollection(aType))
                    {
                        field.setAccessible(true);
                        Object v = field.get(o);
                        List<Object> tmpList = (List<Object>)v;
                        for (int i=0;i<tmpList.size();i++)
                        {
                            Object tmp = tmpList.get(i);
                            if (ObjectUtil.isNullOrWhiteSpace(tmp))
                            {
                                tmpList.set(i,StringUtil.empty);
                            }  else {
                                stringNullOrWhiteSpaceToEmpty(tmp);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(o + "   method=" + field.getName(), e);
                }
            }
        }
    }


    /**
     * 将数据中的字符串全角，转半角
     * @param o bean 对象
     */
     public static void stringFullToHalf(Object o) {
        if (o==null)
        {
            return;
        }
        if (ClassUtil.isStandardProperty(o.getClass()))
        {
            return;
        }
        if (ClassUtil.isArrayType(o.getClass()) && o.getClass().isArray())
        {
            int length = ArrayUtil.getLength(o);
            for (int i=0;i<length;i++)
            {

                Object object = ArrayUtil.get((Object[])o,i);
                if (object instanceof String)
                {
                    Array.set(o,i,StringUtil.fullToHalf((String) object));
                }
            }
        } else
        if (ClassUtil.isCollection(o))
        {
            List<Object> tmpList = (List<Object>)o;
            for (int i=0;i<tmpList.size();i++)
            {
                Object tmp = tmpList.get(i);
                if (tmp instanceof  String)
                {
                    tmpList.set(i,StringUtil.fullToHalf((String) tmp));
                }
            }
        } else
        if (o instanceof Map)
         {
             Map<Object,Object> map = (Map<Object,Object>)o;
             for (Object key:map.keySet())
             {
                 Object tmp = map.get(key);
                 if (tmp instanceof  String)
                 {
                     map.put(key,StringUtil.fullToHalf((String) tmp));
                 }
             }
         }
        Field[] fields = ClassUtil.getDeclaredFields(o.getClass());
        if (fields != null) {
            for (Field field : fields) {
                if (ArrayUtil.indexOf(BeanUtil.STOP_MODIFIERS, field.getModifiers()) != -1) {
                    continue;
                }

                try {
                    Type aType = field.getGenericType();
                    if (aType.equals(String.class))
                    {
                        field.setAccessible(true);
                        Object v = field.get(o);
                        if (v instanceof String && StringUtil.isNullOrWhiteSpace((String)v))
                        {
                            field.set(o,StringUtil.empty);
                        } else {
                            field.set(o,StringUtil.fullToHalf((String)v));
                        }
                    } else if (ClassUtil.isArrayType(aType))
                    {
                        field.setAccessible(true);
                        Object v = field.get(o);
                        if (v instanceof List)
                        {
                            List<Object> tmpList = (List<Object>)v;
                            for (int i=0;i<tmpList.size();i++)
                            {
                                Object tmp = tmpList.get(i);
                                if (tmp instanceof  String)
                                {
                                    tmpList.set(i,StringUtil.fullToHalf((String) tmp));
                                }
                            }
                        } else {
                            int length = ArrayUtil.getLength(v);
                            for (int i=0;i<length;i++)
                            {

                                Object object = ArrayUtil.get((Object[])o,i);
                                if (object instanceof String)
                                {
                                    Array.set(v,i,StringUtil.fullToHalf((String) object));
                                }
                            }
                        }
                    } else
                    if (ClassUtil.isCollection(aType))
                    {
                        field.setAccessible(true);
                        Object v = field.get(o);
                        List<Object> tmpList = (List<Object>)v;
                        for (int i=0;i<tmpList.size();i++)
                        {
                            Object tmp = tmpList.get(i);
                            if (tmp instanceof  String)
                            {
                                tmpList.set(i,StringUtil.fullToHalf((String) tmp));
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(o + "   method=" + field.getName(), e);
                }
            }
        }
    }


}