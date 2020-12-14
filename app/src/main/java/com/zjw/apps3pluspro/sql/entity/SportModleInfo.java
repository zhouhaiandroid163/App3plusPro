package com.zjw.apps3pluspro.sql.entity;


import android.os.Parcel;
import android.os.Parcelable;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.bleservice.BtSerializeation;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.SysUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 运动表
 * 备注：需要的字段
 * 1.用户ID
 * 2.时间(精确到秒/2017-10-18 12:42:59)
 * 3.地图坐标点数据(XY坐标集合)
 * 4.卡路里(千卡,kmal/50)
 * 5.距离(千米,km/50)
 * 6.总步数(/100)
 * 7.运动时长(精确到秒/126)
 * 8.配速(精确到秒/126)
 * 9.心率()
 * 10.地图类型()
 * 11.运动类型()
 * 12.UI类型()
 * 13.插入数据库的时间(unix时间戳)
 * 14.后台同步状态位（0=未同步，1=已同步）
 */
@Entity
public class SportModleInfo implements Parcelable {
    private static final String TAG = SportModleInfo.class.getSimpleName();
    @Id(autoincrement = true)
    private Long _id;
    @NotNull
    /**
     * 1.用户ID
     */
    private String user_id;
    /**
     * 2.时间(开始时间：精确到秒/2017-10-18 12:42:59)
     */
    private String time;
    /**
     * 3.地图坐标点数据(XY坐标集合)
     */
    private String map_data;
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
     * 7.运动时长(精确到秒/126)
     */
    private String sport_duration;
    /**
     * 8.配速(精确到秒/126)
     */
    private String speed;
    /**
     * 9.心率()
     */
    private String heart;
    /**
     * 10.地图类型()
     */
    private String map_type;
    /**
     * 11.运动类型()
     */
    private String sport_type;
    /**
     * 12.UI类型()
     */
    private String ui_type;
    /**
     * 13.插入数据库的时间(unix时间戳)
     */
    private String warehousing_time;
    /**
     * 14.后台同步状态位（0=未同步 or null，1=已同步）
     */
    private String sync_state;

    // 数据来源类型 0：gps and offline sport data  1: portobuf sport
    private int dataSourceType = 0;
    // dataSourceType == 1 以下参数有效
    private String recordPointDataId = "";         // 原始数据的id，16进制字节码
    private long recordPointIdTime = 0;            // ms
    private int recordPointTimeZone = 8;
    private int recordPointVersion = 0;
    private int recordPointTypeDescription = 0;          //0=保留(无效)，1=运动数据，默认1
    private int recordPointSportType = 0;                //0=无，1=户外跑步，2=户外健走， 3=室内跑步， 4=登山，5=越野，6=户外骑行，7=室内骑行，8=自由训练， 9=泳池游泳，10=开放水域游泳
    private int recordPointDataType = 0;                 //0=打点,1=报告,2=GPS
    private int recordPointEncryption;        // 压缩与加密
    private int recordPointDataValid1;        // 数据有效性1
    private int recordPointDataValid2;        // 数据有效性2
    private String recordPointSportData;               // 打点16进制数据

    private int reportGpsEncryption;             // 压缩与加密
    private int reportGpsValid1;                // 数据有效性1
    private String recordGpsTime;               // 经纬度时间戳  s

