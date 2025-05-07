package com.github.jspxnet.enums;

public enum VersionBindEnumType  implements EnumType {

    //未知
    DOMAIN(0, "域名"),

    MAC(1, "MAC"),

    MIXED_KEY(2, "混合KEY");

    private final int value;

    private final String name;

    VersionBindEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public VersionBindEnumType find(int value) {
        for (VersionBindEnumType c : VersionBindEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return VersionBindEnumType.MIXED_KEY;
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
