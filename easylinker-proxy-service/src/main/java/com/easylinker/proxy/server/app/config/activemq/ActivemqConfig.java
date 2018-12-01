package com.easylinker.proxy.server.app.config.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

/**
 * @author mac
 */
@Configuration
public class ActivemqConfig {


    private static String BROKER_URL = "failover:(tcp://127.0.0.1:61616)";


    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerQueue() {
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();

        bean.setConnectionFactory(new ActiveMQConnectionFactory(BROKER_URL));
        return bean;
    }

    @Bean
    public JmsMessagingTemplate jmsMessagingTemplate() {
        return new JmsMessagingTemplate(new ActiveMQConnectionFactory(BROKER_URL));
    }


    @Bean
    public ConnectionFactory connectionFactory(){
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(BROKER_URL);
        return connectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate(){
        return new JmsTemplate(connectionFactory());
    }
}

