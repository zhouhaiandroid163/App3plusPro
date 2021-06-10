package com.zjw.apps3pluspro.bleservice.anaylsis;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.xiaomi.wear.protobuf.FitnessProtos;
import com.xiaomi.wear.protobuf.WearProtos;
import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.bleservice.BleService;
import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.eventbus.DeviceNoSportEvent;
import com.zjw.apps3pluspro.eventbus.DeviceSportStatusEvent;
import com.zjw.apps3pluspro.eventbus.GpsSportDeviceStartEvent;
import com.zjw.apps3pluspro.module.device.entity.FitnessId;
import com.zjw.apps3pluspro.sql.dbmanager.SportModleInfoUtils;
import com.zjw.apps3pluspro.sql.entity.SportModleInfo;
import com.zjw.apps3pluspro.utils.CrcUtils;
import com.zjw.apps3pluspro.utils.GpsSportManager;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class FitnessTools {

    private static SportModleInfoUtils mSportModleInfoUtils = BaseApplication.getSportModleInfoUtils();
    static final int UserProfile = 1; //用户信息
    static final int ids = 2; //id
    static final int id = 3; //ids

    static final int sportRequest = 20; //
    static final int phoneSportDataV2D = 40; //
    static final int deviceSportStatus = 24; //

    private static final String TAG = FitnessTools.class.getSimpleName();


    public static List<byte[]> bleCmdList = new ArrayList<byte[]>();

    public static ArrayList<ArrayList<FitnessId>> bleIdsList = new ArrayList<>();

    public static String analysisFitness(WearProtos.WearPacket wear) {

        FitnessProtos.Fitness fitness = wear.getFitness();
        int wear_id = wear.getId();
        int pos = fitness.getPayloadCase().getNumber();

        String result_str = "";

        System.out.println("数据封装 = " + "wear/type = " + wear.getType());
        System.out.println("数据封装 = " + "wear/id = " + wear.getId());
        System.out.println("数据封装 = " + "pos = " + pos);

        result_str += "wear/type = " + wear.getType() + "\n";
        result_str += "wear/id = " + wear.getId() + "\n";
        result_str += "pos = " + pos + "\n";

        result_str += "描述(参考):体育保健" + "-";

        if (wear_id == 0x00) {
            result_str += "设置用户配置文件" + "\n";
        } else if (wear_id == 0x01) {
            result_str += "获取今天的健身活动" + "\n";
        } else if (wear_id == 0x02) {
            result_str += "获取历史健身活动" + "\n";
        } else if (wear_id == 0x03) {
            result_str += "请求健身IDS" + "\n";
        } else if (wear_id == 0x04) {
            result_str += "请求健身ID" + "\n";
        } else if (wear_id == 0x05) {
            result_str += "确认健身ID" + "\n";
        } else {
            result_str += "未知" + "\n";
        }

        switch (pos) {
            case UserProfile:
                System.out.println("用户信息");
                result_str += "用户信息" + "\n";

//                FitnessProtos.UserProfile user_profile = fitness.getDeviceStatus();
                FitnessProtos.UserProfile user_profile = fitness.getUserProfile();
                System.out.println("数据封装 = " + "fitness/" + "/user_profile===========");
                System.out.println("数据封装 = " + "fitness/" + "user_profile/" + "/height = " + user_profile.getHeight());
                System.out.println("数据封装 = " + "fitness/" + "user_profile/" + "/weight = " + user_profile.getWeight());
                System.out.println("数据封装 = " + "fitness/" + "user_profile/" + "/birth = " + user_profile.getBirth());
                System.out.println("数据封装 = " + "fitness/" + "user_profile/" + "/sex = " + user_profile.getSex());
                System.out.println("数据封装 = " + "fitness/" + "user_profile/" + "/max_hr = " + user_profile.getMaxHr());
                System.out.println("数据封装 = " + "fitness/" + "user_profile/" + "/goal_calorie = " + user_profile.getGoalStep());
                System.out.println("数据封装 = " + "fitness/" + "user_profile/" + "/goal_step = " + user_profile.getGoalStep());

                result_str += "fitness/" + "/user_profile===========" + "\n";
                result_str += "fitness/" + "user_profile/" + "/height = " + user_profile.getHeight() + "\n";
                result_str += "fitness/" + "user_profile/" + "/weight = " + user_profile.getWeight() + "\n";
                result_str += "fitness/" + "user_profile/" + "/birth = " + user_profile.getBirth() + "\n";
                result_str += "fitness/" + "user_profile/" + "/sex = " + user_profile.getSex() + "\n";
                result_str += "fitness/" + "user_profile/" + "/max_hr = " + user_profile.getMaxHr() + "\n";
                result_str += "fitness/" + "user_profile/" + "/goal_calorie = " + user_profile.getGoalStep() + "\n";
                result_str += "fitness/" + "user_profile/" + "/goal_step = " + user_profile.getGoalStep() + "\n";


                break;

            case ids:
                System.out.println("ids");

                bleIdsList.clear();
                bleCmdList.clear();
                currentIndex = 0;

                SysUtils.logContentI(TAG, "ids = " + BleTools.printHexString(fitness.getIds().toByteArray()));
                byte[] id_byte = fitness.getIds().toByteArray();

                for (int i = 0; i < id_byte.length / 7; i++) {
                    byte[] idData = new byte[7];
                    for (int j = 0; j < 7; j++) {
                        idData[j] = id_byte[i * 7 + j];
                    }
                    SysUtils.logContentE(TAG, "id = " + BleTools.printHexString(idData));

                    byte[] time_byte = new byte[4];
                    time_byte[0] = idData[3];
                    time_byte[1] = idData[2];
                    time_byte[2] = idData[1];
                    time_byte[3] = idData[0];
                    long time = CrcUtils.bytes2Int001(time_byte);
                    int shiqu = idData[4] & 0xff;
                    shiqu = shiqu * 15 / 60;
                    int version_number = idData[5] & 0xff;

                    int miaoshu = idData[6] & 0xff;
                    int typeDescription = miaoshu >>> 7;
                    int sportType = (miaoshu >> 2) & 0x1f;
                    int dataType = miaoshu & 0x03;
                    String description = "";
                    switch (typeDescription) {
                        case 0:
                            description = "保留(无效)";
                            break;
                        case 1:
                            description = "运动数据";
                            break;
                    }
                    switch (sportType) {
                        case 0:
                            description = description + "  无";
                            break;
                        case 1:
                            description = description + "  户外跑步";
                            break;
                        case 2:
                            description = description + "  户外健走";
                            break;
                        case 3:
                            description = description + "  室内跑步";
                            break;
                        case 4:
                            description = description + "  登山";
                            break;
                        case 5:
                            description = description + "  越野";
                            break;
                        case 6:
                            description = description + "  户外骑行";
                            break;
                        case 7:
                            description = description + "  室内骑行";
                            break;
                        case 8:
                            description = description + "  自由训练";
                            break;
                        case 9:
                            description = description + "  篮球";
                            break;
                        case 10:
                            description = description + "  足球";
                            break;
                        case 11:
                            description = description + "  乒乓球";
                            break;
                        case 12:
                            description = description + "  羽毛球";
                            break;
                    }

                    if (BaseApplication.getBleDeviceTools().getSupportProtoNewSport()) {
                        int version = idData[5] & 0xff;
                        int type = idData[6] & 0xff;
                        sportType = getSportType(version, type);
                        version_number = idData[5] & 0x0f;
                        switch (sportType) {
                            case 200:
                                description = description + "  泳池游泳-H";
                                break;
                            case 201:
                                description = description + "  公开水域游泳-H";
                                break;
                        }
                    }
                    SysUtils.logContentI(TAG, "时间 = " + time + "  时区 = " + shiqu + "  版本号 = " + version_number);

                    switch (dataType) {
                        case 0:
                            description = description + "  打点";
                            break;
                        case 1:
                            description = description + "  报告";
                            break;
                        case 2:
                            description = description + "  GPS";
                            break;
                    }
                    SysUtils.logContentI(TAG, "description = " + description);
                    SysUtils.logContentI(TAG, "类型描述 = " + typeDescription + "  运动类型 = " + sportType + "  数据类型 = " + dataType);

                    if (typeDescription == 1) {
                        bleCmdList.add(idData);

                        if (bleIdsList.size() == 0) {
                            addFitnessList(time, idData);
                        } else {
                            boolean isAdd = false;
                            for (int m = 0; m < bleIdsList.size(); m++) {
                                ArrayList<FitnessId> fitnessIds = bleIdsList.get(m);
                                if (fitnessIds.get(0).time == time) {
                                    isAdd = true;
                                    fitnessIds.add(new FitnessId(time, idData));
                                }
                            }
                            if (!isAdd) {
                                addFitnessList(time, idData);
                            }
                        }
                    }
                }
                BleService.bluetoothLeService.sendBroadcast(new Intent(BroadcastTools.ACTION_CMD_GET_SPORT));
                break;
            case id:
                System.out.println("id");
                result_str += "id" + "\n";
//                System.out.println("数据封装 = " + "fitness/" + "/id  = " + fitness.getId());
                System.out.println("数据封装 = " + "fitness/" + "/id(数据列表ID-单个)  = " + BleTools.printHexString(fitness.getId().toByteArray()));
//                result_str +="fitness/" + fitness.getId()+"/id  = "  + "\n";
                result_str += "fitness/" + "/id(数据列表ID-单个)  = " + BleTools.printHexString(fitness.getId().toByteArray()) + "\n";
                break;

            case sportRequest:
                FitnessProtos.SportRequest sportRequest = fitness.getSportRequest();

                int state = sportRequest.getState().getNumber();
                int time = sportRequest.getTimestamp();
                int timeZone = sportRequest.getTimezone().getOffset() / 4;
                int sportType = sportRequest.getSportType().getNumber();
                byte[] ids = sportRequest.getIds().toByteArray();
                int versions = sportRequest.getSupportVersions();

                String idString = BleTools.printHexString(ids);

                Log.i(TAG, "sportType=" + sportType + " state=" + state + " time=" + time + " timeZone=" + timeZone + " versions=" + versions + " idString=" + idString);

//                Toast.makeText(HomeActivity.homeActivity, "idString.length() + =" + idString.length(), Toast.LENGTH_LONG).show();

                EventBus.getDefault().post(new GpsSportDeviceStartEvent(state));
                break;
            case phoneSportDataV2D:
                FitnessProtos.PhoneSportDataV2D phoneSportDataV2D = fitness.getPhoneSportDataV2D();
                int time222 = phoneSportDataV2D.getTimestamp();
                Log.i(TAG, "  time222=" + time222);
                Log.i(TAG, "  phoneSportDataV2D=" + phoneSportDataV2D.toString());
                break;
            case deviceSportStatus:
                FitnessProtos.SportStatus sportStatus = fitness.getSportStatus();
                int duration = sportStatus.getDuration();
                long timestamp = sportStatus.getTimestamp();
                int curSportType = sportStatus.getSportType().getNumber();
                boolean paused = sportStatus.getPaused();
                boolean standalone = sportStatus.getStandalone();
                boolean appLaunched = sportStatus.getAppLaunched();

                Log.i(TAG, "sportType = " + curSportType + " paused = " + paused + " timestamp = " + timestamp + " duration = " + duration);
                Log.i(TAG, "standalone = " + standalone + " appLaunched = " + appLaunched);

                if (!standalone) {
                    EventBus.getDefault().post(new DeviceSportStatusEvent(curSportType, paused));
                }
                break;
            case 0: // 没有数据
                if (wear.getId() == FitnessProtos.Fitness.FitnessID.GET_SPORT_STATUS.getNumber()) {
                    EventBus.getDefault().post(new DeviceNoSportEvent());
                }
                break;
        }
        return result_str;
    }

    public static byte[] getDeviceGpsState() {
        MyLog.i(TAG, "getDeviceGpsState");
        FitnessProtos.Fitness.Builder builder = FitnessProtos.Fitness.newBuilder();

        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.FITNESS)
                .setId(FitnessProtos.Fitness.FitnessID.GET_SPORT_STATUS.getNumber())
                .setFitness(builder);
//        try {
//            WearProtos.WearPacket wear = WearProtos.WearPacket.parseFrom(wear1.build().toByteArray());
//            FitnessTools.analysisFitness(wear);
//        } catch (InvalidProtocolBufferException e) {
//            e.printStackTrace();
//        }

        return wear1.build().toByteArray();
    }


    public static byte[] getGpsReady(GpsSportManager.GpsInfo gpsInfo) {
        FitnessProtos.Fitness.Builder builder = FitnessProtos.Fitness.newBuilder();

        FitnessProtos.SportResponse.Builder xxx = FitnessProtos.SportResponse.newBuilder();
        xxx.setCode(FitnessProtos.SportResponse.Code.OK);
        builder.setSportResponse(xxx);

        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.FITNESS)
                .setId(FitnessProtos.Fitness.FitnessID.SPORT_REQUEST.getNumber())
                .setFitness(builder);

        try {
            WearProtos.WearPacket wear = WearProtos.WearPacket.parseFrom(wear1.build().toByteArray());
            FitnessTools.analysisFitness(wear);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        return wear1.build().toByteArray();
    }

    public static byte[] getGpsByte(GpsSportManager.GpsInfo gpsInfo) {
        FitnessProtos.Fitness.Builder builder = FitnessProtos.Fitness.newBuilder();

        FitnessProtos.PhoneSportDataV2D.Builder phoneSportDataV2D = FitnessProtos.PhoneSportDataV2D.newBuilder();
        phoneSportDataV2D.setLatitude(gpsInfo.latitude);
        phoneSportDataV2D.setLongitude(gpsInfo.longitude);

        long time = System.currentTimeMillis() / 1000;
        phoneSportDataV2D.setTimestamp((int) time);

        switch (gpsInfo.gpsAccuracy) {
            case GpsSportManager.GpsInfo.GPS_LOW:
                phoneSportDataV2D.setGpsAccuracy(FitnessProtos.GpsAccuracy.GPS_LOW);
                break;
            case GpsSportManager.GpsInfo.GPS_MEDIUM:
                phoneSportDataV2D.setGpsAccuracy(FitnessProtos.GpsAccuracy.GPS_MEDIUM);
                break;
            case GpsSportManager.GpsInfo.GPS_HIGH:
                phoneSportDataV2D.setGpsAccuracy(FitnessProtos.GpsAccuracy.GPS_HIGH);
                break;
        }

        builder.setPhoneSportDataV2D(phoneSportDataV2D);
        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.FITNESS)
                .setId(FitnessProtos.Fitness.FitnessID.PHONE_SPORT_DATA_V2D.getNumber())
                .setFitness(builder);

        try {
            WearProtos.WearPacket wear = WearProtos.WearPacket.parseFrom(wear1.build().toByteArray());
            FitnessTools.analysisFitness(wear);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        return wear1.build().toByteArray();
    }

    private static void addFitnessList(long time, byte[] my_data) {
        ArrayList<FitnessId> fitnessIds = new ArrayList<>();
        fitnessIds.add(new FitnessId(time, my_data));
        bleIdsList.add(fitnessIds);
    }

    public static int currentIndex = 0;
    public static int deleteIndex = 0;

    public static byte[] deleteSportId() {

        ArrayList<FitnessId> fitnessIds = bleIdsList.get(deleteIndex);
        int size = 0;
        for (int i = 0; i < fitnessIds.size(); i++) {
            size = size + fitnessIds.get(i).id.length;
        }
        byte[] valueByte = new byte[size];

        int destPos = 0;
        for (int i = 0; i < fitnessIds.size(); i++) {
            byte[] valueByteI = fitnessIds.get(i).id;
            System.arraycopy(valueByteI, 0, valueByte, destPos, valueByteI.length);
            destPos = destPos + valueByteI.length;
        }

        FitnessProtos.Fitness.Builder builder = FitnessProtos.Fitness.newBuilder();
//        builder.setId(ByteString.copyFrom(FitnessTools.bleCmdList.get(deleteIndex)));
        builder.setId(ByteString.copyFrom(valueByte));
        WearProtos.WearPacket.Builder wear = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.FITNESS)
                .setId(FitnessProtos.Fitness.FitnessID.CONFIRM_FITNESS_ID.getNumber())
                .setFitness(builder);
        deleteIndex++;
        return wear.build().toByteArray();
    }

    public static byte[] getSportIds(int type) {
        int value;
        if (type == 0) {
            value = FitnessProtos.Fitness.FitnessID.GET_TODAY_FITNESS_IDS.getNumber();
        } else {
            value = FitnessProtos.Fitness.FitnessID.GET_HISTORY_FITNESS_IDS.getNumber();
        }
        FitnessProtos.Fitness.Builder builder = FitnessProtos.Fitness.newBuilder();

        WearProtos.WearPacket.Builder wear = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.FITNESS)
                .setId(value)
                .setFitness(builder);
        return wear.build().toByteArray();
    }

    public static byte[] requestFitnessId() {
        if (currentIndex < bleIdsList.size()) {
            ArrayList<FitnessId> fitnessIds = bleIdsList.get(currentIndex);
            if (fitnessIds.size() > 0) {
                int size = 0;
                for (int i = 0; i < fitnessIds.size(); i++) {
                    size = size + fitnessIds.get(i).id.length;
                }
                byte[] valueByte = new byte[size];

                int destPos = 0;
                for (int i = 0; i < fitnessIds.size(); i++) {
                    byte[] valueByteI = fitnessIds.get(i).id;
                    System.arraycopy(valueByteI, 0, valueByte, destPos, valueByteI.length);
                    destPos = destPos + valueByteI.length;
                }
                FitnessProtos.Fitness.Builder builder = FitnessProtos.Fitness.newBuilder();
                builder.setIds(ByteString.copyFrom(valueByte));
                WearProtos.WearPacket.Builder wear = WearProtos.WearPacket.newBuilder()
                        .setType(WearProtos.WearPacket.Type.FITNESS)
                        .setId(FitnessProtos.Fitness.FitnessID.REQUEST_FITNESS_IDS.getNumber())
                        .setFitness(builder);
                currentIndex = currentIndex + 1;
                return wear.build().toByteArray();
            }
        }
        Log.e(TAG, " requestFitnessId data is Exception");
        return null;

//        if (currentIndex + 1 < FitnessTools.bleCmdList.size()) {
//            byte[] valueByte1 = FitnessTools.bleCmdList.get(currentIndex);
//            byte[] valueByte = FitnessTools.bleCmdList.get(currentIndex + 1);
//            byte[] byte_3 = new byte[valueByte1.length + valueByte.length];
//            System.arraycopy(valueByte1, 0, byte_3, 0, valueByte1.length);
//            System.arraycopy(valueByte, 0, byte_3, valueByte1.length, valueByte.length);
//
//            FitnessProtos.Fitness.Builder builder = FitnessProtos.Fitness.newBuilder();
////        builder.setId(ByteString.copyFrom(valueByte1));
//            builder.setIds(ByteString.copyFrom(byte_3));
//            WearProtos.WearPacket.Builder wear = WearProtos.WearPacket.newBuilder()
//                    .setType(WearProtos.WearPacket.Type.FITNESS)
//                    .setId(FitnessProtos.Fitness.FitnessID.REQUEST_FITNESS_IDS.getNumber())
//                    .setFitness(builder);
//
//            currentIndex = currentIndex + 2;
//            return wear.build().toByteArray();
//        } else {
//            return null;
//        }
    }

    private static SportModleInfo sportModleInfo = null;

    public static void parsing(String[] sportData) {
        long idTime = Long.parseLong(sportData[3] + sportData[2] + sportData[1] + sportData[0], 16);
        int timeZone = BleTools.hexString2bytes(sportData[4])[0] / 4;
        int version = Integer.parseInt(sportData[5], 16);
        int description = Integer.parseInt(sportData[6], 16);
        int typeDescription = description >> 7;
        int sportType = (description >> 2) & 0x1f;
        int dataType = description & 0x03;

        if (BaseApplication.getBleDeviceTools().getSupportProtoNewSport()) {
            sportType = getSportType(version, description);
            version = Integer.parseInt(sportData[5], 16) & 0x0f;
        }

        SysUtils.logContentI(TAG, " time = " + idTime + " timeZone = " + timeZone + " version = " + version + " typeDescription = " + typeDescription + " sportType = " + sportType + " dataType = " + dataType);

        // 打点
        if (dataType == 0) {
            if (sportModleInfo == null) {
                sportModleInfo = new SportModleInfo();
            }

            sportModleInfo.setUser_id(BaseApplication.getUserId());
            sportModleInfo.setDataSourceType(1);
            sportModleInfo.setRecordPointDataId(sportData[0] + "-" + sportData[1] + "-" + sportData[2] + "-" + sportData[3] + "-" + sportData[4] + "-" + sportData[5] + "-" + sportData[6]);
            sportModleInfo.setRecordPointIdTime(idTime * 1000);
            sportModleInfo.setRecordPointTimeZone(timeZone);
            sportModleInfo.setRecordPointVersion(version);
            sportModleInfo.setRecordPointTypeDescription(typeDescription);
            sportModleInfo.setRecordPointSportType(sportType);
            sportModleInfo.setRecordPointDataType(dataType);

            StringBuilder recordPointSportData = new StringBuilder();
            //户外跑步/户外健走/越野/登山
            if (isData1(sportType)) {
                // data description 3 byte  index 7 8 9
                sportModleInfo.setRecordPointEncryption(Integer.parseInt(sportData[7], 16));
                sportModleInfo.setRecordPointDataValid1(Integer.parseInt(sportData[8], 16));
                sportModleInfo.setRecordPointDataValid2(Integer.parseInt(sportData[9], 16));
                // At last 4 byte is check crc32
                for (int i = 10; i < sportData.length - 4; ) {
                    recordPointSportData.append(sportData[i]).append("-").append(sportData[i + 1]).append("-").append(sportData[i + 2]).append("-").append(sportData[i + 3] + "-");
                    long altitude = Long.parseLong(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                    i += 4;
                    recordPointSportData.append(sportData[i]).append("-").append(sportData[i + 1]).append("-").append(sportData[i + 2]).append("-").append(sportData[i + 3]).append("-");
                    long dataNumber = Long.parseLong(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                    i += 4;
                    recordPointSportData.append(sportData[i]).append("-").append(sportData[i + 1]).append("-").append(sportData[i + 2]).append("-").append(sportData[i + 3]).append("-");
                    long time = Long.parseLong(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                    i += 4;
                    for (int j = 0; j < dataNumber; j++) {
                        recordPointSportData.append(sportData[i]).append("-").append(sportData[i + 1]).append("-").append(sportData[i + 2]).append("-").append(sportData[i + 3]).append("-");
//                        int byte1 = Integer.parseInt(sportData[i], 16);
//                        int byte2 = Integer.parseInt(sportData[i + 1], 16);
//                        int byte3 = Integer.parseInt(sportData[i + 2], 16);
//                        int byte4 = Integer.parseInt(sportData[i + 3], 16);
//                        int cal = byte1 >> 4;
//                        int step = byte1 & 0x0f;
//                        int heart = byte2;
//                        boolean isFullKilometer = (byte3 >> 7) == 1;
//                        int heightType = (byte3 >> 6) & 0x01;//0=下降，1=上升
//                        double height = (byte3 & 0x1f) / 10.0; // 实际数据精确到0.1，但是打点存储×10后保存为整数
//                        double distance = byte4 / 10.0;
                        i += 4;
                        // one data paring over, insert to db
                    }
                    SysUtils.logContentI(TAG, " altitude = " + altitude + " dataNumber = " + dataNumber + " time = " + time);
                }

            } else if (isData2(sportType)) {
                //室内跑步
                // data description 3 byte  index 7 8 9
                sportModleInfo.setRecordPointEncryption(Integer.parseInt(sportData[7], 16));
                sportModleInfo.setRecordPointDataValid1(Integer.parseInt(sportData[8], 16));
                sportModleInfo.setRecordPointDataValid2(Integer.parseInt(sportData[9], 16));
                // At last 4 byte is check crc32
                for (int i = 10; i < sportData.length - 4; ) {
                    recordPointSportData.append(sportData[i] + "-" + sportData[i + 1] + "-" + sportData[i + 2] + "-" + sportData[i + 3] + "-");
                    long dataNumber = Long.parseLong(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                    i += 4;
                    recordPointSportData.append(sportData[i] + "-" + sportData[i + 1] + "-" + sportData[i + 2] + "-" + sportData[i + 3] + "-");
                    long time = Long.parseLong(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                    i += 4;
                    for (int j = 0; j < dataNumber; j++) {
                        recordPointSportData.append(sportData[i] + "-" + sportData[i + 1] + "-" + sportData[i + 2] + "-");
//                        int byte1 = Integer.parseInt(sportData[i], 16);
//                        int byte2 = Integer.parseInt(sportData[i + 1], 16);
//                        int byte3 = Integer.parseInt(sportData[i + 2], 16);
//                        int cal = byte1 >> 4;
//                        int step = byte1 & 0x0f;
//                        int heart = byte2;
//                        double distance = byte3 / 10.0;
                        i += 3;
                        // one data paring over, insert to db
                    }
                    SysUtils.logContentI(TAG, " dataNumber = " + dataNumber + " time = " + time);
                }
            } else if (isData3(sportType)) {
                // 户外骑行
                // data description 3 byte  index 7 8 9
                sportModleInfo.setRecordPointEncryption(Integer.parseInt(sportData[7], 16));
                sportModleInfo.setRecordPointDataValid1(Integer.parseInt(sportData[8], 16));
                sportModleInfo.setRecordPointDataValid2(Integer.parseInt(sportData[9], 16));
                // At last 4 byte is check crc32
                for (int i = 10; i < sportData.length - 4; ) {
                    recordPointSportData.append(sportData[i] + "-" + sportData[i + 1] + "-" + sportData[i + 2] + "-" + sportData[i + 3] + "-");
                    long altitude = Long.parseLong(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                    i += 4;
                    recordPointSportData.append(sportData[i] + "-" + sportData[i + 1] + "-" + sportData[i + 2] + "-" + sportData[i + 3] + "-");
                    long dataNumber = Long.parseLong(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                    i += 4;
                    recordPointSportData.append(sportData[i] + "-" + sportData[i + 1] + "-" + sportData[i + 2] + "-" + sportData[i + 3] + "-");
                    long time = Long.parseLong(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                    i += 4;
                    for (int j = 0; j < dataNumber; j++) {
                        recordPointSportData.append(sportData[i] + "-" + sportData[i + 1] + "-" + sportData[i + 2] + "-");
//                        int byte1 = Integer.parseInt(sportData[i], 16);
//                        int byte2 = Integer.parseInt(sportData[i + 1], 16);
//                        int byte3 = Integer.parseInt(sportData[i + 2], 16);
//                        int cal = byte1;
//                        int heart = byte2;
//                        boolean isFullKilometer = (byte3 >> 7) == 1;
//                        int heightType = (byte3 >> 6) & 0x01;//0=下降，1=上升
//                        double height = (byte3 & 0x1f) / 10.0; // 实际数据精确到0.1，但是打点存储×10后保存为整数
                        i += 3;
                        // one data paring over, insert to db
                    }
                    SysUtils.logContentI(TAG, " dataNumber = " + dataNumber + " time = " + time);
                }

            } else if (isData4(sportType)) {
                //室内单车/自由训练/ 各种打球
                // data description 2 byte  index 7 8
                sportModleInfo.setRecordPointEncryption(Integer.parseInt(sportData[7], 16));
                sportModleInfo.setRecordPointDataValid1(Integer.parseInt(sportData[8], 16));
                // At last 4 byte is check crc32
                for (int i = 9; i < sportData.length - 4; ) {
                    recordPointSportData.append(sportData[i] + "-" + sportData[i + 1] + "-" + sportData[i + 2] + "-" + sportData[i + 3] + "-");
                    long dataNumber = Long.parseLong(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                    i += 4;
                    recordPointSportData.append(sportData[i] + "-" + sportData[i + 1] + "-" + sportData[i + 2] + "-" + sportData[i + 3] + "-");
                    long time = Long.parseLong(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                    i += 4;
                    for (int j = 0; j < dataNumber; j++) {
                        recordPointSportData.append(sportData[i] + "-" + sportData[i + 1] + "-");
//                        int byte1 = Integer.parseInt(sportData[i], 16);
//                        int byte2 = Integer.parseInt(sportData[i + 1], 16);
//                        int heart = byte1;
//                        int cal = byte2;
                        i += 2;
                        // one data paring over, insert to db
                    }
                    SysUtils.logContentI(TAG, " dataNumber = " + dataNumber + " time = " + time);
                }
            } else if (isData5(sportType)) {
                //泳池游泳/开放水域游泳
                // data description 3 byte  index 7 8 9
                sportModleInfo.setRecordPointEncryption(Integer.parseInt(sportData[7], 16));
                sportModleInfo.setRecordPointDataValid1(Integer.parseInt(sportData[8], 16));
                sportModleInfo.setRecordPointDataValid2(Integer.parseInt(sportData[9], 16));
                // At last 4 byte is check crc32
                for (int i = 10; i < sportData.length - 4; ) {
                    recordPointSportData.append(sportData[i] + "-" + sportData[i + 1] + "-" + sportData[i + 2] + "-" + sportData[i + 3] + "-");
                    long dataNumber = Long.parseLong(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                    i += 4;
                    recordPointSportData.append(sportData[i] + "-" + sportData[i + 1] + "-" + sportData[i + 2] + "-" + sportData[i + 3] + "-");
                    long time = Long.parseLong(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                    i += 4;
                    for (int k = 0; k < dataNumber; k++) {
                        for (int j = 0; j < 24; j++) {
                            recordPointSportData.append(sportData[i] + "-");
                            i += 1;
                        }
                    }
                    SysUtils.logContentI(TAG, " dataNumber = " + dataNumber + " time = " + time);
                }

            }
            sportModleInfo.setRecordPointSportData(recordPointSportData.substring(0, recordPointSportData.length() - 1));
        } else if (dataType == 2) {
            // gps
            if (sportModleInfo == null) {
                sportModleInfo = new SportModleInfo();
            }
            int encryption = Integer.parseInt(sportData[7], 16);
            int dataValid1 = Integer.parseInt(sportData[8], 16);
            sportModleInfo.setReportGpsEncryption(encryption);
            sportModleInfo.setReportGpsValid1(dataValid1);

            StringBuilder timeString = new StringBuilder();
            StringBuilder latLon = new StringBuilder();

            for (int i = 9; i < sportData.length - 4; ) {
                long time = Long.parseLong(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                i += 4;
                timeString.append(time).append(",");
                float lon = BleTools.hexToFloat(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i]);
                i += 4;
                float lat = BleTools.hexToFloat(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i]);
                i += 4;
                latLon.append(lon).append(",").append(lat).append(";");
            }
            if (timeString.length() > 0) {
                SysUtils.logContentI(TAG, " timeString = " + timeString.toString());
                SysUtils.logContentI(TAG, " latLon = " + latLon.toString());
                sportModleInfo.setRecordGpsTime(timeString.substring(0, timeString.length() - 1));
                sportModleInfo.setMap_data(latLon.substring(0, latLon.length() - 1));
            }
        } else if (dataType == 1) {
            // 报告
            //户外跑步/户外健走/越野/登山
            if (isData1(sportType)) {
                int encryption = Integer.parseInt(sportData[7], 16);
                int dataValid1 = Integer.parseInt(sportData[8], 16);
                int dataValid2 = Integer.parseInt(sportData[9], 16);
                int dataValid3 = Integer.parseInt(sportData[10], 16);
                int dataValid4 = Integer.parseInt(sportData[11], 16);

                sportModleInfo.setReportEncryption(encryption);
                sportModleInfo.setReportDataValid1(dataValid1);
                sportModleInfo.setReportDataValid2(dataValid2);
                sportModleInfo.setReportDataValid3(dataValid3);
                sportModleInfo.setReportDataValid4(dataValid4);

                SysUtils.logContentI(TAG, " dataValid1 = " + dataValid1 + " dataValid2 = " + dataValid2 + " dataValid3 = " + dataValid3 + " dataValid4 = " + dataValid4);

                int i = 12;
                long sportStartTime = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportSportStartTime(sportStartTime * 1000);
                i += 4;
                long sportEndTime = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportSportEndTime(sportEndTime * 1000);
                i += 4;
                long duration = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportDuration(duration);
                i += 4;
                long distance = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportDistance(distance);
                i += 4;
                long cal = Integer.parseInt(sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportCal(cal);
                i += 2;
                long fastPace = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportFastPace(fastPace);
                i += 4;
                long slowestPace = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportSlowestPace(slowestPace);
                i += 4;
                float fastSpeed = BleTools.getFloat(BleTools.hexString2bytes(sportData[i] + " " + sportData[i + 1] + " " + sportData[i + 2] + " " + sportData[i + 3]));
                sportModleInfo.setReportFastSpeed((float) fastSpeed);
                i += 4;
                long totalStep = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportTotalStep(totalStep);
                i += 4;
                int maxStepSpeed = Integer.parseInt(sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportMaxStepSpeed(maxStepSpeed);
                i += 2;
                int avgHeart = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportAvgHeart(avgHeart);
                i += 1;
                int maxHeart = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportMaxHeart(maxHeart);
                i += 1;
                int minHeart = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportMinHeart(minHeart);
                i += 1;
                float cumulativeRise = BleTools.getFloat(BleTools.hexString2bytes(sportData[i] + " " + sportData[i + 1] + " " + sportData[i + 2] + " " + sportData[i + 3]));
                sportModleInfo.setReportCumulativeRise((float) cumulativeRise);
                i += 4;
                float cumulativeDecline = BleTools.getFloat(BleTools.hexString2bytes(sportData[i] + " " + sportData[i + 1] + " " + sportData[i + 2] + " " + sportData[i + 3]));
                sportModleInfo.setReportCumulativeDecline((float) cumulativeDecline);
                i += 4;
                float avgHeight = BleTools.getFloat(BleTools.hexString2bytes(sportData[i] + " " + sportData[i + 1] + " " + sportData[i + 2] + " " + sportData[i + 3]));
                sportModleInfo.setReportAvgHeight((float) avgHeight);
                i += 4;
                float maxHeight = BleTools.getFloat(BleTools.hexString2bytes(sportData[i] + " " + sportData[i + 1] + " " + sportData[i + 2] + " " + sportData[i + 3]));
                sportModleInfo.setReportMaxHeight((float) maxHeight);
                i += 4;
                float minHeight = BleTools.getFloat(BleTools.hexString2bytes(sportData[i] + " " + sportData[i + 1] + " " + sportData[i + 2] + " " + sportData[i + 3]));
                sportModleInfo.setReportMinHeight((float) minHeight);
                i += 4;
                float trainingEffect = BleTools.getFloat(BleTools.hexString2bytes(sportData[i] + " " + sportData[i + 1] + " " + sportData[i + 2] + " " + sportData[i + 3]));
                sportModleInfo.setReportTrainingEffect((float) trainingEffect);
                i += 4;
                int maxOxygenIntake = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportMaxOxygenIntake(maxOxygenIntake);
                i += 1;
                int energyConsumption = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportEnergyConsumption(energyConsumption);
                i += 1;
                long recoveryTime = Integer.parseInt(sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportRecoveryTime(recoveryTime);
                i += 2;
                long heartLimitTime = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartLimitTime(heartLimitTime);
                i += 4;
                long heartAnaerobic = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartAnaerobic(heartAnaerobic);
                i += 4;
                long heartAerobic = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartAerobic(heartAerobic);
                i += 4;
                long heartFatBurning = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartFatBurning(heartFatBurning);
                i += 4;
                long heartWarmUp = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartWarmUp(heartWarmUp);
                i += 4;

                SysUtils.logContentI(TAG, " sportStartTime = " + sportStartTime + " sportEndTime = " + sportEndTime + "  heartFatBurning = " + heartFatBurning + " heartWarmUp = " + heartWarmUp);
            } else if (isData2(sportType)) {
                // 室内
                int encryption = Integer.parseInt(sportData[7], 16);
                int dataValid1 = Integer.parseInt(sportData[8], 16);
                int dataValid2 = Integer.parseInt(sportData[9], 16);
                int dataValid3 = Integer.parseInt(sportData[10], 16);

                sportModleInfo.setReportEncryption(encryption);
                sportModleInfo.setReportDataValid1(dataValid1);
                sportModleInfo.setReportDataValid2(dataValid2);
                sportModleInfo.setReportDataValid3(dataValid3);

                SysUtils.logContentI(TAG, " dataValid1 = " + dataValid1 + " dataValid2 = " + dataValid2 + " dataValid3 = " + dataValid3);

                int i = 11;
                long sportStartTime = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportSportStartTime(sportStartTime * 1000);
                i += 4;
                long sportEndTime = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportSportEndTime(sportEndTime * 1000);
                i += 4;
                long duration = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportDuration(duration);
                i += 4;
                long distance = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportDistance(distance);
                i += 4;
                long cal = Integer.parseInt(sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportCal(cal);
                i += 2;
                long fastPace = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportFastPace(fastPace);
                i += 4;
                long slowestPace = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportSlowestPace(slowestPace);
                i += 4;
                long totalStep = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportTotalStep(totalStep);
                i += 4;
                int maxStepSpeed = Integer.parseInt(sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportMaxStepSpeed(maxStepSpeed);
                i += 2;
                int avgHeart = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportAvgHeart(avgHeart);
                i += 1;
                int maxHeart = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportMaxHeart(maxHeart);
                i += 1;
                int minHeart = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportMinHeart(minHeart);
                i += 1;
                float trainingEffect = BleTools.getFloat(BleTools.hexString2bytes(sportData[i] + " " + sportData[i + 1] + " " + sportData[i + 2] + " " + sportData[i + 3]));
                sportModleInfo.setReportTrainingEffect((float) trainingEffect);
                i += 4;
                int maxOxygenIntake = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportMaxOxygenIntake(maxOxygenIntake);
                i += 1;
                int energyConsumption = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportEnergyConsumption(energyConsumption);
                i += 1;
                long recoveryTime = Integer.parseInt(sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportRecoveryTime(recoveryTime);
                i += 2;
                long heartLimitTime = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartLimitTime(heartLimitTime);
                i += 4;
                long heartAnaerobic = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartAnaerobic(heartAnaerobic);
                i += 4;
                long heartAerobic = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartAerobic(heartAerobic);
                i += 4;
                long heartFatBurning = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartFatBurning(heartFatBurning);
                i += 4;
                long heartWarmUp = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartWarmUp(heartWarmUp);
                i += 4;
                SysUtils.logContentI(TAG, " sportStartTime = " + sportStartTime + " sportEndTime = " + sportEndTime + "  heartFatBurning = " + heartFatBurning + " heartWarmUp = " + heartWarmUp);
            } else if (isData3(sportType)) {
                // 户外骑行
                int encryption = Integer.parseInt(sportData[7], 16);
                int dataValid1 = Integer.parseInt(sportData[8], 16);
                int dataValid2 = Integer.parseInt(sportData[9], 16);
                int dataValid3 = Integer.parseInt(sportData[10], 16);
                int dataValid4 = Integer.parseInt(sportData[11], 16);

                sportModleInfo.setReportEncryption(encryption);
                sportModleInfo.setReportDataValid1(dataValid1);
                sportModleInfo.setReportDataValid2(dataValid2);
                sportModleInfo.setReportDataValid3(dataValid3);
                sportModleInfo.setReportDataValid4(dataValid4);

                SysUtils.logContentI(TAG, " dataValid1 = " + dataValid1 + " dataValid2 = " + dataValid2 + " dataValid3 = " + dataValid3 + " dataValid4 = " + dataValid4);

                int i = 12;
                long sportStartTime = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportSportStartTime(sportStartTime * 1000);
                i += 4;
                long sportEndTime = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportSportEndTime(sportEndTime * 1000);
                i += 4;
                long duration = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportDuration(duration);
                i += 4;
                long distance = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportDistance(distance);
                i += 4;
                long cal = Integer.parseInt(sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportCal(cal);
                i += 2;
                long fastPace = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportFastPace(fastPace);
                i += 4;
                long slowestPace = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportSlowestPace(slowestPace);
                i += 4;
                float fastSpeed = BleTools.getFloat(BleTools.hexString2bytes(sportData[i] + " " + sportData[i + 1] + " " + sportData[i + 2] + " " + sportData[i + 3]));
                sportModleInfo.setReportFastSpeed((float) fastSpeed);
                i += 4;
                int avgHeart = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportAvgHeart(avgHeart);
                i += 1;
                int maxHeart = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportMaxHeart(maxHeart);
                i += 1;
                int minHeart = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportMinHeart(minHeart);
                i += 1;
                float cumulativeRise = BleTools.getFloat(BleTools.hexString2bytes(sportData[i] + " " + sportData[i + 1] + " " + sportData[i + 2] + " " + sportData[i + 3]));
                sportModleInfo.setReportCumulativeRise((float) cumulativeRise);
                i += 4;
                float cumulativeDecline = BleTools.getFloat(BleTools.hexString2bytes(sportData[i] + " " + sportData[i + 1] + " " + sportData[i + 2] + " " + sportData[i + 3]));
                sportModleInfo.setReportCumulativeDecline((float) cumulativeDecline);
                i += 4;
                float avgHeight = BleTools.getFloat(BleTools.hexString2bytes(sportData[i] + " " + sportData[i + 1] + " " + sportData[i + 2] + " " + sportData[i + 3]));
                sportModleInfo.setReportAvgHeight((float) avgHeight);
                i += 4;
                float maxHeight = BleTools.getFloat(BleTools.hexString2bytes(sportData[i] + " " + sportData[i + 1] + " " + sportData[i + 2] + " " + sportData[i + 3]));
                sportModleInfo.setReportMaxHeight((float) maxHeight);
                i += 4;
                float minHeight = BleTools.getFloat(BleTools.hexString2bytes(sportData[i] + " " + sportData[i + 1] + " " + sportData[i + 2] + " " + sportData[i + 3]));
                sportModleInfo.setReportMinHeight((float) minHeight);
                i += 4;
                float trainingEffect = BleTools.getFloat(BleTools.hexString2bytes(sportData[i] + " " + sportData[i + 1] + " " + sportData[i + 2] + " " + sportData[i + 3]));
                sportModleInfo.setReportTrainingEffect((float) trainingEffect);
                i += 4;
                int maxOxygenIntake = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportMaxOxygenIntake(maxOxygenIntake);
                i += 1;
                int energyConsumption = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportEnergyConsumption(energyConsumption);
                i += 1;
                long recoveryTime = Integer.parseInt(sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportRecoveryTime(recoveryTime);
                i += 2;
                long heartLimitTime = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartLimitTime(heartLimitTime);
                i += 4;
                long heartAnaerobic = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartAnaerobic(heartAnaerobic);
                i += 4;
                long heartAerobic = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartAerobic(heartAerobic);
                i += 4;
                long heartFatBurning = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartFatBurning(heartFatBurning);
                i += 4;
                long heartWarmUp = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartWarmUp(heartWarmUp);
                i += 4;
                SysUtils.logContentI(TAG, " sportStartTime = " + sportStartTime + " sportEndTime = " + sportEndTime + "  heartFatBurning = " + heartFatBurning + " heartWarmUp = " + heartWarmUp);
            } else if (isData4(sportType)) {
                //室内单车/自由训练/篮球/足球/乒乓球/羽毛球
                int encryption = Integer.parseInt(sportData[7], 16);
                int dataValid1 = Integer.parseInt(sportData[8], 16);
                int dataValid2 = Integer.parseInt(sportData[9], 16);

                sportModleInfo.setReportEncryption(encryption);
                sportModleInfo.setReportDataValid1(dataValid1);
                sportModleInfo.setReportDataValid2(dataValid2);

                SysUtils.logContentI(TAG, " dataValid1 = " + dataValid1 + " dataValid2 = " + dataValid2);

                int i = 10;
                long sportStartTime = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportSportStartTime(sportStartTime * 1000);
                i += 4;
                long sportEndTime = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportSportEndTime(sportEndTime * 1000);
                i += 4;
                long duration = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportDuration(duration);
                i += 4;
                long cal = Integer.parseInt(sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportCal(cal);
                i += 2;
                int avgHeart = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportAvgHeart(avgHeart);
                i += 1;
                int maxHeart = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportMaxHeart(maxHeart);
                i += 1;
                int minHeart = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportMinHeart(minHeart);
                i += 1;
                float trainingEffect = BleTools.getFloat(BleTools.hexString2bytes(sportData[i] + " " + sportData[i + 1] + " " + sportData[i + 2] + " " + sportData[i + 3]));
                sportModleInfo.setReportTrainingEffect((float) trainingEffect);
                i += 4;
                int energyConsumption = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportEnergyConsumption(energyConsumption);
                i += 1;
                long recoveryTime = Integer.parseInt(sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportRecoveryTime(recoveryTime);
                i += 2;
                long heartLimitTime = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartLimitTime(heartLimitTime);
                i += 4;
                long heartAnaerobic = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartAnaerobic(heartAnaerobic);
                i += 4;
                long heartAerobic = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartAerobic(heartAerobic);
                i += 4;
                long heartFatBurning = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartFatBurning(heartFatBurning);
                i += 4;
                long heartWarmUp = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportHeartWarmUp(heartWarmUp);
                i += 4;
                SysUtils.logContentI(TAG, " sportStartTime = " + sportStartTime + " sportEndTime = " + sportEndTime + "  heartFatBurning = " + heartFatBurning + " heartWarmUp = " + heartWarmUp);
            } else if (isData5(sportType)) {
                sportModleInfo.setReportEncryption(Integer.parseInt(sportData[7], 16));
                sportModleInfo.setReportDataValid1(Integer.parseInt(sportData[8], 16));
                sportModleInfo.setReportDataValid2(Integer.parseInt(sportData[9], 16));

                int i = 10;
                long sportStartTime = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportSportStartTime(sportStartTime * 1000);
                i += 4;
                long sportEndTime = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportSportEndTime(sportEndTime * 1000);
                i += 4;
                long duration = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportDuration(duration);
                i += 4;
                long distance = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportDistance(distance);
                i += 4;
                long cal = Integer.parseInt(sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportCal(cal);
                i += 2;
                long fastPace = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportFastPace(fastPace);
                i += 4;
                long slowestPace = Integer.parseInt(sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportSlowestPace(slowestPace);
                i += 4;
                int energyConsumption = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportEnergyConsumption(energyConsumption);
                i += 1;
                long recoveryTime = Integer.parseInt(sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportRecoveryTime(recoveryTime);
                i += 2;
                int reportTotalSwimNum = Integer.parseInt(sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportTotalSwimNum(reportTotalSwimNum);
                i += 2;
                int swimStyle = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportSwimStyle(swimStyle);
                i += 1;
                int reportMaxSwimFrequency = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportMaxSwimFrequency(reportMaxSwimFrequency);
                i += 1;
                int reportFaceAboutNum = Integer.parseInt(sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportFaceAboutNum(reportFaceAboutNum);
                i += 2;
                int reportAvgSwolf = Integer.parseInt(sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportAvgSwolf(reportAvgSwolf);
                i += 2;
                int reportOptimalSwolf = Integer.parseInt(sportData[i + 1] + sportData[i], 16);
                sportModleInfo.setReportOptimalSwolf(reportOptimalSwolf);
                i += 2;
                int reportPoolWidth = Integer.parseInt(sportData[i], 16);
                sportModleInfo.setReportPoolWidth(reportPoolWidth);
                i += 1;
                SysUtils.logContentI(TAG, " sportStartTime = " + sportStartTime + " sportEndTime = " + sportEndTime + "  reportAvgSwolf = " + reportAvgSwolf + " duration = " + duration);
            }
            SysUtils.logContentW(TAG, " report over and start inseart db");
            Long timeDifference = System.currentTimeMillis() - sportModleInfo.getRecordPointIdTime();
            if (timeDifference > 0 && timeDifference < 3 * 30 * 24 * 3600L * 1000L) {
                if (sportModleInfo != null) {
                    List<SportModleInfo> querySportModleInfos = mSportModleInfoUtils.queryByRecordPointIdTime(BaseApplication.getUserId(), sportModleInfo.getRecordPointIdTime());
                    if (querySportModleInfos != null && querySportModleInfos.size() > 0) {
                        SysUtils.logContentW(TAG, " report over and updateData db");
                        sportModleInfo.set_id(querySportModleInfos.get(0).get_id());
                        mSportModleInfoUtils.updateData(sportModleInfo);
                    } else {
                        if (sportModleInfo.getReportDuration() > 24 * 60 * 60) {
                            SysUtils.logContentI(TAG, "reportDuration > 24 * 60 * 60");
                        } else {
                            Log.w(TAG, " report over and inseart db");
                            mSportModleInfoUtils.insertData(sportModleInfo);
                        }
                    }
                } else {
                    SysUtils.logContentE(TAG, " report over and data is Exception");
                }
            } else {
                SysUtils.logContentW(TAG, " time is exception");
            }
            sportModleInfo = null;
            BleService.bluetoothLeService.sendBroadcast(new Intent(BroadcastTools.ACTION_CMD_GET_SPORT));
        }
    }

    public static int getSportType(int version, int description) {
        int data1 = (version & 0x80) >> 7;
        int data2 = ((version & 0x70) << 1) | ((description & 0x7C) >> 2);
        return data1 * 256 + data2;
    }

    public static boolean isData1(int sportType) {
        if (sportType == 1 || sportType == 2 || sportType == 4 || sportType == 5) {
            return true;
        }
        return sportType > 12 && sportType < 21;
    }

    public static boolean isData2(int sportType) {
        return sportType == 3 || sportType == 37 || sportType == 38;
    }

    public static boolean isData3(int sportType) {
        return sportType == 6;
    }

    public static boolean isData4(int sportType) {
        if (sportType > 6 && sportType < 13) {
            return true;
        }
        if (sportType > 20 && sportType < 37) {
            return true;
        }
        return sportType > 38 && sportType < 124;
    }

    public static boolean isData5(int sportType) {
        return sportType == 200 || sportType == 201;
    }


}
