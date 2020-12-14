package com.zjw.apps3pluspro.bleservice.anaylsis;

import com.google.protobuf.InvalidProtocolBufferException;
import com.xiaomi.wear.protobuf.NotificationProtos;
import com.xiaomi.wear.protobuf.WearProtos;
import com.zjw.apps3pluspro.utils.NewTimeUtils;

import java.util.ArrayList;

public class NotificationTools {

    static final int NotifyData = 1; //推送数据
    static final int NotifyId = 2; //推送ID
    static final int NotifyDataList = 3; //推送数据列表
    static final int NotifyIdList = 4; //推送ID列表

    public static String analysisNotification(WearProtos.WearPacket wear) {

        NotificationProtos.Notification notiffication = wear.getNotification();
        int id = wear.getId();
        int pos = notiffication.getPayloadCase().getNumber();

        String result_str = "";

        System.out.println("数据封装 = " + "wear/type = " + wear.getType());
        System.out.println("数据封装 = " + "wear/id = " + wear.getId());
        System.out.println("数据封装 = " + "pos = " + pos);

        result_str += "wear/type = " + wear.getType() + "\n";
        result_str += "wear/id = " + wear.getId() + "\n";
        result_str += "pos = " + pos + "\n";

        result_str += "描述(参考):通知推送" + "-";

        if (id == 0x00) {
            result_str += "添加通知" + "\n";
        } else if (id == 0x01) {
            result_str += "删除通知" + "\n";
        } else if (id == 0x02) {
            result_str += "挂断" + "\n";
        } else {
            result_str += "未知" + "\n";
        }


        switch (pos) {
            case NotifyData:
                System.out.println("推送数据");
                result_str += "推送数据" + "\n";

                NotificationProtos.NotifyData notify_data = notiffication.getData();

                System.out.println("数据封装 = " + "notiffication/" + "/notify_data===========");
                System.out.println("数据封装 = " + "notiffication/" + "/notify_data/app_id = " + notify_data.getAppId());
                System.out.println("数据封装 = " + "notiffication/" + "/notify_data/app_name = " + notify_data.getAppName());
                System.out.println("数据封装 = " + "notiffication/" + "/notify_data/title = " + notify_data.getTitle());
                System.out.println("数据封装 = " + "notiffication/" + "/notify_data/sub_title = " + notify_data.getSubTitle());
                System.out.println("数据封装 = " + "notiffication/" + "/notify_data/text = " + notify_data.getText());
                System.out.println("数据封装 = " + "notiffication/" + "/notify_data/date = " + notify_data.getDate());
                System.out.println("数据封装 = " + "notiffication/" + "/notify_data/uid = " + notify_data.getUid());
                System.out.println("数据封装 = " + "notiffication/" + "/notify_data/call_type = " + notify_data.getCallType());

                result_str += "notiffication/" + "/notify_data===========" + "\n";
                result_str += "notiffication/" + "/notify_data/app_id = " + notify_data.getAppId() + "\n";
                result_str += "notiffication/" + "/notify_data/app_name = " + notify_data.getAppName() + "\n";
                result_str += "notiffication/" + "/notify_data/title = " + notify_data.getTitle() + "\n";
                result_str += "notiffication/" + "/notify_data/sub_title = " + notify_data.getSubTitle() + "\n";
                result_str += "notiffication/" + "/notify_data/text = " + notify_data.getText() + "\n";
                result_str += "notiffication/" + "/notify_data/date = " + notify_data.getDate() + "\n";
                result_str += "notiffication/" + "/notify_data/uid = " + notify_data.getUid() + "\n";
                result_str += "notiffication/" + "/notify_data/call_type = " + notify_data.getCallType() + "\n";


                break;

            case NotifyId:
                System.out.println("推送ID");
                result_str += "推送ID" + "\n";

                NotificationProtos.NotifyId notify_id = notiffication.getId();

                System.out.println("数据封装 = " + "notiffication/" + "/notify_id===========");
                System.out.println("数据封装 = " + "notiffication/" + "/notify_id/uid = " + notify_id.getUid());
                System.out.println("数据封装 = " + "notiffication/" + "/notify_id/app_id = " + notify_id.getAppId());

                result_str += "notiffication/" + "/notify_id===========" + "\n";
                result_str += "notiffication/" + "/notify_id/uid = " + notify_id.getUid() + "\n";
                result_str += "notiffication/" + "/notify_id/app_id = " + notify_id.getAppId() + "\n";

                break;

            case NotifyDataList:
                System.out.println("推送数据列表");
                result_str += "推送数据列表" + "\n";

                NotificationProtos.NotifyData.List notify_list = notiffication.getDataList();

                for (int i = 0; i < notify_list.getListCount(); i++) {

                    NotificationProtos.NotifyData notify_data_info = notify_list.getList(i);

                    System.out.println("数据封装 = " + "notiffication" + "/notify_data=========== i = " + i);
                    System.out.println("数据封装 = " + "notiffication" + "/notify_data/app_id = " + notify_data_info.getAppId());
                    System.out.println("数据封装 = " + "notiffication" + "/notify_data/app_name = " + notify_data_info.getAppName());
                    System.out.println("数据封装 = " + "notiffication" + "/notify_data/title = " + notify_data_info.getTitle());
                    System.out.println("数据封装 = " + "notiffication" + "/notify_data/sub_title = " + notify_data_info.getSubTitle());
                    System.out.println("数据封装 = " + "notiffication" + "/notify_data/text = " + notify_data_info.getText());
                    System.out.println("数据封装 = " + "notiffication" + "/notify_data/date = " + notify_data_info.getDate());
                    System.out.println("数据封装 = " + "notiffication" + "/notify_data/uid = " + notify_data_info.getUid());
                    System.out.println("数据封装 = " + "notiffication" + "/notify_data/call_type = " + notify_data_info.getCallType());

                    result_str += "\n" + "notiffication" + "/notify_data=========== i = " + i + "\n";
                    result_str += "notiffication" + "/notify_data/app_id = " + notify_data_info.getAppId() + "\n";
                    result_str += "notiffication" + "/notify_data/app_name = " + notify_data_info.getAppName() + "\n";
                    result_str += "notiffication" + "/notify_data/title = " + notify_data_info.getTitle() + "\n";
                    result_str += "notiffication" + "/notify_data/sub_title = " + notify_data_info.getSubTitle() + "\n";
                    result_str += "notiffication" + "/notify_data/text = " + notify_data_info.getText() + "\n";
                    result_str += "notiffication" + "/notify_data/date = " + notify_data_info.getDate() + "\n";
                    result_str += "notiffication" + "/notify_data/uid = " + notify_data_info.getUid() + "\n";
                    result_str += "notiffication" + "/notify_data/call_type(来电类型) = " + notify_data_info.getCallType() + "\n";

                }

                break;

            case NotifyIdList:
                System.out.println("推送ID列表");
                result_str += "推送ID列表" + "\n";


                NotificationProtos.NotifyId.List notify_id_list = notiffication.getIdList();


                for (int i = 0; i < notify_id_list.getListCount(); i++) {

                    NotificationProtos.NotifyId notify_id_info = notify_id_list.getList(i);

                    System.out.println("数据封装 = " + "notiffication" + "/notify_id=========== i = " + i);
                    System.out.println("数据封装 = " + "notiffication" + "/notify_id/uid = " + notify_id_info.getUid());
                    System.out.println("数据封装 = " + "notiffication" + "/notify_id/app_id = " + notify_id_info.getAppId());

                    result_str += "\n" + "notiffication" + "/notify_id=========== i = " + i + "\n";
                    result_str += "notiffication" + "/notify_id/uid = " + notify_id_info.getUid() + "\n";
                    result_str += "notiffication" + "/notify_id/app_id = " + notify_id_info.getAppId() + "\n";

                }

                break;


        }
        return result_str;

    }


