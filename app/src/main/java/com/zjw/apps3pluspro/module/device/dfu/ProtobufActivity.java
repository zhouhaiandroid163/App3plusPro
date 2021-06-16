package com.zjw.apps3pluspro.module.device.dfu;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.bleservice.BleConstant;
import com.zjw.apps3pluspro.bleservice.BleService;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.bleservice.UpdateInfoService;
import com.zjw.apps3pluspro.eventbus.BlueToothStateEvent;
import com.zjw.apps3pluspro.eventbus.GetDeviceProtoOtaPrepareStatusEvent;
import com.zjw.apps3pluspro.eventbus.GetDeviceProtoOtaPrepareStatusSuccessEvent;
import com.zjw.apps3pluspro.eventbus.UploadThemeStateEvent;
import com.zjw.apps3pluspro.eventbus.tools.EventTools;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.DeviceBean;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.BleCmdManager;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.DialogUtils;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.ThemeManager;
import com.zjw.apps3pluspro.utils.ThemeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

public class ProtobufActivity extends BaseActivity {

    private static final String TAG = ProtobufActivity.class.getSimpleName();

    private String curCmd;

    @Override
    protected int setLayoutId() {
        return R.layout.protobuf_activity;
    }

    private Handler handler;

    @Override
    protected void initViews() {
        super.initViews();
        waitDialog = new WaitDialog(this);
        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.firmware_update));

        EventTools.SafeRegisterEventBus(this);
        handler = new Handler();

        initBroadcast();

        SysUtils.makeRootDirectory(Constants.UPDATE_DEVICE_FILE);

