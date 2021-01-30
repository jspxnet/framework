package com.github.jspxnet.network.rpc.service.command;

import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.utils.BeanUtil;
import io.netty.channel.Channel;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/23 23:32
 * description: jspx-framework
 **/
public class PingCmd extends INetCommand {

    static final public String NAME = INetCommand.PING;

    /**
     * 执行方法
     * @param channel 连接
     * @param command  请求命令
     * @return 返回
     */
    @Override
    public SendCmd execute(Channel channel, SendCmd command)
    {
        SendCmd reply = BeanUtil.copy(command, SendCmd.class);
        reply.setAction(INetCommand.PONG);
        return reply;
    }

}

