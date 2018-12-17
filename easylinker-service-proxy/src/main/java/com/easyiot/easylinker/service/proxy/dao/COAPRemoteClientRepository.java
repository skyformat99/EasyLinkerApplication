package com.easyiot.easylinker.service.proxy.dao;

import com.easyiot.easylinker.service.proxy.model.client.CoapRemoteClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface COAPRemoteClientRepository extends MongoRepository<CoapRemoteClient,Long> {
     Page<CoapRemoteClient> findAllByUserId(Long userId, Pageable pageable);

    CoapRemoteClient findTopById(Long id);

    Long countAllByUserId(Long userId);
}
