package com.github.jspxnet.network.rpc.client.command;

import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.route.RouteManage;
import com.github.jspxnet.network.rpc.model.route.RouteSession;
import com.github.jspxnet.network.rpc.model.route.impl.RouteChannelManage;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.StringUtil;
import io.netty.channel.Channel;

import java.util.List;

public class RouteCmd extends INetCommand {


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
        RouteManage routeManage = RouteChannelManage.getInstance();
        if (INetCommand.TYPE_JSON.equals(command.getType()))
        {
            String str = command.getData();
            if (StringUtil.isJsonObject(str))
            {
                JSONObject json = new JSONObject(str);
                //只有同一个功能组的才加入进来
                JSONArray jsonArray = json.getJSONArray(RouteChannelManage.KEY_ROUTE);
                List<RouteSession> list = jsonArray.parseObject(RouteSession.class);
                routeManage.joinCheckRoute(list);
            }
        }

        SendCmd replyCmd = BeanUtil.copy(command, SendCmd.class);
        replyCmd.setAction(INetCommand.ROUTE);
        replyCmd.setType(INetCommand.TYPE_JSON);
        replyCmd.setData(routeManage.getSendRouteTable());
        return replyCmd;
    }
}

