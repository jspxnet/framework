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

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-7-30
 * Time: 18:25:50
 * uniqueObject
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CalcUnique {
    String caption();

    /**
     * Calc sql eg: select count() from ${entity1}
     *
     * @return sql
     */
    String sql();

    /**
     * 载入表名,例如 select * from ${entity2} as a,${entity2} as b
     * load entity database table name load eg: select * from ${entity1} as a,${entity2} as b
     *
     * @return 实体对象
     */
    Class<?>[] entity();

    //本实体的字段，将作为参数传递给SQL
    String[] value() default {};
}