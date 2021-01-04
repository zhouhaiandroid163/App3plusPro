package com.zjw.apps3pluspro.module.device;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.bleservice.BleConstant;
import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.bleservice.BtSerializeation;
import com.zjw.apps3pluspro.bleservice.UpdateInfoService;
import com.zjw.apps3pluspro.eventbus.BlueToothStateEvent;
import com.zjw.apps3pluspro.eventbus.GetDeviceProtoAGpsPrepareStatusSuccessEvent;
import com.zjw.apps3pluspro.eventbus.GetDeviceProtoOtaPrepareStatusEvent;
import com.zjw.apps3pluspro.eventbus.GetDeviceProtoOtaPrepareStatusSuccessEvent;
import com.zjw.apps3pluspro.eventbus.tools.EventTools;
import com.zjw.apps3pluspro.module.device.dfu.BleDfuActivity;
import com.zjw.apps3pluspro.module.device.dfu.ProtobufActivity;
import com.zjw.apps3pluspro.module.device.dfurtk.RtkDfuActivity;
import com.zjw.apps3pluspro.module.device.weather.WeatherMainActivity;
import com.zjw.apps3pluspro.module.home.PageManagementActivity;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.DeviceBean;
import com.zjw.apps3pluspro.network.okhttp.MyOkHttpClient;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.BleCmdManager;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.DialogUtils;
import com.zjw.apps3pluspro.utils.GoogleFitManager;
import com.zjw.apps3pluspro.utils.JavaUtil;
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

public class DeviceMoreSetActivity extends BaseActivity {

    private static final String TAG = DeviceMoreSetActivity.class.getSimpleName();
    @BindView(R.id.layoutWearType)
    RelativeLayout layoutWearType;
    @BindView(R.id.tvUnitYingzhi)
    CheckBox tvUnitYingzhi;
    @BindView(R.id.tvUnitGongzhi)
    CheckBox tvUnitGongzhi;
    @BindView(R.id.tvClock24)
    CheckBox tvClock24;
    @BindView(R.id.tvClock12)
    CheckBox tvClock12;
    @BindView(R.id.tvCentigrade)
    CheckBox tvCentigrade;
    @BindView(R.id.tvFahrenheitDegree)
    CheckBox tvFahrenheitDegree;
    @BindView(R.id.layoutTemperature)
    LinearLayout layoutTemperature;
    @BindView(R.id.tvMacAdress)
    TextView tvMacAdress;
    @BindView(R.id.layoutRestoreFactory)
    Button layoutRestoreFactory;
    @BindView(R.id.tvVersionText)
    TextView tvVersionText;
    @BindView(R.id.tvVersionName)
    TextView tvVersionName;
    @BindView(R.id.layoutDeviceUpdate)
    Button layoutDeviceUpdate;
    @BindView(R.id.layoutPage)
    RelativeLayout layoutPage;
    @BindView(R.id.indexPage)
    View indexPage;
    @BindView(R.id.layoutWeather)
    RelativeLayout layoutWeather;
    @BindView(R.id.weatherIndex)
    View weatherIndex;
    @BindView(R.id.tvDeviceName)
    TextView tvDeviceName;
    @BindView(R.id.ivPower)
    ImageView ivPower;
    @BindView(R.id.layoutUpdateAGpsDate)
    Button layoutUpdateAGpsDate;

    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    @Override
    protected int setLayoutId() {
        return R.layout.device_more_set_layout;
    }

