package com.github.jspxnet.enums;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2020-1-12
 * Time: 10:20:34
 */

public enum RecycleEnumType implements EnumType {
    //未知
    UNKNOWN(-1, "未知"),
    //是
    YES_RECYCLE(1, "是"),
    //否
    NO_RECYCLE(0, "否");

    private final int value;
    private final String name;

    RecycleEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public RecycleEnumType find(int value) {
        for (RecycleEnumType c : RecycleEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return RecycleEnumType.UNKNOWN;
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
