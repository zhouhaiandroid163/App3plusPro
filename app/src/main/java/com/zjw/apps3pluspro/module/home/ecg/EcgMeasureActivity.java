package com.zjw.apps3pluspro.module.home.ecg;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;

import com.zjw.apps3pluspro.base.BaseActivity;
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
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.dbmanager.HealthInfoUtils;
import com.zjw.apps3pluspro.sql.entity.HealthInfo;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.view.ecg.ECGView;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.utils.MyUtils;


import org.json.JSONObject;

import java.util.ArrayList;


public class EcgMeasureActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = EcgMeasureActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    //数据库存储
    private HealthInfoUtils mHealthInfoUtils = BaseApplication.getHealthInfoUtils();

    //锁屏
    PowerManager.WakeLock wakeLock = null;

    //播放音频
    private MediaPlayer mMediaPlayer;

    //PPG，为了画图流畅，提供服务的
    private ArrayList<Integer> PpgData;
    private int xiabiao = 0;
    private int PpgMax = 0;
    private int PpgMin = 0;


    private String my_ecg_data = "";
    private String my_ppg_data = "";

    private ECGView ecg_measure_ecgview;
    private ECGView ecg_measure_ppgview;
    private Handler mHandler;
//    private AnaPara mAnaPara;

    private static final int MSG_DATA_ECG = 0x11;
    public static final int MSG_PLAY_RBO_SOUND = 0x12;
    private static final int MSG_DATA_PPG = 0x13;

    private TextView public_head_title;
    private TextView ecg_measure_heart, ecg_measure_sbp, ecg_measure_dbp;

    private boolean IsCalibration = false;

    private LinearLayout public_help, public_cancel;
    private LinearLayout ll_ecg_measure_bp_view;

    //View
    private LinearLayout ll_ecg_measure_bootom_view1, ll_ecg_measure_bootom_view2, ll_ecg_measure_bootom_view3, ll_ecg_measure_bootom_view4, ll_ecg_measure_bootom_view5, ll_ecg_measure_bootom_view6, ll_ecg_measure_bootom_view7;


    //测量倒计时
    private TextView measure_count_down_number;
    //准备测量倒计时
    private TextView measure_count_ready_number;
    //手离开电极倒计时
    private TextView measure_count_no_touch_number;
    //分析中
    private TextView ecg_measure_analysis;
    //加载框
    private WaitDialog waitDialog;

    private TextView tvEcgMeasureTitle, tvPpgMeasureTitle;


    @Override
    protected int setLayoutId() {
        return R.layout.activity_ecg_measure;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = EcgMeasureActivity.this;
        waitDialog = new WaitDialog(mContext);
        waitDialog.show(getString(R.string.ignored));
        //PPG
        PpgData = new ArrayList<Integer>();

        //不锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏

        //播放音频
        mMediaPlayer = MediaPlayer.create(mContext, R.raw.dd8);
        initView();
        initBroadcast();
        initData();

        enableNotifacationEcgRead();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                enableNotifacationPpgRead();
            }
        }, 800);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isDestroy) {
                    return;
                }
                waitDialog.close();
                startEcgMesure();
            }
        }, 1500);
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onResume() {
        super.onResume();
        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
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
    protected void onDestroy() {
        waitDialog.dismiss();
        isDestroy = true;
        setEcgMeasure(false);

        if (PpgHandler != null) {
            PpgHandler.removeCallbacksAndMessages(null);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        if (TimerOneHandler != null) {
            TimerOneHandler.removeCallbacksAndMessages(null);
        }

        if (TimerTwoHandler != null) {
            TimerTwoHandler.removeCallbacksAndMessages(null);
        }

        if (TimerThreeHandler != null) {
            TimerThreeHandler.removeCallbacksAndMessages(null);
        }

        if (TimerFourHandler != null) {
            TimerFourHandler.removeCallbacksAndMessages(null);
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


    private void initView() {

        ecg_init();
        ppg_init();
        handler_init();

        findViewById(R.id.public_head_back).setOnClickListener(this);
        public_head_title = (TextView) findViewById(R.id.public_head_title);
        public_head_title.setText(getText(R.string.measure_title));

        public_help = (LinearLayout) findViewById(R.id.public_help);
        public_cancel = (LinearLayout) findViewById(R.id.public_cancel);
        public_help.setOnClickListener(this);
        public_cancel.setOnClickListener(this);

        ecg_measure_heart = (TextView) findViewById(R.id.ecg_measure_heart);
        ecg_measure_sbp = (TextView) findViewById(R.id.ecg_measure_sbp);
        ecg_measure_dbp = (TextView) findViewById(R.id.ecg_measure_dbp);


        ll_ecg_measure_bootom_view1 = (LinearLayout) findViewById(R.id.ll_ecg_measure_bootom_view1);
        ll_ecg_measure_bootom_view2 = (LinearLayout) findViewById(R.id.ll_ecg_measure_bootom_view2);
        ll_ecg_measure_bootom_view3 = (LinearLayout) findViewById(R.id.ll_ecg_measure_bootom_view3);
        ll_ecg_measure_bootom_view4 = (LinearLayout) findViewById(R.id.ll_ecg_measure_bootom_view4);
        ll_ecg_measure_bootom_view5 = (LinearLayout) findViewById(R.id.ll_ecg_measure_bootom_view5);
        ll_ecg_measure_bootom_view6 = (LinearLayout) findViewById(R.id.ll_ecg_measure_bootom_view6);
        ll_ecg_measure_bootom_view7 = (LinearLayout) findViewById(R.id.ll_ecg_measure_bootom_view7);

        ll_ecg_measure_bp_view = (LinearLayout) findViewById(R.id.ll_ecg_measure_bp_view);

        measure_count_down_number = (TextView) findViewById(R.id.measure_count_down_number);
        measure_count_ready_number = (TextView) findViewById(R.id.measure_count_ready_number);
        measure_count_no_touch_number = (TextView) findViewById(R.id.measure_count_no_touch_number);
        ecg_measure_analysis = (TextView) findViewById(R.id.ecg_measure_analysis);

        tvEcgMeasureTitle = (TextView) findViewById(R.id.tvEcgMeasureTitle);
        tvPpgMeasureTitle = (TextView) findViewById(R.id.tvPpgMeasureTitle);

        findViewById(R.id.start_ecg).setOnClickListener(this);
        findViewById(R.id.restart_ecg).setOnClickListener(this);
        findViewById(R.id.ecg_measure_help).setOnClickListener(this);

//        ecg_measure_heart.setText(HealthyCache.HealthyCacheHeart);
//        ecg_measure_sbp.setText(HealthyCache.HealthyCacheSystolic);
//        ecg_measure_dbp.setText(HealthyCache.HealthyCacheDiastolic);

    }


    void initData() {


        try {
            Intent intent = this.getIntent();
            if (intent.getExtras().getString(IntentConstants.MesureType) != null) {
                //校准
                if (intent.getExtras().getString(IntentConstants.MesureType).equals(IntentConstants.MesureType_Calibration)) {
                    IsCalibration = true;
                    public_head_title.setText(getText(R.string.jiaozhun_title));
                    ll_ecg_measure_bp_view.setVisibility(View.GONE);
                }
                //测量
                else {
                    IsCalibration = false;
                    public_head_title.setText(getText(R.string.measure_title));
                    ll_ecg_measure_bp_view.setVisibility(View.VISIBLE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        MyLog.i(TAG, "是否是校准 = IsCalibration = " + IsCalibration);

        if (mBleDeviceTools.get_device_is_ppg_hr_jiaozhun()) {
            tvEcgMeasureTitle.setText("ECG*");
            tvPpgMeasureTitle.setText("PPG*");
        } else {
            tvEcgMeasureTitle.setText("ECG");
            tvPpgMeasureTitle.setText("PPG");
        }

        updateTopTitleUi(1);


    }


    void handler_init() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch (msg.what) {
                    case MSG_DATA_ECG:

                        int ecg_data = (int) (msg.arg2);
                        if (ecg_data > 0.0000001f) {
                            ecg_measure_ecgview.setLinePoint(ecg_data);
                        }

                        break;

                    case MSG_PLAY_RBO_SOUND:
//                        MyLog.i(TAG, 心跳R波电需要声音  收到");
                        mMediaPlayer.start();

                        break;

                    case MSG_DATA_PPG:

                        int ppg_data = (int) (msg.arg2);
                        PpgMax = (int) ppg_data;
                        int[] drawingData = MyUtils.TransformationPPgData(PpgMin, PpgMax);
                        for (int i = 0; i < drawingData.length; i++) {
                            ecg_measure_ppgview.setLinePoint(drawingData[i]);
                        }
                        PpgMin = PpgMax;

                        break;


                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    //初始化心电
    void ecg_init() {
        ecg_measure_ecgview = (ECGView) findViewById(R.id.ecg_measure_ecgview);
        ecg_measure_ecgview.setMaxPointAmount(Constants.DrawEcgWidth);
        ecg_measure_ecgview.setMaxYNumber(Constants.DrawEcgHeight);
        ecg_measure_ecgview.setRemovedPointNum(20);
        ecg_measure_ecgview.setEveryNPoint(5);//
        ecg_measure_ecgview.setEffticeValue(50);//
        ecg_measure_ecgview.setEveryNPointRefresh(2);//
        ecg_measure_ecgview.setTitle("");

    }

    //处理心电数据
    double HandlerEcg(int end) {
        my_ecg_data += String.valueOf(end) + ",";

        double reult = 0;
        if (mUserSetTools.get_user_wear_way() == 1) {
            reult = handlerEcg(mHandler, end, true);
        } else {
            reult = handlerEcg(mHandler, end, false);
        }

        reult = Constants.DrawEcgHeight / 2 - reult * Constants.DrawEcgHeight * Constants.DrawEcgZip;
        return reult;
    }

    /**
     * 发送心电数据画图
     *
     * @param ecg_date
     */
    void sendEcgDate(int ecg_date) {
        double ecg_yy = HandlerEcg(ecg_date);
        Message message = new Message();
        message.what = MSG_DATA_ECG;
        message.arg2 = (int) ecg_yy;
        mHandler.sendMessage(message);
    }


    //PPG初始化
    void ppg_init() {
        ecg_measure_ppgview = (ECGView) findViewById(R.id.ecg_measure_ppgview);
        ecg_measure_ppgview.setMaxPointAmount(Constants.DrawPpgWidth);
        ecg_measure_ppgview.setMaxYNumber(Constants.DrawPpgHeight);
        ecg_measure_ppgview.setRemovedPointNum(20);
        ecg_measure_ppgview.setEveryNPoint(5);//
        ecg_measure_ppgview.setEffticeValue(50);//
        ecg_measure_ppgview.setEveryNPointRefresh(2);//
        ecg_measure_ppgview.setTitle("");
    }

    //处理PPG数据
    double HandlerPPG(int end) {
        double reult = 0;

        if (mUserSetTools.get_user_wear_way() == 1) {
            reult = HandlerPpg(end, true);
        } else {
            reult = HandlerPpg(end, false);
        }
        reult = Constants.DrawPpgHeight / 2 - reult * Constants.DrawPpgHeight * Constants.DrawPpgZip;
        return reult;
    }

    /**
     * 发送PPG数据画图
     *
     * @param ecg_date
     */
    void sendPpgDate(int ecg_date) {
        double ppg_yy = HandlerPPG(ecg_date);
        Message message = new Message();
        message.what = MSG_DATA_PPG;
        message.arg2 = (int) ppg_yy;
        mHandler.sendMessage(message);
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            //返回
            case R.id.public_head_back:
                finish();
                break;

            //开始测量
            case R.id.start_ecg:
                startEcgMesure();
                break;

            //重新测量
            case R.id.restart_ecg:
                startEcgMesure();
                break;

            //帮助
            case R.id.public_help:
                Intent intent1 = new Intent(mContext, EcgMesureHelpActivity.class);
                startActivity(intent1);
                break;

            //取消测量
            case R.id.public_cancel:
                QuitUserDialog();
                break;
            //测量帮助
            case R.id.ecg_measure_help:
                Intent intent2 = new Intent(mContext, EcgMesureHelpActivity.class);
                startActivity(intent2);
                break;
        }
    }


    private void initBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastTools.ACTION_GATT_ECG_DATA);
        filter.addAction(BroadcastTools.ACTION_GATT_PPG_DATA);
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

                //接收心电数据
                case BroadcastTools.ACTION_GATT_ECG_DATA:

                    Bundle ecg_budle = intent.getExtras();
                    int[] ecg_page = ecg_budle.getIntArray(BroadcastTools.INTENT_PUT_ECG_DATA);
                    int is_tuoluo = ecg_budle.getInt(BroadcastTools.INTENT_PUT_ECG_TUOLUO);

//                    MyLog.i(TAG, "导联状态 = is_tuoluo = " + is_tuoluo);

                    if (StageTag == 2) {
                        //当导联不脱落的时候，进入下一个阶段
                        //导联脱落
                        if (is_tuoluo == 0) {

                        } //导联不脱落
                        else {

                            MyLog.i(TAG, "交互状态 = 2.检测到手指已经放到电极片上了，进入下一个阶段");
                            updateMeasureUi(3);
                            TimerOneStop();
                            closeEcg();
                            TimerTwoStart();

                        }
                    } else if (StageTag == 4 || StageTag == 5) {
                        //导联脱落
                        if (is_tuoluo == 0) {
                            TimerThreeIsPause = true;
                            TimerFourIsPause = false;
                            is_effective = false;
                            updateMeasureUi(5);

                        } //导联不脱落
                        else {
                            TimerThreeIsPause = false;
                            TimerFourdown = 5;
                            TimerFourIsPause = true;
                            if (MyUtils.isDataiDentical(ecg_page)) {
                                is_effective = false;
                            } else {
                                is_effective = true;
                            }
                            updateMeasureUi(4);

                        }
                    }


                    //发送心电数据
                    for (int i = 0; i < ecg_page.length; i++) {
                        sendEcgDate(ecg_page[i]);
                    }

                    break;

                //接收PPG数据
                case BroadcastTools.ACTION_GATT_PPG_DATA:

                    Bundle ppg_budle = intent.getExtras();
                    int[] ppg_page1 = ppg_budle.getIntArray(BroadcastTools.INTENT_PUT_PPG_DATA);

                    for (int i = 0; i < ppg_page1.length; i++) {
                        PpgData.add(ppg_page1[i]);
                        my_ppg_data += String.valueOf(ppg_page1[i]) + ",";
                    }


                    break;

                case BroadcastTools.ACCTION_GATT_HEART_DATA:
                    Bundle b = intent.getExtras();
                    int device_heart = b.getInt(BroadcastTools.INTENT_PUT_HEART_DATA);
                    MyLog.i(TAG, "测试心率值 这里接到值了 =  device_heart = " + device_heart);
                    if (device_heart <= 0) {
                        ppgHeart = 0;
                    } else {
                        ppgHeart = device_heart;
                    }
                    break;

            }
        }
    };


    /**
     * 清除数据
     */
    void clearData() {

        setEcgMeasure(true);
        my_ecg_data = "";
        my_ppg_data = "";
        xiabiao = 0;
        ppgHeart = 0;
        PpgData.clear();
        initEcgData();
        initPpgData();
    }


    // PPG画图的计时器
    private boolean PpgIsStop = false;
    private Handler PpgHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (!PpgIsStop) {

                        PpgHandler.sendEmptyMessageDelayed(1, 40);

                        if (PpgData.size() > xiabiao + 1) {

                            for (int i = 0; i < 2; i++) {
                                int abc = PpgData.get(xiabiao);
                                sendPpgDate(abc);
                                xiabiao += 1;
//                                MyLog.i(TAG, "调用111 = size = " + PpgData.size() + "  xiabiao = " + xiabiao);
                            }
                        }
                    }


                    break;
                case 0:
                    break;
            }
        }

    };

    private void PpgStart() {
        PpgHandler.sendEmptyMessage(1);
        PpgIsStop = false;
    }

    private void PpgStop() {
        PpgHandler.sendEmptyMessage(0);
        PpgIsStop = true;
    }


    //=============阶段标志位==============
    private int StageTag = 1;

    //======== 第一个定时器====================
    private boolean TimerOneIsStop = false;
    private int TimerOnedown = 10;

    private Handler TimerOneHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 添加更新ui的代码
                    if (!TimerOneIsStop) {

                        TimerOneHandler.sendEmptyMessageDelayed(1, 1000);
                        TimerOnedown -= 1;

                        MyLog.i(TAG, "定时器1 = 回调 TimerOnedown = " + TimerOnedown);
                        if (TimerOnedown <= 0) {
                            TimerOneStop();
                            MyLog.i(TAG, "交互状态 = 超过十秒，测量失败");
                            handleMeasureFail();

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
        MyLog.i(TAG, "交互状态 = 1.把手放到电极上");

        TimerOneHandler.sendEmptyMessage(1);
        TimerOneIsStop = false;

    }

    private void TimerOneStop() {

        MyLog.i(TAG, "定时器1 = 回调 TimerOneStop()");

        TimerOneHandler.sendEmptyMessage(0);
        TimerOneIsStop = true;
        TimerOnedown = 10;
    }


    //======== 第二个定时器====================
    private boolean TimerTwoIsStop = false;
    private int TimerTwodown = 3;

    private Handler TimerTwoHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 添加更新ui的代码
                    if (!TimerTwoIsStop) {

                        TimerTwoHandler.sendEmptyMessageDelayed(1, 1000);


                        TimerTwodown -= 1;


                        measure_count_ready_number.setText(String.valueOf(TimerTwodown));
                        MyLog.i(TAG, "定时器2 = 回调 TimerTwodown = " + TimerTwodown);
                        if (TimerTwodown <= 0) {
                            TimerTwoStop();
                            MyLog.i(TAG, "交互状态 = 超过3秒，进入下一个阶段");
//                            StageTag = 3;
                            updateMeasureUi(4);
                            openEcg();
                            TimerThreeStart();
                            TimerFourStart();
                        }
                    }
                    break;
                case 0:
                    break;
            }
        }

    };

    private void TimerTwoStart() {

        MyLog.i(TAG, "定时器2 = 回调 TimerTwoStart()");
        MyLog.i(TAG, "交互状态 = 3.准备测量3秒");

        TimerTwoHandler.sendEmptyMessage(1);
        TimerTwoIsStop = false;

    }

    private void TimerTwoStop() {

        MyLog.i(TAG, "定时器2 = 回调 TimerTwoStop()");

        TimerTwoHandler.sendEmptyMessage(0);
        TimerTwoIsStop = true;
        TimerTwodown = 3;
    }

    //======== 第三个定时器====================
    private boolean TimerThreeIsStop = false;
    private boolean TimerThreeIsPause = false;
    private int TimerThreedown = 30;

    private Handler TimerThreeHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 添加更新ui的代码
                    if (!TimerThreeIsStop) {


                    }
                    break;
                case 0:
                    break;
            }
        }

    };

    private void TimerThreeStart() {

        MyLog.i(TAG, "定时器3 = 回调 TimerThreeStart()");
        MyLog.i(TAG, "交互状态 = 4.正式测量30秒");

        TimerThreeHandler.sendEmptyMessage(1);
        TimerThreeIsStop = false;
        TimerThreeIsPause = false;

    }


    private void TimerThreeStop() {

        MyLog.i(TAG, "定时器3 = 回调 TimerThreeStop()");
        TimerThreeHandler.sendEmptyMessage(0);
        TimerThreeIsStop = true;
        TimerThreeIsPause = false;
        TimerThreedown = 30;
    }

    //======== 第四个定时器====================
    private boolean TimerFourIsStop = false;
    private boolean TimerFourIsPause = true;
    private int TimerFourdown = 5;

    private Handler TimerFourHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 添加更新ui的代码
                    if (!TimerFourIsStop) {

                        handleMeasureStart();

                        TimerFourHandler.sendEmptyMessageDelayed(1, 1000);

                        if (!TimerFourIsPause) {

                            MyLog.i(TAG, "定时器4 = 回调 TimerFourdown = " + TimerFourdown);

                            TimerFourdown -= 1;

                            measure_count_no_touch_number.setText(String.valueOf(TimerFourdown));

                            if (TimerFourdown <= 0) {

                                TimerFourStop();
                                TimerThreeStop();

                                MyLog.i(TAG, "交互状态 = 测量失败");
                                handleMeasureFail();


                            }
                        }
                    }
                    break;
                case 0:
                    break;
            }
        }
    };


    private void TimerFourStart() {

        MyLog.i(TAG, "定时器4 = 回调 TimerFourStart()");

        TimerFourHandler.sendEmptyMessage(1);
        TimerFourIsStop = false;
        TimerFourIsPause = false;

    }

    private void TimerFourStop() {

        MyLog.i(TAG, "定时器4 = 回调 TimerFourStop()");

        TimerFourHandler.sendEmptyMessage(0);
        TimerFourIsStop = true;
        TimerFourIsPause = false;
        TimerFourdown = 5;
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
                                ecg_measure_analysis.setText(".");
                            } else if (TimerFivedown == 2) {
                                ecg_measure_analysis.setText("..");
                            } else if (TimerFivedown == 1) {
                                ecg_measure_analysis.setText("...");
                            } else if (TimerFivedown == 0) {
                                ecg_measure_analysis.setText("...");
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

        ll_ecg_measure_bootom_view1.setVisibility(View.GONE);
        ll_ecg_measure_bootom_view2.setVisibility(View.GONE);
        ll_ecg_measure_bootom_view3.setVisibility(View.GONE);
        ll_ecg_measure_bootom_view4.setVisibility(View.GONE);
        ll_ecg_measure_bootom_view5.setVisibility(View.GONE);
        ll_ecg_measure_bootom_view6.setVisibility(View.GONE);
        ll_ecg_measure_bootom_view7.setVisibility(View.GONE);

        StageTag = type;

        //初始界面
        if (StageTag == 1) {

            ll_ecg_measure_bootom_view1.setVisibility(View.VISIBLE);

            updateTopTitleUi(1);

            //10秒等待
        } else if (StageTag == 2) {

            ll_ecg_measure_bootom_view2.setVisibility(View.VISIBLE);
            updateTopTitleUi(2);

            //3秒准备测量
        } else if (StageTag == 3) {

            ll_ecg_measure_bootom_view3.setVisibility(View.VISIBLE);
            updateTopTitleUi(2);

            //30秒测量
        } else if (StageTag == 4) {

            ll_ecg_measure_bootom_view4.setVisibility(View.VISIBLE);
            updateTopTitleUi(2);

            //导联脱落
        } else if (StageTag == 5) {

            ll_ecg_measure_bootom_view5.setVisibility(View.VISIBLE);
            updateTopTitleUi(2);

            //测量失败
        } else if (StageTag == 6) {

            ll_ecg_measure_bootom_view6.setVisibility(View.VISIBLE);
            updateTopTitleUi(1);

            //分析中
        } else if (StageTag == 7) {

            ll_ecg_measure_bootom_view7.setVisibility(View.VISIBLE);
            updateTopTitleUi(2);

        }

    }

    private int ppgHeart = 0;
    //是否接触皮肤，皮肤不干燥
    private boolean is_effective = false;

    int nowHR = 0;

    void handleMeasureStart() {

        nowHR = getEcgHr();

        MyLog.i(TAG, "handleMeasureStart = nowHR = " + nowHR + "  是否支持校准 = " + mBleDeviceTools.get_device_is_ppg_hr_jiaozhun() + "  ppgHeart = " + ppgHeart);

        //如果支持PPG校准的话，就进行校准
        if (mBleDeviceTools.get_device_is_ppg_hr_jiaozhun() && is_effective) {
            MyLog.i(TAG, "一键测量 = 支持PPG校准");
            nowHR = MyUtils.getNewHr(ppgHeart, nowHR);
        }
        MyLog.i(TAG, "handleMeasureStart = nowHR = " + nowHR);

        ecg_measure_heart.setText(String.valueOf(nowHR));
        ecg_measure_sbp.setText(String.valueOf(EcgMeasureUitls.getMeasureSBP(mUserSetTools, nowHR)));
        ecg_measure_dbp.setText(String.valueOf(EcgMeasureUitls.getMeasureDBP(mUserSetTools, nowHR)));

        TimerThreeHandler.sendEmptyMessageDelayed(1, 1000);
        TimerThreedown -= 1;


        if (!TimerThreeIsPause) {

            MyLog.i(TAG, "定时器3 = 回调 TimerThreedown = " + TimerThreedown);
            measure_count_down_number.setText(String.valueOf(TimerThreedown));

            if (TimerThreedown <= 0) {

                TimerThreeStop();
                TimerFourStop();
                MyLog.i(TAG, "交互状态 = 5.测量结束");
                handleMeasureEnd();

            }
        }


    }


    //测量失败
    void handleMeasureFail() {

        MyLog.i(TAG, "handleMeasureFail()");
        clearData();
        closeEcg();
        updateMeasureUi(6);

    }


    //测量结束
    void handleMeasureEnd() {

        //关闭心电开关
        closeEcg();


        //最后结果是否合法
        if (nowHR > DefaultVale.USER_HEART_MAX || nowHR < DefaultVale.USER_HEART_MIN) {
            MyLog.i(TAG, "测量结果 = 数值不合法");
            //测量失败
            handleMeasureFail();
            return;
        }

        MyLog.i(TAG, "handleMeasureEnd = nowHR = " + nowHR);


        handleMeasureSuccess();

    }

    HealthInfo mHealthInfo;

    //测量成功
    void handleMeasureSuccess() {

        MyLog.i(TAG, "测量结果 = 测量成功");

        //如果是校准，保存校准值
        if (IsCalibration) {

            if (nowHR < 50) {
                mUserSetTools.set_calibration_heart(50);
            } else if (nowHR > 110) {
                mUserSetTools.set_calibration_heart(110);
            } else {
                mUserSetTools.set_calibration_heart(nowHR);
            }
            //上传校准值给手环
            ResultCalibrationHeart();
            //上传校准值到后台
            CalibrationData mCalibrationData = new CalibrationData();
            mCalibrationData.setCalibrationHeart(String.valueOf(mUserSetTools.get_calibration_heart()));
            mCalibrationData.setCalibrationSystolic(String.valueOf(mUserSetTools.get_calibration_systolic()));
            mCalibrationData.setCalibrationDiastolic(String.valueOf(mUserSetTools.get_calibration_diastolic()));
            uploadCalibrationInfo(mCalibrationData);

        }
        //是测量
        else {

            MyLog.i(TAG, "my_ecg_data = " + my_ecg_data.length());
            MyLog.i(TAG, "my_ppg_data = " + my_ppg_data.length());

            mHealthInfo = HealthInfo.getEcgMeasureResult(MyTime.getAllTime(), nowHR, getEcgPtpAvg(), my_ecg_data, my_ppg_data);

            MyLog.i(TAG, "mHealthInfo = " + mHealthInfo.toString());

            //这里需要上传测量结果给手环
            ResultMeasureHeart(mHealthInfo);

//            ecg_measure_heart.setText(HealthyCache.HealthyCacheHeart);
//            ecg_measure_sbp.setText(HealthyCache.HealthyCacheSystolic);
//            ecg_measure_dbp.setText(HealthyCache.HealthyCacheDiastolic);

            //这里需要跳转界面，显示测量结果
            updateMeasureUi(7);
            TimerFiveStart();

            //这里需要上传测量结果给后台，和保存到本地
            saveHealthDdata(mHealthInfo);
        }

    }


    /**
     * 更新标题栏UI
     *
     * @param tag
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


    /**
     * 退出测量对话框
     */
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


    //测量结束
    void handleMeasureRsult() {

        if (mHealthInfo != null) {
            Intent intent = new Intent(mContext, EcgMesureDetailsActivity.class);
            intent.putExtra(IntentConstants.HealthInfo, mHealthInfo);
            startActivity(intent);
            finish();
        }

    }

    /**
     * 启动测量
     */
    void startEcgMesure() {
        //蓝牙已连接
        if (HomeActivity.ISBlueToothConnect()) {
            TimerOneStart();
            openEcg();
            clearData();
            PpgStart();
            updateMeasureUi(2);
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

                        if (isDestroy) {
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
                        if (isDestroy) {
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

                            if (isDestroy) {
                                return;
                            }
                            MyLog.i(TAG, "请求接口-上传健康数据 请求失败 = message = " + arg0.getMessage());
                            waitDialog.close();

                        }
                    });

        }


    }

    /**
     * 解析数据
     */
    private void ResultDataParsing(HealthBean mHealthBean, HealthInfo mHealthInfo) {

        MyLog.i(TAG, "解析 = mHealthBean = " + mHealthBean.toString());

        boolean isSuccessHealth = mHealthInfoUtils.MyUpdateToSyncOne(mHealthInfo);

        MyLog.i(TAG, "数据库-健康数据 同步状态 = isSuccessHealth = " + isSuccessHealth);

    }


}
