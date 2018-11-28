package com.easylinker.rabbitmq.provider;

import com.easylinker.rabbitmq.provider.provider.HelloRabbitMQProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitMQTest {

    @Autowired
    private HelloRabbitMQProvider provider;

    @Test
    public void testSender(){
        for (int i = 0; i < 1000; i++) {
            provider.send(i);
        }

    }

}
