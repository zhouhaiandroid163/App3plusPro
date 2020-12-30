package com.zjw.apps3pluspro.eventbus;

/**
 * Created by android
 * on 2020/12/29
 */
public class GetDeviceProtoWatchFacePrepareStatusEvent {

    public String themeId;
    public int themeSize;
    public GetDeviceProtoWatchFacePrepareStatusEvent(String themeId, int themeSize){
        this.themeId = themeId;
        this.themeSize = themeSize;
    }
}
