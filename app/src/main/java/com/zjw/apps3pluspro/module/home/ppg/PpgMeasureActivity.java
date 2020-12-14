package com.zjw.apps3pluspro.module.home.ppg;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.home.ecg.EcgMeasureUitls;
import com.zjw.apps3pluspro.module.home.ecg.EcgMesureHelpActivity;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.CalibrationData;
import com.zjw.apps3pluspro.network.entity.HealthData;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.HealthBean;
import com.zjw.apps3pluspro.network.javabean.UserBean;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.dbmanager.HealthInfoUtils;
import com.zjw.apps3pluspro.sql.entity.HealthInfo;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;

import java.util.ArrayList;


/**
 * PPG测量
 */
public class PpgMeasureActivity extends BaseActivity implements OnClickListener {
    private final String TAG = PpgMeasureActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    //数据库存储
    private HealthInfoUtils mHealthInfoUtils = BaseApplication.getHealthInfoUtils();

    private Handler mHandler;

    //锁屏
    PowerManager.WakeLock wakeLock = null;
    //动画
    private Animation myAnimation1, myAnimation2, myAnimation3;
    //倒计时
    private TextView measure_count_down_number;

    private ImageView my_hear_img1, my_hear_img2, my_hear_img3;
    private RelativeLayout rl_my_hear;

    private TextView public_head_title;
    private TextView ppg_measure_heart, ppg_measure_sbp, ppg_measure_dbp;
    private LinearLayout ll_ppg_measure_bp_view;
    //View
    private LinearLayout ll_ppg_measure_bootom_view1, ll_ppg_measure_bootom_view2, ll_ppg_measure_bootom_view3, ll_ppg_measure_bootom_view4;
    private LinearLayout public_help, public_cancel;
    private TextView ppg_measure_analysis;

    private int NowHeart = 0;

    //加载圈
    private WaitDialog waitDialog;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_ppg_measure;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = PpgMeasureActivity.this;
        waitDialog = new WaitDialog(mContext);
        waitDialog.show(getString(R.string.ignored));
        mHandler = new Handler();

        //应用运行时，保持屏幕高亮，不锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initView();
        initBroadcast();
        initData();

        waitDialog.close();

        startPPGMeasure();
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onResume() {
        super.onResume();
        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.ON_AFTER_RELEASE, TAG);
        wakeLock.acquire();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (wakeLock != null) {
            wakeLock.release();
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
    }

    private boolean isDestroy = false;
    @Override
    protected void onDestroy() {
        isDestroy = true;

        setEcgMeasure(false);

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        if (TimerOneHandler != null) {
            TimerOneHandler.removeCallbacksAndMessages(null);
        }

        if (TimerFiveHandler != null) {
            TimerFiveHandler.removeCallbacksAndMessages(null);
        }
        closeEcg();

        if (broadcastReceiver != null) {
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {
            }
        }
        super.onDestroy();

    }


    boolean IsCalibration = false;


