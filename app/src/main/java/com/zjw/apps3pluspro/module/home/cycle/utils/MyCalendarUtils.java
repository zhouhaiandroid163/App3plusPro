package com.zjw.apps3pluspro.module.home.cycle.utils;

import android.content.Context;

import com.haibin.calendarview.Calendar;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyCalendarUtils {


    public static final String ONE_TYPE = "ONE_TYPE";
    //    public static final int ONE_TEXT_COLOR = 0xFFFFFFFF;
//    public static final int ONE_BG_COLOR = 0xFFEF84BA;
    public static final int ONE_TEXT_COLOR = R.color.cycle_color_type1_text;
    public static final int ONE_BG_COLOR = R.color.cycle_color_type1_bg;


    public static final String TWO_TYPE = "TWO_TYPE";
    //    public static final int TWO_TEXT_COLOR = 0xFFC1E5B9;
//    public static final int TWO_BG_COLOR = 0xFFFFFFFF;
    public static final int TWO_TEXT_COLOR = R.color.cycle_color_type2_text;
    public static final int TWO_BG_COLOR = R.color.cycle_color_type2_bg;


    public static final String THREE_TYPE = "THREE_TYPE";
    //    public static final int THREE_TEXT_COLOR = 0xFFDCA9E4;
//    public static final int THREE_BG_COLOR = 0xFFFFFFFF;
    public static final int THREE_TEXT_COLOR = R.color.cycle_color_type3_text;
    public static final int THREE_BG_COLOR = R.color.cycle_color_type3_bg;


    public static int getTextColor(Context context, String type) {

//        int result = 0xFF000000;
        int result = R.color.cycle_color_type0_text;

        if (type == null || type.equals("")) {
            return result;

        }

        if (type.equals(ONE_TYPE)) {

            result = ONE_TEXT_COLOR;

        } else if (type.equals(TWO_TYPE)) {

            result = TWO_TEXT_COLOR;

        } else if (type.equals(THREE_TYPE)) {

            result = THREE_TEXT_COLOR;

        }

        return result;
//        return context.getResources().getColor(result);

    }

    public static int getBgColor(Context context, String type) {

//        int result = 0xFF000000;
        int result = R.color.transparent;

        if (type == null || type.equals("")) {
            return result;
        }
        if (type.equals(ONE_TYPE)) {

            result = ONE_BG_COLOR;

        } else if (type.equals(TWO_TYPE)) {

            result = TWO_BG_COLOR;

        } else if (type.equals(THREE_TYPE)) {

            result = THREE_BG_COLOR;

        }

        return result;
//        return context.getResources().getColor(result);

    }


    /**
     * 获取某月的天数
     *
     * @param year  年
     * @param month 月
     * @return 某月的天数
     */
    public static int getMonthDaysCount(int year, int month) {
        int count = 0;
        //判断大月份
        if (month == 1 || month == 3 || month == 5 || month == 7
                || month == 8 || month == 10 || month == 12) {
            count = 31;
        }

        //判断小月
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            count = 30;
        }

        //判断平年与闰年
        if (month == 2) {
            if (isLeapYear(year)) {
                count = 29;
            } else {
                count = 28;
            }
        }
        return count;
    }

    /**
     * 是否是闰年
     *
     * @param year year
     * @return 是否是闰年
     */
    public static boolean isLeapYear(int year) {
        return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int differentDaysByMillisecond(String startDate, String endDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        int startDay = 0;
        int endDay = 0;

        Date date1 = null;
        Date date2 = null;
        try {
            date1 = format.parse(startDate);
            date2 = format.parse(endDate);

            startDay = (int) (date1.getTime() / 1000);
            endDay = (int) (date2.getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.err.println(startDay);
        System.err.println(endDay);

        int days = (int) ((date1.getTime() - date2.getTime()) / (1000 * 3600 * 24));
        return days;
    }

    /**
     * 处于当前周期的第几天
     *
     * @return
     */
    public static int getCycleNumber(BleDeviceTools mBleDeviceTools, UserSetTools mUserSetTools, String date) {

        int result = 0;

        String start_time = mUserSetTools.get_nv_start_date();

        int period = mBleDeviceTools.get_device_cycle_jingqi();
        int one = mBleDeviceTools.get_device_cycle_anqunqiyi();
        int danger = mBleDeviceTools.get_device_cycle_weixianqi();
        int two = mBleDeviceTools.get_device_cycle_anquanqier();

        Map<String, Calendar> map = new HashMap<>();

        int max_lenght = period + one + danger + two;


//        System.out.println("测试日期 max_lenght = " + max_lenght + "  period = " + period + "  one = " + one + "  danger = " + danger + " two = " + two);

        int xiabiao = MyCalendarUtils.differentDaysByMillisecond(date, start_time);

        result = xiabiao % max_lenght + 1;

        return result;
    }

    /**
     * 获取当前出去
     *
     * @param mBleDeviceTools
     * @param mUserSetTools
     * @param date
     * @return
     */
    public static int getCycleState(BleDeviceTools mBleDeviceTools, UserSetTools mUserSetTools, String date) {

        int result = 0;

        int period = mBleDeviceTools.get_device_cycle_jingqi();
        int one = mBleDeviceTools.get_device_cycle_anqunqiyi();
        int danger = mBleDeviceTools.get_device_cycle_weixianqi();
        int two = mBleDeviceTools.get_device_cycle_anquanqier();

        int number_1 = period;
        int number_2 = period + one;
        int number_3 = period + one + danger;
        int number_4 = period + one + danger + two;

        int number = getCycleNumber(mBleDeviceTools, mUserSetTools, date);

        if (number > 0 && number <= number_1) {
            result = 1;
        } else if (number > number_1 && number <= number_2) {
            result = 2;
        } else if (number > number_2 && number <= number_3) {
            result = 3;
        } else if (number > number_3 && number <= number_4) {
            result = 4;
        }

        return result;
    }

    /**
     * 根据状态获取对应的字符串
     *
     * @param state
     * @return
     */
    public static String getCycleStateStr(Context context, int state) {
        String result = "";

        //月经期
        if (state == 1) {
            result = String.valueOf(context.getString(R.string.cycle_period));
            //安全期
        } else if (state == 2) {
            result = String.valueOf(context.getString(R.string.cycle_security));
            //危险期
        } else if (state == 3) {
            result = String.valueOf(context.getString(R.string.cycle_danger));
            //安全期
        } else if (state == 4) {
            result = String.valueOf(context.getString(R.string.cycle_security));
        }

        return result;

    }

    /**
     * 根据状态获取对应的字符串
     *
     * @param state
     * @return
     */
    public static int getCycleStateColor(int state) {
        int result = R.color.cycle_color_type1_bg;

        if (state == 1) {
            result = R.color.cycle_color_type1_bg;
        } else if (state == 2) {
            result = R.color.cycle_color_type2_text;
        } else if (state == 3) {
            result = R.color.cycle_color_type3_text;
        } else if (state == 4) {
            result = R.color.cycle_color_type2_text;
        }
        return result;

    }

}
