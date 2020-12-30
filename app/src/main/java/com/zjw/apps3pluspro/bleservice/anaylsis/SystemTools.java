package com.zjw.apps3pluspro.bleservice.anaylsis;


import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.xiaomi.wear.protobuf.CommonProtos;
import com.xiaomi.wear.protobuf.SystemProtos;
import com.xiaomi.wear.protobuf.WearProtos;
import com.zjw.apps3pluspro.eventbus.GetDeviceProtoOtaPrepareStatusSuccessEvent;
import com.zjw.apps3pluspro.eventbus.PageDeviceSyncOverEvent;
import com.zjw.apps3pluspro.module.home.entity.PageItem;
import com.zjw.apps3pluspro.utils.NewTimeUtils;
import com.zjw.apps3pluspro.utils.PageManager;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.greenrobot.eventbus.EventBus;

public class SystemTools {


    static final int RESET = 1; //重置模式
    static final int GET_DEVICE_STATUS = 2; //设备状态
    static final int GET_DEVICE_INFO = 3; //设备信息
    static final int SET_SYSTEM_TIME = 4; //系统时间
    static final int FindMode = 5; //找手机模式

    static final int RaiseWristBrightScreen = 7; //设置抬腕亮屏
    static final int Widget = 8; //小装置
    static final int WidgetList = 9; //小装置列表
    static final int Shortcut = 12; //快捷方式
    private static final String TAG = SystemTools.class.getSimpleName();


