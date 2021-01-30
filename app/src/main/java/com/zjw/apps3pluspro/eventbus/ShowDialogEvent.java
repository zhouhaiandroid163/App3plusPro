package com.zjw.apps3pluspro.eventbus;

/**
 * Created by android
 * on 2021/1/30
 */
public class ShowDialogEvent {
    public int type = 0; // 0 定位权限未开启  1 gps开关未开启

    public ShowDialogEvent(int type) {
        this.type = type;
    }
}
