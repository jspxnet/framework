package com.github.jspxnet.network.rpc.env;

import com.ecwid.consul.v1.health.model.HealthService;
import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.network.consul.ConsulService;
import com.github.jspxnet.network.rpc.model.route.impl.RouteChannelManage;
import com.github.jspxnet.network.rpc.model.route.RouteSession;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/2/19 20:01
 * description: 查询分布式调用适配器
 **/
@Slf4j
public  class DiscoveryServiceAddress {
    private DiscoveryServiceAddress()
    {

    }

    //分组读取计数
    private static final Map<String,Integer> GROUP_CURRENT = Collections.synchronizedMap(new HashMap<>());

    private static ConsulService consulService;
    private static String serviceDiscoverMode;

    /**
     * 查询路由表中可用的服务器
     * @param groupName 分组名称
     * @return 可用的分组地址
     */
    public static InetSocketAddress getSocketAddress(String groupName)
    {
        if (serviceDiscoverMode==null)
        {
            serviceDiscoverMode =EnvFactory.getEnvironmentTemplate().getString(Environment.serviceDiscoverMode);
            if (StringUtil.isNull(serviceDiscoverMode))
            {
                serviceDiscoverMode = Environment.defaultValue;
            }
        }

        if (Environment.consul.equalsIgnoreCase(serviceDiscoverMode))
        {
            if (consulService==null)
            {
                consulService = EnvFactory.getBeanFactory().getBean(ConsulService.class);
            }
            HealthService.Service service  = consulService.getRunServices(Environment.defaultValue);
            if (service==null)
            {
                service  = consulService.getRunServices(groupName);
            }
            if (service==null)
            {
                log.error("consul 当前没有可用的访问");
                return null;
            }
            return new InetSocketAddress(service.getAddress(),service.getPort());
        }

        //这里优先从路由表里边得到---begin
        List<InetSocketAddress> groupSocketAddressList =  new ArrayList<>();
        List<RouteSession> routeSessionList = RouteChannelManage.getInstance().getRouteSessionList();
        for (RouteSession routeSession:routeSessionList)
        {
            if (groupName.equalsIgnoreCase(routeSession.getGroupName())&&routeSession.getOnline()== YesNoEnumType.YES.getValue())
            {
                groupSocketAddressList.add(routeSession.getSocketAddress());
            }
        }

        String tempGroup = groupName;
        //如果没用到默认组里边找
        if (groupSocketAddressList.isEmpty() )
        {
            for (RouteSession routeSession:routeSessionList)
            {
                if (Environment.defaultValue.equalsIgnoreCase(routeSession.getGroupName())&&routeSession.getOnline()== YesNoEnumType.YES.getValue())
                {
                    tempGroup = Environment.defaultValue;
                    groupSocketAddressList.add(routeSession.getSocketAddress());
                }
            }
        }

        if (groupSocketAddressList.isEmpty())
        {
            log.error("网络不中不能找到适配的nettyRpc服务：{},{}",groupName,tempGroup);
        }

        //这里要判断地址的有效性
        synchronized (GROUP_CURRENT)
        {
            int current = GROUP_CURRENT.getOrDefault(tempGroup.toLowerCase(),0);
            if (current>=groupSocketAddressList.size())
            {
                current = 0;
            }
            try {
                return groupSocketAddressList.get(current);
            } finally {
                current++;
                GROUP_CURRENT.put(groupName,current);
            }
        }
        //这里优先从路由表里边得到---end

    }
}
