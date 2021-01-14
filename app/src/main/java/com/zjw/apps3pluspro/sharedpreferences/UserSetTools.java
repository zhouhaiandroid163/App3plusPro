package com.zjw.apps3pluspro.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.log.MyLog;


/**
 * 用户相关的轻量级存储工具类
 */

public class UserSetTools {

    private Context cx;
    private static String BleDeviceXml = "user_set_share";

    public UserSetTools(Context context) {
        this.cx = context;
    }

    public SharedPreferences getSharedPreferencesCommon() {
        SharedPreferences settin = cx.getSharedPreferences(BleDeviceXml, 0);
        return settin;
    }

    //设置公制英制， true = 公制， false = 英制
    public void set_user_unit_type(boolean user_unit_type) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("user_unit_type", user_unit_type);
        editor.commit();
    }

    public boolean get_user_unit_type() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("user_unit_type", false);
    }


    /**
     * 血压等级
     *
     * @param blood_grade
     */
    public void set_blood_grade(int blood_grade) {
        MyLog.i("UserBean", "请求回调-blood_grade = " + blood_grade);
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("blood_grade", blood_grade);
        editor.commit();
    }

    public int get_blood_grade() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("blood_grade", DefaultVale.USER_BP_LEVEL);
    }

    //校准-心率
    public void set_calibration_heart(int calibration_heart) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("calibration_heart", calibration_heart);
        editor.commit();
    }

    public int get_calibration_heart() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("calibration_heart", DefaultVale.USER_HEART);
    }


    //校准-高压-收缩压
    public void set_calibration_systolic(int calibration_par1) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("calibration_par1", calibration_par1);
        editor.commit();
    }

    public int get_calibration_systolic() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("calibration_par1", DefaultVale.USER_SYSTOLIC);
    }

    //校准-低压-舒张压
    public void set_calibration_diastolic(int calibration_par2) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("calibration_par2", calibration_par2);
        editor.commit();
    }

    public int get_calibration_diastolic() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("calibration_par2", DefaultVale.USER_DIASTOLIC);
    }


    //手机号
    public void set_user_phone(String user_phone) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("user_phone", user_phone);
        editor.commit();
    }

    //手机号
    public String get_user_phone() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("user_phone", "");
    }

    //邮箱
    public void set_user_email(String user_email) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("user_email", user_email);
        editor.commit();
    }

    public String get_user_email() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("user_email", "");
    }


    //心电图提示
    public void set_user_ecg_prompt(boolean user_ecg_promp) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("user_ecg_promp", user_ecg_promp);
        editor.commit();
    }

    public boolean get_user_ecg_promp() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("user_ecg_promp", true);
    }

    //是否校准过 0=未校准过，1=校准过
    public void set_is_par(int is_par) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("is_par", is_par);
        editor.commit();
    }

    public int get_is_par() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("is_par", 0);
    }


    //用户登录状态
    public void set_user_login(boolean user_login) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("user_login", user_login);
        editor.commit();
    }

    public boolean get_user_login() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("user_login", false);
    }


    /**
     * 用户的佩戴方式
     * 1代表 左手
     * 0代表 右手
     *
     * @param wear_way
     */
    public void set_user_wear_way(int wear_way) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("wear_way", wear_way);
        editor.commit();
    }

    public int get_user_wear_way() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("wear_way", DefaultVale.USER_WEARWAY);
    }


    //运动目标
    public void set_user_exercise_target(String user_exercise_target) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("user_exercise_target", user_exercise_target);
        editor.commit();
    }

    public String get_user_exercise_target() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("user_exercise_target", String.valueOf(DefaultVale.USER_SPORT_TARGET));
    }


    //睡眠目标
    public void set_user_sleep_target(String user_sleep_target) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("user_sleep_target", user_sleep_target);
        editor.commit();
    }

    public String get_user_sleep_target() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("user_sleep_target", String.valueOf(DefaultVale.USER_SLEEP_TARGET));
    }




    //最近一次步数步数，用来计算健康值
    public void set_user_stpe(int user_stpe) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("user_stpe", user_stpe);
        editor.commit();
    }

    public int get_user_stpe() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("user_stpe", 0);
    }

    //健康值
    public void set_health_index(int health_index) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("health_index", health_index);
        editor.commit();
    }

    public int get_health_index() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("health_index", 0);
    }

    //疲劳值
    public void set_fatigu_index(int fatigu_index) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("fatigu_index", fatigu_index);
        editor.commit();
    }

    public int get_fatigu_index() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("fatigu_index", 0);
    }

    //身心负荷
    public void set_load_index(int load_index) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("load_index", load_index);
        editor.commit();
    }

    public int get_load_index() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("load_index", 0);
    }

    //身体素质
    public void set_body_index(int body_index) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("body_index", body_index);
        editor.commit();
    }

    public int get_body_index() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("body_index", 0);
    }


    //心脏功能
    public void set_heart_index(int heart_index) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("heart_index", heart_index);
        editor.commit();
    }

    public int get_heart_index() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("heart_index", 0);
    }


    //体重转换产生的误差
    public void set_weight_disparity(int weight_disparity) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("weight_disparity", weight_disparity);
        editor.commit();
    }

    public int get_user_weight_disparity() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("weight_disparity", 0);
    }

    //是否是谷歌地图 ture=谷歌，false=高德
    public void set_is_google_map(boolean is_google_map) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("is_google_map", is_google_map);
        editor.commit();
    }

    public boolean get_is_google_map() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("is_google_map", true);
    }

    //用户是否设置过
    public void set_map_enable(boolean map_enable) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("map_enable", map_enable);
        editor.commit();
    }

    public boolean get_map_enable() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("map_enable", false);
    }

    //用户是否设置过
    public void set_map_city(String map_city) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("map_city", map_city);
        editor.commit();
    }

    public String get_map_city() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("map_city", "");
    }


    //==============生理周期==============


    //是否是第一次使用，是=需要弹出设置，否=需要直接进入页面，如果切换男女，需要账变成false
    //true = 是第一次 false = 不是第一次。
    public void set_device_is_one_cycle(boolean device_is_one_cycle) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_is_one_cycle", device_is_one_cycle);

        editor.commit();
    }

    public boolean get_device_is_one_cycle() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_is_one_cycle", true);
    }


    //生理周期-默认28天
    public void set_nv_cycle(int nv_cycle) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("nv_cycle", nv_cycle);
        editor.commit();
    }

    public int get_nv_cycle() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("nv_cycle", 28);
    }

    //生理周期-天数5天
    public void set_nv_lenght(int nv_lenght) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("nv_lenght", nv_lenght);
        editor.commit();
    }

    public int get_nv_lenght() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("nv_lenght", 5);
    }

    //生理周期-默认为空
    public void set_nv_start_date(String nv_start_date) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("nv_start_date", nv_start_date);
        editor.commit();
    }

    public String get_nv_start_date() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("nv_start_date", "");
    }


    //设置设备生理周期提醒开关
    public void set_nv_device_switch(boolean nv_device_switch) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("nv_device_switch", nv_device_switch);
        editor.commit();
    }

    public boolean get_nv_device_switch() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("nv_device_switch", false);
    }


    //账号
    public void set_user_account(String user_account) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("user_account", user_account);
        editor.commit();
    }

    public String get_user_account() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("user_account", "");
    }

    //密码
    public void set_user_password(String user_password) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("user_password", user_password);
        editor.commit();
    }

    public String get_user_password() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("user_password", "");
    }

    //登录类型
    public void set_user_login_type(String user_login_type) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("user_login_type", user_login_type);
        editor.commit();
    }

    public String get_user_login_type() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("user_login_type", "");
    }


    //用户Uid
    public void set_user_id(String user_id) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("user_id", user_id);
        editor.commit();
    }

    public String get_user_id() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("user_id", "");
    }


    //注册时间
    public void set_user_register_time(String user_register_time) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("user_register_time", user_register_time);
        editor.commit();
    }

    public String get_user_register_time() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("user_register_time", "");
    }

    //昵称
    public void set_user_nickname(String user_nickname) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("user_nickname", user_nickname);
        editor.commit();
    }

    public String get_user_nickname() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("user_nickname", "");
    }

    //身高
    public void set_user_height(int user_height) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("user_height", user_height);
        editor.commit();
    }

    public int get_user_height() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("user_height", DefaultVale.USER_HEIGHT);
    }

    //体重
    public void set_user_weight(int user_weight) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("user_weight", user_weight);
        editor.commit();
    }

    public int get_user_weight() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("user_weight", DefaultVale.USER_WEIGHT);
    }

    //性别 0=男，1=女
    public void set_user_sex(int user_sex) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("user_sex", user_sex);
        editor.commit();
    }

    public int get_user_sex() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("user_sex", DefaultVale.USER_SEX);
    }


    //生日
    public void set_user_birthday(String user_birthday) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("user_birthday", user_birthday);
        editor.commit();
    }

    public String get_user_birthday() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("user_birthday", "");
    }

    //头像URL
    public void set_user_head_url(String user_head_url) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("user_head_url", user_head_url);
        editor.commit();
    }

    public String get_user_head_url() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("user_head_url", "");
    }

    //头像原始数据
    public void set_user_head_bast64(String head_bast64) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("head_bast64", head_bast64);
        editor.commit();
    }

    public String get_uesr_head_bast64() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("head_bast64", "");
    }

    //上传固件信息到后台
    public void set_service_upload_device_info(String service_upload_device_info) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("service_upload_device_info", service_upload_device_info);
        editor.commit();
    }

    public String get_service_upload_device_info() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("service_upload_device_info", "");
    }

    //上传解绑固件信息到后台
    public void set_service_upload_un_device_info(String service_upload_un_device_info) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("service_upload_un_device_info", service_upload_un_device_info);
        editor.commit();
    }

    public String get_service_upload_un_device_info() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("service_upload_un_device_info", "");
    }



    //============版本升级，时间间隔============

    //版本升级，请求时间
    public void set_update_app_request_time(long update_app_request_time) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putLong("update_app_request_time", update_app_request_time);
        editor.commit();
    }

    public long get_update_app_request_time() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getLong("update_app_request_time", 0);
    }

    //是否同意权限
    public void set_is_privacy_protocol(boolean is_privacy_protocol) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("is_privacy_protocol", is_privacy_protocol);
        editor.commit();
    }

    public boolean get_is_privacy_protocol() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("is_privacy_protocol", false);
    }

    //是否同意隐私协议
    public void set_is_jurisdiction_tip(boolean is_jurisdiction_tip) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("is_jurisdiction_tip", is_jurisdiction_tip);
        editor.commit();
    }

    public boolean get_is_jurisdiction_tip() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("is_jurisdiction_tip", false);
    }

    //登录类型
    public void setUserAuthorizationCode(String code) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("setUserAuthorizationCode", code);
        editor.apply();
    }

    public String getUserAuthorizationCode() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("setUserAuthorizationCode", "");
    }

    //游客登录
    public void setTouristUUid(String code) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("setTouristUUid", code);
        editor.apply();
    }

    public String getTouristUUid() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("setTouristUUid", "");
    }

    //当天天气
    public void set_weather_now_data(String weather_now_data) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("weather_now_data", weather_now_data);
        editor.commit();
    }

    public String get_weather_now_data() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("weather_now_data", "");
    }

    //历史天气
    public void set_weather_his_data(String weather_his_data) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("weather_his_data", weather_his_data);
        editor.commit();
    }

    public String get_weather_his_data() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("weather_his_data", "");
    }

}
