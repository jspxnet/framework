package com.github.jspxnet.network.rpc.service.route;


import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.rpc.client.NettyClient;
import com.github.jspxnet.network.rpc.client.ReplyCmdFactory;
import com.github.jspxnet.network.rpc.env.MasterSocketAddress;
import com.github.jspxnet.network.rpc.model.SendCommandFactory;
import com.github.jspxnet.network.rpc.model.route.RouteChannelManage;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.route.RouteSession;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jspx.net
 * <p>
 * author: chenYuan
 * date: 2020/6/22 23:48
 * description: 是服务器之间发送路由
 **/
@Slf4j
public class RouteService extends Thread implements Runnable {

    private static final RouteChannelManage ROUTE_CHANNEL_MANAGE = RouteChannelManage.getInstance();

    //第一次使用配置服务器地址,以后将切换到路由表
    private static final NettyClient NETTY_CLIENT = new NettyClient();
    private static int configCount = 1;
    private static long lastTimeMillis = System.currentTimeMillis();
    private void init() throws Exception {

        //初始化数据 begin
        MasterSocketAddress masterSocketAddress = MasterSocketAddress.getInstance();
        List<RouteSession> routeSessionList = new ArrayList<>();
        List<String> nameList = masterSocketAddress.getDefaultSocketAddressGroupNames();
        for (String name:nameList)
        {
            List<SocketAddress> defaultSocketAddressList = masterSocketAddress.getDefaultSocketAddressList(name);
            for (SocketAddress socketAddress : defaultSocketAddressList)
            {
                RouteSession routeSession = new RouteSession();
                routeSession.setSocketAddress(socketAddress);
                routeSession.setGroupName(name);
                routeSession.setCreateTimeMillis(System.currentTimeMillis());
                routeSession.setLastRequestTime(System.currentTimeMillis());
                routeSessionList.add(routeSession);
            }
        }

        JSONObject json = new JSONObject();
        json.put(RouteChannelManage.KEY_ROUTE,routeSessionList);
        configCount = routeSessionList.size();
        for (RouteSession routeSession:routeSessionList)
        {
            SendCmd cmd = SendCommandFactory.createCommand(INetCommand.REGISTER);
            cmd.setType(INetCommand.TYPE_JSON);
            cmd.setData(json.toString());
            SendCmd reply = NETTY_CLIENT.send(routeSession.getSocketAddress(), cmd);
            if (reply != null && INetCommand.TYPE_JSON.equals(reply.getType())) {
                String str = reply.getData();
                if (StringUtil.isJsonObject(str)) {
                    JSONObject jsonTmp = new JSONObject(str);
                    //只有同一个功能组的才加入进来
                    JSONArray jsonArray = jsonTmp.getJSONArray(RouteChannelManage.KEY_ROUTE);
                    List<RouteSession> list = jsonArray.parseObject(RouteSession.class);
                    ROUTE_CHANNEL_MANAGE.join(list);
                }
            }
        }
        //初始化数据 end
    }

