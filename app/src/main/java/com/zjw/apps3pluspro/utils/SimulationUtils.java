package com.zjw.apps3pluspro.utils;

import android.content.Context;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sql.dbmanager.ContinuitySpo2InfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.ContinuityTempInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.HealthInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.MeasureSpo2InfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.SportModleInfoUtils;

public class SimulationUtils {
    private static final String TAG = SimulationUtils.class.getSimpleName();
    //数据库存储
    private HealthInfoUtils mHealthInfoUtils = BaseApplication.getHealthInfoUtils();
    private SportModleInfoUtils mSportModleInfoUtils = BaseApplication.getSportModleInfoUtils();
    private ContinuitySpo2InfoUtils mContinuitySpo2InfoUtils = BaseApplication.getContinuitySpo2InfoUtils();
    private MeasureSpo2InfoUtils mMeasureSpo2InfoUtils = BaseApplication.getMeasureSpo2InfoUtils();
    private static ContinuityTempInfoUtils mContinuityTempInfoUtils = BaseApplication.getContinuityTempInfoUtils();

    public static void simulationData() {
////        模拟数据
////        模拟插入睡眠数据
//        SleepInfoUtils mSleepInfoUtils = BaseApplication.getSleepInfoUtils();
//        SleepInfo mSleepInfo = new SleepInfo();
//        mSleepInfo.setUser_id(BaseApplication.getUserId());
//        mSleepInfo.setDate("2020-09-22");
////        String model_dta = "289,611,1122,1636,1730,2755,4130,4547,5090,5539,6274,8771,9826,12611,13154,13380,14882,14917";
//        String model_dta = "43618,44611,47650,47684,48066,35,962,2147,2370,3395,3810,4100,4194,5155,5378,6563,6882,7939,8418,10755,11266,12132,12290,12324";
//        mSleepInfo.setData(model_dta);
//        mSleepInfo.setSync_state("0");
//        boolean isSuccess = mSleepInfoUtils.MyUpdateData(mSleepInfo);
////        boolean isSuccess = mSleepInfoUtils.MyUpdateDataTest(mSleepInfo);
//        MyLog.i(TAG, "模拟插入睡眠数据 isSuccess = " + isSuccess);


////        //模拟插入整点心率数据
//        HeartInfoUtils mPoHeartInfoUtils = BaseApplication.getHeartInfoUtils();
//        HeartInfo mPoHeartInfo = new HeartInfo();
//        mPoHeartInfo.setUser_id(BaseApplication.getUserId());
//        mPoHeartInfo.setDate("2019-05-17");
////        mPoHeartInfo.setData("0,0,0,0,0,0,0,0,101,102,59,66,79,71,66,100,57,59,61,65,66,67,0,0");
////        mPoHeartInfo.setData("0,0,0,0,0,0,0,0,71,62,69,76,89,61,76,80,67,79,81,75,65,60,0,0");
////        mPoHeartInfo.setData("0,0,0,0,0,0,0,98,61,72,65,71,82,63,74,85,66,77,88,79,75,70,0,0");
////        mPoHeartInfo.setData("0,0,0,0,55,66,77,68,71,82,75,61,62,63,74,81,62,73,84,69,65,67,0,0");
//        mPoHeartInfo.setData("0,0,0,0,75,76,67,78,51,72,65,71,62,64,79,88,67,76,85,64,63,62,0,0");
//        mPoHeartInfo.setData_type("0");
//        boolean isSuccess = mPoHeartInfoUtils.MyUpdateData(mPoHeartInfo);
//        MyLog.i(TAG, "模拟插入整点心率数据 isSuccess = " + isSuccess);
//
//        //模拟插入连续心率数据
//        HeartInfoUtils mPoHeartInfoUtils = BaseApplication.getHeartInfoUtils();
//        HeartInfo mWoHeartInfo = new HeartInfo();
//        mWoHeartInfo.setUser_id(BaseApplication.getUserId());
//        mWoHeartInfo.setDate("2020-08-20");
////        mWoHeartInfo.setData("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,75,81,71,86,80,73,81,73,83,78,90,56,71,107,85,92,76,93,69,82,69,68,70,62,109,123,105,111,89,71,94,95,104,65,119,89,113,72,63,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
////        mWoHeartInfo.setData("0,0,0,0,0,0,0,0,0,0,0,59,70,50,50,90,90,79,102,100,105,108,99,98,98,97,96,95,94,66,68,67,65,84,93,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,59,91,62,70,72,73,70,70,70,70,70,81,83,82,82,83,82,81,83,85,87,77,79,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,76,77,78,79,59,69,66,65,63,62,0,0,0,75,81,71,86,80,73,81,73,83,78,0,0,0,92,91,0,0,0,0,0,0,0,0,0,0,75,81,71,86,80,73,81,73,83,78,90,56,71,107,85,92,76,93,69,82,69,68,70,62,109,123,105,111,89,71,94,95,104,65,119,89,113,72,63,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
//        mWoHeartInfo.setData("0,0,0,0,0,0,0,0,0,0,0,59,70,50,50,90,90,79,102,100,105,108,99,98,98,97,96,95,94,66,68,67,65,84,93,92,91,87,88,89,79,77,76,72,73,70,69,68,67,66,65,64,63,62,61,60,59,91,62,70,72,73,70,70,70,70,70,81,83,82,82,83,82,81,83,85,87,77,79,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,76,77,78,79,59,69,66,65,63,62,63,64,65,75,81,71,86,80,73,81,73,83,78,78,75,90,92,91,85,84,83,82,81,79,78,77,76,75,75,81,71,86,80,73,81,73,83,78,90,56,71,107,85,92,76,93,69,82,69,68,70,62,109,123,105,111,89,71,94,95,104,65,119,89,113,72,63,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
//        mWoHeartInfo.setData_type("1");
//        boolean isSuccess3 = mPoHeartInfoUtils.MyUpdateData(mWoHeartInfo);
//        MyLog.i(TAG, "模拟插入连续心率数据 isSuccess3 = " + isSuccess3);


////        模拟插入步数数据
//        MovementInfoUtils mMovementInfoUtils = BaseApplication.getMovementInfoUtils();
//        String sport_date = "2020-08-20";
////        String sport_data = "0,0,0,0,0,0,579,991,101,102,59,66,79,71,66,700,657,559,461,365,66,67,0,0";
////        String sport_data = "0,0,102,100,10,263,379,191,101,102,419,66,79,71,66,170,57,259,146,165,316,167,169,0";
//        String sport_data = "0,0,102,100,10,263,379,191,101,102,419,66,79,71,66,170,57,259,0,0,0,0,0,0";
////        String sport_data = "0,0,102,100,0,263,0,191,101,0,419,66,79,71,0,170,57,259,146,165,316,10,0,0";
////        String sport_data = "0,0,102,0,0,263,0,191,101,0,419,66,79,0,0,170,57,259,146,0,316,10,0,0";
////        String sport_data = "0,0,102,0,0,263,0,191,101,0,419,66,79,0,0,170,57,259,146,0,316,10,0,0";
//        String[] data = sport_data.split(",");
//        int total_step = 0;
//        for (int i = 0; i < data.length; i++) {
//            total_step += Integer.valueOf(data[i]);
//        }
//        MyLog.i(TAG, "固件返回值 = 算法2");
//        String Calory = AppUtils.GetFormat(0, BleTools.getTwoCalory(170, 70, total_step));
//        String Distance = AppUtils.GetFormat(3, BleTools.getTwoDistance(170, total_step) - 0.005f);
//        MovementInfo mMovementInfo = new MovementInfo();
//        mMovementInfo.setUser_id(BaseApplication.getUserId());
//        mMovementInfo.setDate(sport_date);
//        mMovementInfo.setData(sport_data);
//        mMovementInfo.setTotal_step(String.valueOf(total_step));
//        mMovementInfo.setCalorie(Calory);
//        mMovementInfo.setDisance(Distance);
//        mMovementInfo.setSync_state("0");
//        mMovementInfo.setStep_algorithm_type("1");
//        boolean isSuccess4 = mMovementInfoUtils.MyUpdateData(mMovementInfo);
////        boolean isSuccess4 = mMovementInfoUtils.MyUpdateDataTest(mMovementInfo);
//        MyLog.i(TAG, "模拟插入运动数据 isSuccess = " + isSuccess4);

//        StringBuffer strbuf = new StringBuffer();
//
//
//        for (int i = 0; i < Integer.MAX_VALUE-10; i++) {
//            strbuf.append("A");
//        }
//
//        String str = strbuf.toString();
//
//        MyLog.i(TAG, "模拟插入PPG测量数据 str = " + str.length());
//
////        //模拟插入ppg健康数据/
//        HealthInfo mHealthInfo = new HealthInfo();
//        mHealthInfo.setUser_id(BaseApplication.getUserId());
//        mHealthInfo.setMeasure_time("2019-05-21" + " 08:15:63");
//        mHealthInfo.setData_id("");
//        mHealthInfo.setHealth_heart("65");
//        mHealthInfo.setHealth_systolic("116");
//        mHealthInfo.setHealth_diastolic("63");
//        mHealthInfo.setHealth_ecg_report("1");
//        mHealthInfo.setEcg_data("");
//        mHealthInfo.setPpg_data("");
//        mHealthInfo.setIndex_health_index("79");
//        mHealthInfo.setIndex_body_quality("71");
//        mHealthInfo.setIndex_body_load("75");
//        mHealthInfo.setIndex_fatigue_index("49");
//        mHealthInfo.setIndex_cardiac_function("67");
//        mHealthInfo.setSensor_type("2");//支持PPG
//        mHealthInfo.setIs_suppor_bp("0");//不支持血压
//        mHealthInfo.setSync_state("1");
//        mHealthInfo.setEcg_data(str);
//
//        boolean isSuccess = mHealthInfoUtils.MyUpdateData(mHealthInfo);
//        MyLog.i(TAG, "模拟插入PPG测量数据 isSuccess = " + isSuccess);

//        //模拟插入多运动-跑步
//        SportModleInfo mSportModleInfo1 = new SportModleInfo();w
//        mSportModleInfo1.setUser_id(BaseApplication.getUserId());
//        mSportModleInfo1.setTime("2019-05-29 07:42:38");
//        mSportModleInfo1.setSport_duration("810");
//        mSportModleInfo1.setSport_type("0");
//        mSportModleInfo1.setUi_type("0");
//        mSportModleInfo1.setTotal_step("1597");
//        boolean isSuccess1 = mSportModleInfoUtils.MyUpdateData(mSportModleInfo1);
//        MyLog.i(TAG, "模拟插入多运动-跑步 isSuccess = " + isSuccess1);
//
//
//
//        //模拟插入多运动-健走
//        SportModleInfo mSportModleInfo2 = new SportModleInfo();
//        mSportModleInfo2.setUser_id(BaseApplication.getUserId());
//        mSportModleInfo2.setTime("2019-06-01 07:01:20");
//        mSportModleInfo2.setSport_duration("612");
//        mSportModleInfo2.setSport_type("1");
//        mSportModleInfo2.setUi_type("0");
//        mSportModleInfo2.setTotal_step("2127");
//        boolean isSuccess2 = mSportModleInfoUtils.MyUpdateData(mSportModleInfo2);
//        MyLog.i(TAG, "模拟插入多运动-健走 isSuccess = " + isSuccess2);
//
//
////        //模拟插入多运动-爬山
//        SportModleInfo mSportModleInfo3 = new SportModleInfo();
//        mSportModleInfo3.setUser_id(BaseApplication.getUserId());
//        mSportModleInfo3.setTime("2019-05-25 06:14:23");
//        mSportModleInfo3.setSport_duration("1201");
//        mSportModleInfo3.setSport_type("2");
//        mSportModleInfo3.setUi_type("0");
//        mSportModleInfo3.setTotal_step("4051");
//        boolean isSuccess3 = mSportModleInfoUtils.MyUpdateData(mSportModleInfo3);
//        MyLog.i(TAG, "模拟插入多运动-爬山 isSuccess3 = " + isSuccess3);
//
//         //模拟插入多运动-乒乓球
//        SportModleInfo mSportModleInfo4 = new SportModleInfo();
//        mSportModleInfo4.setUser_id(BaseApplication.getUserId());
//        mSportModleInfo4.setTime("2019-05-26 19:06:51");
//        mSportModleInfo4.setSport_duration("1031");
//        mSportModleInfo4.setSport_type("3");
//        mSportModleInfo4.setUi_type("1");
//        mSportModleInfo4.setCalorie("105");
//        boolean isSuccess4 = mSportModleInfoUtils.MyUpdateData(mSportModleInfo4);
//        MyLog.i(TAG, "模拟插入多运动-乒乓球 isSuccess = " + isSuccess4);
//
//        //模拟插入多运动-篮球
//        SportModleInfo mSportModleInfo5 = new SportModleInfo();
//        mSportModleInfo5.setUser_id(BaseApplication.getUserId());
//        mSportModleInfo5.setTime("2019-05-21 08:23:11");
//        mSportModleInfo5.setSport_duration("531");
//        mSportModleInfo5.setSport_type("4");
//        mSportModleInfo5.setUi_type("1");
//        mSportModleInfo5.setCalorie("181");
//        boolean isSuccess5 = mSportModleInfoUtils.MyUpdateData(mSportModleInfo5);
//        MyLog.i(TAG, "模拟插入多运动-篮球 isSuccess = " + isSuccess5);


//        //模拟插入多运动-足球
//        SportModleInfo mSportModleInfo6 = new SportModleInfo();
//        mSportModleInfo6.setUser_id(BaseApplication.getUserId());
//        mSportModleInfo6.setTime("2019-05-31 19:50:11");
//        mSportModleInfo6.setSport_duration("379");
//        mSportModleInfo6.setSport_type("5");
//        mSportModleInfo6.setUi_type("1");
//        mSportModleInfo6.setCalorie("219");
//        boolean isSuccess6 = mSportModleInfoUtils.MyUpdateData(mSportModleInfo6);
//        MyLog.i(TAG, "模拟插入多运动-足球 isSuccess = " + isSuccess6);


//        //模拟插入连续血氧
//        ContinuitySpo2Info mContinuitySpo2Info = new ContinuitySpo2Info();
//        mContinuitySpo2Info.setUser_id(BaseApplication.getUserId());
//        mContinuitySpo2Info.setDate("2020-04-05");
////        mContinuitySpo2Info.setData("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,95,98,98,96,90,97,98,93,98,98,99,96,97,97,96,99,96,99,99,98,99,98,97,96,99,93,95,96,89,91,94,95,97,99,98,95,96,97,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
//        mContinuitySpo2Info.setData("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,95,96,97,98,99,98,97,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,95,98,98,96,90,97,98,93,98,98,99,96,97,97,96,99,96,99,99,98,99,98,97,96,99,93,95,96,89,91,94,95,97,99,98,95,96,97,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
//        mContinuitySpo2Info.setSync_state("0");
//        boolean isSuccess1 = mContinuitySpo2InfoUtils.MyUpdateData(mContinuitySpo2Info);
//        MyLog.i(TAG, "模拟插入连续血氧 isSuccess = " + isSuccess1);
//
//        requestServerTools.uploadContinuitySpo2Data(TAG, getApplicationContext(), mContinuitySpo2InfoUtils);

////        模拟插入连续体温
//        ContinuityTempInfo mContinuityTempInfo = new ContinuityTempInfo();
//        mContinuityTempInfo.setUser_id(BaseApplication.getUserId());
//        mContinuityTempInfo.setDate("2020-09-04");
//        mContinuityTempInfo.setTemp_difference("0");
////        mContinuityTempInfo.setData("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,15,18,18,16,10,17,18,13,18,18,11,16,17,17,16,11,16,9,11,18,11,18,17,16,11,13,15,16,18,11,22,15,17,11,18,15,16,17,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
//        mContinuityTempInfo.setData("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,15,16,15,16,17,18,19,20,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,15,18,18,16,10,17,18,13,18,18,11,16,17,17,16,11,16,11,11,18,11,18,17,16,11,13,15,8,23,11,14,15,17,11,18,15,16,17,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
//        mContinuityTempInfo.setSync_state("1");
//        boolean isSuccess2 = mContinuityTempInfoUtils.MyUpdateData(mContinuityTempInfo);
//        MyLog.i(TAG, "模拟插连续体温 isSuccess = " + isSuccess2);


        //模拟 清空连续血氧
//        mContinuitySpo2InfoUtils.deleteAllData();

        //模拟 清空连续体温
//        mContinuityTempInfoUtils.deleteAllData();


        //模拟插入连续心率数据
//        HeartInfoUtils mHeartInfoUtils = BaseApplication.getHeartInfoUtils();
//
//        HeartInfo mWoHeartInfo1 = new HeartInfo();
//        mWoHeartInfo1.setUser_id(BaseApplication.getUserId());
//        mWoHeartInfo1.setDate("2020-08-10");
//        mWoHeartInfo1.setData("10,0,0,0,0,0,0,0,0,0,0,59,70,50,50,90,90,79,102,100,105,108,99,98,98,97,96,95,94,66,68,67,65,84,93,92,91,87,88,89,79,77,76,72,73,70,69,68,67,66,65,64,63,62,61,60,59,91,62,70,72,73,70,70,70,70,70,81,83,82,82,83,82,81,83,85,87,77,79,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,76,77,78,79,59,69,66,65,63,62,63,64,65,75,81,71,86,80,73,81,73,83,78,78,75,90,92,91,85,84,83,82,81,79,78,77,76,75,75,81,71,86,80,73,81,73,83,78,90,56,71,107,85,92,76,93,69,82,69,68,70,62,109,123,105,111,89,71,94,95,104,65,119,89,113,72,63,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
//        mWoHeartInfo1.setData_type("1");
//        mWoHeartInfo1.setSync_state("0");
////        boolean isSuccess1 = mHeartInfoUtils.MyUpdateData(mWoHeartInfo1);
//        boolean isSuccess1 = mHeartInfoUtils.MyUpdateDataTest(mWoHeartInfo1);
//        MyLog.i(TAG, "模拟插入连续心率数据 isSuccess1 = " + isSuccess1);
////
//        //模拟插入连续心率数据
//        HeartInfo mWoHeartInfo2 = new HeartInfo();
//        mWoHeartInfo2.setUser_id(BaseApplication.getUserId());
//        mWoHeartInfo2.setDate("2020-08-12");
//        mWoHeartInfo2.setData("20,0,0,0,0,0,0,0,0,0,0,59,70,50,50,90,90,79,102,100,105,108,99,98,98,97,96,95,94,66,68,67,65,84,93,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,59,91,62,70,72,73,70,70,70,70,70,81,83,82,82,83,82,81,83,85,87,77,79,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,76,77,78,79,59,69,66,65,63,62,0,0,0,75,81,71,86,80,73,81,73,83,78,0,0,0,92,91,0,0,0,0,0,0,0,0,0,0,75,81,71,86,80,73,81,73,83,78,90,56,71,107,85,92,76,93,69,82,69,68,70,62,109,123,105,111,89,71,94,95,104,65,119,89,113,72,63,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
//        mWoHeartInfo2.setData_type("1");
//        mWoHeartInfo2.setSync_state("0");
////        boolean isSuccess2 = mHeartInfoUtils.MyUpdateData(mWoHeartInfo2);
//        boolean isSuccess2 = mHeartInfoUtils.MyUpdateDataTest(mWoHeartInfo2);
//        MyLog.i(TAG, "模拟插入连续心率数据 isSuccess2 = " + isSuccess2);
////
//        //模拟插入连续心率数据
//        HeartInfo mWoHeartInfo3 = new HeartInfo();
//        mWoHeartInfo3.setUser_id(BaseApplication.getUserId());
//        mWoHeartInfo3.setDate("2020-08-11");
//        mWoHeartInfo3.setData("30,0,0,0,0,0,0,0,0,0,0,59,70,50,50,90,90,79,102,100,105,108,99,98,98,97,96,95,94,66,68,67,65,84,93,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,59,91,62,70,72,73,70,70,70,70,70,81,83,82,82,83,82,81,83,85,87,77,79,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,76,77,78,79,59,69,66,65,63,62,0,0,0,75,81,71,86,80,73,81,73,83,78,0,0,0,92,91,0,0,0,0,0,0,0,0,0,0,75,81,71,86,80,73,81,73,83,78,90,56,71,107,85,92,76,93,69,82,69,68,70,62,109,123,105,111,89,71,94,95,104,65,119,89,113,72,63,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
//        mWoHeartInfo3.setData_type("1");
//        mWoHeartInfo3.setSync_state("0");
////        boolean isSuccess3 = mHeartInfoUtils.MyUpdateData(mWoHeartInfo3);
//        boolean isSuccess3 = mHeartInfoUtils.MyUpdateDataTest(mWoHeartInfo3);
//        MyLog.i(TAG, "模拟插入连续心率数据 isSuccess3 = " + isSuccess3);
//
//        //模拟插入连续心率数据
//        HeartInfo mWoHeartInfo4 = new HeartInfo();
//        mWoHeartInfo4.setUser_id(BaseApplication.getUserId());
//        mWoHeartInfo4.setDate("2020-08-10");
//        mWoHeartInfo4.setData("40,0,0,0,0,0,0,0,0,0,0,59,70,50,50,90,90,79,102,100,105,108,99,98,98,97,96,95,94,66,68,67,65,84,93,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,59,91,62,70,72,73,70,70,70,70,70,81,83,82,82,83,82,81,83,85,87,77,79,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,76,77,78,79,59,69,66,65,63,62,0,0,0,75,81,71,86,80,73,81,73,83,78,0,0,0,92,91,0,0,0,0,0,0,0,0,0,0,75,81,71,86,80,73,81,73,83,78,90,56,71,107,85,92,76,93,69,82,69,68,70,62,109,123,105,111,89,71,94,95,104,65,119,89,113,72,63,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
//        mWoHeartInfo4.setData_type("1");
//        mWoHeartInfo4.setSync_state("0");
////        boolean isSuccess4 = mHeartInfoUtils.MyUpdateData(mWoHeartInfo4);
//        boolean isSuccess4 = mHeartInfoUtils.MyUpdateDataTest(mWoHeartInfo4);
//        MyLog.i(TAG, "模拟插入连续心率数据 isSuccess4 = " + isSuccess4);
    }

