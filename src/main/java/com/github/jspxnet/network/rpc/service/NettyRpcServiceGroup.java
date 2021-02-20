package com.github.jspxnet.network.rpc.service;

import com.ecwid.consul.v1.agent.model.NewService;
import com.github.jspxnet.boot.DaemonThreadFactory;
import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.network.consul.ConsulService;
import com.github.jspxnet.network.rpc.env.RpcConfig;
import com.github.jspxnet.network.rpc.model.route.RouteChannelManage;
import com.github.jspxnet.network.rpc.model.route.RouteSession;
import com.github.jspxnet.network.rpc.service.route.RouteService;
import com.github.jspxnet.utils.IpUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
import java.util.Arrays;
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
    private static final  Map<InetSocketAddress,NettyRpcServer> SERVER_LIST = new HashMap<>();
    private static final RouteService routeService = new RouteService();
    private static final RpcConfig RPC_CONFIG = RpcConfig.getInstance();
    /**
     * 指定启动一个服务器
     *
     * @param routeSession 服务器地址
     */
    private NettyRpcServer createService(RouteSession routeSession)  {
        NettyRpcServer nettyRpcServer = SERVER_LIST.get(routeSession.getSocketAddress());
        if (nettyRpcServer==null)
        {
            nettyRpcServer = new NettyRpcServer(routeSession);
            SERVER_LIST.put(routeSession.getSocketAddress(),nettyRpcServer);
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

        int groupCount = RPC_CONFIG.getGroupCount();
        if (groupCount<=0)
        {
            log.info("------->netty rpc 调用配置参数,分组个数 rpc.localGroupCount={}配置错误,不能等于小于0",groupCount);
            return;
        }
        List<RouteSession> routeSessionList = RPC_CONFIG.getConfigRouteSessionList();
        DaemonThreadFactory threadFactory =  new DaemonThreadFactory(RPC_THREAD_NAME);
        for (RouteSession routeSession:routeSessionList)
        {
            if (groupCount<=0)
            {
                break;
            }
            if (IpUtil.isPortUsing(routeSession.getSocketAddress()))
            {
                log.info("------->netty rpc 调用地址端口已经被占用,将跳过不启动:{},同一台服务器是用同一个配置属于正常,否则检查配置是否有误",routeSession.getSocketAddress());
                continue;
            }
            NettyRpcServer nettyRpcServer = createService(routeSession);
            threadFactory.newThread(nettyRpcServer).start();

            if (Environment.consul.equalsIgnoreCase(RPC_CONFIG.getServiceDiscoverMode()))
            {
                //注册
                ConsulService consulService = EnvFactory.getBeanFactory().getBean(ConsulService.class);
                if (consulService==null)
                {

                    log.info("注册发现服务是用consul,但是没有找到ioc中配置的consulService");
                    continue;
                }

                NewService discoveryService = new NewService();
                discoveryService.setId(nettyRpcServer.getId());
                discoveryService.setName(nettyRpcServer.getName());
                discoveryService.setAddress(IpUtil.getOnlyIp(routeSession.getSocketAddress()));
                discoveryService.setPort(routeSession.getSocketAddress().getPort());
                discoveryService.setTags(Arrays.asList(StringUtil.split("jspx rpc"," ")));

                NewService.Check check = new NewService.Check();
                check.setTcp(discoveryService.getAddress()+":" + discoveryService.getPort());
                check.setInterval(RPC_CONFIG.getTimeout()*2+"s");
                check.setTimeout(RPC_CONFIG.getTimeout()+"s");
                discoveryService.setCheck(check);
                consulService.register(discoveryService);
            } else
            {
                RouteChannelManage.getStartList().add(routeSession.getSocketAddress());
            }
            groupCount--;
        }

        if (!Environment.consul.equalsIgnoreCase(RPC_CONFIG.getServiceDiscoverMode()))
        {
            DaemonThreadFactory routeThreadFactory = new DaemonThreadFactory(RPC_ROUTE_THREAD_NAME);
            routeThreadFactory.newThread(routeService).start();
        }
        started = true;
    }

    public void stop() {

        if (!started)
        {
            return;
        }

        started = false;
        String serviceDiscoverMode = RPC_CONFIG.getServiceDiscoverMode();
        if (!Environment.consul.equalsIgnoreCase(serviceDiscoverMode))
        {
            routeService.shutdown();
        }
        ConsulService consulService = EnvFactory.getBeanFactory().getBean(ConsulService.class);
        if (consulService!=null)
        {
            for (NettyRpcServer nettyRpcServer:SERVER_LIST.values())
            {
                if (nettyRpcServer==null)
                {
                    continue;
                }
                if (Environment.consul.equalsIgnoreCase(serviceDiscoverMode))
                {
                    log.info("删除consul注册服务{}",nettyRpcServer.getId());

                    consulService.deregister(nettyRpcServer.getId());
                }
                nettyRpcServer.close();
            }
        }

        SERVER_LIST.clear();
    }

}
