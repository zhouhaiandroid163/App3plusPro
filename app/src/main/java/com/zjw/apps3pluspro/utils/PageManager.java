package com.zjw.apps3pluspro.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.module.home.entity.PageItem;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * on 2020/5/5.
 */
public class PageManager {
    private static final String TAG = PageManager.class.getSimpleName();
    private static PageManager pageManager;
    private Gson gson;
    private GsonBuilder builder;

    private ArrayList<PageItem> pageAppList = new ArrayList<>();// 第一种app首页布局
    public final static int PAGE_HIDE = -1; // 隐藏的标记位置
    public final static int PAGE_APP_ECG = 0;
    public final static int PAGE_APP_EXERCISE = 1;
    public final static int PAGE_APP_HEART = 2;
    public final static int PAGE_APP_SLEEP = 3;
    public final static int PAGE_APP_GPS_SPORT = 4;
    public final static int PAGE_APP_BLOOD_PRESSURE = 5;
    public final static int PAGE_APP_MENSTRUAL_PERIOD = 6;
    public final static int PAGE_APP_BLOOD_OXYGEN = 7;
    public final static int PAGE_APP_TEMPERATURE = 8;

    public ArrayList<PageItem> pageDeviceList = new ArrayList<>();
    public ArrayList<PageItem> pageDeviceNoMove = new ArrayList<>();
    public ArrayList<PageItem> pageDeviceMove = new ArrayList<>();
    public ArrayList<PageItem> pageDeviceHide = new ArrayList<>();

    public final static int PAGE_DEVICE_SETTING = 0x01;                //设置
    public final static int PAGE_DEVICE_FITNESS = 0x02;                    //运动健身
    public final static int PAGE_DEVICE_HEART_RATE = 0x03;                //心率
    public final static int PAGE_DEVICE_PRESSURE = 0x04;                //压力
    public final static int PAGE_DEVICE_ENERGY = 0x05;                    //能量
    public final static int PAGE_DEVICE_SLEEP = 0x06;                    //睡眠
    public final static int PAGE_DEVICE_BREATH = 0x07;                    //呼吸训练
    public final static int PAGE_DEVICE_ANAEROBIC_THRESHOLD = 0x08;        //无氧阈
    public final static int PAGE_DEVICE_CALENDAR = 0x09;                //日历
    public final static int PAGE_DEVICE_CLOCK = 0x0A;                    //闹钟
    public final static int PAGE_DEVICE_STOPWATCH = 0x0B;                //秒表
    public final static int PAGE_DEVICE_TIME_KEEPING = 0x0C;            //倒计时
    public final static int PAGE_DEVICE_WEATHER = 0x0D;                    //天气
    public final static int PAGE_DEVICE_STOCK = 0x0E;                    //股票
    public final static int PAGE_DEVICE_APP_LIST = 0x0F;                //应用程序列表
    public final static int PAGE_DEVICE_SPORT_LIST = 0x10;                //多运动
    public final static int PAGE_DEVICE_HOME = 0x11;                    //主表盘
    public final static int PAGE_DEVICE_MUSIC_CONTROLLER = 0x12;        //音乐控制
    public final static int PAGE_DEVICE_VOICE_ASSISTANT = 0x13;            //语音助理
    public final static int PAGE_DEVICE_BLOOD_OXYGEN = 0x14;            //血氧

    public final static int PAGE_DEVICE_ALIPAY = 0x20;                    //支付宝
    public final static int PAGE_DEVICE_CARD = 0x21;                    //卡包
    public final static int PAGE_DEVICE_DOOR = 0x22;                    //门卡
    public final static int PAGE_DEVICE_BUS = 0x23;                        //公共汽车卡
    public final static int PAGE_DEVICE_BANK = 0x24;                    //银行卡

