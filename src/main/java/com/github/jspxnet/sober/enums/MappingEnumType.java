/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.enums;

import com.github.jspxnet.enums.EnumType;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-7
 * Time: 18:16:21
 */
public enum MappingEnumType implements EnumType {


    UNKNOWN(0, "UNKNOWN"),

    OneToMany(1, "OneToMany"),

    OneToOne(2, "OneToOne"),

    ManyToOne(3, "ManyToOne");


    //多对一
    public static final String MANY_ONE = "ManyToOne";
    //一对一
    public static final String ONE_ONE = "OneToOne";
    //一对多
    public static final String ONE_MANY = "OneToMany";

    final private int value;
    final private String name;


    MappingEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public MappingEnumType find(int value) {
        for (MappingEnumType c : MappingEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return MappingEnumType.UNKNOWN;
    }
    static public MappingEnumType find(String name) {
        for (MappingEnumType c : MappingEnumType.values()) {
            if (c.name.equalsIgnoreCase(name)) {
                return c;
            }
        }
        return MappingEnumType.UNKNOWN;
    }
    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public String getName() {
        return this.name;
    }
}