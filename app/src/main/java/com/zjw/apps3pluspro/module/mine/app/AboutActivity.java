package com.zjw.apps3pluspro.module.mine.app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.device.WeixinActivity;
import com.zjw.apps3pluspro.module.mine.user.PrivacyProtocolActivity;
import com.zjw.apps3pluspro.module.mine.user.UserProtocolActivity;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.AppVersionBean;
import com.zjw.apps3pluspro.bleservice.UpdateInfoService;
import com.zjw.apps3pluspro.network.okhttp.MyOkHttpClient;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.AuthorityManagement;
import com.zjw.apps3pluspro.utils.DialogUtils;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;


/**
 * 关于页面
 */
public class AboutActivity extends BaseActivity implements OnClickListener {
    private final String TAG = AboutActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    //APP相关
    private TextView tv_app_upload_version_state, tv_app_upload_version_name, tv_app_upload_base_version_name;

    //微信相关
    private View view_wechat;
    private RelativeLayout rl_wechat;

    //=====APP升级相关=====
    private String AppVersion, AppDownloadAddress;
    public final int UPDATE = 2;
    private Dialog updateAPPDialog;
    private Dialog progressDialog;
    private UpdateInfoService updateInfoService;
    private boolean is_fast = true;
    private MyActivityManager manager;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = AboutActivity.this;

        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);

        updateInfoService = new UpdateInfoService(mContext);
        initView();
        intData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTagUi();
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
        // TODO Auto-generated method stub
        findViewById(R.id.public_head_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.mine_more));

        tv_app_upload_version_state = (TextView) findViewById(R.id.tv_app_upload_version_state);
        tv_app_upload_version_name = (TextView) findViewById(R.id.tv_app_upload_version_name);
        tv_app_upload_base_version_name = (TextView) findViewById(R.id.tv_app_upload_base_version_name);

        view_wechat = (View) findViewById(R.id.view_wechat);
        rl_wechat = (RelativeLayout) findViewById(R.id.rl_wechat);
        rl_wechat.setOnClickListener(this);

        findViewById(R.id.rl_about_feedback).setOnClickListener(this);
        findViewById(R.id.rl_about_privacy).setOnClickListener(this);
        findViewById(R.id.rl_user_protocol).setOnClickListener(this);
        findViewById(R.id.rl_about_upload).setOnClickListener(this);


        tv_app_upload_version_name.setText(MyUtils.getAppInfo());
    }

    void intData() {
//        if (Constants.isBast && Constants.BastVersion != null) {
//            tv_app_upload_base_version_name.setText(Constants.BastVersion);
//            tv_app_upload_base_version_name.setVisibility(View.VISIBLE);
//        } else {
//            tv_app_upload_base_version_name.setVisibility(View.GONE);
//        }
        requestAppVersion(true);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            //返回
            case R.id.public_head_back:
                finish();
                break;

            //意见反馈
            case R.id.rl_about_feedback:
                startActivity(new Intent(mContext, FeedBackActivity.class));
                break;

            //微信运动
            case R.id.rl_wechat:
                startActivity(new Intent(mContext, WeixinActivity.class));
                break;

            //隐私政策
            case R.id.rl_about_privacy:
                startActivity(new Intent(mContext, PrivacyProtocolActivity.class));
                break;
            case R.id.rl_user_protocol:
                startActivity(new Intent(mContext, UserProtocolActivity.class));
                break;
            //APP更新
            case R.id.rl_about_upload:
                requestAppVersion(false);
                break;
        }
    }
    //=================升级相关=====================


    /**
     * 获取服务器版本号
     */
    private void requestAppVersion(boolean is_fast) {

        this.is_fast = is_fast;

        RequestInfo mRequestInfo = RequestJson.getAppUpdateInfo(mContext);

        MyLog.i(TAG, "请求接口-获取APP版本号 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        MyLog.i(TAG, "请求接口-获取APP版本号 result = " + result);

                        AppVersionBean mAppVerionBean = ResultJson.AppVerionBean(result);

                        //请求成功
                        if (mAppVerionBean.isRequestSuccess()) {
                            if (mAppVerionBean.isgetAPPVersionSuccess() == 1) {

                                MyLog.i(TAG, "请求接口-获取APP版本号 成功");
                                if (mAppVerionBean.isAppUpdate(mContext)) {
                                    MyLog.i(TAG, "请求接口-获取APP版本号 需要升级");
                                    GetAppVersionResultDataParsing(mAppVerionBean.getData());
                                    updateAppVersionUi(1);
                                } else {
                                    MyLog.i(TAG, "请求接口-获取APP版本号 不需要升级");
                                    updateAppVersionUi(0);
                                }

                            } else if (mAppVerionBean.isgetAPPVersionSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取APP版本号 失败");
                                updateAppVersionUi(0);
                            } else {
                                MyLog.i(TAG, "请求接口-获取APP版本号 请求异常(1)");
                                updateAppVersionUi(0);
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取APP版本号 请求异常(0)");

                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub

                        MyLog.i(TAG, "请求接口-获取APP版本号 请求失败 = message = " + arg0.getMessage());


                        return;
                    }
                });


    }


    /**
     * 解析数据
     */
    private void GetAppVersionResultDataParsing(AppVersionBean.DataBean mDataBean) {

        AppVersion = mDataBean.getAppVersion();
        AppDownloadAddress = mDataBean.getAppDownloadUrl();

        MyLog.i(TAG, "获取APP版本号 getAppVersion = " + AppVersion);
        MyLog.i(TAG, "获取APP版本号 getAppDownloadUrl = " + AppDownloadAddress);

        ForceUpdate();
    }


    /**
     * 升级对话框
     */
    private void showUpdateDialog() {

        LinearLayout update_dialog_bg;
        TextView tv_update_title, tv_update_msg;
        Button btn_update_cancel, btn_update_ok;
        Display display;


        View view = LayoutInflater.from(mContext).inflate(
                R.layout.update_dialog, null);
        WindowManager windowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        update_dialog_bg = (LinearLayout) view
                .findViewById(R.id.update_dialog_bg);
        tv_update_title = (TextView) view.findViewById(R.id.tv_update_title);
        tv_update_msg = (TextView) view.findViewById(R.id.tv_update_msg);
        btn_update_cancel = (Button) view.findViewById(R.id.btn_update_cancel);
        btn_update_ok = (Button) view.findViewById(R.id.btn_update_ok);
        updateAPPDialog = new Dialog(mContext, R.style.AlertDialogStyle);
        updateAPPDialog.setContentView(view);
        updateAPPDialog.setCanceledOnTouchOutside(false);
        update_dialog_bg.setLayoutParams(new FrameLayout.LayoutParams(
                (int) (display.getWidth() * 0.85), LinearLayout.LayoutParams.WRAP_CONTENT));


        tv_update_title.setText(getString(R.string.version_title) + AppVersion);

        btn_update_cancel.setText(R.string.dialog_no);
        btn_update_ok.setText(R.string.dialog_yes);
        btn_update_cancel.setOnClickListener(new UpdateDialogListener());
        btn_update_ok.setOnClickListener(new UpdateDialogListener());
        updateAPPDialog.show();
        updateAPPDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    dialog.dismiss();
                }
                return false;
            }
        });
    }

    class UpdateDialogListener implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            switch (arg0.getId()) {
                case R.id.btn_update_ok:
                    updateAPPDialog.dismiss();

                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        downFile(AppDownloadAddress);
                    } else {
                        AppUtils.showToast(mContext, R.string.sd_card);
                    }

