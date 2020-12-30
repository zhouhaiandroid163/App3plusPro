package com.zjw.apps3pluspro.bleservice.anaylsis;

import com.xiaomi.wear.protobuf.GnssProtos;
import com.xiaomi.wear.protobuf.WatchFaceProtos;
import com.xiaomi.wear.protobuf.WearProtos;
import com.zjw.apps3pluspro.eventbus.GetDeviceProtoAGpsPrepareStatusSuccessEvent;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by android
 * on 2020/12/15
 */
public class GnssTools {
    private static final String TAG = GnssTools.class.getSimpleName();

    public static String analysisGnss(WearProtos.WearPacket wear) {
        GnssProtos.Gnss gnss = wear.getGnss();
        int payload = gnss.getPayloadCase().getNumber();
        MyLog.i(TAG, "analysisGnss payload = " + payload);
        switch (payload) {
            case 2:
                GnssProtos.Data data = gnss.getData();
                MyLog.i(TAG, "analysisGnss getNeedGpsInfo = " + data.getNeedGpsInfo());
                EventBus.getDefault().post(new GetDeviceProtoAGpsPrepareStatusSuccessEvent(data.getNeedGpsInfo()));
                break;
        }
        return null;
    }
}
