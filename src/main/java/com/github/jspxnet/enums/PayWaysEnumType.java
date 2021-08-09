package com.github.jspxnet.enums;

/**
 * Created by jspx.net
 *
  * author: chenYuan
 * date: 2020/3/3 13:47
 * description: jspxpro
 */
public enum  PayWaysEnumType implements EnumType {
    //未知
    UNKNOWN(-1,"未知"),

    ONLINE(1,"在线支付"),

    TRANSFER(2,"系统内转账支付"),

    MONEY(9,"现金");

    private final int value;
    private final String name;

    PayWaysEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static PayWaysEnumType find(int value) {
        for (PayWaysEnumType c : PayWaysEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return PayWaysEnumType.UNKNOWN;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }
}
