package com.github.jspxnet.enums;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/2/19 0:04
 * description: 错误归类
 **/
public enum ErrorEnumType implements EnumType {

    //错误类型
    UNKNOWN(-1, "未知"),

    NEED_LOGIN(-32602, "没有登陆"),

    PARAMETERS(-32604, "参数错误"),

    //给用户看的
    WARN(-32606, "警告提示"),

    LOGIC(-32608, "逻辑错误"),

    POWER(-32610, "权限不够"),

    FORMAT(-32612, "格式错误"),

    UNSUPPORTED(-32614, "系统不支持"),

    UNSUPPORTED_ENCODING(-32616, "不支持的编码"),

    CONFORMING_SPEC(-32618, "规范不合格"),

    METHOD_NOT_FOUND(-32620, "方法不存在"),

    CONFIG(-32622, "无效配置"),

    OVERTIME(-32624, "超时"),

    CONGEAL(-32626, "被冻结"),

    NO_DATA(-32628, "无数据"),

    SYSTEM(-32630, "操作系统错误"),

    CALL_API(-32632, "接口调用错误"),

    TRANSPORT(-32634, "传输错误"),

    APPLICATION(-32636, "底层错误"),

    CONNECT(-32638, "数据库连接错误"),

    DATABASE(-32640, "数据库操作错误"),

    NO_MONEY(-32641, "需要充值才能开发此功能"),

    SIGNATURE_VERIFY(-32642, "签名验证错误"),

    KEY_VERIFY(-32644, "KEY验证错误"),

    KEY_LOCK_WAIT(-32645, "当前操作被锁定,稍后操作");

    private final int value;
    private final String name;

    ErrorEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public ErrorEnumType find(int value) {
        for (ErrorEnumType c : ErrorEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return ErrorEnumType.UNKNOWN;
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
