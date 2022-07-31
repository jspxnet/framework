package com.github.jspxnet.enums;

public enum BoolEnumType implements EnumType {
    //是
    YES(1, "是"),
    //否
    NO(0, "否");

    private final int value;
    private final String name;

    BoolEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public BoolEnumType find(int value) {
        for (BoolEnumType c : BoolEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return BoolEnumType.NO;
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
