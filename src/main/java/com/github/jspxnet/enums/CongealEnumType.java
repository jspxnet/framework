package com.github.jspxnet.enums;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2020-1-12
 * Time: 10:20:34
 */

public enum CongealEnumType implements EnumType {

    //未知
    UNKNOWN(-1, "未知"),
    //是
    YES_CONGEAL(1, "冻结"),
    //否
    NO_CONGEAL(0, "正常");

    private final int value;
    private final String name;

    CongealEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public CongealEnumType find(int value) {
        for (CongealEnumType c : CongealEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return CongealEnumType.NO_CONGEAL;
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
