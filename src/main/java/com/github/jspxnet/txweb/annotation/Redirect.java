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
 * date: 2007-2-28
 * Time: 0:25:48
 * type=返回处理类
 * <p>
 * 调转方法 目前不推荐使用,等待servlet 3.0 成熟
 */

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Redirect {
    //text/html; charset=UTF-8
    String contentType() default "";

    //跳转方式 location  redirect
    String type() default "template";

    //跳转到 路径,不代文件后缀
    String location() default "";
}