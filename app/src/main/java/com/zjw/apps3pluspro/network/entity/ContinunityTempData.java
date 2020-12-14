package com.zjw.apps3pluspro.network.entity;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sql.entity.ContinuityTempInfo;
import com.zjw.apps3pluspro.utils.MyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 连续体温数据，上传后台需要
 */
public class ContinunityTempData {
    public String userId;//用户ID
    public String temperatureData;//数据
    public String temperatureDifference;//温度差值
    public String temperatureDate;//日期
    public String deviceMac;//蓝牙地址
    public String appName;//APP名称
    public String appVersion;//APP版本号
    public String deviceUnixTime;//手环数据上传到App端App时间  传unix时间戳
    public String appUnixTime;//App数据上传到服务端App时间    传unix时间戳

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTemperatureData() {
        return temperatureData;
    }

    public void setTemperatureData(String temperatureData) {
        this.temperatureData = temperatureData;
    }

    public String getTemperatureDifference() {
        return temperatureDifference;
    }

    public void setTemperatureDifference(String temperatureDifference) {
        this.temperatureDifference = temperatureDifference;
    }

    public String getTemperatureDate() {
        return temperatureDate;
    }

    public void setTemperatureDate(String temperatureDate) {
        this.temperatureDate = temperatureDate;
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

    public ContinunityTempData(Context mContext, ContinuityTempInfo mContinuityTempInfo) {
        super();
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        setUserId(mContinuityTempInfo.getUser_id());
        setTemperatureData(mContinuityTempInfo.getData());
        setTemperatureDate(mContinuityTempInfo.getDate());
        setAppVersion(MyUtils.getAppInfo());
        setAppName(MyUtils.getAppName());
        setDeviceMac(mBleDeviceTools.get_ble_mac());

        setAppUnixTime(String.valueOf(System.currentTimeMillis()));
        setDeviceUnixTime(mContinuityTempInfo.getWarehousing_time());

    }

    public static JSONArray getContinuityTempDataListData(ArrayList<ContinunityTempData> myListContinunityTempData) {

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用

        JSONArray jsonArray1 = new JSONArray();

        for (int i = 0; i < myListContinunityTempData.size(); i++) {
            jsonArray1.put(getJSONbJECTData(myListContinunityTempData.get(i)));
        }

        return jsonArray1;

    }

    public static JSONObject getJSONbJECTData(ContinunityTempData mContinunityTempData) {
        JSONObject myJSONObject = new JSONObject();

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        String jsonTest = gson.toJson(mContinunityTempData, ContinunityTempData.class);

        try {
            myJSONObject = new JSONObject(jsonTest);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return myJSONObject;

    }


}
