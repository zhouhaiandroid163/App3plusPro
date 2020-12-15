package com.zjw.apps3pluspro.bleservice;

import android.content.Context;

import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sql.dbmanager.ContinuitySpo2InfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.ContinuityTempInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.HealthInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.HeartInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.MeasureSpo2InfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.MeasureTempInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.MovementInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.SleepInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.SportModleInfoUtils;
import com.zjw.apps3pluspro.sql.entity.ContinuitySpo2Info;
import com.zjw.apps3pluspro.sql.entity.ContinuityTempInfo;
import com.zjw.apps3pluspro.sql.entity.HealthInfo;
import com.zjw.apps3pluspro.sql.entity.HeartInfo;
import com.zjw.apps3pluspro.sql.entity.MeasureSpo2Info;
import com.zjw.apps3pluspro.sql.entity.MeasureTempInfo;
import com.zjw.apps3pluspro.sql.entity.MovementInfo;
import com.zjw.apps3pluspro.sql.entity.SleepInfo;
import com.zjw.apps3pluspro.sql.entity.SportModleInfo;
import com.zjw.apps3pluspro.utils.GoogleFitManager;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.List;

public class HandleDeviceDataTools {
    private static final String TAG = HandleDeviceDataTools.class.getSimpleName();

    /**
     * 处理运动数据
     *
     * @param data
     */
    public static void HandleMotion(MovementInfoUtils mMovementInfoUtils, byte[] data) {
        boolean isCheck = BleTools.checkData(data);
        MyLog.i(TAG, "蓝牙回调 - 运动数据 校验结果 = " + isCheck);

        if (isCheck) {
            MyLog.i(TAG, "解析运动数据 日期 = " + BleTools.getDate(data));

            if (!BleTools.isRightfulnessTime(BleTools.getDate(data))) {
                // 写入错误的数据到error
                SysUtils.logErrorDataI(TAG, BleTools.getDate(data) + "  byte=" + BleTools.bytes2HexString(data));
                return;
            }

            MovementInfo mMovementInfo = MovementInfo.getMontionModle(data);
            MyLog.i(TAG, "解析运动数据 mMovementInfo = " + mMovementInfo.toString());

            if (mMovementInfo != null && BtSerializeation.checkDeviceDate(mMovementInfo.getDate())) {

                boolean isNull = mMovementInfoUtils.MyQuestDataInNull(mMovementInfo);
                GoogleFitManager.getInstance().postGooglefitData(mMovementInfo);

                if (isNull) {
                    MyLog.i(TAG, "插入运动表 = 数据改变更新数据");
                    boolean isSuccess = mMovementInfoUtils.MyUpdateData(mMovementInfo);
                    if (isSuccess) {
                        MyLog.i(TAG, "插入运动表成功！");
                    } else {
                        MyLog.i(TAG, "插入运动表失败！");
                    }
                } else {
                    MyLog.i(TAG, "插入运动表 = 数据未改变");
                }
            } else {
                MyLog.i(TAG, "ble 得到 运动 mBleMotionModle = null 或者 日期不合法");
            }
        }
    }


    /**
     * 处理睡眠数据
     *
     * @param data
     */
    public static void HandleSleep(SleepInfoUtils mSleepInfoUtils, byte[] data) {

        boolean isCheck = BleTools.checkData(data);

        MyLog.i(TAG, "蓝牙回调 - 睡眠数据 校验结果 = " + isCheck);

        if (isCheck) {

            MyLog.i(TAG, "解析睡眠数据 日期 = " + MyTime.getBeforeDay(BleTools.getDate(data)));
            if (!BleTools.isRightfulnessTime(BleTools.getDate(data))) {
                // 写入错误的数据到error
                SysUtils.logErrorDataI(TAG, BleTools.getDate(data) + "  byte=" + BleTools.bytes2HexString(data));
                return;
            }
            SleepInfo mSleepInfo = SleepInfo.getSleepInfo(data);

            if (mSleepInfo != null && BtSerializeation.checkDeviceDate(mSleepInfo.getDate())) {

                MyLog.i(TAG, "睡眠数据 = mSleepInfo = " + mSleepInfo.toString());

                boolean isNull = mSleepInfoUtils.MyQuestDataInNull(mSleepInfo);

                if (isNull) {
                    MyLog.i(TAG, "插入睡眠表 = 数据不存在");
                    boolean isSuccess = mSleepInfoUtils.MyUpdateData(mSleepInfo);
                    if (isSuccess) {
                        MyLog.i(TAG, "插入睡眠表成功！");
                    } else {
                        MyLog.i(TAG, "插入睡眠表失败！");
                    }
                } else {
                    MyLog.i(TAG, "插入睡眠表 = 数据已存在");
                }

                if (BleTools.isSleepSuccessTwo(mSleepInfo)) {
                } else {
                    MyLog.i(TAG, "插入睡眠表 = 数据不合法");
                }

            } else {
                MyLog.i(TAG, "ble 得到 睡眠 mBleSleepModle = null 或者 日期不合法");
            }
        }
    }

    /**
     * 处理整点心率数据
     *
     * @param data
     */
    public static void HandlePoHeart(HeartInfoUtils mHeartInfoUtils, byte[] data) {

        boolean isCheck = BleTools.checkData(data);
        MyLog.i(TAG, "蓝牙回调 - 整点心率数据 校验结果 = " + isCheck);

        if (isCheck) {

            MyLog.i(TAG, "解析整点心率数据 日期 = " + BleTools.getDate(data));
            if (!BleTools.isRightfulnessTime(BleTools.getDate(data))) {
                // 写入错误的数据到error
                SysUtils.logErrorDataI(TAG, BleTools.getDate(data) + "  byte=" + BleTools.bytes2HexString(data));
                return;
            }
            HeartInfo mHeartInfo = HeartInfo.getPoHeartInfo(data);

            MyLog.i(TAG, "解析整点心率数据 mHeartInfo = " + mHeartInfo.toString());

            if (mHeartInfo != null && BtSerializeation.checkDeviceDate(mHeartInfo.getDate())) {

                boolean isNull = mHeartInfoUtils.MyQuestDataInNull(mHeartInfo);
                if (isNull) {
                    MyLog.i(TAG, "插入整点心率表 = 数据不存在");
                    boolean isSuccess = mHeartInfoUtils.MyUpdateData(mHeartInfo);
                    if (isSuccess) {
                        MyLog.i(TAG, "插入整点心率表成功！");
                    } else {
                        MyLog.i(TAG, "插入整点心率表失败！");
                    }
                } else {
                    MyLog.i(TAG, "插入整点心率表 = 数据已存在");
                }

            } else {
                MyLog.i(TAG, "ble 得到整点心率 mBleHeartModle = null 或者 日期不合法");
            }
        }
    }

    /**
     * 处理连续心率数据
     *
     * @param data
     */
    public static void HandleWoHeart(HeartInfoUtils mHeartInfoUtils, byte[] data) {

        boolean isCheck = BleTools.checkData(data);
        MyLog.i(TAG, "蓝牙回调 - 连续心率数据 校验结果 = " + isCheck);

        if (isCheck) {
            MyLog.i(TAG, "蓝牙回调 ble 同步数据 解析连续心率数据= 日期 =  " + BleTools.getDate(data));
            if (!BleTools.isRightfulnessTime(BleTools.getDate(data))) {
                // 写入错误的数据到error
                SysUtils.logErrorDataI(TAG, BleTools.getDate(data) + "  byte=" + BleTools.bytes2HexString(data));
                return;
            }
            HeartInfo mHeartInfo = HeartInfo.getWoHeartInfo(data);

            if (mHeartInfo != null && BtSerializeation.checkDeviceDate(mHeartInfo.getDate())) {

                MyLog.i(TAG, "蓝牙回调 连续心率 mHeartInfo = " + mHeartInfo);

                boolean isNull = mHeartInfoUtils.MyQuestDataInNull(mHeartInfo);
                if (isNull) {
                    MyLog.i(TAG, "插入连续心率表 = 数据不存在");
                    boolean isSuccess = mHeartInfoUtils.MyUpdateData(mHeartInfo);
                    if (isSuccess) {
                        MyLog.i(TAG, "插入连续心率表成功！");
                    } else {
                        MyLog.i(TAG, "插入连续心率表失败！");
                    }
                } else {
                    MyLog.i(TAG, "插入连续心率表 = 数据已存在");
                }
            } else {
                MyLog.i(TAG, "ble 得到连续心率 mBleHeartModle = null 或者 日期不合法");
            }
        }
    }


