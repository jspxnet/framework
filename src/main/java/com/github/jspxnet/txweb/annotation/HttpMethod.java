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

import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.utils.StringUtil;

import java.lang.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-7-3
 * Time: 15:33:35
 * Action   SQL       HTTP
 * <p>
 * Create    Insert      PUT
 * Read     Select         GET
 * Update   Update      POST
 * Delete    Delete      DELETE
 */

@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpMethod {

    //动作名称,在权限里边显示,也可以配置在TXWeb的caption属性里边
    String caption();

    //是否支持手机页面，如果是,那么模板读取的时候，是手机端将读取 name.mobile.ftl的文件
    boolean mobile() default false;

    //强制要求加密传输
    boolean secret() default false;

    String namespace() default Sioc.global;

    //RESTFull 路径,目录部分,也就是配置的命名空间，actionName是入口点，如果入口方法为@表示方法名称
    //这里和spring 有区别，这里相当于 namespace/actionName    的方式，目的是在一个目录下能够有多个bean入口，这样传统开发会方便很多
    String actionName() default StringUtil.empty;

}