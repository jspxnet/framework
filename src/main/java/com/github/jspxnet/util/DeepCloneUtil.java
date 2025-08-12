package com.github.jspxnet.util;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 深度拷贝对象
 */
public class DeepCloneUtil {
    private DeepCloneUtil()
    {

    }
    // 缓存已拷贝对象，解决循环引用问题
    private static final Map<Object, Object> CLONE_CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T deepClone(T obj) {
        if (obj == null) {
            return null;
        }

        // 检查缓存，避免循环引用导致的无限递归
        if (CLONE_CACHE.containsKey(obj)) {
            return (T) CLONE_CACHE.get(obj);
        }

        // 处理基本类型及其包装类
        if (obj instanceof Number || obj instanceof Boolean ||
                obj instanceof Character || obj instanceof String) {
            return obj;
        }

        // 处理数组
        if (obj.getClass().isArray()) {
            return cloneArray(obj);
        }

        // 处理集合
        if (obj instanceof Collection) {
            return (T) cloneCollection((Collection<?>) obj);
        }

        // 处理Map
        if (obj instanceof Map) {
            return (T) cloneMap((Map<?, ?>) obj);
        }

        // 处理其他对象
        try {
            T clonedObj = cloneObject(obj);
            CLONE_CACHE.put(obj, clonedObj);
            return clonedObj;
        } catch (Exception e) {
            throw new RuntimeException("Deep clone failed for object: " + obj.getClass(), e);
        } finally {
            if (!CLONE_CACHE.isEmpty()) {
                CLONE_CACHE.clear();
            }
        }
    }

    private static <T> T cloneArray(T array) {
        int length = Array.getLength(array);
        Class<?> componentType = array.getClass().getComponentType();
        Object newArray = Array.newInstance(componentType, length);

        CLONE_CACHE.put(array, newArray);

        if (componentType.isPrimitive()) {
            System.arraycopy(array, 0, newArray, 0, length);
        } else {
            for (int i = 0; i < length; i++) {
                Array.set(newArray, i, deepClone(Array.get(array, i)));
            }
        }
        return (T) newArray;
    }

    private static <E> Collection<E> cloneCollection(Collection<E> collection) {
        Collection<E> newCollection;
        try {
            newCollection = collection.getClass().newInstance();
        } catch (Exception e) {
            // 默认使用ArrayList
            newCollection = new ArrayList<>();
        }

        CLONE_CACHE.put(collection, newCollection);

        for (E item : collection) {
            newCollection.add(deepClone(item));
        }
        return newCollection;
    }

    private static <K, V> Map<K, V> cloneMap(Map<K, V> map) {
        Map<K, V> newMap;
        try {
            newMap = map.getClass().newInstance();
        } catch (Exception e) {
            // 默认使用HashMap
            newMap = new HashMap<>();
        }

        CLONE_CACHE.put(map, newMap);

        for (Map.Entry<K, V> entry : map.entrySet()) {
            newMap.put(deepClone(entry.getKey()), deepClone(entry.getValue()));
        }
        return newMap;
    }

    @SuppressWarnings("unchecked")
    private static <T> T cloneObject(T obj) throws Exception {
        // 优先尝试序列化方式
        try {
            return serializableClone(obj);
        } catch (NotSerializableException e) {
            // 序列化失败则尝试反射clone方法
            try {
                Method cloneMethod = obj.getClass().getMethod("clone");
                cloneMethod.setAccessible(true);
                return (T) cloneMethod.invoke(obj);
            } catch (NoSuchMethodException ex) {
                throw new NotSerializableException("Object neither serializable nor cloneable: " + obj.getClass());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T serializableClone(T obj) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (T) ois.readObject();
    }
}
