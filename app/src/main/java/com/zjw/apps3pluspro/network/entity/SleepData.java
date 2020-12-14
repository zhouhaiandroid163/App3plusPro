package com.zjw.apps3pluspro.network.entity;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.entity.SleepInfo;
import com.zjw.apps3pluspro.utils.MyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 睡眠数据，上传后台需要
 */
public class SleepData {
    public String userId;//用户ID
    public String sleepDate;//日期
    public String sleepRawdata;//睡眠原始数据

    public String sleepTime;//睡眠时间
    public String sleepQaCode;//睡眠质量
    public String sleepBeginTime;//入睡时间
    public String sleepEndTime;//醒来时间

    public String sleepTarget;//睡眠目标
    public String deviceMac;//MAC地址
    public String appName;//APP名字
    public String appVersion;//APP版本

    public String deviceUnixTime;//手环数据上传到App端App时间  传unix时间戳
    public String appUnixTime;//App数据上传到服务端App时间    传unix时间戳

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSleepDate() {
        return sleepDate;
    }

    public void setSleepDate(String sleepDate) {
        this.sleepDate = sleepDate;
    }

    public String getSleepRawdata() {
        return sleepRawdata;
    }

    public void setSleepRawdata(String sleepRawdata) {
        this.sleepRawdata = sleepRawdata;
    }

    public String getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String getSleepQaCode() {
        return sleepQaCode;
    }

    public void setSleepQaCode(String sleepQaCode) {
        this.sleepQaCode = sleepQaCode;
    }

    public String getSleepBeginTime() {
        return sleepBeginTime;
    }

    public void setSleepBeginTime(String sleepBeginTime) {
        this.sleepBeginTime = sleepBeginTime;
    }

    public String getSleepEndTime() {
        return sleepEndTime;
    }

    public void setSleepEndTime(String sleepEndTime) {
        this.sleepEndTime = sleepEndTime;
    }

    public String getSleepTarget() {
        return sleepTarget;
    }

    public void setSleepTarget(String sleepTarget) {
        this.sleepTarget = sleepTarget;
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

    public SleepData() {
        super();
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

    public SleepData(Context mContext, SleepInfo mSleepInfo) {
        super();
        UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        setUserId(mSleepInfo.getUser_id());
        setSleepDate(mSleepInfo.getDate());
        setSleepRawdata(mSleepInfo.getData());
        setSleepTime("");
        setSleepQaCode("");
        setSleepBeginTime("");
        setSleepEndTime("");
        setSleepTarget(mUserSetTools.get_user_sleep_target());
        setAppVersion(MyUtils.getAppInfo());
        setAppName(MyUtils.getAppName());
        setDeviceMac(mBleDeviceTools.get_ble_mac());

        setAppUnixTime(String.valueOf(System.currentTimeMillis()));
        setDeviceUnixTime(mSleepInfo.getWarehousing_time());
    }


    public static JSONArray getSleepDataListData(ArrayList<SleepData> myListSleepData) {

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用

        JSONArray jsonArray1 = new JSONArray();

        for (int i = 0; i < myListSleepData.size(); i++) {
            jsonArray1.put(getSleepData(myListSleepData.get(i)));
        }

        return jsonArray1;

    }

    public static JSONObject getSleepData(SleepData mySleepData) {
        JSONObject myJSONObject = new JSONObject();

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        String jsonTest = gson.toJson(mySleepData, SleepData.class);

        try {
            myJSONObject = new JSONObject(jsonTest);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return myJSONObject;

    }

}
