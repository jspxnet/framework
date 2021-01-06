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
import com.github.jspxnet.network.rpc.model.route.RouteChannelManage;
import com.github.jspxnet.network.rpc.model.transfer.ChannelSession;
import com.github.jspxnet.network.rpc.env.RpcConfig;
import com.github.jspxnet.utils.DateUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ServerHandlerAdapter extends ChannelInboundHandlerAdapter {

    private static final SessionChannelManage sessionChannelManage = SessionChannelManage.getInstance();

    @Override
    public void channelActive(ChannelHandlerContext ctx)  {
        Channel channel = ctx.channel();
        ChannelSession channelSession = new ChannelSession();
        channelSession.setChannelId(channel.id());
        channelSession.setHeartbeatTimes(0);
        channelSession.setOnline(YesNoEnumType.YES.getValue());
        channelSession.setSocketAddress(channel.remoteAddress());
        sessionChannelManage.add(channelSession);
        sessionChannelManage.add(channel);

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

        ChannelSession netSession = sessionChannelManage.getSession(channel.id());
        if (netSession == null) {
            return;
        }
        sessionChannelManage.removeSession(channel.id());
        sessionChannelManage.remove(channel);
        channel.close();


    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

      //  log.debug("localAddress:{}--channelRead---remoteAddress:{}", ctx.channel().localAddress(), ctx.channel().remoteAddress());
        try {
            RpcInvokerFactory.invokeService(ctx, (String) msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    //接收完成
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();

    }

    //发生异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel channel = ctx.channel();
        if (channel == null) {
            System.out.println("channel is null");
            cause.printStackTrace();
            return;
        }
        if (!channel.isActive()) {
            System.out.println("SimpleClient:" + channel.remoteAddress() + "异常并关闭");
            channel.close();
        }
        log.debug("异常并关闭,用户主动退出:" + channel.remoteAddress());
        cleanSession(ctx.channel());
        ctx.disconnect(ctx.newPromise());
        ctx.close();
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
        ChannelSession netSession = sessionChannelManage.getSession(ctx.channel().id());
        if (netSession != null) {

            if (netSession.getHeartbeatTimes()==0)
            {
                SendCmd sendCmd = SendCommandFactory.createCommand(INetCommand.PING);
                ctx.channel().writeAndFlush(sendCmd);
            }
            netSession.setHeartbeatTimes(netSession.getHeartbeatTimes() + 1);
            if (netSession.getHeartbeatTimes()>1)
            {
                netSession.setOnline(YesNoEnumType.NO.getValue());
            }
            if (netSession.getHeartbeatTimes() >= 3 && System.currentTimeMillis() - netSession.getLastRequestTime() > DateUtil.SECOND * RpcConfig.getInstance().getTimeout())
            {
                cleanSession(ctx.channel());
            }
        } else {
            //session 为空的情况，直接删除
            cleanSession(ctx.channel());
         }
    }

}
