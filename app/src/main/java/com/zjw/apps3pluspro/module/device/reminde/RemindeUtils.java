package com.zjw.apps3pluspro.module.device.reminde;

import android.content.Context;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.db.AlarmDao;
import com.zjw.apps3pluspro.module.device.entity.AlarmClockModel;
import com.zjw.apps3pluspro.module.device.entity.DurgModel;
import com.zjw.apps3pluspro.module.device.entity.MettingModel;
import com.zjw.apps3pluspro.module.device.entity.SitModel;
import com.zjw.apps3pluspro.module.device.entity.WaterModel;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.RemindeTools;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RemindeUtils {


    /**
     * 校准分钟格式，预防超出正常造成异常
     *
     * @param year
     * @return
     */
    public static int CalibrationYear(int year) {

        if (year < 0) {
            year = 0;
        }
        if (year > 63) {
            year = 63;
        }

        return year;
    }

    /**
     * 校准分钟格式，预防超出正常造成异常
     *
     * @param month
     * @return
     */
    public static int CalibrationMonth(int month) {

        if (month < 1) {
            month = 1;
        }
        if (month > 12) {
            month = 12;
        }

        return month;
    }

    /**
     * 校准分钟格式，预防超出正常造成异常
     *
     * @param day
     * @return
     */
    public static int CalibrationDay(int day) {

        if (day < 1) {
            day = 1;
        }
        if (day > 31) {
            day = 31;
        }

        return day;
    }

    /**
     * 校准小时格式，预防超出正常造成异常
     *
     * @param hour
     * @return
     */
    public static int CalibrationHour(int hour) {

        if (hour < 0) {
            hour = 0;
        }
        if (hour > 23) {
            hour = 23;
        }

        return hour;
    }

    /**
     * 校准分钟格式，预防超出正常造成异常
     *
     * @param hour
     * @return
     */
    public static int CalibrationMin(int hour) {

        if (hour < 0) {
            hour = 0;
        }
        if (hour > 59) {
            hour = 59;
        }

        return hour;
    }

    /**
     * 校准间隔时间,预防超出正常造成异常
     *
     * @param value
     * @param max
     * @param min
     * @return
     */
    public static int CalibrationCycle(int value, int min, int max) {

        if (value < min) {
            value = min;
        }
        if (value > max) {
            value = max;
        }

        return value;
    }


    /**
     * 当添加闹钟的时候，为了防止ID重复，导致异常。
     *
     * @return
     */
    public static int getDataId(Context context) {

        List<AlarmClockModel> list = getAlarmClock(context);

        int result = 0;

        if (list == null) {
            result = 0;
        } else {

            for (int i = 0; i < 5; i++) {
                result = getId(list, i);
                if (result != -1) {
                    return result;
                }
            }
        }


        return result;
    }

    public static int getId(List<AlarmClockModel> list, int id) {
        int reesult = -1;

        if (list.size() == 0) {
            reesult = 1;
        } else {
            for (int i = 0; i < list.size(); i++) {

                if (list.get(i).getId() == id) {
                    reesult = -1;
                    break;
                } else {
                    reesult = id;
                }
            }
        }


        return reesult;

    }


    /**
     * 获取小时数组
     *
     * @return
     */
    public static List<String> getHourListData() {
        List<String> dataHours = new ArrayList<String>();
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                dataHours.add("0" + String.valueOf(i));
            } else {
                dataHours.add(String.valueOf(i));
            }
        }
        return dataHours;
    }


    /**
     * 获取分钟数组
     *
     * @return
     */
    public static List<String> getMinListData() {
        List<String> dataMin = new ArrayList<String>();

        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                dataMin.add("0" + String.valueOf(i));
            } else {
                dataMin.add(String.valueOf(i));
            }
        }
        return dataMin;
    }

    /**
     * 获取日期 精确到秒
     *
     * @return
     */
    public static String getDefultTime() {

        Date date = new Date();
        SimpleDateFormat sdformat = new SimpleDateFormat("HH:mm");//24小时制
        return sdformat.format(date);
    }


    /**
     * 存储吃药提醒数据
     *
     * @param mDurgModel
     */
    public static void setDurgModel(DurgModel mDurgModel) {

        RemindeTools mNtfUsp = BaseApplication.getRemindeTools();

        int medicine_start_h_time = mDurgModel.getMedicineStartHourTime();
        int medicine_start_m_time = mDurgModel.getMedicineStartMinTime();
        int medicine_end_h_time = mDurgModel.getMedicineEndHourTime();
        int medicine_end_m_time = mDurgModel.getMedicineEndMinTime();
        int medicine_cycle_time = mDurgModel.getMedicineCycleTime();
        boolean medicine_enable = mDurgModel.isMedicineEnable();

        medicine_start_h_time = RemindeUtils.CalibrationHour(medicine_start_h_time);
        medicine_start_m_time = RemindeUtils.CalibrationMin(medicine_start_m_time);
        medicine_end_h_time = RemindeUtils.CalibrationHour(medicine_end_h_time);
        medicine_end_m_time = RemindeUtils.CalibrationMin(medicine_end_m_time);
        medicine_cycle_time = RemindeUtils.CalibrationCycle(medicine_cycle_time, 4, 12);


        mNtfUsp.WriteKeyValue(DurgModel.KEY_STARTH, medicine_start_h_time);
        mNtfUsp.WriteKeyValue(DurgModel.KEY_STARTM, medicine_start_m_time);
        mNtfUsp.WriteKeyValue(DurgModel.KEY_ENDH, medicine_end_h_time);
        mNtfUsp.WriteKeyValue(DurgModel.KEY_ENDM, medicine_end_m_time);
        mNtfUsp.WriteKeyValue(DurgModel.KEY_PERIOD, medicine_cycle_time);
        mNtfUsp.WriteKeyValue(DurgModel.KEY_MEDICAL, medicine_enable ? 1 : 0);

    }


    /**
     * 获取吃药提醒数据
     *
     * @return
     */
    public static DurgModel getDurgModel() {

        RemindeTools mNtfUsp = BaseApplication.getRemindeTools();

        int medicine_start_h_time = (mNtfUsp.GetValue(DurgModel.KEY_STARTH, 8));
        int medicine_start_m_time = (mNtfUsp.GetValue(DurgModel.KEY_STARTM, 0));
        int medicine_end_h_time = (mNtfUsp.GetValue(DurgModel.KEY_ENDH, 20));
        int medicine_end_m_time = (mNtfUsp.GetValue(DurgModel.KEY_ENDM, 0));
        int medicine_cycle_time = (mNtfUsp.GetValue(DurgModel.KEY_PERIOD, 4));
        boolean medicine_enable = mNtfUsp.GetValue(DurgModel.KEY_MEDICAL, 0) == 1;

        medicine_start_h_time = RemindeUtils.CalibrationHour(medicine_start_h_time);
        medicine_start_m_time = RemindeUtils.CalibrationMin(medicine_start_m_time);
        medicine_end_h_time = RemindeUtils.CalibrationHour(medicine_end_h_time);
        medicine_end_m_time = RemindeUtils.CalibrationMin(medicine_end_m_time);
        medicine_cycle_time = RemindeUtils.CalibrationCycle(medicine_cycle_time, 4, 12);


        return new DurgModel(medicine_start_h_time, medicine_start_m_time, medicine_end_h_time, medicine_end_m_time, medicine_cycle_time, medicine_enable);
    }


    /**
     * 获取久坐提醒数据
     *
     * @return
     */
    public static SitModel getSitModel() {

        RemindeTools mNtfUsp = BaseApplication.getRemindeTools();


        int long_sit_start_h_time = (mNtfUsp.GetValue(SitModel.KEY_STARTH, 8));
        int long_sit_start_m_time = (mNtfUsp.GetValue(SitModel.KEY_STARTM, 0));
        int long_sit_end_h_time = (mNtfUsp.GetValue(SitModel.KEY_ENDH, 20));
        int long_sit_end_m_time = (mNtfUsp.GetValue(SitModel.KEY_ENDM, 0));
        int long_sit_cycle_time = (mNtfUsp.GetValue(SitModel.KEY_PERIOD, 0));
        boolean long_sit_enable = mNtfUsp.GetValue(SitModel.KEY_LONG_SIT, 0) == 1;

        long_sit_start_h_time = RemindeUtils.CalibrationHour(long_sit_start_h_time);
        long_sit_start_m_time = RemindeUtils.CalibrationMin(long_sit_start_m_time);
        long_sit_end_h_time = RemindeUtils.CalibrationHour(long_sit_end_h_time);
        long_sit_end_m_time = RemindeUtils.CalibrationMin(long_sit_end_m_time);
        long_sit_cycle_time = RemindeUtils.CalibrationCycle(long_sit_cycle_time, 1, 4);


        return new SitModel(long_sit_start_h_time, long_sit_start_m_time, long_sit_end_h_time, long_sit_end_m_time, long_sit_cycle_time, long_sit_enable);
    }

    /**
     * 存储久坐提醒数据
     *
     * @param mSitModel
     */
    public static void setSitModel(SitModel mSitModel) {

        RemindeTools mNtfUsp = BaseApplication.getRemindeTools();

        int long_sit_start_h_time = mSitModel.getSitStartHourTime();
        int long_sit_start_m_time = mSitModel.getSitStartMinTime();
        int long_sit_end_h_time = mSitModel.getSitEndHourTime();
        int long_sit_end_m_time = mSitModel.getSitEndMinTime();
        int long_sit_cycle_time = mSitModel.getSitCycleTime();
        boolean long_sit_enable = mSitModel.isSitEnable();

        long_sit_start_h_time = RemindeUtils.CalibrationHour(long_sit_start_h_time);
        long_sit_start_m_time = RemindeUtils.CalibrationMin(long_sit_start_m_time);
        long_sit_end_h_time = RemindeUtils.CalibrationHour(long_sit_end_h_time);
        long_sit_end_m_time = RemindeUtils.CalibrationMin(long_sit_end_m_time);
        long_sit_cycle_time = RemindeUtils.CalibrationCycle(long_sit_cycle_time, 1, 4);


        mNtfUsp.WriteKeyValue(SitModel.KEY_STARTH, long_sit_start_h_time);
        mNtfUsp.WriteKeyValue(SitModel.KEY_STARTM, long_sit_start_m_time);
        mNtfUsp.WriteKeyValue(SitModel.KEY_ENDH, long_sit_end_h_time);
        mNtfUsp.WriteKeyValue(SitModel.KEY_ENDM, long_sit_end_m_time);
        mNtfUsp.WriteKeyValue(SitModel.KEY_PERIOD, long_sit_cycle_time);
        mNtfUsp.WriteKeyValue(SitModel.KEY_LONG_SIT, long_sit_enable ? 1 : 0);

    }


    /**
     * 获取喝水提醒数据
     *
     * @return
     */
    public static WaterModel getWaterModel() {

        RemindeTools mNtfUsp = BaseApplication.getRemindeTools();


        int drink_water_start_h_time = (mNtfUsp.GetValue(WaterModel.KEY_STARTH, 8));
        int drink_water_start_m_time = (mNtfUsp.GetValue(WaterModel.KEY_STARTM, 0));
        int drink_water_end_h_time = (mNtfUsp.GetValue(WaterModel.KEY_ENDH, 20));
        int drink_water_end_m_time = (mNtfUsp.GetValue(WaterModel.KEY_ENDM, 0));
        int drink_water_cycle_time = (mNtfUsp.GetValue(WaterModel.KEY_PERIOD, 0));
        boolean drink_enable = mNtfUsp.GetValue(WaterModel.KEY_DRINKING, 0) == 1;

        drink_water_start_h_time = RemindeUtils.CalibrationHour(drink_water_start_h_time);
        drink_water_start_m_time = RemindeUtils.CalibrationMin(drink_water_start_m_time);
        drink_water_end_h_time = RemindeUtils.CalibrationHour(drink_water_end_h_time);
        drink_water_end_m_time = RemindeUtils.CalibrationMin(drink_water_end_m_time);
        drink_water_cycle_time = RemindeUtils.CalibrationCycle(drink_water_cycle_time, 1, 4);


        return new WaterModel(drink_water_start_h_time, drink_water_start_m_time, drink_water_end_h_time, drink_water_end_m_time, drink_water_cycle_time, drink_enable);
    }

    /**
     * 存储喝水提醒数据
     *
     * @param mWaterModel
     */
    public static void setWaterModel(WaterModel mWaterModel) {

        RemindeTools mNtfUsp = BaseApplication.getRemindeTools();

        int drink_water_start_h_time = mWaterModel.getDrinkingStartHourTime();
        int drink_water_start_m_time = mWaterModel.getDrinkingStartMinTime();
        int drink_water_end_h_time = mWaterModel.getDrinkingEndHourTime();
        int drink_water_end_m_time = mWaterModel.getDrinkingEndMinTime();
        int drink_water_cycle_time = mWaterModel.getDrinkingCycleTime();
        boolean drink_enable = mWaterModel.isDrinkingEnable();

        drink_water_start_h_time = RemindeUtils.CalibrationHour(drink_water_start_h_time);
        drink_water_start_m_time = RemindeUtils.CalibrationMin(drink_water_start_m_time);
        drink_water_end_h_time = RemindeUtils.CalibrationHour(drink_water_end_h_time);
        drink_water_end_m_time = RemindeUtils.CalibrationMin(drink_water_end_m_time);
        drink_water_cycle_time = RemindeUtils.CalibrationCycle(drink_water_cycle_time, 1, 4);


        mNtfUsp.WriteKeyValue(WaterModel.KEY_STARTH, drink_water_start_h_time);
        mNtfUsp.WriteKeyValue(WaterModel.KEY_STARTM, drink_water_start_m_time);
        mNtfUsp.WriteKeyValue(WaterModel.KEY_ENDH, drink_water_end_h_time);
        mNtfUsp.WriteKeyValue(WaterModel.KEY_ENDM, drink_water_end_m_time);
        mNtfUsp.WriteKeyValue(WaterModel.KEY_PERIOD, drink_water_cycle_time);
        mNtfUsp.WriteKeyValue(WaterModel.KEY_DRINKING, drink_enable ? 1 : 0);

    }

    /**
     * 获取会提提醒数据
     *
     * @return
     */
    public static MettingModel getMettingModel() {

        String TAG = "MoreSetActivity";

        RemindeTools mNtfUsp = BaseApplication.getRemindeTools();

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        int metting_year = mNtfUsp.GetValue(MettingModel.KEY_YEAR, c.get(Calendar.YEAR) - 2000);
        int metting_mon = mNtfUsp.GetValue(MettingModel.KEY_MONTH, c.get(Calendar.MONTH) + 1);
        int metting_day = mNtfUsp.GetValue(MettingModel.KEY_DAY, c.get(Calendar.DAY_OF_MONTH));
        int metting_hour = mNtfUsp.GetValue(MettingModel.KEY_HOUR, c.get(Calendar.HOUR_OF_DAY));
        int metting_min = mNtfUsp.GetValue(MettingModel.KEY_MIN, c.get(Calendar.MINUTE));
        boolean metting_enable = mNtfUsp.GetValue(MettingModel.KEY_METTING, 0) == 1;

        metting_year = RemindeUtils.CalibrationYear(metting_year);
        metting_mon = RemindeUtils.CalibrationMonth(metting_mon);
        metting_day = RemindeUtils.CalibrationDay(metting_day);
        metting_hour = RemindeUtils.CalibrationHour(metting_hour);
        metting_min = RemindeUtils.CalibrationMin(metting_min);

       MyLog.i(TAG,"获取会提提醒数据 = metting_year = " + metting_year);
       MyLog.i(TAG,"获取会提提醒数据 = metting_mon = " + metting_mon);
       MyLog.i(TAG,"获取会提提醒数据 = metting_day = " + metting_day);
       MyLog.i(TAG,"获取会提提醒数据 = metting_hour = " + metting_hour);
       MyLog.i(TAG,"获取会提提醒数据 = metting_min = " + metting_min);


        return new MettingModel(metting_year, metting_mon, metting_day, metting_hour, metting_min, metting_enable);
    }

    /**
     * 存储会议提醒数据
     *
     * @param mMettingModel
     */
    public static void setMettingModel(MettingModel mMettingModel) {

        String TAG = "MoreSetActivity";

        RemindeTools mNtfUsp = BaseApplication.getRemindeTools();

        int metting_year = mMettingModel.getMettingYear();
        int metting_mon = mMettingModel.getMettingMonth();
        int metting_day = mMettingModel.getMettingDay();
        int metting_hour = mMettingModel.getMettingHour();
        int metting_min = mMettingModel.getMettingMin();
        boolean metting_enable = mMettingModel.isMettingEnable();

        metting_year = RemindeUtils.CalibrationYear(metting_year);
        metting_mon = RemindeUtils.CalibrationMonth(metting_mon);
        metting_day = RemindeUtils.CalibrationDay(metting_day);
        metting_hour = RemindeUtils.CalibrationHour(metting_hour);
        metting_min = RemindeUtils.CalibrationMin(metting_min);

       MyLog.i(TAG,"存储会议提醒数据 = metting_year = " + metting_year);
       MyLog.i(TAG,"存储会议提醒数据 = metting_mon = " + metting_mon);
       MyLog.i(TAG,"存储会议提醒数据 = metting_day = " + metting_day);
       MyLog.i(TAG,"存储会议提醒数据 = metting_hour = " + metting_hour);
       MyLog.i(TAG,"存储会议提醒数据 = metting_min = " + metting_min);

        mNtfUsp.WriteKeyValue(MettingModel.KEY_YEAR, metting_year);
        mNtfUsp.WriteKeyValue(MettingModel.KEY_MONTH, metting_mon);
        mNtfUsp.WriteKeyValue(MettingModel.KEY_DAY, metting_day);
        mNtfUsp.WriteKeyValue(MettingModel.KEY_HOUR, metting_hour);
        mNtfUsp.WriteKeyValue(MettingModel.KEY_MIN, metting_min);
        mNtfUsp.WriteKeyValue(MettingModel.KEY_METTING, metting_enable ? 1 : 0);

    }

    /**
     * 查询闹钟
     *
     * @param context
     * @return
     */
    public static List<AlarmClockModel> getAlarmClock(Context context) {

        AlarmDao alarmDao = new AlarmDao(context);

        return alarmDao.query2();

    }


    /**
     * 插入闹钟
     *
     * @param context
     * @param mAlarmClockModel
     */
    public static void insertAlarmClock(Context context, AlarmClockModel mAlarmClockModel) {

        AlarmDao alarmDao = new AlarmDao(context);
        alarmDao.insert(mAlarmClockModel);


    }


    /**
     * 修改闹钟
     *
     * @param context
     * @param mAlarmClockModel
     */
    public static void updateClockAlarmClock(Context context, AlarmClockModel mAlarmClockModel) {

        AlarmDao alarmDao = new AlarmDao(context);
        alarmDao.updateClock(mAlarmClockModel);


    }

    /**
     * 删除闹钟
     *
     * @param context
     * @param item
     */
    public static void deleteAlarmClock(Context context, AlarmClockModel item) {

        AlarmDao alarmDao = new AlarmDao(context);
        alarmDao.delete(item);


    }

    /**
     * 查询是否有打开的闹钟
     *
     * @param context
     */
    public static boolean getAlarmClockisBoolean(Context context) {

        boolean result = false;

        AlarmDao alarmDao = new AlarmDao(context);

        List<AlarmClockModel> list = alarmDao.query2();

        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {

                AlarmClockModel mAlarmClockModel = list.get(i);

                if (((mAlarmClockModel.getRepeat()) & 0x80) != 0) {
                    result = true;
                    break;
                }


            }
        }


        return result;
    }

    public static boolean getNotifaceisBoolean(Context context) {

        boolean result = false;

        BleDeviceTools mBleDeviceTools = new BleDeviceTools(context);

        if (mBleDeviceTools.get_reminde_qq()
                || mBleDeviceTools.get_reminde_wx()
                || mBleDeviceTools.get_reminde_skype()
                || mBleDeviceTools.get_reminde_facebook()
                || mBleDeviceTools.get_reminde_whatsapp()
                || mBleDeviceTools.get_reminde_linkedin()
                || mBleDeviceTools.get_reminde_twitter()
                || mBleDeviceTools.get_reminde_viber()
                || mBleDeviceTools.get_reminde_line()
                ) {
            result = true;
        }
     

        return result;

    }


}
