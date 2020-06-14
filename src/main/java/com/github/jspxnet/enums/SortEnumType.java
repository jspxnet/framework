package com.github.jspxnet.enums;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2020-1-12
 * Time: 10:20:34
 */

public enum SortEnumType implements EnumType {

    //结对置顶
    ABS_TOP(8, "结对置顶"),
    //置顶
    TOP(6, "置顶"),
    //锁定提前
    LOCKED(7, "锁定提前"),
    //默认
    DEFAULT(0, "默认"),
    //下沉
    DOWN(-1, "下沉");

    private int value;
    private String name;

    SortEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public SortEnumType find(int value) {
        for (SortEnumType c : SortEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return SortEnumType.DEFAULT;
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
