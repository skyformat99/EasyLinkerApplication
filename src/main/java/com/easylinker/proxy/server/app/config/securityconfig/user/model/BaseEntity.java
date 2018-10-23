package com.easylinker.proxy.server.app.config.securityconfig.user.model;

import org.springframework.data.annotation.Id;

public class BaseEntity {
    @Id
    private Long id = System.currentTimeMillis() + (long) (Math.random() * 100000L);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
