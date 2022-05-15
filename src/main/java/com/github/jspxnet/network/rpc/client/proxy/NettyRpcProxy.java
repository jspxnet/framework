package com.github.jspxnet.network.rpc.client.proxy;

import com.github.jspxnet.network.rpc.model.transfer.RequestTo;
import com.github.jspxnet.network.rpc.model.transfer.ResponseTo;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetSocketAddress;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/10 22:13
 * description: jspbox
 **/
@Slf4j
public class NettyRpcProxy {
    private NettyRpcProxy()
    {

    }


    static  public <T>T create(Class<T>  target,String url, HttpServletRequest request, HttpServletResponse response)
    {
        RpcMethodInterceptor clientInvocationHandler = new RpcMethodInterceptor();
        clientInvocationHandler.setUrl(url);
        clientInvocationHandler.setRequest(request);
        clientInvocationHandler.setResponse(response);
        //目前还没有继承接口,后边看是否需要
        return createProxyInstance(target,clientInvocationHandler);

    }
    /**
     *
     * @param target 类对象
     * @param url 路径
     * @param address 服务器分组服务, 默认就是网关路径
     * @param <T> 类型
     * @return 代理对象
     */
    static  public <T>T create(Class<T>  target,String url,  InetSocketAddress address)
    {
        RpcMethodInterceptor clientInvocationHandler = new RpcMethodInterceptor();
        clientInvocationHandler.setUrl(url);
        clientInvocationHandler.setAddress(address);
        //目前还没有继承接口,后边看是否需要
        return createProxyInstance(target,clientInvocationHandler);
    }
    /**
     *
     * @param target 类对象
     * @param url 路径
     * @param serviceName 服务器分组服务, 默认就是网关路径
     * @param <T> 类型
     * @return 代理对象
     */
    static  public <T>T create(Class<T> target,String url,  String serviceName)
    {
        RpcMethodInterceptor clientInvocationHandler = new RpcMethodInterceptor();
        clientInvocationHandler.setUrl(url);
        clientInvocationHandler.setServiceName(serviceName);
        clientInvocationHandler.setAddress(null);
        //目前还没有继承接口,后边看是否需要
        return createProxyInstance(target,clientInvocationHandler);
    }
    /**
     *
     * @param cls 类型
     * @param callback 拦截器
     * @param <T> 类型
     * @return 对象
     */
    @SuppressWarnings("all")
    public static <T> T createProxyInstance(Class<T> cls, Callback callback){
        return (T) Enhancer.create(cls, callback);
    }
}
