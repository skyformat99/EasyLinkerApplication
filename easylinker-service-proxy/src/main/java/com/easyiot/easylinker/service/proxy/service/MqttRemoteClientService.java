package com.easyiot.easylinker.service.proxy.service;

import com.easyiot.easylinker.service.proxy.dao.MqttRemoteClientRepository;
import com.easyiot.easylinker.service.proxy.model.client.MqttRemoteClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MqttRemoteClientService implements BaseService<MqttRemoteClient> {

    private final MqttRemoteClientRepository mqttRemoteClientRepository;

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
        for (Long l : ids) {
            mqttRemoteClientRepository.deleteById(l);
        }

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

    @Override
    public Page<MqttRemoteClient> findAllByUserId(Long userId, Pageable pageable) {
        return mqttRemoteClientRepository.findAllByUserId(userId, pageable);
    }

    public Long count(Long userId) {
        return mqttRemoteClientRepository.countAllByUserId(userId);
    }

    public Long onlineCount(Long userId) {
        return mqttRemoteClientRepository.countAllByUserIdAndOnLine(userId, false);
    }

    public Page<MqttRemoteClient> findByNameLike(String name, Long userId, int pageNum, int pageSize){
        PageRequest pageRequest = PageRequest.of(pageNum-1, pageSize);
        return mqttRemoteClientRepository.findByNameLikeAndUserId(name, userId, pageRequest);
    }
}
