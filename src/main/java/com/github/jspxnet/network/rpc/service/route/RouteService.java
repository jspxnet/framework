package com.github.jspxnet.network.rpc.service.route;


import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.GsonUtil;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.rpc.client.NettyClient;
import com.github.jspxnet.network.rpc.client.ReplyCmdFactory;
import com.github.jspxnet.network.rpc.env.MasterSocketAddress;
import com.github.jspxnet.network.rpc.env.RpcConfig;
import com.github.jspxnet.network.rpc.model.SendCommandFactory;
import com.github.jspxnet.network.rpc.model.route.RouteChannelManage;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.route.RouteSession;
import com.github.jspxnet.utils.BeanUtil;
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

    private void init() throws Exception {
        //初始化数据 begin
        List<String> nameList = MasterSocketAddress.getInstance().getDefaultSocketAddressGroupNames();
        for (String name:nameList)
        {
            List<SocketAddress> defaultSocketAddressList = MasterSocketAddress.getInstance().getDefaultSocketAddressList(name);
            for (SocketAddress socketAddress : defaultSocketAddressList) {
                SendCmd cmd = SendCommandFactory.createCommand(INetCommand.REGISTER);
                cmd.setType(INetCommand.TYPE_JSON);
                SendCmd reply = NETTY_CLIENT.send(socketAddress, cmd);
                if (reply != null && INetCommand.TYPE_JSON.equals(reply.getType())) {
                    String str = reply.getData();
                    if (StringUtil.isJsonObject(str)) {

                        JSONObject json = new JSONObject(str);
                        //只有同一个功能组的才加入进来
                        JSONArray jsonArray = json.getJSONArray(RouteChannelManage.KEY_ROUTE);
                        List<RouteSession> list = jsonArray.parseObject(RouteSession.class);
                        ROUTE_CHANNEL_MANAGE.join(list);
                    }
                }
                //把路由表自己保管起来
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
                List<String> groupNameList =  linkRoute();
                Thread.sleep(DateUtil.SECOND);
                ROUTE_CHANNEL_MANAGE.cleanOffRoute();
                MasterSocketAddress.getInstance().flushAddress(groupNameList);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        NETTY_CLIENT.shutdown();

    }

    private List<String> linkRoute() {
        List<RouteSession> routeSessionList = ROUTE_CHANNEL_MANAGE.getRouteSessionList();
        if (routeSessionList == null || routeSessionList.isEmpty()) {
            return null;
        }
        List<String> result = new ArrayList<>();
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
                        result.addAll(ROUTE_CHANNEL_MANAGE.join(list));
                    }
                }
                //把路由表自己保管起来
            } catch (Exception e) {
                ROUTE_CHANNEL_MANAGE.routeOff(routeSession.getSocketAddress());
                e.printStackTrace();
                log.error("RPC路由网络中存在异常服务器:{},错误:{}", ObjectUtil.toString(routeSession), e.getMessage());
            }
        }
        return result;
    }

    /**
     * 原则注册的地址是否正确
     */
    private void checkSocketAddressRoute() {
        List<SocketAddress> checkRouteSocketList = ROUTE_CHANNEL_MANAGE.getCheckRouteSocketList();
        if (checkRouteSocketList.isEmpty()) {
            return;
        }
        SendCmd cmd = new SendCmd();
        cmd.setAction(INetCommand.GET_ROUTE);
        cmd.setType(INetCommand.TYPE_JSON);
        for (SocketAddress socketAddress : checkRouteSocketList) {
            try {
                SendCmd reply = NETTY_CLIENT.send(socketAddress, cmd);
                if (reply != null && INetCommand.TYPE_JSON.equals(reply.getType())) {
                    String str = reply.getData();
                    if (StringUtil.isJsonObject(str)) {
                        JSONObject json = new JSONObject(str);
                        //只有同一个功能组的才加入进来
                        JSONArray jsonArray = json.getJSONArray(RouteChannelManage.KEY_ROUTE);
                        List<RouteSession> list = jsonArray.parseObject(RouteSession.class);
                        ROUTE_CHANNEL_MANAGE.join(list);
                    }
                }
                //把路由表自己保管起来
                Thread.sleep(RpcConfig.getInstance().getTimeout() * DateUtil.SECOND);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("RPC路由网络中存在异常服务器:{},错误:{}", ObjectUtil.toString(socketAddress), e.getMessage());
            }
        }
        checkRouteSocketList.clear();
    }
}
