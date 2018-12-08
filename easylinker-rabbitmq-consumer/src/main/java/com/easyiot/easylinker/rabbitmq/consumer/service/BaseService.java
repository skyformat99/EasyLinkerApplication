package com.easyiot.easylinker.rabbitmq.consumer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseService<T> {
    void save(T t);
    void delete(T t);
    Page <T>getAll(Pageable pageable);


}
