package com.zjw.apps3pluspro.module.device.entity;

import org.jetbrains.annotations.NotNull;

/**
 * Created by android
 * on 2020/12/16
 */
public class ThemeMarketMyThemeItem {
    public String dialCode;
    public String dialImageUrl;
    public String name;
    public boolean isCurrent;
    public boolean canRemove;

    @NotNull
    @Override
    public String toString() {
        return "ThemeMarketMyThemeItem{" +
                "dialCode='" + dialCode + '\'' +
                ", dialImageUrl='" + dialImageUrl + '\'' +
                ", name='" + name + '\'' +
                ", isCurrent=" + isCurrent +
                ", canRemove=" + canRemove +
                '}';
    }
}
