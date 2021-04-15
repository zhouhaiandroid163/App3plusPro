package com.zjw.apps3pluspro.module.mine.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.CalibrationData;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.UserBean;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 运动目标
 */
public class TargetStepActivity extends Activity implements View.OnClickListener {
    private final String TAG = TargetStepActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();

    private MyActivityManager manager;

    //标志位，1=从肤色跳转过来的，2=从我的界面跳转过来的
    String intent_tag = "";
    public static final String IntentTargetStep = "intent_target_step";

    private TextView tv_target_steps;
    private String target_steps;
    private SeekBar sb_sport_target_step;

    private WaitDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setStatusBarMode(this, true, R.color.base_activity_bg);
        setContentView(R.layout.activity_target_step);
        mContext = TargetStepActivity.this;
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        waitDialog = new WaitDialog(mContext);

        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        waitDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
    }

    private void initView() {
        findViewById(R.id.public_head_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.target_steps));

        tv_target_steps = (TextView) findViewById(R.id.tv_target_steps);
        sb_sport_target_step = (SeekBar) findViewById(R.id.sb_sport_target_step);
        //设置字体
//        tv_target_steps.setTypeface(FontsUtils.modefyCHU(mContext));
        findViewById(R.id.bton_target_step_ok).setOnClickListener(this);
    }

    List<String> data = new ArrayList<String>();

    private void initData() {

        //初始化一个数组,添加数据.
        for (int i = 0; i < Constants.TargetStepCount; i++) {
            data.add("" + (i * 1000 + Constants.TargetStepMin));
        }
        //设置最大值
        sb_sport_target_step.setMax(data.size() - 1);

        //如果步数不为空
        if (mUserSetTools.get_user_exercise_target() != null && !mUserSetTools.get_user_exercise_target().equals("")) {

            target_steps = mUserSetTools.get_user_exercise_target();

            if (Integer.parseInt(target_steps) < Constants.TargetStepMin) {

                target_steps = String.valueOf(Constants.TargetStepMin);

            } else if (Integer.parseInt(target_steps) > Constants.TargetStepMax) {

                target_steps = String.valueOf(Constants.TargetStepMax);

            } else {

                target_steps = (Integer.parseInt(target_steps) / 1000) * 1000 + "";
            }

            tv_target_steps.setText(target_steps);
            int index = ((int) Float.parseFloat(target_steps) - Constants.TargetStepMin) / 1000;
            sb_sport_target_step.setProgress(index);
            //如果步数为空
        } else {
            tv_target_steps.setText(String.valueOf(Constants.TargetStepDefult));
            sb_sport_target_step.setProgress(7);
        }


        sb_sport_target_step.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                MyLog.i(TAG, "onProgressChanged() = progress = " + progress);
                tv_target_steps.setText(data.get(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                MyLog.i(TAG, "onStartTrackingTouch()");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MyLog.i(TAG, "onStopTrackingTouch()");
            }
        });

        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        if (bundle != null && bundle.getString(IntentTargetStep) != null && !bundle.getString(IntentTargetStep).equals("")) {
            intent_tag = bundle.getString(IntentTargetStep);
            MyLog.i(TAG, "intent_tag = " + intent_tag);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //返回
            case R.id.public_head_back:
                finish();
                break;

            //保存
            case R.id.bton_target_step_ok:

                String save_target = tv_target_steps.getText().toString();

                //如果为空，则赋默认值
                if (JavaUtil.checkIsNull(save_target)) {
                    mUserSetTools.set_user_exercise_target(String.valueOf(DefaultVale.USER_SPORT_TARGET));
                }

                mUserSetTools.set_user_exercise_target(save_target);
                BroadcastTools.sendBleTargetStepData(mContext);

                CalibrationData mCalibrationData = new CalibrationData();
                mCalibrationData.setSportTarget(mUserSetTools.get_user_exercise_target());


                waitDialog.show(getString(R.string.loading0));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        uploadCalibrationInfo(mCalibrationData);
                    }
                }, Constants.serviceHandTime);


                break;
        }
    }


    /**
     * 上传校准信息到服务器
     */
    private void uploadCalibrationInfo(CalibrationData mCalibrationData) {


        RequestInfo mRequestInfo = RequestJson.modifyCalibrationInfo(mCalibrationData);

        MyLog.i(TAG, "请求接口-修改校准信息 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-修改校准信息  result = " + result.toString());

                        UserBean mUserBean = ResultJson.UserBean(result);

                        //请求成功
                        if (mUserBean.isRequestSuccess()) {
                            if (mUserBean.uploadUserSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-修改校准信息 成功");
                                HandleFinish();
                            } else if (mUserBean.uploadUserSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-修改校准信息 失败");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else {
                                MyLog.i(TAG, "请求接口-修改校准信息 请求异常(1)");
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

                        MyLog.i(TAG, "请求接口-修改校准信息 请求失败 = message = " + arg0.getMessage());
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                        return;
                    }
                });


    }

    void HandleFinish() {
        //录入信息
        if (intent_tag.equals("1")) {
            Intent mIntent = new Intent(mContext, TargetSleepActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(TargetSleepActivity.IntentSleepTarget, "1");
            mIntent.putExtras(bundle);
            startActivity(mIntent);
        }
        //编辑信息
        else {
            AppUtils.showToast(mContext, R.string.change_ok);
            finish();
        }
    }


}
