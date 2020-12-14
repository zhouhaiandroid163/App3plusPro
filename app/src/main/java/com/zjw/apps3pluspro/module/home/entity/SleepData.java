package com.zjw.apps3pluspro.module.home.entity;

import android.content.Context;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.utils.MyTime;

import java.util.List;

/**
 * 睡眠数据信息
 */


public class SleepData {

    private String sleep_type;
    private String startTime;

    /**
     * @return 睡眠类型（0x00:熬夜，0x01:进入睡眠，0x02:浅睡眠，0x03:熟睡，0x04:睡醒，0x05:退出睡眠）
     */
    public String getSleep_type() {
        return sleep_type;
    }

    /**
     * @param sleep_type 睡眠类型（0x00:熬夜，0x01:进入睡眠，0x02:浅睡眠，0x03:熟睡，0x04:睡醒，0x05:退出睡眠）
     */
    public void setSleep_type(String sleep_type) {
        this.sleep_type = sleep_type;
    }

    /**
     * @return 开始时间
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime 开始时间
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }


    /**
     * @param type 睡眠类型
     * @param time 开始时间
     */
    public SleepData(String type, String time) {
        super();
        setSleep_type(type);
        setStartTime(time);
    }


    @Override
    public String toString() {
        return "SleepData{" +
                "sleep_type='" + sleep_type + '\'' +
                ", startTime='" + startTime + '\'' +
                '}';
    }

    public SleepData() {
        super();
    }

    public String getSleepState(Context mContext) {
        String[] SleepState = {mContext.getString(R.string.stay_up), mContext.getString(R.string.get_into_sleep)
                , mContext.getString(R.string.light_sleep)
                , mContext.getString(R.string.deep_sleep)
                , mContext.getString(R.string.sober)
                , mContext.getString(R.string.wake)
        };

        return SleepState[Integer.valueOf(sleep_type)];
    }

    public String getStartTimeMin() {
        return String.valueOf(MyTime.getMin2(this.startTime));
    }

    /**
     * 返回过过滤熬夜后的
     *
     * @param sleep_list_data
     * @return
     */
    public static List<SleepData> getNorSleepData(List<SleepData> sleep_list_data) {


        if(sleep_list_data!=null&&!sleep_list_data.equals("")&&sleep_list_data.size()>0)
        {
            if (sleep_list_data.get(0).getSleep_type().equals("0")) {
                sleep_list_data.remove(0);
            }
        }

        return sleep_list_data;
    }

}