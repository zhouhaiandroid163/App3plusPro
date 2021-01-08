package com.zjw.apps3pluspro.bleservice.anaylsis;

import com.xiaomi.wear.protobuf.FactoryProtos;
import com.xiaomi.wear.protobuf.WearProtos;
import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.utils.SysUtils;

public class FactoryTools {

    static final int FactoryMode = 1; //工厂模式
    static final int NfcInfoList = 2; //id
    static final int FileInfo = 3; //ids


    public static String analysisFactory(WearProtos.WearPacket wear) {

        FactoryProtos.Factory factory = wear.getFactory();
        int wear_id = wear.getId();
        int pos = factory.getPayloadCase().getNumber();

        String result_str = "";

        System.out.println("数据封装 = " + "wear/type = " + wear.getType());
        System.out.println("数据封装 = " + "wear/id = " + wear.getId());
        System.out.println("数据封装 = " + "pos = " + pos);

        result_str += "wear/type = " + wear.getType() + "\n";
        result_str += "wear/id = " + wear.getId() + "\n";
        result_str += "pos = " + pos + "\n";

        result_str += "描述(参考):工厂相关" + "-";

        if (wear_id == 0x00) {
            result_str += "设置模式" + "\n";
        } else if (wear_id == 0x01) {
            result_str += "配置NFC" + "\n";
        } else if (wear_id == 0x02) {
            result_str += "存储文件" + "\n";
        } else {
            result_str += "未知" + "\n";
        }


        switch (pos) {
            case FactoryMode:
                System.out.println("工厂模式");
                result_str += "工厂模式" + "\n";

                System.out.println("数据封装 = " + "factory/" + "/mode = " + factory.getMode());
                result_str += "factory/" + "/mode = " + factory.getMode() + "\n";

                break;

            case NfcInfoList:
                System.out.println("NFC列表");
                result_str += "NFC列表" + "\n";

                FactoryProtos.NfcInfo.List nfc_info_list = factory.getNfcList();


                for (int i = 0; i < nfc_info_list.getListCount(); i++) {


                    FactoryProtos.NfcInfo nfc_info = nfc_info_list.getList(i);

                    System.out.println("数据封装 = " + "factory/" + "NfcInfoList/pos = " + i + " =========");
                    System.out.println("数据封装 = " + "factory/" + "NfcInfo=====");
                    System.out.println("数据封装 = " + "factory/" + "NfcInfo/key = " + nfc_info.getKey());
                    System.out.println("数据封装 = " + "factory/" + "NfcInfo/value = " + nfc_info.getValue());

                    result_str += "factory" + "NfcInfoList/pos = " + i + " =========" + "\n";
                    result_str += "factory" + "factory/" + "NfcInfo=====" + "\n";
                    result_str += "factory" + "factory/" + "NfcInfo/key = " + nfc_info.getKey() + "\n";
                    result_str += "factory" + "factory/" + "NfcInfo/value " + nfc_info.getValue() + "\n";

                }

                break;

            case FileInfo:
                System.out.println("文件");
                result_str += "文件" + "\n";

                FactoryProtos.FileInfo file_info = factory.getFile();
                System.out.println("数据封装 = " + "factory/" + "FileInfo=====");
                System.out.println("数据封装 = " + "factory/" + "FileInfo/name = " + file_info.getName());
                System.out.println("数据封装 = " + "factory/" + "FileInfo/is_append = " + file_info.getIsAppend());
                System.out.println("数据封装 = " + "factory/" + "FileInfo/data = " + file_info.getData());

                result_str += "factory" + "factory/" + "FileInfo=====" + "\n";
                result_str += "factory" + "factory/" + "FileInfo/name = " + file_info.getName() + "\n";
                result_str += "factory" + "factory/" + "FileInfo/is_append  = " + file_info.getIsAppend() + "\n";
                result_str += "factory" + "factory/" + "FileInfo/data  = " + file_info.getData() + "\n";
                result_str += "factory" + "factory/" + "FileInfo/data  = " + BleTools.printHexString(file_info.getData().toByteArray()) + "\n";

                SysUtils.logDeviceContentI("Device log", file_info.getName());
                SysUtils.logDeviceContentI("Device log", SysUtils.hexStr2Str(SysUtils.printHexString(file_info.getData().toByteArray()).replace(" ", "")));
                break;

        }

        return result_str;
    }


}
