package com.easylinker.rabbitmq.consumer.service;

import com.easylinker.rabbitmq.consumer.dao.MqttRemoteClientRepository;
import com.easylinker.rabbitmq.consumer.model.mqtt.MqttRemoteClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MqttRemoteClientService implements BaseService<MqttRemoteClient> {
    private final
    MqttRemoteClientRepository mqttRemoteClientRepository;

    @Autowired
    public MqttRemoteClientService(MqttRemoteClientRepository mqttRemoteClientRepository) {
        this.mqttRemoteClientRepository = mqttRemoteClientRepository;
    }

    @Override
    public void save(MqttRemoteClient mqttRemoteClient) {
        this.mqttRemoteClientRepository.save(mqttRemoteClient);

    }

    @Override
    public void delete(MqttRemoteClient mqttRemoteClient) {

        mqttRemoteClientRepository.delete(mqttRemoteClient);
    }

    public void delete(Long id) {
        mqttRemoteClientRepository.deleteById(id);

    }

    public void delete(Long ids[]) {
        for (Long l : ids)
            mqttRemoteClientRepository.deleteById(l);

    }

    @Override
    public Page<MqttRemoteClient> getAll(Pageable pageable) {
        return mqttRemoteClientRepository.findAll(pageable);
    }

    public MqttRemoteClient findOneByUsernameAndPassword(String username, String password) {
        return mqttRemoteClientRepository.findTopByUsernameAndPassword(username, password);
    }


    public MqttRemoteClient findOneById(Long id) {
        return mqttRemoteClientRepository.findTopById(id);
    }

    public MqttRemoteClient findOneByClientId(String clientId) {
        return mqttRemoteClientRepository.findTopByClientId(clientId);
    }

    public Page<MqttRemoteClient> findAllByUserId(Long userId, Pageable pageable) {
        return mqttRemoteClientRepository.findAllByUserId(userId, pageable);
    }
}
