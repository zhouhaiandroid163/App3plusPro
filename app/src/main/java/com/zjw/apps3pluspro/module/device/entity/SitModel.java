package com.zjw.apps3pluspro.module.device.entity;

import com.zjw.apps3pluspro.utils.MyUtils;

/**
 * 吃药提醒
 */
public class SitModel {

    public static final String KEY_PERIOD = "sit_period";
    public static final String KEY_STARTH = "sit_starth";
    public static final String KEY_STARTM = "sit_startm";
    public static final String KEY_ENDH = "sit_endh";
    public static final String KEY_ENDM = "sit_endm";
    public static final String KEY_LONG_SIT = "long_sit";

    int SitStartHourTime;
    int SitStartMinTime;
    int SitEndHourTime;
    int SitEndMinTime;
    int SitCycleTime;
    boolean SitEnable;

    public int getSitStartHourTime() {
        return SitStartHourTime;
    }

    public void setSitStartHourTime(int sitStartHourTime) {
        SitStartHourTime = sitStartHourTime;
    }

    public int getSitStartMinTime() {
        return SitStartMinTime;
    }

    public void setSitStartMinTime(int sitStartMinTime) {
        SitStartMinTime = sitStartMinTime;
    }

    public int getSitEndHourTime() {
        return SitEndHourTime;
    }

    public void setSitEndHourTime(int sitEndHourTime) {
        SitEndHourTime = sitEndHourTime;
    }

    public int getSitEndMinTime() {
        return SitEndMinTime;
    }

    public void setSitEndMinTime(int sitEndMinTime) {
        SitEndMinTime = sitEndMinTime;
    }

    public int getSitCycleTime() {
        return SitCycleTime;
    }

    public void setSitCycleTime(int sitCycleTime) {
        SitCycleTime = sitCycleTime;
    }

    public boolean isSitEnable() {
        return SitEnable;
    }

    public void setSitEnable(boolean sitEnable) {
        SitEnable = sitEnable;
    }

    @Override
    public String toString() {
        return "SitModel{" +
                "SitStartHourTime=" + SitStartHourTime +
                ", SitStartMinTime=" + SitStartMinTime +
                ", SitEndHourTime=" + SitEndHourTime +
                ", SitEndMinTime=" + SitEndMinTime +
                ", SitCycleTime=" + SitCycleTime +
                ", SitEnable=" + SitEnable +
                '}';
    }

    public SitModel() {
        super();
    }

    public SitModel(int start_h_time, int start_m_time, int end_h_time, int end_m_time, int cycle_time, boolean enabnle) {

        super();
        setSitStartHourTime(start_h_time);
        setSitStartMinTime(start_m_time);
        setSitEndHourTime(end_h_time);
        setSitEndMinTime(end_m_time);
        setSitCycleTime(cycle_time);
        setSitEnable(enabnle);

    }

    public String getStartTime() {
        return MyUtils.MyFormatTime(SitStartHourTime) + ":" + MyUtils.MyFormatTime(SitStartMinTime);
    }

    public String getEndTime() {
        return MyUtils.MyFormatTime(SitEndHourTime) + ":" + MyUtils.MyFormatTime(SitEndMinTime);
    }





    /**
     * 提醒设置，比较时间大小
     */
    public boolean isOldTime() {

        boolean result = false;

        if (SitStartHourTime > SitEndHourTime) {
            return true;
        } else if (SitStartHourTime == SitEndHourTime && SitStartMinTime > SitEndMinTime) {
            return true;
        }

        return result;
    }


}
