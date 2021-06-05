package com.zjw.apps3pluspro.bleservice;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RemoteController;
import android.media.session.PlaybackState;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.bleservice.anaylsis.AnalysisProtoData;
import com.zjw.apps3pluspro.bleservice.anaylsis.FitnessTools;
import com.zjw.apps3pluspro.broadcastreceiver.PhoneReceiver;
import com.zjw.apps3pluspro.eventbus.DeviceNoSportEvent;
import com.zjw.apps3pluspro.eventbus.DeviceSportStatusEvent;
import com.zjw.apps3pluspro.eventbus.DeviceToAppSportStateEvent;
import com.zjw.apps3pluspro.eventbus.DeviceWatchFaceDeleteEvent;
import com.zjw.apps3pluspro.eventbus.DeviceWatchFaceListSyncEvent;
import com.zjw.apps3pluspro.eventbus.DeviceWatchFaceSetEvent;
import com.zjw.apps3pluspro.eventbus.GetDeviceProtoOtaPrepareStatusEvent;
import com.zjw.apps3pluspro.eventbus.GetDeviceProtoWatchFacePrepareStatusEvent;
import com.zjw.apps3pluspro.eventbus.GpsSportDeviceStartEvent;
import com.zjw.apps3pluspro.eventbus.LocationChangeEventBus;
import com.zjw.apps3pluspro.eventbus.PageDeviceSetEvent;
import com.zjw.apps3pluspro.eventbus.PageDeviceSyncEvent;
import com.zjw.apps3pluspro.eventbus.RefreshGpsInfoEvent;
import com.zjw.apps3pluspro.eventbus.SendOpenWeatherDataEvent;
import com.zjw.apps3pluspro.eventbus.SyncDeviceSportEvent;
import com.zjw.apps3pluspro.eventbus.UploadThemeStateEvent;
import com.zjw.apps3pluspro.eventbus.tools.EventTools;
import com.zjw.apps3pluspro.module.device.DeviceManager;
import com.zjw.apps3pluspro.module.home.ecg.EcgMeasureActivity;
import com.zjw.apps3pluspro.module.device.reminde.RemindeUtils;
import com.zjw.apps3pluspro.module.device.entity.MusicInfo;
import com.zjw.apps3pluspro.bleservice.protocol.BleBaseProtocol;
import com.zjw.apps3pluspro.bleservice.scan.ExtendedBluetoothDevice;
import com.zjw.apps3pluspro.bleservice.scan.MyBleScanState;
import com.zjw.apps3pluspro.bleservice.scan.NordicsemiBleScanner;
import com.zjw.apps3pluspro.bleservice.scan.SimpleScanCallback;
import com.zjw.apps3pluspro.module.home.sport.DeviceSportManager;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.dbmanager.ContinuitySpo2InfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.ContinuityTempInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.HealthInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.HeartInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.MeasureSpo2InfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.MeasureTempInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.MovementInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.SleepInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.SportModleInfoUtils;
import com.zjw.apps3pluspro.sql.entity.HealthInfo;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.BleCmdManager;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.FileUtil;
import com.zjw.apps3pluspro.utils.GpsSportManager;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MusicSyncManager;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.NotificationUtils;
import com.zjw.apps3pluspro.utils.PhoneUtil;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.ThemeManager;
import com.zjw.apps3pluspro.utils.location.ForegroundLocationService;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.utils.log.TraceErrorLog;
import com.zjw.ffitsdk.BleProtocol;
import com.zjw.ffitsdk.SimplePerformerListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import no.nordicsemi.android.support.v18.scanner.ScanResult;


public class BleService extends Service {

    private final String TAG = BleService.class.getSimpleName();
    private final static String TAG_CONTENT = "zh ble ";
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    //数据库存储
    private MovementInfoUtils mMovementInfoUtils = BaseApplication.getMovementInfoUtils();
    private SleepInfoUtils mSleepInfoUtils = BaseApplication.getSleepInfoUtils();
    private HeartInfoUtils mHeartInfoUtils = BaseApplication.getHeartInfoUtils();
    private HealthInfoUtils mHealthInfoUtils = BaseApplication.getHealthInfoUtils();
    private SportModleInfoUtils mSportModleInfoUtils = BaseApplication.getSportModleInfoUtils();

    private ContinuitySpo2InfoUtils mContinuitySpo2InfoUtils = BaseApplication.getContinuitySpo2InfoUtils();
    private MeasureSpo2InfoUtils mMeasureSpo2InfoUtils = BaseApplication.getMeasureSpo2InfoUtils();
    private ContinuityTempInfoUtils mContinuityTempInfoUtils = BaseApplication.getContinuityTempInfoUtils();
    private MeasureTempInfoUtils mMeasureTempInfoUtils = BaseApplication.getMeasureTempInfoUtils();

    private static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    private static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";

    //广播
    private final int MSG_FIND_PHONE_Viber = 0x02;//震动
    private final int MSG_FIND_PHONE_Media = 0x03;//音频
    private final int MSG_SET_NLS = 0x10011001;//发送通知

    //找手机相关
    private MediaPlayer mMediaPlayer; //播放语音
    private int mViberTimes = 0;  //播放震动

    private Handler mHandler;
    private Handler mBleHandler;
    private Handler mConnectTimeOutHandler;
    private Handler mSyncTimeoutHandle;

    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    public BluetoothAdapter mBluetoothAdapter;

    //蓝牙协议
    BleProtocol mBleProtocol;
    BleBaseProtocol mBleBaseProtocol;

    //==============蓝牙相关服务=======================
    // 系统
    private BluetoothGattService mSYSTEMervice = null;  //服务
    BluetoothGattCharacteristic characteristic_system_read;//读取数据 特征
    // ECG
    private BluetoothGattService mECGService = null;//服务
    BluetoothGattCharacteristic characteristic_ecg_read;//读取数据 特征
    // PPG
    private BluetoothGattService mPPGService = null;//服务
    BluetoothGattCharacteristic characteristic_ppg_read;//读取数据 特征
    // 图片
    private BluetoothGattService mImagerService = null;//服务
    //BluetoothGattCharacteristic characteristic_imager_read;//读取数据 特征
    //BluetoothGattCharacteristic characteristic_imager_write;//写入数据 特征
    // 主题
    private BluetoothGattService mThemeService = null;//服务
    BluetoothGattCharacteristic characteristic_theme_read;//读取数据 特征
    BluetoothGattCharacteristic characteristic_theme_write;//写入数据 特征
    // LOG
    private BluetoothGattService mLogService = null;//服务
    BluetoothGattCharacteristic characteristic_log_read;//读取数据 特征

    private BluetoothGattService mProtobufService = null;
    BluetoothGattCharacteristic characteristic_protobuf_1;
    BluetoothGattCharacteristic characteristic_protobuf_2;
    BluetoothGattCharacteristic characteristic_protobuf_3;
    BluetoothGattCharacteristic characteristic_protobuf_4;

    private boolean isSupportBigMtu = false;
    public static int currentMtu = 20;
    private BluetoothGattService mBigService = null;
    BluetoothGattCharacteristic characteristic_big_3;


    //==============蓝牙使能状态位=======================
    public boolean enableNorticeSysTag = false;
    public boolean enableNorticeEcgTag = false;
    public boolean enableNorticePpgTag = false;
    public boolean enableNorticeThemeTag = false;
    public boolean enableNorticeLogTag = false;

    //初始化是否完成
    public boolean IsServicesDiscovered = false;
//    private PowerManager.WakeLock wakeLock;

    public static String curBleAddress = BaseApplication.getBleDeviceTools().get_ble_mac();
    public static String curBleName = BaseApplication.getBleDeviceTools().get_ble_name();

    public boolean isEnableNorticeSysTag() {
        return enableNorticeSysTag;
    }

    public void setEnableNorticeSysTag(boolean enableNorticeSysTag) {
        this.enableNorticeSysTag = enableNorticeSysTag;
    }

    public boolean isEnableNorticeEcgTag() {
        return enableNorticeEcgTag;
    }

    public void setEnableNorticeEcgTag(boolean enableNorticeEcgTag) {
        this.enableNorticeEcgTag = enableNorticeEcgTag;
    }

    public boolean isEnableNorticePpgTag() {
        return enableNorticePpgTag;
    }

    public void setEnableNorticePpgTag(boolean enableNorticePpgTag) {
        this.enableNorticePpgTag = enableNorticePpgTag;
    }

    public boolean isEnableNorticeThemeTag() {
        return enableNorticeThemeTag;
    }

    public void setEnableNorticeThemeTag(boolean enableNorticeThemeTag) {
        this.enableNorticeThemeTag = enableNorticeThemeTag;
    }

    public boolean isEnableNorticeLogTag() {
        return enableNorticeLogTag;
    }

    public void setEnableNorticeLogTag(boolean enableNorticeLogTag) {
        this.enableNorticeLogTag = enableNorticeLogTag;
    }

    public boolean isServicesDiscovered() {
        return IsServicesDiscovered;
    }

    public void setServicesDiscovered(boolean servicesDiscovered) {
        IsServicesDiscovered = servicesDiscovered;
    }

    private static int mConnectionState = 0;

    public static boolean syncState = false;

    private MyBluetoothGattCallback mGattCallback;

    //离线心电数据存储
    private StringBuilder OffLineEcgData = new StringBuilder();
    private String OffLineEcgTime = "";
    private int OffLineEcgNowPageSize = 0;
    int OffLineOverMaxtime = 25;
    public static boolean OffLineOverIsStop = true;

    //监听来电广播之类的
    private long OFFHOOK_LAST_TIME = 0;
    private long IDLE_LAST_TIME = 0;
    private long CALL_STATE_RINGING_LAST_TIME = 0;
    private long FindPhoneTime = 0;
    private MyBroadcastReceiver mBroadcastReceiver;
    private ServiceBinder serviceBinder;

    //来电
    private PhoneReceiver mPhoneReceiver;

    //==================音乐控制======================
    private RemoteController.OnClientUpdateListener mOnClientUpdateListener;
    AudioManager mAudioManager;
    int mCurrent = -1;
    int mMaxVolume = 15;

