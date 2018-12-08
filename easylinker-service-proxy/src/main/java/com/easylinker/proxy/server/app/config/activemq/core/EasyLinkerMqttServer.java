package com.easylinker.proxy.server.app.config.activemq.core;

import com.easylinker.proxy.server.app.config.activemq.installer.AuthPluginInstaller;
import com.easylinker.proxy.server.app.config.activemq.installer.IpFrequencyLimitPluginInstaller;
import com.easylinker.proxy.server.app.config.redis.RedisService;
import com.easylinker.proxy.server.app.service.ClientDataEntryService;
import com.easylinker.proxy.server.app.service.MqttRemoteClientService;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 自己实现的AMQ的Mqtt服务器
 * # Auth type
 * # 1:username
 * # 2:clientID
 * # 3:anonymous
 *
 * @author mac
 */
@Component
public class EasyLinkerMqttServer extends BrokerService implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public EasyLinkerMqttServer(@Value("${easylinker.mqtt.server.host}")
                                        String host,
                                @Value("${easylinker.mqtt.server.port}")
                                        int port,
                                @Value("${easylinker.mqtt.server.auth}")
                                        int authType,
                                MqttRemoteClientService service,
                                StringRedisTemplate stringRedisTemplate,
                                RedisService redisService,
                                ClientDataEntryService clientDataEntryService,
                                AmqpTemplate amqpTemplate) throws Exception {
        setPlugins(new BrokerPlugin[]{new AuthPluginInstaller(service, authType, stringRedisTemplate, clientDataEntryService, amqpTemplate),
                new IpFrequencyLimitPluginInstaller(redisService)});
        /**
         * Activemq 的通知消息相关的资料在这里
         * http://activemq.apache.org/advisory-message.html
         */
        setAdvisorySupport(false);
        setPersistent(false);
        /**
         * 开启MQTT支持
         */
        TransportConnector mqttConnector = new TransportConnector();
        mqttConnector.setUri(new URI("mqtt://" + host + ":" + port));

        /**
         * 开启TCP支持,主要用在内部消息通信
         */
        TransportConnector tcpConnector = new TransportConnector();
        tcpConnector.setUri(new URI("tcp://" + host + ":" + 61616));

        addConnector(mqttConnector);
        addConnector(tcpConnector);
        setBrokerName("EasyLinkerMqttServer");
        setDataDirectory("./activemq-data");
        List<PolicyEntry> policyEntryList = new ArrayList<>();
        PolicyEntry policyEntry = new PolicyEntry();
        policyEntry.setTopic(">");
        policyEntry.setAdvisoryForConsumed(true);
        policyEntryList.add(policyEntry);
        PolicyMap policyMap = new PolicyMap();
        policyMap.setPolicyEntries(policyEntryList);
        setDestinationPolicy(policyMap);
        

    }

    @Override
    public void start() throws Exception {
        super.start();
        logger.info("MQTT server started !");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }
}
