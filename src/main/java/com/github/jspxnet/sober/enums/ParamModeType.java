package com.github.jspxnet.sober.enums;


import com.github.jspxnet.enums.EnumType;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/12/3 21:42
 * description:  参数接收模式,实用不同场合,比如微信登陆这些,不一定规范
 **/
public enum ParamModeType implements EnumType  {
    //参数接收模式

    //默认计数
    RocMode(0,RocMode.class, "ROC"),
    //完整的JSON接收
    JsonMode(1,JsonMode.class, "JSON"),

    //root 参数直接赋值给变量名称
    SpringMode(2,SpringMode.class, "SPRING");

    final private int index;

    final private Class<?> value;

    //名称
    final  private String name;

    ParamModeType(int index,Class<?> value, String name) {
        this.index = index;
        this.value = value;
        this.name = name;
    }

    static public ParamModeType find(Class<?> value) {
        for (ParamModeType c : ParamModeType.values()) {
            if (c.value.equals(value)) {
                return c;
            }
        }
        return ParamModeType.RocMode;
    }

    static public boolean equals(Class<?> value1,Class<?> value2) {
        if (value1==null||value2==null) {
            return true;
        }
        return value1.equals(value2);
    }

    @Override
    public int getValue() {
        return this.index;
    }

    @Override
    public String getName() {
        return this.name;
    }


    private abstract static class JsonMode {

        private JsonMode() {

        }
    }

    private abstract static class RocMode {

        private RocMode() {

        }
    }

    private abstract static class SpringMode {
        private SpringMode() {

        }
    }

    private abstract static class NullMode {
        private NullMode() {

        }
    }

}
