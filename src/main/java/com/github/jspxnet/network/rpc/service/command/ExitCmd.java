package com.github.jspxnet.network.rpc.service.command;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.cmd.ICmd;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.utils.BeanUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/21 22:45
 * description: 请求退出
 **/
public class ExitCmd  extends INetCommand{
    static final private BeanFactory BEAN_FACTORY = EnvFactory.getBeanFactory();
    static final public String NAME = INetCommand.EXIT;

    @Override
    public SendCmd execute(Channel channel, SendCmd command)
    {
        channel.close();
        return BeanUtil.copy(command, SendCmd.class);
    }


}

