package com.zjw.apps3pluspro.module.home.ppg;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.home.ecg.EcgReportActivity;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.entity.HealthInfo;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.RoundProgressView;


public class PpgMesureDetailsActivity extends BaseActivity implements OnClickListener {
    private String TAG = "PpgMesureDetailsActivity";
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    private Context mContext;


    TextView mesure_history_heart, mesure_details_systolic, mesure_details_diastolic;

    HealthInfo mHealthInfo = null;


    //顶部圆形进度条-运动
    private RoundProgressView ppgRoundProgressView;

    //健康指数
    private TextView ppg_details_health_value;
    private TextView ppg_details_health_state;
    private TextView health_index_text;

    private TextView public_head_title;

    @Override
    protected int setLayoutId() {
        isTextDark = false;
        bgColor = R.color.title_bg_ecg;
        return R.layout.activity_ppg_mesure_details;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = PpgMesureDetailsActivity.this;
        initView();
        initData();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        findViewById(R.id.public_head_back).setOnClickListener(this);
        public_head_title = (TextView) findViewById(R.id.public_head_title);
        public_head_title.setTextColor(getResources().getColor(R.color.white));
        public_head_title.setBackgroundColor(getResources().getColor(R.color.title_bg_ecg));
        public_head_title.setText(getResources().getString(R.string.ppg_health_index));
        public_head_title.setOnClickListener(this);
        findViewById(R.id.public_head_back_img).setBackground(getResources().getDrawable(R.mipmap.white_back_bg));
        findViewById(R.id.rl_public_head_bg).setBackgroundColor(getResources().getColor(R.color.title_bg_ecg));

        mesure_history_heart = (TextView) findViewById(R.id.mesure_history_heart);
        mesure_details_systolic = (TextView) findViewById(R.id.mesure_details_systolic);
        mesure_details_diastolic = (TextView) findViewById(R.id.mesure_details_diastolic);

        ppg_details_health_value = (TextView) findViewById(R.id.ppg_details_health_value);
        ppg_details_health_state = (TextView) findViewById(R.id.ppg_details_health_state);
        health_index_text = (TextView) findViewById(R.id.health_index_text);


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
            String measure_time = !JavaUtil.checkIsNull(mHealthInfo.getMeasure_time()) ? mHealthInfo.getMeasure_time() : "";

            String health_value = !JavaUtil.checkIsNull(mHealthInfo.getIndex_health_index()) ? mHealthInfo.getIndex_health_index() : "0";

            mesure_history_heart.setText(heart);
            mesure_details_systolic.setText(systolic);
            mesure_details_diastolic.setText(diastolic);
//            public_head_title.setText(measure_time);


            int health_number = Integer.parseInt(health_value);

            ppgRoundProgressView.setProgress(health_number / 100f);


            if (health_number > 0) {
                if (health_number <= 70) {
                    ppg_details_health_state.setText(getString(R.string.health_index_sub));
                    health_index_text.setText(getString(R.string.hrv_help_health_proposal_text1));
                } else if (health_number <= 90) {
                    ppg_details_health_state.setText(getString(R.string.health_index_good));
                    health_index_text.setText(getString(R.string.hrv_help_health_proposal_text2));
                } else {
                    ppg_details_health_state.setText(getString(R.string.health_index_optimal));
                    health_index_text.setText(getString(R.string.hrv_help_health_proposal_text3));
                }
                ppg_details_health_value.setText(health_value);
            } else {
                ppg_details_health_value.setText(getString(R.string.sleep_gang));
                ppg_details_health_state.setText(getString(R.string.sleep_gang));
            }


        }


    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.public_head_back:
                finish();
                break;

            case R.id.ecg_details_to_presentation:
                if (mHealthInfo != null) {
                    Intent intent = new Intent(mContext, EcgReportActivity.class);
                    intent.putExtra(IntentConstants.HealthInfo, mHealthInfo);
                    startActivity(intent);
                }
                break;
//            case R.id.ppg_details_index_help:
//                Intent intent = new Intent(mContext, HrvIndexHelpActivity.class);
//                startActivity(intent);
//                break;


        }
    }


}
