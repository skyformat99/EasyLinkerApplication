package com.easyiot.easylinker.service.proxy.config.pay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.easyiot.easylinker.service.proxy.config.pay.alipay.AliPayConfig;
import com.easyiot.easylinker.service.proxy.config.pay.wxpay.WXPayConfig;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @Author:   zhaolei
 * @Version   1.0.0
 *
 * @Description: 支付配置
 * @Date:     2018/11/27 14:33
 * Copyright (C), 2016-2018, EasyLinker V3
 */

@Configuration
public class PayConfiguration {

    /**
     * 微信支付配置
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "pay.weixin")
    public WXPayConfig wechatPayConfig(){
        return new WXPayConfig();
    }

    @Bean
    public WXPay wxPay(WXPayConfig payConfig){
        return new WXPay(payConfig, WXPayConstants.SignType.HMACSHA256);
    }

    /**
     * 支付宝支付配置
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "pay.alipay")
    public AliPayConfig alipayConfig(){
        return new AliPayConfig();
    }

    @Bean
    public AlipayClient alipayClient(AliPayConfig aliPayConfig){
        return new DefaultAlipayClient(
                aliPayConfig.getGateway(),
                aliPayConfig.getAppId(),
                aliPayConfig.getPrivateKey(),
                aliPayConfig.getFormat(),
                aliPayConfig.getCharset(),
                aliPayConfig.getPublicKey(),
                aliPayConfig.getSignType()
        );
    }
}
