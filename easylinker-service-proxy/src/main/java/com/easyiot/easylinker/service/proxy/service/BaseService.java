package com.easyiot.easylinker.service.proxy.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseService<T> {
    void save(T t);
    void delete(T t);
    Page <T>getAll(Pageable pageable);


}
