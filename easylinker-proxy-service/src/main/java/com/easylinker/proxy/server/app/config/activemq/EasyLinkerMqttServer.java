package com.easylinker.proxy.server.app.config.activemq;

import com.easylinker.proxy.server.app.service.MqttRemoteClientService;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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

/**
 * <persistenceAdapter>
 * <replicatedLevelDB
 * directory="${activemq.data}/leveldb"
 * replicas="3"
 * bind="tcp://0.0.0.0:0"
 * zkAddress="ip1:2181,ip2:2181,ip3:2181"
 * hostname="192.168.199.23"
 * sync="local_disk"
 * zkPath="/activemq/leveldb-stores"
 * />
 * </persistenceAdapter>
 */
@Component
public class EasyLinkerMqttServer extends BrokerService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public EasyLinkerMqttServer(@Value("${easylinker.mqtt.server.host}")
                                        String host,
                                @Value("${easylinker.mqtt.server.port}")
                                        int port,
                                @Value("${easylinker.mqtt.server.auth}")
                                        int authType,
                                MqttRemoteClientService service,
                                StringRedisTemplate stringRedisTemplate,
                                RedisTemplate redisTemplate
    ) throws Exception {
        setPlugins(new BrokerPlugin[]{new AuthPluginInstaller(service, authType, stringRedisTemplate, redisTemplate)});
        /**
         * Activemq 的通知消息相关的资料在这里
         * http://activemq.apache.org/advisory-message.html
         */
        setAdvisorySupport(true);

        setPersistent(true);
        TransportConnector connector = new TransportConnector();
        connector.setUri(new URI("mqtt://" + host + ":" + port));
        addConnector(connector);
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

}
