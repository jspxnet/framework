package com.github.jspxnet.network.rpc.service.command;

import com.github.jspxnet.network.rpc.model.cmd.ICmd;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.route.RouteChannelManage;
import com.github.jspxnet.utils.BeanUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/24 22:30
 * description: jspbox
 * 请求后得到的路由表
 **/
public class GetRouteCmd extends INetCommand {
    private RouteChannelManage routeChannelManage = RouteChannelManage.getInstance();

    static final public String NAME = INetCommand.GET_ROUTE;
    @Override
    public SendCmd execute(Channel channel, SendCmd command)
    {
        SendCmd reply = BeanUtil.copy(command, SendCmd.class);
        reply.setAction(INetCommand.GET_ROUTE);
        reply.setType(INetCommand.TYPE_JSON);
        reply.setData(routeChannelManage.getSendRouteTable());
        return reply;
    }

}