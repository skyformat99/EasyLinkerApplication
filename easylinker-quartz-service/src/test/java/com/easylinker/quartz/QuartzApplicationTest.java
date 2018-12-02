package com.easylinker.quartz;


import com.easylinker.quartz.service.HelloJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class QuartzApplicationTest {

    @Autowired
    private Scheduler scheduler;

    @Test
    public void main() throws SchedulerException {

        JobDetail detail = JobBuilder.newJob(HelloJob.class)
                .withIdentity("testJob", "defalt").build();
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        triggerBuilder.withIdentity("testTrigger", "defalt");
        triggerBuilder.startNow();
        triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?"));
        CronTrigger cronTrigger = (CronTrigger) triggerBuilder.build();

        scheduler.scheduleJob(detail, cronTrigger);

    }
}
