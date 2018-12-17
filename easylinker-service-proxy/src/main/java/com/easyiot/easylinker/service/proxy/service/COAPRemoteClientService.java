package com.easyiot.easylinker.service.proxy.service;

import com.easyiot.easylinker.service.proxy.dao.COAPRemoteClientRepository;
import com.easyiot.easylinker.service.proxy.model.client.CoapRemoteClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class COAPRemoteClientService implements BaseService<CoapRemoteClient> {
    @Autowired
    COAPRemoteClientRepository coapRemoteClientRepository;


    @Override
    public void save(CoapRemoteClient coapRemoteClient) {
        coapRemoteClientRepository.save(coapRemoteClient);

    }

    @Override
    public void delete(CoapRemoteClient coapRemoteClient) {
        coapRemoteClientRepository.delete(coapRemoteClient);

    }

    @Override
    public Page<CoapRemoteClient> getAll(Pageable pageable) {
        return coapRemoteClientRepository.findAll(pageable);
    }


    @Override
    public Page<CoapRemoteClient> findAllByUserId(Long userId, Pageable pageable) {
        return coapRemoteClientRepository.findAllByUserId(userId, pageable);
    }

    public CoapRemoteClient findOneById(Long id) {
        return coapRemoteClientRepository.findTopById(id);
    }

    public Object count(Long userId) {
        return coapRemoteClientRepository.countAllByUserId(userId);
    }

    public void delete(long id) {
        coapRemoteClientRepository.deleteById(id);
    }
}
