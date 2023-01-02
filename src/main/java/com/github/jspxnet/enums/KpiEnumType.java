package com.github.jspxnet.enums;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2020-1-12
 * Time: 10:20:34
 *
 */
public enum KpiEnumType implements EnumType {

    //差
    SERIOUS(-1, "差"),
    //好
    GOOD(1, "好"),
    //否
    MEDIUM(0, "中");

    private final int value;
    private final String name;

    KpiEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public KpiEnumType find(int value) {
        for (KpiEnumType c : KpiEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return KpiEnumType.MEDIUM;
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
