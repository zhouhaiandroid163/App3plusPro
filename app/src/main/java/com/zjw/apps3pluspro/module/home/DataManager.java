package com.zjw.apps3pluspro.module.home;

import android.content.Context;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.module.home.sport.DeviceSportManager;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.ContinuitySpo2ListBean;
import com.zjw.apps3pluspro.network.javabean.ContinuityTempListBean;
import com.zjw.apps3pluspro.network.javabean.HealthBean;
import com.zjw.apps3pluspro.network.javabean.HeartBean;
import com.zjw.apps3pluspro.network.javabean.MeasureSpo2ListBean;
import com.zjw.apps3pluspro.network.javabean.MeasureTempListBean;
import com.zjw.apps3pluspro.network.javabean.SleepBean;
import com.zjw.apps3pluspro.network.javabean.SportBean;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.dbmanager.ContinuitySpo2InfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.ContinuityTempInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.HealthInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.HeartInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.MeasureSpo2InfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.MeasureTempInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.MovementInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.SleepInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.SportModleInfoUtils;
import com.zjw.apps3pluspro.sql.entity.ContinuitySpo2Info;
import com.zjw.apps3pluspro.sql.entity.ContinuityTempInfo;
import com.zjw.apps3pluspro.sql.entity.HealthInfo;
import com.zjw.apps3pluspro.sql.entity.HeartInfo;
import com.zjw.apps3pluspro.sql.entity.MeasureSpo2Info;
import com.zjw.apps3pluspro.sql.entity.MeasureTempInfo;
import com.zjw.apps3pluspro.sql.entity.MovementInfo;
import com.zjw.apps3pluspro.sql.entity.SleepInfo;
import com.zjw.apps3pluspro.sql.entity.SportModleInfo;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.NewTimeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by android
 * on 2020/5/12.
 */
public class DataManager {
    private static final String TAG = DataManager.class.getSimpleName();
    private static DataManager dataManager;

    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    private MovementInfoUtils mMovementInfoUtils = BaseApplication.getMovementInfoUtils();
    private SleepInfoUtils mSleepInfoUtils = BaseApplication.getSleepInfoUtils();
    private HeartInfoUtils mHeartInfoUtils = BaseApplication.getHeartInfoUtils();
    private HealthInfoUtils mHealthInfoUtils = BaseApplication.getHealthInfoUtils();
    private SportModleInfoUtils mSportModleInfoUtils = BaseApplication.getSportModleInfoUtils();

    private ContinuitySpo2InfoUtils mContinuitySpo2InfoUtils = BaseApplication.getContinuitySpo2InfoUtils();
    private ContinuityTempInfoUtils mContinuityTempInfoUtils = BaseApplication.getContinuityTempInfoUtils();
    private MeasureSpo2InfoUtils mMeasureSpo2InfoUtils = BaseApplication.getMeasureSpo2InfoUtils();
    private MeasureTempInfoUtils mMeasureTempInfoUtils = BaseApplication.getMeasureTempInfoUtils();


    public interface GetDataSuccess {
        void onSuccess(Object object);
    }

