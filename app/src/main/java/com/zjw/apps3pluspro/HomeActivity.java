package com.zjw.apps3pluspro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.adapter.HomePagerAdapter;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.bleservice.BleConstant;
import com.zjw.apps3pluspro.bleservice.BleService;
import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.bleservice.BtSerializeation;
import com.zjw.apps3pluspro.bleservice.MyNotificationsListenerService;
import com.zjw.apps3pluspro.bleservice.UpdateInfoService;
import com.zjw.apps3pluspro.broadcastreceiver.HomeWatcherReceiver;
import com.zjw.apps3pluspro.eventbus.AuthorizationStateEvent;
import com.zjw.apps3pluspro.eventbus.BlueToothStateEvent;
import com.zjw.apps3pluspro.eventbus.BluetoothAdapterStateEvent;
import com.zjw.apps3pluspro.eventbus.DataSyncCompleteEvent;
import com.zjw.apps3pluspro.eventbus.DeviceInfoEvent;
import com.zjw.apps3pluspro.eventbus.DeviceToAppSportStateEvent;
import com.zjw.apps3pluspro.eventbus.DialInfoCompleteEvent;
import com.zjw.apps3pluspro.eventbus.DismissAGpsUpdateDialogEvent;
import com.zjw.apps3pluspro.eventbus.GetDeviceProtoAGpsPrepareStatusSuccessEvent;
import com.zjw.apps3pluspro.eventbus.OffEcgSyncStateEvent;
import com.zjw.apps3pluspro.eventbus.SendOpenWeatherDataEvent;
import com.zjw.apps3pluspro.eventbus.ShowDialogEvent;
import com.zjw.apps3pluspro.eventbus.SyncTimeLoadingEvent;
import com.zjw.apps3pluspro.eventbus.SyncTimeOutEvent;
import com.zjw.apps3pluspro.eventbus.tools.EventTools;
import com.zjw.apps3pluspro.module.device.AGpsUpdateActivity;
import com.zjw.apps3pluspro.module.device.DeviceFragment;
import com.zjw.apps3pluspro.module.device.DeviceManager;
import com.zjw.apps3pluspro.module.device.ScanDeviceTypeActivity;
import com.zjw.apps3pluspro.module.device.dfu.BleDfuActivity;
import com.zjw.apps3pluspro.module.device.dfu.ProtobufActivity;
import com.zjw.apps3pluspro.module.device.dfurtk.RtkDfuActivity;
import com.zjw.apps3pluspro.module.device.entity.DeviceModel;
import com.zjw.apps3pluspro.module.device.weather.WeatherBean;
import com.zjw.apps3pluspro.module.device.weather.openweather.WeatherManager;
import com.zjw.apps3pluspro.module.friend.FriendRankFragment;
import com.zjw.apps3pluspro.module.home.DataFragment;
import com.zjw.apps3pluspro.module.home.sport.DeviceSportManager;
import com.zjw.apps3pluspro.module.mine.MeFragment;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.AppVersionBean;
import com.zjw.apps3pluspro.network.javabean.DeviceBean;
import com.zjw.apps3pluspro.network.okhttp.MyOkHttpClient;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.dbmanager.SportModleInfoUtils;
import com.zjw.apps3pluspro.sql.entity.SportModleInfo;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.AuthorityManagement;
import com.zjw.apps3pluspro.utils.BluetoothUtil;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.DialogUtils;
import com.zjw.apps3pluspro.utils.GoogleFitManager;
import com.zjw.apps3pluspro.utils.GpsSportManager;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.SimulationUtils;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.view.pager.LazyViewPager;
import com.zjw.apps3pluspro.view.pager.MyViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * 主页home界面
 */
public class HomeActivity extends BaseActivity {
    public static Activity homeActivity;
    public static boolean isFirstOnCreate = false;

    private final String TAG = HomeActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    private MyActivityManager manager;

    public static final int BleStateResult = 4;

    //暂不使用
    public static boolean isHomeSyncTime = false;

    private boolean UserIsOpen = true;

    private ImageView bootom_menu_img1, bootom_menu_img2, bootom_menu_img3, bootom_menu_img4;
    private TextView bootom_menu_text1, bootom_menu_text2, bootom_menu_text3, bootom_menu_text4, tvSyncData;

    private LinearLayout main_off_line_sync_view;
    private RadioGroup main_radio;
    private MyViewPager home_viewpager;

//    public static BleService mService;

    private Handler mBleHandler;

    private int PresentationTag = 0;

    //延迟连接
    private int PostDelayTime = 1000;


    //加载圈
    private WaitDialog waitDialog;

    private boolean is_device_update_one = true;
    public static boolean is_device_update_show_dialog = true;
    private UpdateInfoService updateInfoService;

    private int connectCountNum = -1;
    private TextView tvConnectCount;
    @BindView(R.id.tvProgress)
    TextView tvProgress;


    private View view1, view2, view3, view4;

    @Override
    protected int setLayoutId() {
        isTextDark = false;
        return R.layout.activity_home;
    }

    RadioButton radio_today, rbDevice, radio_care, rodio_mine;

    @Override
    protected void initViews() {
        super.initViews();

        main_off_line_sync_view = (LinearLayout) findViewById(R.id.main_off_line_sync_view);
        bootom_menu_img1 = (ImageView) findViewById(R.id.bootom_menu_img1);
        bootom_menu_img2 = (ImageView) findViewById(R.id.bootom_menu_img2);
        bootom_menu_img3 = (ImageView) findViewById(R.id.bootom_menu_img3);
        bootom_menu_img4 = (ImageView) findViewById(R.id.bootom_menu_img4);

        bootom_menu_text1 = (TextView) findViewById(R.id.bootom_menu_text1);
        bootom_menu_text2 = (TextView) findViewById(R.id.bootom_menu_text2);
        bootom_menu_text3 = (TextView) findViewById(R.id.bootom_menu_text3);
        bootom_menu_text4 = (TextView) findViewById(R.id.bootom_menu_text4);

        radio_today = findViewById(R.id.radio_today);
        rbDevice = findViewById(R.id.rbDevice);
        radio_care = findViewById(R.id.radio_care);
        rodio_mine = findViewById(R.id.rodio_mine);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);

