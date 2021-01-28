package com.github.jspxnet.network.rpc.model.route;

import com.github.jspxnet.sober.annotation.Column;
import lombok.Data;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/23 0:24
 * description: jspbox
 **/
@Data
public class RouteSession implements Serializable {

    private String groupName;

    private InetSocketAddress socketAddress;

    @Column(caption="最后请求时间",notNull = true)
    private long lastRequestTime = System.currentTimeMillis();

    @Column(caption="登陆时间",notNull = true)
    private long createTimeMillis  = System.currentTimeMillis();

    //用户状态,在线,离线
    private int online = 0;

    //心跳计数
    private int heartbeatTimes = 0;

}
