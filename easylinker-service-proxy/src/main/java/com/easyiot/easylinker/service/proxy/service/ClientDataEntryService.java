package com.easyiot.easylinker.service.proxy.service;

import com.easyiot.easylinker.service.proxy.dao.ClientDataEntryRepository;
import com.easyiot.easylinker.service.proxy.model.client.ClientDataEntry;
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

    @Override
    public Page<ClientDataEntry> findAllByUserId(Long userId, Pageable pageable) {
        return null;
    }

    /**
     * 根据客户端的ID查找数据
     *
     * @param clientId
     * @param pageable
     * @return
     */
    public Page<ClientDataEntry> getByClientId(Long clientId, Pageable pageable) {
        return clientDataEntryRepository.findAllByRemoteDeviceId(clientId, pageable);
    }
}
