package com.github.jspxnet.enums;

/**
 * 自定义空间类型
 */
public enum ControlTypeEnumType implements EnumType {

    //是
    CONTAINER(1, "布局容器"),

    //基础控件
    BASE(2, "基础控件"),

    //高级控件
    ADVANCE(3, "高级控件"),

    //未知
    CUSTOM(9, "自定义");

    private final int value;

    private final String name;

    ControlTypeEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public ControlTypeEnumType find(int value) {
        for (ControlTypeEnumType c : ControlTypeEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return ControlTypeEnumType.CUSTOM;
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