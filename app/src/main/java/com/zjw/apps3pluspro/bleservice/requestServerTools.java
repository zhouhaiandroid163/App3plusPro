package com.zjw.apps3pluspro.bleservice;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.ContinunitySpo2Data;
import com.zjw.apps3pluspro.network.entity.ContinunityTempData;
import com.zjw.apps3pluspro.network.entity.HealthData;
import com.zjw.apps3pluspro.network.entity.HeartData;
import com.zjw.apps3pluspro.network.entity.MeasureSpo2Data;
import com.zjw.apps3pluspro.network.entity.MeasureTempData;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.entity.SleepData;
import com.zjw.apps3pluspro.network.entity.SportData;
import com.zjw.apps3pluspro.network.javabean.ContinuitySpo2ListBean;
import com.zjw.apps3pluspro.network.javabean.ContinuityTempListBean;
import com.zjw.apps3pluspro.network.javabean.CurrencyBean;
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
import com.zjw.apps3pluspro.sql.entity.ContinuitySpo2Info;
import com.zjw.apps3pluspro.sql.entity.ContinuityTempInfo;
import com.zjw.apps3pluspro.sql.entity.HealthInfo;
import com.zjw.apps3pluspro.sql.entity.HeartInfo;
import com.zjw.apps3pluspro.sql.entity.MeasureSpo2Info;
import com.zjw.apps3pluspro.sql.entity.MeasureTempInfo;
import com.zjw.apps3pluspro.sql.entity.MovementInfo;
import com.zjw.apps3pluspro.sql.entity.SleepInfo;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


//蓝牙服务类，请求后台相关工具类
public class requestServerTools {

    private static final String TAG = requestServerTools.class.getSimpleName();

