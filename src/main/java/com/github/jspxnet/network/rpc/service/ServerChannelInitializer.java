package com.github.jspxnet.network.rpc.service;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/9 22:21
 * description: jspbox
 **/

import com.github.jspxnet.network.rpc.env.RpcConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.logging.InternalLogLevel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    public ServerChannelInitializer()
    {
    }

    @Override
    protected void initChannel(SocketChannel ch)  {
        RpcConfig rpcConfig = RpcConfig.getInstance();
        ch.config().setSendBufferSize(rpcConfig.getBufferSize());
        ch.config().setReceiveBufferSize(rpcConfig.getBufferSize());
        ch.config().setTcpNoDelay(true);
        //ch.pipeline().addLast("logging",new LoggingHandler("INFO"));//设置log监听器，并且日志级别为debug，方便观察运行流程
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new LengthFieldBasedFrameDecoder(rpcConfig.getMaxFrameLength(), 0, 4, 0, 4));
        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));
        pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8));

        //心跳促发,5秒不连接断开
        pipeline.addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));

        //逻辑处理
        pipeline.addLast(new ServerHandlerAdapter());

    }
}
