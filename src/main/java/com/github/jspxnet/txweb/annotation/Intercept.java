package com.github.jspxnet.txweb.annotation;

import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.txweb.Interceptor;
import com.github.jspxnet.utils.StringUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/6 21:51
 * description: 拦截器注入标签
 * IgnoreIntercept
 *这个只是一个方法拦截器,不是类拦截器
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Intercept {
    //拦截器类
    Class<Interceptor> value();
    //命名空间
    String namespace() default StringUtil.empty;
}
