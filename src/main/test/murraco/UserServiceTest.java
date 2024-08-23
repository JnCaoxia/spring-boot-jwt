package murraco;

import lombok.extern.slf4j.Slf4j;
import murraco.model.AppUser;
import murraco.model.dto.AppUserDTO;
import murraco.rocketmq.RocketMQProducer;
import murraco.service.UserDataService;
import org.apache.rocketmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserServiceTest {

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private RocketMQProducer rocketMQProducer;

    @Test
    public void testGetUserById(){
        AppUserDTO user = userDataService.getUserById(1);
        log.info("user:{}", user);
    }

    @Test
    public void testSaveUser() {
        AppUser user = userDataService.createUser("nazi", "123@qq.com");
        log.info("testSaveUser:{}", user);
    }

    @Test
    public void testRocketProducer(){
        rocketMQProducer.sendTaggedMessage("OrderTopic", "tagA", "nazi666-888");
    }

    @Test
    public void testRocketMQSendTransactionMsg(){
        rocketMQProducer.sendTransactionalMessage("test1:tagA", "nazi888-000-fail", "this is arg 888");
    }

    @Test
    public void testDelayMsg(){
        rocketMQProducer.sendDelayMsg("test1:tagA", "delay msg", 3);
    }


}
