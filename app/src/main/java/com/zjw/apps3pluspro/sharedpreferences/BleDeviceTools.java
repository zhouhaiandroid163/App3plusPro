package com.zjw.apps3pluspro.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.zjw.apps3pluspro.utils.DefaultVale;

/**
 * 设备相关的轻量级存储工具类
 */
public class BleDeviceTools {

    private Context cx;
    private static String BleDeviceXml = "ble_device_share";

    public BleDeviceTools(Context context) {
        this.cx = context;
    }

    public SharedPreferences getSharedPreferencesCommon() {
        SharedPreferences settin = cx.getSharedPreferences(BleDeviceXml, 0);
        return settin;
    }

    //保存连上的蓝牙名
    public void set_ble_name(String ble_name) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("ble_name", ble_name);
        editor.commit();
    }

    public String get_ble_name() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("ble_name", "");
    }


    //保存连上的MAC
    public void set_ble_mac(String ble_mac) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("ble_mac", ble_mac);
        editor.commit();
    }

    public String get_ble_mac() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("ble_mac", "");
    }

    /**
     * @param IsSupportBigPage
     */
    public void setIsSupportBigPage(boolean IsSupportBigPage) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsSupportBigPage", IsSupportBigPage);
        editor.commit();
    }

    public boolean getIsSupportBigPage() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsSupportBigPage", false);
    }


    //保存的通话蓝牙名
    public void set_call_ble_name(String call_ble_name) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("call_ble_name", call_ble_name);
        editor.commit();
    }

    public String get_call_ble_name() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("call_ble_name", "");
    }


    //保存的通话MAC
    public void set_call_ble_mac(String call_ble_mac) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("call_ble_mac", call_ble_mac);
        editor.commit();
    }

    public String get_call_ble_mac() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("call_ble_mac", "");
    }

    //保存已注册微信
    public String get_wx_mac_address() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("wx_mac_address", "");
    }

    public void set_wx_mac_address(String wx_mac_address) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("wx_mac_address", wx_mac_address);
        editor.commit();
    }

    //保存设备类型
    public int get_ble_device_type() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("ble_device_type", 1);
    }

    public void set_ble_device_type(int ble_device_type) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("ble_device_type", ble_device_type);
        editor.commit();
    }

    //设备版本号
    public int get_ble_device_version() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("ble_device_version", 1);
    }

    public void set_ble_device_version(int ble_device_version) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("ble_device_version", ble_device_version);
        editor.commit();
    }

    //设备版本号
    public int get_ble_device_sub_version() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("ble_device_sub_version", 0);
    }

    public void set_ble_device_sub_version(int ble_device_sub_version) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("ble_device_sub_version", ble_device_sub_version);
        editor.commit();
    }

    //设备电量
    public int get_ble_device_power() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("ble_device_power", 0);
    }

    //设备电量
    public void set_ble_device_power(int ble_device_power) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("ble_device_power", ble_device_power);
        editor.commit();
    }

    //来电提醒开关
    public void set_reminde_call(boolean reminde_call) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_call", reminde_call);
        editor.commit();
    }

    public boolean get_reminde_call() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_call", false);
    }

    //短信提醒开关
    public void set_reminde_mms(boolean reminde_mms) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_mms", reminde_mms);
        editor.commit();
    }

    public boolean get_reminde_mms() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_mms", false);
    }

    //QQ开关
    public void set_reminde_qq(boolean reminde_qq) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_qq", reminde_qq);
        editor.commit();
    }

    public boolean get_reminde_qq() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_qq", false);
    }

    //微信开关
    public void set_reminde_wx(boolean reminde_wx) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_wx", reminde_wx);
        editor.commit();
    }

    public boolean get_reminde_wx() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_wx", false);
    }


    //skype 开关
    public void set_reminde_skype(boolean reminde_skype) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_skype", reminde_skype);
        editor.commit();
    }

    public boolean get_reminde_skype() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_skype", false);
    }

    //facebook 开关
    public void set_facebook(boolean reminde_facebook) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_facebook", reminde_facebook);
        editor.commit();
    }

    public boolean get_reminde_facebook() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_facebook", false);
    }

    //whatsapp 开关
    public void set_reminde_whatsapp(boolean reminde_whatsapp) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_whatsapp", reminde_whatsapp);
        editor.commit();
    }

    public boolean get_reminde_whatsapp() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_whatsapp", false);
    }

    //领英 开关
    public void set_reminde_linkedin(boolean reminde_linkedin) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_linkedin", reminde_linkedin);
        editor.commit();
    }

    public boolean get_reminde_linkedin() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_linkedin", false);
    }

    //推特 开关
    public void set_reminde_twitter(boolean reminde_twitter) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_twitter", reminde_twitter);
        editor.commit();
    }

    public boolean get_reminde_twitter() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_twitter", false);
    }


    //Viber 开关
    public void set_reminde_viber(boolean reminde_viber) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_viber", reminde_viber);
        editor.commit();
    }

    public boolean get_reminde_viber() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_viber", false);
    }

    //Line开关
    public void set_reminde_line(boolean reminde_line) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_line", reminde_line);
        editor.commit();
    }

    public boolean get_reminde_line() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_line", false);
    }

    //Gmail开关
    public void set_reminde_gmail(boolean reminde_gmail) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_gmail", reminde_gmail);

        editor.commit();
    }

    public boolean get_reminde_gmail() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_gmail", false);
    }

    //OutLook开关
    public void set_reminde_outlook(boolean reminde_outlook) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_outlook", reminde_outlook);

        editor.commit();
    }

    public boolean get_reminde_outlook() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_outlook", false);
    }

    //instagrem开关
    public void set_reminde_instagram(boolean reminde_instagram) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_instagram", reminde_instagram);

        editor.commit();
    }

    public boolean get_reminde_instagram() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_instagram", false);
    }

    //snapchat开关
    public void set_reminde_snapchat(boolean reminde_snapchat) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_snapchat", reminde_snapchat);

        editor.commit();
    }

    public boolean get_reminde_snapchat() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_snapchat", false);
    }

    //iosmail开关
    public void set_reminde_iosmail(boolean reminde_iosmail) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_iosmail", reminde_iosmail);

        editor.commit();
    }

    public boolean get_reminde_iosmail() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_iosmail", false);
    }

    //zalo开关
    public void set_reminde_zalo(boolean reminde_zalo) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_zalo", reminde_zalo);

        editor.commit();
    }

    public boolean get_reminde_zalo() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_zalo", false);
    }

    //telegram开关
    public void set_reminde_telegram(boolean reminde_telegram) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_telegram", reminde_telegram);

        editor.commit();
    }

    public boolean get_reminde_telegram() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_telegram", false);
    }

    //youtube开关
    public void set_reminde_youtube(boolean reminde_youtube) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_youtube", reminde_youtube);

        editor.commit();
    }

    public boolean get_reminde_youtube() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_youtube", false);
    }

    //kakao_talk开关
    public void set_reminde_kakao_talk(boolean reminde_kakao_talk) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_kakao_talk", reminde_kakao_talk);

        editor.commit();
    }

    public boolean get_reminde_kakao_talk() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_kakao_talk", false);
    }

    //vk开关
    public void set_reminde_vk(boolean reminde_vk) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_vk", reminde_vk);

        editor.commit();
    }

    public boolean get_reminde_vk() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_vk", false);
    }

    //ok开关
    public void set_reminde_ok(boolean reminde_ok) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_ok", reminde_ok);

        editor.commit();
    }

    public boolean get_reminde_ok() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_ok", false);
    }

    //icq开关
    public void set_reminde_icq(boolean reminde_icq) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("reminde_icq", reminde_icq);

        editor.commit();
    }

    public boolean get_reminde_icq() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("reminde_icq", false);
    }

    // 抬腕亮屏开关
    public void set_taiwan(boolean taiwan) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("taiwan", taiwan);
        editor.commit();
    }

    public boolean get_taiwan() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("taiwan", true);
    }


    // 整点测量心率开关
    public boolean get_point_measurement_heart() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("point_measurement_heart", false);
    }

    public void set_point_measurement_heart(boolean point_measurement_heart) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("point_measurement_heart", point_measurement_heart);
        editor.commit();
    }

    // 连续心率开关
    public boolean get_persist_measurement_heart() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("persist_measurement_heart", false);
    }

    public void set_persist_measurement_heart(boolean persist_measurement_heart) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("persist_measurement_heart", persist_measurement_heart);
        editor.commit();
    }

    // 勿扰模式开关
    public boolean get_not_disturb() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("not_disturb", false);
    }

    public void set_not_disturb(boolean not_disturb) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("not_disturb", not_disturb);
        editor.commit();
    }

    // 勿扰时间
    public Integer getNotDisturbStartHour() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("getNotDisturbStartHour", 22);
    }

    public void setNotDisturbStartHour(int hour) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("getNotDisturbStartHour", hour);
        editor.apply();
    }

    public Integer getNotDisturbStartMin() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("getNotDisturbStartMin", 0);
    }

    public void setNotDisturbStartMin(int hour) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("getNotDisturbStartMin", hour);
        editor.apply();
    }

    public Integer getNotDisturbEndHour() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("getNotDisturbEndHour", 8);
    }

    public void setNotDisturbEndHour(int hour) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("getNotDisturbEndHour", hour);
        editor.apply();
    }

    public Integer getNotDisturbEndMin() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("getNotDisturbEndMin", 0);
    }

    public void setNotDisturbEndMin(int hour) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("getNotDisturbEndMin", hour);
        editor.apply();
    }

    // 屏保开关
    public boolean get_device_screensaver() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_screensaver", false);
    }

    public void set_device_screensaverb(boolean device_screensaver) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_screensaver", device_screensaver);
        editor.commit();
    }

    //显示用户自定义图片  0 = 系统图片）， 1 = 用户图片,用base64是否为空来判断。
    public void set_screensaver_is_user_imager(int screensaver_is_show_date) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("screensaver_is_user_imager", screensaver_is_show_date);
        editor.commit();
    }

    public int get_screensaver_is_user_imager() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("screensaver_is_user_imager", 1);
    }

    // 时钟格式
    public void set_colock_type(int colock_type) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("colock_type", colock_type);
        editor.commit();
    }

    public int get_colock_type() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("colock_type", 1);
    }

    // 单位
    public void set_device_unit(int device_unit) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("device_unit", device_unit);
        editor.commit();
    }

    public int get_device_unit() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("device_unit", 0);
    }

    //Ecg 频率 0 = 125 ， 1 = 250
    public void set_ecg_frequency(int ecg_frequency) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("ecg_frequency", ecg_frequency);
        editor.commit();
    }

    public int get_ecg_frequency() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("ecg_frequency", 0);
    }

    //ppg 频率 0 = 25 ， 1 = ？
    public void set_ppg_frequency(int ppg_frequency) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("ppg_frequency", ppg_frequency);
        editor.commit();
    }

    public int get_ppg_frequency() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("ppg_frequency", 0);
    }

    //是否支持ECG 0 = 不支持 ， 1 = 支持
    public void set_is_support_ecg(int is_support_ecg) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("is_support_ecg", is_support_ecg);
        editor.commit();
    }

    public int get_is_support_ecg() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("is_support_ecg", 0);
    }

    //是否支持PPG 0 = 不支持 ， 1 = 支持
    public void set_is_support_ppg(int is_support_ppg) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("is_support_ppg", is_support_ppg);
        editor.commit();
    }

    public int get_is_support_ppg() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("is_support_ppg", 1);
    }

    //是否支持血压PPG 0 = 支持 ， 1 = 不支持
    public void set_is_support_blood(int is_support_blood) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("is_support_blood", is_support_blood);
        editor.commit();
    }

    public int get_is_support_blood() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("is_support_blood", 1);
    }

    //是否支持连续心率 0 = 不支持（整点心率） ， 1 = 支持（运动心率）
    public void set_is_support_persist_heart(int is_support_persist_heart) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("is_support_persist_heart", is_support_persist_heart);
        editor.commit();
    }

    public int get_is_support_persist_heart() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("is_support_persist_heart", 1);
    }

    //计步算法类型 0 = 算法一 ， 1 = 算法二  = 设备传什么存什么-用户上传后台
    public void set_device_step_algorithm(int device_step_algorithm) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("device_step_algorithm", device_step_algorithm);
        editor.commit();
    }

    public int get_device_step_algorithm() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("device_step_algorithm", 1);
    }

    //计步算法类型 0 = 算法一 ， 1 = 算法二 = 考虑了老算法-版本号之类的-用于计算数据
    public void set_step_algorithm_type(int step_algorithm_type) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("step_algorithm_type", step_algorithm_type);
        editor.commit();
    }

    public int get_step_algorithm_type() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("step_algorithm_type", 2);
    }

    //消息推送类型 0 = 算法一（长度100） ， 1 = 算法二（长度250）
    public void set_notiface_type(int notiface_type) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("notiface_type", notiface_type);
        editor.commit();
    }

    public int get_notiface_type() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("notiface_type", 0);
    }