    public final static int PAGE_DEVICE_BLOOD_PRESSURE = 0x81;            //血压
    public final static int PAGE_DEVICE_ECG_FUNCTION = 0x82;            //心电
    public final static int PAGE_DEVICE_INFORMATION = 0x83;                //信息
    public final static int PAGE_DEVICE_ACTIVITY_HEAT = 0x84;            //活动热量
    public final static int PAGE_DEVICE_STEP_COUNTER = 0x85;            //计步
    public final static int PAGE_DEVICE_CALORIE = 0x86;                    //卡路里
    public final static int PAGE_DEVICE_DISTANCE = 0x87;                //距离
    public final static int PAGE_DEVICE_PHYSIONLOGICAL_CYCLE = 0x88;    //生理周期
    public final static int PAGE_DEVICE_COMPASS = 0x89;                    //指南针
    public final static int PAGE_DEVICE_ABOUT = 0x8A;                    //关于
    public final static int PAGE_DEVICE_MORE_SETTING = 0x8B;            //更多
    public final static int PAGE_DEVICE_ATMOSPHERIC_PRESSURE = 0x8C;    //气压
    public final static int PAGE_DEVICE_PHYSICAL_EXERCISE = 0x8D;        //锻炼
    public final static int PAGE_DEVICE_DAILY_STATE = 0x8E;                //状态
    public final static int PAGE_DEVICE_TOOL_LIST = 0x8F;                //工具
    public final static int PAGE_DEVICE_QR_CODE_DOWNLOAD = 0x90;        //二维码
    public final static int PAGE_DEVICE_TEMPERATURE = 0x91;                //体温

    public static PageManager getInstance() {
        if (pageManager == null) {
            pageManager = new PageManager();
        }
        return pageManager;
    }

    BleDeviceTools mBleDeviceTools;

    private PageManager() {
        builder = new GsonBuilder();
        gson = builder.create();
        mBleDeviceTools = BaseApplication.getBleDeviceTools();
        String Json = mBleDeviceTools.getCardSortJson();
        if (Json.length() > 0) {
            List<PageItem> p2 = new ArrayList<PageItem>();
            Type type1 = new TypeToken<List<PageItem>>() {
            }.getType();
            p2 = gson.fromJson(Json, type1);
            pageAppList.addAll(p2);
        }
    }

    public void cleanList() {
//        pagelist.clear();
    }

    public void cleanAllPageDeviceList() {
        PageManager.getInstance().pageDeviceList.clear();
        PageManager.getInstance().pageDeviceNoMove.clear();
        PageManager.getInstance().pageDeviceMove.clear();
        PageManager.getInstance().pageDeviceHide.clear();
    }

    public void addPage(PageItem item) {
        pageAppList.add(item);
        if (item.index != PAGE_HIDE) {
            swapPageList(item.index);
        }
    }

    public void removePage(int index) {
        for (int i = 0; i < pageAppList.size(); i++) {
            if (index == pageAppList.get(i).index) {
                pageAppList.remove(pageAppList.get(i));
            }
        }
    }

    public ArrayList<PageItem> getPageAppList() {
        return pageAppList;
    }

    public ArrayList<PageItem> getPageDeviceList() {
        if (pageDeviceList.size() == 0) {
            pageDeviceList.addAll(pageDeviceMove);
            PageItem item = new PageItem(PageManager.PAGE_HIDE, true);
            item.index = PageManager.PAGE_HIDE;
            pageDeviceList.add(item);
            pageDeviceList.addAll(pageDeviceHide);
        }
        return pageDeviceList;
    }

    public void setCardJson() {
        Type type = new TypeToken<List<PageItem>>() {
        }.getType();
        String jsonListTest = gson.toJson(pageAppList, type);
        mBleDeviceTools.setCardSortJson(jsonListTest);
    }

    public int getIndexPosition(int index) {
        int position = -1;
        for (int i = 0; i < pageAppList.size(); i++) {
            if (index == pageAppList.get(i).index) {
                position = i;
                break;
            }
        }
        return position;
    }

