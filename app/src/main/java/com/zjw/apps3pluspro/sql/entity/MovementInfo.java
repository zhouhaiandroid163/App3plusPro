package com.zjw.apps3pluspro.sql.entity;


import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.utils.AppUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * 运动表
 * 备注：需要的字段
 * 1.用户ID
 * 2.日期(精确到天/2017-10-18)
 * 3.原始数据(24条/100,200,300)
 * 4.卡路里(千卡,kmal/50)
 * 5.距离(千米,km/50)
 * 6.总步数(/100)
 * 7.插入数据库的时间(unix时间戳)
 * 8.身高(170-公制(cm))
 * 9.体重(65-公制(kg))
 * 10.算法(0,1,2)
 * 11.后台同步状态位（0=未同步，1=已同步）
 */
@Entity
public class MovementInfo {
    private static final String TAG = MovementInfo.class.getSimpleName();
    @Id(autoincrement = true)
    private Long _id;
    @NotNull
    /**
     * 1.用户ID
     */
    private String user_id;
    /**
     * 2.日期(精确到天/2017-10-18)
     */
    private String date;
    /**
     * 3.原始数据(24条/100,200,300)
     */
    private String data;
    /**
     * 4.卡路里(千卡,kmal/50)
     */
    private String calorie;
    /**
     * 5.距离(千米,km/50)
     */
    private String disance;
    /**
     * 6.总步数(/100)
     */
    private String total_step;
    /**
     * 7.插入数据库的时间(unix时间戳)
     */
    private String warehousing_time;
    /**
     * 身高(170-公制(cm))
     */
    private String height;

    /**
     * 9.体重(65-公制(kg))
     */
    private String weight;
    /**
     * 10.算法(0,1,2)
     */
    private String step_algorithm_type;
    /**
     * 11.后台同步状态位（0=未同步，1=已同步）
     */
    private String sync_state;

    @Generated(hash = 1819419629)
    public MovementInfo(Long _id, @NotNull String user_id, String date, String data, String calorie,
                        String disance, String total_step, String warehousing_time, String height, String weight,
                        String step_algorithm_type, String sync_state) {
        this._id = _id;
        this.user_id = user_id;
        this.date = date;
        this.data = data;
        this.calorie = calorie;
        this.disance = disance;
        this.total_step = total_step;
        this.warehousing_time = warehousing_time;
        this.height = height;
        this.weight = weight;
        this.step_algorithm_type = step_algorithm_type;
        this.sync_state = sync_state;
    }

