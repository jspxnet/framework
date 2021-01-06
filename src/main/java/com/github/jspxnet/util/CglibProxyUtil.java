package com.github.jspxnet.util;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/7/10 21:44
 * description: Cglib 创建对象
 **/
@Slf4j
public class CglibProxyUtil {

    /**
     *
     * @param cls 类型
     * @param callback 拦截器
     * @param <T> 类型
     * @return 对象
     */
    public static <T> T getProxyInstance(Class<T> cls, Callback callback){
        return (T) Enhancer.create(cls, callback);
    }


}
