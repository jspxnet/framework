package com.github.jspxnet.network.rpc.env;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.cache.redis.RedissonClientConfig;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.network.rpc.model.route.RouteChannelManage;
import com.github.jspxnet.network.rpc.model.route.RouteSession;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.utils.IpUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private static final List<SocketAddress> defaultSocketAddressList = Collections.synchronizedList(new ArrayList<>());
    private static int current = 0;
    private static MasterSocketAddress instance = new  MasterSocketAddress();
    public static MasterSocketAddress getInstance()
    {
        return instance;
    }

    //@Ref(bind = RedissonClientConfig.class)
    static RedissonClient redissonClient;

    private MasterSocketAddress()
    {
        defaultSocketAddressList.addAll(RpcConfig.getInstance().getMasterGroupList());
        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        redissonClient = (RedissonClient)beanFactory.getBean(RedissonClientConfig.class);
    }

    synchronized public SocketAddress getSocketAddress()
    {
        if (ObjectUtil.isEmpty(defaultSocketAddressList))
        {
            log.error("RPC服务调用,没有配置服务器地址列表");
            return null;
        }
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

    public List<SocketAddress> getDefaultSocketAddressList() {

        List<SocketAddress> result = new ArrayList<>();
        for (SocketAddress socketAddress:defaultSocketAddressList)
        {
            String ipStr = IpUtil.getIp(socketAddress);
            result.add(IpUtil.getSocketAddress(ipStr));
        }

        return result;
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

        StringBuilder ips = new StringBuilder();
        List<RouteSession>  list = routeChannelManage.getRouteSessionList();
        if (redissonClient==null)
        {
            defaultSocketAddressList.clear();
        }
        for (RouteSession session:list)
        {
            if (YesNoEnumType.YES.getValue()==session.getOnline())
            {
                if (redissonClient==null)
                {
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
