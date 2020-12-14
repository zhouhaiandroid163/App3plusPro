package com.zjw.apps3pluspro.eventbus;

/**
 * Created by android
 * on 2020/5/18.
 */
public class GpsSportDeviceStartEvent {

    public static final int SPORT_STATE_START = 0;
    public static final int SPORT_STATE_PAUSE = 1;
    public static final int SPORT_STATE_RESUME = 2;
    public static final int SPORT_STATE_STOP = 3;

    public int state = 0;

    public GpsSportDeviceStartEvent(int state) {
        this.state = state;
    }
}
