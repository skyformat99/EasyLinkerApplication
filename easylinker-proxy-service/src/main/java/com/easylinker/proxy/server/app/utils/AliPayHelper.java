package com.easylinker.proxy.server.app.utils;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.easylinker.proxy.server.app.config.pay.alipay.AliPayConfig;
import com.easylinker.proxy.server.app.model.AlipayOrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @Author:   zhaolei
 * @Version   1.0.4
 *
 * @Description: 支付宝参数处理
 * @Date:     2018/11/27 14:44
 * Copyright (C), 2016-2018, EasyLinker V3
 */

@Slf4j
@Component
public class AliPayHelper {

    private final AlipayClient alipayClient;
    private final AliPayConfig aliPayConfig;
    @Autowired
    public AliPayHelper(AlipayClient alipayClient, AliPayConfig aliPayConfig){
        this.alipayClient = alipayClient;
        this.aliPayConfig = aliPayConfig;
    }

    /**
     * 扫码支付
     * @param orderId 订单号
     * @param totalAmount 总金额
     * @param title 商品标题
     * @return
     */
    public String creatAliPayUrl(String orderId, Long totalAmount, String title){
        AlipayTradePrecreateRequest createRequest = new AlipayTradePrecreateRequest();
        createRequest.setNotifyUrl(aliPayConfig.getNotifyUrl());

        AlipayOrderEntity orderEntity = new AlipayOrderEntity();
        orderEntity.setOut_trade_no(orderId);
        orderEntity.setTotal_amount(totalAmount.toString());
        orderEntity.setSubject(title);
        orderEntity.setBody(title);
        orderEntity.setTimeout_express("10m");

        String order = JSONObject.toJSONString(orderEntity);

        createRequest.setBizContent(order);
        try {
            AlipayTradePrecreateResponse createResponse = alipayClient.execute(createRequest);

            String qrCode = createResponse.getQrCode();
            return qrCode;

        } catch (AlipayApiException e) {
            log.error("[创建支付宝订单错误],错误信息：{}", e);
        }
        return null;
    }

    public void isValidSign(HttpServletRequest request){
        Map<String, String> params = handleResult(request);
        try {
            boolean validSign = AlipaySignature.rsaCheckV1(params, aliPayConfig.getPublicKey(),
                    aliPayConfig.getCharset(), aliPayConfig.getSignType());
            if (!validSign){
                log.error("验证签名失败，签名有误！");
                throw new RuntimeException("签名校验异常，签名不一致！");
            }

            // TODO 调试使用，上线屏蔽
            log.info("[支付宝异步通知验签成功] {}", params);

        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> handleResult(HttpServletRequest request){
        Map<String, String[]> requestParams = request.getParameterMap();
        Map<String, String> params = new HashMap<>();
        for (String key : requestParams.keySet()){
            String[] values = requestParams.get(key);
            String value = "";
            for (int i = 0; i<values.length; i++){
                value = (i == values.length - 1) ? value + values[i] : value + values[i] + ",";
            }
            params.put(key, value);
        }

        return params;
    }


}
