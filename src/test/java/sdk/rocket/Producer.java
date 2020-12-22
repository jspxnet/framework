package sdk.rocket;

import com.github.jspxnet.utils.ObjectUtil;
import org.apache.rocketmq.client.ClientConfig;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.MQClientAPIImpl;
import org.apache.rocketmq.client.impl.MQClientManager;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.protocol.body.TopicList;
import org.apache.rocketmq.remoting.exception.RemotingException;

public class Producer {
    //http://www.tianshouzhi.com/api/tutorials/rocketmq/414
    public static void main(String[] args) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {




        DefaultMQProducer producer = new DefaultMQProducer("jspx-net","my-group-name-A");
        producer.setNamesrvAddr("192.168.0.200:9876");
        producer.start();
        MQClientManager mqClientManager = MQClientManager.getInstance();
        MQClientInstance mqClientInstance = mqClientManager.getOrCreateMQClientInstance(producer);
        mqClientInstance.start();

        MQClientAPIImpl api = mqClientInstance.getMQClientAPIImpl();
        TopicList topicList = api.getTopicListFromNameServer(1000);
        System.out.println(ObjectUtil.toString(mqClientInstance.getClientId()));
        System.out.println(ObjectUtil.toString(topicList));
       // producer.start();
        for (int i=0;i<100;i++)
        {
            Message message = new Message("topic-name-A","tag-name-A",("Message : my message " + i).getBytes());
            producer.sendOneway(message);
        }
        System.out.println("Message sended");
        producer.shutdown();
    }
}
