package com.zjw.apps3pluspro.eventbus;

import com.zjw.apps3pluspro.module.device.entity.ThemeMarketMyThemeItem;

import java.util.ArrayList;

/**
 * Created by android
 * on 2020/11/9.
 */
public class DeviceWatchFaceListSyncOverEvent {
    public String themeString = "";// "1221221215165,5454511329856122"

    public ArrayList<ThemeMarketMyThemeItem> list;
    public DeviceWatchFaceListSyncOverEvent(String themeString, ArrayList<ThemeMarketMyThemeItem> list) {
        this.themeString = themeString;
        this.list = list;
    }
}