    /**
     * 处理设备信息
     *
     * @param data
     */
    public static void handleDeviceInfo(BleService mBleService, BleDeviceTools mBleDeviceTools, byte[] data) {

        int DevicePower = data[13] & 0xff;
//        int DeviceType = data[14] & 0xff;
        int deviceType = ((data[7] & 0xff) << 8) | data[14] & 0xff;
        int DeviceVersion = data[15] & 0xff;
        String DeviceName = mBleDeviceTools.get_ble_name();
        String DeviceMac = mBleDeviceTools.get_ble_mac();

        MyLog.i(TAG, "固件返回值 = DevicePower = " + DevicePower);

        mBleDeviceTools.set_ble_device_power(DevicePower);

        int[] deviceParams4 = MyUtils.BinstrToIntArray(data[16]);
        int[] deviceParams5 = MyUtils.BinstrToIntArray(data[17]);
        int[] deviceParams6 = MyUtils.BinstrToIntArray(data[18]);
        int[] deviceParams7 = MyUtils.BinstrToIntArray(data[19]);
        int[] deviceParams8 = MyUtils.BinstrToIntArray(data[20]);
        int[] deviceParams9 = MyUtils.BinstrToIntArray(data[21]);
        int[] deviceParams10 = MyUtils.BinstrToIntArray(data[22]); // 参数10

//        for (int i = 0; i < result_data.length; i++) {
//            MyLog.i(TAG, "固件返回值 = i = " + i + "  result_data = " + result_data[i]);
//        }

        if (deviceParams4[0] == 0) {
            MyLog.i(TAG, "固件返回值 = ECG 频率 = 125");
            mBleDeviceTools.set_ecg_frequency(0);
        } else {
            MyLog.i(TAG, "固件返回值 = ECG 频率 = 250");
            mBleDeviceTools.set_ecg_frequency(1);
        }


        if (deviceParams4[1] == 0 && deviceParams4[2] == 0) {
            MyLog.i(TAG, "固件返回值 = ECG = 有");
            MyLog.i(TAG, "固件返回值 = PPG = 无");
            mBleDeviceTools.set_is_support_ecg(1);
            mBleDeviceTools.set_is_support_ppg(0);
        } else if (deviceParams4[1] == 0 && deviceParams4[2] == 1) {
            MyLog.i(TAG, "固件返回值 = ECG = 有");
            MyLog.i(TAG, "固件返回值 = PPG = 有");
            mBleDeviceTools.set_is_support_ecg(1);
            mBleDeviceTools.set_is_support_ppg(1);
        } else if (deviceParams4[1] == 1 && deviceParams4[2] == 0) {
            MyLog.i(TAG, "固件返回值 = ECG = 无");
            MyLog.i(TAG, "固件返回值 = PPG = 有");
            mBleDeviceTools.set_is_support_ecg(0);
            mBleDeviceTools.set_is_support_ppg(1);
        } else if (deviceParams4[1] == 1 && deviceParams4[2] == 1) {
            MyLog.i(TAG, "固件返回值 = ECG = 无");
            MyLog.i(TAG, "固件返回值 = PPG = 无");
            mBleDeviceTools.set_is_support_ecg(0);
            mBleDeviceTools.set_is_support_ppg(0);
        }

        if (deviceParams4[3] == 0) {
            MyLog.i(TAG, "固件返回值 = PPG 频率 = 25");
            mBleDeviceTools.set_ppg_frequency(0);
        } else {
            mBleDeviceTools.set_ppg_frequency(1);
            MyLog.i(TAG, "固件返回值 = PGG 频率 = 不知道？");
        }

        if (deviceParams4.length >= 8) {

            if (deviceParams4[5] == 1) {
                MyLog.i(TAG, "固件返回值 = 微信运动 = 不支持 = (“版本号大于等于23”则且“微信标志位不支持”)=还是不支持");
                mBleDeviceTools.set_is_support_wx_sport(false);
            } else {
                MyLog.i(TAG, "固件返回值 = 微信运动 = 支持 = （“版本号大于等于23”则且“微信标志位不支持”)=支持");
                mBleDeviceTools.set_is_support_wx_sport(true);
            }

            if (DeviceVersion != 0) {
                if (DeviceVersion < 15) {
                    mBleDeviceTools.set_step_algorithm_type(0);
                    MyLog.i(TAG, "固件返回值 = 计步算法 = 算法0");
                } else {
                    if (deviceParams4[6] == 1) {
                        MyLog.i(TAG, "固件返回值 = 计步算法 = 算法2");
                        mBleDeviceTools.set_step_algorithm_type(2);
                        mBleDeviceTools.set_device_step_algorithm(1);
                    } else {
                        mBleDeviceTools.set_step_algorithm_type(1);
                        mBleDeviceTools.set_device_step_algorithm(0);
                        MyLog.i(TAG, "固件返回值 = 计步算法 = 算法1");
                    }
                }
            }

            if (deviceParams4[7] == 1) {
                mBleDeviceTools.set_notiface_type(1);
                MyLog.i(TAG, "固件返回值 = 通知类型 = 推送类型2");
            } else {
                mBleDeviceTools.set_notiface_type(0);
                MyLog.i(TAG, "固件返回值 = 通知类型 = 推送类型1");
            }

        }

        if (deviceParams5[0] == 1) {
            mBleDeviceTools.set_is_support_blood(1);
            MyLog.i(TAG, "固件返回值 = 血压 = 不支持");

        } else {
            mBleDeviceTools.set_is_support_blood(0);
            MyLog.i(TAG, "固件返回值 = 血压 = 支持");
        }

        if (deviceParams5[1] == 1) {
            mBleDeviceTools.set_is_support_persist_heart(1);
            MyLog.i(TAG, "固件返回值 = 心率 = 连续心率");

        } else {
            mBleDeviceTools.set_is_support_persist_heart(0);
            MyLog.i(TAG, "固件返回值 = 心率 = 整点心率");
        }


        if (deviceParams5[2] == 1) {
            MyLog.i(TAG, "固件返回值 传图片 = 支持");
            mBleDeviceTools.set_is_screen_saver(true);

            //获取屏保信息-加延迟
            mBleService.getDerviceScreensaverInfoDelayTime(600);


        } else {
            MyLog.i(TAG, "固件返回值 传图片 = 不支持");
            mBleDeviceTools.set_is_screen_saver(false);
        }

        if (deviceParams5[3] == 1) {
            MyLog.i(TAG, "固件返回值 Flash = 可用");
            mBleDeviceTools.set_screensaver_falsh(1);

        } else {
            MyLog.i(TAG, "固件返回值 Flash = 不可用");
            mBleDeviceTools.set_screensaver_falsh(0);
        }

        int deviceHheme = (data[17] << 4 >> 5 & 0x07);
        MyLog.i(TAG, "固件返回值 = 屏保个数 = " + deviceHheme);
        mBleDeviceTools.set_theme_count(deviceHheme);

        int is_support_screen = (data[17] & 0x01);
        MyLog.i(TAG, "固件返回值 = 是否支持屏幕设置 = " + is_support_screen);

        if (is_support_screen == 1) {
            mBleDeviceTools.set_is_support_screen(true);
        } else {
            mBleDeviceTools.set_is_support_screen(false);
        }

        if (deviceParams6[0] == 1) {
            MyLog.i(TAG, "固件返回值 = 天气 = 支持");
            mBleDeviceTools.set_is_weather(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 天气 = 不支持");
            mBleDeviceTools.set_is_weather(false);
        }
        if (deviceParams6[1] == 1) {
            MyLog.i(TAG, "固件返回值 = 扫描方式 = 竖向");
            mBleDeviceTools.set_is_scanf_type(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 扫描方式 = 横向");
            mBleDeviceTools.set_is_scanf_type(false);
        }

        if (deviceParams6[2] == 1) {
            MyLog.i(TAG, "固件返回值 = 生理周期 = 支持");
            mBleDeviceTools.set_device_is_cycle(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 生理周期 = 不支持");
            mBleDeviceTools.set_device_is_cycle(false);
        }

        if (deviceParams6[3] == 0 && deviceParams6[4] == 0) {
            MyLog.i(TAG, "离线运动模式 = 不支持");
            mBleDeviceTools.set_device_is_off_sport(false);
            mBleDeviceTools.set_device_off_sport_type(1);
        } else if (deviceParams6[3] == 0 && deviceParams6[4] == 1) {
            MyLog.i(TAG, "离线运动模式 = 支持 协议1=01");
            mBleDeviceTools.set_device_is_off_sport(true);
            mBleDeviceTools.set_device_off_sport_type(1);
        } else if (deviceParams6[3] == 1 && deviceParams6[4] == 0) {
            MyLog.i(TAG, "离线运动模式 = 支持 协议2=10");
            mBleDeviceTools.set_device_is_off_sport(true);
            mBleDeviceTools.set_device_off_sport_type(2);
        } else if (deviceParams6[3] == 1 && deviceParams6[4] == 1) {
            MyLog.i(TAG, "离线运动模式 = 支持 协议3=11");
            mBleDeviceTools.set_device_is_off_sport(true);
            mBleDeviceTools.set_device_off_sport_type(3);
        }

        if (deviceParams6[5] == 1) {
            MyLog.i(TAG, "固件返回值 = 睡眠偏好 = 支持");
            mBleDeviceTools.set_device_is_sleep_time(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 睡眠偏好 = 不支持");
            mBleDeviceTools.set_device_is_sleep_time(false);
        }

        if (deviceParams6[6] == 1) {
            MyLog.i(TAG, "固件返回值 = 勿扰模式 = 支持");
            mBleDeviceTools.set_device_is_not_disrub(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 勿扰模式 = 不支持");
            mBleDeviceTools.set_device_is_not_disrub(false);
        }

        if (deviceParams6[7] == 1) {
            MyLog.i(TAG, "固件返回值 = 心率间隔 = 支持");
            mBleDeviceTools.set_device_is_interval_hr(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 心率间隔 = 不支持");
            mBleDeviceTools.set_device_is_interval_hr(false);
        }

        if (deviceParams7[0] == 1) {
            MyLog.i(TAG, "固件返回值 = PPG校准 = 支持");
            mBleDeviceTools.set_device_is_ppg_hr_jiaozhun(true);
        } else {
            MyLog.i(TAG, "固件返回值 = PPG校准 = 不支持");
            mBleDeviceTools.set_device_is_ppg_hr_jiaozhun(false);
        }

        int app_type = 0;

        if (deviceParams7[1] == 1) {
            app_type += 8;
        }

        if (deviceParams7[2] == 1) {
            app_type += 4;
        }

        if (deviceParams7[3] == 1) {
            app_type += 2;
        }

        if (deviceParams7[4] == 1) {
            app_type += 1;
        }

        mBleDeviceTools.set_device_notice_type(app_type);
        MyLog.i(TAG, "固件返回值 = APP推送类型 = " + app_type);

        if (deviceParams7[5] == 1) {
            MyLog.i(TAG, "固件返回值 = 目标距离 = 支持");
            mBleDeviceTools.set_device_is_distance_target(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 目标距离 = 不支持");
            mBleDeviceTools.set_device_is_distance_target(false);
        }

        if (deviceParams7[6] == 1) {
            MyLog.i(TAG, "固件返回值 = 目标卡路里 = 支持");
            mBleDeviceTools.set_device_is_calorie_target(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 目标卡路里 = 不支持");
            mBleDeviceTools.set_device_is_calorie_target(false);
        }

        if (deviceParams7[7] == 1) {
            MyLog.i(TAG, "固件返回值 = 温度单位设置 = 支持");
            mBleDeviceTools.set_device_temperature_unit(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 温度单位设置 = 不支持");
            mBleDeviceTools.set_device_temperature_unit(false);
        }

        if (deviceParams8[0] == 1) {
            MyLog.i(TAG, "固件返回值 = 闹钟是否有效(开启免打扰) = 支持");
            mBleDeviceTools.set_device_dont_disturb_is_clock(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 闹钟是否有效(开启免打扰) = 不支持");
            mBleDeviceTools.set_device_dont_disturb_is_clock(false);
        }


        //000 = 不支持
        if (deviceParams8[1] == 0 && deviceParams8[2] == 0 && deviceParams8[3] == 0) {
            MyLog.i(TAG, "固件返回值 = 是否支持主题传输及主题传输版本 = 不支持");
            mBleDeviceTools.set_device_is_theme_transmission(false);
            mBleDeviceTools.set_device_theme_transmission_version(0);
        } else if (deviceParams8[1] == 0 && deviceParams8[2] == 0 && deviceParams8[3] == 1) {
            MyLog.i(TAG, "固件返回值 = 是否支持主题传输及主题传输版本 = 支持 = 版本1");
            mBleDeviceTools.set_device_is_theme_transmission(true);
            mBleDeviceTools.set_device_theme_transmission_version(1);
        } else if (deviceParams8[1] == 0 && deviceParams8[2] == 1 && deviceParams8[3] == 0) {
            MyLog.i(TAG, "固件返回值 = 是否支持主题传输及主题传输版本 = 不支持 = 版本2");
            mBleDeviceTools.set_device_is_theme_transmission(false);
            mBleDeviceTools.set_device_theme_transmission_version(2);
        } else if (deviceParams8[1] == 0 && deviceParams8[2] == 1 && deviceParams8[3] == 1) {
            MyLog.i(TAG, "固件返回值 = 是否支持主题传输及主题传输版本 = 不支持 = 版本3");
            mBleDeviceTools.set_device_is_theme_transmission(false);
            mBleDeviceTools.set_device_theme_transmission_version(3);
        } else {
            MyLog.i(TAG, "固件返回值 = 是否支持主题传输及主题传输版本 = 不支持");
            mBleDeviceTools.set_device_is_theme_transmission(false);
            mBleDeviceTools.set_device_theme_transmission_version(0);
        }

        if (deviceParams8[4] == 1) {
            MyLog.i(TAG, "固件返回值 = 是否支持音乐控制 = 支持");
            mBleDeviceTools.set_device_music_control(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 是否支持音乐控制 = 不支持");
            mBleDeviceTools.set_device_music_control(false);
        }

        //000 = 不支持
        if (deviceParams8[5] == 0 && deviceParams8[6] == 0 && deviceParams8[7] == 0) {
            MyLog.i(TAG, "固件返回值 = 蓝牙平台 = nordic");
            mBleDeviceTools.set_device_platform_type(0);
        } else if (deviceParams8[5] == 0 && deviceParams8[6] == 0 && deviceParams8[7] == 1) {
            MyLog.i(TAG, "固件返回值 = 蓝牙平台 = Realtek");
            mBleDeviceTools.set_device_platform_type(1);
        } else if (deviceParams8[5] == 0 && deviceParams8[6] == 1 && deviceParams8[7] == 0) {
            MyLog.i(TAG, "固件返回值 = 蓝牙平台 = dialog");
            mBleDeviceTools.set_device_platform_type(2);
        } else {
            MyLog.i(TAG, "固件返回值 = 蓝牙平台 = nordic");
            mBleDeviceTools.set_device_platform_type(0);
        }

        //000 = 不支持
        if (deviceParams9[0] == 0 && deviceParams9[1] == 0) {
            MyLog.i(TAG, "固件返回值 = 音频传输 = 不支持");
            mBleDeviceTools.set_device_is_music_transmission(false);
            mBleDeviceTools.set_device_music_transmission_version(0);
        } else if (deviceParams9[0] == 0 && deviceParams9[1] == 1) {
            MyLog.i(TAG, "固件返回值 = 音频传输 = 支持");
            mBleDeviceTools.set_device_is_music_transmission(true);
            mBleDeviceTools.set_device_music_transmission_version(1);
        } else {
            MyLog.i(TAG, "固件返回值 = 音频传输 = 不支持");
            mBleDeviceTools.set_device_is_music_transmission(false);
            mBleDeviceTools.set_device_music_transmission_version(0);
        }

        if (deviceParams9[2] == 1) {
            MyLog.i(TAG, "固件返回值 = 是否支持勿扰时间段(新的) = 支持");
            mBleDeviceTools.set_device_is_time_disturb(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 是否支持勿扰时间段(新的) = 不支持");
            mBleDeviceTools.set_device_is_time_disturb(false);
        }

        if (deviceParams9[3] == 1) {
            MyLog.i(TAG, "固件返回值 = 是否支持勿扰功能 = 不支持");
            mBleDeviceTools.set_device_is_disturb(false);
        } else {
            MyLog.i(TAG, "固件返回值 = 是否支持勿扰功能 = 支持");
            mBleDeviceTools.set_device_is_disturb(true);
        }

        if (deviceParams9[4] == 1) {
            MyLog.i(TAG, "固件返回值 = 是否支持联系人功能 = 支持");
            mBleDeviceTools.set_is_support_mail_list(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 是否支持联系人功能 = 不支持");
            mBleDeviceTools.set_is_support_mail_list(false);
        }

        if (deviceParams9[5] == 1) {
            MyLog.i(TAG, "固件返回值 = 是否支持血氧 = 支持");
            mBleDeviceTools.set_is_support_spo2(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 是否支持血氧 = 不支持");
            mBleDeviceTools.set_is_support_spo2(false);
        }

        if (deviceParams9[6] == 1) {
            MyLog.i(TAG, "固件返回值 = 是否支持体温 = 支持");
            mBleDeviceTools.set_is_support_temp(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 是否支持体温 = 不支持");
            mBleDeviceTools.set_is_support_temp(false);
        }
        if (deviceParams9[7] == 1) {
            MyLog.i(TAG, "固件返回值 = 是否支持整点血氧 = 支持");
            mBleDeviceTools.setSupportWholeBloodOxygen(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 是否支持整点血氧 = 不支持");
            mBleDeviceTools.setSupportWholeBloodOxygen(false);
        }
        if (deviceParams10[0] == 1) {
            MyLog.i(TAG, "固件返回值 = 是否支持连续血氧 = 支持");
            mBleDeviceTools.setSupportContinuousBloodOxygen(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 是否支持连续血氧 = 不支持");
            mBleDeviceTools.setSupportContinuousBloodOxygen(false);
        }

        if (deviceParams10[1] == 1) {
            MyLog.i(TAG, "固件返回值 = 是否支持整点体温 = 支持");
            mBleDeviceTools.setSupportWholeTemp(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 是否支持整点体温 = 不支持");
            mBleDeviceTools.setSupportWholeTemp(false);
        }
        if (deviceParams10[2] == 1) {
            MyLog.i(TAG, "固件返回值 = 是否支持连续体温 = 支持");
            mBleDeviceTools.setSupportContinuousTemp(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 是否支持连续体温 = 不支持");
            mBleDeviceTools.setSupportContinuousTemp(false);
        }
        if (deviceParams10[3] == 1) {
            MyLog.i(TAG, "固件返回值 = 是否支持离线血氧 = 支持");
            mBleDeviceTools.setSupportOfflineBloodOxygen(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 是否支持离线血氧 = 不支持");
            mBleDeviceTools.setSupportOfflineBloodOxygen(false);
        }

        if (deviceParams10[4] == 1) {
            MyLog.i(TAG, "固件返回值 = 是否支持离线体温 = 支持");
            mBleDeviceTools.setSupportOfflineTemp(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 是否支持离线体温 = 不支持");
            mBleDeviceTools.setSupportOfflineTemp(false);
        }

        if (deviceParams10[5] == 1) {
            MyLog.i(TAG, "固件返回值 = 新的设备类型规则 = 支持");
            mBleDeviceTools.setSupportNewDeviceType(true);

//            AB 00 00 12 00 00 00 00 03 00
//            08 00 0D 32 19 43 50 00 10
//            11 14 14
//
//            27 00 00 5E
            // 新规则
            String deviceParams13 = Integer.toHexString(data[25] & 0xff);
            String deviceParams12 = Integer.toHexString(data[24] & 0xff);
            String deviceParams11 = Integer.toHexString(data[23] & 0xff);


            String deviceParams2 = Integer.toHexString(data[14] & 0xff);
            deviceType = Integer.parseInt(deviceParams13 + deviceParams12 + deviceParams11 + deviceParams2, 16);
            MyLog.i(TAG, "固件返回值 = deviceType = " + deviceType);


        } else {
            MyLog.i(TAG, "固件返回值 = 新的设备类型规则 = 不支持");
            mBleDeviceTools.setSupportNewDeviceType(false);
        }


        if (deviceParams10[6] == 1) {
            MyLog.i(TAG, "固件返回值 = 校验方式（无效） = 新规则");
//            mBleDeviceTools.setSupportNewDeviceCrc(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 校验方式 (无效)= 老规则");
//            mBleDeviceTools.setSupportNewDeviceCrc(false);
        }
        if (deviceParams10[7] == 1) {
            MyLog.i(TAG, "固件返回值 = 小版本号 = 支持");
            mBleDeviceTools.setSupporDeviceSubVersion(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 小版本号 = 不支持");
            mBleDeviceTools.setSupporDeviceSubVersion(false);
        }

        int[] deviceParams14 = MyUtils.BinstrToIntArray(data[26]); // 参数14
        //支持proto
        if (deviceParams14[0] == 1) {
            MyLog.i(TAG, "固件返回值 = protobuf = 支持");
            mBleDeviceTools.setIsSupportProtobuf(true);

            //支持打点运动
            if (deviceParams14[1] == 1) {
                MyLog.i(TAG, "固件返回值 = 打点多运动 = 支持");
                mBleDeviceTools.setPointExercise(true);
            } else {
                MyLog.i(TAG, "固件返回值 = 打点多运动 = 不支持");
                mBleDeviceTools.setPointExercise(false);
            }

            if(deviceParams14[3] == 1){
                MyLog.i(TAG, "固件返回值 = 卡片排序 = 支持");
                mBleDeviceTools.setIsSupportPageDevice(true);
            } else {
                MyLog.i(TAG, "固件返回值 = 卡片排序 = 不支持");
                mBleDeviceTools.setIsSupportPageDevice(false);
            }

            if(deviceParams14[4] == 1){
                MyLog.i(TAG, "固件返回值 = GPS运动 = 支持");
                mBleDeviceTools.setIsSupportGpsSport(true);
            } else {
                MyLog.i(TAG, "固件返回值 = GPS运动 = 不支持");
                mBleDeviceTools.setIsSupportGpsSport(false);
            }
            if(deviceParams14[5] == 1){
                MyLog.i(TAG, "固件返回值 = GPS传感器 = 支持");
                mBleDeviceTools.setIsGpsSensor(true);
            } else {
                MyLog.i(TAG, "固件返回值 = GPS传感器 = 不支持");
                mBleDeviceTools.setIsGpsSensor(false);
            }
            if(deviceParams14[6] == 1){
                MyLog.i(TAG, "固件返回值 = 内置表盘选择 = 支持");
                mBleDeviceTools.setIsSupportBuiltDialSelection(true);
            } else {
                MyLog.i(TAG, "固件返回值 = 内置表盘选择 = 不支持");
                mBleDeviceTools.setIsSupportBuiltDialSelection(false);
            }

        } else {
            MyLog.i(TAG, "固件返回值 = protobuf = 不支持 2");
            mBleDeviceTools.setIsSupportProtobuf(false);

            MyLog.i(TAG, "固件返回值 = 打点多运动 = 不支持 2");
            mBleDeviceTools.setPointExercise(false);

            MyLog.i(TAG, "固件返回值 = 卡片排序 = 不支持 2");
            mBleDeviceTools.setIsSupportPageDevice(false);

            MyLog.i(TAG, "固件返回值 = GPS运动 = 不支持 2");
            mBleDeviceTools.setIsSupportGpsSport(false);

            MyLog.i(TAG, "固件返回值 = GPS传感器 = 不支持 2");
            mBleDeviceTools.setIsGpsSensor(false);

            MyLog.i(TAG, "固件返回值 = 内置表盘选择 = 不支持 2");
            mBleDeviceTools.setIsSupportBuiltDialSelection(false);

        }
        //升级方式
        if (deviceParams14[2] == 1) {
            MyLog.i(TAG, "固件返回值 = 升级方式 = probuf应用层");
            mBleDeviceTools.setDeviceUpdateType(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 升级方式 = 官方（默认）");
            mBleDeviceTools.setDeviceUpdateType(false);
        }

        int deviceParams15 = data[27] & 0xff; // 参数15
        int subVersion = deviceParams15;//小版本号
        MyLog.i(TAG, "固件返回值 = 小版本号 = " + subVersion);

        int[] deviceParams16 = MyUtils.BinstrToIntArray(data[28]); // 参数16
        //体温间隔
        if (deviceParams16[0] == 1) {
            MyLog.i(TAG, "固件返回值 = 体温间隔 = 支持");
            mBleDeviceTools.setIsTempMeasurementInterval(true);
        } else {
            MyLog.i(TAG, "固件返回值 = 体温间隔 = 不支持");
            mBleDeviceTools.setIsTempMeasurementInterval(false);
        }
        //Corona-Warn-App
        if (deviceParams16[1] == 1) {
            MyLog.i(TAG, "固件返回值 = Corona-Warn-App = 支持");
            mBleDeviceTools.setIsCoronaNotiface(true);
        } else {
            MyLog.i(TAG, "固件返回值 = Corona-Warn-App = 不支持");
            mBleDeviceTools.setIsCoronaNotiface(false);
        }

        MyLog.i(TAG, "固件返回值 = DeviceName = " + DeviceName);
        MyLog.i(TAG, "固件返回值 = DeviceMac = " + DeviceMac);
        MyLog.i(TAG, "固件返回值 = DevicePower = " + DevicePower);
        MyLog.i(TAG, "固件返回值 = DeviceVersion = " + DeviceVersion);
        MyLog.i(TAG, "固件返回值 = DeviceType = " + deviceType);
        MyLog.i(TAG, "固件返回值 = data lenght = " + data.length);
        MyLog.i(TAG, "固件返回值 = data lenght result_two_data = " + deviceParams5.length);

        if (!JavaUtil.checkIsNull(DeviceMac) && !JavaUtil.checkIsNull(DeviceName) && deviceType != 0 & DeviceVersion != 0) {
            if (deviceType >= 1 && DeviceVersion >= 1) {
                mBleDeviceTools.set_ble_device_version(DeviceVersion);
                mBleDeviceTools.set_ble_device_type(deviceType);
                mBleDeviceTools.set_ble_device_sub_version(subVersion);
            }
            //上传手环版本号
            requestServerTools.uploadBraceletVersion();
        }

        //发送广播，设备信息已处理完毕，需要对应的界面，更新UI
//        mBleService.broadcastUpdateNew(BleConstant.ACTION_DATA_DEVICE_END);
        BroadcastTools.broadcastDeviceComplete(mBleService.getApplicationContext());
    }


    public static void handleScreensaverInfo(BleDeviceTools mBleDeviceTools, byte[] data) {

        int screensaverWidth = data[14] & 0xff;
        int screensaverHeight = data[16] & 0xff;
        int timeWidth = data[17] & 0xff;
        int timeHeight = data[18] & 0xff;
        int[] result_data = MyUtils.BinstrToIntArray(data[19]);


        MyLog.i(TAG, "屏保返回值 = screensaverWidth = " + screensaverWidth);
        MyLog.i(TAG, "屏保返回值 = screensaverHeight = " + screensaverHeight);
        MyLog.i(TAG, "屏保返回值 = timeWidth = " + timeWidth);
        MyLog.i(TAG, "屏保返回值 = timeHeight  = " + timeHeight);


        mBleDeviceTools.set_screensaver_resolution_width(screensaverWidth);
        mBleDeviceTools.set_screensaver_resolution_height(screensaverHeight);

        mBleDeviceTools.set_screensaver_time_width(timeWidth);
        mBleDeviceTools.set_screensaver_time_height(timeHeight);

        MyLog.i(TAG, "屏保返回值 = result_data[0]  = " + result_data[0]);
        MyLog.i(TAG, "屏保返回值 = result_data[1]  = " + result_data[1]);
        MyLog.i(TAG, "屏保返回值 = result_data[2]  = " + result_data[2]);


        if (result_data[0] == 0 && result_data[1] == 0 && result_data[2] == 0) {
            MyLog.i(TAG, "屏保返回值 = 屏幕类型  = 方形");
            mBleDeviceTools.set_screen_shape(0);

        } else if (result_data[0] == 0 && result_data[1] == 0 && result_data[2] == 1) {
            MyLog.i(TAG, "屏保返回值 = 屏幕类型  = 球拍1");
            mBleDeviceTools.set_screen_shape(1);

//            int xx2 = (screensaverWidth - timeWidth) / 2;
//            int yy2 = (screensaverHeight - timeHeight) / 2;
//            MyLog.i(TAG, "屏保返回值 xx2 = " + xx2);
//            MyLog.i(TAG, "屏保返回值 yy2 = " + yy2);
//            mBleDeviceTools.set_screensaver_x_time(xx2);
//            mBleDeviceTools.set_screensaver_y_time(yy2);
//            mBleDeviceTools.set_screensaver_post_time(1);

            MyLog.i(TAG, "屏保返回值 = 屏幕类型  = 球拍2");

        } else if (result_data[0] == 0 && result_data[1] == 1 && result_data[2] == 0) {
            MyLog.i(TAG, "屏保返回值 = 屏幕类型  = 圆形");
            mBleDeviceTools.set_screen_shape(2);


            int xx2 = (screensaverWidth - timeWidth) / 2;
            int yy2 = (screensaverHeight - timeHeight) / 2;
            MyLog.i(TAG, "xx2 = " + xx2);
            MyLog.i(TAG, "yy2 = " + yy2);

            mBleDeviceTools.set_screensaver_x_time(xx2);
            mBleDeviceTools.set_screensaver_y_time(yy2);
            mBleDeviceTools.set_screensaver_post_time(0);
        }
    }

    /**
     * 多运动模式
     *
     * @param mSportModleInfoUtils
     * @param data
     */
    public static void handleDeviceSportInfo(SportModleInfoUtils mSportModleInfoUtils, byte[] data) {
        int count = data[15] & 0xff;
        MyLog.i(TAG, "多运动模式  = 个数 = " + count);
        List<SportModleInfo> sport_list = SportModleInfo.getBleSportModleList(data);
        if (sport_list != null && sport_list.size() > 0) {
            MyLog.i(TAG, "多运动模式 my_list = " + sport_list.toString());
            boolean isSuccess = mSportModleInfoUtils.insertInfoList(sport_list);
            MyLog.i(TAG, "多运动模式 插入多条数据 = isSuccess = " + isSuccess);
        }
    }

    /**
     * 处理离线心电数据
     */
    public static boolean HandleOffEcgData(BleService mBleservice, int ecgHr, int ecgPtpAvg, String OffLineEcgTime, StringBuilder OffLineEcgData, HealthInfoUtils mHealthInfoUtils) {
        HealthInfo mHealthInfo = HealthInfo.getOffHealthInfo(OffLineEcgTime, ecgHr, ecgPtpAvg, OffLineEcgData.toString());
        if (mHealthInfo != null) {

            if (!BleTools.isRightfulnessTime(OffLineEcgTime)) {
                // 写入错误的数据到error
                SysUtils.logErrorDataI(TAG, OffLineEcgTime + "  byte=OffEcg");
            }

            mBleservice.ResultMeasureHeart(mHealthInfo);
            MyLog.i(TAG, "离线心电数据 = mHealthInfo = " + mHealthInfo.toString());
            saveHealthDdata(mBleservice.getApplicationContext(), mHealthInfoUtils, mHealthInfo);
            return true;
        } else {
            MyLog.i(TAG, "离线心电数据 = mHealthInfo =null 对象为空，或者数据不合法");
            return false;
        }

    }


    /**
     * 保存单条健康数据
     *
     * @param mHealthInfo
     */
    public static void saveHealthDdata(Context context, HealthInfoUtils mHealthInfoUtils, HealthInfo mHealthInfo) {

        MyLog.i(TAG, "插入健康表 =  mHealthInfo = " + mHealthInfo.toString());
        boolean isSuccess_health = mHealthInfoUtils.MyUpdateData(mHealthInfo);
        if (isSuccess_health) {
            MyLog.i(TAG, "插入健康表成功！");
        } else {
            MyLog.i(TAG, "插入健康表失败！");
        }

        requestServerTools.updateHealthyData(context, mHealthInfoUtils, mHealthInfo);
    }

    /**
     * 离线血压
     *
     * @param data
     */
    public static void handleOffLineBpInfo(Context context, HealthInfoUtils mHealthInfoUtils, byte[] data) {

        int count = data[15] & 0xff;
        MyLog.i(TAG, "离线血压  = 个数 = " + count);
        List<HealthInfo> health_list = HealthInfo.getHealthInfoList(data);
        if (health_list != null && health_list.size() > 0) {
            MyLog.i(TAG, "离线血压 my_list  = " + health_list.toString());
            for (int i = 0; i < health_list.size(); i++) {
                MyLog.i(TAG, "离线血压 my_list  i = " + i + "  health = " + health_list.get(i).toString());
            }
            saveListHealthData(context, mHealthInfoUtils, health_list);
        } else {
            MyLog.i(TAG, "离线血压 my_list  = null");
        }
    }


    /**
     * 保存多条健康数据
     */
    public static void saveListHealthData(Context context, HealthInfoUtils mHealthInfoUtils, List<HealthInfo> health_list) {
        MyLog.i(TAG, "插入健康表 - 多条 =  mHealthInfo = " + health_list.toString());
        boolean isSuccess = mHealthInfoUtils.insertInfoList(health_list);
        if (isSuccess) {
            MyLog.i(TAG, "插入健康表成功！");
        } else {
            MyLog.i(TAG, "插入健康失败！");
        }
    }


    /**
     * 处理主题传输信息-存储
     *
     * @param data
     */
    public static void handleDeviceThemeTransmissionInfo(BleDeviceTools mBleDeviceTools, byte[] data) {
//        int count = data[15] & 0xff;
//        MyLog.i(TAG, "处理主题传输信息  = 个数 = " + count);

        int[] result_data = MyUtils.BinstrToIntArray(data[13]);

        if (result_data[0] == 0 && result_data[1] == 0 && result_data[2] == 0) {
            MyLog.i(TAG, "主题传输信息 = 屏幕类型  = 方形");
            mBleDeviceTools.set_device_theme_shape(0);
        } else if (result_data[0] == 0 && result_data[1] == 0 && result_data[2] == 1) {
            MyLog.i(TAG, "主题传输信息 = 屏幕类型  = 球拍");
            mBleDeviceTools.set_device_theme_shape(1);
        } else if (result_data[0] == 0 && result_data[1] == 1 && result_data[2] == 0) {
            MyLog.i(TAG, "主题传输信息 = 屏幕类型  = 圆形");
            mBleDeviceTools.set_device_theme_shape(2);
        } else if (result_data[0] == 0 && result_data[1] == 1 && result_data[2] == 1) {
            MyLog.i(TAG, "主题传输信息 = 屏幕类型  = 圆角矩形1");
            mBleDeviceTools.set_device_theme_shape(3);
        }

        if (result_data[3] == 1) {
            MyLog.i(TAG, "主题传输信息 = 扫描方式  = 垂直扫描(大字库)");
            mBleDeviceTools.set_device_theme_scanning_mode(true);
        } else {
            MyLog.i(TAG, "主题传输信息 = 扫描方式  = 水平扫描(小字库)");
            mBleDeviceTools.set_device_theme_scanning_mode(false);
        }

        if (result_data[4] == 1) {
            MyLog.i(TAG, "主题传输信息 = 是否支持心率 = true");
            mBleDeviceTools.set_device_theme_is_support_heart(true);
        } else {
            MyLog.i(TAG, "主题传输信息 = 是否支持心率 = false");
            mBleDeviceTools.set_device_theme_is_support_heart(false);
        }

        int width = ((data[14] & 0xff) << 8) | (data[15] & 0xff);
        int height = ((data[16] & 0xff) << 8) | (data[17] & 0xff);


        MyLog.i(TAG, "主题传输信息 = 分辨率 宽度  = " + width);
        MyLog.i(TAG, "主题传输信息 = 分辨率 高度  = " + height);

        mBleDeviceTools.set_device_theme_resolving_power_width(width);
        mBleDeviceTools.set_device_theme_resolving_power_height(height);


//        int default_lan = data[18] & 0xff;
//
//        mBleDeviceTools.set_device_theme_default_lan(default_lan);
//
//
//        MyLog.i(TAG, "主题传输信息 = 默认语言  = " + default_lan);
//
//        int support_lan_data = ((data[19] & 0xff) << 24) | ((data[20] & 0xff) << 16) | ((data[21] & 0xff) << 8) | (data[22] & 0xff);
//
//        mBleDeviceTools.set_device_theme_support_lan_data(support_lan_data);
//
//        int[] result_lan1 = MyUtils.BinstrToIntArray(data[19]);
//        int[] result_lan2 = MyUtils.BinstrToIntArray(data[20]);
//        int[] result_lan3 = MyUtils.BinstrToIntArray(data[21]);
//        int[] result_lan4 = MyUtils.BinstrToIntArray(data[22]);
//
//
//        for (int i = 0; i < result_lan1.length; i++) {
//            if (result_lan1[i] == 1) {
//                MyLog.i(TAG, "主题传输信息 = 支持的语言(第1组)  = " + i);
//            }
//        }
//
//        for (int i = 0; i < result_lan2.length; i++) {
//            if (result_lan2[i] == 1) {
//                MyLog.i(TAG, "主题传输信息 = 支持的语言(第2组)  = " + i);
//            }
//        }
//
//        for (int i = 0; i < result_lan3.length; i++) {
//            if (result_lan3[i] == 1) {
//                MyLog.i(TAG, "主题传输信息 = 支持的语言(第3组)  = " + i);
//            }
//        }
//
//        for (int i = 0; i < result_lan4.length; i++) {
//            if (result_lan4[i] == 1) {
//                MyLog.i(TAG, "主题传输信息 = 支持的语言(第4组)  = " + i);
//            }
//

        int surplus_space = (((data[18] & 0xff) << 8) | (data[19] & 0xff));
        mBleDeviceTools.set_device_theme_available_space(surplus_space);
        MyLog.i(TAG, "主题传输信息 = 剩余空间  = " + surplus_space);

        int[] deviceParams1 = MyUtils.BinstrToIntArray(data[20]);

        //数据格式
        if (deviceParams1[0] == 1) {
            MyLog.i(TAG, "主题传输信息 = 数据格式 = 反向");
            mBleDeviceTools.setClockDialDataFormat(1);
        } else {
            MyLog.i(TAG, "主题传输信息 = 数据格式 = 正向");
            mBleDeviceTools.setClockDialDataFormat(0);
        }
        //生成方式
        if (deviceParams1[1] == 1) {
            MyLog.i(TAG, "主题传输信息 = 生成方式 = 木兰");
            mBleDeviceTools.setClockDialGenerationMode(1);
        } else {
            MyLog.i(TAG, "主题传输信息 = 生成方式 = 舟海");
            mBleDeviceTools.setClockDialGenerationMode(0);
        }

        //传输方式
        if (deviceParams1[2] == 1) {
            MyLog.i(TAG, "主题传输信息 = 传输方式 = 木兰");
            mBleDeviceTools.setClockDialTransmissionMode(1);
        } else {
            MyLog.i(TAG, "主题传输信息 = 传输方式 = 舟海");
            mBleDeviceTools.setClockDialTransmissionMode(0);
        }

    }

    /**
     * 处理主题传输信息-存储
     *
     * @param data
     */
    public static int handleDeviceMtuInfo(BleDeviceTools mBleDeviceTools, byte[] data) {

        int mtuVal = data[13] & 0xFF;

        MyLog.i(TAG, "蓝牙回调 主题传输信息 = MTU 01  = " + mtuVal);

        mtuVal = mtuVal - 3;

        if (mtuVal < 20) {
            mtuVal = 20;
        }

//        heartVal = 20;

        MyLog.i(TAG, "蓝牙回调 主题传输信息 = MTU 02  = " + mtuVal);
        mBleDeviceTools.set_device_mtu_num(mtuVal);

        return mtuVal;

    }

    /**
     * 处理主题传输信息-存储
     *
     * @param data
     */
    public static void handleCallDeviceInfo(BleDeviceTools mBleDeviceTools, byte[] data) {

        String hexString = BleTools.bytes2HexString(data);
        MyLog.i(TAG, "蓝牙回调 经典蓝牙设备信息 = hexString = " + hexString);

        int call_ble_data_len = data[12] & 0xFF;
        int call_ble_name_len = call_ble_data_len - 6;


        if (call_ble_data_len > 6) {

            byte[] mac_byte = new byte[6];
            for (int i = 0; i < 6; i++) {
                mac_byte[i] = data[13 + i];

                //模拟数据
//            if (i == 0) {
//                mac_byte[i] = (byte) 0xF1;
//            }
            }

            byte[] name_byte = new byte[call_ble_name_len];
            for (int i = 0; i < call_ble_name_len; i++) {
                name_byte[i] = data[13 + 6 + i];
            }

            String mac_str = BleTools.getMacAddress(mac_byte);
            String name_str = BleTools.getBleName(name_byte);

            MyLog.i(TAG, "蓝牙回调 通话设备信息 = mac_str = " + mac_str);
            MyLog.i(TAG, "蓝牙回调 通话设备信息 = name_str = " + name_str);

            mac_str = mac_str.toUpperCase();

            MyLog.i(TAG, "蓝牙回调 通话设备信息 = mac_str = " + mac_str);

            mBleDeviceTools.set_call_ble_mac(mac_str);
            mBleDeviceTools.set_call_ble_name(name_str);

        } else {

            mBleDeviceTools.set_call_ble_mac("");
            mBleDeviceTools.set_call_ble_name("");

            MyLog.i(TAG, "蓝牙回调 经典蓝牙设备信息 = 长度不够");
        }


//        mBleDeviceTools.set_device_mtu_num(heartVal);

    }

    /**
     * 处理连续血氧数据
     *
     * @param data
     */
    public static void HandleContinuitySpo2(ContinuitySpo2InfoUtils mContinuitySpo2InfoUtils, byte[] data) {

        boolean isCheck = BleTools.checkData(data);
        MyLog.i(TAG, "蓝牙回调 - 处理连续血氧数据 校验结果 = " + isCheck);

        if (isCheck) {
            MyLog.i(TAG, "蓝牙回调 ble 同步数据 处理连续血氧数据= 日期 =  " + BleTools.getDate(data));
            if (!BleTools.isRightfulnessTime(BleTools.getDate(data))) {
                // 写入错误的数据到error
                SysUtils.logErrorDataI(TAG, BleTools.getDate(data) + "  byte=" + BleTools.bytes2HexString(data));
                return;
            }
            ContinuitySpo2Info mContinuitySpo2Info = ContinuitySpo2Info.getContinuitySpo2Info(data);
            if (mContinuitySpo2Info != null && BtSerializeation.checkDeviceDate(mContinuitySpo2Info.getDate())) {

                MyLog.i(TAG, "蓝牙回调 连续血氧 mContinuitySpo2Info = " + mContinuitySpo2Info);

                boolean isNull = mContinuitySpo2InfoUtils.MyQuestDataInNull(mContinuitySpo2Info);
                if (isNull) {
                    MyLog.i(TAG, "插入连续血氧表 = 数据不存在");
                    boolean isSuccess = mContinuitySpo2InfoUtils.MyUpdateData(mContinuitySpo2Info);
                    if (isSuccess) {
                        MyLog.i(TAG, "插入连续血氧表成功！");
                    } else {
                        MyLog.i(TAG, "插入连续血氧表失败！");
                    }
                } else {
                    MyLog.i(TAG, "插入连续血氧表 = 数据已存在");
                }

            } else {
                MyLog.i(TAG, "ble 得到连续血氧 mBleHeartModle = null 或者 日期不合法");
            }

        }
    }

    /**
     * 处理连续体温数据
     *
     * @param data
     */
    public static void HandleContinuityTemp(ContinuityTempInfoUtils mContinuityTempInfoUtils, byte[] data) {

        boolean isCheck = BleTools.checkData(data);
        MyLog.i(TAG, "蓝牙回调 - 处理连续体温数据 校验结果 = " + isCheck);

        if (isCheck) {
            MyLog.i(TAG, "蓝牙回调 ble 同步数据 处理连续体温数据= 日期 =  " + BleTools.getDate(data));
            if (!BleTools.isRightfulnessTime(BleTools.getDate(data))) {
                // 写入错误的数据到error
                SysUtils.logErrorDataI(TAG, BleTools.getDate(data) + "  byte=" + BleTools.bytes2HexString(data));
                return;
            }

            ContinuityTempInfo mContinuityTempInfo = ContinuityTempInfo.getContinuityTempInfo(data);

            if (mContinuityTempInfo != null && BtSerializeation.checkDeviceDate(mContinuityTempInfo.getDate())) {

                MyLog.i(TAG, "蓝牙回调 连续体温 mContinuityTempInfo = " + mContinuityTempInfo.toString());

                boolean isNull = mContinuityTempInfoUtils.MyQuestDataInNull(mContinuityTempInfo);
                if (isNull) {
                    MyLog.i(TAG, "插入连续体温表 = 数据不存在");
                    boolean isSuccess = mContinuityTempInfoUtils.MyUpdateData(mContinuityTempInfo);
                    if (isSuccess) {
                        MyLog.i(TAG, "插入连续体温表成功！");
                    } else {
                        MyLog.i(TAG, "插入连续体温表失败！");
                    }
                } else {
                    MyLog.i(TAG, "插入连续体温表 = 数据已存在");
                }


            } else {
                MyLog.i(TAG, "ble 得到连续体温 mBleHeartModle = null 或者 日期不合法");
            }

        }
    }

    /**
     * 处理离线血氧数据
     *
     * @param data
     */
    public static void HandleMeasureSpo2(Context context, MeasureSpo2InfoUtils mMeasureSpo2InfoUtils, byte[] data) {

        int count = data[15] & 0xff;
        MyLog.i(TAG, "离线血氧  = 个数 = " + count);
        List<MeasureSpo2Info> health_list = MeasureSpo2Info.getInfoList(data);
        if (health_list != null && health_list.size() > 0) {
            MyLog.i(TAG, "离线血氧 my_list  = " + health_list.toString());
            for (int i = 0; i < health_list.size(); i++) {
                MyLog.i(TAG, "离线血氧 my_list  i = " + i + "  health = " + health_list.get(i).toString());
            }
            saveListMeasureSpo2Data(context, mMeasureSpo2InfoUtils, health_list);
        } else {
            MyLog.i(TAG, "离线血氧 my_list  = null");
        }
    }

    /**
     * 保存多条健康数据
     */
    public static void saveListMeasureSpo2Data(Context context, MeasureSpo2InfoUtils mMeasureSpo2InfoUtils, List<MeasureSpo2Info> data_list) {
        MyLog.i(TAG, "插入离线血氧 - 多条 =  mHealthInfo = " + data_list.toString());
        boolean isSuccess = mMeasureSpo2InfoUtils.insertInfoList(data_list);
        if (isSuccess) {
            MyLog.i(TAG, "插入离线血氧表成功！");
        } else {
            MyLog.i(TAG, "插入离线血氧失败！");
        }

    }

    /**
     * 处理离线体温数据
     *
     * @param data
     */
    public static void HandleMeasureTemp(Context context, MeasureTempInfoUtils mMeasureTempInfoUtils, byte[] data) {


        int count = data[15] & 0xff;
        MyLog.i(TAG, "离线体温  = 个数 = " + count);
        List<MeasureTempInfo> health_list = MeasureTempInfo.getInfoList(data);
        if (health_list != null && health_list.size() > 0) {
            MyLog.i(TAG, "离线体温 my_list  = " + health_list.toString());
            for (int i = 0; i < health_list.size(); i++) {
                MyLog.i(TAG, "离线体温 my_list  i = " + i + "  health = " + health_list.get(i).toString());
            }
            saveListMeasureTempData(context, mMeasureTempInfoUtils, health_list);
        } else {
            MyLog.i(TAG, "离线体温 my_list  = null");
        }
    }


    /**
     * 保存多条健康数据
     */
    public static void saveListMeasureTempData(Context context, MeasureTempInfoUtils mMeasureTempInfoUtils, List<MeasureTempInfo> data_list) {
        MyLog.i(TAG, "插入离线体温 - 多条 =  mHealthInfo = " + data_list.toString());
        boolean isSuccess = mMeasureTempInfoUtils.insertInfoList(data_list);
        if (isSuccess) {
            MyLog.i(TAG, "插入离线体温表成功！");
        } else {
            MyLog.i(TAG, "插入离线体温失败！");
        }

    }


    /**
     * 用户行为
     */
    public static void HandleUserBehavior(Context context, byte[] data, String mac) {
        boolean isCheck = BleTools.checkData(data);
        MyLog.i(TAG, "蓝牙回调 - 用户行为 校验结果 = " + isCheck);
        MyLog.i(TAG, "蓝牙回调 用户行为 = 日期 =  " + BleTools.getDate(data));

        String date = BleTools.getDate(data);

        String blueData = BleTools.bytes2HexString(data);
        String strCmd[] = blueData.split(" ");

        int i = 17;
        int daytimeAbove = Integer.parseInt(strCmd[i] + strCmd[i + 1], 16);
        i += 2;
        int daytimeBelow = Integer.parseInt(strCmd[i] + strCmd[i + 1], 16);
        i += 2;
        int nightAbove = Integer.parseInt(strCmd[i] + strCmd[i + 1], 16);
        i += 2;
        int nightBelow = Integer.parseInt(strCmd[i] + strCmd[i + 1], 16);
        i += 2;
        int shockNum = Integer.parseInt(strCmd[i] + strCmd[i + 1], 16);
        i += 2;
        int daytimeBrightScreenNum = Integer.parseInt(strCmd[i] + strCmd[i + 1], 16);
        i += 2;
        int nightBrightScreenNum = Integer.parseInt(strCmd[i] + strCmd[i + 1], 16);
        i += 2;
        int voltageValue = Integer.parseInt(strCmd[i] + strCmd[i + 1], 16);
        i += 2;
        int manualHeartNum = Integer.parseInt(strCmd[i] + strCmd[i + 1], 16);
        i += 2;
        int manualBloodPressureNum = Integer.parseInt(strCmd[i] + strCmd[i + 1], 16);
        i += 2;
        int nowVoltageValue = Integer.parseInt(strCmd[i] + strCmd[i + 1], 16);

        SysUtils.logUserBehaviorI(TAG,

                "  日期=" + date
                        + "  白天高于默认亮度亮屏时间=" + daytimeAbove
                        + "  白天低于或等于默认亮度亮屏时间=" + daytimeBelow
                        + "  晚上高于默认亮度亮屏时间=" + nightAbove
                        + "  晚上低于或等于默认亮度亮屏时间=" + nightBelow
                        + "  马达震动次数=" + shockNum
                        + "  白天抬碗亮屏次数=" + daytimeBrightScreenNum
                        + "  晚上抬碗亮屏次数=" + nightBrightScreenNum
                        + "  0点电压值=" + voltageValue
                        + "  手动测量心率次数=" + manualHeartNum
                        + "  手动测量血压次数=" + manualBloodPressureNum
                        + "  当前电压=" + nowVoltageValue
                , mac);


    }

    /**
     * 处理设备基本信息
     *
     * @param data
     */
    public static int handleDeviceBasicInfo(BleDeviceTools mBleDeviceTools, byte[] data) {

        int mtuVal = (int) ((((int) data[13] & 0xff) << 8) | data[14] & 0xff);
        MyLog.i(TAG, "处理设备基本信息 = MTU 01  = " + mtuVal);
        mtuVal = mtuVal - 3;
        if (mtuVal < 20) {
            mtuVal = 20;
        }

        MyLog.i(TAG, "蓝牙回调 处理设备基本信息 = MTU 02  = " + mtuVal);
        mBleDeviceTools.set_device_mtu_num(mtuVal);

        int CrcType = data[15] & 0xff;
        int[] deviceParams1 = MyUtils.BinstrToIntArray(data[16]); // 参数10

        if (CrcType == 0) {
            MyLog.i(TAG, "蓝牙回调 处理设备基本信息 = 校验方式 = 老规则(CRC8)");
            mBleDeviceTools.setSupportNewDeviceCrc(false);
        } else if (CrcType == 1) {
            MyLog.i(TAG, "蓝牙回调 处理设备基本信息 = 校验方式 = 新规则(CRC32)");
            mBleDeviceTools.setSupportNewDeviceCrc(true);
        }

        if (deviceParams1[0] == 1) {
            MyLog.i(TAG, "蓝牙回调 处理设备基本信息 = 是否需要单包回应 = 是");
            mBleDeviceTools.setisReplyOnePack(true);
        } else {
            MyLog.i(TAG, "蓝牙回调 处理设备基本信息 = 是否需要单包回应 = 否");
            mBleDeviceTools.setisReplyOnePack(false);
        }

        return mtuVal;

    }
}
