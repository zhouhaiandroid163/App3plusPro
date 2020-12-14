package com.zjw.apps3pluspro.view.mycalendar;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyCalendarUtils {


    public static final String ONE_TYPE = "ONE_TYPE";
    public static final int ONE_TEXT_COLOR = 0xFF232323;
    public static final int ONE_BG_COLOR = 0xFFFFFFFF;


    public static final String TWO_TYPE = "TWO_TYPE";
    public static final int TWO_TEXT_COLOR = 0xFFC1E5B9;
    public static final int TWO_BG_COLOR = 0xFFFFFFFF;


    public static final String THREE_TYPE = "THREE_TYPE";
    public static final int THREE_TEXT_COLOR = 0xFFDCA9E4;
    public static final int THREE_BG_COLOR = 0xFFFFFFFF;


    public static int getTextColor(Context context, String type) {

        int result = 0xFF000000;
//        int result = R.color.cycle_color_type0_text;

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

        int result = 0x99EFEFEF;
//        int result = R.color.cycle_color_type0_bg;

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


//    public static String getNewTime(String s, int n) {
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//            Calendar cd = Calendar.getInstance();
//            cd.setTime(sdf.parse(s));
//            cd.add(Calendar.DATE, n);
//            return sdf.format(cd.getTime());
//
//        } catch (Exception e) {
//            return null;
//        }
//
//    }


    public static Calendar getNewTime(String s, int n) {

//        String[] date = new String[3];

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(s));
            cd.add(Calendar.DATE, n);

            return cd;

        } catch (Exception e) {
            return null;
        }

    }

    @SuppressWarnings("all")
    public static com.haibin.calendarview.Calendar MygetSchemeCalendar(java.util.Calendar myCalendar, int color, String text) {

        int year = myCalendar.get(java.util.Calendar.YEAR);
        int month = myCalendar.get(java.util.Calendar.MONTH) + 1;
        int day = myCalendar.get(java.util.Calendar.DAY_OF_MONTH);


        com.haibin.calendarview.Calendar calendar = new com.haibin.calendarview.Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);


        return calendar;
    }

    @SuppressWarnings("all")
    public static com.haibin.calendarview.Calendar MygetSchemeCalendar(String date, int color, String text) {

        String datasuzup[] = date.split("-");
        int year = Integer.valueOf(datasuzup[0]);
        int month = Integer.valueOf(datasuzup[1]);
        int day = Integer.valueOf(datasuzup[2]);


        com.haibin.calendarview.Calendar calendar = new com.haibin.calendarview.Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);


        return calendar;
    }


}