    public int getIndexPositionPageDevice(int index) {
        int position = -1;
        for (int i = 0; i < pageDeviceList.size(); i++) {
            if (index == pageDeviceList.get(i).index) {
                position = i;
                break;
            }
        }
        return position;
    }

    private void swapPageList(int index) {
        int fromPosition = getIndexPosition(index);
        int toPosition = getIndexPosition(PAGE_HIDE);
        if (toPosition == -1) {
            return;
        }
        for (int i = fromPosition; i > toPosition; i--) {
            Collections.swap(pageAppList, i, i - 1);
        }
    }

    public boolean isHideApp(int index) {
        ArrayList<PageItem> pageList = PageManager.getInstance().getPageAppList();
        for (int i = 0; i < pageList.size(); i++) {
            if (pageList.get(i).index == PageManager.PAGE_HIDE) {
                return true;
            }
            if (index == pageList.get(i).index) {
                return false;
            }
        }
        return false;
    }

    public boolean isHideDevice(int index) {
        ArrayList<PageItem> pageList = pageDeviceList;
        for (int i = 0; i < pageList.size(); i++) {
            if (pageList.get(i).index == PageManager.PAGE_HIDE) {
                return true;
            }
            if (index == pageList.get(i).index) {
                return false;
            }
        }
        return false;
    }

