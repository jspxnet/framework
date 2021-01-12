package com.github.jspxnet.util;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


/**
 * 目前只做兼容一下,后期方便扩展
 * 例子:
 * <pre>{@code
 *     TypeReference<RocResponse<List<FrameworkSkinDto>>> typeReference = new TypeReference<RocResponse<List<FrameworkSkinDto>>>(){};
 *    typeReference.getType()
 *    }
 * </pre>
 * @param <T> 泛型
 */
public class TypeReference<T> extends TypeToken<T> implements Type  {

}
