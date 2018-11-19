package com.easylinker.proxy.server.app.config.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

import java.util.UUID;

/**
 * 定时任务具体业务接口
 * 要实现业务可以在这里加入逻辑
 * 比如现在实现的是:定时发送消息给设备
 * context.getJobDetail().getJobDataMap().getLongValue("deviceId"):获取设备的ID
 * context.getTrigger().getJobDataMap().getString("jobJson"):获取发送的JSON数据
 * 下面可以直发送了
 */
@DisallowConcurrentExecution
public class MessageJob extends BaseJob {
    private Long id = System.currentTimeMillis();
    private String jobName = UUID.randomUUID().toString().replace("-", "").substring(0, 10);    //任务名
    private String jobGroup = "JOB_GROUP";    //任务组
    private String cronExpression;    //cron表达式
    private String jobDescription = "JOB";    //描述
    private String jobJson;
    private Boolean started=false;
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getStarted() {
        return started;
    }

    public void setStarted(Boolean started) {
        this.started = started;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobJson() {
        return jobJson;
    }

    public void setJobJson(String jobJson) {
        this.jobJson = jobJson;
    }

    @Override
    public void execute(JobExecutionContext context) {

        System.out.println("JOB开始运行:" + getClass().getName()+"_"+context.getJobDetail().getJobDataMap().toString());
    }
}
