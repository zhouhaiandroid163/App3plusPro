package com.zjw.apps3pluspro.module.device;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
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
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;


/**
 * 佩戴方式
 */

public class WearTypeActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = WearTypeActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    private RelativeLayout rl_wear_type_left, rl_wear_type_right;

    private int WearType = 1;

    private WaitDialog waitDialog;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_wear_type;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = WearTypeActivity.this;
        waitDialog = new WaitDialog(mContext);
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
        setTvTitle(R.string.wear_way);

        rl_wear_type_left = (RelativeLayout) findViewById(R.id.rl_wear_type_left);
        rl_wear_type_right = (RelativeLayout) findViewById(R.id.rl_wear_type_right);

        rl_wear_type_left.setOnClickListener(this);
        rl_wear_type_right.setOnClickListener(this);

        findViewById(R.id.wear_type_save).setOnClickListener(this);

    }


    void initData() {

        WearType = mUserSetTools.get_user_wear_way();
    }

    void updateUI() {

        //左手
        if (WearType == 1) {
            MyLog.i(TAG, "updateUI 左手");
            rl_wear_type_left.setAlpha(1.0f);
            rl_wear_type_right.setAlpha(0.5f);
        }
        //右手
        else {
            MyLog.i(TAG, "updateUI 左手");
            rl_wear_type_left.setAlpha(0.5f);
            rl_wear_type_right.setAlpha(1.0f);
        }
    }


    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rl_wear_type_left:
                MyLog.i(TAG, "左手");
                WearType = 1;
                updateUI();
                break;

            case R.id.rl_wear_type_right:
                MyLog.i(TAG, "右手");
                WearType = 0;
                updateUI();
                break;

            case R.id.wear_type_save:
                mUserSetTools.set_user_wear_way(WearType);
                CalibrationData mCalibrationData = new CalibrationData();
                mCalibrationData.setWearWay(String.valueOf(mUserSetTools.get_user_wear_way()));
                uploadCalibrationInfo(mCalibrationData);
                break;

        }
    }


    /**
     * 上传用户到服务器
     */
    private void uploadCalibrationInfo(CalibrationData mCalibrationData) {

        waitDialog.show(getString(R.string.loading0));


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
                                AppUtils.showToast(mContext, R.string.save_ok);
                                finish();

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

}
