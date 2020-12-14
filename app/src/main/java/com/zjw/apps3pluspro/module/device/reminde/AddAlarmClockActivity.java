package com.zjw.apps3pluspro.module.device.reminde;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.module.device.entity.AlarmClockModel;
import com.zjw.apps3pluspro.view.picker.PickerOneView;
import com.zjw.apps3pluspro.view.picker.PickerTwoView;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.log.MyLog;


/**
 * 编辑和添加闹钟闹钟页面
 */
public class AddAlarmClockActivity extends Activity implements OnClickListener {
    private final String TAG = AddAlarmClockActivity.class.getSimpleName();
    private Context mContext;

    private MyActivityManager manager;

    public static final int AddAlarmClockResultEditData = 500;

    private String clockHour, clockMin;

    private TextView public_head_title;

    AlarmClockModel mAlarmClockModel = null;

    private String AlarmClockType = "";

    //睡眠目标的两个pickView
    private PickerOneView pv_add_alarm_hour;
    private PickerTwoView pv_add_alarm_min;

    private Button btn_delete_alarm_color;
    private TextView tv_add_alarm_clock_only_one;
    private TextView tv_add_alarm_clock_1, tv_add_alarm_clock_2, tv_add_alarm_clock_3, tv_add_alarm_clock_4, tv_add_alarm_clock_5, tv_add_alarm_clock_6, tv_add_alarm_clock_7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm_clock);
        mContext = AddAlarmClockActivity.this;
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.tvRightText).setVisibility(View.VISIBLE);
        btn_delete_alarm_color = (Button) findViewById(R.id.btn_delete_alarm_color);
        btn_delete_alarm_color.setOnClickListener(this);
        findViewById(R.id.tvRightText).setOnClickListener(this);

        findViewById(R.id.public_head_back).setOnClickListener(this);
        public_head_title = (TextView) findViewById(R.id.public_head_title);


        pv_add_alarm_hour = (PickerOneView) findViewById(R.id.pv_add_alarm_hour);
        pv_add_alarm_min = (PickerTwoView) findViewById(R.id.pv_add_alarm_min);

        pv_add_alarm_hour.setOnSelectListener(new PickerOneView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                clockHour = text;
                mAlarmClockModel.setTimeAlarm(clockHour + ":" + clockMin);
            }
        });

        pv_add_alarm_min.setOnSelectListener(new PickerTwoView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                clockMin = text;
                mAlarmClockModel.setTimeAlarm(clockHour + ":" + clockMin);
            }
        });
        findViewById(R.id.btn_delete_alarm_color).setOnClickListener(this);
        findViewById(R.id.rl_add_alarm_cycle).setOnClickListener(this);

        tv_add_alarm_clock_only_one = (TextView) findViewById(R.id.tv_add_alarm_clock_only_one);
        tv_add_alarm_clock_1 = (TextView) findViewById(R.id.tv_add_alarm_clock_1);
        tv_add_alarm_clock_2 = (TextView) findViewById(R.id.tv_add_alarm_clock_2);
        tv_add_alarm_clock_3 = (TextView) findViewById(R.id.tv_add_alarm_clock_3);
        tv_add_alarm_clock_4 = (TextView) findViewById(R.id.tv_add_alarm_clock_4);
        tv_add_alarm_clock_5 = (TextView) findViewById(R.id.tv_add_alarm_clock_5);
        tv_add_alarm_clock_6 = (TextView) findViewById(R.id.tv_add_alarm_clock_6);
        tv_add_alarm_clock_7 = (TextView) findViewById(R.id.tv_add_alarm_clock_7);

    }


    void initData() {


        Intent intent = getIntent();

        mAlarmClockModel = intent.getParcelableExtra(IntentConstants.Intent_AlarmClock);
        AlarmClockType = intent.getStringExtra(IntentConstants.Intent_AlarmClockType);

        MyLog.i(TAG, "接收到 = mAlarmClockModel = " + mAlarmClockModel);

        //添加闹钟
        if (!AlarmClockType.equals(IntentConstants.Intent_AlarmClockEdit)) {
            mAlarmClockModel = AlarmClockModel.getDefultAlarmClockModel(mContext);
            btn_delete_alarm_color.setVisibility(View.GONE);
            public_head_title.setText(getString(R.string.add_clock));
        } else {
            btn_delete_alarm_color.setVisibility(View.VISIBLE);
            public_head_title.setText(getString(R.string.edit_alarm_clock));
        }
        updateUi();

    }


    void updateUi() {

        String isTime = !JavaUtil.checkIsNull(String.valueOf(mAlarmClockModel.getTimeAlarm())) ? String.valueOf(mAlarmClockModel.getTimeAlarm()) : "";
        clockHour = isTime.split(":")[0];
        clockMin = isTime.split(":")[1];

        pv_add_alarm_hour.setData(RemindeUtils.getHourListData(), Integer.valueOf(clockHour));
        pv_add_alarm_min.setData(RemindeUtils.getMinListData(), Integer.valueOf(clockMin));

        if (mAlarmClockModel.weekDay.charAt(0) == '1') {
            tv_add_alarm_clock_1.setVisibility(View.GONE);
            tv_add_alarm_clock_2.setVisibility(View.GONE);
            tv_add_alarm_clock_3.setVisibility(View.GONE);
            tv_add_alarm_clock_4.setVisibility(View.GONE);
            tv_add_alarm_clock_5.setVisibility(View.GONE);
            tv_add_alarm_clock_6.setVisibility(View.GONE);
            tv_add_alarm_clock_7.setVisibility(View.GONE);
            tv_add_alarm_clock_only_one.setVisibility(View.VISIBLE);

        } else {

            tv_add_alarm_clock_only_one.setVisibility(View.GONE);

            if (mAlarmClockModel.weekDay.charAt(1) == '1') {
                tv_add_alarm_clock_1.setVisibility(View.VISIBLE);
            } else {
                tv_add_alarm_clock_1.setVisibility(View.GONE);
            }

            if (mAlarmClockModel.weekDay.charAt(2) == '1') {
                tv_add_alarm_clock_2.setVisibility(View.VISIBLE);
            } else {
                tv_add_alarm_clock_2.setVisibility(View.GONE);
            }

            if (mAlarmClockModel.weekDay.charAt(3) == '1') {
                tv_add_alarm_clock_3.setVisibility(View.VISIBLE);
            } else {
                tv_add_alarm_clock_3.setVisibility(View.GONE);
            }

            if (mAlarmClockModel.weekDay.charAt(4) == '1') {
                tv_add_alarm_clock_4.setVisibility(View.VISIBLE);
            } else {
                tv_add_alarm_clock_4.setVisibility(View.GONE);
            }

            if (mAlarmClockModel.weekDay.charAt(5) == '1') {
                tv_add_alarm_clock_5.setVisibility(View.VISIBLE);
            } else {
                tv_add_alarm_clock_5.setVisibility(View.GONE);
            }

            if (mAlarmClockModel.weekDay.charAt(6) == '1') {
                tv_add_alarm_clock_6.setVisibility(View.VISIBLE);
            } else {
                tv_add_alarm_clock_6.setVisibility(View.GONE);
            }

            if (mAlarmClockModel.weekDay.charAt(7) == '1') {
                tv_add_alarm_clock_7.setVisibility(View.VISIBLE);
            } else {
                tv_add_alarm_clock_7.setVisibility(View.GONE);
            }
        }


    }


    private int mRepeat1 = 0;


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = null;
        switch (v.getId()) {

            //返回
            case R.id.public_head_back:
                manager.popOneActivity(this);
                break;

            //删除闹钟
            case R.id.btn_delete_alarm_color:
                MyLog.i(TAG, "删除闹钟 = getRepeat = " + mAlarmClockModel.getRepeat());
                MyLog.i(TAG, "删除闹钟 = mRepeat1 = " + mRepeat1);

                intent = new Intent(mContext, AddAlarmClockActivity.class);
                intent.putExtra(IntentConstants.Intent_AlarmClock, mAlarmClockModel);
                setResult(AlarmClockActivity.AlarmClockResultDelete, intent);
                manager.popOneActivity(this);
                break;

            //修改闹钟周期
            case R.id.rl_add_alarm_cycle:
                MyLog.i(TAG, "修改闹钟周期 = getRepeat = " + mAlarmClockModel.getRepeat());
                MyLog.i(TAG, "修改闹钟周期 = mRepeat1 = " + mRepeat1);

                intent = new Intent(mContext, AlarmClockCycleActivity.class);
                intent.putExtra(IntentConstants.Intent_AlarmClock, mAlarmClockModel);
                startActivityForResult(intent, AddAlarmClockResultEditData);
                break;

            //保存闹钟
            case R.id.tvRightText:

                mRepeat1 = mAlarmClockModel.getRepeat();

                MyLog.i(TAG, "保存闹钟 = getRepeat = " + mAlarmClockModel.getRepeat());
                MyLog.i(TAG, "保存闹钟 = mRepeat1 = " + mRepeat1);

                //把状态强制设置成开
                mRepeat1 |= 0x80;
                mAlarmClockModel.setRepeat(mRepeat1);
                mAlarmClockModel.setTimeAlarm(clockHour + ":" + clockMin);

                intent = new Intent(mContext, AddAlarmClockActivity.class);
                intent.putExtra(IntentConstants.Intent_AlarmClock, mAlarmClockModel);
                //编辑闹钟
                if (AlarmClockType.equals(IntentConstants.Intent_AlarmClockEdit)) {
                    setResult(AlarmClockActivity.AlarmClockResultEdit, intent);
                }
                //添加闹钟
                else {
                    setResult(AlarmClockActivity.AlarmClockResultAdd, intent);
                }
                manager.popOneActivity(this);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyLog.i(TAG, "闹钟显示界面=========onActivityResult");

        //添加闹钟
        if (resultCode == AddAlarmClockResultEditData) {

            MyLog.i(TAG, "添加闹钟 = getRepeat = " + mAlarmClockModel.getRepeat());
            MyLog.i(TAG, "添加闹钟 = mRepeat1 = " + mRepeat1);

            MyLog.i(TAG, "闹钟问题 500 修改重复周期");
            MyLog.i(TAG, "mAlarmClockModel = " + mAlarmClockModel.toString());
            mAlarmClockModel = data.getParcelableExtra(IntentConstants.Intent_AlarmClock);
            if (mAlarmClockModel != null) {
                RemindeUtils.updateClockAlarmClock(mContext, mAlarmClockModel);
                updateUi();
            }
        }
    }


}
