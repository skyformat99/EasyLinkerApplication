package com.easyiot.easylinker.service.quartz.config.druid;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @Author:   zhaolei
 * @Version   1.0.0
 *
 * @Description: Druid Web配置
 * @Date:     2018/11/28 23:42
 * Copyright (C), 2016-2018, EasyLinker V3
 */

@Slf4j
@Configuration
public class DruidConfiguration {

    /**
     * StatViewServlet配置
     * @return
     */
    @Bean
    public ServletRegistrationBean druidServlet(){
        log.info("init Druid Servlet Configuration");

        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
        servletRegistrationBean.setServlet(new StatViewServlet());
        servletRegistrationBean.addUrlMappings("/druid/*");
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("resetEnable", "false");
        initParameters.put("loginUsername", "admin");
        initParameters.put("loginPassword", "12345");
        initParameters.put("allow", "");  // 白名单
//        initParameters.put("deny", "");  //黑名单
        servletRegistrationBean.setInitParameters(initParameters);
        return servletRegistrationBean;
    }

    /**
     * WebStatFilter配置
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        return filterRegistrationBean;
    }
}
