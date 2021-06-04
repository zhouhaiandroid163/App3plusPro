package com.zjw.apps3pluspro.utils.location;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.eventbus.LocationChangeEventBus;
import com.zjw.apps3pluspro.utils.GpsSportManager;
import com.zjw.apps3pluspro.utils.NotificationUtils;
import com.zjw.apps3pluspro.utils.SysUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by android
 * on 2021/1/21
 */
public class ForegroundLocationService extends Service {
    private static final String TAG = ForegroundLocationService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private GpsSportManager gpsSportManager;
    private Thread locationThread = null;

    public PowerManager.WakeLock wakeLock = null;
    public PowerManager powerManager = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        super.onCreate();
        SysUtils.logContentI(TAG, "onCreate");
        startForgeGround();
        SchedulerManager.getInstance().createJob(this);

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        wl.acquire();
        wl.release();

        if (locationThread == null) {
            locationThread = new Thread(() -> gpsSportManager.getLatLon(ForegroundLocationService.this, locationListener));
            locationThread.start();
        }
    }

    GpsSportManager.LocationListener locationListener = gpsInfo -> {
        SysUtils.logContentI(TAG, " location is change");
        EventBus.getDefault().post(new LocationChangeEventBus(gpsInfo));
    };

    private void startForgeGround() {
        NotificationUtils notificationUtils = new NotificationUtils(this);
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = notificationUtils.getChannelNotification(getString(R.string.app_name), getString(R.string.weather_location_ing)).build();
        } else {
            notification = notificationUtils.getNotification_25(getString(R.string.app_name), getString(R.string.weather_location_ing)).build();
        }
        int id = getClass().hashCode();
        startForeground(id, notification);

        gpsSportManager = new GpsSportManager(1, id, notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Intent intentAlarm = new Intent("LOCATION_CLOCK");
            intentAlarm.setClassName(this, "com.zjw.ffit.utils.location.AlarmReceiver");
            intentAlarm.setPackage("com.zjw.ffit.utils.location");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intentAlarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            // 循环时间
            int TIME_INTERVAL = 5 * 60 * 1000;
            // 此处必须使用SystemClock.elapsedRealtime，否则闹钟无法接收
            long triggerAtMillis = SystemClock.elapsedRealtime();

            // 更新开启时间
            triggerAtMillis += TIME_INTERVAL;

            if (alarmManager == null) {
                return START_NOT_STICKY;
            }
            // pendingIntent 为发送广播
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
            } else {// api19以前还是可以使用setRepeating重复发送广播
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, TIME_INTERVAL, pendingIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        SysUtils.logContentI(TAG, "onDestroy");
        if (locationThread != null) {
            locationThread.interrupt();
            locationThread = null;
        }
        gpsSportManager.stopGps(ForegroundLocationService.this);
        gpsSportManager = null;

        if (wakeLock != null) {
            if (wakeLock.isHeld())
                wakeLock.release();
            wakeLock = null;
        }

        SchedulerManager.getInstance().cancelJob();

        super.onDestroy();
    }

}