    @Override
    protected void initViews() {
        super.initViews();
        setTvTitle(R.string.more_set_tip);
        EventTools.SafeRegisterEventBus(this);

        tvMacAdress.setText(mBleDeviceTools.get_ble_mac());
        tvDeviceName.setText(mBleDeviceTools.get_ble_name());

        int power = mBleDeviceTools.get_ble_device_power();
        MyLog.i(TAG, "电量 power = " + power);
        if (power >= 90) {
            ivPower.setBackgroundResource(R.mipmap.electricity_100);
        } else if (power >= 75) {
            ivPower.setBackgroundResource(R.mipmap.electricity_75);
        } else if (power >= 50) {
            ivPower.setBackgroundResource(R.mipmap.electricity_50);
        } else if (power >= 25) {
            ivPower.setBackgroundResource(R.mipmap.electricity_25);
        } else if (power >= 0) {
            ivPower.setBackgroundResource(R.mipmap.electricity_0);
        }

        String version_name = BleTools.getDeviceVersionName(mBleDeviceTools);
        if (!JavaUtil.checkIsNull(version_name)) {
            tvVersionName.setText(version_name);
        }

        tvUnitYingzhi.setChecked(false);
        tvUnitGongzhi.setChecked(false);
        tvClock12.setChecked(false);
        tvClock24.setChecked(false);
        tvCentigrade.setChecked(false);
        tvFahrenheitDegree.setChecked(false);

        if (mBleDeviceTools.get_device_unit() == 0) {
            tvUnitYingzhi.setChecked(true);
        } else {
            tvUnitGongzhi.setChecked(true);
        }
        if (mBleDeviceTools.get_colock_type() == 0) {
            tvClock12.setChecked(true);
        } else {
            tvClock24.setChecked(true);
        }
        if (mBleDeviceTools.getTemperatureType() == 0) {
            tvCentigrade.setChecked(true);
        } else {
            tvFahrenheitDegree.setChecked(true);
        }

        if (mBleDeviceTools.get_device_temperature_unit()) {
            layoutTemperature.setVisibility(View.VISIBLE);
        } else {
            layoutTemperature.setVisibility(View.GONE);
        }
        if (mBleDeviceTools.getIsSupportPageDevice()) {
            layoutPage.setVisibility(View.VISIBLE);
            indexPage.setVisibility(View.VISIBLE);
        } else {
            layoutPage.setVisibility(View.GONE);
            indexPage.setVisibility(View.GONE);
        }

        if (mBleDeviceTools.getIsGpsSensor()) {
            layoutUpdateAGpsDate.setVisibility(View.VISIBLE);
        } else {
            layoutUpdateAGpsDate.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastTools.ACTION_GATT_DEVICE_COMPLETE);
        filter.addAction(BroadcastTools.TAG_CLOSE_PHOTO_ACTION);
        filter.setPriority(1000);
        registerReceiver(broadcastReceiver, filter);

        if (HomeActivity.getBlueToothStatus() == BleConstant.STATE_CONNECTED) {
            getDeviceInfo();
        }
        waitDialog = new WaitDialog(context);
    }

    private void RestoreFactoryDialog() {
        DialogUtils.BaseDialog(context,
                context.getResources().getString(R.string.dialog_prompt),
                context.getResources().getString(R.string.restore_factory_tip),
                context.getDrawable(R.drawable.black_corner_bg),
                new DialogUtils.DialogClickListener() {
                    @Override
                    public void OnOK() {
                        unBindDevice();
                    }

                    @Override
                    public void OnCancel() {
                    }
                }
        );
    }

    public static final int REQUEST_TIME = 0x00000002; // 心率自动测量间隔时间返回页面的result
    public static final int REQUEST_DFU = 0x00000003; // 心率自动测量间隔时间返回页面的result

    private WaitDialog waitDialog;

    private boolean isClick = false;