//                    if (Build.VERSION.SDK_INT < 29) {
//                    } else {
//                        Uri uri = Uri.parse(AppDownloadAddress);
//                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                        startActivity(intent);
//                    }
                    break;
                case R.id.btn_update_cancel:
                    updateAPPDialog.dismiss();
                    break;

                default:
                    finish();
                    break;
            }
        }

    }

    /**
     * 下载文件
     *
     * @param url
     */
    void downFile(final String url) {
        progressDialog = DialogUtils.BaseDialogShowProgress(context,
                context.getResources().getString(R.string.download_title),
                context.getResources().getString(R.string.loading0),
                context.getDrawable(R.drawable.black_corner_bg)
        );
        updateInfoService.downLoadFile(url, progressDialog, handler1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateInfoService.handleActivityResult(requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        updateInfoService.handlePermissionsResult(requestCode, grantResults);
    }

    /**
     * 初始化升级
     */
    private void ForceUpdate() {
        // TODO Auto-generated method stub
        new Thread() {
            public void run() {
                try {
                    handler1.sendEmptyMessage(UPDATE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    private Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE:
                    // 如果有更新就提示

                    if (!is_fast) {
                        if (AuthorityManagement.verifyStoragePermissions(AboutActivity.this)) {
                            showUpdateDialog();
                            MyLog.i(TAG, "SD卡权限 已获取");
                        } else {
                            MyLog.i(TAG, "SD卡权限 未获取");
                        }
                    }

                    break;
            }

        }

    };


    //===============更新UI=============

    /**
     * 标志位相关
     */
    void updateTagUi() {

        view_wechat.setVisibility(View.GONE);
        rl_wechat.setVisibility(View.GONE);


//        if (mBleDeviceTools.get_ble_mac() != null && !mBleDeviceTools.get_ble_mac().equals("")
//                && mBleDeviceTools.get_ble_device_version() != -1) {
//
//            int device_number = mBleDeviceTools.get_ble_device_version();
//
//            if (AppUtils.isZh(mContext)) {
//                if (device_number >= 23 && mBleDeviceTools.get_is_support_wx_sport()) {
//                    view_wechat.setVisibility(View.VISIBLE);
//                    rl_wechat.setVisibility(View.VISIBLE);
//                }
//            }
//
//
//        }
    }


    /**
     * APP版本相关
     *
     * @param type 1=有新版本 0=没有新版本
     */
    void updateAppVersionUi(int type) {
        if (type == 1) {
            if (AppVersion != null && !AppVersion.equals("")) {
                tv_app_upload_version_name.setText(AppVersion);
            }
            tv_app_upload_version_state.setText(getString(R.string.device_new_version));
            tv_app_upload_version_state.setTextColor(Color.GREEN);

        } else {
            tv_app_upload_version_name.setText(MyUtils.getAppInfo());
            tv_app_upload_version_state.setText(getString(R.string.already_new));
            tv_app_upload_version_state.setTextColor(Color.WHITE);
            if (!is_fast) {
                AppUtils.showToast(mContext, R.string.already_new);
            }
        }

    }


}


