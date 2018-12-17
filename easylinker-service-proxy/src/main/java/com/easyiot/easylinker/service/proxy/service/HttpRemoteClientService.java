package com.easyiot.easylinker.service.proxy.service;

import com.easyiot.easylinker.service.proxy.dao.HttpRemoteClientRepository;
import com.easyiot.easylinker.service.proxy.model.client.HttpRemoteClient;
import com.easyiot.easylinker.service.proxy.model.client.MqttRemoteClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class HttpRemoteClientService implements BaseService<HttpRemoteClient> {
    @Autowired
    HttpRemoteClientRepository httpRemoteClientRepository;

    @Override
    public void save(HttpRemoteClient httpRemoteClient) {
        httpRemoteClientRepository.save(httpRemoteClient);

    }

    @Override
    public void delete(HttpRemoteClient httpRemoteClient) {

        httpRemoteClientRepository.delete(httpRemoteClient);
    }

    @Override
    public Page<HttpRemoteClient> getAll(Pageable pageable) {
        return httpRemoteClientRepository.findAll(pageable);
    }

    @Override
    public Page<HttpRemoteClient> findAllByUserId(Long userId, Pageable pageable) {
        return httpRemoteClientRepository.findAllByUserId(userId, pageable);
    }
    public HttpRemoteClient findOneById(Long id) {
        return httpRemoteClientRepository.findTopById(id);
    }

    public Long count(Long userId) {
        return httpRemoteClientRepository.countAllByUserId(userId);
    }

    public void delete(Long id) {
        httpRemoteClientRepository.deleteById(id);

    }

}
