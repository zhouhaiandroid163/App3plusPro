package com.zjw.apps3pluspro.module.device.reminde;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.device.entity.AlarmClockModel;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.log.MyLog;

import butterknife.OnClick;


/**
 * 编辑和添加闹钟闹钟页面
 */
public class AlarmClockCycleActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener {
    private final String TAG = AlarmClockCycleActivity.class.getSimpleName();
    private Context mContext;

    private String weekDay = "", Mon = "0", Tue = "0", Wed = "0", Thu = "0", Fri = "0", Sat = "0", Sun = "0", Nev = "0";

    AlarmClockModel mAlarmClockModel = null;


    private CheckBox cx_alarm_clock_cycle_1, cx_alarm_clock_cycle_2, cx_alarm_clock_cycle_3,
            cx_alarm_clock_cycle_4, cx_alarm_clock_cycle_5, cx_alarm_clock_cycle_6, cx_alarm_clock_cycle_7;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_alarm_clock_cycle;
    }
    @Override
    protected void initViews() {
        super.initViews();
        mContext = AlarmClockCycleActivity.this;
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        initView();
        initData();
    }

    @OnClick({R.id.tvRightText})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvRightText:
                weekDay = Nev + Mon + Tue + Wed + Thu + Fri + Sat + Sun;
                MyLog.i(TAG, "闹钟周期 weekDay = " + weekDay);
                MyLog.i(TAG, "闹钟周期 getMyRepeat = " + getMyRepeat());

                mAlarmClockModel.setRepeat(getMyRepeat());
                mAlarmClockModel.setWeekDay(weekDay);

                Intent intent = new Intent(mContext, AlarmClockCycleActivity.class);
                intent.putExtra(IntentConstants.Intent_AlarmClock, mAlarmClockModel);
                setResult(AddAlarmClockActivity.AddAlarmClockResultEditData, intent);
                manager.popOneActivity(this);
                break;
        }
    }

    private void initView() {
        findViewById(R.id.tvRightText).setVisibility(View.VISIBLE);
        findViewById(R.id.public_head_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.public_head_title)).setText(getText(R.string.clock_set_des));

        cx_alarm_clock_cycle_1 = (CheckBox) findViewById(R.id.cx_alarm_clock_cycle_1);
        cx_alarm_clock_cycle_2 = (CheckBox) findViewById(R.id.cx_alarm_clock_cycle_2);
        cx_alarm_clock_cycle_3 = (CheckBox) findViewById(R.id.cx_alarm_clock_cycle_3);
        cx_alarm_clock_cycle_4 = (CheckBox) findViewById(R.id.cx_alarm_clock_cycle_4);
        cx_alarm_clock_cycle_5 = (CheckBox) findViewById(R.id.cx_alarm_clock_cycle_5);
        cx_alarm_clock_cycle_6 = (CheckBox) findViewById(R.id.cx_alarm_clock_cycle_6);
        cx_alarm_clock_cycle_7 = (CheckBox) findViewById(R.id.cx_alarm_clock_cycle_7);

        cx_alarm_clock_cycle_1.setOnCheckedChangeListener(this);
        cx_alarm_clock_cycle_2.setOnCheckedChangeListener(this);
        cx_alarm_clock_cycle_3.setOnCheckedChangeListener(this);
        cx_alarm_clock_cycle_4.setOnCheckedChangeListener(this);
        cx_alarm_clock_cycle_5.setOnCheckedChangeListener(this);
        cx_alarm_clock_cycle_6.setOnCheckedChangeListener(this);
        cx_alarm_clock_cycle_7.setOnCheckedChangeListener(this);


    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.public_head_back:
                manager.popOneActivity(this);
                break;
        }
    }

    int getMyRepeat() {
//        Nev + Mon + Tue + Wed + Thu + Fri + Sat + Sun;

        int result = 0;

        if (Integer.valueOf(Mon) != 0) {
            result += 1;
        }
        if (Integer.valueOf(Tue) != 0) {
            result += 2;
        }
        if (Integer.valueOf(Wed) != 0) {
            result += 4;
        }
        if (Integer.valueOf(Thu) != 0) {
            result += 8;
        }
        if (Integer.valueOf(Fri) != 0) {
            result += 16;
        }
        if (Integer.valueOf(Sat) != 0) {
            result += 32;
        }
        if (Integer.valueOf(Sun) != 0) {
            result += 64;
        }

        return result;

    }

    void initData() {


        Intent intent = getIntent();

        mAlarmClockModel = intent.getParcelableExtra(IntentConstants.Intent_AlarmClock);

        MyLog.i(TAG, "接收到 = mAlarmClockModel = " + mAlarmClockModel);


        if (mAlarmClockModel != null) {

            weekDay = !JavaUtil.checkIsNull(String.valueOf(mAlarmClockModel.getWeekDay())) ? String.valueOf(mAlarmClockModel.getWeekDay()) : "0";

            MyLog.i(TAG, "闹钟问题 weekDay = " + weekDay);


            if (weekDay.length() == 8) {

                if (weekDay.charAt(0) == '1') {
                    MyLog.i(TAG, "测试001 - 0");
                } else {
                    if (weekDay.charAt(1) == '1') {
                        cx_alarm_clock_cycle_1.setChecked(true);
                        mRepeat1 |= 0x01;
                        Mon = "1";
                        MyLog.i(TAG, "测试001 - 1 - 1");
                    } else {
                        cx_alarm_clock_cycle_1.setChecked(false);
                        MyLog.i(TAG, "测试001 - 1 - 0");
                        mRepeat1 &= ~0x01;
                        Mon = "0";
                    }

                    if (weekDay.charAt(2) == '1') {
                        cx_alarm_clock_cycle_2.setChecked(true);
                        mRepeat1 |= 0x02;
                        Tue = "1";
                        MyLog.i(TAG, "测试001 - 2 - 1");
                    } else {
                        MyLog.i(TAG, "测试001 - 2 - 0");
                        cx_alarm_clock_cycle_2.setChecked(false);
                        mRepeat1 &= ~0x02;
                        Tue = "0";
                    }

                    if (weekDay.charAt(3) == '1') {
                        cx_alarm_clock_cycle_3.setChecked(true);
                        mRepeat1 |= 0x04;
                        Wed = "1";
                        MyLog.i(TAG, "测试001 - 3 - 1");
                    } else {
                        cx_alarm_clock_cycle_3.setChecked(false);
                        mRepeat1 &= ~0x04;
                        Wed = "0";
                        MyLog.i(TAG, "测试001 - 3 - 0");
                    }

                    if (weekDay.charAt(4) == '1') {

                        cx_alarm_clock_cycle_4.setChecked(true);
                        mRepeat1 |= 0x08;
                        Thu = "1";

                        MyLog.i(TAG, "测试001 - 4 - 1");
                    } else {
                        cx_alarm_clock_cycle_4.setChecked(false);
                        mRepeat1 &= ~0x08;
                        Thu = "0";
                        MyLog.i(TAG, "测试001 - 4 - 0");
                    }

                    if (weekDay.charAt(5) == '1') {

                        mRepeat1 |= 0x10;
                        Fri = "1";

                        cx_alarm_clock_cycle_5.setChecked(true);
                        MyLog.i(TAG, "测试001 - 5 - 1");
                    } else {
                        cx_alarm_clock_cycle_5.setChecked(false);
                        mRepeat1 &= ~0x10;
                        Fri = "0";
                        MyLog.i(TAG, "测试001 - 5 - 0");
                    }

                    if (weekDay.charAt(6) == '1') {
                        cx_alarm_clock_cycle_6.setChecked(true);
                        mRepeat1 |= 0x20;
                        Sat = "1";
                        MyLog.i(TAG, "测试001 - 6 - 1");
                    } else {
                        cx_alarm_clock_cycle_6.setChecked(false);
                        mRepeat1 &= ~0x20;
                        Sat = "0";
                        MyLog.i(TAG, "测试001 - 6 - 0");
                    }

                    if (weekDay.charAt(7) == '1') {
                        cx_alarm_clock_cycle_7.setChecked(true);
                        mRepeat1 |= 0x40;
                        Sun = "1";

                        MyLog.i(TAG, "测试001 - 7 - 1");
                    } else {
                        cx_alarm_clock_cycle_7.setChecked(false);
                        mRepeat1 &= ~0x40;
                        Sun = "0";
                        MyLog.i(TAG, "测试001 - 7 - 0");
                    }
                }
            }


        }


    }


    private int mRepeat1 = 0;
    private MyActivityManager manager;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


        if (cx_alarm_clock_cycle_1.isChecked() || cx_alarm_clock_cycle_2.isChecked()
                || cx_alarm_clock_cycle_3.isChecked() || cx_alarm_clock_cycle_4.isChecked()
                || cx_alarm_clock_cycle_5.isChecked() || cx_alarm_clock_cycle_6.isChecked()
                || cx_alarm_clock_cycle_7.isChecked()) {
            Nev = "0";
        } else {
            Nev = "1";
        }

        switch (buttonView.getId()) {
            case R.id.cx_alarm_clock_cycle_1:
                if (isChecked) {
                    mRepeat1 |= 0x01;
                    Mon = "1";
                } else {
                    mRepeat1 &= ~0x01;
                    Mon = "0";
                }
                break;
            case R.id.cx_alarm_clock_cycle_2:
                if (isChecked) {
                    mRepeat1 |= 0x02;
                    Tue = "1";
                } else {
                    mRepeat1 &= ~0x02;
                    Tue = "0";
                }
                break;
            case R.id.cx_alarm_clock_cycle_3:
                if (isChecked) {
                    mRepeat1 |= 0x04;
                    Wed = "1";
                } else {
                    mRepeat1 &= ~0x04;
                    Wed = "0";
                }
                break;
            case R.id.cx_alarm_clock_cycle_4:
                if (isChecked) {
                    mRepeat1 |= 0x08;
                    Thu = "1";
                } else {
                    mRepeat1 &= ~0x08;
                    Thu = "0";
                }
                break;
            case R.id.cx_alarm_clock_cycle_5:
                if (isChecked) {
                    mRepeat1 |= 0x10;
                    Fri = "1";
                } else {
                    mRepeat1 &= ~0x10;
                    Fri = "0";
                }
                break;
            case R.id.cx_alarm_clock_cycle_6:
                if (isChecked) {
                    mRepeat1 |= 0x20;
                    Sat = "1";
                } else {
                    mRepeat1 &= ~0x20;
                    Sat = "0";
                }
                break;
            case R.id.cx_alarm_clock_cycle_7:
                if (isChecked) {
                    mRepeat1 |= 0x40;
                    Sun = "1";
                } else {
                    mRepeat1 &= ~0x40;
                    Sun = "0";
                }
                break;
        }
    }


}
