package com.github.jspxnet.network.rpc.service.route;


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
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private static final NettyClientPool NETTY_CLIENT = NettyClientPool.getInstance();

    private static boolean isRun = true;

    private void init() {
        RpcConfig rpcConfig = RpcConfig.getInstance();
        //初始化数据 begin
        MasterSocketAddress masterSocketAddress = MasterSocketAddress.getInstance();
        List<RouteSession> routeSessionList = new ArrayList<>();
        List<String> nameList = masterSocketAddress.getDefaultSocketAddressGroupNames();
        for (String name : nameList) {
            if (StringUtil.isNull(name)) {
                continue;
            }
            List<InetSocketAddress> defaultSocketAddressList = masterSocketAddress.getDefaultSocketAddressList(name);
            for (InetSocketAddress socketAddress : defaultSocketAddressList) {
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

        for (RouteSession routeSession : routeSessionList) {
            SendCmd cmd = SendCommandFactory.createCommand(INetCommand.REGISTER);
            cmd.setType(INetCommand.TYPE_JSON);
            cmd.setData(json.toString());
            cmd.setMd5(EncryptUtil.getMd5(json.toString() + rpcConfig.getJoinKey()));

            SendCmd reply = null;
            try {
                reply = NETTY_CLIENT.send(routeSession.getSocketAddress(), cmd);
            } catch (Exception e) {
                if (rpcConfig.isDebug()) {
                    log.debug("测试,netty rpc 调用服务器没有启动:{}", routeSession.getSocketAddress());
                }
            }
            if (reply != null && INetCommand.TYPE_JSON.equals(reply.getType())) {
                String str = reply.getData();
                String md5V = EncryptUtil.getMd5(str + rpcConfig.getJoinKey());
                if (StringUtil.isNull(reply.getMd5()) && !reply.getMd5().equalsIgnoreCase(md5V)) {
                    if (rpcConfig.isDebug()) {
                        log.debug("netty rpc join key 验证错误不允许加入:{}", routeSession.getSocketAddress());
                    }
                } else if (StringUtil.isJsonObject(str)) {
                    JSONObject jsonTmp = new JSONObject(str);
                    //只有同一个功能组的才加入进来
                    JSONArray jsonArray = jsonTmp.getJSONArray(RouteChannelManage.KEY_ROUTE);
                    List<RouteSession> list = jsonArray.parseObject(RouteSession.class);
                    ROUTE_CHANNEL_MANAGE.joinCheckRoute(list);
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
        RpcConfig rpcConfig = RpcConfig.getInstance();
        List<String> nameList = masterSocketAddress.getDefaultSocketAddressGroupNames();
        List<RouteSession> routeSessionList = new ArrayList<>();
        for (String name : nameList) {
            if (StringUtil.isNull(name)) {
                continue;
            }
            List<InetSocketAddress> defaultSocketAddressList = rpcConfig.getMasterGroupList(name);
            if (ObjectUtil.isEmpty(defaultSocketAddressList)) {
                continue;
            }
            for (InetSocketAddress socketAddress : defaultSocketAddressList) {
                RouteSession routeSession = new RouteSession();
                routeSession.setSocketAddress(socketAddress);
                routeSession.setGroupName(name);
                routeSession.setCreateTimeMillis(System.currentTimeMillis());
                routeSession.setLastRequestTime(System.currentTimeMillis());
                //本地启动端口ip不判断
                if (!RouteChannelManage.getStartList().contains(socketAddress)) {
                    routeSessionList.add(routeSession);
                }
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
            cmd.setMd5(EncryptUtil.getMd5(json.toString() + rpcConfig.getJoinKey()));
            try {
                SendCmd reply = NETTY_CLIENT.send(routeSession.getSocketAddress(), cmd);
                if (reply == null || reply.getAction().equalsIgnoreCase(INetCommand.EXCEPTION)) {
                    ROUTE_CHANNEL_MANAGE.routeOff(routeSession.getSocketAddress());
                    continue;
                }
                if (reply != null && INetCommand.TYPE_JSON.equals(reply.getType())) {
                    String str = reply.getData();
                    String md5V = EncryptUtil.getMd5(str + rpcConfig.getJoinKey());
                    if (StringUtil.isNull(reply.getMd5()) && !reply.getMd5().equalsIgnoreCase(md5V)) {
                        if (rpcConfig.isDebug()) {
                            log.debug("netty rpc join key 验证错误不允许加入:{}", routeSession.getSocketAddress());
                        }
                    } else if (StringUtil.isJsonObject(str)) {
                        JSONObject jsonTmp = new JSONObject(str);
                        //只有同一个功能组的才加入进来
                        JSONArray jsonArray = jsonTmp.getJSONArray(RouteChannelManage.KEY_ROUTE);
                        List<RouteSession> list = jsonArray.parseObject(RouteSession.class);
                        ROUTE_CHANNEL_MANAGE.joinCheckRoute(list);
                    }
                }
            } catch (Exception e) {
                //..
                if (rpcConfig.isDebug()) {
                    log.debug("检测关联服务器没有上线:{}", routeSession.getSocketAddress());
                }
            }
        }
    }

    /**
     * 检查网络传递的路由表,如果可用加入到路由表里边
     */
    private void checkRouteJoin() {
        Map<InetSocketAddress, RouteSession> routeSessionMap = ROUTE_CHANNEL_MANAGE.getCheckRouteSocketMap();
        if (ObjectUtil.isEmpty(routeSessionMap)) {
            return;
        }
        List<RouteSession> checkRouteSessionList = ROUTE_CHANNEL_MANAGE.getNotCheckRouteSessionList(new ArrayList<>(routeSessionMap.values()));

        RpcConfig rpcConfig = RpcConfig.getInstance();
        if (rpcConfig.isDebug()) {
            log.debug("测试进入的路由表:{}", ObjectUtil.toString(routeSessionMap));
        }
        //可以使用的路由表
        List<RouteSession> canUseList = new ArrayList<>();
        //交换路由表
        for (RouteSession routeSession : checkRouteSessionList) {
            try {
                SendCmd cmd = SendCommandFactory.createCommand(INetCommand.PING);
                cmd.setType(INetCommand.TYPE_JSON);
                SendCmd reply = NETTY_CLIENT.send(routeSession.getSocketAddress(), cmd);
                if (reply != null && INetCommand.PONG.equalsIgnoreCase(reply.getAction())) {

                    canUseList.add(routeSession);
                }
                //把路由表自己保管起来
            } catch (Exception e) {
                if (rpcConfig.isDebug()) {
                    log.debug("netty rpc 新进入路由验证没有连接:{}", routeSession.getSocketAddress());
                }
            }
        }

        if (rpcConfig.isDebug()) {
            log.debug("测试后可以添加用的路由表:{}", ObjectUtil.toString(canUseList));
        }
        ROUTE_CHANNEL_MANAGE.joinRoute(canUseList);
        routeSessionMap.clear();
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
                refreshLinkRoute();
                MasterSocketAddress.getInstance().flushAddress();
                if (System.currentTimeMillis() - lastRelevancyTimeMillis > rpcConfig.getRoutesSecond() * DateUtil.SECOND * 4 && rpcConfig.isDebug()) {
                    log.debug("当前路由表:\r\n{}", RouteChannelManage.getInstance().getSendRouteTable());
                }
                Thread.sleep(rpcConfig.getRoutesSecond() * DateUtil.SECOND);
                if (System.currentTimeMillis() - lastRelevancyTimeMillis > rpcConfig.getRoutesSecond() * DateUtil.SECOND*10)
                {
                    lastRelevancyTimeMillis = System.currentTimeMillis();
                    relevancy();
                }
                checkRouteJoin();
            } catch (Exception e) {
                //...
                if (rpcConfig.isDebug()) {
                    log.debug(e.getMessage());
                }
            }
        }
        //NETTY_CLIENT.shutdown();
        isRun = false;

    }

    /**
     * 验证本地有效的路由表里边是否有失效的,并且得到其他服务器的路由表
     */
    private void refreshLinkRoute() {
        List<RouteSession> routeSessionList = ROUTE_CHANNEL_MANAGE.getRouteSessionList();
        if (ObjectUtil.isEmpty(routeSessionList)) {
            return;
        }

        routeSessionList = ROUTE_CHANNEL_MANAGE.getNotRouteSessionList(routeSessionList);
        RpcConfig rpcConfig = RpcConfig.getInstance();
        //交换路由表
        for (RouteSession routeSession : routeSessionList) {
            try {
                SendCmd cmd = SendCommandFactory.createCommand(INetCommand.REGISTER);
                cmd.setType(INetCommand.TYPE_JSON);
                JSONObject json = new JSONObject();
                json.put(RouteChannelManage.KEY_ROUTE, routeSessionList);
                cmd.setMd5(EncryptUtil.getMd5(json.toString() + rpcConfig.getJoinKey()));
                SendCmd reply = NETTY_CLIENT.send(routeSession.getSocketAddress(), cmd);
                if (reply == null || reply.getAction().equalsIgnoreCase(INetCommand.EXCEPTION)) {
                    ROUTE_CHANNEL_MANAGE.routeOff(routeSession.getSocketAddress());
                    continue;
                }
                if (ReplyCmdFactory.isSysCmd(reply.getAction())) {
                    continue;
                }
                if (INetCommand.TYPE_JSON.equals(reply.getType())) {
                    String str = reply.getData();

                    String md5V = EncryptUtil.getMd5(str + rpcConfig.getJoinKey());
                    if (StringUtil.isNull(reply.getMd5()) && !reply.getMd5().equalsIgnoreCase(md5V)) {
                        if (rpcConfig.isDebug()) {
                            log.debug("netty rpc join key 验证错误不允许加入:{}", routeSession.getSocketAddress());
                        }
                    } else
                    if (StringUtil.isJsonObject(str)) {
                        json = new JSONObject(str);
                        //只有同一个功能组的才加入进来
                        JSONArray jsonArray = json.getJSONArray(RouteChannelManage.KEY_ROUTE);
                        List<RouteSession> list = jsonArray.parseObject(RouteSession.class);
                        ROUTE_CHANNEL_MANAGE.joinCheckRoute(list);
                    }
                }
                //把路由表自己保管起来
            } catch (Exception e) {
                ROUTE_CHANNEL_MANAGE.routeOff(routeSession.getSocketAddress());
                if (rpcConfig.isDebug()) {
                    log.debug("netty rpc 路由网络中存在异常服务器:{}", ObjectUtil.toString(routeSession));
                }

            }
        }
    }

    /**
     * 验证当前路由表里边是否有失效的ip服务器
     */
    private void checkSocketAddressRoute() {
        List<RouteSession> checkRouteSocketList = ROUTE_CHANNEL_MANAGE.getRouteSessionList();
        if (checkRouteSocketList.isEmpty()) {
            return;
        }
        checkRouteSocketList = ROUTE_CHANNEL_MANAGE.getNotCheckRouteSessionList(checkRouteSocketList);
        RpcConfig rpcConfig = RpcConfig.getInstance();
        SendCmd cmd = SendCommandFactory.createCommand(INetCommand.REGISTER);
        cmd.setType(INetCommand.TYPE_JSON);
        JSONObject json = new JSONObject();
        json.put(RouteChannelManage.KEY_ROUTE, ROUTE_CHANNEL_MANAGE.getRouteSessionList());
        cmd.setMd5(EncryptUtil.getMd5(json.toString() + rpcConfig.getJoinKey()));
        for (RouteSession routeSession : checkRouteSocketList) {
            try {
                SendCmd reply = NETTY_CLIENT.send(routeSession.getSocketAddress(), cmd);
                if (reply != null && INetCommand.TYPE_JSON.equals(reply.getType())) {
                    String str = reply.getData();
                    String md5V = EncryptUtil.getMd5(str + rpcConfig.getJoinKey());
                    if (StringUtil.isNull(reply.getMd5()) && !reply.getMd5().equalsIgnoreCase(md5V)) {
                        if (rpcConfig.isDebug()) {
                            log.debug("netty rpc join key 验证错误不允许加入:{}", routeSession.getSocketAddress());
                        }
                    } else
                    if (StringUtil.isJsonObject(str)) {
                        json = new JSONObject(str);
                        //只有同一个功能组的才加入进来
                        JSONArray jsonArray = json.getJSONArray(RouteChannelManage.KEY_ROUTE);
                        List<RouteSession> list = jsonArray.parseObject(RouteSession.class);
                        //把路由表自己保管起来
                        ROUTE_CHANNEL_MANAGE.joinCheckRoute(list);
                    }
                } else {
                    ROUTE_CHANNEL_MANAGE.routeOff(routeSession.getSocketAddress());
                }
            } catch (Exception e) {
                ROUTE_CHANNEL_MANAGE.routeOff(routeSession.getSocketAddress());
                if (rpcConfig.isDebug()) {
                    log.debug("netty rpc路由网络中存在无效服务器:{}", routeSession.getSocketAddress());
                }
            }
        }
        checkRouteSocketList.clear();
    }

    public void shutdown() {
        isRun = false;
        NettyClientPool.getInstance().shutdown();
    }

}
