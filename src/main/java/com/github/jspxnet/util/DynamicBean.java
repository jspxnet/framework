package com.github.jspxnet.util;

import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.utils.ReflectUtil;
import net.sf.cglib.beans.BeanMap;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class DynamicBean implements Serializable {

    private final BeanMap propertyMap;
    /**
     * 实体Object
     */
    @JsonIgnore
    private final Object target;
    public DynamicBean(Class<?> cls,Map<String, Class<?>> propertyMap) {
        this.target = ReflectUtil.generateBean(cls,propertyMap);
        this.propertyMap = BeanMap.create(this.target);
    }

    public DynamicBean(Map<String, Class<?>> propertyMap) {

        this.target = ReflectUtil.generateBean(propertyMap);

        this.propertyMap = BeanMap.create(this.target);
    }
    /*
     */

    /**
     * 给bean属性赋值
     *
     * @param property 属性名
     * @param value    值
     */
    public void setValue(String property, Object value) {
        if (!"empty".equals(property)) {
            propertyMap.put(property, value);
        }
    }

    /**
     * 通过属性名得到属性值
     *
     * @param property 属性名
     * @return 值
     */
    public Object getValue(String property) {
        return propertyMap.get(property);
    }

    /**
     * @return 得到该实体bean对象
     */
    public Object getTarget() {
        return target;
    }

    /**
     * @return 返回所有属性
     */
    public Set<String> keySet() {
        return propertyMap.keySet();
    }



}