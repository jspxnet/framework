package com.github.jspxnet.network.rpc.client;

import com.github.jspxnet.json.GsonUtil;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.*;

/**
 * Created by jspx.net
 * <p>
 * author: chenYuan
 * date: 2020/6/11 21:29
 * description: jspbox
 **/
@Slf4j
public class ClientHandlerAdapter extends ChannelInboundHandlerAdapter {

    public ClientHandlerAdapter() {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.channel().read();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof String)) {
            log.info("接收到非法数据");
            return;
        }
        String jsonStr = INetCommand.getDecodePacket((String) msg);
        if (StringUtil.isEmpty(jsonStr)||StringUtil.isNull(jsonStr)) {
            log.info("接收到非法数据,解密不能识别");
            return;
        }
        SendCmd reply = GsonUtil.createGson().fromJson(jsonStr,SendCmd.class);
        if (reply == null || StringUtil.isNull(reply.getId())) {
            log.info("无调用ID");
            return;
        }
        //接收到的数据都放在这里
        ResultHashMap resultHashMap = ResultHashMap.getInstance();
        ArrayBlockingQueue<SendCmd> queue = resultHashMap.get(reply.getId());
        if (queue == null) {
            log.info("队列中无此ID:{}", ObjectUtil.toString(reply));
            return;
        }
        try {
            queue.put(reply);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            resultHashMap.remove(reply.getId());
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }


    //一直没有读,主动发出心跳请求
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        //log.info("直没有读,主动发出心跳请求");


    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        //log.info("直没有写,主动发出心跳请求");

    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
        //log.info("定时发出心跳:"+ctx.channel().remoteAddress());


    }

}
