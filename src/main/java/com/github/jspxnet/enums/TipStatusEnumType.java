package com.github.jspxnet.enums;

public enum TipStatusEnumType implements EnumType {

    //未知
    UNKNOWN(0, "未知"),

    START(1, "开始"),

    RUNING(2, "运行中"),

    FINISH(3, "结束"),

    PAUSE(11, "暂停"),

    INFO(12, "提示"),

    ERROR(13, "异常");


    private final int value;

    private final String name;

    TipStatusEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public TipStatusEnumType find(int value) {
        for (TipStatusEnumType c : TipStatusEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return TipStatusEnumType.UNKNOWN;
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
