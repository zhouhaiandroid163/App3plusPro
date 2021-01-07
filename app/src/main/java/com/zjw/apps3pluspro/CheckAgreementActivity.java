package com.zjw.apps3pluspro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.mine.user.LoginActivity;
import com.zjw.apps3pluspro.module.mine.user.PrivacyProtocolActivity;
import com.zjw.apps3pluspro.module.mine.user.UserProtocolActivity;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.log.MyLog;


public class CheckAgreementActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "CheckAgreementActivity";
    private MyActivityManager manager;

    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    // 更新版本要用到的一些信息

    @Override
    protected int setLayoutId() {
        return R.layout.activity_check_agreement;
    }

    @Override
    protected void initViews() {
        super.initViews();
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        initView();
    }

    private void initView() {
        // TODO Auto-generated method stub
//        findViewById(R.id.public_head_back).setOnClickListener(this);
//        findViewById(R.id.rl_about_privacy).setOnClickListener(this);
        findViewById(R.id.tv_user).setOnClickListener(this);
        findViewById(R.id.tv_privacy).setOnClickListener(this);
        findViewById(R.id.btn_center).setOnClickListener(this);
        findViewById(R.id.btn_agree).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        Intent intent = null;
        switch (v.getId()) {

            case R.id.tv_user:
                intent = new Intent(CheckAgreementActivity.this, UserProtocolActivity.class);
                startActivity(intent);
                break;

            case R.id.tv_privacy:
                intent = new Intent(CheckAgreementActivity.this, PrivacyProtocolActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_center:
                manager.popOneActivity(this);
                break;

            case R.id.btn_agree:
                mUserSetTools.set_is_privacy_protocol(true);
                MyLog.i(TAG,"英文自动登录 检查状态= checkLogin = " + mUserSetTools.get_user_login());
                if (mUserSetTools.get_user_login()) {
                    startActivity(new Intent(CheckAgreementActivity.this, HomeActivity.class));
                    mUserSetTools.set_user_login(true);
                    manager.popOneActivity(CheckAgreementActivity.this);
                } else {
                    startActivity(new Intent(CheckAgreementActivity.this, LoginActivity.class));
                    manager.popOneActivity(CheckAgreementActivity.this);
                }
                break;

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
    }


}


