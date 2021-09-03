package com.github.jspxnet.network.rpc.model.cmd;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/13 0:11
 * description: jspx-framework
 **/


import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.rpc.env.RpcConfig;
import com.github.jspxnet.network.util.PacketUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 采用JSON 格式传输数据
 * {
 * "cmd":
 *
 * }
 * author chenYuan
 */
@Slf4j
public abstract class INetCommand implements ICmd {

    static public final String ACTION = "action";
    static public final String TYPE = "type";

    static public final String RPC = "rpc";

    static public final String HTTP = "http";

    static public final String EXCEPTION = "exception";
    //得到路由表
    static public final String ROUTE = "route";
    //注册,并得到路由表
    static public final String REGISTER = "register";

    //请求得到路由表
    //static public final String GET_ROUTE = "get_route";

    static public final String PING = "ping";
    static public final String PONG = "pong";

    //不做任何回复
    static public final String OK = "ok";

    static public final String EXIT = "exit";
    static public final String TYPE_JSON = "json";
    static public final String TYPE_BASE64 = "base64";
    static public final String TYPE_TXT = "txt";


    private static String secretKey = RpcConfig.getInstance().getSecretKey();
    private static String cipherIv = RpcConfig.getInstance().getCipherIv();

    static public void setSecretKey(String key) {
        secretKey = key;
    }

    public static String getSecretKey() {
        return secretKey;
    }

    static public void setCipherIv(String iv) {
        cipherIv = iv;
    }

    public static String getCipherIv() {
        return cipherIv;
    }

    public static String getDecodePacket(String text)
    {
        try {
            return PacketUtil.getDecodePacket(text,getSecretKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public  static void sendEncodePacket(Channel channel, SendCmd cmd) throws Exception {
        if (channel==null)
        {
            throw  new Exception("channel为空");
        }
        String sendData = PacketUtil.getEncodePacket(new JSONObject(cmd).toString(),secretKey);
        try {
            channel.writeAndFlush(sendData);
        } catch (Exception e)
        {
            throw  new Exception("发送信息发生掉线");
        }
    }


    /**
     * 执行方法
     * @param ctx 连接
     * @param command  请求命令
     * @return 返回
     */
    @Override
    public SendCmd execute(ChannelHandlerContext ctx, SendCmd command)
    {
        //收到要退出的通知
        return execute(ctx.channel(), command);
    }

    public static boolean isConnect(Channel channel)
    {
        if (channel==null) {
            return false;
        }
        return channel.isActive()||channel.isOpen();
    }

}
