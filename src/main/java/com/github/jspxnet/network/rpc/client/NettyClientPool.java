package com.github.jspxnet.network.rpc.client;

import com.github.jspxnet.boot.DaemonThreadFactory;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.env.RpcConfig;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import java.net.SocketAddress;
import java.util.Map;
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
    private static volatile NettyClientPool instance = null;
    private static ChannelPoolMap<SocketAddress,FixedChannelPool> pools;
    private final Bootstrap bootstrap = new Bootstrap();
    private final NioEventLoopGroup workersGroup;
    private static final RpcConfig RPC_CONFIG = RpcConfig.getInstance();

    private NettyClientPool(){
        workersGroup = new NioEventLoopGroup(RPC_CONFIG.getWorkThread(),new DaemonThreadFactory("NettyRpcClientPool"));
        bootstrap.group(workersGroup)
                .channel(NioSocketChannel.class)
                .handler(new ClientChannelInitializer())
                .option(ChannelOption.SO_RCVBUF, RPC_CONFIG.getBufferSize())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, RPC_CONFIG.getTimeout()* DateUtil.SECOND);

        pools = new AbstractChannelPoolMap<SocketAddress, FixedChannelPool>() {
            @Override
            protected FixedChannelPool newPool(SocketAddress key) {
                //lastRecentUsed: true就是后进先出，false 就是先进先出
                return new FixedChannelPool(bootstrap.remoteAddress(key),new NettyChannelPoolHandler(), ChannelHealthChecker.ACTIVE, FixedChannelPool.AcquireTimeoutAction.NEW, RPC_CONFIG.getTimeout()* DateUtil.SECOND, 50, 2147483647, true, false);
            }
        };
    }

    public boolean isRun()
    {
        return pools != null;
    }
    /**
     *
     * @return 单例
     */
    public static NettyClientPool getInstance(){
        if(instance == null) {
            synchronized (NettyClientPool.class) {
                if(instance == null) {
                    instance = new NettyClientPool();
                }
            }
        }
        return instance;
    }

    /**
     *
     * @param address  发送地址
     * @param command 发送命令
     * @return 返回结果
     * @throws Exception 异常
     */
    public SendCmd send(SocketAddress address, SendCmd command) throws Exception {
        if (address==null)
        {
            return null;
        }
        final FixedChannelPool pool = pools.get(address);
        Channel channel = null;
        try {
            channel = pool.acquire().get();
            return send(channel, command);
        } finally {
            if (channel!=null)
            {
                pool.release(channel);
            }

        }
    }

    private SendCmd send(Channel channel, SendCmd command) throws Exception {
        if (command==null || StringUtil.isNull(command.getId()))
        {
            return null;
        }
        ArrayBlockingQueue<SendCmd> queue = new ArrayBlockingQueue<>(1);
        ResultHashMap resultHashMap = ResultHashMap.getInstance();
        resultHashMap.put(command.getId(),queue);
        INetCommand.sendEncodePacket(channel,command);
        return queue.poll(RPC_CONFIG.getTimeout(), TimeUnit.SECONDS);
    }

    public void shutdown() {
        if (pools==null)
        {
            return;
        }
        AbstractChannelPoolMap<SocketAddress,FixedChannelPool> poolMap = (AbstractChannelPoolMap<SocketAddress,FixedChannelPool>) pools;
        for (Map.Entry<SocketAddress, FixedChannelPool> inetSocketAddressFixedChannelPoolEntry : poolMap) {
            if (inetSocketAddressFixedChannelPoolEntry==null)
            {
                continue;
            }
            FixedChannelPool pool = inetSocketAddressFixedChannelPoolEntry.getValue();
            if (pool==null)
            {
                continue;
            }
            pool.close();
        }
        poolMap.close();
        workersGroup.shutdownGracefully();
    }
}
