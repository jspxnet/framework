package com.github.jspxnet.network.rpc.client.command;

import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.rpc.env.MasterSocketAddress;
import com.github.jspxnet.network.rpc.env.RpcConfig;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.route.RouteSession;
import com.github.jspxnet.network.rpc.model.route.RouteChannelManage;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.StringUtil;
import io.netty.channel.Channel;

import java.util.List;

public class RouteCmd extends INetCommand {
    private RouteChannelManage routeChannelManage = RouteChannelManage.getInstance();

    static final public String NAME = INetCommand.ROUTE;

    /**
     * 执行方法
     * @param channel 连接
     * @param command  请求命令
     * @return 返回
     */
    @Override
    public SendCmd execute(Channel channel, SendCmd command)
    {
        if (INetCommand.TYPE_JSON.equals(command.getType()))
        {
            String str = command.getData();
            if (StringUtil.isJsonObject(str))
            {
                JSONObject json = new JSONObject(str);

                String groupName = json.getString(RouteChannelManage.KEY_GROUP_NAME);
                if (!StringUtil.isNull(groupName)&& RpcConfig.getInstance().getGroupName().equalsIgnoreCase(groupName))
                {
                    //只有同一个功能组的才加入进来
                    JSONArray jsonArray = json.getJSONArray(RouteChannelManage.KEY_ROUTE);
                    List<RouteSession> list = jsonArray.parseObject(RouteSession.class);
                    routeChannelManage.join(list);
                }
                
            }
        }

        SendCmd replyCmd = BeanUtil.copy(command, SendCmd.class);
        replyCmd.setAction(INetCommand.ROUTE);
        replyCmd.setType(INetCommand.TYPE_JSON);
        replyCmd.setData(routeChannelManage.getSendRouteTable());
        return replyCmd;
    }
}