    private int reportEncryption;             // 压缩与加密
    private int reportDataValid1;             // 数据有效性1
    private int reportDataValid2;             // 数据有效性2
    private int reportDataValid3;             // 数据有效性3
    private int reportDataValid4;             // 数据有效性4
    private long reportSportStartTime;                      //运动开始时间
    private long reportSportEndTime;                        //运动结束时间
    private long reportDuration;                            //运动总时长 s
    private long reportDistance;                            //总里程 m
    private long reportCal;                                 //卡路里 kcal
    private long reportFastPace;                            //最快配速
    private long reportSlowestPace;                         //最慢配速
    private float reportFastSpeed;                         //最快速度
    private long reportTotalStep;                           //总步数
    private int reportMaxStepSpeed;                         //最大步频
    private int reportAvgHeart;                             //平均心率
    private int reportMaxHeart;
    private int reportMinHeart;
    private float reportCumulativeRise;                    //累计上升
    private float reportCumulativeDecline;                 //累计下降
    private float reportAvgHeight;                         //平均高度
    private float reportMaxHeight;
    private float reportMinHeight;
    private float reportTrainingEffect;                    //训练效果
    private int reportMaxOxygenIntake;                      //最大摄氧量
    private int reportEnergyConsumption;                    //身体能量消耗
    private long reportRecoveryTime;                        //预计恢复时间
    private long reportHeartLimitTime;                      //心率-极限时长
    private long reportHeartAnaerobic;                      //心率-无氧耐力时长
    private long reportHeartAerobic;                        //心率-有氧耐力时长
    private long reportHeartFatBurning;                     //心率-燃脂时长
    private long reportHeartWarmUp;                         //心率-热身时长

    private long serviceId; // 服务器数据id
    private String deviceMac; // mac id

    @Generated(hash = 925053204)
    public SportModleInfo() {
    }

    public SportModleInfo(byte[] data, int dataFormatVersion) {

        String sport_date = geBpTime(data);
        if (dataFormatVersion == 1) {

            String[] dataString = BleTools.bytes2HexString(data).split(" ");
            int n = 4;
            int sport_time = Integer.parseInt(dataString[n] + dataString[n + 1] + dataString[n + 2] + dataString[n + 3], 16);
            n = n + 4;
            int spor_type = Integer.parseInt(dataString[n],16);
            n = n + 1;
            int ui_type = Integer.parseInt(dataString[n],16);
            n = n + 1;
            int sport_step = Integer.parseInt(dataString[n] + dataString[n + 1] + dataString[n + 2] + dataString[n + 3], 16);
            n = n + 4;
            int sport_kcal = Integer.parseInt(dataString[n] + dataString[n + 1] + dataString[n + 2] + dataString[n + 3], 16);
            n = n + 4;
            int sport_distance= Integer.parseInt(dataString[n] + dataString[n + 1] + dataString[n + 2] + dataString[n + 3], 16);
            n = n + 4;
            int spor_hr =Integer.parseInt(dataString[n],16);

            setData(sport_date, sport_time, spor_type, ui_type, sport_step, sport_kcal, sport_distance, spor_hr);
        } else {
            int sport_time = ((data[4] & 0xFF) << 8) | data[5] & 0xFF;
            int spor_type = (data[6] >> 4) & 0xf;
            int ui_type = data[6] & 0xf;
            int sport_step = ((data[7] & 0xFF) << 8) | data[8] & 0xFF;
            int sport_kcal = ((data[9] & 0xFF) << 8) | data[10] & 0xFF;
            int sport_distance = ((data[11] & 0xFF) << 8) | data[12] & 0xFF;
            int spor_hr = data[13] & 0xff;
            setData(sport_date, sport_time, spor_type, ui_type, sport_step, sport_kcal, sport_distance, spor_hr);
        }
    }

    public static final Creator<SportModleInfo> CREATOR = new Creator<SportModleInfo>() {
        @Override
        public SportModleInfo createFromParcel(Parcel in) {
            return new SportModleInfo(in);
        }

        @Override
        public SportModleInfo[] newArray(int size) {
            return new SportModleInfo[size];
        }
    };

