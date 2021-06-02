package com.zjw.apps3pluspro.eventbus;


import com.zjw.apps3pluspro.utils.GpsSportManager;

/**
 * Created by android
 * on 2021/5/13
 */
public class RefreshGpsInfoEvent {
    public GpsSportManager.GpsInfo gpsInfo;

    public RefreshGpsInfoEvent(GpsSportManager.GpsInfo gpsInfo) {
        this.gpsInfo = gpsInfo;
    }
}
