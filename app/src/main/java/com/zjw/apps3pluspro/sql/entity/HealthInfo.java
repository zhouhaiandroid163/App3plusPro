package com.zjw.apps3pluspro.sql.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.bleservice.BtSerializeation;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.dbmanager.HealthInfoUtils;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 健康表
 * 备注：需要的字段
 * 1.用户ID
 * 2.测量时间(精确到秒/2017-10-18 16:27:20)
 * 3.心率(/70bpm)
 * 4.收缩压(高压/120mmHg)
 * 5.舒张压(低压/70mmHg)
 * 6.HRV分析(0=心电未见异常，1 =心率过缓，2=心率过快)
 * 7.ECG原始数据（逗号隔开的字符串/100,200,300）
 * 8.PPG原始数据（逗号隔开的字符串/100,200,300）
 * 9.健康值(综合值/80)
 * 10.疲劳指数(80)
 * 11.身心负荷(80)
 * 12.身体素质(80)
 * 13.心脏功能(80)
 * 14.传感器类型(0=ECG,1,ECG+PPG,2=PPG,3=无)(4=心电手环+加离线心电，5=心电手环+离线血压，6=运动手环+离线血压)
 * 15.是否支持血压(0=支持，1=不支持)
 * 16.数据ID(后台返回的)
 * 17.插入数据库的时间(unix时间戳)
 * 18.后台同步状态位（0=未同步，1=已同步）
 */
@Entity
public class HealthInfo implements Parcelable {
    private static final String TAG = HealthInfo.class.getSimpleName();
    @Id(autoincrement = true)
    private Long _id;
    @NotNull
    /**
     * 1.用户ID
     */
    private String user_id;
    /**
     * 2.测量时间(精确到秒/2017-10-18 16:27:20)
     */
    private String measure_time;
    /**
     * 3.心率(/70bpm)
     */
    private String health_heart;
    /**
     * 4.收缩压(高压/120mmHg)
     */
    private String health_systolic;
    /**
     * 5.舒张压(低压/70mmHg)
     */
    private String health_diastolic;
    /**
     * 6.HRV分析(0=心电未见异常，1 =心率过缓，2=心率过快)
     */
    private String health_ecg_report;
    /**
     * 7.ECG原始数据（逗号隔开的字符串/100,200,300）
     */
    private String ecg_data;
    /**
     * 8.PPG原始数据（逗号隔开的字符串/100,200,300）
     */
    private String ppg_data;
    /**
     * 9.健康值(综合值/80)
     */
    private String index_health_index;
    /**
     * 10.疲劳指数(80)
     */
    private String index_fatigue_index;
    /**
     * 11.身心负荷(80)
     */
    private String index_body_load;
    /**
     * 12.身体素质(80)
     */
    private String index_body_quality;
    /**
     * 13.心脏功能(80)
     */
    private String index_cardiac_function;
    /**
     * 14.传感器类型(0=ECG,1,ECG+PPG,2=PPG,3=无)(4=心电手环+加离线心电，5=心电手环+离线血压，6=运动手环+离线血压)
     */
    private String sensor_type;
    /**
     * 15.是否支持血压(0=支持，1=不支持)
     */
    private String is_suppor_bp;
    /**
     * 16.数据ID（后台返回的）
     */
    private String data_id;
    /**
     * 17.插入数据库的时间(unix时间戳)
     */
    private String warehousing_time;
    /**
     * 18.后台同步状态位（0=未同步，1=已同步）
     */
    private String sync_state;

    @Generated(hash = 430420077)
    public HealthInfo(Long _id, @NotNull String user_id, String measure_time, String health_heart, String health_systolic, String health_diastolic, String health_ecg_report,
                      String ecg_data, String ppg_data, String index_health_index, String index_fatigue_index, String index_body_load, String index_body_quality,
                      String index_cardiac_function, String sensor_type, String is_suppor_bp, String data_id, String warehousing_time, String sync_state) {
        this._id = _id;
        this.user_id = user_id;
        this.measure_time = measure_time;
        this.health_heart = health_heart;
        this.health_systolic = health_systolic;
        this.health_diastolic = health_diastolic;
        this.health_ecg_report = health_ecg_report;
        this.ecg_data = ecg_data;
        this.ppg_data = ppg_data;
        this.index_health_index = index_health_index;
        this.index_fatigue_index = index_fatigue_index;
        this.index_body_load = index_body_load;
        this.index_body_quality = index_body_quality;
        this.index_cardiac_function = index_cardiac_function;
        this.sensor_type = sensor_type;
        this.is_suppor_bp = is_suppor_bp;
        this.data_id = data_id;
        this.warehousing_time = warehousing_time;
        this.sync_state = sync_state;
    }

