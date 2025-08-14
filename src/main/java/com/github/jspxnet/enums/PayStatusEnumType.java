package com.github.jspxnet.enums;



/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2020/1/17 17:28
 * description: jspxpro
 * @author chenYuan
 */
public enum PayStatusEnumType implements EnumType {

    // 支付状态 1：未支付；2：已支付；3:支付失败；4：支付关闭；5：用户取消支付; 6:系统解锁余额；7：支付中

    UNKNOWN(0, "未知"),

    NOPAYED(1, "未支付"),

    PAYED(2, "已支付"),

    FAILED(3, "支付失败"),

    CLOSED(4, "支付关闭"),

    CANCEL(5, "用户取消支付"),

    UNLOCK(6, "系统解锁余额"),

    PAYING(7, "支付中"),

    RETURN(8, "退款"),

    //专用于支付订单查询
    ERROR(99, "查询错误"),
    ;



    PayStatusEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static PayStatusEnumType find(int value) {
        for (PayStatusEnumType c : PayStatusEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return PayStatusEnumType.UNKNOWN;
    }

    private int value;
    private String name;


    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }
}
