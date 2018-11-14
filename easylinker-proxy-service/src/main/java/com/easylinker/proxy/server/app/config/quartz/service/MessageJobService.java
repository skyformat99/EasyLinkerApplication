package com.easylinker.proxy.server.app.config.quartz.service;

import com.easylinker.proxy.server.app.config.quartz.MessageJob;
import com.easylinker.proxy.server.app.config.quartz.dao.MessageJobRepository;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     *
     * @param userId
     * @param scheduleJob
     * @return
     * @throws Exception
     */
    @Transactional
    public Job add(Long userId, MessageJob scheduleJob) throws SchedulerException {


        if (scheduler.isShutdown()) scheduler.start();
        JobDetail jobDetail = JobBuilder.newJob(MessageJob.class)
                .withIdentity(scheduleJob.getId().toString(), scheduleJob.getJobGroup()).build();
        jobDetail.getJobDataMap().put("jobJson", scheduleJob.getJobJson());

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(scheduleJob.getId().toString(), scheduleJob.getJobGroup())
                .withSchedule(CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression())).build();
        //持久化道业务逻辑数据库里面
        scheduleJob.setUserId(userId);
        scheduleJob.setStarted(true);
        messageJobRepository.save(scheduleJob);
        scheduler.scheduleJob(jobDetail, trigger);
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
            messageJob.setStarted(true);
            messageJobRepository.save(messageJob);
            scheduler.resumeJob(JobKey.jobKey(jobKey.toString(), "JOB_GROUP"));
        }


    }

    /**
     * 获取JOB列表
     *
     * @param userId
     * @param pageable
     * @return
     */

    public Page<MessageJob> list(Long userId, Pageable pageable) {
        return messageJobRepository.findAllByUserId(userId, pageable);
    }
}