    public static void uploadDeviceData(Context context) {
        UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
        RequestInfo upload_device_info = RequestJson.bindDeviceInfo(context, false);
        if (TextUtils.isEmpty(mUserSetTools.get_service_upload_device_info()) || !upload_device_info.toString().equalsIgnoreCase(mUserSetTools.get_service_upload_device_info())) {

            mUserSetTools.set_service_upload_un_device_info("");
            RequestInfo mRequestInfo = RequestJson.bindDeviceInfo(context, true);
            NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                @Override
                public void onMySuccess(JSONObject result) {
                    // TODO Auto-generated method stub

                    MyLog.i(TAG, "请求接口-上传设备信息 请求成功 = result = " + result.toString());

                    CurrencyBean mCurrencyBean = ResultJson.CurrencyBean(result);

                    //请求成功
                    if (mCurrencyBean.isRequestSuccess()) {

                        if (mCurrencyBean.uploadSuccess() == 1) {
                            mUserSetTools.set_service_upload_device_info(upload_device_info.toString());

                            MyLog.i(TAG, "请求接口-上传设备信息 成功");


                        } else if (mCurrencyBean.uploadSuccess() == 0) {
                            MyLog.i(TAG, "请求接口-上传设备信息 失败");
                        } else {
                            MyLog.i(TAG, "请求接口-上传设备信息 请求异常(1)");
                        }
                        //请求失败
                    } else {
                        MyLog.i(TAG, "请求接口-上传设备信息 请求异常(0)");

                    }
                }

                @Override
                public void onMyError(VolleyError arg0) {
                    // TODO Auto-generated method stub
                    MyLog.i(TAG, "请求接口-上传设备信息 请求失败 = message = " + arg0.getMessage());
                }
            });
        } else {
            MyLog.i(TAG, "数据库-上传设备信息 和上次一样");
        }

    }

    /**
     * 解绑
     *
     * @param context
     */
    public static void uploadUnDeviceData(Context context) {

        RequestInfo mRequestInfo = RequestJson.unbindDeviceInfo();

        UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();


        if (mBleDeviceTools.get_ble_mac() != null && !mBleDeviceTools.get_ble_mac().equals("")) {

            MyLog.i(TAG, "数据库-上传解绑信息 MAC不为空");

            if (mUserSetTools.get_service_upload_un_device_info() != null
                    && !mUserSetTools.toString().equals(mUserSetTools.get_service_upload_un_device_info())) {

                mUserSetTools.set_service_upload_un_device_info(mUserSetTools.toString());

                MyLog.i(TAG, "数据库-上传解绑信息 和上次不一样");

                MyLog.i(TAG, "数据库-上传解绑信息" + mRequestInfo.toString());

                NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                        new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                            @Override
                            public void onMySuccess(JSONObject result) {
                                // TODO Auto-generated method stub

                                MyLog.i(TAG, "请求接口-上传解绑信息 请求成功 = result = " + result.toString());

                                CurrencyBean mCurrencyBean = ResultJson.CurrencyBean(result);

                                //请求成功
                                if (mCurrencyBean.isRequestSuccess()) {

                                    if (mCurrencyBean.uploadSuccess() == 1) {

                                        MyLog.i(TAG, "请求接口-上传解绑信息 成功");


                                    } else if (mCurrencyBean.uploadSuccess() == 0) {
                                        MyLog.i(TAG, "请求接口-上传解绑信息 失败");
                                    } else {
                                        MyLog.i(TAG, "请求接口-上传解绑信息 请求异常(1)");
                                    }
                                    //请求失败
                                } else {
                                    MyLog.i(TAG, "请求接口-上传解绑信息 请求异常(0)");

                                }

                            }

                            @Override
                            public void onMyError(VolleyError arg0) {
                                // TODO Auto-generated method stub

                                MyLog.i(TAG, "请求接口-上传解绑信息 请求失败 = message = " + arg0.getMessage());
                                return;
                            }
                        });

            } else {
                MyLog.i(TAG, "数据库-上传解绑信息 和上次一样");
            }
        } else {
            MyLog.i(TAG, "数据库-上传解绑信息 MAC为空");
        }


    }


    public static void uploadSportData(Context context, final MovementInfoUtils mMovementInfoUtils) {

        List<MovementInfo> movementInfo_list = mMovementInfoUtils.MyQueryToSyncAll(BaseApplication.getUserId());
        try {
            for (int i = 0; i < movementInfo_list.size(); i++) {
                if (i > 0) {
                    if (movementInfo_list.get(i).getDate().equalsIgnoreCase(movementInfo_list.get(i - 1).getDate())) {
                        // delete this data
                        mMovementInfoUtils.deleteDataMovementInfo(movementInfo_list.get(i));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        movementInfo_list = mMovementInfoUtils.MyQueryToSyncAll(BaseApplication.getUserId());

        ArrayList<SportData> sport_data_list = new ArrayList<SportData>();

        if (movementInfo_list.size() > 0) {
            MyLog.i(TAG, "数据库-步数 获取未同步的 不为空 = " + movementInfo_list.size());
            for (int i = 0; i < movementInfo_list.size(); i++) {
                MyLog.i(TAG, "数据库-步数 获取未同步的 不为空 = " + movementInfo_list.get(i).toString());
                sport_data_list.add(new SportData(context, movementInfo_list.get(i)));
            }
        } else {
            MyLog.i(TAG, "数据库-步数 获取未同步的 空空空 = ");
        }

        if (sport_data_list.size() > 0) {

            RequestInfo mRequestInfo = RequestJson.uploadSportData(sport_data_list);

            MyLog.i(TAG, "数据库-上传运动数据" + mRequestInfo.toString());

            NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                    new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                        @Override
                        public void onMySuccess(JSONObject result) {
                            // TODO Auto-generated method stub

                            MyLog.i(TAG, "请求接口-上传运动数据 请求成功 = result = " + result.toString());

                            SportBean mSportBean = ResultJson.SportBean(result);

                            //请求成功
                            if (mSportBean.isRequestSuccess()) {

                                if (mSportBean.isUploadSportSuccess() == 1) {

                                    MyLog.i(TAG, "请求接口-上传运动数据 成功");

                                    List<SportBean.DataBean> dataBeanList = mSportBean.getData();

                                    if (dataBeanList != null && dataBeanList.size() > 0) {
                                        for (int i = 0; i < dataBeanList.size(); i++) {

                                            MyLog.i(TAG, "数据库-上传运动数据 状态 = i = " + i + " dataBeanList = " + dataBeanList.get(i).toString());

                                            if (dataBeanList.get(i).getStatus() == 1) {

                                                boolean isSuccessStep = mMovementInfoUtils.MyupdateToSyncDataId(
                                                        String.valueOf(dataBeanList.get(i).getUserId()), dataBeanList.get(i).getSportDate());

                                                MyLog.i(TAG, "数据库-修改步数 同步状态 = isSuccessStep = " + isSuccessStep);

                                            } else {
                                                MyLog.i(TAG, "单条运动上传失败");
                                            }

                                        }
                                    }


                                } else if (mSportBean.isUploadSportSuccess() == 0) {
                                    MyLog.i(TAG, "请求接口-上传运动数据 失败");
                                } else {
                                    MyLog.i(TAG, "请求接口-上传运动数据 请求异常(1)");
                                }

                                //请求失败
                            } else {
                                MyLog.i(TAG, "请求接口-上传运动数据 请求异常(0)");

                            }


                        }

                        @Override
                        public void onMyError(VolleyError arg0) {
                            // TODO Auto-generated method stub

                            MyLog.i(TAG, "请求接口-上传运动数据 请求失败 = message = " + arg0.getMessage());
                            return;
                        }
                    });

        }
    }

    public static void uploadSleepData(Context context, final SleepInfoUtils mSleepInfoUtils) {

        List<SleepInfo> sleepinfo_list = mSleepInfoUtils.MyQueryToSyncAll(BaseApplication.getUserId());
        try {
            for (int i = 0; i < sleepinfo_list.size(); i++) {
                if (i > 0) {
                    if (sleepinfo_list.get(i).getDate().equalsIgnoreCase(sleepinfo_list.get(i - 1).getDate())) {
                        // delete this data
                        mSleepInfoUtils.deleteDataSleepInfo(sleepinfo_list.get(i));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sleepinfo_list = mSleepInfoUtils.MyQueryToSyncAll(BaseApplication.getUserId());


        ArrayList<SleepData> sleep_data_list = new ArrayList<SleepData>();

        if (sleepinfo_list.size() > 0) {
            MyLog.i(TAG, "数据库-睡眠 获取未同步的 不为空 = " + sleepinfo_list.size());
            for (int i = 0; i < sleepinfo_list.size(); i++) {
                MyLog.i(TAG, "数据库-睡眠 获取未同步的 不为空 = " + sleepinfo_list.get(i).toString());
                sleep_data_list.add(new SleepData(context, sleepinfo_list.get(i)));
            }
        } else {
            MyLog.i(TAG, "数据库-睡眠 获取未同步的 空空空 = ");
        }

        if (sleep_data_list.size() > 0) {

            RequestInfo mRequestInfo = RequestJson.uploadSleepData(sleep_data_list);

            MyLog.i(TAG, "数据库-上传睡眠数据" + mRequestInfo.toString());

            NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                    new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                        @Override
                        public void onMySuccess(JSONObject result) {
                            // TODO Auto-generated method stub

                            MyLog.i(TAG, "请求接口-上传睡眠数据 请求成功 = result = " + result.toString());

                            SleepBean mSleepBean = ResultJson.SleepBean(result);

                            //请求成功
                            if (mSleepBean.isRequestSuccess()) {

                                if (mSleepBean.isUploadSleepSuccess() == 1) {

                                    MyLog.i(TAG, "请求接口-上传睡眠数据 成功");

                                    List<SleepBean.DataBean> dataBeanList = mSleepBean.getData();

                                    if (dataBeanList != null && dataBeanList.size() > 0) {
                                        for (int i = 0; i < dataBeanList.size(); i++) {

                                            MyLog.i(TAG, "数据库-上传睡眠数据 状态 = i = " + i
                                                    + " dataBeanList = " + dataBeanList.get(i).toString());

                                            if (dataBeanList.get(i).getStatus() == 1) {

                                                boolean isSuccessSleep = mSleepInfoUtils.MyupdateToSyncDataId(
                                                        String.valueOf(dataBeanList.get(i).getUserId()), dataBeanList.get(i).getSleepDate());

                                                MyLog.i(TAG, "数据库-修改睡眠 同步状态 = isSuccessSleep = " + isSuccessSleep);

                                            } else {
                                                MyLog.i(TAG, "单条睡眠上传失败");
                                            }
                                        }
                                    }

                                } else if (mSleepBean.isUploadSleepSuccess() == 0) {
                                    MyLog.i(TAG, "请求接口-上传睡眠数据 失败");
                                } else {
                                    MyLog.i(TAG, "请求接口-上传睡眠数据 请求异常(1)");
                                }

                                //请求失败
                            } else {
                                MyLog.i(TAG, "请求接口-上传睡眠数据 请求异常(0)");

                            }

                        }

                        @Override
                        public void onMyError(VolleyError arg0) {
                            // TODO Auto-generated method stub

                            MyLog.i(TAG, "请求接口-上传睡眠数据 请求失败 = message = " + arg0.getMessage());
                            return;
                        }
                    });

        }
    }


    public static void uploadHeartData(Context context, final HeartInfoUtils mHeartInfoUtils) {
        //根据ID查询所有数据，包括连续心率和整点心率
        List<HeartInfo> heartinfo_list = mHeartInfoUtils.MyQueryToSyncAll(BaseApplication.getUserId());
        try {
            for (int i = 0; i < heartinfo_list.size(); i++) {
                if (i > 0) {
                    if (heartinfo_list.get(i).getDate().equalsIgnoreCase(heartinfo_list.get(i - 1).getDate())) {
                        // delete this data
                        mHeartInfoUtils.deleteDataHeartinfo(heartinfo_list.get(i));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        heartinfo_list = mHeartInfoUtils.MyQueryToSyncAll(BaseApplication.getUserId());


        //要上传后台的数据
        ArrayList<HeartData> heart_data_list = new ArrayList<HeartData>();
        //查询出来的数据大于0
        if (heartinfo_list.size() > 0) {
            MyLog.i(TAG, "数据库-心率 获取未同步的 不为空 = " + heartinfo_list.size());
            for (int i = 0; i < heartinfo_list.size(); i++) {
                MyLog.i(TAG, "数据库-心率 获取未同步的 不为空 = " + heartinfo_list.get(i).toString());
                heart_data_list.add(new HeartData(context, heartinfo_list.get(i)));
            }
        }
        //数据不够不上传
        else {
            MyLog.i(TAG, "数据库-心率 获取未同步的 空空空 = ");
        }

        if (heart_data_list.size() > 0) {

            RequestInfo mRequestInfo = RequestJson.uploadHeartListData(heart_data_list);

            MyLog.i(TAG, "数据库-上传心率数据" + mRequestInfo.toString());

            NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                    new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                        @Override
                        public void onMySuccess(JSONObject result) {
                            // TODO Auto-generated method stub

                            MyLog.i(TAG, "请求接口-上传心率数据 请求成功 = result = " + result.toString());

                            HeartBean mHeartBean = ResultJson.HeartBean(result);

                            //请求成功
                            if (mHeartBean.isRequestSuccess()) {

                                if (mHeartBean.isUploadHeartSuccess() == 1) {

                                    MyLog.i(TAG, "请求接口-上传心率数据 成功");

                                    List<HeartBean.DataBean> dataBeanList = mHeartBean.getData();


                                    if (dataBeanList != null && dataBeanList.size() > 0) {
                                        for (int i = 0; i < dataBeanList.size(); i++) {

//                                            MyLog.i(TAG, "数据库-上传心率数据 状态 = i = " + i + " dataBeanList = " + dataBeanList.get(i).toString());

                                            if (dataBeanList.get(i).getStatus() == 1) {

                                                boolean isSuccessHeart = mHeartInfoUtils.MyupdateToSyncDataId(
                                                        String.valueOf(dataBeanList.get(i).getUserId()),
                                                        dataBeanList.get(i).getHeartRateRawDate(),
                                                        dataBeanList.get(i).getHeartRateType()
                                                );

                                                MyLog.i(TAG, "数据库-修改心率 同步状态 = isSuccessHeart = " + isSuccessHeart);

                                            } else {
                                                MyLog.i(TAG, "上传心率数据 上传失败");
                                            }
                                        }
                                    }

                                } else if (mHeartBean.isUploadHeartSuccess() == 0) {
                                    MyLog.i(TAG, "请求接口-上传心率数据 失败");
                                } else {
                                    MyLog.i(TAG, "请求接口-上传心率数据 请求异常(1)");
                                }
                                //请求失败
                            } else {
                                MyLog.i(TAG, "请求接口-上传心率数据 请求异常(0)");

                            }

                        }

                        @Override
                        public void onMyError(VolleyError arg0) {
                            // TODO Auto-generated method stub

                            MyLog.i(TAG, "请求接口-上传心率数据 请求失败 = message = " + arg0.getMessage());
                            return;
                        }
                    });

        }
    }

    /**
     * 上传健康数据
     */
    static void updateHealthyData(Context mContext, final HealthInfoUtils mHealthInfoUtils, final HealthInfo mHealthInfo) {


        ArrayList<HealthData> health_data_list = new ArrayList<HealthData>();

        health_data_list.add(new HealthData(mContext, mHealthInfo));


        if (health_data_list.size() > 0) {


//            waitDialog.show(getString(R.string.loading0));

            RequestInfo mRequestInfo = RequestJson.uploadHealthData(health_data_list);

            MyLog.i(TAG, "数据库-上传健康数据" + mRequestInfo.getRequestJson());

            NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                    new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                        @Override
                        public void onMySuccess(JSONObject result) {
                            // TODO Auto-generated method stub

                            MyLog.i(TAG, "请求接口-上传健康数据 result = " + result);

                            HealthBean mHealthBean = ResultJson.HealthBean(result);

                            //请求成功
                            if (mHealthBean.isRequestSuccess()) {
                                if (mHealthBean.isUploadHealthSuccess() == 1) {
                                    MyLog.i(TAG, "请求接口-上传健康数据 成功");

//                                    ResultDataParsing(mHealthBean, mHealthInfo);

                                    boolean isSuccessHealth = mHealthInfoUtils.MyUpdateToSyncOne(mHealthInfo);

                                    MyLog.i(TAG, "数据库-健康数据 同步状态 = isSuccessHealth = " + isSuccessHealth);
                                } else if (mHealthBean.isUploadHealthSuccess() == 0) {
                                    MyLog.i(TAG, "请求接口-上传健康数据 失败");
                                } else {
                                    MyLog.i(TAG, "请求接口-上传健康数据 请求异常(1)");
                                }
                                //请求失败
                            } else {
                                MyLog.i(TAG, "请求接口-上传健康数据 请求异常(0)");
                            }


                        }

                        @Override
                        public void onMyError(VolleyError arg0) {
                            // TODO Auto-generated method stub


                            MyLog.i(TAG, "请求接口-上传健康数据 请求失败 = message = " + arg0.getMessage());
//                            waitDialog.close();

                        }
                    });

        }


    }

    /**
     * 上传健康数据-最新的几条
     */
    static void updateListHealthyData(Context mContext, final HealthInfoUtils mHealthInfoUtils) {

        final List<HealthInfo> healtInfo_list = mHealthInfoUtils.MyQueryToSyncAll(BaseApplication.getUserId());


        ArrayList<HealthData> health_data_list = new ArrayList<HealthData>();

        if (healtInfo_list != null && healtInfo_list.size() > 0) {
            MyLog.i(TAG, "数据库-健康数据 获取未同步的 不为空 = " + healtInfo_list.size());
            for (int i = 0; i < healtInfo_list.size(); i++) {
                health_data_list.add(new HealthData(mContext, healtInfo_list.get(i)));
            }

        } else {
            MyLog.i(TAG, "数据库-健康数据 获取未同步的 空空空 = ");
        }

        if (health_data_list.size() > 0) {

            RequestInfo mRequestInfo = RequestJson.uploadHealthData(health_data_list);

            MyLog.i(TAG, "数据库-上传健康数据" + mRequestInfo.getRequestJson());

            NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                    new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                        @Override
                        public void onMySuccess(JSONObject result) {
                            // TODO Auto-generated method stub

                            MyLog.i(TAG, "请求接口-上传健康数据 result = " + result);

                            HealthBean mHealthBean = ResultJson.HealthBean(result);

                            //请求成功
                            if (mHealthBean.isRequestSuccess()) {
                                if (mHealthBean.isUploadHealthSuccess() == 1) {
                                    MyLog.i(TAG, "请求接口-上传健康数据 成功");

//                                    ResultDataParsing(mHealthBean, mHealthInfo);

                                    boolean isSuccessHealth = mHealthInfoUtils.MyUpdateToSync(healtInfo_list);

                                    MyLog.i(TAG, "数据库-健康数据 同步状态 = isSuccessHealth = " + isSuccessHealth);
                                } else if (mHealthBean.isUploadHealthSuccess() == 0) {
                                    MyLog.i(TAG, "请求接口-上传健康数据 失败");
                                } else {
                                    MyLog.i(TAG, "请求接口-上传健康数据 请求异常(1)");
                                }
                                //请求失败
                            } else {
                                MyLog.i(TAG, "请求接口-上传健康数据 请求异常(0)");
                            }


                        }

                        @Override
                        public void onMyError(VolleyError arg0) {
                            // TODO Auto-generated method stub


                            MyLog.i(TAG, "请求接口-上传健康数据 请求失败 = message = " + arg0.getMessage());
//                            waitDialog.close();

                        }
                    });
        }


    }

    /**
     * 上传手环的版本号
     */
    static void uploadBraceletVersion() {


//        RequestInfo mRequestInfo = RequestJson.uploadDeviceInfo();
//
//        MyLog.i(TAG, "请求接口-上传设备信息 mRequestInfo = " + mRequestInfo.toString());
//
//        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
//                new VolleyInterface(getApplicationContext(), VolleyInterface.mListener, VolleyInterface.mErrorListener) {
//
//                    @Override
//                    public void onMySuccess(JSONObject result) {
//                        // TODO Auto-generated method stub
//
//
//                        MyLog.i(TAG, "请求接口-上传设备信息 result = " + result);
//
//
//                        UserBean mUserBean = new Gson().fromJson(result.toString(), UserBean.class);
//
//                        MyLog.i(TAG, "请求接口-上传设备信息 result = " + mUserBean.getResult());
//                        MyLog.i(TAG, "请求接口-上传设备信息 result = " + mUserBean.getMsg());
//
//                        if (mUserBean.getResult() == 1) {
//
//
//                            MyLog.i(TAG, "请求接口-上传设备信息 成功");
//
//
//                        } else {
//
//                            MyLog.i(TAG, "请求接口-上传设备信息 失败");
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onMyError(VolleyError arg0) {
//                        // TODO Auto-generated method stub
//
//                        MyLog.i(TAG, "请求接口-上传设备信息 请求失败 = message = " + arg0.getMessage());
//
//
//                        return;
//                    }
//                });

    }


    public static void uploadContinuitySpo2Data(Context context, final ContinuitySpo2InfoUtils mContinuitySpo2InfoUtils) {
        List<ContinuitySpo2Info> info_list = mContinuitySpo2InfoUtils.MyQueryToSyncAll(BaseApplication.getUserId());
        try {
            for (int i = 0; i < info_list.size(); i++) {
                if (i > 0) {
                    if (info_list.get(i).getDate().equalsIgnoreCase(info_list.get(i - 1).getDate())) {
                        // delete this data
                        mContinuitySpo2InfoUtils.deleteDataContinuitySpo2Info(info_list.get(i));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        info_list = mContinuitySpo2InfoUtils.MyQueryToSyncAll(BaseApplication.getUserId());

        ArrayList<ContinunitySpo2Data> data_list = new ArrayList<ContinunitySpo2Data>();

        if (info_list.size() > 0) {

            MyLog.i(TAG, "数据库-连续血氧 获取未同步的 不为空 = " + info_list.size());
            for (int i = 0; i < info_list.size(); i++) {
                MyLog.i(TAG, "数据库-连续血氧 获取未同步的 不为空 = " + info_list.get(i).toString());
                data_list.add(new ContinunitySpo2Data(context, info_list.get(i)));
            }
        } else {
            MyLog.i(TAG, "数据库-连续血氧 获取未同步的 空空空 = ");
        }

        if (data_list.size() > 0) {

            RequestInfo mRequestInfo = RequestJson.uploadContinuitySpo2Data(data_list);

            MyLog.i(TAG, "请求接口-上传连续血氧数据" + mRequestInfo.toString());

            List<ContinuitySpo2Info> finalInfo_list = info_list;
            NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                    new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                        @Override
                        public void onMySuccess(JSONObject result) {
                            // TODO Auto-generated method stub

                            MyLog.i(TAG, "请求接口-上传连续血氧数据 请求成功 = result = " + result.toString());

                            ContinuitySpo2ListBean mContinuitySpo2ListBean = ResultJson.ContinuitySpo2ListBean(result);

                            //请求成功
                            if (mContinuitySpo2ListBean.isRequestSuccess()) {

                                if (mContinuitySpo2ListBean.isGetSuccess() == 1) {

                                    MyLog.i(TAG, "请求接口-上传连续血氧数据 成功");
                                    boolean isSuccess = mContinuitySpo2InfoUtils.MyUpdateToSync(finalInfo_list);
                                    MyLog.i(TAG, "数据库-上传连续血氧数据 同步状态 = isSuccess = " + isSuccess);

                                } else if (mContinuitySpo2ListBean.isGetSuccess() == 0) {
                                    MyLog.i(TAG, "请求接口-上传连续血氧数据 失败");
                                } else {
                                    MyLog.i(TAG, "请求接口-上传连续血氧数据 请求异常(1)");
                                }

                                //请求失败
                            } else {
                                MyLog.i(TAG, "请求接口-上传连续血氧数据 请求异常(0)");

                            }
                        }

                        @Override
                        public void onMyError(VolleyError arg0) {
                            // TODO Auto-generated method stub

                            MyLog.i(TAG, "请求接口-上传连续血氧数据 请求失败 = message = " + arg0.getMessage());
                            return;
                        }
                    });

        }
    }

    public static void uploadContinuityTempData(Context context, final ContinuityTempInfoUtils mContinuityTempInfoUtils) {
        List<ContinuityTempInfo> info_list = mContinuityTempInfoUtils.MyQueryToSyncAll(BaseApplication.getUserId());

        try {
            for (int i = 0; i < info_list.size(); i++) {
                if (i > 0) {
                    if (info_list.get(i).getDate().equalsIgnoreCase(info_list.get(i - 1).getDate())) {
                        // delete this data
                        mContinuityTempInfoUtils.deleteDataContinuityTempInfo(info_list.get(i));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        info_list = mContinuityTempInfoUtils.MyQueryToSyncAll(BaseApplication.getUserId());


        ArrayList<ContinunityTempData> data_list = new ArrayList<ContinunityTempData>();

        if (info_list.size() > 0) {

            MyLog.i(TAG, "数据库-连续体温 获取未同步的 不为空 = " + info_list.size());
            for (int i = 0; i < info_list.size(); i++) {
                MyLog.i(TAG, "数据库-连续体温 获取未同步的 不为空 = " + info_list.get(i).toString());
                data_list.add(new ContinunityTempData(context, info_list.get(i)));
            }
        } else {
            MyLog.i(TAG, "数据库-连续体温 获取未同步的 空空空 = ");
        }

        if (data_list.size() > 0) {

            RequestInfo mRequestInfo = RequestJson.uploadContinuityTempData(data_list);

            MyLog.i(TAG, "请求接口-上传连续体温数据" + mRequestInfo.toString());

            List<ContinuityTempInfo> finalInfo_list = info_list;
            NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                    new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                        @Override
                        public void onMySuccess(JSONObject result) {
                            // TODO Auto-generated method stub

                            MyLog.i(TAG, "请求接口-上传连续体温数据 请求成功 = result = " + result.toString());

                            ContinuityTempListBean mContinuityTempListBean = ResultJson.ContinuityTempListBean(result);

                            //请求成功
                            if (mContinuityTempListBean.isRequestSuccess()) {

                                if (mContinuityTempListBean.isGetSuccess() == 1) {

                                    MyLog.i(TAG, "请求接口-上传连续体温数据 成功");
                                    boolean isSuccess = mContinuityTempInfoUtils.MyUpdateToSync(finalInfo_list);
                                    MyLog.i(TAG, "数据库-上传连续体温数据 同步状态 = isSuccess = " + isSuccess);


                                } else if (mContinuityTempListBean.isGetSuccess() == 0) {
                                    MyLog.i(TAG, "请求接口-上传连续体温数据 失败");
                                } else {
                                    MyLog.i(TAG, "请求接口-上传连续体温数据 请求异常(1)");
                                }

                                //请求失败
                            } else {
                                MyLog.i(TAG, "请求接口-上传连续体温数据 请求异常(0)");

                            }


                        }

                        @Override
                        public void onMyError(VolleyError arg0) {
                            // TODO Auto-generated method stub

                            MyLog.i(TAG, "请求接口-上传连续体温数据 请求失败 = message = " + arg0.getMessage());
                            return;
                        }
                    });

        }
    }


    /**
     * 上传血氧数据-最新的几条
     */
    static void updateMeasureSpo2ListData(Context mContext, final MeasureSpo2InfoUtils mMeasureSpo2InfoUtils) {

        ArrayList<MeasureSpo2Data> data_list = new ArrayList<MeasureSpo2Data>();

        List<MeasureSpo2Info> healtInfo_list = mMeasureSpo2InfoUtils.MyQueryToSyncAll(BaseApplication.getUserId());

        if (healtInfo_list != null && healtInfo_list.size() > 0) {
            MyLog.i(TAG, "数据库-离线血氧 获取未同步的 不为空 = " + healtInfo_list.size());
            for (int i = 0; i < healtInfo_list.size(); i++) {
                data_list.add(new MeasureSpo2Data(mContext, healtInfo_list.get(i)));
            }

        } else {
            MyLog.i(TAG, "数据库-离线血氧 获取未同步的 空空空 = ");
        }

        if (data_list.size() > 0) {

            RequestInfo mRequestInfo = RequestJson.uploadMeasureSpo2Data(data_list);

            MyLog.i(TAG, "数据库-上传离线血氧" + mRequestInfo.getRequestJson());

            NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                    new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                        @Override
                        public void onMySuccess(JSONObject result) {
                            // TODO Auto-generated method stub

                            MyLog.i(TAG, "请求接口-上传离线血氧 result = " + result);

                            MeasureSpo2ListBean mMeasureSpo2ListBean = ResultJson.MeasureSpo2ListBean(result);

                            //请求成功
                            if (mMeasureSpo2ListBean.isRequestSuccess()) {
                                if (mMeasureSpo2ListBean.isGetSuccess() == 1) {
                                    MyLog.i(TAG, "请求接口-上传离线血氧 成功");

                                    boolean isSuccessHealth = mMeasureSpo2InfoUtils.MyUpdateToSync(healtInfo_list);

                                    MyLog.i(TAG, "数据库-上传离线血氧 同步状态 = isSuccessHealth = " + isSuccessHealth);
                                } else if (mMeasureSpo2ListBean.isGetSuccess() == 0) {
                                    MyLog.i(TAG, "请求接口-上传离线血氧 失败");
                                } else {
                                    MyLog.i(TAG, "请求接口-上传离线血氧 请求异常(1)");
                                }
                                //请求失败
                            } else {
                                MyLog.i(TAG, "请求接口-上传离线血氧 请求异常(0)");
                            }


                        }

                        @Override
                        public void onMyError(VolleyError arg0) {
                            // TODO Auto-generated method stub


                            MyLog.i(TAG, "请求接口-上传离线血氧 请求失败 = message = " + arg0.getMessage());

                        }
                    });
        }


    }

    /**
     * 上传温度数据-最新的几条
     */
    static void updateMeasureTempListData(Context mContext, final MeasureTempInfoUtils mMeasureTempInfoUtils) {

        List<MeasureTempInfo> healtInfo_list = mMeasureTempInfoUtils.MyQueryToSyncAll(BaseApplication.getUserId());

        ArrayList<MeasureTempData> data_list = new ArrayList<MeasureTempData>();

        if (healtInfo_list != null && healtInfo_list.size() > 0) {

            MyLog.i(TAG, "数据库-离线体温 获取未同步的 不为空 = " + healtInfo_list.size());
            for (int i = 0; i < healtInfo_list.size(); i++) {
                data_list.add(new MeasureTempData(mContext, healtInfo_list.get(i)));
            }

        } else {
            MyLog.i(TAG, "数据库-离线体温 获取未同步的 空空空 = ");
        }

        if (data_list.size() > 0) {

            RequestInfo mRequestInfo = RequestJson.uploadMeasureTempData(data_list);

            MyLog.i(TAG, "数据库-上传离线体温" + mRequestInfo.getRequestJson());

            NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                    new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                        @Override
                        public void onMySuccess(JSONObject result) {
                            // TODO Auto-generated method stub

                            MyLog.i(TAG, "请求接口-上传离线体温 result = " + result);

                            MeasureTempListBean mMeasureTempListBean = ResultJson.MeasureTempListBean(result);

                            //请求成功
                            if (mMeasureTempListBean.isRequestSuccess()) {
                                if (mMeasureTempListBean.isGetSuccess() == 1) {
                                    MyLog.i(TAG, "请求接口-上传离线体温 成功");

                                    boolean isSuccessHealth = mMeasureTempInfoUtils.MyUpdateToSync(healtInfo_list);

                                    MyLog.i(TAG, "数据库-离线体温 同步状态 = isSuccessHealth = " + isSuccessHealth);
                                } else if (mMeasureTempListBean.isGetSuccess() == 0) {
                                    MyLog.i(TAG, "请求接口-上传离线体温 失败");
                                } else {
                                    MyLog.i(TAG, "请求接口-上传离线体温 请求异常(1)");
                                }
                                //请求失败
                            } else {
                                MyLog.i(TAG, "请求接口-上传离线体温 请求异常(0)");
                            }


                        }

                        @Override
                        public void onMyError(VolleyError arg0) {
                            // TODO Auto-generated method stub


                            MyLog.i(TAG, "请求接口-上传离线体温 请求失败 = message = " + arg0.getMessage());

                        }
                    });
        }


    }


    public static final int SYNC_TIME_START_TAG = 1;
    public static final int SYNC_TIME_END_TAG = 2;

    public static void syncWatchTime(Context mContext, int type) {
//        RequestInfo mRequestInfo = RequestJson.syncWatchTime(type);
//        MyLog.i(TAG, " syncWatchTime = " + mRequestInfo.getRequestJson());
//        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
//                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
//                    @Override
//                    public void onMySuccess(JSONObject result) {
//                        MyLog.i(TAG, " syncWatchTime = " + result);
//                    }
//                    @Override
//                    public void onMyError(VolleyError arg0) {
//                        MyLog.i(TAG, " syncWatchTime = " + arg0);
//                    }
//                });
    }


}