    @Generated(hash = 470263149)
    public MovementInfo() {
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

    public String getCalorie() {
        return this.calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getDisance() {
        return this.disance;
    }

    public void setDisance(String disance) {
        this.disance = disance;
    }

    public String getTotal_step() {
        return this.total_step;
    }

    public void setTotal_step(String total_step) {
        this.total_step = total_step;
    }

    public String getSync_state() {
        return this.sync_state;
    }

    public void setSync_state(String sync_state) {
        this.sync_state = sync_state;
    }


    @Override
    public String toString() {
        return "MovementInfo{" +
                "_id=" + _id +
                ", user_id='" + user_id + '\'' +
                ", date='" + date + '\'' +
                ", data='" + data + '\'' +
                ", calorie='" + calorie + '\'' +
                ", disance='" + disance + '\'' +
                ", total_step='" + total_step + '\'' +
                ", warehousing_time='" + warehousing_time + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", step_algorithm_type='" + step_algorithm_type + '\'' +
                ", sync_state='" + sync_state + '\'' +
                '}';
    }

    public static MovementInfo getMontionModle(byte[] data) {

        UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

        int sport_item_count = data[16] & 0x1F;
        int sportStep = 0;
        String Calory = "0";
        String Distance = "0";

        int sport_data_pos = 17;
        String sport_data = "";

        for (int i = 0; i < sport_item_count; i++) {
            int step = (int) ((((int) data[sport_data_pos] & 0xff) << 8) | data[sport_data_pos + 1] & 0xff);
            sport_data += step;
            sportStep += step;
            sport_data_pos += 2;
            if (i < sport_item_count - 1) {
                sport_data += ",";
            }
        }

        int height = 170;
        if (mUserSetTools.get_user_height() != 0) {
            height = mUserSetTools.get_user_height();
        }

        int weight = 65;
        if (mUserSetTools.get_user_weight() != 0) {
            weight = mUserSetTools.get_user_weight();
        }

        //模拟数据===========================

//        height = 170;
//        sportStep = 230;

//        height = 170;
//        sportStep = 345;

//        height = 170;
//        sportStep = 353;

//        height = 180;
//        sportStep = 1124;


        MyLog.i(TAG, "固件返回值 = height = " + height);
        MyLog.i(TAG, "固件返回值 = weight = " + weight);
        MyLog.i(TAG, "固件返回值 = sportStep = " + sportStep);



        if (mBleDeviceTools.get_step_algorithm_type() == 1) {
            MyLog.i(TAG, "固件返回值 = 算法1");
            Calory = AppUtils.GetFormat(0, BleTools.getOneCalory(height, weight, sportStep));
//            Distance = AppUtils.GetFormat(3, BleTools.getOneDistance(height, sportStep) - 0.005f);
//            Distance = String.valueOf(BleTools.getOneDistance(height, sportStep) - 0.005f);
//            Distance = String.valueOf(BleTools.getOneDistance(height, sportStep));
            Distance = AppUtils.GetTwoFormat(BleTools.getOneDistance(height, sportStep));
        } else if (mBleDeviceTools.get_step_algorithm_type() == 2) {
            MyLog.i(TAG, "固件返回值 = 算法2");
            Calory = AppUtils.GetFormat(0, BleTools.getTwoCalory(height, weight, sportStep));
//            Distance = AppUtils.GetFormat(3, BleTools.getTwoDistance(height, sportStep) - 0.005f);
//            Distance = String.valueOf(BleTools.getTwoDistance(height, sportStep) - 0.005f);
//            Distance = String.valueOf(BleTools.getTwoDistance(height, sportStep));
            Distance = AppUtils.GetTwoFormat(BleTools.getTwoDistance(height, sportStep));
        } else {
            MyLog.i(TAG, "固件返回值 = 算法0");
            Calory = AppUtils.GetFormat(0, BleTools.getOldCalory(sportStep));
//            Distance = AppUtils.GetFormat(3, BleTools.getOldDistance(sportStep) - 0.005f);
//            Distance = AppUtils.GetFormat(3, BleTools.getOldDistance(sportStep) - 0.005f);
//            Distance = String.valueOf(BleTools.getOldDistance(sportStep) - 0.005f);
//            Distance = String.valueOf(BleTools.getOldDistance(sportStep));
            Distance = AppUtils.GetTwoFormat(BleTools.getOldDistance(sportStep));
        }

        MyLog.i(TAG, "算法验证 = Distance = " + Distance);

        Distance = Distance.replace(",", ".");

        if (Float.valueOf(Distance) <= 0.005) {
            Distance = "0";
        }

        MovementInfo mMovementInfo = new MovementInfo();
        mMovementInfo.setUser_id(BaseApplication.getUserId());
        mMovementInfo.setDate(BleTools.getDate(data));
        mMovementInfo.setData(sport_data);
        mMovementInfo.setCalorie(Calory);
        mMovementInfo.setDisance(Distance);
        mMovementInfo.setTotal_step(String.valueOf(sportStep));

//        //如果步数不等于0，则需要上传后台，标志位，置为0
//        if (sportStep != 0) {
//            mMovementInfo.setSync_state("0");
//            //如果步数等于0，则不需要上传后台，标志位，置为1
//        } else {
//            mMovementInfo.setSync_state("1");
//        }
        mMovementInfo.setHeight(String.valueOf(height));
        mMovementInfo.setWeight(String.valueOf(weight));
        mMovementInfo.setStep_algorithm_type(String.valueOf(mBleDeviceTools.get_device_step_algorithm()));
        mMovementInfo.setSync_state("0");


        return mMovementInfo;
    }

    public String getWarehousing_time() {
        return this.warehousing_time;
    }

    public void setWarehousing_time(String warehousing_time) {
        this.warehousing_time = warehousing_time;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return this.weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getStep_algorithm_type() {
        return this.step_algorithm_type;
    }

    public void setStep_algorithm_type(String step_algorithm_type) {
        this.step_algorithm_type = step_algorithm_type;
    }


}
