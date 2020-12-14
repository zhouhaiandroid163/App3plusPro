package com.zjw.apps3pluspro.sql.entity;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.bleservice.BtSerializeation;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

import java.util.ArrayList;
import java.util.List;

/**
 * 睡眠表
 * 备注：需要的字段
 * 1.用户ID
 * 2.测量时间(精确到秒/2017-10-18 16:27:20)
 * 3.手腕温度(20/10=2.0)-修改为体温
 * 4.温差=人体温度=手腕温度+温差(20/10=2.0)//暂不使用
 * 5.插入数据库的时间(unix时间戳)
 * 6.后台同步状态位（0=未同步，1=已同步）
 */
@Entity
public class MeasureTempInfo {
    private static final String TAG = MeasureTempInfo.class.getSimpleName();
    @Id(autoincrement = true)
    private Long _id;
    @NotNull
    /**
     *1.用户ID
     */
    private String user_id;
    /**
     * 2.测量时间(精确到秒/2017-10-18 16:27:20)
     */
    private String measure_time;
    /**
     * 3.手腕温度(20/2.0)-修改为体温
     */
    private String measure_wrist_temp;
    /**
     * 4.温差=人体温度=手腕温度+温差(20/10=2.0)
     */
    private String measure_temp_difference;//暂不使用
    /**
     * 5.插入数据库的时间(unix时间戳)
     */
    private String warehousing_time;
    /**
     * 6.后台同步状态位（0=未同步，1=已同步）
     */
    private String sync_state;

    @Generated(hash = 1812939085)
    public MeasureTempInfo(Long _id, @NotNull String user_id, String measure_time,
                           String measure_wrist_temp, String measure_temp_difference,
                           String warehousing_time, String sync_state) {
        this._id = _id;
        this.user_id = user_id;
        this.measure_time = measure_time;
        this.measure_wrist_temp = measure_wrist_temp;
        this.measure_temp_difference = measure_temp_difference;
        this.warehousing_time = warehousing_time;
        this.sync_state = sync_state;
    }

    @Generated(hash = 1162895650)
    public MeasureTempInfo() {
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

    public String getMeasure_time() {
        return this.measure_time;
    }

    public void setMeasure_time(String measure_time) {
        this.measure_time = measure_time;
    }

    public String getMeasure_wrist_temp() {
        return this.measure_wrist_temp;
    }

    public void setMeasure_wrist_temp(String measure_wrist_temp) {
        this.measure_wrist_temp = measure_wrist_temp;
    }

    public String getMeasure_temp_difference() {
        return this.measure_temp_difference;
    }

    public void setMeasure_temp_difference(String measure_temp_difference) {
        this.measure_temp_difference = measure_temp_difference;
    }

    public String getWarehousing_time() {
        return this.warehousing_time;
    }

    public void setWarehousing_time(String warehousing_time) {
        this.warehousing_time = warehousing_time;
    }

    public String getSync_state() {
        return this.sync_state;
    }

    public void setSync_state(String sync_state) {
        this.sync_state = sync_state;
    }


    @Override
    public String toString() {
        return "MeasureTempInfo{" +
                "_id=" + _id +
                ", user_id='" + user_id + '\'' +
                ", measure_time='" + measure_time + '\'' +
                ", measure_wrist_temp='" + measure_wrist_temp + '\'' +
                ", measure_temp_difference='" + measure_temp_difference + '\'' +
                ", warehousing_time='" + warehousing_time + '\'' +
                ", sync_state='" + sync_state + '\'' +
                '}';
    }

    /**
     * 离线测量温度数据
     *
     * @param data
     * @return
     */
    public static MeasureTempInfo getOffMeasureTempInfo(byte[] data) {


        String measure_time = BleTools.geBpTime(data);
        if (!BleTools.isRightfulnessTime(BleTools.geBpTime(data))) {
            // 写入错误的数据到error
            SysUtils.logErrorDataI(TAG, BleTools.geBpTime(data) + "  byte=" + BleTools.bytes2HexString(data));
            return null;
        }

        int measure_wrist_temp = data[4] & 0xff;

        MyLog.i(TAG, "离线体温 measure_time = " + measure_time);
        MyLog.i(TAG, "离线体温 measure_wrist_temp = " + measure_wrist_temp);

        MeasureTempInfo mMeasureTempInfo = new MeasureTempInfo();
        mMeasureTempInfo.setUser_id(BaseApplication.getUserId());
        mMeasureTempInfo.setMeasure_time(measure_time);
        mMeasureTempInfo.setMeasure_wrist_temp(String.valueOf(measure_wrist_temp));
        mMeasureTempInfo.setSync_state("0");

        return mMeasureTempInfo;
    }

    /**
     * 离线测量温度数据处理
     *
     * @param data
     * @return
     */
    public static List<MeasureTempInfo> getInfoList(byte[] data) {

        List<MeasureTempInfo> mInfoList = new ArrayList<>();

        if (data.length >= 16) {
            int count = data[15] & 0xff;
            int temp_difference = data[14] & 0xff;
            MyLog.i(TAG, "离线体温 count = " + count);
            MyLog.i(TAG, "离线体温 temp_difference = " + temp_difference);

            if (count != 0) {

                for (int i = 0; i < count; i++) {
                    byte[] ss_date = new byte[5];

                    for (int j = 0; j < 5; j++) {
                        ss_date[j] = data[16 + j + i * 5];
                    }

                    MeasureTempInfo mMeasureTempInfo = MeasureTempInfo.getOffMeasureTempInfo(ss_date);
                    mMeasureTempInfo.setMeasure_temp_difference(String.valueOf(temp_difference));


                    if (mMeasureTempInfo != null && BtSerializeation.checkDeviceTime(mMeasureTempInfo.getMeasure_time())) {

                        if (!BleTools.isRightfulnessTime(mMeasureTempInfo.getMeasure_time())) {
                            // 写入错误的数据到error
                            SysUtils.logErrorDataI(TAG, mMeasureTempInfo.getMeasure_time() + "  byte=" + BleTools.bytes2HexString(data));
                        } else {
                            if (!JavaUtil.checkIsNull(mMeasureTempInfo.getMeasure_wrist_temp())) {
                                mInfoList.add(mMeasureTempInfo);
                            }
                        }

                    } else {
//                        MyLog.i(TAG,"ble 得到 血压 mHealthyModle = null 或者 日期不合法");
                    }
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
        return mInfoList;

    }


    public static List<MeasureTempInfo> HandleNoData(List<MeasureTempInfo> my_list) {

        List<MeasureTempInfo> result_list = new ArrayList<>();

        if (my_list.size() > 0) {
            for (int i = 0; i < my_list.size(); i++) {


                if (my_list.get(i).getMeasure_temp_difference() != null && !my_list.get(i).getMeasure_temp_difference().equals("")
                        && my_list.get(i).getMeasure_wrist_temp() != null && !my_list.get(i).getMeasure_wrist_temp().equals("")
                ) {
                    result_list.add(my_list.get(i));
                }

            }
        }
        return result_list;
    }

}

