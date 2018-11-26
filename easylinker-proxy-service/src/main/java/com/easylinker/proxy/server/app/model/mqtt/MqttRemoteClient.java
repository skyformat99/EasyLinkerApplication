package com.easylinker.proxy.server.app.model.mqtt;

import com.easylinker.proxy.server.app.model.BaseEntity;

import java.util.List;
import java.util.UUID;

/**
 * 持久户化的设备客户端
 * 这个类用来关联状态
 */

public class MqttRemoteClient extends BaseEntity {

    //MQTT 协议本身支持的鉴权：username password
    //本设计为了实现统计在线效果，扩展了一个是否在线标记
    //同时增加了ACL属性
    private String username = UUID.randomUUID().toString().replace("-", "");
    private String password = UUID.randomUUID().toString().replace("-", "");
    private String clientId = UUID.randomUUID().toString().replace("-", "");
    private Boolean onLine = false;
    //下面是一些业务逻辑级别的扩展字段
    private String name = UUID.randomUUID().toString().substring(0, 10);
    private String info = "Nothing";
    private Long userId;

    private Long dataRows=10000L;

    public Long getDataRows() {
        return dataRows;
    }

    public void setDataRows(Long dataRows) {
        this.dataRows = dataRows;
    }

    //ACL 描述
    private List<ClientACLEntry> aclEntries;
    //
    private List<ClientACLGroupEntry>  clientACLGroupEntries;


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


    public Boolean getOnLine() {
        return onLine;
    }

    public void setOnLine(Boolean onLine) {
        this.onLine = onLine;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<ClientACLEntry> getAclEntries() {
        return aclEntries;
    }

    public void setAclEntries(List<ClientACLEntry> aclEntries) {
        this.aclEntries = aclEntries;
    }

    public List<ClientACLGroupEntry> getClientACLGroupEntries() {
        return clientACLGroupEntries;
    }

    public void setClientACLGroupEntries(List<ClientACLGroupEntry> clientACLGroupEntries) {
        this.clientACLGroupEntries = clientACLGroupEntries;
    }

    @Override
    public String toString() {
        return "{" + this.getId() + "," +
                this.getClientId() + ","
                + this.getUsername() + ","
                + this.getPassword() + "}";
    }
}
