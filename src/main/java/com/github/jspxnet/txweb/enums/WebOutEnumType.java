package com.github.jspxnet.txweb.enums;

import com.github.jspxnet.enums.EnumType;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/3/6 23:07
 * description: 网页请求返回格式
 **/
public enum  WebOutEnumType implements EnumType {

    //头类型
    XML(1, "XML"),

    JSON(2, "JSON"),

    JAVASCRIPT(3, "javascript"),

    HTML(4, "html"),

    TEXT(5, "text");

    /**
     *  
     * 0:游客
     * 2:普通用户
     * 4:普通管理员(不能登录后台)
     * 6:普通管理员(能登录后台)
     * 10:超级管理员
     */

    final  private int value;
    final  private String name;

    WebOutEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public WebOutEnumType find(int value) {
        for (WebOutEnumType c : WebOutEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return WebOutEnumType.TEXT;
    }

    static public WebOutEnumType find(String name) {
        for (WebOutEnumType c : WebOutEnumType.values()) {
            if (c.name.equalsIgnoreCase(name)) {
                return c;
            }
        }
        return WebOutEnumType.TEXT;
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
