package com.easyiot.easylinker.rabbitmq.consumer;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author wwhai
 */
@Component
@RabbitListener(queuesToDeclare = @org.springframework.amqp.rabbit.annotation.Queue("client_charging"))
public class ClientChargingMQConsumer {
    /**
     * {
     * "canCharging":false,
     * "dataRows":"0",
     * "clientId":"989a61e9b168440996714cc444863b20",
     * "type":"CHARGING"
     * }
     * ----消息是个JSON串
     * canCharging：表示是否可扣费，也就是有没有余额
     * dataRows：缓存在redis等待落地更新的数据行
     * type：固定名称
     * clientId：当前的客户端
     * ----业务逻辑
     * 1 监听是否 canCharging
     * 2 如果true则放行消息
     * 3 如果否则更新数据并且落地 dataRows
     *
     *
     * @return
     */
    @Bean
    public Queue clientChargingQueue() {
        return new Queue("client_charging");
    }

    @RabbitHandler
    public void process(String message) {

    }
}
