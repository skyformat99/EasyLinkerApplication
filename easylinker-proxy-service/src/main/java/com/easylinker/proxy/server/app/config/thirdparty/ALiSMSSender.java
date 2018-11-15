package com.easylinker.proxy.server.app.config.thirdparty;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ALiSMSSender {
    private IAcsClient acsClient;
    private SendSmsRequest request;
    @Value("${ali.sms.accessKeyId}")
    String accessKeyId;
    @Value("${ali.sms.accessKeySecret}")
    String accessKeySecret;
    @Value("${ali.sms.template}")
    String template;
    @Value("${ali.sms.signName}")
    String signName;

    /**
     * 直接在构造函数中初始化参数
     */
    public ALiSMSSender() {
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "Dysmsapi", "dysmsapi.aliyuncs.com");
        acsClient = new DefaultAcsClient(profile);
        request = new SendSmsRequest();
        request.setMethod(MethodType.POST);
        request.setSignName(signName);
        request.setTemplateCode(template);

    }

//
//    static final String accessKeyId = "LTAIqzQNsdImTdZO";
//    static final String accessKeySecret = "0O4gBo6hOv2RpEDXP44p4kvR9jK3j9";
//    String template = "SMS_14235739";
//    String signName = "爱卓网络科技";
    public boolean sendSms(String to, String code) throws ClientException {
        request.setPhoneNumbers(to);
        request.setTemplateParam("{\"code\":\" " + code + " \", \"product\": \"EasyLinker\"}");
        SendSmsResponse response = acsClient.getAcsResponse(request);
        if (response.getCode() != null && response.getCode().equals("OK")) {
            System.out.println("发送成功！" + "key:" + to + " Value:" + code);
            return true;
        } else {
            System.out.println("发送失败！" + "key:" + to + " Value:" + code);
            return false;
        }
    }
}

