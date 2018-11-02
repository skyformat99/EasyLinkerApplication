package com.easylinker.proxy.server.app.model;

/**
 * 客户端ACL描述类
 * topic:订阅的主题
 * acls:权限描述
 * 1:sub
 * 2:pub
 * 3:sub&pub
 */
public class ClientACLEntry extends BaseEntity {
    private String topic;
    private int acl;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getAcl() {
        return acl;
    }

    public void setAcl(int acl) {
        this.acl = acl;
    }
}
