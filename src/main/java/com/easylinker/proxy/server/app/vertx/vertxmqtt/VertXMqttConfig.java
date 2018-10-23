package com.easylinker.proxy.server.app.vertx.vertxmqtt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VertXMqttConfig {
    //默认端口1883
    @Value("${vertx.mqtt.server.port}")
    private int port = 1883;
    //默认关闭匿名连接模式
    @Value("${vertx.mqtt.server.anonymous}")
    private Boolean anonymous = false;
    //默认是username认证
    //支持username clientId两种形式的认证
    @Value("${vertx.mqtt.server.auth}")
    private String auth = "username";

    @Value("${vertx.mqtt.server.host}")
    private String host = "0.0.0.0";

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }


    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

}
