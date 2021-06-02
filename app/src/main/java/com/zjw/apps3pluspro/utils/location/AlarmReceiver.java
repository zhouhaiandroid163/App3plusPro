package com.zjw.apps3pluspro.utils.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zjw.apps3pluspro.bleservice.BleService;
import com.zjw.apps3pluspro.eventbus.GpsSportDeviceStartEvent;

/**
 * Created by android
 * on 2021/5/14
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("AlarmReceiver", "--->>>   onReceive  LOCATION_CLOCK");
        if (intent.getAction().equals("LOCATION_CLOCK")) {
            if (BleService.currentGpsSportState == GpsSportDeviceStartEvent.SPORT_STATE_START || BleService.currentGpsSportState == GpsSportDeviceStartEvent.SPORT_STATE_RESUME) {
                Intent locationIntent = new Intent(context, ForegroundLocationService.class);
                context.startService(locationIntent);
                Log.e("AlarmReceiver", "startService ForegroundLocationService");
            }
        }
    }
}
