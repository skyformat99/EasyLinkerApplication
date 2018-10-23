package com.easylinker.proxy.server.app;

import com.easylinker.proxy.server.app.config.quartz.BaseJob;
import com.easylinker.proxy.server.app.config.quartz.MyJob;
import com.easylinker.proxy.server.app.vertx.vertxmqtt.MqttServerRunner;
import com.easylinker.proxy.server.app.vertx.vertxmqtt.VertXMqttServer;
import io.vertx.core.Verticle;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Base64;

@SpringBootApplication
public class Main implements CommandLineRunner {
    @Autowired
    @Qualifier("Scheduler")
    Scheduler scheduler;

    @Bean
    public Verticle runMqttServer() {
        return MqttServerRunner.run(new VertXMqttServer());
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
//        MyJob myJob = new MyJob();
//        myJob.setCronExpression("* * * * * ? *");
//        addJob(myJob);

    }


    private void addJob(MyJob scheduleJob) throws Exception {
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
