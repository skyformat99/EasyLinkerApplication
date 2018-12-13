package com.easyiot.easylinker.service.proxy.service;

import com.easyiot.easylinker.service.proxy.dao.ChargeBillRepository;
import com.easyiot.easylinker.service.proxy.model.charge.ChargeBill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author wwhai
 */
@Service("ChargeBillService")
public class ChargeBillService implements BaseService<ChargeBill> {
    private final ChargeBillRepository chargeBillRepository;

    @Autowired
    public ChargeBillService(ChargeBillRepository chargeBillRepository) {
        this.chargeBillRepository = chargeBillRepository;
    }

    @Override
    public void save(ChargeBill chargeBill) {
        chargeBillRepository.save(chargeBill);
    }

    @Override
    public void delete(ChargeBill chargeBill) {
        chargeBillRepository.delete(chargeBill);
    }

    @Override
    public Page<ChargeBill> getAll(Pageable pageable) {
        return chargeBillRepository.findAll(pageable);
    }

    @Override
    public Page<ChargeBill> findAllByUserId(Long userId, Pageable pageable) {
        return null;
    }
}
