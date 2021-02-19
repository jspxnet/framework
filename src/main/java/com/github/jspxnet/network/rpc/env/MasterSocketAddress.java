package com.github.jspxnet.network.rpc.env;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.network.rpc.model.route.RouteChannelManage;
import com.github.jspxnet.network.rpc.model.route.RouteSession;
import com.github.jspxnet.utils.IpUtil;
import com.github.jspxnet.utils.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/21 14:08
 * description: 得到有效的masterIp地址
 **/
@Slf4j
public class MasterSocketAddress {
    private static final Map<String,List<InetSocketAddress>> GROUP_LIST_MAP = new HashMap<>();
    private static int current = 0;
    final private static MasterSocketAddress INSTANCE = new  MasterSocketAddress();
    public static MasterSocketAddress getInstance()
    {
        return INSTANCE;
    }


    private MasterSocketAddress()
    {
        String[] groupNames = RpcConfig.getInstance().getGroupNames();
        if (ObjectUtil.isEmpty(groupNames))
        {
            return;
        }
        for (String name:groupNames)
        {
            List<InetSocketAddress> defaultSocketAddressList = Collections.synchronizedList(new ArrayList<>());
            defaultSocketAddressList.addAll(RpcConfig.getInstance().getMasterGroupList(name));
            GROUP_LIST_MAP.put(name,defaultSocketAddressList);
        }
    }

    synchronized public InetSocketAddress getSocketAddress(String serviceName)
    {
        if (ObjectUtil.isEmpty(GROUP_LIST_MAP))
        {
            log.error("RPC服务调用,没有配置服务器地址列表");
            return null;
        }
        List<InetSocketAddress> defaultSocketAddressList = GROUP_LIST_MAP.get(serviceName);
        if (ObjectUtil.isEmpty(defaultSocketAddressList))
        {
            defaultSocketAddressList = GROUP_LIST_MAP.get(Environment.defaultValue);
        }
        if (ObjectUtil.isEmpty(defaultSocketAddressList))
        {
            log.error("RPC服务调用,没有配置服务器地址列表");
            return null;
        }
        //这里要判断地址的有效性
        if (current>=defaultSocketAddressList.size())
        {
            current = 0;
        }
        try {
            return defaultSocketAddressList.get(current);
        } finally {
            current++;
        }
    }

    public List<InetSocketAddress> getDefaultSocketAddressList(String serviceName) {
        List<InetSocketAddress> defaultSocketAddressList = GROUP_LIST_MAP.get(serviceName);
        List<InetSocketAddress> result = new ArrayList<>();
        for (SocketAddress socketAddress:defaultSocketAddressList)
        {
            String ipStr = IpUtil.getIp(socketAddress);
            result.add(IpUtil.getSocketAddress(ipStr));
        }
        return result;
    }

    public List<String> getDefaultSocketAddressGroupNames() {
        return new ArrayList<>(GROUP_LIST_MAP.keySet());
    }


    public boolean removeGroupSocketAddress(String groupName,InetSocketAddress socketAddress ) {

        List<InetSocketAddress> list = GROUP_LIST_MAP.get(groupName);
        if (list!=null)
        {
            return list.remove(socketAddress);
        }
        return false;
    }

    /**
     * 将路由表放入请求缓存中
     */
    synchronized public void flushAddress()
    {
        RouteChannelManage routeChannelManage = RouteChannelManage.getInstance();
        if (routeChannelManage==null||routeChannelManage.getRouteSessionCount()<1)
        {
            return;
        }
        List<InetSocketAddress> saveList = new ArrayList<>();
        for (String groupName:GROUP_LIST_MAP.keySet())
        {
            if (groupName==null)
            {
                continue;
            }
            List<RouteSession> list = routeChannelManage.getRouteSessionList();
            for (RouteSession session:list)
            {
                if (YesNoEnumType.NO.getValue()==session.getOnline())
                {
                    continue;
                }
                if (groupName.equals(session.getGroupName()))
                {
                    saveList.add(session.getSocketAddress());
                }
            }
            if (!ObjectUtil.isEmpty(list))
            {
                List<InetSocketAddress> defaultSocketAddressList = GROUP_LIST_MAP.computeIfAbsent(groupName, k -> new ArrayList<>());
                defaultSocketAddressList.clear();
                defaultSocketAddressList.addAll(saveList);
            }
        }
    }



}
