package com.github.jspxnet.network.rpc.service.route;


import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.rpc.client.NettyClientPool;
import com.github.jspxnet.network.rpc.client.ReplyCmdFactory;
import com.github.jspxnet.network.rpc.env.RpcConfig;
import com.github.jspxnet.network.rpc.model.SendCommandFactory;
import com.github.jspxnet.network.rpc.model.route.impl.RouteChannelManage;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.route.RouteManage;
import com.github.jspxnet.network.rpc.model.route.RouteSession;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
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
    //第一次使用配置服务器地址,以后将切换到路由表
    //有被误判拦截ip的可能

    private static int debugTimes = 0;


    private void init() {
        RpcConfig rpcConfig = RpcConfig.getInstance();

        //初始化数据 begin
        List<RouteSession> routeSessionList = new ArrayList<>();
        String[] nameList = rpcConfig.getGroupNames();
        for (String name : nameList) {
            if (StringUtil.isNull(name)) {
                continue;
            }
            List<InetSocketAddress> defaultSocketAddressList = rpcConfig.getMasterGroupList(name);
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
        NettyClientPool nettyClient = NettyClientPool.getInstance();
        for (RouteSession routeSession : routeSessionList) {
            SendCmd cmd = SendCommandFactory.createCommand(INetCommand.REGISTER);
            cmd.setType(INetCommand.TYPE_JSON);
            cmd.setData(json.toString());
            cmd.setMd5(EncryptUtil.getMd5(json + rpcConfig.getJoinKey()));

            SendCmd reply = null;
            try {
                reply = nettyClient.send(routeSession.getSocketAddress(), cmd);
            } catch (Exception e) {
                if (rpcConfig.isDebug()) {
                    log.debug("检测,netty rpc 服务没有启动:{}",IpUtil.getIp(routeSession.getSocketAddress()));
                    debugTimes++;
                }
            }
            if (reply != null && INetCommand.TYPE_JSON.equals(reply.getType())) {
                String str = reply.getData();
                String md5V = EncryptUtil.getMd5(str + rpcConfig.getJoinKey());
                if (StringUtil.isNull(reply.getMd5()) && !reply.getMd5().equalsIgnoreCase(md5V)) {
                    if (rpcConfig.isDebug()) {
                        log.debug("netty rpc join key 验证错误不允许加入:{}",IpUtil.getIp(routeSession.getSocketAddress()));
                    }
                } else if (StringUtil.isJsonObject(str)) {
                    //只有同一个功能组的才加入进来
                    JSONArray jsonArray = new JSONObject(str).getJSONArray(RouteChannelManage.KEY_ROUTE);
                    List<RouteSession> list = jsonArray.parseObject(RouteSession.class);
                     RouteChannelManage.getInstance().joinCheckRoute(list);
                }
            }
        }
        //初始化数据 end
    }

    @Override
    public void run() {
        init();
        NettyClientPool nettyClient = NettyClientPool.getInstance();
        int routeTableCharLength = 0;

        boolean debug = RpcConfig.getInstance().isDebug();
        while (nettyClient.isRun()) {
            try {

                if (debug&&debugTimes<20) {
                    String routeInfo = RouteChannelManage.getInstance().getSendRouteTable();
                    if (routeTableCharLength != routeInfo.length())
                    {
                        log.debug("当前路由表:\r\n{}", routeInfo);

                    }
                    routeTableCharLength = routeInfo.length();
                }
                //检查可用的路由表
                refreshLinkRoute();

                Thread.sleep(StringUtil.toLong(RpcConfig.getInstance().getRoutesSecond() * DateUtil.SECOND+ StringUtil.empty));
                //检查待测试的路由表
                checkRouteJoin();
            } catch (Exception e) {
                //...
                log.error("路由检测异常",e);
            }
        }
        nettyClient.shutdown();
    }
    /**
     * 检查网络传递的路由表,如果可用加入到路由表里边
     */
    private void checkRouteJoin() {
        RouteManage routeManage = RouteChannelManage.getInstance();
        List<RouteSession> checkRouteSessionList = routeManage.getNeedCheckRouteSessionList();
        if (ObjectUtil.isEmpty(checkRouteSessionList))
        {
            return;
        }
        RpcConfig rpcConfig = RpcConfig.getInstance();
        boolean debug = rpcConfig.isDebug();
        if (debug&debugTimes<20) {
            log.debug("测试进入的路由表:{}", ObjectUtil.toString(checkRouteSessionList));
            debugTimes++;
        }

        //可以使用的路由表
        List<RouteSession> canUseList = new ArrayList<>();

        NettyClientPool nettyClient = NettyClientPool.getInstance();
        //交换路由表
        for (RouteSession routeSession : checkRouteSessionList) {
            try {
                SendCmd cmd = SendCommandFactory.createCommand(INetCommand.REGISTER);
                cmd.setType(INetCommand.TYPE_JSON);
                JSONObject json = new JSONObject();
                json.put(RouteChannelManage.KEY_ROUTE, routeManage.getRouteSessionList());
                cmd.setMd5(EncryptUtil.getMd5(json.toString() + rpcConfig.getJoinKey()));
                SendCmd reply = nettyClient.send(routeSession.getSocketAddress(), cmd);
                if (reply == null || reply.getAction().equalsIgnoreCase(INetCommand.EXCEPTION)) {
                    continue;
                }
                if (ReplyCmdFactory.isSysCmd(reply.getAction())) {
                    continue;
                }
                if (INetCommand.TYPE_JSON.equals(reply.getType())) {
                    String str = reply.getData();

                    String md5V = EncryptUtil.getMd5(str + rpcConfig.getJoinKey());
                    if (StringUtil.isNull(reply.getMd5()) && !reply.getMd5().equalsIgnoreCase(md5V)) {
                        if (debug&&debugTimes<20) {
                            log.debug("netty rpc join key 验证错误不允许加入:{}", routeSession.getSocketAddress());
                        }
                    } else
                    if (StringUtil.isJsonObject(str)) {

                        //路由表加入待测试
                        json = new JSONObject(str);
                        JSONArray jsonArray = json.getJSONArray(RouteChannelManage.KEY_ROUTE);
                        List<RouteSession> list = jsonArray.parseObject(RouteSession.class);
                        routeManage.joinCheckRoute(list);
                        //自己加入可用
                        canUseList.add(routeSession);
                    }
                }
                //把路由表自己保管起来
            } catch (Exception e) {
                routeManage.routeOff(routeSession.getSocketAddress());
                if (debug&&debugTimes<20) {
                    log.debug("netty rpc 路由网络中存在异常服务器:{},\r\n错误:{}", ObjectUtil.toString(routeSession),e.getMessage());

                }
            }
        }

        if (!ObjectUtil.isEmpty(canUseList))
        {
            if (debug&&debugTimes<20) {
                log.debug("添加路由表:{}", ObjectUtil.toString(canUseList));
                debugTimes++;
            }
            routeManage.joinRoute(canUseList);
        }
        routeManage.clearCheckRouteSocketMap();
    }
    /**
     * 验证本地有效的路由表里边是否有失效的,并且得到其他服务器的路由表
     * 检查已经有效的路由表是否有效,更新心条
     */
    private void refreshLinkRoute() {
        RouteManage routeManage = RouteChannelManage.getInstance();
        List<RouteSession> routeSessionList = routeManage.getRouteSessionList();
        NettyClientPool nettyClient = NettyClientPool.getInstance();
        //交换路由表
        for (RouteSession routeSession : routeSessionList) {
            try {
                SendCmd cmd = SendCommandFactory.createCommand(INetCommand.PING);
                cmd.setType(INetCommand.TYPE_JSON);
                SendCmd reply = nettyClient.send(routeSession.getSocketAddress(), cmd);
                if (reply != null && INetCommand.PONG.equalsIgnoreCase(reply.getAction()))
                {
                    routeManage.routeOn(routeSession.getSocketAddress());
                } else
                {
                    routeManage.routeOff(routeSession.getSocketAddress());
                }
                //把路由表自己保管起来
            } catch (Exception e) {
                routeManage.routeOff(routeSession.getSocketAddress());
                if (RpcConfig.getInstance().isDebug()&&debugTimes<20) {
                    log.debug("心跳检查无连接:{},\r\n错误:{}", IpUtil.getIp(routeSession.getSocketAddress()),e.getLocalizedMessage());
                }
            }
        }
        routeManage.cleanOffRoute();
    }

    public void shutdown() {

        NettyClientPool.getInstance().shutdown();
    }

}
