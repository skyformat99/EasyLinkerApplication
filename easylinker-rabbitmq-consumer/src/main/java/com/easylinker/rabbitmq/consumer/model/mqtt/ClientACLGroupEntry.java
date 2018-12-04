package com.easylinker.rabbitmq.consumer.model.mqtt;

import com.easylinker.rabbitmq.consumer.model.BaseEntity;

import java.util.UUID;



public class ClientACLGroupEntry extends BaseEntity {
    private String name = UUID.randomUUID().toString().replace("-", "");
    private int acl = 3;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAcl() {
        return acl;
    }

    public void setAcl(int acl) {
        this.acl = acl;
    }
}
