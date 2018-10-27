package com.easylinker.proxy.server.app.config.activemq;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthPluginInstaller implements BrokerPlugin {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public Broker installPlugin(Broker broker) throws Exception {
        return new AuthPluginBroker(broker);
    }
}
