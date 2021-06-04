package com.zjw.apps3pluspro.eventbus;

/**
 * Created by android
 * on 2021/6/2
 */
public class SendOpenWeatherDataEvent {
    public int status = 0;

    public SendOpenWeatherDataEvent(int status) {
        this.status = status;
    }
}
