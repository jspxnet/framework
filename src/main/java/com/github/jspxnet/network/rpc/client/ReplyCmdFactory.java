package com.github.jspxnet.network.rpc.client;

import com.github.jspxnet.network.rpc.client.command.*;
import com.github.jspxnet.network.rpc.model.SendCommandFactory;
import com.github.jspxnet.network.rpc.model.cmd.ICmd;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import io.netty.channel.Channel;
import java.util.HashMap;
import java.util.Map;

public class ReplyCmdFactory {
    final static private Map<String, String> CMD_ACTION_MAP = new HashMap<>();
    static
    {
        //让客户端退出
        CMD_ACTION_MAP.put(ExitCmd.NAME,ExitCmd.class.getName());
        //ping
        CMD_ACTION_MAP.put(PingCmd.NAME,PingCmd.class.getName());
    }

    public static boolean isSysCmd(String action)
    {
        return CMD_ACTION_MAP.containsKey(action);
    }

    /**
     *
     * @param channel 频道
     * @param command 命令
     * @return 返回
     * @throws Exception 异常
     */
    public static SendCmd exeSysReply(Channel channel, SendCmd command) throws Exception {
        String actionCmdName =  CMD_ACTION_MAP.get(command.getAction());
        if (StringUtil.isNull(actionCmdName))
        {
            return SendCommandFactory.createExceptionCommand("不存在的指令");
        }
        ICmd cmd = (ICmd) ClassUtil.newInstance(actionCmdName);
        return cmd.execute(channel,command);
    }

}
