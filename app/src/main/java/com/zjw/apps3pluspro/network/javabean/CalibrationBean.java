package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.log.MyLog;

public class CalibrationBean {
    private static final String TAG = CalibrationBean.class.getSimpleName();
    /**
     * data : {"sleepTarget":360,"calibrationSystolic":125,"calibrationDiastolic":69,"bloodPressureLevel":1,"userId":3,"wearWay":"L","sportTarget":8000,"calibrationHeart":70}
     * result : 1
     * code : 0000
     * msg : 请求成功
     * codeMsg : 操作成功！
     */

    private DataBean data;
    private int result;
    private String code;
    private String msg;
    private String codeMsg;


    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCodeMsg() {
        return codeMsg;
    }

    public void setCodeMsg(String codeMsg) {
        this.codeMsg = codeMsg;
    }

    public static class DataBean {
        /**
         * sleepTarget : 360
         * calibrationSystolic : 125
         * calibrationDiastolic : 69
         * bloodPressureLevel : 1
         * userId : 3
         * wearWay : L
         * sportTarget : 8000
         * calibrationHeart : 70
         */

        private int sleepTarget;
        private int calibrationSystolic;
        private int calibrationDiastolic;
        private int bloodPressureLevel;
        private int userId;
        private String wearWay;
        private int sportTarget;
        private int calibrationHeart;

        public int getSleepTarget() {
            return sleepTarget;
        }

        public void setSleepTarget(int sleepTarget) {
            this.sleepTarget = sleepTarget;
        }

        public int getCalibrationSystolic() {
            return calibrationSystolic;
        }

        public void setCalibrationSystolic(int calibrationSystolic) {
            this.calibrationSystolic = calibrationSystolic;
        }

        public int getCalibrationDiastolic() {
            return calibrationDiastolic;
        }

        public void setCalibrationDiastolic(int calibrationDiastolic) {
            this.calibrationDiastolic = calibrationDiastolic;
        }

        public int getBloodPressureLevel() {
            return bloodPressureLevel;
        }

        public void setBloodPressureLevel(int bloodPressureLevel) {
            this.bloodPressureLevel = bloodPressureLevel;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getWearWay() {
            return wearWay;
        }

        public void setWearWay(String wearWay) {
            this.wearWay = wearWay;
        }

        public int getSportTarget() {
            return sportTarget;
        }

        public void setSportTarget(int sportTarget) {
            this.sportTarget = sportTarget;
        }

        public int getCalibrationHeart() {
            return calibrationHeart;
        }

        public void setCalibrationHeart(int calibrationHeart) {
            this.calibrationHeart = calibrationHeart;
        }
    }


    /**
     * 是否请求成功
     *
     * @return
     */
    public boolean isRequestSuccess() {
        if (getResult() == ResultJson.Result_success) {
            return true;
        } else {
            return false;
        }

    }


    /**
     * 个人信息获取是否成功
     *
     * @return
     */
    public int isUserSuccess() {
        //获取成功
        if (ResultJson.Code_operation_success.equals(getCode())) {
            return 1;
        }
        //没有数据
        else if (ResultJson.Code_no_data.equals(getCode())) {
            return 0;

        } else {
            return -1;
        }
    }


