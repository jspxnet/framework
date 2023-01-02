package com.github.jspxnet.enums;

/**
 * @author chenyuan
 */

public enum PageModeEnumType implements EnumType {

    //添加编辑
    AEUI(1, "添加编辑"),

    //只看页面
    ACUI(2, "只看"),

    //列表页面
    LIST(3, "列表"),

    //添加页面
    ADD(4, "添加"),

    //编辑页面
    UPDATE(5, "编辑");

    //外链页面
 //   LINK(5, "外联");

    private final int value;
    private final String name;

    PageModeEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public PageModeEnumType find(int value) {
        for (PageModeEnumType c : PageModeEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return PageModeEnumType.LIST;
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
