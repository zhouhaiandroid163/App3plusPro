package com.zjw.apps3pluspro.module.device.clockdial.custom;

import android.graphics.Bitmap;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.module.device.clockdial.custom.model.HandleCustomClockDialModle;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.ThemeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;


/**
 * 自定义表盘数据处理
 */
public class CustomClockDialNewUtils {
    private final static String TAG = CustomClockDialNewUtils.class.getSimpleName();

    public static byte[] getNewCustomClockDialData(String fileName, int color_R, int color_G, int color_B, Bitmap bg_bmp, Bitmap text_bitmp) {
        initThemeVersion();

        String file_path = Constants.DOWN_THEME_FILE + fileName;
        MyLog.i(TAG, "file_path = " + file_path);

        byte[] source_data = ThemeUtils.getBytes(file_path);
        MyLog.i(TAG, "source_data = " + source_data.length);

        MyLog.i(TAG, "getNewCustomClockDialData source_data len = " + source_data.length);


        //黑色需要处理
        if (color_R < 8) {
            color_R = 8;
        }
        if (color_G < 8) {
            color_G = 8;
        }
        if (color_B < 8) {
            color_B = 8;
        }

        byte[] targetData = null;
        int version = source_data[0] & 0xff;

        HandleCustomClockDialModle mHandleCustomClockDialModle = new HandleCustomClockDialModle(source_data);
        MyLog.i(TAG, "getNewCustomClockDialData mHandleCustomClockDialModle len = " + mHandleCustomClockDialModle.toString());

        if (version == 1) {
            targetData = HandleUtilsV1.getData(source_data, color_R, color_G, color_B, bg_bmp, text_bitmp);
        }
        return targetData;
    }

    public static int curThemeVersionRule = 0;
    public static final int themeVersionRule_Positive = 1;
    public static final int themeVersionRule_Reverse = 2;

    private static void initThemeVersion() {
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        if (mBleDeviceTools.getClockDialGenerationMode() == 1) {
            if (mBleDeviceTools.getClockDialMuLanVersion() == 1) {//木兰-规则1
                //正向
                curThemeVersionRule = themeVersionRule_Positive;
            } else if (mBleDeviceTools.getClockDialMuLanVersion() == 2) {//木兰-规则2
                //反向
                curThemeVersionRule = themeVersionRule_Reverse;
            } else { //木兰-老规则
                if (mBleDeviceTools.getClockDialDataFormat() == 1) {
                    //反向
                    curThemeVersionRule = themeVersionRule_Reverse;
                } else {
                    //正向
                    curThemeVersionRule = themeVersionRule_Positive;
                }
            }
        } else { //舟海表盘规则
            if (mBleDeviceTools.getClockDialDataFormat() == 1) { //反向
                curThemeVersionRule = themeVersionRule_Reverse;
            } else {//正向
                curThemeVersionRule = themeVersionRule_Positive;
            }
        }
    }


}
