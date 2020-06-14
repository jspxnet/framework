package com.github.jspxnet.network.rpc.service.command;

import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.route.RouteChannelManage;
import com.github.jspxnet.utils.BeanUtil;
import io.netty.channel.Channel;

public class RouteCmd extends INetCommand {
    private static final RouteChannelManage routeChannelManage = RouteChannelManage.getInstance();

    static final public String NAME = INetCommand.ROUTE;
    /**
     * 执行方法
     * @param channel 连接
     * @param command  请求命令
     * @return 返回
     */
    @Override
    public SendCmd execute(Channel channel, SendCmd command)
    {
        SendCmd replyCmd = BeanUtil.copy(command, SendCmd.class);
        replyCmd.setAction(INetCommand.ROUTE);
        replyCmd.setType(INetCommand.TYPE_JSON);
        replyCmd.setData(routeChannelManage.getSendRouteTable());
        return replyCmd;
    }
}

