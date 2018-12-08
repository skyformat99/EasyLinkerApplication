package com.easylinker.proxy.server.app.config.pay.alipay;

import lombok.Data;

/**
 *
 * @Author:   zhaolei
 * @Version   1.0.0
 *
 * @Description: Alipay 配置信息
 * @Date:     2018/11/26 1:28
 * Copyright (C), 2016-2018, EasyLinker V3
 */

@Data
public class AliPayConfig {

    private String appId;

    private String privateKey;

    private String publicKey;

    private String gateway;

    private String returnUrl;

    private String notifyUrl;

    private String signType;

    private String charset;

    private String format;
}
