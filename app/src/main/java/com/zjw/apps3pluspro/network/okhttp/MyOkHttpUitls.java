package com.zjw.apps3pluspro.network.okhttp;

import com.zjw.apps3pluspro.utils.MyUtils;

public class MyOkHttpUitls {


    public static String getUserAgent() {
        String result = "";
        String userAgent = System.getProperty("http.agent");
        String appVersionName = MyUtils.getAppInfo();
        String appVersionNumber = String.valueOf(MyUtils.getVersionCode());
        String appName = MyUtils.getAppName();
        result = appName + "/" + appVersionNumber + " " + userAgent;
        return result;
    }
}
