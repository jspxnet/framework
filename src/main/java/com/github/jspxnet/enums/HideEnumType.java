package com.github.jspxnet.enums;

public enum HideEnumType implements EnumType {

    //未知
    UNKNOWN(-1, "未知"),
    //是
    Hide(1, "隐藏"),
    //否
    SHOW(0, "显示");

    private final int value;
    private final String name;

    HideEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public HideEnumType find(int value) {
        for (HideEnumType c : HideEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return HideEnumType.SHOW;
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
