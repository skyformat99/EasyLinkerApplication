package com.easylinker.proxy.server.app.vertx.vertxmqtt.client.dao;


import com.easylinker.proxy.server.app.vertx.vertxmqtt.client.model.VertXMqttRemoteClient;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VertXMqttRemoteClientRepository extends MongoRepository<VertXMqttRemoteClient, Long> {
    VertXMqttRemoteClient findTopByUsernameAndPassword(String username, String password);

    VertXMqttRemoteClient findTopByClientId(String clientId);

    List<VertXMqttRemoteClient> findAllByOnLine(Boolean online);
}
