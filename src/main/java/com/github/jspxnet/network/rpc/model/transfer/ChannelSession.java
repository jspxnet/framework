package com.github.jspxnet.network.rpc.model.transfer;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.IDType;
import com.github.jspxnet.sober.annotation.Id;
import io.netty.channel.ChannelId;
import lombok.Data;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Date;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/9 22:33
 * description: 路由表节点信息
 **/
@Data
public class ChannelSession implements Serializable {

    //服务器id,防止重复
    private ChannelId channelId = null;

    private SocketAddress socketAddress;

    @Column(caption="最后请求时间",notNull = true)
    private long lastRequestTime = System.currentTimeMillis();

    @Column(caption="登陆时间",notNull = true)
    private long createTimeMillis  = System.currentTimeMillis();

    //用户状态,在线,离线
    private int online = 0;

    //心跳计数
    private int heartbeatTimes = 0;

}
