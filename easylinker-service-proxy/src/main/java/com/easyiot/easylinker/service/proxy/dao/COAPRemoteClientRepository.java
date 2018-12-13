package com.easyiot.easylinker.service.proxy.dao;

import com.easyiot.easylinker.service.proxy.model.client.COAPRemoteClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface COAPRemoteClientRepository extends MongoRepository<COAPRemoteClient,Long> {
     Page<COAPRemoteClient> findAllByUserId(Long userId, Pageable pageable);

    COAPRemoteClient findTopById(Long id);

    Long countAllByUserId(Long userId);
}
