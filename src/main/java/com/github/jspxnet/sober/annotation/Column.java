/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.annotation;

import com.github.jspxnet.enums.EnumType;
import com.github.jspxnet.utils.StringUtil;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)ԭ
 * date: 2007-2-5
 * Time: 18:03:10
 * PropertySource
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    /**
     * @return 字段文字说明
     */
    String caption();

    /**
     * @return 字段是否可以为空
     */
    boolean notNull() default false;

    /**
     * 选择范围
     * 例如:"小;中;大"或者 "1:小;2:中;3:大"
     *
     * @return 选择范围
     */
    String option() default StringUtil.empty;


    Class<?> enumType() default NullClass.class;

    /**
     * 验证更具js库 函数，和配置验证里边的条件一样
     * 在前后段不分离的时候使用的,如果在前后端分离的结构中不用使用此字段
     * @return 验证表达式
     */
    String dataType() default StringUtil.empty;

    /**
     * @return 默认值, 映射到数据库
     */
    String defaultValue() default StringUtil.empty;

    /**
     * @return 长度, 映射到数据库
     */
    int length() default 0;

    /**
     * 输入框类型,对应html表单  的type 属性，例如:text
     * 目前
     *
     * @return 输入框类型
     */
    String input() default "text";

    /**
     * @return 在导出的时候是否隐藏
     */
    boolean hidden() default false;

}