package com.github.jspxnet.enums;

public enum PlatformEnumType implements EnumType {

    //跨平台
    ANY(0, "任意"),

    //基础控件
    PC(1, "电脑"),

    //高级控件
    MOBILE(2, "手持设备"),

    //高级控件
    PAD(3, "PAD"),

    //未知
    CUSTOM(9, "自定义");

    private final int value;

    private final String name;

    PlatformEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public PlatformEnumType find(int value) {
        for (PlatformEnumType c : PlatformEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return PlatformEnumType.CUSTOM;
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