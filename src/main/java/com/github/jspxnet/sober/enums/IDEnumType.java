package com.github.jspxnet.sober.enums;

import com.github.jspxnet.enums.EnumType;

public enum IDEnumType implements EnumType {

    //主
    MAIN(0, "main"),
    //子
    SUB(1, "sub"),
    //孙
    SON(2, "son"),
    //重孙
    great(3, "great");

    final private int value;
    final private String name;

    IDEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public IDEnumType find(int value) {
        for (IDEnumType c : IDEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return IDEnumType.MAIN;
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
