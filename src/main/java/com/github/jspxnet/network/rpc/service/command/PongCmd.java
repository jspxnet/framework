package com.github.jspxnet.network.rpc.service.command;

import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.transfer.ChannelSession;
import com.github.jspxnet.network.rpc.service.SessionChannelManage;
import com.github.jspxnet.utils.BeanUtil;
import io.netty.channel.Channel;


/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/24 0:29
 * description:
 **/
public class PongCmd extends INetCommand {
    static final public String NAME = INetCommand.OK;

    /**
     * 执行方法
     * @param channel 连接
     * @param command  请求命令
     * @return 返回
     */
    @Override
    public SendCmd execute(Channel channel, SendCmd command)
    {
        SessionChannelManage sessionChannelManage = SessionChannelManage.getInstance();
        ChannelSession netSession = sessionChannelManage.getSession(channel.id());
        netSession.setHeartbeatTimes(0);
        netSession.setOnline(YesNoEnumType.YES.getValue());
        SendCmd reply = BeanUtil.copy(command, SendCmd.class);
        reply.setAction(INetCommand.OK);
        return reply;
    }

}