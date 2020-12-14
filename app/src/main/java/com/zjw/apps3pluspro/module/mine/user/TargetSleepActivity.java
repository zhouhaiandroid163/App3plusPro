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
import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.CalibrationData;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.UserBean;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 睡眠目标
 */
public class TargetSleepActivity extends Activity implements View.OnClickListener {
    private final String TAG = TargetSleepActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();

    public static final String IntentSleepTarget = "intent_sleep_target";
    //标志位，1=从步数目标跳转过来的，2=从我的界面跳转过来的
    String intent_tag = "";

    private MyActivityManager manager;

    private String str_target_sleep_time;

    private TextView tv_target_sleep_time;
    private SeekBar sb_target_sleep_time;

    private String totalMin;
    private WaitDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setStatusBarMode(this, true, R.color.base_activity_bg);
        setContentView(R.layout.activity_sleep_target);
        mContext = TargetSleepActivity.this;
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        waitDialog = new WaitDialog(mContext);
        initView();
        initData();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
    }

    private void initView() {

        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.sleep_target));
        findViewById(R.id.public_head_back).setOnClickListener(this);

        tv_target_sleep_time = (TextView) findViewById(R.id.tv_target_sleep_time);
        sb_target_sleep_time = (SeekBar) findViewById(R.id.sb_target_sleep_time);
        findViewById(R.id.bton_sleep_target_ok).setOnClickListener(this);


    }

    List<String> data = new ArrayList<String>();

    private void initData() {

        //初始化一个数组,添加数据.
        for (int i = 0; i < Constants.TargetSleepCount; i++) {
            data.add("" + (i * 30 + Constants.TargetSleepMin));
        }

        //设置最大值
        sb_target_sleep_time.setMax(data.size() - 1);


        if (!JavaUtil.checkIsNull(mUserSetTools.get_user_sleep_target())) {


            str_target_sleep_time = mUserSetTools.get_user_sleep_target();


            if (Integer.parseInt(str_target_sleep_time) < Constants.TargetSleepMin) {

                str_target_sleep_time = String.valueOf(Constants.TargetSleepMin);

            } else if (Integer.parseInt(str_target_sleep_time) > Constants.TargetSleepMax) {

                str_target_sleep_time = String.valueOf(Constants.TargetSleepMax);

            } else {

//                str_target_sleep_time = (Integer.parseInt(str_target_sleep_time) / 60) * 60 + "";
                str_target_sleep_time = (Integer.parseInt(str_target_sleep_time) / 30) * 30 + "";

            }

            tv_target_sleep_time.setText(MyTime.getHours(str_target_sleep_time));

            int index = ((int) Float.parseFloat(str_target_sleep_time) - Constants.TargetSleepMin) / 30;
            sb_target_sleep_time.setProgress(index);

            //如果睡眠目标为空为空
        } else {

            str_target_sleep_time = String.valueOf(Constants.TargetSleepDefult);
        }

        sb_target_sleep_time.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                MyLog.i(TAG, "onProgressChanged() = progress = " + progress);


                tv_target_sleep_time.setText(MyTime.getHours(String.valueOf(progress * 30 + Constants.TargetSleepMin)));

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
        if (bundle != null && bundle.getString(IntentSleepTarget) != null && !bundle.getString(IntentSleepTarget).equals("")) {
            intent_tag = bundle.getString(IntentSleepTarget);
            MyLog.i(TAG, "intent_tag = " + intent_tag);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.public_head_back:
                finish();
                break;


            case R.id.bton_sleep_target_ok:

                try {
                    totalMin = String.valueOf((int) (Float.parseFloat(tv_target_sleep_time.getText().toString()) * 60));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    totalMin = String.valueOf(DefaultVale.USER_SLEEP_TARGET);
                }
                mUserSetTools.set_user_sleep_target(totalMin);

                CalibrationData mCalibrationData = new CalibrationData();
                mCalibrationData.setSleepTarget(mUserSetTools.get_user_sleep_target());

                waitDialog.show(getString(R.string.loading0));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //上传用户信息
                        uploadCalibrationInfo(mCalibrationData);
                    }
                }, Constants.serviceHandTime);


                break;

        }
    }


    /**
     * 上传用户到服务器
     */
    private void uploadCalibrationInfo(CalibrationData mCalibrationData) {


        RequestInfo mRequestInfo = RequestJson.modifyCalibrationInfo(mCalibrationData);

        MyLog.i(TAG, "请求接口-修改个人信息 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-修改个人信息  result = " + result.toString());

                        UserBean mUserBean = ResultJson.UserBean(result);

                        //请求成功
                        if (mUserBean.isRequestSuccess()) {

                            if (mUserBean.uploadUserSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-修改个人信息 成功");

                                HandleFinish();

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

                        return;
                    }
                });


    }


    void HandleFinish() {

        //录入信息
        if (intent_tag.equals("1")) {
            mUserSetTools.set_user_login(true);//登录状态
            startActivity(new Intent(mContext, HomeActivity.class));
            manager.finishAllActivity();
        }
        //编辑信息
        else {
            AppUtils.showToast(mContext, R.string.change_ok);
            finish();
        }
    }


}


