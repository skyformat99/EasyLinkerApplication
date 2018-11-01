package com.easylinker.proxy.server.app.config.activemq;

import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.net.URI;

/**
 * 自己实现的AMQ的Mqtt服务器
 */
@Component
public class EasyLinkerMqttServer extends BrokerService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public EasyLinkerMqttServer() throws Exception {
        logger.info("加载插件......");
        setPlugins(new BrokerPlugin[]{new AuthPluginInstaller()});
        setAdvisorySupport(false);
        setPersistent(false);


        TransportConnector connector = new TransportConnector();
        connector.setUri(new URI("mqtt://localhost:1883"));

        addConnector(connector);



    }

//    public static void main(String[] args) throws Exception {
//        EasyLinkerMqttServer easyLinkerMqttServer = new EasyLinkerMqttServer();
//        try {
//            easyLinkerMqttServer.start();
//        } catch (Exception e) {
//            System.out.println("----------------");
//        }
//    }


}
