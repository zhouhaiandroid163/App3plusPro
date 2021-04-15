package com.zjw.apps3pluspro.network;


import android.content.Context;
import android.text.TextUtils;

import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.network.entity.CalibrationData;
import com.zjw.apps3pluspro.network.entity.ContinunitySpo2Data;
import com.zjw.apps3pluspro.network.entity.ContinunityTempData;
import com.zjw.apps3pluspro.network.entity.HealthData;
import com.zjw.apps3pluspro.network.entity.HeartData;
import com.zjw.apps3pluspro.network.entity.MeasureSpo2Data;
import com.zjw.apps3pluspro.network.entity.MeasureTempData;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.entity.SleepData;
import com.zjw.apps3pluspro.network.entity.SportData;
import com.zjw.apps3pluspro.network.entity.UserData;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.entity.SportModleInfo;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.network.AESUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;


/**
 * 获取,请求后台的Json数据
 * Created by zjw on 2018/3/7.
 */

public class RequestJson {

    // 微信地址=暂时用老服务器的地址，因为怕有冲突，互相抢Token
    public static String WEIXIN_BASE_URL = "http://www.wearheart.cn:8089/zh";
    //=====================================接口地址============================================
    //生产地址-用来检测是否是正式服务器
    public static final String releaseRequestUrl = "http://ffit.wearheart.cn/zh_watch/";

    //生产地址
//    public static final String requestUrl = "http://ffit.wearheart.cn/zh_watch/";
    //测试地址
    public static final String requestUrl = "http://47.106.117.114:8090/zh_watch/";
    //本地地址
//    public static final String requestUrl = "http://192.168.0.13:8080/zh_watch/";


    //    public static final String userLoginUrl = requestUrl + "/userApi/userLogin";
    //    public static final String userRegisterUrl = requestUrl + "/userApi/userRegister";
//    public static final String getValidationCodeUrl = requestUrl + "/checkCodeApi/getValidationCode";//获取验证码
//    public static final String checkValidationCodeUrl = requestUrl + "/checkCodeApi/checkValidationCode";
    //    public static final String checkUserRegisteredUrl = requestUrl + "/userApi/checkUserRegistered";
//    public static final String thirdPartyLoginUrl = requestUrl + "/userApi/thirdPartyLogin";
    //    public static final String getUserInfoUrl = requestUrl + "/userApi/getUserInfo";
//    public static final String modifyUserInfoUrl = requestUrl + "/userApi/modifyUserInfo";
//    public static final String modifyUserPasswordUrl = requestUrl + "/userApi/modifyUserPassword";
//    public static final String uploadUserInfoHeadUrl = requestUrl + "/userApi/uploadUserInfoHead";

    //    public static final String getSportDataUrl = requestUrl + "/homepageApi/getSportData";

    //    public static final String getdHealthDataUrl = requestUrl + "/healthApi/getdHealthData";
    public static final String deleteHealthDataUrl = requestUrl + "/healthApi/deleteHealthData";
    //    public static final String uploadPhoneInfoUrl = requestUrl + "/userApi/uploadPhoneInfo";
//    public static final String uploadDeviceInfoUrl = requestUrl + "/deviceApi/uploadDeviceInfo";
//    public static final String getDeviceUpdateInfoUrl = requestUrl + "/firmwareApi/getDeviceUpdateInfo";
    //    public static final String getAppUpdateInfoUrl = requestUrl + "/appApi/getAppUpdateInfo";
//    public static final String feedbackUrl = requestUrl + "/feekBackApi/feedback";
    //    public static final String getFriendsListUrl = requestUrl + "/friendApi/getFriendsList";
//    public static final String searchFriendsUrl = requestUrl + "/friendApi/searchFriends";
//    public static final String getFriendsInfoUrl = requestUrl + "/friendApi/getFriendsInfo";
//    public static final String deleteFriendUrl = requestUrl + "/friendApi/deleteFriend";
    //    public static final String addFriendsdUrl = requestUrl + "/friendApi/addFriends";
//    public static final String requestFriendsListUrl = requestUrl + "/friendApi/requestFriendsList";
    //    public static final String acceptingFriendUrl = requestUrl + "/friendApi/acceptingFriend";
    //    public static final String queryWeatherByAreaUrl = requestUrl + "/weatherApi/queryWeatherByArea";
//    public static final String queryWeatherByAreaUrl = requestUrl + "/heFengApi/queryWeatherByArea";
//    public static final String queryWeatherForecastUrl = requestUrl + "/heFengApi/queryWeatherForecast";

    //=====================================新接口=============================================
    public static final String queryByLoginNameUrl = requestUrl + "ffit/user/queryByLoginName";//查询是否注册过
    public static final String registerUrl = requestUrl + "ffit/user/register";//注册
    public static final String getValidationCodeUrl = requestUrl + "ffit/auth/getCode";//获取验证码
    public static final String checkValidationCodeUrl = requestUrl + "ffit/user/forgetPwd";
    public static final String loginUrl = requestUrl + "ffit/user/login";//登录
    public static final String modifyUserPasswordUrl = requestUrl + "ffit/user/resetPwd";//修改密码

    public static final String thirdPartyLoginUrl = requestUrl + "ffit/thirdParty/ssoLogin";//第三方登录

    public static final String getUserInfoUrl = requestUrl + "ffit/userInfo/getUserInfo";//根据用户ID查询用户基本信息
    public static final String modifyUserInfoUrl = requestUrl + "ffit/userInfo/save";//保存用户信息
    public static final String queryByUserIDUrl = requestUrl + "ffit/calibration/queryByUserID";//根据用户ID查询校准信息
    public static final String modifyCalibrationInfoUrl = requestUrl + "ffit/calibration/save";//保存校准信息
    public static final String uploadUserInfoHeadUrl = requestUrl + "ffit/userInfo/uploadHeadUrl";//修改用户头像

    public static final String uploadSportDataUrl = requestUrl + "ffit/sport/saveList";//上传运动数据
    public static final String getSportDataUrl = requestUrl + "ffit/sport/getDataList";//获取天-运动数据暂不处理
    public static final String getSportListDataUrl = requestUrl + "ffit/sport/getDataList";//获取运动数据
    public static final String uploadSleepListDataUrl = requestUrl + "ffit/sleep/saveList";//上传睡眠数据
    public static final String getSleepListDataUrl = requestUrl + "ffit/sleep/getDataList";//获取睡眠数据

    public static final String uploadHeartListDataUrl = requestUrl + "ffit/heartRate/saveList";//上传心率数据
    public static final String getHeartListDataUrl = requestUrl + "ffit/heartRate/getDataList";//获取心率数据

    public static final String uploadHealthDataUrl = requestUrl + "ffit/health/saveList";//上传健康数据
    public static final String getdHealthDataUrl = requestUrl + "ffit/health/getDataList";//获取健康数据
    public static final String getdHealthEcgPpgDataUrl = requestUrl + "ffit/health/getDataByID";//获取健康数据


    public static final String getFriendsListUrl = requestUrl + "ffit/careFriend/getFriendList";//获取好友列表
    public static final String searchFriendsdUrl = requestUrl + "ffit/careFriend/searchFriend";//搜索好友
    public static final String addFriendsdUrl = requestUrl + "ffit/careFriend/addReq";//发出，加别人好请求
    public static final String requestFriendsListUrl = requestUrl + "ffit/careFriend/getReqFriendList";//好友申请列表
    public static final String handleFriendUrl = requestUrl + "ffit/careFriend/updStatus";//处理好友的申请

    public static final String getAppUpdateInfoUrl = requestUrl + "ffit/appUpdate/getAppVersion";//APP升级
    public static final String uploadBindDevice = requestUrl + "ffit/device/conn";//绑定设备上传
    public static final String unbindDevice = requestUrl + "ffit/device/disConn";//解绑设备
    public static final String downLoadBindDevice = requestUrl + "ffit/device/info";//用户设备信息

