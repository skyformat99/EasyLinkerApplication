package com.easylinker.proxy.server.app.model;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

public class BaseEntity implements Serializable {
    @Id
    private Long id = System.currentTimeMillis() + (long) (Math.random() * 100000L);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
