package com.easyiot.easylinker.rabbitmq.consumer.service;

import com.easyiot.easylinker.rabbitmq.consumer.dao.ClientDataEntryRepository;
import com.easyiot.easylinker.rabbitmq.consumer.model.mqtt.ClientDataEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service("ClientDataEntryService")
public class ClientDataEntryService implements BaseService<ClientDataEntry> {

    @Autowired
    ClientDataEntryRepository clientDataEntryRepository;

    @Override
    public void save(ClientDataEntry clientDataEntry) {
        clientDataEntryRepository.save(clientDataEntry);

    }

    @Override
    public void delete(ClientDataEntry clientDataEntry) {
        clientDataEntryRepository.delete(clientDataEntry);

    }

    @Override
    public Page<ClientDataEntry> getAll(Pageable pageable) {
        return clientDataEntryRepository.findAll(pageable);
    }

    /**
     * 根据客户端的ID查找数据
     *
     * @param clientId
     * @param pageable
     * @return
     */
    public Page<ClientDataEntry> getByClientId(Long clientId, Pageable pageable) {
        return clientDataEntryRepository.findAllByClientId(clientId, pageable);
    }
}
