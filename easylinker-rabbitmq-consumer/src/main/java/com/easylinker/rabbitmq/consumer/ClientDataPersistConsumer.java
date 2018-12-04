package com.easylinker.rabbitmq.consumer;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 持久化消息消费者
 * 只要接受到消息就直接入库
 *
 * @author wwhai
 */
@Component
@RabbitListener(queuesToDeclare = @org.springframework.amqp.rabbit.annotation.Queue("client_data_persist"))
public class ClientDataPersistConsumer {
    @Bean
    public Queue clientChargingQueue() {
        return new Queue("client_charging");
    }

    @RabbitHandler
    public void process(String message) {

    }
}
