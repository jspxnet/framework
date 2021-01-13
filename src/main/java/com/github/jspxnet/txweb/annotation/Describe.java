package com.github.jspxnet.txweb.annotation;

import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.utils.StringUtil;

import java.lang.annotation.*;


@Documented
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Describe {
    //这里保存文字太多了，还是保存文件路径吧
    //如果是文件名,可以包含多个，每个数组保存一个文件
    //路径在document目录
    String[] value() default StringUtil.empty;

    String namespace() default Sioc.global;

    //方法同名的时候区分标识
    String flag() default StringUtil.empty;
}
