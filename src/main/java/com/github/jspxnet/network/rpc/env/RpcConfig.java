package com.github.jspxnet.network.rpc.env;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.network.rpc.model.route.RouteSession;
import com.github.jspxnet.utils.IpUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jspx.net
 * <p>
 * author: chenYuan
 * date: 2020/6/21 0:46
 * description: rpc服务配置
 **/
@Slf4j
public class RpcConfig {
    final static private EnvironmentTemplate ENV_TEMPLATE = EnvFactory.getEnvironmentTemplate();

    final static private String USE_NETTY_RPC = "useNettyRpc";

    final static private String USE_RPC_SECRET = "useRpcSecret";

    final static private String USE_RPC_ZIP = "useRpcZip";

    final static private String NETTY_RPC_DEBUG = "nettyRpcDebug";

    //包的最大长度
    final static private String MAX_FRAME_LENGTH = "rpc_maxFrameLength";
    //缓存大小
    final static private String BUFFER_SIZE = "rpc_bufferSize";

    //队列的大小
    final static private String BACKLOG = "rpc_backlog";

    //线程数,用CPU数量
    final static private String WORK_THREAD = "rpc_workThread";

    //服务器本机地址
    final static private String LOCAL_ADDRESS = "rpc_localAddress";

    final static private String LOCAL_GROUP_NAME = "rpc_localGroupName";

    //分组个数,是用在多个tomcat是用统一个配置的时候,每个容器启动几个服务
    final static private String LOCAL_GROUP_COUNT = "rpc_localGroupCount";

    //服务器本机地址
    final static private String MASTER_GROUP = "rpc_master_group";

    //服务器本机功能组名称
    final static private String GROUP_NAMES = "rpc_group_names";

    final static private String JOIN_KEY = "rpc_joinKey";

    //超时,单位为秒
    final static private String TIMEOUT = "rpc_timeout";

    //路由秒数
    final static private String ROUTES_SECOND = "rpc_routesSecond";

    //分布式服务器发现模式
    final static private String SERVICE_DISCOVER_MODE = "serviceDiscoverMode";

    //127.0.0.1IP自动转内网ip
    final static private String LOCAL_IP_AUTO_PUBLIC_IP = "localIpAutoPublicIp";

    final static private RpcConfig INSTANCE = new RpcConfig();

    public static RpcConfig getInstance() {

        return INSTANCE;
    }

    public boolean isUseNettyRpc() {
        return ENV_TEMPLATE.getBoolean(USE_NETTY_RPC);
    }

    public boolean isDebug() {
        return ENV_TEMPLATE.getBoolean(NETTY_RPC_DEBUG);
    }

    public int getMaxFrameLength() {
        return ENV_TEMPLATE.getInt(MAX_FRAME_LENGTH, 1048576);
    }

    public int getBufferSize() {
        return ENV_TEMPLATE.getInt(BUFFER_SIZE, 1024);
    }

    public int getBacklog() {
        return ENV_TEMPLATE.getInt(BACKLOG, 1024);
    }

    public int getWorkThread() {
        return ENV_TEMPLATE.getInt(WORK_THREAD, 1);
    }

    public int getGroupCount() {
        return ENV_TEMPLATE.getInt(LOCAL_GROUP_COUNT, 1);
    }

    public int getTimeout() {
        return ENV_TEMPLATE.getInt(TIMEOUT, 10)<3?5:ENV_TEMPLATE.getInt(TIMEOUT, 10);
    }

    public int getRoutesSecond() {
        return ENV_TEMPLATE.getInt(ROUTES_SECOND, 5);
    }

    public String getLocalAddress() {
        return ENV_TEMPLATE.getString(LOCAL_ADDRESS);
    }

    public String getJoinKey() {
        return ENV_TEMPLATE.getString(JOIN_KEY);
    }


    public List<InetSocketAddress> getLocalAddressList() {
        String localAddressStr = getLocalAddress();
        List<InetSocketAddress> result = IpUtil.getSocketAddressList(localAddressStr);
        if (getLocalIpAutoPublicIp()) {
            InetAddress inetAddress = IpUtil.getPublicIp();
            if (inetAddress == null) {
                return result;
            }

            String ip = inetAddress.getHostAddress();
            if (!StringUtil.isNull(ip)) {
                for (int i = 0; i < result.size(); i++) {
                    InetSocketAddress address = result.get(i);
                    if ("127.0.0.1".equalsIgnoreCase(IpUtil.getOnlyIp(address))) {
                        address = new InetSocketAddress(ip, address.getPort());
                        result.set(i, address);
                    }
                }
            }
        }
        return result;
    }

