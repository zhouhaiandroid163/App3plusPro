package com.zjw.apps3pluspro.bleservice;

import android.graphics.Color;
import android.util.Log;

import com.zjw.apps3pluspro.bleservice.anaylsis.FitnessTools;
import com.zjw.apps3pluspro.bleservice.anaylsis.SystemTools;
import com.zjw.apps3pluspro.bleservice.anaylsis.WatchFaceTools;
import com.zjw.apps3pluspro.module.device.entity.AlarmClockModel;
import com.zjw.apps3pluspro.module.device.entity.DurgModel;
import com.zjw.apps3pluspro.module.device.entity.MettingModel;
import com.zjw.apps3pluspro.module.device.entity.MusicInfo;
import com.zjw.apps3pluspro.module.device.entity.SitModel;
import com.zjw.apps3pluspro.module.device.entity.WaterModel;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.BmpUtils;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.GpsSportManager;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.utils.MyTime;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class BtSerializeation {

    private static final String TAG = BtSerializeation.class.getSimpleName();

    public static final byte CMD_01 = 0x01;
    public static final byte CMD_02 = 0x02;

    // 设置指令key
    public static final byte KEY_SYNC_TIME = (byte) 0x01;//同步时间
    private static final byte KEY_ALARM_CLOCK = (byte) 0x02;//闹钟
    private static final byte KEY_STEP_TARGET = (byte) (byte) 0x03;//步数目标
    private static final byte KEY_USER_PROFILE = (byte) 0x04;//用户信息
    //    private static final int SET_KEY_XXX05 = (byte) 0x05;//暂不使用
    public static final byte KEY_SET_REBOOT = (byte) 0x06;//恢复出厂设置
    private static final byte KEY_SIT_NOTIFICATION = (byte) 0x07;//久坐
    private static final byte KEY_SET_COMPANY = (byte) 0x08;//单位
    private static final byte KEY_TIME_FORMAT = (byte) 0x09;//时间格式
    private static final byte KEY_SET_BRIGHT_SCREEN = (byte) 0x0a;//抬腕亮屏
    //    private static final byte KEY_CUT_SCREEN = (byte) 0x0b;//作废
    private static final byte KEY_INPUT_DFU = (byte) 0x0c;//进入DFU模式
    private static final byte KEY_POINT_MEASUREMENT = (byte) 0x0d;//整点心率测量开关
    private static final byte KEY_MEDICAL_NOTIFICATION = (byte) 0x15;//吃药
    private static final byte KEY_DRINKING_NOTIFICATION = (byte) 0x16;//喝水
    private static final byte KEY_MEETING_NOTIFICATION = (byte) 0x17;//会议
    public static final byte KEY_FIND_DEVICE_INSTRA = (byte) 0x18;//找设备
    private static final byte KEY_OPEN_ECG = (byte) 0x19;
    private static final byte KEY_CLOSE_ECG = (byte) 0x19;
    private static final byte KEY_RESULT_HEART = (byte) 0x1a;
    private static final byte KEY_SEND_NEW_INIT_DEVICE = (byte) 0x1b;//发送手机类型/发送设备类型/中英文
    private static final byte KEY_GET_DEVICE_VERSION = (byte) 0x1c;
    private static final byte KEY_SET_INIT_SET = (byte) 0x1e;
    private static final byte KEY_SET_USER_PAR = (byte) 0x1f;

    private static final byte KEY_DO_NOT_DISTRUB = (byte) 0x20;
    private static final byte KEY_GET_MAC_ADDRESS = (byte) 0x21;
    private static final byte KEY_PERSIST_MEASUREMENT = (byte) 0x29;

    private static final byte KEY_SEND_DEVICE_PHOTO = (byte) 0x30;
    private static final byte KEY_SEND_CLOSE_CALL = (byte) 0x31;
    private static final byte KEY_SCREEN_SAVER_SET = (byte) 0x32;
    private static final byte KEY_DEVICE_SCREEN_SAVER_INFO = (byte) 0x33;//获取屏保参数设置
    private static final byte KEY_DEVICE_SKIN_COLOUR = (byte) 0x34;
    private static final byte KEY_DEVICE_THEME = (byte) 0x35;
    private static final byte KEY_SCREEN_BRIGHTNESS = (byte) 0x36;//屏幕亮度等级
    private static final byte KEY_BRIGHTNESS_TIME = (byte) 0x37;//亮屏时间
    public static final byte KEY_NOT_DISTURB = (byte) 0x38;//勿扰时间
    private static final byte KEY_SET_WATHER = (byte) 0x3a;
    private static final byte KEY_SET_CYCLE = (byte) 0x3d;

    private static final byte KEY_SET_TEMPERATURE_TYPE = (byte) 0x49;//温度单位

    public static final byte KEY_MUSIC_CONTROL_INFO_NEW = (byte) 0x55;//音乐控制
    private static final byte KEY_DEVICE_THEME_INFO = (byte) 0x56;
    private static final byte KEY_END_DEVICE_SLEEP_TAG = (byte) 0x57;
    private static final byte KEY_CLOSE_DEVICE_PHOTO = (byte) 0x58;
    private static final byte KEY_GET_DEVICE_MTU = (byte) 0x59;
    private static final byte KEY_GET_CALL_DEVICE_INFO = (byte) 0x5c;
    private static final byte KEY_CLEAN_MAIL_LIST = (byte) 0x5d;
    private static final byte KEY_SET_CONTINUITY_SPO2 = (byte) 0x5e;//连续血氧
    private static final byte KEY_SET_CONTINUITY_TEMP = (byte) 0x5f;//连续体温
    public static final int KEY_SET_TIMEZONE = 0x69;//时区
    private static final byte KEY_SET_DEVICE_ERROR_LOG = (byte) 0xee;
    private static final byte KEY_GET_DEVICE_ERROR_LOG = (byte) 0xef;

    public static final byte KEY_AGPS = (byte) 0x6b;
    public static final byte KEY_DEVICE_TO_APP_SPORT = (byte) 0x6d;
    public static final byte KEY_SET_GOAL = (byte) 0x6f;

    //大数据传输通道。
    private static final byte KEY_SEND_THEME_HEAD = (byte) 0x01;
    private static final byte KEY_SEND_MUSIC_HEAD = (byte) 0x02;
    private static final byte KEY_MAIL_LIST_HEAD = (byte) 0x03;

    public static byte[] getBleData(byte[] dataValue, byte cmdType, int key) {
        int resultCmdLength = 13;
        int dataLength = 0;

        if (dataValue != null) {
            resultCmdLength = dataValue.length + 13;
            dataLength = dataValue.length;
        }

        byte[] resultCmd = new byte[resultCmdLength];
        resultCmd[0] = (byte) 0xAB;  // 固定cmd ，app -> device
        resultCmd[1] = (byte) 0x00;  // 2 bit	1 bit	1 bit	4 bit (version)

        int l1Length = resultCmdLength - 8;               // L1长度
        resultCmd[2] = (byte) ((l1Length >> 8) & 0xff);
        resultCmd[3] = (byte) (l1Length & 0xff);

        resultCmd[4] = (byte) 0x00;  // crc

        resultCmd[5] = (byte) 0x00;  // 保留位
        resultCmd[6] = (byte) 0x00;  // 保留位
        resultCmd[7] = (byte) 0x00;  // 保留位

        // ************ 上面 L1 （8字节）  数据协议， 下面 L2 **********

        resultCmd[8] = cmdType;  // cmd 01 设置命令01 提醒命令02 返回数据命令03
        resultCmd[9] = (byte) 0x00; // 4bit 版本  4 保留、
        resultCmd[10] = (byte) key; // key

        resultCmd[11] = (byte) ((dataLength >> 8) & 0xff);  // 数据长度 高位
        resultCmd[12] = (byte) (dataLength & 0xff);         // 数据长度 低位 （n - 13）

        //*** 13 -> n - 1 ***** L2      数据
        if (dataValue != null) {
            System.arraycopy(dataValue, 0, resultCmd, 13, dataValue.length);
        }
        return resultCmd;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    //////////===================指令封装=====================

    public static int byteArrayToInt(byte[] b) {
        return
                (b[0] & 0xFF) << 8 |
                        (b[1] & 0xFF);
    }

    public static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    /**
     * 同步时间
     *
     * @return
     */
    public static byte[] syncTime() {
        MyLog.i(TAG, "写入 同步时间");
        byte[] time = new byte[17];
        time[0] = (byte) 0xab;
        time[3] = (byte) 9;//L1长度
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date tempDate = new Date(System.currentTimeMillis());
        String dateString = sdf.format(tempDate);
        char[] hexChars = dateString.toCharArray();
        int year = charToByte(hexChars[2]) * 10 + charToByte(hexChars[3]);
        int mon = charToByte(hexChars[4]) * 10 + charToByte(hexChars[5]);
        int day = charToByte(hexChars[6]) * 10 + charToByte(hexChars[7]);
        int hour = charToByte(hexChars[8]) * 10 + charToByte(hexChars[9]);
        int min = charToByte(hexChars[10]) * 10 + charToByte(hexChars[11]);
        int sec = charToByte(hexChars[12]) * 10 + charToByte(hexChars[13]);
        // Log.d(TAG, String.format("set device time:20%2d-%2d-%2d:%2d-%2d-%2d",
        // year, mon, day, hour, min, sec));
        time[8] = 1;
        time[10] = KEY_SYNC_TIME;
        time[12] = 4;
        time[13] = (byte) ((year << 2) | (mon >> 2));
        time[14] = (byte) (((mon & 0x03) << 6) | (day << 1) | (hour >> 4));
        time[15] = (byte) (((hour & 0x0f) << 4) | (min >> 2));
        time[16] = (byte) (((min & 0x03) << 6) | (sec));
        // Log.d(TAG, "sync time:");
        // for (int i = 0; i < 17; i++){
        // Log.d(TAG, String.format("byte[%d] = %2x", i, time[i]));
        // }
        return time;
    }


    /**
     * 设置闹钟
     *
     * @param clockItems
     * @return
     */
    public static byte[] setDeviceAlarm(List<AlarmClockModel> clockItems) {

        MyLog.i(TAG, "设置闹钟 clockItems = " + clockItems.toString());

        int length = 13 + (5 * clockItems.size());
        byte[] alarm = new byte[length];
        alarm[0] = (byte) 0xab;
        // alarm[3] = 15;
        alarm[3] = (byte) (5 + 5 * clockItems.size());
        alarm[8] = 1;
        alarm[10] = KEY_ALARM_CLOCK;
        // alarm[12] = 10; // 5/per alarm * 2
        alarm[12] = (byte) (5 * clockItems.size()); // 5/per alarm * 3

        for (int i = 0; i < clockItems.size(); i++) {
            String timeAlarm = clockItems.get(i).getTimeAlarm();
            int hour = Integer.parseInt(timeAlarm.split(":")[0]);
            int min = Integer.parseInt(timeAlarm.split(":")[1]);
            int id = clockItems.get(i).getId();
            int repeat = clockItems.get(i).getRepeat();


            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            String dateTime = sdfTime.format(new Date(System
                    .currentTimeMillis()));

            int currentHour = Integer.parseInt(dateTime.split(":")[0]);
            int currentMin = Integer.parseInt(dateTime.split(":")[1]);

            MyLog.i(TAG, "闹钟测试 旧闹钟 id ======================== " + id);
            MyLog.i(TAG, "闹钟测试 旧闹钟 repeat = " + repeat);
            MyLog.i(TAG, "闹钟测试 旧闹钟 hour = " + hour);
            MyLog.i(TAG, "闹钟测试 旧闹钟 min = " + min);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateString = sdf
                    .format(new Date(System.currentTimeMillis()));
            if ((hour == currentHour && min < currentMin)
                    || (hour < currentHour)) {
                dateString = sdf.format(new Date(System.currentTimeMillis()
                        + 1000 * 60 * 60 * 24));
            } else {
                dateString = sdf.format(new Date(System.currentTimeMillis()));
            }
            char[] hexChars = dateString.toCharArray();
//            int year = charToByte(hexChars[2]) * 10 + charToByte(hexChars[3]);
            //年份减去1，为了屏蔽当次，不提醒，如果要加上仅一次提醒在修改代码。
            int year = charToByte(hexChars[2]) * 10 + charToByte(hexChars[3]) - 1;

            //等于 1000 0000 = 仅一次 = 加回1
            if (repeat == 128 || repeat == 0) {
                year += 1;
            }


            MyLog.i(TAG, "设置闹钟输出 year = " + year);

            int mon = charToByte(hexChars[4]) * 10 + charToByte(hexChars[5]);
            int day = charToByte(hexChars[6]) * 10 + charToByte(hexChars[7]);

            MyLog.i("闹钟测试 旧闹钟", "year = " + year);
            MyLog.i("闹钟测试 旧闹钟", "mon = " + mon);
            MyLog.i("闹钟测试 旧闹钟", "day = " + day);
            MyLog.i("闹钟测试 旧闹钟", "repeat = " + repeat);

            alarm[13 + i * 5] = (byte) ((year << 2) | (mon >> 2));
            alarm[14 + i * 5] = (byte) (((mon & 0x03) << 6) | (day << 1) | (hour >> 4));
            alarm[15 + i * 5] = (byte) (((hour & 0xf) << 4) | (min >> 2));
            alarm[16 + i * 5] = (byte) (((min & 0x3) << 6) | (id << 3));
            alarm[17 + i * 5] = (byte) repeat;
        }

        MyLog.i("闹钟测试 旧闹钟", BleTools.printHexString(alarm));
        return alarm;

    }


    /**
     * 设置目标步数
     *
     * @param TargetStep
     * @return
     */
    public static byte[] setTargetStep(int TargetStep) {
        byte[] target = new byte[17];//修改
        target[0] = (byte) 0xab;
        target[3] = 9;//L1长度-修改
        target[8] = 1;
        target[10] = KEY_STEP_TARGET;
        target[12] = 4;
        target[13] = (byte) (TargetStep >> 24);
        target[14] = (byte) ((TargetStep & 0x00ff0000) >> 16);
        target[15] = (byte) ((TargetStep & 0x0000ff00) >> 8);
        target[16] = (byte) (TargetStep & 0xff);
        return target;
    }

    /**
     * 设置用户资料
     *
     * @param sex    0:男性,1:女性
     * @param age    年龄
     * @param hight  身高cm
     * @param weight 体重kg
     * @return
     */
    public static byte[] setUserProfile(int sex, int age, int hight, int weight) {

        int isMale = 0;
        if (sex == 1) {
            isMale = 1;
        }

        byte[] userInfo = new byte[17];
        userInfo[0] = (byte) 0xab;
        userInfo[3] = 9;//L1长度
        userInfo[8] = 1;
        userInfo[10] = KEY_USER_PROFILE;
        userInfo[12] = 4;
        userInfo[13] = (byte) ((isMale << 7) | age);
        userInfo[14] = (byte) hight;
        userInfo[15] = (byte) weight;
        userInfo[16] = (byte) 0;

        return userInfo;
    }


    /**
     * 恢复出厂设置
     *
     * @return
     */
    public static byte[] resetFactory() {
        byte[] bind = new byte[13];
        bind[0] = (byte) 0xab;
        bind[3] = 5;//L1长度
        bind[8] = 1;
        bind[9] = (byte) 0x80;//不需要回应
        bind[10] = KEY_SET_REBOOT;
        bind[12] = 0;
        return bind;
    }

    /**
     * 设置手环显示的时间格式
     *
     * @param value 时间格式 0代表12小时制 1代表24小时制
     * @return
     */
    public static byte[] setTimeFormat(int value) {
        byte[] measure = new byte[14];
        measure[0] = (byte) 0xab;
        measure[3] = 6;//L1长度-修改
        measure[8] = 1;
        measure[10] = KEY_TIME_FORMAT;
        measure[12] = 1;
        measure[13] = (byte) value;
        return measure;
    }

    /**
     * 久坐提醒
     *
     * @return
     */
    public static byte[] setSitNotification(SitModel mSitModel) {

        MyLog.i(TAG, "mSitModel = " + mSitModel.toString());

        int startHour = mSitModel.getSitStartHourTime();
        int startMin = mSitModel.getSitStartMinTime();
        int endHour = mSitModel.getSitEndHourTime();
        int endMin = mSitModel.getSitEndMinTime();
        int period = mSitModel.getSitCycleTime();

        //0=1小时，1=2小时，所以减1
        if (period > 0) {
            period -= 1;
        }

        int enable = 0;

        if (mSitModel.isSitEnable()) {
            enable = 1;
        }

        byte[] bind = new byte[17];
        bind[0] = (byte) 0xab;
        bind[3] = 9;//L1长度
        bind[8] = 0x01;
        bind[10] = KEY_SIT_NOTIFICATION;
        bind[12] = 4;
        bind[13] = (byte) ((enable << 7) | ((period + 1) << 4) | (startHour >> 1));
        bind[14] = (byte) (((startHour & 0x01) << 7) | (startMin << 1) | (endHour >> 4));
        bind[15] = (byte) (((endHour & 0x0f) << 4) | (endMin >> 2));
        bind[16] = (byte) ((endMin & 0x03) << 6);

        return bind;
    }


    /**
     * 会议提醒
     *
     * @return
     */
    public static byte[] setMeetingNotification(MettingModel mMettingModel) {

        MyLog.i(TAG, "mMettingModel = " + mMettingModel.toString());

        int year = mMettingModel.getMettingYear();
        int month = mMettingModel.getMettingMonth();
        int day = mMettingModel.getMettingDay();
        int hour = mMettingModel.getMettingHour();
        int min = mMettingModel.getMettingMin();

        int enable = 0;
        if (mMettingModel.isMettingEnable()) {
            enable = 1;
        }


        byte[] alarm = new byte[18];
        alarm[0] = (byte) 0xab;
        alarm[3] = 10;//L1长度
        alarm[8] = 1;
        alarm[10] = KEY_MEETING_NOTIFICATION;
        alarm[12] = 5;  // 5/per alarm * 2
        alarm[13] = (byte) ((year << 2) | (month >> 2));
        alarm[14] = (byte) (((month & 0x03) << 6) | (day << 1) | (hour >> 4));
        //alarm[14] = (byte) (hour1 >> 4);
        alarm[15] = (byte) (((hour & 0xf) << 4) | (min >> 2));
        alarm[16] = (byte) (((min & 0x3) << 6));
        alarm[17] = (byte) ((byte) enable << 7);

        return alarm;
    }


    /**
     * 吃药提醒
     *
     * @return
     */
    public static byte[] setMedicalNotification(DurgModel mDurgModle) {

        MyLog.i(TAG, "mDurgModle = " + mDurgModle.toString());

        int startHour = mDurgModle.getMedicineStartHourTime();
        int startMin = mDurgModle.getMedicineStartMinTime();
        int endHour = mDurgModle.getMedicineEndHourTime();
        int endMin = mDurgModle.getMedicineEndMinTime();
        int period = mDurgModle.getMedicineCycleTime();

        int enable = 0;
        if (mDurgModle.isMedicineEnable()) {
            enable = 1;
        }

        byte[] bind = new byte[17];
        bind[0] = (byte) 0xab;
        bind[3] = 9;//L1长度
        bind[8] = 0x01;
        bind[10] = KEY_MEDICAL_NOTIFICATION;
        bind[12] = 4;
        bind[13] = (byte) ((enable << 7) | (startHour >> 1));
        bind[14] = (byte) (((startHour & 0x01) << 7) | (startMin << 1) | (endHour >> 4));
        bind[15] = (byte) (((endHour & 0x0f) << 4) | (endMin >> 2));
        bind[16] = (byte) (((endMin & 0x03) << 6) | ((period)));

        return bind;
    }

    /**
     * 喝水提醒
     *
     * @return
     */
    public static byte[] setDrinkingNotification(WaterModel mWaterModel) {

        MyLog.i(TAG, "mWaterModel = " + mWaterModel.toString());

        int startHour = mWaterModel.getDrinkingStartHourTime();
        int startMin = mWaterModel.getDrinkingStartMinTime();
        int endHour = mWaterModel.getDrinkingEndHourTime();
        int endMin = mWaterModel.getDrinkingEndMinTime();
        int period = mWaterModel.getDrinkingCycleTime();


        //0=1小时，1=2小时，所以减1
        if (period > 0) {
            period -= 1;
        }

        int enable = 0;
        if (mWaterModel.isDrinkingEnable()) {
            enable = 1;
        }

        byte[] bind = new byte[17];
        bind[0] = (byte) 0xab;
        bind[3] = 9;//L1长度
        bind[8] = 0x01;
        bind[10] = KEY_DRINKING_NOTIFICATION;
        bind[12] = 4;
        bind[13] = (byte) ((enable << 7) | ((period + 1) << 4) | (startHour >> 1));
        bind[14] = (byte) (((startHour & 0x01) << 7) | (startMin << 1) | (endHour >> 4));
        bind[15] = (byte) (((endHour & 0x0f) << 4) | (endMin >> 2));
        bind[16] = (byte) ((endMin & 0x03) << 6);

        return bind;
    }


    /**
     * 找手环
     *
     * @return
     */
    public static byte[] findDeviceInstra() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 0x05;//L1长度
        data[8] = 0x01;
        data[9] = (byte) 0x80;//不需要回应
        data[10] = KEY_FIND_DEVICE_INSTRA;

        return data;
    }


    //打开心电
    public static byte[] OpenEcg() {
        byte[] data = new byte[14];
        data[0] = (byte) 0xab;
        data[1] = (byte) 0x00;
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x06;//L1长度

        data[4] = (byte) 0x00;
        data[5] = (byte) 0x00;
        data[6] = (byte) 0x00;
        data[7] = (byte) 0x00;

        data[8] = (byte) 0x01;
        data[9] = (byte) 0x00;
        data[10] = KEY_OPEN_ECG;
        data[11] = (byte) 0x00;
        data[12] = (byte) 0x01;
        data[13] = (byte) 0x01;

        return data;
    }

    //关闭心电
    public static byte[] CloseEcg() {
        byte[] data = new byte[14];
        data[0] = (byte) 0xab;
        data[1] = (byte) 0x00;
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x06;//L1长度

        data[4] = (byte) 0x00;
        data[5] = (byte) 0x00;
        data[6] = (byte) 0x00;
        data[7] = (byte) 0x00;

        data[8] = (byte) 0x01;
        data[9] = (byte) 0x00;
        data[10] = KEY_CLOSE_ECG;
        data[11] = (byte) 0x00;
        data[12] = (byte) 0x01;
        data[13] = (byte) 0x00;

        return data;
    }


    //返回测量结果
    public static byte[] result_heart(int heart, int par1, int par2) {
        byte[] data = new byte[16];
        data[0] = (byte) 0xab;
        data[3] = 0x08;//L1长度
        data[8] = 0x01;
        data[10] = KEY_RESULT_HEART;
        data[11] = (byte) 0x00;
        data[12] = (byte) 0x03;
        data[13] = (byte) heart;
        data[14] = (byte) par1;
        data[15] = (byte) par2;

        return data;
    }

    //进入DFU模式
    public static byte[] inputDfu() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[1] = (byte) 0x00;
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x05;//L1长度

        data[4] = (byte) 0x00;
        data[5] = (byte) 0x00;
        data[6] = (byte) 0x00;
        data[7] = (byte) 0x00;

        data[8] = (byte) 0x01;
        data[9] = (byte) 0x00;
        data[10] = KEY_INPUT_DFU;
        data[11] = (byte) 0x00;
        data[12] = (byte) 0x00;

        return data;
    }


    //发送手机类型/发送设备类型/中英文
    public static byte[] SendNewInitDevice(int language) {
        byte[] data = new byte[15];
        data[0] = (byte) 0xab;
        data[1] = (byte) 0x00;
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x07;//L1长度

        data[4] = (byte) 0x00;
        data[5] = (byte) 0x00;
        data[6] = (byte) 0x00;
        data[7] = (byte) 0x00;

        data[8] = (byte) 0x01;
        data[9] = (byte) 0x00;
        data[10] = KEY_SEND_NEW_INIT_DEVICE;
        data[11] = (byte) 0x00;
        data[12] = (byte) 0x02;

        data[13] = (byte) 0x00;//android
        data[14] = (byte) language;//语言

        return data;
    }


    /**
     * 电量 获取设备版本号
     *
     * @return
     */
    public static byte[] getDeviceVersion() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 5;//L1长度
        data[8] = 1;
        data[10] = KEY_GET_DEVICE_VERSION;
        data[12] = 0;
        return data;
    }


    /**
     * 抬腕亮屏开关
     *
     * @param value 时间格式 0代表关 1代表开，默认开
     * @return
     */
    public static byte[] setBrightScreen(int value) {
        byte[] bright_screen = new byte[14];
        bright_screen[0] = (byte) 0xab;
        bright_screen[3] = 6;//L1长度-修改
        bright_screen[8] = 1;
        bright_screen[10] = KEY_SET_BRIGHT_SCREEN;
        bright_screen[12] = 1;
        bright_screen[13] = (byte) value;
        return bright_screen;
    }

