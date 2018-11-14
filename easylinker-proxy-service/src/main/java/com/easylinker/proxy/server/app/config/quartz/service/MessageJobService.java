package com.easylinker.proxy.server.app.config.quartz.service;

import com.easylinker.proxy.server.app.config.quartz.MessageJob;
import com.easylinker.proxy.server.app.config.quartz.dao.MessageJobRepository;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageJobService {
    private final MessageJobRepository messageJobRepository;

    private Scheduler scheduler;

    public MessageJobService(MessageJobRepository jobRepository,
                             @Qualifier("Scheduler") Scheduler scheduler) {
        this.messageJobRepository = jobRepository;
        this.scheduler = scheduler;
    }

    /**
     * 添加
     *
     * @param scheduleJob
     * @return
     * @throws Exception
     */
    @Transactional
    public Job add(MessageJob scheduleJob) throws SchedulerException {


        if (scheduler.isShutdown()) scheduler.start();
//        JobDetail jobDetail = JobBuilder.newJob(MessageJob.class)
//                .withIdentity(scheduleJob.getId().toString(), scheduleJob.getJobGroup()).build();
//
//        CronTrigger trigger = TriggerBuilder.newTrigger()
//                .withIdentity(scheduleJob.getId().toString(), scheduleJob.getJobGroup())
//                .withSchedule(CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression())).build();
        //持久化道业务逻辑数据库里面
        messageJobRepository.save(scheduleJob);
        scheduler.scheduleJob(JobBuilder.newJob(MessageJob.class)
                        .withIdentity(scheduleJob.getId().toString(), scheduleJob.getJobGroup()).build(),
                TriggerBuilder.newTrigger()
                        .withIdentity(scheduleJob.getId().toString(), scheduleJob.getJobGroup())
                        .withSchedule(CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression())).build());
        return scheduleJob;

    }

    /**
     * @param jobKey
     * @throws SchedulerException
     */
    public void delete(Long jobKey) throws SchedulerException {
        MessageJob messageJob = messageJobRepository.findTopById(jobKey);
        if (messageJob != null) {
            messageJobRepository.delete(messageJob);
            scheduler.deleteJob(JobKey.jobKey(jobKey.toString(), "JOB_GROUP"));
        }


    }

    /**
     * @param jobKey
     * @throws SchedulerException
     */
    public void pause(Long jobKey) throws SchedulerException {
        MessageJob messageJob = messageJobRepository.findTopById(jobKey);
        if (messageJob != null && messageJob.getStarted()) {
            messageJob.setStarted(false);
            messageJobRepository.save(messageJob);
            scheduler.pauseJob(JobKey.jobKey(jobKey.toString(), "JOB_GROUP"));
        }

    }

    /**
     * @param jobKey
     * @throws Exception
     */
    public void resume(Long jobKey) throws SchedulerException {
        MessageJob messageJob = messageJobRepository.findTopById(jobKey);
        if (messageJob != null && (!messageJob.getStarted())) {

            scheduler.resumeJob(JobKey.jobKey(jobKey.toString(), "JOB_GROUP"));
        }


    }
}
