package com.easylinker.proxy.server.app.config.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * job父类，包含一个抽象函方法，将实现推迟到具体的子类
 */
@Component
public abstract class BaseJob implements Job, Serializable {

    @Override
    public void execute(JobExecutionContext context) {

    }
}