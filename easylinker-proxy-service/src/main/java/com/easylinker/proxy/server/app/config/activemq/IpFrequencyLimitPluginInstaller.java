package com.easylinker.proxy.server.app.config.activemq;

import com.easylinker.proxy.server.app.config.redis.RedisService;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;

/**
 * 基于Redis实现的IP频率控制
 *
 * @author wwhai
 */
public class IpFrequencyLimitPluginInstaller implements BrokerPlugin {
    private final RedisService redisService;

    public IpFrequencyLimitPluginInstaller(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public Broker installPlugin(Broker broker) {
        return new IpFrequencyLimitPluginBroker(broker, redisService);
    }
}
