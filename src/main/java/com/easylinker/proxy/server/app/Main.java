package com.easylinker.proxy.server.app;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    @Qualifier("Scheduler")
    Scheduler scheduler;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

    }

//
//    private void addJob(MyJob scheduleJob) throws Exception {
//        if (scheduler.isShutdown()) scheduler.start();
//        JobDetail jobDetail = JobBuilder.newJob(MyJob.class)
//                .withIdentity(scheduleJob.getId().toString(), scheduleJob.getJobGroup()).build();
//        //表达式调度构建器(即任务执行的时间)
//        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
//        //按新的cronExpression表达式构建一个新的trigger
//        CronTrigger trigger = TriggerBuilder.newTrigger()
//                .withIdentity(scheduleJob.getId().toString(), scheduleJob.getJobGroup())
//                .withSchedule(scheduleBuilder).build();
//
//        scheduler.scheduleJob(jobDetail, trigger);
//
//    }


}
