package com.github.jspxnet.sioc.annotation;

import com.github.jspxnet.utils.StringUtil;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PropPrefix {
    String prefix() default StringUtil.empty;

}
