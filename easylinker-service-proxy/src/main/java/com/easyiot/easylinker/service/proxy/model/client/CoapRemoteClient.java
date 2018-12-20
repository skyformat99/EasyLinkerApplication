package com.easyiot.easylinker.service.proxy.model.client;

import com.easyiot.easylinker.service.proxy.model.BaseEntity;

import java.util.UUID;

/**
 * COAP 设备
 */
public class CoapRemoteClient extends BaseEntity {
    private String token = UUID.randomUUID().toString().replace("-", "");
    private String clientId = UUID.randomUUID().toString().split("-")[4];
    private Long userId;
    private String name = UUID.randomUUID().toString().substring(0, 10);
    private String info = "Nothing";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