        tvSyncData = (TextView) findViewById(R.id.tvSyncData);
        main_radio = (RadioGroup) findViewById(R.id.main_radio);

        main_radio.setOnCheckedChangeListener((group, checkedId) -> {

            switch (checkedId) {
                // 今日
                case R.id.radio_today:
                    lastIndex = 1;
                    home_viewpager.setCurrentItem(0, false);
                    updateUi(0);
                    break;
                // 好友
                case R.id.radio_care:
                    lastIndex = 2;
                    home_viewpager.setCurrentItem(2, false);
                    updateUi(1);
                    break;
                // 设备
                case R.id.rbDevice:
                    if (JavaUtil.checkIsNull(mBleDeviceTools.get_ble_mac())) {
                        startActivity(new Intent(homeActivity, ScanDeviceTypeActivity.class));
                        radio_today.setChecked(false);
                        rbDevice.setChecked(false);
                        radio_care.setChecked(false);
                        rodio_mine.setChecked(false);

                        switch (lastIndex) {
                            case 1:
                                radio_today.setChecked(true);
                                break;
                            case 2:
                                radio_care.setChecked(true);
                                break;
                            case 3:
                                break;
                            case 4:
                                rodio_mine.setChecked(true);
                                break;
                        }
                    } else {
                        lastIndex = 3;
                        home_viewpager.setCurrentItem(1, false);
                        updateUi(2);
                    }
                    break;
//                    //数据
//                    case R.id.radio_data:
//                        if (PresentationTag == 0) {
//                            toDay();
//                        } else {
//                            toWeek();
//                        }
//                        break;

                // 我
                case R.id.rodio_mine:
                    lastIndex = 4;
                    home_viewpager.setCurrentItem(3, false);
                    updateUi(3);
                    break;
            }
        });


        findViewById(R.id.get_error_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceErrorLog();
            }
        });

        findViewById(R.id.set_error_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDeviceErrorLog();
            }
        });

        findViewById(R.id.simulationData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimulationUtils.simulationData();
            }
        });
        findViewById(R.id.btGetSport).setOnClickListener(v -> {

        });


        findViewById(R.id.queryData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryData();
            }
        });

        findViewById(R.id.updateData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadService();
            }
        });

//        tvConnectCount = (TextView) findViewById(R.id.tvConnectCount);
//
//        findViewById(R.id.btnClearConnectCount).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                connectCountNum = 0;
//                tvConnectCount.setText("连接次数统计：" + connectCountNum);
//
//            }
//        });
    }

    int lastIndex = 1;

    @Override
    protected void initDatas() {
        super.initDatas();
        homeActivity = this;
        mContext = this;
        isFirstOnCreate = true;
        registerHomeKeyReceiver(this);
        SysUtils.logContentE(TAG, "onCreate");
        SysUtils.logAppRunning(TAG, "onCreate  app version = " + MyUtils.getAppInfo());
        initBleData();
        EventTools.SafeRegisterEventBus(this);
        waitDialog = new WaitDialog(mContext);
        mBleHandler = new Handler();
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        initBroadcast();
        initAdpter();
        updateUi(0);
        initAllAuthority();
        requestAppVersion();
        getLocation();

        SysUtils.makeRootDirectory(Constants.ERROR_DATA_IMG);

        if (!MyNotificationsListenerService.isEnabled(mContext)) {
            String msg = context.getString(R.string.allow_notification_authority_tip_left)
                    + context.getString(R.string.app_name)
                    + context.getString(R.string.allow_notification_authority_tip_right);
            DialogUtils.BaseDialog(mContext, context.getString(R.string.dialog_prompt), msg, context.getDrawable(R.drawable.black_corner_bg), new DialogUtils.DialogClickListener() {
                @Override
                public void OnOK() {
                    MyNotificationsListenerService.openNotificationAccess(context);
                }

                @Override
                public void OnCancel() {

                }
            });
        }
        initGooglefit();
        getConnectStatus();

        DeviceSportManager.Companion.getInstance().getIsUploadMoreSport(1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DeviceSportManager.Companion.getInstance().getIsUploadMoreSport(2);
            }
        }, 1000);

        updateOldSportData();
        tryConnectDevice();

        if (JavaUtil.checkIsNull(mBleDeviceTools.get_ble_mac())) {
            startActivity(new Intent(this, ScanDeviceTypeActivity.class));
        } else {
            BluetoothUtil.enableBluetooth(HomeActivity.this, BleStateResult);
        }
    }

    private SportModleInfoUtils mSportModleInfoUtils = BaseApplication.getSportModleInfoUtils();

    private void updateOldSportData() {
        try {
            List<SportModleInfo> list = mSportModleInfoUtils.queryOldSportData();
            mSportModleInfoUtils.updateOldSportData(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        SysUtils.logContentE(TAG, "onResume");

        if (mHomeKeyReceiver.isGoHome) {
            uploadRunningInfo();
            mHomeKeyReceiver.isGoHome = false;
        }

        if (lastIndex == 3) {
            if (JavaUtil.checkIsNull(mBleDeviceTools.get_ble_mac())) {
                rbDevice.setChecked(false);
                radio_care.setChecked(false);
                rodio_mine.setChecked(false);

                radio_today.setChecked(true);
            }
        }

        try {
            if (aGpsDialog != null && aGpsDialog.isShowing()) {
                aGpsDialog.dismiss();
                aGpsDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        SysUtils.logContentE(TAG, "onStop");
        super.onStop();
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SysUtils.logContentE(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        waitDialog.dismiss();
        unregisterHomeKeyReceiver(this);
        SysUtils.logContentE(TAG, "onDestroy");
        EventTools.SafeUnregisterEventBus(this);

        if (mBleHandler != null) {
            mBleHandler.removeCallbacksAndMessages(null);
        }


//        try {
//            if (mService != null && isBind) {
//                unbindService(mServiceConnection);
//                isBind = false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if (broadcastReceiver != null) {
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {
            }
        }


        super.onDestroy();
    }


    //跳转到-数据-天界面
    public void toDay() {
        home_viewpager.setCurrentItem(1, false);
        updateUi(1);
        PresentationTag = 0;
    }

    //跳转到-数据-周界面
    public void toWeek() {
        home_viewpager.setCurrentItem(2, false);
        updateUi(1);
        PresentationTag = 1;
    }

    void initBleData() {
        BleService.syncState = false;
        is_device_update_show_dialog = true;
    }

    void initAdpter() {

        home_viewpager = (MyViewPager) findViewById(R.id.home_viewpager);

        List<Fragment> fragLists = new ArrayList<Fragment>();
        fragLists.add(new DataFragment());
        fragLists.add(new DeviceFragment());
        fragLists.add(new FriendRankFragment());
        fragLists.add(new MeFragment());

        HomePagerAdapter mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), fragLists);

        home_viewpager.setAdapter(mHomePagerAdapter);
        home_viewpager.setOnPageChangeListener(new LazyViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                MyLog.i(TAG, "onPageScrolled");
            }

            @Override
            public void onPageSelected(int position) {
                MyLog.i(TAG, "onPageSelected");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                MyLog.i(TAG, "onPageScrollStateChanged");
            }
        });

        home_viewpager.setCurrentItem(0, false);
        main_radio.check(R.id.radio_today);
    }

