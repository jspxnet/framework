package com.github.jspxnet.txweb.annotation;

import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.utils.StringUtil;

import java.lang.annotation.*;

/**
 * URL路径变量作为参数
 *<code>
 *
 *     Operate(caption = "路径参数", method = "/pname/{name}/{id}", post = false)
 *     public RocResponse getPathValue( PathVar(name = "name") String name, PathVar(name = "id") String id)
 *
 * </code>
 *
 *
 */
@Documented
@Target({ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PathVar {
    /**
     *
     * @return 描述
     */
    String caption() default StringUtil.empty;

    int min() default -1;

    /**
     * 字符串时默认最大为 50000
     * @return 返回限制
     */
    long max() default Integer.MAX_VALUE;

    /**
     * 字符串参数时才有效
     * @return 安全级别
     */
    SafetyEnumType level() default SafetyEnumType.LOW;
    /**
     *
     * @return 变量名称,如果为空,默认为当前变量名称
     */
    String name() default StringUtil.empty;

    //错误的时候显示的提示信息
    String message() default StringUtil.empty;
}
