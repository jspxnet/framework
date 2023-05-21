package com.github.jspxnet.utils;

import com.github.jspxnet.util.DynamicBean;
import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;
import java.util.HashMap;
import java.util.Map;

/**
 * cglib-nodep-3.3.0 应用，动态创建一个动态类和相关属性
 * <pre>{@code
 *  Map<String,Object> properties =  new HashMap<>();
 *  properties.put("address","浙江杭州");
 *  properties.put("age",26);
 *  Object testBean1 = ReflectUtil.createDynamicBean(testBean,properties);
 *  }</pre>
 */
public final  class ReflectUtil {
    private ReflectUtil()
    {

    }
    /**
     * @param propertyMap 属性 map
     * @return 根据属性生成对象
     */
    public static Object generateBean(Map<String, Class<?>> propertyMap) {
        return generateBean(null, propertyMap);
    }

    /**
     *
     * @param superClass 基础对象类型
     * @param propertyMap 字段映射
     * @param <T> 类型
     * @return 根据属性生成对象
     */
    public static <T> T generateBean(Class<T> superClass, Map<String, Class<?>> propertyMap) {
        BeanGenerator generator = new BeanGenerator();
        if (superClass != null) {
            generator.setSuperclass(superClass);
        }
        BeanGenerator.addProperties(generator, propertyMap);
        return (T)generator.create();
    }

    /**
     * 动态为对象增加属性
     *
     * @param dest            要增加属性的对象
     * @param addProperties   要增加的属性名及值，属性名可以和原有属性重名，但如果重名的话，值的类型必须一致，否则抛异常
     * @return 返回增加了属性的新对象
     */
    public static Object createDynamicBean(Object dest, Map<String, Object> addProperties) {
        Map<String, Class<?>> propertyMap = getMapPropertiesType(addProperties);
        DynamicBean dynamicBean = new DynamicBean(dest.getClass(),propertyMap);

        String[] fieldNames = ClassUtil.getDeclaredFieldNames(dest.getClass());

        // 添加旧属性值
        for (String fieldName:fieldNames)
        {
            try {
                if (!addProperties.containsKey(fieldName)) {
                    dynamicBean.setValue(fieldName, BeanUtil.getProperty(dest,fieldName));
                }
            } catch (Exception e) {
                throw new RuntimeException("对象添加旧属性失败，" + e.getMessage());
            }
        }

        // 添加新属性值
        addProperties.forEach((k, v) -> {
            try {
                dynamicBean.setValue(k, v);
            } catch (Exception e) {
                throw new RuntimeException("对象添加新属性失败，" + e.getMessage());
            }
        });
        return dynamicBean.getTarget();
    }

    /**
     * 支持数据库的动态bean
     *
     * @param addProperties 添加属性
     * @return 支持数据库的动态bean
     */
    public static Object createDynamicBean(Map<String, Object> addProperties) {
        Map<String, Class<?>> propertyMap = getMapPropertiesType(addProperties);
        DynamicBean dynamicBean = new DynamicBean(propertyMap);
        // 添加新属性值
        for (String k:addProperties.keySet())
        {
            try {
                Object o = addProperties.get(k);
                dynamicBean.setValue(k,o);
            } catch (Exception e) {
                throw new RuntimeException("对象添加新属性失败，" + e.getMessage());
            }
        }
        return dynamicBean.getTarget();
    }

    /**
     * 得到map 的属性类型
     * @param map map
     * @return 类型 map
     */
    public static Map<String, Class<?>> getMapPropertiesType(Map<String, Object> map) {
        Map<String, Class<?>> result = new HashMap<>();
        for (String key : map.keySet()) {
            Object obj = map.get(key);
            if (obj != null) {
                result.put(key, obj.getClass());
            } else {
                result.put(key, String.class);
            }
        }
        return result;
    }

    /**
     * 动态Bean 得到HashMap
     * @param obj 动态bean
     * @return map值
     */
    public static Map<String,Object> getValueMap(Object obj) {
        BeanMap beanMap = BeanMap.create(obj);
        Map<String,Object> map = new HashMap<>();
        for (Object entryObj:beanMap.entrySet())
        {
            Map.Entry<String, Object> entry = ( Map.Entry<String, Object>)entryObj;
            map.put(entry.getKey(),entry.getValue());
        }
        return map;
    }
}