    public static final String feedbackUrl = requestUrl + "feekBackApi/feedback";//意见反馈//沿用老接口
    public static final String feedbackUrl2 = requestUrl + "ffit/feedBack/opinionSubmission";//意见反馈//沿用老接口
    public static final String getDeviceUpdateInfoUrl = requestUrl + "ffit/firmware/getFirewareUpgradeVersion";//固件升级//沿用老接口

    public static final String getThemePageList = requestUrl + "ffit/theme/getPageList";//获取主题列表
    public static final String getThemeFile = requestUrl + "ffit/theme/getThemeFile";//获取主题列表

    public static final String getMusicPageList = requestUrl + "ffit/song/getPageList";//获取音频列表

    public static final String uploadContinunitySpo2Url = requestUrl + "ffit/spo/saveData";//上传连续血氧数据
    public static final String getContinuitySpo2DataUrl = requestUrl + "ffit/spo/getDataByDate";//获取连续血氧数据
    public static final String getContinuitySpo2ListDataUrl = requestUrl + "ffit/spo/getDataList";//获取连续血氧列表数据
    public static final String uploadMeasureSpo2Url = requestUrl + "ffit/spo/saveMeasureData";//上传离线测量血氧数据
    public static final String getMeasureSpo2Url = requestUrl + "ffit/spo/getMeasureDataList";//获取离线测量血氧数据

    public static final String uploadContinunityTempUrl = requestUrl + "ffit/temp/saveData";//上传连续体温数据
    public static final String getContinuityTempDataUrl = requestUrl + "ffit/temp/getDataByDate";//获取连续体温数据
    public static final String getContinuityTempListDataUrl = requestUrl + "ffit/temp/getDataList";//获取连续体温列表数据
    public static final String uploadMeasureTempUrl = requestUrl + "ffit/temp/saveMeasureData";//上传离线测量体温数据
    public static final String getMeasureTempUrl = requestUrl + "ffit/temp/getMeasureDataList";//获取离线测量体温数据
    public static final String uploadAppInfo = requestUrl + "ffit/openapp/startApp";    //上传APP信息到后台
    public static final String uploadErrorInfo = requestUrl + "ffit/openapp/uploadErrorLog";    //上传闪退等错误信息到后台
    public static final String getSystemPermissionImageUrl = requestUrl + "ffit/appKeepAlive/imageList";
    public static final String getSupportLanguage = requestUrl + "ffit/faq/languageList";
    public static final String getModuleList = requestUrl + "ffit/faq/moduleList";
    public static final String getNotesList = requestUrl + "ffit/faq/notesList";
    public static final String postGpsSport = requestUrl + "ffit/moreSport/saveGPSList";
    public static final String postRecordPointList = requestUrl + "ffit/moreSport/saveRecordPointList";
    public static final String getMoreSportData = requestUrl + "ffit/moreSport/getSportListByDay";
    public static final String getMoreSportDataDetail = requestUrl + "ffit/moreSport/getData";
    public static final String getIsUploadMoreSport = requestUrl + "ffit/moreSport/getSysDic";
    public static final String syncWatchTime = requestUrl + "ffit/device/syncWatchTime";
    public static final String getMainDialList = requestUrl + "ffit/dial/getHomeList";
    public static final String getMoreDialPageList = requestUrl + "ffit/dial/moreDialPageList";
    public static final String getDialDetails = requestUrl + "ffit/dial/info";
    public static final String uploadDialDownloadRecording = requestUrl + "ffit/dial/downloadDialLog";
    // 天气
    public static final String getWeatherCity = requestUrl + "ffit/heFengApi/lookupCity";
    public static final String getWeatherArea = requestUrl + "ffit/heFengApi/nowWeather";
    public static final String getWeatherForecast = requestUrl + "ffit/heFengApi/forecast7dWeather";
    //
    public static final String getLto = requestUrl + "ffit/bream/downloadLto";

    //new version theme
    public static final String getHomeByProductList = requestUrl + "ffit/dial/getHomeByProductList";
    public static final String moreDialPageByProductList = requestUrl + "ffit/dial/moreDialPageByProductList";
    public static final String queryDialProduct = requestUrl + "ffit/dial/queryDialProduct";


    //====================================检查服务器============================================

    public static boolean checkServiceReleaseUrl() {

        if (releaseRequestUrl.equals(requestUrl)) {
            return true;
        } else {
            return false;
        }
    }

    //=====================================登录注册============================================


    /**
     * 第三方登录
     *
     * @param mContext
     * @param openid
     * @param uid
     * @param name
     * @param iconurl
     * @param registerTime
     * @return
     */
    public static RequestInfo thirdPartyLogin(Context mContext, String openid, String uid, String name, String iconurl, String registerTime, String accountType) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("openid", openid);
            request_json.put("uid", uid);
            request_json.put("name", name);
            request_json.put("gender", "0");//固定传0=男
            request_json.put("iconurl", iconurl);
            request_json.put("registerTime", registerTime);
            request_json.put("phoneSystemType", "2");
            request_json.put("phoneType", MyUtils.getPhoneModel());
            request_json.put("phoneSystemVersion", MyUtils.getOsVersion());
            request_json.put("phoneMac", "");
            request_json.put("meid", "");
            request_json.put("appName", MyUtils.getAppName());//APP名称
            request_json.put("appVersion", MyUtils.getAppInfo());//APP版本号
            request_json.put("accountType", accountType);//APP版本号


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, thirdPartyLoginUrl);
    }

    //=====================================删除健康数据============================================

    /**
     * 删除日期
     *
     * @param id
     * @return
     */
    public static RequestInfo deleteHealthData(String id) {
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, deleteHealthDataUrl);
    }


    //=====================================好友相关============================================

