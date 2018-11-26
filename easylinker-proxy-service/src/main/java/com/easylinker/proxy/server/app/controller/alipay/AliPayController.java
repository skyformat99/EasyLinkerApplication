package com.easylinker.proxy.server.app.controller.alipay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.easylinker.proxy.server.app.config.mvc.WebReturnResult;
import com.easylinker.proxy.server.app.model.AlipayOrderEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 支付宝支付Controller
 *
 * @author wwhai
 */
@RestController
@RequestMapping("/pay")
public class AliPayController {

    @Value("${alipay.appId}")
    private String appId;
    @Value("${alipay.privateKey}")
    private String privateKey;
    @Value("${alipay.notifyUrl}")
    private String notifyUrl;
    @Value("${alipay.returnUrl}")
    private String returnUrl;
    @Value("${alipay.gateway}")
    private String gateway;
    @Value("${alipay.charset}")
    private String charset;
    @Value("${alipay.format}")
    private String format;
    @Value("${alipay.publicKey}")
    private String publicKey;
    @Value("${alipay.signType}")
    private String signType;

    /**
     * 创建订单
     * TODO 此接口后期重构，只负责创建订单和获取支付链接，避免在Controller中直接使用
     *
     * @throws Exception
     */
    @RequestMapping("/pay")
    public void pay(HttpServletResponse response) throws Exception {


        // 创建请求客户端
        AlipayClient client = new DefaultAlipayClient(gateway,
                appId, privateKey, format, charset, publicKey, signType);

        // 设置请求参数
        AlipayTradePagePayRequest alipayTradePagePayRequest = new AlipayTradePagePayRequest();
        alipayTradePagePayRequest.setReturnUrl(returnUrl);
        alipayTradePagePayRequest.setNotifyUrl(notifyUrl);

        // 封装订单信息
        AlipayOrderEntity orderEntity = new AlipayOrderEntity();
        orderEntity.setTradeNumber(UUID.randomUUID().toString().replace("-", ""));
        orderEntity.setTotalAmount("100");
        orderEntity.setSubject("云易物联接口充值");
        orderEntity.setBody("云易物联接口调用次数充值");
        orderEntity.setTimeoutExpress("10m");
        String order = JSONObject.toJSONString(orderEntity);
        alipayTradePagePayRequest.setBizContent(order);
        String form = client.pageExecute(alipayTradePagePayRequest).getBody();
        // 生成表单
        response.setContentType("text/html;charset=" + charset);
        // 直接将完整的表单html输出到页面
        response.getWriter().write(form);
        response.getWriter().flush();
        response.getWriter().close();
    }

    /**
     * 支付宝同步回调地址
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping("/returnUrl")
    public JSONObject returnUrl(HttpServletRequest request) throws Exception {

        // 获取支付宝GET过来反馈信息（官方固定代码）
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        // 调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(verifySign(params, requestParams), publicKey, charset, signType);

        // 返回界面
        if (signVerified) {
            String out_trade_no = request.getParameter("out_trade_no");
            String trade_no = request.getParameter("trade_no");
            String total_amount = request.getParameter("total_amount");

            System.out.println("====================回调页面=======================================");
            System.out.println("商户订单号：" + out_trade_no);
            System.out.println("支付宝交易号：" + trade_no);
            System.out.println("付款金额：" + total_amount);
            return WebReturnResult.returnTipMessage(1, "Success!");
        } else {
            return WebReturnResult.returnTipMessage(0, "Failed!");

        }

    }

    /**
     * 支付宝服务器异步通知
     * 详细信息请参考https://docs.open.alipay.com/270/105902/
     *
     * @param request
     * @throws Exception
     */
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) throws Exception {
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        verifySign(params, requestParams);
        // 调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, publicKey, charset, signType);
        // 验证成功 更新订单信息
        // 验证成功 更新订单信息
        if (signVerified) {
            // 商户订单号
            String out_trade_no = request.getParameter("out_trade_no");
            String trade_no = request.getParameter("trade_no");
            // 交易状态
            String trade_status = request.getParameter("trade_status");
            System.out.println("=====================异步通知结果=================================");
            System.out.println("商户订单号：" + out_trade_no);
            System.out.println("支付宝交易号：" + trade_no);
            System.out.println("交易状态：" + trade_status);
            // 修改数据库

            /**
             * 程序执行完后必须打印输出“success”（不包含引号）。
             * 如果商户反馈给支付宝的字符不是success这7个字符，
             * 支付宝服务器会不断重发通知，直到超过24小时22分钟。一般情况下，25小时以内完成8次通知
             * （通知的间隔频率一般是：4m,10m,10m,1h,2h,6h,15h）
             */
            return "success";
        } else {
            return "error";

        }
    }

    private Map<String, String> verifySign(Map<String, String> params, Map<String, String[]> requestParams) {
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }

}
