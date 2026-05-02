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

import com.github.jspxnet.sioc.util.TypeUtil;
import com.github.jspxnet.utils.ArrayUtil;

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
 *
 * com.github.jspxnet.sober.annotation.CalcUnique
 * CalcUnique(caption = "统计用户数量", sql = "SELECT count(1) FROM ${follow} WHERE followType=0 AND nodeId=? AND putUid=?", entity = {Follow.class}, value = {"name", "putUid"})
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
     * 动态对象就是表名
     * @return 实体对象
     */
    String[] entity() ;

    //返回类型
    String type() default  TypeUtil.TYPE_STRING;


    //是优化参数,不用全部带入,没有全部带入
    String[] params();

}