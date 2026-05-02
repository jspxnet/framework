package com.github.jspxnet.utils;

import javassist.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Javassist 应用，动态创建一个动态类和相关属性
 * <pre>{@code
 *  Map<String,Object> properties =  new HashMap<>();
 *  properties.put("address","浙江杭州");
 *  properties.put("age",26);
 *  Object testBean1 = ReflectUtil.createDynamicBean(testBean,properties);
 * 
 *  }</pre>
 * 
 * ReflectUtil 是一个动态创建类的工具类，可以根据属性动态创建一个类，并为该类增加属性。使用Javassist来实现
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
        try {
            ClassPool pool = ClassPool.getDefault();
            
            // 生成唯一的类名
            String className = BeanUtil.JAVASSIST_ENTITY_START+"DynamicBean.";

            if (superClass != null) {
                className = BeanUtil.JAVASSIST_ENTITY_START+"DynamicBean." +  superClass.getName();
            }
            className = className + StringUtil.DOT + Thread.currentThread().getId();

            CtClass ctClass = pool.get(className);
            if (ctClass==null)
            {
                ctClass = pool.makeClass(className);
            }

            // 设置父类
            if (superClass != null) {
                ctClass.setSuperclass(pool.get(superClass.getName()));
            }
            
            // 添加属性和相应的getter/setter方法
            for (Map.Entry<String, Class<?>> entry : propertyMap.entrySet()) {
                String fieldName = entry.getKey();
                Class<?> fieldType = entry.getValue();
                
                // 添加字段
                CtClass fieldTypeCt = pool.get(fieldType.getName());
                CtField field = new CtField(fieldTypeCt, fieldName, ctClass);
                field.setModifiers(javassist.Modifier.PRIVATE);
                ctClass.addField(field);
                
                // 添加getter方法
                String getterName = "get" + StringUtil.capitalize(fieldName);
                CtMethod getter = CtNewMethod.getter(getterName, field);
                ctClass.addMethod(getter);
                
                // 添加setter方法
                String setterName = "set" + StringUtil.capitalize(fieldName);
                CtMethod setter = CtNewMethod.setter(setterName, field);
                ctClass.addMethod(setter);
            }
            
            // 生成类
           // Class<?> clazz = ctClass.toClass();
            @SuppressWarnings("unchecked")
            Class<T> generatedClass = (Class<T>) ctClass.toClass();
            return generatedClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("创建动态Bean失败: " + e.getMessage(), e);
        }
    }


    /**
     * 动态为对象增加属性
     *
     * @param destObj            要增加属性的对象，可以是类模板，也可以是一个 有值的实体，值可以保留在新的动态对象中
     * @param addProperties   要增加的属性名及值，属性名可以和原有属性重名，但如果重名的话，值的类型必须一致，否则抛异常
     * @return 返回增加了属性的新对象
     */
    public static Object createDynamicBean(Object destObj, Map<String, Object> addProperties) {

        Object dest = destObj;
        Class<?>  originalClass = null;
        if (destObj instanceof Class)
        {
            originalClass = ( Class<?> )destObj;
        } else {
            originalClass = destObj.getClass();
        }
        try {
            ClassPool pool = ClassPool.getDefault();
            // 获取原始类的信息
            String className = BeanUtil.JAVASSIST_ENTITY_START+ "ExtendedBean." + originalClass + StringUtil.DOT  +  UUID.randomUUID().toString().replace("-", StringUtil.empty);
            CtClass ctClass = pool.get(className);
            if (ctClass==null)
            {
                ctClass = pool.makeClass(className);
                // 设置父类为原始类
                ctClass.setSuperclass(pool.get(originalClass.getName()));
            }



            java.lang.reflect.Field[] originalFields = originalClass.getDeclaredFields();
            // 获取原始类的所有字段并复制
            if (!(destObj instanceof Class))
            {
                for (java.lang.reflect.Field field : originalFields) {
                    if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                        CtField ctField = new CtField(pool.get(field.getType().getName()), field.getName(), ctClass);
                        ctField.setModifiers(javassist.Modifier.PRIVATE);
                        ctClass.addField(ctField);

                        // 添加getter方法
                        String getterName = "get" + StringUtil.capitalize(field.getName());
                        CtMethod getter = CtNewMethod.getter(getterName, ctField);
                        ctClass.addMethod(getter);

                        // 添加setter方法
                        String setterName = "set" + StringUtil.capitalize(field.getName());
                        CtMethod setter = CtNewMethod.setter(setterName, ctField);
                        ctClass.addMethod(setter);
                    }
                }
            }

            
            // 添加新的属性
            for (Map.Entry<String, Object> entry : addProperties.entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();
                Class<?> fieldType = fieldValue != null ? fieldValue.getClass() : String.class;
                
                // 检查是否与现有字段重名
                boolean isExistingField = false;
                for (java.lang.reflect.Field originalField : originalFields) {
                    if (originalField.getName().equals(fieldName)) {
                        isExistingField = true;
                        // 验证类型一致性
                        if (!originalField.getType().isAssignableFrom(fieldType)) {
                            throw new IllegalArgumentException("字段重名但类型不一致: " + fieldName);
                        }
                        break;
                    }
                }
                
                if (!isExistingField) {
                    // 添加新字段
                    CtField newField = new CtField(pool.get(fieldType.getName()), fieldName, ctClass);
                    newField.setModifiers(javassist.Modifier.PRIVATE);
                    ctClass.addField(newField);
                    
                    // 添加getter方法
                    String getterName = "get" + StringUtil.capitalize(fieldName);
                    CtMethod getter = CtNewMethod.getter(getterName, newField);
                    ctClass.addMethod(getter);
                    
                    // 添加setter方法
                    String setterName = "set" + StringUtil.capitalize(fieldName);
                    CtMethod setter = CtNewMethod.setter(setterName, newField);
                    ctClass.addMethod(setter);
                }
            }
            
            // 生成类
            Class<?> extendedClass = ctClass.toClass();
            Object newInstance = extendedClass.newInstance();
            
            // 复制原始对象的属性值
            if (!(destObj instanceof Class))
            {
                for (java.lang.reflect.Field field : originalFields) {
                    if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                        field.setAccessible(true);
                        Object value = field.get(dest);
                        java.lang.reflect.Field newInstanceField = extendedClass.getDeclaredField(field.getName());
                        newInstanceField.setAccessible(true);
                        newInstanceField.set(newInstance, value);
                    }
                }

            }

            // 设置新增的属性值
            for (Map.Entry<String, Object> entry : addProperties.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();
                
                try {
                    java.lang.reflect.Method setter = extendedClass.getMethod("set" + StringUtil.capitalize(fieldName), value.getClass());
                    setter.invoke(newInstance, value);
                } catch (NoSuchMethodException e) {
                    // 如果类型不匹配，尝试查找兼容类型的setter
                    java.lang.reflect.Method[] methods = extendedClass.getMethods();
                    for (java.lang.reflect.Method method : methods) {
                        if (method.getName().equals("set" + StringUtil.capitalize(fieldName)) && method.getParameterCount() == 1) {
                            Class<?> paramType = method.getParameterTypes()[0];
                            if (paramType.isAssignableFrom(value.getClass()) || isCompatibleType(paramType, value)) {
                                method.invoke(newInstance, value);
                                break;
                            }
                        }
                    }
                }
            }
            
            return newInstance;
        } catch (Exception e) {
            throw new RuntimeException("创建动态Bean失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 检查值是否与参数类型兼容
     */
    private static boolean isCompatibleType(Class<?> paramType, Object value) {
        if (value == null) {
            return !paramType.isPrimitive();
        }
        if (paramType == int.class && value instanceof Integer) return true;
        if (paramType == long.class && value instanceof Long) return true;
        if (paramType == float.class && value instanceof Float) return true;
        if (paramType == double.class && value instanceof Double) return true;
        if (paramType == boolean.class && value instanceof Boolean) return true;
        if (paramType == byte.class && value instanceof Byte) return true;
        if (paramType == char.class && value instanceof Character) return true;
        if (paramType == short.class && value instanceof Short) return true;
        return false;
    }

    /**
     * 支持数据库的动态bean
     *
     * @param addProperties 添加属性
     * @return 支持数据库的动态bean
     */
    public static Object createDynamicBean(Map<String, Object> addProperties) {
        try {
            // 关键修改1：放弃全局ClassPool，创建独立的ClassPool实例（继承默认搜索路径）
            ClassPool pool = new ClassPool(true);

            String className = BeanUtil.JAVASSIST_ENTITY_START+ "Dynamic."  +  UUID.randomUUID().toString().replace("-", StringUtil.empty);
            CtClass ctClass = pool.makeClass(className);


            // 添加基础Object类的功能
            for (Map.Entry<String, Object> entry : addProperties.entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();
                Class<?> fieldType = fieldValue != null ? fieldValue.getClass() : String.class;
                
                // 添加字段
                CtClass fieldTypeCt = pool.get(fieldType.getName());
                CtField field = new CtField(fieldTypeCt, fieldName, ctClass);
                field.setModifiers(javassist.Modifier.PRIVATE);
                ctClass.addField(field);
                
                // 添加getter方法
                String getterName = "get" + StringUtil.capitalize(fieldName);
                CtMethod getter = CtNewMethod.getter(getterName, field);
                ctClass.addMethod(getter);
                
                // 添加setter方法
                String setterName = "set" + StringUtil.capitalize(fieldName);
                CtMethod setter = CtNewMethod.setter(setterName, field);
                ctClass.addMethod(setter);
            }
            
            // 生成类
            Class<?> generatedClass = ctClass.toClass();
            Object instance = generatedClass.newInstance();
            
            // 设置属性值
            for (Map.Entry<String, Object> entry : addProperties.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();
                
                try {
                    java.lang.reflect.Method setter = generatedClass.getMethod("set" + StringUtil.capitalize(fieldName), value.getClass());
                    setter.invoke(instance, value);
                } catch (NoSuchMethodException e) {
                    // 如果类型不匹配，尝试查找兼容类型的setter
                    java.lang.reflect.Method[] methods = generatedClass.getMethods();
                    for (java.lang.reflect.Method method : methods) {
                        if (method.getName().equals("set" + StringUtil.capitalize(fieldName)) && method.getParameterCount() == 1) {
                            Class<?> paramType = method.getParameterTypes()[0];
                            if (paramType.isAssignableFrom(value.getClass()) || isCompatibleType(paramType, value)) {
                                method.invoke(instance, value);
                                break;
                            }
                        }
                    }
                }
            }
            
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("创建动态Bean失败: " + e.getMessage(), e);
        }
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
        Map<String,Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        
        // 获取所有声明的字段
        java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                try {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    Object value = field.get(obj);
                    map.put(fieldName, value);
                } catch (IllegalAccessException e) {
                    // 忽略无法访问的字段
                }
            }
        }
        return map;
    }


/*    public static void main(String[] args) {
          Map<String,Object> properties =  new HashMap<>();
          properties.put("address","浙江杭州");
          properties.put("age",26);
          Object testBean1 = ReflectUtil.createDynamicBean(BaseEntity.class, properties);
          Object testBean2 = ReflectUtil.createDynamicBean(new BaseEntity(), properties);
          Object testBean3 = ReflectUtil.createDynamicBean(properties);
          System.out.println(ObjectUtil.toString(testBean1,4));
          System.out.println(ObjectUtil.toString(testBean2,4));
          System.out.println(ObjectUtil.toString(testBean3,4));
    }*/
}
