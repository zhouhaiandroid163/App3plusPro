package com.zjw.apps3pluspro.bleservice.anaylsis;

import com.xiaomi.wear.protobuf.CalendarProtos;
import com.xiaomi.wear.protobuf.WearProtos;

public class CalendarTools {

    static final int CalendarInfo = 1; //日历详情
    static final int CalendarInfoList = 2; //日历详情列表


    public static String analysisCalendar(WearProtos.WearPacket wear) {

        CalendarProtos.Calendar calendar = wear.getCalendar();
//        int id = wear.getId();
        int pos = calendar.getPayloadCase().getNumber();

        String result_str = "";

        System.out.println("数据封装 = " + "wear/type = " + wear.getType());
        System.out.println("数据封装 = " + "wear/id = " + wear.getId());
        System.out.println("数据封装 = " + "pos = " + pos);

        result_str += "wear/type = " + wear.getType() + "\n";
        result_str += "wear/id = " + wear.getId() + "\n";
        result_str += "pos = " + pos + "\n";


        switch (pos) {
            case CalendarInfo:
                System.out.println("日历详情");
                result_str += "日历详情" + "\n";

                CalendarProtos.CalendarInfo calendar_info = calendar.getCalendarInfo();
                System.out.println("数据封装 = " + "calendar/" + "calendar_info=====");
                System.out.println("数据封装 = " + "calendar/" + "calendar_info/title = " + calendar_info.getTitle());
                System.out.println("数据封装 = " + "calendar/" + "calendar_info/description = " + calendar_info.getDescription());
                System.out.println("数据封装 = " + "calendar/" + "calendar_info/location = " + calendar_info.getLocation());
                System.out.println("数据封装 = " + "calendar/" + "calendar_info/start = " + calendar_info.getStart());
                System.out.println("数据封装 = " + "calendar/" + "calendar_info/end = " + calendar_info.getEnd());
                System.out.println("数据封装 = " + "calendar/" + "calendar_info/all_day = " + calendar_info.getAllDay());
                System.out.println("数据封装 = " + "calendar/" + "calendar_info/reminder_minutes = " + calendar_info.getReminderMinutes());

                result_str += "calendar" + "/calendar_info/calendar_info=====" + "\n";
                result_str += "calendar" + "/calendar_info/title = " + calendar_info.getTitle() + "\n";
                result_str += "calendar" + "/calendar_info/description = " + calendar_info.getDescription() + "\n";
                result_str += "calendar" + "/calendar_info/location = " + calendar_info.getLocation() + "\n";
                result_str += "calendar" + "/calendar_info/start = " + calendar_info.getStart() + "\n";
                result_str += "calendar" + "/calendar_info/end = " + calendar_info.getEnd() + "\n";
                result_str += "calendar" + "/calendar_info/all_day = " + calendar_info.getAllDay() + "\n";
                result_str += "calendar" + "/calendar_info/reminder_minutes = " + calendar_info.getReminderMinutes() + "\n";


                break;

            case CalendarInfoList:
                System.out.println("日历详情列表");
                result_str += "日历详情列表" + "\n";

                CalendarProtos.CalendarInfo.List calendar_info_list = calendar.getCalendarInfoList();

                for (int i = 0; i < calendar_info_list.getListCount(); i++) {

                    CalendarProtos.CalendarInfo calendar_info_info = calendar_info_list.getList(i);

                    System.out.println("数据封装 = " + "calendar/" + "calendar_info/pos = " + i + " =========");

                    System.out.println("数据封装 = " + "calendar/" + "calendar_info=====");
                    System.out.println("数据封装 = " + "calendar/" + "calendar_info/title = " + calendar_info_info.getTitle());
                    System.out.println("数据封装 = " + "calendar/" + "calendar_info/description = " + calendar_info_info.getDescription());
                    System.out.println("数据封装 = " + "calendar/" + "calendar_info/location = " + calendar_info_info.getLocation());
                    System.out.println("数据封装 = " + "calendar/" + "calendar_info/start = " + calendar_info_info.getStart());
                    System.out.println("数据封装 = " + "calendar/" + "calendar_info/end = " + calendar_info_info.getEnd());
                    System.out.println("数据封装 = " + "calendar/" + "calendar_info/all_day = " + calendar_info_info.getAllDay());
                    System.out.println("数据封装 = " + "calendar/" + "calendar_info/reminder_minutes = " + calendar_info_info.getReminderMinutes());

                    result_str += "calendar" + "/calendar_info/calendar_info=====" + "\n";
                    result_str += "calendar" + "/calendar_info/title = " + calendar_info_info.getTitle() + "\n";
                    result_str += "calendar" + "/calendar_info/description = " + calendar_info_info.getDescription() + "\n";
                    result_str += "calendar" + "/calendar_info/location = " + calendar_info_info.getLocation() + "\n";
                    result_str += "calendar" + "/calendar_info/start = " + calendar_info_info.getStart() + "\n";
                    result_str += "calendar" + "/calendar_info/end = " + calendar_info_info.getEnd() + "\n";
                    result_str += "calendar" + "/calendar_info/all_day = " + calendar_info_info.getAllDay() + "\n";
                    result_str += "calendar" + "/calendar_info/reminder_minutes = " + calendar_info_info.getReminderMinutes() + "\n";

                }


                break;


        }

        return result_str;
    }


}
