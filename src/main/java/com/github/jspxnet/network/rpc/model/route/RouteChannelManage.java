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
    final public static String KEY_GROUP_NAME = "name";
    final private static RouteChannelManage INSTANCE = new RouteChannelManage();

    private RouteChannelManage()
    {
        initConfigRoute();
    }

    public void initConfigRoute()
    {
        //初始化默认的路由表,就是自己的IP地址
        List<SocketAddress> list = RpcConfig.getInstance().getLocalAddressList();
        for (SocketAddress socketAddress:list)
        {
            RouteSession routeSession = new RouteSession();
            routeSession.setSocketAddress(socketAddress);
            routeSession.setOnline(YesNoEnumType.YES.getValue());
            routeSession.setHeartbeatTimes(0);
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
     * 等待检测可加入路由表的地址
     */
    private final List<SocketAddress> checkRouteSocketList = Collections.synchronizedList(new ArrayList<>());

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
        json.put(KEY_GROUP_NAME,RpcConfig.getInstance().getGroupName());
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
        for (RouteSession routeSession:list)
        {
            if (!routeSocketMap.containsKey(routeSession.getSocketAddress()))
            {
                routeSocketMap.put(routeSession.getSocketAddress(),routeSession);
            }
        }
        /*
        Enumeration<String> keys = routeSocketMap.keys();
        while (keys.hasMoreElements())
        {
            String key=keys.nextElement();
            RouteSession session = routeSocketMap.get(key);
            if (YesNoEnumType.NO.getValue()==session.getOnline())
            {
                routeSocketMap.remove(key);
            }
        }
        */
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
     * 添加路由表
     * @param address 地址
     */
    public void addCheckRouteSocket(SocketAddress address)
    {
        //路由表里边已经有了
        if (routeSocketMap.containsKey(address))
        {
            return;
        }
        if (!checkRouteSocketList.contains(address))
        {
            checkRouteSocketList.add(address);
        }
    }

    /**
     *
     * @return 地址列表
     */
    public List<SocketAddress> getCheckRouteSocketList()
    {
        return checkRouteSocketList;
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
            if (routeSession.getHeartbeatTimes()>2)
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
            if (YesNoEnumType.NO.getValue()==routeSession.getOnline()&&routeSession.getHeartbeatTimes()>2)
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
