package com.github.jspxnet.network.rpc.service;

import com.github.jspxnet.network.rpc.env.RpcConfig;
import com.github.jspxnet.network.rpc.service.route.RouteService;
import com.github.jspxnet.sioc.SchedulerManager;
import com.github.jspxnet.sioc.scheduler.SchedulerTaskManager;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/21 13:26
 * description: jspbox
 **/
public class NettyRpcServiceGroup {

    private static final NettyRpcServiceGroup INSTANCE = new NettyRpcServiceGroup();
    public static NettyRpcServiceGroup getInstance(){
        return INSTANCE;
    }
    private static final  Map<SocketAddress,NettyRpcServer> SERVER_LIST = new HashMap<>();

    /**
     * 指定启动一个服务器
     * @param socketAddress 服务器地址
     */
    public void start(SocketAddress socketAddress)  {
        NettyRpcServer nettyRpcServer = SERVER_LIST.get(socketAddress);
        if (nettyRpcServer==null)
        {
            nettyRpcServer = new NettyRpcServer(socketAddress);
            nettyRpcServer.setDaemon(true);
            nettyRpcServer.start();
        } else
        if (!nettyRpcServer.isRun())
        {
            nettyRpcServer.start();
        }
    }

    /**
     * 启动所有配置的服务器
     */
    public void start()
    {
        RpcConfig rpcConfig = RpcConfig.getInstance();
        List<SocketAddress> addressList = rpcConfig.getLocalAddressList();
        for (SocketAddress socketAddress:addressList)
        {
            start(socketAddress);
        }

        SchedulerManager schedulerManager = SchedulerTaskManager.getInstance();
        schedulerManager.add(new RouteService());
    }

    public  void stop() {
        for (NettyRpcServer nettyRpcServer:SERVER_LIST.values())
        {
            if (nettyRpcServer!=null)
            {
                nettyRpcServer.interrupt();
            }
        }
    }

}
