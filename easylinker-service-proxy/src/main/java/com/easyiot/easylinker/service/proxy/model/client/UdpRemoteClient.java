package com.easyiot.easylinker.service.proxy.model.client;

import com.easyiot.easylinker.service.proxy.model.BaseEntity;

public class UdpRemoteClient extends BaseEntity {
    private String type = "UDP";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
