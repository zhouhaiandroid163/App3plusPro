package com.zjw.apps3pluspro.utils;import android.os.Environment;import java.io.File;/** * 类说明-常量 * * @author 55498 */public class Constants {    //================发布版本操作================    //===========内侧============    public static final boolean isBast = true;//是否是内侧版(true=内侧版本,false=正式版本)    public static final String BastVersion = "Beta1.6";//内侧版本号(正式版本置为Beta1.0)    public static boolean NO_PROXY = false; // 开启反代理(false=内测版本，关闭反代理，ture=正式版本，打开反代理)    public static final boolean SAVE_LOG = true; // true 写入指定文件日志    public static final boolean SAVE_DEVICE_LOG = false; // true 写入设备错误log    public static final boolean CRASH_LOG = true; // crash true 写入指定文件日志    //===========发布============//    public static final boolean isBast = false;//是否是内侧版(true=内侧版本,false=正式版本)//    public static final String BastVersion = "Beta1.0";//内侧版本号(正式版本置为Beta1.0)//    public static boolean NO_PROXY = true; // 开启反代理(false=内测版本，关闭反代理，ture=正式版本，打开反代理)//    public static final boolean SAVE_LOG = false; // true 写入指定文件日志//    public static final boolean SAVE_DEVICE_LOG = false; // true 写入设备错误log//    public static final boolean CRASH_LOG = true; // crash true 写入指定文件日志    //================发布版本操作================    public static long CONNECT_TIMEOUT = 10 * 1000; // 连接超时    public static long SCAN_PERIOD_SEARCH_DEVICE_ACTIVITY = 30 * 1000; // 扫描时间    public static long SCAN_PERIOD_BLE_SERVICE = 3 * 1000; // 扫描时间    public static long CONNECT_TIMES = 3; // 连接次数    public static int SYNC_TIMEOUT = 60 * 1000; // 同步超时    public static final int detectBleCmdPeriod = 80;//蓝牙发送间隔    public static final int detectBleCmdReissueCount = 5;//蓝牙补发间隔    public static final int detectBleCmdReissuePeriod = 100;//蓝牙补发间隔    public static final int serviceHandTime = 1000;    // 文件目录    public static final String APP_NAME = "com.zjw.apps3pluspro";    public static final String HOME_DIR = Environment.getExternalStorageDirectory().getPath() + File.separator + APP_NAME;    public static final String P_LOG_PATH = HOME_DIR + File.separator + "logs" + File.separator;    public static final String CRASH_DIR = HOME_DIR + File.separator + "crash" + File.separator;    public static final String APK_DIR = HOME_DIR + File.separator + "apk" + File.separator;    public static final String UPDATE_DEVICE_FILE = HOME_DIR + File.separator + "update_device" + File.separator;    public static final String DOWN_MUSIC_FILE = HOME_DIR + File.separator + "music" + File.separator;    public static final String DOWN_THEME_FILE = HOME_DIR + File.separator + "theme" + File.separator;    public static final String DEVICE_ERROR_LOG_FILE = HOME_DIR + File.separator + "device_log" + File.separator;    public static final String MAP_FILE = HOME_DIR + File.separator + "map" + File.separator;    public static final String HEAD_IMG = HOME_DIR + File.separator + "headImg" + File.separator;    public static final String P_HABIT_PATH = HOME_DIR + File.separator + "habit" + File.separator;    public static final String P_AMAP_GPS_PATH = HOME_DIR + File.separator + "Amap" + File.separator;    public static final String P_GOOGLEMAP_GPS_PATH = HOME_DIR + File.separator + "Googlemap" + File.separator;    public static final String CUSTOM_IMG = HOME_DIR + File.separator + "customImg" + File.separator;    public static final String ERROR_DATA_IMG = HOME_DIR + File.separator + "errorData" + File.separator;    public static final String P_GPS_LOG = HOME_DIR + File.separator + "gps" + File.separator;    public static final String THEME_DIR = "";    public static final String LOC_DIR = HOME_DIR + File.separator + "Loc" + File.separator;    public static final String P_LOG_KML = HOME_DIR + File.separator + "kml" + File.separator;    public static final String P_PICTURE = HOME_DIR + File.separator + "picture" + File.separator;    //Assets目录    public static final String ASSETS_CUSTOM_DIAL_DIR = "custom_dial_data" + File.separator;    // 文件名字    public static final String P_LOG_BLE_FILENAME = "ble.log";    public static final String P_LOG_GPS_FILENAME = "gps.log";    public static final String P_LOG_ERROR_DATA_FILENAME = "error.log";    public static final String P_DEVICE_LOG_BLE_FILENAME = "device.log";    public static final String P_LOG_LOC_FILENAME = "loc.log";    public static final boolean SAVE_APP_RUNNING_LOG = true;    public static final String P_LOG_APP_RUNNING = "appRunning.log";    public static final String IMAGE_FILE_LOCATION = HOME_DIR + File.separator + "headImg" + File.separator;    public static final String IMAGE_FILE_LOCATION_TEMP = "temp.jpg";    public static int TargetStepMax = 30000;    public static int TargetStepMin = 3000;    public static int TargetStepDefult = 8000;    public static int TargetStepCount = (TargetStepMax - TargetStepMin) / 1000 + 1;    public static int TargetSleepMax = 600;    public static int TargetSleepMin = 240;    public static int TargetSleepDefult = 480;    //    public static int TargetSleepCount = (TargetSleepMax - TargetSleepMin) / 60 + 1;    public static int TargetSleepCount = (TargetSleepMax - TargetSleepMin) / 30 + 1;    //过滤蓝牙名称    public static final String REDMI_BAND = "Redmi Band";    public static final String ZI_LONG = "ZiLong Band";    public static final String REDMI_SMART_BAND = "Redmi Smart Band";    public static final String MI_SMART_BAND = "Mi Smart Band";    public static final String zzLong_Band = "zzLong Band";    public static final String redmi1 = "redmi";    public static final String Redmi2 = "Redmi";    public static final String Redmi3 = "Mi Watch Lite";//    public static final String redmi1 = "xxxxxredmi";//    public static final String Redmi2 = "xxxxRedmi";    // 心电数据    public static int DrawEcgTime = 10;    public static float DrawEcgZip = 0.0085f * 3 * 1.5f;    public static float DrawEcgHeight = 900f;    public static int DrawEcgWidth = 500;    public static float DrawPpgZip = 0.55f * 7f;    public static float DrawPpgHeight = 900f;    public static int DrawPpgWidth = 500;    //拍照回调标志位    public static final int TakingTag = 2;    public static final int PhotoTag = 1;    public static final int TailoringResult = 5;    //当解析年份小于2017或者月小于等于0或者日小于等于0的时候，强制变成这个默认日期    public final static String DEVICE_DEFULT_DATE = "2017-08-08";    public final static String DEVICE_DEFULT_TIME = "2017-08-08 12:00:00";    public static final double GPS_OFFSET_DISTANCE_MIN = 5.0;    public static final double GPS_OFFSET_DISTANCE_MAX = 300.0;    //finish activity time Delay    public static final int FINISH_ACTIVITY_DELAY_TIME = 2 * 1000;}