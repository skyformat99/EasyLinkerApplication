package com.easylinker.proxy.server.app.config.activemq;

import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.util.IOExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 自己实现的AMQ的Mqtt服务器
 */
@Component
public class EasyLinkerMqttServer extends BrokerService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public EasyLinkerMqttServer() {
        logger.info("加载插件......");
        setPlugins(new BrokerPlugin[]{new AuthPluginInstaller()});
        setAdvisorySupport(false);
        setPersistent(false);

        setIoExceptionHandler(new IOExceptionHandler() {
            @Override
            public void handle(IOException e) {
                System.out.println("异常");
            }

            @Override
            public void setBrokerService(BrokerService brokerService) {
                System.out.println("setBrokerService");


            }
        });


        setTransportConnectorURIs(new String[]{"mqtt://0.0.0.0:1883?wireFormat.maxInactivityDuration=30000&amp;wireFormat.maxInactivityDurationInitalDelay=10000"});

    }

    public static void main(String[] args) throws Exception {
        EasyLinkerMqttServer easyLinkerMqttServer = new EasyLinkerMqttServer();
        easyLinkerMqttServer.start();
    }


}
