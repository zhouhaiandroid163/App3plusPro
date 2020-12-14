package com.zjw.apps3pluspro.module.home.cycle;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.home.cycle.utils.MyCalendarUtils;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.NewTimeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.HashMap;
import java.util.Map;


/**
 * 生理周期
 */
public class CycleActivity extends BaseActivity implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener,
        OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    private static final String TAG = CycleActivity.class.getSimpleName();
    private Context mContext;
    //请轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    TextView public_head_title;


    private TextView text_cycle_end;


    CalendarView mCalendarView;
    CalendarLayout mCalendarLayout;

    private int my_year, my_mon, my_day;
    private TextView tv_calendar_cycle_date;
    private TextView text_cycle_date;
    private SwitchCompat sw_cycle_end, sw_cycle_device;
    private LinearLayout lin_cycle_type1, lin_cycle_type2;
    private TextView text_cycle_no_change;

    private String my_now_date = "";

    @Override
    protected int setLayoutId() {
        isTextDark = false;
        bgColor = R.color.title_bg_cycle;
        return R.layout.activity_cycle;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = CycleActivity.this;
        initView();
        initData();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        loadData();
    }

    private void initView() {
        // TODO Auto-generated method stub
        findViewById(R.id.public_head_back).setOnClickListener(this);
        public_head_title = (TextView) findViewById(R.id.public_head_title);
        public_head_title.setText(getString(R.string.cycle_tile));
        findViewById(R.id.public_head_back_img).setBackground(getResources().getDrawable(R.mipmap.white_back_bg));
        findViewById(R.id.rl_public_head_bg).setBackgroundColor(getResources().getColor(R.color.title_bg_cycle));
        public_head_title.setTextColor(getResources().getColor(R.color.white));

        mCalendarView = (CalendarView) findViewById(R.id.calendarView);

        mCalendarLayout = (CalendarLayout) findViewById(R.id.calendarLayout);
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnCalendarSelectListener(this);

        text_cycle_end = (TextView) findViewById(R.id.text_cycle_end);

        tv_calendar_cycle_date = (TextView) findViewById(R.id.tv_calendar_cycle_date);

        text_cycle_date = (TextView) findViewById(R.id.text_cycle_date);

        findViewById(R.id.ll_calendar_cycle_left).setOnClickListener(this);
        findViewById(R.id.ll_calendar_cycle_right).setOnClickListener(this);


        findViewById(R.id.public_head_edit).setVisibility(View.VISIBLE);
        findViewById(R.id.public_head_edit).setOnClickListener(this);

        findViewById(R.id.public_head_rili).setVisibility(View.VISIBLE);
        findViewById(R.id.public_head_rili).setOnClickListener(this);

        findViewById(R.id.lin_cycle_time).setOnClickListener(this);

        sw_cycle_end = (SwitchCompat) findViewById(R.id.sw_cycle_end);
        sw_cycle_device = (SwitchCompat) findViewById(R.id.sw_cycle_device);
//        sw_cycle_end.setOnCheckedChangeListener(this);


        sw_cycle_device.setChecked(mUserSetTools.get_nv_device_switch());
        sw_cycle_device.setOnCheckedChangeListener(this);

        lin_cycle_type1 = (LinearLayout) findViewById(R.id.lin_cycle_type1);
        lin_cycle_type2 = (LinearLayout) findViewById(R.id.lin_cycle_type2);

        text_cycle_no_change = (TextView) findViewById(R.id.text_cycle_no_change);

        findViewById(R.id.button_cycle_go_to_today).setOnClickListener(this);

        findViewById(R.id.cycle_test_data).setOnClickListener(this);
        findViewById(R.id.cycle_go_to_cycle_info).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        Intent intent;
        switch (v.getId()) {

            case R.id.public_head_back:
                finish();
                break;

            //左箭头
            case R.id.ll_calendar_cycle_left:
                mCalendarView.scrollToPre();
                break;

            //右箭头
            case R.id.ll_calendar_cycle_right:
                mCalendarView.scrollToNext();
                break;

            //编辑
            case R.id.public_head_edit:
                if (mUserSetTools.get_device_is_one_cycle()) {
                    intent = new Intent(mContext, CycleInitActivity.class);
                    intent.putExtra(CycleInitActivity.CYCLE_INIT_TAG_TYPE, CycleInitActivity.CYCLE_INIT_TAG_A);
                    startActivity(intent);

                } else if (mUserSetTools.get_nv_start_date().equals("")) {
                    intent = new Intent(mContext, CycleInitActivity.class);
                    intent.putExtra(CycleInitActivity.CYCLE_INIT_TAG_TYPE, CycleInitActivity.CYCLE_INIT_TAG_A);
                    startActivity(intent);
                } else {
                    intent = new Intent(mContext, CycleInitActivity.class);
                    intent.putExtra(CycleInitActivity.CYCLE_INIT_TAG_TYPE, CycleInitActivity.CYCLE_INIT_TAG_B);
                    startActivity(intent);
                }
                break;

            //跳转到今天
            case R.id.public_head_rili:
                mCalendarView.scrollToCurrent();
                my_now_date = MyTime.getTime();
                updateUiType(1);
                break;

            //修改最近一次
            case R.id.lin_cycle_time:
                SetCycleDateDialog();
                break;

            //跳转到今天
            case R.id.button_cycle_go_to_today:
                mCalendarView.scrollToCurrent();
                my_now_date = MyTime.getTime();
                updateUiType(1);
                break;

            case R.id.cycle_test_data:
                onClickCycle();
                break;

            //跳转到生理周期说明
            case R.id.cycle_go_to_cycle_info:
                intent = new Intent(CycleActivity.this, CycleInfoActivity.class);
                startActivity(intent);
                break;

        }
    }

    protected void initData() {

//        mCalendarView.setOnlyCurrentMode();//仅显示当前月份
        mCalendarView.setFixMode();
//        mCalendarView.setAllMode();
        my_now_date = MyTime.getTime();
        tv_calendar_cycle_date.setText(MyTime.getMonth());

    }


    @SuppressWarnings("all")
    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {

        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);

        return calendar;
    }


    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    //选中日期
    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {

        String date = NewTimeUtils.FormatDateYYYYMM(calendar.getYear(), calendar.getMonth());

        tv_calendar_cycle_date.setText(date);

        String tag = calendar.getScheme();

//        if (isClick) {

        int year = calendar.getYear();
        int mon = calendar.getMonth();
        int day = calendar.getDay();

        String year_str = "";
        String mon_str = "";
        String day_str = "";

        year_str = String.valueOf(year);
        mon_str = String.valueOf(mon);
        day_str = String.valueOf(day);

        if (mon < 10) {
            mon_str = "0" + mon_str;
        }
        if (day < 10) {
            day_str = "0" + day_str;
        }


        my_now_date = year_str + "-" + mon_str + "-" + day_str;

        updateDateUi(my_now_date);


//        }

        MyLog.i(TAG, "生理周期测试 选中 = tag = " + tag + "   isClick= " + isClick);

    }

    int UiState = 0;

    @Override
    public void onYearChange(int year) {
//        mTextMonthDay.setText(String.valueOf(year));
    }


    public static int TODAY_COLOCR = 0xFFFF0000;

    //加载数据
    void loadData() {
        loadCycleData();
        mCalendarView.setSchemeDate(getCycData());
        BroadcastTools.sendBleCycleData(mContext);
    }

    //计算Cycl数据
    void loadCycleData() {


        int know_cyc = mUserSetTools.get_nv_cycle();
        int know_period = mUserSetTools.get_nv_lenght();
        String cycle_start_date = mUserSetTools.get_nv_start_date();


        if (know_cyc < 15 || know_cyc > 100) {
            know_cyc = 28;

        }

        if (know_period < 2 || know_period > 14) {
            know_period = 5;
        }

        if (TextUtils.isEmpty(cycle_start_date)) {
            cycle_start_date = MyTime.getTime();
        }

        text_cycle_date.setText(cycle_start_date);

        my_year = Integer.parseInt(cycle_start_date.split("-")[0]);
        my_mon = Integer.parseInt(cycle_start_date.split("-")[1]);
        my_day = Integer.parseInt(cycle_start_date.split("-")[2]);


        //经期天数
        int day_period = 0;
        //安全期1天数
        int day_security_one = 0;
        //危险期
        int day_danger = 0;
        //安全期2天数
        int day_security_two = 0;

//===========================================================
        //经期天数
        day_period = know_period;
        //安全期2天数-固定
        day_security_two = 9;
        //系数1-固定
        int coe_1 = 10;
        //安全期1天数
        day_security_one = know_cyc - day_period - coe_1 - day_security_two;

        //当安全期1小于0，也就是负数的时候
        if (day_security_one < 0) {
            day_danger = coe_1 + day_security_one;
            day_security_one = 0;
            //否则大于等于0
        } else {
            day_danger = coe_1;
        }


        MyLog.i(TAG, "经期规律==  开始 = " + cycle_start_date + "  周期 = " + know_cyc + "  天数 = " + know_period + "  ==");
        MyLog.i(TAG, "经期规律    经期 = " + day_period + "  安全期1 = " + day_security_one + "  危险期 = " + day_danger + "  安全期2 = " + day_security_two);
        MyLog.i(TAG, "经期规律 开始 = " + cycle_start_date + "  周期 = " + know_cyc + "  天数 = " + know_period + "\n" + "经期 = " + day_period + "  安全期1 = " + day_security_one + "  危险期 = " + day_danger + "  安全期2 = " + day_security_two);


        mBleDeviceTools.set_device_cycle_jingqi(day_period);
        mBleDeviceTools.set_device_cycle_anqunqiyi(day_security_one);
        mBleDeviceTools.set_device_cycle_weixianqi(day_danger);
        mBleDeviceTools.set_device_cycle_anquanqier(day_security_two);

    }

    Map<String, Calendar> getCycData() {


        int today_year = mCalendarView.getCurYear();
        int today_month = mCalendarView.getCurMonth();
        int today_day = mCalendarView.getCurDay();

        MyLog.i(TAG, "生理周期测试 today_year = " + today_year + "  today_month = " + today_month + "  one = " + today_day);

        int period = mBleDeviceTools.get_device_cycle_jingqi();
        int one = mBleDeviceTools.get_device_cycle_anqunqiyi();
        int danger = mBleDeviceTools.get_device_cycle_weixianqi();
        int two = mBleDeviceTools.get_device_cycle_anquanqier();


        Map<String, Calendar> map = new HashMap<>();

        int max_lenght = period + one + danger + two;

        int number_1 = period;
        int number_2 = period + one;
        int number_3 = period + one + danger;
        int number_4 = period + one + danger + two;


        MyLog.i(TAG, "测试日期 max_lenght = " + max_lenght + "  period = " + period + "  one = " + one + "  danger = " + danger + " two = " + two);

        int years = my_year;
        int month = my_mon;
        int day = my_day;

        boolean is_start = false;
        int xiabiao = 0;


        for (int y = my_year; y < (my_year + 2); y++) {
            for (int m = 1; m <= 12; m++) {

//                获取某月的天数
                int d_max = MyCalendarUtils.getMonthDaysCount(y, m);

                for (int d = 1; d <= d_max; d++) {

                    if (years == y && month == m && day == d) {

                        is_start = true;
                    }

                    if (is_start) {

                        int number = xiabiao % max_lenght;

                        number = number + 1;

                        if (number > 0 && number <= number_1) {

//                           MyLog.i(TAG,"测试日期 取余 map_2 number = " + number + " 经期 ");

                            map.put(getSchemeCalendar(y, m, d, MyCalendarUtils.ONE_BG_COLOR, MyCalendarUtils.ONE_TYPE).toString(),
                                    getSchemeCalendar(y, m, d, MyCalendarUtils.ONE_BG_COLOR, MyCalendarUtils.ONE_TYPE));

                            if (y == today_year && m == today_month && d == today_day) {
                                TODAY_COLOCR = MyCalendarUtils.ONE_TEXT_COLOR;
                                MyLog.i(TAG, "生理周期测试 = 今天 1111");
                            }


                        } else if (number > number_1 && number <= number_2) {

//                           MyLog.i(TAG,"测试日期 取余 map_2 number = " + number + " 安全期1 ");

                            map.put(getSchemeCalendar(y, m, d, MyCalendarUtils.TWO_BG_COLOR, MyCalendarUtils.TWO_TYPE).toString(),
                                    getSchemeCalendar(y, m, d, MyCalendarUtils.TWO_BG_COLOR, MyCalendarUtils.TWO_TYPE));

                            if (y == today_year && m == today_month && d == today_day) {
                                TODAY_COLOCR = MyCalendarUtils.TWO_TEXT_COLOR;
                                MyLog.i(TAG, "生理周期测试 = 今天 2222");
                            }


                        } else if (number > number_2 && number <= number_3) {
//                           MyLog.i(TAG,"测试日期 取余 map_2 number = " + number + " 危险期 ");

                            map.put(getSchemeCalendar(y, m, d, MyCalendarUtils.THREE_BG_COLOR, MyCalendarUtils.THREE_TYPE).toString(),
                                    getSchemeCalendar(y, m, d, MyCalendarUtils.THREE_BG_COLOR, MyCalendarUtils.THREE_TYPE));

                            if (y == today_year && m == today_month && d == today_day) {
                                TODAY_COLOCR = MyCalendarUtils.THREE_TEXT_COLOR;
                                MyLog.i(TAG, "生理周期测试 = 今天 3333");
                            }

                        } else if (number > number_3 && number <= number_4) {

//                           MyLog.i(TAG,"测试日期 取余 map_2 number = " + number + " 安全期2 ");

                            map.put(getSchemeCalendar(y, m, d, MyCalendarUtils.TWO_BG_COLOR, MyCalendarUtils.TWO_TYPE).toString(),
                                    getSchemeCalendar(y, m, d, MyCalendarUtils.TWO_BG_COLOR, MyCalendarUtils.TWO_TYPE));

                            if (y == today_year && m == today_month && d == today_day) {
                                TODAY_COLOCR = MyCalendarUtils.TWO_TEXT_COLOR;
                                MyLog.i(TAG, "生理周期测试 = 今天 4444");
                            }
                        }
                        xiabiao += 1;
                    }
                }
            }
        }

        return map;
    }

    String dialog_cycle_start_date = "";

    void SetCycleDateDialog() {

        dialog_cycle_start_date = mUserSetTools.get_nv_start_date();

        if (TextUtils.isEmpty(dialog_cycle_start_date)) {
            dialog_cycle_start_date = MyTime.getTime();
        }

        my_year = Integer.parseInt(dialog_cycle_start_date.split("-")[0]);
        my_mon = Integer.parseInt(dialog_cycle_start_date.split("-")[1]);
        my_day = Integer.parseInt(dialog_cycle_start_date.split("-")[2]);

        DatePickerDialog pickerDialog = new DatePickerDialog(CycleActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int mon, int day) {

                String now_date = year + "-" + (mon + 1) + "-" + day;

                MyLog.i(TAG, "生理周期 选择日期 = now_date = " + now_date);

                if (MyTime.getIsOldCycleDate(now_date)) {

                    my_year = year;
                    my_mon = mon + 1;
                    my_day = day;

                    dialog_cycle_start_date = my_year + "-" + my_mon + "-" + my_day;

                    if (TextUtils.isEmpty(dialog_cycle_start_date)) {
                        dialog_cycle_start_date = MyTime.getTime();
                    }

                    mUserSetTools.set_nv_start_date(dialog_cycle_start_date);
                    loadData();
                } else {
                    AppUtils.showToast(mContext, R.string.cycle_init_tip2);
                }

//                text_cycle_init_date.setText(CycleInitStartDate);

            }
        }, Integer.valueOf(my_year), my_mon - 1, my_day);
        pickerDialog.setTitle(null);
        pickerDialog.show();

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {

//            case R.id.sw_cycle_end:
//
//
//                break;

            case R.id.sw_cycle_device:
                mUserSetTools.set_nv_device_switch(isChecked);
                BroadcastTools.sendBleCycleData(mContext);

                break;


        }


    }

    void updateDateUi(String now_date) {

        String cycle_start_date = mUserSetTools.get_nv_start_date();

        String today_date = MyTime.getTime();

        //是否是以前的日期 //选中的日期和今天的日期比
        boolean is_old_date_1 = MyTime.compare_date(now_date, today_date);

        //是否是以前的日期 //选中的日期和今天的日期比
        boolean is_old_date_2 = MyTime.compare_date(now_date, cycle_start_date);


        MyLog.i(TAG, "经期测试 = cycle_start_date = " + cycle_start_date + " now_date = " + now_date + " today_date = " + today_date + "  is_old_date_1= " + is_old_date_1 + " is_old_date_2 = " + is_old_date_2);


        //是否是当天
        if (now_date.equals(today_date)) {
            updateUiType(1);
        }
        //不是当天
        else {
            //不是以前的日期，也就是未来时间的时候
            if (!is_old_date_1) {
                updateUiType(2);
                //不是以前的日期，也就是过去时间的时候
            } else if (is_old_date_2) {
                updateUiType(3);
                //否则是可以操作的时间段
            } else {
                updateUiType(1);

            }
        }

    }

    void updateUiType(int type) {
        MyLog.i(TAG, "updateUiType = type = " + type);
        lin_cycle_type1.setVisibility(View.GONE);
        lin_cycle_type2.setVisibility(View.GONE);

        if (type == 1) {
            lin_cycle_type1.setVisibility(View.VISIBLE);
        } else if (type == 2) {
            lin_cycle_type2.setVisibility(View.VISIBLE);
            text_cycle_no_change.setText(getString(R.string.cycle_new_no_change));
        } else if (type == 3) {
            lin_cycle_type2.setVisibility(View.VISIBLE);
            text_cycle_no_change.setText(getString(R.string.cycle_old_no_change));
        }
        updateUiState();

    }

    void updateUiState() {


        String cycle_start_date = mUserSetTools.get_nv_start_date();

        int count = MyCalendarUtils.differentDaysByMillisecond(my_now_date, cycle_start_date);
        count += 1;

        MyLog.i(TAG, "经期测试 = count = " + count);

        //经期天数
        int day_period = mBleDeviceTools.get_device_cycle_jingqi();
        int x1 = day_period;
        int x2 = day_period + 6;

        //t+1
        if (count == 1) {
            MyLog.i(TAG, "经期测试 = R = R1");
            UiState = 1;
            sw_cycle_end.setChecked(true);
            text_cycle_end.setText(getString(R.string.cycle_start));
        }
        //f+2
        else if (count > 1 && count < x1) {
            MyLog.i(TAG, "经期测试 = R = R2");
            UiState = 2;
            sw_cycle_end.setChecked(false);
            text_cycle_end.setText(getString(R.string.cycle_end));
        }
        //t+2
        else if (count == x1) {
            MyLog.i(TAG, "经期测试 = R = R3");
            UiState = 3;
            sw_cycle_end.setChecked(true);
            text_cycle_end.setText(getString(R.string.cycle_end));
        }
        //f+2
        else if (count > x1 && count < x2) {
            MyLog.i(TAG, "经期测试 = R = R4");
            UiState = 4;
            sw_cycle_end.setChecked(false);
            text_cycle_end.setText(getString(R.string.cycle_end));
        }
        //f+1
        else if (count >= x2) {
            MyLog.i(TAG, "经期测试 = R = R5");
            UiState = 5;
            sw_cycle_end.setChecked(false);
            text_cycle_end.setText(getString(R.string.cycle_start));
        }
    }


    void onClickCycle() {

        String cycle_start_date = mUserSetTools.get_nv_start_date();

        //是否是以前的日期 //选中的日期和今天的日期比

        int count = MyCalendarUtils.differentDaysByMillisecond(my_now_date, cycle_start_date);
        count += 1;

        if (UiState == 1) {
            MyLog.i(TAG, "经期测试 = RT = RT1");
            SetCycleDateDialog();
        } else if (UiState == 2) {
            MyLog.i(TAG, "经期测试 = RT = RT2 = " + count);
            mUserSetTools.set_nv_lenght(count);
            loadData();
        } else if (UiState == 3) {
            MyLog.i(TAG, "经期测试 = RT = RT3");
            AppUtils.showToast(mContext, R.string.cycle_init_tip1);
        } else if (UiState == 4) {
            MyLog.i(TAG, "经期测试 = RT = RT4");
            mUserSetTools.set_nv_lenght(count);
            loadData();
        } else if (UiState == 5) {
            MyLog.i(TAG, "经期测试 = RT = RT5");
            mUserSetTools.set_nv_start_date(my_now_date);
            loadData();
        }

        int my_year = Integer.parseInt(my_now_date.split("-")[0]);
        int my_mon = Integer.parseInt(my_now_date.split("-")[1]);
        int my_day = Integer.parseInt(my_now_date.split("-")[2]);

        MyLog.i(TAG, "生理周期测试 跳转到指定日期 = my_year = " + +my_year + "  my_mon= " + my_mon + "  my_day = " + my_day);
        mCalendarView.scrollToCalendar(my_year, my_mon, my_day);

//        updateDateUi(my_now_date);
    }


}
