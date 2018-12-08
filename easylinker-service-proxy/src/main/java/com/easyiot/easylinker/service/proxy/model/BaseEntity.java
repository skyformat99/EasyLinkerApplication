package com.easyiot.easylinker.service.proxy.model;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class BaseEntity implements Serializable {
    @Id
    private Long id = System.currentTimeMillis() + (long) (Math.random() * 100000L);
    private Date createTime = new Date();

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
