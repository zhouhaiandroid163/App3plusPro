package com.zjw.apps3pluspro.module.device;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.mycamera.CameraActivity;
import com.lidroid.xutils.BitmapUtils;
import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseFragment;
import com.zjw.apps3pluspro.eventbus.BlueToothStateEvent;
import com.zjw.apps3pluspro.eventbus.DataSyncCompleteEvent;
import com.zjw.apps3pluspro.eventbus.DeviceInfoEvent;
import com.zjw.apps3pluspro.eventbus.DialInfoCompleteEvent;
import com.zjw.apps3pluspro.eventbus.SyncTimeLoadingEvent;
import com.zjw.apps3pluspro.eventbus.SyncTimeOutEvent;
import com.zjw.apps3pluspro.eventbus.tools.EventTools;
import com.zjw.apps3pluspro.module.device.backgroundpermission.BackgroundPermissionMainActivity;
import com.zjw.apps3pluspro.module.device.clockdial.ClockDialActivity;
import com.zjw.apps3pluspro.module.device.clockdial.CustomClockDialUtils;
import com.zjw.apps3pluspro.module.device.clockdial.MyThemeActivity;
import com.zjw.apps3pluspro.module.device.clockdial.ThemeMarketActivity;
import com.zjw.apps3pluspro.module.device.reminde.MessagePushActivity;
import com.zjw.apps3pluspro.module.device.reminde.DisturbActivity;
import com.zjw.apps3pluspro.module.device.reminde.RemindSettingActivity;
import com.zjw.apps3pluspro.module.mine.user.UserHelpActivity;
import com.zjw.apps3pluspro.bleservice.BleConstant;
import com.zjw.apps3pluspro.bleservice.BleService;
import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.bleservice.BtSerializeation;
import com.zjw.apps3pluspro.bleservice.requestServerTools;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.AuthorityManagement;
import com.zjw.apps3pluspro.utils.DialMarketManager;
import com.zjw.apps3pluspro.utils.DialogUtils;
import com.zjw.apps3pluspro.utils.GoogleFitManager;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.PageManager;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by android
 * on 2020/5/9.
 */
public class DeviceFragment extends BaseFragment {
    private static final String TAG = DeviceFragment.class.getSimpleName();
    @BindView(R.id.ivDeviceIcon)
    ImageView ivDeviceIcon;
    @BindView(R.id.tvDeviceName)
    TextView tvDeviceName;
    @BindView(R.id.ivBattery)
    ImageView ivBattery;
    @BindView(R.id.tvSyncState)
    TextView tvSyncState;
    @BindView(R.id.ivSync)
    ImageView ivSync;
    @BindView(R.id.layoutDialMarket)
    LinearLayout layoutDialMarket;
    @BindView(R.id.tvTitle1)
    TextView tvTitle1;
    @BindView(R.id.layoutMessage)
    LinearLayout layoutMessage;
    @BindView(R.id.tvTitle2)
    TextView tvTitle2;
    @BindView(R.id.layoutRemind)
    LinearLayout layoutRemind;
    @BindView(R.id.tvTitle5)
    TextView tvTitle5;
    @BindView(R.id.scRaiseHandLightScreen)
    SwitchCompat scRaiseHandLightScreen;
    @BindView(R.id.tvTitle6)
    TextView tvTitle6;
    @BindView(R.id.scWeather)
    SwitchCompat scWeather;
    @BindView(R.id.tvTitle7)
    TextView tvTitle7;
    @BindView(R.id.layoutFindDevice)
    LinearLayout layoutFindDevice;
    @BindView(R.id.tvTitle8)
    TextView tvTitle8;
    @BindView(R.id.layoutPhotograph)
    LinearLayout layoutPhotograph;
    @BindView(R.id.layoutRaiseWristBrightenScreen)
    LinearLayout layoutRaiseWristBrightenScreen;
    @BindView(R.id.tvTitle9)
    TextView tvTitle9;
    @BindView(R.id.tvTitle10)
    TextView tvTitle10;
    @BindView(R.id.layoutMoreSetting)
    LinearLayout layoutMoreSetting;
    @BindView(R.id.tvTitle11)
    TextView tvTitle11;
    @BindView(R.id.layoutUseGuide)
    LinearLayout layoutUseGuide;
    @BindView(R.id.tvUnbind)
    TextView tvUnbind;
    @BindView(R.id.layoutNoDevice)
    LinearLayout layoutNoDevice;
    @BindView(R.id.svHasDevice)
    ScrollView svHasDevice;
    @BindView(R.id.layoutAddDevice)
    LinearLayout layoutAddDevice;

