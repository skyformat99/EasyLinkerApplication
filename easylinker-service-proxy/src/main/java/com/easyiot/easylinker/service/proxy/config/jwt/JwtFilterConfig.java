package com.easyiot.easylinker.service.proxy.config.jwt;

import com.easyiot.easylinker.service.proxy.config.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class JwtFilterConfig {
    private final RedisService redisService;

    @Autowired
    public JwtFilterConfig(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 配置过滤器
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean<Filter> someFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(JwtFilter());
        registration.addUrlPatterns("/api/v_1_0/*");
        return registration;
    }

    /**
     * 创建一个bean
     *
     * @return
     */
    @Bean(name = "JwtFilter")
    public Filter JwtFilter() {
        return new JwtFilter(redisService);
    }

}
