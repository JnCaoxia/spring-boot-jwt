package murraco.rocketmq;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(topic = "test1", consumerGroup = "my-consumer-group", selectorExpression = "tagA || tagB")
public class RocketMQConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        // 处理消息
        System.out.println("Received message: " + message);
    }
}