    public static byte[] getNotifaceCallList() {

        NotificationProtos.Notification.Builder notification = NotificationProtos.Notification.newBuilder();

        ArrayList<NotificationProtos.NotifyData> notify_list = new ArrayList<>();
        notify_list.add(getDataNotifyData());
        notification.setDataList(getDataList(notify_list));

        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.NOTIFICATION)
                .setId((byte) NotificationProtos.Notification.NotificationID.ADD_NOTIFY.getNumber())
                .setNotification(notification);

        try {
            WearProtos.WearPacket wear = WearProtos.WearPacket.parseFrom(wear1.build().toByteArray());
            SystemTools.analysisSystem(wear);

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }


        return wear1.build().toByteArray();
    }

    public static byte[] getNotifaceCall() {
        NotificationProtos.Notification.Builder notification = NotificationProtos.Notification.newBuilder();
        notification.setData(getDataNotifyData());
        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.NOTIFICATION)
                .setId((byte) NotificationProtos.Notification.NotificationID.ADD_NOTIFY.getNumber())
                .setNotification(notification);

        try {
            WearProtos.WearPacket wear = WearProtos.WearPacket.parseFrom(wear1.build().toByteArray());
            SystemTools.analysisSystem(wear);

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return wear1.build().toByteArray();
    }

    public static byte[] getNotifaceCall(NotificationProtos.NotifyData.Builder notify_data) {

        NotificationProtos.Notification.Builder notification = NotificationProtos.Notification.newBuilder();

        notification.setData(notify_data);

        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.NOTIFICATION)
                .setId((byte) NotificationProtos.Notification.NotificationID.ADD_NOTIFY.getNumber())
                .setNotification(notification);

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
     * 模拟添加来电提醒
     *
     * @return
     */
    public static NotificationProtos.NotifyData getDataNotifyData() {
        NotificationProtos.NotifyData.Builder notify_data = NotificationProtos.NotifyData.newBuilder();
        String[] my_date = NewTimeUtils.getAllTime2().split("_");
        String my_date_str = my_date[0] + my_date[1] + my_date[2] + "T" + my_date[3] + my_date[4] + my_date[5];

        notify_data.setAppId("com.tencent.mobileqq");
        notify_data.setAppName("QQ");
        notify_data.setTitle("123456");
        notify_data.setSubTitle("");
        notify_data.setText("1111");
        notify_data.setDate(my_date_str);
        notify_data.setUid(265);
        notify_data.setCallType(NotificationProtos.CallType.NOTHING);

        return notify_data.build();
    }


    public static NotificationProtos.NotifyData.List getDataList(ArrayList<NotificationProtos.NotifyData> notify_list) {
        NotificationProtos.NotifyData.List.Builder data = NotificationProtos.NotifyData.List.newBuilder();
        for (int i = 0; i < notify_list.size(); i++) {
            data.addList(i, notify_list.get(i));
        }
        return data.build();
    }

}
