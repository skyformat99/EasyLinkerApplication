package com.easylinker.proxy.server.app.service;

import com.easylinker.proxy.server.app.dao.MqttRemoteClientRepository;
import com.easylinker.proxy.server.app.model.MqttRemoteClient;
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

    }

    @Override
    public void delete(MqttRemoteClient mqttRemoteClient) {

    }

    @Override
    public Page<MqttRemoteClient> getAll(Pageable pageable) {
        return mqttRemoteClientRepository.findAll(pageable);
    }

    public MqttRemoteClient findOneByUsernameAndPassword(String username, String passwords) {
        return mqttRemoteClientRepository.findTopByUsernameAndPassword(username, passwords);
    }


}
