package com.easylinker.rabbitmq.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 *
 * @Author:   zhaolei
 * @Version   1.0.0
 *
 * @Description: RabbitMQ 消费者测试
 * @Date:     2018/11/28 19:55
 * Copyright (C), 2016-2018, EasyLinker V3
 */

@Component
@RabbitListener(queues = "TestRabbit")
public class HelloRabbitMQConsumer {
    @RabbitHandler
    public void process(String msg){
        System.out.println("Consumer:" + msg);
    }
}
