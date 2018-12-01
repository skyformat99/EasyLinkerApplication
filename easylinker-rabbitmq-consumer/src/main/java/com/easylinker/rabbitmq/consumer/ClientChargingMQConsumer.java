package com.easylinker.rabbitmq.consumer;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author wwhai
 */
@Component
@RabbitListener(queues = "client_charging")
public class ClientChargingMQConsumer {
    @Bean
    public Queue clientChargingQueue(){
        return new Queue("client_charging");
    }
    @RabbitHandler
    public void process(String msg) {
        System.out.println("ClientChargingMQConsumer:" + msg);

    }
}
