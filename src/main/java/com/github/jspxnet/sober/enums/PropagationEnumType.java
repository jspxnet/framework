package com.github.jspxnet.sober.enums;

import com.github.jspxnet.enums.EnumType;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/4/21 8:59
 * description:事务类型
 */
public enum  PropagationEnumType implements EnumType {

    //默认计数
    DEFAULT(1, "DEFAULT"),
    //事务计数重置
    NEW(2, "NEW");


    final  private int value;
    final  private String name;

    PropagationEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public PropagationEnumType find(int value) {
        for (PropagationEnumType c : PropagationEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return PropagationEnumType.DEFAULT;
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