//    /**
//     * 打开服务
//     */
//    private boolean isBind = false;
//
//    public void startBleService() {
//        if (mService == null) {
//            SysUtils.logContentE(TAG, "startBleService()");
//            Intent serviceIntent = new Intent(this, BleService.class);
//            isBind = bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
//        }
//    }
//
//    private final ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(final ComponentName className, final IBinder rawBinder) {
//            isBind = true;
//            mService = ((BleService.ServiceBinder) rawBinder).getService();
//            MyLog.i(TAG, "蓝牙状态 connectBleDevcie() 002");
//            connectBleDevcie();
//        }
//
//        @Override
//        public void onServiceDisconnected(final ComponentName classname) {
//            mService = null;
//        }
//    };


    /**
     * 初始化广播
     */
    private void initBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastTools.ACTION_RESULT_BLE_DEVICE_ACTION);
        filter.addAction(BroadcastTools.ACTION_GATT_CONNECTED);
        filter.addAction(BroadcastTools.ACTION_GATT_DISCONNECTED);
        filter.addAction(BroadcastTools.ACTION_GATT_CONNECTING);
        filter.addAction(BroadcastTools.ACTION_GATT_CONNECT_TIME_OUT);
        filter.addAction(BroadcastTools.ACTION_GATT_CONNECT_DISCOVER_SERVICES);
        filter.addAction(BroadcastTools.ACTION_GATT_CONNECT_BIND_SUCCESS);
        filter.addAction(BroadcastTools.ACTION_GATT_CONNECT_BIND_ERROR);

        filter.addAction(BroadcastTools.ACTION_GATT_SYNC_COMPLETE);
        filter.addAction(BroadcastTools.ACTION_GATT_OFF_LINE_ECG_START);
        filter.addAction(BroadcastTools.ACTION_GATT_OFF_LINE_ECG_END);
        filter.addAction(BroadcastTools.ACTION_GATT_OFF_LINE_ECG_NO_OK);
        filter.addAction(BroadcastTools.ACTION_GATT_CALL_DEVICE_INFO);
        filter.addAction(BroadcastTools.ACTION_BLUE_STATE_CHANGED);
        filter.addAction(BroadcastTools.TAG_CLOSE_PHOTO_ACTION);
        filter.addAction(BroadcastTools.ACTION_GATT_DEVICE_COMPLETE);
        filter.addAction(BroadcastTools.ACTION_SYNC_LOADING);
        filter.addAction(BroadcastTools.ACTION_SYNC_TIME_OUT);
        filter.addAction(BroadcastTools.ACTION_GATT_DIAL_COMPLETE);

        filter.addAction(BroadcastTools.ACTION_CMD_APP_START);
        filter.addAction(BroadcastTools.ACTION_CMD_DEVICE_START);
        filter.addAction(BroadcastTools.ACTION_CMD_APP_CONFIRM);
        filter.addAction(BroadcastTools.ACTION_CMD_DEVICE_CONFIRM);
        filter.addAction(BroadcastTools.ACTION_CMD_GET_SPORT);
        filter.addAction(BroadcastTools.ACTION_CMD_DEVICE_TRANSMISSION_DATA);

        filter.addAction(BroadcastTools.ACTION_GATT_DEVICE_TO_APP_SPORT_STATE);

        filter.setPriority(1000);
        registerReceiver(broadcastReceiver, filter);
    }

    /**
     * 广播监听
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @SuppressWarnings({"unused", "unused"})
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                switch (intent.getAction()) {

                    //搜索回调连接设备
                    case BroadcastTools.ACTION_RESULT_BLE_DEVICE_ACTION:
                        MyLog.i(TAG, "choose device to connnecting");
                        //                    waitDialog.show(context.getString(R.string.loading3));
                        DeviceModel btDevice = intent.getParcelableExtra(BroadcastTools.BLE_DEVICE);
                        if (btDevice != null) {
                            //                        mBleDeviceTools.set_ble_mac(btDevice.address);
                            //                        mBleDeviceTools.set_ble_name(btDevice.name);
                            bindDeviceConnect(btDevice.address);
                        }
                        break;

                    case BroadcastTools.ACTION_GATT_CONNECT_DISCOVER_SERVICES:
                        EventBus.getDefault().post(new BlueToothStateEvent(BleConstant.STATE_DISCOVER_SERVICES));
                        break;
                    case BroadcastTools.ACTION_GATT_CONNECT_BIND_SUCCESS:
                        EventBus.getDefault().post(new BlueToothStateEvent(BleConstant.STATE_BIND_SUCCESS));
                        break;
                    case BroadcastTools.ACTION_GATT_CONNECT_BIND_ERROR:
                        EventBus.getDefault().post(new BlueToothStateEvent(BleConstant.STATE_BIND_ERROR));
                        break;
                    //已连接
                    case BroadcastTools.ACTION_GATT_CONNECTED:
                        isFirstShowAGpsDialog = true;
                        mConnectionState = BleConstant.STATE_CONNECTED;
                        EventBus.getDefault().post(new BlueToothStateEvent(BleConstant.STATE_CONNECTED));
                        MyLog.i(TAG, "已连接");
                        if (waitDialog != null) {
                            waitDialog.close();
                        }
                        //                    connectCountNum++;
                        //                    tvConnectCount.setText("连接次数统计：" + connectCountNum);
                        break;
                    //已断开
                    case BroadcastTools.ACTION_GATT_DISCONNECTED:
                        aGpsDialog = null;
                        isFirstShowAGpsDialog = false;
                        GpsSportManager.getInstance().stopGps(homeActivity);
                        mConnectionState = BleConstant.STATE_DISCONNECTED;
                        BleService.syncState = false;
                        EventBus.getDefault().post(new BlueToothStateEvent(BleConstant.STATE_DISCONNECTED));
                        break;
                    case BroadcastTools.ACTION_GATT_CONNECTING:
                        mConnectionState = BleConstant.STATE_CONNECTING;
                        BleService.syncState = false;
                        MyLog.i(TAG, "连接中");
                        EventBus.getDefault().post(new BlueToothStateEvent(BleConstant.STATE_CONNECTING));
                        break;
                    case BroadcastTools.ACTION_GATT_CONNECT_TIME_OUT:
                        mConnectionState = BleConstant.STATE_CONNECTED_TIMEOUT;
                        BleService.syncState = false;
                        EventBus.getDefault().post(new BlueToothStateEvent(BleConstant.STATE_CONNECTED_TIMEOUT));
                        MyLog.i(TAG, "超时");
                        //同步完成
                    case BroadcastTools.ACTION_GATT_SYNC_COMPLETE:
                        BleService.syncState = false;
                        EventBus.getDefault().post(new DataSyncCompleteEvent());
                        isHomeSyncTime = false;
                        if (is_device_update_one) {
                            is_device_update_one = false;
                            String version_name = BleTools.getDeviceVersionName(mBleDeviceTools);
                            if (!JavaUtil.checkIsNull(version_name)) {
                                getNetDeviceVersion(mBleDeviceTools.get_ble_device_type(), mBleDeviceTools.get_ble_device_version(), mBleDeviceTools.get_device_platform_type());
                            }
                        }
                        syncWeather();
                        break;
                    case BroadcastTools.ACTION_GATT_DEVICE_COMPLETE:
                        EventBus.getDefault().post(new DeviceInfoEvent());
                        break;
                    case BroadcastTools.ACTION_GATT_OFF_LINE_ECG_START:
                        MyLog.i(TAG, "离线心电数据 = 开始");
                        if (main_off_line_sync_view != null && main_off_line_sync_view.getVisibility() != View.VISIBLE) {
                            main_off_line_sync_view.setVisibility(View.VISIBLE);
                        }
                        EventBus.getDefault().post(new OffEcgSyncStateEvent(OffEcgSyncStateEvent.OFF_ECG_SYNC_STATE_START));
                        break;

                    case BroadcastTools.ACTION_GATT_OFF_LINE_ECG_END:
                        MyLog.i(TAG, "离线心电数据 = 结束");
                        if (main_off_line_sync_view != null) {
                            main_off_line_sync_view.setVisibility(View.GONE);
                        }
                        EventBus.getDefault().post(new OffEcgSyncStateEvent(OffEcgSyncStateEvent.OFF_ECG_SYNC_STATE_END));
                        break;

                    case BroadcastTools.ACTION_GATT_OFF_LINE_ECG_NO_OK:
                        MyLog.i(TAG, "离线心电数据 = 心电质量差");
                        AppUtils.showToast(mContext, R.string.off_ecg_quality_no_ok);
                        break;


                    case BroadcastTools.ACTION_SYNC_LOADING:
                        EventBus.getDefault().post(new SyncTimeLoadingEvent());
                        break;

                    case BroadcastTools.ACTION_SYNC_TIME_OUT:
                        EventBus.getDefault().post(new SyncTimeOutEvent());
                        break;


                    //通话设备信息回调
                    case BroadcastTools.ACTION_GATT_CALL_DEVICE_INFO:
                        MyLog.i(TAG, "通话设备信息回调");

                        String call_ble_mac = mBleDeviceTools.get_call_ble_mac();
                        String call_ble_name = mBleDeviceTools.get_call_ble_name();

                        MyLog.i(TAG, "通话设备信息回调 call_ble_mac = " + call_ble_mac);
                        MyLog.i(TAG, "通话设备信息回调 call_ble_name = " + call_ble_name);

                        //如果没配对
                        if (!MyUtils.checkBlePairMac(mBleDeviceTools.get_call_ble_mac())) {
                            if (DeviceIsConnect()) {
                                try {
                                    createBond();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;

                    case BroadcastTools.ACTION_GATT_DIAL_COMPLETE:
                        EventBus.getDefault().post(new DialInfoCompleteEvent());
                        break;

                    case BroadcastTools.ACTION_BLUE_STATE_CHANGED:
                        int state = intent.getIntExtra("extra_data", 0);
                        EventBus.getDefault().post(new BluetoothAdapterStateEvent(state));
                        break;

                    case BroadcastTools.TAG_CLOSE_PHOTO_ACTION:
                        closePhoto();
                        break;
                    case BroadcastTools.ACTION_GATT_DEVICE_TO_APP_SPORT_STATE:
                        int sportState = intent.getIntExtra(BroadcastTools.ACTION_GATT_DEVICE_TO_APP_SPORT_TAG, -1);
                        EventBus.getDefault().post(new DeviceToAppSportStateEvent(sportState));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private Dialog aGpsDialog;
    private boolean isFirstShowAGpsDialog = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getDeviceProtoAGpsPrepareStatusSuccessEvent(GetDeviceProtoAGpsPrepareStatusSuccessEvent event) {
        if (event.needGpsInfo && isFirstShowAGpsDialog) {
            SysUtils.logAppRunning(TAG, "GetDeviceProtoAGpsPrepareStatusSuccessEvent = " + true);
            isFirstShowAGpsDialog = false;
            // download file
            aGpsDialog = DialogUtils.BaseDialog(homeActivity,
                    context.getResources().getString(R.string.dialog_prompt),
                    context.getResources().getString(R.string.update_AGPS_date),
                    context.getDrawable(R.drawable.black_corner_bg),
                    new DialogUtils.DialogClickListener() {
                        @Override
                        public void OnOK() {
                            startActivity(new Intent(HomeActivity.this, AGpsUpdateActivity.class));
                            aGpsDialog = null;
                        }

                        @Override
                        public void OnCancel() {
                            aGpsDialog = null;
                        }
                    }
            );
            aGpsDialog.setCancelable(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dismissAGpsUpdateDialogEvent(DismissAGpsUpdateDialogEvent event) {
        try {
            if (aGpsDialog != null && aGpsDialog.isShowing()) {
                aGpsDialog.dismiss();
                aGpsDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateUi(int item) {

        bootom_menu_img1.setBackgroundResource(R.mipmap.my_bootom_motion_off);
        bootom_menu_img2.setBackgroundResource(R.mipmap.my_bootom_week_off);
        bootom_menu_img3.setBackgroundResource(R.mipmap.my_bootom_care_off);
        bootom_menu_img4.setBackgroundResource(R.mipmap.my_bootom_my_off);

        bootom_menu_text1.setTextColor(this.getResources().getColor(R.color.bootom_color_off));
        bootom_menu_text2.setTextColor(this.getResources().getColor(R.color.bootom_color_off));
        bootom_menu_text3.setTextColor(this.getResources().getColor(R.color.bootom_color_off));
        bootom_menu_text4.setTextColor(this.getResources().getColor(R.color.bootom_color_off));

        view1.setVisibility(View.INVISIBLE);
        view2.setVisibility(View.INVISIBLE);
        view3.setVisibility(View.INVISIBLE);
        view4.setVisibility(View.INVISIBLE);

        switch (item) {
            case 0:
                view1.setVisibility(View.VISIBLE);
                bootom_menu_img1.setBackgroundResource(R.mipmap.my_bootom_motion_on);
                bootom_menu_text1.setTextColor(this.getResources().getColor(R.color.bootom_color_on));
                break;
            case 1:
                view2.setVisibility(View.VISIBLE);
                bootom_menu_img3.setBackgroundResource(R.mipmap.my_bootom_care_on);
                bootom_menu_text3.setTextColor(this.getResources().getColor(R.color.bootom_color_on));
                break;
            case 2:
                view3.setVisibility(View.VISIBLE);
                bootom_menu_img2.setBackgroundResource(R.mipmap.my_bootom_week_on);
                bootom_menu_text2.setTextColor(this.getResources().getColor(R.color.bootom_color_on));
                break;
            case 3:
                view4.setVisibility(View.VISIBLE);
                bootom_menu_img4.setBackgroundResource(R.mipmap.my_bootom_my_on);
                bootom_menu_text4.setTextColor(this.getResources().getColor(R.color.bootom_color_on));
                break;
            default:
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GoogleFitManager.getInstance().handleSignInResult(requestCode, resultCode, data, this);

        if (updateInfoService != null) {
            updateInfoService.handleActivityResult(requestCode);
        }

        switch (requestCode) {
            case BleStateResult:

                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    return;
                }
                if (mBluetoothAdapter.isEnabled()) {
                    UserIsOpen = true;
                } else {
                    UserIsOpen = false;
                    MyLog.i(TAG, "回调 蓝牙没打开");
                }
                break;
        }
    }

    /**
     * 监听返回键
     */


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    void initAllAuthority() {
        if (AuthorityManagement.verifyAll(this)) {
            MyLog.i(TAG, "获取所有权限 已授权");
        } else {
            MyLog.i(TAG, "获取所有权限 未授权");
        }
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
//            AuthorityManagement.permissionSTORAGE(this, 0);
//            AuthorityManagement.permissionLOCATION(this);
//        } else {
//            if (AuthorityManagement.verifyAll(this)) {
//                MyLog.i(TAG, "获取所有权限 已授权");
//            } else {
//                MyLog.i(TAG, "获取所有权限 未授权");
//            }
//        }

        MyLog.i(TAG, "创建文件夹");
        SysUtils.makeRootDirectory(Constants.HOME_DIR);
        SysUtils.makeRootDirectory(Constants.P_LOG_PATH);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (updateInfoService != null) {
            updateInfoService.handlePermissionsResult(requestCode, grantResults);
        }
        switch (requestCode) {
            case AuthorityManagement.REQUEST_EXTERNAL_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.i(TAG, "获取定位权限 回调允许");
                } else {
                    MyLog.i(TAG, "获取定位权限 回调拒绝");
                    SysUtils.logAppRunning(TAG, "获取定位权限 回调拒绝");
                    showSettingDialog(getString(R.string.setting_dialog_location));
                }
            }
            break;
            case AuthorityManagement.REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.i(TAG, "SD卡权限 回调允许");
                } else {
                    MyLog.i(TAG, "SD卡权限 回调拒绝");
                    SysUtils.logAppRunning(TAG, "SD卡权限 回调拒绝");
                    showSettingDialog(getString(R.string.setting_dialog_storage));
                }
            }
            break;
            case AuthorityManagement.REQUEST_EXTERNAL_CALL_CAMERA:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    //成功，开启摄像头
