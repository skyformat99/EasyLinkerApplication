package com.easylinker.proxy.server.app.service;

import com.easylinker.proxy.server.app.model.MqttRemoteClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MqttRemoteClientService implements BaseServive<MqttRemoteClient>{
    @Override
    public void save(MqttRemoteClient mqttRemoteClient) {

    }

    @Override
    public void delete(MqttRemoteClient mqttRemoteClient) {

    }

    @Override
    public Page<MqttRemoteClient> getAll(Pageable pageable) {
        return null;
    }
}
