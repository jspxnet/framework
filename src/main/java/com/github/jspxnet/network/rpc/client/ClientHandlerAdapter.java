package com.github.jspxnet.network.rpc.client;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.rpc.model.cmd.SendCmd;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
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
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //log.debug("--------------channelActive=" + ctx.channel());
        ctx.channel().read();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (!(msg instanceof String)) {
            log.debug("接收到非法数据");
            return;
        }
        String jsonStr = INetCommand.getDecodePacket((String) msg);

        if (StringUtil.isNull(jsonStr)) {
            log.error("接收到非法数据,解密不能识别");
            return;
        }
        JSONObject json = new JSONObject(jsonStr);
        SendCmd reply = json.parseObject(SendCmd.class);
        if (reply == null || StringUtil.isNull(reply.getId())) {
            log.debug("无调用ID");
            return;
        }

        //接收到的数据都放在这里
        ResultHashMap resultHashMap = ResultHashMap.getInstance();
        ArrayBlockingQueue<SendCmd> queue = resultHashMap.get(reply.getId());
        if (queue == null) {
            log.debug("队列中无此ID:{}", ObjectUtil.toString(reply));
            return;
        }
        try {
            queue.put(reply);
            resultHashMap.remove(reply.getId());
        } catch (InterruptedException e) {
            e.printStackTrace();
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
