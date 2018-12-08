package com.easylinker.proxy.server.app.config.rabbitmq;


import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    @Bean
    public Queue clientChargingQueue(){
        return new Queue("clientCharging");
    }
}
