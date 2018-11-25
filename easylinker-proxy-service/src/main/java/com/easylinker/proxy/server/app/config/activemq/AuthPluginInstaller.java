package com.easylinker.proxy.server.app.config.activemq;

import com.easylinker.proxy.server.app.service.ClientDataEntryService;
import com.easylinker.proxy.server.app.service.MqttRemoteClientService;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author mac
 */
public class AuthPluginInstaller implements BrokerPlugin {

    private int authType;
    private MqttRemoteClientService service;

    private StringRedisTemplate stringRedisTemplate;

    private ClientDataEntryService clientDataEntryService;


    AuthPluginInstaller(MqttRemoteClientService service, int authType, StringRedisTemplate stringRedisTemplate, ClientDataEntryService clientDataEntryService) {
        this.service = service;
        this.stringRedisTemplate = stringRedisTemplate;
        this.authType = authType;
        this.clientDataEntryService = clientDataEntryService;
    }

    @Override
    public Broker installPlugin(Broker broker) {
        return new AuthPluginBroker(broker, service, authType, stringRedisTemplate,clientDataEntryService);
    }
}
