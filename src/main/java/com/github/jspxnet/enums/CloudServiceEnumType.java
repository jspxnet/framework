package com.github.jspxnet.enums;

/**
 * author chenYuan
 * 定义云服务商支持范围
 */
public enum CloudServiceEnumType implements EnumType {

    //0:阿里云;
    Ali(0, "Ali"),

    //1:华为云
    HuaWei(1, "HuaWei");

    //FastDfs(2, "fastDfs");

    final private int value;
    final private String name;

    CloudServiceEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public CloudServiceEnumType find(int value) {
        for (CloudServiceEnumType c : CloudServiceEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return CloudServiceEnumType.Ali;
    }

    static public CloudServiceEnumType find(String name) {
        for (CloudServiceEnumType c : CloudServiceEnumType.values()) {
            if (c.name.equalsIgnoreCase(name)) {
                return c;
            }
        }
        return CloudServiceEnumType.Ali;
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
