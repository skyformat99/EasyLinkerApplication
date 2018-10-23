package com.easylinker.proxy.server.app.vertx.vertxmqtt;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

/**
 * Mqtt 服务器启动辅助器
 */
public class MqttServerRunner {
    public static Verticle run(Verticle verticle){

        Vertx.vertx().deployVerticle(verticle);

        return verticle;

    }

    public static void main(String[] args) {
        run(new VertXMqttServer());
    }
}
