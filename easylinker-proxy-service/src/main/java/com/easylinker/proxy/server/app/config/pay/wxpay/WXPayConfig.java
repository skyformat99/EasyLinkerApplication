package com.easylinker.proxy.server.app.config.pay.wxpay;

import lombok.Data;

import java.io.InputStream;

@Data
public class WXPayConfig implements com.github.wxpay.sdk.WXPayConfig {

    private String appID;

    private String mchID;

    private String key;

    private int httpConnectTimeoutMs;

    private int httpReadTimeoutMs;

    private String notifyUrl;


    @Override
    public InputStream getCertStream() {
        return null;
    }
}