    @Generated(hash = 203853863)
    public HealthInfo() {
    }

    @Override
    public String toString() {
        return "HealthInfo{" +
                "_id=" + _id +
                ", user_id='" + user_id + '\'' +
                ", measure_time='" + measure_time + '\'' +
                ", health_heart='" + health_heart + '\'' +
                ", health_systolic='" + health_systolic + '\'' +
                ", health_diastolic='" + health_diastolic + '\'' +
                ", health_ecg_report='" + health_ecg_report + '\'' +
//                ", ecg_data='" + ecg_data + '\'' +
//                ", ppg_data='" + ppg_data + '\'' +
                ", index_health_index='" + index_health_index + '\'' +
                ", index_fatigue_index='" + index_fatigue_index + '\'' +
                ", index_body_load='" + index_body_load + '\'' +
                ", index_body_quality='" + index_body_quality + '\'' +
                ", index_cardiac_function='" + index_cardiac_function + '\'' +
                ", sensor_type='" + sensor_type + '\'' +
                ", is_suppor_bp='" + is_suppor_bp + '\'' +
                ", data_id='" + data_id + '\'' +
                ", warehousing_time='" + warehousing_time + '\'' +
                ", sync_state='" + sync_state + '\'' +
                '}';
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

    public String getHealth_heart() {
        return this.health_heart;
    }

    public void setHealth_heart(String health_heart) {
        this.health_heart = health_heart;
    }

    public String getHealth_systolic() {
        return this.health_systolic;
    }

    public void setHealth_systolic(String health_systolic) {
        this.health_systolic = health_systolic;
    }

    public String getHealth_diastolic() {
        return this.health_diastolic;
    }

    public void setHealth_diastolic(String health_diastolic) {
        this.health_diastolic = health_diastolic;
    }

    public String getHealth_ecg_report() {
        return this.health_ecg_report;
    }

    public void setHealth_ecg_report(String health_ecg_report) {
        this.health_ecg_report = health_ecg_report;
    }

    public String getEcg_data() {
        return this.ecg_data;
    }

    public void setEcg_data(String ecg_data) {
        this.ecg_data = ecg_data;
    }

    public String getPpg_data() {
        return this.ppg_data;
    }

    public void setPpg_data(String ppg_data) {
        this.ppg_data = ppg_data;
    }

    public String getIndex_health_index() {
        return this.index_health_index;
    }

    public void setIndex_health_index(String index_health_index) {
        this.index_health_index = index_health_index;
    }

    public String getIndex_fatigue_index() {
        return this.index_fatigue_index;
    }

    public void setIndex_fatigue_index(String index_fatigue_index) {
        this.index_fatigue_index = index_fatigue_index;
    }

    public String getIndex_body_load() {
        return this.index_body_load;
    }

    public void setIndex_body_load(String index_body_load) {
        this.index_body_load = index_body_load;
    }

    public String getIndex_body_quality() {
        return this.index_body_quality;
    }

    public void setIndex_body_quality(String index_body_quality) {
        this.index_body_quality = index_body_quality;
    }

    public String getIndex_cardiac_function() {
        return this.index_cardiac_function;
    }

    public void setIndex_cardiac_function(String index_cardiac_function) {
        this.index_cardiac_function = index_cardiac_function;
    }

    public String getData_id() {
        return this.data_id;
    }

    public void setData_id(String data_id) {
        this.data_id = data_id;
    }


    public String getIs_suppor_bp() {
        return this.is_suppor_bp;
    }

    public void setIs_suppor_bp(String is_suppor_bp) {
        this.is_suppor_bp = is_suppor_bp;
    }

    public String getSensor_type() {
        return this.sensor_type;
    }

    public void setSensor_type(String sensor_type) {
        this.sensor_type = sensor_type;
    }


    public String getSync_state() {
        return this.sync_state;
    }

    public void setSync_state(String sync_state) {
        this.sync_state = sync_state;
    }


    public static List<HealthInfo> HandleNoData(List<HealthInfo> my_list) {

        List<HealthInfo> result_list = new ArrayList<>();

        if (my_list.size() > 0) {
            for (int i = 0; i < my_list.size(); i++) {


//                if (my_list.get(i).getData_id() == null || (my_list.get(i).getData_id() != null && my_list.get(i).getData_id().equals(ResultJson.Duflet_health_data_id))) {
//
////                    MyLog.i(TAG, "需要删除 i = " + i + "  data_id = " + my_list.get(i).getData_id() + "  time = " + my_list.get(i).getMeasure_time());
////                    my_list.remove(i);
//
//                } else {
//                    result_list.add(my_list.get(i));
////                    MyLog.i(TAG, "保留 i = " + i + "  data_id = " + my_list.get(i).getData_id() + "  time = " + my_list.get(i).getMeasure_time());
//                }

                if (my_list.get(i).getData_id() != null && my_list.get(i).getData_id().equals(ResultJson.Duflet_health_data_id)) {

                } else {

                    result_list.add(my_list.get(i));
                }

            }
        }
        return result_list;
    }

    /**
     * 过滤无效数据
     *
     * @param mHealthInfo
     * @return
     */
    public static HealthInfo HandleNoData(HealthInfo mHealthInfo) {
        if (mHealthInfo != null) {

            if (mHealthInfo.getData_id() != null && mHealthInfo.getData_id().equals(ResultJson.Duflet_health_data_id)) {
                mHealthInfo = null;
            }
        }

        return mHealthInfo;

    }


    /**
     * 离线心电数据
     *
     * @param measure_heart
     * @param my_ecg_data
     */
    public static HealthInfo getOffHealthInfo(String OffLineEcgTime, int measure_heart, int measure_ptp_avg, String my_ecg_data) {


        MyLog.i(TAG, "测量结果 = measure_heart = " + measure_heart);

        //最后结果是否合法
        if (measure_heart > DefaultVale.USER_HEART_MIN && measure_heart < DefaultVale.USER_HEART_MAX) {
            MyLog.i(TAG, "测量结果 = 数值合法");

            BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

            UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
            int user_heart = mUserSetTools.get_calibration_heart();
            int user_systolic = mUserSetTools.get_calibration_systolic();
            String systolic = String.valueOf(MyUtils.getGaoYaUser2(measure_heart, user_heart, user_systolic));
            String diastolic = String.valueOf(MyUtils.getDiYaUser2(measure_heart, user_heart, user_systolic));

            MyLog.i(TAG, "  measure_heart = " + measure_heart);
            MyLog.i(TAG, "  user_heart = " + user_heart);
            MyLog.i(TAG, "  user_systolic = " + user_systolic);
            MyLog.i(TAG, "  systolic = " + systolic);
            MyLog.i(TAG, "  user_heart = " + diastolic);

            int heightValue = !JavaUtil.checkIsNull(String.valueOf(mUserSetTools.get_user_height())) ? mUserSetTools.get_user_height() : DefaultVale.USER_HEIGHT;
            int weightValue = !JavaUtil.checkIsNull(String.valueOf(mUserSetTools.get_user_weight())) ? mUserSetTools.get_user_weight() : DefaultVale.USER_WEIGHT;
            int stepValue = !JavaUtil.checkIsNull(String.valueOf(mUserSetTools.get_user_stpe())) ? mUserSetTools.get_user_stpe() : 0;
            int health_number = 0;
            int AmpAvg = 0;
            int fatigue_index = 0;
            int load_index = 0;
            int body_index = 0;
            int heart_index = 0;


            String report = "0";
            if (measure_heart <= 50) {
                report = "1";
            } else if (measure_heart >= 100) {
                report = "2";
            }


            health_number = MyUtils.getHrvHealthNumber(heightValue, weightValue
                    , stepValue, measure_heart);

            AmpAvg = measure_ptp_avg;

            fatigue_index = MyUtils.getFatigueIndex(measure_heart);
            load_index = MyUtils.getLoadIndex(heightValue, weightValue
                    , measure_heart);
            body_index = MyUtils.getBodyIndex(heightValue, weightValue
                    , measure_heart, AmpAvg);
            heart_index = MyUtils.getHeartIndex(measure_heart, AmpAvg);


            MyLog.i(TAG, "  AmpAvg = " + AmpAvg);
            MyLog.i(TAG, "  health_number = " + health_number);
            MyLog.i(TAG, "  fatigue_index = " + fatigue_index);
            MyLog.i(TAG, "  load_index = " + load_index);
            MyLog.i(TAG, "  body_index = " + body_index);
            MyLog.i(TAG, "  heart_index = " + heart_index);
            MyLog.i(TAG, "  measure_heart = " + measure_heart);


            HealthInfo mHealthInfo = new HealthInfo();
            mHealthInfo.setUser_id(BaseApplication.getUserId());
            mHealthInfo.setHealth_heart(String.valueOf(measure_heart));
            mHealthInfo.setHealth_systolic(String.valueOf(systolic));
            mHealthInfo.setHealth_diastolic(String.valueOf(diastolic));
            mHealthInfo.setHealth_ecg_report(report);
            mHealthInfo.setEcg_data(my_ecg_data);
            mHealthInfo.setPpg_data("");
            mHealthInfo.setIndex_health_index(String.valueOf(health_number));
            mHealthInfo.setIndex_fatigue_index(String.valueOf(fatigue_index));
            mHealthInfo.setIndex_body_load(String.valueOf(load_index));
            mHealthInfo.setIndex_cardiac_function(String.valueOf(heart_index));
            mHealthInfo.setIndex_body_quality(String.valueOf(body_index));
//            mHealthInfo.setMeasure_time(MyTime.getAllTime());
            mHealthInfo.setMeasure_time(OffLineEcgTime);
            mHealthInfo.setData_id("0");
            mHealthInfo.setSync_state("0");


//            传感器类型(0=ECG,1,ECG+PPG,2=PPG,3=无)(4=心电手环+加离线心电，5=心电手环+离线血压，6=运动手环+离线血压)
            //离线心电数据，固定传0
            mHealthInfo.setSensor_type("4");

            //是否支持血压(0 = 支持 ， 1 = 不支持)
            if (mBleDeviceTools.get_is_support_blood() == 0) {
                mHealthInfo.setIs_suppor_bp("0");
            } else {
                mHealthInfo.setIs_suppor_bp("1");
            }


            return mHealthInfo;

        } else {
            MyLog.i(TAG, "测量结果 = 数值不合法");
            return null;
        }


    }

    /**
     * 离线血压数据
     *
     * @param data
     * @return
     */
    public static HealthInfo getOffBpHealthInfo(byte[] data) {

        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

        String TAG = "HealthInfo";

        UserSetTools mUserSetTools = BaseApplication.getUserSetTools();

        String measure_time = BleTools.geBpTime(data);
        if (!BleTools.isRightfulnessTime(BleTools.geBpTime(data))) {
            // 写入错误的数据到error
            SysUtils.logErrorDataI(TAG, BleTools.geBpTime(data) + "  byte=" + BleTools.bytes2HexString(data));
            return null;
        }

        int measure_heart = data[4] & 0xff;
        int systolic = data[5] & 0xff;
        int diastolic = data[6] & 0xff;

        MyLog.i(TAG, "measure_time = " + measure_time);
        MyLog.i(TAG, "  measure_heart = " + measure_heart);
        MyLog.i(TAG, "  systolic = " + systolic);
        MyLog.i(TAG, "  user_heart = " + diastolic);


        int heightValue = !JavaUtil.checkIsNull(String.valueOf(mUserSetTools.get_user_height())) ? mUserSetTools.get_user_height() : DefaultVale.USER_HEIGHT;
        int weightValue = !JavaUtil.checkIsNull(String.valueOf(mUserSetTools.get_user_weight())) ? mUserSetTools.get_user_weight() : DefaultVale.USER_WEIGHT;
        int stepValue = !JavaUtil.checkIsNull(String.valueOf(mUserSetTools.get_user_stpe())) ? mUserSetTools.get_user_stpe() : 0;
        int health_number = 0;
        int AmpAvg = 0;
        int fatigue_index = 0;
        int load_index = 0;
        int body_index = 0;
        int heart_index = 0;

        String report = "0";
        if (measure_heart <= 50) {
            report = "1";
        } else if (measure_heart >= 100) {
            report = "2";
        }

        health_number = MyUtils.getHrvHealthNumber(heightValue, weightValue, stepValue, measure_heart);
        AmpAvg = 100;
        fatigue_index = MyUtils.getFatigueIndex(measure_heart);
        load_index = MyUtils.getLoadIndex(heightValue, weightValue, measure_heart);
        body_index = MyUtils.getBodyIndex(heightValue, weightValue, measure_heart, AmpAvg);
        heart_index = MyUtils.getHeartIndex(measure_heart, AmpAvg);

        MyLog.i(TAG, "  health_number = " + health_number);
        MyLog.i(TAG, "  AmpAvg = " + AmpAvg);
        MyLog.i(TAG, "  fatigue_index = " + fatigue_index);
        MyLog.i(TAG, "  load_index = " + load_index);
        MyLog.i(TAG, "  body_index = " + body_index);
        MyLog.i(TAG, "  heart_index = " + heart_index);
        MyLog.i(TAG, "  measure_heart = " + measure_heart);


        HealthInfo mHealthInfo = new HealthInfo();
        mHealthInfo.setUser_id(BaseApplication.getUserId());
        mHealthInfo.setHealth_heart(String.valueOf(measure_heart));
        mHealthInfo.setHealth_systolic(String.valueOf(systolic));
        mHealthInfo.setHealth_diastolic(String.valueOf(diastolic));
        mHealthInfo.setHealth_ecg_report(report);
        mHealthInfo.setEcg_data("");
        mHealthInfo.setPpg_data("");
        mHealthInfo.setIndex_health_index(String.valueOf(health_number));
        mHealthInfo.setIndex_fatigue_index(String.valueOf(fatigue_index));
        mHealthInfo.setIndex_body_load(String.valueOf(load_index));
        mHealthInfo.setIndex_cardiac_function(String.valueOf(heart_index));
        mHealthInfo.setIndex_body_quality(String.valueOf(body_index));
        mHealthInfo.setMeasure_time(measure_time);
        mHealthInfo.setData_id("0");
        mHealthInfo.setSync_state("0");

        //传感器类型(0=ECG,1,ECG+PPG,2=PPG,3=无)(4=心电手环+加离线心电，5=心电手环+离线血压，6=运动手环+离线血压)
        if (mBleDeviceTools.get_is_support_ecg() == 1) {
            mHealthInfo.setSensor_type("5");
        } else {
            mHealthInfo.setSensor_type("6");
        }

        //是否支持血压(0 = 支持 ， 1 = 不支持)
        if (mBleDeviceTools.get_is_support_blood() == 0) {
            mHealthInfo.setIs_suppor_bp("0");
        } else {
            mHealthInfo.setIs_suppor_bp("1");
        }

        return mHealthInfo;

    }

    /**
     * 离线血压数据处理
     *
     * @param data
     * @return
     */
    public static List<HealthInfo> getHealthInfoList(byte[] data) {

        List<HealthInfo> mHealthInfoList = new ArrayList<>();

        if (data.length >= 16) {
            int count = data[15] & 0xff;
            if (count != 0) {

                for (int i = 0; i < count; i++) {
                    byte[] ss_date = new byte[7];

                    for (int j = 0; j < 7; j++) {
                        ss_date[j] = data[16 + j + i * 7];
                    }

                    HealthInfo mHealthInfo = HealthInfo.getOffBpHealthInfo(ss_date);


                    if (mHealthInfo != null && BtSerializeation.checkDeviceTime(mHealthInfo.getMeasure_time())) {

                        if (!BleTools.isRightfulnessTime(mHealthInfo.getMeasure_time())) {
                            // 写入错误的数据到error
                            SysUtils.logErrorDataI(TAG, mHealthInfo.getMeasure_time() + "  byte=" + BleTools.bytes2HexString(data));
                        } else {
                            if (!JavaUtil.checkIsNull(mHealthInfo.getHealth_heart()) &&
                                    !JavaUtil.checkIsNull(mHealthInfo.getHealth_systolic()) && !
                                    JavaUtil.checkIsNull(mHealthInfo.getHealth_diastolic())) {

//                            mHealthInfoList.add(HealthInfo.getOffBpHealthInfo(ss_date));
                                mHealthInfoList.add(mHealthInfo);
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


        return mHealthInfoList;

    }


    /**
     * 心电测量结果
     *
     * @param measure_time
     * @param measure_heart
     * @param ecg_data
     * @param ppg_data
     * @return
     */
    public static HealthInfo getEcgMeasureResult(String measure_time, int measure_heart, int measure_ptp_avg, String ecg_data, String ppg_data) {

        String TAG = "HealthInfo.getEcgMeasureResult()";

        UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

        int calibration_heart = (mUserSetTools.get_calibration_heart() > 0) ? mUserSetTools.get_calibration_heart() : DefaultVale.USER_HEART;
        int calibration_sbp = (mUserSetTools.get_calibration_systolic() > 0) ? mUserSetTools.get_calibration_systolic() : DefaultVale.USER_SYSTOLIC;
        int calibration_dbp = (mUserSetTools.get_calibration_diastolic() > 0) ? mUserSetTools.get_calibration_diastolic() : DefaultVale.USER_DIASTOLIC;

        int heightValue = (mUserSetTools.get_user_height() > 0) ? mUserSetTools.get_user_height() : DefaultVale.USER_HEIGHT;
        int weightValue = (mUserSetTools.get_user_weight() > 0) ? mUserSetTools.get_user_weight() : DefaultVale.USER_WEIGHT;
        int stepValue = mUserSetTools.get_user_stpe();

        String report = "0";
        if (measure_heart <= 50) {
            report = "1";
        } else if (measure_heart >= 100) {
            report = "2";
        }


        String systolic = String.valueOf(MyUtils.getGaoYaUser2(measure_heart, calibration_heart, calibration_sbp));
        String diastolic = String.valueOf(MyUtils.getDiYaUser2(measure_heart, calibration_heart, calibration_sbp));

        int AmpAvg = measure_ptp_avg;

        //健康指数
        int health_number = MyUtils.getHrvHealthNumber(heightValue, weightValue, stepValue, measure_heart);
        int fatigue_index = MyUtils.getFatigueIndex(measure_heart);
        int load_index = MyUtils.getLoadIndex(heightValue, weightValue, measure_heart);
        int body_index = MyUtils.getBodyIndex(heightValue, weightValue, measure_heart, AmpAvg);
        int heart_index = MyUtils.getHeartIndex(measure_heart, AmpAvg);


        MyLog.i(TAG, "测量结果 = 峰值 = AmpAvg = " + AmpAvg);

        MyLog.i(TAG, "测量结果 = health_number = " + health_number);
        MyLog.i(TAG, "测量结果 = fatigue_index = " + fatigue_index);
        MyLog.i(TAG, "测量结果 = load_index = " + load_index);
        MyLog.i(TAG, "测量结果 = body_index = " + body_index);
        MyLog.i(TAG, "测量结果 = heart_index = " + heart_index);


        //是否需要处理===============
        if (health_number > 0 && health_number <= 100) {
            mUserSetTools.set_health_index(health_number);
        } else {
            mUserSetTools.set_health_index(0);
        }

        if (fatigue_index > 0 && fatigue_index <= 100) {
            mUserSetTools.set_fatigu_index(fatigue_index);
        } else {
            mUserSetTools.set_fatigu_index(0);
        }

        if (load_index > 0 && load_index <= 100) {
            mUserSetTools.set_load_index(load_index);
        } else {
            mUserSetTools.set_load_index(0);
        }

        if (body_index > 0 && body_index <= 100) {
            mUserSetTools.set_body_index(body_index);
        } else {
            mUserSetTools.set_body_index(0);
        }

        if (heart_index > 0 && heart_index <= 100) {
            mUserSetTools.set_heart_index(heart_index);
        } else {
            mUserSetTools.set_heart_index(0);
        }
        //是否需要处理===============

        HealthInfo mHealthInfo = new HealthInfo();
        mHealthInfo.setUser_id(BaseApplication.getUserId());
        mHealthInfo.setMeasure_time(measure_time);
        mHealthInfo.setPpg_data(ppg_data);
        mHealthInfo.setEcg_data(ecg_data);
        mHealthInfo.setHealth_heart(String.valueOf(measure_heart));
        mHealthInfo.setHealth_systolic(systolic);
        mHealthInfo.setHealth_diastolic(diastolic);
        mHealthInfo.setHealth_ecg_report(report);
        mHealthInfo.setIndex_health_index(String.valueOf(health_number));
        mHealthInfo.setIndex_fatigue_index(String.valueOf(fatigue_index));
        mHealthInfo.setIndex_cardiac_function(String.valueOf(heart_index));
        mHealthInfo.setIndex_body_quality(String.valueOf(body_index));
        mHealthInfo.setIndex_body_load(String.valueOf(load_index));
        mHealthInfo.setData_id("0");
        mHealthInfo.setSync_state("0");


        //传感器类型(0=ECG,1,ECG+PPG,2=PPG,3=无)
        if (mBleDeviceTools.get_is_support_ecg() == 1 && mBleDeviceTools.get_is_support_ppg() == 0) {
            mHealthInfo.setSensor_type("0");
        } else if (mBleDeviceTools.get_is_support_ecg() == 1 && mBleDeviceTools.get_is_support_ppg() == 1) {
            mHealthInfo.setSensor_type("1");
        } else if (mBleDeviceTools.get_is_support_ecg() == 0 && mBleDeviceTools.get_is_support_ppg() == 1) {
            mHealthInfo.setSensor_type("2");
        } else {
            mHealthInfo.setSensor_type("3");
        }
        //是否支持血压(0 = 支持 ， 1 = 不支持)
        if (mBleDeviceTools.get_is_support_blood() == 0) {
            mHealthInfo.setIs_suppor_bp("0");
        } else {
            mHealthInfo.setIs_suppor_bp("1");
        }


        return mHealthInfo;

    }


    /**
     * PPG测量结果
     *
     * @param measure_time
     * @param measure_heart
     * @return
     */
    public static HealthInfo getPpgMeasureResult(String measure_time, int measure_heart, int measure_ptp_avg) {

        String TAG = "HealthInfo.getPpgMeasureResult()";

        UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

        int calibration_heart = (mUserSetTools.get_calibration_heart() > 0) ? mUserSetTools.get_calibration_heart() : DefaultVale.USER_HEART;
        int calibration_sbp = (mUserSetTools.get_calibration_systolic() > 0) ? mUserSetTools.get_calibration_systolic() : DefaultVale.USER_SYSTOLIC;
        int calibration_dbp = (mUserSetTools.get_calibration_diastolic() > 0) ? mUserSetTools.get_calibration_diastolic() : DefaultVale.USER_DIASTOLIC;

        int heightValue = (mUserSetTools.get_user_height() > 0) ? mUserSetTools.get_user_height() : DefaultVale.USER_HEIGHT;
        int weightValue = (mUserSetTools.get_user_weight() > 0) ? mUserSetTools.get_user_weight() : DefaultVale.USER_WEIGHT;
        int stepValue = mUserSetTools.get_user_stpe();

        String report = "0";
        if (measure_heart <= 50) {
            report = "1";
        } else if (measure_heart >= 100) {
            report = "2";
        }


        String systolic = String.valueOf(MyUtils.getGaoYaUser2(measure_heart, calibration_heart, calibration_sbp));
        String diastolic = String.valueOf(MyUtils.getDiYaUser2(measure_heart, calibration_heart, calibration_sbp));

        int AmpAvg = measure_ptp_avg;

        //健康指数
        int health_number = MyUtils.getHrvHealthNumber(heightValue, weightValue, stepValue, measure_heart);
        int fatigue_index = MyUtils.getFatigueIndex(measure_heart);
        int load_index = MyUtils.getLoadIndex(heightValue, weightValue, measure_heart);
        int body_index = MyUtils.getBodyIndex(heightValue, weightValue, measure_heart, AmpAvg);
        int heart_index = MyUtils.getHeartIndex(measure_heart, AmpAvg);


        MyLog.i(TAG, "测量结果 = 峰值 = AmpAvg = " + AmpAvg);

        MyLog.i(TAG, "测量结果 = health_number = " + health_number);
        MyLog.i(TAG, "测量结果 = fatigue_index = " + fatigue_index);
        MyLog.i(TAG, "测量结果 = load_index = " + load_index);
        MyLog.i(TAG, "测量结果 = body_index = " + body_index);
        MyLog.i(TAG, "测量结果 = heart_index = " + heart_index);


        //是否需要处理===============
        if (health_number > 0 && health_number <= 100) {
            mUserSetTools.set_health_index(health_number);
        } else {
            mUserSetTools.set_health_index(0);
        }

        if (fatigue_index > 0 && fatigue_index <= 100) {
            mUserSetTools.set_fatigu_index(fatigue_index);
        } else {
            mUserSetTools.set_fatigu_index(0);
        }

        if (load_index > 0 && load_index <= 100) {
            mUserSetTools.set_load_index(load_index);
        } else {
            mUserSetTools.set_load_index(0);
        }

        if (body_index > 0 && body_index <= 100) {
            mUserSetTools.set_body_index(body_index);
        } else {
            mUserSetTools.set_body_index(0);
        }

        if (heart_index > 0 && heart_index <= 100) {
            mUserSetTools.set_heart_index(heart_index);
        } else {
            mUserSetTools.set_heart_index(0);
        }
        //是否需要处理===============

        HealthInfo mHealthInfo = new HealthInfo();
        mHealthInfo.setUser_id(BaseApplication.getUserId());
        mHealthInfo.setMeasure_time(measure_time);
        mHealthInfo.setPpg_data("");
        mHealthInfo.setEcg_data("");
        mHealthInfo.setHealth_heart(String.valueOf(measure_heart));
        mHealthInfo.setHealth_systolic(systolic);
        mHealthInfo.setHealth_diastolic(diastolic);
        mHealthInfo.setHealth_ecg_report(report);
        mHealthInfo.setIndex_health_index(String.valueOf(health_number));
        mHealthInfo.setIndex_fatigue_index(String.valueOf(fatigue_index));
        mHealthInfo.setIndex_cardiac_function(String.valueOf(heart_index));
        mHealthInfo.setIndex_body_quality(String.valueOf(body_index));
        mHealthInfo.setIndex_body_load(String.valueOf(load_index));
        mHealthInfo.setData_id("0");
        mHealthInfo.setSync_state("0");


        //传感器类型(0=ECG,1,ECG+PPG,2=PPG,3=无)
        if (mBleDeviceTools.get_is_support_ecg() == 1 && mBleDeviceTools.get_is_support_ppg() == 0) {
            mHealthInfo.setSensor_type("0");
        } else if (mBleDeviceTools.get_is_support_ecg() == 1 && mBleDeviceTools.get_is_support_ppg() == 1) {
            mHealthInfo.setSensor_type("1");
        } else if (mBleDeviceTools.get_is_support_ecg() == 0 && mBleDeviceTools.get_is_support_ppg() == 1) {
            mHealthInfo.setSensor_type("2");
        } else {
            mHealthInfo.setSensor_type("3");
        }
        //是否支持血压(0 = 支持 ， 1 = 不支持)
        if (mBleDeviceTools.get_is_support_blood() == 0) {
            mHealthInfo.setIs_suppor_bp("0");
        } else {
            mHealthInfo.setIs_suppor_bp("1");
        }


        return mHealthInfo;

    }


    /**
     * 获取健康指数，均值
     *
     * @param mHealthInfoUtils
     * @param week_lis
     * @param is_support_ecg
     * @return
     */
    public static List<String> getHealthIndexAvgDataList(HealthInfoUtils mHealthInfoUtils, ArrayList<String> week_lis, boolean is_support_ecg) {

        String TAG = "HealthInfo";

        List<String> result_data_list = new ArrayList<>();
        List<HealthInfo> health_my_list;

        for (int i = 0; i < week_lis.size(); i++) {

            if (is_support_ecg) {
                health_my_list = mHealthInfoUtils.queryToDateList(BaseApplication.getUserId(), week_lis.get(i), week_lis.get(i), true);
                MyLog.i(TAG, "result_data_list过滤前1 = size = " + result_data_list.size());
            } else {
                health_my_list = mHealthInfoUtils.queryToDateList(BaseApplication.getUserId(), week_lis.get(i), week_lis.get(i), false);
                MyLog.i(TAG, "result_data_list过滤前2 = size = " + result_data_list.size());
            }


            //过滤数据
            if (health_my_list.size() > 0) {
                health_my_list = HealthInfo.HandleNoData(health_my_list);
            }

            MyLog.i(TAG, "result_data_list过滤后 = size = " + result_data_list.size());


            if (health_my_list.size() > 0) {
                int my_count = 0;
                int my_total_health_index = 0;
                int my_avg_total_health_index = 0;

                for (int j = 0; j < health_my_list.size(); j++) {

                    HealthInfo myHealthInfo = health_my_list.get(j);

                    //健康值
                    String my_health_index = !JavaUtil.checkIsNull(myHealthInfo.getIndex_health_index()) ? myHealthInfo.getIndex_health_index() : "0";

                    if (!JavaUtil.checkIsNull(myHealthInfo.getIndex_health_index())) {
                        my_count += 1;

                        try {
                            my_total_health_index += Integer.valueOf(my_health_index);
                        } catch (Exception e) {
                            MyLog.i(TAG, "捕捉异常 e = " + e.toString());
                        }
                    }
                }
                if (!JavaUtil.checkIsNull(String.valueOf(my_total_health_index)) && my_count > 0) {
                    my_avg_total_health_index = my_total_health_index / my_count;
                }

                result_data_list.add(String.valueOf(my_avg_total_health_index));
            } else {
                result_data_list.add(String.valueOf("0"));
            }
        }

        return result_data_list;


    }


    //===================Parcel相关==================

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(user_id);
        dest.writeString(measure_time);
        dest.writeString(health_heart);
        dest.writeString(health_systolic);
        dest.writeString(health_diastolic);
        dest.writeString(health_ecg_report);
        dest.writeString(ecg_data);
        dest.writeString(ppg_data);
        dest.writeString(index_health_index);
        dest.writeString(index_fatigue_index);
        dest.writeString(index_body_load);
        dest.writeString(index_body_quality);
        dest.writeString(index_cardiac_function);
        dest.writeString(sensor_type);
        dest.writeString(is_suppor_bp);
        dest.writeString(data_id);
        dest.writeString(sync_state);

    }

    public String getWarehousing_time() {
        return this.warehousing_time;
    }

    public void setWarehousing_time(String warehousing_time) {
        this.warehousing_time = warehousing_time;
    }


    protected HealthInfo(Parcel in) {
        user_id = in.readString();
        measure_time = in.readString();
        health_heart = in.readString();
        health_systolic = in.readString();
        health_diastolic = in.readString();
        health_ecg_report = in.readString();
        ecg_data = in.readString();
        ppg_data = in.readString();
        index_health_index = in.readString();
        index_fatigue_index = in.readString();
        index_body_load = in.readString();
        index_body_quality = in.readString();
        index_cardiac_function = in.readString();
        sensor_type = in.readString();
        is_suppor_bp = in.readString();
        data_id = in.readString();
        sync_state = in.readString();
    }


    public static final Parcelable.Creator<HealthInfo> CREATOR = new Creator<HealthInfo>() {
        @Override
        public HealthInfo createFromParcel(Parcel in) {
            return new HealthInfo(in);
        }

        @Override
        public HealthInfo[] newArray(int size) {
            return new HealthInfo[size];
        }
    };

    //是否有效
    public boolean isValidData() {
        if (sensor_type.equals("0") || sensor_type.equals("1") || sensor_type.equals("2") || sensor_type.equals("4") || sensor_type.equals("6")) {
            return true;
        } else {
            return false;
        }
    }

    //是否是心电设备
    public boolean isEcgDevice() {
        if (sensor_type.equals("0") || sensor_type.equals("1") || sensor_type.equals("4")) {
            return true;
        } else {
            return false;
        }
    }

    //是否是运动设备
    public boolean isSportDevice() {
        if (sensor_type.equals("2") || sensor_type.equals("6")) {
            return true;
        } else {
            return false;
        }
    }

}
