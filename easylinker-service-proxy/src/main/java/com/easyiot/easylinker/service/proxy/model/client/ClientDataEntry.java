package com.easyiot.easylinker.service.proxy.model.client;

import com.alibaba.fastjson.JSONObject;
import com.easyiot.easylinker.service.proxy.model.BaseEntity;

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
     * 将数据库原来的字段clientId改为了remoteDeviceId，
     * 但是在系统调用的方法内没有进行修改，依然使用clientId
     * 从下面的Getter和Setter方法里也可以明显的看出来
     * TODO 这里是个坑，但是不影响使用
     */
    private Long remoteDeviceId;

    /**
     * 关联设备的类型
     */
    private String remoteDeviceType;

    public String getRemoteDeviceType() {
        return remoteDeviceType;
    }

    public void setRemoteDeviceType(String remoteDeviceType) {
        this.remoteDeviceType = remoteDeviceType;
    }

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

    public Long getClientId() {
        return remoteDeviceId;
    }

    public void setClientId(Long clientId) {
        this.remoteDeviceId = clientId;
    }
}
