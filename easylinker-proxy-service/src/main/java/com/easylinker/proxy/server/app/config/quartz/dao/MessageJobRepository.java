package com.easylinker.proxy.server.app.config.quartz.dao;

import com.easylinker.proxy.server.app.config.quartz.MessageJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageJobRepository extends MongoRepository<MessageJob, Long> {
    MessageJob findTopById(Long id);

    Page<MessageJob> findAllByUserId(Long userId, Pageable pageable);
}
