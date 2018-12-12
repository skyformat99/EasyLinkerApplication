package com.easyiot.easylinker.service.proxy.dao;

import com.easyiot.easylinker.service.proxy.model.client.ClientDataEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClientDataEntryRepository extends MongoRepository<ClientDataEntry, Long> {
    Page<ClientDataEntry> findAllByClientId(Long clientId, Pageable pageable);
}