//                    MyLog.i(TAG, "拍照权限 回调允许");
//                    goToCameraActivity();
//                } else {
//                    MyLog.i(TAG, "拍照权限 回调拒绝");
//                    showSettingDialog(getString(R.string.setting_dialog_call_camera));
//                    //授权失败
//                }
                break;
            case AuthorityManagement.REQUEST_EXTERNAL_MAIL_LIST:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    //成功，开启摄像头
//                    MyLog.i(TAG, "读取联系人 回调允许");
//                    PhoneUtil.contacNameByNumber(this, "123456");
//                } else {
//                    MyLog.i(TAG, "读取联系人 回调拒绝");
//                    //授权失败
//                }
                break;
            case AuthorityManagement.REQUEST_EXTERNAL_PHONE_STATE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    //成功，开启摄像头
//                    MyLog.i(TAG, "读取来电状态 回调允许");
//                } else {
//                    MyLog.i(TAG, "读取来电状态 回调拒绝");
//                    //授权失败
//                }
                break;
            case AuthorityManagement.REQUEST_EXTERNAL_ALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.i(TAG, "所有权限 回调允许");
                } else {
                    MyLog.i(TAG, "所有权限 回调拒绝");
//                    showSettingDialog(getString(R.string.setting_dialog_storage));
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    void showSettingDialog(String content) {
        DialogUtils.BaseDialog(context,
                context.getResources().getString(R.string.dialog_prompt),
                content,
                context.getDrawable(R.drawable.black_corner_bg),
                new DialogUtils.DialogClickListener() {
                    @Override
                    public void OnOK() {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }

                    @Override
                    public void OnCancel() {
                    }
                }
                , getString(R.string.setting_dialog_setting));
    }


    void queryData() {
//        HeartInfoUtils mHeartInfoUtils = BaseApplication.getHeartInfoUtils();
////        List<HeartInfo> heartinfo_list = mHeartInfoUtils.MyQueryToSyncAll(BaseApplication.getUserId());
//        List<HeartInfo> heartinfo_list = mHeartInfoUtils.MyQueryToData(BaseApplication.getUserId());
//
//        for (int i = 0; i < heartinfo_list.size(); i++) {
//            MyLog.i(TAG, "查询数据 heartinfo_list = i " + i + " heartinfo_list = " + heartinfo_list.get(i).toString());
//        }
////        MyLog.i(TAG, "查询数据 heartinfo_list = " + heartinfo_list.toString());

    }

    void uploadService() {
//        HeartInfoUtils mHeartInfoUtils = BaseApplication.getHeartInfoUtils();
//        requestServerTools.uploadHeartData(getApplicationContext(), mHeartInfoUtils);
    }

    /**
     * 获取服务器版本号
     */
    private void getNetDeviceVersion(int model, int c_upgrade_version, int device_platform_type) {
        // TODO Auto-generated method stub

        //测试数据
//        model = 1000;
//        c_upgrade_version = 10;
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
                                if (mDeviceBean.getData().getMustUpdate() == 1) {
                                    MyLog.i(TAG, "请求接口-获取设备版本号 强制升级");
                                    if (is_device_update_show_dialog) {
                                        MyLog.i(TAG, "请求接口-获取设备版本号 弹出升级提示框");
                                        is_device_update_show_dialog = false;
                                        showDeviceUpdateDialog();
                                    } else {
                                        MyLog.i(TAG, "请求接口-获取设备版本号 不弹出升级提示框");
                                    }

                                } else {

                                }

                            } else if (mDeviceBean.isOk() == 2) {

                            } else {

                            }
                            //请求失败
                        } else {

                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub

                        MyLog.i(TAG, "请求接口-获取设备版本号 请求失败 = message = " + arg0.getMessage());
//                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                        return;
                    }
                });
    }

    public static final int REQUEST_TIME = 0x00000002; // 心率自动测量间隔时间返回页面的result
    public static final int REQUEST_DFU = 0x00000003; // 心率自动测量间隔时间返回页面的result

    /**
     * 升级对话框
     */
    private void showDeviceUpdateDialog() {
        DialogUtils.BaseDialog(context,
                context.getResources().getString(R.string.dialog_prompt),
                context.getResources().getString(R.string.force_upgrade_is_update_title),
                context.getDrawable(R.drawable.black_corner_bg),
                new DialogUtils.DialogClickListener() {
                    @Override
                    public void OnOK() {
                        if (mBleDeviceTools.getIsSupportProtobuf() && mBleDeviceTools.getDeviceUpdateType()) {
                            if (mBleDeviceTools.getIsSupportGetDeviceProtoStatus()) {
                                Intent intent = new Intent(mContext, ProtobufActivity.class);
                                startActivity(intent);
                            } else {
                                if (mBleDeviceTools.get_ble_device_power() >= 25) {
                                    Intent intent = new Intent(mContext, ProtobufActivity.class);
                                    startActivity(intent);
                                } else {
                                    AppUtils.showToast(mContext, R.string.dfu_error_low_power);
                                }
                            }
                        } else {
                            if (mBleDeviceTools.get_ble_device_power() >= 25) {
                                if (mBleDeviceTools.get_device_platform_type() == 0) {
                                    Intent intent = new Intent(mContext, BleDfuActivity.class);
                                    startActivityForResult(intent, REQUEST_DFU);//此处的requestCode应与下面结果处理函中调用的requestCode一致
                                } else if (mBleDeviceTools.get_device_platform_type() == 1) {
                                    Intent intent = new Intent(mContext, RtkDfuActivity.class);
                                    startActivityForResult(intent, REQUEST_DFU);//此处的requestCode应与下面结果处理函中调用的requestCode一致
                                }
                            } else {
                                AppUtils.showToast(mContext, R.string.dfu_error_low_power);
                            }
                        }
                    }

                    @Override
                    public void OnCancel() {
                    }
                }
        );
    }


    private void initGooglefit() {
        if (!mBleDeviceTools.getIsOpenGooglefit()) {
            MyLog.e(TAG, "google fit is close");
            return;
        }
        GoogleFitManager.getInstance().OpenGoogleFit(this);
    }

    private void requestAppVersion() {
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
                                    SysUtils.logAppRunning(TAG, "请求接口-获取APP版本号 需要升级");
                                    GetAppVersionResultDataParsing(mAppVerionBean.getData());
                                } else {
                                    MyLog.i(TAG, "请求接口-获取APP版本号 不需要升级");
                                }
                            } else if (mAppVerionBean.isgetAPPVersionSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取APP版本号 失败");
                            } else {
                                MyLog.i(TAG, "请求接口-获取APP版本号 请求异常(1)");
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
                    }
                });
    }

    //=====APP升级相关=====
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    private String AppVersion, AppDownloadAddress;

    private void GetAppVersionResultDataParsing(AppVersionBean.DataBean mDataBean) {

        AppVersion = mDataBean.getAppVersion();
        AppDownloadAddress = mDataBean.getAppDownloadUrl();

        MyLog.i(TAG, "获取APP版本号 getAppVersion = " + AppVersion);
        MyLog.i(TAG, "获取APP版本号 getAppDownloadUrl = " + AppDownloadAddress);

        if (mDataBean.getMustUpdate() == 1) {
            ForceUpdate();
        } else {
            long quest_time = mUserSetTools.get_update_app_request_time();
            if (!MyTime.checkUploadAppLongTime(quest_time)) {
                MyLog.i(TAG, "time short ,no update");
            } else {
                ForceUpdate();
            }
        }
    }

    public final int UPDATE = 2;

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
                    try {
                        if (AuthorityManagement.verifyStoragePermissions(HomeActivity.this)) {
                            showUpdateDialog();
                            MyLog.i(TAG, "SD卡权限 已获取");
                        } else {
                            MyLog.i(TAG, "SD卡权限 未获取");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    private Dialog updateAPPDialog;
    private Dialog progressDialog;

    private void showUpdateDialog() {
        LinearLayout update_dialog_bg;
        TextView tv_update_title, tv_update_msg;
        Button btn_update_cancel, btn_update_ok;
        Display display;
        View view = LayoutInflater.from(mContext).inflate(R.layout.update_dialog, null);
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
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
                    break;
                case R.id.btn_update_cancel:
                    mUserSetTools.set_update_app_request_time(MyTime.getMyLongTime());
                    updateAPPDialog.dismiss();
                    break;

                default:
                    MyActivityManager.getInstance().finishAllActivity();
                    break;
            }
        }
    }

    void downFile(final String url) {
        progressDialog = DialogUtils.BaseDialogShowProgress(context,
                context.getResources().getString(R.string.download_title),
                context.getResources().getString(R.string.loading0),
                context.getDrawable(R.drawable.black_corner_bg)
        );
        updateInfoService = new UpdateInfoService(mContext);
        updateInfoService.downLoadFile(url, progressDialog, handler1);
    }


    public static int mConnectionState = BleConstant.STATE_DISCONNECTED;

    public boolean DeviceIsConnect() {
        if (mConnectionState == BleConstant.STATE_CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    //是否需要连接，如果不是已连接且不是连接中。则需要连接
    public void tryConnectDevice() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SysUtils.logContentI(TAG, "mConnectionState = " + mConnectionState);
                if (mConnectionState != BleConstant.STATE_CONNECTED && mConnectionState != BleConstant.STATE_CONNECTING) {
                    reconnectDevice();
                }
            }
        }, 500);
    }

    public static int getBlueToothStatus() {
        return mConnectionState;
    }

    public static boolean ISBlueToothConnect() {
        if (mConnectionState == BleConstant.STATE_CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void authorizationStateEvent(AuthorizationStateEvent event) {
        if (mUserSetTools.get_user_login()) {
            AppUtils.showToast(mContext, R.string.login_invalid);
        }
        disconnect();
        DeviceManager.getInstance().unBind(context, null);
        MyOkHttpClient.getInstance().quitApp(HomeActivity.this);
    }

    private LocationManager locationManager;
    private String locationProvider = null;
    public static double phoneLat = 0.0;
    public static double phoneLon = 0.0;

    private void stopLocation() {
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
            locationManager = null;
            locationListener = null;
        }
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //获取所有可用的位置提供器
            List<String> providers = locationManager.getProviders(true);

            if (providers.size() == 0) {
                uploadRunningInfo();
                return;
            }

            if (providers.contains(LocationManager.GPS_PROVIDER)) {
                locationProvider = LocationManager.GPS_PROVIDER;
            }

            if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                locationProvider = LocationManager.NETWORK_PROVIDER;
            }


            if (locationProvider == null) {
                uploadRunningInfo();
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //监视地理位置变化
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
                Location location = locationManager.getLastKnownLocation(locationProvider);
                if (location != null) {
                    //输入经纬度
                    phoneLat = location.getLatitude();
                    phoneLon = location.getLongitude();
                    stopLocation();

                }
            } else {
                //监视地理位置变化
                locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
                Location location = locationManager.getLastKnownLocation(locationProvider);
                if (location != null) {
                    //                Toast.makeText(this, location.getLatitude() + " " + location.getLongitude() + "", Toast.LENGTH_SHORT).show();
                    phoneLat = location.getLatitude();
                    phoneLon = location.getLongitude();
                    stopLocation();
                }
            }
            getAddress(phoneLat, phoneLon);
            uploadRunningInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadRunningInfo() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    RequestInfo mRequestInfo = RequestJson.uploadAppInfo(mContext);
                    Log.i(TAG, "uploadAppInfo=" + mRequestInfo.toString());
                    NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
                        @Override
                        public void onMySuccess(JSONObject result) {
                        }

                        @Override
                        public void onMyError(VolleyError arg0) {
                        }
                    });
                }
            }, 5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LocationListener locationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                //不为空,显示地理位置经纬度
                phoneLat = location.getLatitude();
                phoneLon = location.getLongitude();
                stopLocation();
            }
        }
    };

    public static String cityName = "";
    public static String province = "";
    public static String country = "";

    public static void getAddress(double latitude, double longitude) {
        List<Address> addList = null;
        Geocoder ge = new Geocoder(homeActivity, Locale.SIMPLIFIED_CHINESE);
        try {
            addList = ge.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addList != null && addList.size() > 0) {
            for (int i = 0; i < addList.size(); i++) {
                Address ad = addList.get(i);
                cityName = ad.getSubAdminArea();
                province = ad.getAdminArea();
                country = ad.getCountryName();
            }
        }
    }

    private HomeWatcherReceiver mHomeKeyReceiver = null;

    private void registerHomeKeyReceiver(Context context) {
        mHomeKeyReceiver = new HomeWatcherReceiver();
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.registerReceiver(mHomeKeyReceiver, homeFilter);
    }

    private void unregisterHomeKeyReceiver(Context context) {
        if (null != mHomeKeyReceiver) {
            context.unregisterReceiver(mHomeKeyReceiver);
        }
    }

    public void syncData() {
        if (ISBlueToothConnect()) {
            mBleDeviceTools.setLastUploadDataServiceTime(0);
            BleService.syncState = true;
            endSleepTag();
            syncTime();
        }
    }

    private void syncWeather() {
        if (mBleDeviceTools.get_is_weather() && mBleDeviceTools.getWeatherSwitch()) {
            if (System.currentTimeMillis() - mBleDeviceTools.getWeatherSyncTime() > 10 * 60 * 1000L) {
                if (mBleDeviceTools.getWeatherMode() == 3) {
                    if (mBleDeviceTools.getWeatherGps().isEmpty()) {
                    } else {
                        String[] gps = mBleDeviceTools.getWeatherGps().split(",");
                        if (gps.length > 1) {
                            WeatherManager.getInstance().getCurrentWeather(false,false, Double.parseDouble(gps[1]), Double.parseDouble(gps[0]), new WeatherManager.GetOpenWeatherListener() {
                                @Override
                                public void onSuccess() {
                                    EventBus.getDefault().post(new SendOpenWeatherDataEvent(0));
                                }

                                @Override
                                public void onFail() {

                                }
                            });
                        }
                    }
                } else {
                    if (mBleDeviceTools.getWeatherCity().isEmpty()) {
//                    GpsSportManager.getInstance().getLatLon(this, gpsInfo -> {
//                        GpsSportManager.getInstance().stopGps(this);
//                        requestWeather();
//                    });
                    } else {
                        requestWeather();
                    }
                }
            }
        }
    }

    private void requestWeather() {
//        GpsSportManager.getInstance().getWeatherCity(this, () -> {
        GpsSportManager.getInstance().getWeatherArea(this, () -> {
            ArrayList<WeatherBean> myWeatherModle = WeatherBean.getHisData(HomeActivity.this);
            if (myWeatherModle != null) {
                System.out.println("请求天气 = 历史 解析2 " + myWeatherModle.toString());

                byte[] t2 = WeatherBean.getWaeatherListData(myWeatherModle);
                System.out.println("请求天气  t2 = " + BleTools.printHexString(t2));

                float atmosphericPressure = 0;
                try {
                    atmosphericPressure = Float.parseFloat(myWeatherModle.get(0).getPressure());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                byte[] t3 = BtSerializeation.setWeather(atmosphericPressure, t2);
                System.out.println("请求天气  t3 = " + BleTools.printHexString(t3));

                sendData(t3);
            }
        });
//        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDialogEvent(ShowDialogEvent event) {
        switch (event.type) {
            case 0:
                AuthorityManagement.verifyLocation(this);
                break;
            case 1:
                DialogUtils.showSettingGps(this);
                break;
        }
    }

}
