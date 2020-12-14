package com.zjw.apps3pluspro.eventbus;

/**
 * Created by android
 * on 2020/5/12.
 */
public class DeviceSportStatusEvent {
    public int sportType;
    public boolean paused;

    public DeviceSportStatusEvent(int sportType, boolean paused) {
        this.sportType = sportType;
        this.paused = paused;
    }

}
