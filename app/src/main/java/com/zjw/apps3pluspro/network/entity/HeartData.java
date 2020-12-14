package com.zjw.apps3pluspro.network.entity;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sql.entity.HeartInfo;
import com.zjw.apps3pluspro.utils.MyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 心率数据，上传后台需要
 */
public class HeartData {
    public String userId;//用户ID
    public String heartRateRawDate;//日期
    public String heartRateRawdata;//数据
    public String heartRateType;//类型
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

    public String getHeartRateRawDate() {
        return heartRateRawDate;
    }

    public void setHeartRateRawDate(String heartRateRawDate) {
        this.heartRateRawDate = heartRateRawDate;
    }

    public String getHeartRateRawdata() {
        return heartRateRawdata;
    }

    public void setHeartRateRawdata(String heartRateRawdata) {
        this.heartRateRawdata = heartRateRawdata;
    }

    public String getHeartRateType() {
        return heartRateType;
    }

    public void setHeartRateType(String heartRateType) {
        this.heartRateType = heartRateType;
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

    public HeartData(Context mContext, HeartInfo mHeartInfo) {
        super();
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        setUserId(mHeartInfo.getUser_id());
        setHeartRateRawDate(mHeartInfo.getDate());
        setHeartRateRawdata(mHeartInfo.getData());
        setHeartRateType(mHeartInfo.getData_type());
        setAppVersion(MyUtils.getAppInfo());
        setAppName(MyUtils.getAppName());
        setDeviceMac(mBleDeviceTools.get_ble_mac());

        setAppUnixTime(String.valueOf(System.currentTimeMillis()));
        setDeviceUnixTime(mHeartInfo.getWarehousing_time());
    }


    public static JSONArray getDataListData(ArrayList<HeartData> myLisHeartData) {

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用

        JSONArray jsonArray1 = new JSONArray();

        for (int i = 0; i < myLisHeartData.size(); i++) {
            jsonArray1.put(getHeartData(myLisHeartData.get(i)));
        }

        return jsonArray1;

    }


    public static JSONObject getHeartData(HeartData myHeartData) {
        JSONObject myJSONObject = new JSONObject();

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        String jsonTest = gson.toJson(myHeartData, HeartData.class);

        try {
            myJSONObject = new JSONObject(jsonTest);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return myJSONObject;

    }


}
