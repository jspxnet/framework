package com.github.jspxnet.txweb.enums;

import com.github.jspxnet.enums.EnumType;

public enum UploadVerifyEnumType implements EnumType {

    // 0:游客方式不允许上传,其他通过角色配置  默认  1:游客也放开； 2:通过验证ApiKey判断
    //游客方式不允许上传,其他通过角色配置
    DEFAULT(0, "默认方式"),

    ROLE(1, "完全角色控制"),

    //下边两个主要为了
    API_KEY_1(2, "ApiKey直接比对"),

    API_KEY_2(3, "ApiKey直接比对加时间戳签名");

    private final int value;
    private final String name;

    UploadVerifyEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public UploadVerifyEnumType find(int value) {
        for (UploadVerifyEnumType c : UploadVerifyEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return UploadVerifyEnumType.DEFAULT;
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