    private String[] getLocalGroupList() {
        return StringUtil.split(ENV_TEMPLATE.getString(LOCAL_GROUP_NAME), StringUtil.SEMICOLON);
    }


    public List<InetSocketAddress> getMasterGroupList(String groupName) {
        String masterGroupStr = ENV_TEMPLATE.getString(MASTER_GROUP + StringUtil.UNDERLINE + groupName);
        return IpUtil.getSocketAddressList(masterGroupStr);
    }

    public String getServiceDiscoverMode() {
        return ENV_TEMPLATE.getString(SERVICE_DISCOVER_MODE);
    }

    public String getSecretKey() {
        return ENV_TEMPLATE.getString(Environment.secretKey, Environment.defaultDrug);
    }

    /**
     *
     * @return 是否加密传输
     */
    public boolean getUseRpcSecret()
    {
        return ENV_TEMPLATE.getBoolean(USE_RPC_SECRET);
    }


    public boolean getUseRpcZip()
    {
        return ENV_TEMPLATE.getBoolean(USE_RPC_ZIP);
    }


    public String getCipherIv() {
        return ENV_TEMPLATE.getString(Environment.cipherIv);
    }

    public String[] getGroupNames() {
        return StringUtil.split(ENV_TEMPLATE.getString(GROUP_NAMES, "default"), StringUtil.SEMICOLON);
    }

    public boolean getLocalIpAutoPublicIp() {
        return ENV_TEMPLATE.getBoolean(LOCAL_IP_AUTO_PUBLIC_IP);
    }

    /**
     *
     * @return 得到配置的路由表
     */
    public List<RouteSession> createConfigRouteSessionList() {
        List<RouteSession> result = new ArrayList<>();
        //初始化默认的路由表,就是自己的IP地址---begin
        String[] groupNames = getLocalGroupList();
        if (ObjectUtil.isEmpty(groupNames))
        {
            log.error("本地路由表分组没有配置");
            return new ArrayList<>(0);
        }
        List<InetSocketAddress> list = getLocalAddressList();
        int i = 0;
        for (InetSocketAddress socketAddress : list) {
            RouteSession routeSession = new RouteSession();
            routeSession.setSocketAddress(socketAddress);
            routeSession.setOnline(YesNoEnumType.YES.getValue());
            routeSession.setHeartbeatTimes(0);
            if (groupNames.length >= list.size()) {
                routeSession.setGroupName(groupNames[i]);
                i++;
            } else {
                routeSession.setGroupName(groupNames[0]);
            }
            result.add(routeSession);
        }
        //初始化默认的路由表,就是自己的IP地址---end

        //在加上配置的集群ip表-----begin
        result.addAll(createMasterRouteSessionList());
        //在加上配置的集群ip表-----end
        return result;
    }

    /**
     * 配置的关联ip 路由表
     * @return 配置的关联ip
     */
    public List<RouteSession> createMasterRouteSessionList() {
        List<RouteSession> result = new ArrayList<>();
        //在加上配置的集群ip表-----begin
        String[] mastGroupName = getGroupNames();
        if (ObjectUtil.isEmpty(mastGroupName))
        {
            log.error("集群路由表分组没有配置");
            return new ArrayList<>(0);
        }
        int j = 0;
        for (String groupName : mastGroupName) {
            List<InetSocketAddress> addressGroupList = getMasterGroupList(groupName);
            for (InetSocketAddress socketAddress : addressGroupList) {
                RouteSession routeSession = new RouteSession();
                routeSession.setSocketAddress(socketAddress);
                routeSession.setOnline(YesNoEnumType.YES.getValue());
                routeSession.setHeartbeatTimes(0);
                if (mastGroupName.length >= addressGroupList.size()) {
                    routeSession.setGroupName(mastGroupName[j]);
                    j++;
                } else {
                    routeSession.setGroupName(mastGroupName[0]);
                }
                result.add(routeSession);
            }
        }
        //在加上配置的集群ip表-----end
        return result;
    }
}