    @Override
    public void run() {

        try {
            init();
            Thread.sleep(DateUtil.SECOND);
            while (true) {
                checkSocketAddressRoute();
                Thread.sleep(DateUtil.SECOND);
                linkRoute();
                Thread.sleep(DateUtil.SECOND);
                ROUTE_CHANNEL_MANAGE.cleanOffRoute();
                Thread.sleep(DateUtil.SECOND);
                MasterSocketAddress.getInstance().flushAddress();
                log.debug("当前路由表:\r\n{}",RouteChannelManage.getInstance().getSendRouteTable());
                if (System.currentTimeMillis()-lastTimeMillis>DateUtil.MINUTE)
                {
                    log.debug("当前路由表:\r\n{}",RouteChannelManage.getInstance().getSendRouteTable());
                    lastTimeMillis = System.currentTimeMillis();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        NETTY_CLIENT.shutdown();

    }

    private void linkRoute() {
        List<RouteSession> routeSessionList = ROUTE_CHANNEL_MANAGE.getRouteSessionList();
        if (routeSessionList == null || routeSessionList.isEmpty()) {
            return ;
        }

        for (RouteSession routeSession : routeSessionList) {
            if (YesNoEnumType.NO.getValue() == routeSession.getOnline()) {
                continue;
            }
            try {
                Thread.sleep(DateUtil.SECOND);
                SendCmd getRoute = SendCommandFactory.createCommand(INetCommand.GET_ROUTE);
                getRoute.setType(INetCommand.TYPE_JSON);
                Channel channel = NETTY_CLIENT.connect(routeSession.getSocketAddress());
                if (!INetCommand.isConnect(channel))
                {
                    ROUTE_CHANNEL_MANAGE.routeOff(routeSession.getSocketAddress());
                    continue;
                }
                SendCmd reply = NETTY_CLIENT.send(routeSession.getSocketAddress(), getRoute);
                if (reply == null || reply.getAction().equalsIgnoreCase(INetCommand.EXCEPTION)) {
                    ROUTE_CHANNEL_MANAGE.routeOff(routeSession.getSocketAddress());
                    continue;
                }

                if (ReplyCmdFactory.isSysCmd(reply.getAction())) {
                    continue;
                }

                if (INetCommand.TYPE_JSON.equals(reply.getType())) {
                    String str = reply.getData();
                    if (StringUtil.isJsonObject(str)) {
                        JSONObject json = new JSONObject(str);
                        //只有同一个功能组的才加入进来
                        JSONArray jsonArray = json.getJSONArray(RouteChannelManage.KEY_ROUTE);
                        List<RouteSession> list = jsonArray.parseObject(RouteSession.class);
                        ROUTE_CHANNEL_MANAGE.join(list);
                    }
                }

                //如果路由表里边只有自己,配置里边还有其他的,要让其他的注册过来
                if (ROUTE_CHANNEL_MANAGE.getRouteSessionList().size()<=configCount && System.currentTimeMillis()-lastTimeMillis>DateUtil.MINUTE)
                {
                    init();
                    lastTimeMillis = System.currentTimeMillis();
                }
                //把路由表自己保管起来
            } catch (Exception e) {
                ROUTE_CHANNEL_MANAGE.routeOff(routeSession.getSocketAddress());
                e.printStackTrace();
                log.error("RPC路由网络中存在异常服务器:{},错误:{}", ObjectUtil.toString(routeSession), e.getMessage());
            }
        }
    }

    /**
     * 验证注册的地址是否正确
     */
    private void checkSocketAddressRoute() {
        List<RouteSession> checkRouteSocketList = ROUTE_CHANNEL_MANAGE.getRouteSessionList();
        if (checkRouteSocketList.isEmpty()) {
            return;
        }
        SendCmd cmd = new SendCmd();
        cmd.setAction(INetCommand.GET_ROUTE);
        cmd.setType(INetCommand.TYPE_JSON);
        for (RouteSession routeSession : checkRouteSocketList) {
            try {
                SendCmd reply = NETTY_CLIENT.send(routeSession.getSocketAddress(), cmd);
                if (reply != null && INetCommand.TYPE_JSON.equals(reply.getType())) {
                    String str = reply.getData();
                    if (StringUtil.isJsonObject(str)) {
                        JSONObject json = new JSONObject(str);
                        //只有同一个功能组的才加入进来
                        JSONArray jsonArray = json.getJSONArray(RouteChannelManage.KEY_ROUTE);
                        List<RouteSession> list = jsonArray.parseObject(RouteSession.class);
                        //把路由表自己保管起来
                        ROUTE_CHANNEL_MANAGE.join(list);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("RPC路由网络中存在异常服务器:{},错误:{}", ObjectUtil.toString(routeSession), e.getMessage());
            }
        }
        checkRouteSocketList.clear();
    }
}
