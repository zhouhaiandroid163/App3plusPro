package com.zjw.apps3pluspro.eventbus;

/**
 * Created by android
 * on 2020/12/29
 */
public class GetDeviceProtoOtaPrepareStatusEvent {
    public boolean isForce;
    public String version;
    public String md5;

    public GetDeviceProtoOtaPrepareStatusEvent(boolean isForce, String version, String md5) {
        this.isForce = isForce;
        this.version = version;
        this.md5 = md5;
    }
}
