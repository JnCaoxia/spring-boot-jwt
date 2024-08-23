package murraco.rocketmq;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class RocketMQProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    // 发送简单消息
    public void sendSimpleMessage(String topic, String message) {
        rocketMQTemplate.convertAndSend(topic, message);
    }

    // 发送带标签的消息
    public void sendTaggedMessage(String topic, String tag, String message) {
        rocketMQTemplate.convertAndSend(topic + ":" + tag, message);
    }

    // 发送带同步消息
    public void sendSyncMessage(String topic, String message) {
        rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(message).build());
    }

    // 发送事务消息
    public void sendTransactionalMessage(String topic, String message, Object arg) {
        rocketMQTemplate.sendMessageInTransaction(topic,
                MessageBuilder.withPayload(message).build(),
                arg);
    }

    // 发送延迟消息
    public void sendDelayMsg(String topic, String message, int delayLevel){
        rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(message).build(), 1000, delayLevel);

    }
}
