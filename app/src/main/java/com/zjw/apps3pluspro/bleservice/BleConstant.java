package com.zjw.apps3pluspro.bleservice;

import java.util.UUID;


/**
 * 常量
 */
public class BleConstant {

    // 蓝牙KEY
    static final int Key_Motion = 0x02;//运动
    static final int Key_Sleep = 0x03;//睡眠
    static final int Key_Complete = 0x04;//同步完成
    static final int Key_PoHeart = 0x05;//整点心率
    static final int Key_Photo = 0x06;//摇一摇拍照指令
    static final int Key_FindPhone = 0x07;//找手机
    static final int Key_DeviceInfo = 0x08;//设备信息
    static final int Key_DeviceMac = 0x09;//设备MAC地址
    static final int Key_MeasuringHeart = 0x0b;//测量心率
    static final int Key_HangPhone = 0x0c;//来电拒接
    static final int Key_MoHeart = 0x0d;//连续心率
    static final int Key_ScreensaverInfo = 0x0e;//屏幕信息相关
    static final int Key_ScreensaverIsSuccess = 0x0f;//设置屏保成功
    static final int Key_OffLineBpInfo = 0x10;//离线血压
    static final int Key_IneffectiveSleep = 0x11;//无效睡眠
    static final int Key_DeiceMotion = 0x18;//多运动模式
    //音乐控制相关
    static final int Key_MusicControlCmd = 0x1e;//指令
    static final int Key_MusicControlGet = 0x00;//获取信息
    static final int Key_MusicControlPlay = 0x01;//播放
    static final int Key_MusicControlSuspend = 0x02;//暂停
    static final int Key_MusicControlLastOne = 0x03;//上一首
    static final int Key_MusicControlNextOne = 0x04;//下一首
    static final int Key_MusicControlVolumeTop = 0x05;//增加音量
    static final int Key_MusicControlVolumeDown = 0x06;//减少音量
    static final int Key_ThemeTransmissionInfo = 0x1f;//主题传输信息
    static final int Key_MtuInfo = 0x21;//MTU信息
    static final int Key_CallDeviceInfo = 0x24;//经典蓝牙设备信息

    static final int Key_ContinuitySpo2 = 0x25;//连续血氧
    static final int Key_ContinuityTemp = 0x26;//连续体温
    static final int Key_OffMeasureSpo2 = 0x27;//离线血氧
    static final int Key_OffMeasureTemp = 0x28;//离线体温
    static final int Key_UserBehavior = 0x29;//用户行为
    static final int Key_DeviceBasicInfo = 0x31;//设备信息
    static final int Key_DeviceSendUnbind = 0x33;//设备发出解绑命令
    static final int Key_DeviceBindInfo = 0x34;//绑定回复
    static final int Key_DeviceToAppSport = 0x35;//辅助运动定位
    static final int KEY_DEVICE_ANSWER = 0xac;
    //推送Key
    public static final int NotifaceMsgPhone = 0x01;
    public static final int NotifaceMsgQq = 0x02;
    public static final int NotifaceMsgWx = 0x03;
    public static final int NotifaceMsgMsg = 0x04;
    public static final int NotifaceMsgSkype = 0x05;
    public static final int NotifaceMsgWhatsapp = 0x06;
    public static final int NotifaceMsgFacebook = 0x07;
    public static final int NotifaceMsgLink = 0x08;
    public static final int NotifaceMsgTwitter = 0x09;
    public static final int NotifaceMsgViber = 0x0a;
    public static final int NotifaceMsgLine = 0x0b;

    public static final int NotifaceGmail = 0x0e;
    public static final int NotifaceOutLook = 0x0f;
    public static final int NotifaceInstagram = 0x10;
    public static final int NotifaceSnapchat = 0x11;
    public static final int NotifaceIosMail = 0x12;

    public static final int NotifaceZalo = 0x15;
    public static final int NotifaceTelegram = 0x16;
    public static final int NotifaceYouTube = 0x17;
    public static final int NotifaceKakaoTalk = 0x18;
    public static final int NotifaceVK = 0x19;
    public static final int NotifaceOK = 0x1a;
    public static final int NotifaceICQ = 0x1b;


    //使能ID
    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    //基本-服务ID
    public static final UUID UUID_BASE_SERVICE = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
    //基本-写入数据ID
    public static final UUID UUID_BASE_WRITE = UUID.fromString("00000002-0000-1000-8000-00805f9b34fb");
    //基本-读取数据ID
    public static final UUID UUID_BASE_READ = UUID.fromString("00000003-0000-1000-8000-00805f9b34fb");

