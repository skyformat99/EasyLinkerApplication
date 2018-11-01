package com.easylinker.proxy.server.app.config.quartz;


import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

/**
 * This JobFactory auto wires automatically the created quartz bean with spring @Autowired dependencies.
 * Quartz的工厂
 *
 */

@Component
public class QuartzJobFactory extends AdaptableJobFactory {

    private final AutowireCapableBeanFactory capableBeanFactory;

    @Autowired
    public QuartzJobFactory(AutowireCapableBeanFactory capableBeanFactory) {
        this.capableBeanFactory = capableBeanFactory;
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        // 调用父类的方法
        Object jobInstance = super.createJobInstance(bundle);
        // 进行注入
        capableBeanFactory.autowireBean(jobInstance);
        return jobInstance;
    }
}