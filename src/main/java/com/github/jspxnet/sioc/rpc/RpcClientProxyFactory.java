package com.github.jspxnet.sioc.rpc;

import java.lang.reflect.Proxy;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/1/5 23:24
 * description: 为了解决token传递问题
 **/
public class RpcClientProxyFactory {

    public static RpcClientProxy createRpcClientProxy(Class<?> targetClass)
    {
        return (RpcClientProxy) Proxy.newProxyInstance(RpcClientProxy.class.getClassLoader(),
                new Class[]{RpcClientProxy.class,targetClass}, new RpcClientInvocationHandler(targetClass));
    }

}