//
//    /**
//     * 转腕切屏开关
//     */
//    public static byte[] setCutScreen(int value) {
//        byte[] bright_screen = new byte[14];
//        bright_screen[0] = (byte) 0xab;
//        bright_screen[3] = 5;
//        bright_screen[8] = 1;
//        bright_screen[10] = KEY_CUT_SCREEN;
//        bright_screen[12] = 1;
//        bright_screen[13] = (byte) value;
//        return bright_screen;
//    }


    /**
     * 整点测量心率
     */
    public static byte[] setPointMeasurement(int value) {
        byte[] bright_screen = new byte[14];
        bright_screen[0] = (byte) 0xab;
        bright_screen[3] = 6;//L1长度-修改
        bright_screen[8] = 1;
        bright_screen[10] = KEY_POINT_MEASUREMENT;
        bright_screen[12] = 1;
        bright_screen[13] = (byte) value;
        return bright_screen;
    }

    /**
     * 连续测量心率
     */
    public static byte[] setPersistMeasurement(int value) {
        byte[] bright_screen = new byte[14];
        bright_screen[0] = (byte) 0xab;
        bright_screen[3] = 6;//L1长度-修改
        bright_screen[8] = 1;
        bright_screen[10] = KEY_PERSIST_MEASUREMENT;
        bright_screen[12] = 1;
        bright_screen[13] = (byte) value;
        return bright_screen;
    }

    /**
     * 免打扰开关
     */
    public static byte[] setDoNotDistrub(int value) {
        byte[] bright_screen = new byte[14];
        bright_screen[0] = (byte) 0xab;
        bright_screen[3] = 6;//L1长度-修改
        bright_screen[8] = 1;
        bright_screen[10] = KEY_DO_NOT_DISTRUB;
        bright_screen[12] = 1;
        bright_screen[13] = (byte) value;
        return bright_screen;
    }

    /**
     * 单位设置
     *
     * @return
     */
    public static byte[] setCompany(int value) {
        byte[] bright_screen = new byte[14];
        bright_screen[0] = (byte) 0xab;
        bright_screen[3] = 6;//L1长度-修改
        bright_screen[8] = 1;
        bright_screen[10] = KEY_SET_COMPANY;
        bright_screen[12] = 1;
        bright_screen[13] = (byte) value;
        return bright_screen;
    }

    //打开摇一摇拍照指令
    public static byte[] sendDevicePhoto() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 0x05;//L1长度
        data[8] = 0x01;
        data[10] = KEY_SEND_DEVICE_PHOTO;
        return data;
    }

    //关闭摇一摇拍照指令
    public static byte[] closeDevicePhoto() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 0x05;//L1长度
        data[8] = 0x01;
        data[10] = KEY_CLOSE_DEVICE_PHOTO;

        return data;
    }

    //发送指令让设备退出来电界面
    public static byte[] sendCloseCall() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 0x05;//L1长度
        data[8] = 0x01;
        data[10] = KEY_SEND_CLOSE_CALL;

        return data;
    }


    /**
     * 开关设置
     *
     * @return
     */
    public static byte[] setInitSet(int time, int unit, int bright, int zhuanwan, int heart, int miandarao,
                                    int new_heart, int theme, int skin, int screen_brighess, int briness_time,
                                    int temperatureType, int CountinuityTemp, int wholeHourTemp, int continuousBloodOxygen, int wholeHourBloodOxygen) {

        int parameterLength = 4;
        byte[] bright_screen = new byte[13 + parameterLength];
        bright_screen[0] = (byte) 0xab;
        bright_screen[3] = (byte) (5 + parameterLength);//L1长度-修改
        bright_screen[8] = 1;
        bright_screen[10] = KEY_SET_INIT_SET;
        bright_screen[12] = (byte) parameterLength;

        int parameterFlag1 = 0;
        int[] parameter1 = {time, unit, bright, zhuanwan, heart, miandarao, new_heart, 0};
        for (int i = 0; i < parameter1.length; i++) {
            if (parameter1[i] == 1) {
                parameterFlag1 += Math.pow(2, 7 - i);
            }
        }
        bright_screen[13] = (byte) parameterFlag1;
        MyLog.i(TAG, "init switch parameter1Flag=" + BleTools.toBinary(parameterFlag1, 8));

        int parameterFlag2 = 0;
        parameterFlag2 = ((theme << 5) | (skin << 2));
        if (temperatureType == 1) {
            parameterFlag2 = parameterFlag2 | 0x02;
        }
        if (CountinuityTemp == 1) {
            parameterFlag2 = parameterFlag2 | 0x01;
        }
        bright_screen[14] = (byte) parameterFlag2;
        MyLog.i(TAG, "init switch parameterFlag2=" + BleTools.toBinary(parameterFlag2, 8));

        int parameterFlag3 = 0;
        parameterFlag3 = ((screen_brighess << 4) | (briness_time & 0xf));
        bright_screen[15] = (byte) parameterFlag3;
        MyLog.i(TAG, "init switch parameterFlag3=" + BleTools.toBinary(parameterFlag3, 8));

        int parameterFlag4 = 0;
        int[] parameter4 = {wholeHourTemp, continuousBloodOxygen, wholeHourBloodOxygen, 0, 0, 0, 0, 0};
        for (int i = 0; i < parameter4.length; i++) {
            if (parameter4[i] == 1) {
                parameterFlag4 += Math.pow(2, 7 - i);
            }
        }
        bright_screen[16] = (byte) parameterFlag4;
        MyLog.i(TAG, "init switch parameterFlag4=" + BleTools.toBinary(parameterFlag4, 8));


//        int aa = 0;
//        int bb = 0;
//        int cc = 0;
//        if (time == 1) {
//            aa = aa | 0x80;
//        }
//        if (unit == 1) {
//            aa = aa | 0x40;
//        }
//        if (bright == 1) {
//            aa = aa | 0x20;
//        }
//        if (zhuanwan == 1) {
//            aa = aa | 0x10;
//        }
//
//        if (heart == 1) {
//            aa = aa | 0x08;
//        }
//
//        if (miandarao == 1) {
//            aa = aa | 0x04;
//        }
//
//        if (new_heart == 1) {
//            aa = aa | 0x02;
//        }
//
//        bright_screen[13] = (byte) aa;
//        MyLog.i(TAG, "init switch byte1=" + BleTools.toBinary(aa, 8));
//
//        bb = ((theme << 5) | (skin << 2));
//
//        if (temperatureType == 1) {
//            bb = bb | 0x02;
//        }
//        if (CountinuityTemp == 1) {
//            bb = bb | 0x01;
//        }
//
//        bright_screen[14] = (byte) bb;
//
//        MyLog.i(TAG, "init switch byte2=" + BleTools.toBinary(bb, 8));
//
//        cc = ((screen_brighess << 4) | (briness_time & 0xf));
//        bright_screen[15] = (byte) cc;
//
//        MyLog.i(TAG, "init switch byte3=" + BleTools.toBinary(cc, 8));

        return bright_screen;
    }

    /**
     * 上传校准数据
     *
     * @return
     */
    public static byte[] setUserPar(int user_par, int user_heart) {

        MyLog.i(TAG, "上传校准数据 = 高压 = " + user_par + "  心率 = " + user_heart);

        if (user_par > 250) {
            user_par = 250;
        } else if (user_par <= 0) {
            user_par = 0;

        }
        if (user_heart > 250) {
            user_heart = 250;
        } else if (user_heart <= 0) {
            user_heart = 0;

        }
        byte[] bright_screen = new byte[15];
        bright_screen[0] = (byte) 0xab;
        bright_screen[3] = 7;//L1长度-修改
        bright_screen[8] = 1;
        bright_screen[10] = KEY_SET_USER_PAR;
        bright_screen[12] = 2;
        bright_screen[13] = (byte) user_par;
        bright_screen[14] = (byte) user_heart;
        return bright_screen;
    }

    /**
     * 来电提醒
     *
     * @param pn
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte[] notifyIncommingCall(String pn) throws UnsupportedEncodingException {

        System.out.println("来电处理1 = " + pn);

        if (pn.length() >= 32) {
            pn = pn.substring(0, 31);
        }

        System.out.println("来电处理2 = " + pn);

        pn = BleTools.FiltrationOne(pn);
        byte[] hexChar = pn.getBytes();
        byte[] notifiCall = new byte[13 + 1 + hexChar.length];
        notifiCall[0] = (byte) 0xab;
        notifiCall[3] = (byte) (5 + 1 + hexChar.length);//L1长度
        notifiCall[8] = 2;
        notifiCall[10] = BleConstant.NotifaceMsgPhone;
        notifiCall[12] = (byte) (hexChar.length + 1);
        notifiCall[13] = (byte) (pn.getBytes().length & 255 | 128);//1  1   6

        for (int i = 0; i < hexChar.length; i++) {
            notifiCall[14 + i] = (byte) hexChar[i];
        }

        String str = "";
        for (int i = 0; i < notifiCall.length; i++) {
            String strTmp = Integer.toHexString(notifiCall[i]);
            if (strTmp.length() > 2) {
                strTmp = strTmp.substring(strTmp.length() - 2);
            } else if (strTmp.length() < 2) {
                strTmp = "0" + strTmp;
            }
            str = str + strTmp;
        }

        MyLog.i(TAG, "来电话了！str = " + str.toUpperCase());
        MyLog.i(TAG, "来电话了！byte= " + notifiCall.length);
        MyLog.i(TAG, "来电的16进制源数据为= " + str.toUpperCase());

        return notifiCall;
    }


    /**
     * 来电提醒(G20 - BleService.DeviceType==6&&DeviceVersion==16)
     *
     * @param pn
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte[] notifyIncommingCallG20(String pn) throws UnsupportedEncodingException {

        MyLog.i(TAG, "输出结果 = abccc1 =" + pn);
        MyLog.i(TAG, "输出结果 = abccc1 =" + pn.getBytes().length);

        if (pn.getBytes().length == 15 && pn.length() == 5) {
            pn = pn + "    ";

        } else if (pn.getBytes().length == pn.length()) {
            if (pn.getBytes().length >= 9 && pn.getBytes().length <= 11) {
                pn = pn + "    ";

            }

        }

        MyLog.i(TAG, "输出结果 = abccc2 =" + pn);
        MyLog.i(TAG, "输出结果 = abccc2 =" + pn.getBytes().length);

        pn = BleTools.FiltrationOne(pn);

        byte[] hexChar = pn.getBytes();
        byte[] notifiCall = new byte[13 + 1 + hexChar.length];
        notifiCall[0] = (byte) 0xab;
        notifiCall[3] = (byte) (5 + 1 + hexChar.length);//L1长度
        notifiCall[8] = 2;
        notifiCall[10] = BleConstant.NotifaceMsgPhone;
        notifiCall[12] = (byte) (hexChar.length + 1);
        notifiCall[13] = (byte) (pn.getBytes().length & 255 | 128);//1  1   6

        for (int i = 0; i < hexChar.length; i++) {
            notifiCall[14 + i] = (byte) hexChar[i];
        }

        String str = "";
        for (int i = 0; i < notifiCall.length; i++) {
            String strTmp = Integer.toHexString(notifiCall[i]);
            if (strTmp.length() > 2) {
                strTmp = strTmp.substring(strTmp.length() - 2);
            } else if (strTmp.length() < 2) {
                strTmp = "0" + strTmp;
            }
            str = str + strTmp;
        }

        MyLog.i(TAG, "来电话了！str = " + str.toUpperCase());
        MyLog.i(TAG, "来电话了！byte= " + notifiCall.length);
        return notifiCall;
    }


    /**
     * 消息推送
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte[] notifyMsg(BleDeviceTools mBleDeviceTools, String pn, int app_type) throws UnsupportedEncodingException {
        SysUtils.logContentI(TAG, " app type=" + app_type + " postmessage=" + pn);

        if (mBleDeviceTools.get_notiface_type() == 1) {
            pn = BleTools.FiltrationTwo(pn);
        } else {
            pn = BleTools.FiltrationOne(pn);
        }

        byte[] hexChar = pn.getBytes();
        byte[] notifiCall = new byte[13 + 1 + hexChar.length];
        notifiCall[0] = (byte) 0xab;
        notifiCall[3] = (byte) (5 + 1 + hexChar.length);//L1长度
        notifiCall[8] = 2;
        notifiCall[10] = (byte) app_type;//key 需要变（来电=1，QQ=2,）
        notifiCall[12] = (byte) (hexChar.length + 1);
        notifiCall[13] = (byte) (hexChar.length);
        for (int i = 0; i < hexChar.length; i++) {
            notifiCall[14 + i] = (byte) hexChar[i];
        }

        String str = "";
        for (int i = 0; i < notifiCall.length; i++) {
            String strTmp = Integer.toHexString(notifiCall[i]);
            if (strTmp.length() > 2) {
                strTmp = strTmp.substring(strTmp.length() - 2);
            } else if (strTmp.length() < 2) {
                strTmp = "0" + strTmp;
            }
            str = str + strTmp;
        }
        return notifiCall;
    }

    //设置生理周期
    public static byte[] setCycle(String sex, UserSetTools mUserSetTools, BleDeviceTools mBleDeviceTools) {

        int period = mBleDeviceTools.get_device_cycle_jingqi();
        int one = mBleDeviceTools.get_device_cycle_anqunqiyi();
        int danger = mBleDeviceTools.get_device_cycle_weixianqi();
        int two = mBleDeviceTools.get_device_cycle_anquanqier();

        int is_open = 0;

        int year = 0;
        int mon = 1;
        int day = 1;


        if (mUserSetTools.get_nv_device_switch() && !sex.equals("0") && !mUserSetTools.get_device_is_one_cycle()) {
            is_open = 1;
        } else {
            is_open = 0;
        }


        if (mUserSetTools.get_nv_start_date() != null && !mUserSetTools.get_nv_start_date().equals("")) {
            String time[] = mUserSetTools.get_nv_start_date().split("-");
            year = Integer.valueOf(time[0]) - 2000;
            mon = Integer.valueOf(time[1]);
            day = Integer.valueOf(time[2]);
        }


        MyLog.i(TAG, "上传生理周期数据 = is_open = " + is_open);
        MyLog.i(TAG, "上传生理周期数据 = year = " + year);
        MyLog.i(TAG, "上传生理周期数据 = mon = " + mon);
        MyLog.i(TAG, "上传生理周期数据 = day = " + day);

        MyLog.i(TAG, "上传生理周期数据 = period = " + period);
        MyLog.i(TAG, "上传生理周期数据 = one = " + one);
        MyLog.i(TAG, "上传生理周期数据 = danger = " + danger);
        MyLog.i(TAG, "上传生理周期数据 = two = " + two);


        byte[] data = new byte[19];

        data[0] = (byte) 0xab;
        data[3] = 0x0b;//L1长度
        data[8] = 0x01;
        data[10] = KEY_SET_CYCLE;
        data[12] = 6;

        data[13] = (byte) ((year << 2) | (mon >> 2));
        data[14] = (byte) (((mon & 0x03) << 6) | (day << 1) | (is_open & 0x01));

        data[15] = (byte) (period & 0xff);
        data[16] = (byte) (one & 0xff);
        data[17] = (byte) (danger & 0xff);
        data[18] = (byte) (two & 0xff);

//      MyLog.i(TAG,"上传生理周期数据  data = " + BleService.printHexString(data));


        return data;

    }


    /**
     * 获取屏保参数设置
     *
     * @return
     */
    public static byte[] getDeviceScreensaverInfo() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 0x05;//L1长度
        data[8] = 0x01;
        data[10] = KEY_DEVICE_SCREEN_SAVER_INFO;

        return data;
    }


    /**
     * 设置屏保参数设置
     *
     * @param mBleDeviceTools
     * @return
     */
    public static byte[] ScreensaverSet(BleDeviceTools mBleDeviceTools) {


        int color = 0, coordinate_x = 0, coordinate_y = 0, isShowTime = 0, isShowDate = 0, isUserImager = 0;

        boolean isShowScreensaver = false;

        if (mBleDeviceTools != null) {

            if (!JavaUtil.checkIsNull(mBleDeviceTools.get_screensaver_bast64())) {
                isUserImager = 1;
            } else {
                isUserImager = 0;
            }

            color = mBleDeviceTools.get_screensaver_color();
            coordinate_x = mBleDeviceTools.get_screensaver_x_time();
            coordinate_y = mBleDeviceTools.get_screensaver_y_time();
            isShowTime = mBleDeviceTools.get_screensaver_is_show_time();
            isShowDate = mBleDeviceTools.get_screensaver_is_show_date();
            isShowScreensaver = mBleDeviceTools.get_device_screensaver();
//            isUserImager = mBleDeviceTools.get_screensaver_is_user_imager();
        }


        MyLog.i(TAG, " 发送数据 = coordinate_x " + coordinate_x);
        MyLog.i(TAG, " 发送数据 = coordinate_y " + coordinate_y);
        MyLog.i(TAG, " 发送数据 = isShowTime " + isShowTime);
        MyLog.i(TAG, " 发送数据 = isShowDate " + isShowDate);
        MyLog.i(TAG, " 发送数据 = isShowScreensaver " + isShowScreensaver);


        byte[] rgb16 = BmpUtils.RGB24TORGB16((byte) Color.red(color), (byte) Color.green(color), (byte) Color.blue(color));

        String rgbhexString = BmpUtils.bytes2HexString16TXT(rgb16);
        MyLog.i(TAG, " 发送数据 = 颜色  " + rgbhexString);

        byte[] data = new byte[20];
        data[0] = (byte) 0xab;
        data[3] = 0x0c;//L1长度
        data[8] = 0x01;
        data[10] = KEY_SCREEN_SAVER_SET;
        data[12] = 0x07;

        //颜色
        data[13] = rgb16[0];
        data[14] = rgb16[1];
        //X
        data[15] = 0x00;
        data[16] = (byte) coordinate_x;
        //Y’5
        data[17] = 0x00;
        data[18] = (byte) coordinate_y;
        //参数
        int aa = 0;
        if (isShowTime == 1) {
            aa = aa | 0x80;
        }
        if (isShowDate == 1) {
            aa = aa | 0x40;
        }
        if (isShowScreensaver) {
            aa = aa | 0x20;
        }
        if (isUserImager == 1) {
            aa = aa | 0x10;
        }

        data[19] = (byte) aa;

        String hexString = BmpUtils.bytes2HexString16TXT(data);

        MyLog.i(TAG, " 发送数据 = " + hexString);

        return data;
    }


    /**
     * 设置主题
     *
     * @return
     */
    public static byte[] setDeviceTheme(int value) {
        byte[] data = new byte[14];
        data[0] = (byte) 0xab;
        data[3] = 0x06;//L1长度-修改
        data[8] = 0x01;
        data[10] = KEY_DEVICE_THEME;
        data[12] = 1;
        data[13] = (byte) (value);
        return data;
    }

    /**
     * 设置肤色
     *
     * @return
     */
    public static byte[] setDeviceSkinColour(int value) {
        byte[] data = new byte[14];
        data[0] = (byte) 0xab;
        data[3] = 0x06;//L1长度-修改
        data[8] = 0x01;
        data[10] = KEY_DEVICE_SKIN_COLOUR;
        data[12] = 1;
        data[13] = (byte) value;

        return data;
    }

    /**
     * 亮度等级
     *
     * @return
     */
    public static byte[] setScreenBrightness(int value) {
        byte[] data = new byte[14];
        data[0] = (byte) 0xab;
        data[3] = 0x06;//L1长度-修改
        data[8] = 0x01;
        data[10] = KEY_SCREEN_BRIGHTNESS;
        data[12] = 1;
        data[13] = (byte) value;

        return data;
    }

    /**
     * 亮屏时间
     *
     * @return
     */
    public static byte[] setBrightnessTime(int value) {
        byte[] data = new byte[14];
        data[0] = (byte) 0xab;
        data[3] = 0x06;//L1长度-修改
        data[8] = 0x01;
        data[10] = KEY_BRIGHTNESS_TIME;
        data[12] = 1;
        data[13] = (byte) value;

        return data;
    }

    //设置天气
    public static byte[] setWeather(byte[] wather_data) {

        int data_len = wather_data.length;

        int length = 13 + data_len;
        byte[] wather = new byte[length];
        wather[0] = (byte) 0xab;
        wather[3] = (byte) (5 + data_len);//L1长度
        wather[8] = 0x01;
        wather[10] = KEY_SET_WATHER;
        wather[12] = (byte) (data_len);


        for (int i = 0; i < data_len; i++) {
            wather[13 + i] = wather_data[i];
        }
        return wather;

    }


    /**
     * 发送图片头
     *
     * @param width
     * @param height
     * @return
     */
    public static byte[] getImageHead(int width, int height, byte crc) {

        byte[] value = new byte[20];

        value[0] = (byte) 0x55;
        value[1] = (byte) 0xaa;
        value[2] = (byte) 0x00;
        value[3] = (byte) 0x00;

        //图片长
        value[4] = (byte) 0x00;
        value[5] = (byte) width;

        //图片宽
        value[6] = (byte) 0x00;
        value[7] = (byte) height;
        value[18] = crc;


        String hexString2 = BleTools.bytes2HexString(value);
        MyLog.i(TAG, "SendBleActivity 测试数据 = hexString2 = " + hexString2);


        return value;
    }


    //=============设备测试方法============

    //同步时间
    static byte[] syncNewTime(String dateString) {
        MyLog.i(TAG, "同步时间2 = dateString " + dateString);
        // MyUtils.MyLog(Tag, "同步时间");
        byte[] time = new byte[17];
        time[0] = (byte) 0xab;
        time[3] = (byte) 9;
        char[] hexChars = dateString.toCharArray();
        int year = charToByte(hexChars[2]) * 10 + charToByte(hexChars[3]);
        int mon = charToByte(hexChars[4]) * 10 + charToByte(hexChars[5]);
        int day = charToByte(hexChars[6]) * 10 + charToByte(hexChars[7]);
        int hour = charToByte(hexChars[8]) * 10 + charToByte(hexChars[9]);
        int min = charToByte(hexChars[10]) * 10 + charToByte(hexChars[11]);
        int sec = charToByte(hexChars[12]) * 10 + charToByte(hexChars[13]);
        time[8] = 1;
        time[10] = KEY_SYNC_TIME;
        time[12] = 4;
        time[13] = (byte) ((year << 2) | (mon >> 2));
        time[14] = (byte) (((mon & 0x03) << 6) | (day << 1) | (hour >> 4));
        time[15] = (byte) (((hour & 0x0f) << 4) | (min >> 2));
        time[16] = (byte) (((min & 0x03) << 6) | (sec));
        return time;
    }

    /**
     * 检查日期是否不等于默认日期，合法则返回ture,否则返回false
     *
     * @return
     */
    public static boolean checkDeviceDate(String date) {
        if (date != null && !date.equals(Constants.DEVICE_DEFULT_DATE)) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 检查日期是否不等于默认日期，合法则返回ture,否则返回false
     *
     * @return
     */
    public static boolean checkDeviceTime(String date) {
        if (date != null && !date.equals(Constants.DEVICE_DEFULT_TIME)) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 检测当前年份和上一次的年份是否一致，如果一致，返回true,不一致返回flase
     *
     * @return
     */
    public static boolean checkCheckYears(String device_years) {
        String now_years = MyTime.getYears();
        if (device_years.equals(now_years)) {
            return true;
        } else {
            return false;
        }

    }


    /**
     * 发送主题头
     *
     * @param total_page
     * @param mtu
     * @param block_size
     * @return
     */
    public static byte[] sendThemeHead(int total_page, int mtu, int block_size) {

        byte[] value = new byte[11];
        //空头
        value[0] = (byte) 0x00;
        value[1] = (byte) 0x00;
        //指令
        value[2] = (byte) 0x00;
        //总大小
        value[3] = (byte) ((total_page >> 24) & 0xff);
        value[4] = (byte) ((total_page >> 16) & 0xff);
        value[5] = (byte) ((total_page >> 8) & 0xff);
        value[6] = (byte) (total_page & 0xff);
        //MTU
        value[7] = (byte) mtu;
        //每一块大小
        value[8] = (byte) ((block_size >> 8) & 0xff);
        value[9] = (byte) (block_size & 0xff);
        //类型
        value[10] = KEY_SEND_THEME_HEAD;

        String hexString2 = BleTools.bytes2HexString(value);
        MyLog.i(TAG, "SendBleActivity 测试数据 = hexString2 = " + hexString2);


        return value;
    }


    /**
     * 发送音频头
     *
     * @param total_page
     * @param mtu
     * @param block_size
     * @return
     */
    public static byte[] sendMusicHead(int total_page, int mtu, int block_size, String music_name) {

        byte[] hexChar = music_name.getBytes();

        byte[] value = new byte[12 + hexChar.length];
        //空头
        value[0] = (byte) 0x00;
        value[1] = (byte) 0x00;
        //指令
        value[2] = (byte) 0x00;
        //总大小
        value[3] = (byte) ((total_page >> 24) & 0xff);
        value[4] = (byte) ((total_page >> 16) & 0xff);
        value[5] = (byte) ((total_page >> 8) & 0xff);
        value[6] = (byte) (total_page & 0xff);
        //MTU
        value[7] = (byte) mtu;
        //每一块大小
        value[8] = (byte) ((block_size >> 8) & 0xff);
        value[9] = (byte) (block_size & 0xff);
        //类型
        value[10] = KEY_SEND_MUSIC_HEAD;

        value[11] = (byte) (hexChar.length & 0xff);

        for (int i = 0; i < hexChar.length; i++) {
            value[12 + i] = (byte) hexChar[i];
        }

        String hexString2 = BleTools.bytes2HexString(value);
        MyLog.i(TAG, "SendBleActivity 测试数据 = hexString2 = " + hexString2);


        return value;
    }


    /**
     * 发送通讯录头
     *
     * @param total_page
     * @param mtu
     * @param block_size
     * @return
     */
    public static byte[] sendMailListHead(int total_page, int mtu, int block_size, int crc) {

        byte[] value = new byte[15];
        //空头
        value[0] = (byte) 0x00;
        value[1] = (byte) 0x00;
        //指令
        value[2] = (byte) 0x00;
        //总大小
        value[3] = (byte) ((total_page >> 24) & 0xff);
        value[4] = (byte) ((total_page >> 16) & 0xff);
        value[5] = (byte) ((total_page >> 8) & 0xff);
        value[6] = (byte) (total_page & 0xff);
        //MTU
        value[7] = (byte) mtu;
        //每一块大小
        value[8] = (byte) ((block_size >> 8) & 0xff);
        value[9] = (byte) (block_size & 0xff);
        //类型
        value[10] = KEY_MAIL_LIST_HEAD;
        value[11] = (byte) ((crc >> 24) & 0xff);
        value[12] = (byte) ((crc >> 16) & 0xff);
        value[13] = (byte) ((crc >> 8) & 0xff);
        value[14] = (byte) (crc & 0xff);

        String hexString2 = BleTools.bytes2HexString(value);
        MyLog.i(TAG, "SendBleActivity 测试数据 = hexString2 = " + hexString2);


        return value;
    }


    /**
     * 发送数据
     *
     * @param sn
     * @param data
     * @return
     */
    public static byte[] sendThemeData(int sn, byte[] data) {

        byte[] value = new byte[data.length + 2];
        //SN
        value[0] = (byte) ((sn >> 8) & 0xff);
        value[1] = (byte) (sn & 0xff);
        //数据
        for (int i = 0; i < data.length; i++) {
            value[i + 2] = data[i];
        }

        String hexString2 = BleTools.bytes2HexString(value);
        MyLog.i(TAG, "SendBleActivity 测试数据 = hexString2 = " + hexString2);

        return value;
    }

    public static byte[] sendThemeBlockVerrification() {

        byte[] value = new byte[3];
        //空头
        value[0] = (byte) 0x00;
        value[1] = (byte) 0x00;
        //指令
        value[2] = (byte) 0x17;

        String hexString2 = BleTools.bytes2HexString(value);
        MyLog.i(TAG, "SendBleActivity 测试数据 = hexString2 = " + hexString2);

        return value;
    }


    /**
     * 发送音乐播放信息
     *
     * @return
     */
    public static byte[] sendMusicControlInfoNew(MusicInfo mMusicInfo) {
        int valueLenth = 3;

        String MusicName = "";

        if (mMusicInfo.getMusicName() != null && !mMusicInfo.getMusicName().equals("")) {
            MusicName = mMusicInfo.getMusicName();
        }

        MyLog.i(TAG, "音乐控制 更新信息 发出 mMusicInfo:" + mMusicInfo.toString());

        MyLog.i(TAG, "音乐控制 更新信息 发出 MusicName:" + MusicName);

        byte[] hexChar = MusicName.getBytes();


        byte[] data = new byte[13 + valueLenth + hexChar.length];

        MyLog.i(TAG, "音乐控制 更新信息 发出 data:" + data.length);

        data[0] = (byte) 0xab;
        data[3] = (byte) (5 + hexChar.length + valueLenth);
        data[8] = 0x01;
        data[9] = (byte) 0x80;//不需要回应
        data[10] = KEY_MUSIC_CONTROL_INFO_NEW;
        data[12] = (byte) (hexChar.length + valueLenth);

        if (mMusicInfo.getVolumeLevel() >= 0 && mMusicInfo.getVolumeLevel() <= 10000) {
//            data[13] = (byte) (mMusicInfo.getVolumeLevel() & 0xff);
            data[13] = (byte) ((mMusicInfo.getVolumeLevel() & 0xff00) >> 8);
            data[14] = (byte) (mMusicInfo.getVolumeLevel() & 0xff);

        } else {
            data[13] = (byte) 0xff;
            data[14] = (byte) 0xff;
        }

        MyLog.i(TAG, "音乐控制 更新信息 发出 getPlayState:" + mMusicInfo.getPlayState());

        if (mMusicInfo.getPlayState() >= 0 && mMusicInfo.getPlayState() <= 1) {
            data[15] = (byte) (mMusicInfo.getPlayState() & 0xff);
        } else {
            data[15] = (byte) 0xff;
        }

        for (int i = 0; i < hexChar.length; i++) {
            data[16 + i] = (byte) hexChar[i];
        }

        String hexString = BleTools.bytes2HexString(data);
        MyLog.i(TAG, "音乐控制 更新信息 发出 hexString:" + hexString);

        return data;
    }


    /**
     * 获取手环主题信息
     *
     * @return
     */
    public static byte[] getDeviceThemeInfo() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 0x05;//L1长度
        data[8] = 0x01;
        data[10] = KEY_DEVICE_THEME_INFO;

        return data;
    }

    /**
     * 獲取MAC地址
     *
     * @return
     */
    public static byte[] getMacAddress() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 0x05;//L1长度
        data[8] = 0x01;
        data[10] = KEY_GET_MAC_ADDRESS;

        return data;
    }

    /**
     * 获取MTU
     *
     * @return
     */
    public static byte[] getDeviceMtu() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 0x05;//L1长度
        data[8] = 0x01;
        data[10] = KEY_GET_DEVICE_MTU;

        return data;
    }

    /**
     * 获取设备错误log
     *
     * @return
     */
    public static byte[] getDeviceErrorLog() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 0x05;//L1长度
        data[8] = 0x01;
        data[10] = KEY_GET_DEVICE_ERROR_LOG;
        return data;
    }

    /**
     * 设置设备错误log
     *
     * @return
     */
    public static byte[] setDeviceErrorLog() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 0x05;//L1长度
        data[8] = 0x01;
        data[10] = KEY_SET_DEVICE_ERROR_LOG;
        return data;
    }

    //通过APP行为提前结束睡眠
    public static byte[] endDeviceSleepTag() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 0x05;//L1长度
        data[8] = 0x01;
        data[10] = KEY_END_DEVICE_SLEEP_TAG;
        return data;
    }

    /**
     * 获取通话蓝牙-经典蓝牙信息
     *
     * @return
     */
    public static byte[] getCallDeviceInfo() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 0x05;//L1长度
        data[8] = 0x01;
        data[10] = KEY_GET_CALL_DEVICE_INFO;

        return data;
    }

    /**
     * 获取通话蓝牙-经典蓝牙信息
     *
     * @return
     */
    public static byte[] cleanMailList() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 0x05;//L1长度
        data[8] = 0x01;
        data[10] = KEY_CLEAN_MAIL_LIST;

        return data;
    }

    /**
     * 连续血氧测量
     */
    public static byte[] setContinuitySpo2(int value) {
        byte[] data = new byte[14];
        data[0] = (byte) 0xab;
        data[3] = 6;//L1长度-修改
        data[8] = 1;
        data[10] = KEY_SET_CONTINUITY_SPO2;
        data[12] = 1;
        data[13] = (byte) value;
        return data;
    }

    /**
     * 连续体温测量
     */
    public static byte[] setContinuityTemp(int value) {
        byte[] data = new byte[14];
        data[0] = (byte) 0xab;
        data[3] = 6;//L1长度-修改
        data[8] = 1;
        data[10] = KEY_SET_CONTINUITY_TEMP;
        data[12] = 1;
        data[13] = (byte) value;
        return data;
    }

    public static byte[] bindDevice(int type) {
        byte[] data = new byte[1];
        data[0] = (byte) type;
        return getBleData(data, CMD_01, 0x6c);
    }

    /**
     * @param type 温度格式 默认0 摄氏度
     * @return byte ab 00 00 06 00 00 00 00 01 00 49 00 01 00
     */
    public static byte[] setTemperatureType(int type) {
        byte[] temperature = new byte[14];
        temperature[0] = (byte) 0xab;
        temperature[3] = 0x06;//L1长度
        temperature[8] = 0x01;
        temperature[10] = KEY_SET_TEMPERATURE_TYPE;
        temperature[12] = 0x01;
        temperature[13] = (byte) type;
        return temperature;
    }

    public static byte[] appStartCmd(int pack) {
        BleService.currentUuid_proto = BleConstant.CHAR_PROTOBUF_UUID_02;
        byte[] valueByte = new byte[6];
        valueByte[0] = (byte) 0;
        valueByte[1] = (byte) 0;
        valueByte[2] = (byte) 0;
        valueByte[3] = (byte) 0;
        valueByte[4] = (byte) pack;
        valueByte[5] = (byte) 0;
        return valueByte;
    }

    public static byte[] deviceStartCmd() {
        byte[] valueByte = new byte[6];
        valueByte[0] = (byte) 0;
        valueByte[1] = (byte) 0;
        valueByte[2] = (byte) 1;
        valueByte[3] = (byte) 1;
        valueByte[4] = (byte) 0;
        valueByte[5] = (byte) 0;
        return valueByte;
    }

    public static byte[] appConfirm() {
        byte[] valueByte = new byte[6];
        valueByte[0] = (byte) 0;
        valueByte[1] = (byte) 0;
        valueByte[2] = (byte) 1;
        valueByte[3] = (byte) 0;
        valueByte[4] = (byte) 0;
        valueByte[5] = (byte) 0;
        return valueByte;
    }

    // 0 今天  1 历史
    public static byte[] GetSportIds(int type) {
        return getProtoByte(FitnessTools.getSportIds(type));
    }

    public static byte[] requestFitnessId() {
        return getProtoByte(FitnessTools.requestFitnessId());
    }

    public static byte[] deleteSportId() {
        return getProtoByte(FitnessTools.deleteSportId());
    }

    public static byte[] getPageDevice() {
        return getProtoByte(SystemTools.getPageDevice());
    }

    public static byte[] getPageDeviceSet() {
        return getProtoByte(SystemTools.getPageDeviceSet());
    }

    public static byte[] getGpsReady(GpsSportManager.GpsInfo gpsInfo) {
        return getProtoByte(FitnessTools.getGpsReady(gpsInfo));
    }

    public static byte[] getGpsByte(GpsSportManager.GpsInfo gpsInfo) {
        return getProtoByte(FitnessTools.getGpsByte(gpsInfo));
    }

    public static byte[] getRequestGpsStateByte() {
        return getProtoByte(FitnessTools.getDeviceGpsState());
    }

    public static byte[] getDeviceWatchFaceListByte() {
        return getProtoByte(WatchFaceTools.getDeviceWatchFaceList());
    }

    public static byte[] setDeviceWatchFaceByte(String id) {
        return getProtoByte(WatchFaceTools.setDeviceWatchFace(id));
    }

    public static byte[] deleteDeviceWatchFaceByte(String id) {
        return getProtoByte(WatchFaceTools.deleteDeviceWatchFace(id));
    }

    public static byte[] getDeviceWatchFacePrepareStatus(String themeId, int themeSize) {
        return getProtoByte(WatchFaceTools.getDeviceWatchFacePrepareStatus(themeId, themeSize));
    }

    public static byte[] getDeviceOtaPrepareStatus(boolean isForce, String version, String md5) {
        return getProtoByte(SystemTools.getDeviceOtaPrepareStatus(isForce, version, md5));
    }


    private static byte[] getProtoByte(byte[] valueByte) {
        byte[] valueByte1 = new byte[2];
        valueByte1[0] = (byte) 1;
        if (valueByte == null) {
            return valueByte1;
        }
        byte[] byte_3 = new byte[valueByte1.length + valueByte.length];
        System.arraycopy(valueByte1, 0, byte_3, 0, valueByte1.length);
        System.arraycopy(valueByte, 0, byte_3, valueByte1.length, valueByte.length);
        return byte_3;
    }

    /**
     * state 0=失败，1=成功，2=询问
     *
     * @param state
     * @return
     */
    public static byte[] sendSportState(int state) {
        byte[] data = new byte[2];
        data[0] = (byte) 0x00;
        data[1] = (byte) state;
        return getBleData(data, CMD_01, KEY_DEVICE_TO_APP_SPORT);
    }


    public static byte[] sendSportData(double latitude, double longitude, int gpsAccuracy) {
        Log.w(TAG, "locationChangeEventBus latitude = " + latitude + "  longitude = " + longitude);
        byte[] data = new byte[15];
        data[0] = (byte) 0x00;
        data[1] = (byte) 0x04;

        data[2] = (byte) gpsAccuracy;

        int time = (int) (System.currentTimeMillis() / 1000);
        data[3] = (byte) ((time >> 24) & 0xFF);
        data[4] = (byte) ((time >> 16) & 0xFF);
        data[5] = (byte) ((time >> 8) & 0xFF);
        data[6] = (byte) (time & 0xFF);

        int lat = (int) (latitude * 1000000) + 90000000;
        data[7] = (byte) ((lat >> 24) & 0xFF);
        data[8] = (byte) ((lat >> 16) & 0xFF);
        data[9] = (byte) ((lat >> 8) & 0xFF);
        data[10] = (byte) (lat & 0xFF);

        int lon = (int) (longitude * 1000000) + 180000000;
        data[11] = (byte) ((lon >> 24) & 0xFF);
        data[12] = (byte) ((lon >> 16) & 0xFF);
        data[13] = (byte) ((lon >> 8) & 0xFF);
        data[14] = (byte) (lon & 0xFF);

        return getBleData(data, CMD_01, KEY_DEVICE_TO_APP_SPORT);
    }

    public static byte[] sendGoalData(int type, int goal) {
        byte[] data = new byte[5];
        data[0] = (byte) type;
        data[1] = (byte) ((goal >> 24) & 0xFF);
        data[2] = (byte) ((goal >> 16) & 0xFF);
        data[3] = (byte) ((goal >> 8) & 0xFF);
        data[4] = (byte) (goal & 0xFF);
        return getBleData(data, CMD_01, KEY_SET_GOAL);
    }

}
