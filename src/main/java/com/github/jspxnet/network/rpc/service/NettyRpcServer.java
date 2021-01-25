package com.github.jspxnet.network.rpc.service;

import com.github.jspxnet.boot.DaemonThreadFactory;
import com.github.jspxnet.network.rpc.env.RpcConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import java.net.SocketAddress;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/9 22:20
 * description: jspbox
 **/
@Slf4j
public class NettyRpcServer implements Runnable {


    private NioEventLoopGroup bossGroup = null;
    private NioEventLoopGroup workerGroup = null; //CPU 内核数
    private final SocketAddress socketAddress;
    private boolean isRun = false;
    public NettyRpcServer(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }


    @Override
    public void run()
    {
        RpcConfig rpcConfig = RpcConfig.getInstance();
        bossGroup = new NioEventLoopGroup(rpcConfig.getWorkThread(),new DaemonThreadFactory("NettyRpcServerBoss"));
        //CPU 内核数
        workerGroup = new NioEventLoopGroup(rpcConfig.getWorkThread(),new DaemonThreadFactory("NettyRpcServerWorker"));
        try
        {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(socketAddress)
                    .childHandler(new ServerChannelInitializer())
                    .option(ChannelOption.SO_BACKLOG, rpcConfig.getBacklog())//TCP的缓冲区设置
                    .option(ChannelOption.SO_RCVBUF, rpcConfig.getBufferSize())//设置接收缓冲区大小

                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            //UnpooledByteBufAllocator.DEFAULT 非池方式
            //PooledByteBufAllocator.DEFAULT  池方式


            ChannelFuture channelFuture = bootstrap.bind().sync();
            isRun = true;

            log.debug("-- started and listen on port:" + channelFuture.channel().localAddress());
            channelFuture.channel().closeFuture().addListener(ChannelFutureListener.CLOSE).sync();
        }
        catch (Exception e)
        {
            log.error("服务器启动失败",e);
            isRun = false;
        }finally {
            close();
        }
    }

    public boolean isRun() {
        return isRun;
    }

    public void close()
    {
        log.info("RPC 执行了关闭调用");
        if (workerGroup!=null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup!=null) {
            bossGroup.shutdownGracefully();
        }
        isRun = false;
    }


}
