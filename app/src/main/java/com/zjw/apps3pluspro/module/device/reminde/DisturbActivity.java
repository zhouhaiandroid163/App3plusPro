package com.zjw.apps3pluspro.module.device.reminde;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.bleservice.BtSerializeation;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.NewTimeUtils;

import androidx.constraintlayout.widget.ConstraintLayout;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 免打扰提醒
 */
public class DisturbActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = DisturbActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    private MyActivityManager manager;
    private SwitchCompat sb_notice_not_disturb;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_disturb;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = DisturbActivity.this;
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        initView();
    }

    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {

        sb_notice_not_disturb = (SwitchCompat) findViewById(R.id.sb_notice_not_disturb);
        sb_notice_not_disturb.setChecked(mBleDeviceTools.get_not_disturb());
        sb_notice_not_disturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //勿扰时间段-不支持
                if (mBleDeviceTools.get_device_is_time_disturb()) {
                    updateUI();
                }
            }
        });

        findViewById(R.id.tvRightText).setOnClickListener(this);
        findViewById(R.id.tvRightText).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.public_head_title)).setText(R.string.do_not_disturb);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.public_no_bg_head_back:
                finish();
                break;

            case R.id.tvRightText:
                mBleDeviceTools.set_not_disturb(sb_notice_not_disturb.isChecked());

                if (mBleDeviceTools.get_not_disturb()) {
                    writeRXCharacteristic(BtSerializeation.setDoNotDistrub(1));
                } else {
                    writeRXCharacteristic(BtSerializeation.setDoNotDistrub(0));
                }

                if (mBleDeviceTools.get_device_is_time_disturb()) {
                    mBleDeviceTools.setNotDisturbStartHour(startHour);
                    mBleDeviceTools.setNotDisturbStartMin(startMin);
                    mBleDeviceTools.setNotDisturbEndHour(endHour);
                    mBleDeviceTools.setNotDisturbEndMin(endMin);

                    byte[] valueData = new byte[4];
                    valueData[0] = (byte) startHour;
                    valueData[1] = (byte) startMin;
                    valueData[2] = (byte) endHour;
                    valueData[3] = (byte) endMin;

                    byte[] bleData = BtSerializeation.getBleData(valueData, BtSerializeation.CMD_01, BtSerializeation.KEY_NOT_DISTURB);
                    writeRXCharacteristic(bleData);
                }

                finish();
                break;
        }
    }

    @BindView(R.id.tvStartTime)
    TextView tvStartTime;
    @BindView(R.id.tvEndTime)
    TextView tvEndTime;
    @BindView(R.id.tvTitleTip)
    TextView tvTitleTip;

    @BindView(R.id.tvTitle1)
    TextView tvTitle1;
    @BindView(R.id.tvTitle2)
    TextView tvTitle2;

    @BindView(R.id.layoutStartTime)
    ConstraintLayout layoutStartTime;
    @BindView(R.id.layoutEndTime)
    ConstraintLayout layoutEndTime;

    int startHour = 0;
    int startMin = 0;
    int endHour = 0;
    int endMin = 0;

    @Override
    protected void initDatas() {
        super.initDatas();
        startHour = mBleDeviceTools.getNotDisturbStartHour();
        startMin = mBleDeviceTools.getNotDisturbStartMin();
        endHour = mBleDeviceTools.getNotDisturbEndHour();
        endMin = mBleDeviceTools.getNotDisturbEndMin();

        tvStartTime.setText(NewTimeUtils.getTimeStringHHMM(startHour, startMin));
        tvEndTime.setText(NewTimeUtils.getTimeStringHHMM(endHour, endMin));

        if (mBleDeviceTools.get_device_is_time_disturb()) {
            layoutStartTime.setVisibility(View.VISIBLE);
            layoutEndTime.setVisibility(View.VISIBLE);
            tvTitleTip.setVisibility(View.GONE);
            updateUI();
        } else {
            layoutStartTime.setVisibility(View.GONE);
            layoutEndTime.setVisibility(View.GONE);
            tvTitleTip.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.layoutStartTime, R.id.layoutEndTime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layoutStartTime:
                setTime(1, startHour, startMin);
                break;
            case R.id.layoutEndTime:
                setTime(2, endHour, endMin);
                break;
        }
    }

    private void setTime(int type, int hour, int min) {
        String[] time = {"" + hour, "" + min};
        new TimePickerDialog(mContext, (view, hourOfDay, minute) -> {
            if (type == 1) {
                startHour = hourOfDay;
                startMin = minute;
                tvStartTime.setText(NewTimeUtils.getTimeStringHHMM(hourOfDay, minute));
            } else {
                endHour = hourOfDay;
                endMin = minute;
                tvEndTime.setText(NewTimeUtils.getTimeStringHHMM(hourOfDay, minute));
            }
        }, Integer.parseInt(time[0]), Integer.parseInt(time[1]), true).show();
    }

    void updateUI() {
        tvStartTime.setText(NewTimeUtils.getTimeStringHHMM(startHour, startMin));
        tvEndTime.setText(NewTimeUtils.getTimeStringHHMM(endHour, endMin));
        if (sb_notice_not_disturb.isChecked()) {
            layoutStartTime.setEnabled(true);
            layoutEndTime.setEnabled(true);
            tvStartTime.setTextColor(getResources().getColor(R.color.white));
            tvEndTime.setTextColor(getResources().getColor(R.color.white));
            tvTitle1.setTextColor(getResources().getColor(R.color.white));
            tvTitle2.setTextColor(getResources().getColor(R.color.white));
        } else {
            layoutStartTime.setEnabled(false);
            layoutEndTime.setEnabled(false);
            tvStartTime.setTextColor(getResources().getColor(R.color.white_50));
            tvEndTime.setTextColor(getResources().getColor(R.color.white_50));
            tvTitle1.setTextColor(getResources().getColor(R.color.white_50));
            tvTitle2.setTextColor(getResources().getColor(R.color.white_50));
        }
    }
}


