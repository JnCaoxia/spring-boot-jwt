package murraco.configuration;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMQConfig {

    @Value("${rocketmq.producer.group}")
    private String producerGroup;

    @Value("${rocketmq.name-server}")
    private String namesrvAddr;

//    @Bean
//    public DefaultMQProducer defaultMQProducer() {
//        DefaultMQProducer producer = new DefaultMQProducer("my-producer-group");
//        producer.setNamesrvAddr("127.0.0.1:9876");
//        producer.setSendMsgTimeout(3000); // 设置消息发送超时时间
//        return producer;
//    }

    @Bean
    public TransactionMQProducer transactionMQProducer() throws MQClientException {
        TransactionMQProducer producer = new TransactionMQProducer(producerGroup);
        producer.setNamesrvAddr(namesrvAddr);
        producer.setSendMsgTimeout(30000);
        // 你可以在这里设置其他的事务相关配置
        return producer;
    }

    @Bean
    public RocketMQTemplate rocketMQTemplate(TransactionMQProducer producer) {
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        rocketMQTemplate.setProducer(producer);

        // 设置命名空间
//        producer.setNamespace("my-namespace");
        // 设置消息轨迹

        return rocketMQTemplate;
    }
}
