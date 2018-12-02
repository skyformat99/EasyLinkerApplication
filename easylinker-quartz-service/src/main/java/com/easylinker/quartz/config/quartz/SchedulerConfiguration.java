package com.easylinker.quartz.config.quartz;

import org.quartz.Scheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 *
 * @Author:   zhaolei
 * @Version   1.0.0
 *
 * @Description: quartz配置
 * @Date:     2018/11/30 15:16
 * Copyright (C), 2016-2018, EasyLinker V3
 */

@Configuration
public class SchedulerConfiguration {


    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    /**
     * 初始化quartz监听器
     * @return
     */
    @Bean
    public QuartzInitializerListener listener(){
        return new QuartzInitializerListener();
    }

    /**
     * 通过SchedulerFactoryBean获取Scheduler的实例
     * @return
     */
    @Bean
    public Scheduler scheduler(){
        return schedulerFactoryBean.getScheduler();
    }
}
