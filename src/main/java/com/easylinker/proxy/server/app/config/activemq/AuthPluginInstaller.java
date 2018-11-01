package com.easylinker.proxy.server.app.config.activemq;

import com.easylinker.proxy.server.app.service.MqttRemoteClientService;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthPluginInstaller implements BrokerPlugin {
    @Value("${easylinker.mqtt.server.auth}")
    int authType = 1;

    private MqttRemoteClientService service;

    @Autowired
    public AuthPluginInstaller(MqttRemoteClientService service) {
        this.service = service;
    }

    @Override
    public Broker installPlugin(Broker broker) throws Exception {
        return new AuthPluginBroker(broker, service, authType);
    }
}
