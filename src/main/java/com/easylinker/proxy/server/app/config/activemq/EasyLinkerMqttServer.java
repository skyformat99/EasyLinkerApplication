package com.easylinker.proxy.server.app.config.activemq;

import com.easylinker.proxy.server.app.service.MqttRemoteClientService;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.util.IOExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

/**
 * 自己实现的AMQ的Mqtt服务器
 * # Mqtt port
 * easylinker.mqtt.server.port=1883
 * # Auth type
 * # 1:username
 * # 2:clientID
 * # 3:anonymous
 * easylinker.mqtt.server.auth=1
 * # Host
 * easylinker.mqtt.server.host=0.0.0.0
 */
@Component
public class EasyLinkerMqttServer extends BrokerService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public EasyLinkerMqttServer(
            @Value("${easylinker.mqtt.server.host}")
                    String host,
            @Value("${easylinker.mqtt.server.port}")
                    int port,
            @Value("${easylinker.mqtt.server.auth}")
                    int authType,
            MqttRemoteClientService service,
                                StringRedisTemplate stringRedisTemplate,
                                RedisTemplate redisTemplate)
            throws Exception {
        setPlugins(new BrokerPlugin[]{new AuthPluginInstaller(service, authType, stringRedisTemplate, redisTemplate)});
        setAdvisorySupport(false);
        setPersistent(false);
        TransportConnector connector = new TransportConnector();
        connector.setUri(new URI("mqtt://" + host + ":" + port));
        addConnector(connector);
        setIoExceptionHandler(new IOExceptionHandler() {
            @Override
            public void handle(IOException e) {
                logger.error(e.getMessage());

            }

            @Override
            public void setBrokerService(BrokerService brokerService) {

            }
        });

    }

}
