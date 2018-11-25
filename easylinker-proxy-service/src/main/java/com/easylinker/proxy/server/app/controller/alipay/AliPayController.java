package com.easylinker.proxy.server.app.controller.alipay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.easylinker.proxy.server.app.config.alipay.AliPayConfig;
import com.easylinker.proxy.server.app.config.mvc.WebReturnResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.easylinker.proxy.server.app.config.alipay.AliPayConfig.signtype;

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
    private String signType = signtype;

    /**
     * 支付请求
     *
     * @throws Exception
     */
    @RequestMapping("/pay")
    public void pay(HttpServletResponse response) throws Exception {


        // 封装请求客户端
        AlipayClient client = new DefaultAlipayClient(url, appId, privateKey, format, charset, publicKey, signType);

        // 支付请求
        AlipayTradePagePayRequest alipayTradePagePayRequest = new AlipayTradePagePayRequest();
        alipayTradePagePayRequest.setReturnUrl(returnUrl);
        alipayTradePagePayRequest.setNotifyUrl(notifyUrl);
        AlipayTradePayModel model = new AlipayTradePayModel();
        // 设置销售产品码
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        // 设置订单号
        model.setOutTradeNo(System.currentTimeMillis() + "");
        // 订单名称
        model.setSubject("充值客户端");
        // 支付总金额
        model.setTotalAmount("1");
        // 设置商品描述
        model.setBody("充值客户端");
        alipayTradePagePayRequest.setBizModel(model);
        String form = client.pageExecute(alipayTradePagePayRequest).getBody();
        // 生成表单
        response.setContentType("text/html;charset=" + charset);
        // 直接将完整的表单html输出到页面
        response.getWriter().write(form);
        response.getWriter().flush();
        response.getWriter().close();
    }

    /**
     * 同步跳转
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
        boolean signVerified = AlipaySignature.rsaCheckV1(params, publicKey, charset, signtype);

        // 返回界面
        if (signVerified) {
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
    @RequestMapping("/notifyUrl")
    public JSONObject notifyUrl(HttpServletRequest request) throws Exception {
        // 获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        handleResult(params, requestParams);
        // 调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, publicKey, charset, signtype);
        // 验证成功 更新订单信息
        // 验证成功 更新订单信息
        if (signVerified) {
            // 商户订单号
            String out_trade_no = request.getParameter("out_trade_no");
            // 交易状态
            String trade_status = request.getParameter("trade_status");
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
