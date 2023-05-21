package com.github.jspxnet.txweb.enums;

import com.github.jspxnet.enums.EnumType;

public enum DocumentStatusEnumType implements EnumType {
    //反审核后动作
    D(5, "重新审核"),
    //审核通过
    C(4, "已审核"),
    //提交后操作
    B(3, "审核中"),
    //验证后保存
    A(2, "创建"),
    //暂存,草稿
    Z(1, "暂存"),
    //未知
    UNKNOWN(0, "未知");

    private final int value;
    private final String name;

    DocumentStatusEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public DocumentStatusEnumType find(int value) {
        for (DocumentStatusEnumType c : DocumentStatusEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return DocumentStatusEnumType.Z;
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
