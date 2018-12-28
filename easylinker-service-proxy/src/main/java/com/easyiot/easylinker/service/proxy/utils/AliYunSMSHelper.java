package com.easyiot.easylinker.service.proxy.utils;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.easyiot.easylinker.service.proxy.model.SMSParamEntity;
import com.easyiot.easylinker.service.proxy.config.sms.aliyunsms.AliYunSMSConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 *
 * @Author:   zhaolei
 * @Version   1.0.0
 *
 * @Description: 阿里云短信发送工具类
 * @Date:     2018/11/29 23:30
 * Copyright (C), 2016-2018, EasyLinker V3
 */

@Slf4j
@Component
public class AliYunSMSHelper {

    private final AliYunSMSConfig aliYunSMSConfig;
    private final IAcsClient iAcsClient;
    @Autowired
    public AliYunSMSHelper(AliYunSMSConfig aliYunSMSConfig, IAcsClient iAcsClient){
        this.aliYunSMSConfig = aliYunSMSConfig;
        this.iAcsClient = iAcsClient;
    }

    /**
     * 验证码短信
     * @param toPhone
     * @param code
     * @return
     * @throws ClientException
     */
    public boolean sendSms(String toPhone, String code) throws ClientException {
        SendSmsRequest request = new SendSmsRequest();
        request.setMethod(MethodType.POST);
        request.setSignName(aliYunSMSConfig.getSignName());
        request.setTemplateCode(aliYunSMSConfig.getTemplate());
        request.setPhoneNumbers(toPhone);

        SMSParamEntity smsParamEntity = new SMSParamEntity();
        smsParamEntity.setCode(code);
        smsParamEntity.setProduct("EasyLinker");
        String param = JSONObject.toJSONString(smsParamEntity);

        request.setTemplateParam(param);
        SendSmsResponse response = iAcsClient.getAcsResponse(request);

        if (StringUtils.equals(response.getCode(), aliYunSMSConfig.getCode())) {
            log.info("[阿里云短信发送成功] Phone:{},Code:{}", toPhone, code);
            return true;
        } else {
            log.error("[阿里云短信发送失败] Code:{},Message:{},RequestId:{}", response.getCode(), response.getRequestId());
            return false;
        }
    }
}
