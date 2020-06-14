package com.github.jspxnet.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class DefaultTargetType<T> {

    private Type type;
    private Class<T> classType;

    @SuppressWarnings("unchecked")
    public DefaultTargetType() {
        Type superClass = getClass().getGenericSuperclass();
        this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        if (this.type instanceof ParameterizedType) {
            this.classType = (Class<T>) ((ParameterizedType) this.type).getRawType();
        } else {
            this.classType = (Class<T>) this.type;
        }
    }

    public Type getType() {
        return type;
    }

    public Class<T> getClassType() {
        return classType;
    }
}