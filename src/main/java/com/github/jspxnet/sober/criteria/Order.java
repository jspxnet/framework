/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.criteria;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-8
 * Time: 10:13:54
 */
public class Order implements Serializable {
    //排序
    final private boolean ascending;
    //排序字段
    final private String propertyName;


    @Override
    public String toString()
    {
        return propertyName + " " + (ascending ? "asc" : "desc");
    }

    public String toSqlString(String databaseType)
    {
        return propertyName + " " + (ascending ? "asc" : "desc");
    }

    public Order ignoreCase() {
        return this;
    }


    /**
     * @param propertyName 字段
     * @param ascending    排序 desc
     */
    protected Order(String propertyName, boolean ascending) {
        this.propertyName = propertyName;
        this.ascending = ascending;
    }

    /**
     * 先序
     *
     * @param propertyName 字段
     * @return Order 排序对象
     */
    public static Order asc(String propertyName) {
        return new Order(propertyName, true);
    }

    /**
     * 后序
     *
     * @param propertyName 字段
     * @return Order 排序对象
     */
    public static Order desc(String propertyName) {
        return new Order(propertyName, false);
    }


    public String[] getFields() {
        return new String[]{propertyName};
    }

}