    public int getPicture(int index, boolean isGrey) {
        int picture;
        switch (index) {
            case PageManager.PAGE_HIDE:
                if (isGrey) {
                    picture = R.mipmap.ic_launcher;
                } else
                    picture = R.mipmap.ic_launcher;
                break;
            case PageManager.PAGE_APP_ECG:
                if (isGrey) {
                    picture = R.mipmap.data_ecg_grey;
                } else
                    picture = R.mipmap.data_ecg;
                break;
            case PageManager.PAGE_APP_EXERCISE:
                if (isGrey) {
                    picture = R.mipmap.data_exercise_grey;
                } else
                    picture = R.mipmap.data_exercise;
                break;
            case PageManager.PAGE_APP_HEART:
                if (isGrey) {
                    picture = R.mipmap.data_heart_grey;
                } else
                    picture = R.mipmap.data_heart;
                break;
            case PageManager.PAGE_APP_SLEEP:
                if (isGrey) {
                    picture = R.mipmap.data_sleep_grey;
                } else
                    picture = R.mipmap.data_sleep;
                break;
            case PageManager.PAGE_APP_GPS_SPORT:
                if (isGrey) {
                    picture = R.mipmap.data_sport_grey;
                } else
                    picture = R.mipmap.data_sport;
                break;
            case PageManager.PAGE_APP_BLOOD_PRESSURE:
                if (isGrey) {
                    picture = R.mipmap.data_blood_pressure_grey;
                } else
                    picture = R.mipmap.data_blood_pressure;
                break;
            case PageManager.PAGE_APP_MENSTRUAL_PERIOD:
                if (isGrey) {
                    picture = R.mipmap.data_menstrual_period_grey;
                } else
                    picture = R.mipmap.data_menstrual_period;
                break;
            case PageManager.PAGE_APP_BLOOD_OXYGEN:
                if (isGrey) {
                    picture = R.mipmap.data_blood_oxygen_grey;
                } else
                    picture = R.mipmap.data_blood_oxygen;
                break;
            case PageManager.PAGE_APP_TEMPERATURE:
                if (isGrey) {
                    picture = R.mipmap.data_temperature_grey;
                } else
                    picture = R.mipmap.data_temperature;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + index);
        }
        return picture;
    }

    public int getDevicePicture(int id, boolean isGrey) {
        int picture;
        switch (id) {
            case PageManager.PAGE_DEVICE_SETTING:
                if (isGrey) {
                    picture = R.mipmap.page_device_setting_grey;
                } else picture = R.mipmap.page_device_setting;
                break;
            case PageManager.PAGE_DEVICE_FITNESS:
                if (isGrey) {
                    picture = R.mipmap.page_device_fitness_grey;
                } else picture = R.mipmap.page_device_fitness;
                break;
            case PageManager.PAGE_DEVICE_HEART_RATE:
                if (isGrey) {
                    picture = R.mipmap.page_device_heart_rate_grey;
                } else picture = R.mipmap.page_device_heart_rate;
                break;
            case PageManager.PAGE_DEVICE_PRESSURE:
                if (isGrey) {
                    picture = R.mipmap.page_device_pressure_grey;
                } else picture = R.mipmap.page_device_pressure;
                break;
            case PageManager.PAGE_DEVICE_ENERGY:
                if (isGrey) {
                    picture = R.mipmap.page_device_energy_grey;
                } else picture = R.mipmap.page_device_energy;
                break;
            case PageManager.PAGE_DEVICE_SLEEP:
                if (isGrey) {
                    picture = R.mipmap.page_device_sleep_grey;
                } else picture = R.mipmap.page_device_sleep;
                break;
            case PageManager.PAGE_DEVICE_BREATH:
                if (isGrey) {
                    picture = R.mipmap.page_device_breath_grey;
                } else picture = R.mipmap.page_device_breath;
                break;
            case PageManager.PAGE_DEVICE_ANAEROBIC_THRESHOLD:
                if (isGrey) {
                    picture = R.mipmap.page_device_anaerobic_threshold_grey;
                } else picture = R.mipmap.page_device_anaerobic_threshold;
                break;
            case PageManager.PAGE_DEVICE_CALENDAR:
                if (isGrey) {
                    picture = R.mipmap.page_device_calendar_grey;
                } else picture = R.mipmap.page_device_calendar;
                break;
            case PageManager.PAGE_DEVICE_CLOCK:
                if (isGrey) {
                    picture = R.mipmap.page_device_clock_grey;
                } else picture = R.mipmap.page_device_clock;
                break;
            case PageManager.PAGE_DEVICE_STOPWATCH:
                if (isGrey) {
                    picture = R.mipmap.page_device_stopwatch_grey;
                } else picture = R.mipmap.page_device_stopwatch;
                break;
            case PageManager.PAGE_DEVICE_TIME_KEEPING:
                if (isGrey) {
                    picture = R.mipmap.page_device_time_keeping_grey;
                } else picture = R.mipmap.page_device_time_keeping;
                break;
            case PageManager.PAGE_DEVICE_WEATHER:
                if (isGrey) {
                    picture = R.mipmap.page_device_weather_grey;
                } else picture = R.mipmap.page_device_weather;
                break;
            case PageManager.PAGE_DEVICE_STOCK:
                if (isGrey) {
                    picture = R.mipmap.page_device_stock_grey;
                } else picture = R.mipmap.page_device_stock;
                break;
            case PageManager.PAGE_DEVICE_APP_LIST:
                if (isGrey) {
                    picture = R.mipmap.page_device_app_list_grey;
                } else picture = R.mipmap.page_device_app_list;
                break;
            case PageManager.PAGE_DEVICE_SPORT_LIST:
                if (isGrey) {
                    picture = R.mipmap.page_device_sport_list_grey;
                } else picture = R.mipmap.page_device_sport_list;
                break;
            case PageManager.PAGE_DEVICE_HOME:
                if (isGrey) {
                    picture = R.mipmap.page_device_home_grey;
                } else picture = R.mipmap.page_device_home;
                break;
            case PageManager.PAGE_DEVICE_MUSIC_CONTROLLER:
                if (isGrey) {
                    picture = R.mipmap.page_device_music_controller_grey;
                } else picture = R.mipmap.page_device_music_controller;
                break;
            case PageManager.PAGE_DEVICE_VOICE_ASSISTANT:
                if (isGrey) {
                    picture = R.mipmap.page_device_voice_assistant_grey;
                } else picture = R.mipmap.page_device_voice_assistant;
                break;
            case PageManager.PAGE_DEVICE_BLOOD_OXYGEN:
                if (isGrey) {
                    picture = R.mipmap.page_device_blood_oxygen_grey;
                } else picture = R.mipmap.page_device_blood_oxygen;
                break;
            case PageManager.PAGE_DEVICE_ALIPAY:
                if (isGrey) {
                    picture = R.mipmap.page_device_alipay_grey;
                } else picture = R.mipmap.page_device_alipay;
                break;
            case PageManager.PAGE_DEVICE_CARD:
                if (isGrey) {
                    picture = R.mipmap.page_device_card_grey;
                } else picture = R.mipmap.page_device_card;
                break;
            case PageManager.PAGE_DEVICE_DOOR:
                if (isGrey) {
                    picture = R.mipmap.page_device_door_grey;
                } else picture = R.mipmap.page_device_door;
                break;
            case PageManager.PAGE_DEVICE_BUS:
                if (isGrey) {
                    picture = R.mipmap.page_device_bus_grey;
                } else picture = R.mipmap.page_device_bus;
                break;
            case PageManager.PAGE_DEVICE_BANK:
                if (isGrey) {
                    picture = R.mipmap.page_device_bank_grey;
                } else picture = R.mipmap.page_device_bank;
                break;
            case PageManager.PAGE_DEVICE_BLOOD_PRESSURE:
                if (isGrey) {
                    picture = R.mipmap.page_device_blood_pressure_grey;
                } else picture = R.mipmap.page_device_blood_pressure;
                break;
            case PageManager.PAGE_DEVICE_ECG_FUNCTION:
                if (isGrey) {
                    picture = R.mipmap.page_device_ecg_function_grey;
                } else picture = R.mipmap.page_device_ecg_function;
                break;
            case PageManager.PAGE_DEVICE_INFORMATION:
                if (isGrey) {
                    picture = R.mipmap.page_device_information_grey;
                } else picture = R.mipmap.page_device_information;
                break;
            case PageManager.PAGE_DEVICE_ACTIVITY_HEAT:
                if (isGrey) {
                    picture = R.mipmap.page_device_activity_heat_grey;
                } else picture = R.mipmap.page_device_activity_heat;
                break;
            case PageManager.PAGE_DEVICE_STEP_COUNTER:
                if (isGrey) {
                    picture = R.mipmap.page_device_step_counter_grey;
                } else picture = R.mipmap.page_device_step_counter;
                break;
            case PageManager.PAGE_DEVICE_CALORIE:
                if (isGrey) {
                    picture = R.mipmap.page_device_calorie_grey;
                } else picture = R.mipmap.page_device_calorie;
                break;
            case PageManager.PAGE_DEVICE_DISTANCE:
                if (isGrey) {
                    picture = R.mipmap.page_device_distance_grey;
                } else picture = R.mipmap.page_device_distance;
                break;
            case PageManager.PAGE_DEVICE_PHYSIONLOGICAL_CYCLE:
                if (isGrey) {
                    picture = R.mipmap.page_device_physionlogical_cycle_grey;
                } else picture = R.mipmap.page_device_physionlogical_cycle;
                break;
            case PageManager.PAGE_DEVICE_COMPASS:
                if (isGrey) {
                    picture = R.mipmap.page_device_compass_grey;
                } else picture = R.mipmap.page_device_compass;
                break;
            case PageManager.PAGE_DEVICE_ABOUT:
                if (isGrey) {
                    picture = R.mipmap.page_device_about_grey;
                } else picture = R.mipmap.page_device_about;
                break;
            case PageManager.PAGE_DEVICE_MORE_SETTING:
                if (isGrey) {
                    picture = R.mipmap.page_device_more_setting_grey;
                } else picture = R.mipmap.page_device_more_setting;
                break;
            case PageManager.PAGE_DEVICE_ATMOSPHERIC_PRESSURE:
                if (isGrey) {
                    picture = R.mipmap.page_device_atmospheric_pressure_grey;
                } else picture = R.mipmap.page_device_atmospheric_pressure;
                break;
            case PageManager.PAGE_DEVICE_PHYSICAL_EXERCISE:
                if (isGrey) {
                    picture = R.mipmap.page_device_physical_exercise_grey;
                } else picture = R.mipmap.page_device_physical_exercise;
                break;
            case PageManager.PAGE_DEVICE_DAILY_STATE:
                if (isGrey) {
                    picture = R.mipmap.page_device_daily_state_grey;
                } else picture = R.mipmap.page_device_daily_state;
                break;
            case PageManager.PAGE_DEVICE_TOOL_LIST:
                if (isGrey) {
                    picture = R.mipmap.page_device_tool_list_grey;
                } else picture = R.mipmap.page_device_tool_list;
                break;
            case PageManager.PAGE_DEVICE_QR_CODE_DOWNLOAD:
                if (isGrey) {
                    picture = R.mipmap.page_device_qr_code_download_grey;
                } else picture = R.mipmap.page_device_qr_code_download;
                break;
            case PageManager.PAGE_DEVICE_TEMPERATURE:
                if (isGrey) {
                    picture = R.mipmap.page_device_temperature_grey;
                } else picture = R.mipmap.page_device_temperature;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + id);
        }
        return picture;
    }

    public String getDeviceName(int index, Context context) {
        String name = "";
        switch (index) {
            case PageManager.PAGE_HIDE:
                break;
            case PageManager.PAGE_DEVICE_SETTING:
                name = context.getResources().getString(R.string.page_device_setting);
                break;
            case PageManager.PAGE_DEVICE_FITNESS:
                name = context.getResources().getString(R.string.page_device_fitness);
                break;
            case PageManager.PAGE_DEVICE_HEART_RATE:
                name = context.getResources().getString(R.string.page_device_heart_rate);
                break;
            case PageManager.PAGE_DEVICE_PRESSURE:
                name = context.getResources().getString(R.string.page_device_pressure);
                break;
            case PageManager.PAGE_DEVICE_ENERGY:
                name = context.getResources().getString(R.string.page_device_energy);
                break;
            case PageManager.PAGE_DEVICE_SLEEP:
                name = context.getResources().getString(R.string.page_device_sleep);
                break;
            case PageManager.PAGE_DEVICE_BREATH:
                name = context.getResources().getString(R.string.page_device_breath);
                break;
            case PageManager.PAGE_DEVICE_ANAEROBIC_THRESHOLD:
                name = context.getResources().getString(R.string.page_device_anaerobic_threshold);
                break;
            case PageManager.PAGE_DEVICE_CALENDAR:
                name = context.getResources().getString(R.string.page_device_calendar);
                break;
            case PageManager.PAGE_DEVICE_CLOCK:
                name = context.getResources().getString(R.string.page_device_clock);
                break;
            case PageManager.PAGE_DEVICE_STOPWATCH:
                name = context.getResources().getString(R.string.page_device_stopwatch);
                break;
            case PageManager.PAGE_DEVICE_TIME_KEEPING:
                name = context.getResources().getString(R.string.page_device_time_keeping);
                break;
            case PageManager.PAGE_DEVICE_WEATHER:
                name = context.getResources().getString(R.string.page_device_weather);
                break;
            case PageManager.PAGE_DEVICE_STOCK:
                name = context.getResources().getString(R.string.page_device_stock);
                break;
            case PageManager.PAGE_DEVICE_APP_LIST:
                name = context.getResources().getString(R.string.page_device_app_list);
                break;
            case PageManager.PAGE_DEVICE_SPORT_LIST:
                name = context.getResources().getString(R.string.page_device_sport_list);
                break;
            case PageManager.PAGE_DEVICE_HOME:
                name = context.getResources().getString(R.string.page_device_home);
                break;
            case PageManager.PAGE_DEVICE_MUSIC_CONTROLLER:
                name = context.getResources().getString(R.string.page_device_music_controller);
                break;
            case PageManager.PAGE_DEVICE_VOICE_ASSISTANT:
                name = context.getResources().getString(R.string.page_device_voice_assistant);
                break;
            case PageManager.PAGE_DEVICE_BLOOD_OXYGEN:
                name = context.getResources().getString(R.string.page_device_blood_oxygen);
                break;
            case PageManager.PAGE_DEVICE_ALIPAY:
                name = context.getResources().getString(R.string.page_device_alipay);
                break;
            case PageManager.PAGE_DEVICE_CARD:
                name = context.getResources().getString(R.string.page_device_card);
                break;
            case PageManager.PAGE_DEVICE_DOOR:
                name = context.getResources().getString(R.string.page_device_door);
                break;
            case PageManager.PAGE_DEVICE_BUS:
                name = context.getResources().getString(R.string.page_device_bus);
                break;
            case PageManager.PAGE_DEVICE_BANK:
                name = context.getResources().getString(R.string.page_device_bank);
                break;
            case PageManager.PAGE_DEVICE_BLOOD_PRESSURE:
                name = context.getResources().getString(R.string.page_device_blood_pressure);
                break;
            case PageManager.PAGE_DEVICE_ECG_FUNCTION:
                name = context.getResources().getString(R.string.page_device_ecg_function);
                break;
            case PageManager.PAGE_DEVICE_INFORMATION:
                name = context.getResources().getString(R.string.page_device_information);
                break;
            case PageManager.PAGE_DEVICE_ACTIVITY_HEAT:
                name = context.getResources().getString(R.string.page_device_activity_heat);
                break;
            case PageManager.PAGE_DEVICE_STEP_COUNTER:
                name = context.getResources().getString(R.string.page_device_step_counter);
                break;
            case PageManager.PAGE_DEVICE_CALORIE:
                name = context.getResources().getString(R.string.page_device_calorie);
                break;
            case PageManager.PAGE_DEVICE_DISTANCE:
                name = context.getResources().getString(R.string.page_device_distance);
                break;
            case PageManager.PAGE_DEVICE_PHYSIONLOGICAL_CYCLE:
                name = context.getResources().getString(R.string.page_device_physionlogical_cycle);
                break;
            case PageManager.PAGE_DEVICE_COMPASS:
                name = context.getResources().getString(R.string.page_device_compass);
                break;
            case PageManager.PAGE_DEVICE_ABOUT:
                name = context.getResources().getString(R.string.page_device_about);
                break;
            case PageManager.PAGE_DEVICE_MORE_SETTING:
                name = context.getResources().getString(R.string.page_device_more_setting);
                break;
            case PageManager.PAGE_DEVICE_ATMOSPHERIC_PRESSURE:
                name = context.getResources().getString(R.string.page_device_atmospheric_pressure);
                break;
            case PageManager.PAGE_DEVICE_PHYSICAL_EXERCISE:
                name = context.getResources().getString(R.string.page_device_physical_exercise);
                break;
            case PageManager.PAGE_DEVICE_DAILY_STATE:
                name = context.getResources().getString(R.string.page_device_daily_state);
                break;
            case PageManager.PAGE_DEVICE_TOOL_LIST:
                name = context.getResources().getString(R.string.page_device_tool_list);
                break;
            case PageManager.PAGE_DEVICE_QR_CODE_DOWNLOAD:
                name = context.getResources().getString(R.string.page_device_qr_code_download);
                break;
            case PageManager.PAGE_DEVICE_TEMPERATURE:
                name = context.getResources().getString(R.string.page_device_temperature);
                break;
        }
        return name;
    }

    public ArrayList<Integer> pageApp2List = new ArrayList<>();// 第二种app首页布局
    public final static int PAGE_APP2_DISTANCE = 1;
    public final static int PAGE_APP2_STEPS = 2;
    public final static int PAGE_APP2_CAL = 3;
    public final static int PAGE_APP2_WATER = 4;
    public final static int PAGE_APP2_SLEEP = 5;
    public final static int PAGE_APP2_TEMPERATURE = 6;
    public final static int PAGE_APP2_BLOOD_OXYGEN = 7;

}
