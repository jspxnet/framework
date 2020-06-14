package com.github.jspxnet.network.rpc.service;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/9 22:30
 * description: jspbox
 **/


import com.github.jspxnet.network.rpc.model.transfer.ChannelSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionChannelManage {
    final private static SessionChannelManage instance = new SessionChannelManage();
    //屏道id,用户session
    final private static Map<ChannelId, ChannelSession> sessionMap = new ConcurrentHashMap<>();

    //ChannelGroup 和 sessionMap 是不一样多的,这里作为RPC其实不需要,但为了扩展功能,保存
    final private static ChannelGroup channelGroupList = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static SessionChannelManage getInstance()
    {
        return instance;
    }

    private SessionChannelManage()
    {

    }

    public boolean add(Channel channel)
    {
        return channelGroupList.add(channel);
    }

    public ChannelSession add(ChannelSession netSession)
    {
        return sessionMap.put(netSession.getChannelId(),netSession);
    }

    public boolean remove(Channel channel)
    {
        return channelGroupList.remove(channel);
    }

    public ChannelSession getSession(ChannelId channelId)
    {
        return sessionMap.get(channelId);
    }

    public ChannelSession removeSession(ChannelId channelId)
    {
        return sessionMap.remove(channelId);
    }



}
