package com.easylinker.proxy.server.app.config.activemq.plugins;

import com.easylinker.proxy.server.app.config.redis.RedisService;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerFilter;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author wwhai
 * 频率拦截器
 */
public class IpFrequencyLimitPluginBroker extends BrokerFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 同一个IP 5秒内不能请求10次
     */
    private static final int LIMIT = 10;
    private final RedisService redisService;

    public IpFrequencyLimitPluginBroker(Broker next, RedisService redisService) {
        super(next);
        this.redisService = redisService;
    }

    @Override
    public void send(ProducerBrokerExchange producerExchange, Message messageSend) throws Exception {
        String ip = producerExchange.getConnectionContext().getConnection().getRemoteAddress();
        String ipCount = redisService.get(ip);
        if (ipCount == null) {
            logger.error("频率拦截器通过:[" + ip + "]!");
            redisService.setExpires(ip, "1", 5L, TimeUnit.SECONDS);
        } else if (Integer.valueOf(ipCount) > LIMIT) {
            logger.error("频率拦截器拒绝:[" + ip + "]因为该IP请求频率超过最大值!");
            //throw new SecurityException("ACL拒绝:[" + ip + "]因为该IP请求频率太高!");
        } else {
            redisService.increment(ip, 1L);
        }
        super.send(producerExchange, messageSend);

    }
}
