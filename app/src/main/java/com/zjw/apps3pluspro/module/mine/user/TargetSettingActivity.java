package com.zjw.apps3pluspro.module.mine.user;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.bleservice.BtSerializeation;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.CalibrationData;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.UserBean;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.PickerView;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by android
 * on 2020/5/11.
 */
@SuppressLint("Registered")
public class TargetSettingActivity extends BaseActivity {
    private static final String TAG = TargetSettingActivity.class.getSimpleName();
    @BindView(R.id.tvStep)
    TextView tvStep;
    @BindView(R.id.layoutStep)
    LinearLayout layoutStep;
    @BindView(R.id.tvSleep)
    TextView tvSleep;
    @BindView(R.id.layoutSleep)
    LinearLayout layoutSleep;

    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();

    private WaitDialog waitDialog;

    @Override
    protected int setLayoutId() {
        return R.layout.target_activity;
    }

    @Override
    protected void initViews() {
        super.initViews();
        setTvTitle(R.string.me_fragment_target);

        waitDialog = new WaitDialog(context);
    }

    @Override
    protected void onDestroy() {
        waitDialog.dismiss();
        super.onDestroy();
    }

    @BindView(R.id.layoutCal)
    LinearLayout layoutCal;
    @BindView(R.id.layoutDistance)
    LinearLayout layoutDistance;
    @BindView(R.id.layoutActivityTime)
    LinearLayout layoutActivityTime;
    @Override
    protected void onResume() {
        super.onResume();

        initStepTarget();
        initSleepTarget();

        initCalTarget();
        initActivityTimeTarget();
        initDistanceTarget();

//        if (mBleDeviceTools.getIsSupportCalorieTarget()) {
//            layoutCal.setVisibility(View.VISIBLE);
//        } else {
//            layoutCal.setVisibility(View.GONE);
//        }
//        if (mBleDeviceTools.getIsSupportDistanceTarget()) {
//            layoutDistance.setVisibility(View.VISIBLE);
//        } else {
//            layoutDistance.setVisibility(View.GONE);
//        }
        if (mBleDeviceTools.getIsSupportActivityTimeTarget()) {
            layoutActivityTime.setVisibility(View.VISIBLE);
        } else {
            layoutActivityTime.setVisibility(View.GONE);
        }
    }

    private void initDistanceTarget() {
        tvDistance.setText(mUserSetTools.get_user_distance_target());
        if (mUserSetTools.get_user_unit_type()) {
            tvDistanceUnit.setText(getResources().getString(R.string.sport_distance_unit));
        } else {
            tvDistanceUnit.setText(getResources().getString(R.string.unit_mi));
        }
    }

    private void initActivityTimeTarget() {
        tvActivityTime.setText(mUserSetTools.get_user_activity_target());
    }

    private void initCalTarget() {
        tvCal.setText(mUserSetTools.get_user_cal_target());
    }

    private void initStepTarget() {
        String sport_target = !JavaUtil.checkIsNull(mUserSetTools.get_user_exercise_target()) ? mUserSetTools.get_user_exercise_target() : String.valueOf(DefaultVale.USER_SPORT_TARGET);
        tvStep.setText(sport_target);
    }