    private void initView() {


        findViewById(R.id.public_head_back).setOnClickListener(this);
        public_head_title = (TextView) findViewById(R.id.public_head_title);
        public_head_title.setText(getString(R.string.measure));

        public_help = (LinearLayout) findViewById(R.id.public_help);
        public_cancel = (LinearLayout) findViewById(R.id.public_cancel);
        public_help.setOnClickListener(this);
        public_cancel.setOnClickListener(this);


        my_hear_img1 = (ImageView) findViewById(R.id.my_hear_img1);
        my_hear_img2 = (ImageView) findViewById(R.id.my_hear_img2);
        my_hear_img3 = (ImageView) findViewById(R.id.my_hear_img3);
        rl_my_hear = (RelativeLayout) findViewById(R.id.rl_my_hear);

        ppg_measure_sbp = (TextView) findViewById(R.id.ppg_measure_sbp);
        ppg_measure_dbp = (TextView) findViewById(R.id.ppg_measure_dbp);
        ppg_measure_heart = (TextView) findViewById(R.id.ppg_measure_heart);
        ppg_measure_analysis = (TextView) findViewById(R.id.ppg_measure_analysis);
        measure_count_down_number = (TextView) findViewById(R.id.measure_count_down_number);

        ll_ppg_measure_bp_view = (LinearLayout) findViewById(R.id.ll_ppg_measure_bp_view);

        ll_ppg_measure_bootom_view1 = (LinearLayout) findViewById(R.id.ll_ppg_measure_bootom_view1);
        ll_ppg_measure_bootom_view2 = (LinearLayout) findViewById(R.id.ll_ppg_measure_bootom_view2);
        ll_ppg_measure_bootom_view3 = (LinearLayout) findViewById(R.id.ll_ppg_measure_bootom_view3);
        ll_ppg_measure_bootom_view4 = (LinearLayout) findViewById(R.id.ll_ppg_measure_bootom_view4);


        findViewById(R.id.start_ppg).setOnClickListener(this);
        findViewById(R.id.restart_ppg).setOnClickListener(this);
        findViewById(R.id.ppg_measure_help).setOnClickListener(this);


    }

    void initData() {

        myAnimation1 = AnimationUtils.loadAnimation(this, R.anim.my_a);
        myAnimation2 = AnimationUtils.loadAnimation(this, R.anim.my_a);
        myAnimation3 = AnimationUtils.loadAnimation(this, R.anim.my_a);


        try {
            Intent intent = this.getIntent();
            if (intent.getExtras().getString(IntentConstants.MesureType) != null) {
                //校准
                if (intent.getExtras().getString(IntentConstants.MesureType).equals(IntentConstants.MesureType_Calibration)) {
                    IsCalibration = true;
                    public_head_title.setText(getText(R.string.jiaozhun_title));
                    ll_ppg_measure_bp_view.setVisibility(View.GONE);
                }
                //测量
                else {
                    IsCalibration = false;
                    public_head_title.setText(getText(R.string.measure_title));
                    ll_ppg_measure_bp_view.setVisibility(View.VISIBLE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        MyLog.i(TAG, "是否是校准 = IsCalibration = " + IsCalibration);


    }


    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.public_head_back:
                finish();
                break;


            case R.id.start_ppg:

                startPPGMeasure();


                break;

            case R.id.restart_ppg:
//                updateMeasureUi(1);
                startPPGMeasure();
                break;

            case R.id.public_help:
                Intent intent1 = new Intent(mContext, EcgMesureHelpActivity.class);
                startActivity(intent1);

                break;

            case R.id.public_cancel:
                QuitUserDialog();
                break;

            case R.id.ppg_measure_help:
                Intent intent2 = new Intent(mContext, EcgMesureHelpActivity.class);
                startActivity(intent2);
                break;


        }
    }


    /**
     * 清除数据
     */
    void clearData() {
        setEcgMeasure(true);
        NowHeart = 0;
    }


    private void initBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastTools.ACCTION_GATT_HEART_DATA);
        filter.setPriority(1000);
        registerReceiver(broadcastReceiver, filter);
    }

