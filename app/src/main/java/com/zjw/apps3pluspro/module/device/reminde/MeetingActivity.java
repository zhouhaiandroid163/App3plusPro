package com.zjw.apps3pluspro.module.device.reminde;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.widget.SwitchCompat;

import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.device.entity.MettingModel;
import com.zjw.apps3pluspro.bleservice.BleConstant;
import com.zjw.apps3pluspro.bleservice.BtSerializeation;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.log.MyLog;

/**
 * 会议提醒
 */
public class MeetingActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = MeetingActivity.class.getSimpleName();
    private Context mContext;
    private MettingModel mMettingModel;


    private SwitchCompat sb_notice_metting;
    private TextView tv_metting_date, tv_metting_time;


    private TextView tv_metting_remind_time, tv_metting_remind_date;

    private RelativeLayout rl_metting_date, rl_metting_time;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_metting;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = MeetingActivity.this;
        initView();
        initData();
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    void initView() {
        tv_metting_date = (TextView) findViewById(R.id.tv_metting_date);
        tv_metting_time = (TextView) findViewById(R.id.tv_metting_time);

        tv_metting_remind_time = (TextView) findViewById(R.id.tv_metting_remind_time);
        tv_metting_remind_date = (TextView) findViewById(R.id.tv_metting_remind_date);

        rl_metting_date = (RelativeLayout) findViewById(R.id.rl_metting_date);
        rl_metting_time = (RelativeLayout) findViewById(R.id.rl_metting_time);

        rl_metting_date.setOnClickListener(this);
        rl_metting_time.setOnClickListener(this);

        sb_notice_metting = (SwitchCompat) findViewById(R.id.sb_notice_metting);
        sb_notice_metting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                mMettingModel.setMettingEnable(isChecked);
                updateUI();

            }
        });

        findViewById(R.id.tvRightText).setVisibility(View.VISIBLE);
        findViewById(R.id.tvRightText).setOnClickListener(this);
        ((TextView) findViewById(R.id.public_head_title)).setText(R.string.metting_title);

    }

    void initData() {

        mMettingModel = RemindeUtils.getMettingModel();

        MyLog.i(TAG, "获取会提提醒数据 = mMettingModel = " + mMettingModel.toString());


    }

    void updateUI() {

        tv_metting_date.setText(mMettingModel.getDate());
        tv_metting_time.setText(mMettingModel.getTime());
        sb_notice_metting.setChecked(mMettingModel.isMettingEnable());

        if (mMettingModel.isMettingEnable()) {
            rl_metting_date.setVisibility(View.VISIBLE);
            rl_metting_time.setVisibility(View.VISIBLE);
            tv_metting_remind_time.setTextColor(getResources().getColor(R.color.white));
            tv_metting_remind_date.setTextColor(getResources().getColor(R.color.white));
            tv_metting_date.setTextColor(getResources().getColor(R.color.white));
            tv_metting_time.setTextColor(getResources().getColor(R.color.white));

        } else {
            rl_metting_date.setVisibility(View.GONE);
            rl_metting_time.setVisibility(View.GONE);
            tv_metting_remind_time.setTextColor(getResources().getColor(R.color.white_50));
            tv_metting_remind_date.setTextColor(getResources().getColor(R.color.white_50));
            tv_metting_date.setTextColor(getResources().getColor(R.color.white_50));
            tv_metting_time.setTextColor(getResources().getColor(R.color.white_50));
        }

    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.public_no_bg_head_back:
                finish();
                break;
            case R.id.rl_metting_date:
                SetMeetingDate();
                break;
            case R.id.rl_metting_time:
                SetMeetingTime();
                break;
            case R.id.tvRightText:
                saveSetting();
                break;


        }
    }


    void SetMeetingDate() {

        String[] time = MyTime.getDateSuZu();


        DatePickerDialog pickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int mon, int day) {

                mMettingModel.setMettingYear(year - 2000);
                mMettingModel.setMettingMonth(mon + 1);
                mMettingModel.setMettingDay(day);

                updateUI();
            }
        },
                //显示上次选择的日期
                mMettingModel.getMettingYear() + 2000, mMettingModel.getMettingMonth() - 1, mMettingModel.getMettingDay());
        //显示当天的日期
//                Integer.valueOf(time[0]), Integer.valueOf(time[1]) - 1, Integer.valueOf(Integer.valueOf(time[2])));
        pickerDialog.setTitle(null);
        pickerDialog.show();

    }


    void SetMeetingTime() {

        String[] time = {"" + mMettingModel.getMettingHour(), "" + mMettingModel.getMettingMin()};

        new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                mMettingModel.setMettingHour(hourOfDay);
                mMettingModel.setMettingMin(minute);

                updateUI();

            }
        },
//                mMettingModel.getMettingHour(), mMettingModel.getMettingMin(), true)
                Integer.valueOf(time[0]), Integer.valueOf(time[1]), true)
                .show();


    }


    void saveSetting() {

        RemindeUtils.setMettingModel(mMettingModel);

        if (HomeActivity.getBlueToothStatus() != BleConstant.STATE_CONNECTED) {
            AppUtils.showToast(mContext, R.string.no_connection_notification);
            return;
        }
        //写入数据到蓝牙
        writeRXCharacteristic(BtSerializeation.setMeetingNotification(RemindeUtils.getMettingModel()));

        AppUtils.showToast(mContext, R.string.save_ok);
        finish();
    }


}
