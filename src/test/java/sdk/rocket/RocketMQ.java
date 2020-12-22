package sdk.rocket;

import com.github.jspxnet.txweb.util.RequestUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import java.util.List;

public class RocketMQ {

    public static void main(String[] args) throws MQClientException {
//这里填写group名字
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("jspx-net","my-group-name-A");
//NameServer地址

        consumer.setNamesrvAddr("192.168.0.200:9876");
        //1：topic名字 2：tag名字
        consumer.subscribe("topic-name-A", "tag-name-A");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(
                    List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {

                    System.out.println("接收到消息:"+new String(msg.getBody()));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.out.println("Consumer Started!");
    }
}
