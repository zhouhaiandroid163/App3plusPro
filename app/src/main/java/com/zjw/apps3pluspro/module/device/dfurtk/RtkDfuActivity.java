package com.zjw.apps3pluspro.module.device.dfurtk;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.realsil.sdk.dfu.DfuConstants;
import com.realsil.sdk.dfu.DfuException;
import com.realsil.sdk.dfu.image.BinFactory;
import com.realsil.sdk.dfu.image.ImageValidateManager;
import com.realsil.sdk.dfu.image.LoadParams;
import com.realsil.sdk.dfu.model.DfuProgressInfo;
import com.realsil.sdk.dfu.model.Throughput;
import com.realsil.sdk.dfu.utils.BaseDfuAdapter;
import com.realsil.sdk.dfu.utils.DfuAdapter;
import com.realsil.sdk.dfu.utils.DfuHelper;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.DeviceBean;
import com.zjw.apps3pluspro.bleservice.UpdateInfoService;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.DialogUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;

import java.io.File;


public class RtkDfuActivity extends RtkBaseDfuActivity<DfuHelper> implements View.OnClickListener {
    private final String TAG = RtkDfuActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    //打开BT要求
    private static final int ENABLE_BT_REQ = 0;

    //================模拟已经已经连过的设备================

//    final String DEVICE_ADDRESS = "00:E0:44:33:11:12";
//    final String DEVICE_NAME = "BLE_TES";

    //================固件目录路径================
//    final String SDCARD = Environment.getExternalStorageDirectory() + "/1/";
//    final String FILE_PATH = SDCARD + "1.0.0.A.bin";

    private TextView message_text, message_por;
    private ProgressBar rtk_progressbar;
    private Button rtk_start_updaload;

    //锁屏
    PowerManager.WakeLock wakeLock = null;

    private Handler mBleHandler;

    //下载的文件名
    private final String DFU_FILE_NAME = "rtk.bin";

