package com.easyiot.easylinker.service.proxy.dao;

import com.easyiot.easylinker.service.proxy.model.log.SystemLog;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author mac
 */
public interface SystemLogRepository extends MongoRepository<SystemLog,Long> {
}
