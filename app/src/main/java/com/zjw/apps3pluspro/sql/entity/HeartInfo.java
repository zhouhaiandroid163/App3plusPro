package com.zjw.apps3pluspro.sql.entity;


import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * 整点心率表
 * 备注：需要的字段
 * 1.用户ID
 * 2.日期（精确到天/2017-10-18）
 * 3.原始数据（不定，逗号隔开/100,110,120）
 * 4.数据类型（0=24条（整点心率），1=288(连续心率））
 * 5.插入数据库的时间(unix时间戳)
 * 6.后台同步状态位（0=未同步，1=已同步）
 */
@Entity
public class HeartInfo {
    private static final String TAG = HeartInfo.class.getSimpleName();

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
     * 3.原始数据（24条，逗号隔开/100,110,120）
     */
    private String data;
    /**
     * 4.数据类型（0=24条（整点心率），1=288(连续心率））
     */
    private String data_type;
    /**
     * 5.插入数据库的时间(unix时间戳)
     */
    private String warehousing_time;
    /**
     * 6.后台同步状态位（0=未同步，1=已同步）
     */
    private String sync_state;

    @Generated(hash = 557946629)
    public HeartInfo(Long _id, @NotNull String user_id, String date, String data,
                     String data_type, String warehousing_time, String sync_state) {
        this._id = _id;
        this.user_id = user_id;
        this.date = date;
        this.data = data;
        this.data_type = data_type;
        this.warehousing_time = warehousing_time;
        this.sync_state = sync_state;
    }

    @Generated(hash = 547177831)
    public HeartInfo() {
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

    public String getData_type() {
        return this.data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getSync_state() {
        return this.sync_state;
    }

    public void setSync_state(String sync_state) {
        this.sync_state = sync_state;
    }

    @Override
    public String toString() {
        return "HeartInfo{" +
                "_id=" + _id +
                ", user_id='" + user_id + '\'' +
                ", date='" + date + '\'' +
                ", data='" + data + '\'' +
                ", data_type='" + data_type + '\'' +
                ", warehousing_time='" + warehousing_time + '\'' +
                ", sync_state='" + sync_state + '\'' +
                '}';
    }

    /**
     * 解析整点心率
     *
     * @param data
     * @return
     */
    public static HeartInfo getPoHeartInfo(byte[] data) {


        int heart_item_count = data[16] & 0x3F;
        String po_data = "";
        int heart_data_pos = 17;

        //为了统计是否心率都为零
        int total_heart = 0;

        for (int i = 0; i < heart_item_count; i++) {
            int heart_value = data[heart_data_pos + i] & 0xFF;
            if (i % 2 == 0) {
                po_data = po_data + heart_value;
                total_heart += heart_value;
//                MyLog.i(TAG, "total_heart = " + total_heart);
                if (i != (heart_item_count - 2)) {
                    po_data = po_data + ",";
                }
            }
        }

        HeartInfo mHeartInfo = new HeartInfo();

        mHeartInfo.setUser_id(BaseApplication.getUserId());
        mHeartInfo.setDate(BleTools.getDate(data));
        mHeartInfo.setData(po_data);
        mHeartInfo.setData_type("0");


//        //如果心率累加不等于0，则需要上传后台，标志位，置为0
//        if (total_heart != 0) {
//            mHeartInfo.setSync_state("0");
//        }
//        //如果心率累加等于0，则不需要上传后台，标志位，置为1
//        else {
//            mHeartInfo.setSync_state("1");
//        }

        mHeartInfo.setSync_state("0");

        return mHeartInfo;
    }


    /**
     * 获取连续心率
     *
     * @param data
     * @return
     */
    public static HeartInfo getWoHeartInfo(byte[] data) {

        //长度
        int heart_item_count = 288;
        //原始数据
        String wo_data = "";
        int heart_data_pos = 17;

        //为了统计是否心率都为零
        int total_heart = 0;

        for (int i = 0; i < heart_item_count; i++) {

            int heart_value = data[heart_data_pos + i] & 0xFF;
            wo_data = wo_data + heart_value;
            total_heart += heart_value;
            MyLog.i(TAG, "total_heart = " + total_heart);
            if (i != (heart_item_count - 1)) {
                wo_data = wo_data + ",";
            }
        }

        MyLog.i(TAG, "ble 同步数据 解析连续心率数据 = wo_data " + wo_data);

        HeartInfo mHeartInfo = new HeartInfo();
        mHeartInfo.setUser_id(BaseApplication.getUserId());
        mHeartInfo.setDate(BleTools.getDate(data));
        mHeartInfo.setData(wo_data);
        mHeartInfo.setData_type("1");

//        //如果心率累加不等于0，则需要上传后台，标志位，置为0
//        if (total_heart != 0) {
//            mHeartInfo.setSync_state("0");
//        }
//        //如果心率累加等于0，则不需要上传后台，标志位，置为1
//        else {
//            mHeartInfo.setSync_state("1");
//        }

        mHeartInfo.setSync_state("0");

        return mHeartInfo;
    }

    public String getWarehousing_time() {
        return this.warehousing_time;
    }

    public void setWarehousing_time(String warehousing_time) {
        this.warehousing_time = warehousing_time;
    }
}
