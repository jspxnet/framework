package com.github.jspxnet.sioc.annotation;

import java.lang.annotation.*;

/**
 * 类似 spingboot 的 @Import
 * 系统将会扫描这里路径里边是否有 Bean,如果有就加载到ioc容器中
 * 算是二级加载ioc方式
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportIoc {
    Class<?>[] value();
}
