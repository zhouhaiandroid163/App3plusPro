package com.zjw.apps3pluspro.module.device.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by android
 * on 2020/10/7
 */
public class ThemeMarketItem {

    public int dialTypeId;
    public String dialTypeName;
    public int dialType;
    public ArrayList<DialInfo> dialList;

    public static class DialInfo implements Serializable {
        public Long dialId;
        public String dialName;
        public String effectImgUrl;
    }
}
