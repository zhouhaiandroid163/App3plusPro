package com.zjw.apps3pluspro;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.module.mine.user.LoginActivity;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.bleservice.MyNotificationsListenerService;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;


/**
 * 欢迎页
 */

public class SplashActivity extends Activity {
    private final String TAG = SplashActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();

    private MyActivityManager manager;
    private TextView app_version;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 1:
                    //跳转到登录页
//                    startActivity(new Intent(mContext, EasyLoginActivity.class));
                    startActivity(new Intent(mContext, LoginActivity.class));
                    manager.popOneActivity(SplashActivity.this);
                    break;
                case 3:
                    //跳转到主页
                    startActivity(new Intent(mContext, HomeActivity.class));
                    mUserSetTools.set_user_login(true);
                    manager.popOneActivity(SplashActivity.this);
                    break;

                case 5:
                    //跳转到隐私检查页面
                    startActivity(new Intent(mContext, CheckAgreementActivity.class));
                    manager.popOneActivity(SplashActivity.this);
                    break;


            }
        }

    };


    /**
     * 通知需要，服务唤醒
     */
    private void toggleNotificationListeenerService() {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(this, MyNotificationsListenerService.class), PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(this, MyNotificationsListenerService.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        toggleNotificationListeenerService();
        mContext = SplashActivity.this;
        app_version = (TextView) findViewById(R.id.app_version);
        app_version.setText(MyUtils.getAppInfo());
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        if (!RequestJson.checkServiceReleaseUrl()) {
            requestUrlDialog();
        } else {
            checkLogin();
        }

        if (Build.VERSION.SDK_INT >= 26 && !SysUtils.isNotificationEnabled26(this)) {
            SysUtils.toSysNotificationSetting(this);
        }

    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    /**
     * 检查登录状态
     */
    private void checkLogin() {
        if (!mUserSetTools.get_is_privacy_protocol()) {
            mHandler.sendEmptyMessageDelayed(5, 2000);
        } else {
            MyLog.i(TAG, "自动登录 检查状态= checkLogin = " + mUserSetTools.get_user_login());
            if (mUserSetTools.get_user_login()) {
                mHandler.sendEmptyMessageDelayed(3, 2000);
            } else {
                mHandler.sendEmptyMessageDelayed(1, 2000);
            }
        }
    }


    /**
     * 弹出对话框
     */
    void requestUrlDialog() {
        new android.app.AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.dialog_prompt))
                .setMessage(getString(R.string.check_reques_url_msg))
                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        checkLogin();
                    }
                }).show();//在按键响应事件中显示此对话框

    }


}
