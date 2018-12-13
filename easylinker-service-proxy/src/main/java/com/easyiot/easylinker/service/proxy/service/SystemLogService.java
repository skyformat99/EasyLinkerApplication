package com.easyiot.easylinker.service.proxy.service;

import com.easyiot.easylinker.service.proxy.dao.SystemLogRepository;
import com.easyiot.easylinker.service.proxy.model.log.SystemLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author mac
 */
@Service("SystemLogService")
public class SystemLogService implements BaseService<SystemLog> {
    private final SystemLogRepository systemLogRepository;

    @Autowired
    public SystemLogService(SystemLogRepository systemLogRepository) {
        this.systemLogRepository = systemLogRepository;
    }

    @Override
    public void save(SystemLog systemLog) {
        systemLogRepository.save(systemLog);
    }

    @Override
    public void delete(SystemLog systemLog) {
        systemLogRepository.delete(systemLog);

    }

    @Override
    public Page<SystemLog> getAll(Pageable pageable) {
        return systemLogRepository.findAll(pageable);
    }

    @Override
    public Page<SystemLog> findAllByUserId(Long userId, Pageable pageable) {
        return null;
    }
}
