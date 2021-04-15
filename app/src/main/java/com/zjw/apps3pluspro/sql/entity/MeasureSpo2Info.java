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
 * 3.测量血氧值（98）
 * 4.插入数据库的时间(unix时间戳)
 * 5.后台同步状态位（0=未同步，1=已同步）
 */
@Entity
public class MeasureSpo2Info {
    private static final String TAG = MeasureSpo2Info.class.getSimpleName();
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
    private String measure_time;
    /**
     * 3.测量血氧值（98）
     */
    private String measure_spo2;
    /**
     * 4.插入数据库的时间(unix时间戳)
     */
    private String warehousing_time;
    /**
     * 5.后台同步状态位（0=未同步，1=已同步）
     */
    private String sync_state;

    @Generated(hash = 1329083101)
    public MeasureSpo2Info(Long _id, @NotNull String user_id, String measure_time,
                           String measure_spo2, String warehousing_time, String sync_state) {
        this._id = _id;
        this.user_id = user_id;
        this.measure_time = measure_time;
        this.measure_spo2 = measure_spo2;
        this.warehousing_time = warehousing_time;
        this.sync_state = sync_state;
    }

    @Generated(hash = 656457740)
    public MeasureSpo2Info() {
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

    public String getMeasure_spo2() {
        return this.measure_spo2;
    }

    public void setMeasure_spo2(String measure_spo2) {
        this.measure_spo2 = measure_spo2;
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
        return "MeasureSpo2Info{" +
                "_id=" + _id +
                ", user_id='" + user_id + '\'' +
                ", measure_time='" + measure_time + '\'' +
                ", measure_spo2='" + measure_spo2 + '\'' +
                ", warehousing_time='" + warehousing_time + '\'' +
                ", sync_state='" + sync_state + '\'' +
                '}';
    }

    /**
     * 离线测量血氧数据
     *
     * @param data
     * @return
     */
    public static MeasureSpo2Info getOffMeasureSpo2Info(byte[] data) {


        String measure_time = BleTools.geBpTime(data);
        if (!BleTools.isRightfulnessTime(BleTools.geBpTime(data))) {
            // 写入错误的数据到error
            SysUtils.logErrorDataI(TAG, BleTools.geBpTime(data) + "  byte=" + BleTools.bytes2HexString(data));
            return null;
        }

        int measure_spo2 = data[4] & 0xff;

        MyLog.i(TAG, "measure_time = " + measure_time);
        MyLog.i(TAG, "measure_spo2 = " + measure_spo2);
        MeasureSpo2Info mMeasureSpo2Info = new MeasureSpo2Info();
        mMeasureSpo2Info.setUser_id(BaseApplication.getUserId());
        mMeasureSpo2Info.setMeasure_time(measure_time);
        mMeasureSpo2Info.setMeasure_spo2(String.valueOf(measure_spo2));

        return mMeasureSpo2Info;
    }

    /**
     * 离线测量血氧数据处理
     *
     * @param data
     * @return
     */
    public static List<MeasureSpo2Info> getInfoList(byte[] data) {

        List<MeasureSpo2Info> mInfoList = new ArrayList<>();

        if (data.length >= 16) {
            int count = data[15] & 0xff;
            if (count != 0) {

                for (int i = 0; i < count; i++) {
                    byte[] ss_date = new byte[5];

                    for (int j = 0; j < 5; j++) {
                        ss_date[j] = data[16 + j + i * 5];
                    }

                    MeasureSpo2Info mMeasureSpo2Info = MeasureSpo2Info.getOffMeasureSpo2Info(ss_date);


                    if (mMeasureSpo2Info != null && BtSerializeation.checkDeviceTime(mMeasureSpo2Info.getMeasure_time())) {

                        if (!BleTools.isRightfulnessTime(mMeasureSpo2Info.getMeasure_time())) {
                            // 写入错误的数据到error
                            SysUtils.logErrorDataI(TAG, mMeasureSpo2Info.getMeasure_time() + "  byte=" + BleTools.bytes2HexString(data));
                        } else {
                            if (!JavaUtil.checkIsNull(mMeasureSpo2Info.getMeasure_spo2())) {
                                mInfoList.add(mMeasureSpo2Info);
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


    public static List<MeasureSpo2Info> HandleNoData(List<MeasureSpo2Info> my_list) {
        List<MeasureSpo2Info> result_list = new ArrayList<>();
        if (my_list.size() > 0) {
            for (int i = 0; i < my_list.size(); i++) {
                if (my_list.get(i).getMeasure_spo2() != null && !my_list.get(i).getMeasure_spo2().equals("")) {
                    result_list.add(my_list.get(i));
                }
            }
        }
        return result_list;
    }

//
//    /**
//     * 过滤无效数据
//     *
//     * @param mMeasureSpo2Info
//     * @return
//     */
//    public static MeasureSpo2Info HandleNoData(MeasureSpo2Info mMeasureSpo2Info) {
//        if (mMeasureSpo2Info != null) {
//
//            if (mMeasureSpo2Info.getMeasure_spo2() != null && !mMeasureSpo2Info.getMeasure_spo2().equals("")) {
//                mMeasureSpo2Info = null;
//            }
//        }
//
//        return mMeasureSpo2Info;
//
//    }


}

