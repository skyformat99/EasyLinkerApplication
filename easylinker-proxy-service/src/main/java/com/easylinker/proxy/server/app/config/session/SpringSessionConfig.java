package com.easylinker.proxy.server.app.config.session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 2_592_000)//过期时间一个月
@Configuration
public class SpringSessionConfig {
    @Bean
    public static ConfigureRedisAction configureRedisAction() {

        return ConfigureRedisAction.NO_OP;

    }
}
