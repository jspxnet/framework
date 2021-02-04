package com.github.jspxnet.network.rpc.client.proxy;

import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.rpc.client.NettyClientPool;
import com.github.jspxnet.network.rpc.env.MasterSocketAddress;
import com.github.jspxnet.network.rpc.model.SendCommandFactory;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.route.RouteChannelManage;
import com.github.jspxnet.network.rpc.model.route.RouteSession;
import com.github.jspxnet.network.rpc.model.transfer.IocRequest;
import com.github.jspxnet.network.rpc.model.transfer.RequestTo;
import com.github.jspxnet.network.rpc.model.transfer.ResponseTo;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.txweb.AssertException;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.URLUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
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
    private RequestTo request;
    private ResponseTo response;
    private String serviceName;

    //ioc 名称,类名
    private String url;


    public RequestTo getRequest() {
        return request;
    }

    public void setRequest(RequestTo request) {
        this.request = request;
    }

    public ResponseTo getResponse() {
        return response;
    }

    public void setResponse(ResponseTo response) {
        this.response = response;
    }

    public String getServiceName() {
        return serviceName;
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
        IocRequest iocRequest = new IocRequest();
        iocRequest.setMethodName(method.getName());
        iocRequest.setParameters(parameterJson.toString());
        iocRequest.setRequest(request);
        iocRequest.setResponse(response);
        iocRequest.setUrl(url);
        command.setData(EncryptUtil.getBase64Encode(ObjectUtil.getSerializable(iocRequest)));

        if (StringUtil.isEmpty(serviceName))
        {
            serviceName = URLUtil.getRootNamespace(url);
        }
        if (StringUtil.isEmpty(serviceName)||StringUtil.ASTERISK.equals(serviceName))
        {
            serviceName = "default";
        }
        MasterSocketAddress masterSocketAddress = MasterSocketAddress.getInstance();

        InetSocketAddress address = masterSocketAddress.getSocketAddress(serviceName);
        AssertException.isNull(address,"TCP调用没有配置服务器地址");

        SendCmd reply = NettyClientPool.getInstance().send(address, command);
        if (masterSocketAddress.remoteGroupSocketAddress(serviceName,address))
        {
            RouteChannelManage routeChannelManage = RouteChannelManage.getInstance();
            RouteSession routeSession = new RouteSession();
            routeSession.setHeartbeatTimes(0);
            routeSession.setOnline(YesNoEnumType.YES.getValue());
            routeSession.setLastRequestTime(System.currentTimeMillis());
            routeSession.setCreateTimeMillis(System.currentTimeMillis());
            routeSession.setGroupName(serviceName);
            routeSession.setSocketAddress(address);
            routeChannelManage.joinCheckRoute(routeSession);
        }
        return null;
/*
        if (INetCommand.RPC.equalsIgnoreCase(reply.getAction()) && INetCommand.TYPE_BASE64.equals(reply.getType())) {
            IocResponse iocResponse;
            try {
                iocResponse = ObjectUtil.getUnSerializable(EncryptUtil.getBase64Decode(reply.getData()));
            } catch (Throwable e) {
                log.debug("iocRequest={},error:{}",ObjectUtil.toString(iocRequest),e.getMessage());
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
        }*/
       // return null;
    }
}
