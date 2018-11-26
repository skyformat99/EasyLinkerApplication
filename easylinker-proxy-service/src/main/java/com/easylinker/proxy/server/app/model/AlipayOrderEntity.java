package com.easylinker.proxy.server.app.model;

/**
 * @Author: zhaolei
 * @Version 1.0.0
 * @Description: 生成支付宝订单信息所需实体
 * @Date: 2018/11/25 22:17
 * Copyright (C), 2016-2018, EasyLinker V3
 */

public class AlipayOrderEntity extends BaseEntity {
    // 商户订单号
    private String tradeNumber;

    // 订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]。
    private String totalAmount;

    // 订单标题
    private String subject;

    //订单描述(可选)
    private String body;

    // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m
    private String timeoutExpress;

    public String getTradeNumber() {
        return tradeNumber;
    }

    public void setTradeNumber(String tradeNumber) {
        this.tradeNumber = tradeNumber;
    }

    public String getProductNumber() {
        return "FAST_INSTANT_TRADE_PAY";
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTimeoutExpress() {
        return timeoutExpress;
    }

    public void setTimeoutExpress(String timeoutExpress) {
        this.timeoutExpress = timeoutExpress;
    }
}
