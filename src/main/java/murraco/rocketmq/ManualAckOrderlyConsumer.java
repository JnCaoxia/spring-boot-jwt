package murraco.rocketmq;

import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(
    topic = "OrderTopic", 
    consumerGroup = "manual_ack_orderly_group",
    selectorExpression = "tagA || tagB",
    consumeMode = ConsumeMode.ORDERLY
)
public class ManualAckOrderlyConsumer implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void onMessage(MessageExt message) {
        // 这里不会直接处理消息
    }

    @Override
    public void prepareStart(org.apache.rocketmq.client.consumer.DefaultMQPushConsumer consumer) {
        // 注册顺序消费手动ACK监听器
        consumer.registerMessageListener((MessageListenerOrderly) (msgs, context) -> {
            try {
                for (MessageExt msg : msgs) {
                    // 处理消息
                    System.out.printf("Processing orderly message: %s%n", new String(msg.getBody()));

                    // 假设这里是业务处理代码
                }
                return ConsumeOrderlyStatus.SUCCESS; // 返回消费成功状态
            } catch (Exception e) {
                // 处理异常，返回稍后重新消费
                return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
            }
        });
    }
}
