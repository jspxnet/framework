package com.github.jspxnet.network.rpc.model;

import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.utils.RandomUtil;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/23 23:27
 * description: jspx-framework
 **/
public class SendCommandFactory {
    public static SendCmd createCommand(String action)
    {
        SendCmd command = new SendCmd();
        command.setId(RandomUtil.getRandomGUID(24));
        command.setAction(action);
        return command;
    }

    /**
     *
     * @param info 提示信息
     * @return 异常提示
     */
    public static SendCmd createExceptionCommand(String info)
    {
        SendCmd command = new SendCmd();
        command.setId(RandomUtil.getRandomGUID(24));
        command.setAction(INetCommand.EXCEPTION);
        command.setType(INetCommand.TYPE_TXT);
        command.setData(info);
        return command;
    }
}
