package com.github.jspxnet.mq.util;

import org.apache.rocketmq.client.ClientConfig;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.MQClientManager;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/12/22 0:54
 * description: jspbox
 **/
public class MQClientUtil {

    /**
     * 得到mq的高级功能,一般用得不多
     *  用完记得关闭
     *  getMQClientInstance(producer).getMQClientAPIImpl();
     * @param producer 可以是消息生产这
     * @return 客户端接口, 主要是得到 MQClientAPIImpl
     * @throws MQClientException 异常
     */
    public static MQClientInstance getMqClientInstance(ClientConfig producer) throws MQClientException {
        MQClientManager mqClientManager = MQClientManager.getInstance();
        MQClientInstance mqClientInstance = mqClientManager.getOrCreateMQClientInstance(producer);
        mqClientInstance.start();
        return mqClientInstance;
    }


}
