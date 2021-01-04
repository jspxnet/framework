package com.github.jspxnet.network.rpc.env;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.utils.IpUtil;
import com.github.jspxnet.utils.StringUtil;

import java.net.SocketAddress;
import java.util.List;

/**
 * Created by jspx.net
 * <p>
 * author: chenYuan
 * date: 2020/6/21 0:46
 * description: rpc服务配置
 **/
public class RpcConfig {
    private EnvironmentTemplate ENV_TEMPLATE = EnvFactory.getEnvironmentTemplate();
    final static private String USE_NETTY_RPC = "useNettyRpc";

    //包的最大长度
    final static private String MAX_FRAME_LENGTH = "rpc.maxFrameLength";
    //缓存大小
    final static private String BUFFER_SIZE = "rpc.bufferSize";

    //队列的大小
    final static private String BACKLOG = "rpc.backlog";

    //线程数,用CPU数量
    final static private String WORK_THREAD = "rpc.workThread";

    //服务器本机地址
    final static private String LOCAL_ADDRESS = "rpc.localAddress";

    final static private String LOCAL_GROUP_NAME = "rpc.localGroupName";

    //服务器本机地址
    final static private String MASTER_GROUP = "rpc.master.group";


    //服务器本机功能组名称
    final static private String GROUP_NAMES = "rpc.group.names";

    //超时,单位为秒
    final static private String TIMEOUT = "rpc.timeout";


    final static private RpcConfig instance = new RpcConfig();

    public static RpcConfig getInstance() {

        return instance;
    }


    public boolean isUseNettyRpc() {
        return ENV_TEMPLATE.getBoolean(USE_NETTY_RPC);
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

    public int getTimeout() {
        return ENV_TEMPLATE.getInt(TIMEOUT, 5);
    }

    public String getLocalAddress() {
        return ENV_TEMPLATE.getString(LOCAL_ADDRESS);
    }

    public List<SocketAddress> getLocalAddressList() {
        String localAddressStr = getLocalAddress();
        return IpUtil.getSocketAddressList(localAddressStr);
    }

    public String[] getLocalGroupList() {
        return StringUtil.split(ENV_TEMPLATE.getString(LOCAL_GROUP_NAME),StringUtil.SEMICOLON);
    }

    public String getMasterGroup(String groupName) {
        return ENV_TEMPLATE.getString(MASTER_GROUP + StringUtil.DOT + groupName);
    }

    public List<SocketAddress> getMasterGroupList(String groupName) {
        String masterGroupStr = getMasterGroup(groupName);
        return IpUtil.getSocketAddressList(masterGroupStr);
    }


    public String getSecretKey() {
        return ENV_TEMPLATE.getString(Environment.secretKey, Environment.defaultDrug);
    }

    public String getCipherIv() {
        return ENV_TEMPLATE.getString(Environment.cipherIv);
    }

    public String[] getGroupNames() {
        return StringUtil.split(ENV_TEMPLATE.getString(GROUP_NAMES, "default"),StringUtil.SEMICOLON);
    }
}
