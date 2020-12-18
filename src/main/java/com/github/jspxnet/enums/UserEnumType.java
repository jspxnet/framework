package com.github.jspxnet.enums;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2020-1-12
 * Time: 10:20:34
 */

public enum UserEnumType implements EnumType {

    //作者
    ChenYuan(20, "作者"),
    //超级管理员
    ADMINISTRATOR(10, "超级管理员"),
    //普通管理员
    MANAGER(6, "普通管理员"),
    INTENDANT(4, "操作人员"),

    VIP(2, "VIP用户"),
    USER(1, "普通用户"),
    NONE(0, "游客");


    /**
     * 0:游客
     * 2:普通用户
     * 4:普通管理员(不能登录后台)
     * 6:普通管理员(能登录后台)
     * 10:超级管理员
     */

    private int value;
    private String name;

    UserEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public UserEnumType find(int value) {
        for (UserEnumType c : UserEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return UserEnumType.NONE;
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
