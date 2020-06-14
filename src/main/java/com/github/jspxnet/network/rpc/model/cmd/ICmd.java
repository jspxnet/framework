package com.github.jspxnet.network.rpc.model.cmd;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public interface ICmd {

    SendCmd execute(Channel channel, SendCmd command);

    SendCmd execute(ChannelHandlerContext ctx, SendCmd command);
}
