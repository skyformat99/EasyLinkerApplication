package com.easyiot.easylinker.service.proxy.model.client;

import com.easyiot.easylinker.service.proxy.model.BaseEntity;

import java.util.List;

public class UdpRemoteClient extends BaseEntity {
    private String type = "UDP";

    private List location;

    public List getLocation() {
        return location;
    }

    public void setLocation(List location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
