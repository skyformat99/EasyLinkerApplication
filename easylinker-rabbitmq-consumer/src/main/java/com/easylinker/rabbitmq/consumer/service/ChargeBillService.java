package com.easylinker.rabbitmq.consumer.service;

import com.easylinker.rabbitmq.consumer.dao.ChargeBillRepository;
import com.easylinker.rabbitmq.consumer.model.charge.ChargeBill;
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
}
