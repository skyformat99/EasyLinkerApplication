package com.easyiot.easylinker.service.proxy.model.log;

/**
 * 日志类型
 */
public enum SystemLogType {
    /**
     *系统日志
     */
    SYSTEM_LOG_TYPE,
    /**
     *错误日志
     */
    ERROR_LOG_TYPE,
    /**
     *警告日志
     */
    WARMING_LOG_TYPE,
    /**
     *信息日志
     */
    INFO_LOG_TYPE,
    /**
     *MQTT上线
     */
    MQTT_CLIENT_ONLINE,
    /**
     *MQTT下线
     */
    MQTT_CLIENT_OFFLINE

}