    @BindView(R.id.tvCal)
    TextView tvCal;
    @BindView(R.id.tvActivityTime)
    TextView tvActivityTime;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.tvDistanceUnit)
    TextView tvDistanceUnit;

    @OnClick({R.id.layoutStep, R.id.layoutSleep, R.id.layoutCal, R.id.layoutActivityTime, R.id.layoutDistance})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layoutStep:
                showStepDialog();
                break;
            case R.id.layoutSleep:
                showSleepDialog();
                break;
            case R.id.layoutCal:
                initCalDialog();
                break;
            case R.id.layoutDistance:
                initDistanceDialog();
                break;
            case R.id.layoutActivityTime:
                initActivityTimeDialog();
                break;
        }
    }

    private void initActivityTimeDialog() {
        List<String> dataSex = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            dataSex.add(String.valueOf(i * 15 + 15));
        }
        int goal = Integer.parseInt(mUserSetTools.get_user_activity_target());
        int selected = goal / 15 - 1;

        showGoalDialog(2, dataSex, selected);
    }

    private void initDistanceDialog() {
        List<String> dataSex = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            dataSex.add(String.valueOf(i + 1));
        }
        int goal = Integer.parseInt(mUserSetTools.get_user_distance_target());
        int selected = goal - 1;

        showGoalDialog(1, dataSex, selected);
    }

    private void initCalDialog() {
        List<String> dataSex = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            dataSex.add(String.valueOf(i * 100 + 100));
        }
        int goal = Integer.parseInt(mUserSetTools.get_user_cal_target());
        int selected = goal / 100 - 1;

        showGoalDialog(0, dataSex, selected);
    }

    String textTargetStep = "3000";

    private void showStepDialog() {
        View view = getLayoutInflater().inflate(R.layout.target_step_dialog, null);
        Dialog dialog = new Dialog(this, R.style.shareStyle);
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

        PickerView pvList = view.findViewById(R.id.pvList);

        List<String> dataSex = new ArrayList<String>();

        for (int i = 3000; i < 30001; ) {
            dataSex.add(String.valueOf(i));
            i = i + 1000;
        }
        String target_steps = mUserSetTools.get_user_exercise_target();
        textTargetStep = target_steps;
        pvList.setData(dataSex, (Integer.parseInt(target_steps) - 3000) / 1000);

        pvList.setOnSelectListener(text -> textTargetStep = text);

        view.findViewById(R.id.tvCancel).setOnClickListener(v -> dialog.cancel());
        view.findViewById(R.id.tvOk).setOnClickListener(v -> {
            mUserSetTools.set_user_exercise_target(textTargetStep);
            dialog.cancel();
            initStepTarget();

            uploadStep();
        });
        dialog.onWindowAttributesChanged(wl);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void uploadStep() {
        BroadcastTools.sendBleTargetStepData(context);

        CalibrationData mCalibrationData = new CalibrationData();
        mCalibrationData.setSportTarget(mUserSetTools.get_user_exercise_target());

        waitDialog.show(getString(R.string.loading0));
        new Handler().postDelayed(() -> uploadCalibrationInfo(mCalibrationData), Constants.serviceHandTime);
    }

    String textTargetSleep = "8.0";

    private void showSleepDialog() {
        View view = getLayoutInflater().inflate(R.layout.target_step_dialog, null);
        Dialog dialog = new Dialog(this, R.style.shareStyle);
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

        PickerView pvList = view.findViewById(R.id.pvList);

        List<String> dataSex = new ArrayList<String>();

        for (double i = 4.0; i < 10.1; ) {
            dataSex.add(String.valueOf(i));
            i = i + 0.5;
        }
        int target_sleep = Integer.parseInt(mUserSetTools.get_user_sleep_target());
        int index = (target_sleep / 60 - 4) * 2;
        int min = target_sleep % 60;
        if (min != 0) {
            index = index + 1;
        }

        pvList.setData(dataSex, index);

        pvList.setOnSelectListener(text -> textTargetSleep = text);

        view.findViewById(R.id.tvCancel).setOnClickListener(v -> dialog.cancel());
        view.findViewById(R.id.tvOk).setOnClickListener(v -> {

            int minute = (int) (Double.parseDouble(textTargetSleep) * 60);
            mUserSetTools.set_user_sleep_target(String.valueOf(minute));
            dialog.cancel();

            initSleepTarget();

            upLoadSleep();
        });
        dialog.onWindowAttributesChanged(wl);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void upLoadSleep() {
        CalibrationData mCalibrationData = new CalibrationData();
        mCalibrationData.setSleepTarget(mUserSetTools.get_user_sleep_target());

        waitDialog.show(getString(R.string.loading0));
        new Handler().postDelayed(() -> uploadCalibrationInfo(mCalibrationData), Constants.serviceHandTime);

        writeRXCharacteristic(BtSerializeation.sendGoalData(3, Integer.parseInt(mUserSetTools.get_user_sleep_target())));
    }

    private void initSleepTarget() {
        String sleep_target = !JavaUtil.checkIsNull(mUserSetTools.get_user_sleep_target()) ? mUserSetTools.get_user_sleep_target() : String.valueOf(DefaultVale.USER_SLEEP_TARGET);
        tvSleep.setText(MyTime.getHours(sleep_target));
    }

    private void uploadCalibrationInfo(CalibrationData mCalibrationData) {
        RequestInfo mRequestInfo = RequestJson.modifyCalibrationInfo(mCalibrationData);
        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject result) {
                        waitDialog.close();
                        UserBean mUserBean = ResultJson.UserBean(result);
                        //请求成功
                        if (mUserBean.isRequestSuccess()) {
                            if (mUserBean.uploadUserSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-修改个人信息 成功");
                            } else if (mUserBean.uploadUserSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-修改个人信息 失败");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else {
                                MyLog.i(TAG, "请求接口-修改个人信息 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-修改个人信息 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        waitDialog.close();
                        MyLog.i(TAG, "请求接口-修改个人信息 请求失败 = message = " + arg0.getMessage());
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);
                    }
                });
    }

    String textGoal = "0";

    private void showGoalDialog(int type, List<String> dataSex, int selected) {
        switch (type) {
            case 0:
                textGoal = mUserSetTools.get_user_cal_target();
                break;
            case 1:
                textGoal = mUserSetTools.get_user_distance_target();
                break;
            case 2:
                textGoal = mUserSetTools.get_user_activity_target();
                break;
        }
        View view = getLayoutInflater().inflate(R.layout.target_step_dialog, null);
        Dialog dialog = new Dialog(this, R.style.shareStyle);
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

        PickerView pvList = view.findViewById(R.id.pvList);

        pvList.setData(dataSex, selected);

        pvList.setOnSelectListener(text -> textGoal = text);

        view.findViewById(R.id.tvCancel).setOnClickListener(v -> dialog.cancel());
        view.findViewById(R.id.tvOk).setOnClickListener(v -> {
            switch (type) {
                case 0:
                    mUserSetTools.set_user_cal_target(textGoal);
                    initCalTarget();
                    if (mBleDeviceTools.getIsSupportCalorieTarget()) {
                        writeRXCharacteristic(BtSerializeation.sendGoalData(0, Integer.parseInt(mUserSetTools.get_user_cal_target())));
                    }
                    break;
                case 1:
                    mUserSetTools.set_user_distance_target(textGoal);
                    initDistanceTarget();
                    int distance = 0;
                    if (mUserSetTools.get_user_unit_type()) {
                        distance = Integer.parseInt(mUserSetTools.get_user_distance_target()) * 1000;
                    } else {
                        distance = Integer.parseInt(mUserSetTools.get_user_distance_target()) * 1610;
                    }
                    if (mBleDeviceTools.getIsSupportDistanceTarget()) {
                        writeRXCharacteristic(BtSerializeation.sendGoalData(1, distance));
                    }
                    break;
                case 2:
                    mUserSetTools.set_user_activity_target(textGoal);
                    initActivityTimeTarget();
                    writeRXCharacteristic(BtSerializeation.sendGoalData(2, Integer.parseInt(mUserSetTools.get_user_activity_target())));
                    break;
            }
            dialog.cancel();
        });
        dialog.onWindowAttributesChanged(wl);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

}
