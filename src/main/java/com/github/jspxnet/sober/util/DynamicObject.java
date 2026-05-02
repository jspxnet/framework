package com.github.jspxnet.sober.util;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sober.config.SoberTable;
import com.github.jspxnet.utils.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * 动态对象 - 类似金蝶苍穹的 DynamicObject
 * 用于保存任意结构的实体对象
 *
 * 特点：
 * 1. 元数据驱动 - 通过 SoberTable 定义结构
 * 2. 属性容器 - 内部使用 Map 存储值
 * 3. 类型转换 - 提供强类型访问方法
 * 4. 支持嵌套对象和集合
 */
public class DynamicObject {

    /** 对象元数据 */
    @Setter
    @Getter
    private SoberTable tableMeta;

    /** 属性值容器 */
    private final Map<String, Object> values = new HashMap<>();

    /** 扩展属性（不在元数据中定义的属性） */
    private final Map<String, Object> extendsMap = new HashMap<>();

    public DynamicObject() {
    }

    public DynamicObject(SoberTable tableMeta) {
        this.tableMeta = tableMeta;
    }



    // ==================== 基础访问 ====================

    /**
     * 设置属性值
     */
    public void set(String fieldName, Object value) {
        if (tableMeta != null && tableMeta.containsField(fieldName)) {
            values.put(fieldName, value);
        } else {
            extendsMap.put(fieldName, value);
        }
    }

    /**
     * 获取属性值
     */
    public Object get(String fieldName) {
        Object value = values.get(fieldName);
        if (value == null) {
            value = extendsMap.get(fieldName);
        }
        return value;
    }

    // ==================== 类型转换访问 ====================

    public String getString(String fieldName) {
        return ObjectUtil.toString(get(fieldName));
    }

    public int getInt(String fieldName) {
        return ObjectUtil.toInt(get(fieldName));
    }

    public long getLong(String fieldName) {
        return ObjectUtil.toLong(get(fieldName));
    }

    public double getDouble(String fieldName) {
        return ObjectUtil.toDouble(get(fieldName));
    }

    public boolean getBoolean(String fieldName) {
        return ObjectUtil.toBoolean(get(fieldName));
    }

    public Date getDate(String fieldName) {
        return ObjectUtil.toDate(get(fieldName));
    }

    // ==================== 嵌套对象 ====================

    /**
     * 获取关联对象（N:1）
     */
    public DynamicObject getDynamicObject(String fieldName) {
        Object obj = get(fieldName);
        if (obj instanceof DynamicObject) {
            return (DynamicObject) obj;
        }
        if (obj instanceof Map) {
            return new DynamicObject(null).fromMap((Map<String, Object>) obj);
        }
        return null;
    }

    /**
     * 获取关联对象集合（1:N）
     */
    public List<DynamicObject> getDynamicObjects(String fieldName) {
        Object obj = get(fieldName);
        if (obj instanceof List) {
            List<DynamicObject> result = new ArrayList<>();
            for (Object item : (List<?>) obj) {
                if (item instanceof DynamicObject) {
                    result.add((DynamicObject) item);
                } else if (item instanceof Map) {
                    result.add(new DynamicObject(null).fromMap((Map<String, Object>) item));
                }
            }
            return result;
        }
        return Collections.emptyList();
    }

    // ==================== JSON 转换 ====================

    /**
     * 转换为 JSONObject
     */
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        // 元数据属性
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof DynamicObject) {
                json.put(entry.getKey(), ((DynamicObject) value).toJSONObject());
            } else if (value instanceof Collection) {
                json.put(entry.getKey(), collectionToJsonArray((Collection<?>) value));
            } else {
                json.put(entry.getKey(), value);
            }
        }
        // 扩展属性
        for (Map.Entry<String, Object> entry : extendsMap.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        return json;
    }

    /**
     * 从 Map 填充
     */
    public DynamicObject fromMap(Map<String, Object> map) {
        if (map == null) return this;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * 从 JSONObject 填充
     */
    public DynamicObject fromJSONObject(JSONObject json) {
        if (json == null) return this;
        for (String key : json.keys()) {
            set(key, json.get(key));
        }
        return this;
    }

    // ==================== 辅助方法 ====================

    private Object collectionToJsonArray(Collection<?> collection) {
        List<Object> result = new ArrayList<>();
        for (Object item : collection) {
            if (item instanceof DynamicObject) {
                result.add(((DynamicObject) item).toJSONObject());
            } else {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 获取所有属性名
     */
    public Set<String> keySet() {
        Set<String> keys = new LinkedHashSet<>();
        keys.addAll(values.keySet());
        keys.addAll(extendsMap.keySet());
        return keys;
    }

    /**
     * 是否为空
     */
    public boolean isEmpty() {
        return values.isEmpty() && extendsMap.isEmpty();
    }

    /**
     * 转为 DataMap
     */
    public DataMap<String, Object> toDataMap() {
        DataMap<String, Object> map = new DataMap<>();
        map.putAll(values);
        map.putAll(extendsMap);
        return map;
    }

    @Override
    public String toString() {
        return toJSONObject().toString();
    }
}