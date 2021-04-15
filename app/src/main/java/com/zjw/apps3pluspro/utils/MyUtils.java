package com.zjw.apps3pluspro.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.io.File;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.TextUtils.isEmpty;

public class MyUtils {


    /**
     * 获取报告详情
     *
     * @param context
     * @param item
     * @return
     */
    public static String getPresentation(Context context, String item) {

        String TAG = "MyUtils";
        String result = "";


        if (item == null) {
            MyLog.i(TAG, "getPresentation = " + item);
            result = String.valueOf(context.getText(R.string.ecg_measure_hrv_par0));
        } else if (item.equals("1")) {
            result = String.valueOf(context.getText(R.string.ecg_measure_hrv_par1));

        } else if (item.equals("2")) {
            result = String.valueOf(context.getText(R.string.ecg_measure_hrv_par2));

        } else {
            result = String.valueOf(context.getText(R.string.ecg_measure_hrv_par0));

        }


        return result;
    }


    // 分享相片
    public static void SharePhoto(String photoUri, final Activity activity) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        File file = new File(photoUri);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareIntent.setType("image/jpeg");
        activity.startActivity(Intent.createChooser(shareIntent, ""));
    }

    // 分享pdf文件
    public static void SharePdfFile(String photoUri, final Activity activity) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        File file = new File(photoUri);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareIntent.setType("*/*");
        activity.startActivity(Intent.createChooser(shareIntent, ""));
    }

    // 分享相片
    public static void SharePhoto(Bitmap photoUri, final Activity activity) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, getUri(activity, photoUri));
        shareIntent.setType("image/jpeg");
        activity.startActivity(Intent.createChooser(shareIntent, ""));
    }


    public static int getGaoYaUser2(int heart, int user_heart, int user_par1) {


        int now_gaoya = 0;
        int user_gaoya = 0;
        int result_gaoya = 0;


        if (heart == 0) {
            return 0;
        }


        now_gaoya = (int) ((0.0318 * (heart) + 5.12) / (0.133 * 0.44));
        user_gaoya = (int) ((0.0318 * (user_heart) + 5.12) / (0.133 * 0.44));
        result_gaoya = now_gaoya - user_gaoya + user_par1;


        return result_gaoya;
    }


    public static int getDiYaUser2(int heart, int user_heart, int user_par1) {

        int now_gaoya = 0;
        int user_gaoya = 0;
        int result_gaoya = 0;
        int result_diya = 0;


        if (heart == 0) {
            return 0;
        }


        now_gaoya = (int) ((0.0318 * (heart) + 5.12) / (0.133 * 0.44));
        user_gaoya = (int) ((0.0318 * (user_heart) + 5.12) / (0.133 * 0.44));
        result_gaoya = now_gaoya - user_gaoya + user_par1;


        result_diya = (int) (result_gaoya * 0.4 / 0.62);
        return result_diya;
    }

    /**
     * 获取版本号
     *
     * @return 版本号
     */
    public static int getVersionCode() {
        Context context = BaseApplication.getmContext();

        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;

    }

    /**
     * APP版本号
     *
     * @return
     */
    public static String getAppInfo() {
        Context context = BaseApplication.getmContext();
        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            int versionCode = context.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
//            return pkName + "   " + versionName + "  " + versionCode;
            if (Constants.isBast) {
                versionName = versionName + Constants.BastVersion;
            }
            return "V" + versionName;
        } catch (Exception e) {
        }
        return null;
    }


    public static Uri getUri(Context context, Bitmap bitmap) {
        Uri my_uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null));
        return my_uri;
    }


    public static boolean checkInputNumber(String txt) {
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(txt);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }

    }


    // 将二进制字符串转换成int数组
    public static int[] BinstrToIntArray(byte my_byte) {

        int[] result = new int[8];
        byte[] binStr = new byte[1];
        binStr[0] = my_byte;
        String byte_str = binary(binStr, 2);


        for (int i = byte_str.length() - 1; i >= 0; i--) {

            result[i + 8 - byte_str.length()] = Integer.valueOf(byte_str.substring(i, i + 1));
        }


        return result;
    }

    public static String binary(byte[] bytes, int radix) {
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }

    public static int[] TransformationPPgData(int min, int max) {
        int[] result = new int[5];

        int multiple = (max - min) / 5;

        for (int i = 0; i < result.length; i++) {
            result[i] = min + multiple * (i + 1);
        }

//
//        result [1]= min + multiple*2;
//        result [2]= min + multiple*3;
//        result [3]= min + multiple*4;
//        result [4]= min + multiple*5;


        return result;
    }


    public static int getHrvHealthNumber(int user_height, int user_weight, int user_step, int user_heart) {

        String TAG = "MyUtils";
        int result = 85;
        float user_bmi = (float) user_weight / (((float) user_height / (float) 100) * (float) user_height / (float) 100);
        float weight_no_good = 0;
        float setp_no_good = 0;
        float heart_no_good = 0;

        MyLog.i(TAG, "健康值 = user_bmi = " + user_bmi);

        weight_no_good = Math.abs(user_bmi - (float) 21);
        if (weight_no_good > 10) {
            weight_no_good = 10;
        }

        if (weight_no_good < 0) {
            weight_no_good = 0;
        }

        MyLog.i(TAG, "健康值 = weight_no_good = " + weight_no_good);

        if (user_step >= 15000) {
            setp_no_good = 0;
        } else {
            setp_no_good = ((float) 15000 - (float) user_step) / (float) 1500;
        }

        if (setp_no_good > 10) {
            setp_no_good = 10;
        }
        if (setp_no_good < 0) {
            setp_no_good = 0;
        }

        MyLog.i(TAG, "健康值 = setp_no_good = " + setp_no_good);

        if (user_heart <= 0) {
            heart_no_good = 0;
        } else {
            heart_no_good = ((float) user_heart - (float) 50) / (float) 5;

            heart_no_good = Math.abs(heart_no_good);
        }

        if (heart_no_good > 10) {
            heart_no_good = 10;
        }

        if (heart_no_good < 0) {
            heart_no_good = 0;
        }

        MyLog.i(TAG, "健康值 = heart_no_good = " + heart_no_good);

        result = (int) (100 - (weight_no_good * 1.5) - (setp_no_good) - (heart_no_good * 1.5));

        MyLog.i(TAG, "健康值 = result = " + result);

        if (result > 100) {
            result = 100;
        }

        if (result < 60) {
            result = 60;
        }

        return result;

    }

    public static int getFatigueIndex(int user_heart) {

        String TAG = "MyUtils";
        int result = 50;
        float heart_no_good = 0;


        if (user_heart <= 0) {
            heart_no_good = 0;
        } else {
            heart_no_good = ((float) user_heart - (float) 50) / (float) 2;

            heart_no_good = Math.abs(heart_no_good);
        }

        if (heart_no_good > 30) {
            heart_no_good = 30;
        }

        if (heart_no_good < 0) {
            heart_no_good = 0;
        }

        MyLog.i(TAG, "疲劳指数 心率 = heart_no_good = " + heart_no_good);

        result = (int) (35 + heart_no_good);

        MyLog.i(TAG, "疲劳指数 = result = " + result);

        if (result > 65) {
            result = 65;
        }

        if (result < 35) {
            result = 35;
        }

        return result;

    }

    public static int getLoadIndex(int user_height, int user_weight, int user_heart) {
        String TAG = "MyUtils";
        int result = 70;
        float user_bmi = (float) user_weight / (((float) user_height / (float) 100) * (float) user_height / (float) 100);
        float weight_no_good = 0;
        float heart_no_good = 0;

        MyLog.i(TAG, "健康值 = user_bmi = " + user_bmi);

        weight_no_good = Math.abs(user_bmi - (float) 21);
        if (weight_no_good > 10) {
            weight_no_good = 10;
        }

        if (weight_no_good < 0) {
            weight_no_good = 0;
        }

        MyLog.i(TAG, "健康值 = weight_no_good = " + weight_no_good);


        if (user_heart <= 0) {
            heart_no_good = 0;
        } else {
            heart_no_good = ((float) user_heart - (float) 50) / (float) 5;

            heart_no_good = Math.abs(heart_no_good);
        }

        if (heart_no_good > 10) {
            heart_no_good = 10;
        }

        if (heart_no_good < 0) {
            heart_no_good = 0;
        }

        MyLog.i(TAG, "健康值 = heart_no_good = " + heart_no_good);

        result = (int) (35 + (weight_no_good * 1.5) + (heart_no_good * 3));

        MyLog.i(TAG, "健康值 = result = " + result);

        if (result > 80) {
            result = 80;
        }

        if (result < 35) {
            result = 35;
        }

        return result;

    }

    public static int getBodyIndex(int user_height, int user_weight, int user_heart, int amp_avg) {
        String TAG = "MyUtils";
        int result = 70;
        float user_bmi = (float) user_weight / (((float) user_height / (float) 100) * (float) user_height / (float) 100);
        float weight_no_good = 0;
        float heart_no_good = 0;
        float average_amp_no_good = 0;

        MyLog.i(TAG, "健康值 = user_bmi = " + user_bmi);

        weight_no_good = Math.abs(user_bmi - (float) 21);
        if (weight_no_good > 10) {
            weight_no_good = 10;
        }

        if (weight_no_good < 0) {
            weight_no_good = 0;
        }

        MyLog.i(TAG, "健康值 = weight_no_good = " + weight_no_good);


        if (user_heart <= 0) {
            heart_no_good = 0;
        } else {
            heart_no_good = ((float) user_heart - (float) 50) / (float) 5;

            heart_no_good = Math.abs(heart_no_good);
        }

        if (heart_no_good > 10) {
            heart_no_good = 10;
        }

        if (heart_no_good < 0) {
            heart_no_good = 0;
        }

        average_amp_no_good = (200 - amp_avg) / 10;

        if (average_amp_no_good > 10) {
            average_amp_no_good = 20;
        }

        if (average_amp_no_good < 0) {
            average_amp_no_good = 0;
        }


        MyLog.i(TAG, "健康值 = heart_no_good = " + heart_no_good);

        result = (int) (100 - (weight_no_good * 1.5) - (heart_no_good * 1.5) - average_amp_no_good);

        MyLog.i(TAG, "健康值 = result = " + result);

        if (result > 100) {
            result = 100;
        }

        if (result < 50) {
            result = 50;
        }

        return result;

    }


    public static int getHeartIndex(int user_heart, int amp_avg) {
        String TAG = "MyUtils";
        int result = 70;
        float heart_no_good = 0;
        float average_amp_no_good = 0;


        if (user_heart <= 0) {
            heart_no_good = 0;
        } else {
            heart_no_good = ((float) user_heart - (float) 50) / (float) 5;

            heart_no_good = Math.abs(heart_no_good);
        }

        if (heart_no_good > 10) {
            heart_no_good = 10;
        }

        if (heart_no_good < 0) {
            heart_no_good = 0;
        }

        average_amp_no_good = (200 - amp_avg) / 10;

        if (average_amp_no_good > 10) {
            average_amp_no_good = 20;
        }

        if (average_amp_no_good < 0) {
            average_amp_no_good = 0;
        }


        MyLog.i(TAG, "健康值 = heart_no_good = " + heart_no_good);

        result = (int) (100 - (heart_no_good * 1.5) - average_amp_no_good);

        MyLog.i(TAG, "健康值 = result = " + result);

        if (result > 100) {
            result = 100;
        }

        if (result < 65) {
            result = 50;
        }

        return result;

    }


    /**
     * 判断是否蓝牙名合法
     *
     * @param ble_name
     * @return
     */
    public static boolean checkBleName(String ble_name) {
        boolean result = true;

        if (checkBleNameLenght(ble_name, 5) || checkBleNameLenght(ble_name, 4) || checkBleNameLenght(ble_name, 3)
                || checkBleNameLenght(ble_name, 2) || checkBleNameLenght(ble_name, 1)) {
            result = true;
        } else {
            result = false;
        }

        return result;
    }


    /**
     * 根据，判断倒数第几位为，下划线，返回是否合法
     *
     * @param ble_name 蓝牙名
     * @param len      倒数第几位？
     * @return
     */
    public static boolean checkBleNameLenght(String ble_name, int len) {
        boolean result = true;


        if (ble_name != null && !ble_name.equals("")) {
            if (ble_name.length() >= len) {

                String bbb = ble_name.substring((ble_name.length() - len), ble_name.length() - (len - 1));

                if (bbb.equals("_")) {
                    result = true;
                } else {
                    result = false;
                }

            } else {
                result = false;
            }


        } else {
            result = false;
        }


        return result;
    }

    /**
     * 判断是否蓝牙名合法
     *
     * @param mBleDeviceTools
     * @return
     */
    public static String getNewBleName(BleDeviceTools mBleDeviceTools) {
        String name = mBleDeviceTools.get_ble_name();
        if (name != null && !name.equals("") && !name.equals("null") && name.length() >= 1) {
            String character = name.substring(name.length() - 1);
            if (character.equalsIgnoreCase("_")) {
                name = name.substring(0, name.length() - 1);
            }
        }
        return name;
    }


    //颜色条三角符号
    public static void setMargins(View v1, View v2, int number, int tag1, int tag2, int tag3, int tag4) {

        String TAG = "MyUtils";

        float bgWidth = v2.getWidth();

        float bgOneWidth = bgWidth / 3;


        MyLog.i(TAG, "健康值 = bgWidth = " + bgWidth);
        MyLog.i(TAG, "健康值 = bgOneWidth = " + bgOneWidth);
//        int location =(int)(bgWidth*(float)number/(float)Max);
        int location = 0;

        if (number < tag2) {
            location = (int) (bgOneWidth * (float) number / (float) (tag2 - tag1));


            MyLog.i(TAG, "健康值 = 0-70 = location = " + location);
        } else if (number >= tag2 && number < tag3) {
//            location =(int)(bgOneWidth*(float)number/(float)tag1);

            location = (int) (bgOneWidth * (float) (number - tag2) / (float) (tag3 - tag2)) + (int) bgOneWidth;

            MyLog.i(TAG, "健康值 = 70-90 = location = " + location);

        } else {
            location = (int) (bgOneWidth * (float) (number - tag3) / (float) (tag4 - tag3)) + (int) bgOneWidth + (int) bgOneWidth;

            MyLog.i(TAG, "健康值 = 90-100 = location = " + location);
        }


        if (v1.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v1.getLayoutParams();
            p.setMargins(location, 0, 0, 0);
            v1.requestLayout();
        }
    }

    //颜色条三角符号
    public static void setCycleMargins(View v1, View v2, int number) {

        String TAG = "MyUtils";

        float imgWidth = v1.getWidth();
        float bgWidth = v2.getWidth();


        int location = 0;
        if (number == 1) {
            location = (int) (bgWidth * 0.125 - imgWidth / 2);
        } else if (number == 2) {
            location = (int) (bgWidth * 0.355 - imgWidth / 2);
        } else if (number == 3) {
            location = (int) (bgWidth * 0.585 - imgWidth / 2);
        } else if (number == 4) {
            location = (int) (bgWidth * 0.825 - imgWidth / 2);
        }


        if (v1.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v1.getLayoutParams();
            p.setMargins(location, 0, 0, 0);
            v1.requestLayout();
        }
    }

    //颜色条三角符号
    public static void setMarginsTextView(View v1, int x, int y) {

        String TAG = "MyUtils";
        if (v1.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v1.getLayoutParams();
            p.setMargins(x, y, 0, 0);
            v1.requestLayout();
        }
    }

    //颜色条三角符号
    public static void setMarginsTextView(View v1, View v2, int number) {

        String TAG = "MyUtils";

        float vWidth = v1.getWidth();
        float vHeight = v1.getHeight();

        float bgWidth = v2.getWidth();
        float bgHeight = v2.getHeight();

        int seek_left = (int) ((bgWidth - vWidth) / 2);
        int seek_top = (int) ((bgHeight - vHeight) / 2);

        switch (number) {
            case 1:
                seek_top = (int) ((bgHeight) / 3 - vHeight + vHeight * 0.2);
                break;
            case 2:
                seek_top = (int) ((bgHeight - vHeight) / 2);
                break;
            case 3:
                seek_top = (int) ((bgHeight) / 3 * 2 - vHeight * 0.2);
                break;
        }


        MyLog.i(TAG, "健康值 = vWidth = " + vWidth);
        MyLog.i(TAG, "健康值 = vHeight = " + vHeight);
        MyLog.i(TAG, "健康值 = bgWidth = " + bgWidth);
        MyLog.i(TAG, "健康值 = bgHeight = " + bgHeight);
        MyLog.i(TAG, "健康值 = seek_left = " + seek_left);
        MyLog.i(TAG, "健康值 = seek_top = " + seek_top);


        if (v1.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v1.getLayoutParams();
            p.setMargins(seek_left, seek_top, 0, 0);
            v1.requestLayout();
        }
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static boolean isGPSOpen(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//         通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

//        return true;
    }

    /**
     * 0.
     * 强制帮用户打开GPS
     *
     * @param context
     */
    public static void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    public static String CmToInString(String cm) {

        if (Integer.valueOf(cm) == 127 || Integer.valueOf(cm) == 254) {
            return String.valueOf((int) (Double.valueOf(cm) / 2.54));
        } else {
            return String.valueOf((int) (Double.valueOf(cm) / 2.54 + 1));
        }

    }

    public static int CmToInInt(String cm) {

        if (Integer.valueOf(cm) == 127 || Integer.valueOf(cm) == 254) {
            return (int) (Double.valueOf(cm) / 2.54);
        } else {
            return (int) (Double.valueOf(cm) / 2.54 + 1);
        }

    }

    public static String InToCmString(String in) {
        return String.valueOf((int) (Double.valueOf(in) * 2.54));
    }

    public static String KGToLBString(String kg, Context mContext) {

        UserSetTools mUserSetTools = new UserSetTools(mContext);


        return String.valueOf((int) (Double.valueOf(kg) * 2.2) + mUserSetTools.get_user_weight_disparity());

    }

    public static int KGToLBInt(String kg, Context mContext) {

        UserSetTools mUserSetTools = new UserSetTools(mContext);
        return (int) (Double.valueOf(kg) * 2.2 + mUserSetTools.get_user_weight_disparity());

    }

    public static String LBToKGString(String lb) {
        return String.valueOf((int) ((float) (Integer.valueOf(lb)) / 2.2f));
    }

    public static int getWeightDisparity(int number) {
        int aa = (int) ((float) number / 2.2f);
        int bb = (int) (aa * 2.2);

        return number - bb;
    }

    public static String MyFormatTime(int time) {
        String result = "";

        if (time < 10) {
            result = "0" + String.valueOf(time);
        } else {
            result = String.valueOf(time);
        }


        return result;

    }

//    /**
//     * 获取设备唯一标识
//     *
//     * @return
//     */
//    public static String getDeviceId(Context mContext) {
//
//        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
//
//        return tm.getDeviceId();
//
//    }


    /**
     * 获取手机IMEI
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        String imei = "unknown";
        try {
            imei = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }

    /**
     * 获取手机IMSI
     */
    public static String getIMSI(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMSI号
            String imsi = telephonyManager.getSubscriberId();
            if (null == imsi) {
                imsi = "";
            }
            return imsi;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getUniqueId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        @SuppressLint("MissingPermission") String uniqueId = tm.getDeviceId();
        @SuppressLint("MissingPermission") String uniqueId = "";
        if (isEmpty(uniqueId)) {
            uniqueId = android.os.Build.SERIAL;
        }
        if (isEmpty(uniqueId)) {
            uniqueId = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        }
        if (isEmpty(uniqueId)) {
//            uniqueId = getUUID(context);
            uniqueId = "00";
        }
        return uniqueId;
    }

//    private String getUUID(Context context) {
//        String uuid = (String) SPUtils.get(context, "DeviceId", "");
//        if (isEmpty(uuid)) {
//            uuid = UUID.randomUUID().toString();
//            SPUtils.put(context, "DeviceId", uuid);
//        }
//        return uuid;
//    }


    /**
     * 获取系统版本号
     *
     * @return
     */
    public static String getOsVersion() {
        return android.os.Build.VERSION.RELEASE;

    }

    /**
     * 获取手机品牌+型号
     *
     * @return
     */
    public static String getPhoneModel() {
        return android.os.Build.BRAND + " " + android.os.Build.MODEL;

    }

    /**
     * App名字
     *
     * @return
     */
    public static String getAppName() {
        Context context = BaseApplication.getmContext();
        return context.getString(R.string.app_name);
    }


    public static final int multiple = 9;
    public static final int differential = 1052954232;

    /**
     * ID加密
     *
     * @return
     */
    public static String encryptionUid(String user_id) {
        long result;
        try {
            long uid = Long.parseLong(user_id);

            result = uid * multiple + differential;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return String.valueOf(result);
    }

    /**
     * ID解密
     *
     * @return
     */
    public static String decryptionUid(String user_id) {
        long result;
        try {
            long uid = Long.parseLong(user_id);

            result = (uid - differential) / multiple;

            if (result <= 0) {
                result = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return String.valueOf(result);
    }

    public static int getHeartAvg(List data) {
        int avg = 0;
        int total = 0;

        if (data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                total += (int) data.get(i);
            }
            avg = total / data.size();
        }


        return avg;
    }

    public static int getNoTransparentColor(int color) {

        int a = 255;
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Color.argb(a, r, g, b);
    }

    public static boolean isGoogle(Context context) {

        boolean result = true;

        UserSetTools mUserSetTools = new UserSetTools(context);

        if (mUserSetTools.get_map_enable()) {
            if (mUserSetTools.get_is_google_map()) {
                //谷歌
                result = true;
            } else {
                //高德
                result = false;
            }
        } else {
            if (AppUtils.isZh(context)) {
                //高德
                result = false;
            } else {
                //谷歌
                result = true;
            }
        }
        return result;
    }

    public static void setFocusable(EditText editTextIP) {
        editTextIP.setFocusable(true);
        editTextIP.setFocusableInTouchMode(true);
        editTextIP.requestFocus();//获取焦点 光标出现

    }

    public static void noShowInputMethod(View view) {
        /* 隐藏软键盘 */
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }

    /**
     * 获取屏保X坐标,Y坐标
     *
     * @param bianju_width  边距-宽度
     * @param bianju_height 边距-高度
     * @return
     */
    public static int[] getSendBleCoordinate(BleDeviceTools mBleDeviceTools, int bianju_width, int bianju_height) {


        int pos = mBleDeviceTools.get_screensaver_post_time();
        int type = mBleDeviceTools.get_screen_shape();


        //屏幕宽度
        int screensaverWidth = mBleDeviceTools.get_screensaver_resolution_width();
        //屏幕高度
        int screensaverHeight = mBleDeviceTools.get_screensaver_resolution_height();
        //时间宽度
        int timeWidth = mBleDeviceTools.get_screensaver_time_width();
        //时间高度
        int timeHeight = mBleDeviceTools.get_screensaver_time_height();


        int[] result = new int[2];

        //默认居中
//        result[0] = (screensaverWidth - timeWidth) / 2;
//        result[1] = (screensaverHeight - timeHeight) / 2;

        //方屏
        if (type == 0) {

            //居中
            if (pos == 0) {

                int Xcoordinate = (screensaverWidth - timeWidth) / 2;
                int Ycoordinate = (screensaverHeight - timeHeight) / 2;

                result[0] = Xcoordinate;
                result[1] = Ycoordinate;

            }
            //左上
            else if (pos == 3) {

                int Xcoordinate = bianju_width;
                int Ycoordinate = bianju_height;

                result[0] = Xcoordinate;
                result[1] = Ycoordinate;

                //右上
            } else if (pos == 4) {

                int Xcoordinate = screensaverWidth - timeWidth - bianju_width;
                int Ycoordinate = bianju_height;

                result[0] = Xcoordinate;
                result[1] = Ycoordinate;
            }
            //左下
            else if (pos == 5) {

                int Xcoordinate = bianju_width;
                int Ycoordinate = screensaverHeight - timeHeight - bianju_height;

                result[0] = Xcoordinate;
                result[1] = Ycoordinate;

            }
            //右下
            else if (pos == 6) {
                int xx6 = screensaverWidth - timeWidth - bianju_width;
                int yy6 = screensaverHeight - timeHeight - bianju_height;

                result[0] = xx6;
                result[1] = yy6;

            } else {
                result[0] = (screensaverWidth - timeWidth) / 2;
                result[1] = (screensaverHeight - timeHeight) / 2;
            }


            //球拍屏1=240*204
        } else if (type == 1) {

            //居中
            if (pos == 0) {
                int Xcoordinate = (screensaverWidth - timeWidth) / 2;
                int Ycoordinate = (240 - timeHeight) / 2;

                result[0] = Xcoordinate;
                result[1] = Ycoordinate;

                //上
            } else if (pos == 1) {

                int Xcoordinate = (screensaverWidth - timeWidth) / 2;
                int Ycoordinate = 37 + bianju_height;

                result[0] = Xcoordinate;
                result[1] = Ycoordinate;

            }
            //下
            else if (pos == 2) {

                int Xcoordinate = (screensaverWidth - timeWidth) / 2;
                int Ycoordinate = 150 - bianju_height;

                result[0] = Xcoordinate;
                result[1] = Ycoordinate;

                //左
            } else if (pos == 7) {
                int Xcoordinate = 3 + bianju_width;
                int Ycoordinate = (240 - timeHeight) / 2;

                result[0] = Xcoordinate;
                result[1] = Ycoordinate;

                //右
            } else if (pos == 8) {

                int Xcoordinate = screensaverWidth - timeWidth - 3 - bianju_width;
                int Ycoordinate = (240 - timeHeight) / 2;

                result[0] = Xcoordinate;
                result[1] = Ycoordinate;
            } else {
                result[0] = (screensaverWidth - timeWidth) / 2;
                result[1] = (screensaverHeight - timeHeight) / 2;
            }

        } else {

            result[0] = (screensaverWidth - timeWidth) / 2;
            result[1] = (screensaverHeight - timeHeight) / 2;

        }


        return result;

    }

    public static boolean checkBlePairName(String pair_name) {

        boolean result = false;

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();


            for (Iterator<BluetoothDevice> iterator = devices.iterator(); iterator.hasNext(); ) {
                BluetoothDevice device = iterator.next();

                MyLog.i("checkBlePairName", "配对列表 = device_name " + device.getName());
                MyLog.i("checkBlePairName", "配对列表 = device_address " + device.getAddress());

                if (device != null && device.getName() != null && !device.getName().equals("") && device.getName().equals(pair_name)) {
                    result = true;
                    break;
                }

            }

        }

        return result;
    }

    public static boolean checkBlePairMac(String pair_mac) {

        boolean result = false;

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();


            for (Iterator<BluetoothDevice> iterator = devices.iterator(); iterator.hasNext(); ) {
                BluetoothDevice device = iterator.next();

                MyLog.i("checkBlePairMac", "配对列表 = device_name " + device.getName());
                MyLog.i("checkBlePairMac", "配对列表 = device_address " + device.getAddress());

                if (device != null && device.getAddress() != null && !device.getAddress().equals("")) {
                    String device_mac = device.getAddress();
                    device_mac = device_mac.toUpperCase();
                    if (device_mac.equals(pair_mac)) {
                        result = true;
                        break;
                    }
                }


            }

        }

        return result;
    }

    public static int getNewHr(int ppg_hr, int ecg_hr) {
        int jueduizhi = Math.abs((ppg_hr - ecg_hr));
        if (jueduizhi < 10) {
            return ecg_hr;
        } else {
            return ppg_hr;
        }
    }

    public static boolean isDataiDentical(int[] data) {
        boolean result = false;

        if (data != null && data.length >= 9) {
            if (data[0] == data[1] &&
                    data[0] == data[2] &&
                    data[0] == data[3] &&
                    data[0] == data[4] &&
                    data[0] == data[5] &&
                    data[0] == data[6] &&
                    data[0] == data[7] &&
                    data[0] == data[8]) {
                result = true;
            } else {
                result = false;
            }
        }
        return result;

    }

}
