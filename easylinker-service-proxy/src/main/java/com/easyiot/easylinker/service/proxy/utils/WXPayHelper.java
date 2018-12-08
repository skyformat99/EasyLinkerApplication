package com.easyiot.easylinker.service.proxy.utils;

import com.easyiot.easylinker.service.proxy.config.pay.wxpay.WXPayConfig;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @Author:   zhaolei
 * @Version   1.0.4
 *
 * @Description: 微信支付下单工具类
 * @Date:     2018/11/26 17:24
 * Copyright (C), 2016-2018, EasyLinker V3
 */

@Slf4j
@Component
public class WXPayHelper {

    private final WXPay wxPay;
    private final WXPayConfig wxPayConfig;
    @Autowired
    public WXPayHelper(WXPay wxPay, WXPayConfig wxPayConfig){
        this.wxPay = wxPay;
        this.wxPayConfig = wxPayConfig;
    }

    public String createWXPayUrl(String orderId, Long totalAmount, String desc){
        Map<String, String> orderData = new HashMap<>();
        orderData.put("body", desc);
        orderData.put("out_trade_no", orderId);
        orderData.put("total_fee", totalAmount.toString());
        orderData.put("spbill_create_ip", "127.0.0.1");
        orderData.put("notify_url", wxPayConfig.getNotifyUrl());
        orderData.put("trade_type", "NATIVE");
        try {

            Map<String, String> result = wxPay.unifiedOrder(orderData);

            // 参数校验
            isSuccess(result);

            String payUrl = result.get("code_url");
            return payUrl;
        } catch (Exception e) {
            log.error("[创建订单异常]", e);
            return null;
        }
    }

    /**
     * 通信标识和业务标识校验
     * @param result
     */
    public void isSuccess(Map<String, String> result) {
        String returnCode = result.get("return_code");
        if (WXPayConstants.FAIL.equals(returnCode)){
            log.error("[创建订单失败] 错误信息：{}", result.get("return_msg"));
        }
        String resultCode = result.get("result_code");
        if (WXPayConstants.FAIL.equals(resultCode)){
            log.error("[系统错误] 错误代码：{}，错误描述：{}", result.get("err_code"), result.get("err_code_des"));
        }
    }

    public void isValidSign(Map<String, String> data){
        try {
            String signSha256 = WXPayUtil.generateSignature(data, wxPayConfig.getKey(), WXPayConstants.SignType.HMACSHA256);
            String signMd5 = WXPayUtil.generateSignature(data, wxPayConfig.getKey(), WXPayConstants.SignType.MD5);

            // 比较签名
            String sign = data.get("sign");
            if (!StringUtils.equals(sign, signSha256) && !StringUtils.equals(sign, signMd5)) {
                log.error("签名校验异常，签名有误！");
                // todo 异常暂时这么写，后面再优化。
                throw new RuntimeException("签名校验异常，签名不一致！");
            }

            // TODO 调试使用，上线屏蔽
            log.info("[微信异步通知验签成功] {}", data);
        } catch (Exception e) {
            log.error("校验签名失败：{}", e);
        }
    }

    public String mapToXml(Map<String, String> param){
        try {
            String xml = WXPayUtil.mapToXml(param);
            return xml;
        } catch (Exception e) {
            log.error("mapToXml异常，异常信息：{}", e);
        }
        return null;
    }
}
