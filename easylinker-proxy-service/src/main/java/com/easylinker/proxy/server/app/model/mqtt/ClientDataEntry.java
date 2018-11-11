package com.easylinker.proxy.server.app.model.mqtt;

import com.easylinker.proxy.server.app.model.BaseEntity;
import com.google.gson.JsonObject;

/**
 * 客户端产生的数据模型
 */
public class ClientDataEntry extends BaseEntity {
    /**
     * 数据的一些描述信息，默认为空
     */
    private String info;
    /**
     * 真正的数据结构是个JSON，客户端怎么存进来，后台就怎么保存
     */
    private JsonObject data;

    /**
     * 关联的客户端的ID
     */
    private Long clientId;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
