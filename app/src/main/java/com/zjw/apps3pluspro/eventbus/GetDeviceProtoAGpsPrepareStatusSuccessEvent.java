package com.zjw.apps3pluspro.eventbus;

/**
 * Created by android
 * on 2020/12/29
 */
public class GetDeviceProtoAGpsPrepareStatusSuccessEvent {
    public boolean needGpsInfo;

    public GetDeviceProtoAGpsPrepareStatusSuccessEvent(boolean needGpsInfo) {
        this.needGpsInfo = needGpsInfo;
    }
}
