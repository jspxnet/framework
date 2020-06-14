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

import java.lang.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-24
 * Time: 23:51:27
 * 验证
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {

    //formId 校验Form ID
    String id() default "";

    //jspx.test.sioc 命名空间  自动根据类的命名空间得到
    String namespace() default "";
}