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

import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.utils.StringUtil;
import java.lang.annotation.*;



/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-5-25
 * Time: 14:59:33
 * 方法动作定义
 */

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Operate {
    //提交按钮名称
    String method() default "@";

    //是否需要提交后在执行 POST 提交才执行  GET不需要提交
    //ROC的时候，可以直接配置调用
    boolean post() default true;

    //动作名称描述
    String caption() default StringUtil.empty;

    //重复提交验证,0 表示不验证, 数字标识间隔时间,单位为秒
    int repeat() default 0;

    //返回类型,用于生成文档
    Class<?>[] returnType() default {RocResponse.class};

    //返回类型,用于泛型
    String returnTypeModel() default StringUtil.empty;

}