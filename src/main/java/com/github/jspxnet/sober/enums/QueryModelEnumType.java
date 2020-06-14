package com.github.jspxnet.sober.enums;

import com.github.jspxnet.enums.EnumType;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/10/20 21:54
 * description: 查询返回模式
 **/
public enum  QueryModelEnumType implements EnumType {

    //返回list
    LIST(0, "LIST"),
    //单个对象 int  bean 对象
    SINGLE(1, "SINGLE"),
    //得到总行数
    COUNT(2, "COUNT");


    final private int value;
    final private String name;

    QueryModelEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public QueryModelEnumType find(int value) {
        for (QueryModelEnumType c : QueryModelEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return QueryModelEnumType.LIST;
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
