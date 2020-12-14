package com.zjw.apps3pluspro.network;


import com.google.gson.Gson;
import com.zjw.apps3pluspro.network.javabean.AccountBean;
import com.zjw.apps3pluspro.network.javabean.AppVersionBean;
import com.zjw.apps3pluspro.network.javabean.CalibrationBean;
import com.zjw.apps3pluspro.network.javabean.ContinuitySpo2ListBean;
import com.zjw.apps3pluspro.network.javabean.ContinuityTempListBean;
import com.zjw.apps3pluspro.network.javabean.CurrencyBean;
import com.zjw.apps3pluspro.network.javabean.DeviceBean;
import com.zjw.apps3pluspro.network.javabean.EcgPpgHealthBean;
import com.zjw.apps3pluspro.network.javabean.FriendListBean;
import com.zjw.apps3pluspro.network.javabean.HealthBean;
import com.zjw.apps3pluspro.network.javabean.HeartBean;
import com.zjw.apps3pluspro.network.javabean.MeasureSpo2ListBean;
import com.zjw.apps3pluspro.network.javabean.MeasureTempListBean;
import com.zjw.apps3pluspro.network.javabean.MusicBean;
import com.zjw.apps3pluspro.network.javabean.NewFriendInfoBean;
import com.zjw.apps3pluspro.network.javabean.OldBean;
import com.zjw.apps3pluspro.network.javabean.SearchFriendInfoBean;
import com.zjw.apps3pluspro.network.javabean.SleepBean;
import com.zjw.apps3pluspro.network.javabean.SportBean;
import com.zjw.apps3pluspro.network.javabean.ThemeBean;
import com.zjw.apps3pluspro.network.javabean.ThemeFileBean;
import com.zjw.apps3pluspro.network.javabean.UserBean;

import org.json.JSONObject;


/**
 * 获取,请求后台的Json数据
 * Created by zjw on 2018/3/7.
 */

public class ResultJson {


    //===================返回Result==================
    //请求成功
    public static final int Result_success = 1;
    //请求失败
    public static final int Result_fail = 0;

    //===================返回Code==================
    //操作成功
    public static final String Code_operation_success = "0000";
    //操作失败
    public static final String Code_operation_fail = "0001";
    //注册失败
    public static final String Code_register_fail = "0002";
    //已注册
    public static final String Code_is_register_yes = "0003";
    //未注册
    public static final String Code_is_register_no = "0004";
    //密码错误
    public static final String Code_password_fail = "0005";
    //验证码下发失败
    public static final String Code_getcode_fail = "0006";
    //验证码获取频繁
    public static final String Code_getcode_frequently = "0007";
    //验证码已过期,缓存不存在，有可能是过期了，也有可能未获取
    public static final String Code_verificationcode_overdue = "0008";
    //验证码错误
    public static final String Code_verificationcode_fail = "0009";
    //请求参数为空！
    public static final String Code_quest_parameter_null = "0010";
    //请求数据有误！
    public static final String Code_quest_parameter_fail = "0011";
    //无数据
    public static final String Code_no_data = "0012";
    // code 失效
    public static final String AUTHORIZATION_CODE_FAILURE = "0020";
    // code 为空
    public static final String AUTHORIZATION_CODE_NULL = "0021";


    //===================默认空的原始数据==================

    public static final String Duflet_sport_data = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";

    public static final String Duflet_poheart_data = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";

    public static final String Duflet_woheart_data = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";

    public static final String Duflet_continuity_spo2_data = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";

    public static final String Duflet_continuity_temp_data = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";

    public static final String Duflet_health_data_id = "-1";

    public static AccountBean AccountBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), AccountBean.class);
    }

    public static UserBean UserBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), UserBean.class);
    }

    public static CalibrationBean CalibrationBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), CalibrationBean.class);
    }

    public static SportBean SportBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), SportBean.class);
    }

    public static SleepBean SleepBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), SleepBean.class);
    }

    public static HeartBean HeartBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), HeartBean.class);
    }

    public static HealthBean HealthBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), HealthBean.class);
    }

    public static EcgPpgHealthBean EcgPpgHealthBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), EcgPpgHealthBean.class);
    }

    public static SearchFriendInfoBean SearchFriendInfoBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), SearchFriendInfoBean.class);
    }

    public static NewFriendInfoBean NewFriendInfoBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), NewFriendInfoBean.class);
    }

    public static AppVersionBean AppVerionBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), AppVersionBean.class);
    }

    public static OldBean OldBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), OldBean.class);
    }

    public static FriendListBean FriendListBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), FriendListBean.class);
    }

    public static ThemeBean ThemeBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), ThemeBean.class);
    }

    public static ThemeFileBean ThemeFileBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), ThemeFileBean.class);
    }

    public static MusicBean MusicBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), MusicBean.class);
    }

    public static CurrencyBean CurrencyBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), CurrencyBean.class);
    }

    public static DeviceBean DeviceBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), DeviceBean.class);
    }

    public static ContinuitySpo2ListBean ContinuitySpo2ListBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), ContinuitySpo2ListBean.class);
    }


    public static ContinuityTempListBean ContinuityTempListBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), ContinuityTempListBean.class);
    }


    public static MeasureSpo2ListBean MeasureSpo2ListBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), MeasureSpo2ListBean.class);
    }

    public static MeasureTempListBean MeasureTempListBean(JSONObject result) {
        return new Gson().fromJson(result.toString(), MeasureTempListBean.class);
    }


}