    public static void simulationUpdateData(Context mContext) {

//        String jsonStr = "{\"spoMeasureList\":[{\"deviceUnixTime\":\"1604735406232\",\"appVersion\":\"V1.0.25Beta1.9\",\"spoMeasureData\":\"91\",\"spoMeasureTime\":\"2020-11-07 15:49:56\",\"appName\":\"F Fit\",\"deviceMac\":\"CC:00:33:44:00:11\",\"appUnixTime\":\"1607658386155\",\"userId\":\"780669\"},{\"deviceUnixTime\":\"1604735672105\",\"appVersion\":\"V1.0.25Beta1.9\",\"spoMeasureData\":\"91\",\"spoMeasureTime\":\"2020-11-07 15:52:46\",\"appName\":\"F Fit\",\"deviceMac\":\"CC:00:33:44:00:11\",\"appUnixTime\":\"1607658386155\",\"userId\":\"780669\"},{\"deviceUnixTime\":\"1604735672107\",\"appVersion\":\"V1.0.25Beta1.9\",\"spoMeasureData\":\"98\",\"spoMeasureTime\":\"2020-11-07 15:53:38\",\"appName\":\"F Fit\",\"deviceMac\":\"CC:00:33:44:00:11\",\"appUnixTime\":\"1607658386156\",\"userId\":\"780669\"},{\"deviceUnixTime\":\"1605329216446\",\"appVersion\":\"V1.0.25Beta1.9\",\"spoMeasureData\":\"98\",\"spoMeasureTime\":\"2020-11-14 12:46:49\",\"appName\":\"F Fit\",\"deviceMac\":\"CC:00:33:44:00:11\",\"appUnixTime\":\"1607658386157\",\"userId\":\"780669\"},{\"deviceUnixTime\":\"1605521154663\",\"appVersion\":\"V1.0.25Beta1.9\",\"spoMeasureData\":\"97\",\"spoMeasureTime\":\"2020-11-16 18:05:41\",\"appName\":\"F Fit\",\"deviceMac\":\"CC:00:33:44:00:11\",\"appUnixTime\":\"1607658386158\",\"userId\":\"780669\"},{\"deviceUnixTime\":\"1605521389210\",\"appVersion\":\"V1.0.25Beta1.9\",\"spoMeasureData\":\"96\",\"spoMeasureTime\":\"2020-11-16 18:09:42\",\"appName\":\"F Fit\",\"deviceMac\":\"CC:00:33:44:00:11\",\"appUnixTime\":\"1607658386158\",\"userId\":\"780669\"},{\"deviceUnixTime\":\"1605530353311\",\"appVersion\":\"V1.0.25Beta1.9\",\"spoMeasureData\":\"98\",\"spoMeasureTime\":\"2020-11-16 20:39:07\",\"appName\":\"F Fit\",\"deviceMac\":\"CC:00:33:44:00:11\",\"appUnixTime\":\"1607658386159\",\"userId\":\"780669\"},{\"deviceUnixTime\":\"1605530793724\",\"appVersion\":\"V1.0.25Beta1.9\",\"spoMeasureData\":\"93\",\"spoMeasureTime\":\"2020-11-16 20:45:16\",\"appName\":\"F Fit\",\"deviceMac\":\"CC:00:33:44:00:11\",\"appUnixTime\":\"1607658386160\",\"userId\":\"780669\"},{\"deviceUnixTime\":\"1605530793729\",\"appVersion\":\"V1.0.25Beta1.9\",\"spoMeasureData\":\"99\",\"spoMeasureTime\":\"2020-11-16 20:46:20\",\"appName\":\"F Fit\",\"deviceMac\":\"CC:00:33:44:00:11\",\"appUnixTime\":\"1607658386160\",\"userId\":\"780669\"},{\"deviceUnixTime\":\"1605941279698\",\"appVersion\":\"V1.0.25Beta1.9\",\"spoMeasureData\":\"95\",\"spoMeasureTime\":\"2020-11-21 14:47:51\",\"appName\":\"F Fit\",\"deviceMac\":\"CC:00:33:44:00:11\",\"appUnixTime\":\"1607658386161\",\"userId\":\"780669\"},{\"deviceUnixTime\":\"1606793458918\",\"appVersion\":\"V1.0.25Beta1.9\",\"spoMeasureData\":\"98\",\"spoMeasureTime\":\"2020-12-01 11:27:47\",\"appName\":\"F Fit\",\"deviceMac\":\"CC:00:33:44:00:11\",\"appUnixTime\":\"1607658386162\",\"userId\":\"780669\"},{\"deviceUnixTime\":\"1606793458919\",\"appVersion\":\"V1.0.25Beta1.9\",\"spoMeasureData\":\"97\",\"spoMeasureTime\":\"2020-12-01 11:29:37\",\"appName\":\"F Fit\",\"deviceMac\":\"CC:00:33:44:00:11\",\"appUnixTime\":\"1607658386162\",\"userId\":\"780669\"},{\"deviceUnixTime\":\"1606793561307\",\"appVersion\":\"V1.0.25Beta1.9\",\"spoMeasureData\":\"96\",\"spoMeasureTime\":\"2020-12-01 11:32:33\",\"appName\":\"F Fit\",\"deviceMac\":\"CC:00:33:44:00:11\",\"appUnixTime\":\"1607658386163\",\"userId\":\"780669\"},{\"deviceUnixTime\":\"1606804873719\",\"appVersion\":\"V1.0.25Beta1.9\",\"spoMeasureData\":\"99\",\"spoMeasureTime\":\"2020-12-01 14:41:08\",\"appName\":\"F Fit\",\"deviceMac\":\"CC:00:33:44:00:11\",\"appUnixTime\":\"1607658386163\",\"userId\":\"780669\"},{\"deviceUnixTime\":\"1607074652066\",\"appVersion\":\"V1.0.25Beta1.9\",\"spoMeasureData\":\"99\",\"spoMeasureTime\":\"200-01-01 00:03:07\",\"appName\":\"F Fit\",\"deviceMac\":\"CC:00:33:44:00:11\",\"appUnixTime\":\"1607658386164\",\"userId\":\"780669\"},{\"deviceUnixTime\":\"1607074652069\",\"appVersion\":\"V1.0.25Beta1.9\",\"spoMeasureData\":\"98\",\"spoMeasureTime\":\"200-01-01 00:04:10\",\"appName\":\"F Fit\",\"deviceMac\":\"CC:00:33:44:00:11\",\"appUnixTime\":\"1607658386165\",\"userId\":\"780669\"}]}";
//        JSONObject request_json = null;
//
//        try {
//            //String转JSONObject
//            request_json = new JSONObject(jsonStr);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
////        RequestInfo mRequestInfo = RequestJson.uploadMeasureSpo2Data(data_list);
//        RequestInfo mRequestInfo = new RequestInfo(request_json, RequestJson.uploadMeasureSpo2Url);
//
//        MyLog.i(TAG, "数据库-上传离线血氧" + mRequestInfo.getRequestJson());
//
//        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
//                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
//
//                    @Override
//                    public void onMySuccess(JSONObject result) {
//                        // TODO Auto-generated method stub
//
//                        MyLog.i(TAG, "请求接口-上传离线血氧 result = " + result);
//
//                        MeasureSpo2ListBean mMeasureSpo2ListBean = ResultJson.MeasureSpo2ListBean(result);
//
//                        //请求成功
//                        if (mMeasureSpo2ListBean.isRequestSuccess()) {
//                            if (mMeasureSpo2ListBean.isGetSuccess() == 1) {
//                                MyLog.i(TAG, "请求接口-上传离线血氧 成功");
//
////                                boolean isSuccessHealth = mMeasureSpo2InfoUtils.MyUpdateToSync(healtInfo_list);
//
////                                MyLog.i(TAG, "数据库-上传离线血氧 同步状态 = isSuccessHealth = " + isSuccessHealth);
//                            } else if (mMeasureSpo2ListBean.isGetSuccess() == 0) {
//                                MyLog.i(TAG, "请求接口-上传离线血氧 失败");
//                            } else {
//                                MyLog.i(TAG, "请求接口-上传离线血氧 请求异常(1)");
//                            }
//                            //请求失败
//                        } else {
//                            MyLog.i(TAG, "请求接口-上传离线血氧 请求异常(0)");
//                        }
//                    }
//                    @Override
//                    public void onMyError(VolleyError arg0) {
//                        // TODO Auto-generated method stub
//                        MyLog.i(TAG, "请求接口-上传离线血氧 请求失败 = message = " + arg0.getMessage());
//                    }
//                });
    }
}
