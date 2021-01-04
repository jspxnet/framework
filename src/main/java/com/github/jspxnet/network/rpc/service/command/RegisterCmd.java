package com.github.jspxnet.network.rpc.service.command;

import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.route.RouteChannelManage;
import com.github.jspxnet.network.rpc.model.route.RouteSession;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.IpUtil;
import com.github.jspxnet.utils.StringUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/23 0:18
 * description: jspbox
 *
 **/
@Slf4j
public class RegisterCmd extends INetCommand {

    static final public String NAME = INetCommand.REGISTER;
    private static final RouteChannelManage routeChannelManage = RouteChannelManage.getInstance();
    /**
     * 执行方法
     * @param channel 连接
     * @param command  请求命令
     * @return 返回
     */
    @Override
    public SendCmd execute(Channel channel, SendCmd command)
    {
        //有服务器注册上来
        if (INetCommand.TYPE_JSON.equals(command.getType()))
        {
            //注册上来的是一个地址列表,还没有路由表,将路由表给对方
            String str = command.getData();
            if (StringUtil.isJsonObject(str))
            {
                RouteSession routeSession = new RouteSession();
                routeSession.setSocketAddress(channel.remoteAddress());
                routeSession.setOnline(YesNoEnumType.NO.getValue());
                routeSession.setHeartbeatTimes(0);
                routeSession.setGroupName(str);
                routeChannelManage.addCheckRouteSocket(routeSession);
            }
        }

        //注册上来的同时将最新的路由表发给对方
        SendCmd replyCmd = BeanUtil.copy(command, SendCmd.class);
        replyCmd.setAction(INetCommand.OK);
        replyCmd.setType(INetCommand.TYPE_JSON);
        replyCmd.setData(routeChannelManage.getSendRouteTable());
        //log.debug("当前路由表-----------------\r\n{}",routeChannelManage.getSendRouteTable());
        return replyCmd;
    }
}


