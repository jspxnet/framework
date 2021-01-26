package com.github.jspxnet.network.rpc.service;

import com.github.jspxnet.boot.DaemonThreadFactory;
import com.github.jspxnet.network.rpc.env.RpcConfig;
import com.github.jspxnet.network.rpc.service.route.RouteService;
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

    private static final String RPC_THREAD_NAME = "netty_rpc";
    private static final String RPC_ROUTE_THREAD_NAME = "netty_rpc_route";
    private static final NettyRpcServiceGroup INSTANCE = new NettyRpcServiceGroup();
    public static NettyRpcServiceGroup getInstance(){
        return INSTANCE;
    }
    private static final  Map<SocketAddress,NettyRpcServer> SERVER_LIST = new HashMap<>();
    private static final RouteService routeService = new RouteService();
    /**
     * 指定启动一个服务器
     * @param socketAddress 服务器地址
     */
    private NettyRpcServer createService(SocketAddress socketAddress)  {
        NettyRpcServer nettyRpcServer = SERVER_LIST.get(socketAddress);
        if (nettyRpcServer==null)
        {
            nettyRpcServer = new NettyRpcServer(socketAddress);
        }
        return nettyRpcServer;
    }

    /**
     * 启动所有配置的服务器
     */
    public void start()
    {
        RpcConfig rpcConfig = RpcConfig.getInstance();
        List<SocketAddress> addressList = rpcConfig.getLocalAddressList();
        DaemonThreadFactory threadFactory =  new DaemonThreadFactory(RPC_THREAD_NAME);
        for (SocketAddress socketAddress:addressList)
        {
            NettyRpcServer nettyRpcServer = createService(socketAddress);
            threadFactory.newThread(nettyRpcServer).start();
        }
        DaemonThreadFactory routeThreadFactory =  new DaemonThreadFactory(RPC_ROUTE_THREAD_NAME);
        routeThreadFactory.newThread(routeService).start();
    }

    public void stop() {
        routeService.shutdown();
        for (NettyRpcServer nettyRpcServer:SERVER_LIST.values())
        {
            if (nettyRpcServer!=null)
            {
                nettyRpcServer.close();
            }
        }

    }

}