//    /**
//     * 获取好友详情
//     *
//     * @return
//     */
//    public static RequestInfo getFriendsInfo(String user_id) {
//        JSONObject request_json = new JSONObject();
//        try {
//            request_json.put("userId", user_id);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return new RequestInfo(request_json, getFriendsInfoUrl);
//    }
//
//
//    /**
//     * 删除好友
//     *
//     * @param friend_id = 当前用户和好友两个人之间的关联ID
//     * @return
//     */
//    public static RequestInfo deleteFriend(String friend_id) {
//        JSONObject request_json = new JSONObject();
//        try {
//            request_json.put("friendId", friend_id);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return new RequestInfo(request_json, deleteFriendUrl);
//    }


    //===================新接口调试==================

    //=====================================注册登录相关============================================


    /**
     * 用户是否注册过
     *
     * @param account
     * @return
     */
    public static RequestInfo checkUserRegistered(String account) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("loginName", account);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, queryByLoginNameUrl);
    }


    /**
     * 注册
     *
     * @param account  账号
     * @param password 密码
     * @param type     0=手机号，1=邮箱
     * @return
     */
    public static RequestInfo userRegister(Context mContext, String account, String password, String type) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("loginName", account);//账号
            request_json.put("accountType", type);//登录类型
            request_json.put("password", password);//密码
            request_json.put("registerTime", MyTime.getAllTime());//注册时间
            request_json.put("phoneSystemType", "2");//手机系统类型 0:其他,1:ios,2:andorid
            request_json.put("phoneType", MyUtils.getPhoneModel());//手机型号
            request_json.put("phoneSystemVersion", MyUtils.getOsVersion());//系统版本号
            request_json.put("phoneMac", "");//MAC地址
            request_json.put("meid", "");//MEID
            request_json.put("appName", MyUtils.getAppName());//APP名字
            request_json.put("appVersion", MyUtils.getAppInfo());//APP版本号
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, registerUrl);
    }


    /**
     * 获取验证码
     *
     * @param account     账号
     * @param accountType 账号类型 1：手机 2：邮箱
     * @param reqType     请求类型 1：注册 2：忘记密码
     * @return
     */
    public static RequestInfo getValidationCode(String account, int accountType, int reqType) {

        JSONObject request_json = new JSONObject();

        try {

            request_json.put("loginName", account);
            request_json.put("accountType", accountType);
            request_json.put("reqType", reqType);
            request_json.put("appId", "02");
            request_json.put("emailType", 2);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getValidationCodeUrl);
    }


    /**
     * 验证验证码
     *
     * @param account         账号
     * @param validation_code 验证码
     * @param new_password    新密码
     * @return
     */
    public static RequestInfo checkValidationCode(Context mContext, String account, String validation_code, String new_password) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("loginName", account);
            request_json.put("resetPassword", new_password);
            request_json.put("validationCode", validation_code);

            request_json.put("phoneSystemType", "2");//手机系统类型 0:其他,1:ios,2:andorid
            request_json.put("phoneType", MyUtils.getPhoneModel());//手机型号
            request_json.put("phoneSystemVersion", MyUtils.getOsVersion());//系统版本号
            request_json.put("phoneMac", "");//MAC地址
            request_json.put("meid", "");//MEID
            request_json.put("appName", MyUtils.getAppName());//APP名字
            request_json.put("appVersion", MyUtils.getAppInfo());//APP版本号
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, checkValidationCodeUrl);
    }

    /**
     * 登录
     *
     * @param account  账号
     * @param password 密码
     * @return
     */
    public static RequestInfo userLogin(Context mContext, String account, String password) {

        JSONObject request_json = new JSONObject();

        try {
            request_json.put("loginName", account);//账号
            request_json.put("password", password);//密码
            request_json.put("phoneSystemType", "2");//手机系统类型 0:其他,1:ios,2:andorid
            request_json.put("phoneType", MyUtils.getPhoneModel());//手机型号
            request_json.put("phoneSystemVersion", MyUtils.getOsVersion());//系统版本号
            request_json.put("phoneMac", "");//MAC地址
            request_json.put("meid", "");//MEID
            request_json.put("appName", MyUtils.getAppName());//APP名字
            request_json.put("appVersion", MyUtils.getAppInfo());//APP版本号
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, loginUrl);
    }

    /**
     * 修改用户密码
     *
     * @return
     */
    public static RequestInfo modifyUserPassword(Context mContext, String oldPassword, String newPassword) {

        UserSetTools mUserSetTools = BaseApplication.getUserSetTools();

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("loginName", mUserSetTools.get_user_account());
            request_json.put("oldPassword", oldPassword);
            request_json.put("resetPassword", newPassword);

            request_json.put("phoneSystemType", "2");//手机系统类型 0:其他,1:ios,2:andorid
            request_json.put("phoneType", MyUtils.getPhoneModel());//手机型号
            request_json.put("phoneSystemVersion", MyUtils.getOsVersion());//系统版本号
            request_json.put("phoneMac", "");//MAC地址
            request_json.put("meid", "");//MEID
            request_json.put("appName", MyUtils.getAppName());//APP名字
            request_json.put("appVersion", MyUtils.getAppInfo());//APP版本号
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, modifyUserPasswordUrl);
    }


    //=====================================用户相关============================================


    /**
     * 获取用户信息
     *
     * @return
     */
    public static RequestInfo getUserInfo() {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getUserInfoUrl);
    }


    /**
     * 获取用户信息
     *
     * @return
     */
    public static RequestInfo getUserInfo(String user_id) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getUserInfoUrl);
    }

    /**
     * 修改用户信息
     *
     * @return
     */
    public static RequestInfo modifyUserInfo(Context context, UserData mUserData, boolean isuploadCityName) {

        if (mUserData == null) {
            return null;
        }

        mUserData.setUserId(BaseApplication.getUserId());
        if (isuploadCityName) {
            mUserData.setCity(HomeActivity.cityName);
        }
        mUserData.setAppName(context.getString(R.string.app_name));
        mUserData.setAppVersion(MyUtils.getAppInfo());

        mUserData.setLongitude(String.valueOf(HomeActivity.phoneLon));
        mUserData.setLatitude(String.valueOf(HomeActivity.phoneLat));

        JSONObject request_json = UserData.getUserData(mUserData);


        return new RequestInfo(request_json, modifyUserInfoUrl);
    }


    /**
     * 获取校准信息
     *
     * @return
     */
    public static RequestInfo getUserCalibrationInfo() {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, queryByUserIDUrl);
    }


    /**
     * 修改校准信息
     *
     * @return
     */
    public static RequestInfo modifyCalibrationInfo(CalibrationData mCalibrationData) {

        if (mCalibrationData == null) {
            return null;
        }

        mCalibrationData.setUserId(BaseApplication.getUserId());

        JSONObject request_json = CalibrationData.getCalibrationData(mCalibrationData);

        return new RequestInfo(request_json, modifyCalibrationInfoUrl);
    }


    /**
     * 上传用户头像
     *
     * @param head_image_data
     * @return
     */
    public static RequestInfo uploadUserInfoHead(String head_image_data) {
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("headImageFormat", "png");
            request_json.put("headImageData", head_image_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, uploadUserInfoHeadUrl);
    }


    //=====================================运动相关============================================

    /**
     * 上传运动数据
     *
     * @param sport_data_list
     * @return
     */
    public static RequestInfo uploadSportData(ArrayList<SportData> sport_data_list) {


        if (sport_data_list == null || sport_data_list.size() == 0) {
            return null;
        }

        JSONArray jsonArray = SportData.getSportDataListData(sport_data_list);

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("sportList", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, uploadSportDataUrl);
    }


    /**
     * 获取运动数据
     *
     * @param begin_time 开始时间
     * @param end_time   结束时间
     */
    public static RequestInfo getSportData(String begin_time, String end_time) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("sportDateB", begin_time);
            request_json.put("sportDateE", end_time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, getSportDataUrl);
    }

    /**
     * 获取运动数据
     *
     * @param begin_time 开始时间
     * @param end_time   结束时间
     */
    public static RequestInfo getSportListData(String begin_time, String end_time) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("sportDateB", begin_time);
            request_json.put("sportDateE", end_time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, getSportListDataUrl);
    }


    /**
     * 上传睡眠数据
     *
     * @param sleep_data_list
     * @return
     */
    public static RequestInfo uploadSleepData(ArrayList<SleepData> sleep_data_list) {

        if (sleep_data_list == null || sleep_data_list.size() == 0) {
            return null;
        }

        JSONArray jsonArray = SleepData.getSleepDataListData(sleep_data_list);

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("sleepList", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, uploadSleepListDataUrl);
    }

    /**
     * 获取睡眠数据
     *
     * @param begin_time 开始时间
     * @param end_time   结束时间
     */
    public static RequestInfo getSleepListData(String begin_time, String end_time) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("sleepDateB", begin_time);
            request_json.put("sleepDateE", end_time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, getSleepListDataUrl);
    }


    /**
     * 上传所有心率数据，包括连续心率，和整点心率
     *
     * @param heart_data_list
     * @return
     */
    public static RequestInfo uploadHeartListData(ArrayList<HeartData> heart_data_list) {

        if (heart_data_list == null || heart_data_list.size() == 0) {
            return null;
        }

        JSONArray jsonArray = HeartData.getDataListData(heart_data_list);

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("heartRateList", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, uploadHeartListDataUrl);
    }


    /**
     * 获取整点心率数据
     *
     * @param begin_time 开始时间
     * @param end_time   结束时间
     */
    public static RequestInfo getPoListData(String begin_time, String end_time) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("heartRateRawDateB", begin_time);
            request_json.put("heartRateRawDateE", end_time);
            request_json.put("heartRateType", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, getHeartListDataUrl);
    }


    /**
     * 获取整点心率数据
     *
     * @param begin_time 开始时间
     * @param end_time   结束时间
     */
    public static RequestInfo getPoListData(String begin_time, String end_time, String user_id) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", user_id);
            request_json.put("heartRateRawDateB", begin_time);
            request_json.put("heartRateRawDateE", end_time);
            request_json.put("heartRateType", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, getHeartListDataUrl);
    }


    /**
     * 获取连续心率数据
     *
     * @param begin_time 开始时间
     * @param end_time   结束时间
     */
    public static RequestInfo getWoListData(String begin_time, String end_time) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("heartRateRawDateB", begin_time);
            request_json.put("heartRateRawDateE", end_time);
            request_json.put("heartRateType", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, getHeartListDataUrl);
    }

    /**
     * 获取连续心率数据
     *
     * @param begin_time 开始时间
     * @param end_time   结束时间
     */
    public static RequestInfo getWoListData(String begin_time, String end_time, String user_id) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", user_id);
            request_json.put("heartRateRawDateB", begin_time);
            request_json.put("heartRateRawDateE", end_time);
            request_json.put("heartRateType", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, getHeartListDataUrl);
    }


    //=====================================健康相关============================================

    /**
     * 上传健康数据
     *
     * @param health_data_list
     * @return
     */
    public static RequestInfo uploadHealthData(ArrayList<HealthData> health_data_list) {

        if (health_data_list == null || health_data_list.size() == 0) {
            return null;
        }

        JSONArray jsonArray = HealthData.getHealthDataListData(health_data_list);

        JSONObject request_json = new JSONObject();

        try {
            request_json.put("healthList", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, uploadHealthDataUrl);
    }

    /**
     * @param begin_time 开始时间
     * @param end_time   结束时间
     * @return
     */
    public static RequestInfo getdHealthData(String begin_time, String end_time) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("beginHealthDate", begin_time);
            request_json.put("endHealthDate", end_time);
            request_json.put("pageSize", "50");
            request_json.put("pageIndex", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, getdHealthDataUrl);
    }


    /**
     * @param begin_time 开始时间
     * @param end_time   结束时间
     * @param user_id    结束时间
     * @return
     */
    public static RequestInfo getdHealthData(String begin_time, String end_time, String user_id) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", user_id);
            request_json.put("beginHealthDate", begin_time);
            request_json.put("endHealthDate", end_time);
            request_json.put("pageSize", "50");
            request_json.put("pageIndex", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, getdHealthDataUrl);
    }


    /**
     * @param id 数据ID
     * @return
     */
    public static RequestInfo getdHealthPpgEcgData(String id) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("id", id);
            request_json.put("userId", BaseApplication.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, getdHealthEcgPpgDataUrl);
    }


    //=====================================好友相关============================================

    /**
     * 获取好友列表
     *
     * @return
     */
    public static RequestInfo getFriendsList() {
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getFriendsListUrl);
    }


    /**
     * 关键字搜索好友
     *
     * @return
     */
    public static RequestInfo searchFriends(String friend_uid) {
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("friendUserId", friend_uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, searchFriendsdUrl);
    }

    /**
     * @param friend_uid  被添加者ID
     * @param request_msg 消息
     * @return
     */
    public static RequestInfo addFriends(String friend_uid, String request_msg) {
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("friendUserId", friend_uid);
            request_json.put("reqMsg", request_msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, addFriendsdUrl);
    }

    /**
     * 好友添加申请列表
     *
     * @return
     */
    public static RequestInfo requestFriendsList() {
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, requestFriendsListUrl);
    }

    /**
     * 处理好友数据
     *
     * @param data_id
     * @param friend_id
     * @param frend_state
     * @return
     */
    public static RequestInfo handleFriend(String data_id, String friend_id, String frend_state) {
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("id", data_id);
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("friendUserId", friend_id);
            request_json.put("friendStatus", frend_state);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, handleFriendUrl);
    }

    /**
     * 修改备注
     *
     * @param data_id
     * @param friend_id
     * @param rename
     * @return
     */
    public static RequestInfo handleFriendToRemarks(String data_id, String friend_id, String rename) {
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("id", data_id);
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("friendUserId", friend_id);
            request_json.put("friendStatus", "4");
            request_json.put("rename", rename);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, handleFriendUrl);
    }


    //============================APP相关=========================

    /**
     * 获取APP升级信息
     *
     * @return
     */
    public static RequestInfo getAppUpdateInfo(Context context) {
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("appName", context.getString(R.string.app_name));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getAppUpdateInfoUrl);
    }

    /**
     * 意见反馈
     *
     * @param mContext
     * @param feedback_content
     * @param feedback_email
     * @return
     */
    public static RequestInfo feedback(Context mContext, String feedback_content, String feedback_email, int type) {
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("feedbackContent", feedback_content);
            request_json.put("feedbackEmail", feedback_email);
            request_json.put("phoneModel", "Android");
            request_json.put("appMsg", MyUtils.getAppName() + "_" + MyUtils.getAppInfo());
            request_json.put("phoneSystem", MyUtils.getPhoneModel());
            request_json.put("appId", "02");
            if (type == 3) {
                request_json.put("feekBackType", type);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, feedbackUrl);
    }

    //=====================================手环相关============================================

    /**
     * 获取固件升级信息
     *
     * @param device_model
     * @return
     */
    public static RequestInfo getDeviceUpdateInfo(String device_model, String upload_version, int firmware_Platform) {
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("deviceType", device_model);
            request_json.put("versionBefore", upload_version);
            request_json.put("firmwarePlatform", firmware_Platform);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getDeviceUpdateInfoUrl);

    }

    /**
     * 上传绑定设备信息
     *
     * @return
     */
    public static RequestInfo bindDeviceInfo(Context context, boolean isUpload) {
        JSONObject request_json = new JSONObject();
        JSONObject request_deviceInfo_json = new JSONObject();
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("deviceMac", mBleDeviceTools.get_ble_mac());
            request_json.put("deviceName", mBleDeviceTools.get_ble_name());
            request_json.put("deviceType", mBleDeviceTools.get_ble_device_type());
            request_json.put("deviceVersion", mBleDeviceTools.get_ble_device_version());
            request_json.put("deviceIdentifier", "");
            request_json.put("phoneSystemType", "2");
            request_json.put("phoneType", MyUtils.getPhoneModel());
            request_json.put("phoneSystemVersion", MyUtils.getOsVersion());
            request_json.put("phoneMac", "");


            request_deviceInfo_json.put("deviceName", mBleDeviceTools.get_ble_name());
            request_deviceInfo_json.put("deviceMac", mBleDeviceTools.get_ble_mac());
            request_deviceInfo_json.put("deviceVersion", mBleDeviceTools.get_ble_device_version());

            String version_name = BleTools.getDeviceVersionName(mBleDeviceTools);
            if (!JavaUtil.checkIsNull(version_name)) {
                request_deviceInfo_json.put("deviceFullname", version_name);
            } else {
                request_deviceInfo_json.put("deviceFullname", "");
            }
            request_deviceInfo_json.put("ecgFrequency", String.valueOf(mBleDeviceTools.get_ecg_frequency()));
            request_deviceInfo_json.put("ppgFrequency", String.valueOf(mBleDeviceTools.get_ppg_frequency()));
            request_deviceInfo_json.put("ecgDetect", mBleDeviceTools.get_is_support_ecg());
            request_deviceInfo_json.put("bpDetect", mBleDeviceTools.get_is_support_blood());


            if (mBleDeviceTools.get_is_support_ppg() == 1) {
                request_deviceInfo_json.put("hrDetect", 1);
            } else {
                request_deviceInfo_json.put("hrDetect", 0);
            }

            request_deviceInfo_json.put("continuousHr", mBleDeviceTools.get_is_support_persist_heart());

            request_deviceInfo_json.put("stepType", String.valueOf(mBleDeviceTools.get_step_algorithm_type()));
            request_deviceInfo_json.put("pushType", String.valueOf(mBleDeviceTools.get_notiface_type()));

            if (mBleDeviceTools.get_is_support_wx_sport()) {
                request_deviceInfo_json.put("wechatSport", 1);
            } else {
                request_deviceInfo_json.put("wechatSport", 0);
            }

            request_deviceInfo_json.put("themeSet", mBleDeviceTools.get_theme_count());
            if (mBleDeviceTools.get_is_support_screen()) {
                request_deviceInfo_json.put("screenTimeSet", 1);
            } else {
                request_deviceInfo_json.put("screenTimeSet", 0);
            }

            if (mBleDeviceTools.get_is_weather()) {
                request_deviceInfo_json.put("weatherPush", 1);
            } else {
                request_deviceInfo_json.put("weatherPush", 0);
            }


            if (mBleDeviceTools.get_is_screen_saver()) {
                request_deviceInfo_json.put("screenProtection", 1);
            } else {
                request_deviceInfo_json.put("screenProtection", 0);
            }

            request_deviceInfo_json.put("screenFont", mBleDeviceTools.get_screensaver_resolution_width());
            request_deviceInfo_json.put("screenWidth", mBleDeviceTools.get_screensaver_resolution_width());
            request_deviceInfo_json.put("screenHeight", mBleDeviceTools.get_screensaver_resolution_height());
            request_deviceInfo_json.put("screenTimeWidth", mBleDeviceTools.get_screensaver_time_width());
            request_deviceInfo_json.put("screenTimeHeight", mBleDeviceTools.get_screensaver_time_height());
            request_deviceInfo_json.put("screenType", mBleDeviceTools.get_screen_shape());

            if (mBleDeviceTools.get_is_scanf_type()) {
                request_deviceInfo_json.put("scanningMode", 1);
            } else {
                request_deviceInfo_json.put("scanningMode", 0);
            }


            if (mBleDeviceTools.get_device_is_cycle()) {
                request_deviceInfo_json.put("physiologicalCycle", 1);
            } else {
                request_deviceInfo_json.put("physiologicalCycle", 0);
            }

            request_deviceInfo_json.put("offLineSport", mBleDeviceTools.get_device_off_sport_type());


            if (mBleDeviceTools.get_device_is_sleep_time()) {
                request_deviceInfo_json.put("sleepTimeQuantum", 1);
            } else {
                request_deviceInfo_json.put("sleepTimeQuantum", 0);
            }

            if (mBleDeviceTools.get_device_is_not_disrub()) {
                request_deviceInfo_json.put("disturbTimeQuantum", 1);
            } else {
                request_deviceInfo_json.put("disturbTimeQuantum", 0);
            }

            if (mBleDeviceTools.get_device_is_interval_hr()) {
                request_deviceInfo_json.put("hrInterval", 1);
            } else {
                request_deviceInfo_json.put("hrInterval", 0);
            }

            if (mBleDeviceTools.get_device_is_ppg_hr_jiaozhun()) {
                request_deviceInfo_json.put("ppgCalibration", 1);
            } else {
                request_deviceInfo_json.put("ppgCalibration", 0);
            }

            request_deviceInfo_json.put("appPushType", mBleDeviceTools.get_device_notice_type());

            if (mBleDeviceTools.get_device_is_distance_target()) {
                request_deviceInfo_json.put("supportTargetDistance", 1);
            } else {
                request_deviceInfo_json.put("supportTargetDistance", 0);
            }

            if (mBleDeviceTools.get_device_is_calorie_target()) {
                request_deviceInfo_json.put("supportTargetCalorie", 1);
            } else {
                request_deviceInfo_json.put("supportTargetCalorie", 0);
            }

            if (mBleDeviceTools.get_device_temperature_unit()) {
                request_deviceInfo_json.put("temperatureUnitSet", 1);
            } else {
                request_deviceInfo_json.put("temperatureUnitSet", 0);
            }

            if (mBleDeviceTools.get_device_dont_disturb_is_clock()) {
                request_deviceInfo_json.put("clockValid", 1);
            } else {
                request_deviceInfo_json.put("clockValid", 0);
            }

            if(isUpload){
                request_deviceInfo_json.put("deviceUnixTime", System.currentTimeMillis());
            }

            if (mBleDeviceTools.get_device_is_music_transmission()) {
                request_deviceInfo_json.put("deviceAudio", 1);
            } else {
                request_deviceInfo_json.put("deviceAudio", 0);
            }

            request_deviceInfo_json.put("appVersion", MyUtils.getAppInfo());
            request_deviceInfo_json.put("appName", MyUtils.getAppName());

            request_json.put("deviceInfo", request_deviceInfo_json);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, uploadBindDevice);
    }


    /**
     * 上传解绑设备信息
     *
     * @return
     */
    public static RequestInfo unbindDeviceInfo() {
        JSONObject request_json = new JSONObject();
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("deviceMac", mBleDeviceTools.get_ble_mac());
//            request_json.put("deviceName", mBleDeviceTools.get_ble_name());
//            request_json.put("deviceType", mBleDeviceTools.get_ble_device_type());
//            request_json.put("deviceVersion", mBleDeviceTools.get_ble_device_version());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, unbindDevice);
    }

    //====================好友详情============ID开放出来==============

    /**
     * 获取运动数据
     *
     * @param begin_time 开始时间
     * @param end_time   结束时间
     */
    public static RequestInfo getFriendSportListData(String begin_time, String end_time, String user_id) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", user_id);
            request_json.put("sportDateB", begin_time);
            request_json.put("sportDateE", end_time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, getSportListDataUrl);
    }

    /**
     * 获取睡眠数据
     *
     * @param begin_time 开始时间
     * @param end_time   结束时间
     */
    public static RequestInfo getSleepListData(String begin_time, String end_time, String user_id) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", user_id);
            request_json.put("sleepDateB", begin_time);
            request_json.put("sleepDateE", end_time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, getSleepListDataUrl);
    }

    //====================主题相关===================

    /**
     * 获取主题列表
     *
     * @return
     */
    public static RequestInfo getThemePageList(int device_width, int device_height, int device_shape,
                                               boolean device_is_heart, int it_bin_size) {
        JSONObject request_json = new JSONObject();
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        try {
            request_json.put("deviceWidth", device_width);
            request_json.put("deviceHeight", device_height);
            request_json.put("deviceShape", device_shape);
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("productNo", mBleDeviceTools.get_ble_device_type());
            if (!device_is_heart) {
                request_json.put("deviceIsHeart", 0);
            }
            request_json.put("ltBinSize", it_bin_size);
            request_json.put("pageIndex", 1);
            request_json.put("pageSize", 50);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getThemePageList);
    }

    /**
     * 获取主题列表
     *
     * @return
     */
    public static RequestInfo getThemeFile(int themeId, String binFileName) {
        JSONObject request_json = new JSONObject();
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        try {
            request_json.put("themeId", themeId);
            request_json.put("binFileName", binFileName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getThemeFile);
    }

    //====================音频相关===================

    /**
     * 获取音频列表
     *
     * @return
     */
    public static RequestInfo getMusicPageList() {
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("pageIndex", 1);
            request_json.put("pageSize", 30);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getMusicPageList);
    }

    //====================血氧相关===================

    /**
     * 上传连续血氧数据
     *
     * @return
     */
    public static RequestInfo uploadContinuitySpo2Data(ArrayList<ContinunitySpo2Data> spo2_data_list) {


        if (spo2_data_list == null || spo2_data_list.size() == 0) {
            return null;
        }

        JSONArray jsonArray = ContinunitySpo2Data.getContinuityTempDataListData(spo2_data_list);

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("spoList", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, uploadContinunitySpo2Url);


    }

    /**
     * 获取连续血氧天数据
     *
     * @param date 日期
     */
    public static RequestInfo getContinuitySpo2Data(String date) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("spoDate", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getContinuitySpo2DataUrl);
    }


    /**
     * 获取连续血氧列表数据
     *
     * @param time 日期
     */
    public static RequestInfo getContinuitySpo2ListData(String time) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("spoDateB", time);
            request_json.put("spoDateE", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, getContinuitySpo2ListDataUrl);
    }

    /**
     * 上传离线血氧列表数据
     *
     * @param data_list 开始时间
     */
    public static RequestInfo uploadMeasureSpo2Data(ArrayList<MeasureSpo2Data> data_list) {

        if (data_list == null || data_list.size() == 0) {
            return null;
        }

        JSONArray jsonArray = MeasureSpo2Data.getMeasureSpo2DataListData(data_list);

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("spoMeasureList", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, uploadMeasureSpo2Url);
    }


    /**
     * 获取测量血氧列表数据
     *
     * @param date 日期
     */
    public static RequestInfo getMeasureSpo2ListData(String date) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("spoDateB", date);
            request_json.put("spoDateE", date);
            request_json.put("pageSize", "50");
            request_json.put("pageIndex", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, getMeasureSpo2Url);
    }


    //====================体温相关===================

    /**
     * 上传连续体温数据
     *
     * @return
     */
    public static RequestInfo uploadContinuityTempData(ArrayList<ContinunityTempData> temp_data_list) {

        if (temp_data_list == null || temp_data_list.size() == 0) {
            return null;
        }

        JSONArray jsonArray = ContinunityTempData.getContinuityTempDataListData(temp_data_list);

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("temperatureList", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, uploadContinunityTempUrl);


    }


    /**
     * 获取连续体温天数据
     *
     * @param date 日期
     */
    public static RequestInfo getContinuityTempData(String date) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("temperatureDate", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, getContinuityTempDataUrl);
    }

    /**
     * 获取连续体温数据
     *
     * @param time 开始时间
     * @param time 结束时间
     */
    public static RequestInfo getContinuityTempListData(String time) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("temperatureDateB", time);
            request_json.put("temperatureDateE", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, getContinuityTempListDataUrl);
    }


    /**
     * 上传离线体温列表数据
     *
     * @param data_list 开始时间
     */
    public static RequestInfo uploadMeasureTempData(ArrayList<MeasureTempData> data_list) {

        if (data_list == null || data_list.size() == 0) {
            return null;
        }

        JSONArray jsonArray = MeasureTempData.getContinuitySpo2DataListData(data_list);

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("temperatureMeasureList", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, uploadMeasureTempUrl);
    }


    /**
     * 获取连续体温列表数据
     *
     * @param time 日期
     */
    public static RequestInfo getMeasureTempListData(String time) {

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("temperatureDateB", time);
            request_json.put("temperatureDateE", time);
            request_json.put("pageSize", "50");
            request_json.put("pageIndex", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, getMeasureTempUrl);
    }

    private static void putCommonJson(JSONObject request_json, Context context) {
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("phoneSystemType", 2);// android 2 ios 1
            request_json.put("phoneSystemLanguage", AppUtils.getLanguageFullName(context));
            request_json.put("imei", MyUtils.getIMEI(context));
            request_json.put("appVersion", MyUtils.getAppInfo());
            request_json.put("appName", context.getResources().getString(R.string.app_name));
            request_json.put("phoneType", MyUtils.getPhoneModel());
            request_json.put("phoneSystemVersion", MyUtils.getOsVersion());
            request_json.put("phoneMac", SysUtils.getLocalMacAddressFromIp());
            request_json.put("phoneName", SysUtils.getDeviceName());
            request_json.put("idfv", "");
            request_json.put("appUnix", String.valueOf(System.currentTimeMillis()));
            request_json.put("country", HomeActivity.country);
            request_json.put("province", HomeActivity.province);
            request_json.put("city", HomeActivity.cityName);
            request_json.put("internetType", SysUtils.getNetWorkType(context));
            request_json.put("simType", SysUtils.getSubscriptionOperatorType(context));
            if (HomeActivity.phoneLon != 0.0 || HomeActivity.phoneLat != 0.0) {
                request_json.put("longitude", HomeActivity.phoneLon);
                request_json.put("latitude", HomeActivity.phoneLat);
            }
            request_json.put("ip", SysUtils.getIPAddress(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static RequestInfo uploadAppInfo(Context context) {
        JSONObject request_json = new JSONObject();
        try {
            putCommonJson(request_json, context);
            request_json.put("phoneSystemArea", context.getResources().getConfiguration().locale.getDisplayCountry(Locale.SIMPLIFIED_CHINESE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, uploadAppInfo);
    }

    public static RequestInfo uploadErrorInfo(Context context, String errorText) {
        JSONObject request_json = new JSONObject();
        try {
            putCommonJson(request_json, context);
            request_json.put("errorText", errorText);
            request_json.put("errorType", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, uploadErrorInfo);
    }

    public static RequestInfo getSystemPermissionImageUrl(String module, String phoneBrand) {
        JSONObject request_json = new JSONObject();
        try {
            if (Locale.getDefault().getLanguage().contains("zh")) {
                request_json.put("languageCode", "2");
            } else {
                request_json.put("languageCode", "1");
            }
            request_json.put("module", module);
            request_json.put("phoneBrand", phoneBrand);
            request_json.put("app", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getSystemPermissionImageUrl);
    }

    public static RequestInfo getSupportLanguage() {
        JSONObject request_json = new JSONObject();
        return new RequestInfo(request_json, getSupportLanguage);
    }

    public static RequestInfo getModuleList(String languageCode) {
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("languageCode", languageCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getModuleList);
    }

    public static RequestInfo getHtmlUrl(String languageCode, int moduleId) {
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("languageCode", languageCode);
            request_json.put("moduleId", moduleId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getNotesList);
    }

    public static String compress(String str) throws IOException {
        if (null == str || str.length() <= 0) {
            return str;
        }
        // 创建一个新的输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 使用默认缓冲区大小创建新的输出流
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        // 将字节写入此输出流
        gzip.write(str.getBytes("utf-8")); // 因为后台默认字符集有可能是GBK字符集，所以此处需指定一个字符集
        gzip.close();
        // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
        return out.toString("ISO-8859-1");
    }

    public static RequestInfo postGpsSport(List<SportModleInfo> sportModleInfoList) {
        JSONObject request_json = null;
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < sportModleInfoList.size(); i++) {
                SportModleInfo mSportModleInfo = sportModleInfoList.get(i);
                JSONObject mJSONObject = new JSONObject();
                mJSONObject.put("userId", Long.parseLong(BaseApplication.getUserId()));
                if (TextUtils.isEmpty(mSportModleInfo.getMap_data())) {
                    mJSONObject.put("mapData", "");
                } else {
                    String mapData = mSportModleInfo.getMap_data();
                    mapData = compress(mapData);
                    mJSONObject.put("mapData", AESUtils.encrypt(mapData, "wo.szzhkjyxgs.20"));
                }

                mJSONObject.put("calorie", mSportModleInfo.getCalorie());
                mJSONObject.put("distance", mSportModleInfo.getDisance());
                mJSONObject.put("totalStep", mSportModleInfo.getTotal_step());
                mJSONObject.put("sportDuration", mSportModleInfo.getSport_duration());
                mJSONObject.put("speed", mSportModleInfo.getSpeed());
                mJSONObject.put("heart", mSportModleInfo.getHeart());
                mJSONObject.put("sportType", mSportModleInfo.getSport_type());
                mJSONObject.put("uiType", mSportModleInfo.getUi_type());
                mJSONObject.put("mapType", mSportModleInfo.getMap_type() == null ? 0 : mSportModleInfo.getMap_type());
                mJSONObject.put("appUnixTime", System.currentTimeMillis());
                mJSONObject.put("appName", "3+ PRO");
                mJSONObject.put("appVersion", MyUtils.getAppInfo());
                mJSONObject.put("sportBeginUnixTime", mSportModleInfo.getRecordPointIdTime());
                mJSONObject.put("sportEndUnixTime", mSportModleInfo.getRecordPointIdTime() + 1000 * Long.parseLong(mSportModleInfo.getSport_duration()));
                mJSONObject.put("queryUnixTime", mSportModleInfo.getRecordPointIdTime());
                mJSONObject.put("timeZone", mSportModleInfo.getRecordPointTimeZone());
                jsonArray.put(mJSONObject);
            }
            request_json = new JSONObject();
            try {
                request_json.put("gpsList", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, postGpsSport);
    }

    public static RequestInfo postRecordPointList(List<SportModleInfo> sportModleInfoList) {
        JSONObject request_json = null;
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < sportModleInfoList.size(); i++) {
                SportModleInfo mSportModleInfo = sportModleInfoList.get(i);
                if (TextUtils.isEmpty(mSportModleInfo.getRecordPointSportData())) {
                    continue;
                }
                JSONObject mJSONObject = new JSONObject();
                mJSONObject.put("userId", Long.parseLong(BaseApplication.getUserId()));
                mJSONObject.put("deviceMac", mSportModleInfo.getDeviceMac());
                mJSONObject.put("queryUnixTime", mSportModleInfo.getRecordPointIdTime());
                mJSONObject.put("timeZone", mSportModleInfo.getRecordPointTimeZone());
                mJSONObject.put("appName", "3+ PRO");
                mJSONObject.put("appVersion", MyUtils.getAppInfo());
                mJSONObject.put("deviceUnixTime", mSportModleInfo.getWarehousing_time());
                mJSONObject.put("appUnixTime", System.currentTimeMillis());
                mJSONObject.put("recordPointDataId", mSportModleInfo.getRecordPointDataId());
                mJSONObject.put("recordPointVersion", mSportModleInfo.getRecordPointVersion());
                mJSONObject.put("recordPointTypeDescription", mSportModleInfo.getRecordPointTypeDescription());
                mJSONObject.put("recordPointSportType", mSportModleInfo.getRecordPointSportType());
                mJSONObject.put("recordPointEncryption", mSportModleInfo.getRecordPointEncryption());
                mJSONObject.put("recordPointDataValid1", mSportModleInfo.getRecordPointDataValid1());
                mJSONObject.put("recordPointDataValid2", mSportModleInfo.getRecordPointDataValid2());

                if (TextUtils.isEmpty(mSportModleInfo.getRecordPointSportData())) {
                    mJSONObject.put("recordPointSportData", "");
                } else {
                    String recordPointSportData = mSportModleInfo.getRecordPointSportData();
                    recordPointSportData = compress(recordPointSportData);
                    mJSONObject.put("recordPointSportData", AESUtils.encrypt(recordPointSportData, "wo.szzhkjyxgs.20"));
                }

                if (TextUtils.isEmpty(mSportModleInfo.getMap_data())) {
                    mJSONObject.put("mapData", "");
                } else {
                    String mapData = mSportModleInfo.getMap_data();
                    mapData = compress(mapData);
                    mJSONObject.put("mapData", AESUtils.encrypt(mapData, "wo.szzhkjyxgs.20"));
                }

                mJSONObject.put("reportEncryption", mSportModleInfo.getReportEncryption());
                mJSONObject.put("reportDataValid1", mSportModleInfo.getReportDataValid1());
                mJSONObject.put("reportDataValid2", mSportModleInfo.getReportDataValid2());
                mJSONObject.put("reportDataValid3", mSportModleInfo.getReportDataValid3());
                mJSONObject.put("reportDataValid4", mSportModleInfo.getReportDataValid4());
                mJSONObject.put("reportSportStartTime", mSportModleInfo.getReportSportStartTime());
                mJSONObject.put("reportSportEndTime", mSportModleInfo.getReportSportEndTime());
                mJSONObject.put("reportDuration", mSportModleInfo.getReportDuration());
                mJSONObject.put("reportDistance", mSportModleInfo.getReportDistance());
                mJSONObject.put("reportCal", mSportModleInfo.getReportCal());
                mJSONObject.put("reportFastPace", mSportModleInfo.getReportFastPace());
                mJSONObject.put("reportSlowestPace", mSportModleInfo.getReportSlowestPace());
                mJSONObject.put("reportFastSpeed", mSportModleInfo.getReportFastSpeed());
                mJSONObject.put("reportTotalStep", mSportModleInfo.getReportTotalStep());
                mJSONObject.put("reportMaxStepSpeed", mSportModleInfo.getReportMaxStepSpeed());
                mJSONObject.put("reportAvgHeart", mSportModleInfo.getReportAvgHeart());
                mJSONObject.put("reportMaxHeart", mSportModleInfo.getReportMaxHeart());
                mJSONObject.put("reportMinHeart", mSportModleInfo.getReportMinHeart());
                mJSONObject.put("reportCumulativeRise", mSportModleInfo.getReportCumulativeRise());
                mJSONObject.put("reportCumulativeDecline", mSportModleInfo.getReportCumulativeDecline());
                mJSONObject.put("reportAvgHeight", mSportModleInfo.getReportAvgHeight());
                mJSONObject.put("reportMaxHeight", mSportModleInfo.getReportMaxHeight());
                mJSONObject.put("reportMinHeight", mSportModleInfo.getReportMinHeight());
                mJSONObject.put("reportTrainingEffect", mSportModleInfo.getReportTrainingEffect());
                mJSONObject.put("reportMaxOxygenIntake", mSportModleInfo.getReportMaxOxygenIntake());
                mJSONObject.put("reportEnergyConsumption", mSportModleInfo.getReportEnergyConsumption());
                mJSONObject.put("reportRecoveryTime", mSportModleInfo.getReportRecoveryTime());
                mJSONObject.put("reportHeartLimitTime", mSportModleInfo.getReportHeartLimitTime());
                mJSONObject.put("reportHeartAnaerobic", mSportModleInfo.getReportHeartAnaerobic());
                mJSONObject.put("reportHeartAerobic", mSportModleInfo.getReportHeartAerobic());
                mJSONObject.put("reportHeartFatBurning", mSportModleInfo.getReportHeartFatBurning());
                mJSONObject.put("reportHeartWarmUp", mSportModleInfo.getReportHeartWarmUp());
                mJSONObject.put("gpsDataValid1", mSportModleInfo.getReportGpsValid1());
                mJSONObject.put("gpsEncryption", mSportModleInfo.getReportGpsEncryption());
//                mJSONObject.put("gpsUnixDatas", mSportModleInfo.getRecordGpsTime());
                mJSONObject.put("numberOfSwims", mSportModleInfo.getReportTotalSwimNum());
                mJSONObject.put("description", mSportModleInfo.getReportSwimStyle());
                mJSONObject.put("maximumStrokeFrequency", mSportModleInfo.getReportMaxSwimFrequency());
                mJSONObject.put("numberOfTurns", mSportModleInfo.getReportFaceAboutNum());
                mJSONObject.put("averageSwolf", mSportModleInfo.getReportAvgSwolf());
                mJSONObject.put("bestSwolf", mSportModleInfo.getReportOptimalSwolf());
                mJSONObject.put("poolWidth", mSportModleInfo.getReportPoolWidth());
                jsonArray.put(mJSONObject);
            }
            request_json = new JSONObject();
            try {
                request_json.put("recordPointList", jsonArray);
                if (jsonArray.length() == 0) {
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, postRecordPointList);
    }

    public static RequestInfo getMoreSportData(String date) {
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", Long.parseLong(BaseApplication.getUserId()));
            request_json.put("sportDate", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getMoreSportData);
    }

    public static RequestInfo getMoreSportDataDetail(int dataType, Long id) {
        JSONObject request_json = new JSONObject();
        try {
            if (dataType == 0) {
                dataType = 1;
            } else if (dataType == 1) {
                dataType = 2;
            }
            request_json.put("dataType", dataType);
            request_json.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getMoreSportDataDetail);
    }

    public static RequestInfo getIsUploadMoreSport(int type) {
        JSONObject request_json = new JSONObject();
        try {
            switch (type) {
                case 1:
                    request_json.put("key", "GPSDATA");
                    break;
                case 2:
                    request_json.put("key", "RECORDPOINTDATA");
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getIsUploadMoreSport);
    }


    public static RequestInfo syncWatchTime(int type) {
        JSONObject request_json = new JSONObject();

        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("deviceMac", mBleDeviceTools.get_ble_mac());
            request_json.put("deviceVersion", mBleDeviceTools.get_ble_device_version());
            request_json.put("deviceType", mBleDeviceTools.get_ble_device_type());
            request_json.put("dataType", type);
            request_json.put("appUnixTime", String.valueOf(System.currentTimeMillis()));
            request_json.put("phoneSystemType", 2);
            request_json.put("phoneType", MyUtils.getPhoneModel());
            request_json.put("phoneSystemVersion", android.os.Build.VERSION.RELEASE);
            request_json.put("appVersion", MyUtils.getAppInfo());
            request_json.put("appName", MyUtils.getAppName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new RequestInfo(request_json, syncWatchTime);
    }

    public static RequestInfo getMainDialList() {
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("deviceWidth", mBleDeviceTools.get_device_theme_resolving_power_width());
            request_json.put("deviceHeight", mBleDeviceTools.get_device_theme_resolving_power_height());
            request_json.put("deviceShape", mBleDeviceTools.get_device_theme_shape());
            if (!mBleDeviceTools.get_device_theme_is_support_heart()) {
                request_json.put("deviceIsHeart", 0);
            }
            request_json.put("ltBinSize", mBleDeviceTools.get_device_theme_available_space());
            request_json.put("productNo", mBleDeviceTools.get_ble_device_type());
//            request_json.put("productVersion", deviceWidth);
            request_json.put("userId", BaseApplication.getUserId());

//            if (Locale.getDefault().getLanguage().contains("zh")) {
//                request_json.put("languageCode", "1");
//            } else {
            request_json.put("languageCode", "0");
//            }
            request_json.put("clockDialDataFormat", mBleDeviceTools.getClockDialDataFormat());
            request_json.put("clockDialDataGenerationMode", mBleDeviceTools.getClockDialGenerationMode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getMainDialList);
    }

    public static RequestInfo getMoreDialPageList(int pageNum, int dialTypeId) {
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("deviceWidth", mBleDeviceTools.get_device_theme_resolving_power_width());
            request_json.put("deviceHeight", mBleDeviceTools.get_device_theme_resolving_power_height());
            request_json.put("deviceShape", mBleDeviceTools.get_device_theme_shape());
            if (!mBleDeviceTools.get_device_theme_is_support_heart()) {
                request_json.put("deviceIsHeart", 0);
            }
            request_json.put("ltBinSize", mBleDeviceTools.get_device_theme_available_space());
            request_json.put("productNo", mBleDeviceTools.get_ble_device_type());
//            request_json.put("productVersion", deviceWidth);
            request_json.put("userId", BaseApplication.getUserId());

//            if (Locale.getDefault().getLanguage().contains("zh")) {
//                request_json.put("languageCode", "1");
//            } else {
            request_json.put("languageCode", "0");
//            }
            request_json.put("clockDialDataFormat", mBleDeviceTools.getClockDialDataFormat());
            request_json.put("clockDialDataGenerationMode", mBleDeviceTools.getClockDialGenerationMode());
            request_json.put("dialTypeId", dialTypeId);
            request_json.put("pageIndex", pageNum);
            request_json.put("pageSize", 15);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getMoreDialPageList);
    }

    public static RequestInfo getDialDetails(long dialId) {
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("userId", BaseApplication.getUserId());
            request_json.put("dialId", dialId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getDialDetails);
    }

    public static RequestInfo uploadDialDownloadRecording(long dialId, int dataType, Context context) {
        JSONObject requestJson = new JSONObject();
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        try {
            requestJson.put("userId", BaseApplication.getUserId());
            requestJson.put("dialId", dialId);
            if (mBleDeviceTools.get_device_theme_scanning_mode()) {
                requestJson.put("dialFileType", 2);
            } else {
                requestJson.put("dialFileType", 1);
            }
            requestJson.put("deviceType", mBleDeviceTools.get_ble_device_type());
            requestJson.put("dataType", dataType);
            requestJson.put("phoneSystemType", 2);
            requestJson.put("phoneSystemLanguage", AppUtils.getLanguageFullName(context));
            requestJson.put("imei", MyUtils.getIMEI(context));
            requestJson.put("appVersion", MyUtils.getAppInfo());
            requestJson.put("appName", MyUtils.getAppName());
            requestJson.put("phoneType", MyUtils.getPhoneModel());
            requestJson.put("phoneSystemVersion", android.os.Build.VERSION.RELEASE);
            requestJson.put("phoneMac", SysUtils.getLocalMacAddressFromIp());
            requestJson.put("phoneName", SysUtils.getDeviceName());
            requestJson.put("idfv", "");
            requestJson.put("appUnix", System.currentTimeMillis());
            requestJson.put("country", HomeActivity.country);
            requestJson.put("province", HomeActivity.province);
            requestJson.put("city", HomeActivity.cityName);
            requestJson.put("internetType", SysUtils.getNetWorkType(context));
            requestJson.put("simType", SysUtils.getSubscriptionOperatorType(context));
            requestJson.put("longitude", HomeActivity.phoneLon);
            requestJson.put("latitude", HomeActivity.phoneLat);
            requestJson.put("ip", SysUtils.getIPAddress(context));
            requestJson.put("phoneSystemArea", context.getResources().getConfiguration().locale.getDisplayCountry(Locale.SIMPLIFIED_CHINESE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(requestJson, uploadDialDownloadRecording);
    }

    public static RequestInfo getWeatherArea(Context context) {
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("location", mBleDeviceTools.getWeatherCityID());
            request_json.put("lang", SysUtils.getNewLanguage(context));
            request_json.put("unit", "m");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getWeatherArea);
    }

    public static RequestInfo getWeatherForecast(Context context) {
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("location", mBleDeviceTools.getWeatherCityID());
            request_json.put("lang", SysUtils.getNewLanguage(context));
            request_json.put("unit", "m");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getWeatherForecast);
    }

    public static RequestInfo getWeatherCity(Context context) {
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("location", mBleDeviceTools.getWeatherGps());
            request_json.put("lang", SysUtils.getNewLanguage(context));
            request_json.put("unit", "m");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getWeatherCity);
    }

    public static RequestInfo getWeatherCityBySearch(Context context, String city) {
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("location", city);
            request_json.put("lang", SysUtils.getNewLanguage(context));
            request_json.put("unit", "m");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getWeatherCity);
    }

    public static RequestInfo getLto() {
        JSONObject request_json = new JSONObject();
        return new RequestInfo(request_json, getLto);
    }

    public static RequestInfo uploadBindDeviceInfo(String deviceMac, String deviceName) {
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("userId", BaseApplication.getUserId());
            requestJson.put("deviceMac", deviceMac);
            requestJson.put("deviceName", deviceName);
            requestJson.put("deviceType", "0");
            requestJson.put("deviceVersion", "0");
            requestJson.put("deviceIdentifier", "0");
            requestJson.put("appVersion", MyUtils.getAppInfo());
            requestJson.put("appName", MyUtils.getAppName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(requestJson, uploadBindDevice);
    }

    public static RequestInfo unBindDeviceInfo(String deviceMac) {
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("userId", BaseApplication.getUserId());
            requestJson.put("deviceMac", deviceMac);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(requestJson, unbindDevice);
    }

    public static RequestInfo downLoadBindDevice() {
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("userId", BaseApplication.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(requestJson, downLoadBindDevice);
    }

    public static RequestInfo moreDialPageByProductList(int pageNum, int dialTypeId) {
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("productNo", mBleDeviceTools.get_device_theme_resolving_power_width());
            request_json.put("productVersion", mBleDeviceTools.get_device_theme_resolving_power_height());
            request_json.put("userId", BaseApplication.getUserId());
//            if (Locale.getDefault().getLanguage().contains("zh")) {
//                request_json.put("languageCode", "1");
//            } else {
            request_json.put("languageCode", "0");
//            }
            request_json.put("dialTypeId", dialTypeId);
            request_json.put("pageIndex", pageNum);
            request_json.put("pageSize", 15);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, moreDialPageByProductList);
    }

    public static RequestInfo getHomeByProductList() {
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("productNo", mBleDeviceTools.get_ble_device_type());
            request_json.put("productVersion", mBleDeviceTools.get_ble_device_version());
            request_json.put("userId", BaseApplication.getUserId());
//            if (Locale.getDefault().getLanguage().contains("zh")) {
//                request_json.put("languageCode", "1");
//            } else {
            request_json.put("languageCode", "0");
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, getHomeByProductList);
    }

    public static RequestInfo queryDialProduct() {
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        JSONObject request_json = new JSONObject();
        try {
            request_json.put("productNo", mBleDeviceTools.get_ble_device_type());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RequestInfo(request_json, queryDialProduct);
    }


}

