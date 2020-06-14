package com.github.jspxnet.sioc.annotation;

import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.sioc.util.Empty;
import com.github.jspxnet.utils.StringUtil;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
    //默认为类名id
    String id() default StringUtil.empty;

    //默认为类名
    Class<?> bind() default Empty.class;

    //命名空间
    String namespace() default Sioc.global;

    //代理创建
    String create() default StringUtil.empty;

    //单例模式
    boolean singleton() default false;

    //初始化方法
    String initMethod() default StringUtil.empty;

    //卸载方法
    String destroyMethod() default StringUtil.empty;
}
