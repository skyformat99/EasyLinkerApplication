package com.easylinker.proxy.server.app.config.thirdparty;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ALiSMSSender {
    @Value("${ali.sms.accessKeyId}")
    String accessKeyId;
    @Value("${ali.sms.accessKeySecret}")
    String accessKeySecret;
    @Value("${ali.sms.template}")
    String template;
    @Value("${ali.sms.signName}")
    String signName;
    private static final Logger logger = LoggerFactory.getLogger(ALiSMSSender.class);
    public boolean sendSms(String to, String code) throws ClientException {
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "Dysmsapi", "dysmsapi.aliyuncs.com");
        IAcsClient acsClient = new DefaultAcsClient(profile);
        SendSmsRequest request = new SendSmsRequest();
        request.setMethod(MethodType.POST);
        request.setSignName(signName);
        request.setTemplateCode(template);
        request.setPhoneNumbers(to);
        request.setTemplateParam("{\"code\":\" " + code + " \", \"product\": \"EasyLinker\"}");
        SendSmsResponse response = acsClient.getAcsResponse(request);
        if (response.getCode() != null && response.getCode().equals("OK")) {
            System.out.println("发送成功！" + "key:" + to + " Value:" + code);
            logger.info("验证码发送成功：手机号：{},验证码：{}", to, code);
            return true;
        } else {
            logger.error("验证码发送失败：错误码：{},请求ID：{},错误信息：{}", response.getCode(), response.getRequestId(), response.getMessage());
            return false;
        }
    }
}

