package com.github.jspxnet.network.rpc.service;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.rpc.model.transfer.IocResponse;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.cmd.ICmd;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.service.command.*;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/10 21:58
 * description: jspbox
 **/
@Slf4j
public class RpcInvokerFactory {

    final static private Map<String, String> CMD_ACTION_MAP = new HashMap<>();
    static
    {
        //ioc调用
        CMD_ACTION_MAP.put(RpcCmd.NAME, RpcCmd.class.getName());
        //客户端请求退出
        CMD_ACTION_MAP.put(ExitCmd.NAME,ExitCmd.class.getName());
        //得到路由信息, 同时返回本服务器知道的路由信息,客户端收到后不回复
        CMD_ACTION_MAP.put(RouteCmd.NAME,RouteCmd.class.getName());
        //ping
        CMD_ACTION_MAP.put(PingCmd.NAME,PingCmd.class.getName());
        //注册进来作为服务器
        CMD_ACTION_MAP.put(RegisterCmd.NAME,RegisterCmd.class.getName());
        //请求得到路由
        CMD_ACTION_MAP.put(GetRouteCmd.NAME,GetRouteCmd.class.getName());
    }


    public static void invokeService(ChannelHandlerContext ctx,String str) throws Exception {

        String jsonStr = INetCommand.getDecodePacket(str);
        if (null==jsonStr)
        {
            return;
        }

        JSONObject json = new JSONObject(jsonStr);
        SendCmd command = json.parseObject(SendCmd.class);
        String actionCmdName =  CMD_ACTION_MAP.get(command.getAction());
        if (StringUtil.isNull(actionCmdName))
        {
            SendCmd reply = BeanUtil.copy(command, SendCmd.class);
            IocResponse rpcResponse = new IocResponse();
            rpcResponse.setError(new Exception("未知的action命令名称"));
            try {
                reply.setData(EncryptUtil.getBase64Encode(ObjectUtil.getSerializable(rpcResponse)));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            INetCommand.sendEncodePacket(ctx.channel(),reply);
            return;
        }
        ICmd cmd = (ICmd)ClassUtil.newInstance(actionCmdName);
        if (!command.getAction().contains(INetCommand.ROUTE))
        {
            log.debug("<---{}接收到:{},cmd:{}",ctx.channel().localAddress(),ctx.channel().remoteAddress(),command.getAction());
        }
        SendCmd reply =  cmd.execute(ctx,command);
        //log.debug("--->回复到:{},cmd:{}",ctx.channel().remoteAddress(),ObjectUtil.toString(reply));
        if (reply!=null)
        {
            INetCommand.sendEncodePacket(ctx.channel(),reply);
        }
    }
}
