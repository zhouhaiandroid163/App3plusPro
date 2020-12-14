package com.zjw.apps3pluspro.network.entity;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.entity.MovementInfo;
import com.zjw.apps3pluspro.utils.MyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 多运动数据，上传后台需要
 */
public class SportData {
    public String userId;//用户ID
    public String sportDate;//日期
    public String step;//步数
    public String calorie;//卡路里
    public String distance;//距离
    public String todayTargetStep;//运动目标
    public String sportRawdata;//原始数据
    public String deviceMac;//MAC地址
    public String appName;//APP名字
    public String appVersion;//APP版本
    public String deviceUnixTime;//手环数据上传到App端App时间  传unix时间戳
    public String appUnixTime;//App数据上传到服务端App时间    传unix时间戳

    public String height;//身高
    public String weight;//体重
    public int firmwareMethod;//计算算法
    public String deviceVersion;//设备版本号

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSportDate() {
        return sportDate;
    }

    public void setSportDate(String sportDate) {
        this.sportDate = sportDate;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTodayTargetStep() {
        return todayTargetStep;
    }

    public void setTodayTargetStep(String todayTargetStep) {
        this.todayTargetStep = todayTargetStep;
    }

    public String getSportRawdata() {
        return sportRawdata;
    }

    public void setSportRawdata(String sportRawdata) {
        this.sportRawdata = sportRawdata;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDeviceUnixTime() {
        return deviceUnixTime;
    }

    public void setDeviceUnixTime(String deviceUnixTime) {
        this.deviceUnixTime = deviceUnixTime;
    }

    public String getAppUnixTime() {
        return appUnixTime;
    }

    public void setAppUnixTime(String appUnixTime) {
        this.appUnixTime = appUnixTime;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getFirmwareMethod() {
        return firmwareMethod;
    }

    public void setFirmwareMethod(int firmwareMethod) {
        this.firmwareMethod = firmwareMethod;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public SportData() {
        super();
    }


    public SportData(Context mContext, MovementInfo mMovementInfo) {
        super();

        UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        setUserId(mMovementInfo.getUser_id());
        setSportDate(mMovementInfo.getDate());
        setStep(mMovementInfo.getTotal_step());
        setCalorie(mMovementInfo.getCalorie());
        setDistance(mMovementInfo.getDisance());
        setSportRawdata(mMovementInfo.getData());
        setAppVersion(MyUtils.getAppInfo());
        setAppName(MyUtils.getAppName());
        setDeviceMac(mBleDeviceTools.get_ble_mac());
        setTodayTargetStep(mUserSetTools.get_user_exercise_target());

        setAppUnixTime(String.valueOf(System.currentTimeMillis()));
        setDeviceUnixTime(mMovementInfo.getWarehousing_time());

        setHeight(mMovementInfo.getHeight());
        setWeight(mMovementInfo.getWeight());
        if (mMovementInfo.getStep_algorithm_type() != null && !mMovementInfo.getStep_algorithm_type().equals("")) {
            setFirmwareMethod(Integer.valueOf(mMovementInfo.getStep_algorithm_type()));
        } else {
            setFirmwareMethod(1);
        }

        setDeviceVersion(String.valueOf(mBleDeviceTools.get_ble_device_type()));

    }

    public static JSONArray getSportDataListData(ArrayList<SportData> myListSportDataData) {

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用

        JSONArray jsonArray1 = new JSONArray();

        for (int i = 0; i < myListSportDataData.size(); i++) {
            jsonArray1.put(getSportData(myListSportDataData.get(i)));
        }

        return jsonArray1;

    }

    public static JSONObject getSportData(SportData mySportData) {
        JSONObject myJSONObject = new JSONObject();

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        String jsonTest = gson.toJson(mySportData, SportData.class);

        try {
            myJSONObject = new JSONObject(jsonTest);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return myJSONObject;

    }

}
