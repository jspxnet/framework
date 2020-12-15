package sdk.rocket;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

public class Producer {
    //http://www.tianshouzhi.com/api/tutorials/rocketmq/414
    public static void main(String[] args) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer("my-group-name-A");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();
        Message message = new Message("topic-name-A","tag-name-A","Message : My blog address guozh.net".getBytes());
        producer.send(message);
        System.out.println("Message sended");
        producer.shutdown();
    }
}
