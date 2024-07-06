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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)ԭ
 * date: 2007-2-5
 * Time: 23:29:37
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    /**
     * @return 数据库表名
     */
    String name() default "";

    /**
     * @return 描述名称
     */
    String caption() default "";

    /**
     * @return 是否适用缓存
     */
    boolean cache() default true;

    /**
     * 只有在对象结构的时候生效
     * @return 是否自动清理缓存
     */
    boolean autoCleanCache() default false;

    /**
     * 应为 PO，DO，DTO，VO 的的概念太多，实际使用中  PO 持久对象就是Table的映射，
     * TO,DTO都只是为了传输数据的分层，这里就简化一下，create为false的时候，就表示非持久化对象
     * 用于VO,TO,DTO
     *
     * @return 是否创建库结构
     */
    boolean create() default true;
}