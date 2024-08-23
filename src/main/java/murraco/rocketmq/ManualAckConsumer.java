package murraco.rocketmq;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.support.RocketMQConsumerLifecycleListener;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RocketMQMessageListener(
    topic = "OrderTopic", 
    consumerGroup = "manual_ack_consumer_group",
    selectorExpression = "tagA || tagB",
    consumeMode = ConsumeMode.CONCURRENTLY // 或者 ORDERLY
)
public class ManualAckConsumer implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void onMessage(MessageExt message) {
        // 这里不会直接处理消息
    }

    @Override
    public void prepareStart(org.apache.rocketmq.client.consumer.DefaultMQPushConsumer consumer) {
        // 注册手动ACK监听器
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            try {
                for (MessageExt msg : msgs) {
                    // 处理消息
                    System.out.printf("Processing message: %s%n", new String(msg.getBody()));

                    // 假设这里是业务处理代码
                    // 处理成功后，手动ACK
                    context.setAckIndex(msgs.size() - 1); // 表示所有消息都被成功处理
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS; // 返回消费成功状态
            } catch (Exception e) {
                // 处理异常，返回稍后重新消费
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
    }
}
