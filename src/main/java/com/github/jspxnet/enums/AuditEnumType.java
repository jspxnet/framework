package com.github.jspxnet.enums;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2020-1-12
 * Time: 10:20:34
 *
 */
public enum AuditEnumType implements EnumType {
    //未知
    WAIT(0, "待审"),

    //未知
    OK(1, "通过"),

    //未知
    NO(2, "不过");

    private final int value;
    private final String name;

    AuditEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public AuditEnumType find(int value) {
        for (AuditEnumType c : AuditEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return AuditEnumType.WAIT;
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
