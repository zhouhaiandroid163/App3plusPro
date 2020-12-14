package com.zjw.apps3pluspro.module.device.entity;


import com.zjw.apps3pluspro.utils.MyUtils;

/**
 * 会议提醒
 */
public class MettingModel {

    public static final String KEY_YEAR = "metting_year";
    public static final String KEY_MONTH = "metting_mon";
    public static final String KEY_DAY = "metting_day";
    public static final String KEY_HOUR = "metting_hour";
    public static final String KEY_MIN = "metting_min";
    public static final String KEY_METTING = "metting";

    int MettingYear;
    int MettingMonth;
    int MettingDay;
    int MettingHour;
    int MettingMin;
    boolean MettingEnable;

    public int getMettingYear() {
        return MettingYear;
    }

    public void setMettingYear(int mettingYear) {
        MettingYear = mettingYear;
    }

    public int getMettingMonth() {
        return MettingMonth;
    }

    public void setMettingMonth(int mettingMonth) {
        MettingMonth = mettingMonth;
    }

    public int getMettingDay() {
        return MettingDay;
    }

    public void setMettingDay(int mettingDay) {
        MettingDay = mettingDay;
    }

    public int getMettingHour() {
        return MettingHour;
    }

    public void setMettingHour(int mettingHour) {
        MettingHour = mettingHour;
    }

    public int getMettingMin() {
        return MettingMin;
    }

    public void setMettingMin(int mettingMin) {
        MettingMin = mettingMin;
    }

    public boolean isMettingEnable() {
        return MettingEnable;
    }

    public void setMettingEnable(boolean mettingEnable) {
        MettingEnable = mettingEnable;
    }

    public MettingModel() {
        super();
    }

    @Override
    public String toString() {
        return "MettingModel{" +
                "MettingYear=" + MettingYear +
                ", MettingMonth=" + MettingMonth +
                ", MettingDay=" + MettingDay +
                ", MettingHour=" + MettingHour +
                ", MettingMin=" + MettingMin +
                ", MettingEnable=" + MettingEnable +
                '}';
    }

    public MettingModel(int year, int month, int day, int hour, int min, boolean enabnle) {

        super();
        setMettingYear(year);
        setMettingMonth(month);
        setMettingDay(day);
        setMettingHour(hour);
        setMettingMin(min);
        setMettingEnable(enabnle);

    }

    public String getDate() {
        return String.valueOf(2000 + MettingYear) + "-" + MyUtils.MyFormatTime(MettingMonth) + "-" + MyUtils.MyFormatTime(MettingDay);
    }

    public String getTime() {
        return MyUtils.MyFormatTime(MettingHour) + ":" + MyUtils.MyFormatTime(MettingMin);
    }






}