    /**
     * 推送通知，为了常驻后台
     */
    private void startForgeGround() {
        NotificationUtils notificationUtils = new NotificationUtils(this);
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = notificationUtils.getChannelNotification(getString(R.string.app_name), getString(R.string.motificattion_text)).build();
        } else {
            notification = notificationUtils.getNotification_25(getString(R.string.app_name), getString(R.string.motificattion_text)).build();
        }
        startForeground(getClass().hashCode(), notification);
    }

    private Thread mProcessCmdThread = null;
    private Thread mProcessCmdThreadProto04 = null;
    private Thread mProcessCmdThreadProto = null;
    public static BleService bluetoothLeService;

    @Override
    public void onCreate() {
        SysUtils.logContentI(TAG, "onCreate");
        SysUtils.logAppRunning(TAG, "service is onCreate");
        EventTools.SafeRegisterEventBus(this);

        if (mProcessCmdThread == null) {
            mProcessCmdThread = new Thread(process_cmd_runnable);
            mProcessCmdThread.start();
        }
        if (mProcessCmdThreadProto04 == null) {
            mProcessCmdThreadProto04 = new Thread(process_cmd_runnable_proto04);
            mProcessCmdThreadProto04.start();
        }
        if (mProcessCmdThreadProto == null) {
            mProcessCmdThreadProto = new Thread(process_cmd_proto_runnable);
            mProcessCmdThreadProto.start();
        }


        bluetoothLeService = this;

//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, BleService.class.getName());
//        wakeLock.acquire();

        mGattCallback = new MyBluetoothGattCallback();
        startForgeGround();
        initReceiver();
        mBleHandler = new Handler();
        mConnectTimeOutHandler = new Handler();
        mSyncTimeoutHandle = new Handler();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_FIND_PHONE_Viber:
                        if (mViberTimes-- > 0) {
                            Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                            vib.vibrate(600);
                            mHandler.sendEmptyMessageDelayed(MSG_FIND_PHONE_Viber, 400);
                        }
                        break;

                    case MSG_FIND_PHONE_Media:
                        if (mMediaPlayer != null) {
                            mMediaPlayer.stop();
                            mMediaPlayer.release();
                        }

                        mMediaPlayer = MediaPlayer.create(BleService.this, R.raw.fail);
                        mMediaPlayer.start();

                        break;

//                    case MSG_SET_NLS:
//                        mHandler.sendEmptyMessageDelayed(MSG_SET_NLS, 1000);
//                        startForgeGround();
//                        break;
                }
            }
        };


        if (initialize()) {
            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                SysUtils.logContentI(TAG, "蓝牙状态 = 关闭");
            }
            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                SysUtils.logContentI(TAG, "蓝牙状态 = 打开");
            }
        }

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mCurrent = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        mBleProtocol = new BleProtocol(this);
        mBleProtocol.setmSimplePerformerListener(mPerformerListener);

        mBleBaseProtocol = new BleBaseProtocol();

        // 创建
        SysUtils.makeRootDirectory(Constants.DEVICE_ERROR_LOG_FILE);

        reconnectDevcie();
    }

    protected void onResume() {
        SysUtils.logContentI(TAG, "onResume");
        // TODO Auto-generated method stub
    }

    protected void onStop() {
        SysUtils.logContentI(TAG, "onStop");
        SysUtils.logAppRunning(TAG, "service is onStop");
        // TODO Auto-generated method stub
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
    }

    @Override
    public void onDestroy() {
        EventTools.SafeUnregisterEventBus(this);
        if (protoHandler != null) {
            protoHandler.removeCallbacksAndMessages(null);
        }
        bluetoothLeService = null;
        mProcessCmd = false;
        if (mProcessCmdThread != null) {
            mProcessCmdThread.interrupt();
            mProcessCmdThread = null;
        }
        if (mProcessCmdThreadProto04 != null) {
            mProcessCmdThreadProto04.interrupt();
            mProcessCmdThreadProto04 = null;
        }
        if (mProcessCmdThreadProto != null) {
            mProcessCmdThreadProto.interrupt();
            mProcessCmdThreadProto = null;
        }


//        if (wakeLock != null) {
//            wakeLock.release();
//            wakeLock = null;
//        }

        SysUtils.logContentI(TAG, "onDestroy");
        //移除来电监听
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mBleHandler != null) {
            mBleHandler.removeCallbacksAndMessages(null);
        }

        if (OffLineOverHandler != null) {
            OffLineOverHandler.removeCallbacksAndMessages(null);
        }
        if (mSyncTimeoutHandle != null) {
            mSyncTimeoutHandle.removeCallbacksAndMessages(null);
        }

        removeBindHandle();
        removeConnectTimeOutHandler();

        unRegisterPhoneReceiver();
        unregisterReceiver(mBroadcastReceiver);
        disconnect();
        SysUtils.logContentI(TAG, "onDestroy");
        SysUtils.logAppRunning(TAG, "service is onDestroy");
        super.onDestroy();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String cmd = bundle.getString("cmd");
                if (cmd != null) {
                    SysUtils.logContentE(TAG, "onStartCommand =" + cmd);
                    if (cmd.equals("setDeviceErrorLog")) {
                        setDeviceErrorLog();
                    } else if (cmd.equals("getDeviceErrorLog")) {
                        getDeviceErrorLog();
                    } else if (cmd.equals("bindDeviceConnect")) {
                        String address = bundle.getString("address");
                        bindDeviceConnect(address);
                    } else if (cmd.equals("createBond")) {
                        try {
                            createBond();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (cmd.equals("closePhoto")) {
                        closePhoto();
                    } else if (cmd.equals("writeRXCharacteristic")) {
                        byte[] params = bundle.getByteArray("params");
                        writeRXCharacteristic(params);
                    } else if (cmd.equals("disconnect")) {
                        removeBindHandle();
                        removeConnectTimeOutHandler();
                        disconnect();
                    } else if (cmd.equals("endSleepTag")) {
                        endSleepTag();
                    } else if (cmd.equals("syncTime")) {
                        syncTime();
                    } else if (cmd.equals("openPhoto")) {
                        openPhoto();
                    } else if (cmd.equals("clearConnectInfo")) {
                        removeBindHandle();
                        removeConnectTimeOutHandler();
                    } else if (cmd.equals("enableNotifacationThemeRead")) {
                        if (!isEnableNorticeThemeTag()) {
                            enableNotifacationThemeRead();
                        }
                    } else if (cmd.equals("sendMailListHead")) {
                        int total_page = intent.getIntExtra("total_page", 0);
                        int mtu = intent.getIntExtra("mtu", 0);
                        int block_size = intent.getIntExtra("block_size", 0);
                        int crc = intent.getIntExtra("crc", 0);
                        SysUtils.logContentI(TAG, "onStartCommand = total_page " + total_page);
                        SysUtils.logContentI(TAG, "onStartCommand = mtu " + mtu);
                        SysUtils.logContentI(TAG, "onStartCommand = block_size " + block_size);
                        SysUtils.logContentI(TAG, "onStartCommand = crc " + crc);
                        sendMailListHead(total_page, mtu, block_size, crc);
                    } else if (cmd.equals("sendThemeData")) {
                        sendThemeData(intent.getIntExtra("SnNum", 0), intent.getByteArrayExtra("send_data"));
                    } else if (cmd.equals("sendThemeBlockVerfication")) {
                        sendThemeBlockVerfication();
                    } else if (cmd.equals("cleanMailList")) {
                        cleanMailList();
                    } else if (cmd.equals("sendMusicHead")) {
                        int total_page = intent.getIntExtra("total_page", 0);
                        int mtu = intent.getIntExtra("mtu", 0);
                        int block_size = intent.getIntExtra("block_size", 0);
                        String fileName = intent.getStringExtra("fileName");
                        SysUtils.logContentI(TAG, "onStartCommand = total_page " + total_page);
                        SysUtils.logContentI(TAG, "onStartCommand = mtu " + mtu);
                        SysUtils.logContentI(TAG, "onStartCommand = block_size " + block_size);
                        SysUtils.logContentI(TAG, "onStartCommand = fileName " + fileName);
                        sendMusicHead(total_page, mtu, block_size, fileName);
                    } else if (cmd.equals("sendThemeHead")) {
                        int total_page = intent.getIntExtra("total_page", 0);
                        int mtu = intent.getIntExtra("mtu", 0);
                        int block_size = intent.getIntExtra("block_size", 0);
                        SysUtils.logContentI(TAG, "onStartCommand = total_page " + total_page);
                        SysUtils.logContentI(TAG, "onStartCommand = mtu " + mtu);
                        SysUtils.logContentI(TAG, "onStartCommand = block_size " + block_size);
                        sendThemeHead(total_page, mtu, block_size);
                    } else if (cmd.equals("setDeviceScreensaverInfo")) {
                        setDeviceScreensaverInfo();
                    } else if (cmd.equals("getDeviceScreensaverInfo")) {
                        getDeviceScreensaverInfo();
                    } else if (cmd.equals("sendImageHead")) {
                        sendImageHead(intent.getIntExtra("OutputX", 0), intent.getIntExtra("OutputY", 0), intent.getByteExtra("crc", new Byte("0")));
                    } else if (cmd.equals("send_image_data")) {
                        send_image_data(intent.getByteArrayExtra("crc"));
                    } else if (cmd.equals("enableNotifacationEcgRead")) {
                        if (!isEnableNorticeEcgTag()) {
                            enableNotifacationEcgRead();
                        }
                    } else if (cmd.equals("enableNotifacationPpgRead")) {
                        if (!isEnableNorticePpgTag()) {
                            enableNotifacationPpgRead();
                        }
                    } else if (cmd.equals("setEcgMeasure")) {
                        setEcgMeasure(intent.getBooleanExtra("is_ecg_measure", false));
                    } else if (cmd.equals("openEcg")) {
                        openEcg();
                    } else if (cmd.equals("closeEcg")) {
                        closeEcg();
                    } else if (cmd.equals("ResultCalibrationHeart")) {
                        ResultCalibrationHeart();
                    } else if (cmd.equals("ResultMeasureHeart")) {
                        ResultMeasureHeart(intent.getParcelableExtra("HealthInfo"));
                    } else if (cmd.equals("getDeviceInfo")) {
                        getDeviceInfo();
                    } else if (cmd.equals("restore_factory")) {
                        restore_factory();
                    } else if (cmd.equals("setDeviceCycleInfo")) {
                        setDeviceCycleInfo();
                    } else if (cmd.equals("getConnectStatus")) {
                        getConnectStatus();
                    } else if (cmd.equals("reconnectDevice")) {
                        reconnectDevcie();
                    } else if (cmd.equals("send")) {
                        byte[] params = bundle.getByteArray("params");
                        if (params != null) {
                            writeRXCharacteristic(params);
                        }
                    } else if (cmd.equals("proto")) {
                        byte[] params = bundle.getByteArray("params");
                        if (params != null) {
                            bleCmdList_proto.add(params);
                        }
                    } else if (cmd.equals("sendProtoUpdateData")) {
                        byte[] params = bundle.getByteArray("params");
                        if (params != null) {
                            bleCmdListProto04.add(params);
                        }
                    } else if (cmd.equals("initDeviceCmd")) {
                        initDeviceCmd();
                    }
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void getConnectStatus() {
        int status = getBlueToothStatus();
        switch (status) {
            case BleConstant.STATE_DISCONNECTED:
                BroadcastTools.broadcastDeviceDisconnected(getApplicationContext());
                break;
            case BleConstant.STATE_CONNECTING:
                BroadcastTools.broadcastDeviceConnecting(getApplicationContext());
                break;
            case BleConstant.STATE_CONNECTED:
                BroadcastTools.broadcastDeviceConnected(getApplicationContext());
                break;
            case BleConstant.STATE_CONNECTED_TIMEOUT:
                BroadcastTools.broadcastDeviceConnectTimeout(getApplicationContext());
                break;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (serviceBinder == null) {
            serviceBinder = new ServiceBinder();
        }
        return serviceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    /**
     * 初始化监听，来电，个人信息，推送等
     */
    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BroadcastTools.ACTION_NOTIFICATION_SEND_DATA);
        intentFilter.addAction(BroadcastTools.ACTION_NOTIFICATION_SEND_SET_USER_INFO);
        intentFilter.addAction(BroadcastTools.ACTION_NOTIFICATION_SEND_SET_TARGET_STEP);
        intentFilter.addAction(BroadcastTools.ACTION_NOTIFICATION_SEND_SET_DEVICE_THEME);
        intentFilter.addAction(BroadcastTools.ACTION_NOTIFICATION_SEND_SET_SKIN_COLOUR);
        intentFilter.addAction(BroadcastTools.ACTION_NOTIFICATION_SEND_SET_SCREEN_BRIGHESS);
        intentFilter.addAction(BroadcastTools.ACTION_NOTIFICATION_SEND_SET_BRIHNESS_TIME);
        intentFilter.addAction(BroadcastTools.ACTION_NOTIFICATION_SEND_SET_CYCLE);
        intentFilter.addAction(BroadcastTools.ACTION_NOTIFICATION_SEND_KUGOU_MUSIC);
        intentFilter.addAction(BroadcastTools.ACTION_CMD_GET_SPORT);
        intentFilter.addAction(VOLUME_CHANGED_ACTION);
        intentFilter.addAction(ACTION_DATA_AVAILABLE1);
        intentFilter.addAction(ACTION_DATA_AVAILABLE2);
        intentFilter.addAction(ACTION_DATA_AVAILABLE3);
        intentFilter.addAction(ACTION_DATA_AVAILABLE4);

        mBroadcastReceiver = new MyBroadcastReceiver();
        registerReceiver(mBroadcastReceiver, intentFilter);
        registerPhoneReceiver();
    }


    /**
     * 初始化本地蓝牙适配器的引用
     *
     * @return 初始化是否成功结果
     */
    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }


    Runnable connectTimeOutRunable = new Runnable() {
        @Override
        public void run() {
            if (ISBlueToothConnect()) {
                SysUtils.logContentI(TAG, "connectTimeOutRunable device connect state =true");
                return;
            }
            removeConnectTimeOutHandler();
            reconnectDevcie();
        }
    };

    public void removeConnectTimeOutHandler() {
        if (mConnectTimeOutHandler != null) {
            SysUtils.logContentI(TAG, "removemConnectTimeOutHandler");
            mConnectTimeOutHandler.removeCallbacksAndMessages(null);
        } else {
            SysUtils.logContentI(TAG, "removemConnectTimeOutHandler mConnectTimeOutHandler = null");
        }
    }


    private SimpleScanCallback simpleScanCallback = new SimpleScanCallback() {
        @Override
        public void onBleScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
        }

        @Override
        public void onBleScan(List<ScanResult> results) {
            for (final ScanResult result : results) {
                final ExtendedBluetoothDevice device = new ExtendedBluetoothDevice(result);

                String scanAdress = device.device.getAddress();

//                MyLog.i(TAG, "onBleScan scanAdress = " + scanAdress);

                //找到设备
                if (scanAdress.equalsIgnoreCase(mBleDeviceTools.get_ble_mac())) {

                    SysUtils.logContentI(TAG, "onBleScan mac is found");

                    if (baseBleScanner != null) {
                        baseBleScanner.onStopBleScan();
                    }

                    if (mBleHandler != null) {
                        mBleHandler.removeCallbacksAndMessages(null);
                    }

                    removeConnectTimeOutHandler();
                    mConnectTimeOutHandler.postDelayed(connectTimeOutRunable, Constants.CONNECT_TIMEOUT);
                    connect(mBleDeviceTools.get_ble_mac());

                    return;
                }
            }
        }

        @Override
        public void onBleScanStop(MyBleScanState scanState) {//扫描异常停止
        }
    };

    private NordicsemiBleScanner baseBleScanner;

    public void reconnectDevcie() {

        removeBindHandle();

        if (mBluetoothAdapter != null) {
            if (mUserSetTools.get_user_login()) {

                if (baseBleScanner != null) {
                    baseBleScanner.onStopBleScan();
                }

                if (mBleHandler != null) {
                    mBleHandler.removeCallbacksAndMessages(null);
                }

                if (!JavaUtil.checkIsNull(mBleDeviceTools.get_ble_mac())) {
                    SysUtils.logContentI(TAG, "reconnectDevcie mac =" + mBleDeviceTools.get_ble_mac());
                    SysUtils.logAppRunning(TAG, "reconnectDevcie mac =" + mBleDeviceTools.get_ble_mac());

                    baseBleScanner = new NordicsemiBleScanner(this, simpleScanCallback);
                    baseBleScanner.onStartBleScan();

//                    setBlueToothStatus(BleConstant.STATE_CONNECTING);
//                    BroadcastTools.broadcastDeviceConnecting(getApplicationContext());

                    mBleHandler.postDelayed(stopScanRunable, Constants.SCAN_PERIOD_BLE_SERVICE);

                } else {
                    SysUtils.logContentI(TAG, "reconnectDevcie mac = null");
                    SysUtils.logAppRunning(TAG, "reconnectDevcie mac = null");
                }
            } else {
                SysUtils.logContentI(TAG, "尝试连接 未登录");
                SysUtils.logAppRunning(TAG, "尝试连接 未登录");
            }
        }
    }

    Runnable stopScanRunable = new Runnable() {
        @Override
        public void run() {
            SysUtils.logContentI(TAG, "try to reconnect");
            SysUtils.logAppRunning(TAG, "try to reconnect");
            if (baseBleScanner != null) {
                baseBleScanner.onStopBleScan();
            }

            if (ISBlueToothConnect()) {
                SysUtils.logContentI(TAG, "stopScanRunable device connect state =true");
                SysUtils.logAppRunning(TAG, "stopScanRunable device connect state =true");
                return;
            }

            removeConnectTimeOutHandler();
            mConnectTimeOutHandler.postDelayed(connectTimeOutRunable, Constants.CONNECT_TIMEOUT);
            connect(mBleDeviceTools.get_ble_mac());
        }
    };

    private boolean isBindConnect = false;
    private Handler bindHandler;
    private int reTryTimes = 0;
    private String bindAddress = "";

    public void removeBindHandle() {
        if (bindHandler != null) {
            SysUtils.logContentI(TAG, "removeBindHandle");
            bindHandler.removeCallbacksAndMessages(null);
        } else {
            SysUtils.logContentI(TAG, "removeBindHandle connectHandler = null");
        }
    }


    public void bindDeviceConnect(String address) {
        isBindConnect = true;
        bindAddress = address;

        if (bindHandler == null) {
            bindHandler = new Handler();
        }

        connect(bindAddress);
        bindHandler.postDelayed(bindDeviceRunable, Constants.CONNECT_TIMEOUT);
        reTryTimes = 1;
    }

    Runnable bindDeviceRunable = new Runnable() {
        @Override
        public void run() {
            timeOutNum = 0;
            if (reTryTimes < Constants.CONNECT_TIMES) {
                SysUtils.logContentI(TAG, "bindDeviceRunable retry reTryTimes = " + reTryTimes);
                SysUtils.logAppRunning(TAG, "bindDeviceRunable retry reTryTimes = " + reTryTimes);
                reTryTimes++;
                connect(bindAddress);
                bindHandler.postDelayed(bindDeviceRunable, Constants.CONNECT_TIMEOUT);
            } else {
                SysUtils.logContentI(TAG, "connectDeviceRunable overtime");
                SysUtils.logAppRunning(TAG, "connectDeviceRunable overtime");

                // 连接三次失败
                isBindConnect = false;
                removeBindHandle();
                removeConnectTimeOutHandler();

                if (ISBlueToothConnect()) {
                    SysUtils.logContentI(TAG, "connectDeviceRunable device connect state = is connect");
                    SysUtils.logAppRunning(TAG, "connectDeviceRunable device connect state = is connect");
                    return;
                } else {
                    SysUtils.logContentI(TAG, "蓝牙未连接");
                }

                disconnect();
                //清空数据
                mBleDeviceTools.set_ble_mac("");
                mBleDeviceTools.set_call_ble_mac("");
                mBleDeviceTools.set_ble_name("");
                mBleDeviceTools.set_call_ble_name("");
                mUserSetTools.set_service_upload_device_info("");
                changeDeviceGatt();

            }

        }
    };

    /**
     * 连接设备
     */

    private int timeOutNum = 0;

    public boolean connect(String address) {
        bInitGattServices = false;
        disconnect();
//        if (mBluetoothGatt != null) {
//            mBluetoothGatt.close();
//        }

        SysUtils.logContentI(TAG, "connect() address = " + address);
        SysUtils.logAppRunning(TAG, "connect() address = " + address);

        mBluetoothManager = null;
        mBluetoothAdapter = null;
        mBluetoothGatt = null;
        initialize();

        if (mBluetoothAdapter == null) {
            SysUtils.logContentI(TAG, "connect() mBluetoothAdapter = null");
            SysUtils.logAppRunning(TAG, "connect() mBluetoothAdapter = null");
            return false;
        }

        if (address == null) {
            SysUtils.logContentI(TAG, "connect() ddress = null");
            SysUtils.logAppRunning(TAG, "connect() ddress = null");
            return false;
        }
        try {
            BluetoothDevice device;

            SysUtils.logContentW(TAG, "connect() timeOutNum =" + timeOutNum);
            SysUtils.logAppRunning(TAG, "connect() timeOutNum =" + timeOutNum);

            if (timeOutNum >= 3) {
                device = mBluetoothAdapter.getRemoteDevice("AA:BB:CC:DD:EE:FF");
                SysUtils.logContentE(TAG, "connect() timeOutNum = changeGatt");
                SysUtils.logAppRunning(TAG, "connect() timeOutNum = changeGatt");
                timeOutNum = 0;
            } else {
                device = mBluetoothAdapter.getRemoteDevice(address);
                timeOutNum++;
            }

            if (device == null) {
                SysUtils.logContentE(TAG, "getRemoteDevice device is null");
                SysUtils.logAppRunning(TAG, "getRemoteDevice device is null");
                return false;
            }

            mBluetoothGatt = device.connectGatt(BleService.this, false, mGattCallback);
            setBlueToothStatus(BleConstant.STATE_CONNECTING);
            BroadcastTools.broadcastDeviceConnecting(getApplicationContext());
        } catch (Exception e) {
            SysUtils.logContentE(TAG, "getRemoteDevice e=" + e);
            SysUtils.logAppRunning(TAG, "getRemoteDevice e=" + e);
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * 断开蓝牙连接
     */
    public void disconnect() {
        SysUtils.logContentI(TAG, "disconnect()");
        SysUtils.logAppRunning(TAG, "disconnect()");
        setBlueToothStatus(BleConstant.STATE_DISCONNECTED);
        BroadcastTools.broadcastDeviceDisconnected(getApplicationContext());
        try {
            if (mBluetoothGatt != null) {
                SysUtils.logContentW(TAG, "disconnect() mBluetoothGatt != null");
//                mBluetoothGatt.disconnect();
                mBluetoothGatt.close();
                mBluetoothGatt = null;

            } else {
                SysUtils.logContentE(TAG, "disconnect() mBluetoothGatt = null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 写指令到蓝牙设备
     *
     * @param value
     * @return
     */
    public boolean writeRXCharacteristic(final byte[] value) {
        if (!OffLineOverIsStop) {
            SysUtils.logContentE(TAG, "sync offline , return writeRXCharacteristic");
            return false;
        }
        boolean status = false;
        if (mBluetoothGatt == null) {
            SysUtils.logContentE(TAG, "mBluetoothGatt is null , return writeRXCharacteristic");
            return false;
        }
        int length = value.length;
        int copy_size = 0;
        while (length > 0) {
            if (length < currentMtu) {
                byte[] val = new byte[length];
                for (int i = 0; i < length; i++) {
                    val[i] = value[i + copy_size];
                }
                if (isSupportBigMtu) {
                    currentUuid = BleConstant.CHAR_BIG_UUID_02;
                } else {
                    currentUuid = BleConstant.UUID_BASE_WRITE;
                }
                bleCmdList.add(val);
            } else {
                byte[] val = new byte[currentMtu];
                for (int i = 0; i < currentMtu; i++) {
                    val[i] = value[i + copy_size];
                }
                if (isSupportBigMtu) {
                    currentUuid = BleConstant.CHAR_BIG_UUID_02;
                } else {
                    currentUuid = BleConstant.UUID_BASE_WRITE;
                }
                bleCmdList.add(val);
            }
            copy_size += currentMtu;
            length -= currentMtu;
        }
        return true;
    }

    private void resetBleCmdState(boolean bClearData) {
        initReplyStatus();
        recvData = "";
        isSending = false;
        lastSendTime = 0;
        bBleCmdSessionCompleted = true;
        if (bClearData) {
            bleCmdList.clear();
            bleCmdList_proto.clear();
        }
    }

    private String[] requireSessionControlNoResponseCommanId1 = {
            Integer.toHexString(BtSerializeation.KEY_SYNC_TIME & 0xFF),
            Integer.toHexString(BtSerializeation.KEY_FIND_DEVICE_INSTRA & 0xFF),
            Integer.toHexString(BtSerializeation.KEY_SET_REBOOT & 0xFF),
            Integer.toHexString(BtSerializeation.KEY_MUSIC_CONTROL_INFO_NEW & 0xFF)
    };


    protected boolean isNoSessionCommand(String action, String commandId) {
        boolean bRequireSessionControl = false;
        if (commandId.equalsIgnoreCase(Integer.toHexString(BtSerializeation.CMD_01 & 0xFF))) {
            for (String s : requireSessionControlNoResponseCommanId1) {
                if (action.equalsIgnoreCase(s)) {
                    bRequireSessionControl = true;
                    break;
                }
            }
        } else if (commandId.equalsIgnoreCase(Integer.toHexString(BtSerializeation.CMD_02 & 0xFF))) {
            bRequireSessionControl = true;
        }
        return bRequireSessionControl;
    }

    public static UUID currentUuid = BleConstant.UUID_BASE_WRITE;
    public static UUID currentUuid_proto = BleConstant.UUID_BASE_WRITE;
    private boolean bBleCmdSessionCompleted = true;
    private String recvData = "";
    private boolean bInitGattServices;
    private boolean mProcessCmd = true;
    private List<byte[]> bleCmdList = new ArrayList<byte[]>();
    private List<byte[]> bleCmdList_proto = new ArrayList<byte[]>();
    private List<byte[]> bleCmdListProto04 = new ArrayList<byte[]>();
    //    private byte[] sendingByte;
    private boolean isSending = false;
    private long lastSendTime = 0;
    private int sendMaxPack = 0;
    private int sendOver = 0;
    private boolean isReply = false;
    Runnable process_cmd_runnable = new Runnable() {
        @Override
        public void run() {
            while (mProcessCmd) {
                try {
                    Thread.sleep(Constants.detectBleCmdPeriod);
                    if (!bInitGattServices)
                        continue;
                    boolean connectState = ISBlueToothConnect();
                    int cmdSize = bleCmdList.size();

                    if (!connectState) {
                        resetBleCmdState(false);
                    } else {
                        if (!isSending) {
                            if (cmdSize > 0) {
                                byte[] paramCmd = bleCmdList.remove(0);
                                String action = "";
                                String commandId = "";
                                int length = 0;
                                if (paramCmd.length > 12) {
                                    if (sendMaxPack == 0 && sendOver == 0) {
                                        commandId = Integer.toHexString(paramCmd[8] & 0xFF);
                                        action = Integer.toHexString(paramCmd[10] & 0xFF);
                                        length = paramCmd[12] & 0xFF;

                                        int totalLength = length + 13;
                                        if (totalLength % currentMtu == 0) {
                                            sendMaxPack = totalLength / currentMtu;
                                        } else {
                                            sendMaxPack = totalLength / currentMtu + 1;
                                        }
                                    }
                                }
                                isSending = false;
                                if (currentUuid.equals(BleConstant.UUID_BASE_WRITE)) {
                                    writeCharacteristic(paramCmd, BleConstant.UUID_BASE_SERVICE, BleConstant.UUID_BASE_WRITE);
                                } else if (currentUuid.equals(BleConstant.CHAR_BIG_UUID_02)) {
                                    if (sendMaxPack == 1) {
                                        initSendStatus(action, commandId, paramCmd);
                                        initReplyStatus();
                                    } else {
                                        if (sendOver < sendMaxPack) {
                                            sendOver++;
                                            if (sendOver == 1) {
                                                if (action.length() > 0) {
                                                    if (isNoSessionCommand(action, commandId)) {
                                                    } else {
                                                        // 需要回复的命令
                                                        isReply = true;
                                                    }
                                                }
                                            } else {
                                                if (sendOver == sendMaxPack) {
                                                    if (isReply) {
                                                        if (!mBleDeviceTools.getisReplyOnePack()) {
                                                            //只回复最后一个小包
                                                            isSending = true;
                                                            lastSendTime = System.currentTimeMillis();
                                                        }

                                                        initReplyStatus();
                                                    }

                                                }
                                            }

                                            if (isReply && mBleDeviceTools.getisReplyOnePack()) {
                                                // 每一个小包都需要回复
                                                isSending = true;
                                                lastSendTime = System.currentTimeMillis();
                                            }

                                        }
                                    }
                                    writeCharacteristic(paramCmd, BleConstant.UUID_BIG_SERVICE, BleConstant.CHAR_BIG_UUID_02);

                                }/* else if (currentUuid.equals(BleConstant.CHAR_PROTOBUF_UUID_03)) {
                                    writeCharacteristic(paramCmd, BleConstant.UUID_PROTOBUF_SERVICE, BleConstant.CHAR_PROTOBUF_UUID_03);
                                } else {
                                    if (isReplyDevice(paramCmd)) {
                                        writeCharacteristic(paramCmd, BleConstant.UUID_PROTOBUF_SERVICE, BleConstant.CHAR_PROTOBUF_UUID_01);
                                    } else {
                                        writeCharacteristic(paramCmd, BleConstant.UUID_PROTOBUF_SERVICE, BleConstant.CHAR_PROTOBUF_UUID_02);
                                    }
                                }*/
                            }
                        } else {
                            // 发送中....
                            if (System.currentTimeMillis() - lastSendTime > 3000) {
                                SysUtils.logContentE(TAG, "process_cmd_runnable cmd send time out");
                                SysUtils.logAppRunning(TAG, "process_cmd_runnable cmd send time out");
                                isSending = false;
                            }
                        }

                    }
                } catch (Exception e) {
                    resetBleCmdState(false);
                    SysUtils.logContentE(TAG, "process_cmd_runnable e=" + e);
                    SysUtils.logAppRunning(TAG, "process_cmd_runnable e=" + e);
                }
            }
        }
    };

    private void initReplyStatus() {
        isReply = false;
        sendMaxPack = 0;
        sendOver = 0;
    }

    private void initSendStatus(String action, String commandId, byte[] paramCmd) {
        if (action.length() > 0) {
            if (isNoSessionCommand(action, commandId)) {
                //不需要回复的命令
                if (action.equalsIgnoreCase(Integer.toHexString(BtSerializeation.KEY_SYNC_TIME & 0xFF))
                        && commandId.equalsIgnoreCase(Integer.toHexString(BtSerializeation.CMD_01 & 0xFF))
                ) {
                    // 同步
                    syncState = true;
                    if (mSyncTimeoutHandle != null) {
                        mSyncTimeoutHandle.removeCallbacksAndMessages(null);
                        mSyncTimeoutHandle.postDelayed(() -> {
                            syncState = false;
                            SysUtils.logContentI(TAG, "sync data time out");
                            BroadcastTools.broadcastSyncTimeOut(getApplicationContext());
                        }, Constants.SYNC_TIMEOUT);
                    }
                }
            } else {
                isSending = true;
                lastSendTime = System.currentTimeMillis();
            }
        }
    }

    private int curPiece = 0;
    private int maxPiece = 0;
    private int curPacket;
    private int recvMaxPacket;
    private String recvData3 = "";

    private void displayData3(String data) {
        if (data != null) {
            SysUtils.logContentI(TAG, TAG_CONTENT + "displayData3 : " + data);
            SysUtils.logAppRunning(TAG, TAG_CONTENT + "displayData3 : " + data);

            String[] strCmd;
            strCmd = data.split(" ");
            Intent intent = new Intent();
            try {
                currentUuid_proto = BleConstant.CHAR_PROTOBUF_UUID_03;
                if (strCmd[0].equalsIgnoreCase("00") && strCmd[1].equalsIgnoreCase("00")) {
                    if (strCmd[2].equalsIgnoreCase("00") && strCmd[3].equalsIgnoreCase("00")) {
                        recvMaxPacket = Integer.parseInt(strCmd[5] + strCmd[4], 16);
//                        intent.setAction(BroadcastTools.ACTION_CMD_DEVICE_START);
//                        sendBroadcast(intent);
                        writeCharacteristic(BtSerializeation.deviceStartCmd(), BleConstant.UUID_PROTOBUF_SERVICE, BleConstant.CHAR_PROTOBUF_UUID_03);

                        bBleCmdSessionCompleted = true;
                        return;
                    }
                } else {
                    curPacket = Integer.parseInt(strCmd[1] + strCmd[0], 16);
                    if (curPacket == 1) {
                        maxPiece = Integer.parseInt(strCmd[3] + strCmd[2], 16);
                        curPiece = Integer.parseInt(strCmd[5] + strCmd[4], 16);
                        recvData3 = recvData3 + data.substring(18);
                    } else {
                        recvData3 = recvData3 + data.substring(6);
                    }

                }
                if (curPacket < recvMaxPacket) {
                    refreshProtobufSportTimeOut();
                    return;
                }
                if (curPiece < maxPiece) {
                    SysUtils.logContentI(TAG, TAG_CONTENT + " app confirm");
                    SysUtils.logAppRunning(TAG, TAG_CONTENT + " app confirm");
//                    intent.setAction(BroadcastTools.ACTION_CMD_APP_CONFIRM);
//                    sendBroadcast(intent);
                    writeCharacteristic(BtSerializeation.appConfirm(), BleConstant.UUID_PROTOBUF_SERVICE, BleConstant.CHAR_PROTOBUF_UUID_03);
                    return;
                }
                SysUtils.logContentI(TAG, TAG_CONTENT + " app confirm");
                SysUtils.logAppRunning(TAG, TAG_CONTENT + " app confirm");
//                intent.setAction(BroadcastTools.ACTION_CMD_APP_CONFIRM);
//                sendBroadcast(intent);
                writeCharacteristic(BtSerializeation.appConfirm(), BleConstant.UUID_PROTOBUF_SERVICE, BleConstant.CHAR_PROTOBUF_UUID_03);
                //解析
                String[] sportData = recvData3.split(" ");
                SysUtils.logContentI(TAG, TAG_CONTENT + " recvData3 =" + sportData.length);
                SysUtils.logAppRunning(TAG, TAG_CONTENT + " recvData3 =" + sportData.length);
                FitnessTools.parsing(sportData);

            } catch (Exception e) {
                e.printStackTrace();
            }
            recvData3 = "";
            resetBleCmdState(false);
        }
    }

    private void displayData2(String data) {
        if (data != null) {
            SysUtils.logContentI(TAG, TAG_CONTENT + "displayData2 : " + data);
            SysUtils.logAppRunning(TAG, TAG_CONTENT + "displayData2 : " + data);
            recvData = data;
            String[] strCmd;
            strCmd = recvData.split(" ");
            Intent intent = new Intent();
            try {
                currentUuid_proto = BleConstant.CHAR_PROTOBUF_UUID_02;
                //设备回复成功，
                if (Arrays.equals("00 00 01 01 00 00".split(" "), strCmd)) {
//                    intent.setAction(BroadcastTools.ACTION_CMD_APP_START);
//                    sendBroadcast(intent);
                    sendBleData2();
                }
                // 设备确认
                if (Arrays.equals("00 00 01 00 00 00".split(" "), strCmd)) {
//                    intent.setAction(BroadcastTools.ACTION_CMD_DEVICE_CONFIRM);
//                    sendBroadcast(intent);

                    sendNextBleData2();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            resetBleCmdState(false);
        }
    }

    private long lastDisplayTime = 0;
    private String lastData = "";

    private void displayData1(String data) {
        if (data != null) {
            SysUtils.logContentI(TAG, TAG_CONTENT + "displayData1 : " + data);
            SysUtils.logAppRunning(TAG, TAG_CONTENT + "displayData1 : " + data);
            /*if (System.currentTimeMillis() - lastDisplayTime < 5 && lastData.equalsIgnoreCase(data)) {
                SysUtils.logContentI(TAG, TAG_CONTENT + "displayData1 : repeat data");
            } else */
            {
                String[] strCmd;
                strCmd = data.split(" ");
                Intent intent = new Intent();
                try {
                    currentUuid_proto = BleConstant.CHAR_PROTOBUF_UUID_01;
                    if (strCmd[0].equalsIgnoreCase("00") && strCmd[1].equalsIgnoreCase("00")) {
                        if (strCmd[2].equalsIgnoreCase("00") && strCmd[3].equalsIgnoreCase("00")) {
                            recvMaxPacket = Integer.parseInt(strCmd[5] + strCmd[4], 16);

//                            intent.setAction(BroadcastTools.ACTION_CMD_DEVICE_START);
//                            sendBroadcast(intent);
                            writeCharacteristic(BtSerializeation.deviceStartCmd(), BleConstant.UUID_PROTOBUF_SERVICE, BleConstant.CHAR_PROTOBUF_UUID_01);
                            resetBleCmdState(false);
                            return;
                        }
                    } else {
                        curPacket = Integer.parseInt(strCmd[1] + strCmd[0], 16);
                        if (curPacket == 1) {
                            recvData = data.substring(6);
                        } else {
                            recvData = recvData + data.substring(6);
                        }
                    }

                    if (curPacket < recvMaxPacket) {
                        refreshProtobufSportTimeOut();
                        return;
                    }

//                    intent.setAction(BroadcastTools.ACTION_CMD_APP_CONFIRM);
//                    sendBroadcast(intent);
                    writeCharacteristic(BtSerializeation.appConfirm(), BleConstant.UUID_PROTOBUF_SERVICE, BleConstant.CHAR_PROTOBUF_UUID_01);

                    strCmd = recvData.split(" ");
                    byte[] valueByte = new byte[strCmd.length];
                    for (int i = 0; i < strCmd.length; i++) {
                        valueByte[i] = (byte) Integer.parseInt(strCmd[i], 16);
                    }
                    String analysisProtoData = AnalysisProtoData.getInstance().analysisData(valueByte);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                resetBleCmdState(false);
            }
        }
    }

    private boolean isReplyDevice(byte[] paramCmd) {
        if (paramCmd != null && paramCmd.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(paramCmd.length);
            for (byte byteChar : paramCmd)
                stringBuilder.append(String.format("%02X ", byteChar));
            String[] strCmd;
            strCmd = stringBuilder.toString().split(" ");
            if (Arrays.equals("00 00 01 01 00 00".split(" "), strCmd)) {
                return true;
            }
            return Arrays.equals("00 00 01 00 00 00".split(" "), strCmd);
        }
        return false;
    }

    public void writeCharacteristic(byte[] value, UUID serviceUUid, UUID uuid) {
        try {
            SysUtils.logContentI(TAG, "writeRXCharacteristic =" + BleTools.bytes2HexString(value));
            SysUtils.logAppRunning(TAG, "writeRXCharacteristic =" + BleTools.bytes2HexString(value));
            if (mBluetoothGatt == null) {
                return;
            }
            BluetoothGattService gap_service = mBluetoothGatt.getService(serviceUUid);
            if (gap_service == null) {
                return;
            }
            BluetoothGattCharacteristic dev_name = gap_service.getCharacteristic(uuid);
            if (dev_name == null) {
                return;
            }
            dev_name.setValue(value);
            boolean status = mBluetoothGatt.writeCharacteristic(dev_name);


            for (int i = 1; i <= Constants.detectBleCmdReissueCount; i++) {
                if (status) {
                    break;
                }
                SysUtils.logContentI(TAG, "uuid = " + uuid + " writeCharacteristic status =  number of reissues " + i + " status = " + status);
                SysUtils.logAppRunning(TAG, "uuid = " + uuid + " writeCharacteristic status =  number of reissues " + i + " status = " + status);
                try {
                    Thread.sleep(Constants.detectBleCmdReissuePeriod);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                status = mBluetoothGatt.writeCharacteristic(dev_name);
            }
            SysUtils.logContentI(TAG, "uuid = " + uuid + " writeCharacteristic status end = " + status);
            SysUtils.logAppRunning(TAG, "uuid = " + uuid + " writeCharacteristic status end = " + status);
        } catch (Exception e) {
            e.printStackTrace();
            SysUtils.logAppRunning(TAG, "writeCharacteristic Exception = " + e);
        }

    }

    public class ServiceBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    class MyBluetoothGattCallback extends BluetoothGattCallback {

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {

            SysUtils.logContentW(TAG, "onConnectionStateChange status =" + status + "   newState =" + newState);
            SysUtils.logAppRunning(TAG, "onConnectionStateChange status =" + status + "   newState =" + newState);

            if (mBluetoothGatt != null) {
                if (mBluetoothGatt != gatt) {
                    SysUtils.logContentE(TAG, "onConnectionStateChange mBluetoothGatt != gatt");
                    return;
                } else {
                    SysUtils.logContentW(TAG, "onConnectionStateChange mBluetoothGatt == gatt");
                }
            } else {
                SysUtils.logContentE(TAG, "onConnectionStateChange mBluetoothGatt == null");
                return;
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (mProcessCmdThread == null) {
                    SysUtils.logContentE(TAG, "mProcessCmdThread is null");
                    mProcessCmd = true;
                    mProcessCmdThread = new Thread(process_cmd_runnable);
                    mProcessCmdThread.start();
                }

                resetBleCmdState(true);
                timeOutNum = 0;
                SysUtils.logContentW(TAG, "onConnectionStateChange Connected");

                if (isBindConnect) {
                    isBindConnect = false;
                    removeBindHandle();
                }

                removeConnectTimeOutHandler();
                handleConnectBle();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (mSyncTimeoutHandle != null) {
                    mSyncTimeoutHandle.removeCallbacksAndMessages(null);
                }
                SysUtils.logContentW(TAG, "onConnectionStateChange Disconnected");
                SysUtils.logAppRunning(TAG, "onConnectionStateChange Disconnected");
                gatt.close();
                disconnect();
                removeConnectTimeOutHandler();
                handleDisconnectedBle(gatt);
            } else {
                SysUtils.logContentW(TAG, "onConnectionStateChange other status =" + status + "   newState=" + newState);
                gatt.close();
//                disconnect();
//                removeConnectTimeOutHandler();
//                handleDisconnectedBle(gatt);
            }

        }


        /**
         * 处理连接设备
         */
        private void handleConnectBle() {

            TraceErrorLog.setFileName(MyTime.getAllTime());
            TraceErrorLog.init(true);
            TraceErrorLog.i("连上了");

            initOffLineEcg();
            setEcgMeasure(false);

            SysUtils.logContentI(TAG, "handleConnectBle = curBleAddress = " + curBleAddress);
            SysUtils.logContentI(TAG, "handleConnectBle = curBleName = " + curBleName);

            SysUtils.logContentI(TAG, "handleConnectBle = mBluetoothGatt.getDevice().getAddress() = " + mBluetoothGatt.getDevice().getAddress());
            SysUtils.logContentI(TAG, "handleConnectBle = mBluetoothGatt.getDevice().getName() = " + mBluetoothGatt.getDevice().getName());


            if (!JavaUtil.checkIsNull(curBleAddress)) {
                mBleDeviceTools.set_ble_mac(curBleAddress);
            } else if (!JavaUtil.checkIsNull(mBluetoothGatt.getDevice().getAddress())) {
                mBleDeviceTools.set_ble_mac(mBluetoothGatt.getDevice().getAddress());
            }

            if (!JavaUtil.checkIsNull(curBleName)) {
                mBleDeviceTools.set_ble_name(curBleName);
            } else if (!JavaUtil.checkIsNull(mBluetoothGatt.getDevice().getName())) {
                mBleDeviceTools.set_ble_name(mBluetoothGatt.getDevice().getName());
            }

            mBleDeviceTools.set_ble_device_power(0);
            mBleDeviceTools.set_ble_device_type(0);
            mBleDeviceTools.set_ble_device_version(0);
            OffLineOverIsStop = true;
            setBlueToothStatus(BleConstant.STATE_CONNECTED);
            BroadcastTools.broadcastDeviceConnected(getApplicationContext());

            setEnableNorticeSysTag(false);
            setEnableNorticeEcgTag(false);
            setEnableNorticePpgTag(false);
            setEnableNorticeThemeTag(false);
            setEnableNorticeLogTag(false);
            setServicesDiscovered(false);
            mBleDeviceTools.set_device_mtu_num(20);

            //调用这个来初始化,不能去掉！
            boolean isSuccess = mBluetoothGatt.discoverServices();
            SysUtils.logContentI(TAG, "discoverServices isSuccess = " + isSuccess);


        }


        /**
         * 处理断开设备
         */
        private void handleDisconnectedBle(BluetoothGatt gatt) {
            SysUtils.logContentI(TAG, "处理断开状态");
            initDisconnectParameter();

            mBleDeviceTools.set_ble_device_type(0);
            mBleDeviceTools.set_ble_device_version(0);

            setBlueToothStatus(BleConstant.STATE_DISCONNECTED);
            BroadcastTools.broadcastDeviceDisconnected(getApplicationContext());

            if (!JavaUtil.checkIsNull(mBleDeviceTools.get_ble_mac())) {


                if (mBluetoothAdapter == null) {
                    SysUtils.logContentE(TAG, "handleDisconnectedBle = mBluetoothAdapter=null");
                    return;
                }

                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                    SysUtils.logContentW(TAG, "handleDisconnectedBle = STATE_OFF");
                }

                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {

                    SysUtils.logContentW(TAG, "handleDisconnectedBle = STATE_ON");
                    if (isBindConnect) {
                        reTryTimes++;
                        connect(mBleDeviceTools.get_ble_mac());
                        bindHandler.postDelayed(bindDeviceRunable, Constants.CONNECT_TIMEOUT);
                    } else {
                        SysUtils.logContentI(TAG, "connect fail begin reconnect");
                        timeOutNum = 0;
                        reconnectDevcie();
                    }
                }
            } else {
                SysUtils.logContentI(TAG, "handleDisconnectedBle mac = null");
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            // 初始化
            initDeviceParameter();

            SysUtils.logContentI(TAG, "onServicesDiscovered ");
            SysUtils.logAppRunning(TAG, "onServicesDiscovered ");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                new Thread(() -> {
                    try {
                        iCallDeviceInfo = false;
                        isDeviceThemeInfo = false;
                        isDeviceMtu = false;
                        isInitTimeZone = false;
                        isSupportBigMtu = false;
                        mBleDeviceTools.setIsSupportBigPage(false);
                        currentMtu = 20;

                        List<BluetoothGattService> gattServices = getSupportedGattServices();
                        boolean flag_ecg = false;
                        boolean flag_ppg = false;
                        boolean flag_sys = false;
                        boolean flag_theme = false;
                        boolean flag_log = false;
                        boolean flag_protobuf = false;
                        // 如果找到这个特定的服务ID就初始化一些东西
                        if (gattServices != null && gattServices.size() == 0) {
                            connectBleRXError();
                            return;
                        }
                        if (gattServices == null) {
                            connectBleRXError();
                            return;
                        }
                        for (BluetoothGattService gattService : gattServices) {

                            if (gattService.getUuid() != null) {
                                SysUtils.logContentI(TAG, "初始化 getUuid ===== = " + gattService.getUuid());

                                if (gattService.getUuid().equals(BleConstant.UUID_BASE_SERVICE)) {
                                    SysUtils.logContentI(TAG, "初始化 系统(小包) ===== = 发现服务");
                                    SysUtils.logAppRunning(TAG, "初始化 系统(小包) ===== = 发现服务");
                                    flag_sys = true;
                                    mSYSTEMervice = gattService;
                                    setServicesDiscovered(true);
                                }

                                if (gattService.getUuid().equals(BleConstant.UUID_BIG_SERVICE)) {
                                    SysUtils.logContentI(TAG, " 初始化 系统(大包) ===== = 发现服务");
                                    SysUtils.logAppRunning(TAG, " 初始化 系统(大包) ===== = 发现服务");
                                    mBigService = gattService;
                                    setServicesDiscovered(true);
                                    isSupportBigMtu = true;
                                    mBleDeviceTools.setIsSupportBigPage(true);
                                }

                                if (gattService.getUuid().equals(BleConstant.UUID_ECG_SERVICE)) {
                                    SysUtils.logContentI(TAG, "初始化 ECG ===== = 发现服务");
                                    SysUtils.logAppRunning(TAG, "初始化 ECG ===== = 发现服务");
                                    flag_ecg = true;
                                    mECGService = gattService;
                                }
                                if (gattService.getUuid().equals(BleConstant.UUID_PPG_SERVICE)) {
                                    SysUtils.logContentI(TAG, "初始化 PPG ===== = 发现服务");
                                    SysUtils.logAppRunning(TAG, "初始化 PPG ===== = 发现服务");
                                    flag_ppg = true;
                                    mPPGService = gattService;
                                }

                                if (gattService.getUuid().equals(BleConstant.UUID_THEME_SERVICE)) {
                                    SysUtils.logContentI(TAG, "初始化 THEME ===== 发现服务");
                                    SysUtils.logAppRunning(TAG, "初始化 THEME ===== 发现服务");
                                    flag_theme = true;
                                    mThemeService = gattService;
                                }

//                        if (gattService.getUuid().equals(BleConstant.UUID_LOG_SERVICE)) {
//                            SysUtils.logContentI(TAG, "初始化 LOG ===== = 发现服务");
//                            flag_log = true;
//                            mLogService = gattService;
//                        }

                                if (gattService.getUuid().equals(BleConstant.UUID_PROTOBUF_SERVICE)) {
                                    SysUtils.logContentI(TAG, "初始化 protobuf service");
                                    SysUtils.logAppRunning(TAG, "初始化 protobuf service");
                                    flag_protobuf = true;
                                    mProtobufService = gattService;
                                }

                            }
                        }
                        int delayTime = 100;
                        if (flag_sys) {
                            mBleHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    characteristic_system_read = mSYSTEMervice.getCharacteristic(BleConstant.UUID_BASE_READ);
                                    if (enableNotifacationSysRead()) {
                                        SysUtils.logContentI(TAG, "系统数据(小包) = 使能成功");
                                        SysUtils.logAppRunning(TAG, "系统数据(小包) = 使能成功");
                                    } else {
                                        SysUtils.logContentI(TAG, "系统数据(小包) = 使能失败");
                                        SysUtils.logAppRunning(TAG, "系统数据(小包) = 使能失败");
                                    }
                                }
                            }, delayTime);
                            delayTime += 400;
                        }
                        if (isSupportBigMtu) {
                            mBleHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    characteristic_big_3 = mBigService.getCharacteristic(BleConstant.CHAR_BIG_UUID_03);
                                    if (enableNotifacationSysRead()) {
                                        SysUtils.logContentI(TAG, "系统数据(大包) = 使能成功");
                                        SysUtils.logAppRunning(TAG, "系统数据(大包) = 使能成功");
                                    } else {
                                        SysUtils.logContentI(TAG, "系统数据(大包) = 使能失败");
                                        SysUtils.logAppRunning(TAG, "系统数据(大包) = 使能失败");
                                    }
                                }
                            }, delayTime);
                            delayTime += 400;
                        }

                        if (flag_ecg) {
                            // 延迟使能
                            mBleHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    characteristic_ecg_read = mECGService.getCharacteristic(BleConstant.UUID_ECG_READ);
                                    if (enableNotifacationEcgRead()) {
                                        SysUtils.logContentI(TAG, "ECG数据 = 使能成功");
                                        SysUtils.logAppRunning(TAG, "ECG数据 = 使能成功");
                                    } else {
                                        SysUtils.logContentI(TAG, "ECG数据 = 使能失败");
                                        SysUtils.logAppRunning(TAG, "ECG数据 = 使能失败");
                                    }
                                }
                            }, delayTime);
                            delayTime += 400;
                        }

                        if (flag_ppg) {
                            // 延迟使能
                            mBleHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    characteristic_ppg_read = mPPGService.getCharacteristic(BleConstant.UUID_PPG_READ);
                                    if (enableNotifacationPpgRead()) {
                                        SysUtils.logContentI(TAG, "PPG数据 = 使能成功");
                                        SysUtils.logAppRunning(TAG, "PPG数据 = 使能成功");
                                    } else {
                                        SysUtils.logContentI(TAG, "PPG数据 = 使能失败");
                                        SysUtils.logAppRunning(TAG, "PPG数据 = 使能失败");
                                    }
                                }
                            }, delayTime);
                            delayTime += 400;
                        }

                        if (flag_theme) {
                            // 延迟使能
                            mBleHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    characteristic_theme_read = mThemeService.getCharacteristic(BleConstant.UUID_THEME_READ);
                                    characteristic_theme_write = mThemeService.getCharacteristic(BleConstant.UUID_THEME_WRITE);
                                    if (enableNotifacationThemeRead()) {
                                        SysUtils.logContentI(TAG, "主题数据 = 使能成功");
                                        SysUtils.logAppRunning(TAG, "主题数据 = 使能成功");
                                    } else {
                                        SysUtils.logContentI(TAG, "主题数据 = 使能失败");
                                        SysUtils.logAppRunning(TAG, "主题数据 = 使能失败");
                                    }
                                }
                            }, delayTime);
                            delayTime += 400;
                        }

//                if (flag_log) {
//                    // 延迟使能
//                    mBleHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            characteristic_log_read = mLogService.getCharacteristic(BleConstant.UUID_LOG_READ);
//                            if (enableNotifacationLogRead()) {
//                                SysUtils.logContentI(TAG, "LOG数据 = 使能成功");
//                                TraceErrorLog.i("LOG数据 = 使能成功");
//                            } else {
//                                SysUtils.logContentI(TAG, "LOG数据 = 使能失败");
//                                TraceErrorLog.i("LOG数据 = 使能成功");
//                            }
//                        }
//                    }, 1350);
//                }

//                int delayTime = 1500;

                        Thread.sleep(delayTime);
                        if (flag_protobuf) {
                            characteristic_protobuf_1 = mProtobufService.getCharacteristic(BleConstant.CHAR_PROTOBUF_UUID_01);
                            characteristic_protobuf_2 = mProtobufService.getCharacteristic(BleConstant.CHAR_PROTOBUF_UUID_02);
                            characteristic_protobuf_3 = mProtobufService.getCharacteristic(BleConstant.CHAR_PROTOBUF_UUID_03);
                            characteristic_protobuf_4 = mProtobufService.getCharacteristic(BleConstant.CHAR_PROTOBUF_UUID_04);

                            if (enableNotificationBoolean("proto", mBluetoothGatt, characteristic_protobuf_1)) {
                                SysUtils.logContentI(TAG, "characteristic_protobuf_1 on success");
                            } else {
                                SysUtils.logContentI(TAG, "characteristic_protobuf_1  error");
                                SysUtils.logAppRunning(TAG, "characteristic_protobuf_1  error");
                                connectBleRXError();
                                return;
                            }

                            Thread.sleep(400);
                            if (enableNotificationBoolean("proto", mBluetoothGatt, characteristic_protobuf_2)) {
                                SysUtils.logContentI(TAG, "characteristic_protobuf_2 on success");
                            } else {
                                SysUtils.logContentI(TAG, "characteristic_protobuf_2 error");
                                SysUtils.logAppRunning(TAG, "characteristic_protobuf_2 error");
                                connectBleRXError();
                                return;
                            }
                            Thread.sleep(400);
                            if (enableNotificationBoolean("proto", mBluetoothGatt, characteristic_protobuf_3)) {
                                SysUtils.logContentI(TAG, "characteristic_protobuf_3 on success");
                            } else {
                                SysUtils.logContentI(TAG, "characteristic_protobuf_3  error");
                                SysUtils.logAppRunning(TAG, "characteristic_protobuf_3  error");
                                connectBleRXError();
                                return;
                            }

                            Thread.sleep(400);
                            if (enableNotificationBoolean("proto", mBluetoothGatt, characteristic_protobuf_4)) {
                                SysUtils.logContentI(TAG, "characteristic_protobuf_4 on success");
                            } else {
                                SysUtils.logContentI(TAG, "characteristic_protobuf_4  error");
                                SysUtils.logAppRunning(TAG, "characteristic_protobuf_4  error");
                                connectBleRXError();
                                return;
                            }
                            Thread.sleep(400);
                        } else {
                            if (!mBleDeviceTools.get_ble_name().contains(BleConstant.PLUS_HR)) {
                                connectBleRXError();
                                return;
                            }
                        }
                        //发送初始化数据
                        Thread.sleep(400);
                        if (flag_sys || isSupportBigMtu) {
                            resetBleCmdState(true);
                            bInitGattServices = true;

                            mBleHandler.post(() -> {
                                isFirstConnect = true;
                                BroadcastTools.broadcastDeviceConnectedDISCOVERSERVICES(getApplicationContext());
                                if (!BaseApplication.isScanActivity) { // 重连
                                    if (!mBleDeviceTools.get_ble_name().contains(BleConstant.PLUS_HR)) {
                                        mBindDeviceHandler = new Handler();
                                        mBindDeviceHandler.postDelayed(runnableTime, 3 * 1000);
                                        writeRXCharacteristic(BtSerializeation.bindDevice(1));
                                    } else {
                                        initDeviceCmd();
                                    }
                                }
                            });

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        SysUtils.logContentI(TAG, "onServicesDiscovered is Exception" + e);
                        SysUtils.logAppRunning(TAG, "onServicesDiscovered is Exception" + e);
                        connectBleRXError();
                    }
                }).start();

            } else {
                SysUtils.logContentI(TAG, "onServicesDiscovered is error");
                SysUtils.logAppRunning(TAG, "onServicesDiscovered is error");
            }
        }

        @Override
        public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {

        }


        //接收蓝牙传递过来的值
        public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            try {
                if (characteristic.getUuid().equals(BleConstant.CHAR_PROTOBUF_UUID_01)) {
                    broadcastUpdate(ACTION_DATA_AVAILABLE1, characteristic);
                } else if (characteristic.getUuid().equals(BleConstant.CHAR_PROTOBUF_UUID_02)) {
                    broadcastUpdate(ACTION_DATA_AVAILABLE2, characteristic);
                } else if (characteristic.getUuid().equals(BleConstant.CHAR_PROTOBUF_UUID_03)) {
                    broadcastUpdate(ACTION_DATA_AVAILABLE3, characteristic);
                } else if (characteristic.getUuid().equals(BleConstant.CHAR_PROTOBUF_UUID_04)) {
                    broadcastUpdate(ACTION_DATA_AVAILABLE4, characteristic);
                } else {
                    byte[] mydata = characteristic.getValue();
                    String my_hexString = BleTools.bytes2HexString(mydata);
                    //解析心电数据
                    //                if (characteristic.getUuid().equals(BleConstant.UUID_ECG_READ)) {
                    //                    SysUtils.logContentI(TAG, "onCharacteristicChanged = Ecg = " + my_hexString);
                    //                } else if (characteristic.getUuid().equals(BleConstant.UUID_PPG_READ)) {
                    //                    SysUtils.logContentI(TAG, "onCharacteristicChanged = Ppg = " + my_hexString);
                    //                } else if (characteristic.getUuid().equals(BleConstant.UUID_BASE_READ)) {
                    //                    SysUtils.logContentI(TAG, "onCharacteristicChanged = System = " + my_hexString);
                    //                } else if (characteristic.getUuid().equals(BleConstant.UUID_THEME_READ)) {
                    //                    SysUtils.logContentI(TAG, "onCharacteristicChanged = theme = " + my_hexString);
                    //                } else if (characteristic.getUuid().equals(BleConstant.UUID_LOG_READ)) {
                    //                    SysUtils.logContentI(TAG, "onCharacteristicChanged = Log = " + my_hexString);
                    //                }

                    SysUtils.logContentI(TAG, "onCharacteristicChanged  uuid = " + characteristic.getUuid() + " data = " + my_hexString);
                    SysUtils.logAppRunning(TAG, "onCharacteristicChanged  uuid = " + characteristic.getUuid() + " data = " + my_hexString);
                    if (mydata != null && mydata.length > 0) {
                        readingBleData(characteristic);
                    } else {
                        SysUtils.logContentE(TAG, "onCharacteristicChanged = mydata = null");
                        SysUtils.logAppRunning(TAG, "onCharacteristicChanged = mydata = null");
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                mBleBaseProtocol.setRcvDataState(0);
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            SysUtils.logContentI(TAG, " onMtuChanged = " + mtu + " status = " + status);
            SysUtils.logAppRunning(TAG, " onMtuChanged = " + mtu + " status = " + status);
        }
    }

    private Handler mBindDeviceHandler;
    Runnable runnableTime = () -> {
        Log.w(TAG, " bindDevice time out");
        initDeviceCmd();
    };

    public final static String EXTRA_DATA = Constants.APP_NAME + "bluetooth.EXTRA_DATA";
    public final static String ACTION_DATA_AVAILABLE1 = Constants.APP_NAME + "com.example.protobuf.bluetooth.ACTION_DATA_AVAILABLE";
    public final static String ACTION_DATA_AVAILABLE2 = Constants.APP_NAME + "com.example.protobuf.bluetooth.ACTION_DATA_AVAILABLE2";
    public final static String ACTION_DATA_AVAILABLE3 = Constants.APP_NAME + "com.example.protobuf.bluetooth.ACTION_DATA_AVAILABLE3";
    public final static String ACTION_DATA_AVAILABLE4 = Constants.APP_NAME + "com.example.protobuf.bluetooth.ACTION_DATA_AVAILABLE4";

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        try {
//            final Intent intent = new Intent(action);
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));

