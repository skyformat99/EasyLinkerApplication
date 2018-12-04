package com.easylinker.rabbitmq.consumer.model.mqtt;

import com.alibaba.fastjson.JSONObject;
import com.easylinker.rabbitmq.consumer.model.BaseEntity;

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
    private JSONObject data;

    /**
     * 关联的客户端的ID
     */
    private String clientId;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
