package com.zjw.apps3pluspro.utils;


import com.zjw.apps3pluspro.utils.log.MyLog;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 时间类
 *
 * @author Administrator
 */
public class MyTime {


    public static String getLastMonth(int count) {
        Date d = new Date();
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
        gc.setTime(d);
        gc.add(Calendar.MONTH, count);
        gc.set(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DATE));
        return sf.format(gc.getTime());
    }


    public static int getDaysOfMonth(String month) {

        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM"); // 如果写成年月日的形式的话，要写小d，如："yyyy/MM/dd"
        try {
            rightNow.setTime(simpleDate.parse(month)); // 要计算你想要的月份，改变这里即可
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);
    }


    /**
     * 获取月份
     *
     * @return
     */
    public static String getMonth() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Date date = new Date();
        return format.format(date);
    }

    /**
     * 获取年月日
     *
     * @return 2018-04-10
     */
    public static String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return format.format(date);
    }

    /**
     * 获取年月日 数组
     *
     * @return
     */
    public static String[] getDateSuZu() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                Locale.CHINESE);
        String abd = sdf.format(System.currentTimeMillis());
        return abd.split("-");
    }

    /**
     * 获取日期 精确到秒
     *
     * @return
     */
    public static String[] getTimeSuZu() {

        Date date = new Date();
        SimpleDateFormat sdformat = new SimpleDateFormat("HH-mm");//24小时制
        String LgTime = sdformat.format(date);


        return LgTime.split("-");
    }


    /**
     * 获取完整时间
     *
     * @return
     */
    public static String getAllTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return format.format(date);
    }

    public static String getMeasureTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        return format.format(date);
    }


    /* 获取当前日期是星期几 */
    public static int dayForWeek(String pTime) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(format.parse(pTime));
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == (Calendar.SUNDAY)) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }


    /**
     * 获取两个时间点的时间差 格式：hh:ss
     */
    public static String TotalTime2(String sd1, String sd2) {
        String TAG = "MyTime";

        String tatal_time = "0";

        int start_time = getMin2(sd1);
        int start_end = getMin2(sd2);

        MyLog.i(TAG, "拖动睡眠 计算出来的 start_time= " + start_time);
        MyLog.i(TAG, "拖动睡眠 计算出来的 start_end= " + start_end);

        tatal_time = String.valueOf(start_end - start_time);


        return tatal_time;
    }

    /**
     * 获取两个时间点的时间差 格式：hh:ss
     */
    public static int getMyTotalTime(String start_time, String end_time) {

        int result = 0;
        String start[] = start_time.split(":");
        int start_hours = 0;
        int start_min = 0;
//        if (Integer.valueOf(start[0]) >= 20 && Integer.valueOf(start[0]) <= 24) {
        if (Integer.valueOf(start[0]) >= 18 && Integer.valueOf(start[0]) <= 24) {
            start_hours = Integer.valueOf(start[0]);
        } else {
            start_hours = Integer.valueOf(start[0]) + 24;
        }
        start_min = Integer.valueOf((start[1]));

        String end[] = end_time.split(":");
        int end_hours = 0;
        int end_min = 0;
//        if (Integer.valueOf(end[0]) >= 20 && Integer.valueOf(end[0]) <= 24) {
        if (Integer.valueOf(end[0]) >= 18 && Integer.valueOf(end[0]) <= 24) {
            end_hours = Integer.valueOf(end[0]);
        } else {
            end_hours = Integer.valueOf(end[0]) + 24;
        }
        end_min = Integer.valueOf((end[1]));


        result = (end_hours * 60 + end_min) - (start_hours * 60 + start_min);

        if (result < 0) {
            result = 0;
        }

        return result;

    }


    public static int getMin2(String time) {
        int result = 0;
        String s1[] = time.split(":");

        int hours = 0;
        int min = 0;
//        if (Integer.valueOf(s1[0]) >= 22 && Integer.valueOf(s1[0]) <= 24) {
//        if (Integer.valueOf(s1[0]) >= 20 && Integer.valueOf(s1[0]) <= 24) {
        if (Integer.valueOf(s1[0]) >= 18 && Integer.valueOf(s1[0]) <= 24) {
            hours = Integer.valueOf(s1[0]);
        } else {
            hours = Integer.valueOf(s1[0]) + 24;
        }
        min = Integer.valueOf((s1[1]));

        result = hours * 60 + min;

        return result;


    }


    /**
     * 获取几点几小时中的小时
     *
     * @param total
     * @return
     */
    public static String getMyHour(int total) {

        return String.valueOf((total / 60));
    }

    /**
     * 获取几点几小时中的小时
     *
     * @param total
     * @return
     */
    public static String getMyMin(int total) {

        return String.valueOf((total % 60));
    }


    // 获取当前日期的前一天
    public static String getBeforeDay(int number) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, number);
        Date date = c.getTime();
        return format.format(date);
    }


    /**
     * 比较日期大小
     *
     * @param DATE1
     * @param DATE2
     * @return
     */
    public static boolean compare_date(String DATE1, String DATE2) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            return dt1.before(dt2);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }


    /**
     * 总时间 转换成小时
     *
     * @param totalMin
     * @return
     */
    public static String getTotalMinOfHour(String totalMin) {

        int mins = Integer.parseInt(totalMin);
        int hour = (mins % (24 * 60)) / 60;
        return hour + "";
    }

    /**
     * 总时间 转换成分钟
     *
     * @param totalMin
     * @return
     */
    public static String getTotalMinOfMin(String totalMin) {

        int mins = Integer.parseInt(totalMin);
        int min = (mins % (24 * 60)) % 60;
        return min + "";
    }


    /**
     * 分钟转换成小时
     * 90分钟 = 》 1.5 小时
     *
     * @param totalMin
     * @return
     */
    public static String getHours(String totalMin) {
        DecimalFormat df = new DecimalFormat("######0.0");
        double mins = Integer.parseInt(totalMin);

        if (mins <= 0) {
            mins = 0;
        } else if (mins <= 6) {
            mins = 6;
        }
        double min2 = mins / 60;

        String result = df.format(min2);

        result = result.replace(",", ".");


        return result;
    }

    /**
     * 分钟转换成小时
     * 90分钟 = 》 1.5 小时
     *
     * @param totalMin
     * @return
     */
    public static String getSleepTime_H(String totalMin, String defaultValue) {

        int mins = 0;

        if (totalMin == null)
            return defaultValue;
        try {
            mins = Integer.valueOf(totalMin);
        } catch (NumberFormatException x) {
            return defaultValue;
        }

        int hours_value = 0;

        String hours_str = "00";

        if (mins >= 60) {
            hours_value = mins / 60;

            if (hours_value < 10) {
                hours_str = "0" + String.valueOf(hours_value);
            } else {
                hours_str = String.valueOf(hours_value);
            }

        } else {
            hours_str = "00";
        }


        return hours_str;
    }

    /**
     * 分钟转换成小时
     * 90分钟 = 》 1.5 小时
     *
     * @param totalMin
     * @return
     */
    public static String getSleepTime_M(String totalMin, String defaultValue) {

        int mins = 0;

        if (totalMin == null)
            return defaultValue;
        try {
            mins = Integer.valueOf(totalMin);
        } catch (NumberFormatException x) {
            return defaultValue;
        }


        int min_value = 0;

        String min_str = "00";


        if (mins > 0) {
            min_value = mins % 60;

            if (min_value < 10) {
                min_str = "0" + String.valueOf(min_value);
            } else {
                min_str = String.valueOf(min_value);
            }

        } else {
            min_str = "00";
        }


        return min_str;
    }


    /**
     * 分钟转换成小时
     * 90分钟 = 》 1.5 小时
     *
     * @param totalMin
     * @return
     */
    public static String getHoursInt(String totalMin) {
        DecimalFormat caloriesFmt = new DecimalFormat(",##0.0");
        int mins = Integer.parseInt(totalMin);
        if (mins <= 0) {
            mins = 0;
        } else if (mins <= 6) {
            mins = 6;
        }
//        int hour = mins / 60;
//        int minute = mins % 60;
        return caloriesFmt.format(mins / 60.0d);
    }


    /**
     * 辅助日历用到的
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long getDaysBetween(String date1, String date2) {

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Long c = null;
        try {
            c = sf.parse(date2).getTime() - sf.parse(date1).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c / 1000 / 60 / 60 / 24;//天
    }


    /**
     * 完整日期 转换 成时间
     * 2015-10-10 15:36:57  = 》 15:36:57
     *
     * @param time
     * @return
     */
    public static String getMyData(String time) {
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");
        result = format2.format(date);

        return result;

    }

    /**
     * 获取年龄
     *
     * @param time
     * @return
     */
    public static String getAge(String time) {

        String TAG = "MyTime";

        MyLog.i(TAG, "获取年龄 开始的 = " + time);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        MyLog.i(TAG, "获取年龄 当前的 = " + str);


        String result = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat format2 = new SimpleDateFormat("yyyy");
        result = format2.format(date);

        int result_int = Integer.valueOf(str) - Integer.valueOf(result);
        if (result_int <= 0) {
            result_int = 0;
        }

        MyLog.i(TAG, "获取年龄 用户的  = " + result);

        return String.valueOf(result_int);

    }


    /**
     * 判断输入日期，是否是未来日期
     *
     * @param time
     * @return
     */
    public static boolean getIsOldTime(String time) {

        String TAG = "MyTime";

        if (time == null && time.equals("")) {
            return true;
        }

        boolean result = true;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = format.format(date);

        String[] today_st = today.split("-");
        String[] time_str = time.split("-");

        if (today_st != null && today_st.length == 3 && time_str != null && time_str.length == 3) {

            MyLog.i(TAG, "设置生日 = abc  today = " + Integer.valueOf(today_st[0]) + "  time = " + Integer.valueOf(time_str[0]));
            MyLog.i(TAG, "设置生日 = abc  today = " + Integer.valueOf(today_st[1]) + "  time = " + Integer.valueOf(time_str[1]));
            MyLog.i(TAG, "设置生日 = abc  today = " + Integer.valueOf(today_st[2]) + "  time = " + Integer.valueOf(time_str[2]));
            if (Integer.valueOf(today_st[0]) < Integer.valueOf(time_str[0])) {
                result = false;
            } else if (Integer.valueOf(today_st[0]) > Integer.valueOf(time_str[0])) {

            } else {

                if (Integer.valueOf(today_st[1]) < Integer.valueOf(time_str[1])) {
                    result = false;
                } else if (Integer.valueOf(today_st[1]) > Integer.valueOf(time_str[1])) {
                } else {


                    if (Integer.valueOf(today_st[2]) < Integer.valueOf(time_str[2])) {
                        result = false;
                    } else if (Integer.valueOf(today_st[2]) > Integer.valueOf(time_str[2])) {
                    } else {
                        result = false;
                    }
                }
            }

        }


        return result;
    }

    // 获取当前日期的前一天
    public static boolean getIsOldCycleDate(String time) {

        String TAG = "MyTime";

        if (time == null && time.equals("")) {
            return true;
        }

        boolean result = true;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = format.format(date);

        String[] today_st = today.split("-");
        String[] time_str = time.split("-");

        if (today_st != null && today_st.length == 3 && time_str != null && time_str.length == 3) {
//            for(int i = 0 ;i<today_st.length;i++)
//            {
//                System.out.println("输出结果 i = " + i + "  today = " + today_st[i] + "  time = " + time_str[i]);
//
//                if( Integer.valueOf(today_st[i])<Integer.valueOf(time_str[i]))
//                {
//                    result = false;
//                    break;
//                }
//            }
            MyLog.i(TAG, "设置生日 = abc  today = " + Integer.valueOf(today_st[0]) + "  time = " + Integer.valueOf(time_str[0]));
            MyLog.i(TAG, "设置生日 = abc  today = " + Integer.valueOf(today_st[1]) + "  time = " + Integer.valueOf(time_str[1]));
            MyLog.i(TAG, "设置生日 = abc  today = " + Integer.valueOf(today_st[2]) + "  time = " + Integer.valueOf(time_str[2]));
            if (Integer.valueOf(today_st[0]) < Integer.valueOf(time_str[0])) {
                MyLog.i(TAG, "设置生日 = abc  today111111111111");
                result = false;
            } else if (Integer.valueOf(today_st[0]) > Integer.valueOf(time_str[0])) {
                MyLog.i(TAG, "设置生日 = abc  0000000000");

            } else {

                MyLog.i(TAG, "设置生日 = abc  222222222222");
                if (Integer.valueOf(today_st[1]) < Integer.valueOf(time_str[1])) {
                    MyLog.i(TAG, "设置生日 = abc  333333333");
                    result = false;
                } else if (Integer.valueOf(today_st[1]) > Integer.valueOf(time_str[1])) {
                    MyLog.i(TAG, "设置生日 = abc  444444");
                } else {

                    MyLog.i(TAG, "设置生日 = abc  555555555");

                    if (Integer.valueOf(today_st[2]) < Integer.valueOf(time_str[2])) {
                        MyLog.i(TAG, "设置生日 = abc  666666");
                        result = false;
                    } else if (Integer.valueOf(today_st[2]) >= Integer.valueOf(time_str[2])) {
                        MyLog.i(TAG, "设置生日 = abc  7777777");
                    } else {
                        result = false;
                        MyLog.i(TAG, "设置生日 = abc  888888");
                    }
                }
            }

        }


        return result;
    }


    /**
     * 获取当前日期 后一天
     *
     * @param time
     * @return
     */
    public static String getBeforeDay(String time) {
        String result = "";
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            Date date = dateFormat.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date = calendar.getTime();
            result = dateFormat.format(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 获取日期 2017-10-10 10:10:10
     *
     * @param time 时间
     * @return 0-10
     */
    public static String MyFormatDate(String time) {
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat format2 = new SimpleDateFormat("MM-dd");
        result = format2.format(date);

        return result;

    }


    /**
     * 获取上一周的开始日期
     *
     * @param time 2017-10-16
     * @return 2017-10-10
     */
    public static String GetLastWeektDate(String time) {
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        date = calendar.getTime();

        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        result = format2.format(date);

        return result;

    }

    /**
     * 获取上一周的开始日期
     *
     * @param time 2017-10-16
     * @return 2017-10-10
     */
    public static String GetLastWeektDate(String time, int count) {
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, count);
        date = calendar.getTime();

        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        result = format2.format(date);

        return result;

    }


    /**
     * 获取本月日期
     *
     * @param time
     * @return 2017-10
     */
    public static String GetNowMonthtDate(String time) {
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM");
        result = format2.format(date);

        return result;

    }

    /**
     * 获取下一月的日期
     *
     * @param time = 2017-10-10 日期
     * @return 2017-11
     */
    public static String GetNextMonthtDate(String time) {
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, +1);
        date = calendar.getTime();

        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM");
        result = format2.format(date);

        return result;

    }


    /**
     * 判断日期是否超过注册日期
     *
     * @param date
     * @param rigster
     * @return
     */
    public static String TimeCompare(String date, String rigster) {

        String result = date;

        //格式化时间
        SimpleDateFormat CurrentTime = new SimpleDateFormat("yyyy-MM-dd");
        try {

            Date str_date = CurrentTime.parse(date);
            Date str_rigster = CurrentTime.parse(rigster);

            if (((str_date.getTime() - str_rigster.getTime()) < 0)) {
                result = rigster;
            } else {
                result = date;
            }


        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return result;

    }

    /**
     * 判断日期是否小于注册日期
     *
     * @param date
     * @param rigster
     * @return
     */
    public static String TimeCompareMin(String date, String rigster) {

        String result = date;

        //格式化时间
        SimpleDateFormat CurrentTime = new SimpleDateFormat("yyyy-MM-dd");
        try {

            Date str_date = CurrentTime.parse(date);
            Date str_rigster = CurrentTime.parse(rigster);

            if (((str_date.getTime() - str_rigster.getTime()) > 0)) {
                result = date;
            } else {
                result = rigster;
            }


        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return result;

    }


    public static ArrayList<String> getWeekDateList(String start_time, String end_time) {

        ArrayList<String> date_list = new ArrayList<>();

        try {


            long start_long = dateToStamp(start_time);
            long end_long = dateToStamp(end_time);
            int day = (int) ((end_long - start_long) / 86400 / 1000);


            for (int i = 0; i <= day; i++) {


                date_list.add(stampToDate(start_long + i * 86400 * 1000));

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date_list;

    }

    public static ArrayList<String> getMonthDateList(String start_time, String end_time) {

        ArrayList<String> date_list = new ArrayList<>();

        try {


            long start_long = dateToStamp(start_time);
            long end_long = dateToStamp(end_time);
            int day = (int) ((end_long - start_long) / 86400 / 1000);

            String month = getMyDataTwo(start_time);

//           MyLog.i(TAG,"待处理 月份 = " + month);
//           MyLog.i(TAG,"待处理 day = " + day);

            for (int i = 0; i <= day; i++) {

//               MyLog.i(TAG,"待处理 i = " + i + " date = " + stampToDate(start_long + i * 86400 * 1000));

                if (i < 9) {
//                   MyLog.i(TAG,"待处理 i = " + i + " date = " + month + "-0" + (i+1));
                    date_list.add(month + "-0" + (i + 1));
                } else {
//                   MyLog.i(TAG,"待处理 i = " + i + " date = " + month + "-" + (i+1));
                    date_list.add(month + "-" + (i + 1));

                }


            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date_list;

    }

    /*
     * 将时间转换为时间戳
     */
    private static long dateToStamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(s);
        return date.getTime();
    }

    /*
     * 将时间戳转换为时间
     */
    private static String stampToDate(long lt) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


    /**
     * 获取这个月有多少天
     *
     * @return
     */
    public static String getMonthDay(String date) {
        String result = "31";
        int year = Integer.valueOf(date.split("-")[0]);
        int month = Integer.valueOf(date.split("-")[1]);

        if (month == 4 || month == 6 || month == 9 || month == 11) {
            result = "30";

        } else if (month == 2) {

            result = String.valueOf(get2Days(year));
//            result = "29";


        }

        return date + "-" + result;

    }

    private static int get2Days(int year)//计算某一年2月份有多少天
    {
        Calendar c = Calendar.getInstance();
        c.set(year, 2, 1);//0-11->1-12  将日期设置为某一年的3月1号
        c.add(Calendar.DAY_OF_MONTH, -1);//将日期减去一天，即日期变成2月的最后一天
        return c.get(Calendar.DAY_OF_MONTH);//返回二月最后一天的具体值
    }


    /**
     * 提醒设置，比较时间大小
     *
     * @param start_hours
     * @param start_end
     * @param end_hours
     * @param end_min
     * @return
     */
    public static boolean isOldTime(int start_hours, int start_end, int end_hours, int end_min) {


        boolean result = false;

        if (start_hours > end_hours) {
            return true;
        } else if (start_hours == end_hours && start_end > end_min) {
            return true;
        }


        return result;
    }

    /**
     * 完整日期 转换 成时间
     * 2015-10-10 = 》 2015-10
     *
     * @param time
     * @return
     */
    public static String getMyDataTwo(String time) {
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM");
        result = format2.format(date);

        return result;

    }

    public static String getMeasure() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        return format.format(date);
    }


    //时间戳转字符串
    public static Long getMyLongTime() {
        return System.currentTimeMillis();
    }

    //时间戳转字符串
    public static boolean checkLongTime(Long time) {

        String TAG = "MyTime";

        int interval_time = 1000 * 60 * 10;
//        int interval_time = 1000 * 30;
        long tt = System.currentTimeMillis() - time;
        tt = Math.abs(tt);
        MyLog.i(TAG, "规定时间 = tt = " + tt);

        if (tt > interval_time) {
            return true;
        } else {
            return false;


        }
    }

    //获取当前年份
    public static String getYears() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        Date date = new Date();
        return format.format(date);
    }

    //获取前一天，完整时间
    public static String getOldOneDay() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar c = Calendar.getInstance();
//        c.add(Calendar.MINUTE, -2);
        c.add(Calendar.DATE, -1);
        Date date = c.getTime();
        return format.format(date);
    }

    public static int getTimeDistance(String start_time, String end_time) {
        {


            Date beginDate = null;
            Date endDate = null;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                beginDate = simpleDateFormat.parse(start_time);
                endDate = simpleDateFormat.parse(end_time);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            Calendar beginCalendar = Calendar.getInstance();
            beginCalendar.setTime(beginDate);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);
            long beginTime = beginCalendar.getTime().getTime();
            long endTime = endCalendar.getTime().getTime();
            int betweenDays = (int) ((endTime - beginTime) / (1000 * 60 * 60 * 24));//先算出两时间的毫秒数之差大于一天的天数

            endCalendar.add(Calendar.DAY_OF_MONTH, -betweenDays);//使endCalendar减去这些天数，将问题转换为两时间的毫秒数之差不足一天的情况
            endCalendar.add(Calendar.DAY_OF_MONTH, -1);//再使endCalendar减去1天
            if (beginCalendar.get(Calendar.DAY_OF_MONTH) == endCalendar.get(Calendar.DAY_OF_MONTH))//比较两日期的DAY_OF_MONTH是否相等
                return betweenDays + 1;    //相等说明确实跨天了
            else
                return betweenDays + 0;    //不相等说明确实未跨天

        }
    }

    //检查APP升级时间间隔
    public static boolean checkUploadAppLongTime(Long time) {
        String TAG = "MyTime.checkUploadAppLongTime()";
        int interval_time = 1000 * 60 * 60 * 24 * 15;
//        int interval_time = 1000 * 30;
        long tt = System.currentTimeMillis() - time;
        tt = Math.abs(tt);
        MyLog.i(TAG, "规定时间 = tt = " + tt);
        if (tt > interval_time) {
            return true;
        } else {
            return false;
        }
    }

}