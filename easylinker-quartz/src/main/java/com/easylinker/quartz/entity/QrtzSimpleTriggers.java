package com.easylinker.quartz.entity;

import javax.persistence.*;

@Table(name = "qrtz_simple_triggers")
public class QrtzSimpleTriggers {
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

    @Column(name = "REPEAT_COUNT")
    private Long repeatCount;

    @Column(name = "REPEAT_INTERVAL")
    private Long repeatInterval;

    @Column(name = "TIMES_TRIGGERED")
    private Long timesTriggered;

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
     * @return REPEAT_COUNT
     */
    public Long getRepeatCount() {
        return repeatCount;
    }

    /**
     * @param repeatCount
     */
    public void setRepeatCount(Long repeatCount) {
        this.repeatCount = repeatCount;
    }

    /**
     * @return REPEAT_INTERVAL
     */
    public Long getRepeatInterval() {
        return repeatInterval;
    }

    /**
     * @param repeatInterval
     */
    public void setRepeatInterval(Long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    /**
     * @return TIMES_TRIGGERED
     */
    public Long getTimesTriggered() {
        return timesTriggered;
    }

    /**
     * @param timesTriggered
     */
    public void setTimesTriggered(Long timesTriggered) {
        this.timesTriggered = timesTriggered;
    }
}