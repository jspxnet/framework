package com.github.jspxnet.sober.enums;

import com.github.jspxnet.enums.EnumType;

public enum EntityLevelEnumType implements EnumType {

    //主表
    MAIN(0, "main"),
    //子表
    SUB(1, "sub"),
    
    //孙表
    SON(2, "son"),
    //后边的使用数字直接表示
    LEVEL(3, "LEVEL");
    final private int value;
    final private String name;

    EntityLevelEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public EntityLevelEnumType find(int value) {
        for (EntityLevelEnumType c : EntityLevelEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return EntityLevelEnumType.MAIN;
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

