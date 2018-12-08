package com.easylinker.proxy.server.app.dao;

import com.easylinker.proxy.server.app.model.mqtt.ClientDataEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClientDataEntryRepository extends MongoRepository<ClientDataEntry, Long> {
    Page<ClientDataEntry> findAllByClientId(Long clientId, Pageable pageable);
}
