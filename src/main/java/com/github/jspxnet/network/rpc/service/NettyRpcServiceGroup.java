package com.github.jspxnet.network.rpc.service;

import com.github.jspxnet.boot.DaemonThreadFactory;
import com.github.jspxnet.network.rpc.env.RpcConfig;
import com.github.jspxnet.network.rpc.model.route.RouteChannelManage;
import com.github.jspxnet.network.rpc.service.route.RouteService;
import com.github.jspxnet.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
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
@Slf4j
public class NettyRpcServiceGroup {

    private static final String RPC_THREAD_NAME = "netty_rpc";
    private static final String RPC_ROUTE_THREAD_NAME = "netty_rpc_route";
    private static boolean started = false;
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
        if (started)
        {
            return;
        }
        RpcConfig rpcConfig = RpcConfig.getInstance();
        int groupCount = rpcConfig.getGroupCount();
        if (groupCount<=0)
        {
            log.info("------->netty rpc 调用配置参数,分组个数 rpc.localGroupCount={}配置错误,不能等于小于0",groupCount);
            return;
        }
        List<InetSocketAddress> addressList = rpcConfig.getLocalAddressList();
        DaemonThreadFactory threadFactory =  new DaemonThreadFactory(RPC_THREAD_NAME);
        for (InetSocketAddress socketAddress:addressList)
        {
            if (groupCount<=0)
            {
                break;
            }
            if (IpUtil.isPortUsing(socketAddress))
            {
                log.info("------->netty rpc 调用地址端口已经被占用,将跳过不启动:{},同一台服务器是用同一个配置属于正常,否则检查配置是否有误",socketAddress);
                continue;
            }
            NettyRpcServer nettyRpcServer = createService(socketAddress);
            threadFactory.newThread(nettyRpcServer).start();
            RouteChannelManage.getStartList().add(socketAddress);
            groupCount--;
        }
        DaemonThreadFactory routeThreadFactory = new DaemonThreadFactory(RPC_ROUTE_THREAD_NAME);
        routeThreadFactory.newThread(routeService).start();
        started = true;
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
