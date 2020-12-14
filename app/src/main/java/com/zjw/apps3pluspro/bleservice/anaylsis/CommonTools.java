package com.zjw.apps3pluspro.bleservice.anaylsis;

import com.xiaomi.wear.protobuf.CommonProtos;

public class CommonTools {

    public static CommonProtos.KeyValue getKeyValue(String key, int value) {

        CommonProtos.KeyValue.Builder key_value = CommonProtos.KeyValue.newBuilder();
        key_value.setKey(key);
        key_value.setValue(value);

        return key_value.build();
    }


//    public static String getKeyValue(String title, CommonProtos.KeyValue key_value) {
//
//        String result_str = "";
//        System.out.println("数据封装 = " + "common/" + title + "=======");
//        System.out.println("数据封装 = " + "common/" + "KeyValue/Key = " + key_value.getKey());
//        System.out.println("数据封装 = " + "common/" + "KeyValue/Value = " + key_value.getValue());
//
//        result_str += "common" + "/KeyValue/Key = = " + key_value.getKey() + "\n";
//        result_str += "common" + "/KeyValue/Value = " + key_value.getValue() + "\n";
//
//
//        return result_str;
//
//    }

    public static String getKeyValue(CommonProtos.KeyValue key_value) {

        String result_str = "";
        System.out.println("数据封装 = " + "common/" + "KeyValue/Key = " + key_value.getKey());
        System.out.println("数据封装 = " + "common/" + "KeyValue/Value = " + key_value.getValue());

        result_str += "common" + "/KeyValue/Key = " + key_value.getKey() + "\n";
        result_str += "common" + "/KeyValue/Value = " + key_value.getValue() + "\n";


        return result_str;

    }

    public static CommonProtos.RangeValue getRangeValue(int from, int to) {

        CommonProtos.RangeValue.Builder range_value = CommonProtos.RangeValue.newBuilder();
        range_value.setFrom(from);
        range_value.setTo(to);

        return range_value.build();
    }

    public static String getRangeValue(CommonProtos.RangeValue reange_value) {
        String result_str = "";
        System.out.println("数据封装 = " + "common/" + "RangeValue======");
        System.out.println("数据封装 = " + "common/" + "RangeValue/from = " + reange_value.getFrom());
        System.out.println("数据封装 = " + "common/" + "RangeValue/to = " + reange_value.getTo());

        result_str += "common/" + "RangeValue======" + "\n";
        result_str += "common/" + "RangeValue/from = " + reange_value.getFrom() + "\n";
        result_str += "common/" + "RangeValue/to = " + reange_value.getTo() + "\n";
        return result_str;

    }

    public static String getDate(CommonProtos.Date date) {

        String result_str = "";
        System.out.println("数据封装 = " + "common/" + "Date======");
        System.out.println("数据封装 = " + "common/" + "Date/year = " + date.getYear());
        System.out.println("数据封装 = " + "common/" + "Date/month = " + date.getMonth());
        System.out.println("数据封装 = " + "common/" + "Date/day = " + date.getDay());

        result_str += "common/" + "Date======" + "\n";
        result_str += "common" + "/Date/year = " + date.getYear() + "\n";
        result_str += "common" + "/Date/month = " + date.getMonth() + "\n";
        result_str += "common" + "/Date/day = " + date.getDay() + "\n";


        return result_str;

    }

    public static String getTime(CommonProtos.Time time) {

        String result_str = "";
        System.out.println("数据封装 = " + "common/" + "Time======");
        System.out.println("数据封装 = " + "common/" + "Time/hour = " + time.getHour());
        System.out.println("数据封装 = " + "common/" + "Time/minuter = " + time.getMinuter());
        System.out.println("数据封装 = " + "common/" + "Time/second = " + time.getSecond());
        System.out.println("数据封装 = " + "common/" + "Time/millisecond = " + time.getMillisecond());

        result_str += "common/" + "Time======" + "\n";
        result_str += "common" + "/Time/hour = " + time.getHour() + "\n";
        result_str += "common" + "/Time/minuter = " + time.getMinuter() + "\n";
        result_str += "common" + "/Time/second = " + time.getSecond() + "\n";
        result_str += "common" + "/Time/millisecond = " + time.getMillisecond() + "\n";


        return result_str;

    }


    public static String getTimezone(CommonProtos.Timezone time_zone) {

        String result_str = "";
        System.out.println("数据封装 = " + "common" + "/Timezone======");
        System.out.println("数据封装 = " + "common" + "/Timezone/offset = " + time_zone.getOffset());
        System.out.println("数据封装 = " + "common" + "/Timezone/dst_saving = " + time_zone.getDstSaving());
        System.out.println("数据封装 = " + "common" + "/Timezone/id = " + time_zone.getId());

        result_str += "common" + "/Timezone======" + "\n";
        result_str += "common" + "/Timezone/offset = " + time_zone.getOffset() + "\n";
        result_str += "common" + "/Timezone/dst_saving = " + time_zone.getDstSaving() + "\n";
        result_str += "common" + "/Timezone/id = " + time_zone.getId() + "\n";

        return result_str;

    }

}
