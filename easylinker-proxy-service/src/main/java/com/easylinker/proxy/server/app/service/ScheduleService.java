package com.easylinker.proxy.server.app.service;

import com.easylinker.proxy.server.app.config.quartz.MyJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

    private Scheduler scheduler;

    public ScheduleService(@Qualifier("Scheduler") Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void addJob(MyJob scheduleJob) throws Exception {
        if (scheduler.isShutdown()) scheduler.start();
        JobDetail jobDetail = JobBuilder.newJob(MyJob.class)
                .withIdentity(scheduleJob.getId().toString(), scheduleJob.getJobGroup()).build();
        //表达式调度构建器(即任务执行的时间)
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
        //按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(scheduleJob.getId().toString(), scheduleJob.getJobGroup())
                .withSchedule(scheduleBuilder).build();

        scheduler.scheduleJob(jobDetail, trigger);

    }
}
