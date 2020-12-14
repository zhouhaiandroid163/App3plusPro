package com.zjw.apps3pluspro.eventbus;

/**
 * Created by android
 * on 2020/5/18.
 */
public class OffEcgSyncStateEvent {

    public final static int OFF_ECG_SYNC_STATE_START = 1;
    public final static int OFF_ECG_SYNC_STATE_END = 2;

    public int state;

    public OffEcgSyncStateEvent(int state) {
        this.state = state;
    }
}
