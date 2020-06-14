package com.github.jspxnet.network.rpc.client.proxy;

import com.github.jspxnet.network.rpc.env.MasterSocketAddress;
import com.github.jspxnet.network.rpc.model.transfer.RequestTo;
import com.github.jspxnet.network.rpc.model.transfer.ResponseTo;
import com.github.jspxnet.util.CglibProxyUtil;
import com.github.jspxnet.utils.StringUtil;
import java.net.SocketAddress;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/10 22:13
 * description: jspbox
 **/
public class NettyRpcProxy {

    static  public <T>T  create(Class<T> target)
    {
        return create(target,null,null,null,null, null);
    }

    static  public <T>T  create(Class<T> target,SocketAddress address)
    {
        return create(target,null,null,null,null,address);
    }

    static  public <T>T create(Class<T> target,String namespace,SocketAddress address)
    {
        return create(target,null,namespace,null,null,address);
    }

    static  public <T>T create(Class<T> target,  String namespace, RequestTo requestTo, ResponseTo responseTo)
    {
        return create(target,null,namespace,requestTo,responseTo,null);
    }

    static  public <T>T create(Class<T> target, String actionName, String namespace, RequestTo requestTo, ResponseTo responseTo)
    {
        return create(target,actionName,namespace,requestTo,responseTo,null);
    }
    /**
     *
     * @param target 类对象
     * @param actionName ioc名称
     * @param namespace  命名空间
     * @param requestTo web请求
     * @param responseTo web应答
     * @param address 服务器地址
     * @param <T> 类型
     * @return 代理对象
     */
    static  public <T>T create(Class<T>  target,String actionName,String namespace, RequestTo requestTo, ResponseTo responseTo, SocketAddress address)
    {
        if (address==null)
        {
            address = MasterSocketAddress.getInstance().getSocketAddress();
        }
        RpcMethodInterceptor clientInvocationHandler = new RpcMethodInterceptor();
        clientInvocationHandler.setClassName(StringUtil.isNull(actionName)?target.getName():actionName);
        clientInvocationHandler.setNamespace(namespace);
        clientInvocationHandler.setRequest(requestTo);
        clientInvocationHandler.setResponse(responseTo);
        clientInvocationHandler.setAddress(address);
        return CglibProxyUtil.getProxyInstance(target,clientInvocationHandler);
    }

}
