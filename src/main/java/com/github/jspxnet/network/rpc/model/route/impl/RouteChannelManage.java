package com.github.jspxnet.network.rpc.model.route.impl;

import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.rpc.env.RpcConfig;
import com.github.jspxnet.network.rpc.model.route.RouteManage;
import com.github.jspxnet.network.rpc.model.route.RouteSession;
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
public class RouteChannelManage implements RouteManage {
    final public static String KEY_ROUTE = "route";
    private static RouteManage INSTANCE;
    //本服务启动的ip地址,统一不判断，不检测是连接
    private static final List<InetSocketAddress> START_LIST = new ArrayList<>();

    //保存的是配置的关联ip，如果路由表里边没有就自动添加到检测列表里边等待检测
    private static final List<RouteSession> MASTER_LIST = new ArrayList<>();

    private RouteChannelManage()
    {
        RpcConfig rpcConfig = RpcConfig.getInstance();
        List<RouteSession>  list = rpcConfig.createConfigRouteSessionList();
        for (RouteSession routeSession:list)
        {
            //这里会去掉相同的配置
            routeSocketMap.put(routeSession.getSocketAddress(),routeSession);
        }
        log.debug("初始化本地路由表:{}",ObjectUtil.toString(list));

        if (START_LIST.isEmpty())
        {
            START_LIST.addAll(rpcConfig.getLocalAddressList());
        }

        if (MASTER_LIST.isEmpty())
        {
            MASTER_LIST.addAll(rpcConfig.createMasterRouteSessionList());
        }

    }
    /**
     *
     * @return 单例
     */
    public static RouteManage getInstance(){
        if (INSTANCE==null)
        {
            synchronized (RouteChannelManage.class)
            {
                INSTANCE = new RouteChannelManage();
            }
        }
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


    /**
     * 判断是否为本地服务
     * @param socketAddress ip地址
     * @return  判断是否为本地服务
     */

    @Override
    public boolean isLocalAddress(InetSocketAddress socketAddress) {
        return START_LIST.contains(socketAddress);
    }


    /**
     *
     * @return 得到要发送的路由表, json
     */
    @Override
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
    @Override
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

    @Override
    public void joinCheckRoute(RouteSession routeSession)
    {
        if (ObjectUtil.isEmpty(routeSession))
        {
            return;
        }

        if (START_LIST.contains(routeSession.getSocketAddress()))
        {
            return;
        }

        if (routeSocketMap.containsKey(routeSession.getSocketAddress()))
        {
            //路由表里边已经有了
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
    @Override
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
    @Override
    public List<RouteSession> getRouteSessionList()
    {
        return new ArrayList<>(routeSocketMap.values());
    }

    /**
     *
     * @return 拷贝一份出去,带检查的路由表
     */
  /*  public Map<InetSocketAddress, RouteSession> getCheckRouteSocketMap()
    {
        return checkRouteSocketMap;
    }*/

    /**
     * 情况检查的路由表
     */
    @Override
    public void clearCheckRouteSocketMap()
    {
        if (checkRouteSocketMap.isEmpty())
        {
            return;
        }
        checkRouteSocketMap.clear();
    }



    /**
     * 得到当前需要检查的路由表,先排除自己本地服务,如果本地配置的没有加入本地配置的ip去检查
     * @return 当前路由表排除当前服务
     */
    @Override
    public List<RouteSession> getNeedCheckRouteSessionList()
    {
        List<RouteSession> result = new ArrayList<>();
        for (RouteSession routeSession:checkRouteSocketMap.values())
        {
            if (START_LIST.contains(routeSession.getSocketAddress()))
            {
                continue;
            }
            if (!routeSocketMap.containsKey(routeSession.getSocketAddress()))
            {
                result.add(routeSession);
            }
        }
        for (RouteSession routeSession:MASTER_LIST)
        {
            if (!result.contains(routeSession))
            {
                result.add(routeSession);
            }
        }
        return result;
    }


    /**
     * checkRouteSocketMap 里边过滤掉本地的地址,和路由表里边已经有的地址
     * @return 待检查的路由列表, 需要检查的
     */
/*
    public List<RouteSession> getWaitCheckRouteSessionList()
    {
        if (ObjectUtil.isEmpty(checkRouteSocketMap))
        {
            return null;
        }

        List<RouteSession> result = new ArrayList<>();
        for (RouteSession routeSession:checkRouteSocketMap.values())
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
*/


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
    @Override
    public void routeOff(InetSocketAddress address)
    {
        if (START_LIST.contains(address))
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

    @Override
    public void routeOn(InetSocketAddress address)
    {
        if (START_LIST.contains(address))
        {
            return;
        }
        RouteSession routeSession = routeSocketMap.get(address);
        if (routeSession!=null)
        {
            routeSession.setHeartbeatTimes(0);
            routeSession.setOnline(YesNoEnumType.YES.getValue());
        }
    }

    /**
     * 清理下线的地址
     */
    @Override
    public void cleanOffRoute()
    {
        Enumeration<InetSocketAddress> keys = routeSocketMap.keys();
        while (keys.hasMoreElements())
        {
            SocketAddress key=keys.nextElement();
            if (START_LIST.contains(key))
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
