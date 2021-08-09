package com.github.jspxnet.enums;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2020-1-12
 * Time: 10:20:34
 */
public enum SexEnumType implements EnumType {

    //差
    UNKNOWN(-1, "未知"),
    //男
    MAN(1, "男"),
    //女
    WOMAN(0, "女");

/*
    static public final int man = 1;
    static public final int woman = 0;
    static public final int unknown = -1;
*/

    private final int value;
    private final String name;

    SexEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public SexEnumType find(int value) {
        for (SexEnumType c : SexEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return SexEnumType.UNKNOWN;
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
