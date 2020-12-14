package com.zjw.apps3pluspro.utils;

/**
 * Created by zjw on 2018/3/14.
 */

public class DefaultVale {


    //默认注册时间
    public final static String USER_DEFULT_REGISTER_TIME = "2017-03-01";

    //心率范围
    public static final int USER_HEART_MIN = 30;
    public static final int USER_HEART_MAX = 280;

    //收缩压范围
    public static final int USER_SYSTOLIC_MIN = 40;
    public static final int USER_SYSTOLIC_MAX = 280;

    //舒张压范围
    public static final int USER_DIASTOLIC_MIN = 30;
    public static final int USER_DIASTOLIC_MAX = 200;

    //默认校准值
    public static final int USER_BP_LEVEL = -1;
    public static final int USER_HEART = 70;
    public static final int USER_SYSTOLIC = 120;
    public static final int USER_DIASTOLIC = 70;

    //收缩压血压等级
    public static final int USER_SYSTOLIC_STATE0 = 90;
    public static final int USER_SYSTOLIC_STATE1 = 100;
    public static final int USER_SYSTOLIC_STATE2 = 120;
    public static final int USER_SYSTOLIC_STATE3 = 140;
    public static final int USER_SYSTOLIC_STATE4 = 160;

    //舒张压血压等级
    public static final int USER_DIASTOLIC_STATE0 = 55;
    public static final int USER_DIASTOLIC_STATE1 = 65;
    public static final int USER_DIASTOLIC_STATE2 = 70;
    public static final int USER_DIASTOLIC_STATE3 = 80;
    public static final int USER_DIASTOLIC_STATE4 = 90;

    public static final String USER_BIRTHDAY = "1993-10-07";
    public static final int USER_HEIGHT = 170;
    public static final int USER_WEIGHT = 65;
    public static final int USER_SEX = 1;
    public static final int USER_WEARWAY = 1;

    public static final int USER_SPORT_TARGET = 8000;
    public static final int USER_SLEEP_TARGET = 480;

    public static final String USER_WEAR_WAY = "L";


    public static final int DEVICE_SCREEN_BRIGHTNESS = 3;
    public static final int DEVICE_BRIGHENSS_TIME = 5;


    public static int getSystoLicLevle(int state) {

        if (state == 0) {

            return USER_SYSTOLIC_STATE0;

        } else if (state == 1) {
            return USER_SYSTOLIC_STATE1;
        } else if (state == 3) {

            return USER_SYSTOLIC_STATE3;

        } else if (state == 4) {

            return USER_SYSTOLIC_STATE4;

        } else {
            return USER_SYSTOLIC_STATE2;
        }


    }

    public static int getDiastolicLevle(int state) {

        if (state == 0) {

            return USER_DIASTOLIC_STATE0;

        } else if (state == 1) {
            return USER_DIASTOLIC_STATE1;
        } else if (state == 3) {

            return USER_DIASTOLIC_STATE3;

        } else if (state == 4) {

            return USER_DIASTOLIC_STATE4;

        } else {
            return USER_DIASTOLIC_STATE2;
        }


    }


}
