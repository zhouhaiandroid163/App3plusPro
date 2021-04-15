package com.zjw.apps3pluspro.module.friend;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.home.entity.HeartModel;
import com.zjw.apps3pluspro.module.home.entity.SleepModel;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.HealthBean;
import com.zjw.apps3pluspro.network.javabean.HeartBean;
import com.zjw.apps3pluspro.network.javabean.NewFriendInfoBean;
import com.zjw.apps3pluspro.network.javabean.SleepBean;
import com.zjw.apps3pluspro.network.javabean.SportBean;
import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.entity.HealthInfo;
import com.zjw.apps3pluspro.sql.entity.HeartInfo;
import com.zjw.apps3pluspro.sql.entity.MovementInfo;
import com.zjw.apps3pluspro.sql.entity.SleepInfo;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.CircleImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * 好友的详情界面
 */
public class FriendRankInfoActivity extends BaseActivity implements OnClickListener {
    private final String TAG = FriendRankInfoActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    private WaitDialog waitDialog;
    private CircleImageView ci_public_user_head;

    private String FriendDataId;//数据ID
    private String FriendFid;//好友ID
    private String FriendNickName;//好友昵称
    private String FriendRemarksName;//好友昵称
    private String friendHearUrl;//好友头像

    private TextView public_head_title;

    public static final int FriendRemarksNameCode = 100;//好友备注
    public static final String DeleteTag = "DeleteTag";//是否删除好友
    public static final String DeleteTag_YES = "is_delete_yes";//是否删除好友
    public static final String DeleteTag_NO = "is_delete_no";//是否删除好友


    //心电相关
    private TextView tv_friend_info_ecg_state, tv_friend_info_ecg_time, tv_friend_info_health_number, tv_friend_info_health_state;
    private TextView tv_friend_info_ecg_heart, tv_friend_info_ecg_sdp, tv_friend_info_ecg_dbp;
    private RelativeLayout rl_friend_info_ecg_view;

    //步数相关
    private TextView tv_friend_info_sport_step, tv_friend_info_sport_distance, tv_friend_info_sport_distance_unit, tv_friend_info_sport_calory;

    //睡眠相关
    private TextView tv_friend_info_sleep_time_h, tv_friend_info_sleep_time_m,
            tv_friend_info_sleep_deep_time_h, tv_friend_info_sleep_deep_time_m,
            tv_friend_info_sleep_deep_proportion,
            tv_friend_info_sleep_light_time_h, tv_friend_info_sleep_light_time_m,
            tv_friend_info_sleep_light_proportion,
            tv_friend_info_sleep_sober_time_h, tv_friend_info_sleep_sober_time_m,
            tv_friend_info_sleep_sober_proportion;

    //心率相关
    private TextView tv_friend_info_heart_last, tv_friend_info_heart_avg, tv_friend_info_heart_height, tv_friend_info_heart_low;

    //血压相关
    private TextView tv_friend_info_bp_state, tv_friend_info_bp_time, tv_friend_info_bp_heart, tv_friend_info_bp_sbp, tv_friend_info_bp_dbp;
    private RelativeLayout rl_friend_rank_bp_view;


    @Override
    protected int setLayoutId() {
        return R.layout.activity_friend_rank_info;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = FriendRankInfoActivity.this;
        waitDialog = new WaitDialog(mContext);

        initView();
        initData();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
    }

    @Override
    protected void onDestroy() {
        waitDialog.dismiss();
        super.onDestroy();
    }

