package com.zjw.apps3pluspro.module.device.weather;

import java.util.HashMap;
import java.util.Map;


public class CondCodeMap {
    static Map<String, String> map;
    private static final String KEY0 = "0";  //晴天
    private static final String KEY1 = "1";  //多云
    private static final String KEY2 = "2";  //阴天
    private static final String KEY3 = "3";  //阵雨
    private static final String KEY4 = "4";  //雷阵雨
    private static final String KEY5 = "5";  //小雨
    private static final String KEY6 = "6";  //中雨
    private static final String KEY7 = "7";  //大雨
    private static final String KEY8 = "8";  //小雪
    private static final String KEY9 = "9";  //中雪
    private static final String KEY10 = "10";  //大雪
    private static final String KEY11 = "11";  //未知

    static {
        if (map == null) {
            map = new HashMap<String, String>();
            //晴天
            map.put("100", KEY0);
            //多云
            map.put("101", KEY1);
            map.put("102", KEY1);
            map.put("103", KEY1);
            map.put("200", KEY1);
            map.put("201", KEY1);
            map.put("202", KEY1);
            map.put("203", KEY1);
            map.put("204", KEY1);
            map.put("205", KEY1);
            map.put("206", KEY1);
            map.put("207", KEY1);
            map.put("208", KEY1);
            map.put("209", KEY1);
            map.put("210", KEY1);
            map.put("211", KEY1);
            map.put("212", KEY1);
            map.put("213", KEY1);
            //阴天
            map.put("500", KEY2);
            map.put("501", KEY2);
            map.put("502", KEY2);
            map.put("503", KEY2);
            map.put("504", KEY2);
            map.put("507", KEY2);
            map.put("508", KEY2);
            map.put("509", KEY2);
            map.put("510", KEY2);
            map.put("511", KEY2);
            map.put("512", KEY2);
            map.put("513", KEY2);
            map.put("514", KEY2);
            map.put("515", KEY2);
            map.put("900", KEY2);
            map.put("901", KEY2);
            map.put("104", KEY2);
            //阵雨
            map.put("300", KEY3);
            map.put("301", KEY3);
            //雷阵雨
            map.put("302", KEY4);
            map.put("303", KEY4);
            map.put("304", KEY4);
            //小雨
            map.put("305", KEY5);
            map.put("514", KEY5);
            map.put("309", KEY5);
            //中雨
            map.put("306", KEY6);
            map.put("315", KEY6);
            map.put("399", KEY6);
            //大雨
            map.put("307", KEY7);
            map.put("308", KEY7);
            map.put("310", KEY7);
            map.put("311", KEY7);
            map.put("312", KEY7);
            map.put("313", KEY7);
            map.put("316", KEY7);
            map.put("317", KEY7);
            map.put("318", KEY7);
            //小雪
            map.put("400", KEY8);
            map.put("408", KEY8);
            //中雪
            map.put("401", KEY9);
            map.put("409", KEY9);
            map.put("499", KEY9);
            //中雪
            map.put("402", KEY10);
            map.put("403", KEY10);
            map.put("404", KEY10);
            map.put("405", KEY10);
            map.put("406", KEY10);
            map.put("407", KEY10);
            map.put("410", KEY10);
            //未知
            map.put("999", KEY11);

        }
    }

    public static String getCondCode(String condCode) {
        String val = map.get(condCode);
        if (val != null && !val.isEmpty()) {
            return val;
        } else {
            return KEY11;
        }
    }

}