    private void setData(String sport_date, int sport_time, int spor_type, int ui_type, int sport_step, int sport_kcal, int sport_distance, int spor_hr) {
        setUser_id(BaseApplication.getUserId());
        setTime(sport_date);
        setSport_duration(String.valueOf(sport_time));
        setSport_type(String.valueOf(spor_type));
        setUi_type(String.valueOf(ui_type));
        setTotal_step(String.valueOf(sport_step));
        setCalorie(String.valueOf(sport_kcal));
        setDisance(String.valueOf(sport_distance * 10));
        setHeart(String.valueOf(spor_hr));
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

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMap_data() {
        return this.map_data;
    }

    public void setMap_data(String map_data) {
        this.map_data = map_data;
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

    public String getSport_duration() {
        return this.sport_duration;
    }

    public void setSport_duration(String sport_duration) {
        this.sport_duration = sport_duration;
    }

    public String getSpeed() {
        return this.speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getHeart() {
        return this.heart;
    }

    public void setHeart(String heart) {
        this.heart = heart;
    }

    public String getMap_type() {
        return this.map_type;
    }

    public void setMap_type(String map_type) {
        this.map_type = map_type;
    }

    public String getSport_type() {
        return this.sport_type;
    }

    public void setSport_type(String sport_type) {
        this.sport_type = sport_type;
    }

    public String getUi_type() {
        return this.ui_type;
    }

    public void setUi_type(String ui_type) {
        this.ui_type = ui_type;
    }

    public String getSync_state() {
        return this.sync_state;
    }

    public void setSync_state(String sync_state) {
        this.sync_state = sync_state;
    }

    @Override
    public String toString() {
        return "SportModleInfo{" +
                "_id=" + _id +
                ", user_id='" + user_id + '\'' +
                ", time='" + time + '\'' +
//                ", map_data='" + map_data + '\'' +
                ", calorie='" + calorie + '\'' +
                ", disance='" + disance + '\'' +
                ", total_step='" + total_step + '\'' +
                ", sport_duration='" + sport_duration + '\'' +
                ", speed='" + speed + '\'' +
                ", heart='" + heart + '\'' +
                ", map_type='" + map_type + '\'' +
                ", sport_type='" + sport_type + '\'' +
                ", ui_type='" + ui_type + '\'' +
                ", warehousing_time='" + warehousing_time + '\'' +
                ", sync_state='" + sync_state + '\'' +
                ", dataSourceType=" + dataSourceType +
//                ", recordPointDataId='" + recordPointDataId + '\'' +
//                ", recordPointIdTime=" + recordPointIdTime +
//                ", recordPointTimeZone=" + recordPointTimeZone +
//                ", recordPointVersion=" + recordPointVersion +
//                ", recordPointTypeDescription=" + recordPointTypeDescription +
//                ", recordPointSportType=" + recordPointSportType +
//                ", recordPointDataType=" + recordPointDataType +
//                ", recordPointEncryption=" + recordPointEncryption +
//                ", recordPointDataValid1=" + recordPointDataValid1 +
//                ", recordPointDataValid2=" + recordPointDataValid2 +
//                ", recordPointSportData='" + recordPointSportData + '\'' +
//                ", reportEncryption=" + reportEncryption +
//                ", reportDataValid1=" + reportDataValid1 +
//                ", reportDataValid2=" + reportDataValid2 +
//                ", reportDataValid3=" + reportDataValid3 +
//                ", reportDataValid4=" + reportDataValid4 +
//                ", reportSportStartTime=" + reportSportStartTime +
//                ", reportSportEndTime=" + reportSportEndTime +
//                ", reportDuration=" + reportDuration +
//                ", reportDistance=" + reportDistance +
//                ", reportCal=" + reportCal +
//                ", reportFastPace=" + reportFastPace +
//                ", reportSlowestPace=" + reportSlowestPace +
//                ", reportFastSpeed=" + reportFastSpeed +
//                ", reportTotalStep=" + reportTotalStep +
//                ", reportMaxStepSpeed=" + reportMaxStepSpeed +
//                ", reportAvgHeart=" + reportAvgHeart +
//                ", reportMaxHeart=" + reportMaxHeart +
//                ", reportMinHeart=" + reportMinHeart +
//                ", reportCumulativeRise=" + reportCumulativeRise +
//                ", reportCumulativeDecline=" + reportCumulativeDecline +
//                ", reportAvgHeight=" + reportAvgHeight +
//                ", reportMaxHeight=" + reportMaxHeight +
//                ", reportMinHeight=" + reportMinHeight +
//                ", reportTrainingEffect=" + reportTrainingEffect +
//                ", reportMaxOxygenIntake=" + reportMaxOxygenIntake +
//                ", reportEnergyConsumption=" + reportEnergyConsumption +
//                ", reportRecoveryTime=" + reportRecoveryTime +
//                ", reportHeartLimitTime=" + reportHeartLimitTime +
//                ", reportHeartAnaerobic=" + reportHeartAnaerobic +
//                ", reportHeartAerobic=" + reportHeartAerobic +
//                ", reportHeartFatBurning=" + reportHeartFatBurning +
//                ", reportHeartWarmUp=" + reportHeartWarmUp +
                ", serviceId=" + serviceId +
                ", deviceMac='" + deviceMac + '\'' +
                '}';
    }

    //===================Parcel相关==================

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(time);
        dest.writeString(map_data);
        dest.writeString(calorie);
        dest.writeString(disance);
        dest.writeString(total_step);
        dest.writeString(sport_duration);
        dest.writeString(speed);
        dest.writeString(heart);
        dest.writeString(map_type);
        dest.writeString(sport_type);
        dest.writeString(ui_type);
        dest.writeString(sync_state);
        dest.writeLong(serviceId);
    }


    protected SportModleInfo(Parcel in) {
        user_id = in.readString();
        time = in.readString();
        map_data = in.readString();
        calorie = in.readString();
        disance = in.readString();
        total_step = in.readString();
        sport_duration = in.readString();
        speed = in.readString();
        heart = in.readString();
        map_type = in.readString();
        sport_type = in.readString();
        ui_type = in.readString();
        sync_state = in.readString();
        serviceId = in.readInt();
    }

    @Generated(hash = 377549718)
    public SportModleInfo(Long _id, @NotNull String user_id, String time, String map_data, String calorie, String disance, String total_step,
            String sport_duration, String speed, String heart, String map_type, String sport_type, String ui_type, String warehousing_time,
            String sync_state, int dataSourceType, String recordPointDataId, long recordPointIdTime, int recordPointTimeZone, int recordPointVersion,
            int recordPointTypeDescription, int recordPointSportType, int recordPointDataType, int recordPointEncryption, int recordPointDataValid1,
            int recordPointDataValid2, String recordPointSportData, int reportGpsEncryption, int reportGpsValid1, String recordGpsTime,
            int reportEncryption, int reportDataValid1, int reportDataValid2, int reportDataValid3, int reportDataValid4, long reportSportStartTime,
            long reportSportEndTime, long reportDuration, long reportDistance, long reportCal, long reportFastPace, long reportSlowestPace,
            float reportFastSpeed, long reportTotalStep, int reportMaxStepSpeed, int reportAvgHeart, int reportMaxHeart, int reportMinHeart,
            float reportCumulativeRise, float reportCumulativeDecline, float reportAvgHeight, float reportMaxHeight, float reportMinHeight,
            float reportTrainingEffect, int reportMaxOxygenIntake, int reportEnergyConsumption, long reportRecoveryTime, long reportHeartLimitTime,
            long reportHeartAnaerobic, long reportHeartAerobic, long reportHeartFatBurning, long reportHeartWarmUp, long serviceId, String deviceMac) {
        this._id = _id;
        this.user_id = user_id;
        this.time = time;
        this.map_data = map_data;
        this.calorie = calorie;
        this.disance = disance;
        this.total_step = total_step;
        this.sport_duration = sport_duration;
        this.speed = speed;
        this.heart = heart;
        this.map_type = map_type;
        this.sport_type = sport_type;
        this.ui_type = ui_type;
        this.warehousing_time = warehousing_time;
        this.sync_state = sync_state;
        this.dataSourceType = dataSourceType;
        this.recordPointDataId = recordPointDataId;
        this.recordPointIdTime = recordPointIdTime;
        this.recordPointTimeZone = recordPointTimeZone;
        this.recordPointVersion = recordPointVersion;
        this.recordPointTypeDescription = recordPointTypeDescription;
        this.recordPointSportType = recordPointSportType;
        this.recordPointDataType = recordPointDataType;
        this.recordPointEncryption = recordPointEncryption;
        this.recordPointDataValid1 = recordPointDataValid1;
        this.recordPointDataValid2 = recordPointDataValid2;
        this.recordPointSportData = recordPointSportData;
        this.reportGpsEncryption = reportGpsEncryption;
        this.reportGpsValid1 = reportGpsValid1;
        this.recordGpsTime = recordGpsTime;
        this.reportEncryption = reportEncryption;
        this.reportDataValid1 = reportDataValid1;
        this.reportDataValid2 = reportDataValid2;
        this.reportDataValid3 = reportDataValid3;
        this.reportDataValid4 = reportDataValid4;
        this.reportSportStartTime = reportSportStartTime;
        this.reportSportEndTime = reportSportEndTime;
        this.reportDuration = reportDuration;
        this.reportDistance = reportDistance;
        this.reportCal = reportCal;
        this.reportFastPace = reportFastPace;
        this.reportSlowestPace = reportSlowestPace;
        this.reportFastSpeed = reportFastSpeed;
        this.reportTotalStep = reportTotalStep;
        this.reportMaxStepSpeed = reportMaxStepSpeed;
        this.reportAvgHeart = reportAvgHeart;
        this.reportMaxHeart = reportMaxHeart;
        this.reportMinHeart = reportMinHeart;
        this.reportCumulativeRise = reportCumulativeRise;
        this.reportCumulativeDecline = reportCumulativeDecline;
        this.reportAvgHeight = reportAvgHeight;
        this.reportMaxHeight = reportMaxHeight;
        this.reportMinHeight = reportMinHeight;
        this.reportTrainingEffect = reportTrainingEffect;
        this.reportMaxOxygenIntake = reportMaxOxygenIntake;
        this.reportEnergyConsumption = reportEnergyConsumption;
        this.reportRecoveryTime = reportRecoveryTime;
        this.reportHeartLimitTime = reportHeartLimitTime;
        this.reportHeartAnaerobic = reportHeartAnaerobic;
        this.reportHeartAerobic = reportHeartAerobic;
        this.reportHeartFatBurning = reportHeartFatBurning;
        this.reportHeartWarmUp = reportHeartWarmUp;
        this.serviceId = serviceId;
        this.deviceMac = deviceMac;
    }






    public static List<SportModleInfo> getBleSportModleList(byte[] data) {

        List<SportModleInfo> mBleSportModleList = new ArrayList<>();

        if (data.length >= 16) {

            int dataFormatVersion = data[13] & 0xff;

            int count = data[15] & 0xff;
            if (count != 0) {
                int timeLength = 4;
                int dataLength = 10;
                if (dataFormatVersion == 1) {
                    dataLength = 19;
                }
                for (int i = 0; i < count; i++) {
                    byte[] ss_date = new byte[timeLength + dataLength];

                    for (int j = 0; j < ss_date.length; j++) {
                        ss_date[j] = data[16 + j + i * ss_date.length];
                    }
                    SportModleInfo mSportModleInfo = new SportModleInfo(ss_date, dataFormatVersion);
                    if (BtSerializeation.checkDeviceTime(mSportModleInfo.getTime())) {
                        if (!BleTools.isRightfulnessTime(mSportModleInfo.getTime())) {
                            // 写入错误的数据到error
                            SysUtils.logErrorDataI(TAG, mSportModleInfo.getTime() + "  byte=" + BleTools.bytes2HexString(data));
                        } else {
                            mBleSportModleList.add(mSportModleInfo);
                        }
                    }

                }
            } else {
                return null;
            }
        } else {
            return null;
        }


        return mBleSportModleList;


    }

    /**
     * 解析日期
     *
     * @param data
     * @return
     */
    public static String geBpTime(byte[] data) {
        int Ayear = ((data[0] & 0xfc) >> 2);
        int Amon = ((data[0] & 0x03) << 2) | ((data[1] & 0xc0) >> 6);
        int Aday = ((data[1] & 0x3e) >> 1);
        int Ahour = ((data[1] & 0x01) << 4) | ((data[2] & 0xf0) >> 4);
        int Amin = ((data[2] & 0x0f) << 2) | ((data[3] & 0xc0) >> 6);
        int Asec = data[3] & 0x3f;


        if (Ayear >= 17 && Amon > 0 && Aday > 0) {
            String AmonStr, AdayStr, AhourStr, AminStr, AsecStr;

            if (Aday < 10) {
                AdayStr = "0" + String.valueOf(Aday);
            } else {
                AdayStr = String.valueOf(Aday);
            }
            if (Amon < 10) {
                AmonStr = "0" + String.valueOf(Amon);
            } else {
                AmonStr = String.valueOf(Amon);
            }

            if (Ahour < 10) {
                AhourStr = "0" + String.valueOf(Ahour);
            } else {
                AhourStr = String.valueOf(Ahour);
            }

            if (Amin < 10) {
                AminStr = "0" + String.valueOf(Amin);
            } else {
                AminStr = String.valueOf(Amin);
            }

            if (Asec < 10) {
                AsecStr = "0" + String.valueOf(Asec);
            } else {
                AsecStr = String.valueOf(Asec);
            }

            return "20" + Ayear + "-" + AmonStr + "-" + AdayStr + " " + AhourStr + ":" + AminStr + ":" + AsecStr;
        } else {
            return Constants.DEVICE_DEFULT_TIME;
        }


    }

    public String getWarehousing_time() {
        return this.warehousing_time;
    }

    public void setWarehousing_time(String warehousing_time) {
        this.warehousing_time = warehousing_time;
    }

    public int getDataSourceType() {
        return this.dataSourceType;
    }

    public void setDataSourceType(int dataSourceType) {
        this.dataSourceType = dataSourceType;
    }


    public int getRecordPointEncryption() {
        return this.recordPointEncryption;
    }

    public void setRecordPointEncryption(int recordPointEncryption) {
        this.recordPointEncryption = recordPointEncryption;
    }

    public int getRecordPointDataValid1() {
        return this.recordPointDataValid1;
    }

    public void setRecordPointDataValid1(int recordPointDataValid1) {
        this.recordPointDataValid1 = recordPointDataValid1;
    }

    public int getRecordPointDataValid2() {
        return this.recordPointDataValid2;
    }

    public void setRecordPointDataValid2(int recordPointDataValid2) {
        this.recordPointDataValid2 = recordPointDataValid2;
    }


    public int getReportEncryption() {
        return this.reportEncryption;
    }

    public void setReportEncryption(int reportEncryption) {
        this.reportEncryption = reportEncryption;
    }

    public int getReportDataValid1() {
        return this.reportDataValid1;
    }

    public void setReportDataValid1(int reportDataValid1) {
        this.reportDataValid1 = reportDataValid1;
    }

    public int getReportDataValid2() {
        return this.reportDataValid2;
    }

    public void setReportDataValid2(int reportDataValid2) {
        this.reportDataValid2 = reportDataValid2;
    }

    public int getReportDataValid3() {
        return this.reportDataValid3;
    }

    public void setReportDataValid3(int reportDataValid3) {
        this.reportDataValid3 = reportDataValid3;
    }

    public int getReportDataValid4() {
        return this.reportDataValid4;
    }

    public void setReportDataValid4(int reportDataValid4) {
        this.reportDataValid4 = reportDataValid4;
    }

    public String getRecordPointDataId() {
        return this.recordPointDataId;
    }

    public void setRecordPointDataId(String recordPointDataId) {
        this.recordPointDataId = recordPointDataId;
    }

    public long getRecordPointIdTime() {
        return this.recordPointIdTime;
    }

    public void setRecordPointIdTime(long recordPointIdTime) {
        this.recordPointIdTime = recordPointIdTime;
    }

    public int getRecordPointTimeZone() {
        return this.recordPointTimeZone;
    }

    public void setRecordPointTimeZone(int recordPointTimeZone) {
        this.recordPointTimeZone = recordPointTimeZone;
    }

    public int getRecordPointVersion() {
        return this.recordPointVersion;
    }

    public void setRecordPointVersion(int recordPointVersion) {
        this.recordPointVersion = recordPointVersion;
    }

    public int getRecordPointTypeDescription() {
        return this.recordPointTypeDescription;
    }

    public void setRecordPointTypeDescription(int recordPointTypeDescription) {
        this.recordPointTypeDescription = recordPointTypeDescription;
    }

    public int getRecordPointSportType() {
        return this.recordPointSportType;
    }

    public void setRecordPointSportType(int recordPointSportType) {
        this.recordPointSportType = recordPointSportType;
    }

    public int getRecordPointDataType() {
        return this.recordPointDataType;
    }

    public void setRecordPointDataType(int recordPointDataType) {
        this.recordPointDataType = recordPointDataType;
    }

    public String getRecordPointSportData() {
        return this.recordPointSportData;
    }

    public void setRecordPointSportData(String recordPointSportData) {
        this.recordPointSportData = recordPointSportData;
    }

    public long getReportSportStartTime() {
        return this.reportSportStartTime;
    }

    public void setReportSportStartTime(long reportSportStartTime) {
        this.reportSportStartTime = reportSportStartTime;
    }

    public long getReportSportEndTime() {
        return this.reportSportEndTime;
    }

    public void setReportSportEndTime(long reportSportEndTime) {
        this.reportSportEndTime = reportSportEndTime;
    }

    public long getReportDuration() {
        return this.reportDuration;
    }

    public void setReportDuration(long reportDuration) {
        this.reportDuration = reportDuration;
    }

    public long getReportDistance() {
        return this.reportDistance;
    }

    public void setReportDistance(long reportDistance) {
        this.reportDistance = reportDistance;
    }

    public long getReportCal() {
        return this.reportCal;
    }

    public void setReportCal(long reportCal) {
        this.reportCal = reportCal;
    }

    public long getReportFastPace() {
        return this.reportFastPace;
    }

    public void setReportFastPace(long reportFastPace) {
        this.reportFastPace = reportFastPace;
    }

    public long getReportSlowestPace() {
        return this.reportSlowestPace;
    }

    public void setReportSlowestPace(long reportSlowestPace) {
        this.reportSlowestPace = reportSlowestPace;
    }

    public float getReportFastSpeed() {
        return this.reportFastSpeed;
    }

    public void setReportFastSpeed(float reportFastSpeed) {
        this.reportFastSpeed = reportFastSpeed;
    }

    public long getReportTotalStep() {
        return this.reportTotalStep;
    }

    public void setReportTotalStep(long reportTotalStep) {
        this.reportTotalStep = reportTotalStep;
    }

    public int getReportMaxStepSpeed() {
        return this.reportMaxStepSpeed;
    }

    public void setReportMaxStepSpeed(int reportMaxStepSpeed) {
        this.reportMaxStepSpeed = reportMaxStepSpeed;
    }

    public int getReportAvgHeart() {
        return this.reportAvgHeart;
    }

    public void setReportAvgHeart(int reportAvgHeart) {
        this.reportAvgHeart = reportAvgHeart;
    }

    public int getReportMaxHeart() {
        return this.reportMaxHeart;
    }

    public void setReportMaxHeart(int reportMaxHeart) {
        this.reportMaxHeart = reportMaxHeart;
    }

    public int getReportMinHeart() {
        return this.reportMinHeart;
    }

    public void setReportMinHeart(int reportMinHeart) {
        this.reportMinHeart = reportMinHeart;
    }

    public float getReportCumulativeRise() {
        return this.reportCumulativeRise;
    }

    public void setReportCumulativeRise(float reportCumulativeRise) {
        this.reportCumulativeRise = reportCumulativeRise;
    }

    public float getReportCumulativeDecline() {
        return this.reportCumulativeDecline;
    }

    public void setReportCumulativeDecline(float reportCumulativeDecline) {
        this.reportCumulativeDecline = reportCumulativeDecline;
    }

    public float getReportAvgHeight() {
        return this.reportAvgHeight;
    }

    public void setReportAvgHeight(float reportAvgHeight) {
        this.reportAvgHeight = reportAvgHeight;
    }

    public float getReportMaxHeight() {
        return this.reportMaxHeight;
    }

    public void setReportMaxHeight(float reportMaxHeight) {
        this.reportMaxHeight = reportMaxHeight;
    }

    public float getReportMinHeight() {
        return this.reportMinHeight;
    }

    public void setReportMinHeight(float reportMinHeight) {
        this.reportMinHeight = reportMinHeight;
    }

    public float getReportTrainingEffect() {
        return this.reportTrainingEffect;
    }

    public void setReportTrainingEffect(float reportTrainingEffect) {
        this.reportTrainingEffect = reportTrainingEffect;
    }

    public int getReportMaxOxygenIntake() {
        return this.reportMaxOxygenIntake;
    }

    public void setReportMaxOxygenIntake(int reportMaxOxygenIntake) {
        this.reportMaxOxygenIntake = reportMaxOxygenIntake;
    }

    public int getReportEnergyConsumption() {
        return this.reportEnergyConsumption;
    }

    public void setReportEnergyConsumption(int reportEnergyConsumption) {
        this.reportEnergyConsumption = reportEnergyConsumption;
    }

    public long getReportRecoveryTime() {
        return this.reportRecoveryTime;
    }

    public void setReportRecoveryTime(long reportRecoveryTime) {
        this.reportRecoveryTime = reportRecoveryTime;
    }

    public long getReportHeartLimitTime() {
        return this.reportHeartLimitTime;
    }

    public void setReportHeartLimitTime(long reportHeartLimitTime) {
        this.reportHeartLimitTime = reportHeartLimitTime;
    }

    public long getReportHeartAnaerobic() {
        return this.reportHeartAnaerobic;
    }

    public void setReportHeartAnaerobic(long reportHeartAnaerobic) {
        this.reportHeartAnaerobic = reportHeartAnaerobic;
    }

    public long getReportHeartAerobic() {
        return this.reportHeartAerobic;
    }

    public void setReportHeartAerobic(long reportHeartAerobic) {
        this.reportHeartAerobic = reportHeartAerobic;
    }

    public long getReportHeartFatBurning() {
        return this.reportHeartFatBurning;
    }

    public void setReportHeartFatBurning(long reportHeartFatBurning) {
        this.reportHeartFatBurning = reportHeartFatBurning;
    }

    public long getReportHeartWarmUp() {
        return this.reportHeartWarmUp;
    }

    public void setReportHeartWarmUp(long reportHeartWarmUp) {
        this.reportHeartWarmUp = reportHeartWarmUp;
    }

    public long getServiceId() {
        return this.serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    public String getDeviceMac() {
        return this.deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public int getReportGpsEncryption() {
        return reportGpsEncryption;
    }

    public void setReportGpsEncryption(int reportGpsEncryption) {
        this.reportGpsEncryption = reportGpsEncryption;
    }

    public int getReportGpsValid1() {
        return reportGpsValid1;
    }

    public void setReportGpsValid1(int reportGpsValid1) {
        this.reportGpsValid1 = reportGpsValid1;
    }

    public String getRecordGpsTime() {
        return recordGpsTime;
    }

    public void setRecordGpsTime(String recordGpsTime) {
        this.recordGpsTime = recordGpsTime;
    }
}
