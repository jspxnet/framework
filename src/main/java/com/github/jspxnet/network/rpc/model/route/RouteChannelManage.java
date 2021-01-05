package com.github.jspxnet.network.rpc.model.route;

import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.rpc.env.RpcConfig;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.IpUtil;
import com.github.jspxnet.utils.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/22 23:12
 * description: master 路由表
 **/
@Slf4j
public class RouteChannelManage {
    final public static String KEY_ROUTE = "route";
    final private static RouteChannelManage INSTANCE = new RouteChannelManage();

    private RouteChannelManage()
    {
        initConfigRoute();
    }

    public void initConfigRoute()
    {
        RpcConfig rpcConfig = RpcConfig.getInstance();
        //初始化默认的路由表,就是自己的IP地址
        String[] groupNames = rpcConfig.getLocalGroupList();
        List<SocketAddress> list = rpcConfig.getLocalAddressList();
        int i = 0;
        for (SocketAddress socketAddress:list)
        {
            RouteSession routeSession = new RouteSession();
            routeSession.setSocketAddress(socketAddress);
            routeSession.setOnline(YesNoEnumType.YES.getValue());
            routeSession.setHeartbeatTimes(0);
            if (groupNames.length>=list.size())
            {
                routeSession.setGroupName(groupNames[i]);
            } else
            {
                routeSession.setGroupName(groupNames[0]);
            }
            routeSocketMap.put(socketAddress,routeSession);
        }
    }
    /**
     *
     * @return 单例
     */
    public static RouteChannelManage getInstance(){
        return INSTANCE;
    }

    /**
     * 路由表
     */
    private ConcurrentHashMap<SocketAddress, RouteSession> routeSocketMap = new ConcurrentHashMap<>();


    /**
     *
     * @return 得到要发送的路由表, json
     */
    public String getSendRouteTable()
    {
        List<RouteSession> list = new ArrayList<>();
        JSONObject json = new JSONObject();
        for (RouteSession routeSession:routeSocketMap.values())
        {
            if (YesNoEnumType.YES.getValue()==routeSession.getOnline())
            {
                list.add(routeSession);
            }
        }
        json.put(KEY_ROUTE,list);
        return json.toString(4);
    }


    /**
     * 放入请求得到的路由表
     * @param list 路由表
     */
    public void join(List<RouteSession> list)
    {
        if (ObjectUtil.isEmpty(list))
        {
            return;
        }

        log.info("json RouteSession :{}",ObjectUtil.toString(list));
        for (RouteSession routeSession:list)
        {
            if (!routeSocketMap.containsKey(routeSession.getSocketAddress()))
            {
                routeSocketMap.put(routeSession.getSocketAddress(),routeSession);
            } else
            {
                RouteSession routeSessionTmp = routeSocketMap.get(routeSession.getSocketAddress());
                if (routeSessionTmp!=null&&YesNoEnumType.NO.getValue()==routeSessionTmp.getOnline())
                {
                    routeSessionTmp.setOnline(YesNoEnumType.YES.getValue());
                }
            }
        }

    }


    /**
     *
     * @return 拷贝一份出去
     */
    public List<RouteSession> getRouteSessionList()
    {
        return BeanUtil.copyList(routeSocketMap.values(),RouteSession.class);
    }


    /**
     *
     * @return 当前实际可用的节点数量
     */
    synchronized public int getRouteSessionCount()
    {
        int result = 0;
        for (RouteSession session:routeSocketMap.values())
        {
            if (session!=null&&YesNoEnumType.YES.getValue()==session.getOnline())
            {
                result++;
            }
        }
        return result;
    }

    /**
     *
     * @param address 标记下线
     */
    public void routeOff(SocketAddress address)
    {
        RouteSession routeSession = routeSocketMap.get(address);
        if (routeSession!=null)
        {
            routeSession.setHeartbeatTimes(routeSession.getHeartbeatTimes()+1);
            if (routeSession.getHeartbeatTimes()>3)
            {
                routeSession.setOnline(YesNoEnumType.NO.getValue());
            }
        }
    }

    /**
     * 清理下线的地址
     */
    public void cleanOffRoute()
    {
        Enumeration<SocketAddress> keys = routeSocketMap.keys();
        while (keys.hasMoreElements())
        {
            SocketAddress key=keys.nextElement();
            RouteSession routeSession = routeSocketMap.get(key);
            if (YesNoEnumType.NO.getValue()==routeSession.getOnline()&&routeSession.getHeartbeatTimes()>3)
            {
                log.debug("路由表清除下线服务器:{}",IpUtil.getIp(routeSession.getSocketAddress()));
                routeSocketMap.remove(key);
            }
        }
        if (getRouteSessionCount()<1)
        {
            initConfigRoute();
        }
    }
}
