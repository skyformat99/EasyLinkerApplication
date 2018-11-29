package com.easylinker.quartz.entity;

import javax.persistence.*;

@Table(name = "qrtz_calendars")
public class QrtzCalendars {
    @Id
    @Column(name = "SCHED_NAME")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private String schedName;

    @Id
    @Column(name = "CALENDAR_NAME")
    private String calendarName;

    @Column(name = "CALENDAR")
    private byte[] calendar;

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
     * @return CALENDAR
     */
    public byte[] getCalendar() {
        return calendar;
    }

    /**
     * @param calendar
     */
    public void setCalendar(byte[] calendar) {
        this.calendar = calendar;
    }
}