    private void initView() {
        // TODO Auto-generated method stub

        findViewById(R.id.public_head_back).setOnClickListener(this);
//        findViewById(R.id.public_user_head).setVisibility(View.VISIBLE);
        findViewById(R.id.public_head_friend_edit).setVisibility(View.VISIBLE);
        findViewById(R.id.public_head_friend_edit).setOnClickListener(this);

        public_head_title = (TextView) findViewById(R.id.public_head_title);
        ci_public_user_head = (CircleImageView) findViewById(R.id.ci_public_user_head);

        //心电相关
        tv_friend_info_ecg_state = (TextView) findViewById(R.id.tv_friend_info_ecg_state);
        tv_friend_info_ecg_time = (TextView) findViewById(R.id.tv_friend_info_ecg_time);
        tv_friend_info_health_number = (TextView) findViewById(R.id.tv_friend_info_health_number);
        tv_friend_info_health_state = (TextView) findViewById(R.id.tv_friend_info_health_state);
        tv_friend_info_ecg_heart = (TextView) findViewById(R.id.tv_friend_info_ecg_heart);
        tv_friend_info_ecg_sdp = (TextView) findViewById(R.id.tv_friend_info_ecg_sdp);
        tv_friend_info_ecg_dbp = (TextView) findViewById(R.id.tv_friend_info_ecg_dbp);
        rl_friend_info_ecg_view = (RelativeLayout) findViewById(R.id.rl_friend_info_ecg_view);

        //步数相关
        tv_friend_info_sport_step = (TextView) findViewById(R.id.tv_friend_info_sport_step);
        tv_friend_info_sport_distance = (TextView) findViewById(R.id.tv_friend_info_sport_distance);
        tv_friend_info_sport_distance_unit = (TextView) findViewById(R.id.tv_friend_info_sport_distance_unit);
        tv_friend_info_sport_calory = (TextView) findViewById(R.id.tv_friend_info_sport_calory);

        //睡眠相关
        tv_friend_info_sleep_time_h = (TextView) findViewById(R.id.tv_friend_info_sleep_time_h);
        tv_friend_info_sleep_time_m = (TextView) findViewById(R.id.tv_friend_info_sleep_time_m);

        tv_friend_info_sleep_deep_time_h = (TextView) findViewById(R.id.tv_friend_info_sleep_deep_time_h);
        tv_friend_info_sleep_deep_time_m = (TextView) findViewById(R.id.tv_friend_info_sleep_deep_time_m);

        tv_friend_info_sleep_deep_proportion = (TextView) findViewById(R.id.tv_friend_info_sleep_deep_proportion);

        tv_friend_info_sleep_light_time_h = (TextView) findViewById(R.id.tv_friend_info_sleep_light_time_h);
        tv_friend_info_sleep_light_time_m = (TextView) findViewById(R.id.tv_friend_info_sleep_light_time_m);

        tv_friend_info_sleep_light_proportion = (TextView) findViewById(R.id.tv_friend_info_sleep_light_proportion);

        tv_friend_info_sleep_sober_time_h = (TextView) findViewById(R.id.tv_friend_info_sleep_sober_time_h);
        tv_friend_info_sleep_sober_time_m = (TextView) findViewById(R.id.tv_friend_info_sleep_sober_time_m);

        tv_friend_info_sleep_sober_proportion = (TextView) findViewById(R.id.tv_friend_info_sleep_sober_proportion);

        //心率相关
        tv_friend_info_heart_last = (TextView) findViewById(R.id.tv_friend_info_heart_last);
        tv_friend_info_heart_avg = (TextView) findViewById(R.id.tv_friend_info_heart_avg);
        tv_friend_info_heart_height = (TextView) findViewById(R.id.tv_friend_info_heart_height);
        tv_friend_info_heart_low = (TextView) findViewById(R.id.tv_friend_info_heart_low);

        //血压相关
        tv_friend_info_bp_state = (TextView) findViewById(R.id.tv_friend_info_bp_state);
        tv_friend_info_bp_time = (TextView) findViewById(R.id.tv_friend_info_bp_time);
        tv_friend_info_bp_heart = (TextView) findViewById(R.id.tv_friend_info_bp_heart);
        tv_friend_info_bp_sbp = (TextView) findViewById(R.id.tv_friend_info_bp_sbp);
        tv_friend_info_bp_dbp = (TextView) findViewById(R.id.tv_friend_info_bp_dbp);
        rl_friend_rank_bp_view = (RelativeLayout) findViewById(R.id.rl_friend_rank_bp_view);

        // 删除好友
        findViewById(R.id.btn_friend_info_delete).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent;
        switch (v.getId()) {
            case R.id.public_head_back:
                finish();
                break;

            case R.id.btn_friend_info_delete:
                showDeleteFirendDialog();
                break;

            case R.id.public_head_friend_edit:
                Intent mIntent = new Intent(FriendRankInfoActivity.this, FriendInfoHandleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(IntentConstants.FriendDataId, FriendDataId);
                bundle.putString(IntentConstants.FriendFId, FriendFid);
                bundle.putString(IntentConstants.FriendUserName, FriendNickName);
                bundle.putString(IntentConstants.FriendRemarksName, FriendRemarksName);
                bundle.putString(IntentConstants.FriendUserHeadUrl, friendHearUrl);
                mIntent.putExtras(bundle);
//                startActivity(mIntent);
                startActivityForResult(mIntent, FriendRemarksNameCode);


                break;

        }
    }

    private void initData() {


        Intent intent = getIntent();
        if (!JavaUtil.checkIsNull(intent.getStringExtra(IntentConstants.FriendDataId))) {
            FriendDataId = intent.getStringExtra(IntentConstants.FriendDataId);
        }

        if (!JavaUtil.checkIsNull(intent.getStringExtra(IntentConstants.FriendFId))) {
            FriendFid = intent.getStringExtra(IntentConstants.FriendFId);
        }

        if (!JavaUtil.checkIsNull(intent.getStringExtra(IntentConstants.FriendUserName))) {
            FriendNickName = intent.getStringExtra(IntentConstants.FriendUserName);
        }
        if (!JavaUtil.checkIsNull(intent.getStringExtra(IntentConstants.FriendRemarksName))) {
            FriendRemarksName = intent.getStringExtra(IntentConstants.FriendRemarksName);
        }

        if (!JavaUtil.checkIsNull(intent.getStringExtra(IntentConstants.FriendUserHeadUrl))) {
            friendHearUrl = intent.getStringExtra(IntentConstants.FriendUserHeadUrl);
        }


        updateUi();

        requestSportData(MyTime.getTime());
        requestSleepData(MyTime.getTime());

        //连续心率
        if (mBleDeviceTools.get_is_support_persist_heart() == 1) {
            requestWoheartData(MyTime.getTime());
        }
        //整点心率
        else {
            requestPoheartData(MyTime.getTime());
        }

        requestHealthData(MyTime.getTime());


    }


    void showDeleteFirendDialog() {
        new android.app.AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.dialog_prompt))//设置对话框标题
                .setMessage(getString(R.string.delete_friend_content))//设置显示的内容
                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {//添加确定按钮
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        DeleteFriend();
                    }

                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {//添加返回按钮

            @Override

            public void onClick(DialogInterface dialog, int which) {//响应事件
                // TODO Auto-generated method stub
            }

        }).show();//在按键响应事件中显示此对话框

    }


    /**
     * 删除好友
     */
    private void DeleteFriend() {

        waitDialog.show(getString(R.string.loading0));
//
        RequestInfo mRequestInfo = RequestJson.handleFriend(FriendDataId, FriendFid, "2");

        MyLog.i(TAG, "请求接口-删除好友 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        MyLog.i(TAG, "请求接口-删除好友 result = " + result);

                        waitDialog.close();

                        NewFriendInfoBean mNewFriendInfoBean = ResultJson.NewFriendInfoBean(result);

                        //请求成功
                        if (mNewFriendInfoBean.isRequestSuccess()) {

                            if (mNewFriendInfoBean.isNewFriendSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-搜索好友 成功");
                                BaseApplication.isAddFriend = true;
                                finish();
                            } else if (mNewFriendInfoBean.isNewFriendSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-搜索好友 失败");
                            } else if (mNewFriendInfoBean.isNewFriendSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-搜索好友 无数据");
                            } else {
                                MyLog.i(TAG, "请求接口-搜索好友 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }

                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-搜索好友 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);

                        }


                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub


                        MyLog.i(TAG, "请求接口-删除好友 请求失败 = message = " + arg0.getMessage());
                        waitDialog.close();
                        AppUtils.showToast(mContext, R.string.server_try_again_code0);


                        return;
                    }
                });


    }

    void updateUi() {

        if (!JavaUtil.checkIsNull(FriendNickName)) {
            public_head_title.setText(FriendNickName);
            if (!JavaUtil.checkIsNull(FriendRemarksName)) {
                public_head_title.setText(FriendRemarksName + "(" + FriendNickName + ")");
            } else {
                public_head_title.setText(FriendNickName);
            }
        } else {
            public_head_title.setText("");
        }


//        if (friendHearUrl != null) {
//            BitmapUtils bitmapUtils = new BitmapUtils(mContext);
//            bitmapUtils.display(ci_public_user_head, friendHearUrl);
//        }

//        MyLog.i(TAG, "updateUi()-更新UI");
//
//        MovementInfo mMovementInfo = mMovementInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime());
//        SleepInfo mSleepInfo = mSleepInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime());
////        HeartInfo mHeartInfo = mHeartInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime());
////        HealthInfo mHealthInfo = mHealthInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime());
//
//
//        //模拟运动数据
//        mMovementInfo.setCalorie("59");
//        mMovementInfo.setTotal_step("8500");
//        mMovementInfo.setDisance("1.22");
//        mMovementInfo.setData("200,1,1,400,500,658,100,595,1022,1152,115,995,551,334,119,332,1115,101,778,815,331,1,1,200");
//
//        if (mMovementInfo != null) {
//            initExerciseData(mMovementInfo);
//        }
//
//        //模拟睡眠数据
//        String model_dta = "289,611,1122,1636,1730,2755,4130,4547,5090,5539,6274,8771,9826,12611,13154,13380,14882,14917";
//        mSleepInfo.setData(model_dta);
//
//        if (mSleepInfo != null) {
//            initSleepData(mSleepInfo);
//        }


//        //是否支持连续心率
//        if (mBleDeviceTools.get_is_support_persist_heart() == 1) {
//            //模拟整点心率
//            mWoHeartInfo = new WoHeartInfo();
////            mPoHeartInfo.setData("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
//            mWoHeartInfo.setData("118,156,0,55,0,77,0,0,0,0,0,0,0,79,0,0,0,0,0,0,0,0,100,0,0,0,0,150,0,0,0,0,0,112,113,104,97,67,68,69,71,68,65,66,57,102,99,131,103,104,105,105,111,97,87,87,79,86,84,91,45,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
//            if (mWoHeartInfo != null) {
//                initWoHeartData(mWoHeartInfo);
//            }
//        } else {
//            //模拟整点心率
//            mPoHeartInfo = new PoHeartInfo();
////            mPoHeartInfo.setData("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
//            mPoHeartInfo.setData("120,151,105,130,131,131,94,113,124,108,142,130,131,156,122,123,0,0,0,0,0,0,0,0");
//
//            if (mPoHeartInfo != null) {
//                initPoHeartData(mPoHeartInfo);
//            }
//        }


//        //模拟健康数据
//        mHealthInfo = new HealthInfo();
//
//        mHealthInfo.setEcg_data("");
//        mHealthInfo.setPpg_data("");
//        mHealthInfo.setHealth_heart("76");
//        mHealthInfo.setHealth_systolic("129");
//        mHealthInfo.setHealth_diastolic("79");
//        mHealthInfo.setHealth_ecg_report("1");
//
//        mHealthInfo.setIndex_body_load("79");
//        mHealthInfo.setIndex_body_quality("81");
//        mHealthInfo.setIndex_cardiac_function("77");
//        mHealthInfo.setIndex_fatigue_index("83");
//        mHealthInfo.setIndex_health_index("80");
//
//        mHealthInfo.setMeasure_time("2019-03-18 15:15:15");
//
//
//        if (mHealthInfo != null) {
//            initHealthData(mHealthInfo);
//        }

    }

    /**
     * 刷新健康数据
     *
     * @param mHealthInfo
     */
    public void initHealthData(HealthInfo mHealthInfo) {


        if (mHealthInfo != null) {

            MyLog.i(TAG, " mHealthInfo = " + mHealthInfo.toString());

            String s_heart = !JavaUtil.checkIsNull(mHealthInfo.getHealth_heart()) ? mHealthInfo.getHealth_heart() : "0";
            String s_systolic = !JavaUtil.checkIsNull(mHealthInfo.getHealth_systolic()) ? mHealthInfo.getHealth_systolic() : "0";
            String s_diastole = !JavaUtil.checkIsNull(mHealthInfo.getHealth_diastolic()) ? mHealthInfo.getHealth_diastolic() : "0";
            String s_index = !JavaUtil.checkIsNull(mHealthInfo.getIndex_health_index()) ? mHealthInfo.getIndex_health_index() : "0";
            String s_time = !JavaUtil.checkIsNull(mHealthInfo.getMeasure_time()) ? mHealthInfo.getMeasure_time() : "";
            String sensor = !JavaUtil.checkIsNull(mHealthInfo.getSensor_type()) ? mHealthInfo.getSensor_type() : "";


//            传感器类型(0=ECG,1,ECG+PPG,2=PPG,3=无)(4=心电手环+加离线心电，5=心电手环+离线血压，6=运动手环+离线血压)
            //心电手环-ECG测量
            if (mHealthInfo.isEcgDevice()) {
                rl_friend_info_ecg_view.setVisibility(View.VISIBLE);
                rl_friend_rank_bp_view.setVisibility(View.GONE);
            }
            //运动手环-PPG测量
            else {
                rl_friend_info_ecg_view.setVisibility(View.GONE);
                rl_friend_rank_bp_view.setVisibility(View.VISIBLE);
            }

            //心电手环-ECG测量
            if (mHealthInfo.isEcgDevice()) {
                tv_friend_info_ecg_heart.setText(s_heart);
                tv_friend_info_ecg_sdp.setText(s_systolic);
                tv_friend_info_ecg_dbp.setText(s_diastole);
                tv_friend_info_ecg_time.setText(s_time);

                tv_friend_info_health_state.setVisibility(View.VISIBLE);

                if (!JavaUtil.checkIsNull(s_heart) && !JavaUtil.checkIsNull(s_index) && Integer.valueOf(s_index) > 0) {

                    tv_friend_info_health_number.setText(s_index);

                    if (Integer.valueOf(s_index) <= 70) {
                        tv_friend_info_health_state.setText(getString(R.string.health_index_sub));
                    } else if (Integer.valueOf(s_index) > 70 && Integer.valueOf(s_index) < 90) {
                        tv_friend_info_health_state.setText(getString(R.string.health_index_good));
                    } else if (Integer.valueOf(s_index) >= 90) {
                        tv_friend_info_health_state.setText(getString(R.string.health_index_optimal));
                    }


                    if (Integer.valueOf(s_heart) <= 50) {
                        setState(tv_friend_info_ecg_state, 1);
//                    setState(tv_friend_info_bp_state, 1);
                    } else if (Integer.valueOf(s_heart) > 50 && Integer.valueOf(s_index) < 90) {
                        setState(tv_friend_info_ecg_state, 2);
//                    setState(tv_friend_info_bp_state, 2);
                    } else if (Integer.valueOf(s_heart) >= 90) {
                        setState(tv_friend_info_ecg_state, 3);
//                    setState(tv_friend_info_bp_state, 3);
                    }

                } else {
                    tv_friend_info_health_state.setVisibility(View.GONE);
                }

            } else {
                tv_friend_info_health_state.setVisibility(View.GONE);
                tv_friend_info_bp_heart.setText(s_heart);
                tv_friend_info_bp_sbp.setText(s_diastole);
                tv_friend_info_bp_dbp.setText(s_systolic);
                tv_friend_info_bp_time.setText(s_time);
            }


        }

    }


    /**
     * 更新运动数据
     *
     * @param mMovementInfo
     */
    public void initExerciseData(MovementInfo mMovementInfo) {


        String steps = !JavaUtil.checkIsNull(mMovementInfo.getTotal_step()) ? mMovementInfo.getTotal_step() : "0";
        String calory = !JavaUtil.checkIsNull(mMovementInfo.getCalorie()) ? mMovementInfo.getCalorie() : "0";
        String distance = !JavaUtil.checkIsNull(mMovementInfo.getDisance()) ? mMovementInfo.getDisance() : "0";
        String targetSteps = mUserSetTools.get_user_exercise_target();
        String sport_data = mMovementInfo.getData();


        MyLog.i(TAG, "数据库-获取运动数据 steps  = " + steps);
        MyLog.i(TAG, "数据库-获取运动数据 calory  = " + calory);
        MyLog.i(TAG, "数据库-获取运动数据 distance  = " + distance);
        MyLog.i(TAG, "数据库-获取运动数据 targetSteps  = " + targetSteps);
        MyLog.i(TAG, "数据库-获取运动数据 sport_data  = " + sport_data);


        distance = distance.replace(",", ".");

        if (mBleDeviceTools.get_device_unit() == 1) {
            tv_friend_info_sport_distance_unit.setText(getString(R.string.sport_distance_unit));
//            distance = AppUtils.GetFormat(2, Float.valueOf(distance) + 0.005f);
            distance = AppUtils.GetTwoFormat(Float.valueOf(distance));
        } else {
            tv_friend_info_sport_distance_unit.setText(getString(R.string.unit_mi));
//            distance = AppUtils.GetFormat(2, (Float.valueOf(distance) + 0.005f) / 1.61f);
            //这里用的是本地的身高，后期估计需要-修改。
            distance = AppUtils.GetTwoFormat(Float.valueOf(BleTools.getBritishSystem(steps)));
        }

        mUserSetTools.set_user_stpe(Integer.valueOf(steps));
        tv_friend_info_sport_step.setText(steps);
        tv_friend_info_sport_distance.setText(distance);
        tv_friend_info_sport_calory.setText(calory);


    }


    /**
     * 更新睡眠数据
     *
     * @param mSleepInfo
     */
    public void initSleepData(SleepInfo mSleepInfo) {


        if (!JavaUtil.checkIsNull(mSleepInfo.getData())) {
            SleepModel mSleepModel = new SleepModel(mSleepInfo);
            if (!JavaUtil.checkIsNull(mSleepModel.getSleepTotalTime())) {


                String s_day_sleep_time = mSleepModel.getSleepSleepTime();
                String s_sleep_deep_time = mSleepModel.getSleepDeep();
                String s_sleep_light_time = mSleepModel.getSleepLight();
                String s_sleep_sober_time = mSleepModel.getSleepSoberTime();

                String s_sleep_deep_prorportion = String.valueOf(Integer.valueOf(s_sleep_deep_time) * 100 / Integer.valueOf(s_day_sleep_time));
                String s_sleep_light_prorportion = String.valueOf(Integer.valueOf(s_sleep_light_time) * 100 / Integer.valueOf(s_day_sleep_time));
                String s_sleep_sober_prorportion = String.valueOf(Integer.valueOf(s_sleep_sober_time) * 100 / Integer.valueOf(s_day_sleep_time));

                int progress1 = Integer.parseInt(mSleepModel.getSleepDeep());
                int progress2 = Integer.parseInt(mSleepModel.getSleepLight());
                int progress3 = Integer.parseInt(mSleepModel.getSleepSoberTime());

                int deepProportion = 0;
                int lightProportion = 0;
                int wokeProportion = 0;

                int total = progress1 + progress2 + progress3;

                deepProportion = progress1 * 100 / total;

                if (progress3 == 0) {
                    lightProportion = 100 - progress1 * 100 / total;
                    wokeProportion = 0;
                } else if (progress3 * 100 / total == 0) {
                    lightProportion = 100 - progress1 * 100 / total - 1;
                    wokeProportion = 1;
                } else {
                    lightProportion = 100 - progress1 * 100 / total - progress3 * 100 / total;
                    wokeProportion = progress3 * 100 / total;
                }
                tv_friend_info_sleep_deep_proportion.setText(String.valueOf(deepProportion));
                tv_friend_info_sleep_light_proportion.setText(String.valueOf(lightProportion));
                tv_friend_info_sleep_sober_proportion.setText(String.valueOf(wokeProportion));

                //睡眠时间
                tv_friend_info_sleep_time_h.setText(MyTime.getSleepTime_H(s_day_sleep_time, mContext.getString(R.string.sleep_gang)));
                tv_friend_info_sleep_time_m.setText(MyTime.getSleepTime_M(s_day_sleep_time, mContext.getString(R.string.sleep_gang)));

                tv_friend_info_sleep_deep_time_h.setText(MyTime.getSleepTime_H(s_sleep_deep_time, mContext.getString(R.string.sleep_gang)));
                tv_friend_info_sleep_deep_time_m.setText(MyTime.getSleepTime_M(s_sleep_deep_time, mContext.getString(R.string.sleep_gang)));

                tv_friend_info_sleep_light_time_h.setText(MyTime.getSleepTime_H(mSleepModel.getSleepLight(), mContext.getString(R.string.sleep_gang)));
                tv_friend_info_sleep_light_time_m.setText(MyTime.getSleepTime_M(mSleepModel.getSleepLight(), mContext.getString(R.string.sleep_gang)));

                tv_friend_info_sleep_sober_time_h.setText(MyTime.getSleepTime_H(mSleepModel.getSleepSoberTime(), mContext.getString(R.string.sleep_gang)));
                tv_friend_info_sleep_sober_time_m.setText(MyTime.getSleepTime_M(mSleepModel.getSleepSoberTime(), mContext.getString(R.string.sleep_gang)));

            } else {
                tv_friend_info_sleep_time_h.setText(mContext.getString(R.string.sleep_gang));
                tv_friend_info_sleep_time_m.setText(mContext.getString(R.string.sleep_gang));

                tv_friend_info_sleep_deep_time_h.setText(mContext.getString(R.string.sleep_gang));
                tv_friend_info_sleep_deep_time_m.setText(mContext.getString(R.string.sleep_gang));

                tv_friend_info_sleep_light_time_h.setText(mContext.getString(R.string.sleep_gang));
                tv_friend_info_sleep_light_time_m.setText(mContext.getString(R.string.sleep_gang));

                tv_friend_info_sleep_sober_time_h.setText(mContext.getString(R.string.sleep_gang));
                tv_friend_info_sleep_sober_time_m.setText(mContext.getString(R.string.sleep_gang));

            }
        } else {
            tv_friend_info_sleep_time_h.setText(mContext.getString(R.string.sleep_gang));
            tv_friend_info_sleep_time_m.setText(mContext.getString(R.string.sleep_gang));

            tv_friend_info_sleep_deep_time_h.setText(mContext.getString(R.string.sleep_gang));
            tv_friend_info_sleep_deep_time_m.setText(mContext.getString(R.string.sleep_gang));

            tv_friend_info_sleep_light_time_h.setText(mContext.getString(R.string.sleep_gang));
            tv_friend_info_sleep_light_time_m.setText(mContext.getString(R.string.sleep_gang));

            tv_friend_info_sleep_sober_time_h.setText(mContext.getString(R.string.sleep_gang));
            tv_friend_info_sleep_sober_time_m.setText(mContext.getString(R.string.sleep_gang));
        }


        //模拟数据
//        tv_day_sleep_time.setText(MyTime.getHours("435"));
//        tv_day_sleep_deep_time.setText(MyTime.getHours("100"));
//        tv_day_sleep_light_time.setText(MyTime.getHours("120"));
//        tv_day_sleep_sober_time.setText(MyTime.getHours("25"));

//        day_sleep_deep_proportion.setText(AnalyticalUtils.getCompletionRateSleep(mUserSetTools, "100"));
//        day_sleep_light_proportion.setText(AnalyticalUtils.getCompletionRateSleep(mUserSetTools, "120"));
//        day_sleep_sober_proportion.setText(AnalyticalUtils.getCompletionRateSleep(mUserSetTools, "25"));

//        tv_day_sleep_taget.setText(MyTime.getHours(targetSleep));
//        sb_day_sleep_target.setMax(Integer.valueOf(targetSleep));
//        sb_day_sleep_target.setProgress(Integer.valueOf("435"));


        //模拟睡眠数据
//        String model_dta = "48577,48802,803,1314,4259,5154,5956,5986,7331,7906,15493";
//        String model_dta = "289,611,1122,1636,1730,2755,4130,4547,5090,5539,6274,8771,9826,12611,13154,13380,14882,14917";
//        mSleepInfo.setData(model_dta);
//        showSleepChart(mSleepInfo);


    }

    /**
     * 更新整点心率数据
     * <p>
     * //     * @param mWoHeartInfo
     */
    private void initHeartData(HeartInfo mHeartInfo) {

        MyLog.i(TAG, "心率 mHeartInfo = " + mHeartInfo.toString());

        HeartModel mWoHeartModel = new HeartModel(mHeartInfo);

        MyLog.i(TAG, "整点心率 mWoHeartModel = " + mWoHeartModel.toString());

        //最近一次心率
        String last_heart = mWoHeartModel.getLastHeart();
        //全天平均心率
        String day_avg_heart = mWoHeartModel.getHeartDayAverage();
        //全天最高心率
        String day_max_heart = mWoHeartModel.getHeartDayMax();
        //全天最低心率
        String day_min_heart = mWoHeartModel.getHeartDayMin();

        MyLog.i(TAG, "整点心率 last_heart = " + last_heart);
        MyLog.i(TAG, "整点心率 day_avg_heart = " + day_avg_heart);
        MyLog.i(TAG, "整点心率 day_max_heart = " + day_max_heart);
        MyLog.i(TAG, "整点心率 day_min_heart = " + day_min_heart);


        if (!JavaUtil.checkIsNull(day_avg_heart)) {

            tv_friend_info_heart_last.setText(last_heart);
            tv_friend_info_heart_avg.setText(day_avg_heart);
            tv_friend_info_heart_height.setText(day_max_heart);
            tv_friend_info_heart_low.setText(day_min_heart);

        } else {

            tv_friend_info_heart_last.setText(mContext.getString(R.string.sleep_gang));
            tv_friend_info_heart_avg.setText(mContext.getString(R.string.sleep_gang));
            tv_friend_info_heart_height.setText(mContext.getString(R.string.sleep_gang));
            tv_friend_info_heart_low.setText(mContext.getString(R.string.sleep_gang));

        }


    }


    void setState(TextView tv_view, int tag) {

        if (tag == 1) {
            tv_view.setText(getString(R.string.user_par_state1));
            tv_view.setBackgroundResource(R.drawable.my_circle_bg_two_1);
            tv_view.setTextColor(getResources().getColor(R.color.my_circle_bg_two_1_bg));
        } else if (tag == 2) {
            tv_view.setText(getString(R.string.user_par_state2));
            tv_view.setBackgroundResource(R.drawable.my_circle_bg_two_2);
            tv_view.setTextColor(getResources().getColor(R.color.my_circle_bg_two_2_bg));
        } else if (tag == 3) {
            tv_view.setText(getString(R.string.user_par_state3));
            tv_view.setBackgroundResource(R.drawable.my_circle_bg_two_3);
            tv_view.setTextColor(getResources().getColor(R.color.my_circle_bg_two_3_bg));
        }

    }


    private void requestSportData(final String date) {

//        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.getFriendSportListData(date, date, FriendFid);

        MyLog.i(TAG, "请求接口-获取运动数据 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

//                        waitDialog.close();


                        MyLog.i(TAG, "请求接口-获取运动数据 请求成功 = result = " + result.toString());

                        SportBean mSportBean = ResultJson.SportBean(result);

                        //请求成功
                        if (mSportBean.isRequestSuccess()) {
                            if (mSportBean.isGetSportSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取运动数据 成功");
                                ResultSportDataParsing(mSportBean, date);
                            } else if (mSportBean.isGetSportSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取运动数据 失败");
                            } else if (mSportBean.isGetSportSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-获取运动数据 无数据");
                            } else {
                                MyLog.i(TAG, "请求接口-获取运动数据 请求异常(1)");
                            }

                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取运动数据 请求异常(0)");

                        }

                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        MyLog.i(TAG, "请求接口-获取运动数据 请求失败 = message = " + arg0.getMessage());
//                        waitDialog.close();
//                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                        return;
                    }
                });


    }


    /**
     * 解析数据
     */
    private void ResultSportDataParsing(SportBean mSportBean, String date) {

        MyLog.i(TAG, "请求接口-获取运动数据 size = " + mSportBean.getData().size());

        if (mSportBean.getData().size() > 0) {
            MovementInfo mMovementInfo = mSportBean.getMovementInfo(mSportBean.getData().get(0));
            if (mMovementInfo != null) {
                initExerciseData(mMovementInfo);
            }

        }
    }

    private void requestSleepData(final String date) {

//        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.getSleepListData(date, date, FriendFid);

        MyLog.i(TAG, "请求接口-获取睡眠数据 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

//                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-获取睡眠数据 请求成功 = result = " + result.toString());

                        SleepBean mSleepBean = ResultJson.SleepBean(result);

                        //请求成功
                        if (mSleepBean.isRequestSuccess()) {

                            if (mSleepBean.isGetSleepSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取睡眠数据 成功");
                                ResultSleepDataParsing(mSleepBean, date);
                            } else if (mSleepBean.isGetSleepSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取睡眠数据 失败");
                            } else if (mSleepBean.isGetSleepSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-获取睡眠数据 无数据");
                            } else {
                                MyLog.i(TAG, "请求接口-获取睡眠数据 请求异常(1)");
                            }

                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取睡眠数据 请求异常(0)");
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        MyLog.i(TAG, "请求接口-获取睡眠数据 请求失败 = message = " + arg0.getMessage());
//                        waitDialog.close();
//                        AppUtils.showToast(mContext, R.string.net_worse_try_again);
                        return;
                    }
                });
    }

    /**
     * 解析数据
     */
    private void ResultSleepDataParsing(SleepBean mSleepBean, String date) {


        MyLog.i(TAG, "请求接口-获取睡眠数据 size = " + mSleepBean.getData().size());

        if (mSleepBean.getData().size() > 0) {
            SleepInfo mSleepInfo = mSleepBean.getSleepInfo(mSleepBean.getData().get(0));
            if (mSleepInfo != null) {
                initSleepData(mSleepInfo);
            }
        }
    }

    private void requestWoheartData(final String date) {

//        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.getWoListData(date, date, FriendFid);

        MyLog.i(TAG, "请求接口-获取连续心率数据 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

//                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-获取连续心率数据 请求成功 = result = " + result.toString());

                        HeartBean mHeartBean = ResultJson.HeartBean(result);

                        //请求成功
                        if (mHeartBean.isRequestSuccess()) {

                            if (mHeartBean.isGetHeartSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取连续心率数据 成功");
                                ResultWoHeartDataParsing(mHeartBean, date);
                            } else if (mHeartBean.isGetHeartSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取连续心率数据 失败");
                            } else if (mHeartBean.isGetHeartSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-获取连续心率数据 无数据");
                            } else {
                                MyLog.i(TAG, "请求接口-获取连续心率数据 请求异常(1)");
                            }

                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取连续心率数据 请求异常(0)");
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        MyLog.i(TAG, "请求接口-获取连续心率数据 请求失败 = message = " + arg0.getMessage());
//                        waitDialog.close();
//                        AppUtils.showToast(mContext, R.string.net_worse_try_again);
                        return;
                    }
                });
    }

    /**
     * 解析数据
     */
    private void ResultWoHeartDataParsing(HeartBean mHeartBean, String date) {

        MyLog.i(TAG, "解析 = mWoHeartBean = " + mHeartBean.toString());

        if (mHeartBean.getData().size() >= 1) {

            HeartInfo mHeartInfo = mHeartBean.getHeartInfo(mHeartBean.getData().get(0));

            if (mHeartInfo != null) {
                initHeartData(mHeartInfo);
            }
        }
    }

    private void requestPoheartData(final String date) {

//        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.getPoListData(date, date, FriendFid);

        MyLog.i(TAG, "请求接口-获取整点心率数据 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

//                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-获取整点心率数据 请求成功 = result = " + result.toString());

                        HeartBean mHeartBean = ResultJson.HeartBean(result);

                        //请求成功
                        if (mHeartBean.isRequestSuccess()) {

                            if (mHeartBean.isGetHeartSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取整点心率数据 成功");
                                ResultPoHeartDataParsing(mHeartBean, date);
                            } else if (mHeartBean.isGetHeartSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取整点心率数据 失败");
                            } else if (mHeartBean.isGetHeartSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-获取整点心率数据 无数据");
                            } else {
                                MyLog.i(TAG, "请求接口-获取整点心率数据 请求异常(1)");
                            }

                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取整点心率数据 请求异常(0)");
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        MyLog.i(TAG, "请求接口-获取整点心率数据 请求失败 = message = " + arg0.getMessage());
//                        waitDialog.close();
//                        AppUtils.showToast(mContext, R.string.net_worse_try_again);
                        return;
                    }
                });
    }

    /**
     * 解析数据
     */
    private void ResultPoHeartDataParsing(HeartBean mHeartBean, String date) {

        MyLog.i(TAG, "解析 = mHeartBean = " + mHeartBean.toString());

        if (mHeartBean.getData().size() > 0) {

            HeartInfo mHeartInfo = mHeartBean.getHeartInfo(mHeartBean.getData().get(0));

            if (mHeartInfo != null) {
                initHeartData(mHeartInfo);
            }
        }


    }

    /**
     * 请求当前用户健康数据
     */
    private void requestHealthData(final String date) {

//        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.getdHealthData(date, date, FriendFid);

        MyLog.i(TAG, "请求接口-获取健康数据" + mRequestInfo.getRequestJson());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub
//                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-获取健康数据 result = " + result);

                        HealthBean mHealthBean = ResultJson.HealthBean(result);

                        //请求成功
                        if (mHealthBean.isRequestSuccess()) {
                            if (mHealthBean.isGetHealthSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取健康数据 成功");
                                ResultDataParsing(mHealthBean, date);
                            } else if (mHealthBean.isGetHealthSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取健康数据 失败");
                            } else if (mHealthBean.isGetHealthSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-获取健康数据 无数据");
                            } else {
                                MyLog.i(TAG, "请求接口-获取健康数据 请求异常(1)");
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-上传健康数据 请求异常(0)");
                        }


                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub


                        MyLog.i(TAG, "请求接口-获取健康数据 请求失败 = message = " + arg0.getMessage());
//                        waitDialog.close();

                    }
                });


    }


    /**
     * 解析当前用户数据
     *
     * @param mHealthBean
     */
    private void ResultDataParsing(HealthBean mHealthBean, String date) {


        MyLog.i(TAG, "请求接口-获取健康数据 总大小size = " + mHealthBean.getData().getPageCount());
        MyLog.i(TAG, "请求接口-获取健康数据 当前页size = " + mHealthBean.getData().getHealthList().size());

        List<HealthInfo> new_health_list = new ArrayList<>();

        if (mHealthBean.getData().getHealthList().size() >= 1) {

            MyLog.i(TAG, "请求接口-获取健康数据 getHealthMeasuringTime = " + mHealthBean.getData().getHealthList().get(0).getHealthMeasuringTime());

            List<HealthInfo> health_list = mHealthBean.getHealthInfoList(mHealthBean.getData().getHealthList());


            for (int i = 0; i < health_list.size(); i++) {

                HealthInfo mHealthInfo = health_list.get(i);


                if (mHealthInfo.isValidData()) {
                    new_health_list.add(mHealthInfo);

                }
            }

        }
        if (new_health_list.size() >= 1) {

            Collections.sort(new_health_list, new SortByHealthList());

            HealthInfo mHealthInfo = new_health_list.get(0);
            if (mHealthInfo != null) {
                initHealthData(mHealthInfo);
            }

        }


    }


    class SortByHealthList implements Comparator {
        public int compare(Object o1, Object o2) {


            HealthInfo mHealthInfo1 = (HealthInfo) o1;
            HealthInfo mHealthInfo2 = (HealthInfo) o2;


            String str1 = mHealthInfo1.getMeasure_time();
            String str2 = mHealthInfo2.getMeasure_time();
            int aa = str2.compareTo(str1);


            return aa;

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //添加闹钟
        if (resultCode == FriendRankInfoActivity.FriendRemarksNameCode) {

            FriendRemarksName = data.getStringExtra(IntentConstants.FriendRemarksName);
            String delete_tag = data.getStringExtra(DeleteTag);

            //删除好友操作
            if (delete_tag != null && delete_tag.equals(DeleteTag_YES)) {
                finish();
            } else {
                updateUi();
            }


        }

    }

}
