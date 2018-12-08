package com.easyiot.easylinker.service.proxy.config.activemq.installer;

import com.easyiot.easylinker.service.proxy.config.redis.RedisService;
import com.easyiot.easylinker.service.proxy.config.activemq.plugins.IpFrequencyLimitPlugin;
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
        return new IpFrequencyLimitPlugin(broker, redisService);
    }
}
