package com.github.jspxnet.network.rpc.client.proxy;

import com.github.jspxnet.network.rpc.model.transfer.RequestTo;
import com.github.jspxnet.network.rpc.model.transfer.ResponseTo;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

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


    static  public <T>T create(Class<T>  target,String url, String serviceName)
    {
        return create(target,url,null,null,serviceName);
    }
    static  public <T>T create(Class<T>  target,String url, RequestTo requestTo, ResponseTo responseTo)
    {
        return create(target,url,requestTo,responseTo,null);
    }
    /**
     *
     * @param target 类对象
     * @param url 路径
     * @param requestTo web请求
     * @param responseTo web应答
     * @param serviceName 服务器分组服务, 默认就是网关路径
     * @param <T> 类型
     * @return 代理对象
     */
    static  public <T>T create(Class<T>  target,String url, RequestTo requestTo, ResponseTo responseTo, String serviceName)
    {
        RpcMethodInterceptor clientInvocationHandler = new RpcMethodInterceptor();
        clientInvocationHandler.setUrl(url);
        clientInvocationHandler.setRequest(requestTo);
        clientInvocationHandler.setResponse(responseTo);
        clientInvocationHandler.setServiceName(serviceName);
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
    public static <T> T createProxyInstance(Class<T> cls, Callback callback){
        return (T) Enhancer.create(cls, callback);
    }
}
