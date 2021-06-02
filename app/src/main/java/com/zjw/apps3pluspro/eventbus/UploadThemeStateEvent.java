package com.zjw.apps3pluspro.eventbus;

/**
 * Created by android
 * on 2021/5/13
 */
public class UploadThemeStateEvent {
    public int state;
    public int progress;

    public UploadThemeStateEvent(int state, int progress) {
        this.state = state;
        this.progress = progress;
    }
}
