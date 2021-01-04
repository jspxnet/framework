package com.github.jspxnet.network.rpc.client.proxy;

import com.github.jspxnet.network.rpc.env.MasterSocketAddress;
import com.github.jspxnet.network.rpc.model.transfer.RequestTo;
import com.github.jspxnet.network.rpc.model.transfer.ResponseTo;
import com.github.jspxnet.txweb.AssertException;
import com.github.jspxnet.util.CglibProxyUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.URLUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/10 22:13
 * description: jspbox
 **/
@Slf4j
public class NettyRpcProxy {

    static  public <T>T  create(Class<T> target)
    {
        return create(target,null,null,null, null);
    }

    static  public <T>T create(Class<T> target, String url,String serviceName )
    {
        return create(target,url,null,null,serviceName);
    }
    static  public <T>T create(Class<T> target, String url, RequestTo requestTo, ResponseTo responseTo)
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
        if (StringUtil.isEmpty(serviceName))
        {
            serviceName = URLUtil.getRootNamespace(url);
        }
        if (StringUtil.isEmpty(serviceName))
        {
            serviceName = "default";
        }

        SocketAddress address = MasterSocketAddress.getInstance().getSocketAddress(serviceName);
        AssertException.isNull(address,"TCP调用没有配置服务器地址");
        RpcMethodInterceptor clientInvocationHandler = new RpcMethodInterceptor();
        clientInvocationHandler.setUrl(url);
        clientInvocationHandler.setRequest(requestTo);
        clientInvocationHandler.setResponse(responseTo);
        clientInvocationHandler.setAddress(address);
        return CglibProxyUtil.getProxyInstance(target,clientInvocationHandler);
    }

}