    //ECG=服务ID
    public final static UUID UUID_ECG_SERVICE = UUID.fromString("00003e01-0000-1000-8000-00805f9b34fb");
    // ECG=读取数据ID
    public static final UUID UUID_ECG_READ = UUID.fromString("00003e03-0000-1000-8000-00805f9b34fb");

    // PPG=服务ID
    public final static UUID UUID_PPG_SERVICE = UUID.fromString("00002c01-0000-1000-8000-00805f9b34fb");
    // PPG=读取数据ID
    public static final UUID UUID_PPG_READ = UUID.fromString("00002c03-0000-1000-8000-00805f9b34fb");

    //图片-服务ID
    public static final UUID UUID_IMAGER_SERVICE = UUID.fromString("00003d01-0000-1000-8000-00805f9b34fb");
    //图片-写入数据ID
    public static final UUID UUID_IMAGER_WRITE = UUID.fromString("00003d02-0000-1000-8000-00805f9b34fb");
    //图片-读取数据ID
//    public static final UUID UUID_IMAGER_READ = UUID.fromString("00003d03-0000-1000-8000-00805f9b34fb");

    //主题-服务ID
    public static final UUID UUID_THEME_SERVICE = UUID.fromString("00001601-0000-1000-8000-00805f9b34fb");
    //主题-写入数据ID
    public static final UUID UUID_THEME_WRITE = UUID.fromString("00001602-0000-1000-8000-00805f9b34fb");
    //主题-读取数据ID
    public static final UUID UUID_THEME_READ = UUID.fromString("00001603-0000-1000-8000-00805f9b34fb");

    // LOG=服务ID
    public final static UUID UUID_LOG_SERVICE = UUID.fromString("00001701-0000-1000-8000-00805f9b34fb");
    // LOGS=读取数据ID
    public static final UUID UUID_LOG_READ = UUID.fromString("00001703-0000-1000-8000-00805f9b34fb");

//    //主题-服务ID
//    public static final UUID UUID_THEME_SERVICE = UUID
//            .fromString("1314F000-1000-9000-7000-301291E21220");
//    //主题-写入数据ID
//    public static final UUID UUID_THEME_WRITE = UUID
//            .fromString("1314F003-1000-9000-7000-301291E21220");
//    //主题-读取数据ID
//    public static final UUID UUID_THEME_READ = UUID
//            .fromString("1314F006-1000-9000-7000-301291E21220");

    // protobuf基本-服务ID
    public final static UUID UUID_PROTOBUF_SERVICE = UUID.fromString("16186f00-0000-1000-8000-00807f9b34fb");
    public static final UUID CHAR_PROTOBUF_UUID_01 = UUID.fromString("16186f01-0000-1000-8000-00807f9b34fb");
    public static final UUID CHAR_PROTOBUF_UUID_02 = UUID.fromString("16186f02-0000-1000-8000-00807f9b34fb");
    public static final UUID CHAR_PROTOBUF_UUID_03 = UUID.fromString("16186f03-0000-1000-8000-00807f9b34fb");
    public static final UUID CHAR_PROTOBUF_UUID_04 = UUID.fromString("16186f04-0000-1000-8000-00807f9b34fb");
    // 大包数据
    public static final UUID UUID_BIG_SERVICE = UUID.fromString("00002001-0000-1000-8000-00805f9b34fb");
    public static final UUID CHAR_BIG_UUID_02 = UUID.fromString("00002002-0000-1000-8000-00805f9b34fb"); // 写
    public static final UUID CHAR_BIG_UUID_03 = UUID.fromString("00002003-0000-1000-8000-00805f9b34fb"); // 读

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_CONNECTED_TIMEOUT = 3;
    public static final int STATE_DISCOVER_SERVICES = 4;
    public static final int STATE_BIND_SUCCESS = 5;
    public static final int STATE_BIND_ERROR = 6;

    public static final String E07 = "E07";
    public static final String PLUS_HR = "3PLUS HR+";
    public static final String E08 = "E08";
    public static final String PLUS_Vibe = "3PLUS Vibe+";

    public static final UUID SCAN_RECORD = UUID.fromString("0000fe68-0000-1000-8000-00805f9b34fb");


}
