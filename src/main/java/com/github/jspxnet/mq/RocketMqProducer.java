package com.github.jspxnet.mq;


import com.github.jspxnet.sioc.annotation.Destroy;
import com.github.jspxnet.sioc.annotation.Init;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import lombok.extern.slf4j.Slf4j;



/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2020/12/21 21:06
 * description: mq消息发送端
 * 单例模式
 **/

@Slf4j
public class RocketMqProducer {
    private Object defaultMQProducer = null;

    public Object getDefaultMQProducer()
    {
        return defaultMQProducer;
    }
    @Init
    public void init() {
        try {
            if (ClassUtil.hasClass("org.apache.rocketmq.client.producer.DefaultMQProducer"))
            {
                defaultMQProducer = ClassUtil.newInstance("org.apache.rocketmq.client.producer.DefaultMQProducer");
            }
            if (defaultMQProducer!=null)
            {
                BeanUtil.invoke(defaultMQProducer,"start");
            }
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("RocketMq 客户端启动失败", e);
        }
    }

    @Destroy
    public void destroy() {
        if (defaultMQProducer!=null)
        {
            try {
                BeanUtil.invoke(defaultMQProducer,"shutdown");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
