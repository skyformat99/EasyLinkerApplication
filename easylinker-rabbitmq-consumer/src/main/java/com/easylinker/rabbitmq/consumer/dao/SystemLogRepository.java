package com.easylinker.rabbitmq.consumer.dao;

import com.easylinker.rabbitmq.consumer.model.log.SystemLog;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author mac
 */
public interface SystemLogRepository extends MongoRepository<SystemLog, Long> {
}
