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

import com.github.jspxnet.utils.StringUtil;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-6
 * Time: 10:28:36
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Nexus {

    /**
     * @return 映射关系
     */
    String mapping();

    /**
     * 自己表的字段
     *
     * @return String
     */
    String field() default StringUtil.empty;

    /**
     * 对应的数据字段
     * 另外一个表的字段
     *
     * @return String
     */
    String targetField() default StringUtil.empty;

    /**
     * 对应的类
     *
     * @return Class
     */

    Class targetEntity();

    /**
     * 条件 使用 ssql表达式
     *
     * @return 默认条件
     */
    String term() default StringUtil.empty;

    /**
     * 映射条件,这里为表达式，如果成立，才载入映射
     *
     * @return 映射条件
     */
    String where() default StringUtil.empty;

    /**
     * createDate:D
     *
     * @return 排序
     */
    String orderBy() default StringUtil.empty;

    /**
     * @return 关联删除
     */
    boolean delete() default true;

    /**
     * @return 关联更新
     */
    boolean update() default false;

    /**
     * @return 关联保持，这里是基础，只有这里开启，save(obj,true) 才有效
     */
    boolean save() default false;

    /**
     * @return 多层关联
     */
    boolean chain() default false;

    /**
     * @return 关联的数据个数，0 为系统默认支持最大个数
     */
    String length() default "0";
}