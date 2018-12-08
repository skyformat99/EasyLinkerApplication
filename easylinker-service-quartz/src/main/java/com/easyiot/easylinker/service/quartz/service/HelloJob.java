package com.easyiot.easylinker.service.quartz.service;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

@Slf4j
public class HelloJob implements BaseJob {

    public HelloJob(){

    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            log.info("HelloJob,Easylinker V3 计划任务系统，当前时间：{}, Shudeuler:", DateUtil.now(), context.getScheduler().getSchedulerName());
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
