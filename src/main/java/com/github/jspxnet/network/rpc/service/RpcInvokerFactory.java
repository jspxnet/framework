package com.github.jspxnet.network.rpc.service;

import com.github.jspxnet.json.GsonUtil;
import com.github.jspxnet.network.rpc.model.transfer.IocResponse;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.cmd.ICmd;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.service.command.*;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.util.HessianSerializableUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
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
        //注册进来作为服务器,包含了请求路由的功能,GetRoute 作废
        CMD_ACTION_MAP.put(RegisterCmd.NAME,RegisterCmd.class.getName());

    }


    public static void invokeService(ChannelHandlerContext ctx,String str)  {
        if (ctx==null)
        {
            log.error("ChannelHandlerContext ctx 为空");
            return;
        }
        String jsonStr = INetCommand.getDecodePacket(str);
        if (!StringUtil.isJsonObject(jsonStr))
        {
            log.debug("str 不是有效的json,请求:{} \r\n返回:{}",str,jsonStr);
            return;
        }

        SendCmd command = GsonUtil.createGson().fromJson(jsonStr,SendCmd.class);
        if (command==null)
        {
            log.error("command json 格式解析非法:{}",jsonStr);
            return;
        }
        String actionCmdName =  CMD_ACTION_MAP.get(command.getAction());
        ICmd cmd = null;
        try {
            cmd = (ICmd) ClassUtil.newInstance(actionCmdName);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("异常载入命令:{}",actionCmdName);
            return;
        }
        SendCmd reply =  cmd.execute(ctx,command);
        if (reply==null||StringUtil.isNull(actionCmdName))
        {
            reply = BeanUtil.copy(command, SendCmd.class);
            IocResponse rpcResponse = new IocResponse();
            rpcResponse.setError(new Exception(actionCmdName+"未知的action命令名称"));
            try {
                reply.setData(EncryptUtil.getBase64Encode(HessianSerializableUtil.getSerializable(rpcResponse)));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            try {
                INetCommand.sendEncodePacket(ctx.channel(),reply);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("发送数据发生异常",e);
            }
            return;
        }

        try {
            INetCommand.sendEncodePacket(ctx.channel(),reply);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送数据发生异常",e);
        }

    }

}
