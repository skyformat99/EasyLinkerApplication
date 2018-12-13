package com.easyiot.easylinker.service.proxy.dao;

import com.easyiot.easylinker.service.proxy.model.client.HttpRemoteClient;
import com.easyiot.easylinker.service.proxy.model.client.MqttRemoteClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HttpRemoteClientRepository extends MongoRepository<HttpRemoteClient,Long> {
    Page<HttpRemoteClient> findAllByUserId(Long userId, Pageable pageable);

    HttpRemoteClient findTopById(Long id);
    Long countAllByUserId(Long userId);

}
