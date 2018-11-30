package com.easylinker.quartz.entity;

import javax.persistence.*;

@Table(name = "qrtz_scheduler_state")
public class QrtzSchedulerState {
    @Id
    @Column(name = "SCHED_NAME")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private String schedName;

    @Id
    @Column(name = "INSTANCE_NAME")
    private String instanceName;

    @Column(name = "LAST_CHECKIN_TIME")
    private Long lastCheckinTime;

    @Column(name = "CHECKIN_INTERVAL")
    private Long checkinInterval;

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
     * @return INSTANCE_NAME
     */
    public String getInstanceName() {
        return instanceName;
    }

    /**
     * @param instanceName
     */
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    /**
     * @return LAST_CHECKIN_TIME
     */
    public Long getLastCheckinTime() {
        return lastCheckinTime;
    }

    /**
     * @param lastCheckinTime
     */
    public void setLastCheckinTime(Long lastCheckinTime) {
        this.lastCheckinTime = lastCheckinTime;
    }

    /**
     * @return CHECKIN_INTERVAL
     */
    public Long getCheckinInterval() {
        return checkinInterval;
    }

    /**
     * @param checkinInterval
     */
    public void setCheckinInterval(Long checkinInterval) {
        this.checkinInterval = checkinInterval;
    }
}