package com.github.jspxnet.enums;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2020-1-12
 * Time: 10:20:34
 */
public enum VoteEnumType implements EnumType {

    /*
        static public final int RADIO_BUTTON = 0;
    static public final int MULTI_SELECT = 1;
     */
    //多选
    MULTIPLE(1, "多选"),
    //单选
    SINGLE(0, "单选");

    private int value;
    private String name;

    VoteEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public VoteEnumType find(int value) {
        for (VoteEnumType c : VoteEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return VoteEnumType.SINGLE;
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
