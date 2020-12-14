package com.zjw.apps3pluspro.module.device.entity;


import com.zjw.apps3pluspro.utils.MyUtils;

/**
 * 喝水提醒
 */
public class WaterModel {

    public static final String KEY_PERIOD = "drinking_period";
    public static final String KEY_STARTH = "drinking_starth";
    public static final String KEY_STARTM = "drinking_startm";
    public static final String KEY_ENDH = "drinking_endh";
    public static final String KEY_ENDM = "drinking_endm";
    public static final String KEY_DRINKING = "drinking";

    int DrinkingStartHourTime;
    int DrinkingStartMinTime;
    int DrinkingEndHourTime;
    int DrinkingEndMinTime;
    int DrinkingCycleTime;
    boolean DrinkingEnable;


    public int getDrinkingStartHourTime() {
        return DrinkingStartHourTime;
    }

    public void setDrinkingStartHourTime(int drinkingStartHourTime) {
        DrinkingStartHourTime = drinkingStartHourTime;
    }

    public int getDrinkingStartMinTime() {
        return DrinkingStartMinTime;
    }

    public void setDrinkingStartMinTime(int drinkingStartMinTime) {
        DrinkingStartMinTime = drinkingStartMinTime;
    }

    public int getDrinkingEndHourTime() {
        return DrinkingEndHourTime;
    }

    public void setDrinkingEndHourTime(int drinkingEndHourTime) {
        DrinkingEndHourTime = drinkingEndHourTime;
    }

    public int getDrinkingEndMinTime() {
        return DrinkingEndMinTime;
    }

    public void setDrinkingEndMinTime(int drinkingEndMinTime) {
        DrinkingEndMinTime = drinkingEndMinTime;
    }

    public int getDrinkingCycleTime() {
        return DrinkingCycleTime;
    }

    public void setDrinkingCycleTime(int drinkingCycleTime) {
        DrinkingCycleTime = drinkingCycleTime;
    }

    public boolean isDrinkingEnable() {
        return DrinkingEnable;
    }

    public void setDrinkingEnable(boolean drinkingEnable) {
        DrinkingEnable = drinkingEnable;
    }

    public WaterModel() {
        super();
    }

    public WaterModel(int start_h_time, int start_m_time, int end_h_time, int end_m_time, int cycle_time, boolean enabnle) {

        super();
        setDrinkingStartHourTime(start_h_time);
        setDrinkingStartMinTime(start_m_time);
        setDrinkingEndHourTime(end_h_time);
        setDrinkingEndMinTime(end_m_time);
        setDrinkingCycleTime(cycle_time);
        setDrinkingEnable(enabnle);

    }

    public String getStartTime() {
        return MyUtils.MyFormatTime(DrinkingStartHourTime) + ":" + MyUtils.MyFormatTime(DrinkingStartMinTime);
    }

    public String getEndTime() {
        return MyUtils.MyFormatTime(DrinkingEndHourTime) + ":" + MyUtils.MyFormatTime(DrinkingEndMinTime);
    }


    /**
     * 提醒设置，比较时间大小
     */
    public boolean isOldTime() {

        boolean result = false;

        if (DrinkingStartHourTime > DrinkingEndHourTime) {
            return true;
        } else if (DrinkingStartHourTime == DrinkingEndHourTime && DrinkingStartMinTime > DrinkingEndMinTime) {
            return true;
        }

        return result;
    }


}