    // 广播
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @SuppressWarnings({"unused", "unused"})
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            switch (action) {

                case BroadcastTools.ACCTION_GATT_HEART_DATA:

                    Bundle b = intent.getExtras();
                    int device_heart = b.getInt(BroadcastTools.INTENT_PUT_HEART_DATA);
                    MyLog.i(TAG, "测试心率值 这里接到值了 =  device_heart = " + device_heart);
                    if (device_heart <= 0) {
                        NowHeart = 0;
                    } else {
                        NowHeart = device_heart;
                    }
                    break;


            }
        }
    };


    //============调用蓝牙指令==========

    private void OpenPpg() {
        MyLog.i(TAG, "测量开关 = OpenPpg()");
        openEcg();
    }

    private void ClosePpg() {
        MyLog.i(TAG, "测量开关 = ClosePpg()");
        closeEcg();
    }


    //=============阶段标志位==============
    private int StageTag = 1;

    //======== 第一个定时器====================
    private boolean TimerOneIsStop = false;
    private int TimerOnedown = 30;

    private Handler TimerOneHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 添加更新ui的代码
                    if (!TimerOneIsStop) {


                        ppg_measure_heart.setText(String.valueOf(NowHeart));
                        ppg_measure_sbp.setText(String.valueOf(EcgMeasureUitls.getMeasureSBP(mUserSetTools, NowHeart)));
                        ppg_measure_dbp.setText(String.valueOf(EcgMeasureUitls.getMeasureDBP(mUserSetTools, NowHeart)));

                        TimerOneHandler.sendEmptyMessageDelayed(1, 1000);
                        TimerOnedown -= 1;

                        measure_count_down_number.setText(String.valueOf(TimerOnedown));

                        MyLog.i(TAG, "定时器1 = 回调 TimerOnedown = " + TimerOnedown);

                        if (TimerOnedown <= 0) {
                            TimerOneStop();

                            MyLog.i(TAG, "交互状态 = 测量结束");

                            handleMeasureEnd();
                        } else {
                            if (TimerOnedown % 4 == 0) {
//                            rl_my_hear.startAnimation(myAnimation);
                                my_hear_img1.startAnimation(myAnimation1);
                            } else if (TimerOnedown % 4 == 1) {
                                my_hear_img2.startAnimation(myAnimation2);
                            } else if (TimerOnedown % 4 == 2) {
                                my_hear_img3.startAnimation(myAnimation3);
                            } else if (TimerOnedown % 4 == 2) {
                                my_hear_img3.startAnimation(myAnimation3);
                            } else if (TimerOnedown % 4 == 3) {
//                                my_hear_img3.startAnimation(myAnimation3);
                            }


                        }
                    }
                    break;
                case 0:
                    break;
            }
        }

    };

    private void TimerOneStart() {

        MyLog.i(TAG, "定时器1 = 回调 TimerOneStart()");
        MyLog.i(TAG, "交互状态 = 正在测量");

        TimerOneHandler.sendEmptyMessage(1);
        TimerOneIsStop = false;

    }

    private void TimerOneStop() {

        MyLog.i(TAG, "定时器1 = 回调 TimerOneStop()");

        TimerOneHandler.sendEmptyMessage(0);
        TimerOneIsStop = true;
        TimerOnedown = 30;
    }


    //======== 第五个定时器====================
    private boolean TimerFiveIsStop = false;
    private int TimerFivedown = 4;

    private Handler TimerFiveHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 添加更新ui的代码
                    if (!TimerFiveIsStop) {


                        TimerFiveHandler.sendEmptyMessageDelayed(1, 1000);

                        if (!TimerFiveIsStop) {

                            MyLog.i(TAG, "定时器5 = 回调 TimerFivedown = " + TimerFivedown);

                            TimerFivedown -= 1;
                            if (TimerFivedown == 3) {
                                ppg_measure_analysis.setText(".");
                            } else if (TimerFivedown == 2) {
                                ppg_measure_analysis.setText("..");
                            } else if (TimerFivedown == 1) {
                                ppg_measure_analysis.setText("...");
                            } else if (TimerFivedown == 0) {
                                ppg_measure_analysis.setText("...");
                            }

                            if (TimerFivedown <= 0) {

                                TimerFiveStop();
                                MyLog.i(TAG, "交互状态 = 分析结束，需要跳转");
                                handleMeasureRsult();


                            }
                        }
                    }
                    break;
                case 0:
                    break;
            }
        }
    };


    private void TimerFiveStart() {

        MyLog.i(TAG, "定时器5 = 回调 TimerFiveStart()");

        TimerFiveHandler.sendEmptyMessage(1);
        TimerFiveIsStop = false;


    }

    private void TimerFiveStop() {

        MyLog.i(TAG, "定时器5 = 回调 TimerFiveStop()");

        TimerFiveHandler.sendEmptyMessage(0);
        TimerFiveIsStop = true;
        TimerFivedown = 4;
    }


    //===============测量UI切换=================

    void updateMeasureUi(int type) {

        ll_ppg_measure_bootom_view1.setVisibility(View.GONE);
        ll_ppg_measure_bootom_view2.setVisibility(View.GONE);
        ll_ppg_measure_bootom_view3.setVisibility(View.GONE);
        ll_ppg_measure_bootom_view4.setVisibility(View.GONE);

        StageTag = type;

        //初始界面
        if (StageTag == 1) {
            ll_ppg_measure_bootom_view1.setVisibility(View.VISIBLE);
            updateTopTitleUi(1);
            //测量中
        } else if (StageTag == 2) {
            ll_ppg_measure_bootom_view2.setVisibility(View.VISIBLE);
            updateTopTitleUi(2);
            //测量失败
        } else if (StageTag == 3) {
            ll_ppg_measure_bootom_view3.setVisibility(View.VISIBLE);
            updateTopTitleUi(1);
        } else if (StageTag == 4) {
            ll_ppg_measure_bootom_view4.setVisibility(View.VISIBLE);
            updateTopTitleUi(1);
        }

    }


    /**
     * @param tag 1=未测量  2 =测量中
     */
    void updateTopTitleUi(int tag) {

        public_help.setVisibility(View.GONE);
        public_cancel.setVisibility(View.GONE);

        if (tag == 1) {
//            public_help.setVisibility(View.VISIBLE);
        } else {
            public_cancel.setVisibility(View.VISIBLE);

        }

    }


    void handleMeasureEnd() {

        ClosePpg();

        MyLog.i(TAG, "NowHeart = " + NowHeart);

        //最后结果是否合法
        if (NowHeart > DefaultVale.USER_HEART_MAX || NowHeart < DefaultVale.USER_HEART_MIN) {
            MyLog.i(TAG, "测量结果 = 数值不合法");
            //测量失败
            handleMeasureFail();
            return;
        }

        handleMeasureSuccess();


    }

    void handleMeasureFail() {
        MyLog.i(TAG, "handleMeasureFail()");
        clearData();
        updateMeasureUi(3);

    }


    HealthInfo mHealthInfo;

    void handleMeasureSuccess() {
        MyLog.i(TAG, "handleMeasureSuccess()");


        //如果是校准
        if (IsCalibration) {

            if (NowHeart < 50) {
                mUserSetTools.set_calibration_heart(50);
            } else if (NowHeart > 110) {
                mUserSetTools.set_calibration_heart(110);
            } else {
                mUserSetTools.set_calibration_heart(NowHeart);
            }

            //上传校准值给手环
            ResultCalibrationHeart();
            //上传校准值到后台
            CalibrationData mCalibrationData = new CalibrationData();
            mCalibrationData.setCalibrationHeart(String.valueOf(mUserSetTools.get_calibration_heart()));
            mCalibrationData.setCalibrationSystolic(String.valueOf(mUserSetTools.get_calibration_systolic()));
            mCalibrationData.setCalibrationDiastolic(String.valueOf(mUserSetTools.get_calibration_diastolic()));
            uploadCalibrationInfo(mCalibrationData);

        } else {

            mHealthInfo = HealthInfo.getPpgMeasureResult(MyTime.getAllTime(), NowHeart, getEcgPtpAvg());

            MyLog.i(TAG, "mHealthInfo = " + mHealthInfo.toString());

            //这里需要上传测量结果给手环
            ResultMeasureHeart(mHealthInfo);

            updateMeasureUi(4);
            TimerFiveStart();

            //这里需要上传测量结果给后台，和保存到本地
            saveHealthDdata(mHealthInfo);
        }


    }


    void QuitUserDialog() {
        new android.app.AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.dialog_prompt))//设置对话框标题

                .setMessage(getString(R.string.quit_measure_tip))//设置显示的内容

                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {//添加确定按钮

                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        finish();
                    }

                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {//添加返回按钮


            @Override

            public void onClick(DialogInterface dialog, int which) {//响应事件

                // TODO Auto-generated method stub


            }

        }).show();//在按键响应事件中显示此对话框

    }


    void handleMeasureRsult() {

        if (mHealthInfo != null) {
            Intent intent = new Intent(mContext, PpgMesureDetailsActivity.class);
            intent.putExtra(IntentConstants.HealthInfo, mHealthInfo);
            startActivity(intent);
            finish();
        }

    }

    void startPPGMeasure() {
        if (HomeActivity.ISBlueToothConnect()) {
            clearData();
            OpenPpg();
            updateMeasureUi(2);
            TimerOneStart();
        } else {
            AppUtils.showToast(mContext, R.string.no_connection_notification);
        }
    }

    /**
     * 上传校准信息到服务器
     */
    private void uploadCalibrationInfo(CalibrationData mCalibrationData) {

        waitDialog.show(getString(R.string.loading0));


        RequestInfo mRequestInfo = RequestJson.modifyCalibrationInfo(mCalibrationData);

        MyLog.i(TAG, "请求接口-修改校准信息 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub
                        if(isDestroy){
                            return;
                        }
                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-修改校准信息  result = " + result.toString());

                        UserBean mUserBean = ResultJson.UserBean(result);


                        //请求成功
                        if (mUserBean.isRequestSuccess()) {

                            if (mUserBean.uploadUserSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-修改校准信息 成功");

                            } else if (mUserBean.uploadUserSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-修改校准信息 失败");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else {
                                MyLog.i(TAG, "请求接口-修改校准信息 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-修改个人信息 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);

                        }

                        upitNowActivity();


                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        if(isDestroy){
                            return;
                        }
                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-修改校准信息 请求失败 = message = " + arg0.getMessage());

                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                        upitNowActivity();

                        return;
                    }
                });

    }


    void upitNowActivity() {

        if (IsCalibration) {
            mUserSetTools.set_is_par(1);
            AppUtils.showToast(mContext, R.string.jiaozhun_toast);
            finish();
        }


    }

    void saveHealthDdata(HealthInfo mHealthInfo) {

        MyLog.i(TAG, "插入健康表 =  mHealthInfo = " + mHealthInfo.toString());
        boolean isSuccess_health = mHealthInfoUtils.MyUpdateData(mHealthInfo);
        if (isSuccess_health) {
            MyLog.i(TAG, "插入健康表成功！");
        } else {
            MyLog.i(TAG, "插入健康表失败！");
        }

        updateHealthyData(mHealthInfo);
    }


    /**
     * 上传健康数据
     */
    private void updateHealthyData(final HealthInfo mHealthInfo) {


        ArrayList<HealthData> health_data_list = new ArrayList<HealthData>();

        health_data_list.add(new HealthData(mContext, mHealthInfo));


        if (health_data_list.size() > 0) {


            waitDialog.show(getString(R.string.loading0));

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
                                    ResultDataParsing(mHealthBean, mHealthInfo);
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

                            if(isDestroy){
                                return;
                            }
                            MyLog.i(TAG, "请求接口-上传健康数据 请求失败 = message = " + arg0.getMessage());
                            waitDialog.close();

                        }
                    });

        }


    }

    /**
     * 解析健康数据
     */
    private void ResultDataParsing(HealthBean mHealthBean, HealthInfo mHealthInfo) {

        MyLog.i(TAG, "解析 = mHealthBean = " + mHealthBean.toString());

        boolean isSuccessHealth = mHealthInfoUtils.MyUpdateToSyncOne(mHealthInfo);

        MyLog.i(TAG, "数据库-健康数据 同步状态 = isSuccessHealth = " + isSuccessHealth);

    }


}
