package com.easylinker.rabbitmq.consumer.model.log;

import com.easylinker.rabbitmq.consumer.model.BaseEntity;

/**
 * @author mac
 */
public class SystemLog extends BaseEntity {
    /**
     * 日志类型
     */
    private SystemLogType systemLogType;
    /**
     * 详情描述
     */
    private String info;
    /**
     * 如果是异常，这里就是原因
     */
    private String cause;

    public SystemLogType getSystemLogType() {
        return systemLogType;
    }

    public void setSystemLogType(SystemLogType systemLogType) {
        this.systemLogType = systemLogType;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}
