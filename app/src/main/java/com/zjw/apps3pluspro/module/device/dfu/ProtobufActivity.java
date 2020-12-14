package com.zjw.apps3pluspro.module.device.dfu;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.bleservice.UpdateInfoService;
import com.zjw.apps3pluspro.eventbus.AppConfirmEvent;
import com.zjw.apps3pluspro.eventbus.BlueToothStateEvent;
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
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

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

        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.firmware_update));

        EventTools.SafeRegisterEventBus(this);
        handler = new Handler();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ThemeManager.ACTION_CMD_APP_START);
        intentFilter.addAction(ThemeManager.ACTION_CMD_DEVICE_START);
        intentFilter.addAction(ThemeManager.ACTION_CMD_APP_CONFIRM);
        intentFilter.addAction(ThemeManager.ACTION_CMD_DEVICE_CONFIRM);
        intentFilter.addAction(ThemeManager.ACTION_CMD_DEVICE_REISSUE_PACK);
        registerReceiver(receiver, intentFilter);
        initBroadcast();

        SysUtils.makeRootDirectory(Constants.UPDATE_DEVICE_FILE);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        tvMac.setText("mac:" + mBleDeviceShare.getDeviceMac());
    }

    @Override
    protected void onDestroy() {
        EventTools.SafeUnregisterEventBus(this);
        unregisterReceiver(receiver);
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

    private int curPiece = 0;
    private int curPieceSendPack = 0;
    private String type = "";

    private void startUploadThemePiece() {
        curCmd = "btUploadTheme";
        curPiece++;
        if (ThemeManager.getInstance().dataPackTotalPieceLength - curPiece >= 1) {
            curPieceSendPack = ThemeManager.getInstance().dataPieceMaxPack;
        } else {
            curPieceSendPack = ThemeManager.getInstance().dataPieceEndPack;
        }
        sendProtoUpdateData(BleCmdManager.getInstance().appStartCmd(curPieceSendPack));
    }

    private void uploadDataPiece() {
        for (int i = 0; i < curPieceSendPack; i++) {
            sendProtoUpdateData(BleCmdManager.getInstance().sendThemePiece(i + 1, curPiece));
        }
        if (progressBar != null) {
            progressBar.setProgress(curPiece * 100 / ThemeManager.getInstance().dataPackTotalPieceLength);
        }
        if ("watch".equalsIgnoreCase(type)) {
            tvDeviceUpdateProgress.setText("" + curPiece * 100 / ThemeManager.getInstance().dataPackTotalPieceLength + "%");
        } else {
            tvDeviceUpdateProgress.setText("" + curPiece * 100 / ThemeManager.getInstance().dataPackTotalPieceLength + "%");
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            switch (action) {
                case ThemeManager.ACTION_CMD_APP_START:
                    switch (curCmd) {
                        case "btUploadTheme":
                            uploadDataPiece();
                            break;
                    }
                    break;
                case ThemeManager.ACTION_CMD_DEVICE_CONFIRM:
                    switch (curCmd) {
                        case "btUploadTheme":
                            if (curPiece == ThemeManager.getInstance().dataPackTotalPieceLength) {
                                if ("watch".equalsIgnoreCase(type)) {
                                    tvDeviceUpdateProgress.setText("上传主题");
                                } else {
                                    tvDeviceUpdateProgress.setText("固件升级");
                                }
                                Toast.makeText(ProtobufActivity.this, getResources().getString(R.string.dfu_success), Toast.LENGTH_SHORT).show();
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                handler.postDelayed(() -> finish(), 0 * 1000);
                            } else {
                                startUploadThemePiece();
                            }
                            break;
                    }
                    break;
                case ThemeManager.ACTION_CMD_DEVICE_START:
                    sendProtoUpdateData(BleCmdManager.getInstance().deviceStartCmd());
                    break;
                case ThemeManager.ACTION_CMD_APP_CONFIRM:
                    sendProtoUpdateData(BleCmdManager.getInstance().appConfirm());
                    break;
                case ThemeManager.ACTION_CMD_DEVICE_REISSUE_PACK:
                    int pageNum = intent.getIntExtra("packNum", 0);
                    sendProtoUpdateData(BleCmdManager.getInstance().sendThemePiece(pageNum, curPiece));
                    break;
            }
        }
    };


    public static int capacity = 50;
    public static String firmwareVersion = "";

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void AppConfirmEvent(AppConfirmEvent event) {
        sendProtoUpdateData(BleCmdManager.getInstance().appConfirm());
    }

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

//        msg.setText("上传中...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);


        progressDialog.setOnDismissListener(dialog -> {
        });
    }

    private JSONObject versionObject;
    private String version, downloadAddress, downloadContentEN,
            downloadContentZH, isNeedUpdate;

    private void getNetDeviceVersion(int model, int c_upgrade_version, int device_platform_type) {
        update.setEnabled(false);
        update.setVisibility(View.GONE);

        RequestInfo mRequestInfo = RequestJson.getDeviceUpdateInfo(String.valueOf(model), String.valueOf(c_upgrade_version), device_platform_type);
        MyLog.i(TAG, "请求接口-获取设备版本号 mRequestInfo = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(this, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject result) {
                        MyLog.i(TAG, "请求接口-获取设备版本号 result = " + result);
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
        filter.setPriority(1000);
        registerReceiver(broadcastReceiver, filter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @SuppressWarnings({"unused", "unused"})
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BroadcastTools.ACTION_UPDATE_DEVICE_FILE_STATE_SUCCESS:
                    MyLog.i(TAG, "DFU 收到下载固件成功！");
                    startDfu();
                    break;
            }
        }
    };

    private void startDfu() {
        curPiece = 0;
        type = "ota";
        showDialog();
        ThemeManager.getInstance().initUpload(ProtobufActivity.this, type, null);
        startUploadThemePiece();
        progressDialog.setCancelable(false);
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
}
