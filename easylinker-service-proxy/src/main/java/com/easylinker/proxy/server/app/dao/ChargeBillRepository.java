package com.easylinker.proxy.server.app.dao;

import com.easylinker.proxy.server.app.model.charge.ChargeBill;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author wwhai
 */
public interface ChargeBillRepository extends MongoRepository<ChargeBill, Long> {
}
