package com.zjw.apps3pluspro.utils.location;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;


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

    @Override
    public void onCreate() {
        super.onCreate();
        SysUtils.logContentI(TAG, "onCreate");
        startForgeGround();
        gpsSportManager = new GpsSportManager();

        if (locationThread == null) {
            locationThread = new Thread(() -> gpsSportManager.getLatLon(ForegroundLocationService.this, locationListener));
            locationThread.start();
        }
    }

    GpsSportManager.LocationListener locationListener = gpsInfo -> {
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
        startForeground(getClass().hashCode(), notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
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

        super.onDestroy();
    }
}
