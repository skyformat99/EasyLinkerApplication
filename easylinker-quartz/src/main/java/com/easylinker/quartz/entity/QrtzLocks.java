package com.easylinker.quartz.entity;

import javax.persistence.*;

@Table(name = "qrtz_locks")
public class QrtzLocks {
    @Id
    @Column(name = "SCHED_NAME")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private String schedName;

    @Id
    @Column(name = "LOCK_NAME")
    private String lockName;

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
     * @return LOCK_NAME
     */
    public String getLockName() {
        return lockName;
    }

    /**
     * @param lockName
     */
    public void setLockName(String lockName) {
        this.lockName = lockName;
    }
}