    public static DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    public void getSportDay(Context context, boolean isRequestService, GetDataSuccess getSportDaySuccess) {
        MovementInfo mMovementInfo = mMovementInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime());
        if (mMovementInfo != null) {
            getSportDaySuccess.onSuccess(mMovementInfo);
        } else {
            if (isRequestService) {
                requestSportData(getSportDaySuccess, context, MyTime.getTime());
            }
        }
    }

    private void requestSportData(final GetDataSuccess getSportDaySuccess, final Context context, final String date) {
        RequestInfo mRequestInfo = RequestJson.getSportListData(date, date);
        MyLog.i(TAG, "requestSportData = mRequestInfo = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject result) {
                        if (result != null && !result.toString().equals("")) {
                            MyLog.i(TAG, "requestSportData = result = " + result.toString());
                        }
                        SportBean mSportBean = ResultJson.SportBean(result);
                        if (mSportBean.isRequestSuccess()) {
                            if (mSportBean.isGetSportSuccess() == 1) {
                                ResultSportDataParsing(context, mSportBean, date, getSportDaySuccess);
                            } else if (mSportBean.isGetSportSuccess() == 2) {
                                SportBean.insertNullData(mMovementInfoUtils, date);
                                getSportDay(context, false, getSportDaySuccess);
                            } else {
                                getSportDaySuccess.onSuccess(null);
                            }
                        } else {
                            getSportDaySuccess.onSuccess(null);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        getSportDaySuccess.onSuccess(null);
                    }
                });
    }

    private void ResultSportDataParsing(Context context, SportBean mSportBean, String date, GetDataSuccess getSportDaySuccess) {
        if (mSportBean.getData().size() > 0) {
            MovementInfo mMovementInfo = mSportBean.getMovementInfo(mSportBean.getData().get(0));
            boolean isSuccess = mMovementInfoUtils.MyUpdateData(mMovementInfo);
        } else {
            SportBean.insertNullData(mMovementInfoUtils, date);
        }
        getSportDay(context, false, getSportDaySuccess);
    }

    public void getSleepDay(Context context, boolean isRequestService, GetDataSuccess getDataSuccess) {
        SleepInfo mSleepInfo = mSleepInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime());
        if (mSleepInfo != null) {
            getDataSuccess.onSuccess(mSleepInfo);
        } else {
            if (isRequestService) {
                requestSleepData(MyTime.getTime(), context, getDataSuccess);
            }
        }
    }

    private void ResultSleepDataParsing(SleepBean mSleepBean, String date, Context context, GetDataSuccess getDataSuccess) {
        if (mSleepBean.getData().size() > 0) {
            SleepInfo mSleepInfo = mSleepBean.getSleepInfo(mSleepBean.getData().get(0));
            boolean isSuccess = mSleepInfoUtils.MyUpdateData(mSleepInfo);
            if (!isSuccess) {
                MyLog.i(TAG, "插入睡眠表失败！");
            }
        } else {
            SleepBean.insertNullData(mSleepInfoUtils, date);
        }
        getSleepDay(context, false, getDataSuccess);
    }

    private void requestSleepData(final String date, final Context context, final GetDataSuccess getDataSuccess) {
        RequestInfo mRequestInfo = RequestJson.getSleepListData(date, date);
        MyLog.i(TAG, "requestSleepData = mRequestInfo = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject result) {
                        if (result != null && !result.toString().equals("")) {
                            MyLog.i(TAG, "requestSleepData = result = " + result.toString());
                        }
                        SleepBean mSleepBean = ResultJson.SleepBean(result);
                        //请求成功
                        if (mSleepBean.isRequestSuccess()) {
                            if (mSleepBean.isGetSleepSuccess() == 1) {
                                ResultSleepDataParsing(mSleepBean, date, context, getDataSuccess);
                            } else if (mSleepBean.isGetSleepSuccess() == 0) {
                                getDataSuccess.onSuccess(null);
                            } else if (mSleepBean.isGetSleepSuccess() == 2) {
                                SleepBean.insertNullData(mSleepInfoUtils, date);
                                getSleepDay(context, false, getDataSuccess);
                            } else {
                                getDataSuccess.onSuccess(null);
                            }
                        } else {
                            getDataSuccess.onSuccess(null);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        getDataSuccess.onSuccess(null);
                    }
                });
    }

    //=============================获取连续心率=====================

    public void getContinuousHeart(Context homeActivity, boolean isRequestService, GetDataSuccess getDataSuccess) {
        HeartInfo mHeartInfo = mHeartInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime(), "1");
        if (mHeartInfo != null) {
            getDataSuccess.onSuccess(mHeartInfo);
        } else {
            MyLog.i(TAG, "mHeartInfo = null");
            if (isRequestService) {
                requestWoheartData(MyTime.getTime(), homeActivity, getDataSuccess);
            }
        }
    }

    private void requestWoheartData(final String date, final Context context, final GetDataSuccess getDataSuccess) {
        RequestInfo mRequestInfo = RequestJson.getWoListData(date, date);
        MyLog.i(TAG, "requestWoheartData = mRequestInfo = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject result) {
                        if (result != null && !result.toString().equals("")) {
                            MyLog.i(TAG, "requestWoheartData = result = " + result.toString());
                        }
                        HeartBean mHeartBean = ResultJson.HeartBean(result);
                        if (mHeartBean.isRequestSuccess()) {
                            if (mHeartBean.isGetHeartSuccess() == 1) {
                                ResultWoHeartDataParsing(mHeartBean, date, context, getDataSuccess);
                            } else if (mHeartBean.isGetHeartSuccess() == 0) {
                                getDataSuccess.onSuccess(null);
                            } else if (mHeartBean.isGetHeartSuccess() == 2) {
                                HeartBean.insertWoNullData(mHeartInfoUtils, date);
                                getContinuousHeart(context, false, getDataSuccess);
                            } else {
                                getDataSuccess.onSuccess(null);
                            }
                        } else {
                            getDataSuccess.onSuccess(null);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        getDataSuccess.onSuccess(null);
                    }
                });
    }

    private void ResultWoHeartDataParsing(HeartBean mHeartBean, String date, Context context, GetDataSuccess getDataSuccess) {
        if (mHeartBean.getData().size() >= 1) {
            HeartInfo mHeartInfo = HeartBean.getHeartInfo(mHeartBean.getData().get(0));
            boolean isSuccess = mHeartInfoUtils.MyUpdateData(mHeartInfo);
            if (isSuccess) {
                MyLog.i(TAG, "插入连续心率表成功！");
            } else {
                MyLog.i(TAG, "插入连续心率失败！");
            }
        } else {
            HeartBean.insertWoNullData(mHeartInfoUtils, date);
        }
        getContinuousHeart(context, false, getDataSuccess);
    }

    //===================================Whole snack rate====================

    public void getWholeSnackHeart(Context context, boolean isRequestService, GetDataSuccess getDataSuccess) {
        HeartInfo mPoHeartInfo = mHeartInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime(), "0");
        if (mPoHeartInfo != null) {
            getDataSuccess.onSuccess(mPoHeartInfo);
        } else {
            if (isRequestService) {
                requestPoheartData(MyTime.getTime(), context, getDataSuccess);
            }
        }
    }

    private void requestPoheartData(final String date, final Context context, final GetDataSuccess getDataSuccess) {
        RequestInfo mRequestInfo = RequestJson.getPoListData(date, date);
        MyLog.i(TAG, "requestPoheartData = mRequestInfo = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject result) {
                        HeartBean mHeartBean = ResultJson.HeartBean(result);
                        if (mHeartBean.isRequestSuccess()) {
                            if (mHeartBean.isGetHeartSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取整点心率数据 成功");
                                ResultPoHeartDataParsing(mHeartBean, date, context, getDataSuccess);
                            } else if (mHeartBean.isGetHeartSuccess() == 0) {
                                getDataSuccess.onSuccess(null);
                            } else if (mHeartBean.isGetHeartSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-获取整点心率数据 无数据");
                                HeartBean.insertPoNullData(mHeartInfoUtils, date);
                                getWholeSnackHeart(context, false, getDataSuccess);
                            } else {
                                getDataSuccess.onSuccess(null);
                            }
                        } else {
                            getDataSuccess.onSuccess(null);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        getDataSuccess.onSuccess(null);
                    }
                });
    }

    /**
     * 解析数据
     */
    private void ResultPoHeartDataParsing(HeartBean mHeartBean, String date, Context context, GetDataSuccess getDataSuccess) {
        if (mHeartBean.getData().size() > 0) {
            HeartInfo mHeartInfo = HeartBean.getHeartInfo(mHeartBean.getData().get(0));
            boolean isSuccess = mHeartInfoUtils.MyUpdateData(mHeartInfo);
            if (isSuccess) {
                MyLog.i(TAG, "插入整点心率表成功！");
            } else {
                MyLog.i(TAG, "插入整点心率失败！");
            }
        } else {
            HeartBean.insertPoNullData(mHeartInfoUtils, date);
        }
        getWholeSnackHeart(context, false, getDataSuccess);
    }

    public void getHealthDay(Context context, boolean isRequestService, GetDataSuccess getDataSuccess) {
        HealthInfo mHealthInfo;
        //支持ECG-显示健康布局
        if (mBleDeviceTools.get_is_support_ecg() == 1) {
            mHealthInfo = mHealthInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime(), true);
        } else {
            mHealthInfo = mHealthInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime(), false);
        }
        if (mHealthInfo != null) {
            getDataSuccess.onSuccess(mHealthInfo);
        } else {
            if (isRequestService) {
                requestHealthData(MyTime.getTime(), context, getDataSuccess);
            } else {
                getDataSuccess.onSuccess(null);
            }
        }
    }

    private void requestHealthData(final String date, final Context context, final GetDataSuccess getDataSuccess) {
        RequestInfo mRequestInfo = RequestJson.getdHealthData(date, date);
        MyLog.i(TAG, "请求接口-获取健康数据" + mRequestInfo.getRequestJson());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        HealthBean mHealthBean = ResultJson.HealthBean(result);
                        if (mHealthBean.isRequestSuccess()) {
                            if (mHealthBean.isGetHealthSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取健康数据 成功");
                                ResultDataParsing(mHealthBean, date, context, getDataSuccess);
                            } else if (mHealthBean.isGetHealthSuccess() == 0) {
                                getDataSuccess.onSuccess(null);
                            } else if (mHealthBean.isGetHealthSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-获取健康数据 无数据");
                                HealthBean.insertNullHealth(mHealthInfoUtils, date, true);
                                HealthBean.insertNullHealth(mHealthInfoUtils, date, false);
                                getHealthDay(context, false, getDataSuccess);
                            } else {
                                getDataSuccess.onSuccess(null);
                            }
                            //请求失败
                        } else {
                            getDataSuccess.onSuccess(null);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        getDataSuccess.onSuccess(null);
                    }
                });
    }

    private void ResultDataParsing(HealthBean mHealthBean, String date, Context context, GetDataSuccess getDataSuccess) {
        if (mHealthBean.getData().getHealthList().size() >= 1) {
            List<HealthInfo> health_list = mHealthBean.getHealthInfoList(mHealthBean.getData().getHealthList());
            boolean isSuccess = mHealthInfoUtils.insertInfoList(health_list);
            if (isSuccess) {
                MyLog.i(TAG, "插入健康表成功！");
            } else {
                MyLog.i(TAG, "插入健康失败！");
            }
        } else {
            HealthBean.insertNullHealth(mHealthInfoUtils, date, true);
            HealthBean.insertNullHealth(mHealthInfoUtils, date, false);
        }
        getHealthDay(context, false, getDataSuccess);
    }

    public void getSportModleData(GetDataSuccess getDataSuccess) {
        SportModleInfo mSportModleInfo = null;

        String selectionDate = MyTime.getTime();
        long startTime = NewTimeUtils.getLongTime(selectionDate, NewTimeUtils.TIME_YYYY_MM_DD);
        long endTime = startTime + 24 * 3600 * 1000L - 1;

        List<SportModleInfo> sportModleInfos = mSportModleInfoUtils.queryByTime(startTime, endTime);
        if (sportModleInfos.size() > 0) {
            mSportModleInfo = sportModleInfos.get(0);
            if (mSportModleInfo != null) {
                getDataSuccess.onSuccess(mSportModleInfo);
            } else {
                getDataSuccess.onSuccess(null);
            }
        } else {
            long lastRequestServiceTime = mBleDeviceTools.getLastRequestServiceTime();
            if (System.currentTimeMillis() - lastRequestServiceTime > 3 * 3600 * 1000L) {
                DeviceSportManager.Companion.getInstance().getMoreSportData(selectionDate, new DeviceSportManager.GetDataSuccess() {
                    @Override
                    public void onSuccess() {
                        List<SportModleInfo> sportModleInfos = mSportModleInfoUtils.queryByTime(startTime, endTime);
                        if (sportModleInfos.size() > 0) {
                            SportModleInfo mSportModleInfo = sportModleInfos.get(0);
                            if (mSportModleInfo != null) {
                                getDataSuccess.onSuccess(mSportModleInfo);
                            } else {
                                getDataSuccess.onSuccess(null);
                            }
                        } else {
                            getDataSuccess.onSuccess(null);
                        }
                    }

                    @Override
                    public void onError() {
                        getDataSuccess.onSuccess(null);
                    }
                });
            } else {
                getDataSuccess.onSuccess(null);
            }
        }
    }



    public void getOfflineSpo2(Context context, boolean isRequestService, GetDataSuccess getDataSuccess) {
        List<MeasureSpo2Info> data_list = mMeasureSpo2InfoUtils.queryToDate(BaseApplication.getUserId(), MyTime.getTime());
        MeasureSpo2Info measureSpo2Info = null;
        if (data_list.size() > 0) {
            measureSpo2Info = data_list.get(0);
        }
        if (measureSpo2Info != null) {
            getDataSuccess.onSuccess(measureSpo2Info);
        } else {
            if (isRequestService) {
                requestMeasureSpo2Data(MyTime.getTime(), context, getDataSuccess);
            } else {
                getDataSuccess.onSuccess(null);
            }
        }
    }

    private void requestMeasureSpo2Data(final String date, final Context mContext, GetDataSuccess getDataSuccess) {
        RequestInfo mRequestInfo = RequestJson.getMeasureSpo2ListData(date);
        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub
                        MyLog.i(TAG, "请求接口-获取测量血氧数据 result = " + result);
                        MeasureSpo2ListBean mMeasureSpo2ListBean = ResultJson.MeasureSpo2ListBean(result);
                        //请求成功
                        if (mMeasureSpo2ListBean.isRequestSuccess()) {
                            if (mMeasureSpo2ListBean.isGetSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取测量血氧数据 成功");
                                ResultMeasureSpo2Parsing(mMeasureSpo2ListBean, date, mContext, getDataSuccess);
                            } else if (mMeasureSpo2ListBean.isGetSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取测量血氧数据 失败");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else if (mMeasureSpo2ListBean.isGetSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-获取测量血氧数据 无数据");
                                MeasureSpo2ListBean.insertNullData(mMeasureSpo2InfoUtils, date);
                                getDataSuccess.onSuccess(null);
                            } else {
                                MyLog.i(TAG, "请求接口-获取测量血氧数据 请求异常(1)");
                                getDataSuccess.onSuccess(null);
                            }
                        } else {
                            MyLog.i(TAG, "请求接口-获取测量血氧数据 请求异常(0)");
                            getDataSuccess.onSuccess(null);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        getDataSuccess.onSuccess(null);
                    }
                });
    }

    private void ResultMeasureSpo2Parsing(MeasureSpo2ListBean mMeasureSpo2ListBean, String date, Context mContext, GetDataSuccess getDataSuccess) {
        if (mMeasureSpo2ListBean.getData().getSpoMeasureList().size() >= 1) {
            List<MeasureSpo2Info> data_list = mMeasureSpo2ListBean.getInfoList(mMeasureSpo2ListBean.getData().getSpoMeasureList());
            boolean isSuccess = mMeasureSpo2InfoUtils.insertInfoList(data_list);
            if (isSuccess) {
                MyLog.i(TAG, "插入测量血氧表成功！");
            } else {
                MyLog.i(TAG, "插入测量血氧失败！");
            }
        } else {
            MeasureSpo2ListBean.insertNullData(mMeasureSpo2InfoUtils, date);
        }
        getOfflineSpo2(mContext, false, getDataSuccess);
    }

    public void getContinuitySpo2(Context context, boolean isRequestService, GetDataSuccess getDataSuccess) {
        ContinuitySpo2Info mContinuitySpo2Info = mContinuitySpo2InfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime());
        if (mContinuitySpo2Info != null) {
            getDataSuccess.onSuccess(mContinuitySpo2Info);
        } else {
            if (isRequestService) {
                requestContinuitySpo2Data(MyTime.getTime(), context, getDataSuccess);
            } else {
                getDataSuccess.onSuccess(null);
            }
        }
    }

    private void requestContinuitySpo2Data(final String date, final Context mContext, GetDataSuccess getDataSuccess) {
        RequestInfo mRequestInfo = RequestJson.getContinuitySpo2ListData(date);

        MyLog.i(TAG, "请求接口-获取连续血氧数据 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub
                        MyLog.i(TAG, "请求接口-获取连续血氧数据 请求成功 = result = " + result.toString());
                        ContinuitySpo2ListBean mContinuitySpo2ListBean = ResultJson.ContinuitySpo2ListBean(result);
                        //请求成功
                        if (mContinuitySpo2ListBean.isRequestSuccess()) {

                            if (mContinuitySpo2ListBean.isGetSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取连续血氧数据 成功");
                                ResultSpo2tDataParsing(mContinuitySpo2ListBean, date, mContext, getDataSuccess);
                            } else if (mContinuitySpo2ListBean.isGetSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取连续血氧数据 失败");
                            } else if (mContinuitySpo2ListBean.isGetSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-获取连续血氧数据 无数据");
                                ContinuitySpo2ListBean.insertNullData(mContinuitySpo2InfoUtils, date);
                                getDataSuccess.onSuccess(null);
                            } else {
                                MyLog.i(TAG, "请求接口-获取连续血氧数据 请求异常(1)");
                                getDataSuccess.onSuccess(null);
                            }

                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取连续血氧数据 请求异常(0)");
                            getDataSuccess.onSuccess(null);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        MyLog.i(TAG, "请求接口-获取连续血氧数据 请求失败 = message = " + arg0.getMessage());
                        getDataSuccess.onSuccess(null);
                    }
                });
    }

    private void ResultSpo2tDataParsing(ContinuitySpo2ListBean mContinuitySpo2ListBean, String date, Context mContext, GetDataSuccess getDataSuccess) {
        MyLog.i(TAG, "请求接口-获取连续血氧数据 size = " + mContinuitySpo2ListBean.getData().size());
        if (mContinuitySpo2ListBean.getData().size() > 0) {
            ContinuitySpo2Info mContinuitySpo2Info = mContinuitySpo2ListBean.getContinuitySpo2Info(mContinuitySpo2ListBean.getData().get(0));
            boolean isSuccess = mContinuitySpo2InfoUtils.MyUpdateData(mContinuitySpo2Info);
            if (isSuccess) {
                MyLog.i(TAG, "插入连续血氧表成功！");
            } else {
                MyLog.i(TAG, "插入连续血氧表失败！");
            }
        } else {
            ContinuitySpo2ListBean.insertNullData(mContinuitySpo2InfoUtils, date);
        }
        getContinuitySpo2(mContext, false, getDataSuccess);
    }


    public void getOfflineTempDay(Context context, boolean isRequestService, GetDataSuccess getDataSuccess) {
        List<MeasureTempInfo> data_list = mMeasureTempInfoUtils.queryToDate(BaseApplication.getUserId(), MyTime.getTime());

        MeasureTempInfo measureTempInfo = null;
        if (data_list.size() > 0) {
            measureTempInfo = data_list.get(0);
        }
        if (measureTempInfo != null) {
            getDataSuccess.onSuccess(measureTempInfo);
        } else {
            if (isRequestService) {
                requestOfflineTempData(MyTime.getTime(), context, getDataSuccess);
            } else {
                getDataSuccess.onSuccess(null);
            }
        }
    }
    private void requestOfflineTempData(final String date,  Context mContext, GetDataSuccess getDataSuccess) {
        RequestInfo mRequestInfo = RequestJson.getMeasureTempListData(date);
        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject result) {
                        MeasureTempListBean mMeasureTempListBean = ResultJson.MeasureTempListBean(result);
                        //请求成功
                        if (mMeasureTempListBean.isRequestSuccess()) {
                            if (mMeasureTempListBean.isGetSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取测量体温数据 成功");
                                ResultOfflineTempParsing(mMeasureTempListBean, date, mContext, getDataSuccess);
                            } else if (mMeasureTempListBean.isGetSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取测量体温数据 失败");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else if (mMeasureTempListBean.isGetSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-获取测量体温数据 无数据");
                                MeasureTempListBean.insertNullData(mMeasureTempInfoUtils, date);
                                getDataSuccess.onSuccess(null);
                            } else {
                                getDataSuccess.onSuccess(null);
                            }
                        } else {
                            getDataSuccess.onSuccess(null);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        getDataSuccess.onSuccess(null);
                    }
                });
    }
    private void ResultOfflineTempParsing(MeasureTempListBean mMeasureTempListBean, String date, Context mContext, GetDataSuccess getDataSuccess) {
        if (mMeasureTempListBean.getData().getTemperatureMeasureList().size() >= 1) {
            List<MeasureTempInfo> data_list = mMeasureTempListBean.getInfoList(mMeasureTempListBean.getData().getTemperatureMeasureList());
            boolean isSuccess = mMeasureTempInfoUtils.insertInfoList(data_list);
            if (isSuccess) {
                MyLog.i(TAG, "插入测量体温表成功！");
            } else {
                MyLog.i(TAG, "插入测量体温失败！");
            }
        } else {
            MeasureTempListBean.insertNullData(mMeasureTempInfoUtils, date);
        }
        getOfflineTempDay(mContext, false, getDataSuccess);
    }


    public void getContinuityTempDay(Context context, boolean isRequestService, GetDataSuccess getDataSuccess) {
        ContinuityTempInfo mContinuityTempInfo = mContinuityTempInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime());
        if (mContinuityTempInfo != null) {
            MyLog.i(TAG, "mContinuityTempInfo = " + mContinuityTempInfo.toString());
            getDataSuccess.onSuccess(mContinuityTempInfo);
        } else {
            MyLog.i(TAG, "mContinuityTempInfo = null");
            if (isRequestService) {
                requestContinuityTempData(MyTime.getTime(), context, getDataSuccess);
            } else {
                getDataSuccess.onSuccess(null);
            }
        }
    }

    private void requestContinuityTempData(final String date, Context mContext, GetDataSuccess getDataSuccess) {
        RequestInfo mRequestInfo = RequestJson.getContinuityTempListData(date);
        MyLog.i(TAG, "请求接口-获取连续体温数据 mRequestInfo = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub
                        MyLog.i(TAG, "请求接口-获取连续体温数据 请求成功 = result = " + result.toString());
                        ContinuityTempListBean mContinuityTempListBean = ResultJson.ContinuityTempListBean(result);
                        //请求成功
                        if (mContinuityTempListBean.isRequestSuccess()) {
                            if (mContinuityTempListBean.isGetSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取连续体温数据 成功");
                                ResultTemptDataParsing(mContinuityTempListBean, date, mContext, getDataSuccess);
                            } else if (mContinuityTempListBean.isGetSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取连续体温数据 失败");
                            } else if (mContinuityTempListBean.isGetSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-获取连续体温数据 无数据");
                                ContinuityTempListBean.insertNullData(mContinuityTempInfoUtils, date);
                                getDataSuccess.onSuccess(null);

                            } else {
                                MyLog.i(TAG, "请求接口-获取连续体温数据 请求异常(1)");
                                getDataSuccess.onSuccess(null);
                            }
                        } else {
                            MyLog.i(TAG, "请求接口-获取连续体温数据 请求异常(0)");
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        getDataSuccess.onSuccess(null);
                    }
                });
    }

    /**
     * 解析数据
     */
    private void ResultTemptDataParsing(ContinuityTempListBean mContinuityTempListBean, String date, Context mContext, GetDataSuccess getDataSuccess) {
        MyLog.i(TAG, "请求接口-获取连续体温数据 size = " + mContinuityTempListBean.getData().size());

        if (mContinuityTempListBean.getData().size() > 0) {
            ContinuityTempInfo mContinuityTempInfo = mContinuityTempListBean.getContinuityTempInfo(mContinuityTempListBean.getData().get(0));
            boolean isSuccess = mContinuityTempInfoUtils.MyUpdateData(mContinuityTempInfo);
            if (isSuccess) {
                MyLog.i(TAG, "插入连续体温表成功！");
            } else {
                MyLog.i(TAG, "插入连续体温表失败！");
            }
        } else {
            ContinuityTempListBean.insertNullData(mContinuityTempInfoUtils, date);
        }
        getContinuityTempDay(mContext, false, getDataSuccess);
    }

}