//    //是否支持自定义 0 = 不支持 ， 1 = 支持
//    public int get_is_screensaver() {
//        SharedPreferences settin = getSharedPreferencesCommon();
//        return settin.getInt("is_screensaver", 1);
//    }
//
//    public void set_is_screensaver(int is_screensaver) {
//        SharedPreferences settin = getSharedPreferencesCommon();
//        SharedPreferences.Editor editor = settin.edit();
//        editor.putInt("is_screensaver", is_screensaver);
//        editor.commit();
//    }

    //是否支持自定义屏保 0 = 不支持 ， 1 = 支持
    public boolean get_is_screen_saver() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("is_screen_saver", false);
    }

    public void set_is_screen_saver(boolean is_screen_saver) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("is_screen_saver", is_screen_saver);
        editor.commit();
    }

    //Flash 是否存在 0 = 不支持）， 1 = 支持
    public void set_screensaver_falsh(int screensaver_falsh) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("screensaver_falsh", screensaver_falsh);
        editor.commit();
    }

    public int get_screensaver_falsh() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("screensaver_falsh", 0);
    }

    //显示时间 是否存在 0 = 不支持）， 1 = 支持
    public void set_screensaver_is_show_time(int screensaver_is_show_time) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("screensaver_is_show_time", screensaver_is_show_time);
        editor.commit();
    }

    public int get_screensaver_is_show_time() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("screensaver_is_show_time", 1);
    }

    //显示日期 是否存在 0 = 不支持）， 1 = 支持
    public void set_screensaver_is_show_date(int screensaver_is_show_date) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("screensaver_is_show_date", screensaver_is_show_date);
        editor.commit();
    }

    public int get_screensaver_is_show_date() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("screensaver_is_show_date", 1);
    }

    //屏幕形状 0 = 正方形 ， 1 = 球拍，2 = 圆形
    public int get_screen_shape() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("screen_shape", 0);
    }

    public void set_screen_shape(int screen_shape) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("screen_shape", screen_shape);
        editor.commit();
    }

    //自定义锁屏 日期颜色
    public int get_screensaver_color() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("screensaver_color", -1);
    }

    public void set_screensaver_color(int screensaver_color) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("screensaver_color", screensaver_color);
        editor.commit();
    }

    //图片缓存
    public void set_screensaver_bast64(String screensaver) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("screensaver", screensaver);
        editor.commit();
    }

    public String get_screensaver_bast64() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("screensaver", "");
    }

    //自定义锁屏  时间显示 X坐标
    public int get_screensaver_x_time() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("screensaver_x_time", 0);
    }

    public void set_screensaver_x_time(int screensaver_x_time) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("screensaver_x_time", screensaver_x_time);
        editor.commit();
    }

    //自定义锁屏 时间显示 Y坐标
    public int get_screensaver_y_time() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("screensaver_y_time", 0);
    }

    public void set_screensaver_y_time(int screensaver_y_time) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("screensaver_y_time", screensaver_y_time);
        editor.commit();
    }

    //自定义锁屏 时间显示 位置 / -1=自定义， 0=中间 ，1=上 ，2=下 , 3=左上，4=右上,5=左下，6=右下
    public int get_screensaver_post_time() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("screensaver_post_time", 0);
    }

    public void set_screensaver_post_time(int screensaver_post_time) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("screensaver_post_time", screensaver_post_time);
        editor.commit();
    }

    //自定义锁屏 屏幕宽度
    public int get_screensaver_resolution_width() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("screensaver_resolution_width", 0);
    }

    public void set_screensaver_resolution_width(int screensaver_resolution_width) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("screensaver_resolution_width", screensaver_resolution_width);
        editor.commit();
    }

    //自定义锁屏 屏幕高度
    public int get_screensaver_resolution_height() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("screensaver_resolution_height", 0);
    }

    public void set_screensaver_resolution_height(int screensaver_resolution_height) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("screensaver_resolution_height", screensaver_resolution_height);
        editor.commit();
    }

    //自定义锁屏 时间宽度
    public int get_screensaver_time_width() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("screensaver_time_width", 0);
    }

    public void set_screensaver_time_width(int screensaver_time_width) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("screensaver_time_width", screensaver_time_width);
        editor.commit();
    }

    //自定义锁屏 时间高度
    public int get_screensaver_time_height() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("screensaver_time_height", 0);
    }

    public void set_screensaver_time_height(int screensaver_time_height) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("screensaver_time_height", screensaver_time_height);
        editor.commit();
    }

    //自定义锁屏 发送等级-速度
    public int get_screensaver_speed_level() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("screensaver_speed_level", 2);
    }

    public void set_screensaver_speed_level(int screensaver_speed_level) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("screensaver_speed_level", screensaver_speed_level);
        editor.commit();
    }

    //主题个数
    public int get_theme_count() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("theme_count", 0);
    }

    public void set_theme_count(int theme_count) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("theme_count", theme_count);
        editor.commit();
    }


    //设置主题
    public int get_device_theme() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("device_theme", 0);
    }

    public void set_device_theme(int device_theme) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("device_theme", device_theme);
        editor.commit();
    }

    //肤色选择
    public int get_skin_colour() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("skin_colour", 1);
    }

    public void set_skin_colour(int skin_colour) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("skin_colour", skin_colour);
        editor.commit();
    }

    //是否支持设置屏幕亮度和屏幕亮屏时间
    public boolean get_is_support_screen() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("is_support_screen", false);
    }

    public void set_is_support_screen(boolean is_support_screen) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("is_support_screen", is_support_screen);
        editor.commit();
    }

    //屏幕亮度等级
    public int get_screen_brightness() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("screen_brightness", DefaultVale.DEVICE_SCREEN_BRIGHTNESS);
    }

    public void set_screen_brightness(int screen_brightness) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("screen_brightness", screen_brightness);
        editor.commit();
    }

    //亮屏时间
    public int get_brightness_time() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("brightness_time", DefaultVale.DEVICE_BRIGHENSS_TIME);
    }

    public void set_brightness_time(int brightness_time) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("brightness_time", brightness_time);
        editor.commit();
    }

    //是否支持天气
    public void set_is_weather(boolean is_weather) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("is_weather", is_weather);

        editor.commit();
    }

    public boolean get_is_weather() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("is_weather", false);
    }


    //是否打开，天气
    public void set_weather_is_open(boolean weather_is_open) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("weather_is_open", weather_is_open);

        editor.commit();
    }

    public boolean get_weather_is_open() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("weather_is_open", false);
    }

    //是否是，竖向扫描
    public void set_is_scanf_type(boolean is_scanf_type) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("is_scanf_type", is_scanf_type);
        editor.commit();
    }

    public boolean get_is_scanf_type() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("is_scanf_type", false);
    }


    //==============重复上传JSON==============

    //上传的运动数据Json
    public String get_request_sport_json() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("request_sport_json", "");
    }

    //上传的运动数据Json
    public void set_request_sport_json(String request_sport_json) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("request_sport_json", request_sport_json);
        editor.commit();
    }

    //上传的错误睡眠数据Json
    public String get_request_error_sleep_json() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("request_error_sleep_json", "");
    }

    //上传的错误睡眠数据Json
    public void set_request_error_sleep_json(String request_error_sleep_json) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("request_error_sleep_json", request_error_sleep_json);
        editor.commit();
    }


    //上传的用户信息Json
    public String get_request_user_json() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("request_user_json", "");
    }

    //上传的用户信息Json
    public void set_request_user_json(String request_user_json) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("request_user_json", request_user_json);
        editor.commit();
    }


    //上传设备版本号Json
    public String get_request_device_json() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("request_device_json", "");
    }

    //上传设备版本号Json
    public void set_request_device_json(String request_device_json) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("request_device_json", request_device_json);
        editor.commit();
    }


    //==============生理周期==============

    //是否支持生理周期
    public void set_device_is_cycle(boolean device_is_cycle) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_is_cycle", device_is_cycle);
        editor.commit();
    }

    public boolean get_device_is_cycle() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_is_cycle", false);
    }

    //生理周期天数处理-经期
    public void set_device_cycle_jingqi(int device_cycle_jingqi) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("device_cycle_jingqi", device_cycle_jingqi);
        editor.commit();
    }

    public int get_device_cycle_jingqi() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("device_cycle_jingqi", 0);
    }

    //生理周期天数处理-安全期1
    public void set_device_cycle_anqunqiyi(int device_cycle_anqunqiyi) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("device_cycle_anqunqiyi", device_cycle_anqunqiyi);
        editor.commit();
    }

    public int get_device_cycle_anqunqiyi() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("device_cycle_anqunqiyi", 0);
    }

    //生理周期天数处理-危险期
    public void set_device_cycle_weixianqi(int device_cycle_weixianqi) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("device_cycle_weixianqi", device_cycle_weixianqi);
        editor.commit();
    }

    public int get_device_cycle_weixianqi() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("device_cycle_weixianqi", 0);
    }

    //生理周期天数处理-安全期2
    public void set_device_cycle_anquanqier(int device_cycle_anquanqier) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("device_cycle_anquanqier", device_cycle_anquanqier);
        editor.commit();
    }

    public int get_device_cycle_anquanqier() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("device_cycle_anquanqier", 0);
    }


    //    ============运动模式===========

    //是否支持离线运动数据
    public void set_device_is_off_sport(boolean device_is_off_sport) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_is_off_sport", device_is_off_sport);
        editor.commit();
    }

    public boolean get_device_is_off_sport() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_is_off_sport", false);
    }

    //运动模式解析方式
    public void set_device_off_sport_type(int device_off_sport_type) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("device_off_sport_type", device_off_sport_type);
        editor.commit();
    }

    public int get_device_off_sport_type() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("device_off_sport_type", 1);
    }

    //==============睡眠时间段==============

    //是否支持睡眠时间段设置
    public void set_device_is_sleep_time(boolean device_is_sleep_time) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_is_sleep_time", device_is_sleep_time);

        editor.commit();
    }

    public boolean get_device_is_sleep_time() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_is_sleep_time", false);
    }

    //睡眠开始时间-小时
    public int get_sleep_time_start_hour() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("sleep_time_start_hour", 22);
    }

    public void set_sleep_time_start_hour(int sleep_time_start_hour) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("sleep_time_start_hour", sleep_time_start_hour);
        editor.commit();
    }

    //睡眠开始时间-分钟
    public int get_sleep_time_start_min() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("get_sleep_time_start_min", 0);
    }

    public void set_sleep_time_start_min(int get_sleep_time_start_min) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("get_sleep_time_start_min", get_sleep_time_start_min);
        editor.commit();
    }

    //睡眠结束时间-小时
    public int get_sleep_time_end_hour() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("sleep_time_end_hour", 8);
    }

    public void set_sleep_time_end_hour(int sleep_time_end_hour) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("sleep_time_end_hour", sleep_time_end_hour);
        editor.commit();
    }


    //睡眠结束时间-小时
    public int get_sleep_time_end_min() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("sleep_time_end_min", 0);
    }

    public void set_sleep_time_end_min(int sleep_time_end_min) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("sleep_time_end_min", sleep_time_end_min);
        editor.commit();
    }

    //==============免打扰==============


    //是否支持免打扰时间段
    public void set_device_is_not_disrub(boolean device_is_not_disrub) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_is_not_disrub", device_is_not_disrub);
        editor.commit();
    }

    public boolean get_device_is_not_disrub() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_is_not_disrub", false);
    }

    //勿扰模式开始时间-小时
    public int get_do_not_disrub_time_start_hour() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("do_not_disrub_time_start_hour", 20);
    }

    public void set_do_not_disrub_time_start_hour(int do_not_disrub_time_start_hour) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("do_not_disrub_time_start_hour", do_not_disrub_time_start_hour);
        editor.commit();
    }

    //勿扰模式开始时间-分钟
    public int get_do_not_disrub_time_start_min() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("get_do_not_disrub_time_start_min", 0);
    }

    public void set_get_do_not_disrub_time_start_min(int get_do_not_disrub_time_start_min) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("get_do_not_disrub_time_start_min", get_do_not_disrub_time_start_min);
        editor.commit();
    }

    //勿扰模式结束时间-小时
    public int get_do_not_disrub_time_end_hour() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("do_not_disrub_time_end_hour", 8);
    }

    public void set_do_not_disrub_time_end_hour(int do_not_disrub_time_end_hour) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("do_not_disrub_time_end_hour", do_not_disrub_time_end_hour);
        editor.commit();
    }


    //勿扰模式结束时间-小时
    public int get_do_not_disrub_time_end_min() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("do_not_disrub_time_end_min", 0);
    }

    public void set_do_not_disrub_time_end_min(int do_not_disrub_time_end_min) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("do_not_disrub_time_end_min", do_not_disrub_time_end_min);
        editor.commit();
    }


    //    ============心率间隔设置===========

    //是否支持心率间隔设置
    public void set_device_is_interval_hr(boolean device_is_interval_hr) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_is_interval_hr", device_is_interval_hr);
        editor.commit();
    }

    public boolean get_device_is_interval_hr() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_is_interval_hr", false);
    }

    //==============是否支持PPG-心率校准-==============


    //是否支持PPG-心率校准
    public void set_device_is_ppg_hr_jiaozhun(boolean device_is_ppg_hr_jiaozhun) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_is_ppg_hr_jiaozhun", device_is_ppg_hr_jiaozhun);
        editor.commit();
    }

    public boolean get_device_is_ppg_hr_jiaozhun() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_is_ppg_hr_jiaozhun", false);
    }

    //==============添加其他APP通知类型，默认为0-==============

    //添加其他APP通知类型，默认为=0,类型=1=增加(Gmail，IosMail,Outlook,Instaram,Snapchat)，类型=2增加（Facebook,OtherMessage）
    public int get_device_notice_type() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("device_notice_type", 0);
    }

    public void set_device_notice_type(int device_notice_type) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("device_notice_type", device_notice_type);
        editor.commit();
    }

    //==============添加其他APP通知类型，默认为0-==============

    //是否支持目标距离
    public boolean get_device_is_distance_target() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_is_distance_target", false);
    }

    public void set_device_is_distance_target(boolean device_is_distance_target) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_is_distance_target", device_is_distance_target);
        editor.commit();
    }

    //是否支持卡路里
    public boolean get_device_is_calorie_target() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_is_calorie_target", false);
    }

    public void set_device_is_calorie_target(boolean device_is_calorie_target) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_is_calorie_target", device_is_calorie_target);
        editor.commit();
    }

    //是否温度单位设置
    public boolean get_device_temperature_unit() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_temperature_unit", false);
    }

    public void set_device_temperature_unit(boolean device_temperature_unit) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_temperature_unit", device_temperature_unit);
        editor.commit();
    }

    //闹钟是否有效(开启免打扰)
    public boolean get_device_dont_disturb_is_clock() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_dont_disturb_is_clock", false);
    }

    public void set_device_dont_disturb_is_clock(boolean device_dont_disturb_is_clock) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_dont_disturb_is_clock", device_dont_disturb_is_clock);
        editor.commit();
    }

    //是否支持表盘传输
    public boolean get_device_is_theme_transmission() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_is_theme_transmission", false);
    }

    public void set_device_is_theme_transmission(boolean device_is_theme_transmission) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_is_theme_transmission", device_is_theme_transmission);
        editor.commit();
    }

    //表盘传输版本号
    public int get_device_theme_transmission_version() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("device_theme_transmission_version", 0);
    }

    public void set_device_theme_transmission_version(int device_theme_transmission_version) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("device_theme_transmission_version", device_theme_transmission_version);
        editor.commit();
    }


    //是否支持音乐控制
    public boolean get_device_music_control() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_music_control", false);
    }

    public void set_device_music_control(boolean device_music_control) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_music_control", device_music_control);
        editor.commit();
    }


    //是否支持APP端設置勿擾模式開關
    public boolean get_device_is_time_disturb() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_is_time_disturb", true);
    }

    public void set_device_is_time_disturb(boolean device_is_time_disturb) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_is_time_disturb", device_is_time_disturb);
        editor.commit();
    }

    //是否支持APP端設置勿擾模式開關
    public boolean get_device_is_disturb() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_is_disturb", true);
    }

    public void set_device_is_disturb(boolean device_is_disturb) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_is_disturb", device_is_disturb);
        editor.commit();
    }


    //设备蓝牙平台-更固件升级有关联0=Nordic/1=Realtek/2=Dialog
    public int get_device_platform_type() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("device_platform_type", 0);
    }

    public void set_device_platform_type(int device_platform_type) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("device_platform_type", device_platform_type);
        editor.commit();
    }

    //设备音频传输是否支持
    public boolean get_device_is_music_transmission() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_is_music_transmission", false);
    }

    public void set_device_is_music_transmission(boolean device_is_music_transmission) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_is_music_transmission", device_is_music_transmission);
        editor.commit();
    }

    //设备音频传输版本号
    public int get_device_music_transmission_version() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("device_music_transmission_version", 0);
    }

    public void set_device_music_transmission_version(int device_music_transmission_version) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("device_music_transmission_version", device_music_transmission_version);
        editor.commit();
    }


    //主题形状 = 0=方形/1=球拍/2=圆形/3=圆角矩形1
    public int get_device_theme_shape() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("device_theme_shape", 0);
    }

    public void set_device_theme_shape(int device_theme_shape) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("device_theme_shape", device_theme_shape);
        editor.commit();
    }

    //主题形状 = true = 垂直扫描，false = 水平扫描
    public boolean get_device_theme_scanning_mode() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_theme_scanning_mode", false);
    }

    public void set_device_theme_scanning_mode(boolean device_theme_scanning_mode) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_theme_scanning_mode", device_theme_scanning_mode);
        editor.commit();
    }

    //是否支持心率
    public boolean get_device_theme_is_support_heart() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("device_theme_is_support_heart", false);
    }

    public void set_device_theme_is_support_heart(boolean device_theme_is_support_heart) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("device_theme_is_support_heart", device_theme_is_support_heart);
        editor.commit();
    }

    //分辨率-宽度
    public int get_device_theme_resolving_power_width() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("device_theme_resolving_power_width", 0);
    }

    public void set_device_theme_resolving_power_width(int device_theme_resolving_power_width) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("device_theme_resolving_power_width", device_theme_resolving_power_width);
        editor.commit();
    }


    //分辨率-高度
    public int get_device_theme_resolving_power_height() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("device_theme_resolving_power_height", 0);
    }

    public void set_device_theme_resolving_power_height(int device_theme_resolving_power_height) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("device_theme_resolving_power_height", device_theme_resolving_power_height);
        editor.commit();
    }

    //    //默认语言编号
