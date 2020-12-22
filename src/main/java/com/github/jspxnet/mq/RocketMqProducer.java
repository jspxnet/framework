package com.github.jspxnet.mq;


import com.github.jspxnet.sioc.annotation.Destroy;
import com.github.jspxnet.sioc.annotation.Init;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;


/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/12/21 21:06
 * description: mq消息发送端
 * 单例模式
 **/

@Slf4j
public class RocketMqProducer extends DefaultMQProducer {
    @Init
    public void init() {
        try {
            start();
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("RocketMq 客户端启动失败", e);
        }
    }

    @Destroy
    public void destroy() {
        super.shutdown();
    }

}