    @OnClick({R.id.layoutDeviceUpdate, R.id.layoutWearType, R.id.layoutPage,
            R.id.tvUnitGongzhi, R.id.tvUnitYingzhi,
            R.id.tvClock24, R.id.tvClock12, R.id.layoutUnBind,
            R.id.tvCentigrade, R.id.tvFahrenheitDegree, R.id.layoutWeather, R.id.layoutUpdateAGpsDate,
            R.id.layoutRestoreFactory})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layoutUnBind:
//                startActivity(new Intent(this, ScanDeviceTypeActivity.class));
                UnableBindDeviceDialog();
                break;
            case R.id.layoutWeather:
                startActivity(new Intent(this, WeatherMainActivity.class));
                break;
            case R.id.layoutPage:
                Intent intent2 = new Intent(context, PageManagementActivity.class);
                intent2.putExtra(PageManagementActivity.PAGE_TYPE, PageManagementActivity.PAGE_DEVICE);
                startActivity(intent2);
                break;
            case R.id.layoutDeviceUpdate:
                isClick = true;
                unregisterReceiverLto();
                if (HomeActivity.getBlueToothStatus() == BleConstant.STATE_CONNECTED) {
                    getDeviceInfo();
                } else {
                    AppUtils.showToast(context, R.string.no_connection_notification);
                }
                break;
            case R.id.layoutRestoreFactory:
                if (HomeActivity.getBlueToothStatus() == BleConstant.STATE_CONNECTED) {
                    RestoreFactoryDialog();
                } else {
                    AppUtils.showToast(context, R.string.no_connection_notification);
                }
                break;
            case R.id.layoutWearType:
                startActivity(new Intent(context, WearTypeActivity.class));
                break;
            case R.id.tvUnitGongzhi:
                tvUnitYingzhi.setChecked(false);
                mUserSetTools.set_user_unit_type(true);
                mBleDeviceTools.set_device_unit(1);
                if (HomeActivity.getBlueToothStatus() == BleConstant.STATE_CONNECTED) {
                    writeRXCharacteristic(BtSerializeation.setCompany(1));
                }
                break;
            case R.id.tvUnitYingzhi:
                tvUnitGongzhi.setChecked(false);
                mUserSetTools.set_user_unit_type(false);
                mBleDeviceTools.set_device_unit(0);
                if (HomeActivity.getBlueToothStatus() == BleConstant.STATE_CONNECTED) {
                    writeRXCharacteristic(BtSerializeation.setCompany(0));
                }
                break;
            //摄氏度
            case R.id.tvCentigrade:
                mBleDeviceTools.setTemperatureType(0);
                tvFahrenheitDegree.setChecked(false);
                if (HomeActivity.getBlueToothStatus() == BleConstant.STATE_CONNECTED) {
                    writeRXCharacteristic(BtSerializeation.setTemperatureType(0));
                }
                break;
            // 华氏度
            case R.id.tvFahrenheitDegree:
                tvCentigrade.setChecked(false);
                mBleDeviceTools.setTemperatureType(1);
                if (HomeActivity.getBlueToothStatus() == BleConstant.STATE_CONNECTED) {
                    writeRXCharacteristic(BtSerializeation.setTemperatureType(1));
                }
                break;
            case R.id.tvClock24:
                tvClock12.setChecked(false);
                mBleDeviceTools.set_colock_type(1);
                if (HomeActivity.getBlueToothStatus() == BleConstant.STATE_CONNECTED) {
                    writeRXCharacteristic(BtSerializeation.setTimeFormat(1));
                }
                break;
            case R.id.tvClock12:
                tvClock24.setChecked(false);
                mBleDeviceTools.set_colock_type(0);
                if (HomeActivity.getBlueToothStatus() == BleConstant.STATE_CONNECTED) {
                    writeRXCharacteristic(BtSerializeation.setTimeFormat(0));
                }
                break;
            case R.id.layoutUpdateAGpsDate:
                SysUtils.makeRootDirectory(Constants.UPDATE_DEVICE_FILE);
                if (HomeActivity.getBlueToothStatus() == BleConstant.STATE_CONNECTED) {
                    if (MyOkHttpClient.getInstance().isConnect(context)) {
                        initBroadcastReceiverLto();
                        waitDialog.show(getResources().getString(R.string.ignored));
                        protoHandler = new Handler();
                        protoHandler.postDelayed(getDeviceStatusTimeOut, 10 * 1000);
                        writeRXCharacteristic(BtSerializeation.getBleData(null, BtSerializeation.CMD_01, BtSerializeation.KEY_AGPS));
                    } else {
                        AppUtils.showToast(context, R.string.net_worse_try_again);
                    }
                } else {
                    AppUtils.showToast(context, R.string.no_connection_notification);
                }
                break;
        }
    }

    //监听升级是否成功
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GoogleFitManager.getInstance().handleSignInResult(requestCode, resultCode, data, this);
        if (requestCode == REQUEST_TIME) {
            //升级成功或失败需要提示
        } else if (requestCode == REQUEST_DFU) {
            if (resultCode == RESULT_OK) {
                MyLog.i(TAG, "升级成功！");
                tvVersionText.setText(getString(R.string.already_new));
                tvVersionText.setTextColor(Color.BLACK);
                finish();
            } else {
                finish();
                MyLog.i(TAG, "升级失败！");
            }
        }
    }

    @Override
    protected void onDestroy() {
        EventTools.SafeUnregisterEventBus(this);
        unregisterReceiver(broadcastReceiver);
        unregisterReceiverLto();
        if (protoHandler != null) {
            protoHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    private void initBroadcastReceiverLto() {
        IntentFilter filter = new IntentFilter();

        filter.addAction(BroadcastTools.ACTION_UPDATE_LTO_SUCCESS);
        filter.addAction(ThemeManager.ACTION_CMD_APP_START);
        filter.addAction(ThemeManager.ACTION_CMD_DEVICE_START);
        filter.addAction(ThemeManager.ACTION_CMD_APP_CONFIRM);
        filter.addAction(ThemeManager.ACTION_CMD_DEVICE_CONFIRM);
        filter.addAction(ThemeManager.ACTION_CMD_DEVICE_REISSUE_PACK);

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
                                Toast.makeText(DeviceMoreSetActivity.this, getResources().getString(R.string.send_success), Toast.LENGTH_SHORT).show();
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }

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


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @SuppressWarnings({"unused", "unused"})
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                //设备信息回调完成
                case BroadcastTools.ACTION_GATT_DEVICE_COMPLETE:
                    MyLog.i(TAG, "设备信息回调完成");
                    String version_name = BleTools.getDeviceVersionName(mBleDeviceTools);
                    if (!JavaUtil.checkIsNull(version_name)) {
                        tvVersionName.setText(version_name);
                        getNetDeviceVersion(mBleDeviceTools.get_ble_device_type(), mBleDeviceTools.get_ble_device_version(), mBleDeviceTools.get_device_platform_type());
                    }
                    break;
                case BroadcastTools.ACTION_UPDATE_LTO_SUCCESS:
                    MyLog.i(TAG, "lto 收到下载固件成功！");
                    startDfu();
                    break;
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
                                Toast.makeText(DeviceMoreSetActivity.this, getResources().getString(R.string.send_success), Toast.LENGTH_SHORT).show();
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }

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


    private void getNetDeviceVersion(int model, int c_upgrade_version, int device_platform_type) {
        // TODO Auto-generated method stub

        //测试数据
//        model = 1000;
//        c_upgrade_version = 10;
//        device_platform_type = 1;
        waitDialog.show(getString(R.string.loading0));
        RequestInfo mRequestInfo = RequestJson.getDeviceUpdateInfo(String.valueOf(model), String.valueOf(c_upgrade_version), device_platform_type);

        MyLog.i(TAG, "请求接口-获取设备版本号 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

            @Override
            public void onMySuccess(JSONObject result) {
                waitDialog.close();
                // TODO Auto-generated method stub

                MyLog.i(TAG, "请求接口-获取设备版本号 result = " + result);
                DeviceBean mDeviceBean = ResultJson.DeviceBean(result);

                //请求成功
                if (mDeviceBean.isRequestSuccess()) {
                    if (mDeviceBean.isOk() == 1) {
                        MyLog.i(TAG, "请求接口-获取设备版本号 成功");

                        if (mDeviceBean.isUpdate(mBleDeviceTools)) {
                            MyLog.i(TAG, "固件升级 08 手环管理 需要升级 ");
                            tvVersionText.setText(getString(R.string.device_new_version));
                            tvVersionText.setTextColor(Color.RED);

                            if (isClick) {
                                update();
                            }
                        } else {
                            closeDialog();
                            tvVersionText.setText(getString(R.string.already_new));
                            MyLog.i(TAG, "固件升级 08 手环管理 不需要升级 ");
                        }

                    } else if (mDeviceBean.isOk() == 2) {
                        closeDialog();
                        MyLog.i(TAG, "请求接口-获取设备版本号 未找到数据");
                        tvVersionText.setText(getString(R.string.already_new));
                    } else {
                        closeDialog();
                        MyLog.i(TAG, "请求接口-获取设备版本号 请求异常(1)");
                        tvVersionText.setText(getString(R.string.already_new));
                    }
                    //请求失败
                } else {
                    closeDialog();
                    tvVersionText.setText(getString(R.string.already_new));
                    MyLog.i(TAG, "请求接口-获取设备版本号 请求异常(0)");
                }
            }

            @Override
            public void onMyError(VolleyError arg0) {
                waitDialog.close();
                MyLog.i(TAG, "请求接口-获取设备版本号 请求失败 = message = " + arg0.getMessage());
                AppUtils.showToast(mContext, R.string.net_worse_try_again);
            }
        });
    }

    private void closeDialog() {
        if (isClick) {
            Handler mHandler = new Handler();
            waitDialog.show(getResources().getString(R.string.already_new));
            mHandler.postDelayed(() -> waitDialog.close(), Constants.FINISH_ACTIVITY_DELAY_TIME);
        }
    }

    private void update() {
        if (HomeActivity.getBlueToothStatus() == BleConstant.STATE_CONNECTED) {
            if (mBleDeviceTools.getIsSupportProtobuf() && mBleDeviceTools.getDeviceUpdateType()) {
                if (mBleDeviceTools.getIsSupportGetDeviceProtoStatus()) {
                    Intent intent = new Intent(this, ProtobufActivity.class);
                    startActivity(intent);
                } else {
                    if (mBleDeviceTools.get_ble_device_power() >= 25) {
                        Intent intent = new Intent(this, ProtobufActivity.class);
                        startActivity(intent);
                    } else {
                        AppUtils.showToast(context, R.string.dfu_error_low_power);
                    }
                }

            } else {
                if (mBleDeviceTools.get_ble_device_power() >= 25) {
                    if (mBleDeviceTools.get_device_platform_type() == 0) {
                        Intent intent = new Intent(this, BleDfuActivity.class);
                        startActivityForResult(intent, REQUEST_DFU);//此处的requestCode应与下面结果处理函中调用的requestCode一致
                    } else if (mBleDeviceTools.get_device_platform_type() == 1) {
                        Intent intent = new Intent(context, RtkDfuActivity.class);
                        startActivityForResult(intent, REQUEST_DFU);//此处的requestCode应与下面结果处理函中调用的requestCode一致
                    }
                } else {
                    AppUtils.showToast(context, R.string.dfu_error_low_power);
                }
            }
        } else {
            AppUtils.showToast(context, R.string.no_connection_notification);
        }
    }

    private void UnableBindDeviceDialog() {
        DialogUtils.BaseDialog(context,
                context.getResources().getString(R.string.dialog_prompt),
                context.getResources().getString(R.string.disconnect_bracelet_tip),
                context.getDrawable(R.drawable.black_corner_bg),
                new DialogUtils.DialogClickListener() {
                    @Override
                    public void OnOK() {
                        unBindDevice();
                    }

                    @Override
                    public void OnCancel() {
                    }
                }
        );
    }

    private void unBindDevice() {
        restore_factory();
        Handler mHandler = new Handler();
        mHandler.postDelayed(() -> {
            disconnect();
            BleTools.unBind(context);
            finish();
        }, 2000);
    }


    private Handler protoHandler;
    Runnable getDeviceStatusTimeOut = () -> {
        Log.w(TAG, " getDeviceStatusTimeOut Time Out");
        waitDialog.show(getResources().getString(R.string.device_prepare2));
        protoHandler.removeCallbacksAndMessages(null);
        protoHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                waitDialog.close();
            }
        }, Constants.FINISH_ACTIVITY_DELAY_TIME);
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getDeviceProtoAGpsPrepareStatusSuccessEvent(GetDeviceProtoAGpsPrepareStatusSuccessEvent event) {
        protoHandler.removeCallbacksAndMessages(null);
        if (event.needGpsInfo) {
            // download file
            requestLtoUrl();
        } else {
            waitDialog.show(getResources().getString(R.string.update_AGPS_date_no));
            protoHandler.postDelayed(() -> waitDialog.close(), Constants.FINISH_ACTIVITY_DELAY_TIME);
        }
    }

    UpdateInfoService updateInfoService;
    private Dialog progressDialogDownFile;

    private void requestLtoUrl() {
        RequestInfo mRequestInfo = RequestJson.getLto();
        MyLog.i(TAG, "requestLtoUrl = " + mRequestInfo.toString());
        NewVolleyRequest.RequestGet(mRequestInfo, TAG, new VolleyInterface(BaseApplication.getmContext(), VolleyInterface.mListener, VolleyInterface.mErrorListener) {
            @Override
            public void onMySuccess(JSONObject result) {
                MyLog.i(TAG, "getWeatherCityBySearch result = " + result);
                try {
                    String code = result.optString("code");
                    if (code.equalsIgnoreCase(ResultJson.Code_operation_success)) {
                        waitDialog.close();
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
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyError(VolleyError arg0) {
                MyLog.i(TAG, "getWeatherCityBySearch arg0 = " + arg0);

                waitDialog.show(getResources().getString(R.string.update_AGPS_date_no));
                protoHandler.postDelayed(() -> waitDialog.close(), Constants.FINISH_ACTIVITY_DELAY_TIME);
            }
        });
    }

    private void startDfu() {
        curPiece = 0;
        type = "lto";
        showDialog();
        ThemeManager.getInstance().initUpload(this, type, null);
        startUploadThemePiece();
        progressDialog.setCancelable(false);
    }

    private String curCmd = "";
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
