package com.github.jspxnet.network.rpc.service.route;


import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.rpc.client.NettyClientPool;
import com.github.jspxnet.network.rpc.client.ReplyCmdFactory;
import com.github.jspxnet.network.rpc.env.MasterSocketAddress;
import com.github.jspxnet.network.rpc.env.RpcConfig;
import com.github.jspxnet.network.rpc.model.SendCommandFactory;
import com.github.jspxnet.network.rpc.model.route.RouteChannelManage;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.route.RouteSession;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
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
public class RouteService implements Runnable {
    private static final RouteChannelManage ROUTE_CHANNEL_MANAGE = RouteChannelManage.getInstance();

    //第一次使用配置服务器地址,以后将切换到路由表
    //有被误判拦截ip的可能
    //private static final NettyClient NETTY_CLIENT = new NettyClient();

    private static final NettyClientPool NETTY_CLIENT = NettyClientPool.getInstance();
    private static int configCount = 1;
    private static long lastInitTimeMillis = System.currentTimeMillis();
    private static boolean isRun = true;
    private void init() {
        //初始化数据 begin
        MasterSocketAddress masterSocketAddress = MasterSocketAddress.getInstance();
        List<RouteSession> routeSessionList = new ArrayList<>();
        List<String> nameList = masterSocketAddress.getDefaultSocketAddressGroupNames();
        for (String name : nameList) {
            if (StringUtil.isNull(name)) {
                continue;
            }
            List<SocketAddress> defaultSocketAddressList = masterSocketAddress.getDefaultSocketAddressList(name);
            for (SocketAddress socketAddress : defaultSocketAddressList) {
                RouteSession routeSession = new RouteSession();
                routeSession.setSocketAddress(socketAddress);
                routeSession.setGroupName(name);
                routeSession.setCreateTimeMillis(System.currentTimeMillis());
                routeSession.setLastRequestTime(System.currentTimeMillis());
                routeSessionList.add(routeSession);
            }
        }

        JSONObject json = new JSONObject();
        json.put(RouteChannelManage.KEY_ROUTE, routeSessionList);
        configCount = routeSessionList.size();
        for (RouteSession routeSession : routeSessionList) {
            SendCmd cmd = SendCommandFactory.createCommand(INetCommand.REGISTER);
            cmd.setType(INetCommand.TYPE_JSON);
            cmd.setData(json.toString());

            SendCmd reply = null;
            try {
                reply = NETTY_CLIENT.send(routeSession.getSocketAddress(), cmd);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    /**
     * 检查判断关联ip是否在路由表中
     */
    private void relevancy() {
        //初始化数据 begin
        MasterSocketAddress masterSocketAddress = MasterSocketAddress.getInstance();
        List<RouteSession> routeSessionList = new ArrayList<>();
        RpcConfig rpcConfig = RpcConfig.getInstance();
        List<String> nameList = masterSocketAddress.getDefaultSocketAddressGroupNames();
        for (String name : nameList) {
            if (StringUtil.isNull(name)) {
                continue;
            }
            List<SocketAddress> defaultSocketAddressList = rpcConfig.getMasterGroupList(name);
            if (ObjectUtil.isEmpty(defaultSocketAddressList))
            {
                continue;
            }
            for (SocketAddress socketAddress : defaultSocketAddressList) {
                RouteSession routeSession = new RouteSession();
                routeSession.setSocketAddress(socketAddress);
                routeSession.setGroupName(name);
                routeSession.setCreateTimeMillis(System.currentTimeMillis());
                routeSession.setLastRequestTime(System.currentTimeMillis());
                routeSessionList.add(routeSession);
            }
        }

        //需要检测,没有在路由表的边的服务器,是否起来了
        List<RouteSession> checkSessionList = ROUTE_CHANNEL_MANAGE.getNotRouteSessionList(routeSessionList);
        if (ObjectUtil.isEmpty(checkSessionList)) {
            return;
        }

        JSONObject json = new JSONObject();
        json.put(RouteChannelManage.KEY_ROUTE, ROUTE_CHANNEL_MANAGE.getRouteSessionList());

        for (RouteSession routeSession : checkSessionList) {
            SendCmd cmd = SendCommandFactory.createCommand(INetCommand.REGISTER);
            cmd.setType(INetCommand.TYPE_JSON);
            cmd.setData(json.toString());
            try {
                SendCmd reply = NETTY_CLIENT.send(routeSession.getSocketAddress(), cmd);
                if (reply == null || reply.getAction().equalsIgnoreCase(INetCommand.EXCEPTION)) {
                    ROUTE_CHANNEL_MANAGE.routeOff(routeSession.getSocketAddress());
                    continue;
                }

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
            } catch (Exception e) {
                //..
               if (rpcConfig.isDebug())
               {
                   log.debug("检测关联服务器没有上线:{}", routeSession.getSocketAddress());
               }

            }
        }
    }

    @Override
    public void run() {


        RpcConfig rpcConfig = RpcConfig.getInstance();
        long lastRelevancyTimeMillis = System.currentTimeMillis();
        init();
        while (isRun) {
            try {
                checkSocketAddressRoute();
                ROUTE_CHANNEL_MANAGE.cleanOffRoute();
                linkRoute();
                MasterSocketAddress.getInstance().flushAddress();
                if (rpcConfig.isDebug())
                {
                    log.debug("当前路由表:\r\n{}", RouteChannelManage.getInstance().getSendRouteTable());
                }
                Thread.sleep(rpcConfig.getRoutesSecond() * DateUtil.SECOND);
                if (System.currentTimeMillis() - lastRelevancyTimeMillis > rpcConfig.getRoutesSecond() * DateUtil.SECOND*10) {
                    lastRelevancyTimeMillis = System.currentTimeMillis();
                    relevancy();
                }
            } catch (Exception e) {
                //...
                if (rpcConfig.isDebug())
                {
                    log.debug(e.getMessage());
                }
            }
        }
        //NETTY_CLIENT.shutdown();
        isRun = false;

    }

    private void linkRoute() {
        List<RouteSession> routeSessionList = ROUTE_CHANNEL_MANAGE.getRouteSessionList();
        if (ObjectUtil.isEmpty(routeSessionList)) {
            return;
        }
        //交换路由表
        for (RouteSession routeSession : routeSessionList) {
            if (YesNoEnumType.NO.getValue() == routeSession.getOnline()) {
                continue;
            }
            try {
                SendCmd getRoute = SendCommandFactory.createCommand(INetCommand.GET_ROUTE);
                getRoute.setType(INetCommand.TYPE_JSON);
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
                if (ROUTE_CHANNEL_MANAGE.getRouteSessionList().size() <= configCount && System.currentTimeMillis() - lastInitTimeMillis > DateUtil.MINUTE) {
                    init();
                    lastInitTimeMillis = System.currentTimeMillis();
                }
                //把路由表自己保管起来
            } catch (Exception e) {
                ROUTE_CHANNEL_MANAGE.routeOff(routeSession.getSocketAddress());
                RpcConfig rpcConfig = RpcConfig.getInstance();
                if (rpcConfig.isDebug())
                {
                    log.debug("RPC路由网络中存在异常服务器:{}", ObjectUtil.toString(routeSession));
                }

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
                ROUTE_CHANNEL_MANAGE.routeOff(routeSession.getSocketAddress());
                RpcConfig rpcConfig = RpcConfig.getInstance();
                if (rpcConfig.isDebug())
                {
                    log.debug("RPC路由网络中存在异常服务器:{}", ObjectUtil.toString(routeSession));
                }
            }
        }
        checkRouteSocketList.clear();
    }

    public void shutdown()
    {
        isRun = false;
        NettyClientPool.getInstance().close();
    }

}
