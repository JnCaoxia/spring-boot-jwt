package murraco.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RocketMQTransactionListener
@Slf4j
public class TransactionListenerImpl implements RocketMQLocalTransactionListener {

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        byte[] payload = (byte[]) msg.getPayload();
        String content = new String(payload, StandardCharsets.UTF_8);
        log.info("executeLocalTransaction msg:{}, org:{}, content:{}", msg.getPayload(), arg, content);
        // 执行本地事务逻辑
        try {
            // 本地事务逻辑成功
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            // 本地事务逻辑失败
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        log.info("checkLocalTransaction payload:{}", msg.getPayload());
        // 检查本地事务状态
        return RocketMQLocalTransactionState.COMMIT;
    }
}
