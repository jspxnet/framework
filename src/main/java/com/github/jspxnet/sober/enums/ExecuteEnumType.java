package com.github.jspxnet.sober.enums;

import com.github.jspxnet.enums.EnumType;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/10/17 23:23
 * description: jdbc 的执行方法
 **/
public enum ExecuteEnumType implements EnumType {

    //查询
    QUERY(0, "QUERY"),
    //更新
    UPDATE(1, "UPDATE"),
    //执行
    EXECUTE(2, "EXECUTE");


    final private int value;
    final private String name;

    ExecuteEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public ExecuteEnumType find(int value) {
        for (ExecuteEnumType c : ExecuteEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return ExecuteEnumType.QUERY;
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
