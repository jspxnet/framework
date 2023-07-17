package com.github.jspxnet.sober.criteria;

import com.github.jspxnet.enums.EnumType;


/**
 * @author chenyuan
 */
public enum FilterLogicEnumType implements EnumType {

    /**
     * sql 逻辑 表达式
     */
    AND(1, "AND","并且"),

    OR(2, "OR","或者");

    private final int value;
    private final String key;
    private final String name;

    FilterLogicEnumType(int value, String key, String name) {
        this.value = value;
        this.key = key;
        this.name = name;
    }

    static public FilterLogicEnumType find(int value) {
        for (FilterLogicEnumType c : FilterLogicEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return FilterLogicEnumType.AND;
    }

    static public FilterLogicEnumType find(String value) {
        for (FilterLogicEnumType c : FilterLogicEnumType.values()) {
            if (c.key.equalsIgnoreCase(value)) {
                return c;
            }
        }
        return FilterLogicEnumType.AND;
    }
    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public String getName() {
        return this.name;
    }


    public String getKey() {
        return this.key;
    }
}

