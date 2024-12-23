package com.github.jspxnet.network.rpc.service.command;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.transfer.IocRequest;
import com.github.jspxnet.network.rpc.model.transfer.IocResponse;
import com.github.jspxnet.network.rpc.model.transfer.RequestTo;
import com.github.jspxnet.network.rpc.model.transfer.ResponseTo;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.WebConfigManager;
import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.config.TxWebConfigManager;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.dispatcher.handle.RocHandle;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.txweb.proxy.DefaultActionInvocation;
import com.github.jspxnet.txweb.result.RpcResult;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.util.HessianSerializableUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.URLUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.Map;

/**
 * Created by jspx.net
 * <p>
 * author: chenYuan
 * date: 2020/6/21 16:36
 * description: IOC执行容器
 **/
@Slf4j
public class RpcCmd extends INetCommand {

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
                iocRequest = HessianSerializableUtil.getUnSerializable(EncryptUtil.getBase64Decode(command.getData()));
            } catch (Throwable e) {
                IocResponse rpcResponse = new IocResponse();
                rpcResponse.setError(e);
                try {
                    reply.setData(EncryptUtil.getBase64Encode(HessianSerializableUtil.getSerializable(rpcResponse)));
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
                reply.setData(EncryptUtil.getBase64Encode(HessianSerializableUtil.getSerializable(rpcResponse)));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return reply;
        }

        if (StringUtil.isNull(iocRequest.getMethodName())) {
            IocResponse rpcResponse = new IocResponse();
            rpcResponse.setError(new Exception("不存在的请求方法"));
            try {
                reply.setData(EncryptUtil.getBase64Encode(HessianSerializableUtil.getSerializable(rpcResponse)));
            } catch (IOException ioException) {
                ioException.printStackTrace();
                rpcResponse.setError(ioException);
            }
            return reply;
        }

        exeAction(iocRequest, reply);
        return reply;
    }

    static public Map<String, Object> createEnvironment(IocRequest iocRequest)  {
        String namePart = URLUtil.getFileNamePart(iocRequest.getUrl());
        String namespace = URLUtil.getNamespace(iocRequest.getUrl());
        if (!StringUtil.hasLength(namespace)) {
            namespace = TXWeb.global;
        }

        if (!StringUtil.hasLength(namePart) || StringUtil.BACKSLASH.equals(namePart)) {
            namePart = "index";
        }
        ////////////////////action begin

        //////////////////////////////////环境参数 begin
        Map<String, Object> envParams = TXWebUtil.createEnvironment();
        envParams.put(ActionEnv.Key_ActionName, namePart);
        envParams.put(ActionEnv.Key_Namespace, namespace);
        envParams.put(ActionEnv.Key_RealPath, Dispatcher.getRealPath());
        ///////////////////////////////////环境参数 end
        return envParams;
    }
    static public Map<String, Object> createRocEnvironment(ActionConfig actionConfig, IocRequest iocRequest)  {
        Map<String, Object> envParams = createEnvironment(iocRequest);
        //命名空间初始化begin
        String namespace = actionConfig.getNamespace();
        if (!Sioc.global.equals(namespace) && !StringUtil.isEmpty(namespace))
        {
            envParams.put(ActionEnv.Key_Namespace, namespace);
        }
        String  namePart =  StringUtil.substringAfter(StringUtil.toLowerCase(iocRequest.getUrl()),namespace);

        if (namePart!=null&& namePart.contains(StringUtil.DOT))
        {
            namePart = namePart.substring(0, namePart.lastIndexOf(StringUtil.DOT));
        }
        if (namePart!=null&&namePart.startsWith(StringUtil.BACKSLASH))
        {
            namePart = namePart.substring(1);
        }
        if (StringUtil.isEmpty(namePart))
        {
            namePart = "index";
        }
        if (!StringUtil.isEmpty(namePart))
        {
            envParams.put(ActionEnv.Key_ActionName, namePart);
        }
        //命名空间初始化end
        return envParams;
    }


    static protected ActionConfig getActionConfig(IocRequest iocRequest) throws Exception {
        String namePart = URLUtil.getFileNamePart(iocRequest.getUrl());
        String namespace = URLUtil.getNamespace(iocRequest.getUrl());
        if (!StringUtil.hasLength(namespace)) {
            namespace = TXWeb.global;
        }
        if (!StringUtil.hasLength(namePart) || StringUtil.BACKSLASH.equals(namePart)) {
            namePart = "index";
        }
        WebConfigManager webConfigManager = TxWebConfigManager.getInstance();
        return webConfigManager.getActionConfig(namePart, namespace, true);
    }


    public static void exeAction(IocRequest iocRequest,SendCmd reply) {

        //得到请求对象
        ActionConfig actionConfig = null;
        try {
            actionConfig = getActionConfig(iocRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (actionConfig == null) {
            IocResponse rpcResponse = new IocResponse();
            rpcResponse.setError(new Exception("class not found.找不到执行对象,检查actionName"));
            try {
                reply.setData(EncryptUtil.getBase64Encode(HessianSerializableUtil.getSerializable(rpcResponse)));
            } catch (IOException e) {
                rpcResponse.setError(e);
            }
            return;
        }

        JSONObject json = new JSONObject();
        json.put(Environment.Protocol, Environment.jspxNetRoc);
        json.put(Environment.rocFormat, WebOutEnumType.JSON.getName());
        JSONObject methodJson = new JSONObject();
        methodJson.put(Environment.rocName, iocRequest.getMethodName());
        methodJson.put(Environment.rocParams, new JSONObject(iocRequest.getParameters()));
        json.put(Environment.rocMethod, methodJson);

        Map<String, Object>   envParam = createRocEnvironment(actionConfig,  iocRequest);
        RequestTo requestTo = new RequestTo((Map<String,Object>)iocRequest.getRequest());
        ResponseTo responseTo = new ResponseTo((Map<String,Object>)iocRequest.getResponse());

        requestTo.setAttribute(ActionEnv.Key_REMOTE_TYPE,INetCommand.RPC);
        IocResponse response = new IocResponse();
        ActionInvocation actionInvocation = null;
        try {
            actionInvocation = new DefaultActionInvocation(actionConfig,envParam , RocHandle.NAME,json,requestTo,responseTo);
            actionInvocation.initAction();
            actionInvocation.invoke();
        } catch (Throwable t) {
            response.setError(t);
            t.printStackTrace();
        } finally {
            if (actionInvocation!=null)
            {
                RpcResult rpcResult = new RpcResult();
                try {
                    actionInvocation.executeResult(rpcResult);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.setError(e);
                }
                response.setResult(rpcResult.getResult());
            }
        }
        try {
            reply.setData(EncryptUtil.getBase64Encode(HessianSerializableUtil.getSerializable(response)));
        } catch (IOException e) {
            response.setError(e);
            e.printStackTrace();
        }
    }

    @Override
    public SendCmd execute(ChannelHandlerContext ctx, SendCmd command) {
        //收到要退出的通知
        return execute(ctx.channel(), command);
    }
}