    public static String analysisSystem(WearProtos.WearPacket wear) {

        SystemProtos.System system = wear.getSystem();
        int id = wear.getId();
        int pos = system.getPayloadCase().getNumber();

        String result_str = "";
        String msg = "";

        System.out.println("数据封装 = " + "wear/type = " + wear.getType());
        System.out.println("数据封装 = " + "wear/id = " + wear.getId());
        System.out.println("数据封装 = " + "pos = " + pos);

        result_str += "wear/type = " + wear.getType() + "\n";
        result_str += "wear/id = " + wear.getId() + "\n";
        result_str += "pos = " + pos + "\n";

//        AccountProtos.Account.BIND_INFO_FIELD_NUMBER;

        result_str += "描述(参考):系统相关" + "-";
        msg += "描述(参考):系统相关" + "-";

        if (id == 0x00) {
            result_str += "重置" + "\n";
            msg += "重置" + "\n";
        } else if (id == 0x01) {
            result_str += "获取设备状态" + "\n";
            msg += "获取设备状态" + "\n";
        } else if (id == 0x02) {
            result_str += "获取设备信息" + "\n";
            msg += "获取设备信息" + "\n";
        } else if (id == 0x03) {
            result_str += "置设备时间-同步时间" + "\n";
            msg += "置设备时间-同步时间" + "\n";
        } else if (id == 0x10) {
            result_str += "解绑" + "\n";
            msg += "解绑" + "\n";
        } else if (id == 0x11) {
            result_str += "找手机" + "\n";
            msg += "找手机" + "\n";
        } else if (id == 0x12) {
            result_str += "找手环" + "\n";
            msg += "找手环" + "\n";
        } else if (id == 0x1A) {
            result_str += "设置小部件" + "\n";
            msg += "设置小部件" + "\n";
        } else if (id == 0x1B) {
            result_str += "设置小部件列表" + "\n";
            msg += "设置小部件列表" + "\n";
        } else if (id == 0x1C) {
            result_str += "获取小部件列表" + "\n";
            msg += "获取小部件列表" + "\n";
        } else if (id == 0x20) {
            result_str += "设置快捷方式1" + "\n";
            msg += "设置快捷方式1" + "\n";
        } else if (id == 0x21) {
            result_str += "设置快捷方式2" + "\n";
            msg += "设置快捷方式2" + "\n";
        } else if (id == 0x22) {
            result_str += "设置快捷方式3" + "\n";
            msg += "设置快捷方式3" + "\n";
        } else if (id == 0x23) {
            result_str += "获取快捷方式1" + "\n";
            msg += "获取快捷方式1" + "\n";
        } else if (id == 0x24) {
            result_str += "获取快捷方式2" + "\n";
            msg += "获取快捷方式2" + "\n";
        } else if (id == 0x25) {
            result_str += "获取快捷方式2" + "\n";
            msg += "获取快捷方式2" + "\n";
        } else if (id == 0x19) {
            result_str += "设置抬腕亮屏" + "\n";
            msg += "设置抬腕亮屏" + "\n";
        } else {
            result_str += "未知" + "\n";
            msg += "未知" + "\n";
        }

//        BleService.mi_tag = msg;

        switch (pos) {
            case RESET:
                System.out.println("重置模式");
                result_str += "重置模式" + "\n";
                System.out.println("数据封装 = " + "system" + "/reset_mode===========");
                result_str += "system" + "/reset_mode = " + system.getResetMode() + "\n";

                break;

            case GET_DEVICE_STATUS:
                System.out.println("设备状态");
                result_str += "设备状态" + "\n";

                SystemProtos.DeviceStatus device_status = system.getDeviceStatus();
                SystemProtos.DeviceStatus.Battery battery = device_status.getBattery();
                System.out.println("数据封装 = " + "system" + "/device_status/battery(电量)===========");
                System.out.println("数据封装 = " + "system" + "/device_status/battery/capacity(容量) = " + battery.getCapacity());
                System.out.println("数据封装 = " + "system" + "/device_status/battery/ChargeStatus(充电状态) = " + battery.getChargeStatus());
                result_str += "system" + "/device_status/battery===========" + "\n";
                result_str += "system" + "/device_status/battery/capacity = " + battery.getCapacity() + "\n";
                result_str += "system" + "/device_status/battery/ChargeStatus = " + battery.getChargeStatus() + "\n";

//                EventBus.getDefault().post(new BatteryEvent(battery.getCapacity()));

                break;

            case GET_DEVICE_INFO:
                System.out.println("设备信息");

                result_str += "设备信息" + "\n";

                SystemProtos.DeviceInfo device_info = system.getDeviceInfo();
                System.out.println("数据封装 = " + "system" + "/device_info/serial_number = " + device_info.getSerialNumber());
                System.out.println("数据封装 = " + "system" + "/device_info/firmware_version = " + device_info.getFirmwareVersion());
                System.out.println("数据封装 = " + "system" + "/device_info/imei = " + device_info.getImei());

                result_str += "system" + "/device_info/serial_number = " + device_info.getSerialNumber() + "\n";
                result_str += "system" + "/device_info/firmware_version = " + device_info.getFirmwareVersion() + "\n";
                result_str += "system" + "/device_info/imei = " + device_info.getImei() + "\n";

//                EventBus.getDefault().post(new DeviceInfoEvent(device_info.getFirmwareVersion()));

                break;

            case SET_SYSTEM_TIME:
                System.out.println("系统时间");
                result_str += "系统时间" + "\n";
//
                SystemProtos.SystemTime system_time = system.getSystemTime();
                System.out.println("数据封装 = " + "system" + "/SystemTime=====");

                result_str += "system" + "/SystemTime/date======" + "\n";
                result_str += CommonTools.getDate(system_time.getDate());


                result_str += "system" + "/SystemTime/time======" + "\n";
                result_str += CommonTools.getTime(system_time.getTime());

                result_str += "system" + "/SystemTime/TimeZone======" + "\n";
                result_str += CommonTools.getTimezone(system_time.getTimeZone());

                break;

            case FindMode:
                System.out.println("找手机模式");
                result_str += "找手机模式" + "\n";

                System.out.println("数据封装 = " + "system" + "/find_mode = " + system.getFindMode());
                result_str += "system" + "/find_mode = " + system.getFindMode() + "\n";
                break;

            case Widget:
                System.out.println("小装置");
                result_str += "小装置" + "\n";

                SystemProtos.Widget widget = system.getWidget();
                System.out.println("数据封装 = " + "system" + "/Widget=====");
                System.out.println("数据封装 = " + "system" + "/Widget/function = " + widget.getFunction());
                System.out.println("数据封装 = " + "system" + "/Widget/enable = " + widget.getEnable());
                System.out.println("数据封装 = " + "system" + "/Widget/order = " + widget.getOrder());

                result_str += "system" + "/Widget/function = " + widget.getFunction() + "\n";
                result_str += "system" + "/Widget/enable = " + widget.getEnable() + "\n";
                result_str += "system" + "/Widget/order = " + widget.getOrder() + "\n";


                break;

            case WidgetList:
                System.out.println("小装置列表");
                result_str += "小装置列表" + "\n";

                SystemProtos.Widget.List widget_list = system.getWidgetList();

                PageManager.getInstance().cleanAllPageDeviceList();

                for (int i = 0; i < widget_list.getListCount(); i++) {
                    SystemProtos.Widget widget_info = widget_list.getList(i);
//                    System.out.println("数据封装 = " + "system" + "/Widget/pos = " + i + " =========");
//                    System.out.println("数据封装 = " + "system" + "/Widget=====");
//                    System.out.println("数据封装 = " + "system" + "/Widget/function = " + widget_info.getFunction());
//                    System.out.println("数据封装 = " + "system" + "/Widget/enable = " + widget_info.getEnable());
//                    System.out.println("数据封装 = " + "system" + "/Widget/order = " + widget_info.getOrder());
//
//                    result_str += "system" + "/Widget/function = " + widget_info.getFunction() + "\n";
//                    result_str += "system" + "/Widget/enable = " + widget_info.getEnable() + "\n";
//                    result_str += "system" + "/Widget/order = " + widget_info.getOrder() + "\n";

                    int idFunction = widget_info.getFunction();
                    int order = widget_info.getOrder();
                    boolean enable = widget_info.getEnable();
                    Log.i(TAG, "getFunction = " + idFunction + "  getOrder = " + order + "  getEnable = " + enable);

                    PageItem item = new PageItem();
                    item.index = idFunction;
                    item.position = i;

                    if (order >= 0 && order < 10) {
                        PageManager.getInstance().pageDeviceNoMove.add(item);
                    } else if (order >= 10 && order < 100) {
                        if (enable) {
                            PageManager.getInstance().pageDeviceMove.add(item);
                        } else {
                            PageManager.getInstance().pageDeviceHide.add(item);
                        }
                    }
                }
                EventBus.getDefault().post(new PageDeviceSyncOverEvent());
                break;

            case Shortcut:
                System.out.println("快捷方式");
                result_str += "快捷方式" + "\n";

                SystemProtos.Shortcut shortuct = system.getShortcut();
                System.out.println("数据封装 = " + "system" + "/Shortcut=====");
                System.out.println("数据封装 = " + "system" + "/Shortcut/type = " + shortuct.getType());
                System.out.println("数据封装 = " + "system" + "/Shortcut/sub_type = " + shortuct.getSubType());

                break;
            case RaiseWristBrightScreen:
                break;
            case 17:
                SystemProtos.PrepareOta.Response otaResponse = system.getPrepareOtaResponse();
                MyLog.i(TAG, "watch face PrepareStatus = " + otaResponse.getPrepareStatus().getNumber());
                EventBus.getDefault().post(new GetDeviceProtoOtaPrepareStatusSuccessEvent(otaResponse.getPrepareStatus().getNumber()));
                break;

        }
        return result_str;

    }

