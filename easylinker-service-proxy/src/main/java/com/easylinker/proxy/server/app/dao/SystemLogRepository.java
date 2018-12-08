package com.easylinker.proxy.server.app.dao;

import com.easylinker.proxy.server.app.model.log.SystemLog;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author mac
 */
public interface SystemLogRepository extends MongoRepository<SystemLog,Long> {
}
