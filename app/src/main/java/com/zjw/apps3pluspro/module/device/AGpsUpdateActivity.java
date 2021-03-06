package com.zjw.apps3pluspro.module.device;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.bleservice.BleConstant;
import com.zjw.apps3pluspro.bleservice.BleService;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.bleservice.BtSerializeation;
import com.zjw.apps3pluspro.bleservice.UpdateInfoService;
import com.zjw.apps3pluspro.eventbus.BlueToothStateEvent;
import com.zjw.apps3pluspro.eventbus.GetDeviceProtoAGpsPrepareStatusSuccessEvent;
import com.zjw.apps3pluspro.eventbus.UploadThemeStateEvent;
import com.zjw.apps3pluspro.eventbus.tools.EventTools;
import com.zjw.apps3pluspro.module.device.dfu.ProtobufActivity;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.okhttp.MyOkHttpClient;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.BleCmdManager;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.DialogUtils;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.ThemeManager;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

public class AGpsUpdateActivity extends BaseActivity {

    private static final String TAG = AGpsUpdateActivity.class.getSimpleName();
    private WaitDialog waitDialog;

    @Override
    protected int setLayoutId() {
        return R.layout.a_gps_update_activity;
    }

    @Override
    protected void initViews() {
        super.initViews();
        EventTools.SafeRegisterEventBus(this);
        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.update_AGPS_date));
        waitDialog = new WaitDialog(context);
    }

    @Override
    protected void onDestroy() {
        waitDialog.dismiss();
        EventTools.SafeUnregisterEventBus(this);
        unregisterReceiverLto();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        protoHandler = new Handler();
        SysUtils.makeRootDirectory(Constants.UPDATE_DEVICE_FILE);
        if (HomeActivity.getBlueToothStatus() == BleConstant.STATE_CONNECTED) {
            if (MyOkHttpClient.getInstance().isConnect(context)) {
                initBroadcastReceiverLto();
                requestLtoUrl();
            } else {
                AppUtils.showToast(context, R.string.net_worse_try_again);
                finish();
            }
        } else {
            AppUtils.showToast(context, R.string.no_connection_notification);
            finish();
        }
    }

    private void initBroadcastReceiverLto() {
        IntentFilter filter = new IntentFilter();

        filter.addAction(BroadcastTools.ACTION_UPDATE_LTO_SUCCESS);
        filter.addAction(BroadcastTools.ACTION_DOWN_CLOCK_FILE_STATE_ERROR);

        filter.setPriority(1000);
        registerReceiver(broadcastReceiverLto, filter);
    }

    private void unregisterReceiverLto() {
        try {
            unregisterReceiver(broadcastReceiverLto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver broadcastReceiverLto = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BroadcastTools.ACTION_UPDATE_LTO_SUCCESS:
                    MyLog.i(TAG, "lto 收到下载固件成功！");
                    startDfu();
                    break;
                case BroadcastTools.ACTION_DOWN_CLOCK_FILE_STATE_ERROR:
                    MyLog.i(TAG, "lto 收到下载固件失败！");
                    AppUtils.showToast(context, R.string.net_worse_try_again);
                    finish();
                    break;
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UploadThemeStateEvent(UploadThemeStateEvent event) {
        switch (event.state) {
            case 1:
                Toast.makeText(this, getResources().getString(R.string.send_fail), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, getResources().getString(R.string.dfu_success), Toast.LENGTH_SHORT).show();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                protoHandler.postDelayed(() -> finish(), 0 * 1000);
                break;
        }
    }


    private void startDfu() {
        showDialog();
        progressDialog.setCancelable(false);
        BleService.bluetoothLeService.sendTheme("lto", null);
    }

    private Handler protoHandler;

    UpdateInfoService updateInfoService;
    private Dialog progressDialogDownFile;

    private void requestLtoUrl() {
        RequestInfo mRequestInfo = RequestJson.getLto();
        MyLog.i(TAG, "requestLtoUrl = " + mRequestInfo.toString());
        SysUtils.logAppRunning(TAG, "requestLtoUrl = " + mRequestInfo.toString());
        NewVolleyRequest.RequestGet(mRequestInfo, TAG, new VolleyInterface(BaseApplication.getmContext(), VolleyInterface.mListener, VolleyInterface.mErrorListener) {
            @Override
            public void onMySuccess(JSONObject result) {
                MyLog.i(TAG, "requestLtoUrl result = " + result);
                SysUtils.logAppRunning(TAG, "requestLtoUrl result = " + result);
                try {
                    String code = result.optString("code");
                    if (code.equalsIgnoreCase(ResultJson.Code_operation_success)) {
                        JSONObject dataJson = result.optJSONObject("data");
                        String dataUrl = dataJson.optString("dataUrl");
                        try {
                            updateInfoService = new UpdateInfoService(context);
                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                progressDialogDownFile = DialogUtils.BaseDialogShowProgress(context,
                                        context.getResources().getString(R.string.download_title),
                                        context.getResources().getString(R.string.loading0),
                                        context.getDrawable(R.drawable.black_corner_bg)
                                );
                                updateInfoService.downLoadFileBase(dataUrl, progressDialogDownFile, protoHandler, "lto.brm", BroadcastTools.ACTION_UPDATE_LTO_SUCCESS);
                            } else {
                                AppUtils.showToast(context, R.string.sd_card);
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            finish();
                        }
                    } else {
                        AppUtils.showToast(context, R.string.update_AGPS_date_no);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AppUtils.showToast(context, R.string.update_AGPS_date_no);
                    finish();
                }
            }

            @Override
            public void onMyError(VolleyError arg0) {
                MyLog.i(TAG, "getWeatherCityBySearch arg0 = " + arg0);
                AppUtils.showToast(context, R.string.data_try_again_code1);
                finish();
            }
        });
    }

    private Dialog progressDialog;
    TextView msg, tvDeviceUpdateProgress;
    ImageView ivSyncWhite;
    private ProgressBar progressBar;

    private void showDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this, R.style.progress_dialog);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void blueToothStateEvent(BlueToothStateEvent event) {
        switch (event.state) {
            case BleConstant.STATE_CONNECTING:
                break;
            case BleConstant.STATE_DISCONNECTED:
                AppUtils.showToast(context, R.string.no_connection_notification);
                finish();
                break;
            case BleConstant.STATE_CONNECTED_TIMEOUT:
                break;
            case BleConstant.STATE_CONNECTED:
                break;
            default:
                break;
        }
    }
}
