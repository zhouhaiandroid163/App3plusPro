package com.zjw.apps3pluspro.utils;


import com.zjw.apps3pluspro.bleservice.BtSerializeation;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;

//分析工具类
public class AnalyticalUtils {

    private final String TAG = AnalyticalUtils.class.getSimpleName();

    /**
     * 计算运动目标完成率
     *
     * @param step
     * @return
     */
    public static String getCompletionRate(UserSetTools mUserSetTools, String step) {
        String result = "0";

        String target = mUserSetTools.get_user_exercise_target();


        if (!JavaUtil.checkIsNull(step) && !JavaUtil.checkIsNull(target)) {

            float percent = Float.parseFloat(step) / Float.parseFloat(target);
            int percent_target = (int) (percent * 100);
            result = String.valueOf(percent_target);
        }

        return result;

    }

    /**
     * 计算运动量
     *
     * @param step
     * @return
     */
    public static String getStepIndex(UserSetTools mUserSetTools, String step, int max) {
        String result = "0";

        String target = mUserSetTools.get_user_exercise_target();


        if (!JavaUtil.checkIsNull(step) && !JavaUtil.checkIsNull(target)) {

            float percent = Float.parseFloat(step) / Float.parseFloat(target);
            if (percent > 1) {
                percent = 1;
            }

            int percent_target = (int) (percent * (float) max);

            result = String.valueOf(percent_target);
        }

        return result;

    }

    /**
     * 计算睡眠目标完成率
     *
     * @param sleep_time
     * @return
     */
    public static String getCompletionRateSleep(UserSetTools mUserSetTools, String sleep_time) {
        String result = "0";

        String target = mUserSetTools.get_user_sleep_target();


        if (!JavaUtil.checkIsNull(sleep_time) && !JavaUtil.checkIsNull(target)) {

            float percent = Float.parseFloat(sleep_time) / Float.parseFloat(target);
            int percent_target = (int) (percent * 100);

            if (percent_target > 100) {
                percent_target = 100;
            }
            result = String.valueOf(percent_target);
        }

        return result;

    }

    /**
     * 计算睡眠质量
     *
     * @param sleep_time
     * @return
     */
    public static String getQualitySleep(UserSetTools mUserSetTools, String sleep_time) {
        String result = "0";

        String target = mUserSetTools.get_user_sleep_target();


        if (!JavaUtil.checkIsNull(sleep_time) && !JavaUtil.checkIsNull(target)) {

            float percent = Float.parseFloat(sleep_time) / Float.parseFloat(target);
            int percent_target = (int) (percent * 100);
            if (percent_target > 100) {
                percent_target = 100;
            }
            result = String.valueOf(percent_target);
        }

        return result;

    }


    public static byte[] StringToIntSuZu(String str) {

        if (JavaUtil.checkIsNull(str)) {
            return null;
        }

        String[] StringSuZu = str.split(",");

        byte[] result_data = new byte[StringSuZu.length * 2];


        for (int i = 0; i < StringSuZu.length; i++) {

            byte[] my_byte = BtSerializeation.intToByteArray(Integer.valueOf(StringSuZu[i]));

            result_data[i * 2] = my_byte[0];
            result_data[i * 2 + 1] = my_byte[1];

        }


        return result_data;

    }

    /**
     * 格式化时间，成【03:15】这种数据
     *
     * @param hours 小时
     * @param min   分钟
     * @return
     */
    public static String getStrTime(int hours, int min) {

        String str_hours = String.valueOf(hours);
        String str_min = String.valueOf(min);


        if (hours < 10) {
            str_hours = "0" + str_hours;
        }
        if (min < 10) {
            str_min = "0" + str_min;
        }


        return str_hours + ":" + str_min;

    }


}
