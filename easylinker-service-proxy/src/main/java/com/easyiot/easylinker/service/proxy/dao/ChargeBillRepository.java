package com.easyiot.easylinker.service.proxy.dao;

import com.easyiot.easylinker.service.proxy.model.charge.ChargeBill;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author wwhai
 */
public interface ChargeBillRepository extends MongoRepository<ChargeBill, Long> {
}
