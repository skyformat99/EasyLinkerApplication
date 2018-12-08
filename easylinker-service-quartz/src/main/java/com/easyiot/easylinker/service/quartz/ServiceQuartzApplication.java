package com.easyiot.easylinker.service.quartz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = "com.easylinker.quartz.mapper")
@SpringBootApplication
public class ServiceQuartzApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceQuartzApplication.class, args);
    }
}
