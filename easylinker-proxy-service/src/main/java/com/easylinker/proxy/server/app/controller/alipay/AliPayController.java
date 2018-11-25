package com.easylinker.proxy.server.app.controller.alipay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.easylinker.proxy.server.app.config.alipay.AliPayConfig;
import com.easylinker.proxy.server.app.config.mvc.WebReturnResult;
import com.easylinker.proxy.server.app.model.AlipayOrderEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.easylinker.proxy.server.app.config.alipay.AliPayConfig.sign_type;

/**
 * 支付宝支付Controller
 *
 * @author wwhai
 */
@RestController
@RequestMapping("/pay")
public class AliPayController {

    private String appId = AliPayConfig.app_id;
    private String privateKey = AliPayConfig.private_key;
    private String notifyUrl = AliPayConfig.notify_url;
    private String returnUrl = AliPayConfig.return_url;
    private String url = AliPayConfig.url;
    private String charset = AliPayConfig.charset;
    private String format = AliPayConfig.format;
    private String publicKey = AliPayConfig.public_key;
    private String signType = sign_type;

    /**
     * 创建订单
     *
     * @throws Exception
     */
    @RequestMapping("/pay")
    public void pay(HttpServletResponse response) throws Exception {


        // 创建请求客户端
        AlipayClient client = new DefaultAlipayClient(url,
                appId, privateKey, format, charset, publicKey, signType);

        // 设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(returnUrl);
        alipayRequest.setNotifyUrl(notifyUrl);

        // 封装订单信息
        AlipayOrderEntity orderEntity = new AlipayOrderEntity();
        orderEntity.setOut_trade_no(UUID.randomUUID().toString().replace("-", ""));
        orderEntity.setTotal_amount("100");
        orderEntity.setSubject("云易物联接口充值");
        orderEntity.setBody("云易物联接口调用次数充值");
        //orderEntity.setTimeout_express("10m");
        String order = JSONObject.toJSONString(orderEntity);
        System.out.println(order);
        alipayRequest.setBizContent(order);
        String form = client.pageExecute(alipayRequest).getBody();
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
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        handleResult(params, requestParams);
        // 调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, publicKey, charset, signType);

        // 返回界面
        if (signVerified) {
            String out_trade_no = request.getParameter("out_trade_no");
            String trade_no = request.getParameter("trade_no");
            String total_amount = request.getParameter("total_amount");
            System.out.println("商户订单号："+out_trade_no);
            System.out.println("支付宝交易号："+trade_no);
            System.out.println("付款金额："+total_amount);
            return WebReturnResult.returnTipMessage(1, "Success!");
        } else {
            return WebReturnResult.returnTipMessage(0, "Failed!");

        }

    }

    /**
     * 支付宝服务器异步通知
     *
     * @param request
     * @throws Exception
     */
    @PostMapping("/notify")
    public JSONObject notify(HttpServletRequest request) throws Exception {
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        handleResult(params, requestParams);
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

            System.out.println("商户订单号："+out_trade_no);
            System.out.println("支付宝交易号："+trade_no);
            System.out.println("交易状态："+trade_status);
            // 修改数据库
            return WebReturnResult.returnTipMessage(1, "Success!");
        } else {
            return WebReturnResult.returnTipMessage(0, "Failed!");

        }
    }

    private void handleResult(Map<String, String> params, Map<String, String[]> requestParams) {
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
    }

}
