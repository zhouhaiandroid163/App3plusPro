package com.zjw.apps3pluspro.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yangjungle on 15/7/7.
 */

/**
 * 用于吃药，喝水，久坐，会议提醒用到的。 轻量级存储工具类
 */
public class RemindeTools {

    private Context cx;
    private static String RemindeTools = "RemindeTools";

    public RemindeTools(Context context) {
        this.cx = context;
    }

    public SharedPreferences getSharedPreferencesCommon() {
        SharedPreferences settin = cx.getSharedPreferences(RemindeTools, 0);
        return settin;
    }


    public boolean WriteKeyValue(String Key, int value) {
        try {
            SharedPreferences settin = getSharedPreferencesCommon();
            SharedPreferences.Editor editor = settin.edit();
            editor.putInt(Key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public int GetValue(String key, int def) {
        SharedPreferences settin = getSharedPreferencesCommon();
        return settin.getInt(key, def);
    }


}
