package com.easylinker.proxy.server.app.config.mvcweb;

import com.easylinker.proxy.server.app.config.jwt.JwtFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class JwtFilterConfig {

    /**
     * 配置过滤器
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean<Filter> someFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(JwtFilter());
        registration.addUrlPatterns("/api/v1/*");
        return registration;
    }

    /**
     * 创建一个bean
     *
     * @return
     */
    @Bean(name = "JwtFilter")
    public Filter JwtFilter() {
        return new JwtFilter();
    }

}
