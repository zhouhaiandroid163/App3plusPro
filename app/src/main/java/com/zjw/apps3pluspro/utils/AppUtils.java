package com.zjw.apps3pluspro.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.log.MyLog;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 该项目特有的工具类 字符串什么的
 *
 * @author Administrator
 */
public class AppUtils {
    private static String TAG = AppUtils.class.getSimpleName();

    public static void showToast(Context context, int msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showToastStr(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static String getLanguageFullName(Context context) {
        String result = "";
        Locale locale = context.getResources().getConfiguration().locale;

        MyLog.i(TAG, "getLanguageType locale = " + locale.toString());

        if (!locale.toString().equals("")) {
            result = locale.toString();
        }
        return result;
    }

    public static String getLanguageType(Context context) {
        String result = "";
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        String country = locale.getCountry();

        MyLog.i(TAG, "getLanguageType locale = " + locale.toString());

        if (!language.equals("")) {
            result = language;
        }

        if (!country.equals("")) {
            result = result + "_" + country;
        }
        return result;
    }


    public static boolean isZh(Context context) {

        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        String country = locale.getCountry().toLowerCase();
        MyLog.i(TAG, "当前语言类型 = " + language + "地区 = " + country);

        if (language.endsWith("zh") && country.endsWith("cn"))
            return true;
        else
            return false;

    }

    public static boolean isChina(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();

        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    public static int getCountry(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        int result = 0;

        MyLog.i(TAG, "getCountry = language = " + language);

        //英语
        if (language.endsWith("en")) {
            result = 0;
        }
        //中文
        else if (language.endsWith("zh")) {
            //中文简体
            if (isZh(context)) {
                result = 1;
            }
            //中文繁体
            else {
                result = 2;
            }
        }
        //中文简体
        else if (language.endsWith("zh")) {
            result = 1;
        }
        //日语
        else if (language.endsWith("ja")) {
            result = 3;
        }
        //法语
        else if (language.endsWith("fr")) {
            result = 4;
        }
        //德语
        else if (language.endsWith("de")) {
            result = 5;
        }
        //意大利语
        else if (language.endsWith("it")) {
            result = 6;
        }
        //西班牙语
        else if (language.endsWith("es")) {
            result = 7;
        }
        //俄罗斯语
        else if (language.endsWith("ru")) {
            result = 8;
        }
        //葡萄牙语
        else if (language.endsWith("pt")) {
            result = 9;
        }
        //马来语
        else if (language.endsWith("ms")) {
            result = 10;
        }
        //韩语
        else if (language.endsWith("ko")) {
            result = 11;
        }
        //波兰语
        else if (language.endsWith("pl")) {
            result = 12;
        }
        //泰语
        else if (language.endsWith("th")) {
            result = 13;
        }
        //罗马尼亚语
        else if (language.endsWith("ro")) {
            result = 14;
        }
        //保加利亚语
        else if (language.endsWith("bg")) {
            result = 15;
        }
        //匈牙利语
        else if (language.endsWith("hu")) {
            result = 16;
        }
        //土耳其语
        else if (language.endsWith("tr")) {
            result = 17;
        }
        //捷克语
        else if (language.endsWith("cs")) {
            result = 18;
        }
        //斯洛伐克语
        else if (language.endsWith("sk")) {
            result = 19;
        }
        //丹麦语
        else if (language.endsWith("da")) {
            result = 20;
        }
        //挪威语
        else if (language.endsWith("nb")) {
            result = 21;
        }
        //瑞典语
        else if (language.endsWith("sv")) {
            result = 22;
        }
        //菲律宾语
        else if (language.endsWith("fil")) {
            result = 23;
        }
        //乌克兰语
        else if (language.endsWith("uk")) {
            result = 24;
        }
        //越南语
        else if (language.endsWith("vi")) {
            result = 25;
        } //荷兰语
        else if (language.endsWith("nl")) {
            result = 26;
        }//克罗地亚
        else if (language.endsWith("hr")) {
            result = 27;
        } else {
            result = 0;
        }

        MyLog.i(TAG, "getCountry = result = " + result);

        return result;
    }


    public static String GetFormat(int length, float value) {
        MyLog.i(TAG, "GetFormat 算法验证 = value = " + value);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(length);
        df.setGroupingSize(0);
        df.setRoundingMode(RoundingMode.FLOOR);

        return df.format(value);
    }

    public static String getCalory(int sportStep) {
        UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
        int height = 170;
        if (mUserSetTools.get_user_height() != 0) {
            height = mUserSetTools.get_user_height();
        }

        int weight = 65;
        if (mUserSetTools.get_user_weight() != 0) {
            weight = mUserSetTools.get_user_weight();
        }
        String calory;
        if (BaseApplication.getBleDeviceTools().get_step_algorithm_type() == 1) {
            calory = AppUtils.GetFormat(0, BleTools.getOneCalory(height, weight, sportStep));
        } else if (BaseApplication.getBleDeviceTools().get_step_algorithm_type() == 2) {
            calory = AppUtils.GetFormat(0, BleTools.getTwoCalory(height, weight, sportStep));
        } else {
            calory = AppUtils.GetFormat(0, BleTools.getOldCalory(sportStep));
        }
        return calory;
    }

    public static String getDistance(int sportStep) {
        UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
        int height = 170;
        if (mUserSetTools.get_user_height() != 0) {
            height = mUserSetTools.get_user_height();
        }

        int weight = 65;
        if (mUserSetTools.get_user_weight() != 0) {
            weight = mUserSetTools.get_user_weight();
        }
        String distance;
        if (BaseApplication.getBleDeviceTools().get_step_algorithm_type() == 1) {
            distance = AppUtils.GetTwoFormat(BleTools.getOneDistance(height, sportStep));
        } else if (BaseApplication.getBleDeviceTools().get_step_algorithm_type() == 2) {
            distance = AppUtils.GetTwoFormat(BleTools.getTwoDistance(height, sportStep));
        } else {
            distance = AppUtils.GetTwoFormat(BleTools.getOldDistance(sportStep));
        }
        return distance;
    }

    /**
     * 检查输入的 float类型，是否是两位小数。
     *
     * @return
     */
    public static boolean checkValueIsTwo(float value) {
        String value_str = String.valueOf(value);
        value_str = value_str.replace(".", ",");
        String[] data = value_str.split(",");
        if (data.length == 2) {
            if (data[1].length() == 2) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

//
//    public static String GetTwoFormat(float value) {
//
//        MyLog.i(TAG, "GetTwoFormat 算法验证 = value = " + value);
//
//        float result = (float) (int) (value * 100) / 100;
//
//        MyLog.i(TAG, "GetTwoFormat 算法验证 = result = " + result);
//
//        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
//        String distanceString = decimalFormat.format(result);//format 返回的是字符串
//        distanceString = distanceString.replace(",", ".");
//        return distanceString;
//
//    }


    public static String GetTwoFormat(float value) {
        MyLog.i(TAG, "GetTwoFormat 算法验证 = value = " + value);
        if (!checkValueIsTwo(value)) {

//            float result = (float) (int) (value * 100) / 100;
//            distanceString = String.valueOf(result);

            float result = (float) (int) (value * 100) / 100;
            DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String distanceString = decimalFormat.format(result);//format 返回的是字符串
            distanceString = distanceString.replace(",", ".");
            MyLog.i(TAG, "GetTwoFormat 算法验证 distanceString = " + distanceString);
            return distanceString;
        } else {
            String distanceString = String.valueOf(value);
            MyLog.i(TAG, "GetTwoFormat 算法验证 distanceString = " + distanceString);
            return distanceString;
        }
    }


    public static void setStatusBar(BaseActivity activity, int color) {
        //        View decorView = getWindow().getDecorView();//获取屏幕的decorView
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);//设置全屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            //设置修改状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
            window.setStatusBarColor(activity.getResources().getColor(color));
        }
    }

    /**
     * 设置状态栏模式
     *
     * @param activity
     * @param isTextDark 文字、图标是否为黑色 （false为默认的白色）
     * @param colorId    状态栏颜色
     * @return
     */
    public static void setStatusBarMode(Activity activity, boolean isTextDark, int colorId) {

        if (!isTextDark) {
            //文字、图标颜色不变，只修改状态栏颜色
            setStatusBarColor(activity, colorId);
        } else {
            //修改状态栏颜色和文字图标颜色
            setStatusBarColor(activity, colorId);
            //4.4以上才可以改文字图标颜色
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
               /* if (OSUtil.isMIUI()) {
                    //小米MIUI系统
                    setMIUIStatusBarTextMode(activity, isTextDark);
                } else if (OSUtil.isFlyme()) {
                    //魅族flyme系统
                    setFlymeStatusBarTextMode(activity, isTextDark);
                } else */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //6.0以上，调用系统方法
                    Window window = activity.getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    //4.4以上6.0以下的其他系统，暂时没有修改状态栏的文字图标颜色的方法，有可以加上
                }
            }
        }
    }

    /**
     * 设置Flyme系统状态栏的文字图标颜色
     *
     * @param activity
     * @param isDark   状态栏文字及图标是否为深色
     * @return
     */
    public static boolean setFlymeStatusBarTextMode(Activity activity, boolean isDark) {
        Window window = activity.getWindow();
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (isDark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     *
     * @param activity
     * @param colorId
     */
    public static void setStatusBarColor(Activity activity, int colorId) {

        //Android6.0（API 23）以上，系统方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setStatusBarColor(activity.getResources().getColor(colorId));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //使用SystemBarTint库使4.4版本状态栏变色，需要先将状态栏设置为透明
            setTranslucentStatus(activity);
            //设置状态栏颜色
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(colorId);
        }
    }

    /**
     * 设置状态栏为透明
     *
     * @param activity
     */
    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 设置MIUI系统状态栏的文字图标颜色（MIUIV6以上）
     *
     * @param activity
     * @param isDark   状态栏文字及图标是否为深色
     * @return
     */
    public static boolean setMIUIStatusBarTextMode(Activity activity, boolean isDark) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (isDark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    if (isDark) {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View
                                .SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View
                                .SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View
                                .SYSTEM_UI_FLAG_VISIBLE);
                    }
                }
            } catch (Exception e) {

            }
        }
        return result;
    }


    private static String mYear;
    private static String mMonth;
    private static String mDay;
    private static String mWay;

    @SuppressLint("StringFormatMatches")
    public static String StringData(Context context) {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = context.getResources().getString(R.string.sunday1) + "   ";
        } else if ("2".equals(mWay)) {
            mWay = context.getResources().getString(R.string.monday1) + "   ";
        } else if ("3".equals(mWay)) {
            mWay = context.getResources().getString(R.string.tuesday1) + "   ";
        } else if ("4".equals(mWay)) {
            mWay = context.getResources().getString(R.string.wednesday1) + "   ";
        } else if ("5".equals(mWay)) {
            mWay = context.getResources().getString(R.string.thursday1) + "   ";
        } else if ("6".equals(mWay)) {
            mWay = context.getResources().getString(R.string.friday1) + "   ";
        } else if ("7".equals(mWay)) {
            mWay = context.getResources().getString(R.string.saturday1) + "   ";
        }
        String date = mMonth + "-" + mDay + "    ";
        return String.format(context.getResources().getString(R.string.data_fragment_title), date, mWay);
    }

    public static void initEditTextFocusChange(EditText editText, View view, String hasFocusColor,  String NoFocusColor){
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                view.setBackgroundColor(Color.parseColor(hasFocusColor));
            } else {
                view.setBackgroundColor(Color.parseColor(NoFocusColor));
            }
        });
    }
}
