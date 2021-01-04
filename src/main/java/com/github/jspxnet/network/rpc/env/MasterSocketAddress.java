package com.github.jspxnet.network.rpc.env;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.cache.redis.RedissonClientConfig;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.network.rpc.model.route.RouteChannelManage;
import com.github.jspxnet.network.rpc.model.route.RouteSession;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.utils.IpUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/21 14:08
 * description: 得到有效的masterIp地址
 **/
@Slf4j
public class MasterSocketAddress {
    //保存一分到redis
    private static final String ADDRESS_LIST_KEY = "rpc:master:group:list";
    private static final Map<String,List<SocketAddress>> groupListMap = new HashMap<>();
    private static int current = 0;
    private static MasterSocketAddress INSTANCE = new  MasterSocketAddress();
    public static MasterSocketAddress getInstance()
    {
        return INSTANCE;
    }

    //@Ref(bind = RedissonClientConfig.class)
    static RedissonClient redissonClient;

    private MasterSocketAddress()
    {
        String[] groupNames = RpcConfig.getInstance().getGroupNames();
        if (ObjectUtil.isEmpty(groupNames))
        {
            return;
        }
        for (String name:groupNames)
        {
            List<SocketAddress> defaultSocketAddressList = Collections.synchronizedList(new ArrayList<>());
            defaultSocketAddressList.addAll(RpcConfig.getInstance().getMasterGroupList(name));
            groupListMap.put(name,defaultSocketAddressList);
        }
        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        redissonClient = (RedissonClient)beanFactory.getBean(RedissonClientConfig.class);
    }

    synchronized public SocketAddress getSocketAddress(String serviceName)
    {
        if (ObjectUtil.isEmpty(groupListMap))
        {
            log.error("RPC服务调用,没有配置服务器地址列表");
            return null;
        }
        List<SocketAddress> defaultSocketAddressList = groupListMap.get(serviceName);
        if (ObjectUtil.isEmpty(defaultSocketAddressList))
        {
            defaultSocketAddressList = groupListMap.get("default");
        }
        if (ObjectUtil.isEmpty(defaultSocketAddressList))
        {
            log.error("RPC服务调用,没有配置服务器地址列表");
            return null;
        }
        System.out.println(defaultSocketAddressList.size() + "----------------current=" + current);
        //这里要判断地址的有效性
        if (current>=defaultSocketAddressList.size())
        {
            current = 0;
            if (redissonClient!=null)
            {
                RBucket<String> bucket = redissonClient.getBucket(ADDRESS_LIST_KEY);
                if (bucket!=null)
                {
                    String ips =  bucket.get();
                    if (!StringUtil.isEmpty(ips))
                    {
                        defaultSocketAddressList.clear();
                        defaultSocketAddressList.addAll(IpUtil.getSocketAddressList(ips));
                    }
                }
            }
        }
        try {
            return defaultSocketAddressList.get(current);
        } finally {
            current++;
        }
    }

    public List<SocketAddress> getDefaultSocketAddressList(String serviceName) {
        List<SocketAddress> defaultSocketAddressList = groupListMap.get(serviceName);
        List<SocketAddress> result = new ArrayList<>();
        for (SocketAddress socketAddress:defaultSocketAddressList)
        {
            String ipStr = IpUtil.getIp(socketAddress);
            result.add(IpUtil.getSocketAddress(ipStr));
        }
        return result;
    }

    public List<String> getDefaultSocketAddressGroupNames() {
        return new ArrayList<>(groupListMap.keySet());
    }
    /**
     * 将路由表放入请求缓存中
     */
    synchronized public void flushAddress(List<String> serviceNames)
    {
        RouteChannelManage routeChannelManage = RouteChannelManage.getInstance();
        if (ObjectUtil.isEmpty(serviceNames)|| routeChannelManage==null||routeChannelManage.getRouteSessionCount()<1)
        {
            return;
        }

        for (String groupName:serviceNames)
        {
            StringBuilder ips = new StringBuilder();
            List<RouteSession>  list = routeChannelManage.getRouteSessionList();
            if (redissonClient==null)
            {
                List<SocketAddress> defaultSocketAddressList = groupListMap.get(groupName);
                defaultSocketAddressList.clear();
            }
            for (RouteSession session:list)
            {
                if (YesNoEnumType.YES.getValue()==session.getOnline())
                {
                    if (redissonClient==null)
                    {
                        List<SocketAddress> defaultSocketAddressList = groupListMap.get(groupName);
                        if (defaultSocketAddressList==null)
                        {
                            defaultSocketAddressList = new ArrayList<>();
                        }
                        defaultSocketAddressList.add(session.getSocketAddress());
                    }
                    ips.append(IpUtil.getIp(session.getSocketAddress())).append(StringUtil.SEMICOLON);
                }
            }
            if (ips.toString().endsWith(StringUtil.SEMICOLON))
            {
                ips.setLength(ips.length()-1);
            }
            if (redissonClient!=null&&!redissonClient.getBucket(ADDRESS_LIST_KEY).isExists())
            {
                redissonClient.getBucket(ADDRESS_LIST_KEY).set(ips.toString(),RpcConfig.getInstance().getTimeout()*3, TimeUnit.SECONDS);
            }
        }

    }


}