    @BindView(R.id.ivTheme1)
    ImageView ivTheme1;
    @BindView(R.id.ivTheme2)
    ImageView ivTheme2;
    @BindView(R.id.ivTheme3)
    ImageView ivTheme3;
    @BindView(R.id.layoutSync)
    LinearLayout layoutSync;
    @BindView(R.id.layoutCurDevice)
    ConstraintLayout layoutCurDevice;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.v1)
    View v1;
    @BindView(R.id.tvHourlyHeartTitle)
    TextView tvHourlyHeartTitle;
    @BindView(R.id.scHourlyHeart)
    SwitchCompat scHourlyHeart;
    @BindView(R.id.tvContinuousHeart)
    TextView tvContinuousHeart;
    @BindView(R.id.scContinuousHeart)
    SwitchCompat scContinuousHeart;
    @BindView(R.id.scTemperature)
    SwitchCompat scTemperature;
    @BindView(R.id.tvSpo2Title)
    TextView tvSpo2Title;
    @BindView(R.id.scSpo2)
    SwitchCompat scSpo2;
    @BindView(R.id.tvGoogleFitTitle)
    TextView tvGoogleFitTitle;
    @BindView(R.id.scGoogleFit)
    SwitchCompat scGoogleFit;
    @BindView(R.id.tvScreenSetTitle)
    TextView tvScreenSetTitle;
    @BindView(R.id.layoutHourlyHeart)
    LinearLayout layoutHourlyHeart;
    @BindView(R.id.layoutContinuousHeart)
    LinearLayout layoutContinuousHeart;
    @BindView(R.id.tvTitle4)
    TextView tvTitle4;
    @BindView(R.id.layoutContinuousTemp)
    LinearLayout layoutContinuousTemp;
    @BindView(R.id.layoutContinuousSpo2)
    LinearLayout layoutContinuousSpo2;
    @BindView(R.id.layoutSwather)
    LinearLayout layoutSwather;
    @BindView(R.id.layoutGoogleFit)
    LinearLayout layoutGoogleFit;
    @BindView(R.id.layoutScreenSet)
    LinearLayout layoutScreenSet;
    @BindView(R.id.layoutCardSorting)
    LinearLayout layoutCardSorting;
    @BindView(R.id.tvThemeTitle)
    TextView tvThemeTitle;
    @BindView(R.id.layoutTheme)
    LinearLayout layoutTheme;
    @BindView(R.id.tvScreensaverTitle)
    TextView tvScreensaverTitle;
    @BindView(R.id.layoutScreensaver)
    LinearLayout layoutScreensaver;
    @BindView(R.id.tvNotRemindTitle)
    TextView tvNotRemindTitle;
    @BindView(R.id.layoutNotRemind)
    LinearLayout layoutNotRemind;
    @BindView(R.id.ivCover1)
    ImageView ivCover1;
    @BindView(R.id.ivCover2)
    ImageView ivCover2;
    @BindView(R.id.ivCover3)
    ImageView ivCover3;
    @BindView(R.id.layoutContact)
    LinearLayout layoutContact;

    @BindView(R.id.tvTitleTop)
    TextView tvTitleTop;

    private HomeActivity homeActivity;
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();

    private Handler mHandler = new Handler();

    @Override
    public View initView() {
        EventTools.SafeRegisterEventBus(this);
        homeActivity = (HomeActivity) this.getActivity();
        view = View.inflate(context, R.layout.device_fragment, null);
        return view;
    }

    @Override
    public void initData() {
        if (mBleDeviceTools.getIsSupportTakePicture()) {
            layoutPhotograph.setVisibility(View.VISIBLE);
        } else {
            layoutPhotograph.setVisibility(View.GONE);
        }

        if (mBleDeviceTools.getIsSupportRaiseWristBrightenScreen()) {
            layoutRaiseWristBrightenScreen.setVisibility(View.VISIBLE);
        } else {
            layoutRaiseWristBrightenScreen.setVisibility(View.GONE);
        }


        initDeviceView();
        initSwitchCompat();
        initDialMarket();
    }

    private void initDialMarket() {
        if (!mBleDeviceTools.get_device_is_theme_transmission()) {
            return;
        }

        if (mBleDeviceTools.get_device_theme_shape() == 0) {
            ivTheme1.setBackgroundResource(R.mipmap.device_watch);
            ivTheme2.setBackgroundResource(R.mipmap.device_watch);
            ivTheme3.setBackgroundResource(R.mipmap.device_watch);
        } else if (mBleDeviceTools.get_device_theme_shape() == 1) {
            ivTheme1.setBackgroundResource(R.color.transparent);
            ivTheme2.setBackgroundResource(R.color.transparent);
            ivTheme3.setBackgroundResource(R.color.transparent);
        } else {
            ivTheme1.setBackgroundResource(R.mipmap.device_circular_watch);
            ivTheme2.setBackgroundResource(R.mipmap.device_circular_watch);
            ivTheme3.setBackgroundResource(R.mipmap.device_circular_watch);
        }

        if (mBleDeviceTools.get_device_theme_shape() == 2) {
            ivCover1.setVisibility(View.VISIBLE);
            ivCover2.setVisibility(View.VISIBLE);
            ivCover3.setVisibility(View.VISIBLE);
        } else {
            ivCover1.setVisibility(View.GONE);
            ivCover2.setVisibility(View.GONE);
            ivCover3.setVisibility(View.GONE);
        }

        int device_width = mBleDeviceTools.get_device_theme_resolving_power_width();
        int device_height = mBleDeviceTools.get_device_theme_resolving_power_height();
        if (device_width > 0 && device_height > 0) {
            if (DialMarketManager.getInstance().dialMarketList.size() == 0) {
                DialMarketManager.getInstance().getPageList(new DialMarketManager.GetListOnFinishListen() {
                    @Override
                    public void success() {
                        initTheme();
                    }

                    public void fail() {
                    }

                    public void error() {
                    }
                });
            } else {
                initTheme();
            }
        }
    }

    private void initTheme() {
        if (DialMarketManager.getInstance().dialMarketList.size() == 1) {
            if (!TextUtils.isEmpty(DialMarketManager.getInstance().dialMarketList.get(0).getThemeImgUrl())) {
                new BitmapUtils(homeActivity).display(ivTheme1, DialMarketManager.getInstance().dialMarketList.get(0).getThemeImgUrl());
            }
        }
        if (DialMarketManager.getInstance().dialMarketList.size() == 2) {
            if (!TextUtils.isEmpty(DialMarketManager.getInstance().dialMarketList.get(0).getThemeImgUrl())) {
                new BitmapUtils(homeActivity).display(ivTheme1, DialMarketManager.getInstance().dialMarketList.get(0).getThemeImgUrl());
            }
            if (!TextUtils.isEmpty(DialMarketManager.getInstance().dialMarketList.get(1).getThemeImgUrl())) {
                new BitmapUtils(homeActivity).display(ivTheme2, DialMarketManager.getInstance().dialMarketList.get(1).getThemeImgUrl());
            }
        }
        if (DialMarketManager.getInstance().dialMarketList.size() >= 3) {
            if (!TextUtils.isEmpty(DialMarketManager.getInstance().dialMarketList.get(0).getThemeImgUrl())) {
                new BitmapUtils(homeActivity).display(ivTheme1, DialMarketManager.getInstance().dialMarketList.get(0).getThemeImgUrl());
            }
            if (!TextUtils.isEmpty(DialMarketManager.getInstance().dialMarketList.get(1).getThemeImgUrl())) {
                new BitmapUtils(homeActivity).display(ivTheme2, DialMarketManager.getInstance().dialMarketList.get(1).getThemeImgUrl());
            }
            if (!TextUtils.isEmpty(DialMarketManager.getInstance().dialMarketList.get(2).getThemeImgUrl())) {
                new BitmapUtils(homeActivity).display(ivTheme3, DialMarketManager.getInstance().dialMarketList.get(2).getThemeImgUrl());
            }
        }
    }


    @Override
    public void onResume() {
        initViewState();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        EventTools.SafeUnregisterEventBus(this);
        super.onDestroy();
    }

    private void initSwitchCompat() {
        scHourlyHeart.setChecked(mBleDeviceTools.get_point_measurement_heart());
        scContinuousHeart.setChecked(mBleDeviceTools.get_persist_measurement_heart());
        scTemperature.setChecked(mBleDeviceTools.get_continuity_temp());
        scSpo2.setChecked(mBleDeviceTools.get_continuity_spo2());
        scRaiseHandLightScreen.setChecked(mBleDeviceTools.get_taiwan());
        scWeather.setChecked(mBleDeviceTools.get_weather_is_open());
        scGoogleFit.setChecked(mBleDeviceTools.getIsOpenGooglefit());
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case HomeActivity.BleStateResult:
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    return;
                }
                // 如果本地蓝牙没有开启，则开启
                if (mBluetoothAdapter.isEnabled()) {
                    startActivity(new Intent(homeActivity, ScanDeviceActivity.class));
                    MyLog.i(TAG, "请求打开蓝牙 回调 = 蓝牙状态 打开了");
                } else {
                    MyLog.i(TAG, "请求打开蓝牙 回调 = 蓝牙状态 没打开");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void UnableBindDeviceDialog() {
        new AlertDialog.Builder(context)
                .setTitle(getString(R.string.dialog_prompt))//设置对话框标题
                .setMessage(getString(R.string.disconnect_bracelet_tip))//设置显示的内容
                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {//添加确定按钮
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        if (homeActivity != null) {
                            mBleDeviceTools.setWeatherSyncTime(0);
                            mBleDeviceTools.setLastUploadDataServiceTime(0);
                            mBleDeviceTools.setLastSyncTime(0);
                            mBleDeviceTools.setLastDeviceSportSyncTime(0);
                            requestServerTools.uploadUnDeviceData(context.getApplicationContext());
                            homeActivity.disconnect();
                            mBleDeviceTools.set_ble_mac("");
                            mBleDeviceTools.set_call_ble_mac("");
                            mBleDeviceTools.set_ble_name("");
                            mBleDeviceTools.set_call_ble_name("");
                            mUserSetTools.set_service_upload_device_info("");
                            mBleDeviceTools.set_device_theme_resolving_power_height(0);
                            mBleDeviceTools.set_device_theme_resolving_power_width(0);
                            BleService.setBlueToothStatus(BleConstant.STATE_DISCONNECTED);
                            initViewState();
                            DialMarketManager.getInstance().clearList();
                            PageManager.getInstance().cleanList();
                        }

                    }

                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {//添加返回按钮


            @Override

            public void onClick(DialogInterface dialog, int which) {//响应事件
            }

        }).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void syncTimeLoadingEventEvent(SyncTimeLoadingEvent event) {
        syncAnimation();
        tvSyncState.setText(getResources().getString(R.string.sync_data_ing));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void syncTimeOutEventEvent(SyncTimeOutEvent event) {
        ivSync.clearAnimation();
        ivSync.setBackground(getResources().getDrawable(R.mipmap.icon_complete));
        tvSyncState.setText(getResources().getString(R.string.sync_time_out));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void blueToothStateEvent(BlueToothStateEvent event) {
        setBattery(event.state);
        initBleState(event.state);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deviceInfoComplete(DeviceInfoEvent event) {
        setBattery(homeActivity.getBlueToothStatus());
        initDeviceView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dialInfoCompleteEvent(DialInfoCompleteEvent event) {
        initDialMarket();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dataSyncSuccess(DataSyncCompleteEvent event) {
        ivSync.clearAnimation();
        ivSync.setBackground(getResources().getDrawable(R.mipmap.icon_complete));
        tvSyncState.setText(getResources().getString(R.string.sync_data_success));
    }

    @SuppressLint("StringFormatInvalid")
    private void initViewState() {
        if (JavaUtil.checkIsNull(mBleDeviceTools.get_ble_mac())) {
            layoutNoDevice.setVisibility(View.GONE);
            svHasDevice.setVisibility(View.GONE);
        } else {
            layoutNoDevice.setVisibility(View.GONE);
            svHasDevice.setVisibility(View.VISIBLE);
            tvDeviceName.setText(MyUtils.getNewBleName(mBleDeviceTools));
        }

        setBattery(homeActivity.getBlueToothStatus());
        if (BleService.syncState) {
            if (homeActivity.ISBlueToothConnect()) {
                syncAnimation();
                tvSyncState.setText(getResources().getString(R.string.sync_data_ing));
            } else {
                initBleState(homeActivity.getBlueToothStatus());
            }
        } else {
            if (homeActivity.ISBlueToothConnect()) {
                ivSync.clearAnimation();
                ivSync.setBackground(getResources().getDrawable(R.mipmap.icon_complete));
                long lastSyncTime = mBleDeviceTools.getLastSyncTime();
                if (lastSyncTime > 0) {
                    long time = (System.currentTimeMillis() - lastSyncTime) / (1000 * 60);
                    if (time > 0 && time <= 30) {
                        tvSyncState.setText(String.format(getResources().getString(R.string.sync_data_time), String.valueOf(time)));
                    }
                    if (time > 30 && homeActivity.DeviceIsConnect()) {
                        homeActivity.syncData();
                    }
                    if (time == 0) {
                        tvSyncState.setText(getResources().getString(R.string.sync_data_success));
                    }
                } else {
                    tvSyncState.setText(getResources().getString(R.string.no_data_default));
                }
            } else {
                initBleState(homeActivity.getBlueToothStatus());
            }
        }
    }

    private void setDeviceEnable(boolean isEnable) {
        scHourlyHeart.setEnabled(isEnable);
        scContinuousHeart.setEnabled(isEnable);
        scTemperature.setEnabled(isEnable);
        scSpo2.setEnabled(isEnable);
        scRaiseHandLightScreen.setEnabled(isEnable);
        scWeather.setEnabled(isEnable);
        scGoogleFit.setEnabled(isEnable);

    }

    private void initBleState(int state) {

        setDeviceEnable(false);

        switch (state) {
            case BleConstant.STATE_DISCONNECTED:
                MyLog.i(TAG, "setBattery() state = STATE_DISCONNECTED");
                ivSync.setBackground(getResources().getDrawable(R.mipmap.sync_image));
                tvSyncState.setText(getResources().getString(R.string.index_tip_no_connect1));
                break;
            case BleConstant.STATE_CONNECTING:
                MyLog.i(TAG, "setBattery() state = STATE_CONNECTING");
                tvSyncState.setText(getResources().getString(R.string.loading3));
                syncAnimation();
                break;
            case BleConstant.STATE_CONNECTED:
                MyLog.i(TAG, "setBattery() state = STATE_CONNECTED");
                tvSyncState.setText(getResources().getString(R.string.already_connect_bracelet));
//                tvSyncState.setText(getResources().getString(R.string.sync_data_ing));
                setDeviceEnable(true);
                MyLog.i(TAG, "setBattery() state = STATE_CONNECTING");
                break;
            case BleConstant.STATE_CONNECTED_TIMEOUT:
                MyLog.i(TAG, "setBattery() state = STATE_CONNECTED_TIMEOUT");
                tvSyncState.setText(getResources().getString(R.string.connect_timeout));
                break;
        }
    }

    private void initDeviceView() {
        MyLog.i(TAG, "initDeviceView()");
        tvTitleTop.setText(getResources().getString(R.string.device_title));

        layoutHourlyHeart.setVisibility(View.GONE);
        layoutContinuousHeart.setVisibility(View.GONE);

        layoutContinuousTemp.setVisibility(View.GONE);
        layoutContinuousSpo2.setVisibility(View.GONE);
        layoutSwather.setVisibility(View.GONE);
        layoutGoogleFit.setVisibility(View.GONE);
        layoutScreenSet.setVisibility(View.GONE);
        layoutTheme.setVisibility(View.GONE);
        layoutCardSorting.setVisibility(View.GONE);

        layoutScreensaver.setVisibility(View.GONE);
        layoutNotRemind.setVisibility(View.GONE);

        layoutDialMarket.setVisibility(View.GONE);
        layoutContact.setVisibility(View.GONE);

        if (mBleDeviceTools.get_device_is_theme_transmission()) {
            layoutDialMarket.setVisibility(View.VISIBLE);
        }

        if (mBleDeviceTools.get_is_support_ppg() == 1) {
            if (mBleDeviceTools.get_is_support_persist_heart() == 1) {
                layoutContinuousHeart.setVisibility(View.VISIBLE);
            } else {
                layoutHourlyHeart.setVisibility(View.VISIBLE);
            }
        }

        if (mBleDeviceTools.get_theme_count() > 1) {
            layoutTheme.setVisibility(View.VISIBLE);
        }

        if (mBleDeviceTools.get_is_support_screen()) {
            layoutScreenSet.setVisibility(View.VISIBLE);
        }

        if (!AppUtils.isZh(context)) {
            layoutGoogleFit.setVisibility(View.VISIBLE);
        } else {
            mBleDeviceTools.setIsOpenGooglefit(false);
        }

        if (mBleDeviceTools.get_is_screen_saver()) {
            layoutScreensaver.setVisibility(View.VISIBLE);
        }

        if (mBleDeviceTools.get_is_support_temp() && mBleDeviceTools.getSupportContinuousTemp()) {
            layoutContinuousTemp.setVisibility(View.VISIBLE);
        }

        if (mBleDeviceTools.get_is_support_spo2() && mBleDeviceTools.getSupportContinuousBloodOxygen()) {
            layoutContinuousSpo2.setVisibility(View.VISIBLE);
        }

        if (mBleDeviceTools.get_device_is_disturb()) {
            layoutNotRemind.setVisibility(View.VISIBLE);
        }

        if (mBleDeviceTools.get_is_support_mail_list()) {
            layoutContact.setVisibility(View.VISIBLE);
        }
    }


    private void setBattery(int status) {
        if (status == BleConstant.STATE_CONNECTED) {
            MyLog.i(TAG, "setBattery() state = STATE_CONNECTED");
            int power = mBleDeviceTools.get_ble_device_power();
            MyLog.i(TAG, "电量 power = " + power);
            if (power >= 90) {
                ivBattery.setBackgroundResource(R.mipmap.electricity_100);
            } else if (power >= 75) {
                ivBattery.setBackgroundResource(R.mipmap.electricity_75);
            } else if (power >= 50) {
                ivBattery.setBackgroundResource(R.mipmap.electricity_50);
            } else if (power >= 25) {
                ivBattery.setBackgroundResource(R.mipmap.electricity_25);
            } else if (power >= 0) {
                ivBattery.setBackgroundResource(R.mipmap.electricity_0);
            }
        } else {
            ivBattery.setBackgroundResource(R.mipmap.electricity_0);
        }
    }


    private void goToCameraActivity() {
        if (homeActivity.getBlueToothStatus() == BleConstant.STATE_CONNECTED) {
            homeActivity.openPhoto();
            Intent intent = new Intent(context, CameraActivity.class);
            startActivity(intent);
        } else {
            AppUtils.showToast(context, R.string.no_connection_notification);
        }
    }

    private void showSettingDialog(String title) {
        new AlertDialog.Builder(context)
                .setTitle(getString(R.string.dialog_prompt))//设置对话框标题
                .setMessage(title)//设置显示的内容
                .setPositiveButton(getString(R.string.setting_dialog_setting), new DialogInterface.OnClickListener() {//添加确定按钮

                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }

                }).setNegativeButton(getString(R.string.setting_dialog_cancel), new DialogInterface.OnClickListener() {//添加返回按钮


            @Override

            public void onClick(DialogInterface dialog, int which) {//响应事件
                // TODO Auto-generated method stub
            }

        }).show();//在按键响应事件中显示此对话框
    }

    private void syncAnimation() {
        ivSync.clearAnimation();
        ivSync.setBackground(getResources().getDrawable(R.mipmap.sync_image));
        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(2000);// 设置动画持续周期
        rotate.setRepeatCount(-1);// 设置重复次数
        rotate.setFillAfter(true);// 动画执行完后是否停留在执行完的状态
        rotate.setStartOffset(10);// 执行前的等待时间
        ivSync.setAnimation(rotate);
    }


    @SuppressLint("MissingPermission")
    @OnClick({R.id.layoutDialMarket, R.id.layoutMessage, R.id.layoutRemind, R.id.layoutAddDevice,
            R.id.layoutFindDevice, R.id.layoutPhotograph, R.id.layoutMoreSetting, R.id.layoutUseGuide, R.id.tvUnbind, R.id.layoutRunningPermission,
            R.id.layoutScreensaver, R.id.layoutContact,
            R.id.layoutNotRemind,
            R.id.scHourlyHeart,
            R.id.scContinuousHeart,
            R.id.scTemperature,
            R.id.scSpo2,
            R.id.scRaiseHandLightScreen,
            R.id.scWeather,
            R.id.scGoogleFit,
            R.id.layoutCardSorting,
            R.id.layoutTheme,
            R.id.layoutScreenSet,
            R.id.layoutSync
    })
    public void onViewClicked(View view) {
        boolean isChecked;
        switch (view.getId()) {
            case R.id.layoutRunningPermission:
                startActivity(new Intent(homeActivity, BackgroundPermissionMainActivity.class));
                break;
            //同步时间-数据
            case R.id.layoutSync:
                if (BleService.syncState) {
                    return;
                }
                if (HomeActivity.isSyncSportData) {
                    return;
                }
                homeActivity.syncData();
                break;

            //屏保设置
            case R.id.layoutScreensaver:
                if (homeActivity.DeviceIsConnect()) {
                    if (mBleDeviceTools.get_ble_device_power() >= 50) {
                        startActivity(new Intent(context, SendBleActivity.class));
                    } else {
                        AppUtils.showToast(context, R.string.send_imge_error_low_power);
                    }
                } else {
                    AppUtils.showToast(context, R.string.no_connection_notification);
                }
                break;

            //勿扰模式
            case R.id.layoutNotRemind:
                if (homeActivity.DeviceIsConnect()) {
                    startActivity(new Intent(context, DisturbActivity.class));
                } else {
                    AppUtils.showToast(context, R.string.no_connection_notification);
                }
                break;

            //整点心率
            case R.id.scHourlyHeart:
                isChecked = scHourlyHeart.isChecked();
                mBleDeviceTools.set_point_measurement_heart(isChecked);
                if (homeActivity.DeviceIsConnect()) {
                    if (isChecked) {
                        homeActivity.writeRXCharacteristic(BtSerializeation.setPointMeasurement(1));
                    } else {
                        homeActivity.writeRXCharacteristic(BtSerializeation.setPointMeasurement(0));
                    }
                } else {
                    mBleDeviceTools.set_point_measurement_heart(!isChecked);
                    scHourlyHeart.setChecked(!isChecked);
                }
                break;

            //连续心率
            case R.id.scContinuousHeart:
                isChecked = scContinuousHeart.isChecked();
                mBleDeviceTools.set_persist_measurement_heart(isChecked);
                if (homeActivity.DeviceIsConnect()) {
                    if (isChecked) {
                        homeActivity.writeRXCharacteristic(BtSerializeation.setPersistMeasurement(1));
                        DialogUtils.OpenContinuityDialog(homeActivity);
                    } else {
                        homeActivity.writeRXCharacteristic(BtSerializeation.setPersistMeasurement(0));
                    }
                } else {
                    mBleDeviceTools.set_persist_measurement_heart(!isChecked);
                    scContinuousHeart.setChecked(!isChecked);
                }
                break;

            //连续体温
            case R.id.scTemperature:
                isChecked = scTemperature.isChecked();
                mBleDeviceTools.set_continuity_temp(isChecked);
                if (homeActivity.DeviceIsConnect()) {
                    if (isChecked) {
                        homeActivity.writeRXCharacteristic(BtSerializeation.setContinuityTemp(1));
                        DialogUtils.OpenContinuityDialog(homeActivity);
                    } else {
                        homeActivity.writeRXCharacteristic(BtSerializeation.setContinuityTemp(0));
                    }
                } else {
                    mBleDeviceTools.set_continuity_temp(!isChecked);
                    scTemperature.setChecked(!isChecked);
                }
                break;
            //连续血氧
            case R.id.scSpo2:
                isChecked = scSpo2.isChecked();
                mBleDeviceTools.set_continuity_spo2(isChecked);
                if (homeActivity.DeviceIsConnect()) {
                    if (isChecked) {
                        homeActivity.writeRXCharacteristic(BtSerializeation.setContinuitySpo2(1));
                        DialogUtils.OpenContinuityDialog(homeActivity);
                    } else {
                        homeActivity.writeRXCharacteristic(BtSerializeation.setContinuitySpo2(0));
                    }
                } else {
                    mBleDeviceTools.set_continuity_spo2(!isChecked);
                    scSpo2.setChecked(!isChecked);
                }
                break;

            //抬腕亮屏
            case R.id.scRaiseHandLightScreen:
                isChecked = scRaiseHandLightScreen.isChecked();
                mBleDeviceTools.set_taiwan(isChecked);
                if (homeActivity.DeviceIsConnect()) {
                    if (isChecked) {
                        homeActivity.writeRXCharacteristic(BtSerializeation.setBrightScreen(1));
                    } else {
                        homeActivity.writeRXCharacteristic(BtSerializeation.setBrightScreen(0));
                    }
                } else {
                    mBleDeviceTools.set_taiwan(!isChecked);
                    scRaiseHandLightScreen.setChecked(!isChecked);
                }
                break;

            //天气
            case R.id.scWeather:
                isChecked = scWeather.isChecked();
                mBleDeviceTools.set_weather_is_open(isChecked);
                break;

            //GoogleFit
            case R.id.scGoogleFit:
                isChecked = scGoogleFit.isChecked();
                mBleDeviceTools.setIsOpenGooglefit(isChecked);
                if (isChecked) {
                    GoogleFitManager.getInstance().OpenGoogleFit(homeActivity);
                }
                break;

            //表盘市场
            case R.id.layoutDialMarket:
                if (homeActivity.DeviceIsConnect()) {
                    if (mBleDeviceTools.getClockDialGenerationMode() == 0 && mBleDeviceTools.getClockDialTransmissionMode() == 0) {
                        if (mBleDeviceTools.get_ble_device_power() >= 50) {
                            // 旧接口
                            if (CustomClockDialUtils.checkIsNewClockDial()) {
                                startActivity(new Intent(context, ClockDialActivity.class)); // 自定义
                            } else {
                                startActivity(new Intent(context, MyThemeActivity.class));
                            }
                        } else {
                            AppUtils.showToast(context, R.string.send_imge_error_low_power);
                        }
                    } else {
                        // 新接口表盘分类
                        if (mBleDeviceTools.getIsSupportGetDeviceProtoStatus()) {
                            startActivity(new Intent(context, ThemeMarketActivity.class));
                        } else {
                            if (mBleDeviceTools.get_ble_device_power() >= 50) {
                                startActivity(new Intent(context, ThemeMarketActivity.class));
                            } else {
                                AppUtils.showToast(context, R.string.send_imge_error_low_power);
                            }
                        }
                    }
                } else {
                    AppUtils.showToast(context, R.string.no_connection_notification);
                }
                break;
            case R.id.layoutMessage:
                if (homeActivity.DeviceIsConnect()) {
                    startActivity(new Intent(context, MessagePushActivity.class));
                } else {
                    AppUtils.showToast(context, R.string.no_connection_notification);
                }
                break;

            case R.id.layoutRemind:
                if (homeActivity.DeviceIsConnect()) {
                    startActivity(new Intent(context, RemindSettingActivity.class));
                } else {
                    AppUtils.showToast(context, R.string.no_connection_notification);
                }
                break;
            case R.id.layoutFindDevice:
                if (homeActivity.DeviceIsConnect()) {
                    if (BleTools.checkContinuousClick()) {
                        homeActivity.writeRXCharacteristic(BtSerializeation.findDeviceInstra());
                    }
                } else {
                    AppUtils.showToast(context, R.string.no_connection_notification);
                }
                break;
            case R.id.layoutPhotograph:
                if (AuthorityManagement.verifyStoragePermissions(homeActivity)) {
                    MyLog.i(TAG, "SD卡权限 已获取");
                } else {
                    MyLog.i(TAG, "SD卡权限 未获取");
                    showSettingDialog(getString(R.string.setting_dialog_storage));
                }

                if (AuthorityManagement.verifyPhotogrAuthority(homeActivity)) {
                    MyLog.i(TAG, "拍照权限 已获取");
                    goToCameraActivity();
                } else {
                    MyLog.i(TAG, "拍照权限 未获取");
                    showSettingDialog(getString(R.string.setting_dialog_call_camera));
                }
                break;

            //更多设置
            case R.id.layoutMoreSetting:
//                if (homeActivity.DeviceIsConnect()) {
//                    startActivity(new Intent(homeActivity, DeviceMoreSetActivity.class));
//                } else {
//                    AppUtils.showToast(context, R.string.no_connection_notification);
//                }
                startActivity(new Intent(homeActivity, DeviceMoreSetActivity.class));
                break;
            case R.id.layoutUseGuide:
                startActivity(new Intent(homeActivity, UserHelpActivity.class));
                break;
            case R.id.layoutAddDevice:
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    return;
                }
                if (mBluetoothAdapter.isEnabled()) {
                    startActivity(new Intent(homeActivity, ScanDeviceActivity.class));
                } else {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, HomeActivity.BleStateResult);
                }

                break;
            case R.id.tvUnbind:
                UnableBindDeviceDialog();
                break;

            //一级界面卡片顺序
            case R.id.layoutCardSorting:
                break;

            //主题选择
            case R.id.layoutTheme:
                if (homeActivity.DeviceIsConnect()) {
                    startActivity(new Intent(context, DeviceThemeActivity.class));
                } else {
                    AppUtils.showToast(context, R.string.no_connection_notification);
                }
                break;

            //屏幕设置
            case R.id.layoutScreenSet:
                if (homeActivity.DeviceIsConnect()) {
                    startActivity(new Intent(context, ScreenSettingsActivity.class));
                } else {
                    AppUtils.showToast(context, R.string.no_connection_notification);
                }
                break;
            case R.id.layoutContact:
                if (homeActivity.DeviceIsConnect()) {
                    if (MyUtils.checkBlePairMac(mBleDeviceTools.get_call_ble_mac())) {
                        startActivity(new Intent(context, ContactListActivity.class));
                    } else {
                        showBleSetDialog();
                    }
                } else {
                    AppUtils.showToast(context, R.string.no_connection_notification);
                }
                break;
        }
    }

    private void showBleSetDialog() {

        String msg = "暂不支持";

        if (mBleDeviceTools.get_call_ble_name() != null && !mBleDeviceTools.get_call_ble_name().equals("")
                && mBleDeviceTools.get_call_ble_mac() != null && !mBleDeviceTools.get_call_ble_mac().equals("")
        ) {
            msg = getString(R.string.my_mail_list_dialog_count)
                    + "\n" + mBleDeviceTools.get_call_ble_name()
                    + "\n" + mBleDeviceTools.get_call_ble_mac();

        }

        new android.app.AlertDialog.Builder(context)
                .setTitle(getString(R.string.dialog_prompt))//设置对话框标题
                .setMessage(msg
                )//设置显示的内容
                .setPositiveButton(getString(R.string.setting_dialog_setting), new DialogInterface.OnClickListener() {//添加确定按钮
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));//直接跳转到设置界面
                    }
                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {//添加返回按钮

            @Override

            public void onClick(DialogInterface dialog, int which) {//响应事件
                // TODO Auto-generated method stub

            }

        }).show();//在按键响应事件中显示此对话框

    }
}
