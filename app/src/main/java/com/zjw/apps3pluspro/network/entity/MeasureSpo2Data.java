package com.zjw.apps3pluspro.network.entity;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sql.entity.MeasureSpo2Info;
import com.zjw.apps3pluspro.utils.MyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 测量血氧数据，上传后台需要
 */
public class MeasureSpo2Data {
    public String userId;//用户ID
    public String spoMeasureData;//数据
    public String spoMeasureTime;//日期
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

    public String getSpoMeasureData() {
        return spoMeasureData;
    }

    public void setSpoMeasureData(String spoMeasureData) {
        this.spoMeasureData = spoMeasureData;
    }

    public String getSpoMeasureTime() {
        return spoMeasureTime;
    }

    public void setSpoMeasureTime(String spoMeasureTime) {
        this.spoMeasureTime = spoMeasureTime;
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

    /**
     * 构造方法
     *
     * @param mContext
     * @param mMeasureSpo2Info
     */
    public MeasureSpo2Data(Context mContext, MeasureSpo2Info mMeasureSpo2Info) {
        super();

        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

        setUserId(mMeasureSpo2Info.getUser_id());
        setSpoMeasureData(mMeasureSpo2Info.getMeasure_spo2());
        setSpoMeasureTime(mMeasureSpo2Info.getMeasure_time());
        setAppVersion(MyUtils.getAppInfo());
        setAppName(MyUtils.getAppName());
        setDeviceMac(mBleDeviceTools.get_ble_mac());

        setAppUnixTime(String.valueOf(System.currentTimeMillis()));
        setDeviceUnixTime(mMeasureSpo2Info.getWarehousing_time());

    }



    public static JSONArray getMeasureSpo2DataListData(ArrayList<MeasureSpo2Data> myListMeasureSpo2Data) {

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用

        JSONArray jsonArray1 = new JSONArray();

        for (int i = 0; i < myListMeasureSpo2Data.size(); i++) {
            jsonArray1.put(getJSONbJECTData(myListMeasureSpo2Data.get(i)));
        }

        return jsonArray1;

    }

    public static JSONObject getJSONbJECTData(MeasureSpo2Data mMeasureSpo2Data) {
        JSONObject myJSONObject = new JSONObject();

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        String jsonTest = gson.toJson(mMeasureSpo2Data, MeasureSpo2Data.class);

        try {
            myJSONObject = new JSONObject(jsonTest);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return myJSONObject;

    }



}
