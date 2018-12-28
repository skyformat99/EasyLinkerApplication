package com.easyiot.easylinker.service.proxy.config.mvc;

import com.easyiot.easylinker.service.proxy.config.jwt.JwtRBACHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Autowired
    JwtRBACHandlerInterceptor jwtRBACHandlerInterceptor;

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/**");
        super.addResourceHandlers(registry);
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtRBACHandlerInterceptor).addPathPatterns("/api/v_1_0/**");
        super.addInterceptors(registry);
    }
}