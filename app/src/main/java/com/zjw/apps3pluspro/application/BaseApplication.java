package com.zjw.apps3pluspro.application;

import android.content.Context;

import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.android.mycamera.util.CameraUtil;
import com.android.mycamera.util.UsageStatistics;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.umeng.socialize.PlatformConfig;

import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.bleservice.BleService;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.RemindeTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.dbmanager.ContinuitySpo2InfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.ContinuityTempInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.HealthInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.HeartInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.MeasureSpo2InfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.MeasureTempInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.MovementInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.PhoneInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.SleepInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.SportModleInfoUtils;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.NotificationUtils;

import java.util.Locale;

import no.nordicsemi.android.dfu.DfuServiceInitiator;


/**
 * pplication 类  //兼容5.0以下 继承MultiDexApplication
 */
public class BaseApplication extends MultiDexApplication {
    private final String TAG = BaseApplication.class.getSimpleName();

    //数据库存储
    private static MovementInfoUtils movementInfoUtils;
    private static SleepInfoUtils sleepInfoUtils;
    private static HeartInfoUtils heartInfoUtils;
    private static HealthInfoUtils mHealthInfoUtils;
    private static SportModleInfoUtils mSportModleInfoUtils;
    private static PhoneInfoUtils mPhoneInfoUtils;
    private static ContinuitySpo2InfoUtils mContinuitySpo2InfoUtils;
    private static MeasureSpo2InfoUtils mMeasureSpo2InfoUtils;
    private static ContinuityTempInfoUtils mContinuityTempInfoUtils;
    private static MeasureTempInfoUtils mMeasureTempInfoUtils;
    //轻量级存储
    private static UserSetTools mUserSetTools;
    private static BleDeviceTools mBleDeviceTools;
    private static RemindeTools mRemindeTools;

    //------------------ 项目区分------------
    public static final int APP_UPDATE_MODE_FFit = 80;

    //------------------ 测试项目提交------------
    // 全局上下文环境
    private static Context mContext;
    // 主线程id
    private static int mainThreadId;

    /* Volley */
    public static RequestQueue queue;
    //添加好友的简单处理
    public static boolean isAddFriend = false;

    //添加好友的简单处理
    public static boolean isScanActivity = false;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DfuServiceInitiator.createDfuNotificationChannel(this);
            NotificationUtils notificationUtils = new NotificationUtils(this);
            notificationUtils.createNotificationChannel();
        }

        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new MediaLoader())
                .setLocale(Locale.getDefault())
                .build()
        );

        mContext = getApplicationContext();
        // 主线线程id
        mainThreadId = android.os.Process.myTid();

        if (Constants.CRASH_LOG) {
            MyCrashUtil.getInstance().init(getApplicationContext(), Constants.CRASH_DIR, mContext.getString(R.string.crash_log_tip));
        }

        // 初始化Volley
        queue = Volley.newRequestQueue(mContext);

        UsageStatistics.initialize(this);
        CameraUtil.initialize(this);

//		开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
//		Config.DEBUG = true;
//		QueuedWork.isUseThreadPool = false;
//		UMShareAPI.get(this);

        initSharedPreferences();
        initSQL();
        initCearm();

        Intent gattServiceIntent = new Intent(this, BleService.class);
        gattServiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(gattServiceIntent);
        } else {
            startService(gattServiceIntent);
        }


    }

    //各个平台的配置，建议放在全局Application或者程序入口
    {
        //第三方登录分享-QQ
        PlatformConfig.setQQZone("1109282640", "5fLdz8G8MClNQ3vQ");
        //第三方登录分享-微信
        PlatformConfig.setWeixin("wx4e830871c044df85", "29e56307a9cce429740187b0b8d2bb5b");
        ///第三方登录分享-推特
        PlatformConfig.setTwitter("9HdvxeWpYRe58CkI6QT4K1e4r", "rWkcEPvivgEewDJJCRg51Zi7DVBrWBVi7fKgI4pMglS0bNVBWx");
    }

    public static RequestQueue getHttpQueue() {
        return queue;
    }

    public static Context getmContext() {
        return mContext;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }


    void initSharedPreferences() {
        mUserSetTools = new UserSetTools(this);
        mBleDeviceTools = new BleDeviceTools(this);
        mRemindeTools = new RemindeTools(this);
    }

    public static UserSetTools getUserSetTools() {
        return mUserSetTools;
    }

    public static BleDeviceTools getBleDeviceTools() {
        return mBleDeviceTools;
    }

    public static RemindeTools getRemindeTools() {
        return mRemindeTools;
    }

    public static void setUserId(String user_id) {
        mUserSetTools.set_user_id(user_id);
    }

    public static String getUserId() {
        return mUserSetTools.get_user_id();
    }

    public static String getRegisterTime() {
        return mUserSetTools.get_user_register_time();
    }

    void initSQL() {
        movementInfoUtils = new MovementInfoUtils(this);
        sleepInfoUtils = new SleepInfoUtils(this);
        heartInfoUtils = new HeartInfoUtils(this);
        mHealthInfoUtils = new HealthInfoUtils(this);
        mSportModleInfoUtils = new SportModleInfoUtils(this);
        mPhoneInfoUtils = new PhoneInfoUtils(this);
        mContinuitySpo2InfoUtils = new ContinuitySpo2InfoUtils(this);
        mMeasureSpo2InfoUtils = new MeasureSpo2InfoUtils(this);
        mContinuityTempInfoUtils = new ContinuityTempInfoUtils(this);
        mMeasureTempInfoUtils = new MeasureTempInfoUtils(this);
    }

    public static MovementInfoUtils getMovementInfoUtils() {
        return movementInfoUtils;
    }

    public static HealthInfoUtils getHealthInfoUtils() {
        return mHealthInfoUtils;
    }

    public static SleepInfoUtils getSleepInfoUtils() {
        return sleepInfoUtils;
    }

    public static HeartInfoUtils getHeartInfoUtils() {
        return heartInfoUtils;
    }

    public static SportModleInfoUtils getSportModleInfoUtils() {
        return mSportModleInfoUtils;
    }

    public static PhoneInfoUtils getPhoneInfoUtils() {
        return mPhoneInfoUtils;
    }

    public static ContinuitySpo2InfoUtils getContinuitySpo2InfoUtils() {
        return mContinuitySpo2InfoUtils;
    }

    public static MeasureSpo2InfoUtils getMeasureSpo2InfoUtils() {
        return mMeasureSpo2InfoUtils;
    }

    public static ContinuityTempInfoUtils getContinuityTempInfoUtils() {
        return mContinuityTempInfoUtils;
    }

    public static MeasureTempInfoUtils getMeasureTempInfoUtils() {
        return mMeasureTempInfoUtils;
    }

    //兼容5.0以下
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    //兼容7.0拍照问题
    private void initCearm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }


}
