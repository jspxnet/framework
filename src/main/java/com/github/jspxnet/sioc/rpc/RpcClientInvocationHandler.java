package com.github.jspxnet.sioc.rpc;


import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.network.rpc.client.proxy.NettyRpcProxy;
import com.github.jspxnet.sioc.annotation.RpcClient;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.enums.RpcProtocolEnumType;
import com.github.jspxnet.txweb.service.HessianClient;
import com.github.jspxnet.txweb.service.client.HessianClientFactory;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.URLUtil;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/1/5 23:19
 * description: jspbox
 **/
@Slf4j
public class RpcClientInvocationHandler implements InvocationHandler {

    private final Class<?> target;

    public RpcClientInvocationHandler(Class<?> target)
    {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (target==null)
        {
            throw new Exception("target 调用类对象不允许为空");
        }
        if ("getTarge".equals(method.getName())||"getClass".equals(method.getName())) {
            return target;
        }
        if ("toString".equals(method.getName())) {
            return target.toString();
        }
        if ("getRpcClient".equals(method.getName())) {
            return target.getAnnotation(RpcClient.class);
        }

        RpcClient rpcClient = target.getAnnotation(RpcClient.class);
        if (rpcClient!=null)
        {

            if (RpcProtocolEnumType.TCP.equals(rpcClient.protocol()))
            {
                Object  targetObject = NettyRpcProxy.create(target,rpcClient.url(),rpcClient.groupName());
                if (targetObject==null)
                {
                    throw new Exception(targetObject + " Rpc 创建远程调用对象失败,确认远程服务器已经启动");
                }
                Object result = null;
                if (args==null)
                {
                    result = method.invoke(targetObject);
                } else
                {
                    result =  method.invoke(targetObject, args);
                }
                return result;
            }
            if (RpcProtocolEnumType.HTTP.equals(rpcClient.protocol())) {
                //读取本地配置
                ActionContext actionContext = ThreadContextHolder.getContext();
                HttpServletRequest request = actionContext.getRequest();
                String hessianUrl = rpcClient.url();
                if (StringUtil.isNull(hessianUrl)) {
                    throw new Exception(target.getName() + " RpcClient url is null,不允许为空");
                }
                EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
                if (!hessianUrl.startsWith("http"))
                {
                    String domain = envTemplate.getString(Environment.HTTP_RPC_DOMAIN,"http://127.0.0.1");
                    String namespace = URLUtil.getRootNamespace(hessianUrl);
                    String routesUrl = envTemplate.getString(Environment.HTTP_RPC_ROUTES + namespace);
                    hessianUrl = URLUtil.getFixHessianUrl( hessianUrl, domain, namespace, routesUrl);
                }
                String apiFilterSuffix = envTemplate.getString(Environment.ApiFilterSuffix,"jwc");
                hessianUrl = URLUtil.getFixSuffix(hessianUrl,apiFilterSuffix);
                try {
                    HessianClient hessianClient = HessianClientFactory.getInstance();
                    Object targetObject = hessianClient.create(target,hessianUrl,RequestUtil.getToken(request));
                    return method.invoke(targetObject, args);
                } catch (Exception e)
                {
                    log.error("检查http rpc 调用路径是否正确:{},error:{}",hessianUrl,e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }
        return method.invoke(target, args);
    }
}