//    public int get_device_theme_default_lan() {
//        SharedPreferences settin = getSharedPreferencesCommon();
//        return settin.getInt("device_theme_default_lan", 0);
//    }
//
//    public void set_device_theme_default_lan(int device_theme_default_lan) {
//        SharedPreferences settin = getSharedPreferencesCommon();
//        SharedPreferences.Editor editor = settin.edit();
//        editor.putInt("device_theme_default_lan", device_theme_default_lan);
//        editor.commit();
//    }
//
//    //默认语言编号
//    public int get_device_theme_support_lan_data() {
//        SharedPreferences settin = getSharedPreferencesCommon();
//        return settin.getInt("device_theme_support_lan_data", 0);
//    }
//
//    public void set_device_theme_support_lan_data(int device_theme_support_lan_data) {
//        SharedPreferences settin = getSharedPreferencesCommon();
//        SharedPreferences.Editor editor = settin.edit();
//        editor.putInt("device_theme_support_lan_data", device_theme_support_lan_data);
//        editor.commit();
//    }
//
    //表盘预留空间
    public int get_device_theme_available_space() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("device_theme_available_space", 0);
    }

    public void set_device_theme_available_space(int device_theme_available_space) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("device_theme_available_space", device_theme_available_space);
        editor.commit();
    }

    /**
     * 表盘数据格式 0=正向，1=反向
     *
     * @return
     */
    public int getClockDialDataFormat() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("ClockDialDataFormat", 0);
    }

    public void setClockDialDataFormat(int ClockDialDataFormat) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("ClockDialDataFormat", ClockDialDataFormat);
        editor.commit();
    }

    /**
     * 生成方式 0=舟海，1=木兰
     *
     * @return
     */
    public int getClockDialGenerationMode() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("ClockDialGenerationMode", 0);
    }

    public void setClockDialGenerationMode(int ClockDialGenerationMode) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("ClockDialGenerationMode", ClockDialGenerationMode);
        editor.commit();
    }

    /**
     * 传输方式 0=舟海，1=木兰protobuf
     *
     * @return
     */
    public int getClockDialTransmissionMode() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("ClockDialTransmissionMode", 0);
    }

    public void setClockDialTransmissionMode(int ClockDialTransmissionMode) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("ClockDialTransmissionMode", ClockDialTransmissionMode);
        editor.commit();
    }

    //是否支持微信运动 1=不支持，0=支持，这个标志位要配合版本号的判断条件使用
    public void set_is_support_wx_sport(boolean is_support_wx_sport) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("is_support_wx_sport", is_support_wx_sport);
        editor.commit();
    }

    public boolean get_is_support_wx_sport() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("is_support_wx_sport", true);
    }

    //设备MTU值
    public void set_device_mtu_num(int device_mtu_num) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("device_mtu_num", device_mtu_num);
        editor.commit();
    }

    public int get_device_mtu_num() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("device_mtu_num", 20);
    }

    //是否支持常用联系人 0=不支持，1=支持
    public void set_is_support_mail_list(boolean is_support_mail_list) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("is_support_mail_list", is_support_mail_list);
        editor.commit();
    }

    public boolean get_is_support_mail_list() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("is_support_mail_list", false);
    }

    //是否支持血氧 0=不支持，1=支持
    public void set_is_support_spo2(boolean is_support_spo2) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("is_support_spo2", is_support_spo2);
        editor.apply();
    }

    public boolean get_is_support_spo2() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("is_support_spo2", false);
    }

    //是否支持整点血氧
    public void setSupportWholeBloodOxygen(boolean is_support_spo2) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("SupportWholeBloodOxygen", is_support_spo2);
        editor.apply();
    }

    public boolean getSupportWholeBloodOxygen() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("SupportWholeBloodOxygen", false);
    }

    //是否支持连续血氧
    public void setSupportContinuousBloodOxygen(boolean is_support_spo2) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("SupportContinuousBloodOxygen", is_support_spo2);
        editor.apply();
    }

    public boolean getSupportContinuousBloodOxygen() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("SupportContinuousBloodOxygen", false);
    }

    //是否支持离线血氧
    public void setSupportOfflineBloodOxygen(boolean is_support_spo2) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("SupportOfflineBloodOxygen", is_support_spo2);
        editor.apply();
    }

    public boolean getSupportOfflineBloodOxygen() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("SupportOfflineBloodOxygen", false);
    }

    //是否支持温度 0=不支持，1=支持
    public void set_is_support_temp(boolean is_support_temp) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("is_support_temp", is_support_temp);
        editor.apply();
    }

    public boolean get_is_support_temp() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("is_support_temp", false);
    }

    // 是否支持整点体温
    public void setSupportWholeTemp(boolean is_support_temp) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("setSupportWholeTemp", is_support_temp);
        editor.apply();
    }

    public boolean getSupportWholeTemp() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("setSupportWholeTemp", false);
    }

    // 是否支持连续体温
    public void setSupportContinuousTemp(boolean is_support_temp) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("setSupportContinuousTemp", is_support_temp);
        editor.apply();
    }

    public boolean getSupportContinuousTemp() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("setSupportContinuousTemp", false);
    }

    //是否支持离线体温
    public void setSupportOfflineTemp(boolean is_support_spo2) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("SupportOfflineTemp", is_support_spo2);
        editor.apply();
    }

    public boolean getSupportOfflineTemp() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("SupportOfflineTemp", false);
    }

    // 连续血氧开关
    public boolean get_continuity_spo2() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("continuity_spo2", false);
    }

    public void set_continuity_spo2(boolean continuity_spo2) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("continuity_spo2", continuity_spo2);
        editor.commit();
    }

    // 连续体温开关
    public boolean get_continuity_temp() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("continuity_temp", false);
    }

    public void set_continuity_temp(boolean continuity_temp) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("continuity_temp", continuity_temp);
        editor.apply();
    }


    public void setGooglefitSyncLastTime(long lastTime) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putLong("GooglefitSyncLastTime", lastTime);
        editor.apply();
    }

    public long getGooglefitSyncLastTime() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getLong("GooglefitSyncLastTime", 0);
    }

    public void setLastSyncTime(long lastTime) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putLong("setLastSyncTime", lastTime);
        editor.apply();
    }

    public long getLastSyncTime() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getLong("setLastSyncTime", 0);
    }

    public void setLastRequestServiceTime(long lastTime) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putLong("setLastRequestServiceTime", lastTime);
        editor.apply();
    }

    public long getLastRequestServiceTime() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getLong("setLastRequestServiceTime", 0);
    }

    public void setLastDeviceSportSyncTime(long lastTime) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putLong("setLastDeviceSportSyncTime", lastTime);
        editor.apply();
    }

    public long getLastDeviceSportSyncTime() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getLong("setLastDeviceSportSyncTime", 0);
    }

    public void setIsOpenGooglefit(boolean isOpen) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("setIsOpenGooglefit", isOpen);
        editor.apply();
    }

    public boolean getIsOpenGooglefit() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("setIsOpenGooglefit", false);
    }


    //自定义表盘-图片缓存
    public void set_clock_dial_custom_bast64(String clock_dial_custom_bast64) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("clock_dial_custom_bast64", clock_dial_custom_bast64);
        editor.commit();
    }

    public String get_clock_dial_custom_bast64() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("clock_dial_custom_bast64", "");
    }

    // 温度 默认0 = 摄氏度 , 1 = 华氏度
    public void setTemperatureType(int type) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putInt("TemperatureType", type);
        editor.apply();
    }

    public int getTemperatureType() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt("TemperatureType", 1);
    }

    // 温度 默认0 摄氏度
    public void setCardSortJson(String json) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("CardSortJson", json);
        editor.apply();
    }

    public String getCardSortJson() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("CardSortJson", "");
    }

    //是否支持Protobuf协议
    public void setIsSupportProtobuf(boolean IsSupportProtobuf) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsSupportProtobuf", IsSupportProtobuf);
        editor.apply();
    }

    public boolean getIsSupportProtobuf() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsSupportProtobuf", false);
    }

    //固件升级方式
    public void setPointExercise(boolean PointExercise) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("PointExercise", PointExercise);
        editor.apply();
    }

    public boolean getPointExercise() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("PointExercise", false);
    }

    /**
     * false = 官方升级 ,true = protobuf应用层升级
     *
     * @param DeviceUpdateType
     */
    public void setDeviceUpdateType(boolean DeviceUpdateType) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("DeviceUpdateType", DeviceUpdateType);
        editor.apply();
    }

    public boolean getDeviceUpdateType() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("DeviceUpdateType", false);
    }

    //直达卡片排序
    public void setIsSupportPageDevice(boolean DeviceUpdateType) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("setIsSupportPageDevice", DeviceUpdateType);
        editor.apply();
    }

    //直达卡片排序
    public boolean getIsSupportPageDevice() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("setIsSupportPageDevice", false);
    }

    //GPS运动
    public void setIsSupportGpsSport(boolean IsSupportGpsSport) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsSupportGpsSport", IsSupportGpsSport);
        editor.apply();
    }

    public boolean getIsSupportGpsSport() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsSupportGpsSport", false);
    }

    //GPS传感器
    public void setIsGpsSensor(boolean IsGpsSensor) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsGpsSensor", IsGpsSensor);
        editor.apply();
    }

    public boolean getIsGpsSensor() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsGpsSensor", false);
    }

    //内置表盘选择
    public boolean getIsSupportBuiltDialSelection() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsSupportBuiltDialSelection", false);
    }

    public void setIsSupportBuiltDialSelection(boolean IsSupportBuiltDialSelection) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsSupportBuiltDialSelection", IsSupportBuiltDialSelection);
        editor.apply();
    }

    /**
     * 体温测量间隔
     *
     * @return
     */
    public boolean getIsTempMeasurementInterval() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsTempMeasurementInterval", false);
    }

    public void setIsTempMeasurementInterval(boolean IsTempMeasurementInterval) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsTempMeasurementInterval", IsTempMeasurementInterval);
        editor.apply();
    }

    /**
     * 是否支持-客户定制APP推送？Corona-Warn-App
     *
     * @return
     */
    public boolean getIsCoronaNotiface() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsCoronaNotiface", false);
    }

    public void setIsCoronaNotiface(boolean IsCoronaNotiface) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsCoronaNotiface", IsCoronaNotiface);
        editor.apply();
    }


    //是否只是新的设备类型规则
    public void setSupportNewDeviceType(boolean SupportNewDeviceType) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("SupportNewDeviceType", SupportNewDeviceType);
        editor.apply();
    }

    public boolean getSupportNewDeviceType() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("SupportNewDeviceType", false);
    }

    //是否只是新的设备类型规则
    public void setSupportNewDeviceCrc(boolean SupportNewDeviceCrc) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("SupportNewDeviceCrc", SupportNewDeviceCrc);
        editor.apply();
    }

    public boolean getSupportNewDeviceCrc() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("SupportNewDeviceCrc", false);
    }

    //是否只是新的设备类型规则
    public void setSupporDeviceSubVersion(boolean SupporDeviceSubVersion) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("SupporDeviceSubVersion", SupporDeviceSubVersion);
        editor.apply();
    }

    public boolean getSupporDeviceSubVersionc() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("SupporDeviceSubVersion", false);
    }


    //是否需要单包回应
    public void setisReplyOnePack(boolean isReplyOnePack) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("isReplyOnePack", isReplyOnePack);
        editor.apply();
    }

    public boolean getisReplyOnePack() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("isReplyOnePack", false);
    }

    public long getLastUploadDataServiceTime() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getLong("LastUploadDataServiceTime", 0);
    }

    public void setLastUploadDataServiceTime(long lastTime) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putLong("LastUploadDataServiceTime", lastTime);
        editor.apply();
    }


    // 天气 开关
    public long getWeatherSyncTime() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getLong("setWeatherSyncTime", 0);
    }

    public void setWeatherSyncTime(long cityName) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putLong("setWeatherSyncTime", cityName);
        editor.apply();
    }

    public boolean getWeatherSwitch() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("setWeatherSwitch", false);
    }

    public void setWeatherSwitch(boolean cityName) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("setWeatherSwitch", cityName);
        editor.apply();
    }

    public String getWeatherCity() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("setWeatherCity", "");
    }

    public void setWeatherCity(String cityName) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("setWeatherCity", cityName);
        editor.apply();
    }

    public String getWeatherCityID() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("setWeatherCityID", "");
    }

    public void setWeatherCityID(String cityName) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("setWeatherCityID", cityName);
        editor.apply();
    }

    // lat, lon
    public void setWeatherGps(String gsp) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("setWeatherGps", gsp);
        editor.apply();
    }

    public String getWeatherGps() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("setWeatherGps", "");
    }

    // weather json 一周天气
    public void setWeatherForecast(String gsp) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("setWeatherForecast", gsp);
        editor.apply();
    }

    public String getWeatherForecast() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("setWeatherForecast", "");
    }

    // 当前
    public void setWeatherArea(String gps) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putString("setWeatherArea", gps);
        editor.apply();
    }

    public String getWeatherArea() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getString("setWeatherArea", "");
    }

    public boolean getIsSupportDrinkWater() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsSupportDrinkWater", false);
    }

    public void setIsSupportDrinkWater(boolean IsCoronaNotiface) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsSupportDrinkWater", IsCoronaNotiface);
        editor.apply();
    }

    public boolean getIsSupportLongSit() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsSupportLongSit", false);
    }

    public void setIsSupportLongSit(boolean IsCoronaNotiface) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsSupportLongSit", IsCoronaNotiface);
        editor.apply();
    }

    public boolean getIsSupportMeeting() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsSupportMeeting", false);
    }

    public void setIsSupportMeeting(boolean IsCoronaNotiface) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsSupportMeeting", IsCoronaNotiface);
        editor.apply();
    }

    public boolean getIsSupportDrug() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsSupportDrug", false);
    }

    public void setIsSupportDrug(boolean IsCoronaNotiface) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsSupportDrug", IsCoronaNotiface);
        editor.apply();
    }

    public boolean getIsSupportAlarmClock() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsSupportAlarmClock", false);
    }

    public void setIsSupportAlarmClock(boolean IsCoronaNotiface) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsSupportAlarmClock", IsCoronaNotiface);
        editor.apply();
    }

    public boolean getIsSupportTakePicture() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsSupportTakePicture", false);
    }

    public void setIsSupportTakePicture(boolean IsCoronaNotiface) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsSupportTakePicture", IsCoronaNotiface);
        editor.apply();
    }

    public boolean getIsSupportRaiseWristBrightenScreen() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsSupportRaiseWristBrightenScreen", false);
    }

    public void setIsSupportRaiseWristBrightenScreen(boolean IsCoronaNotiface) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsSupportRaiseWristBrightenScreen", IsCoronaNotiface);
        editor.apply();
    }

    //设备是否支持获取状态，判断是或否可以升级ota表盘电量足够
    public boolean getIsSupportGetDeviceProtoStatus() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("getIsSupportGetDeviceProtoStatus", false);
    }

    public void setIsSupportGetDeviceProtoStatus(boolean getIsSupportGetDeviceProtoStatus) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("getIsSupportGetDeviceProtoStatus", getIsSupportGetDeviceProtoStatus);
        editor.apply();
    }

    //设备是否支持辅助定位
    public boolean getIsSupportAppAuxiliarySport() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsSupportAppAuxiliarySport", false);
    }

    public void setIsSupportAppAuxiliarySport(boolean IsSupportAppAuxiliarySport) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsSupportAppAuxiliarySport", IsSupportAppAuxiliarySport);
        editor.apply();
    }

    //设备是否支持卡路里目标
    public boolean getIsSupportCalorieTarget() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsSupportCalorieTarget", false);
    }

    public void setIsSupportCalorieTarget(boolean IsSupportCalorieTarget) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsSupportCalorieTarget", IsSupportCalorieTarget);
        editor.apply();
    }

    //设备是否支持卡路里目标
    public boolean getIsSupportDistanceTarget() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsSupportDistanceTarget", false);
    }

    public void setIsSupportDistanceTarget(boolean IsSupportDistanceTarget) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsSupportDistanceTarget", IsSupportDistanceTarget);
        editor.apply();
    }

    //设备是否支持活动时长目标
    public boolean getIsSupportActivityTimeTarget() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsSupportActivityTimeTarget", false);
    }

    public void setIsSupportActivityTimeTarget(boolean IsSupportActivityTimeTarget) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsSupportActivityTimeTarget", IsSupportActivityTimeTarget);
        editor.apply();
    }

    //设备是否支持活动时长目标
    public boolean getIsSupportSleepTarget() {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getBoolean("IsSupportSleepTarget", false);
    }

    public void setIsSupportSleepTarget(boolean IsSupportSleepTarget) {
        SharedPreferences settin = getSharedPreferencesCommon();
        SharedPreferences.Editor editor = settin.edit();
        editor.putBoolean("IsSupportSleepTarget", IsSupportSleepTarget);
        editor.apply();
    }
}