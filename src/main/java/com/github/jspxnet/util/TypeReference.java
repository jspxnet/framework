package com.github.jspxnet.util;

import java.lang.reflect.ParameterizedType;

public class TypeReference<T> extends com.alibaba.fastjson.TypeReference {
    public Class<T> getClassType() {
        if (super.getType() instanceof ParameterizedType) {
            return (Class<T>) ((ParameterizedType) this.getType()).getRawType();
        } else {
            return (Class<T>) this.getType();
        }
    }
}
