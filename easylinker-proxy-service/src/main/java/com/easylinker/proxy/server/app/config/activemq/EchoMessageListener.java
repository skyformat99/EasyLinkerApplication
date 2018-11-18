package com.easylinker.proxy.server.app.config.activemq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class EchoMessageListener {
    /**
     * 监听方
     */
    //监听注解
    @JmsListener(destination = ".system.echo")
    public void getQueue(String info) {
        System.out.println("Echo message:" + info);
    }


}
