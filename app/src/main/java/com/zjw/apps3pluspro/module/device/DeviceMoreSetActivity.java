package com.zjw.apps3pluspro.module.device;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.DialogUtils;
import com.zjw.apps3pluspro.utils.GoogleFitManager;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;

import org.json.JSONObject;


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

//        layoutDeviceUpdate.setEnabled(false);

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

//        if (mBleDeviceTools.get_is_weather()) {
//            layoutWeather.setVisibility(View.VISIBLE);
//            weatherIndex.setVisibility(View.VISIBLE);
//        } else {
//            layoutWeather.setVisibility(View.GONE);
//            weatherIndex.setVisibility(View.GONE);
//        }
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

    @OnClick({R.id.layoutDeviceUpdate, R.id.layoutWearType, R.id.layoutPage,
            R.id.tvUnitGongzhi, R.id.tvUnitYingzhi,
            R.id.tvClock24, R.id.tvClock12, R.id.layoutUnBind,
            R.id.tvCentigrade, R.id.tvFahrenheitDegree, R.id.layoutWeather,
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
                String version_name = BleTools.getDeviceVersionName(mBleDeviceTools);
                if (!JavaUtil.checkIsNull(version_name)) {
                    tvVersionName.setText(version_name);
                    getNetDeviceVersion(mBleDeviceTools.get_ble_device_type(), mBleDeviceTools.get_ble_device_version(), mBleDeviceTools.get_device_platform_type());
                } else {
                    if (HomeActivity.getBlueToothStatus() == BleConstant.STATE_CONNECTED) {
                        AppUtils.showToast(context, R.string.device_get_version);
                        getDeviceInfo();
                    } else {
                        AppUtils.showToast(context, R.string.no_connection_notification);
                    }
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
//                layoutDeviceUpdate.setEnabled(false);
                finish();
            } else {
                finish();
                MyLog.i(TAG, "升级失败！");
            }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

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
//                        getNetDeviceVersion(mBleDeviceTools.get_ble_device_type(), mBleDeviceTools.get_ble_device_version(), mBleDeviceTools.get_device_platform_type());
                    }
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
                            layoutDeviceUpdate.setEnabled(true);
                            update();
                        } else {
                            tvVersionText.setText(getString(R.string.already_new));
                            layoutDeviceUpdate.setEnabled(false);
                            MyLog.i(TAG, "固件升级 08 手环管理 不需要升级 ");
                        }

                    } else if (mDeviceBean.isOk() == 2) {
                        MyLog.i(TAG, "请求接口-获取设备版本号 未找到数据");
                        tvVersionText.setText(getString(R.string.already_new));
//                        layoutDeviceUpdate.setEnabled(false);
                    } else {
                        MyLog.i(TAG, "请求接口-获取设备版本号 请求异常(1)");
                        tvVersionText.setText(getString(R.string.already_new));
//                        layoutDeviceUpdate.setEnabled(false);
                    }
                    //请求失败
                } else {
                    tvVersionText.setText(getString(R.string.already_new));
//                    layoutDeviceUpdate.setEnabled(false);
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

    private void update() {
        if (HomeActivity.getBlueToothStatus() == BleConstant.STATE_CONNECTED) {
            //电量大于等于25
            if (mBleDeviceTools.get_ble_device_power() >= 25) {
                if (mBleDeviceTools.getIsSupportProtobuf() && mBleDeviceTools.getDeviceUpdateType()) {
                    Intent intent = new Intent(this, ProtobufActivity.class);
                    startActivity(intent);
                } else {
                    if (mBleDeviceTools.get_device_platform_type() == 0) {
                        Intent intent = new Intent(this, BleDfuActivity.class);
                        startActivityForResult(intent, REQUEST_DFU);//此处的requestCode应与下面结果处理函中调用的requestCode一致
                    } else if (mBleDeviceTools.get_device_platform_type() == 1) {
                        Intent intent = new Intent(context, RtkDfuActivity.class);
                        startActivityForResult(intent, REQUEST_DFU);//此处的requestCode应与下面结果处理函中调用的requestCode一致
                    }
                }


            } else {
                AppUtils.showToast(context, R.string.dfu_error_low_power);
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
        },2000);
    }
}
