package com.zjw.apps3pluspro.module.device.entity;

/**
 * 闹钟实体类
 */
public class AlarmClockItem {
    public int id;
    public String timeAlarm;
    public int repeat;
    public String weekDay;

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public String getTimeAlarm() {
        return timeAlarm;
    }

    public void setTimeAlarm(String timeAlarm) {
        this.timeAlarm = timeAlarm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    @Override
    public String toString() {
        return "AlarmClockItem [id=" + id + ", timeAlarm=" + timeAlarm
                + ", repeat=" + repeat + ", weekDay=" + weekDay + "]";
    }
}
