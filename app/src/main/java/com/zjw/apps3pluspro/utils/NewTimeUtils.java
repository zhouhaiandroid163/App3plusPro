package com.zjw.apps3pluspro.utils;

import android.annotation.SuppressLint;

import com.haibin.calendarview.Calendar;
import com.zjw.apps3pluspro.view.mycalendar.MyCalendarUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewTimeUtils {
    public static String getAllTime2() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSSS");
        Date date = new Date();
        return format.format(date);
    }

    /**
     * 计算数据日期，所在的一周
     *
     * @param register_date
     * @param input_date
     * @return
     */
    public static ArrayList<String> GetLastWeektDate(String register_date, String input_date) {
        String TAG = "NewTimeUtils";

        ArrayList<String> week_list = new ArrayList<>();

        String today_date = MyTime.getTime();
        int weekCount = 1;

        try {
            weekCount = MyTime.dayForWeek(input_date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MyLog.i(TAG, "register_date = " + register_date);
        MyLog.i(TAG, "input_date = " + input_date);
        MyLog.i(TAG, "today_date = " + today_date);
        MyLog.i(TAG, "weekCount = " + weekCount);


        //这周一对应的日期
        String fast_date = "";

        //如果等于周一
        if (weekCount == 1) {
            fast_date = input_date;
        } else {

            //转换成负数+1
            weekCount = -(weekCount) + 1;
            fast_date = getNumberDay(input_date, weekCount);
        }


        MyLog.i(TAG, "fast_date = " + fast_date);

        for (int i = 0; i < 7; i++) {

            String insert_date = getNumberDay(fast_date, i);

            boolean is_max_register = isMaxRegister(register_date, insert_date);
            boolean is_min_today = isMinToday(today_date, insert_date);

            if (is_max_register && is_min_today) {
                week_list.add(insert_date);
            }

            MyLog.i(TAG, "i = " + i + "  insert_date = " + insert_date + "  is_less_register = " + is_max_register + "  is_min_today = " + is_min_today);


        }


        return week_list;

    }


    /**
     * 获取当前日期N天，正数加N天，负数减N天
     *
     * @param time
     * @return
     */
    public static String getNumberDay(String time, int number) {
        String result = "";
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            Date date = dateFormat.parse(time);
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(java.util.Calendar.DAY_OF_MONTH, number);
            date = calendar.getTime();
            result = dateFormat.format(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;

    }


    /**
     * 获取当前日期是否大于等于注册日期
     *
     * @param register_date
     * @param input_date
     * @return
     */
    public static boolean isMaxRegister(String register_date, String input_date) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(register_date);
            Date dt2 = df.parse(input_date);
            return (dt1.before(dt2) || dt1.equals(dt2));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return false;

    }

    /**
     * 获取当前日期是否小于等于今天日期
     *
     * @param register_date
     * @param input_date
     * @return
     */
    public static boolean isMinToday(String register_date, String input_date) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(register_date);
            Date dt2 = df.parse(input_date);
            return (dt1.after(dt2) || dt1.equals(dt2));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;

    }

    public static String FormatDateYYYYMMDD(Calendar calendar) {
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTimeInMillis());
    }

    public static String FormatDateYYYYMM(int year, int month) {

        if (month < 10) {
            return String.valueOf(year) + "-" + "0" + String.valueOf(month);
        } else {
            return String.valueOf(year) + "-" + String.valueOf(month);
        }

    }

    //获取日历数据
    public static Map<String, Calendar> getCycData(String register_time) {
        String TAG = "NewTimeUtils";

        Map<String, Calendar> map = new HashMap<>();
        MyLog.i(TAG, "注册日期 = " + register_time);
        MyLog.i(TAG, "今日日期 = " + MyTime.getTime());

        int date_len = MyTime.getTimeDistance(register_time, MyTime.getTime());
        date_len += 1;
        MyLog.i(TAG, "date_len = " + date_len);
        for (int i = 0; i < date_len; i++) {
            java.util.Calendar my_date = MyCalendarUtils.getNewTime(register_time, i);
            Calendar calendar = MyCalendarUtils.MygetSchemeCalendar(my_date, MyCalendarUtils.ONE_BG_COLOR, MyCalendarUtils.ONE_TYPE);
            map.put(calendar.toString(), calendar);
        }
        return map;

    }


    /**
     * 上一周
     *
     * @param register_date
     * @return
     */
    public static ArrayList<String> GetOldWeektDate(String register_date, int pos) {
        String TAG = "NewTimeUtils";

        ArrayList<String> week_list = new ArrayList<>();

        String today_date = MyTime.getTime();
        int weekCount = 1;

        try {
            weekCount = MyTime.dayForWeek(today_date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MyLog.i(TAG, "register_date = " + register_date);
//        MyLog.i(TAG, "input_date = " + input_date);
        MyLog.i(TAG, "today_date = " + today_date);
        MyLog.i(TAG, "weekCount1 = " + weekCount);
        MyLog.i(TAG, "pos = " + pos);


        //转换成负数+1
        weekCount = (-(weekCount) - 6) + pos * 7;

        MyLog.i(TAG, "weekCount2 = " + weekCount);

        String fast_date = getNumberDay(today_date, weekCount);


        MyLog.i(TAG, "fast_date = " + fast_date);

        for (int i = 0; i < 7; i++) {

            String insert_date = getNumberDay(fast_date, i);

            boolean is_max_register = isMaxRegister(register_date, insert_date);
            boolean is_min_today = isMinToday(today_date, insert_date);

            if (is_max_register && is_min_today) {
                week_list.add(insert_date);
            }


            MyLog.i(TAG, "i = " + i + "  insert_date = " + insert_date + "  is_less_register = " + is_max_register + "  is_min_today = " + is_min_today);

        }

        return week_list;

    }

    /**
     * 完整“日期 ”转换成“时间”
     * 2015-10-10 15:36:57  = 》 2015-10-10
     *
     * @param time
     * @return
     */
    public static String getTimeFormatDate(String time) {
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        result = format2.format(date);

        return result;

    }

    /**
     * 完整日期 转换 成时间
     * 2015-10-10 15:36:57  = 》 15:36
     *
     * @param time
     * @return
     */
    public static String getChangeMyTime1(String time) {
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
        result = format2.format(date);

        return result;

    }

    /**
     * 完整日期 转换 成时间
     * 2015-10-10 15:36:57  = 》 15:36
     *
     * @param time
     * @return
     */
    public static String getChangeMyDate(String time) {
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        result = format2.format(date);

        return result;

    }

    public static final String TIME_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String TIME_YYYY_MM_DD_HH = "yyyy-MM-dd HH";
    public static final String TIME_MM_DD_HHMMSS = "MM-dd HH:mm:ss";
    public static final String TIME_YYYY_MM_DD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String HHMMSS = "HH:mm:ss";

    @SuppressLint("SimpleDateFormat")
    public static String getStringDate(long time, String format) {
        String date = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        date = simpleDateFormat.format(time);
        return date;
    }

    @SuppressLint("SimpleDateFormat")
    public static long getLongTime(String time, String format) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            date.setTime(0);
        }
        return date.getTime();
    }

    public static String getTimeString(long second) {
        String time = "";
        long hour = (second / 3600);
        long minute = (second % 3600) / 60;

        if (hour != 0) {
            if (hour < 10) {
                time = "0" + hour + ":";
            } else {
                time = hour + ":";
            }
            if (minute < 10) {
                time = time + "0" + minute + ":";
            } else {
                time = time + minute + ":";
            }
        } else {
            if (minute < 10) {
                time = time + "0" + minute + ":";
            } else {
                time = time + minute + ":";
            }
        }

        int sec = (int) ((second % 3600) % 60);
        if (sec < 10) {
            time = time + "0" + sec;
        } else {
            time = time + sec;
        }

        return time;
    }

    public static String getTimeStringHHMM(int hour, int minute) {
        String time = "";
        time = String.format("%02d:%02d", hour, minute);
        return time;
    }
}
