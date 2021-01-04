package com.github.jspxnet.network.rpc.client;

import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.env.RpcConfig;
import com.github.jspxnet.utils.DateUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import java.net.SocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/16 0:26
 * description: jspbox
 **/
@Slf4j
public class NettyClientPool {

    /**
     * volatile保持线程之间的可见性，连接池的创建是单例，在这里可加可不加
     */
    volatile private static NettyClientPool nettyClientPool;
    volatile private static ChannelPoolMap<SocketAddress,FixedChannelPool> pools = null;
    private final Bootstrap bootstrap = new Bootstrap();

    private RpcConfig rpcConfig = RpcConfig.getInstance();

    private NettyClientPool(){

        NioEventLoopGroup workersGroup = new NioEventLoopGroup(rpcConfig.getWorkThread());
        bootstrap.group(workersGroup)
                .channel(NioSocketChannel.class)
                .handler(new ClientChannelInitializer())
                .option(ChannelOption.SO_RCVBUF, rpcConfig.getBufferSize())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, rpcConfig.getTimeout()* DateUtil.SECOND);


        pools = new AbstractChannelPoolMap<SocketAddress, FixedChannelPool>() {
            @Override
            protected FixedChannelPool newPool(SocketAddress key) {
                return new FixedChannelPool(bootstrap.remoteAddress(key),new NettyChannelPoolHandler(), ChannelHealthChecker.ACTIVE, FixedChannelPool.AcquireTimeoutAction.NEW, RpcConfig.getInstance().getTimeout()* DateUtil.SECOND, 100, 2147483647, true, false);
            }
        };
    }

    /**
     *
     * @return 单例
     */
    public static NettyClientPool getInstance(){
        if(nettyClientPool == null) {
            synchronized (NettyClientPool.class) {
                if(nettyClientPool == null) {
                    nettyClientPool = new NettyClientPool();
                }
            }
        }
        return nettyClientPool;
    }

    /**
     *
     * @param address  发送地址
     * @param command 发送命令
     * @return 返回结果
     * @throws Exception 异常
     */
    public SendCmd send(SocketAddress address, SendCmd command) throws Exception {

        final FixedChannelPool pool = pools.get(address);
        final Channel channel = pool.acquire().get();
        try {
            return send(channel, command);
        } finally {
            pool.release(channel);
        }
    }

    private SendCmd send(Channel channel, SendCmd command) throws Exception {
        ArrayBlockingQueue<SendCmd> queue = new ArrayBlockingQueue<>(1);
        ResultHashMap resultHashMap = ResultHashMap.getInstance();
        resultHashMap.put(command.getId(),queue);
        INetCommand.sendEncodePacket(channel,command);
        return queue.poll(rpcConfig.getTimeout(), TimeUnit.SECONDS);
    }

}
