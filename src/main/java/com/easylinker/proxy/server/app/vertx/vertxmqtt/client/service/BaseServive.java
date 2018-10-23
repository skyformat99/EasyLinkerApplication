package com.easylinker.proxy.server.app.vertx.vertxmqtt.client.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseServive<T> {
    void save(T t);
    void delete(T t);
    Page <T>getAll(Pageable pageable);
}
