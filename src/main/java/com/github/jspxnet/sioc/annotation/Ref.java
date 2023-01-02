/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sioc.annotation;

import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.sioc.util.Empty;
import com.github.jspxnet.utils.StringUtil;

import java.lang.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-3-7
 * Time: 16:37:45
 * Sioc对象载入
 */

@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ref {
    String name() default StringUtil.empty;  //sioc中的名称

    Class bind() default Empty.class;  //默认为类名

    String namespace() default Sioc.global; //命名空间

    /*
    test为true的时候，namespace自动测试载入,载入的命名空间为配置的空间名，如果是多层配置后，载入的还是配置的空间名，不会自动切换到新的空间名下
    主要是使用在夸命名空间的拦截器上需要考虑到
     */
    boolean test() default false; //载入的时候如果为true,并且没有这个对象的时候不报错，否则会报错误

}
