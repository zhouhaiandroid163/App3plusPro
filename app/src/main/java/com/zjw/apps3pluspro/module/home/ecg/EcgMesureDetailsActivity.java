package com.zjw.apps3pluspro.module.home.ecg;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.entity.UserData;
import com.zjw.apps3pluspro.network.javabean.EcgPpgHealthBean;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.dbmanager.HealthInfoUtils;
import com.zjw.apps3pluspro.sql.entity.HealthInfo;
import com.zjw.apps3pluspro.view.RoundProgressView;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.view.ecg.ECGView;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.utils.MyUtils;

import org.json.JSONObject;


/**
 * 心电测量详情
 */
public class EcgMesureDetailsActivity extends BaseActivity implements OnClickListener {
    private String TAG = EcgMesureDetailsActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();

    //数据库存储
    private HealthInfoUtils mHealthInfoUtils = BaseApplication.getHealthInfoUtils();

    TextView mesure_history_heart, mesure_details_systolic, mesure_details_diastolic;
    private ECGView input_ecg_view;
    private ECGView input_ppg_view;
    private Handler mHandler;
    private static final int MSG_DATA_ECG = 0x14;
    private static final int MSG_DATA_PPG = 0x15;

    int PpgMax = 0;
    int PpgMin = 0;

    private String healthEcgData = "";
    private String healthPpgData = "";
    private TextView play_ecg;

    private String[] ecg_data = null;
    private String[] ppg_data = null;

    HealthInfo mHealthInfo = null;
    UserData mUserData = null;

    //标题
    private TextView public_head_title;

    private ImageView ecg_mesure_start_ecg;

    //健康指数
    private TextView ecg_details_health_value, ecg_details_health_state, ecg_details_fatigue_index, ecg_details_body_quality, ecg_details_cardiac_function, ecg_details_body_load;
    private TextView health_index_text, fatigue_index_text, load_index_text, quality_index_text, heart_index_text;

    //加载相关
    private WaitDialog waitDialog;

    private RelativeLayout r_ecg_view, r_ppg_view;
    private RoundProgressView ppgRoundProgressView;


