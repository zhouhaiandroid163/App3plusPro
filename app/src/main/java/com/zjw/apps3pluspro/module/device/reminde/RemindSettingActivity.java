package com.zjw.apps3pluspro.module.device.reminde;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.MyActivityManager;

/**
 * 更多设置
 */
public class RemindSettingActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = RemindSettingActivity.class.getSimpleName();
    //轻量级存储
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    private Context mContext;
    private MyActivityManager manager;

    private TextView tv_more_notice_meeting, tv_more_notice_water, tv_more_notice_drug, tv_more_notice_sit;
    private TextView tv_more_notice_alarm_clock, tv_more_notice_not;

    private LinearLayout more_notice_rl_not;

    @Override
    protected int setLayoutId() {
        return R.layout.remind_setting_activity;
    }

    @Override
    protected void initViews() {
        super.initViews();
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        mContext = RemindSettingActivity.this;
        initView();
        initData();
    }

    protected void onResume() {
        super.onResume();
        updateUi();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void initView() {
        ((TextView) findViewById(R.id.public_head_title)).setText(getText(R.string.device_remind_set_title));
        findViewById(R.id.public_head_back).setOnClickListener(this);

        tv_more_notice_meeting = (TextView) findViewById(R.id.tv_more_notice_meeting);
        tv_more_notice_water = (TextView) findViewById(R.id.tv_more_notice_water);
        tv_more_notice_drug = (TextView) findViewById(R.id.tv_more_notice_drug);
        tv_more_notice_sit = (TextView) findViewById(R.id.tv_more_notice_sit);

        tv_more_notice_alarm_clock = (TextView) findViewById(R.id.tv_more_notice_alarm_clock);
        tv_more_notice_not = (TextView) findViewById(R.id.tv_more_notice_not);

        more_notice_rl_not = (LinearLayout) findViewById(R.id.more_notice_rl_not);

        findViewById(R.id.more_notice_rl_meeting).setOnClickListener(this);
        findViewById(R.id.more_notice_rl_water).setOnClickListener(this);
        findViewById(R.id.more_notice_rl_drug).setOnClickListener(this);
        findViewById(R.id.more_notice_rl_sit).setOnClickListener(this);

        findViewById(R.id.more_notice_rl_alarm_clock).setOnClickListener(this);
        findViewById(R.id.more_notice_rl_not).setOnClickListener(this);

        LinearLayout layoutDrinkWater = findViewById(R.id.layoutDrinkWater);
        if(mBleDeviceTools.getIsSupportDrinkWater()){
            layoutDrinkWater.setVisibility(View.VISIBLE);
        } else {
            layoutDrinkWater.setVisibility(View.GONE);
        }

        LinearLayout layoutLongSit = findViewById(R.id.layoutLongSit);
        if(mBleDeviceTools.getIsSupportLongSit()){
            layoutLongSit.setVisibility(View.VISIBLE);
        } else {
            layoutLongSit.setVisibility(View.GONE);
        }

        LinearLayout layoutMeeting = findViewById(R.id.layoutMeeting);
        if(mBleDeviceTools.getIsSupportMeeting()){
            layoutMeeting.setVisibility(View.VISIBLE);
        } else {
            layoutMeeting.setVisibility(View.GONE);
        }

        LinearLayout layoutDrug = findViewById(R.id.layoutDrug);
        if(mBleDeviceTools.getIsSupportDrug()){
            layoutDrug.setVisibility(View.VISIBLE);
        } else {
            layoutDrug.setVisibility(View.GONE);
        }
    }

    private void initData() {

//        sb_notice_drug.setChecked(mNtfUsp.GetValue(KEY_MEDICAL, 0) == 1);
//        sb_notice_meeting.setChecked(mNtfUsp.GetValue(KEY_MEETING, 0) == 1);
//        sb_notice_sit.setChecked(mNtfUsp.GetValue(KEY_LONG_SIT, 0) == 1);
//        sb_notice_weater.setChecked(mNtfUsp.GetValue(KEY_DRINKING, 0) == 1);


    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {

            case R.id.public_head_back:
                finish();
                break;

            //会议
            case R.id.more_notice_rl_meeting:
                intent.setClass(mContext, MeetingActivity.class);
                startActivity(intent);
                break;

            //喝水
            case R.id.more_notice_rl_water:
                intent.setClass(mContext, WaterNoticeActivity.class);
                startActivity(intent);
                break;

            //吃药
            case R.id.more_notice_rl_drug:
                intent.setClass(mContext, DurgNoticeActivity.class);
                startActivity(intent);
                break;

            //久坐
            case R.id.more_notice_rl_sit:
                intent.setClass(mContext, SitANoticetivity.class);
                startActivity(intent);
                break;

            //闹钟
            case R.id.more_notice_rl_alarm_clock:
                intent.setClass(mContext, AlarmClockActivity.class);
                startActivity(intent);
                break;

            //免打扰
            case R.id.more_notice_rl_not:
                intent.setClass(mContext, DisturbActivity.class);
                startActivity(intent);
                break;


        }
    }

    void updateUi() {


        if (RemindeUtils.getMettingModel().isMettingEnable()) {
            tv_more_notice_meeting.setText(getText(R.string.notices_opened));
            tv_more_notice_meeting.setTextColor(getResources().getColor(R.color.public_text_color1));
        } else {
            tv_more_notice_meeting.setText(getText(R.string.notices_unopened));
            tv_more_notice_meeting.setTextColor(getResources().getColor(R.color.public_unenable_text_color1));
        }

        if (RemindeUtils.getWaterModel().isDrinkingEnable()) {
            tv_more_notice_water.setText(getText(R.string.notices_opened));
            tv_more_notice_water.setTextColor(getResources().getColor(R.color.public_text_color1));
        } else {
            tv_more_notice_water.setText(getText(R.string.notices_unopened));
            tv_more_notice_water.setTextColor(getResources().getColor(R.color.public_unenable_text_color1));
        }

        if (RemindeUtils.getSitModel().isSitEnable()) {
            tv_more_notice_sit.setText(getText(R.string.notices_opened));
            tv_more_notice_sit.setTextColor(getResources().getColor(R.color.public_text_color1));
        } else {
            tv_more_notice_sit.setText(getText(R.string.notices_unopened));
            tv_more_notice_sit.setTextColor(getResources().getColor(R.color.public_unenable_text_color1));
        }

        if (RemindeUtils.getDurgModel().isMedicineEnable()) {
            tv_more_notice_drug.setText(getText(R.string.notices_opened));
            tv_more_notice_drug.setTextColor(getResources().getColor(R.color.public_text_color1));
        } else {
            tv_more_notice_drug.setText(getText(R.string.notices_unopened));
            tv_more_notice_drug.setTextColor(getResources().getColor(R.color.public_unenable_text_color1));
        }


        if (RemindeUtils.getAlarmClockisBoolean(mContext)) {
            tv_more_notice_alarm_clock.setText(getText(R.string.notices_opened));
            tv_more_notice_alarm_clock.setTextColor(getResources().getColor(R.color.public_text_color1));
        } else {
            tv_more_notice_alarm_clock.setText(getText(R.string.notices_unopened));
            tv_more_notice_alarm_clock.setTextColor(getResources().getColor(R.color.public_unenable_text_color1));
        }

        if (mBleDeviceTools.get_not_disturb()) {
            tv_more_notice_not.setText(getText(R.string.notices_opened));
            tv_more_notice_not.setTextColor(getResources().getColor(R.color.public_text_color1));
        } else {
            tv_more_notice_not.setText(getText(R.string.notices_unopened));
            tv_more_notice_not.setTextColor(getResources().getColor(R.color.public_unenable_text_color1));
        }

//        if (mBleDeviceTools.get_device_is_disturb()) {
//            more_notice_rl_not.setVisibility(View.VISIBLE);
//        } else {
        more_notice_rl_not.setVisibility(View.GONE);
//        }


    }


}


