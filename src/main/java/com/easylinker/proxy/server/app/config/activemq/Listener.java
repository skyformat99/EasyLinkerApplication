package com.easylinker.proxy.server.app.config.activemq;

import org.apache.activemq.transport.TransportListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class Listener implements TransportListener {
    @Override
    public void onCommand(Object o) {

    }

    @Override
    public void onException(IOException e) {

    }

    @Override
    public void transportInterupted() {

    }

    @Override
    public void transportResumed() {

    }
}
