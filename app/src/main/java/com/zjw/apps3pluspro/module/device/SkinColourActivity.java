package com.zjw.apps3pluspro.module.device;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.mine.user.TargetStepActivity;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.entity.UserData;
import com.zjw.apps3pluspro.network.javabean.UserBean;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;


/**
 * 肤色选择
 */
public class SkinColourActivity extends BaseActivity implements View.OnClickListener {

    //标志位，1=个人信息录入跳转过来的，2=从个人信息设置跳转过来的
    String intent_tag = "";

    private final String TAG = SkinColourActivity.class.getSimpleName();
    //轻量级存储
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    private Context mContext;
    private MyActivityManager manager;

    private RadioButton ro_skin_colour1, ro_skin_colour2, ro_skin_colour3, ro_skin_colour4, ro_skin_colour5, ro_skin_colour6;

    int SkinColourType = 0;

    private WaitDialog waitDialog;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_skin_colour;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = SkinColourActivity.this;
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

        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.skin_colour_title));
        findViewById(R.id.public_head_back).setOnClickListener(this);

        ro_skin_colour1 = (RadioButton) findViewById(R.id.ro_skin_colour1);
        ro_skin_colour2 = (RadioButton) findViewById(R.id.ro_skin_colour2);
        ro_skin_colour3 = (RadioButton) findViewById(R.id.ro_skin_colour3);
        ro_skin_colour4 = (RadioButton) findViewById(R.id.ro_skin_colour4);
        ro_skin_colour5 = (RadioButton) findViewById(R.id.ro_skin_colour5);
        ro_skin_colour6 = (RadioButton) findViewById(R.id.ro_skin_colour6);

        findViewById(R.id.rl_skin_colour1).setOnClickListener(this);
        findViewById(R.id.rl_skin_colour2).setOnClickListener(this);
        findViewById(R.id.rl_skin_colour3).setOnClickListener(this);
        findViewById(R.id.rl_skin_colour4).setOnClickListener(this);
        findViewById(R.id.rl_skin_colour5).setOnClickListener(this);
        findViewById(R.id.rl_skin_colour6).setOnClickListener(this);

        findViewById(R.id.btn_skin_colour).setOnClickListener(this);

        BroadcastTools.sendBleSkinData(mContext);

    }


    private void initData() {


        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        if (bundle != null && bundle.getString(IntentConstants.IntentSkinColur) != null && !bundle.getString(IntentConstants.IntentSkinColur).equals("")) {
            intent_tag = bundle.getString(IntentConstants.IntentSkinColur);
            MyLog.i(TAG, "intent_tag = " + intent_tag);
        }

        //录入信息
        if (intent_tag.equals(IntentConstants.IntentSkinColurTypeIntput)) {
            SkinColourType = 0;
//            SkinColourType = 1;
        }
        //编辑信息
        else {
            if (mBleDeviceTools.get_skin_colour() < 0) {
                mBleDeviceTools.set_skin_colour(0);
            } else if (mBleDeviceTools.get_skin_colour() > IntentConstants.SkinColor.length) {
                mBleDeviceTools.set_skin_colour(IntentConstants.SkinColor.length - 1);
            }
            SkinColourType = mBleDeviceTools.get_skin_colour();
        }

        updateUi();


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.public_head_back:
                finish();
                break;

            case R.id.rl_skin_colour1:
                SkinColourType = 0;
                updateUi();
                break;

            case R.id.rl_skin_colour2:
                SkinColourType = 1;
                updateUi();
                break;
            case R.id.rl_skin_colour3:
                SkinColourType = 2;
                updateUi();
                break;

            case R.id.rl_skin_colour4:
                SkinColourType = 3;
                updateUi();
                break;

            case R.id.rl_skin_colour5:
                SkinColourType = 4;
                updateUi();
                break;

            case R.id.rl_skin_colour6:
                SkinColourType = 5;
                updateUi();
                break;

            case R.id.btn_skin_colour:



                mBleDeviceTools.set_skin_colour(SkinColourType);

                BroadcastTools.sendBleSkinData(mContext);

                UserData mUserData = new UserData();
                mUserData.setSkinColor(String.valueOf(mBleDeviceTools.get_skin_colour()));

                waitDialog.show(getString(R.string.loading0));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //上传用户信息
                        uploadUserInfo(mUserData);
                    }
                }, Constants.serviceHandTime);

                break;

        }
    }

//    /**
//     * 发送肤色指令给蓝牙
//     */
//    void sendBleData() {
//        final Intent intent = new Intent();
//        intent.setAction(BleConstant.ACTION_NOTIFICATION_SEND_SET_SKIN_COLOUR);
//        sendBroadcast(intent);
//    }

    void updateUi() {

        ro_skin_colour1.setChecked(false);
        ro_skin_colour2.setChecked(false);
        ro_skin_colour3.setChecked(false);
        ro_skin_colour4.setChecked(false);
        ro_skin_colour5.setChecked(false);
        ro_skin_colour6.setChecked(false);

        switch (SkinColourType) {
            case 0:
                ro_skin_colour1.setChecked(true);
                break;
            case 1:
                ro_skin_colour2.setChecked(true);
                break;
            case 2:
                ro_skin_colour3.setChecked(true);
                break;
            case 3:
                ro_skin_colour4.setChecked(true);
                break;
            case 4:
                ro_skin_colour5.setChecked(true);
                break;
            case 5:
                ro_skin_colour6.setChecked(true);
                break;
        }


    }

    /**
     * 上传用户到服务器
     */
    private void uploadUserInfo(UserData mUserData) {


        RequestInfo mRequestInfo = RequestJson.modifyUserInfo(mContext,mUserData,false);

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
        if (intent_tag.equals(IntentConstants.IntentSkinColurTypeIntput)) {
            Intent mIntent = new Intent(mContext, TargetStepActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(TargetStepActivity.IntentTargetStep, "1");
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


