package com.github.jspxnet.enums;

/**
 * 密钥保存格式
 */
public enum  KeyFormatEnumType implements EnumType {

    STRING(0, "字符串"),
    //好
    HEX(1, "16进制"),
    //否
    BASE64(2, "Base64编码");

    private final int value;
    private final String name;

    KeyFormatEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public KeyFormatEnumType find(int value) {
        for (KeyFormatEnumType c : KeyFormatEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return KeyFormatEnumType.STRING;
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
