package com.zjw.apps3pluspro.module.device.reminde;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.device.entity.SitModel;
import com.zjw.apps3pluspro.bleservice.BleConstant;
import com.zjw.apps3pluspro.bleservice.BtSerializeation;
import com.zjw.apps3pluspro.view.picker.PickerFourView;
import com.zjw.apps3pluspro.view.picker.PickerOneView;
import com.zjw.apps3pluspro.view.picker.PickerThreeView;
import com.zjw.apps3pluspro.view.picker.PickerTwoView;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.ArrayList;
import java.util.List;


/**
 * 久坐提醒
 */
public class SitANoticetivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = SitANoticetivity.class.getSimpleName();
    private Context mContext;
    private SitModel mSitModel;


    private SwitchCompat sb_notice_sit;

    private TextView tv_sit_start_time, tv_sit_time_bar, tv_sit_end_time, tv_sit_cycle, tv_sit_hours;
    private TextView tv_sit_title_cycle, tv_sit_title_time;
    private RelativeLayout rl_sit_cycle, rl_sit_time;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_sit;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = SitANoticetivity.this;
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

        tv_sit_start_time = (TextView) findViewById(R.id.tv_sit_start_time);
        tv_sit_time_bar = (TextView) findViewById(R.id.tv_sit_time_bar);
        tv_sit_end_time = (TextView) findViewById(R.id.tv_sit_end_time);
        tv_sit_cycle = (TextView) findViewById(R.id.tv_sit_cycle);
        tv_sit_hours = (TextView) findViewById(R.id.tv_sit_hours);

        tv_sit_title_cycle = (TextView) findViewById(R.id.tv_sit_title_cycle);
        tv_sit_title_time = (TextView) findViewById(R.id.tv_sit_title_time);

        rl_sit_cycle = (RelativeLayout) findViewById(R.id.rl_sit_cycle);
        rl_sit_time = (RelativeLayout) findViewById(R.id.rl_sit_time);
        rl_sit_cycle.setOnClickListener(this);
        rl_sit_time.setOnClickListener(this);

        sb_notice_sit = (SwitchCompat) findViewById(R.id.sb_notice_sit);
        sb_notice_sit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                mSitModel.setSitEnable(isChecked);
                updateUI();

            }
        });


        findViewById(R.id.tvRightText).setVisibility(View.VISIBLE);
        findViewById(R.id.tvRightText).setOnClickListener(this);
        ((TextView) findViewById(R.id.public_head_title)).setText(R.string.long_sit_title);

    }


    void initData() {

        mSitModel = RemindeUtils.getSitModel();

    }

    void updateUI() {

        MyLog.i(TAG, "getSitCycleTime = " + String.valueOf(mSitModel.getSitCycleTime()));

        tv_sit_cycle.setText(String.valueOf(mSitModel.getSitCycleTime()));
        tv_sit_start_time.setText(mSitModel.getStartTime());
        tv_sit_end_time.setText(mSitModel.getEndTime());
        sb_notice_sit.setChecked(mSitModel.isSitEnable());

        if (mSitModel.isSitEnable()) {
            rl_sit_cycle.setVisibility(View.VISIBLE);
            rl_sit_time.setVisibility(View.VISIBLE);

            tv_sit_start_time.setTextColor(getResources().getColor(R.color.white));
            tv_sit_time_bar.setTextColor(getResources().getColor(R.color.white));
            tv_sit_end_time.setTextColor(getResources().getColor(R.color.white));
            tv_sit_cycle.setTextColor(getResources().getColor(R.color.white));
            tv_sit_hours.setTextColor(getResources().getColor(R.color.white));

            tv_sit_title_time.setTextColor(getResources().getColor(R.color.white));
            tv_sit_title_cycle.setTextColor(getResources().getColor(R.color.white));
        } else {
            rl_sit_cycle.setVisibility(View.GONE);
            rl_sit_time.setVisibility(View.GONE);

            tv_sit_start_time.setTextColor(getResources().getColor(R.color.white_50));
            tv_sit_time_bar.setTextColor(getResources().getColor(R.color.white_50));
            tv_sit_end_time.setTextColor(getResources().getColor(R.color.white_50));
            tv_sit_cycle.setTextColor(getResources().getColor(R.color.white_50));
            tv_sit_hours.setTextColor(getResources().getColor(R.color.white_50));

            tv_sit_title_time.setTextColor(getResources().getColor(R.color.white_50));
            tv_sit_title_cycle.setTextColor(getResources().getColor(R.color.white_50));
        }

    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.public_no_bg_head_back:
                finish();
                break;
            case R.id.rl_sit_cycle:
                showNofifaceSectionDialog();
                break;
            case R.id.rl_sit_time:
                showNofifaceCycleDialog();
                break;
            case R.id.tvRightText:
//                if (mSitModel.isOldTime()) {
//                    InputErrorDialog();
//                } else {
//                    saveSetting();
//                }
                saveSetting();
                break;


        }
    }


    int start_hour = 8;
    int start_min = 0;
    int end_hour = 20;
    int end_min = 0;

    /**
     * 提醒区间设置对话框
     */
    private void showNofifaceSectionDialog() {
        // TODO Auto-generated method stub
        View view = getLayoutInflater().inflate(R.layout.dialog_notiface_section, null);
        final Dialog dialog = new Dialog(mContext, R.style.shareStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;


        PickerOneView pv_notiface_section_start_hour = (PickerOneView) view.findViewById(R.id.pv_notiface_section_start_hour);
        PickerTwoView pv_notiface_section_start_min = (PickerTwoView) view.findViewById(R.id.pv_notiface_section_start_min);
        PickerThreeView pv_notiface_section_end_hour = (PickerThreeView) view.findViewById(R.id.pv_notiface_section_end_hour);
        PickerFourView pv_notiface_section_end_min = (PickerFourView) view.findViewById(R.id.pv_notiface_section_end_min);


        start_hour = mSitModel.getSitStartHourTime();
        start_min = mSitModel.getSitStartMinTime();
        end_hour = mSitModel.getSitEndHourTime();
        end_min = mSitModel.getSitEndMinTime();

        MyLog.i(TAG, "start_hour = " + start_hour);
        MyLog.i(TAG, "start_min = " + start_min);
        MyLog.i(TAG, "end_hour = " + end_hour);
        MyLog.i(TAG, "end_min = " + end_min);

        pv_notiface_section_start_hour.setData(RemindeUtils.getHourListData(), start_hour);
        pv_notiface_section_start_min.setData(RemindeUtils.getMinListData(), start_min);
        pv_notiface_section_end_hour.setData(RemindeUtils.getHourListData(), end_hour);
        pv_notiface_section_end_min.setData(RemindeUtils.getMinListData(), end_min);


        pv_notiface_section_start_hour.setOnSelectListener(new PickerOneView.onSelectListener() {

            @Override
            public void onSelect(String text) {

                MyLog.i(TAG, "pv_notiface_section_start_hour = text = " + text);

                start_hour = Integer.valueOf(text);


            }
        });

        pv_notiface_section_start_min.setOnSelectListener(new PickerTwoView.onSelectListener() {

            @Override
            public void onSelect(String text) {

                MyLog.i(TAG, "pv_notiface_section_start_min = text = " + text);

                start_min = Integer.valueOf(text);


            }
        });

        pv_notiface_section_end_hour.setOnSelectListener(new PickerThreeView.onSelectListener() {

            @Override
            public void onSelect(String text) {

                MyLog.i(TAG, "pv_notiface_section_end_hour = text = " + text);

                end_hour = Integer.valueOf(text);


            }
        });

        pv_notiface_section_end_min.setOnSelectListener(new PickerFourView.onSelectListener() {

            @Override
            public void onSelect(String text) {

                MyLog.i(TAG, "pv_notiface_section_end_min = text = " + text);

                end_min = Integer.valueOf(text);

            }
        });


        view.findViewById(R.id.tv_notiface_section_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        view.findViewById(R.id.tv_notiface_section_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSitModel.setSitStartHourTime(start_hour);
                mSitModel.setSitStartMinTime(start_min);
                mSitModel.setSitEndHourTime(end_hour);
                mSitModel.setSitEndMinTime(end_min);

                updateUI();

                dialog.cancel();


            }
        });

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();


    }


    int cycle = 1;

    /**
     * 间隔时间设置对话框
     */
    private void showNofifaceCycleDialog() {
        // TODO Auto-generated method stub
        View view = getLayoutInflater().inflate(R.layout.dialog_notiface_cycle, null);
        final Dialog dialog = new Dialog(mContext, R.style.shareStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;


        PickerOneView pv_notiface_cycle = (PickerOneView) view.findViewById(R.id.pv_notiface_cycle);


        List<String> dataCycle = new ArrayList<String>();
        dataCycle.add("1");
        dataCycle.add("2");
        dataCycle.add("3");
        dataCycle.add("4");


        cycle = mSitModel.getSitCycleTime();


        MyLog.i(TAG, "cycle = " + cycle);

        pv_notiface_cycle.setData(dataCycle, 0);

        for (int i = 0; i < dataCycle.size(); i++) {

            if (cycle == Integer.valueOf(dataCycle.get(i))) {

                pv_notiface_cycle.setData(dataCycle, i);

                break;
            }
        }


        pv_notiface_cycle.setOnSelectListener(new PickerOneView.onSelectListener() {

            @Override
            public void onSelect(String text) {

                MyLog.i(TAG, "pv_notiface_cycle = text = " + text);

                cycle = Integer.valueOf(text);


            }
        });


        view.findViewById(R.id.tv_notiface_cycle_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        view.findViewById(R.id.tv_notiface_cycle_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSitModel.setSitCycleTime(cycle);

                updateUI();

                dialog.cancel();


            }
        });

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();


    }

    void InputErrorDialog() {


        new AlertDialog.Builder(SitANoticetivity.this)
                .setTitle(getString(R.string.dialog_prompt))//设置对话框标题

                .setMessage(getString(R.string.set_reminde_error))//设置显示的内容

                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {//添加确定按钮

                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                        saveSetting();

                    }

                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {//添加返回按钮


            @Override

            public void onClick(DialogInterface dialog, int which) {//响应事件

                // TODO Auto-generated method stub


            }

        }).show();//在按键响应事件中显示此对话框
    }


    void saveSetting() {

        RemindeUtils.setSitModel(mSitModel);

        if (HomeActivity.getBlueToothStatus() != BleConstant.STATE_CONNECTED) {
            AppUtils.showToast(mContext, R.string.no_connection_notification);
            return;
        }

        //写入数据到蓝牙
        writeRXCharacteristic(BtSerializeation.setSitNotification(RemindeUtils.getSitModel()));

        AppUtils.showToast(mContext, R.string.save_ok);

        finish();
    }


}
