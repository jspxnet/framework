package com.github.jspxnet.enums;


/**
 * @author chenyuan
 */
public enum  ValidateCodeEnumType implements EnumType {
    /**
     * 验证码方式
     */
    SMS(2, "sms","短信"),
    IMG(1, "img","图片验证"),
    general(0, "general","通用");

    private int value;
    private String key;

    private String name;

    ValidateCodeEnumType(int value,String key, String name) {
        this.value = value;
        this.key = key;
        this.name = name;
    }

    static public ValidateCodeEnumType find(int value) {
        for (ValidateCodeEnumType c : ValidateCodeEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return ValidateCodeEnumType.general;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public String getName() {
        return this.name;
    }


    public String getKey() {
        return this.key;
    }
}
