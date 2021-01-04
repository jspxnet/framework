package com.github.jspxnet.network.rpc.client.proxy;

import com.github.jspxnet.network.rpc.client.NettyClientPool;
import com.github.jspxnet.network.rpc.model.SendCommandFactory;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.transfer.IocRequest;
import com.github.jspxnet.network.rpc.model.transfer.IocResponse;
import com.github.jspxnet.network.rpc.model.transfer.RequestTo;
import com.github.jspxnet.network.rpc.model.transfer.ResponseTo;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.utils.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.net.SocketAddress;

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
    private SocketAddress address;

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

    public SocketAddress getAddress() {
        return address;
    }

    public void setAddress(SocketAddress address) {
        this.address = address;
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
        IocRequest iocRequest = new IocRequest();
        iocRequest.setMethodName(method.getName());
        iocRequest.setParameters(args);
        iocRequest.setParameterTypes(method.getParameterTypes());
        iocRequest.setRequest(request);
        iocRequest.setResponse(response);
        iocRequest.setUrl(url);


        log.debug("iocRequest={}",ObjectUtil.toString(iocRequest));

        command.setData(EncryptUtil.getBase64Encode(ObjectUtil.getSerializable(iocRequest)));
        SendCmd reply = NettyClientPool.getInstance().send(address, command);
        if (null == reply) {
            return null;
        }

        if (INetCommand.RPC.equalsIgnoreCase(reply.getAction()) && INetCommand.TYPE_BASE64.equals(reply.getType())) {
            IocResponse iocResponse;
            try {
                iocResponse = ObjectUtil.getUnSerializable(EncryptUtil.getBase64Decode(reply.getData()));
            } catch (Throwable e) {
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
        return null;
    }
}
