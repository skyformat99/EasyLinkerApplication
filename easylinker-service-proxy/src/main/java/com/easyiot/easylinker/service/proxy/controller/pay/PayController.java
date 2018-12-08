package com.easyiot.easylinker.service.proxy.controller.pay;

import com.alibaba.fastjson.JSONObject;
import com.easyiot.easylinker.service.proxy.config.mvc.WebReturnResult;
import com.easyiot.easylinker.service.proxy.utils.AliPayHelper;
import com.easyiot.easylinker.service.proxy.utils.WXPayHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @Author:   zhaolei
 * @Version   2.0.1
 *
 * @Description: PayController
 * @Date:     2018/11/27 16:28
 * Copyright (C), 2016-2018, EasyLinker V3
 */

@Slf4j
@RestController
@RequestMapping("/pay")
public class PayController {

    private final AliPayHelper aliPayHelper;
    private final WXPayHelper wxPayHelper;
    @Autowired
    public PayController(AliPayHelper aliPayHelper, WXPayHelper wxPayHelper){
        this.aliPayHelper = aliPayHelper;
        this.wxPayHelper = wxPayHelper;
    }

    /**
     * 统一下单接口
     * @return
     */
    @GetMapping
    public JSONObject createOrder(){
        String orderId = UUID.randomUUID().toString().replace("-", "");
        Long totalAmount = 10000L;
        String title = "云易物联接口充值";
        String aliPayUrl = aliPayHelper.creatAliPayUrl(orderId, totalAmount, title);
        String wxPayUrl = wxPayHelper.createWXPayUrl(orderId, totalAmount, title);

        JSONObject result = new JSONObject(true);
        result.put("alipay", aliPayUrl);
        result.put("wxpay", wxPayUrl);

        return WebReturnResult.returnDataMessage(10000, "创建订单成功", result);
    }

    /**
     * 支付宝支付异步通知
     * @param request
     * @return
     */
    @PostMapping("/alipay")
    public String notify(HttpServletRequest request){
        aliPayHelper.isValidSign(request);

        // TODO 支付成功后的处理逻辑

        return "success";
    }

    /**
     * 微信支付异步通知
     * @param result
     * @return
     */
    @PostMapping(value = "/wxpay", produces = "application/xml;charset=UTF-8")
    public String notify(@RequestBody Map<String, String> result){
        wxPayHelper.isSuccess(result);
        wxPayHelper.isValidSign(result);

        // TODO 支付成功后处理逻辑

        Map<String, String> msg = new HashMap<>();
        msg.put("return_code", "SUCCESS");
        msg.put("return_msg", "OK");
        return wxPayHelper.mapToXml(msg);
    }

}
