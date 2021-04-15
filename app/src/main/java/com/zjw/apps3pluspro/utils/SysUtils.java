package com.zjw.apps3pluspro.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

import static android.content.Context.AUDIO_SERVICE;

public class SysUtils {

    private static final String KUGOU_MUSIC = "com.kugou.android";
    private static final String TAG = SysUtils.class.getSimpleName();

    /**
     * 音乐控制指令
     *
     * @param context
     * @param keyCode
     */
    public static void controlMusic(Context context, int keyCode) {
        long eventTime = SystemClock.uptimeMillis();
        KeyEvent key = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0);
        dispatchMediaKeyToAudioService(context, key);
        dispatchMediaKeyToAudioService(context, KeyEvent.changeAction(key, KeyEvent.ACTION_UP));
    }

    private static void dispatchMediaKeyToAudioService(Context context, KeyEvent event) {
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        if (audioManager != null) {
            try {
                audioManager.dispatchMediaKeyEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 音量控制指令
     *
     * @param context
     * @param keyCode
     */
    public static void controlVolume(Context context, int keyCode) {
        AudioManager audio = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        if (audio == null) {
            return;
        }
        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, keyCode, AudioManager.FLAG_PLAY_SOUND);
    }


    public static String getKugouMusicName(StatusBarNotification sbn) {
        String result_name = "";
        if (sbn.getPackageName().equals(KUGOU_MUSIC) && sbn.getNotification().tickerText != null) {
            String music_name = sbn.getNotification().tickerText.toString();
            if (!music_name.equals("") && music_name.contains(" - ")) {
                String[] music_data = music_name.split(" - ");
                if (music_data.length >= 2) {
                    result_name = music_data[music_data.length - 1];
                }
            }
        }
        return result_name;
    }


    public static void logContentD(String tag, String content) {
//		Log.d(tag, content);
//
//        if(CommonAttributes.SAVE_LOG == 1) {
//        	SysUtils.writeTxtToFile("debug:" + tag + ":" + content, CommonAttributes.P_LOG_PATH, CommonAttributes.P_LOG_BLE_FILENAME);
//        }
    }

    public static void logContentI(String tag, String content) {
        Log.i(tag, content);
        if (Constants.SAVE_LOG) {
            SysUtils.writeTxtToFile("【info】" + tag + ":" + content, Constants.P_LOG_PATH, Constants.P_LOG_BLE_FILENAME);
        }
    }

    public static void logContentW(String tag, String content) {
        Log.w(tag, content);
        if (Constants.SAVE_LOG) {
            SysUtils.writeTxtToFile("【warning】" + tag + ":" + content, Constants.P_LOG_PATH, Constants.P_LOG_BLE_FILENAME);
        }
    }

    public static void logContentE(String tag, String content) {
        Log.e(tag, content);
        if (Constants.SAVE_LOG) {
            SysUtils.writeTxtToFile("【error】" + tag + ":" + content, Constants.P_LOG_PATH, Constants.P_LOG_BLE_FILENAME);
        }
    }

    public static void logUserBehaviorI(String tag, String content, String mac) {
        Log.i(tag, content);
        if (Constants.SAVE_LOG) {
            SysUtils.writeTxtToFile("【info】" + tag + ":" + content, Constants.P_HABIT_PATH, mac.replace(":", "") + ".log");
        }
    }

    public static void logAmapGpsI(String tag, String content) {
        Log.i(tag, content);
        if (Constants.SAVE_LOG) {
            SysUtils.writeTxtToFile("【info】" + tag + ":" + content, Constants.P_AMAP_GPS_PATH, Constants.P_LOG_GPS_FILENAME);
        }
    }

    public static void logAmapGpsE(String tag, String content) {
        Log.i(tag, content);
        if (Constants.SAVE_LOG) {
            SysUtils.writeTxtToFile("【error】" + tag + ":" + content, Constants.P_AMAP_GPS_PATH, Constants.P_LOG_GPS_FILENAME);
        }
    }

    public static void logGooglemapGpsI(String tag, String content) {
        Log.i(tag, content);
        if (Constants.SAVE_LOG) {
            SysUtils.writeTxtToFile("【info】" + tag + ":" + content, Constants.P_GOOGLEMAP_GPS_PATH, Constants.P_LOG_GPS_FILENAME);
        }
    }

    public static void logErrorDataI(String tag, String content) {
        Log.i(tag, content);
        if (Constants.SAVE_LOG) {
            SysUtils.writeTxtToFile("【info】" + tag + ":" + content, Constants.ERROR_DATA_IMG, Constants.P_LOG_ERROR_DATA_FILENAME);
        }
    }

    public static void logDeviceContentI(String tag, String content) {
        Log.i(tag, content);
        if (Constants.SAVE_LOG) {
            SysUtils.writeTxtToFile("【info】" + tag + ":" + content, Constants.P_LOG_PATH, Constants.P_DEVICE_LOG_BLE_FILENAME);
        }
    }

    public static void logAppRunning(String tag, String content) {
        if (Constants.SAVE_APP_RUNNING_LOG) {
            try {
                String strFilePath = Constants.P_LOG_PATH + Constants.P_LOG_APP_RUNNING;
                makeFilePath(Constants.P_LOG_PATH, Constants.P_LOG_APP_RUNNING);

                File file = new File(strFilePath);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                if(file.length() > 5 * 1024 * 1000L){
                    file.delete();
                    Log.i("xxxxx", "删除文件");
                }
                SysUtils.writeTxtToFile(tag + ":" + content, Constants.P_LOG_PATH, Constants.P_LOG_APP_RUNNING);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 将字符串写入到文本文件中
    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String now = sdf.format(new Date());
        String strContent = now + ":" + strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    // 生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                boolean isSuccess = file.mkdirs();
                Log.i("makeRootDirectory", "isSuccess = " + isSuccess);
            } else if (!file.isDirectory()) {
                boolean isde = file.delete();
                boolean isSuccess = file.mkdirs();
                Log.i("makeRootDirectory ", "isSuccess = " + isSuccess);
            }
        } catch (Exception e) {
            Log.i("makeRootDirectory:", e + "");
        }
    }

    public static void toSysNotificationSetting(Activity activity) {
        ApplicationInfo appInfo = activity.getApplicationInfo();
        String pkg = activity.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                // 这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, pkg);
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, uid);
                //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
                intent.putExtra("app_package", pkg);
                intent.putExtra("app_uid", uid);
                activity.startActivityForResult(intent, 1);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + activity.getApplicationContext().getPackageName()));
                activity.startActivityForResult(intent, 1);
            } else {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                activity.startActivityForResult(intent, 1);
            }
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            activity.startActivityForResult(intent, 1);
        }
    }

    public static boolean isNotificationEnabled26(Context context) {
        try {
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            Method sServiceField = notificationManager.getClass().getDeclaredMethod("getService");
            sServiceField.setAccessible(true);
            Object sService = sServiceField.invoke(notificationManager);

            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;

            Method method = sService.getClass().getDeclaredMethod("areNotificationsEnabledForPackage"
                    , String.class, Integer.TYPE);
            method.setAccessible(true);
            return (boolean) method.invoke(sService, pkg, uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    /**
     * 根据IP地址获取MAC地址
     *
     * @return
     */
    public static String getLocalMacAddressFromIp() {
        String strMacAddr = null;
        try {
            //获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
        }
        return strMacAddr;
    }

    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }
                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }

    public static String getDeviceName() {
        String deviceName = "unknown";
//        try {
//            Class<?> cls = Class.forName("android.os.SystemProperties");
//            Object object = (Object) cls.newInstance();
//            Method getName = cls.getDeclaredMethod("get", String.class);
//            deviceName = (String) getName.invoke(object, "persist.sys.device_name");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return deviceName;
    }


    public static String getNetWorkType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getTypeName();
            }
        }
        return "";
    }

    /**
     * 获取设备拨号运营商
     *
     * @return ["中国电信CTCC":3]["中国联通CUCC:2]["中国移动CMCC":1]["other":0]["无sim卡":-1]
     */
    public static int getSubscriptionOperatorType(Context context) {
        int opeType = -1;
        // No sim
        if (!hasSim(context)) {
            return opeType;
        }

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm.getNetworkOperator();
        // 中国联通
        if ("46001".equals(operator) || "46006".equals(operator) || "46009".equals(operator)) {
            opeType = Integer.parseInt(operator);
            // 中国移动
        } else if ("46000".equals(operator) || "46002".equals(operator) || "46004".equals(operator) || "46007".equals(operator)) {
            opeType = Integer.parseInt(operator);
            // 中国电信
        } else if ("46003".equals(operator) || "46005".equals(operator) || "46011".equals(operator)) {
            opeType = Integer.parseInt(operator);
        } else {
            opeType = 0;
        }
        return opeType;
    }

    /**
     * 检查手机是否有sim卡
     */
    private static boolean hasSim(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm.getSimOperator();
        if (TextUtils.isEmpty(operator)) {
            return false;
        }
        return true;
    }

    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    // 将字符串写入到文本文件中
    public static void writeTxtToFile2(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    public static Intent getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", getPackageName(context), null));
        return localIntent;
    }

    public static synchronized String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getLanguage(Context context) {
        String result = "zh-hans";
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("en")) {
            result = "en";
        }
        //中文
        else if (language.endsWith("zh")) {
            //中文简体
            if (AppUtils.isZh(context)) {
                result = "zh-hans";
            }
            //中文繁体
            else {
                result = "zh-hant";
            }
        }
        //日语
        else if (language.endsWith("ja")) {
            result = "ja";
        }
        //法语
        else if (language.endsWith("fr")) {
            result = "fr";
        }
        //德语
        else if (language.endsWith("de")) {
            result = "de";
        }
        //意大利语
        else if (language.endsWith("it")) {
            result = "it";
        }
        //西班牙语
        else if (language.endsWith("es")) {
            result = "es";
        }
        //俄罗斯语
        else if (language.endsWith("ru")) {
            result = "ru";
        }
        //葡萄牙语
        else if (language.endsWith("pt")) {
            result = "pt";
        }
        //马来语
        else if (language.endsWith("ms")) {
            result = "ms";
        }
        //韩语
        else if (language.endsWith("ko")) {
            result = "ko";
        }
        //波兰语
        else if (language.endsWith("pl")) {
            result = "pl";
        }
        //泰语
        else if (language.endsWith("th")) {
            result = "th";
        }
        //罗马尼亚语
        else if (language.endsWith("ro")) {
            result = "ro";
        }
        //保加利亚语
        else if (language.endsWith("bg")) {
            result = "bg";
        }
        //匈牙利语
        else if (language.endsWith("hu")) {
            result = "hu";
        }
        //土耳其语
        else if (language.endsWith("tr")) {
            result = "tr";
        }
        //捷克语
        else if (language.endsWith("cs")) {
            result = "cs";
        }
        //斯洛伐克语
        else if (language.endsWith("sk")) {
            result = "sk";
        }
        //丹麦语
        else if (language.endsWith("da")) {
            result = "da";
        }
        //挪威语
        else if (language.endsWith("nb")) {
            result = "nb";
        }
        //瑞典语
        else if (language.endsWith("sv")) {
            result = "sv";
        }
        //菲律宾语
        else if (language.endsWith("fil")) {
            result = "fil";
        }
        //乌克兰语
        else if (language.endsWith("uk")) {
            result = "uk";
        }
        //越南语
        else if (language.endsWith("vi")) {
            result = "vi";
        } //荷兰语
        else if (language.endsWith("nl")) {
            result = "nl";
        }//克罗地亚
        else if (language.endsWith("hr")) {
            result = "hr";
        }

        return result;
    }

    public static String getNewLanguage(Context context) {
        String result = "en";
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        //法语
        if (language.endsWith("fr")) {
            result = "fr";
        }
        //西班牙语
        else if (language.endsWith("es")) {
            result = "es";
        }
        return result;
    }

    public static boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps /*|| network*/) {
            return true;
        }
        return false;
    }

    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    private static String hexString = "0123456789abcdef";

    public static String decode(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
        //将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
        return new String(baos.toByteArray());
    }

    public static String printHexString(byte[] b) {
        if (b == null) {
            return "";
        }
        String hexString = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString += hex.toUpperCase() + " ";
        }

        return hexString;
    }

    public static boolean isShow00Pace(int minute, int second) {
        int totalSecond = minute * 60 + second;
        return totalSecond > (50 * 60 + 58) || totalSecond <= 0;
    }

    public static boolean isShow00Pace(int totalSecond) {
        return totalSecond > (50 * 60 + 58) || totalSecond <= 0;
    }

    public static boolean compressBitmap(String srcPath, int ImageSize, String savePath) {
        int subtract;
        Log.i(TAG, "图片处理开始..");
        Bitmap bitmap = compressByResolution(srcPath, 1024, 720); //分辨率压缩
        if (bitmap == null) {
            Log.i(TAG, "bitmap 为空");
            return false;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        Log.i(TAG, "图片分辨率压缩后：" + baos.toByteArray().length / 1024 + "KB");

        while (baos.toByteArray().length > ImageSize * 1024) {  //循环判断如果压缩后图片是否大于ImageSize kb,大于继续压缩
            subtract = setSubstractSize(baos.toByteArray().length / 1024);
            baos.reset();//重置baos即清空baos
            options -= subtract;//每次都减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            Log.i(TAG, "图片压缩后：" + baos.toByteArray().length / 1024 + "KB");
        }
        Log.i(TAG, "图片处理完成!" + baos.toByteArray().length / 1024 + "KB");
        try {
            FileOutputStream fos = new FileOutputStream(new File(savePath));//将压缩后的图片保存的本地上指定路径中
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bitmap != null) {
            bitmap.recycle();
        }
        return true;  //压缩成功返回ture
    }

    private static int setSubstractSize(int imageMB) {
        if (imageMB > 1000) {
            return 60;
        } else if (imageMB > 750) {
            return 40;
        } else if (imageMB > 500) {
            return 20;
        } else {
            return 10;
        }
    }

    private static Bitmap compressByResolution(String imgPath, int w, int h) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, opts);

        int width = opts.outWidth;
        int height = opts.outHeight;
        int widthScale = width / w;
        int heightScale = height / h;

        int scale;
        if (widthScale < heightScale) {  //保留压缩比例小的
            scale = widthScale;
        } else {
            scale = heightScale;
        }

        if (scale < 1) {
            scale = 1;
        }
        Log.i(TAG,"图片分辨率压缩比例：" + scale);

        opts.inSampleSize = scale;

        opts.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, opts);
        return bitmap;
    }

}
