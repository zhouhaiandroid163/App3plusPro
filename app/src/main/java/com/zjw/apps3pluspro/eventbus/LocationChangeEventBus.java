package com.zjw.apps3pluspro.eventbus;


import com.zjw.apps3pluspro.utils.GpsSportManager;

/**
 * Created by android
 * on 2021/1/21
 */
public class LocationChangeEventBus {
    public GpsSportManager.GpsInfo gpsInfo;

    public LocationChangeEventBus(GpsSportManager.GpsInfo gpsInfo) {
        this.gpsInfo = gpsInfo;
    }
}
