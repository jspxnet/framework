package com.github.jspxnet.network.rpc.client;

import com.github.jspxnet.network.rpc.env.RpcConfig;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.charset.StandardCharsets;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/11 21:27
 * description: jspbox
 **/
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {


    public ClientChannelInitializer()
    {

    }


    @Override
    public void initChannel(SocketChannel ch)  {
        RpcConfig rpcConfig = RpcConfig.getInstance();;
        ch.config().setSendBufferSize(rpcConfig.getBufferSize());
        ch.config().setReceiveBufferSize(rpcConfig.getBufferSize());
        ch.config().setTcpNoDelay(true);
        ch.config().setAllocator(PooledByteBufAllocator.DEFAULT);
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new LengthFieldBasedFrameDecoder(rpcConfig.getMaxFrameLength(), 0, 4, 0, 4));
        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));
        pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8));

        //心跳促发
        pipeline.addLast(new IdleStateHandler(0, 0, 5));

        //逻辑处理
        pipeline.addLast(new ClientHandlerAdapter());
    }


}
