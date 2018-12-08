package com.easylinker.proxy.server.app.config.sms.aliyunsms;

import lombok.Data;

@Data
public class AliYunSMSConfig {

    private String accessKeyId;

    private String accessKeySecret;

    private String template;

    private String signName;

    private final String regionId = "cn-hangzhou";

    private final String product = "Dysmsapi";

    private final String domain = "dysmsapi.aliyuncs.com";

    private final String code = "OK";

}
