package com.zjw.apps3pluspro.bleservice.anaylsis;

import com.xiaomi.wear.protobuf.CommonProtos;
import com.xiaomi.wear.protobuf.WatchFaceProtos;
import com.xiaomi.wear.protobuf.WearProtos;
import com.zjw.apps3pluspro.eventbus.DeviceWatchFaceListSyncOverEvent;
import com.zjw.apps3pluspro.eventbus.DeviceWatchFaceOperationSuccessEvent;
import com.zjw.apps3pluspro.eventbus.GetDeviceProtoWatchFacePrepareStatusSuccessEvent;
import com.zjw.apps3pluspro.module.device.entity.ThemeMarketMyThemeItem;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by android
 * on 2020/12/15
 */
public class WatchFaceTools {
    private static final String TAG = WatchFaceTools.class.getSimpleName();

    public static byte[] getDeviceWatchFaceList() {
        MyLog.i(TAG, "getDeviceWatchFaceList");
        WatchFaceProtos.WatchFace.Builder builder = WatchFaceProtos.WatchFace.newBuilder();
        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.WATCH_FACE)
                .setId(WatchFaceProtos.WatchFace.WatchFaceID.GET_INSTALLED_LIST.getNumber())
                .setWatchFace(builder);
        return wear1.build().toByteArray();
    }

    public static byte[] setDeviceWatchFace(String themeId) {
        MyLog.i(TAG, "setDeviceWatchFace");
        WatchFaceProtos.WatchFace.Builder builder = WatchFaceProtos.WatchFace.newBuilder();
        builder.setId(themeId);
        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.WATCH_FACE)
                .setId(WatchFaceProtos.WatchFace.WatchFaceID.SET_WATCH_FACE.getNumber())
                .setWatchFace(builder);
        return wear1.build().toByteArray();
    }
    public static byte[] deleteDeviceWatchFace(String themeId) {
        MyLog.i(TAG, "deleteDeviceWatchFace");
        WatchFaceProtos.WatchFace.Builder builder = WatchFaceProtos.WatchFace.newBuilder();
        builder.setId(themeId);
        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.WATCH_FACE)
                .setId(WatchFaceProtos.WatchFace.WatchFaceID.REMOVE_WATCH_FACE.getNumber())
                .setWatchFace(builder);
        return wear1.build().toByteArray();
    }

    public static byte[] getDeviceWatchFacePrepareStatus(String themeId, int themeSize) {
        MyLog.i(TAG, "getDeviceWatchFacePrepareStatus");
        WatchFaceProtos.PrepareInfo.Builder prepareInfo = WatchFaceProtos.PrepareInfo.newBuilder();
        prepareInfo.setId(themeId);
        prepareInfo.setSize(themeSize);
        prepareInfo.setVersionCode(1);

        WatchFaceProtos.WatchFace.Builder builder = WatchFaceProtos.WatchFace.newBuilder();
        builder.setPrepareInfo(prepareInfo);

        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.WATCH_FACE)
                .setId(WatchFaceProtos.WatchFace.WatchFaceID.PREPARE_INSTALL_WATCH_FACE.getNumber())
                .setWatchFace(builder);
        return wear1.build().toByteArray();
    }

    public static String analysisWatchFace(WearProtos.WearPacket wear) {
        WatchFaceProtos.WatchFace watchFace = wear.getWatchFace();
        int payload = wear.getWatchFace().getPayloadCase().getNumber();
        MyLog.i(TAG, "analysisWatchFace payload = " + payload);
        switch (payload) {
            case 1: // 获取表盘列表
                ArrayList<ThemeMarketMyThemeItem> list = new ArrayList<>();
                StringBuilder themeString = new StringBuilder();
                WatchFaceProtos.WatchFaceItem.List watch_face_list = watchFace.getWatchFaceList();
                for (int i = 0; i < watch_face_list.getListCount(); i++) {
                    WatchFaceProtos.WatchFaceItem watchFaceItem = watch_face_list.getList(i);

                    ThemeMarketMyThemeItem themeMarketMyThemeItem = new ThemeMarketMyThemeItem();
                    themeMarketMyThemeItem.dialCode = watchFaceItem.getId();
                    themeMarketMyThemeItem.name = watchFaceItem.getName();
                    themeMarketMyThemeItem.isCurrent = watchFaceItem.getIsCurrent();
                    themeMarketMyThemeItem.canRemove = watchFaceItem.getCanRemove();
                    list.add(themeMarketMyThemeItem);

                    MyLog.i(TAG, "watch_face_list = " + themeMarketMyThemeItem.toString());

                    themeString.append(watchFaceItem.getId()).append(",");
                }

                EventBus.getDefault().post(new DeviceWatchFaceListSyncOverEvent(themeString.substring(0, themeString.length() - 1), list));
                break;
            case 4: // 操作成功
                EventBus.getDefault().post(new DeviceWatchFaceOperationSuccessEvent());
                break;
            case 5:
                CommonProtos.PrepareStatus prepareStatus = watchFace.getPrepareStatus();
                MyLog.i(TAG, "watch face PrepareStatus = " + prepareStatus.getNumber());
                EventBus.getDefault().post(new GetDeviceProtoWatchFacePrepareStatusSuccessEvent(prepareStatus.getNumber()));
                break;
            default:
                break;
        }

        return null;
    }


}