    @SuppressLint("InvalidWakeLockTag")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtk_dfu);
        mContext = RtkDfuActivity.this;
        mBleHandler = new Handler();
        initBroadcast();
        initView();

        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
        wakeLock.acquire();

        //是否支持ble
        isBLESupported();

        //是否开启ble
        if (!isBLEEnabled()) {
            showBLEDialog();
        }
        initData();

        File destDir = new File(Constants.UPDATE_DEVICE_FILE);
        if (!destDir.exists()) {
            destDir.mkdirs();
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



    /**
     * 更改工作模式
     */
    public void changeWorkMode(int workMode) {
        super.changeWorkMode(workMode);
        updateUiWorkMode(getString(RtkDfuHelperImpl.getWorkModeNameResId(getDfuConfig().getOtaWorkMode())));
    }

    void updateUiWorkMode(String work_modle) {
        MyLog.i(TAG, "固件升级 更新工作模式 work_modle = " + work_modle);
    }

    void updateUiFileState(String color, String file_state) {
        MyLog.i(TAG, "固件升级 更新文件状态 file_state = " + color + "-" + file_state);
    }

    void updateUiDeviceState(String color, String file_state) {
        MyLog.i(TAG, "固件升级 更新设备状态 file_state = " + color + "-" + file_state);
    }

    void updateUiProgress(int progress) {

        if (progress > 0) {
            message_por.setVisibility(View.VISIBLE);
            message_por.setText(String.valueOf(progress) + "%");

            rtk_progressbar.setIndeterminate(false);
            rtk_progressbar.setProgress(progress);
        } else {
            message_por.setVisibility(View.GONE);
            message_por.setText("0%");

            rtk_progressbar.setIndeterminate(true);
        }

    }

    void updateUiUploadState(String upload) {
        message_text.setText(upload);
    }


    void updateButtonState() {

        if (isOtaProcessing()) {

            MyLog.i(TAG, "固件升级 按钮状态 升级中..");
            rtk_start_updaload.setEnabled(false);
            rtk_start_updaload.setVisibility(View.GONE);


            //进程\活动\图像\和\重置=
            if (mProcessState == DfuConstants.PROGRESS_ACTIVE_IMAGE_AND_RESET) {
                MyLog.i(TAG, "固件升级 按钮状态 活动/图像/重置中...");
            } else {
                MyLog.i(TAG, "固件升级 按钮状态 可以终止");
            }


        } else {
            MyLog.i(TAG, "固件升级 按钮状态 不是升级中");

            rtk_start_updaload.setEnabled(true);
            rtk_start_updaload.setVisibility(View.VISIBLE);

            if (mOtaDeviceInfo != null && mBinInfo != null) {
                MyLog.i(TAG, "固件升级 按钮状态 可以升级了");
            } else {
                MyLog.i(TAG, "固件升级 按钮状态 信息为空不可以升级");
            }
        }
    }

    /**
     * 刷新
     * 覆盖-方法重写
     */
    public void refresh() {

        try {
            //选择的设备是否为空=不为空
            if (getmDeviceAddress() != null && !getmDeviceAddress().equals("")) {

                MyLog.i(TAG, "固件升级 refresh = 选择设备 不为空");
                MyLog.i(TAG, "固件升级 refresh = 选择设备 设置标题 = name = " + getmDeviceName() + " address = " + getmDeviceAddress());
                updateUiDeviceState("默认", String.format("%s / %s", getmDeviceName(), getmDeviceAddress()));

                if (mOtaDeviceInfo != null) {

                    MyLog.i(TAG, "固件升级 refresh = ota设备 不为空");
                    updateUiDeviceState("绿色", "空");

                    MyLog.i(TAG, "固件升级 refresh = ota设备 设置标题 = 工作模式 = " + getString(RtkDfuHelperImpl.getWorkModeNameResId(getDfuConfig().getOtaWorkMode())));
                    updateUiWorkMode(getString(RtkDfuHelperImpl.getWorkModeNameResId(getDfuConfig().getOtaWorkMode())));

                } else {

                    MyLog.i(TAG, "固件升级 refresh = ota设备 等于空");
                    updateUiDeviceState("灰色", "空");

                    MyLog.i(TAG, "固件升级 refresh = ota设备 设置标题 = 工作模式 = null");
                    updateUiWorkMode(null);
                }

            }
            //设备为空-
            else {

                MyLog.i(TAG, "固件升级 refresh = 选择设备 设备为空");
                MyLog.i(TAG, "固件升级 refresh = 选择设备 设置标题 = 设备未连接");
                MyLog.i(TAG, "固件升级 refresh = ota设备 设置标题 = 工作模式 = null");

                updateUiDeviceState("灰色", "设备未连接");
                updateUiWorkMode(null);

            }


            //文件路径是否为空=为空
            if (TextUtils.isEmpty(getmFilePath())) {

                mBinInfo = null;
                updateUiFileState("灰色", "文件未加载");

            }
            //不为空
            else {
                //bin文件对象 == null
                if (mBinInfo == null) {
                    //异常处理
                    try {
                        //本地文件升级？=为了做续传？
                        boolean is_file_location = false;
                        MyLog.i(TAG, "固件升级 设置相关 文件位置判断 = " + is_file_location);

                        String is_filesuffix = RtkConstant.FILE_SUFFIX;
                        MyLog.i(TAG, "固件升级 设置相关 后缀名 = " + is_filesuffix);

                        boolean isDfuChipTypeCheckEnabled = RtkConstant.IS_DfuChip_Type_Check_Enabled;
                        MyLog.i(TAG, "固件升级 设置相关 是DFU芯片类型的检查 = " + isDfuChipTypeCheckEnabled);

                        boolean isDfuImageSectionSizeCheckEnabled =
                                RtkConstant.IS_Dfu_Image_Section_Size_Check_Enabled;
                        MyLog.i(TAG, "固件升级 设置相关 是否启用Dfu图像节大小检查 = " + isDfuImageSectionSizeCheckEnabled);

                        boolean isDfuVersionCheckEnabled = RtkConstant.IS_Dfu_Version_Check_Enabled;
                        MyLog.i(TAG, "固件升级 设置相关 是DFU版本检查 = " + isDfuVersionCheckEnabled);

                        MyLog.i(TAG, "固件升级 设置相关 文件位置判断 = false");
                        LoadParams.Builder builder = new LoadParams.Builder()
                                .setPrimaryIcType(DfuConstants.IC_BEE1)
                                .setFilePath(getmFilePath())// Mandatory
                                .setFileSuffix(is_filesuffix)
                                .setOtaDeviceInfo(mOtaDeviceInfo)// Recommend
                                .setIcCheckEnabled(isDfuChipTypeCheckEnabled)
                                .setSectionSizeCheckEnabled(isDfuImageSectionSizeCheckEnabled)
                                .setVersionCheckEnabled(isDfuVersionCheckEnabled);
                        mBinInfo = BinFactory.loadImageBinInfo(builder.build());


                        if (mBinInfo != null) {
                            int checkRet = ImageValidateManager.check(mOtaDeviceInfo, mBinInfo);

                            if (checkRet == ImageValidateManager.ERR_NA) {
                                if (mBinInfo.supportBinInputStreams != null && mBinInfo.supportBinInputStreams.size() <= 0) {

                                    mBinInfo = null;
                                    updateUiFileState("红色", "没有可升级的文件");
                                } else {

                                    getDfuConfig().setFilePath(getmFilePath());
                                    updateUiFileState("绿色", mBinInfo.fileName);
                                }
                            } else {

                                mBinInfo = null;
                                updateUiFileState("红色", RtkDfuHelperImpl.parseImageValidateError(this, checkRet));

                            }
                        } else {
                            updateUiFileState("灰色", getmFilePath());
                        }
                    }
                    //异常
                    catch (DfuException e) {
                        e.printStackTrace();
                        updateUiFileState("红色", RtkDfuHelperImpl.parseErrorCode(this, e.getErrCode()));

                    }
                }
                //bin文件，不为空
                else {
                    updateUiFileState("绿色", mBinInfo.fileName);
                }
            }

            updateButtonState();

        } catch (Exception e) {
            e.printStackTrace();
//            ZLogger.e(e.toString())
        }
    }

    //生命周期
    public void onDestroy() {
        super.onDestroy();
        MyLog.i(TAG, "固件升级 onDestroy()");
        if (mDfuHelper != null) {
            mDfuHelper.abort();//中止
            mDfuHelper.close();//关闭

        }

        if (mBleHandler != null) {
            mBleHandler.removeCallbacksAndMessages(null);
        }

        if (broadcastReceiver != null) {
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {
            }
        }
    }


    /**
     * //是否支持Ble
     */
    private void isBLESupported() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            AppUtils.showToast(mContext, R.string.dfu_no_ble);
            finish();
        }
    }

    /**
     * //显示蓝牙对话框
     */
    private void showBLEDialog() {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, ENABLE_BT_REQ);
    }

    /**
     * 是否打开蓝牙
     *
     * @return
     */
    private boolean isBLEEnabled() {
        final BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter adapter = manager.getAdapter();
        return adapter != null && adapter.isEnabled();
    }


    void initView() {

        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.firmware_update));

        message_text = (TextView) findViewById(R.id.message_text);
        message_por = (TextView) findViewById(R.id.message_por);
        rtk_progressbar = (ProgressBar) findViewById(R.id.rtk_progressbar);

        rtk_start_updaload = (Button) findViewById(R.id.rtk_start_updaload);

        findViewById(R.id.rtk_start_updaload).setOnClickListener(this);
        findViewById(R.id.rtk_stop_updaload).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.public_head_back:
                finish();
                break;
            case R.id.rtk_start_updaload:
                getNetDeviceVersion(mBleDeviceTools.get_ble_device_type(), mBleDeviceTools.get_ble_device_version(), mBleDeviceTools.get_device_platform_type());
                break;

            case R.id.rtk_stop_updaload:
                abortDfu();

            default:
                break;
        }

    }

    void initData() {
        //初始化=并设置Dfu助手回调
        getDfuHelper().initialize(mDfuHelperCallback);
    }


    @Override
    public DfuHelper getDfuHelper() {
        MyLog.i(TAG, "固件升级 getDfuHelper()");
        if (mDfuHelper == null) {
            mDfuHelper = DfuHelper.getInstance(this);
        }
        return mDfuHelper;
    }


    /**
     * 启动DFU
     */
    private void startDfu() {

//        final String SDCARD = Environment.getExternalStorageDirectory() + "/1/";
//        final String FILE_PATH = SDCARD + "1.0.0.A.bin";

        String SDCARD = Constants.UPDATE_DEVICE_FILE  + DFU_FILE_NAME;

        //加载文件
        loadingFile(SDCARD);

        // 延迟使能
        mBleHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                final String DEVICE_ADDRESS = mBleDeviceTools.get_ble_mac();
                final String DEVICE_NAME = mBleDeviceTools.get_ble_name();

                //加载目标设备
                loadingTargetDevice(DEVICE_ADDRESS, DEVICE_NAME);
                //启动升级
                startOtaService();
            }
        }, 500);
    }


    /**
     * 加载设备
     */
    private void loadingTargetDevice(String device_address, String device_name) {
        MyLog.i(TAG, "固件升级 加载目标设备");

        if (!isOtaProcessing()) {
            //OTA 设备信息 等于空
            if (mOtaDeviceInfo == null) {
                //断开当前设备
                getDfuHelper().disconnect();

                setmDeviceAddress(device_address);
                setmDeviceName(device_name);

                //设置设备名称
                getDfuConfig().setLocalName(getmDeviceName());
            }
        }

    }

    /**
     * 加载文件
     */
    private void loadingFile(String file_path) {
        MyLog.i(TAG, "固件升级 = 加载文件");
        if (!isOtaProcessing()) {
            //判断bin文件是否为空=为空
            if (mBinInfo == null) {
                //设置固件路径
                setmFilePath(file_path);
                mBinInfo = null;
                refresh();
            }
        }
    }

    /**
     * 中止升级
     */
    private void abortDfu() {
        MyLog.i(TAG, "固件升级 中止升级");
        getDfuHelper().abort();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    //===================下载文件相关=========================
    UpdateInfoService updateInfoService;


    public final int UPDATE = 2;


    private JSONObject versionObject;
    private String version, downloadAddress, downloadContentEN,
            downloadContentZH, isNeedUpdate;

    /**
     * 获取服务器版本号
     */
    private void getNetDeviceVersion(int model, int c_upgrade_version, int device_platform_type) {
        // TODO Auto-generated method stub

        rtk_start_updaload.setEnabled(false);
        rtk_start_updaload.setVisibility(View.GONE);

//        device_platform_type = 1;

        RequestInfo mRequestInfo = RequestJson.getDeviceUpdateInfo(String.valueOf(model), String.valueOf(c_upgrade_version), device_platform_type);

        MyLog.i(TAG, "请求接口-获取设备版本号 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub


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

                        return;
                    }
                });


    }

    private void ForceUpdate() {
        // TODO Auto-generated method stub
        new Thread() {
            public void run() {
                try {
                    updateInfoService = new UpdateInfoService(mContext);
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
//                    if (updateInfoService.isNeedUpdate(version)) {
//                        showUpdateDialog();
//                    }

//                    showUpdateDialog();
                    downDeviceFile();
                    break;
//                case REFRESH:// 刷新数据的操作
//                    getHomeData(uid, MyTime.getTime());
//                    break;
            }

        }

        ;
    };


    void downDeviceFile() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            downFile(downloadAddress);
        } else {
            AppUtils.showToast(mContext, R.string.sd_card);
        }
    }


    private Dialog progressDialog;

    void downFile(final String url) {
        progressDialog = DialogUtils.BaseDialogShowProgress(mContext,
                mContext.getResources().getString(R.string.download_title),
                mContext.getResources().getString(R.string.loading0),
                mContext.getDrawable(R.drawable.black_corner_bg)
        );
        updateInfoService.downLoadFile2(url, progressDialog, handler1, DFU_FILE_NAME);
    }
    ////////////////////////////

    ///////////////////////

    private void initBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastTools.ACTION_UPDATE_DEVICE_FILE_STATE_SUCCESS);
        filter.addAction(BroadcastTools.ACTION_UPDATE_DEVICE_FILE_STATE_ERROR);
        filter.setPriority(1000);
        registerReceiver(broadcastReceiver, filter);
    }

    // 广播
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
                case BroadcastTools.ACTION_UPDATE_DEVICE_FILE_STATE_ERROR:
                    AppUtils.showToast(context, R.string.net_worse_try_again);
                    finish();
                    break;


            }
        }
    };
    /**
     * dfu回调函数
     */
    private BaseDfuAdapter.DfuHelperCallback mDfuHelperCallback = new BaseDfuAdapter.DfuHelperCallback() {

        /**
         * 状态改变回调
         */
        public void onStateChanged(final int state) {
            super.onStateChanged(state);

            MyLog.i(TAG, "======== 固件升级 onStateChanged ========");

            //启动一个UI线程
            runOnUiThread(new Runnable() {
                public void run() {

                    //状态-ok
                    if (state == DfuAdapter.STATE_INIT_OK) {
                        MyLog.i(TAG, "固件升级 onStateChanged 状态=ok");
                    }

                    //状态-准备
                    else if (state == DfuAdapter.STATE_PREPARED) {
                        MyLog.i(TAG, "固件升级 onStateChanged 状态=准备");

                        //获取ota设备信息
                        mOtaDeviceInfo = getDfuHelper().getOtaDeviceInfo();
                        //发送消息=消息目标信息已更改
                        sendMessage(mHandle, MSG_TARGET_INFO_CHANGED);
                    }
                    //连接错误，或者断开。
                    else if (state == DfuAdapter.STATE_DISCONNECTED || state == DfuAdapter.STATE_CONNECT_FAILED) {
                        MyLog.i(TAG, "固件升级 onStateChanged 状态=取消");

                        //是否正在ota
                        if (!isOtaProcessing()) {
                            mOtaDeviceInfo = null;
                            //发送消息=消息目标信息已更改
                            sendMessage(mHandle, MSG_TARGET_INFO_CHANGED);
                        }
                    }
                    //否则=其他情况不处理
                    else {
                        MyLog.i(TAG, "固件升级 = onStateChanged 状态=其他 情况不处理 state = " + getString(RtkDfuHelperImpl.getAdapterStateResId(state)));
                    }
                }
            });


        }

        /**
         * 错误回调
         */
        public void onError(final int type, final int code) {
            MyLog.i(TAG, "======== 固件升级 onError ========");

            if (isOtaProcessing()) {
                MyLog.i(TAG, "固件升级 onError isOtaProcessing = " + isOtaProcessing());
                mOtaDeviceInfo = null;
            }

            //启动一个UI线程
            runOnUiThread(new Runnable() {
                public void run() {
                    String message = RtkDfuHelperImpl.parseError(getApplicationContext(), type, code);
                    updateUiUploadState(message);

                    //状态中止
                    notifyProcessStateChanged(RtkBaseDfuActivity.STATE_ABORTED);
                }
            });
        }

        /**
         * ota，加载过程升级中，状态改变。
         */
        public void onProcessStateChanged(final int state, Throughput throughput) {
            super.onProcessStateChanged(state, throughput);
            MyLog.i(TAG, "======== 固件升级 onProcessStateChanged ========");

            runOnUiThread(new Runnable() {
                public void run() {

                    //更新当前加载状态
                    mProcessState = state;
                    String message = getString(RtkDfuHelperImpl.getProgressStateResId(state));

                    MyLog.i(TAG, "固件升级 onProcessStateChanged = state = " + state);
                    MyLog.i(TAG, "固件升级 onProcessStateChanged = message = " + message);

                    //进度条-进度图像活动成功
                    if (state == DfuConstants.PROGRESS_IMAGE_ACTIVE_SUCCESS) {
                        MyLog.i(TAG, "固件升级 = onProcessStateChanged，状态改变 = 升级完成");
                        //发送消息
                        updateUiUploadState(message);
                        //设备信息，置空
                        mOtaDeviceInfo = null;
                        //绑定信息，置空
                        mBinInfo = null;
                        //业务处理，什么东西？
                        notifyProcessStateChanged(RtkBaseDfuActivity.STATE_OTA_BANKLINK_PROCESSING);

                        AppUtils.showToast(mContext, R.string.dfu_success);
                        finish();
                    }
                    //正在挂起活动映像=挂起
                    else if (state == DfuConstants.PROGRESS_PENDING_ACTIVE_IMAGE) {
                        MyLog.i(TAG, "固件升级 onProcessStateChanged 状态改变 = 正在挂起活动映像= 挂起，暂停？");
                        updateUiUploadState(message);
                        //弹出，挂起对话框
                        onPendingActiveImage();
                    }
                    //已开始进度 ，刚开始调用？标志
                    else if (state == DfuConstants.PROGRESS_STARTED) {
                        MyLog.i(TAG, "固件升级 onProcessStateChanged 状态改变 = 刚开始升级");

                        updateUiUploadState(message);
                        updateUiProgress(0);
                    }
                    //进度启动流程，正在升级中..?
                    else if (state == DfuConstants.PROGRESS_START_DFU_PROCESS) {
                        MyLog.i(TAG, "固件升级 onProcessStateChanged，状态改变 = 正在升级中..");
                        updateUiUploadState(message);
                    }
                    //其他情况
                    else {
                        MyLog.i(TAG, "固件升级 onProcessStateChanged，状态改变 = 其他情况");
                        updateUiUploadState(message);
                    }
                }
            });


        }

        //该表进度条状态
        public void onProgressChanged(final DfuProgressInfo dfuProgressInfo) {
            super.onProgressChanged(dfuProgressInfo);
            MyLog.i(TAG, "======== 固件升级 onProgressChanged ========");

            runOnUiThread(new Runnable() {
                public void run() {
                    if (mProcessState == DfuConstants.PROGRESS_START_DFU_PROCESS && dfuProgressInfo != null) {

                        MyLog.i(TAG, "固件升级 onProgressChanged = " + dfuProgressInfo.toString());
                        updateUiProgress(dfuProgressInfo.getProgress());
                        updateUiUploadState(getString(R.string.rtkbt_upgrade));
                    }
                }
            });
        }
    };

}
