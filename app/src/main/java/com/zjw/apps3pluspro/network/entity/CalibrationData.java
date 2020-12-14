package com.zjw.apps3pluspro.network.entity;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户数据，上传后台需要
 */
public class CalibrationData implements Parcelable {


    private String userId;//用户ID
    private String sportTarget;//运动目标
    private String sleepTarget;//睡眠目标
    private String bloodPressureLevel;//血压等级
    private String calibrationHeart;//校准心率
    private String calibrationDiastolic;//校准舒张压
    private String calibrationSystolic;//校准收缩压
    private String wearWay;//佩戴方式

    public CalibrationData() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSportTarget() {
        return sportTarget;
    }

    public void setSportTarget(String sportTarget) {
        this.sportTarget = sportTarget;
    }


    public String getSleepTarget() {
        return sleepTarget;
    }

    public void setSleepTarget(String sleepTarget) {
        this.sleepTarget = sleepTarget;
    }


    public String getBloodPressureLevel() {
        return bloodPressureLevel;
    }

    public void setBloodPressureLevel(String bloodPressureLevel) {
        this.bloodPressureLevel = bloodPressureLevel;
    }

    public String getCalibrationHeart() {
        return calibrationHeart;
    }

    public void setCalibrationHeart(String calibrationHeart) {
        this.calibrationHeart = calibrationHeart;
    }

    public String getCalibrationDiastolic() {
        return calibrationDiastolic;
    }

    public void setCalibrationDiastolic(String calibrationDiastolic) {
        this.calibrationDiastolic = calibrationDiastolic;
    }


    public String getCalibrationSystolic() {
        return calibrationSystolic;
    }

    public void setCalibrationSystolic(String calibrationSystolic) {
        this.calibrationSystolic = calibrationSystolic;
    }


    public String getWearWay() {
        return wearWay;
    }

    public void setWearWay(String wearWay) {
        this.wearWay = wearWay;
    }


    public static Creator<CalibrationData> getCREATOR() {
        return CREATOR;
    }

    public static JSONObject getCalibrationData(CalibrationData myUserData) {
        JSONObject myJSONObject = new JSONObject();

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        String jsonTest = gson.toJson(myUserData, CalibrationData.class);

        try {
            myJSONObject = new JSONObject(jsonTest);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return myJSONObject;


    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(userId);
        dest.writeString(sportTarget);
        dest.writeString(sleepTarget);
        dest.writeString(bloodPressureLevel);
        dest.writeString(calibrationHeart);
        dest.writeString(calibrationDiastolic);
        dest.writeString(calibrationSystolic);
        dest.writeString(wearWay);



    }

    protected CalibrationData(Parcel in) {

        userId = in.readString();
        sportTarget = in.readString();
        sleepTarget = in.readString();
        bloodPressureLevel = in.readString();
        calibrationHeart = in.readString();
        calibrationDiastolic = in.readString();
        calibrationSystolic = in.readString();
        wearWay = in.readString();

    }


    public static final Creator<CalibrationData> CREATOR = new Creator<CalibrationData>() {
        @Override
        public CalibrationData createFromParcel(Parcel in) {
            return new CalibrationData(in);
        }

        @Override
        public CalibrationData[] newArray(int size) {
            return new CalibrationData[size];
        }
    };
}
