package com.github.jspxnet.txweb.enums;

import com.github.jspxnet.enums.EnumType;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2020-1-12
 * Time: 10:20:34
 */
public enum SafetyEnumType implements EnumType {

    //高
    HEIGHT(3, "高"),
    //中
    MIDDLE(2, "中"),
    //低
    LOW(1, "低"),
    //无
    NONE(0, "无");

    private final int value;
    private final String name;

    SafetyEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public SafetyEnumType find(int value) {
        for (SafetyEnumType c : SafetyEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return SafetyEnumType.NONE;
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
