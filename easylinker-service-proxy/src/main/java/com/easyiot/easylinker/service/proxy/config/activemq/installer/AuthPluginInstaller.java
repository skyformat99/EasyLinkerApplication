package com.easyiot.easylinker.service.proxy.config.activemq.installer;

import com.easyiot.easylinker.service.proxy.config.activemq.plugins.ClientAuthPlugin;
import com.easyiot.easylinker.service.proxy.service.ClientDataEntryService;
import com.easyiot.easylinker.service.proxy.service.MqttRemoteClientService;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author wwhai
 */
public class AuthPluginInstaller implements BrokerPlugin {
    private final AmqpTemplate amqpTemplate;

    private int authType;
    private MqttRemoteClientService service;

    private StringRedisTemplate stringRedisTemplate;

    private ClientDataEntryService clientDataEntryService;


    @Autowired
    public AuthPluginInstaller(MqttRemoteClientService service, int authType, StringRedisTemplate stringRedisTemplate, ClientDataEntryService clientDataEntryService, AmqpTemplate amqpTemplate) {
        this.service = service;
        this.stringRedisTemplate = stringRedisTemplate;
        this.authType = authType;
        this.clientDataEntryService = clientDataEntryService;
        this.amqpTemplate = amqpTemplate;
    }

    @Override
    public Broker installPlugin(Broker broker) {
        return new ClientAuthPlugin(broker, service, authType, stringRedisTemplate, clientDataEntryService,amqpTemplate);
    }
}
