package com.easyiot.easylinker.service.proxy.dao;


import com.easyiot.easylinker.service.proxy.model.mqtt.MqttRemoteClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MqttRemoteClientRepository extends MongoRepository<MqttRemoteClient, Long> {
    MqttRemoteClient findTopByUsernameAndPassword(String username, String password);

    MqttRemoteClient findTopById(Long id);

    MqttRemoteClient findTopByClientId(String clientId);

    Page<MqttRemoteClient> findAllByUserId(Long userID, Pageable pageable);
}
