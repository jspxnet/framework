package com.github.jspxnet.network.rpc.service.command;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.transfer.IocRequest;
import com.github.jspxnet.network.rpc.model.transfer.IocResponse;
import com.github.jspxnet.network.rpc.model.transfer.RequestTo;
import com.github.jspxnet.network.rpc.model.transfer.ResponseTo;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.WebConfigManager;
import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.config.TXWebConfigManager;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.proxy.DefaultActionInvocation;
import com.github.jspxnet.txweb.result.RpcResult;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by jspx.net
 * <p>
 * author: chenYuan
 * date: 2020/6/21 16:36
 * description: IOC执行容器
 **/
public class RpcCmd extends INetCommand {
    static final private BeanFactory BEAN_FACTORY = EnvFactory.getBeanFactory();
    static final public String NAME = INetCommand.RPC;

    /**
     * 执行方法
     *
     * @param channel 连接
     * @param command 请求命令
     * @return 返回
     */
    @Override
    public SendCmd execute(Channel channel, SendCmd command) {
        SendCmd reply = BeanUtil.copy(command, SendCmd.class);
        IocRequest iocRequest = null;

        //解码
        if (INetCommand.TYPE_BASE64.equals(command.getType())) {
            try {
                iocRequest = ObjectUtil.getUnSerializable(EncryptUtil.getBase64Decode(command.getData()));
            } catch (Throwable e) {
                e.printStackTrace();
                IocResponse rpcResponse = new IocResponse();
                rpcResponse.setError(e);
                try {
                    reply.setData(EncryptUtil.getBase64Encode(ObjectUtil.getSerializable(rpcResponse)));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                return reply;
            }
        }


        if (iocRequest == null) {
            IocResponse rpcResponse = new IocResponse();
            rpcResponse.setError(new Exception("不存在的请求对象"));
            try {
                reply.setData(EncryptUtil.getBase64Encode(ObjectUtil.getSerializable(rpcResponse)));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return reply;
        }

        if (StringUtil.isNull(iocRequest.getMethodName())) {
            IocResponse rpcResponse = new IocResponse();
            rpcResponse.setError(new Exception("不存在的请求方法"));
            try {
                reply.setData(EncryptUtil.getBase64Encode(ObjectUtil.getSerializable(rpcResponse)));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return reply;
        }

        if (iocRequest.getClassName().contains(".")) {
            exeIoc(iocRequest, reply);
        } else {
            exeAction(iocRequest, reply);
        }
        return reply;
    }

    public void exeIoc(IocRequest iocRequest, SendCmd reply) {

        //得到请求对象
        Object serviceBean = BEAN_FACTORY.getBean(iocRequest.getClassName(), iocRequest.getNamespace());
        if (serviceBean == null) {

            IocResponse rpcResponse = new IocResponse();
            rpcResponse.setError(new Exception("不存在的ioc对象," + ObjectUtil.toString(iocRequest)));
            try {
                reply.setData(EncryptUtil.getBase64Encode(ObjectUtil.getSerializable(rpcResponse)));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return;
        }

        //放入web请求
        //如果是ActionSupport 引入 Action 执行器
        if (serviceBean instanceof ActionSupport) {
            ActionSupport actionSupport = (ActionSupport) serviceBean;
            actionSupport.setRequest(new RequestTo((Map) iocRequest.getRequest()));
            actionSupport.setResponse(new ResponseTo((Map) iocRequest.getResponse()));
        }

        IocResponse response = new IocResponse();
        try {
            String methodName = iocRequest.getMethodName();
            Class<?>[] parameterTypes = iocRequest.getParameterTypes();
            Object[] parameters = iocRequest.getParameters();
            Method method = serviceBean.getClass().getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            Object result = method.invoke(serviceBean, parameters);
            response.setResult(result);
        } catch (Throwable t) {
            response.setError(t);
            t.printStackTrace();
        }

        try {
            reply.setData(EncryptUtil.getBase64Encode(ObjectUtil.getSerializable(response)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void exeAction(IocRequest iocRequest, SendCmd reply) {

        //得到请求对象
        WebConfigManager webConfigManager = TXWebConfigManager.getInstance();
        ActionConfig actionConfig = null;
        try {
            actionConfig = webConfigManager.getActionConfig(iocRequest.getClassName(), iocRequest.getNamespace(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (actionConfig == null) {
            IocResponse rpcResponse = new IocResponse();
            rpcResponse.setError(new Exception("class not found.找不到执行对象,检查actionName"));
            try {
                reply.setData(EncryptUtil.getBase64Encode(ObjectUtil.getSerializable(rpcResponse)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        JSONObject json = new JSONObject();
        json.put(Environment.rocFormat, WebOutEnumType.JSON.getName());
        JSONObject methodJson = new JSONObject();
        methodJson.put(Environment.rocName, iocRequest.getMethodName());
        methodJson.put(Environment.rocParams, new JSONArray(iocRequest.getParameters()));
        json.put(Environment.rocMethod, methodJson);

        IocResponse response = new IocResponse();
        try {
            ActionInvocation actionInvocation = new DefaultActionInvocation(actionConfig, TXWebUtil.createEnvironment(), "roc", json, new RequestTo((Map) iocRequest.getRequest()), new ResponseTo((Map) iocRequest.getResponse()));
            actionInvocation.initAction();
            actionInvocation.invoke();
            RpcResult rpcResult = new RpcResult();
            actionInvocation.executeResult(rpcResult);
            Object result = rpcResult.getResult();
            response.setResult(result);
        } catch (Throwable t) {
            response.setError(t);
            t.printStackTrace();
        }
        try {
            reply.setData(EncryptUtil.getBase64Encode(ObjectUtil.getSerializable(response)));
        } catch (IOException e) {
            e.printStackTrace();
            response.setError(e);
        }
    }

    @Override
    public SendCmd execute(ChannelHandlerContext ctx, SendCmd command) {
        //收到要退出的通知
        return execute(ctx.channel(), command);
    }
}
