package com.easyiot.easylinker.rabbitmq.provider.provider;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Date;


/**
 *
 * @Author:   zhaolei
 * @Version   1.0.0
 *
 * @Description: 测试创建一个消息生产者
 * @Date:     2018/11/28 19:24
 * Copyright (C), 2016-2018, EasyLinker V3
 */

@Component
public class HelloRabbitMQProvider {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void send(int i) {
        String data = "第"+i+"条消息："+"Hello," + DateFormat.getTimeInstance().format(new Date());
        System.out.println(data);
        amqpTemplate.convertAndSend("TestRabbit", data);

    }

    public void sendClientCharging(int i) {
        String data = "第"+i+"条消息："+"Hello," + DateFormat.getTimeInstance().format(new Date());
        System.out.println(data);
        amqpTemplate.convertAndSend("client_charging", data);
    }

}
