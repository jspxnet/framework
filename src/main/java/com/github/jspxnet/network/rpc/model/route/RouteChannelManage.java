package com.github.jspxnet.network.rpc.model.route;

import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.rpc.env.RpcConfig;
import com.github.jspxnet.utils.IpUtil;
import com.github.jspxnet.utils.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
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
    //本服务启动的ip地址,配置的不一定都启动,应为要分组,这里的ip不会被路由表不删除
    private static final List<InetSocketAddress> START_LIST = new ArrayList<>();
    private RouteChannelManage()
    {
        initConfigRoute();
    }

    public void initConfigRoute()
    {
        RpcConfig rpcConfig = RpcConfig.getInstance();
        List<RouteSession>  list = rpcConfig.getConfigRouteSessionList();
        int i = 0;
        for (RouteSession routeSession:list)
        {
          routeSocketMap.put(routeSession.getSocketAddress(),routeSession);
        }
        log.debug("初始化路由表:{}",ObjectUtil.toString(list));
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
    private final ConcurrentHashMap<InetSocketAddress, RouteSession> routeSocketMap = new ConcurrentHashMap<>();


    /**
     * 检查表,只有成功了的才加入路由表里边
     */
    private final Map<InetSocketAddress, RouteSession> checkRouteSocketMap = new ConcurrentHashMap<>();


    public static List<InetSocketAddress> getStartList() {
        return START_LIST;
    }

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
    public void joinCheckRoute(List<RouteSession> list)
    {
        if (ObjectUtil.isEmpty(list))
        {
            return;
        }

        for (RouteSession routeSession:list)
        {
            if (routeSession==null)
            {
                continue;
            }
            joinCheckRoute(routeSession);
        }
    }

    public void joinCheckRoute(RouteSession routeSession)
    {
        if (ObjectUtil.isEmpty(routeSession))
        {
            return;
        }


        if (routeSession==null)
        {
            return;
        }
        if (START_LIST.contains(routeSession.getSocketAddress()))
        {
            return;
        }
        if (!checkRouteSocketMap.containsKey(routeSession.getSocketAddress()))
        {
            routeSession.setHeartbeatTimes(0);
            checkRouteSocketMap.put(routeSession.getSocketAddress(),routeSession);
        }
    }
    /**
     * 真正的加入到路由表中
     * @param list 路由表
     */
    public void joinRoute(List<RouteSession> list)
    {
        if (ObjectUtil.isEmpty(list))
        {
            return;
        }
        for (RouteSession routeSession:list)
        {
            if (routeSession==null)
            {
                continue;
            }
            if (!routeSocketMap.containsKey(routeSession.getSocketAddress()))
            {
                routeSession.setOnline(YesNoEnumType.YES.getValue());
                routeSession.setHeartbeatTimes(0);
                routeSocketMap.put(routeSession.getSocketAddress(),routeSession);
            }
        }
    }

    /**
     *
     * @return 拷贝一份出去
     */
    public List<RouteSession> getRouteSessionList()
    {
        return new ArrayList<>(routeSocketMap.values());
    }

    /**
     *
     * @return 拷贝一份出去,带检查的路由表
     */
    public Map<InetSocketAddress, RouteSession> getCheckRouteSocketMap()
    {
        return checkRouteSocketMap;
    }

    /**
     *
     * @param configList 当前路由表
     * @return 当前路由表排除当前服务
     */
    public List<RouteSession> getNotRouteSessionList(List<RouteSession> configList)
    {
        if (ObjectUtil.isEmpty(configList))
        {
            return null;
        }

        List<RouteSession> result = new ArrayList<>();
        for (RouteSession routeSession:configList)
        {
            if (START_LIST.contains(routeSession.getSocketAddress()))
            {
                continue;
            }
            result.add(routeSession);
        }
        return result;
    }


    /**
     *
     * @param configList 带检查的路由列表
     * @return 需要检查的
     */
    public List<RouteSession> getNotCheckRouteSessionList(List<RouteSession> configList)
    {
        if (ObjectUtil.isEmpty(configList))
        {
            return null;
        }

        List<RouteSession> result = new ArrayList<>();
        for (RouteSession routeSession:configList)
        {
            if (START_LIST.contains(routeSession.getSocketAddress()))
            {
                continue;
            }
            if (routeSocketMap.containsKey(routeSession.getSocketAddress()))
            {
                continue;
            }
            result.add(routeSession);
        }
        return result;
    }


    /**
     *
     * @return 当前实际可用的节点数量
     */
    public int getRouteSessionCount()
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
    public void routeOff(InetSocketAddress address)
    {
        if (RouteChannelManage.getStartList().contains(address))
        {
            return;
        }
        RouteSession routeSession = routeSocketMap.get(address);
        if (routeSession!=null)
        {
            routeSession.setHeartbeatTimes(routeSession.getHeartbeatTimes()+1);
            routeSession.setOnline(YesNoEnumType.NO.getValue());
        }
    }

    /**
     * 清理下线的地址
     */
    public void cleanOffRoute()
    {
        List<InetSocketAddress>  localList = RouteChannelManage.getStartList();
        Enumeration<InetSocketAddress> keys = routeSocketMap.keys();
        while (keys.hasMoreElements())
        {
            SocketAddress key=keys.nextElement();
            if (localList.contains(key))
            {
                //本地的不删除
                continue;
            }
            RouteSession routeSession = routeSocketMap.get(key);
            if (YesNoEnumType.NO.getValue()==routeSession.getOnline()&&routeSession.getHeartbeatTimes()>2)
            {
                RpcConfig rpcConfig = RpcConfig.getInstance();
                if (rpcConfig.isDebug())
                {
                    log.debug("路由表清除下线服务器:{}",IpUtil.getIp(routeSession.getSocketAddress()));
                }
                routeSocketMap.remove(key);
            }
        }
    }
}
