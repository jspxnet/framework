/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.annotation;

import com.github.jspxnet.sober.enums.PropagationEnumType;
import com.github.jspxnet.utils.StringUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-8-2
 * Time: 12:56:04
 * 事务标签，这个标签只能使用在 txweb中
 * sober里边是直接调用，不是容器方式，所以拦截不到
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transaction {
    //事务类型
    PropagationEnumType propagation() default PropagationEnumType.DEFAULT;
    //事务错误提示
    String message() default StringUtil.empty;

}