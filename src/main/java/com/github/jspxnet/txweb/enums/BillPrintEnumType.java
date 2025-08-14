package com.github.jspxnet.txweb.enums;

import com.github.jspxnet.enums.EnumType;

public enum  BillPrintEnumType implements EnumType {
    //反审核后动作
    OTHER(6, "其他方式"),

    //审核通过
    WEB(5, "前端打印"),

    //传输JSON给前端 + 模版
    JSON(4, "JSON"),

    //前边几种都说导出方式
    //生成EXCEL方式
    SPIRE(3, "SPIRE控件生成EXCEL方式"),

    //JXSL模版方式 生成EXCEL方式
    JXSL(2, "JXSL"),

    //POI后台方式
    POI(1, "POI"),

    //关闭打印
    NONE(0, "无");

    private final int value;
    private final String name;

    BillPrintEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public BillPrintEnumType find(int value) {
        for (BillPrintEnumType c : BillPrintEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return BillPrintEnumType.NONE;
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
