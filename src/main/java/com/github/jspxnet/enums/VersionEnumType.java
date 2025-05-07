package com.github.jspxnet.enums;

public enum VersionEnumType implements EnumType {

    //未知
    UNKNOWN(0, "未知"),

    FREE(1, "免费版"),

    PROFESSIONAL(2, "专业版"),

    Enterprise(3, "企业版");

    private final int value;

    private final String name;

    VersionEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public VersionEnumType find(int value) {
        for (VersionEnumType c : VersionEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return VersionEnumType.UNKNOWN;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString()
    {
        return this.name;
    }

}
