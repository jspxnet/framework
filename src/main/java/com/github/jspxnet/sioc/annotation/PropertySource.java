package com.github.jspxnet.sioc.annotation;

import com.github.jspxnet.utils.StringUtil;
import java.lang.annotation.*;
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertySource {
    String name() default StringUtil.empty; //全局唯一的文件名称，或者为ID

    String[] value(); //.properties 文件名称,的文件路径，可以是多个

    boolean ignore() default true; //没有找到文件是否报错

    //默认编码
    String encoding() default "UTF-8";
}
