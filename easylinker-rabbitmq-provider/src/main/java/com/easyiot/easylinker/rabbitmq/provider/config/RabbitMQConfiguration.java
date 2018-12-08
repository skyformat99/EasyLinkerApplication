package com.easyiot.easylinker.rabbitmq.provider.config;


import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @Author:   zhaolei
 * @Version   1.0.0
 *
 * @Description: 测试RabbitMQ, 创建一个队列
 * @Date:     2018/11/28 18:59
 * Copyright (C), 2016-2018, EasyLinker V3
 */

@Configuration
public class RabbitMQConfiguration {

    @Bean
    public Queue testRabbitQueue(){
        return new Queue("TestRabbit");
    }

    @Bean
    public Queue clientChargingQueue(){
        return new Queue("client_charging");
    }
}
