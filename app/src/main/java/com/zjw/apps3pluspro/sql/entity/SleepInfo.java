package com.zjw.apps3pluspro.sql.entity;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.bleservice.BtSerializeation;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;


/**
 * 睡眠表
 * 备注：需要的字段
 * 1.用户ID
 * 2.日期（精确到天/2017-10-18）
 * 3.原始数据（ios字符串/48577,48802,803,1314,4259,5154,5956,5986,7331,7906,15493）
 * 4.插入数据库的时间(unix时间戳)
 * 5.后台同步状态位（0=未同步，1=已同步）
 */
@Entity
public class SleepInfo {
    private static final String TAG = SleepInfo.class.getSimpleName();
    @Id(autoincrement = true)
    private Long _id;
    @NotNull
    /**
     *1.用户ID
     */
    private String user_id;
    /**
     * 2.日期（精确到天/2017-10-18）
     */
    private String date;
    /**
     * 3.原始数据（JSON数组字符串/）
     */
    private String data;
    /**
     * 4.插入数据库的时间(unix时间戳)
     */
    private String warehousing_time;
    /**
     * 5.后台同步状态位（0=未同步，1=已同步）
     */
    private String sync_state;

    @Generated(hash = 1019442941)
    public SleepInfo(Long _id, @NotNull String user_id, String date, String data,
                     String warehousing_time, String sync_state) {
        this._id = _id;
        this.user_id = user_id;
        this.date = date;
        this.data = data;
        this.warehousing_time = warehousing_time;
        this.sync_state = sync_state;
    }

    @Generated(hash = 2000577924)
    public SleepInfo() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSync_state() {
        return this.sync_state;
    }

    public void setSync_state(String sync_state) {
        this.sync_state = sync_state;
    }

    public static SleepInfo getSleepInfo(byte[] data) {


        int sleep_count_item;
        int pos = 16;
        String sleep16Orig = "";//IOS睡眠详细信息
        sleep_count_item = data[15];

        //过滤
        MyLog.i(TAG, "sleep_count_item = " + sleep_count_item);
        if (sleep_count_item <= 2) {
            return null;
        }


        for (int i = 0; i < sleep_count_item; i++) {

            byte cca1[] = new byte[2];
            cca1[0] = data[pos + 2 * i];
            cca1[1] = data[pos + 2 * i + 1];

            if (i < sleep_count_item - 1) {
                sleep16Orig += BtSerializeation.byteArrayToInt(cca1) + ",";
            } else {
                sleep16Orig += BtSerializeation.byteArrayToInt(cca1);
            }


        }

        SleepInfo mSleepInfo = new SleepInfo();
        mSleepInfo.setUser_id(BaseApplication.getUserId());
        mSleepInfo.setDate(MyTime.getBeforeDay(BleTools.getDate(data)));
        mSleepInfo.setData(sleep16Orig);
        mSleepInfo.setSync_state("0");

        return mSleepInfo;
    }

    @Override
    public String toString() {
        return "SleepInfo{" +
                "_id=" + _id +
                ", user_id='" + user_id + '\'' +
                ", date='" + date + '\'' +
                ", data='" + data + '\'' +
                ", warehousing_time='" + warehousing_time + '\'' +
                ", sync_state='" + sync_state + '\'' +
                '}';
    }

    public String getWarehousing_time() {
        return this.warehousing_time;
    }

    public void setWarehousing_time(String warehousing_time) {
        this.warehousing_time = warehousing_time;
    }


}

