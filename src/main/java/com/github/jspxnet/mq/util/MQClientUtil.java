package com.github.jspxnet.mq.util;

import org.apache.rocketmq.client.ClientConfig;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.MQClientAPIImpl;
import org.apache.rocketmq.client.impl.MQClientManager;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * @date: 2020/12/22 0:54
 * @description: jspbox
 **/
public class MQClientUtil {

    public static MQClientInstance getMQClientInstance(ClientConfig producer) throws MQClientException {
        MQClientManager mqClientManager = MQClientManager.getInstance();
        MQClientInstance mqClientInstance = mqClientManager.getOrCreateMQClientInstance(producer);
        mqClientInstance.start();
        return mqClientInstance;
    }

    public static MQClientAPIImpl getMQClientAPI(ClientConfig producer) throws MQClientException {
        return getMQClientInstance(producer).getMQClientAPIImpl();
    }
}
