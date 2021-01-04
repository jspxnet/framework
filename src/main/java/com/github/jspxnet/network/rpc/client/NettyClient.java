package com.github.jspxnet.network.rpc.client;

import com.github.jspxnet.network.rpc.model.SendCommandFactory;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.env.RpcConfig;
import com.github.jspxnet.utils.StringUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import java.net.SocketAddress;
import java.util.concurrent.*;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/11 21:25
 * description:这个类只作为测试连接,等小低并发是用
 * 高并发是用连接池类
 *
 **/
@Slf4j
public class NettyClient {
    final private static ResultHashMap RESULT_HASH_MAP = ResultHashMap.getInstance();

    private NioEventLoopGroup workersGroup;
    private final Bootstrap bootstrap = new Bootstrap();
    private ChannelFuture channelFuture = null;
    private SocketAddress address;

    public NettyClient()
    {
        RpcConfig rpcConfig = RpcConfig.getInstance();
        workersGroup = new NioEventLoopGroup(rpcConfig.getWorkThread());
        bootstrap.group(workersGroup)
                .channel(NioSocketChannel.class)
                .handler(new ClientChannelInitializer())
                .option(ChannelOption.SO_RCVBUF, rpcConfig.getBufferSize())
                .option(ChannelOption.SO_KEEPALIVE, false);

    }

    public Channel connect(SocketAddress address)  {
        if (this.address!=null&&channelFuture!=null && this.address.equals(address))
        {
            Channel channel = channelFuture.channel();
            if (channel.isActive())
            {
                return channel;
            }
        }
        this.address = address;
        if (channelFuture!=null&&channelFuture.isSuccess())
        {
            channelFuture.channel().close();
            channelFuture.channel().closeFuture();

        }
        channelFuture = bootstrap.connect(this.address);
        //这里添加短线重连
        channelFuture.addListener(new ChannelFutureListener() {
            Channel channel;
            @Override
            public void operationComplete(ChannelFuture futureListener) {
                if (futureListener.isSuccess()) {
                    channel = futureListener.channel();
                } /*else {
                    log.info("Failed to connect to server, try connect after 3s:3秒后重连");
                    futureListener.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            channelFuture = bootstrap.connect(address);
                        }
                    }, 3, TimeUnit.SECONDS);
                }*/
            }
        });
        return channelFuture.channel();
    }


    /**
     *
     * @param address  发送地址
     * @param command 发送命令
     * @return 返回结果
     * @throws Exception 异常
     */
    public SendCmd send(SocketAddress address, SendCmd command) throws Exception
    {
        Channel channel = connect(address);
        SendCmd reply = sender(channel,  command);
        if (reply==null)
        {
            return SendCommandFactory.createExceptionCommand("不存在的指令");
        }
        if (ReplyCmdFactory.isSysCmd(reply.getAction()))
        {
            //系统指令这里就直接执行了
            try {
                reply = ReplyCmdFactory.exeSysReply(channel, reply);
                if (reply!=null)
                {
                    sender(channel,  reply);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("RPC服务接收到系统指令执行异常", e);
            }
        }
        return reply;
    }

    private SendCmd sender(Channel channel, SendCmd command) throws Exception {
        if (command==null|| StringUtil.isNull(command.getId()))
        {
            return null;
        }
        ArrayBlockingQueue<SendCmd> queue = new ArrayBlockingQueue<>(1);
        RESULT_HASH_MAP.put(command.getId(),queue);
        INetCommand.sendEncodePacket(channel,command);
        return queue.poll(RpcConfig.getInstance().getTimeout(),TimeUnit.SECONDS);
    }

    public void shutdown() {
        workersGroup.shutdownGracefully();
    }

}