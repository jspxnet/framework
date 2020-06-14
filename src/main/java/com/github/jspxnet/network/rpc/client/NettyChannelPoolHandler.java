package com.github.jspxnet.network.rpc.client;

import com.github.jspxnet.network.rpc.env.RpcConfig;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/16 1:07
 * description: jspbox
 **/
@Slf4j
public class NettyChannelPoolHandler implements ChannelPoolHandler {



    @Override
    public void channelReleased(Channel ch) throws Exception {
        //log.debug("---------channelReleased. Channel ID: " + ch.id());
    }
    @Override
    public void channelAcquired(Channel ch) throws Exception {
        //log.debug("---------channelAcquired. Channel ID: " + ch.id());
    }
    @Override
    public void channelCreated(Channel ch) throws Exception {
        //log.debug("---------channelCreated. Channel ID: " + ch.id());

        ch.config().setAllocator(PooledByteBufAllocator.DEFAULT);
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(RpcConfig.getInstance().getMaxFrameLength(), 0, 4, 0, 4));
        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));
        pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8));

        //心跳促发
        pipeline.addLast(new IdleStateHandler(0, 0, 5));
        //逻辑处理
        pipeline.addLast(new ClientHandlerAdapter());

    }
}