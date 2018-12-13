package com.easyiot.easylinker.service.proxy.service;

import com.easyiot.easylinker.service.proxy.dao.COAPRemoteClientRepository;
import com.easyiot.easylinker.service.proxy.model.client.COAPRemoteClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class COAPRemoteClientService implements BaseService<COAPRemoteClient> {
    @Autowired
    COAPRemoteClientRepository coapRemoteClientRepository;


    @Override
    public void save(COAPRemoteClient coapRemoteClient) {
        coapRemoteClientRepository.save(coapRemoteClient);

    }

    @Override
    public void delete(COAPRemoteClient coapRemoteClient) {
        coapRemoteClientRepository.delete(coapRemoteClient);

    }

    @Override
    public Page<COAPRemoteClient> getAll(Pageable pageable) {
        return coapRemoteClientRepository.findAll(pageable);
    }


    @Override
    public Page<COAPRemoteClient> findAllByUserId(Long userId, Pageable pageable) {
        return coapRemoteClientRepository.findAllByUserId(userId, pageable);
    }

    public COAPRemoteClient findOneById(Long id) {
        return coapRemoteClientRepository.findTopById(id);
    }
}