//        findViewById(R.id.public_head_back).setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        tvMac.setText("mac:" + mBleDeviceShare.getDeviceMac());
    }

    @Override
    protected void onDestroy() {
        waitDialog.dismiss();
        EventTools.SafeUnregisterEventBus(this);
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }

    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    @BindView(R.id.update)
    Button update;

    @OnClick({R.id.update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.update:
                getNetDeviceVersion(mBleDeviceTools.get_ble_device_type(), mBleDeviceTools.get_ble_device_version(), mBleDeviceTools.get_device_platform_type());
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UploadThemeStateEvent(UploadThemeStateEvent event) {
        switch (event.state) {
            case 1:
                Toast.makeText(ProtobufActivity.this, getResources().getString(R.string.send_fail), Toast.LENGTH_SHORT).show();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                finish();
                break;
            case 2:
                progressBar.setProgress(event.progress);
                tvDeviceUpdateProgress.setText("" + event.progress + "%");
                break;
            case 3:
                mBleDeviceTools.setWeatherSyncTime(0);
                Toast.makeText(ProtobufActivity.this, getResources().getString(R.string.dfu_success), Toast.LENGTH_SHORT).show();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                handler.postDelayed(() -> finish(), 0 * 1000);
                break;
        }
    }

    public static int capacity = 50;
    public static String firmwareVersion = "";

    private Dialog progressDialog;
    TextView msg, tvDeviceUpdateProgress;
    ImageView ivSyncWhite;
    private ProgressBar progressBar;

    private void showDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(ProtobufActivity.this, R.style.progress_dialog);
            progressDialog.setContentView(R.layout.update_layout);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        msg = (TextView) progressDialog.findViewById(R.id.tvLoading);
        ivSyncWhite = progressDialog.findViewById(R.id.ivSyncWhite);
        progressBar = progressDialog.findViewById(R.id.progressBar);
        tvDeviceUpdateProgress = progressDialog.findViewById(R.id.tvDeviceUpdateProgress);
        progressBar.setVisibility(View.VISIBLE);

        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(2000);// 设置动画持续周期
        rotate.setRepeatCount(-1);// 设置重复次数
        rotate.setFillAfter(true);// 动画执行完后是否停留在执行完的状态
        rotate.setStartOffset(10);// 执行前的等待时间
        ivSyncWhite.setAnimation(rotate);
        ivSyncWhite.setVisibility(View.GONE);

//        msg.setText("上传中...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.setOnDismissListener(dialog -> {
        });
    }

    private JSONObject versionObject;
    private String downloadAddress, downloadContentEN,
            downloadContentZH, isNeedUpdate;

    private void getNetDeviceVersion(int model, int c_upgrade_version, int device_platform_type) {
        update.setEnabled(false);
        update.setVisibility(View.GONE);

        RequestInfo mRequestInfo = RequestJson.getDeviceUpdateInfo(String.valueOf(model), String.valueOf(c_upgrade_version), device_platform_type);
        MyLog.i(TAG, "请求接口-获取设备版本号 mRequestInfo = " + mRequestInfo.toString());
        SysUtils.logAppRunning(TAG, "请求接口 download device version" + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(this, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject result) {
                        MyLog.i(TAG, "请求接口-获取设备版本号 result = " + result);
                        SysUtils.logAppRunning(TAG, "protobuf download device version" + result);
                        DeviceBean mDeviceBean = ResultJson.DeviceBean(result);
                        //请求成功
                        if (mDeviceBean.isRequestSuccess()) {
                            if (mDeviceBean.isOk() == 1) {
                                MyLog.i(TAG, "请求接口-获取设备版本号 成功");
                                if (mDeviceBean.isUpdate(mBleDeviceTools)) {
                                    downloadAddress = mDeviceBean.getData().getVersionUrl();
                                    downloadContentEN = "";
                                    downloadContentZH = "";
                                    isNeedUpdate = "";

                                    isForce = true;
                                    version = mDeviceBean.getData().getVersionAfter();

                                    ForceUpdate();
                                } else {
                                    AppUtils.showToast(mContext, R.string.already_new);
                                    finish();
                                    MyLog.i(TAG, "固件升级 08 手环管理 不需要升级 ");
                                }
                            } else if (mDeviceBean.isOk() == 2) {
                                AppUtils.showToast(mContext, R.string.already_new);
                                finish();
                            } else {
                                AppUtils.showToast(mContext, R.string.already_new);
                                finish();
                            }
                            //请求失败
                        } else {
                            AppUtils.showToast(mContext, R.string.already_new);
                            finish();
                            MyLog.i(TAG, "请求接口-获取设备版本号 请求异常(0)");
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        MyLog.i(TAG, "请求接口-获取设备版本号 请求失败 = message = " + arg0.getMessage());
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);
                    }
                });
    }

    UpdateInfoService updateInfoService;
    public final int UPDATE = 2;

    private void ForceUpdate() {
        // TODO Auto-generated method stub
        new Thread() {
            public void run() {
                try {
                    updateInfoService = new UpdateInfoService(context);
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
                    downDeviceFile();
                    break;
            }
        }
    };

    void downDeviceFile() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            downFile(downloadAddress);
        } else {
            AppUtils.showToast(context, R.string.sd_card);
        }
    }

    //下载的文件名
    private final String DFU_FILE_NAME = "ota.bin";
    private Dialog progressDialogDownFile;

    void downFile(final String url) {
        progressDialogDownFile = DialogUtils.BaseDialogShowProgress(context,
                context.getResources().getString(R.string.download_title),
                context.getResources().getString(R.string.loading0),
                context.getDrawable(R.drawable.black_corner_bg)
        );
        updateInfoService.downLoadFile2(url, progressDialogDownFile, handler1, DFU_FILE_NAME);
    }

    private void initBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastTools.ACTION_UPDATE_DEVICE_FILE_STATE_SUCCESS);
        filter.addAction(BroadcastTools.ACTION_UPDATE_DEVICE_FILE_STATE_ERROR);
        filter.setPriority(1000);
        registerReceiver(broadcastReceiver, filter);
    }

    private Handler protoHandler;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @SuppressWarnings({"unused", "unused"})
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BroadcastTools.ACTION_UPDATE_DEVICE_FILE_STATE_SUCCESS:
                    MyLog.i(TAG, "DFU 收到下载固件成功！");
                    SysUtils.logAppRunning(TAG, "DFU 收到下载固件成功！");
                    // send cmd get device status
                    if (mBleDeviceTools.getIsSupportGetDeviceProtoStatus()) {
                        waitDialog.show(getResources().getString(R.string.ignored));
                        protoHandler = new Handler();
                        protoHandler.postDelayed(getDeviceStatusTimeOut, 10 * 1000);

                        File file = new File(Constants.UPDATE_DEVICE_FILE + "ota.bin");
                        md5 = ThemeUtils.getFileMD5(file);
                        EventBus.getDefault().post(new GetDeviceProtoOtaPrepareStatusEvent(isForce, version, md5));
                    } else {
                        startDfu();
                    }
                    break;
                case BroadcastTools.ACTION_UPDATE_DEVICE_FILE_STATE_ERROR:
                    AppUtils.showToast(context, R.string.net_worse_try_again);
                    finish();
                    break;
            }
        }
    };
    boolean isForce;
    String version;
    String md5;
    private WaitDialog waitDialog;
    Runnable getDeviceStatusTimeOut = () -> {
        Log.w(TAG, " getDeviceStatusTimeOut Time Out");
        waitDialog.show(getResources().getString(R.string.device_prepare2));
        protoHandler.removeCallbacksAndMessages(null);
        protoHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                waitDialog.close();
                finish();
            }
        }, Constants.FINISH_ACTIVITY_DELAY_TIME);
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getDeviceProtoOtaPrepareStatusSuccessEvent(GetDeviceProtoOtaPrepareStatusSuccessEvent event) {
        SysUtils.logAppRunning(TAG, "GetDeviceProtoOtaPrepareStatusSuccessEvent = " + event.status);
        protoHandler.removeCallbacksAndMessages(null);
        switch (event.status) {
            case 0:
                waitDialog.close();
                startDfu();
                break;
            case 1:
                finishActivity(getResources().getString(R.string.device_prepare1));
                break;
            case 2:
            case 5:
            case 3:
                finishActivity(getResources().getString(R.string.device_prepare2));
                break;
            case 4:
                finishActivity(getResources().getString(R.string.device_prepare4));
                break;
        }
    }

    private void finishActivity(String text) {
        waitDialog.show(text);
        Handler handle = new Handler();
        handle.postDelayed(() -> {
            waitDialog.close();
            finish();
        }, Constants.FINISH_ACTIVITY_DELAY_TIME);
    }

    private void startDfu() {
        showDialog();
        progressDialog.setCancelable(false);
        BleService.bluetoothLeService.sendTheme("ota", null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void blueToothStateEvent(BlueToothStateEvent event) {
        switch (event.state) {
            case BleConstant.STATE_CONNECTING:
                break;
            case BleConstant.STATE_DISCONNECTED:
                update.setEnabled(false);
                finish();
                break;
            case BleConstant.STATE_CONNECTED_TIMEOUT:
                update.setEnabled(false);
                break;
            case BleConstant.STATE_CONNECTED:
                update.setEnabled(true);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
