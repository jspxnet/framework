package com.github.jspxnet.enums;
/**
 * 分享枚举类型,用户需要特殊处理，只保存一分就够了
 */
public enum PromoteLinkEnumType  implements EnumType {
    //未知
    USER(0, "用户"),

    //公众号
    WX_MP(1, "公众号"),

    WX_APP(2, "小程序"),

    URL(3, "URL页面"),

    GOODS(4, "商品"),

    ACTIVE(5, "活动"),

    NEWS(6, "新闻"),

    BLOG(7, "bolg"),

    VLOG(8, "vlog"),

    NONE(-1, "NONE");

    //0:用户;1:公众号;2:小程序;3:URL页面;4:商品;5:活动;6:新闻;7:bolg;8:vlog"
    private final int value;
    private final String name;

    PromoteLinkEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public PromoteLinkEnumType find(int value) {
        for (PromoteLinkEnumType c : PromoteLinkEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return PromoteLinkEnumType.NONE;
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