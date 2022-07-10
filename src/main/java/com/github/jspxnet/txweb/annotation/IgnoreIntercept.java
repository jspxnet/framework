package com.github.jspxnet.txweb.annotation;

import com.github.jspxnet.txweb.Interceptor;
import com.github.jspxnet.utils.StringUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface IgnoreIntercept {
    //拦截器类
    Class<Interceptor> value();

}