//                intent.putExtra(EXTRA_DATA, stringBuilder.toString());

                lastDisplayTime = System.currentTimeMillis();
                switch (action) {
                    case ACTION_DATA_AVAILABLE1:
                        displayData1(stringBuilder.toString());
                        break;
                    case ACTION_DATA_AVAILABLE2:
                        displayData2(stringBuilder.toString());
                        break;
                    case ACTION_DATA_AVAILABLE3:
                        displayData3(stringBuilder.toString());
                        break;
                    case ACTION_DATA_AVAILABLE4:
                        displayData4(stringBuilder.toString());
                        break;
                }
            }
//            sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    File log_bin = null;

    /**
     * 解析手环返回的数据内容
     *
     * @param data
     */

    private void parseBleData(byte[] data) {
        try {

            String Magic = String.format("%02X ", data[0]);
            String Command = String.format("%02X ", data[8]);
            String Key = String.format("%02X ", data[10]);

            SysUtils.logContentI(TAG, "parseRcvData Magic=" + Magic + "  Command=" + Command + "  Key=" + Key);
            SysUtils.logAppRunning(TAG, "parseRcvData Magic=" + Magic + "  Command=" + Command + "  Key=" + Key);

            if (data[0] != (byte) 0xab)
                return;
            if (data[8] != 3)
                return;

            switch (data[10]) {
                // 返回运动数据
                case BleConstant.Key_Motion: {
                    MyLog.i(TAG, "蓝牙回调 - 运动数据");
                    HandleDeviceDataTools.HandleMotion(mMovementInfoUtils, data);
                }
                break;

                // 睡眠数据
                case BleConstant.Key_Sleep: {
                    MyLog.i(TAG, "蓝牙回调 - 睡眠数据");
                    HandleDeviceDataTools.HandleSleep(mSleepInfoUtils, data);
                }
                break;

                // 数据返回完成
                case BleConstant.Key_Complete: {
                    MyLog.i(TAG, "蓝牙回调 - 数据完成");
                    requestServerTools.syncWatchTime(this, requestServerTools.SYNC_TIME_END_TAG);
                    mBleDeviceTools.setLastSyncTime(System.currentTimeMillis());
                    if (mSyncTimeoutHandle != null) {
                        mSyncTimeoutHandle.removeCallbacksAndMessages(null);
                    }
                    getOtherData();

                    long lastTime = mBleDeviceTools.getLastUploadDataServiceTime();
                    if (System.currentTimeMillis() - lastTime > 60 * 3600 * 1000L) {
                        requestServerTools.uploadSportData(getApplicationContext(), mMovementInfoUtils);
                        requestServerTools.uploadSleepData(getApplicationContext(), mSleepInfoUtils);
                        requestServerTools.uploadHeartData(getApplicationContext(), mHeartInfoUtils);
                        requestServerTools.uploadContinuitySpo2Data(getApplicationContext(), mContinuitySpo2InfoUtils);
                        requestServerTools.uploadContinuityTempData(getApplicationContext(), mContinuityTempInfoUtils);
                        requestServerTools.updateListHealthyData(getApplicationContext(), mHealthInfoUtils);
                        requestServerTools.updateMeasureSpo2ListData(getApplicationContext(), mMeasureSpo2InfoUtils);
                        requestServerTools.updateMeasureTempListData(getApplicationContext(), mMeasureTempInfoUtils);
                        mBleDeviceTools.setLastUploadDataServiceTime(System.currentTimeMillis());
                    }

                }
                break;

                //整点心率
                case BleConstant.Key_PoHeart: {
                    MyLog.i(TAG, "蓝牙回调 - 整点心率");
                    HandleDeviceDataTools.HandlePoHeart(mHeartInfoUtils, data);
                }
                break;

                // 遥控拍照
                case BleConstant.Key_Photo: {
                    MyLog.i(TAG, "蓝牙回调 - 遥控拍照");
                    initTakePhoto();
                }
                break;

                //找手机
                case BleConstant.Key_FindPhone: {
                    MyLog.i(TAG, "蓝牙回调 - 找手机");
                    HandleFindPhone();
                }
                break;

                //设备信息
                case BleConstant.Key_DeviceInfo: {
                    MyLog.i(TAG, "蓝牙回调 - 设备信息");
                    HandleDeviceDataTools.handleDeviceInfo(this, mBleDeviceTools, data);
                    requestServerTools.uploadDeviceData(getApplicationContext());
                }
                break;

                //MAC地址
                case BleConstant.Key_DeviceMac: {
                    MyLog.i(TAG, "蓝牙回调 - Mac地址");
                }
                break;

                //一键测量返回心率
                case BleConstant.Key_MeasuringHeart: {
                    MyLog.i(TAG, "蓝牙回调 - 一键测量返回心率");
                    int heartVal = data[13] & 0xFF;
                    MyLog.i(TAG, "蓝牙回调 - heartVal = " + heartVal);
                    BroadcastTools.broadcastHeartData(getApplicationContext(), heartVal);
                }
                break;

                //来电拒接
                case BleConstant.Key_HangPhone: {
                    MyLog.i(TAG, "蓝牙回调 - 来电拒接");
                    PhoneUtil.endCall(this);
                    //                PhoneUtil.SetMuteMode(BleService.this, AudioManager.RINGER_MODE_SILENT);
                }
                break;

                //连续心率
                case BleConstant.Key_MoHeart: {
                    MyLog.i(TAG, "蓝牙回调 - 连续心率");
                    HandleDeviceDataTools.HandleWoHeart(mHeartInfoUtils, data);
                }
                break;

                //屏幕信息相关
                case BleConstant.Key_ScreensaverInfo: {
                    HandleDeviceDataTools.handleScreensaverInfo(mBleDeviceTools, data);
                    setDeviceScreensaverInfo();
                }
                break;

                //设置屏保成功
                case BleConstant.Key_ScreensaverIsSuccess: {
                    SysUtils.logContentI(TAG, "ble 同步数据 = 设置屏保是成功");
                    int is_success = data[13] & 0xff;
                    MyLog.i(TAG, "ble 同步数据 = is_success = " + is_success);
                    if (is_success == 1) {
                        MyLog.i(TAG, "ble 同步数据 = yes");
                        BroadcastTools.broadcastSetScreensaverSuccess(getApplicationContext());
                    } else {
                        MyLog.i(TAG, "ble 同步数据 = no");
                        BroadcastTools.broadcastSetScreensaverFail(getApplicationContext());
                    }
                }
                break;

                //离线血压
                case BleConstant.Key_OffLineBpInfo: {
                    MyLog.i(TAG, "蓝牙回调 - 血压测量");
                    //                MyLog.i(TAG, "蓝牙回调 ble 同步数据 = 血压测量-原始数据 = " + BleTools.bytes2HexString(data));
                    HandleDeviceDataTools.handleOffLineBpInfo(getApplicationContext(), mHealthInfoUtils, data);
                }
                break;

                //无效睡眠
                case BleConstant.Key_IneffectiveSleep: {
                    MyLog.i(TAG, "蓝牙回调 - 无效睡眠-原始数据");
                    //                MyLog.i(TAG, "蓝牙回调 ble 同步数据 = 无效睡眠-原始数据 = " + BleTools.bytes2HexString(data));
                }
                break;

                //多运动模式
                case BleConstant.Key_DeiceMotion: {
                    MyLog.i(TAG, "蓝牙回调 - 多运动模式");
                    //                MyLog.i(TAG, "蓝牙回调 ble 同步数据 = 多运动模式-原始数据 = " + BleTools.bytes2HexString(data));
                    HandleDeviceDataTools.handleDeviceSportInfo(mSportModleInfoUtils, data);
                }
                break;

                //音乐控制
                case BleConstant.Key_MusicControlCmd: {
                    if (!mBleDeviceTools.get_device_music_control()) {
                        MyLog.i(TAG, " not support music");
                        break;
                    }
                    MyLog.i(TAG, "ble 同步数据 = 音乐控制-原始数据 = " + BleTools.bytes2HexString(data));
                    int music_control_cmd = data[13] & 0xff;
                    MyLog.i(TAG, "ble 同步数据 = 音乐控制-music_control_cmd = " + music_control_cmd);

                    switch (music_control_cmd) {

                        //获取音乐信息
                        case BleConstant.Key_MusicControlGet:

                            // 延迟使能
                            mBleHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getMusicInfo();
                                }
                            }, 200);

                            break;

                        //播放
                        case BleConstant.Key_MusicControlPlay:
                            //暂停
                        case BleConstant.Key_MusicControlSuspend:
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                if (MusicSyncManager.getInstance().isSessionDestroyed) {
                                    // 延迟使能
                                    mBleHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            getMusicInfo();
                                        }
                                    }, 1000);
                                }
                            }
                            SysUtils.controlMusic(this, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                            break;
                        //上一首
                        case BleConstant.Key_MusicControlLastOne:
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                if (MusicSyncManager.getInstance().isSessionDestroyed) {
                                    // 延迟使能
                                    mBleHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            getMusicInfo();
                                        }
                                    }, 1000);
                                }
                            }
                            SysUtils.controlMusic(this, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                            break;
                        //下一首
                        case BleConstant.Key_MusicControlNextOne:
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                if (MusicSyncManager.getInstance().isSessionDestroyed) {
                                    // 延迟使能
                                    mBleHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            getMusicInfo();
                                        }
                                    }, 1000);
                                }
                            }
                            SysUtils.controlMusic(this, KeyEvent.KEYCODE_MEDIA_NEXT);
                            break;
                        //音量调高
                        case BleConstant.Key_MusicControlVolumeTop:
                            boolean is_success = onKeyDown(KeyEvent.KEYCODE_VOLUME_UP, null);
                            MyLog.i(TAG, "music control = voiceUp = " + is_success);
                            break;
                        //音量调低
                        case BleConstant.Key_MusicControlVolumeDown:
                            boolean is_success_Down = onKeyDown(KeyEvent.KEYCODE_VOLUME_DOWN, null);
                            MyLog.i(TAG, "music control = voiceDown = " + is_success_Down);
                            break;
                    }


                }
                break;

                //主题传输信息
                case BleConstant.Key_ThemeTransmissionInfo: {
                    MyLog.i(TAG, "蓝牙回调 = 主题传输信息");
                    HandleDeviceDataTools.handleDeviceThemeTransmissionInfo(mBleDeviceTools, data);
                    BroadcastTools.broadcastDialComplete(getApplicationContext());
                    isDeviceThemeInfo = true;
                }
                break;

                //主题MTU
                case BleConstant.Key_MtuInfo: {
                    MyLog.i(TAG, "蓝牙回调 = 主题MTU");
                    int mtu = HandleDeviceDataTools.handleDeviceMtuInfo(mBleDeviceTools, data);
                    if (isSupportBigMtu) {
                        currentMtu = mtu;
                    }
                    isDeviceMtu = true;
                }
                break;

                //经典蓝牙设备信息
                case BleConstant.Key_CallDeviceInfo: {
                    MyLog.i(TAG, "蓝牙回调 = 通话设备信息");
                    HandleDeviceDataTools.handleCallDeviceInfo(mBleDeviceTools, data);
                    BroadcastTools.broadcastCallDeviceInfo(getApplicationContext());
                    iCallDeviceInfo = true;
                }
                break;

                case BleConstant.Key_ContinuitySpo2: {
                    MyLog.i(TAG, "蓝牙回调 = 连续血氧");
                    HandleDeviceDataTools.HandleContinuitySpo2(mContinuitySpo2InfoUtils, data);
                }
                break;

                case BleConstant.Key_ContinuityTemp: {
                    MyLog.i(TAG, "蓝牙回调 = 连续体温");
                    HandleDeviceDataTools.HandleContinuityTemp(mContinuityTempInfoUtils, data);
                }
                break;

                case BleConstant.Key_OffMeasureSpo2: {
                    MyLog.i(TAG, "蓝牙回调 = 离线血氧");
                    HandleDeviceDataTools.HandleMeasureSpo2(getApplicationContext(), mMeasureSpo2InfoUtils, data);
                }
                break;

                case BleConstant.Key_OffMeasureTemp: {
                    MyLog.i(TAG, "蓝牙回调 = 离线体温");
                    HandleDeviceDataTools.HandleMeasureTemp(getApplicationContext(), mMeasureTempInfoUtils, data);
                }
                break;

                case BleConstant.Key_UserBehavior: {
                    MyLog.i(TAG, "蓝牙回调 = 用户行为");
                    HandleDeviceDataTools.HandleUserBehavior(getApplicationContext(), data, mBleDeviceTools.get_ble_name() + "_" + mBleDeviceTools.get_ble_mac());
                }
                break;

                case (byte) BleConstant.KEY_DEVICE_ANSWER:
                    String key = Integer.toHexString(data[13] & 0xff);
                    int status = data[14] & 0xff;
                    MyLog.i(TAG, "KEY_DEVICE_ANSWER device is answer key = " + key + " status = " + status);
                    isSending = false;
                    break;
                //设备基本参数
                case BleConstant.Key_DeviceBasicInfo:
                    MyLog.i(TAG, "蓝牙回调 = 设备基本参数");
                    int mtu = HandleDeviceDataTools.handleDeviceBasicInfo(mBleDeviceTools, data);
                    if (isSupportBigMtu) {
                        currentMtu = mtu;
                    }
                    break;
                case BleConstant.Key_DeviceSendUnbind:
                    MyLog.i(TAG, "Key_DeviceSendUnbind = device send unbind ");
                    disconnect();
                    DeviceManager.getInstance().unBind(this, new DeviceManager.DeviceManagerListen() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                        }
                    });
                    DeviceManager.getInstance().unBind(this, null);
                    break;
                case BleConstant.Key_DeviceBindInfo:
                    deviceBindInfo(data);
                    break;

                case BleConstant.Key_DeviceToAppSport:
                    devicetoAppSportInfo(data);
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deviceBindInfo(byte[] data) {
        int type = data[13] & 0xff;
        int result = data[14] & 0xff;
        MyLog.i(TAG, "Key_DeviceSendUnbind = device send bind info " + " type = " + type + " result = " + result);
        SysUtils.logAppRunning(TAG, "Key_DeviceSendUnbind = device send bind info " + " type = " + type + " result = " + result);

        if (0 == type) { // 绑定
            switch (result) {
                case 0: // 拒绝
                    deniedByDevice();
                    break;
                case 1: // 同意
                    BroadcastTools.broadcastDeviceConnectedBIND_SUCCESS(getApplicationContext());
                    break;
            }

        } else if (1 == type) { // 询问状态
            if (mBindDeviceHandler != null) {
                mBindDeviceHandler.removeCallbacksAndMessages(null);
            }
            switch (result) {
                case 0: // 解除绑定
                    deniedByDevice();
                    break;
                case 1: // 同意
                    initDeviceCmd();
                    BroadcastTools.broadcastDeviceConnectedBIND_SUCCESS(getApplicationContext());
                    break;
            }

        }
    }

    private void devicetoAppSportInfo(byte[] data) {
        if (!mBleDeviceTools.getIsSupportAppAuxiliarySport()) {
            MyLog.i(TAG, "devicetoAppSportInfo = no support");
            return;
        }
        int model = data[13] & 0xff;
        int cmd = data[14] & 0xff;
        MyLog.i(TAG, "devicetoAppSportInfo = model = " + model + " cmd = " + cmd);
        //模式A=返回距离
        if (model == 0) {
            switch (cmd) {
                case 0: // 发起运动
                    BroadcastTools.broadcastDevicetoAppSportState(getApplicationContext(), BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_START);
                    break;
                case 1: // 运动已暂停
                    BroadcastTools.broadcastDevicetoAppSportState(getApplicationContext(), BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_PAUSE);
                    break;
                case 2: // 运动继续
                    BroadcastTools.broadcastDevicetoAppSportState(getApplicationContext(), BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_RESUME);
                    break;
                case 3: // 结束运动
                    BroadcastTools.broadcastDevicetoAppSportState(getApplicationContext(), BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_STOP);
                    break;
                case 4: // 正在运动中…
                    BroadcastTools.broadcastDevicetoAppSportState(getApplicationContext(), BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_RESULT_YES);
                    break;
                case 5: // 非运动状态…
                    BroadcastTools.broadcastDevicetoAppSportState(getApplicationContext(), BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_RESULT_NO);
                    break;
            }
        }
    }

    private void deniedByDevice() {
        disconnect();
        DeviceManager.getInstance().unBind(this, new DeviceManager.DeviceManagerListen() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

            }
        });
        DeviceManager.getInstance().unBind(this, null);
        BroadcastTools.broadcastDeviceConnectedBIND_ERROR(getApplicationContext());
    }

    private boolean isInitTimeZone = false;

    private void initTimeZone() {
        try {
            if (mBleDeviceTools.getIsSupportProtobuf() && mBleDeviceTools.getPointExercise()) {
                SimpleDateFormat sdfZone = new SimpleDateFormat("Z", Locale.getDefault());
                String timezone = sdfZone.format(new Date());
                int minute = Integer.parseInt(timezone.substring(3));
                String symbol = timezone.substring(0, 1);
                int hour = Integer.parseInt(timezone.substring(1, 3));

                int timeZone = hour * 4 + minute * 4 / 60;
                if (symbol.equalsIgnoreCase("+")) {
                } else {
                    timeZone = -timeZone;
                }

                byte[] dataValue = new byte[]{(byte) timeZone};
                bleCmdList.add(BtSerializeation.getBleData(dataValue, BtSerializeation.CMD_01, BtSerializeation.KEY_SET_TIMEZONE));

                isInitTimeZone = true;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    private void broadcastUpdate(String action, int state) {
        final Intent intent = new Intent(action);
        intent.putExtra("extra_data", state);
        sendBroadcast(intent);
    }

    /**
     * 广播监听，接收广播，然后发送指令给设备
     */
    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            //蓝牙广播状态
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                    SysUtils.logContentW(TAG, "BluetoothAdapter.ACTION_STATE_CHANGED STATE_OFF");
                    SysUtils.logAppRunning(TAG, "BluetoothAdapter.ACTION_STATE_CHANGED STATE_OFF");
                    broadcastUpdate(BroadcastTools.ACTION_BLUE_STATE_CHANGED, BluetoothAdapter.STATE_OFF);
                    disconnect();
                }
                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    SysUtils.logContentW(TAG, "BluetoothAdapter.ACTION_STATE_CHANGED STATE_ON");
                    SysUtils.logAppRunning(TAG, "BluetoothAdapter.ACTION_STATE_CHANGED STATE_ON");
                    broadcastUpdate(BroadcastTools.ACTION_BLUE_STATE_CHANGED, BluetoothAdapter.STATE_ON);
                    timeOutNum = 0;
                    reconnectDevcie();
                }
            }
            //消息推送
            else if (action.equals(BroadcastTools.ACTION_NOTIFICATION_SEND_DATA)) {
                byte[] neirong1 = intent.getExtras().getByteArray(BroadcastTools.INTENT_PUT_MSG);
                if (neirong1 != null) {
                    SysUtils.logContentE(TAG, " send notification =" + BleTools.bytes2HexString(neirong1));
                    writeRXCharacteristic(neirong1);
                }
            }
            //个人信息
            else if (action.equals(BroadcastTools.ACTION_NOTIFICATION_SEND_SET_USER_INFO)) {
                writeRXCharacteristic(BtSerializeation.setUserProfile(mUserSetTools.get_user_sex(), 24, mUserSetTools.get_user_height(), mUserSetTools.get_user_weight()));
                MyLog.i(TAG, "上传个人信息到BLE =  收到了-发送身高 = " + mUserSetTools.get_user_height());
                MyLog.i(TAG, "上传个人信息到BLE =  收到了-发送体重 = " + mUserSetTools.get_user_weight());
            }
            //运动目标
            else if (action.equals(BroadcastTools.ACTION_NOTIFICATION_SEND_SET_TARGET_STEP)) {
//                String target_steps = JavaUtil.checkIsNull(mUserSetTools.get_user_exercise_target()) ? mUserSetTools.get_user_exercise_target() : String.valueOf(DefaultVale.USER_SPORT_TARGET);
                String target_steps = JavaUtil.checkIsNull(mUserSetTools.get_user_exercise_target()) ? String.valueOf(DefaultVale.USER_SPORT_TARGET) : mUserSetTools.get_user_exercise_target();
                writeRXCharacteristic(BtSerializeation.setTargetStep(Integer.valueOf(target_steps)));
                MyLog.i(TAG, "上传个人信息到BLE =  收到了-目标 = " + target_steps);
            }
            //主题
            else if (action.equals(BroadcastTools.ACTION_NOTIFICATION_SEND_SET_DEVICE_THEME)) {
                int theme = mBleDeviceTools.get_device_theme();
                MyLog.i(TAG, "上传设备主题到BLE =  收到了-主题 = " + theme);
                writeRXCharacteristic(BtSerializeation.setDeviceTheme(theme));
            }
            //肤色
            else if (action.equals(BroadcastTools.ACTION_NOTIFICATION_SEND_SET_SKIN_COLOUR)) {
                int skin = mBleDeviceTools.get_skin_colour();
                MyLog.i(TAG, "上传设置肤色到BLE =  收到了-肤色 = " + skin);
                writeRXCharacteristic(BtSerializeation.setDeviceSkinColour(skin));
            }
            //亮度等级
            else if (action.equals(BroadcastTools.ACTION_NOTIFICATION_SEND_SET_SCREEN_BRIGHESS)) {
                int screen_brighess = mBleDeviceTools.get_screen_brightness();
                MyLog.i(TAG, "上传设置亮度等级=  收到了-亮屏等级 = " + screen_brighess);
                writeRXCharacteristic(BtSerializeation.setScreenBrightness(screen_brighess));
            }
            //亮屏时间
            else if (action.equals(BroadcastTools.ACTION_NOTIFICATION_SEND_SET_BRIHNESS_TIME)) {
                int briness_time = mBleDeviceTools.get_brightness_time();
                MyLog.i(TAG, "上传设置亮屏时间 =  收到了-亮屏时间 = " + briness_time);
                writeRXCharacteristic(BtSerializeation.setBrightnessTime(briness_time));
            }
            //生理周期
            else if (action.equals(BroadcastTools.ACTION_NOTIFICATION_SEND_SET_CYCLE)) {
                MyLog.i(TAG, "上传设备 生理周期数据");
                setDeviceCycleInfo();
            } else if (action.equals(VOLUME_CHANGED_ACTION)) {
//                MyLog.i(TAG, "音乐控制 音量 = VOLUME_CHANGED_ACTION=====");
                int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//                MyLog.i(TAG, "音乐控制 音量 = VOLUME_CHANGED_ACTION = max = " + max);
//                MyLog.i(TAG, "音乐控制 音量 = VOLUME_CHANGED_ACTION = current = " + current);
                onVolumeUpdate(current, max);
            } else if (ACTION_DATA_AVAILABLE2.equals(action)) {
                lastDisplayTime = System.currentTimeMillis();
                displayData2(intent.getStringExtra(EXTRA_DATA));
            } else if (ACTION_DATA_AVAILABLE1.equals(action)) {
                lastDisplayTime = System.currentTimeMillis();
                displayData1(intent.getStringExtra(EXTRA_DATA));
            } else if (ACTION_DATA_AVAILABLE3.equals(action)) {
                lastDisplayTime = System.currentTimeMillis();
                displayData3(intent.getStringExtra(EXTRA_DATA));
            } else if (ACTION_DATA_AVAILABLE4.equals(action)) {
                lastDisplayTime = System.currentTimeMillis();
                displayData4(intent.getStringExtra(EXTRA_DATA));
            } else if (Intent.ACTION_LOCALE_CHANGED.equals(action)) {
                MyLog.i(TAG, " Language change");
                boolean connectState = ISBlueToothConnect();
                if (connectState && !syncState) {
                    MyLog.i(TAG, " send Language cmd because Language change");
                    int country = AppUtils.getCountry(BleService.this);
                    final byte[] init_language = BtSerializeation.SendNewInitDevice(country);
                    writeRXCharacteristic(init_language);
                }
            } else if (BroadcastTools.ACTION_CMD_GET_SPORT.equals(action)) {
                getNextProtoDeviceSport();
            }
        }
    }


    /**
     * 获取蓝牙连接状态
     *
     * @return
     */
    private static int getBlueToothStatus() {
        return mConnectionState;
    }

    /**
     * 设置蓝牙状态
     *
     * @param state
     */
    public static void setBlueToothStatus(int state) {
        mConnectionState = state;
    }


    /**
     * 蓝牙是否已连接
     *
     * @return
     */
    private static boolean ISBlueToothConnect() {
        return BleService.getBlueToothStatus() == BleConstant.STATE_CONNECTED;
    }

    //来电
    public void notiface_phone(String call_start) {
        try {
            writeRXCharacteristic(BtSerializeation.notifyIncommingCall(call_start));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //G20来电
    public void notiface_g20_phone(String call_start) {
        try {
            writeRXCharacteristic(BtSerializeation.notifyIncommingCallG20(call_start));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    //恢复出厂设置
    public void restore_factory() {
        writeRXCharacteristic(BtSerializeation.resetFactory());
    }


    //上传健康数据
    public void ResultMeasureHeart(HealthInfo mHealthInfo) {
        if (mHealthInfo != null) {
            writeRXCharacteristic(BtSerializeation.result_heart(Integer.valueOf(mHealthInfo.getHealth_heart())
                    , Integer.valueOf(mHealthInfo.getHealth_systolic())
                    , Integer.valueOf(mHealthInfo.getHealth_diastolic())));
        }
    }


    //上传校准心率
    public void ResultCalibrationHeart() {
        int user_heart = mUserSetTools.get_calibration_heart();
        int user_systolic = mUserSetTools.get_calibration_systolic();
        writeRXCharacteristic(BtSerializeation.setUserPar(user_systolic, user_heart));
    }

    //打开心电
    public void openEcg() {
        writeRXCharacteristic(BtSerializeation.OpenEcg());
    }

    //关闭心电
    public void closeEcg() {
        writeRXCharacteristic(BtSerializeation.CloseEcg());
    }


    /**
     * Retrieves a list of supported GATT services on the connected device. This
     * should be invoked only after {@code BluetoothGatt#discoverServices()}
     * completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null)
            return null;
        return mBluetoothGatt.getServices();
    }


    public boolean enableNotifacationSysRead() {
        boolean is_enable;
        if (isSupportBigMtu) {
            is_enable = enableNotificationBoolean("big", mBluetoothGatt, characteristic_big_3);
        } else {
            is_enable = enableNotificationBoolean("sys", mBluetoothGatt, characteristic_system_read);
        }
        setEnableNorticeSysTag(is_enable);
        return is_enable;
    }

    public boolean enableNotifacationEcgRead() {
        boolean is_enable = enableNotificationBoolean("ecg", mBluetoothGatt, characteristic_ecg_read);
        setEnableNorticeEcgTag(is_enable);
        return is_enable;
    }

    public boolean enableNotifacationPpgRead() {
        boolean is_enable = enableNotificationBoolean("ppg", mBluetoothGatt, characteristic_ppg_read);
        setEnableNorticePpgTag(is_enable);
        return is_enable;
    }

    public boolean enableNotifacationThemeRead() {
        boolean is_enable = enableNotificationBoolean("theme", mBluetoothGatt, characteristic_theme_read);
        setEnableNorticeThemeTag(is_enable);
        return is_enable;
    }

    public boolean enableNotifacationLogRead() {
        boolean is_enable = enableNotificationBoolean("log", mBluetoothGatt, characteristic_log_read);
        setEnableNorticeLogTag(is_enable);
        return is_enable;
    }

    /**
     * 使能UUID
     *
     * @param tag
     * @param Gatt
     * @param Characteristic
     */
    private boolean enableNotificationBoolean(String tag, BluetoothGatt Gatt, BluetoothGattCharacteristic Characteristic) {

        SysUtils.logContentI(TAG, "enableNotificationBoolean = tag = " + tag);

        try {
            if (Gatt != null) {
                if (Characteristic != null) {
                    Gatt.setCharacteristicNotification(Characteristic, true);
                    final BluetoothGattDescriptor descriptor = Characteristic.getDescriptor(BleConstant.CCCD);
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

                    boolean result = Gatt.writeDescriptor(descriptor);
                    SysUtils.logContentI(TAG, "enableNotificationBoolean 00 = return = " + result);
                    return result;
                } else {
                    SysUtils.logContentI(TAG, "enableNotificationBoolean 11 = return = " + false);
                    return false;
                }
            } else {
                SysUtils.logContentI(TAG, "enableNotificationBoolean 22 = return = " + false);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // 计时器
    private Handler OffLineOverHandler = new Handler() {
        /*
         * edit by yuanjingchao 2014-08-04 19:10
         */
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 添加更新ui的代码
                    if (!OffLineOverIsStop) {
                        MyLog.i(TAG, "离线心电数据 = 计时器 继续  = number =" + OffLineOverMaxtime);

                        OffLineOverMaxtime -= 1;
                        OffLineOverIsStop = true;
                        OffLineOverHandler.sendEmptyMessageDelayed(1, 1000);

                        if (OffLineOverMaxtime < 1) {
                            MyLog.i(TAG, "离线心电数据 = 计时器 次数到了 ");
                            OffLineOverStop();
                        }
                    } else {
                        MyLog.i(TAG, "离线心电数据 = 计时器 不继续 ");
                        OffLineOverStop();
                    }
                    break;
                case 0:
                    break;
            }
        }

    };

    public void OffLineOverStart() {
        OffLineOverHandler.removeMessages(1);
        BroadcastTools.broadcastOffLineEcgStart(getApplicationContext());
        OffLineOverMaxtime = 25;
        MyLog.i(TAG, "离线心电数据 = 计时器 START ");
        OffLineOverHandler.sendEmptyMessage(1);
        OffLineOverIsStop = false;

    }

    public void OffLineOverStop() {
        BroadcastTools.broadcastOffLineEcgEnd(getApplicationContext());
        MyLog.i(TAG, "离线心电数据 = 计时器 STOP ");
        OffLineOverHandler.sendEmptyMessage(0);
        OffLineOverIsStop = true;
    }


    void initOffLineEcg() {
        initEcgData();
        OffLineEcgData = new StringBuilder();
        OffLineEcgTime = "";
        OffLineEcgNowPageSize = 0;
    }

    public void sendImageHead(int width, int height, byte crc) {
        writeImagerCharacteristic(BtSerializeation.getImageHead(width, height, crc));

    }

    /**
     * 写指令到蓝牙设备
     *
     * @param value
     * @return
     */
    public boolean writeImagerCharacteristic(final byte[] value) {
        boolean status = false;
        if (mBluetoothGatt == null)
            return false;
        final BluetoothGattService RxService = mBluetoothGatt.getService(BleConstant.UUID_IMAGER_SERVICE);
        if (RxService == null) {
            return status;
        }
        final BluetoothGattCharacteristic bluetoothGattCharacteristic = RxService.getCharacteristic(BleConstant.UUID_IMAGER_WRITE);
        if (bluetoothGattCharacteristic == null) {
            return status;
        }
        bluetoothGattCharacteristic.setValue(value);
        status = mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
        return status;
    }

    public void checkEnableSysRead() {
        if (isServicesDiscovered()) {
//            MyLog.i(TAG, "系统服务 已初始化");
            if (!isEnableNorticeSysTag()) {
                MyLog.i(TAG, "系统服务 未使能-调用使能");
                enableNotifacationSysRead();
            } else {
//                MyLog.i(TAG, "系统服务 已经使能");
            }
        } else {
            MyLog.i(TAG, "系统服务 未初始化");
        }
    }


    //同步时间
    public boolean syncTime() {
        requestServerTools.syncWatchTime(this, requestServerTools.SYNC_TIME_START_TAG);

        if (!isSupportBigMtu) {
            syncState = true;
            if (mSyncTimeoutHandle != null) {
                mSyncTimeoutHandle.removeCallbacksAndMessages(null);
                mSyncTimeoutHandle.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        syncState = false;
                        SysUtils.logContentI(TAG, "sync data time out");
                        BroadcastTools.broadcastSyncTimeOut(getApplicationContext());
                    }
                }, Constants.SYNC_TIMEOUT);
            }
        }

        checkEnableSysRead();
        clearData();
        //同步中广播
        BroadcastTools.broadcastSyncLoading(getApplicationContext());
        SysUtils.logContentI(TAG, "sync data start");
        return writeRXCharacteristic(BtSerializeation.syncTime());
    }

    //获取设备信息
    public boolean getDeviceInfo() {
        checkEnableSysRead();
        clearData();
        SysUtils.logContentI(TAG, "获取设备信息");
        return writeRXCharacteristic(BtSerializeation.getDeviceVersion());
    }

    //获取屏保信息
    public boolean getDeviceScreensaverInfo() {
        checkEnableSysRead();
        clearData();
        SysUtils.logContentI(TAG, "获取屏保信息");
        return writeRXCharacteristic(BtSerializeation.getDeviceScreensaverInfo());
    }

    public void getDerviceScreensaverInfoDelayTime(int delay_time) {
        // 延迟使能
        mBleHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDeviceScreensaverInfo();
            }
        }, delay_time);
    }

    //设置屏保信息
    public boolean setDeviceScreensaverInfo() {
        SysUtils.logContentI(TAG, "设置屏保设置");
        return writeRXCharacteristic(BtSerializeation.ScreensaverSet(mBleDeviceTools));
    }


    /**
     * 发送屏保-头
     *
     * @param value
     * @return
     */
    public boolean send_image_data(byte[] value) {
        return writeImagerCharacteristic(value);
    }

    //设置生理周期
    public boolean setDeviceCycleInfo() {
        String sex = String.valueOf(mUserSetTools.get_user_sex());
        return writeRXCharacteristic(BtSerializeation.setCycle(sex, mUserSetTools, mBleDeviceTools));
    }

    //初始化设备信息
    void initDeviceCmd() {
        int taiwan = mBleDeviceTools.get_taiwan() ? 1 : 0;//抬腕亮屏
        int zhuanwan = 0;//转腕切屏，已作废，直接发送0
        int heart = mBleDeviceTools.get_point_measurement_heart() ? 1 : 0;//整点心率开关
        int miandarao = mBleDeviceTools.get_not_disturb() ? 1 : 0;//免打扰
        int new_heart = mBleDeviceTools.get_persist_measurement_heart() ? 1 : 0;//连续心率开关
        int theme = mBleDeviceTools.get_device_theme();//主题选择开关
        int skin = mBleDeviceTools.get_skin_colour();//肤色选择开关
        int screen_brighess = mBleDeviceTools.get_screen_brightness();//屏幕亮度登录
        int briness_time = mBleDeviceTools.get_brightness_time();//亮屏时间
        int time = mBleDeviceTools.get_colock_type() == 1 ? 1 : 0;
        int unit = mBleDeviceTools.get_device_unit() == 1 ? 1 : 0;
        int temperatureType = mBleDeviceTools.getTemperatureType() == 1 ? 1 : 0;//温度单位
        int CountinuityTemp = mBleDeviceTools.get_continuity_temp() ? 1 : 0;//连续体温
        int calibration_systolic = mUserSetTools.get_calibration_systolic() > 0 ? mUserSetTools.get_calibration_systolic() : DefaultVale.USER_SYSTOLIC;
        int calibration_heart = mUserSetTools.get_calibration_heart() > 0 ? mUserSetTools.get_calibration_heart() : DefaultVale.USER_HEART;
        int user_height = mUserSetTools.get_user_height() > 0 ? mUserSetTools.get_user_height() : DefaultVale.USER_HEIGHT;
        int user_weight = mUserSetTools.get_user_weight() > 0 ? mUserSetTools.get_user_weight() : DefaultVale.USER_WEIGHT;
        int country = AppUtils.getCountry(BleService.this);

//        String target_steps = JavaUtil.checkIsNull(mUserSetTools.get_user_exercise_target()) ? mUserSetTools.get_user_exercise_target() : String.valueOf(DefaultVale.USER_SPORT_TARGET);
        String target_steps = JavaUtil.checkIsNull(mUserSetTools.get_user_exercise_target()) ? String.valueOf(DefaultVale.USER_SPORT_TARGET) : mUserSetTools.get_user_exercise_target();

        int wholeHourTemp = 0;
        int continuousBloodOxygen = mBleDeviceTools.get_continuity_spo2() ? 1 : 0;
        int wholeHourBloodOxygen = 0;
        final byte[] init_switch = BtSerializeation.setInitSet(time, unit, taiwan, zhuanwan, heart, miandarao, new_heart, theme, skin, screen_brighess, briness_time,
                temperatureType, CountinuityTemp, wholeHourTemp, continuousBloodOxygen, wholeHourBloodOxygen);
        final byte[] init_calibration = BtSerializeation.setUserPar(calibration_systolic, calibration_heart);
        final byte[] init_user = BtSerializeation.setUserProfile(mUserSetTools.get_user_sex(), 24, user_height, user_weight);
        final byte[] init_language = BtSerializeation.SendNewInitDevice(country);
        final byte[] init_steps_target = BtSerializeation.setTargetStep(Integer.valueOf(target_steps));

        //闹钟提醒
        final byte[] init_alarm_clock = BtSerializeation.setDeviceAlarm(RemindeUtils.getAlarmClock(getApplicationContext()));
        //久坐提醒
        final byte[] init_sit = BtSerializeation.setSitNotification(RemindeUtils.getSitModel());
        //会议提醒
        final byte[] init_meeting = BtSerializeation.setMeetingNotification(RemindeUtils.getMettingModel());
        //吃药提醒
        final byte[] init_medical = BtSerializeation.setMedicalNotification(RemindeUtils.getDurgModel());
        //喝水提醒
        final byte[] init_drinking = BtSerializeation.setDrinkingNotification(RemindeUtils.getWaterModel());

        MyLog.i(TAG, "初始化 写入开关数据");
        writeRXCharacteristic(init_switch);

        MyLog.i(TAG, "初始化 写入校准数据");
        writeRXCharacteristic(init_calibration);

        MyLog.i(TAG, "初始化 写入个人信息到");
        writeRXCharacteristic(init_user);

        MyLog.i(TAG, "初始化 写入生理周期信息");
        setDeviceCycleInfo();

        MyLog.i(TAG, "初始化 写入语言类型");
        writeRXCharacteristic(init_language);

        MyLog.i(TAG, "初始化 写入目标步数");
        writeRXCharacteristic(init_steps_target);

        MyLog.i(TAG, "初始化 写入基本目标");
        try {
            writeRXCharacteristic(BtSerializeation.sendGoalData(3, Integer.parseInt(mUserSetTools.get_user_sleep_target())));
            writeRXCharacteristic(BtSerializeation.sendGoalData(2, Integer.parseInt(mUserSetTools.get_user_activity_target())));
            int distance = 0;
            if (mUserSetTools.get_user_unit_type()) {
                distance = Integer.parseInt(mUserSetTools.get_user_distance_target()) * 1000;
            } else {
                distance = Integer.parseInt(mUserSetTools.get_user_distance_target()) * 1610;
            }
            writeRXCharacteristic(BtSerializeation.sendGoalData(1, distance));
            writeRXCharacteristic(BtSerializeation.sendGoalData(0, Integer.parseInt(mUserSetTools.get_user_cal_target())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        MyLog.i(TAG, "初始化 写入久坐提醒");
        writeRXCharacteristic(init_sit);

        MyLog.i(TAG, "初始化 写入会议提醒");
        writeRXCharacteristic(init_meeting);

        MyLog.i(TAG, "初始化 写入吃药提醒");
        writeRXCharacteristic(init_medical);

        MyLog.i(TAG, "初始化 写入喝水提醒");
        writeRXCharacteristic(init_drinking);

//        MyLog.i(TAG, "初始化 写入闹钟");
//        writeRXCharacteristic(init_alarm_clock);

        MyLog.i(TAG, "初始化 同步时间");
        syncTime();

//        mBleHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                boolean is_success = setDeviceScreensaverInfo();
//                MyLog.i(TAG, "初始化 写入屏保信息 = is_success = " + is_success);
//            }
//        }, 3000);

    }

    /**
     * 注册电话监听
     */
    private void registerPhoneReceiver() {
        if (mPhoneReceiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.PHONE_STATE");
            intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);

            mPhoneReceiver = new PhoneReceiver(new PhoneReceiver.OnPhoneListener() {
                @Override
                public void onCallState(int state, String incomingNumber) {
                    SysUtils.logContentI(TAG, "PhoneReceiver state = " + state + " incomingNumber = " + incomingNumber);
                    handlePhoneState(state, incomingNumber);
                }
            });
            registerReceiver(mPhoneReceiver, intentFilter);
        }
    }

    /**
     * 解注册电话监听
     */
    private void unRegisterPhoneReceiver() {
        if (mPhoneReceiver != null) {
            unregisterReceiver(mPhoneReceiver);
        }
    }

    /**
     * 处理打电话状态
     *
     * @param state
     * @param phoneNumber
     */
    void handlePhoneState(int state, String phoneNumber) {
        switch (state) {
            //来电
            case TelephonyManager.CALL_STATE_RINGING:
                if (phoneNumber != null) {
                    if ((System.currentTimeMillis() - CALL_STATE_RINGING_LAST_TIME > 2000)) {
                        SysUtils.logContentI(TAG, "来电测试 = 来电话");
                        if (mBleDeviceTools.get_reminde_call()) {
                            String callName = PhoneUtil.getContactNameFromPhoneBook(BleService.this, phoneNumber);
                            SysUtils.logContentI(TAG, "来电测试 来电 = phoneNumber = " + phoneNumber + "   callName = " + callName);
                            MyLog.i(TAG, "ble 蓝牙服务 来电 ture");
                            SysUtils.logContentI(TAG, "ble 蓝牙 = callName = " + callName);
                            SysUtils.logContentI(TAG, "ble 蓝牙 = callName = BleService.DeviceType = " + mBleDeviceTools.get_ble_device_type());
                            SysUtils.logContentI(TAG, "ble 蓝牙 = callName = BleService.DeviceVersion = " + mBleDeviceTools.get_ble_device_version());
                            //特殊处理
                            if (mBleDeviceTools.get_ble_device_type() == 6 && mBleDeviceTools.get_ble_device_version() == 16) {
                                if (TextUtils.isEmpty(callName)) {
                                    notiface_g20_phone(phoneNumber);
                                } else {
                                    notiface_g20_phone(callName);
                                }
                            } else {
                                if (TextUtils.isEmpty(callName)) {
                                    notiface_phone(phoneNumber);
                                } else {
                                    notiface_phone(callName);
                                }
                            }
                        } else {
                            SysUtils.logContentI(TAG, "ble 蓝牙服务 来电 false");
                        }
                        CALL_STATE_RINGING_LAST_TIME = System.currentTimeMillis();
                    }
                }
                break;

            //挂电话
            case TelephonyManager.CALL_STATE_IDLE:
//                if (phoneNumber != null) {
//                    if ((System.currentTimeMillis() - IDLE_LAST_TIME > 2000)) {
//                        MyLog.i(TAG, "来电测试 = 通话结束");
//                        writeRXCharacteristic(BtSerializeation.sendCloseCall());
//                    }
//                    IDLE_LAST_TIME = System.currentTimeMillis();
//                }

                if (!TextUtils.isEmpty(phoneNumber)) {
                    SysUtils.logContentW(TAG, "phoneNumber is not null and CloseCall cmd");
                    writeRXCharacteristic(BtSerializeation.sendCloseCall());
                }
                break;
            // 接电话了！
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (phoneNumber != null) {
                    if ((System.currentTimeMillis() - OFFHOOK_LAST_TIME > 2000)) {
                        MyLog.i(TAG, "来电测试 = 接电话");
                        writeRXCharacteristic(BtSerializeation.sendCloseCall());
                    }
                    OFFHOOK_LAST_TIME = System.currentTimeMillis();
                }
        }
    }

    void HandleFindPhone() {
        if ((System.currentTimeMillis() - FindPhoneTime > 3000)) {
            mHandler.sendEmptyMessage(MSG_FIND_PHONE_Media);
            mViberTimes = 5;
            mHandler.sendEmptyMessage(MSG_FIND_PHONE_Viber);
            FindPhoneTime = System.currentTimeMillis();
        }
    }


    /**
     * 写指令到蓝牙设备
     *
     * @param value
     * @return
     */
    public boolean writeThemeCharacteristic(final byte[] value) {
        boolean status = false;
        if (mBluetoothGatt == null)
            return false;
        final BluetoothGattService RxService = mBluetoothGatt.getService(BleConstant.UUID_THEME_SERVICE);
        if (RxService == null) {
            return status;
        }
        final BluetoothGattCharacteristic bluetoothGattCharacteristic = RxService.getCharacteristic(BleConstant.UUID_THEME_WRITE);
        if (bluetoothGattCharacteristic == null) {
            return status;
        }
        bluetoothGattCharacteristic.setValue(value);
        status = mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
        return status;
    }


    //===============同步时间完成之后，获取对应的数据================


    private final int intervalTime = 1000;

    /**
     * 获取主题信息数据
     */
    public void getOtherData() {
        MyLog.i(TAG, "getOtherData = start");

        int time = 100;

        initTimeZone();

        if (mBleDeviceTools.get_is_support_mail_list()) {
            if (!iCallDeviceInfo) {
                mBleHandler.postDelayed(this::getCallDeviceInfo, time);
                time = time + intervalTime;
            }
        }

        if (mBleDeviceTools.get_device_is_theme_transmission()) {
            if (!isDeviceThemeInfo) {
                mBleHandler.postDelayed(this::getDeviceThemeInfo, time);
                time = time + intervalTime;
            }
            if (!isDeviceMtu) {
                mBleHandler.postDelayed(this::getDeviceMtu, time);
                time = time + intervalTime;
            }
        }

        mBleHandler.postDelayed(() -> {
            MyLog.i(TAG, "getOtherData = 同步完成-发送广播");
            BroadcastTools.broadcastSyncComplete(getApplicationContext());
        }, time);

        time = time + 100;

        if (mBleDeviceTools.getIsSupportAppAuxiliarySport()) {
            mBleHandler.postDelayed(() -> {
                writeRXCharacteristic(BtSerializeation.sendSportState(2));
            }, time);
            time = time + 500;
        }

        if (mBleDeviceTools.getIsSupportProtobuf() && mBleDeviceTools.getPointExercise()) {
            mBleHandler.postDelayed(() -> {
                if (currentGpsSportState == -1 || currentGpsSportState == GpsSportDeviceStartEvent.SPORT_STATE_STOP || currentGpsSportState == GpsSportDeviceStartEvent.SPORT_STATE_PAUSE) {
                    isSyncSportData = true;
                    getProtoSport();
                }
            }, time);
        }
    }


    private boolean isDeviceThemeInfo = false;

    /**
     * 获取主题信息数据
     */
    public void getDeviceThemeInfo() {
        checkEnableSysRead();
        clearData();
        SysUtils.logContentI(TAG, "蓝牙写入 获取主题信息数据");
        writeRXCharacteristic(BtSerializeation.getDeviceThemeInfo());
    }

    private boolean isDeviceMtu = false;

    /**
     * 获取MTU数据
     */
    public void getDeviceMtu() {
        checkEnableSysRead();
        clearData();
        SysUtils.logContentI(TAG, "蓝牙写入 获取MTU值");
        writeRXCharacteristic(BtSerializeation.getDeviceMtu());
    }


    /**
     * 获取MAC地址
     */
    public void getMacAddress() {
        checkEnableSysRead();
        clearData();

        MyLog.i(TAG, "蓝牙写入 获取MAC地址");
        writeRXCharacteristic(BtSerializeation.getMacAddress());
    }


    private boolean iCallDeviceInfo = false;

    /**
     * 获取经典蓝牙设备信息
     */
    public void getCallDeviceInfo() {

        checkEnableSysRead();
        clearData();

        SysUtils.logContentI(TAG, "蓝牙写入 获取经典蓝牙设备信息");
        writeRXCharacteristic(BtSerializeation.getCallDeviceInfo());

    }

    /**
     * 清空联系人信息
     */
    public void cleanMailList() {
        SysUtils.logContentI(TAG, "蓝牙写入 清空联系人信息");
        writeRXCharacteristic(BtSerializeation.cleanMailList());
    }

    //=========================主题传输===================

//    /**
//     * 主题头
//     *
//     * @param total_page
//     * @param mtu
//     * @param block_size
//     */
//    public void sendThemeHead(int total_page, int mtu, int block_size) {
//        boolean is_success = writeThemeCharacteristic(BtSerializeation.sendThemeHead(total_page, mtu, block_size));
//
//        MyLog.i(TAG, "主题数据 发送头 is_success = " + is_success);
//    }


    /**
     * 主题头
     *
     * @param total_page 总包数
     * @param mtu        MTU
     * @param block_size 块大小
     */
    public void sendThemeHead(int total_page, int mtu, int block_size) {
        boolean is_success = writeThemeCharacteristic(BtSerializeation.sendThemeHead(total_page, mtu, block_size));
        SysUtils.logContentI(TAG, "dial send head is_success = " + is_success);
    }


    /**
     * 音频头
     *
     * @param total_page 总包数
     * @param mtu        MTU
     * @param block_size 块大小
     * @param music_name 文件名
     */
    public void sendMusicHead(int total_page, int mtu, int block_size, String music_name) {
        boolean is_success = writeThemeCharacteristic(BtSerializeation.sendMusicHead(total_page, mtu, block_size, music_name));
//        MyLog.i(TAG, "主题数据 发送头 is_success = " + is_success);
    }


    /**
     * 通讯录头
     *
     * @param total_page
     * @param mtu
     * @param block_size
     * @param crc
     */
    public void sendMailListHead(int total_page, int mtu, int block_size, int crc) {
        boolean is_success = writeThemeCharacteristic(BtSerializeation.sendMailListHead(total_page, mtu, block_size, crc));

//        MyLog.i(TAG, "主题数据 发送头 is_success = " + is_success);
    }

    /**
     * 主题数据
     *
     * @param sn
     * @param data
     */
    public void sendThemeData(int sn, byte[] data) {

        boolean is_success = writeThemeCharacteristic(BtSerializeation.sendThemeData(sn, data));
//        MyLog.i(TAG, "MyThemeActivity.class 发送数据 = " + ThemeUtils.bytes2HexString3(BtSerializeation.sendThemeData(sn, data)));
        SysUtils.logContentI(TAG, "dial data sn  = " + sn + " is_success = " + is_success);
    }

    /**
     * 块验证
     */
    public void sendThemeBlockVerfication() {
        boolean is_success = writeThemeCharacteristic(BtSerializeation.sendThemeBlockVerrification());
        SysUtils.logContentI(TAG, "dial check 01 = " + is_success);
        if (!is_success) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean is_success = writeThemeCharacteristic(BtSerializeation.sendThemeBlockVerrification());
                    SysUtils.logContentI(TAG, "dial check 02 = " + is_success);
                    if (!is_success) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                boolean is_success = writeThemeCharacteristic(BtSerializeation.sendThemeBlockVerrification());
                                SysUtils.logContentI(TAG, "dial check 03 = " + is_success);
                            }
                        }, 100);
                    }
                }
            }, 100);
        }


    }

    void analysisThemeData(byte[] data) {
//        MyLog.i(TAG, "主题数据 解析数据");
        byte key = data[2];

        switch (key) {

            //X1-发送失败-中止
            case 0x11:

                int fial_code = 0;
                if (data.length >= 4) {

                    int code = data[3] & 0xff;

                    if (code == 0) {
                        fial_code = 0;
                    } else if (code == 1) {
                        fial_code = 1;
                    } else if (code == 2) {
                        fial_code = 2;
                    } else if (code == 3) {
                        fial_code = 3;
                    } else {
                        fial_code = 0;
                    }
                }

                SysUtils.logContentI(TAG, "dial 发送失败-中断 fial_code =" + fial_code);
                BroadcastTools.broadcastThemeSuspension(getApplicationContext(), fial_code);
                break;

            //X1-连接间隔
            case 0x12:

                int interval_code = 0;

                if (data.length >= 4) {

                    int code = data[3] & 0xff;

                    if (code == 0) {
                        interval_code = 0;
                    } else if (code == 1) {
                        interval_code = 1;
                    } else if (code == 2) {
                        interval_code = 2;
                    } else {
                        interval_code = 0;
                    }
                }

                SysUtils.logContentI(TAG, "dial 连接间隔 interval_code =" + interval_code);
                BroadcastTools.broadcastThemeInterval(getApplicationContext(), interval_code);
                break;

            //B1-准备就绪
            case 0x13:
                SysUtils.logContentI(TAG, "dial 准备就绪");
                BroadcastTools.broadcastThemeReady(getApplicationContext());
                break;

            //B2-当前块结束
            case 0x14:
                SysUtils.logContentI(TAG, "dial 当前块结束");
                BroadcastTools.broadcastThemeBlockEnd(getApplicationContext());
                break;

            //B3-补发头
            case 0x16:
                SysUtils.logContentI(TAG, "dial 补发头");
                handleThemeRepairHead(data);
                break;

            //B5-响应收到的补发SN
            case 0x18:
//                MyLog.i(TAG, "主题数据 响应收到的补发SN");
                if (data.length >= 5) {
                    int result_sn = ((data[3] & 0xFF) << 8) | (data[4] & 0xFF);

                    SysUtils.logContentI(TAG, "dial 补发SN = " + result_sn);

                    BroadcastTools.broadcastThemeResultSuccessSn(getApplicationContext(), result_sn);
                }
                break;

            //B3-补发成功
            case 0x19:
                SysUtils.logContentI(TAG, "mail list 补发成功");
                BroadcastTools.broadcastThemeRepairSuccess(getApplicationContext());
                break;

            //B3-补发失败
            case 0x20:
                SysUtils.logContentI(TAG, "mail list 补发失败");
                BroadcastTools.broadcastThemeRepairFail(getApplicationContext());
                break;
        }

    }

    boolean ThemeSuppleStart = false;
    int ThemeSuppleTotalNumber = 0;
    int ThemeSuppleNowNumber = 0;
    ArrayList<Integer> ThemSnNumList;

    //处理主题补发头
    void handleThemeRepairHead(byte[] data) {

        if (data.length >= 7) {

            int total_sn_size = ((data[3] & 0xFF) << 24)
                    | ((data[4] & 0xFF) << 16)
                    | ((data[5] & 0xFF) << 8)
                    | ((data[6] & 0xFF));

            MyLog.i(TAG, "主题数据 补发头 total_sn_size " + total_sn_size);

            ThemeSuppleTotalNumber = total_sn_size;

            ThemeSuppleNowNumber = 0;
            ThemSnNumList = new ArrayList<>();
            ThemeSuppleStart = true;
            BroadcastTools.broadcastThemeRepairHead(getApplicationContext());

        }

    }


    //解析主题SN编号
    void analysisThemeSn(byte[] data) {

        int[] sn_data1 = BleTools.getSnData(data);

        MyLog.i(TAG, "主题数据 补发数据 长度 = " + sn_data1.length);
        MyLog.i(TAG, "主题数据 补发数据 第一个 = " + sn_data1[0]);

        //是否开始接接收补发数据
//        if (ThemeSuppleStart && ThemeSuppleTotalNumber > 0) {
        if (ThemeSuppleStart) {

            int[] sn_data = BleTools.getSnData(data);

            for (int i = 0; i < sn_data.length; i++) {
                ThemSnNumList.add(sn_data[i]);
                ThemeSuppleNowNumber++;
            }

            //接收完毕
//            if (ThemeSuppleNowNumber >= ThemeSuppleTotalNumber) {
            ThemeSuppleStart = false;
            BroadcastTools.broadcastThemeResponseSnList(getApplicationContext(), ThemSnNumList);
//            }

        }

    }

    //更新音量信息
    private void onVolumeUpdate(int current, int max_volume) {

//        MyLog.i(TAG, "音乐控制 更新音量========");

        mMaxVolume = max_volume;

        if (current != mCurrent) {
//            MyLog.i(TAG, "音乐控制 更新音量 = 不一样 = " + current);
            mCurrent = current;
            sendMusicData();
        } else {
//            MyLog.i(TAG, "音乐控制 更新音量 = 和上次一样 = " + current);
        }
        mCurrent = current;
    }

    private int mMusicState = 0;
    private String mMusicTitle = "";

    private boolean isDeviceGet = true;

    public void getMusicInfo() {
        isDeviceGet = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MusicSyncManager.getInstance().setMusicListener(new MusicSyncManager.MusicSyncListener() {
                @SuppressLint("WrongConstant")
                @Override
                public void onPlaybackStateChanged(PlaybackState state) {
                    if (state != null) {

                        int curstate = 0;
                        if (state.getState() == 3) {
                            curstate = 0;
                        } else {
                            curstate = 1;
                        }

                        if (mMusicState != curstate) {
                            mMusicState = curstate;
                            sendMusicData();
                        }
                    }
                }

                @Override
                public void onMetadataChanged(String title) {
                    if (isDeviceGet) {
                        isDeviceGet = false;
                        mMusicTitle = title;
                        sendMusicData();
                    } else {
                        if (!title.equals(mMusicTitle)) {
                            mMusicTitle = title;
                            sendMusicData();
                        }
                    }


                }
            });
        } else {
            SysUtils.logContentE(TAG, "Build.VERSION.SDK_INT is not LOLLIPOP");
        }
    }

    void sendMusicData() {
        if (mBleDeviceTools.get_device_music_control()) {
            MusicInfo mMusicInfo = new MusicInfo();

            mMusicInfo.setPlayState(mMusicState);

            if (mCurrent >= 0 && mCurrent <= mMaxVolume) {
                float send_volume = (float) mCurrent / mMaxVolume * 10000;
                MyLog.i(TAG, "music volume =" + send_volume);

                if (send_volume >= 0 && send_volume <= 10000) {
                    mMusicInfo.setVolumeLevel((int) (send_volume));
                }
            }


            if (mMusicTitle != null && !mMusicTitle.equals("")) {
                mMusicInfo.setMusicName(mMusicTitle);
                boolean is_success = writeRXCharacteristic(BtSerializeation.sendMusicControlInfoNew(mMusicInfo));
                MyLog.i(TAG, "send music title success=" + is_success);
            } else {
                MyLog.i(TAG, "music title is null");
            }

        } else {
            MyLog.i(TAG, "not support music");
        }

    }


    boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                // 增加音量
                SysUtils.controlVolume(this, AudioManager.ADJUST_RAISE);
                return true;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                //减少音量
                SysUtils.controlVolume(this, AudioManager.ADJUST_LOWER);
                return true;

            default:
                break;
        }
        return onKeyDown(keyCode, event);
    }


    /**
     * 获取设备错误LOG
     */
    public void getDeviceErrorLog() {
//        if (isServicesDiscovered()) {
//            MyLog.i(TAG, "LOG数据 已初始化");
//            TraceErrorLog.i("LOG数据 已初始化");
//            if (!isEnableNorticeLogTag()) {
//                MyLog.i(TAG, "LOG数据 未使能-调用使能");
//                TraceErrorLog.i("LOG数据 未使能-调用使能");
//                enableNotifacationLogRead();
//            } else {
//                MyLog.i(TAG, "LOG数据 已经使能");
//                TraceErrorLog.i("LOG数据 已经使能");
//            }
//        } else {
//            MyLog.i(TAG, "LOG数据 未初始化");
//            TraceErrorLog.i("LOG数据 未初始化");
//        }
//
//        boolean is_success = writeRXCharacteristic(BtSerializeation.getDeviceErrorLog());
//
//        MyLog.i(TAG, "LOG数据 setLOG = " + is_success);
//        TraceErrorLog.i("LOG数据 setLOG = " + is_success);
    }

    /**
     * 设置设备错误LOG
     */
    public void setDeviceErrorLog() {
//        if (isServicesDiscovered()) {
//            MyLog.i(TAG, "LOG数据 已初始化");
//            if (!isEnableNorticeLogTag()) {
//                MyLog.i(TAG, "LOG数据 未使能-调用使能");
//                enableNotifacationLogRead();
//            } else {
//                MyLog.i(TAG, "LOG数据 已经使能");
//            }
//        } else {
//            MyLog.i(TAG, "LOG数据 未初始化");
//        }
//        boolean is_success = writeRXCharacteristic(BtSerializeation.setDeviceErrorLog());
//        MyLog.i(TAG, "LOG数据 getLOG = " + is_success);
    }


    //==============通过APP行为提前结束睡眠========

    /**
     * @return
     */
    public boolean endSleepTag() {
        MyLog.i(TAG, "发送结束睡眠指令");
        SysUtils.logAppRunning(TAG, "发送结束睡眠指令");
        return writeRXCharacteristic(BtSerializeation.endDeviceSleepTag());
    }

    /**
     * 打开摇一摇拍照界面
     *
     * @return
     */
    public boolean openPhoto() {
        MyLog.i(TAG, "打开摇一摇拍照界面");
        return writeRXCharacteristic(BtSerializeation.sendDevicePhoto());
    }

    /**
     * 关闭摇一摇拍照界面
     *
     * @return
     */
    public boolean closePhoto() {
        MyLog.i(TAG, "关闭摇一摇拍照界面");
        return writeRXCharacteristic(BtSerializeation.closeDevicePhoto());
    }


    /**
     * 与设备配对 参考源码：platform/packages/apps/Settings.git
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
     */
    public boolean createBond() throws Exception {

        String call_ble_mac = mBleDeviceTools.get_call_ble_mac();

        MyLog.i(TAG, "通话设备信息回调 = call_ble_mac = " + call_ble_mac);
        if (call_ble_mac != null && !call_ble_mac.equals("")) {
            final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(call_ble_mac);
            MyLog.i(TAG, "通话设备信息回调 = device = " + device.getAddress());
            Method createBondMethod = device.getClass().getMethod("createBond");
            Boolean returnValue = (Boolean) createBondMethod.invoke(device);
            MyLog.i(TAG, "通话设备信息回调 = returnValue = " + returnValue);
            return returnValue.booleanValue();
        } else {
            return false;
        }

    }

    private void handleOffEcgHead(String date, int page_size) {
        initOffLineEcg();
        OffLineEcgTime = date;
        OffLineEcgNowPageSize = page_size;
        MyLog.i(TAG, "离线心电数据 = 是包头 OffLineEcgTime = " + OffLineEcgTime + "   OffLineEcgNowPageSize " + OffLineEcgNowPageSize);
        OffLineOverStart();
    }

    private void handleOffEcgData(int[] ecg_data) {
        for (int i = 0; i < ecg_data.length; i++) {
            int end = ecg_data[i];
            OffLineEcgData.append(end + ",");
            double reult = HandlerEcg(end, true);
            MyLog.i(TAG, "离线心电数据 reult = " + reult);
        }
        OffLineEcgNowPageSize -= 1;
        if (OffLineEcgNowPageSize == 5) {
            OffLineOverStop();
            //处理离线心电数据
            boolean isOk = HandleDeviceDataTools.HandleOffEcgData(this, getEcgHr(), getEcgPtpAvg(), OffLineEcgTime, OffLineEcgData, mHealthInfoUtils);
            if (!isOk) {
                BroadcastTools.broadcastOffLineEcgNoOk(getApplicationContext());
            }
        } else if (OffLineEcgNowPageSize > 10) {
            OffLineOverIsStop = false;
            MyLog.i(TAG, "离线心电数据 = 是数据 还有 = " + OffLineEcgNowPageSize);
            MyLog.i(TAG, "离线心电数据 = 是数据 MyECGFilter.MyECGFilterHr = " + getEcgHr());
        } else {
            MyLog.i(TAG, "离线心电数据 = 是数据 = 2222222");
        }
    }

    private void handleThemeData(byte[] data) {

        if (data.length >= 3 && data[0] == 0x00 && data[1] == 0x00) {
            analysisThemeData(data);
        } else {
            analysisThemeSn(data);
        }

    }

    private void handleLogData(byte[] data) {

        String hexString = BleTools.bytes2HexString(data);
//               ce fa ef be
        if (data.length >= 4) {
            if ((data[0] == (byte) 0xce) && data[1] == (byte) 0xfa && data[2] == (byte) 0xef && data[3] == (byte) 0xbe) {
                MyLog.i(TAG, "LOG数据 数据头 hexString:" + hexString);
                TraceErrorLog.i("LOG数据 数据头 hexString:" + hexString);
                String file_name = mBleDeviceTools.get_ble_name()
                        + "_" + mBleDeviceTools.get_ble_mac()
                        + "_" + BleTools.getDeviceVersionName(mBleDeviceTools)
                        + "_" + MyTime.getAllTime() + ".bin";
                MyLog.i(TAG, "LOG数据 创建文件 file_name = " + file_name);
                TraceErrorLog.i("LOG数据 创建文件 file_name = " + file_name);
                if (Constants.SAVE_DEVICE_LOG) {
                    log_bin = FileUtil.CreateFile(Constants.DEVICE_ERROR_LOG_FILE, file_name);
                }

            } else {
                MyLog.i(TAG, "LOG数据 数据包 hexString:" + hexString);
                TraceErrorLog.i("LOG数据 数据包 hexString:" + hexString);
            }
        } else {
            MyLog.i(TAG, "LOG数据 数据包 hexString:" + hexString);
            TraceErrorLog.i("LOG数据 数据包 hexString:" + hexString);
        }

        if (log_bin != null) {
            if (Constants.SAVE_DEVICE_LOG) {
                FileUtil.NewwriteToFile(log_bin, data);
            }
            MyLog.i(TAG, "LOG数据 写入数据 文件大小 :" + log_bin.length());
            TraceErrorLog.i("LOG数据 写入数据 文件大小 :" + log_bin.length());
        }

    }

    public void clearData() {
        mBleProtocol.clearData();
        mBleBaseProtocol.clearData();
    }

    public void readingBleData(BluetoothGattCharacteristic characteristic) {
        if (characteristic.getUuid().equals(BleConstant.UUID_BASE_READ) || characteristic.getUuid().equals(BleConstant.CHAR_BIG_UUID_03)) {
            byte[] resultData = mBleBaseProtocol.readingBleData(characteristic);
            if (resultData != null && resultData.length > 10) {
                parseBleData(resultData);
            }
        } else {
            mBleProtocol.ReadingBleData(characteristic);
        }
    }

    public void setEcgMeasure(boolean is_ecg_measure) {
        mBleProtocol.setEcgMeasure(is_ecg_measure);
    }

    public double HandlerEcg(int end, boolean isLeft) {
        return mBleProtocol.HandlerEcg(end, mHandler, isLeft);
    }

    public double HandlerPpg(int end, boolean isLeft) {
        return mBleProtocol.HandlerPpg(end, isLeft);
    }

    public void initEcgData() {
        mBleProtocol.initEcgData();
    }

    public void initPpgData() {
        mBleProtocol.initPpgData();
    }

    public int getEcgHr() {
        return mBleProtocol.getEcgHr();
    }

    public int getEcgPtpAvg() {
        return mBleProtocol.getEcgPtpAvg();
    }

    private SimplePerformerListener mPerformerListener = new SimplePerformerListener() {

//        public void onResponseSystemData(byte[] data) {
//            String my_hexString = BleTools.bytes2BleDataHexString(data);
//            MyLog.i(TAG, "onResponseSystemData = " + my_hexString);
//            parseRcvData(data);
//
//        }

        public void onResponseEcgMeasureData(int[] ecg_data, int lead_tag) {
//            MyLog.i(TAG, "蓝牙回调 onResponseEcgMeasureData = " + "ecg_data = " + ecg_data.length);
//            MyLog.i(TAG, "蓝牙回调 onResponseEcgMeasureData = " + "lead_tag = " + lead_tag);
            BroadcastTools.broadcastEcgData(getApplicationContext(), ecg_data, lead_tag);
        }

        @Override
        public void onResponseEcgMeasureHrSound() {
            Message msg = new Message();
            msg.what = EcgMeasureActivity.MSG_PLAY_RBO_SOUND;
            mHandler.sendMessage(msg);
        }

        public void onResponseEcgOffMeasureStart(String date, int page_size) {
            handleOffEcgHead(date, page_size);
        }

        public void onResponseEcgOffMeasureData(int[] ecg_data) {
            handleOffEcgData(ecg_data);
        }


        @Override
        public void onResponsePpgMeasureData(int[] ppg_data) {
//            MyLog.i(TAG, "onResponsePpgMeasureData length = " + ppg_data.length);
//            MyLog.i(TAG, "onResponsePpgMeasureData data[0]= " + ppg_data[0]);
            BroadcastTools.broadcastPpgData(getApplicationContext(), ppg_data);
        }

        @Override
        public void onResponseThemeData(byte[] data) {
            handleThemeData(data);
        }

        @Override
        public void onResponseLogData(byte[] data) {
            handleLogData(data);

        }
    };

    public static boolean closeBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }
        return mBluetoothAdapter.disable();
    }

    /**
     * 改变GATT后解绑
     */
    public void changeDeviceGatt() {
        SysUtils.logContentW(TAG, "changeDeviceGatt()");
        SysUtils.logAppRunning(TAG, "changeDeviceGatt()");
        connect("AA:BB:CC:DD:EE:FF");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                removeBindHandle();
                removeConnectTimeOutHandler();
                disconnect();
                setBlueToothStatus(BleConstant.STATE_CONNECTED_TIMEOUT);
                BroadcastTools.broadcastDeviceConnectTimeout(getApplicationContext());
            }
        }, 1000);
    }

    private void displayData4(String data) {
        if (data != null) {
            Log.w("time", "displayData  1 ");
            SysUtils.logContentI(TAG, "displayData4 : " + data);
            SysUtils.logAppRunning(TAG, "displayData4 : " + data);

            /*if (System.currentTimeMillis() - lastDisplayTime < 5 && lastData.equalsIgnoreCase(data)) {
                SysUtils.logContentI(TAG, "displayData4 : repeat data");
            } else*/
            {
                Log.w("time", "displayData 2 ");
                lastData = data;

                recvData = data;

                String strCmd[];
                strCmd = recvData.split(" ");
                Intent intent = new Intent();
                try {
                    //设备回复成功，

                    if (Integer.parseInt(strCmd[2], 16) == 1) {
                        if (Integer.parseInt(strCmd[3], 16) == 1) {
                            Log.w("time", "displayData  3 ");
                            ThreadPoolService.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    uploadDataPiece();
                                }
                            });
                        }
                        if (Integer.parseInt(strCmd[3], 16) == 0) {
                            sendNextPiece();
                        }
                    }

                    int packNum = Integer.parseInt(strCmd[5] + strCmd[4], 16);
                    if (packNum > 0) {
                        SysUtils.logContentI(TAG, "displayData4 reissue data : " + data);
                        SysUtils.logAppRunning(TAG, "displayData4 reissue data : " + data);
                        curReissueDataSize++;
//                        intent.setAction(ThemeManager.ACTION_CMD_DEVICE_REISSUE_PACK);
//                        intent.putExtra("packNum", packNum);
//                        sendBroadcast(intent);
                        writeCharacteristicProto4(BleCmdManager.getInstance().sendThemePiece(packNum, themeCurPiece));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                resetBleCmdState(false);
            }
        }
    }

    Runnable process_cmd_runnable_proto04 = () -> {
        while (mProcessCmd) {
            try {
                Thread.sleep(20);
                boolean connectState = ISBlueToothConnect();
                if (!connectState) {
                    resetBleCmdState(false);
                } else {
                    if (bleCmdListProto04 != null && bleCmdListProto04.size() > 0) {
                        byte[] paramCmd = bleCmdListProto04.remove(0);
                        writeCharacteristic(paramCmd, BleConstant.UUID_PROTOBUF_SERVICE, BleConstant.CHAR_PROTOBUF_UUID_04);
                    }
                }
            } catch (Exception e) {
                resetBleCmdState(false);
            }
        }
    };

    Runnable process_cmd_proto_runnable = () -> {
        while (mProcessCmd) {
            try {
                Thread.sleep(Constants.detectBleCmdPeriod);
                if (!bInitGattServices)
                    continue;
                boolean connectState = ISBlueToothConnect();
                int cmdSize = bleCmdList_proto.size();

                if (!connectState) {
                    resetBleCmdState(false);
                } else {
                    if (cmdSize > 0) {
                        byte[] paramCmd = bleCmdList_proto.remove(0);
                        if(paramCmd != null){
                            if (isReplyDevice(paramCmd)) {
                                writeCharacteristic(paramCmd, BleConstant.UUID_PROTOBUF_SERVICE, BleConstant.CHAR_PROTOBUF_UUID_01);
                            } else {
                                writeCharacteristic(paramCmd, BleConstant.UUID_PROTOBUF_SERVICE, BleConstant.CHAR_PROTOBUF_UUID_02);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                resetBleCmdState(false);
                SysUtils.logContentE(TAG, "process_cmd_runnable_proto e=" + e);
                SysUtils.logAppRunning(TAG, "process_cmd_runnable_proto e=" + e);
            }
        }
    };

    private void initDeviceParameter() {
        mBleDeviceTools.setSupportNewDeviceCrc(false);
        mBleBaseProtocol.setRcvDataState(0);
        mBleDeviceTools.setWeatherMode(0);
        mBleDeviceTools.setSupportBatteryPercentage(false);
        mBleDeviceTools.setSupportProtoNewSport(false);
        mBleDeviceTools.setSupportAlexa(false);
        mBleDeviceTools.setisReplyOnePack(false);
        mBleDeviceTools.setSupportNewDeviceCrc(false);
    }

    private void connectBleRXError() {
        disconnect();
        reconnectDevcie();
    }

    private long lastTakePhotoTime = 0;

    private void initTakePhoto() {
        if (System.currentTimeMillis() - lastTakePhotoTime > 3500) {
            lastTakePhotoTime = System.currentTimeMillis();
            BroadcastTools.broadcastDevicePhoto(getApplicationContext());
        }
    }

    // ****************************  proto  **************************
    private String curCmd;
    public static final String GET_SPORT_IDS_TODAY = "GET_SPORT_IDS_TODAY";
    public static final String GET_SPORT_IDS_HISTORY = "GET_SPORT_IDS_HISTORY";
    public static final String REQUEST_FITNESS_ID_TODAY = "REQUEST_FITNESS_ID_TODAY";
    public static final String REQUEST_FITNESS_ID_HISTORY = "REQUEST_FITNESS_ID_HISTORY";
    public static final String DELETE_DEVICE_SPORT_HISTORY = "DELETE_DEVICE_SPORT_HISTORY";
    public static final String DELETE_DEVICE_SPORT_TODAY = "DELETE_DEVICE_SPORT_TODAY";
    public static final String GET_PAGE_DEVICE = "GET_PAGE_DEVICE";
    public static final String SET_PAGE_DEVICE = "SET_PAGE_DEVICE";
    public static final String APP_GPS_READY = "APP_GPS_READY";
    public static final String APP_SEND_GPS = "APP_SEND_GPS";
    public static final String APP_REQUEST_GPS_SPORT_STATE = "APP_REQUEST_GPS_SPORT_STATE";
    public static final String APP_REQUEST_DEVICE_WATCH_FACE_PREPARE_INSTALL = "APP_REQUEST_DEVICE_WATCH_FACE_PREPARE_INSTALL";
    public static final String APP_REQUEST_DEVICE_OTA_PREPARE = "APP_REQUEST_DEVICE_OTA_PREPARE";
    // 获取设备的主题列表
    public static final String APP_REQUEST_DEVICE_WATCH_FACE = "APP_REQUEST_DEVICE_WATCH_FACE";
    public static final String APP_SET_DEVICE_WATCH_FACE = "APP_SET_DEVICE_WATCH_FACE";
    public static final String APP_DELETE_DEVICE_WATCH_FACE = "APP_DELETE_DEVICE_WATCH_FACE";
    // 发送openweatherData
    public static final String APP_SEND_OPEN_WEATHER_LATEST_WEATHER = "APP_SEND_OPEN_WEATHER_LATEST_WEATHER";
    public static final String APP_SEND_OPEN_WEATHER_DAILY_FORECAST = "APP_SEND_OPEN_WEATHER_DAILY_FORECAST";
    public static final String APP_SEND_OPEN_WEATHER_HOURLY_FORECAST = "APP_SEND_OPEN_WEATHER_HOURLY_FORECAST";

    public static boolean isForce;
    public static String version;
    public static String md5;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getDeviceProtoOtaPrepareStatusEvent(GetDeviceProtoOtaPrepareStatusEvent event) {
        isForce = event.isForce;
        version = event.version;
        md5 = event.md5;
        curCmd = APP_REQUEST_DEVICE_OTA_PREPARE;
        bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));
    }

    public static String themeId = "0";
    public static int themeSize = 0;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getDeviceProtoStatusEvent(GetDeviceProtoWatchFacePrepareStatusEvent event) {
        curCmd = APP_REQUEST_DEVICE_WATCH_FACE_PREPARE_INSTALL;
        themeId = event.themeId;
        themeSize = event.themeSize;
        bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deviceWatchFaceListSyncEvent(DeviceWatchFaceListSyncEvent event) {
        curCmd = APP_REQUEST_DEVICE_WATCH_FACE;
        bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));
    }

    public static String currentThemeId = "";

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deviceWatchFaceSetEvent(DeviceWatchFaceSetEvent event) {
        currentThemeId = event.id;
        curCmd = APP_SET_DEVICE_WATCH_FACE;
        bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deviceWatchFaceDeleteEvent(DeviceWatchFaceDeleteEvent event) {
        currentThemeId = event.id;
        curCmd = APP_DELETE_DEVICE_WATCH_FACE;
        bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pageDeviceSyncEvent(PageDeviceSyncEvent event) {
        curCmd = GET_PAGE_DEVICE;
        bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pageDeviceSetEvent(PageDeviceSetEvent event) {
        curCmd = SET_PAGE_DEVICE;
        bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deviceNoSportEvent(DeviceNoSportEvent event) {
        MyLog.w(TAG, " init agps request");
        if (mBleDeviceTools.getIsGpsSensor() && HomeActivity.isFirstOnCreate) {
            HomeActivity.isFirstOnCreate = false;
            writeRXCharacteristic(BtSerializeation.getBleData(null, BtSerializeation.CMD_01, BtSerializeation.KEY_AGPS));
        }
    }

    private boolean isFirstConnect = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deviceSportStatusEvent(DeviceSportStatusEvent event) {
        if (isFirstConnect) {
            MyLog.w(TAG, " deviceSportStatusEvent is first connect");
            if (event.paused) {
                currentGpsSportState = GpsSportDeviceStartEvent.SPORT_STATE_PAUSE;
            } else {
                currentGpsSportState = GpsSportDeviceStartEvent.SPORT_STATE_START;
            }
            isFirstConnect = false;
            curCmd = APP_GPS_READY;
            initGpsSport();
        } else {
            MyLog.w(TAG, " no initGpsSport");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void gpsSportDeviceStartEvent(GpsSportDeviceStartEvent event) {
        currentGpsSportState = event.state;
        switch (currentGpsSportState) {
            case GpsSportDeviceStartEvent.SPORT_STATE_START:
                curCmd = APP_GPS_READY;
                initGpsSport();
                break;
            case GpsSportDeviceStartEvent.SPORT_STATE_PAUSE:
                break;
            case GpsSportDeviceStartEvent.SPORT_STATE_RESUME:
                break;
            case GpsSportDeviceStartEvent.SPORT_STATE_STOP:
                lastGpsInfo = null;
                isSuccessfulPositioning = false;
                stopLocationService();
//                createKml();
                getProtoSport();
                break;
        }
    }

    private void stopLocationService() {
        Intent intent = new Intent(this, ForegroundLocationService.class);
        stopService(intent);
    }

    public static int currentGpsSportState = -1;
    public static GpsSportManager.GpsInfo lastGpsInfo;
    public static boolean isSuccessfulPositioning = false;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void locationChangeEventBus(LocationChangeEventBus event) {
        GpsSportManager.GpsInfo gpsInfo = event.gpsInfo;
        if (mBleDeviceTools.getIsSupportAppAuxiliarySport()) {
            if (appGpsInfo == null || curSportState == BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_RESULT_YES) {
                appGpsInfo = gpsInfo;
                writeRXCharacteristic(BtSerializeation.sendSportState(1));
                curSportState = BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_START;
            } else {
                switch (curSportState) {
                    case BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_START:
                    case BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_RESUME:
                        if (appGpsInfo.latitude == gpsInfo.latitude && appGpsInfo.longitude == gpsInfo.longitude) {
                            Log.i(TAG, "locationChangeEventBus location is not change...");
                        } else {
                            double distance = GpsSportManager.getInstance().getDistance(appGpsInfo.latitude, appGpsInfo.longitude, gpsInfo.latitude, gpsInfo.longitude);
                            if (distance != 0) {
                                appGpsInfo = gpsInfo;
                                Log.w(TAG, "locationChangeEventBus distance = " + distance);
                                EventBus.getDefault().post(new RefreshGpsInfoEvent(gpsInfo));
                                writeRXCharacteristic(BtSerializeation.sendSportData(gpsInfo.latitude, gpsInfo.longitude, gpsInfo.gpsAccuracy));

//                                TrackPoint tp = new TrackPoint(gpsInfo.longitude, gpsInfo.latitude, gpsInfo.altitude, System.currentTimeMillis());
//                                KmlFileManager.getInstance().addData(tp);
                            }
                        }
                        break;
                }
            }
        } else {
            if (isSyncSportData) {
                return;
            } else {
                if (currentGpsSportState == GpsSportDeviceStartEvent.SPORT_STATE_START || currentGpsSportState == GpsSportDeviceStartEvent.SPORT_STATE_RESUME) {
                    if (lastGpsInfo == null) {
                        lastGpsInfo = gpsInfo;
                        curCmd = APP_GPS_READY;
                        bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));
                        isSuccessfulPositioning = true;
                    } else {
                        lastGpsInfo = gpsInfo;

                        EventBus.getDefault().post(new RefreshGpsInfoEvent(gpsInfo));

                        curCmd = APP_SEND_GPS;
                        bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));

//                    TrackPoint tp = new TrackPoint(gpsInfo.longitude, gpsInfo.latitude, gpsInfo.altitude, System.currentTimeMillis());
//                    KmlFileManager.getInstance().addData(tp);
                    }
                }
            }
        }
    }

    public static int curSportState = BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_RESULT_NO;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deviceToAppSportStateEvent(DeviceToAppSportStateEvent event) {
        curSportState = event.state;
        switch (event.state) {
            case BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_START: // 发起运动
            case BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_RESULT_YES: // 正在运动中…
                initGpsSport();
                break;
            case BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_PAUSE: // 运动已暂停
                break;
            case BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_RESUME: // 运动继续
                break;
            case BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_STOP: // 结束运动
                resetAppHelpDevice();
                break;
            case BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_RESULT_NO: // 非运动状态…
                break;
        }
    }

    private GpsSportManager.GpsInfo appGpsInfo = null;

    private void resetAppHelpDevice() {
//        createKml();
        appGpsInfo = null;
        GpsSportManager.getInstance().stopGps(this);
        stopLocationService();
    }

    private void initGpsSport() {
        Intent intent = new Intent(this, ForegroundLocationService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void getProtoSport() {
        if (protoHandler == null) {
            protoHandler = new Handler();
        }
//        Calendar mCalendar = Calendar.getInstance();
//        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
//        mCalendar.set(Calendar.MINUTE, 0);
//        mCalendar.set(Calendar.SECOND, 0);
//        mCalendar.set(Calendar.MILLISECOND, 0);
//        long time = mCalendar.getTimeInMillis();
//        if (mBleDeviceTools.getLastDeviceSportSyncTime() == 0 || mBleDeviceTools.getLastDeviceSportSyncTime() < time) {
//            // 获取历史
//            curCmd = GET_SPORT_IDS_HISTORY;
//            bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));
//        } else {
//            curCmd = GET_SPORT_IDS_TODAY;
//            bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));
//        }
        curCmd = GET_SPORT_IDS_HISTORY;
        bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));
        protoHandler.removeCallbacksAndMessages(null);
        protoHandler.postDelayed(getProtoSportTimeOut, timeOut);
    }

    private Handler protoHandler;
    private int timeOut = 20 * 1000;

    Runnable getProtoSportTimeOut = () -> {
        SysUtils.logContentW("ble", " getProtoSportTimeOut");
        SysUtils.logAppRunning("ble", " getProtoSportTimeOut");
        syncDeviceSportOver();
    };

    private void refreshProtobufSportTimeOut() {
        Log.w("ble", " refreshProtobufSportTimeOut");
        if (protoHandler == null) {
            protoHandler = new Handler();
        }
        protoHandler.removeCallbacksAndMessages(null);
        protoHandler.postDelayed(getProtoSportTimeOut, timeOut);
    }

    private void deleteDeviceSport(String cmd) {
        curCmd = cmd;
        bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));
    }

    private void startSyncTodayDeviceSport() {
        mBleDeviceTools.setLastDeviceSportSyncTime(System.currentTimeMillis());
        curCmd = GET_SPORT_IDS_TODAY;
        bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));
    }

    public static boolean isSyncSportData = false;

    private void syncDeviceSportOver() {
        isSyncSportData = false;
        EventBus.getDefault().post(new SyncDeviceSportEvent(0));
        if (protoHandler != null) {
            protoHandler.removeCallbacksAndMessages(null);
        }
    }

    private void getDeviceGpsSportStatus() {
        if (mBleDeviceTools.getIsSupportGpsSport()) {
            // 询问gps运动结果
            curCmd = APP_REQUEST_GPS_SPORT_STATE;
            bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));
        } else {
            if (mBleDeviceTools.getIsGpsSensor()) {
                EventBus.getDefault().post(new DeviceNoSportEvent());
            }
        }
    }

    private void initDisconnectParameter() {
        curSportState = BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_RESULT_NO;
        isSyncSportData = false;
        currentGpsSportState = -1;
        lastGpsInfo = null;
        isSuccessfulPositioning = false;
        isFirstConnect = false;
        curCmd = "";
        stopLocationService();
    }

    void sendBleData2() {
        SysUtils.logContentI("ble", " BroadcastTools.ACTION_CMD_APP_START = " + curCmd);
        SysUtils.logAppRunning("ble", " BroadcastTools.ACTION_CMD_APP_START = " + curCmd);
        int validDataLength = BaseApplication.getBleDeviceTools().get_device_mtu_num() - 2;
        for (int i = 1; i < BtSerializeation.curPack + 1; i++) {
            if (BtSerializeation.curPack == 1) {
                bleCmdList_proto.add(BtSerializeation.getProtoByte(BtSerializeation.sendingData, i));
            } else {
                byte[] data;
                int dataLength = validDataLength;
                if (i == BtSerializeation.curPack) {
                    // The last packet
                    dataLength = BtSerializeation.sendingData.length - (i - 1) * validDataLength;
                    data = new byte[dataLength];
                } else {
                    data = new byte[validDataLength];
                }
                System.arraycopy(BtSerializeation.sendingData, (i - 1) * validDataLength, data, 0, dataLength);
                bleCmdList_proto.add(BtSerializeation.getProtoByte(data, i));
            }
        }
    }

    void sendNextBleData2() {
        try {
            switch (curCmd) {
                case DELETE_DEVICE_SPORT_HISTORY:
                    refreshProtobufSportTimeOut();
                    if (FitnessTools.deleteIndex == FitnessTools.bleIdsList.size()) {
                        //delete history over
                        SysUtils.logContentW("ble", " delete history over");
                        SysUtils.logAppRunning("ble", " delete history over");
                        startSyncTodayDeviceSport();
                    } else {
                        deleteDeviceSport(DELETE_DEVICE_SPORT_HISTORY);
                    }
                    break;
                case DELETE_DEVICE_SPORT_TODAY:
                    refreshProtobufSportTimeOut();
                    if (FitnessTools.deleteIndex == FitnessTools.bleIdsList.size()) {
                        //delete today over
                        SysUtils.logContentW("ble", " delete today over");
                        SysUtils.logAppRunning("ble", " delete today over");
                        syncDeviceSportOver();
                        getDeviceGpsSportStatus();
                    } else {
                        deleteDeviceSport(DELETE_DEVICE_SPORT_TODAY);
                    }
                    break;
                case APP_GPS_READY:
                    curCmd = APP_SEND_GPS;
                    break;
                case APP_SEND_OPEN_WEATHER_LATEST_WEATHER:
                    curCmd = APP_SEND_OPEN_WEATHER_DAILY_FORECAST;
                    bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));
                    break;
                case APP_SEND_OPEN_WEATHER_DAILY_FORECAST:
                    curCmd = APP_SEND_OPEN_WEATHER_HOURLY_FORECAST;
                    bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));
                    break;
                case APP_SEND_OPEN_WEATHER_HOURLY_FORECAST:
                    SysUtils.logContentW("ble", " weather send over");
                    EventBus.getDefault().post(new SendOpenWeatherDataEvent(1));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void getNextProtoDeviceSport() {
        try {
            refreshProtobufSportTimeOut();
            SysUtils.logContentI("ble", " BroadcastTools.ACTION_CMD_GET_SPORT = " + curCmd);
            SysUtils.logAppRunning("ble", " BroadcastTools.ACTION_CMD_GET_SPORT = " + curCmd);
            switch (curCmd) {
                case GET_SPORT_IDS_TODAY:
                    curCmd = REQUEST_FITNESS_ID_TODAY;
                    break;
                case GET_SPORT_IDS_HISTORY:
                    curCmd = REQUEST_FITNESS_ID_HISTORY;
                    break;
            }
            switch (curCmd) {
                case REQUEST_FITNESS_ID_TODAY:
                    //                tvProgress.setText("today " + (FitnessTools.currentIndex) + "/" + FitnessTools.bleIdsList.size());
                    break;
                case REQUEST_FITNESS_ID_HISTORY:
                    //                tvProgress.setText("History " + (FitnessTools.currentIndex) + "/" + FitnessTools.bleIdsList.size());
                    break;
            }

            if (FitnessTools.bleIdsList.size() != 0) {
                if (FitnessTools.currentIndex >= FitnessTools.bleIdsList.size()) {
                    // over
                    if (curCmd.equalsIgnoreCase(REQUEST_FITNESS_ID_HISTORY)) {
                        SysUtils.logContentW("ble", "REQUEST_FITNESS_ID_HISTORY sync over");
                        SysUtils.logAppRunning("ble", "REQUEST_FITNESS_ID_HISTORY sync over");
                        // history is over and delete the ids
                        FitnessTools.deleteIndex = 0;
                        deleteDeviceSport(DELETE_DEVICE_SPORT_HISTORY);
                        //                                startSyncTodayDeviceSport();

                    } else if (curCmd.equalsIgnoreCase(REQUEST_FITNESS_ID_TODAY)) {
                        SysUtils.logContentW("ble", "REQUEST_FITNESS_ID_TODAY sync over");
                        SysUtils.logAppRunning("ble", "REQUEST_FITNESS_ID_TODAY sync over");
                        DeviceSportManager.Companion.getInstance().uploadMoreSportData();
                        // delete the ids
                        FitnessTools.deleteIndex = 0;
                        deleteDeviceSport(DELETE_DEVICE_SPORT_TODAY);
                    }
                } else {
                    EventBus.getDefault().post(new SyncDeviceSportEvent(1));
                    bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));
                }
            } else {
                switch (curCmd) {
                    case REQUEST_FITNESS_ID_TODAY:
                        SysUtils.logContentW("ble", " today is no data , sync over");
                        SysUtils.logAppRunning("ble", " today is no data , sync over");
                        DeviceSportManager.Companion.getInstance().uploadMoreSportData();
                        syncDeviceSportOver();
                        getDeviceGpsSportStatus();
                        break;
                    case REQUEST_FITNESS_ID_HISTORY:
                        SysUtils.logContentW("ble", " HISTORY is no data");
                        SysUtils.logAppRunning("ble", " HISTORY is no data");
                        startSyncTodayDeviceSport();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ==================== send theme =======
    int curReissueDataSize = 0;
    int writeCharacteristicProto4Fail = 0;
    long sendThemeStartTime = 0;

    BluetoothGattCharacteristic dev_name;

    public void sendTheme(String type, byte[] fileName) {
        if (mBluetoothGatt == null) {
            return;
        }
        BluetoothGattService gap_service = mBluetoothGatt.getService(BleConstant.UUID_PROTOBUF_SERVICE);
        if (gap_service == null) {
            return;
        }
        dev_name = gap_service.getCharacteristic(BleConstant.CHAR_PROTOBUF_UUID_04);
        if (dev_name == null) {
            return;
        }
        dev_name.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

        sendThemeStartTime = System.currentTimeMillis();

        curReissueDataSize = 0;
        writeCharacteristicProto4Fail = 0;
        themeCurPiece = 0;
        ThemeManager.getInstance().initUpload(this, type, fileName);
        startUploadThemePiece();
    }

    private int themeCurPiece = 0;
    private int curPieceSendPack = 0;

    private void startUploadThemePiece() {
        themeCurPiece++;
        curPieceSendPack = (ThemeManager.getInstance().dataPackTotalPieceLength - themeCurPiece >= 1) ? ThemeManager.getInstance().dataPieceMaxPack : ThemeManager.getInstance().dataPieceEndPack;

        if (protoHandler == null) {
            protoHandler = new Handler();
        }
        protoHandler.removeCallbacksAndMessages(null);
        protoHandler.postDelayed(uploadProtoThemeTimeOut, 30 * 1000);
        writeCharacteristicProto4(BleCmdManager.getInstance().appStartCmd(curPieceSendPack));
    }

    Runnable uploadProtoThemeTimeOut = () -> {
        EventBus.getDefault().post(new UploadThemeStateEvent(1, 0)); // 超时
    };

    @SuppressLint("SetTextI18n")
    private void uploadDataPiece() {
        for (int i = 0; i < curPieceSendPack; i++) {
            writeCharacteristicProto4(BleCmdManager.getInstance().sendThemePiece(i + 1, themeCurPiece));
        }

        int progress = (themeCurPiece * 100 / ThemeManager.getInstance().dataPackTotalPieceLength);
        EventBus.getDefault().post(new UploadThemeStateEvent(2, progress)); // 进度
    }

    void sendNextPiece() {
        if (themeCurPiece == ThemeManager.getInstance().dataPackTotalPieceLength) {
            protoHandler.removeCallbacksAndMessages(null);
            EventBus.getDefault().post(new UploadThemeStateEvent(3, 0)); // 完成
            SysUtils.logContentW(TAG, "curReissueDataSize = " + curReissueDataSize + "  time = " + (System.currentTimeMillis() - sendThemeStartTime));
            SysUtils.logAppRunning(TAG, "writeCharacteristicProto4Fail = " + writeCharacteristicProto4Fail);
        } else {
            startUploadThemePiece();
        }
    }

    public void writeCharacteristicProto4(byte[] value) {
        try {
//            SysUtils.logContentI(TAG, "writeCharacteristicProto4 =" + BleTools.bytes2HexString(value));
//            SysUtils.logAppRunning(TAG, "writeCharacteristicProto4 =" + BleTools.bytes2HexString(value));
            Log.i(TAG, "writeCharacteristicProto4 =" + BleTools.bytes2HexString(value));

            dev_name.setValue(value);
            boolean status = mBluetoothGatt.writeCharacteristic(dev_name);

            for (int i = 1; i <= 100; i++) {
                if (status) {
                    break;
                }
//                SysUtils.logContentI(TAG, " writeCharacteristicProto4 status =  number of reissues " + i + " status = " + status);
//                SysUtils.logAppRunning(TAG, " writeCharacteristicProto4 status =  number of reissues " + i + " status = " + status);
                Log.i(TAG, " writeCharacteristicProto4 status =  number of reissues " + i + " status = " + status);
                try {
                    Thread.sleep(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                status = mBluetoothGatt.writeCharacteristic(dev_name);
            }
//            SysUtils.logContentI(TAG, " writeCharacteristicProto4 status end = " + status);
//            SysUtils.logAppRunning(TAG, " writeCharacteristicProto4 status end = " + status);
            Log.i(TAG, " writeCharacteristicProto4 status end = " + status);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendOpenWeatherDataEvent(SendOpenWeatherDataEvent event) {
        if(event.status == 0){
            curCmd = APP_SEND_OPEN_WEATHER_LATEST_WEATHER;
            bleCmdList_proto.add(BtSerializeation.appStartCmd(curCmd));
        }
    }

    // ****************************  proto end **************************
    public boolean writeSpecialCharacteristic(byte[] value) {
        SysUtils.logContentI(TAG, "writeSpecialCharacteristic =" + BleTools.bytes2HexString(value));
        SysUtils.logAppRunning(TAG, "writeSpecialCharacteristic =" + BleTools.bytes2HexString(value));

        List<byte[]> cmdList = new ArrayList<>();
        int length = value.length;
        int copy_size = 0;
        while (length > 0) {
            if (length < currentMtu) {
                byte[] val = new byte[length];
                for (int i = 0; i < length; i++) {
                    val[i] = value[i + copy_size];
                }
                if (isSupportBigMtu) {
                    currentUuid = BleConstant.CHAR_BIG_UUID_02;
                } else {
                    currentUuid = BleConstant.UUID_BASE_WRITE;
                }
                cmdList.add(val);
            } else {
                byte[] val = new byte[currentMtu];
                for (int i = 0; i < currentMtu; i++) {
                    val[i] = value[i + copy_size];
                }
                if (isSupportBigMtu) {
                    currentUuid = BleConstant.CHAR_BIG_UUID_02;
                } else {
                    currentUuid = BleConstant.UUID_BASE_WRITE;
                }
                cmdList.add(val);
            }
            copy_size += currentMtu;
            length -= currentMtu;
        }

        UUID serviceUUid = BleConstant.UUID_BASE_SERVICE;
        UUID uuid = BleConstant.UUID_BASE_WRITE;
        if (currentUuid.equals(BleConstant.CHAR_BIG_UUID_02)) {
            serviceUUid = BleConstant.UUID_BIG_SERVICE;
            uuid = BleConstant.CHAR_BIG_UUID_02;
        }

        if (mBluetoothGatt == null) {
            return false;
        }
        BluetoothGattService gap_service = mBluetoothGatt.getService(serviceUUid);
        if (gap_service == null) {
            return false;
        }
        BluetoothGattCharacteristic dev_name = gap_service.getCharacteristic(uuid);
        if (dev_name == null) {
            return false;
        }

        while (cmdList.size() > 0) {
            byte[] cmd = cmdList.remove(0);
            if (!writeSpecialCharacteristic(dev_name, uuid, cmd)) {
                return false;
            }
        }
        return true;
    }

    private boolean writeSpecialCharacteristic(BluetoothGattCharacteristic dev_name, UUID uuid, byte[] cmd) {
        dev_name.setValue(cmd);
        boolean status = mBluetoothGatt.writeCharacteristic(dev_name);
        for (int i = 1; i <= 100; i++) {
            if (status) {
                break;
            }
            try {
                Thread.sleep(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            status = mBluetoothGatt.writeCharacteristic(dev_name);
        }
        SysUtils.logContentI(TAG, "uuid = " + uuid + " writeSpecialCharacteristic status end = " + status);
        SysUtils.logAppRunning(TAG, "uuid = " + uuid + " writeSpecialCharacteristic status end = " + status);
        return status;
    }
}


