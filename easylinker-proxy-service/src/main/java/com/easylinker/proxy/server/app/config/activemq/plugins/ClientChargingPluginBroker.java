package com.easylinker.proxy.server.app.config.activemq.plugins;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerFilter;

/**
 * mqtt消息计费核心拦截器
 * @author wwhai
 */
public class ClientChargingPluginBroker extends BrokerFilter {
    public ClientChargingPluginBroker(Broker next) {
        super(next);
    }
}
