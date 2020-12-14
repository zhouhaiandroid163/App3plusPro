package com.zjw.apps3pluspro.bleservice.anaylsis;


import com.xiaomi.wear.protobuf.AivsProtos;
import com.xiaomi.wear.protobuf.WearProtos;

public class AivsTools {


    static final int AivsStatus = 1; //状态
    static final int AivsError = 2; //错误
    static final int AivsInstruction = 3; //指令
    static final int AivsInstructionList = 4; //指令列表


    public static String analysisAivs(WearProtos.WearPacket wear) {

        AivsProtos.Aivs aivs = wear.getAivs();
        int id = wear.getId();
        int pos = aivs.getPayloadCase().getNumber();

        String result_str = "";

        System.out.println("数据封装 = " + "wear/type = " + wear.getType());
        System.out.println("数据封装 = " + "wear/id = " + wear.getId());
        System.out.println("数据封装 = " + "pos = " + pos);

        result_str += "wear/type = " + wear.getType() + "\n";
        result_str += "wear/id = " + wear.getId() + "\n";
        result_str += "pos = " + pos + "\n";

//        AccountProtos.Account.BIND_INFO_FIELD_NUMBER;

        result_str += "描述(参考):语音相关" + "-";

        if (id == 0x00) {
            result_str += "同步状态" + "\n";
        } else if (id == 0x01) {
            result_str += "同步错误" + "\n";
        } else if (id == 0x02) {
            result_str += "同步指令" + "\n";
        } else if (id == 0x03) {
            result_str += "同步指令列表，多个指令？" + "\n";
        } else {
            result_str += "未知" + "\n";
        }

        switch (pos) {
            case AivsStatus:
                System.out.println("状态");
                result_str += "状态" + "\n";

//                System.out.println("数据封装 = " + "aivs/" + "/status = " + aivs.getStatus());
//                result_str += "aivs" + "/status = " + aivs.getStatus() + "\n";

                break;

            case AivsError:
                System.out.println("错误");
                result_str += "错误" + "\n";

//                System.out.println("数据封装 = " + "aivs/" + "/error = " + aivs.getError());
//                result_str += "aivs" + "/error = " + aivs.getError() + "\n";

                break;

            case AivsInstruction:
                System.out.println("指令");
                result_str += "指令" + "\n";

//                AivsProtos.AivsInstruction instruction_info = aivs.getInstruction();
//                System.out.println("数据封装 = " + "aivs/" + "/instruction===========");
//                System.out.println("数据封装 = " + "aivs/" + "/instruction/name_space(名称空间) = " + instruction_info.getNameSpace());
//                System.out.println("数据封装 = " + "aivs/" + "/instruction/name = " + device_info.getFirmwareVersion());
//                System.out.println("数据封装 = " + "aivs/" + "/instruction/id = " + device_info.getFirmwareVersion());
//                System.out.println("数据封装 = " + "aivs/" + "/instruction/dialog_id = " + device_info.getFirmwareVersion());
//                System.out.println("数据封装 = " + "aivs/" + "/instruction/name = " + device_info.getFirmwareVersion());
//
//                result_str += "aivs/" + "/instruction===========" + "\n";
//                result_str += "system/" + "/device_info/instruction/name_space(名称空间) = " + instruction_info.getNameSpace() + "\n";

                break;

            case AivsInstructionList:
                System.out.println("指令列表");
                result_str += "指令列表" + "\n";
////
//                SystemProtos.SystemTime system_time = system.getSystemTime();
//                System.out.println("数据封装 = " + "system/" + "SystemTime=====");
//
//                result_str += "system" + "/SystemTime/date======" + "\n";
//                result_str += CommonTools.getDate(system_time.getDate());
//
//
//                result_str += "system" + "/SystemTime/time======" + "\n";
//                result_str += CommonTools.getTime(system_time.getTime());
//
//                result_str += "system" + "/SystemTime/TimeZone======" + "\n";
//                result_str += CommonTools.getTimezone(system_time.getTimeZone());


                break;


        }
        return result_str;

    }


}
