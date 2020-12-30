package com.zjw.apps3pluspro.bleservice.anaylsis;

import com.google.protobuf.InvalidProtocolBufferException;
import com.xiaomi.wear.protobuf.WearProtos;
import com.zjw.apps3pluspro.utils.log.MyLog;

/**
 * Created by android
 * on 2020/6/13.
 */
public class AnalysisProtoData {
    private static AnalysisProtoData analysisProtoData;
    private static final String TAG = AnalysisProtoData.class.getSimpleName();

    public static AnalysisProtoData getInstance() {
        if (analysisProtoData == null) {
            analysisProtoData = new AnalysisProtoData();
        }
        return analysisProtoData;
    }
    public String analysisData(byte[] data_bytes) {
        String result_str = "";
        try {
            WearProtos.WearPacket wear = WearProtos.WearPacket.parseFrom(data_bytes);
            result_str = analysisDataToWear(wear);

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return result_str;
    }
    private String analysisDataToWear(WearProtos.WearPacket wear) {
        String result_str = "";
        MyLog.i(TAG, "AnalysisProtoData Type = " + wear.getType());
        MyLog.i(TAG, "AnalysisProtoData id = " + wear.getId());
        switch (wear.getType()) {
            case ACCOUNT:
                result_str = AccountTools.analysisAccount(wear);
                break;
            case WEATHER:
                result_str = WeatherTools.analysisWeather(wear);
                break;
            case NFC:
                result_str = NfcTools.analysisNfc(wear);
                break;
            case SYSTEM:
                result_str = SystemTools.analysisSystem(wear);
                break;
            case FITNESS:
                result_str = FitnessTools.analysisFitness(wear);
                break;
            case CALENDAR:
                result_str = CalendarTools.analysisCalendar(wear);
                break;
            case FACTORY:
                result_str = FactoryTools.analysisFactory(wear);
                break;
            case NOTIFICATION:
                result_str = NotificationTools.analysisNotification(wear);
                break;
            case STOCK:
                result_str = StockTools.analysisStock(wear);
                break;
            case AIVS:
                result_str = AivsTools.analysisAivs(wear);
                break;
            case WATCH_FACE:
                result_str = WatchFaceTools.analysisWatchFace(wear);
                break;
            case GNSS:
                result_str = GnssTools.analysisGnss(wear);
                break;
        }
        return result_str;
    }
}
