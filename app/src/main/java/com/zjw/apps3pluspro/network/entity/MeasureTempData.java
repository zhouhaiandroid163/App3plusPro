package com.zjw.apps3pluspro.network.entity;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sql.entity.MeasureTempInfo;
import com.zjw.apps3pluspro.utils.MyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 测量体温，上传后台需要
 */
public class MeasureTempData {
    public String userId;//用户ID
    public String temperatureMeasureData;//数据
    public String temperatureMeasureTime;//日期
    public String temperatureDifference;//日期
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

    public String getTemperatureMeasureData() {
        return temperatureMeasureData;
    }

    public void setTemperatureMeasureData(String temperatureMeasureData) {
        this.temperatureMeasureData = temperatureMeasureData;
    }

    public String getTemperatureMeasureTime() {
        return temperatureMeasureTime;
    }

    public void setTemperatureMeasureTime(String temperatureMeasureTime) {
        this.temperatureMeasureTime = temperatureMeasureTime;
    }

    public String getTemperatureDifference() {
        return temperatureDifference;
    }

    public void setTemperatureDifference(String temperatureDifference) {
        this.temperatureDifference = temperatureDifference;
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
     * @param mMeasureTempInfo
     */
    public MeasureTempData(Context mContext, MeasureTempInfo mMeasureTempInfo) {
        super();

        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();


        setUserId(mMeasureTempInfo.getUser_id());
        setTemperatureMeasureData(mMeasureTempInfo.getMeasure_wrist_temp());
        setTemperatureMeasureTime(mMeasureTempInfo.getMeasure_time());
        setTemperatureDifference(mMeasureTempInfo.getMeasure_temp_difference());
        setAppVersion(MyUtils.getAppInfo());
        setAppName(MyUtils.getAppName());
        setDeviceMac(mBleDeviceTools.get_ble_mac());

        setAppUnixTime(String.valueOf(System.currentTimeMillis()));
        setDeviceUnixTime(mMeasureTempInfo.getWarehousing_time());

    }

    public static JSONArray getContinuitySpo2DataListData(ArrayList<MeasureTempData> myListContinuitySpo2Data) {

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用

        JSONArray jsonArray1 = new JSONArray();

        for (int i = 0; i < myListContinuitySpo2Data.size(); i++) {
            jsonArray1.put(getJSONbJECTData(myListContinuitySpo2Data.get(i)));
        }

        return jsonArray1;

    }

    public static JSONObject getJSONbJECTData(MeasureTempData mContinunitySpo2Data) {
        JSONObject myJSONObject = new JSONObject();

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        String jsonTest = gson.toJson(mContinunitySpo2Data, MeasureTempData.class);

        try {
            myJSONObject = new JSONObject(jsonTest);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return myJSONObject;

    }


}
