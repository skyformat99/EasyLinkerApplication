package com.easylinker.proxy.server.app.dao;


import com.easylinker.proxy.server.app.model.mqtt.MqttRemoteClient;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MqttRemoteClientRepository extends MongoRepository<MqttRemoteClient, Long> {
    MqttRemoteClient findTopByUsernameAndPassword(String username, String password);

    MqttRemoteClient findTopByClientId(String clientId);

    List<MqttRemoteClient> findAllByOnLine(Boolean online);
}
