package com.github.jspxnet.network.rpc.client.proxy;

import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.rpc.client.NettyClientPool;
import com.github.jspxnet.network.rpc.env.DiscoveryServiceAddress;
import com.github.jspxnet.network.rpc.model.SendCommandFactory;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.route.impl.RouteChannelManage;
import com.github.jspxnet.network.rpc.model.route.RouteManage;
import com.github.jspxnet.network.rpc.model.route.RouteSession;
import com.github.jspxnet.network.rpc.model.transfer.IocRequest;
import com.github.jspxnet.network.rpc.model.transfer.IocResponse;
import com.github.jspxnet.network.rpc.model.transfer.RequestTo;
import com.github.jspxnet.network.rpc.model.transfer.ResponseTo;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.util.HessianSerializableUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.URLUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;

/**
 * Created by jspx.net
 * <p>
 * author: chenYuan
 * date: 2020/7/10 21:22
 * description: Rpc代理,是用cglib
 **/
@Slf4j
public class RpcMethodInterceptor implements MethodInterceptor {
    private String serviceName;
    private InetSocketAddress address;
    //ioc 名称,类名
    private String url;

    private HttpServletRequest request = null;
    private HttpServletResponse response = null;

    public RpcMethodInterceptor()
    {

    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

        //封装ClassInfo
        SendCmd command = SendCommandFactory.createCommand(INetCommand.RPC);
        command.setType(INetCommand.TYPE_BASE64);
        JSONObject parameterJson = new JSONObject();
        int i=0;
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter:parameters)
        {
            if (i<args.length)
            {
                parameterJson.put(parameter.getName(),args[i]);
            }
            i++;
        }

        if (request==null||response==null)
        {
            ActionContext actionContext = ThreadContextHolder.getContext();
            request = actionContext.getRequest();
            response = actionContext.getResponse();
        }

        IocRequest iocRequest = new IocRequest();
        iocRequest.setMethodName(method.getName());
        iocRequest.setParameters(parameterJson.toString());
        iocRequest.setRequest(new RequestTo(request));
        iocRequest.setResponse(new ResponseTo(response));
        iocRequest.setUrl(url);

        command.setData(EncryptUtil.getBase64Encode(HessianSerializableUtil.getSerializable(iocRequest)));

        if (StringUtil.isEmpty(serviceName))
        {
            serviceName = URLUtil.getRootNamespace(url);
        }
        if (StringUtil.isEmpty(serviceName)||StringUtil.ASTERISK.equals(serviceName))
        {
            serviceName = "default";
        }

        InetSocketAddress address = DiscoveryServiceAddress.getSocketAddress(serviceName);
        if (address==null&&this.address!=null)
        {
            address = this.address;
        }
        if (address==null)
        {
            Thread.sleep(500);
            address = DiscoveryServiceAddress.getSocketAddress(serviceName);
        }

        if (address==null)
        {
            log.error("TCP RPC 调用没有配置服务器地址:{}",serviceName);
            throw new Exception("TCP RPC 调用没有分组服务器地址:" + serviceName);
        }


        SendCmd reply = NettyClientPool.getInstance().send(address, command);
        if (reply == null) {
            //异常后删除重新检查
            RouteManage routeManage = RouteChannelManage.getInstance();
            RouteSession routeSession = new RouteSession();
            routeSession.setHeartbeatTimes(0);
            routeSession.setOnline(YesNoEnumType.YES.getValue());
            routeSession.setLastRequestTime(System.currentTimeMillis());
            routeSession.setCreateTimeMillis(System.currentTimeMillis());
            routeSession.setGroupName(serviceName);
            routeSession.setSocketAddress(address);
            routeManage.joinCheckRoute(routeSession);
            Thread.sleep(100);
            reply = NettyClientPool.getInstance().send(address, command);
            if (reply==null)
            {
                log.error("TCP RPC 调用没有得到返回数据，已经重复过一次:{}",address);
                return null;
            }
        }

        //返回结果
        if (INetCommand.RPC.equalsIgnoreCase(reply.getAction()) && INetCommand.TYPE_BASE64.equals(reply.getType())) {
            IocResponse iocResponse;
            try {
                iocResponse = HessianSerializableUtil.getUnSerializable(EncryptUtil.getBase64Decode(reply.getData()));
            } catch (Throwable e) {
                log.error("iocRequest={},error:{}",ObjectUtil.toString(iocRequest),e.getMessage());
                e.printStackTrace();
                iocResponse = new IocResponse();
                iocResponse.setError(e);
            }
            if (iocResponse == null) {
                return null;
            }
            if (iocResponse.getError() != null) {
                throw iocResponse.getError();
            }
            return iocResponse.getResult();
        }
        log.error("TCP RPC 调用没有得到返回数据，检查调用服务器是否启动运行正常:{}",address);
        return null;
    }
}