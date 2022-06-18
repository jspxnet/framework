package com.github.jspxnet.txweb.enums;

import com.github.jspxnet.enums.EnumType;

/**
 * Created by jspx.net
 * @author chenYuan
 * author: chenYuan
 * date: 2020/6/30 22:37
 * description: 分布式远程调用方式
 *
 *
 * */
public enum RpcProtocolEnumType  implements EnumType {

    //http hessian 方式
    HTTP(1, "http"),
    //netty rpc 方式
    TCP(2, "tcp");

    final private int value;
    final private String name;

    RpcProtocolEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public RpcProtocolEnumType find(int value) {
        for (RpcProtocolEnumType c : RpcProtocolEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return RpcProtocolEnumType.TCP;
    }

    static public RpcProtocolEnumType find(String name) {
        for (RpcProtocolEnumType c : RpcProtocolEnumType.values()) {
            if (c.name.equalsIgnoreCase(name)) {
                return c;
            }
        }
        return RpcProtocolEnumType.TCP;
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
