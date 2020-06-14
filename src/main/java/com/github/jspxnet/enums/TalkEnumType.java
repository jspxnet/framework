package com.github.jspxnet.enums;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2020-1-12
 * Time: 10:20:34
 */

public enum TalkEnumType implements EnumType {

    //未知
    UNKNOWN(-1, "未知"),

    //聊天室
    CHAT(1, "聊天室"),

    ANNOUNCEMENT(2, "公告"),

    NOTICE(3, "应用提示"),

    DRAFT(4, "草稿"),

    INBOX(5, "收件箱"),

    SENT(6, "已发邮件"),

    GARBAGE(7, "垃圾箱"),

    ADMIN_NOTICE(8, "管理员消息"),

    //M方式或为站点消息
    IM(9, "IM私人消息");


    private int value;

    private String name;

    TalkEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public TalkEnumType find(int value) {
        for (TalkEnumType c : TalkEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return TalkEnumType.UNKNOWN;
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
