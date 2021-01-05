package com.github.jspxnet.sioc.rpc;


import com.github.jspxnet.network.rpc.client.proxy.NettyRpcProxy;
import com.github.jspxnet.network.rpc.client.proxy.RpcMethodInterceptor;
import com.github.jspxnet.network.rpc.model.transfer.RequestTo;
import com.github.jspxnet.network.rpc.model.transfer.ResponseTo;
import com.github.jspxnet.sioc.annotation.RpcClient;
import com.github.jspxnet.txweb.enums.RpcProtocolEnumType;
import com.github.jspxnet.txweb.service.HessianClient;
import com.github.jspxnet.txweb.service.client.HessianClientFactory;
import com.github.jspxnet.util.CglibProxyUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/1/5 23:19
 * description: jspbox
 **/
public class RpcClientInvocationHandler implements InvocationHandler {
    private Class<?> target;
    private Object targetObject;
    private HttpServletRequest request;
    private HttpServletResponse response;
    public RpcClientInvocationHandler(Class<?> target)
    {
        this.target = target;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("---------method:" + method.getName());
        System.out.println("---------args:" + ObjectUtil.toString(args));
        if ("getTarge".equals(method.getName())||"getClass".equals(method.getName())) {

            return target;
        }
        if ("toString".equals(method.getName())) {

            return target.toString();
        }
        if ("getRpcClient".equals(method.getName())) {

            return target.getAnnotation(RpcClient.class);
        }
        if ("setRequest".equals(method.getName())) {

            request = (HttpServletRequest)args[0];
            return null;
        }
        if ("setResponse".equals(method.getName())) {

            response = (HttpServletResponse)args[0];
            return null;
        }

        RpcClient rpcClient = target.getAnnotation(RpcClient.class);
        if (RpcProtocolEnumType.TCP.equals(rpcClient.protocol()))
        {
            if (request!=null&&response!=null)
            {
                targetObject = NettyRpcProxy.create(target,rpcClient.url(),new RequestTo(request),new ResponseTo(response),rpcClient.groupName());
            } else
            {
                targetObject = NettyRpcProxy.create(target,rpcClient.url(),rpcClient.groupName());
            }
            return method.invoke(targetObject, args);
        }
        if (RpcProtocolEnumType.HTTP.equals(rpcClient.protocol())) {
            HessianClient hessianClient = HessianClientFactory.getInstance();
            //读取本地配置
            String hessianUrl = rpcClient.url();
            if (StringUtil.isNull(hessianUrl)) {
                throw new Exception(target.getName() + " RpcClient url is null,不允许为空");
            }
            return hessianClient.getInterface(target, hessianUrl);
        }
        return method.invoke(target, args);
    }
}