    public static void saveCalibrationInfo(DataBean mDataBean) {

        UserSetTools mUserSetTools = BaseApplication.getUserSetTools();


        MyLog.i(TAG, "请求回调-校准 = 解析 = mDataBean = " + mDataBean.toString());

        MyLog.i(TAG, "请求回调-用户 = 解析 = 运动目标= " + mDataBean.getSportTarget());
        MyLog.i(TAG, "请求回调-用户 = 解析 = 睡眠目标 = " + mDataBean.getSleepTarget());
        MyLog.i(TAG, "请求回调-用户 = 解析 = 血压等级 = " + mDataBean.getBloodPressureLevel());
        MyLog.i(TAG, "请求回调-用户 = 解析 = 校准心率 = " + mDataBean.getSleepTarget());
        MyLog.i(TAG, "请求回调-用户 = 解析 = 校准高压 = " + mDataBean.getSleepTarget());
        MyLog.i(TAG, "请求回调-用户 = 解析 = 校准低压 = " + mDataBean.getSleepTarget());


        int sport_target = (mDataBean.getSportTarget() > 0) ? mDataBean.getSportTarget() : DefaultVale.USER_SPORT_TARGET;
        int sleep_target = (mDataBean.getSleepTarget() > 0) ? mDataBean.getSleepTarget() : DefaultVale.USER_SLEEP_TARGET;
        int bp_level = mDataBean.getBloodPressureLevel() > 0 ? mDataBean.getBloodPressureLevel() : DefaultVale.USER_BP_LEVEL;
        int calibration_heart = (mDataBean.getCalibrationHeart() > 0) ? mDataBean.getCalibrationHeart() : DefaultVale.USER_HEART;
        int calibration_systolict = (mDataBean.getCalibrationSystolic() > 0) ? mDataBean.getCalibrationSystolic() : DefaultVale.USER_SYSTOLIC;
        int calibration_diastolic = (mDataBean.getCalibrationDiastolic() > 0) ? mDataBean.getCalibrationDiastolic() : DefaultVale.USER_DIASTOLIC;
        int wear_way = (mDataBean.getWearWay() != null && !mDataBean.getWearWay().equals("") && mDataBean.getWearWay().equals("R")) ? 0 : 1;

        try {
            mUserSetTools.set_user_exercise_target(String.valueOf(sport_target));//运动目标
        } catch (Exception e) {
            MyLog.i(TAG, "请求回调-用户 = 解析 = 运动目标异常");
            mUserSetTools.set_user_exercise_target(String.valueOf(DefaultVale.USER_SPORT_TARGET));//运动目标
        }

        try {
            mUserSetTools.set_user_sleep_target(String.valueOf(sleep_target));//睡眠目标
        } catch (Exception e) {
            MyLog.i(TAG, "请求回调-用户 = 解析 = 睡眠目标异常");
            mUserSetTools.set_user_sleep_target(String.valueOf(DefaultVale.USER_SLEEP_TARGET));//睡眠目标
        }


        try {
            mUserSetTools.set_blood_grade(bp_level);//血压等级
        } catch (Exception e) {
            MyLog.i(TAG, "请求回调-用户 = 解析 = 血压等级异常");
            mUserSetTools.set_blood_grade(DefaultVale.USER_BP_LEVEL);//血压等级
        }


        try {
            mUserSetTools.set_calibration_heart(calibration_heart);//校准心率
        } catch (Exception e) {
            MyLog.i(TAG, "请求回调-用户 = 解析 = 校准心率级异常");
            mUserSetTools.set_calibration_heart(DefaultVale.USER_HEART);//校准心率
        }
        try {
            mUserSetTools.set_calibration_systolic(calibration_systolict);//校准高压-收缩压
        } catch (Exception e) {
            MyLog.i(TAG, "请求回调-用户 = 解析 = 校准高压-收缩压异常");
            mUserSetTools.set_calibration_systolic(DefaultVale.USER_SYSTOLIC);//校准高压-收缩压
        }
        try {
            mUserSetTools.set_calibration_diastolic(calibration_diastolic);//校准低压-舒张压
        } catch (Exception e) {
            MyLog.i(TAG, "请求回调-用户 = 解析 = 校准低压-舒张压异常");
            mUserSetTools.set_calibration_diastolic(DefaultVale.USER_DIASTOLIC);//校准低压-舒张压
        }
        try {
            mUserSetTools.set_user_wear_way(wear_way);//穿戴方式
        } catch (Exception e) {
            MyLog.i(TAG, "请求回调-用户 = 解析 =穿戴方式异常");
            mUserSetTools.set_user_wear_way(DefaultVale.USER_WEARWAY);//穿戴方式
        }

    }


}