    @Override
    protected int setLayoutId() {
        isTextDark = false;
        bgColor = R.color.title_bg_ecg;
        return R.layout.activity_ecg_mesure_details;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = EcgMesureDetailsActivity.this;
        waitDialog = new WaitDialog(this);
        initView();
        ecg_init();
        ppg_init();
        handler_init();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //初始化心电
    void ecg_init() {
        input_ecg_view = (ECGView) findViewById(R.id.input_ecg_view);
        input_ecg_view.setMaxPointAmount(Constants.DrawEcgWidth);
        input_ecg_view.setMaxYNumber(Constants.DrawEcgHeight);
        input_ecg_view.setRemovedPointNum(20);
        input_ecg_view.setEveryNPoint(5);//
        input_ecg_view.setEffticeValue(50);//
        input_ecg_view.setEveryNPointRefresh(2);//
        input_ecg_view.setTitle("");

    }

    double HandlerEcg(int end) {

        MyLog.i(TAG, "end = " + end);

        double reult = 0;
        if (mUserSetTools.get_user_wear_way() == 1) {
            reult = handlerEcg(mHandler, end, true);
        } else {
            reult = handlerEcg(mHandler, end, false);
        }

        reult = Constants.DrawEcgHeight / 2 - reult * Constants.DrawEcgHeight * Constants.DrawEcgZip;

        MyLog.i(TAG, "reult 2 = " + reult);

        return reult;

    }

    //PPG初始化
    void ppg_init() {
        input_ppg_view = (ECGView) findViewById(R.id.input_ppg_view);
        input_ppg_view.setMaxPointAmount(Constants.DrawPpgWidth);
        input_ppg_view.setMaxYNumber(Constants.DrawPpgHeight);
        input_ppg_view.setRemovedPointNum(20);
        input_ppg_view.setEveryNPoint(5);//
        input_ppg_view.setEffticeValue(50);//
        input_ppg_view.setEveryNPointRefresh(2);//
        input_ppg_view.setTitle("");
    }

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

    void handler_init() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch (msg.what) {
                    case MSG_DATA_ECG:
                        int ecg_data = (int) (msg.arg2);

                        if (ecg_data > 0.0000001f) {
                            input_ecg_view.setLinePoint(ecg_data);
                        }

                        break;


                    case MSG_DATA_PPG:

                        int ppg_data = (int) (msg.arg2);

                        PpgMax = (int) ppg_data;

                        int[] drawingData = MyUtils.TransformationPPgData(PpgMin, PpgMax);

                        for (int i = 0; i < drawingData.length; i++) {
                            input_ppg_view.setLinePoint(drawingData[i]);
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


    private void initView() {
        findViewById(R.id.public_head_back).setOnClickListener(this);
        public_head_title = (TextView) findViewById(R.id.public_head_title);
        public_head_title.setTextColor(getResources().getColor(R.color.white));
        public_head_title.setText(getResources().getString(R.string.health_index));
        public_head_title.setOnClickListener(this);
        findViewById(R.id.llTitleHelp).setOnClickListener(this);
        findViewById(R.id.public_head_back_img).setBackground(getResources().getDrawable(R.mipmap.white_back_bg));
        findViewById(R.id.rl_public_head_bg).setBackgroundColor(getResources().getColor(R.color.title_bg_ecg));
        findViewById(R.id.llTitleHelp).setVisibility(View.VISIBLE);

        mesure_history_heart = (TextView) findViewById(R.id.mesure_history_heart);
        mesure_details_systolic = (TextView) findViewById(R.id.mesure_details_systolic);
        mesure_details_diastolic = (TextView) findViewById(R.id.mesure_details_diastolic);


        ecg_details_health_value = (TextView) findViewById(R.id.ecg_details_health_value);
        ecg_details_health_state = (TextView) findViewById(R.id.ecg_details_health_state);
        ecg_details_fatigue_index = (TextView) findViewById(R.id.ecg_details_fatigue_index);
        ecg_details_body_quality = (TextView) findViewById(R.id.ecg_details_body_quality);
        ecg_details_cardiac_function = (TextView) findViewById(R.id.ecg_details_cardiac_function);
        ecg_details_body_load = (TextView) findViewById(R.id.ecg_details_body_load);


        health_index_text = (TextView) findViewById(R.id.health_index_text);
        fatigue_index_text = (TextView) findViewById(R.id.fatigue_index_text);
        load_index_text = (TextView) findViewById(R.id.load_index_text);
        quality_index_text = (TextView) findViewById(R.id.quality_index_text);
        heart_index_text = (TextView) findViewById(R.id.heart_index_text);

        r_ecg_view = (RelativeLayout) findViewById(R.id.r_ecg_view);
        r_ppg_view = (RelativeLayout) findViewById(R.id.r_ppg_view);

        ecg_mesure_start_ecg = (ImageView) findViewById(R.id.ecg_mesure_start_ecg);

        play_ecg = (TextView) findViewById(R.id.play_ecg);
        findViewById(R.id.ecg_details_start_playback).setOnClickListener(this);
        findViewById(R.id.ecg_details_to_presentation).setOnClickListener(this);

        ppgRoundProgressView = findViewById(R.id.ppgRoundProgressView);
    }


    void initData() {


        Intent intent = getIntent();
        mHealthInfo = intent.getParcelableExtra(IntentConstants.HealthInfo);

        MyLog.i(TAG, "接收到 = mHealthInfo = " + mHealthInfo);

        if (mHealthInfo != null) {


            String heart = !JavaUtil.checkIsNull(mHealthInfo.getHealth_heart()) ? mHealthInfo.getHealth_heart() : "";
            String systolic = !JavaUtil.checkIsNull(mHealthInfo.getHealth_systolic()) ? mHealthInfo.getHealth_systolic() : "";
            String diastolic = !JavaUtil.checkIsNull(mHealthInfo.getHealth_diastolic()) ? mHealthInfo.getHealth_diastolic() : "";
            String mesure_time = !JavaUtil.checkIsNull(mHealthInfo.getMeasure_time()) ? mHealthInfo.getMeasure_time() : "";

            String health_value = !JavaUtil.checkIsNull(mHealthInfo.getIndex_health_index()) ? mHealthInfo.getIndex_health_index() : "0";
            String fatigue_index = !JavaUtil.checkIsNull(mHealthInfo.getIndex_fatigue_index()) ? mHealthInfo.getIndex_fatigue_index() : "0";
            String body_quality = !JavaUtil.checkIsNull(mHealthInfo.getIndex_body_quality()) ? mHealthInfo.getIndex_body_quality() : "0";
            String cardiac_function = !JavaUtil.checkIsNull(mHealthInfo.getIndex_cardiac_function()) ? mHealthInfo.getIndex_cardiac_function() : "0";
            String body_load = !JavaUtil.checkIsNull(mHealthInfo.getIndex_body_load()) ? mHealthInfo.getIndex_body_load() : "0";
            String Sensor_type = !JavaUtil.checkIsNull(mHealthInfo.getSensor_type()) ? mHealthInfo.getSensor_type() : "";


//            public_head_title.setText(mesure_time);
            mesure_history_heart.setText(heart);
            mesure_details_systolic.setText(systolic);
            mesure_details_diastolic.setText(diastolic);


            ecg_details_fatigue_index.setText(getResources().getString(R.string.hrv_help_fatigue_title) + " " +fatigue_index);
            ecg_details_body_quality.setText(getResources().getString(R.string.hrv_help_quality_title) + " " +body_quality);
            ecg_details_cardiac_function.setText(getResources().getString(R.string.hrv_help_heart_title) + " " +cardiac_function);
            ecg_details_body_load.setText(getResources().getString(R.string.hrv_help_load_title) + " " + body_load);


            int health_number = Integer.parseInt(health_value);
            int fatigu_index = Integer.parseInt(fatigue_index);
            int load_index = Integer.parseInt(body_load);
            int body_index = Integer.parseInt(body_quality);
            int user_heart = Integer.parseInt(cardiac_function);


            ppgRoundProgressView.setProgress(health_number / 100f);

            if (health_number > 0) {
                if (health_number <= 70) {
                    ecg_details_health_state.setText(getString(R.string.health_index_sub));
                    health_index_text.setText(getString(R.string.hrv_help_health_proposal_text1));
                } else if (health_number <= 90) {
                    ecg_details_health_state.setText(getString(R.string.health_index_good));
                    health_index_text.setText(getString(R.string.hrv_help_health_proposal_text2));
                } else {
                    ecg_details_health_state.setText(getString(R.string.health_index_optimal));
                    health_index_text.setText(getString(R.string.hrv_help_health_proposal_text3));
                }
                ecg_details_health_value.setText(String.valueOf(health_value));
            } else {
                ecg_details_health_value.setText(getText(R.string.sleep_gang));
                ecg_details_health_state.setText(getText(R.string.sleep_gang));
            }

            EcgMeasureUitls.updateTextStateUpdate(fatigue_index_text,
                    45, 70,
                    getString(R.string.hrv_help_fatigue_proposal_text1),
                    getString(R.string.hrv_help_fatigue_proposal_text2),
                    getString(R.string.hrv_help_fatigue_proposal_text3),
                    fatigu_index);

            EcgMeasureUitls.updateTextStateUpdate(load_index_text, 55, 80,
                    getString(R.string.hrv_help_load_proposal_text1),
                    getString(R.string.hrv_help_load_proposal_text2),
                    getString(R.string.hrv_help_load_proposal_text3),
                    load_index);

            EcgMeasureUitls.updateTextStateDown(quality_index_text, 70, 90,
                    getString(R.string.hrv_help_quality_proposal_text1),
                    getString(R.string.hrv_help_quality_proposal_text2),
                    getString(R.string.hrv_help_quality_proposal_text3),
                    body_index);

            EcgMeasureUitls.updateTextStateDown(heart_index_text, 70, 90,
                    getString(R.string.hrv_help_heart_proposal_text1),
                    getString(R.string.hrv_help_heart_proposal_text2),
                    getString(R.string.hrv_help_heart_proposal_text3),
                    user_heart);


//            传感器类型(0=ECG,1,ECG+PPG,2=PPG,3=无)(4=心电手环+加离线心电，5=心电手环+离线血压，6=运动手环+离线血压)
            //需要展示心电数据
            if (mHealthInfo.isEcgDevice()) {

                MyLog.i(TAG, "需要展示心电数据");

                MyLog.i(TAG, "ECG数据 = getEcg_data = " + mHealthInfo.getEcg_data());
                MyLog.i(TAG, "ECG数据 = getPpg_data = " + mHealthInfo.getPpg_data());

                //如果心电数据为空
                if (mHealthInfo.getEcg_data() == null || mHealthInfo.getEcg_data().equals("")) {
                    MyLog.i(TAG, "心电数据为空");
                    int data_id = 0;

                    try {
                        data_id = Integer.valueOf(mHealthInfo.getData_id());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //如果数据ID大于0
                    if (data_id > 0) {
                        MyLog.i(TAG, "如果数据ID大于0");
                        requestEcgPpgData(mHealthInfo.getMeasure_time(), String.valueOf(data_id));
                    } else {
                        MyLog.i(TAG, "如果数据ID小于等于0");
                    }

                } else {
                    MyLog.i(TAG, "心电数据不为空");
                }
            } else {
                MyLog.i(TAG, "不需要展示心电数据");
            }


            updateUi(mHealthInfo);


        }


    }

    void updateUi(HealthInfo mHealthInfo) {

        if (!JavaUtil.checkIsNull(mHealthInfo.getEcg_data())) {
            healthEcgData = mHealthInfo.getEcg_data();
            ecg_data = healthEcgData.split(",");

            MyLog.i(TAG, "ECG数据 = healthEcgData = " + healthEcgData.length());
        } else {
            MyLog.i(TAG, "ECG数据 = null");
        }

        if (!JavaUtil.checkIsNull(mHealthInfo.getPpg_data())) {
            healthPpgData = mHealthInfo.getPpg_data();
            ppg_data = healthPpgData.split(",");

            MyLog.i(TAG, "PPG数据 = healthPpgData = " + healthPpgData.length());
        } else {
            MyLog.i(TAG, "PPG数据 = null");
        }

        updateTypeUi(mHealthInfo.getSensor_type());

    }

    void updateTypeUi(String sensor) {

        //PPG+ECG
        if (sensor.equals("1")) {
            r_ppg_view.setVisibility(View.VISIBLE);
            r_ecg_view.setVisibility(View.VISIBLE);
        }
        //ECG
        else {
            r_ppg_view.setVisibility(View.GONE);
            r_ecg_view.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.public_head_back:
                finish();
                break;
            case R.id.ecg_details_start_playback:

                if (!healthEcgData.equals("")) {
                    if (play_ecg.getText().toString().trim().equals(getString(R.string.play_start))) {
                        JingBaoStart();

                    } else {
                        JingBaoStop();
                    }
                } else {
                    AppUtils.showToast(mContext, R.string.no_ecg_data);
                }

                break;
            case R.id.ecg_details_to_presentation:
                if (mHealthInfo != null) {


                    String sexValue = String.valueOf(mUserSetTools.get_user_sex());
                    String UserName = mUserSetTools.get_user_nickname();
                    String weightValue = String.valueOf(mUserSetTools.get_user_weight());
                    String heightValue = String.valueOf(mUserSetTools.get_user_height());
                    String birthdayValue = !JavaUtil.checkIsNull(mUserSetTools.get_user_birthday()) ? mUserSetTools.get_user_birthday() : DefaultVale.USER_BIRTHDAY;


                    mUserData = new UserData();
                    mUserData.setHeight(heightValue);
                    mUserData.setNikname(UserName);
                    mUserData.setWeight(weightValue);
                    mUserData.setSex(sexValue);
                    mUserData.setBirthday(birthdayValue);


                    Intent intent = new Intent(mContext, EcgReportActivity.class);
                    intent.putExtra(IntentConstants.HealthInfo, mHealthInfo);
                    intent.putExtra(IntentConstants.UserInfo, mUserData);
                    startActivity(intent);
                }
                break;
            case R.id.public_head_title:
            case R.id.llTitleHelp:
                Intent intent = new Intent(mContext, HrvIndexHelpActivity.class);
                startActivity(intent);
                break;


        }
    }


    private boolean JingBaoIsStop = false;
    private int ecg_count_down = 0;
    private int ppg_count_down = 0;

    // 计时器
    private Handler JingBaoHandler = new Handler() {
        /*
         * edit by yuanjingchao 2014-08-04 19:10
         */
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 添加更新ui的代码
                    if (!JingBaoIsStop) {

                        JingBaoHandler.sendEmptyMessageDelayed(1, 4);

                        if (ecg_count_down >= ecg_data.length - 30) {


                            JingBaoStop();

                        } else {


                            if (ppg_data != null) {
                                if (ppg_count_down >= ppg_data.length - 6) {
                                    JingBaoStop();
                                }

                            }

                            for (int i = 0; i < 5; i++) {
                                ecg_count_down += 1;
                                sendEcgDate(Integer.valueOf(ecg_data[ecg_count_down]));
                            }
                            if (ppg_data != null) {
                                ppg_count_down += 1;
                                sendPpgDate(Integer.valueOf(ppg_data[ppg_count_down]));
                            }


                        }

                    }
                    break;
                case 0:

                    break;
            }
        }

    };

    void sendEcgDate(int ecg_date) {
        double ecg_yy = HandlerEcg(ecg_date);
        Message message = new Message();
        message.what = MSG_DATA_ECG;
        message.arg2 = (int) ecg_yy;
        mHandler.sendMessage(message);
    }

    void sendPpgDate(int ecg_date) {

        double ppg_yy = HandlerPPG(ecg_date);
        Message message = new Message();
        message.what = MSG_DATA_PPG;
        message.arg2 = (int) ppg_yy;
        mHandler.sendMessage(message);
    }

    private void JingBaoStart() {
        MyLog.i(TAG, "回放 开始");
        initEcgData();
        initPpgData();
        play_ecg.setText(getString(R.string.play_stop));
        ecg_mesure_start_ecg.setBackgroundResource(R.drawable.icon_stop_ecg);
        JingBaoHandler.sendEmptyMessage(1);
        JingBaoIsStop = false;

    }

    private void JingBaoStop() {
        MyLog.i(TAG, "回放 结束");
        play_ecg.setText(getString(R.string.play_start));
        ecg_mesure_start_ecg.setBackgroundResource(R.drawable.icon_start_ecg);
        JingBaoHandler.sendEmptyMessage(0);
        JingBaoIsStop = true;
        ecg_count_down = 0;
        ppg_count_down = 0;
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 请求ECGPPG数据
     */
    private void requestEcgPpgData(final String time, final String data_id) {

        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.getdHealthPpgEcgData(data_id);
        MyLog.i(TAG, "请求接口-获取健康原始数据" + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub
                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-获取健康原始数据 result = " + result);

                        EcgPpgHealthBean mEcgPpgHealthBean = ResultJson.EcgPpgHealthBean(result);

                        //请求成功
                        if (mEcgPpgHealthBean.isRequestSuccess()) {
                            if (mEcgPpgHealthBean.isGetHealthSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取健康原始数据 成功");
                                ResultDataParsing(mEcgPpgHealthBean, time);
                            } else if (mEcgPpgHealthBean.isGetHealthSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取健康原始数据 失败");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else if (mEcgPpgHealthBean.isGetHealthSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-获取健康原始数据 无数据");
//                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
//                                HealthBean.insertNullHealth(mHealthInfoUtils, date, true);
//                                getHealthtDay(false);
                            } else {
                                MyLog.i(TAG, "请求接口-获取健康原始数据 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取健康原始数据 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);
                        }

                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub


                        MyLog.i(TAG, "请求接口-获取健康原始数据 请求失败 = message = " + arg0.getMessage());
                        waitDialog.close();
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                    }
                });


    }


    /**
     * 解析当前用户数据
     *
     * @param mEcgPpgHealthBean
     * @param time
     */
    private void ResultDataParsing(EcgPpgHealthBean mEcgPpgHealthBean, String time) {

        String ecg_data = "";
        String ppg_data = "";

        if (mEcgPpgHealthBean.getData() != null && mEcgPpgHealthBean.getData().getEcgList() != null && mEcgPpgHealthBean.getData().getEcgList().size() > 0) {
            int ecg_size = mEcgPpgHealthBean.getData().getEcgList().size();

            MyLog.i(TAG, "请求接口-获取健康原始数据 ecg_size = " + ecg_size);
            ecg_data = mEcgPpgHealthBean.getData().getEcgList().get(0).getEcg();

        } else {
            MyLog.i(TAG, "请求接口-获取健康原始数据 ecg_size = 0");
        }

        if (mEcgPpgHealthBean.getData() != null && mEcgPpgHealthBean.getData().getPpgList() != null && mEcgPpgHealthBean.getData().getPpgList().size() > 0) {
            int ppg_size = mEcgPpgHealthBean.getData().getPpgList().size();
            MyLog.i(TAG, "请求接口-获取健康原始数据 ppg_size = " + ppg_size);
            ppg_data = mEcgPpgHealthBean.getData().getPpgList().get(0).getPpg();

        } else {
            MyLog.i(TAG, "请求接口-获取健康原始数据 ppg_size = 0");
        }
        if (ecg_data == null) {
            ecg_data = "";
        }

        if (ppg_data == null) {
            ppg_data = "";
        }


        if (!ecg_data.equals("") || !ppg_data.equals("")) {

            if (mHealthInfo != null) {
                mHealthInfo.setEcg_data(ecg_data);
                mHealthInfo.setPpg_data(ppg_data);
                updateUi(mHealthInfo);
            }

            MyLog.i(TAG, "请求接口-获取健康原始数据 ecg_data.len = " + ecg_data.length());
            MyLog.i(TAG, "请求接口-获取健康原始数据 ppg_size.len = " + ppg_data.length());

            boolean isSuccess = mHealthInfoUtils.MyUpdateEcgPpgData(BaseApplication.getUserId(), time, ecg_data, ppg_data);

            if (isSuccess) {
                MyLog.i(TAG, "修改健康表成功！");
            } else {
                MyLog.i(TAG, "修改健康失败！");
            }
        }

    }


}
