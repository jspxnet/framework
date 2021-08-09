package com.github.jspxnet.enums;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/3/6 11:02
 * description: http 支持的方法枚举
 */
public enum  HttpMethodEnumType implements EnumType {

    //简单请求
    GET(1,"GET"),

    //只返回状态行和头标
    HEAD(2,"HEAD"),

    //服务器接受被写入客户端输出流中的数据的请求
    POST(3,"POST"),

    //服务器保存请求数据作为指定URI新内容的请求
    PUT(4,"PUT"),

    //服务器删除URI中命名的资源的请求
    DELETE(5,"DELETE"),

    //关于服务器支持的请求方法信息的请求
    OPTIONS(6,"OPTIONS"),

    //关于服务器支持的请求方法信息的请求
    TRACE(7,"TRACE"),

    //已文档化但当前未实现的一个方法，预留做隧道处理
    CONNECT(8,"CONNECT"),

    ;

    private final int value;
    private final String name;


    HttpMethodEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public HttpMethodEnumType find(int value) {
        for (HttpMethodEnumType c : HttpMethodEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return HttpMethodEnumType.GET;
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