    /**
     * 获取设备信息
     *
     * @return
     */
    public static byte[] getDeviceInfo() {
        SystemProtos.System.Builder system = SystemProtos.System.newBuilder();

        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.SYSTEM)
                .setId((byte) SystemProtos.System.SystemID.GET_DEVICE_INFO.getNumber())
                .setSystem(system);

        try {
            WearProtos.WearPacket wear = WearProtos.WearPacket.parseFrom(wear1.build().toByteArray());
            SystemTools.analysisSystem(wear);

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return wear1.build().toByteArray();
    }


    /**
     * 获取设备状态
     *
     * @return
     */
    public static byte[] getDeviceState() {
        SystemProtos.System.Builder system = SystemProtos.System.newBuilder();

        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.SYSTEM)
                .setId((byte) SystemProtos.System.SystemID.GET_DEVICE_STATUS.getNumber())
                .setSystem(system);

        try {
            WearProtos.WearPacket wear = WearProtos.WearPacket.parseFrom(wear1.build().toByteArray());
            SystemTools.analysisSystem(wear);

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }


        return wear1.build().toByteArray();
    }


    /**
     * 同步时间
     *
     * @return
     */
    public static byte[] getSystemTime() {
        SystemProtos.System.Builder system = SystemProtos.System.newBuilder();

        system.setSystemTime(getDataSystemTime());

        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.SYSTEM)
                .setId((byte) SystemProtos.System.SystemID.SET_SYSTEM_TIME.getNumber())
                .setSystem(system);

        try {
            WearProtos.WearPacket wear = WearProtos.WearPacket.parseFrom(wear1.build().toByteArray());
            SystemTools.analysisSystem(wear);

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }


        return wear1.build().toByteArray();
    }

    public static byte[] setRaiseWristBrightScreen() {
        SystemProtos.System.Builder system = SystemProtos.System.newBuilder();
        SystemProtos.WristScreen.Builder wristScreen = SystemProtos.WristScreen.newBuilder();
        wristScreen.setTimingMode(SystemProtos.TimingMode.ALL_DAY_ON);
        wristScreen.build();
        system.setWristScreen(wristScreen);
        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.SYSTEM)
                .setId((byte) SystemProtos.System.SystemID.SET_WRIST_SCREEN.getNumber())
                .setSystem(system);
        try {
            WearProtos.WearPacket wear = WearProtos.WearPacket.parseFrom(wear1.build().toByteArray());
            SystemTools.analysisSystem(wear);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return wear1.build().toByteArray();
    }

    public static byte[] setReset() {
        SystemProtos.System.Builder system = SystemProtos.System.newBuilder();
        system.setResetMode(SystemProtos.ResetMode.ERASE_ALL);
        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.SYSTEM)
                .setId((byte) SystemProtos.System.SystemID.RESET.getNumber())
                .setSystem(system);
        try {
            WearProtos.WearPacket wear = WearProtos.WearPacket.parseFrom(wear1.build().toByteArray());
            SystemTools.analysisSystem(wear);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return wear1.build().toByteArray();
    }

    public static byte[] getPower() {
        SystemProtos.System.Builder system = SystemProtos.System.newBuilder();

        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.SYSTEM)
                .setId((byte) SystemProtos.System.SystemID.GET_DEVICE_STATUS.getNumber())
                .setSystem(system);
        try {
            WearProtos.WearPacket wear = WearProtos.WearPacket.parseFrom(wear1.build().toByteArray());
            SystemTools.analysisSystem(wear);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return wear1.build().toByteArray();
    }

    //================获取模拟的数据==============

    /**
     * 同步系统时间
     *
     * @return
     */
    public static SystemProtos.SystemTime getDataSystemTime() {

        SystemProtos.SystemTime.Builder system_time = SystemProtos.SystemTime.newBuilder();

        String[] my_date = NewTimeUtils.getAllTime2().split("_");

        CommonProtos.Date.Builder date = CommonProtos.Date.newBuilder();
        date.setYear(Integer.valueOf(my_date[0]));
        date.setMonth(Integer.valueOf(my_date[1]));
        date.setDay(Integer.valueOf(my_date[2]));

        CommonProtos.Time.Builder time = CommonProtos.Time.newBuilder();
        time.setHour(Integer.valueOf(my_date[3]));
        time.setMinuter(Integer.valueOf(my_date[4]));
        time.setSecond(Integer.valueOf(my_date[5]));
        CommonProtos.Timezone.Builder time_zone = CommonProtos.Timezone.newBuilder();
        time_zone.setOffset(32);
        time_zone.setDstSaving(0);
//        time_zone.setId()

        system_time.setDate(date);
        system_time.setTime(time);
        system_time.setTimeZone(time_zone);


        return system_time.build();
    }


    public static byte[] getPageDevice() {
        SystemProtos.System.Builder system = SystemProtos.System.newBuilder();

        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.SYSTEM)
                .setId((byte) SystemProtos.System.SystemID.GET_WIDGET_LIST.getNumber())
                .setSystem(system);
        return wear1.build().toByteArray();
    }

    public static byte[] getPageDeviceSet() {
        SystemProtos.Widget.List.Builder list = SystemProtos.Widget.List.newBuilder();

        int m = 0;
        for (int i = 0; i < PageManager.getInstance().pageDeviceNoMove.size(); i++) {
            PageItem pageItem = PageManager.getInstance().pageDeviceNoMove.get(i);
            SystemProtos.Widget.Builder widget = SystemProtos.Widget.newBuilder();
            widget.setFunction(pageItem.index);
            widget.setEnable(true);
            widget.setOrder(m);
            list.addList(widget);
            m++;
        }

        PageManager.getInstance().pageDeviceMove.clear();
        PageManager.getInstance().pageDeviceHide.clear();
        boolean isMark = false;
        for (int i = 0; i < PageManager.getInstance().pageDeviceList.size(); i++) {
            PageItem item = PageManager.getInstance().pageDeviceList.get(i);
            if (item.isMark) {
                isMark = true;
            } else {
                if (isMark) {
                    PageManager.getInstance().pageDeviceHide.add(item);
                } else {
                    PageManager.getInstance().pageDeviceMove.add(item);
                }
            }
        }

        m = 10;
        for (int i = 0; i < PageManager.getInstance().pageDeviceMove.size(); i++) {
            PageItem pageItem = PageManager.getInstance().pageDeviceMove.get(i);
            SystemProtos.Widget.Builder widget = SystemProtos.Widget.newBuilder();
            widget.setFunction(pageItem.index);
            widget.setEnable(true);
            widget.setOrder(10 + i);
            list.addList(widget);
            m++;
        }

        for (int i = 0; i < PageManager.getInstance().pageDeviceHide.size(); i++) {
            PageItem pageItem = PageManager.getInstance().pageDeviceHide.get(i);
            SystemProtos.Widget.Builder widget = SystemProtos.Widget.newBuilder();
            widget.setFunction(pageItem.index);
            widget.setEnable(false);
            widget.setOrder(m + i);
            list.addList(widget);
        }

        SystemProtos.System.Builder system = SystemProtos.System.newBuilder();
        system.setWidgetList(list);
        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.SYSTEM)
                .setId((byte) SystemProtos.System.SystemID.SET_WIDGET_LIST.getNumber())
                .setSystem(system);
        try {
            WearProtos.WearPacket wear = WearProtos.WearPacket.parseFrom(wear1.build().toByteArray());
            SystemTools.analysisSystem(wear);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return wear1.build().toByteArray();
    }

    public static byte[] getDeviceOtaPrepareStatus(boolean isForce, String version, String md5) {
        MyLog.i(TAG, "getDeviceOtaPrepareStatus");
        SystemProtos.PrepareOta.Request.Builder otaRequest = SystemProtos.PrepareOta.Request.newBuilder();
        otaRequest.setForce(isForce);
        otaRequest.setType(SystemProtos.PrepareOta.Type.ALL);
        otaRequest.setFirmwareVersion(version);
        otaRequest.setFileMd5(md5);

        SystemProtos.System.Builder system = SystemProtos.System.newBuilder();
        system.setPrepareOtaRequest(otaRequest);
        WearProtos.WearPacket.Builder wear = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.SYSTEM)
                .setId((byte) SystemProtos.System.SystemID.PREPARE_OTA.getNumber())
                .setSystem(system);
        return wear.build().toByteArray();
    }


}
