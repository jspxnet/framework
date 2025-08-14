package com.github.jspxnet.enums;


/**
 * @author chenyuan
 */
public enum TimeStatEnumType implements EnumType {

    //时统计
    TIME(0, "自定义"),
    //日统计
    TODAY(1, "日"),
    MONTH(2, "月"),
    QUARTER(3, "季"),
    YEAR(4, "年");

    private final int value;
    private final String name;

    TimeStatEnumType(int value, String name) {
        this.value = value;
        this.name = name;
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
