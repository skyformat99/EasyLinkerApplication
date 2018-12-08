package com.easyiot.easylinker.service.proxy.config.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.easyiot.easylinker.service.proxy.config.sms.aliyunsms.AliYunSMSConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @Author:   zhaolei
 * @Version   1.0.0
 *
 * @Description: 短信参数配置
 * @Date:     2018/11/29 23:30
 * Copyright (C), 2016-2018, EasyLinker V3
 */

@Configuration
public class SMSConfiguration {

    /**
     * 阿里云短信
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "sms.aliyun")
    public AliYunSMSConfig aliYunSMSConfig(){
        return new AliYunSMSConfig();
    }

    @Bean
    public IAcsClient iAcsClient(AliYunSMSConfig config) throws ClientException {
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        IClientProfile profile = DefaultProfile.getProfile(
                config.getRegionId(),
                config.getAccessKeyId(),
                config.getAccessKeySecret()
        );
        DefaultProfile.addEndpoint(
                config.getRegionId(),
                config.getProduct(),
                config.getDomain()
        );
        return new DefaultAcsClient(profile);
    }


}
