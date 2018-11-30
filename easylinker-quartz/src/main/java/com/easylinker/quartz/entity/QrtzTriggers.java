package com.easylinker.quartz.entity;

import javax.persistence.*;

@Table(name = "qrtz_triggers")
public class QrtzTriggers {
    @Id
    @Column(name = "SCHED_NAME")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private String schedName;

    @Id
    @Column(name = "TRIGGER_NAME")
    private String triggerName;

    @Id
    @Column(name = "TRIGGER_GROUP")
    private String triggerGroup;

    @Column(name = "JOB_NAME")
    private String jobName;

    @Column(name = "JOB_GROUP")
    private String jobGroup;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "NEXT_FIRE_TIME")
    private Long nextFireTime;

    @Column(name = "PREV_FIRE_TIME")
    private Long prevFireTime;

    @Column(name = "PRIORITY")
    private Integer priority;

    @Column(name = "TRIGGER_STATE")
    private String triggerState;

    @Column(name = "TRIGGER_TYPE")
    private String triggerType;

    @Column(name = "START_TIME")
    private Long startTime;

    @Column(name = "END_TIME")
    private Long endTime;

    @Column(name = "CALENDAR_NAME")
    private String calendarName;

    @Column(name = "MISFIRE_INSTR")
    private Short misfireInstr;

    @Column(name = "JOB_DATA")
    private byte[] jobData;

    /**
     * @return SCHED_NAME
     */
    public String getSchedName() {
        return schedName;
    }

    /**
     * @param schedName
     */
    public void setSchedName(String schedName) {
        this.schedName = schedName;
    }

    /**
     * @return TRIGGER_NAME
     */
    public String getTriggerName() {
        return triggerName;
    }

    /**
     * @param triggerName
     */
    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    /**
     * @return TRIGGER_GROUP
     */
    public String getTriggerGroup() {
        return triggerGroup;
    }

    /**
     * @param triggerGroup
     */
    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    /**
     * @return JOB_NAME
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * @param jobName
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * @return JOB_GROUP
     */
    public String getJobGroup() {
        return jobGroup;
    }

    /**
     * @param jobGroup
     */
    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    /**
     * @return DESCRIPTION
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return NEXT_FIRE_TIME
     */
    public Long getNextFireTime() {
        return nextFireTime;
    }

    /**
     * @param nextFireTime
     */
    public void setNextFireTime(Long nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    /**
     * @return PREV_FIRE_TIME
     */
    public Long getPrevFireTime() {
        return prevFireTime;
    }

    /**
     * @param prevFireTime
     */
    public void setPrevFireTime(Long prevFireTime) {
        this.prevFireTime = prevFireTime;
    }

    /**
     * @return PRIORITY
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * @param priority
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @return TRIGGER_STATE
     */
    public String getTriggerState() {
        return triggerState;
    }

    /**
     * @param triggerState
     */
    public void setTriggerState(String triggerState) {
        this.triggerState = triggerState;
    }

    /**
     * @return TRIGGER_TYPE
     */
    public String getTriggerType() {
        return triggerType;
    }

    /**
     * @param triggerType
     */
    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    /**
     * @return START_TIME
     */
    public Long getStartTime() {
        return startTime;
    }

    /**
     * @param startTime
     */
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    /**
     * @return END_TIME
     */
    public Long getEndTime() {
        return endTime;
    }

    /**
     * @param endTime
     */
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    /**
     * @return CALENDAR_NAME
     */
    public String getCalendarName() {
        return calendarName;
    }

    /**
     * @param calendarName
     */
    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    /**
     * @return MISFIRE_INSTR
     */
    public Short getMisfireInstr() {
        return misfireInstr;
    }

    /**
     * @param misfireInstr
     */
    public void setMisfireInstr(Short misfireInstr) {
        this.misfireInstr = misfireInstr;
    }

    /**
     * @return JOB_DATA
     */
    public byte[] getJobData() {
        return jobData;
    }

    /**
     * @param jobData
     */
    public void setJobData(byte[] jobData) {
        this.jobData = jobData;
    }
}