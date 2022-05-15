package com.github.jspxnet.network.rpc.service;

/*
 * Created by jspx.net
 * <p>
 * author: chenYuan
 * date: 2020/6/9 22:21
 * description: jspbox
 **/
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.network.rpc.model.SendCommandFactory;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.transfer.ChannelSession;
import com.github.jspxnet.network.rpc.env.RpcConfig;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.IpUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Slf4j
public class ServerHandlerAdapter extends ChannelInboundHandlerAdapter {

    private static final SessionChannelManage SESSION_CHANNEL_MANAGE = SessionChannelManage.getInstance();

    @Override
    public void channelActive(ChannelHandlerContext ctx)  {
        Channel channel = ctx.channel();
        ChannelSession channelSession = new ChannelSession();
        channelSession.setChannelId(channel.id());
        channelSession.setHeartbeatTimes(0);
        channelSession.setOnline(YesNoEnumType.YES.getValue());
        channelSession.setSocketAddress(channel.remoteAddress());
        SESSION_CHANNEL_MANAGE.add(channelSession);
        SESSION_CHANNEL_MANAGE.add(channel);

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }

    /**
     *
     *
     * @param channel 清理掉线，过期的session,包括房间信息
     */
    static private void cleanSession(Channel channel) {
        if (channel == null) {
            return;
        }

        ChannelSession netSession = SESSION_CHANNEL_MANAGE.getSession(channel.id());
        if (netSession == null) {
            return;
        }
        SESSION_CHANNEL_MANAGE.removeSession(channel.id());
        SESSION_CHANNEL_MANAGE.remove(channel);

        channel.pipeline().remove(LengthFieldBasedFrameDecoder.class);
        channel.pipeline().remove(LengthFieldPrepender.class);
        channel.pipeline().remove(StringDecoder.class);
        channel.pipeline().remove(StringEncoder.class);
        channel.pipeline().remove(IdleStateHandler.class);
        channel.pipeline().close();
        channel.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (ctx==null|| ObjectUtil.isEmpty(msg))
        {
            return;
        }
        try {
            RpcInvokerFactory.invokeService(ctx, (String) msg);
            ctx.flush();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("channelRead 发送数据异常",e);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    //接收完成
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        if (ctx==null)
        {
            return;
        }
        ctx.flush();
        //这里不能关闭,因为如果数据量大的情况,会多次进入这里读取数据
    }

    //发生异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel channel = ctx.channel();
        if (channel == null) {
            log.debug("channel is null :{}", channel);
            cause.printStackTrace();
            return;
        }
        try {
            cleanSession(ctx.channel());
            ctx.disconnect(ctx.newPromise());
            ctx.close();
        } catch (Exception e)
        {
            //...
        }
        if (cause instanceof IOException)
        {
            log.debug("对方主动退出:{}",IpUtil.getIp(channel.remoteAddress()));
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    //一直没有读取
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    //一直没有写
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    //没有读也没有写,说明掉线了
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }

    }

    //一直没有读,主动发出心跳请求
    private void handleReaderIdle(ChannelHandlerContext ctx) {
        // System.err.println("---READER_IDLE---长期没有读");

    }

    private void handleWriterIdle(ChannelHandlerContext ctx) {
//        System.err.println("---WRITER_IDLE---长期没有写");

    }

    private void handleAllIdle(ChannelHandlerContext ctx) {
        //主动发出心跳请求
        if (ctx==null)
        {
            return;
        }
        Channel channel = ctx.channel();
        if (channel==null)
        {
            return;
        }
        ChannelSession netSession = SESSION_CHANNEL_MANAGE.getSession(channel.id());
        if (netSession != null) {

            if (netSession.getHeartbeatTimes()==0)
            {
                SendCmd sendCmd = SendCommandFactory.createCommand(INetCommand.PING);
                channel.writeAndFlush(sendCmd);
            }
            netSession.setHeartbeatTimes(netSession.getHeartbeatTimes() + 1);
            if (netSession.getHeartbeatTimes()>1)
            {
                netSession.setOnline(YesNoEnumType.NO.getValue());
            }
            final long waitTime = StringUtil.toLong(DateUtil.SECOND * RpcConfig.getInstance().getTimeout()+"");
            if (netSession.getHeartbeatTimes() >= 3 && System.currentTimeMillis() - netSession.getLastRequestTime() > waitTime)
            {
                cleanSession(channel);
            }
        } else {
            //session 为空的情况，直接删除
            cleanSession(channel);
         }
    }

}
