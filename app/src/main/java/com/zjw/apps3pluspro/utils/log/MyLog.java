package com.zjw.apps3pluspro.utils.log;

import android.util.Log;

import com.zjw.apps3pluspro.application.BaseApplication;

/**
 * Created by zjw on 2017/10/18.
 */

public class MyLog {

    private MyLog() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (isDebug)
//            Log.i(tag + ".class", "MyLog -> " + msg);
            Log.i(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isDebug)
//            Log.e(tag + ".class", "MyLog -> " + msg);
            Log.e(tag, msg);
    }
    public static void w(String tag, String msg) {
        if (isDebug)
//            Log.e(tag + ".class", "MyLog -> " + msg);
            Log.e(tag, msg);
    }

    public static String LOG_TAG_ERROR_ = BaseApplication.getmContext().getPackageName() + "_error";
    ;

    public static void logError(Throwable e) {
        Log.e(LOG_TAG_ERROR_, Log.getStackTraceString(e));
    }
}
