package com.zjw.apps3pluspro.module.home.cycle;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TextView;


import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.PickerView;

import java.util.ArrayList;
import java.util.List;


/**
 * 设置生理周期参数
 */
public class CycleInitActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "CycleInitActivity";
    private Context mContext;
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();


    // 弹框的信息
    private Dialog dialog;

    int CycleInitCycle = 0;
    int CycleInitLenght = 0;
    String CycleInitStartDate = "";

    private TextView text_cycle_init_cycle, text_cycle_init_lenght, text_cycle_init_date;

    private int my_year, my_mon, my_day;

    public static final String CYCLE_INIT_TAG_TYPE = "CYCLE_INIT_TAG_TYPE";

    //第一次使用，需要保存之后跳转到生理周期页面，并关闭当前页面
    public static final String CYCLE_INIT_TAG_A = "CYCLE_INIT_TAG_A";
    //不是第一次,需要保存后，关闭当前页面就行了
    public static final String CYCLE_INIT_TAG_B = "CYCLE_INIT_TAG_B";
    //是第一次,在个人信息跳过来的，需要保存后，关闭当前页面就行了
    public static final String CYCLE_INIT_TAG_C = "CYCLE_INIT_TAG_C";

    private boolean is_one_set = false;

    private boolean is_go_to_cycle = false;

    @Override
    protected int setLayoutId() {
        isTextDark = false;
        bgColor = R.color.title_bg_cycle;
        return R.layout.activity_cycle_init;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = CycleInitActivity.this;
        initView();
        initData();
    }

    private void initView() {
        // TODO Auto-generated method stub
        ((TextView) (findViewById(R.id.public_head_title))).setText(getText(R.string.cycle_init_tile));

        findViewById(R.id.layoutCalendar).setVisibility(View.GONE);
        findViewById(R.id.layoutTitle).setBackgroundColor(getResources().getColor(R.color.title_bg_cycle));

        text_cycle_init_cycle = (TextView) findViewById(R.id.text_cycle_init_cycle);
        text_cycle_init_lenght = (TextView) findViewById(R.id.text_cycle_init_lenght);
        text_cycle_init_date = (TextView) findViewById(R.id.text_cycle_init_date);

        findViewById(R.id.lin_cycle_init_cycle).setOnClickListener(this);
        findViewById(R.id.lin_cycle_init_lenght).setOnClickListener(this);
        findViewById(R.id.lin_cycle_init_time).setOnClickListener(this);
        findViewById(R.id.button_cycle_int_save).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            //返回
            case R.id.public_head_back:
                finish();
                break;

            //重复周期
            case R.id.lin_cycle_init_cycle:
                showLenghtDialog();
                break;

            //经期长度
            case R.id.lin_cycle_init_lenght:
                showCycleDialog();
                break;

            //开始时间
            case R.id.lin_cycle_init_time:
                SetCycleDateDialog();
                break;

            //保存
            case R.id.button_cycle_int_save:

                if (text_cycle_init_date.getText().toString().trim().equals("") ||
                        text_cycle_init_cycle.getText().toString().trim().equals("") ||
                        text_cycle_init_lenght.getText().toString().trim().equals("")) {
                    AppUtils.showToast(mContext, R.string.user_par_erroe1);
                } else {
                    mUserSetTools.set_device_is_one_cycle(false);
                    mUserSetTools.set_nv_start_date(CycleInitStartDate);
                    mUserSetTools.set_nv_cycle(CycleInitCycle);
                    mUserSetTools.set_nv_lenght(CycleInitLenght);
                    if (is_go_to_cycle) {
                        Intent intent = new Intent(CycleInitActivity.this, CycleActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        finish();
                    }
                }
                break;
        }
    }

    protected void initData() {

        try {
            Intent intent = this.getIntent();
            if (intent.getExtras().getString(CYCLE_INIT_TAG_TYPE) != null) {
                if (intent.getExtras().getString(CYCLE_INIT_TAG_TYPE).equals(CYCLE_INIT_TAG_A)) {
                    is_one_set = true;
                    is_go_to_cycle = true;

                } else if (intent.getExtras().getString(CYCLE_INIT_TAG_TYPE).equals(CYCLE_INIT_TAG_B)) {
                    is_one_set = false;
                    is_go_to_cycle = false;

                } else if (intent.getExtras().getString(CYCLE_INIT_TAG_TYPE).equals(CYCLE_INIT_TAG_C)) {
                    is_one_set = true;
                    is_go_to_cycle = false;
                }

                if (is_one_set) {
                    text_cycle_init_date.setText("");
                    text_cycle_init_cycle.setText("");
                    text_cycle_init_lenght.setText("");
                } else {
                    CycleInitCycle = mUserSetTools.get_nv_cycle();
                    CycleInitLenght = mUserSetTools.get_nv_lenght();
                    CycleInitStartDate = mUserSetTools.get_nv_start_date();

                    if (CycleInitCycle < 15 || CycleInitCycle > 100) {
                        CycleInitCycle = 28;
                    }

                    if (CycleInitLenght < 2 || CycleInitLenght > 14) {
                        CycleInitLenght = 5;
                    }

                    if (TextUtils.isEmpty(CycleInitStartDate)) {
                        CycleInitStartDate = MyTime.getTime();
                    }

                    text_cycle_init_date.setText(CycleInitStartDate);
                    text_cycle_init_cycle.setText(String.valueOf(CycleInitCycle));
                    text_cycle_init_lenght.setText(String.valueOf(CycleInitLenght));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 生理周期弹框
     */
    private void showCycleDialog() {
        // TODO Auto-generated method stub
        MyLog.i(TAG, "生理周期 周期 = 类型 " + CycleInitCycle);

        View view = getLayoutInflater().inflate(R.layout.dialog_cycle, null);
        dialog = new Dialog(mContext, R.style.shareStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        TextView tv_cycle_cancel = (TextView) view.findViewById(R.id.tv_cycle_cancel);
        TextView tv_cycle_ok = (TextView) view.findViewById(R.id.tv_cycle_ok);
        PickerView pv_cycle = (PickerView) view.findViewById(R.id.pv_cycle);

        List<String> dataHeight = new ArrayList<String>();

        tv_cycle_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        tv_cycle_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                text_cycle_init_cycle.setText(String.valueOf(CycleInitCycle));
                if (CycleInitCycle > 0) {
                    mUserSetTools.set_nv_cycle(CycleInitCycle);
                }
            }
        });

        for (int i = 15; i <= 100; i++) {
            dataHeight.add("" + i);
        }

        if (CycleInitCycle < 15 || CycleInitCycle > 100) {
            CycleInitCycle = 28;
            pv_cycle.setData(dataHeight, 13);
            MyLog.i(TAG, "生理周期 周期 = CycleInitCycle = " + CycleInitCycle);
        } else {
            int index = CycleInitCycle - 15;
            pv_cycle.setData(dataHeight, index);
        }
        pv_cycle.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                CycleInitCycle = Integer.valueOf(text);
            }
        });

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * 生理周期长度
     */
    private void showLenghtDialog() {
        // TODO Auto-generated method stub
        MyLog.i(TAG, "生理周期 周期 = 长度 " + CycleInitLenght);

        View view = getLayoutInflater().inflate(R.layout.dialog_cycle, null);
        dialog = new Dialog(mContext, R.style.shareStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        TextView tv_cycle_cancel = (TextView) view.findViewById(R.id.tv_cycle_cancel);
        TextView tv_cycle_ok = (TextView) view.findViewById(R.id.tv_cycle_ok);
        PickerView pv_cycle = (PickerView) view.findViewById(R.id.pv_cycle);

        List<String> dataHeight = new ArrayList<String>();

        tv_cycle_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        tv_cycle_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();

                text_cycle_init_lenght.setText(String.valueOf(CycleInitLenght));
                if (CycleInitLenght > 0) {
                    mUserSetTools.set_nv_lenght(CycleInitLenght);
                }

            }
        });


        for (int i = 2; i <= 14; i++) {
            dataHeight.add("" + i);
        }
        if (CycleInitLenght < 2 || CycleInitLenght > 14) {
            CycleInitLenght = 5;
            pv_cycle.setData(dataHeight, 3);
            MyLog.i(TAG, "生理周期 长度 = CycleInitLenght = " + CycleInitLenght);
        } else {
            int index = CycleInitLenght - 2;
            pv_cycle.setData(dataHeight, index);
        }
        pv_cycle.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                CycleInitLenght = Integer.valueOf(text);
            }
        });

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    void SetCycleDateDialog() {

        if (TextUtils.isEmpty(CycleInitStartDate)) {
            CycleInitStartDate = MyTime.getTime();
        }

        my_year = Integer.parseInt(CycleInitStartDate.split("-")[0]);
        my_mon = Integer.parseInt(CycleInitStartDate.split("-")[1]);
        my_day = Integer.parseInt(CycleInitStartDate.split("-")[2]);


        DatePickerDialog pickerDialog = new DatePickerDialog(CycleInitActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int mon, int day) {

                String now_date = year + "-" + (mon + 1) + "-" + day;

                MyLog.i(TAG, "生理周期 选择日期 = now_date = " + now_date);

                if (MyTime.getIsOldCycleDate(now_date)) {

                    my_year = year;
                    my_mon = mon + 1;
                    my_day = day;

                    CycleInitStartDate = my_year + "-" + my_mon + "-" + my_day;

                    if (TextUtils.isEmpty(CycleInitStartDate)) {
                        CycleInitStartDate = MyTime.getTime();
                    }

                    mUserSetTools.set_nv_start_date(CycleInitStartDate);
                    text_cycle_init_date.setText(CycleInitStartDate);


                } else {
                    AppUtils.showToast(mContext, R.string.cycle_init_tip2);
                }

            }
        }, Integer.valueOf(my_year), my_mon - 1, my_day);
        pickerDialog.setTitle(null);
        pickerDialog.show();

    }


}
