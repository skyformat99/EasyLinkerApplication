package com.easyiot.easylinker.rabbitmq.consumer.dao;

 import com.easyiot.easylinker.rabbitmq.consumer.model.charge.ChargeBill;
 import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author wwhai
 */
public interface ChargeBillRepository extends MongoRepository<ChargeBill, Long> {
}
