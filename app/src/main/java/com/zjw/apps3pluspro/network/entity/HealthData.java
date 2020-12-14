package com.zjw.apps3pluspro.network.entity;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sql.entity.HealthInfo;
import com.zjw.apps3pluspro.utils.MyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 健康数据，上传后台需要
 */
public class HealthData {

    private String userId;//用户ID
    private String healthMeasuringTime;//日期
    private String heart;//心率
    private String diastolic;//健康-舒张压
    private String systolic;//健康-收缩压
    private String hrvResult;//健康-心电图报告

//    private String healthEcgData;//健康-心电图数据
//    private String healthPpgData;//健康-心电图数据

    private String healthIndex;//指数-健康指数
    private String fatigueIndex;//指数-疲劳指数
    private String bodyLoad;//指数-身心负荷
    private String bodyQuality;//指数-身体素质
    private String cardiacFunction;//指数-心脏功能

    private String deviceSensorType;//设备传感器
    private String bpStatus;//是否支持血压

    private String appName;//APP名字
    private String appVersion;//APP版本号
    private String deviceMac;//设备MAC地址

    public String deviceUnixTime;//手环数据上传到App端App时间  传unix时间戳
    public String appUnixTime;//App数据上传到服务端App时间    传unix时间戳

    private String ecgList;//
    private String ppgList;//


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHealthMeasuringTime() {
        return healthMeasuringTime;
    }

    public void setHealthMeasuringTime(String healthMeasuringTime) {
        this.healthMeasuringTime = healthMeasuringTime;
    }

    public String getHeart() {
        return heart;
    }

    public void setHeart(String heart) {
        this.heart = heart;
    }

    public String getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(String diastolic) {
        this.diastolic = diastolic;
    }

    public String getSystolic() {
        return systolic;
    }

    public void setSystolic(String systolic) {
        this.systolic = systolic;
    }

    public String getHrvResult() {
        return hrvResult;
    }

    public void setHrvResult(String hrvResult) {
        this.hrvResult = hrvResult;
    }

    public String getHealthIndex() {
        return healthIndex;
    }

    public void setHealthIndex(String healthIndex) {
        this.healthIndex = healthIndex;
    }

    public String getFatigueIndex() {
        return fatigueIndex;
    }

    public void setFatigueIndex(String fatigueIndex) {
        this.fatigueIndex = fatigueIndex;
    }

    public String getBodyLoad() {
        return bodyLoad;
    }

    public void setBodyLoad(String bodyLoad) {
        this.bodyLoad = bodyLoad;
    }

    public String getBodyQuality() {
        return bodyQuality;
    }

    public void setBodyQuality(String bodyQuality) {
        this.bodyQuality = bodyQuality;
    }

    public String getCardiacFunction() {
        return cardiacFunction;
    }

    public void setCardiacFunction(String cardiacFunction) {
        this.cardiacFunction = cardiacFunction;
    }

    public String getDeviceSensorType() {
        return deviceSensorType;
    }

    public void setDeviceSensorType(String deviceSensorType) {
        this.deviceSensorType = deviceSensorType;
    }

    public String getBpStatus() {
        return bpStatus;
    }

    public void setBpStatus(String bpStatus) {
        this.bpStatus = bpStatus;
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

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
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

    public String getEcgList() {
        return ecgList;
    }

    public void setEcgList(String ecgList) {
        this.ecgList = ecgList;
    }

    public String getPpgList() {
        return ppgList;
    }

    public void setPpgList(String ppgList) {
        this.ppgList = ppgList;
    }



    /**
     * 把对象转换成后台要的数据
     *
     * @param myListHealthDataData
     * @return
     */
    public static JSONArray getHealthDataListData(ArrayList<HealthData> myListHealthDataData) {

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用

        JSONArray jsonArray1 = new JSONArray();

        for (int i = 0; i < myListHealthDataData.size(); i++) {

            JSONObject jsonObject1 = getHealthData(myListHealthDataData.get(i));

            try {
                jsonObject1.put("ecgList", getEcgDataJSONArray(myListHealthDataData.get(i).getEcgList()));
                jsonObject1.put("ppgList", getPpgDataJSONArray(myListHealthDataData.get(i).getPpgList()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonArray1.put(jsonObject1);
        }

        return jsonArray1;


    }

    /**
     * 把对象转换成后台要的数据
     *
     * @param myHealthData
     * @return
     */
    public static JSONObject getHealthData(HealthData myHealthData) {
        JSONObject myJSONObject = new JSONObject();

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        String jsonTest = gson.toJson(myHealthData, HealthData.class);

        try {
            myJSONObject = new JSONObject(jsonTest);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return myJSONObject;

    }

    public HealthData() {
        super();
    }

    /**
     * 构造方法
     *
     * @param mContext
     * @param mHealthInfo
     */
    public HealthData(Context mContext, HealthInfo mHealthInfo) {
        super();

        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        setUserId(BaseApplication.getUserId());
        setHealthMeasuringTime(mHealthInfo.getMeasure_time());
        setHeart(mHealthInfo.getHealth_heart());
        setSystolic(mHealthInfo.getHealth_systolic());
        setDiastolic(mHealthInfo.getHealth_diastolic());
        setHrvResult(mHealthInfo.getHealth_ecg_report());
        setHealthIndex(mHealthInfo.getIndex_health_index());
        setFatigueIndex(mHealthInfo.getIndex_fatigue_index());
        setBodyLoad(mHealthInfo.getIndex_body_load());
        setBodyQuality(mHealthInfo.getIndex_body_quality());
        setCardiacFunction(mHealthInfo.getIndex_cardiac_function());
        setEcgList(mHealthInfo.getEcg_data());
        setPpgList(mHealthInfo.getPpg_data());

        setDeviceSensorType(mHealthInfo.getSensor_type());
        setBpStatus(mHealthInfo.getIs_suppor_bp());

        setAppVersion(MyUtils.getAppInfo());
        setAppName(MyUtils.getAppName());
        setDeviceMac(mBleDeviceTools.get_ble_mac());

        setAppUnixTime(String.valueOf(System.currentTimeMillis()));
        setDeviceUnixTime(mHealthInfo.getWarehousing_time());

    }


    /**
     * 获取ECG数据
     *
     * @param ecg_data
     * @return
     */
    public static JSONArray getEcgDataJSONArray(String ecg_data) {
        JSONArray ecg_arr = new JSONArray();
        JSONObject ecg_object = new JSONObject();
        try {
            ecg_object.put("ecg", ecg_data);
//            ecg_object.put("ecg", "0");
            ecg_object.put("groupNum", "1");
            ecg_arr.put(ecg_object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ecg_arr;

    }

    /**
     * 获取PPG数组
     *
     * @param ppg_data
     * @return
     */
    public static JSONArray getPpgDataJSONArray(String ppg_data) {
        JSONArray ecg_arr = new JSONArray();
        JSONObject ecg_object = new JSONObject();
        try {
            ecg_object.put("ppg", ppg_data);
//            ecg_object.put("ppg", "0");
            ecg_object.put("groupNum", "1");
            ecg_arr.put(ecg_object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ecg_arr;

    }


}
