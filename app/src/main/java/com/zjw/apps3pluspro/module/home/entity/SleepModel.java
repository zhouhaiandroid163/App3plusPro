package com.zjw.apps3pluspro.module.home.entity;


import com.zjw.apps3pluspro.sql.entity.SleepInfo;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.utils.AnalyticalUtils;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyTime;

import java.util.ArrayList;
import java.util.List;


public class SleepModel {
    private final String TAG = SleepModel.class.getSimpleName();


    String SleepDate;//日期
    String SleepStartTime;//入睡时间
    String SleepEndTime;//醒来时间
    String SleepStayUpTime;//熬夜
    String SleepDeep;//深睡
    String SleepLight;//浅睡
    String SleepTotalTime;//总时间
    String SleepSleepTime;//睡眠时间
    String SleepWoke;//醒来次数
    String SleepSoberTime;//清醒时间
    List<SleepData> SleepListData;


    public SleepModel() {
        super();
    }

    public String getSleepDate() {
        return SleepDate;
    }

    public void setSleepDate(String sleepDate) {
        SleepDate = sleepDate;
    }


    public String getSleepStartTime() {
        return SleepStartTime;
    }

    public void setSleepStartTime(String sleepStartTime) {
        SleepStartTime = sleepStartTime;
    }

    public String getSleepEndTime() {
        return SleepEndTime;
    }

    public void setSleepEndTime(String sleepEndTime) {
        SleepEndTime = sleepEndTime;
    }

    public String getSleepStayUpTime() {
        return SleepStayUpTime;
    }

    public void setSleepStayUpTime(String sleepStayUpTime) {
        SleepStayUpTime = sleepStayUpTime;
    }

    public String getSleepDeep() {
        return SleepDeep;
    }

    public void setSleepDeep(String sleepDeep) {
        SleepDeep = sleepDeep;
    }

    public String getSleepLight() {
        return SleepLight;
    }

    public void setSleepLight(String sleepLight) {
        SleepLight = sleepLight;
    }

    public String getSleepTotalTime() {
        return SleepTotalTime;
    }

    public void setSleepTotalTime(String sleepTotalTime) {
        SleepTotalTime = sleepTotalTime;
    }

    public String getSleepSleepTime() {
        return SleepSleepTime;
    }

    public void setSleepSleepTime(String sleepSleepTime) {
        SleepSleepTime = sleepSleepTime;
    }

    public String getSleepWoke() {
        return SleepWoke;
    }

    public void setSleepWoke(String sleepWoke) {
        SleepWoke = sleepWoke;
    }

    public String getSleepSoberTime() {
        return SleepSoberTime;
    }

    public void setSleepSoberTime(String sleepSoberTime) {
        SleepSoberTime = sleepSoberTime;
    }

    public List<SleepData> getSleepListData() {
        return SleepListData;
    }

    public void setSleepListData(List<SleepData> sleepListData) {
        SleepListData = sleepListData;
    }
//
//    /**
//     * 解析睡眠数据
//     */
//    public SleepModel(SleepInfo mSleepInfo) {
//        super();
//        String sleep_date = mSleepInfo.getDate();
//        String sleep_data = mSleepInfo.getData();
//        MyLog.i(TAG, "sleep_date = " + sleep_date);
//        MyLog.i(TAG, "sleep_data= " + sleep_data);
//
//        if (JavaUtil.checkIsNull(sleep_data)) {
//            return;
//        }
//
//        byte[] data = AnalyticalUtils.StringToIntSuZu(sleep_data);
//
//        int sleepTotalMin = 0;
//        int deepTotalMin = 0;
//        int ligntTotalMin = 0;
//        int stayupTotalMin = 0;
//        int soberTotalMin = 0;
//        int wokeCount = 0;
//        int allSleepMin = 0;
//
//        List<SleepData> sleepDataList = new ArrayList<SleepData>();
//
//        int fast_hours = (int) (data[0] & 0xf8) >> 3;
//        int fast_min = (int) (data[0] & 0x7) << 3 | (int) (data[1] & 0xE0) >> 5;
//
//
//        if (fast_hours >= 23 || fast_hours < 8) {
//            sleepDataList.add(new SleepData("0", "23:00"));
//
//            stayupTotalMin = MyTime.getMyTotalTime("23:00", AnalyticalUtils.getStrTime(fast_hours, fast_min));
//        }
//
//        for (int i = 0; i < data.length; i += 2) {
//
//            int sleep_type = data[i + 1] & 0x0F;
//            int start_hours = (int) (data[i] & 0xf8) >> 3;
//            int start_min = (int) (data[i] & 0x7) << 3 | (int) (data[i + 1] & 0xE0) >> 5;
//            int end_hours = 0;
//            int end_min = 0;
//
//            sleepDataList.add(new SleepData(String.valueOf(sleep_type), AnalyticalUtils.getStrTime(start_hours, start_min)));
//
//
//            if (i < data.length - 2) {
//                end_hours = (int) (data[i + 2] & 0xf8) >> 3;
//                end_min = (int) (data[i + 2] & 0x7) << 3 | (int) (data[i + 3] & 0xE0) >> 5;
//            } else {
//                end_hours = start_hours;
//                end_min = start_min;
//            }
//
//
//            if (sleep_type == 0x02) { // 浅睡眠
//                ligntTotalMin += MyTime.getMyTotalTime(AnalyticalUtils.getStrTime(start_hours, start_min), AnalyticalUtils.getStrTime(end_hours, end_min));
//            } else if (sleep_type == 0x03) { // 熟睡
//                deepTotalMin += MyTime.getMyTotalTime(AnalyticalUtils.getStrTime(start_hours, start_min), AnalyticalUtils.getStrTime(end_hours, end_min));
//            } else if (sleep_type == 0x04) { // 睡醒
//                soberTotalMin += MyTime.getMyTotalTime(AnalyticalUtils.getStrTime(start_hours, start_min), AnalyticalUtils.getStrTime(end_hours, end_min));
//                wokeCount++;
//            }
//
//        }
//        allSleepMin = deepTotalMin + ligntTotalMin;
//        sleepTotalMin = allSleepMin + stayupTotalMin;
//
//
//        setSleepDate(sleep_date);
//        setSleepDeep(String.valueOf(deepTotalMin));
//        setSleepLight(String.valueOf(ligntTotalMin));
//        setSleepStayUpTime(String.valueOf(stayupTotalMin));
//        setSleepTotalTime(String.valueOf(sleepTotalMin));
//        setSleepSleepTime(String.valueOf(allSleepMin));
//        setSleepWoke(String.valueOf(wokeCount));
//        setSleepSoberTime(String.valueOf(soberTotalMin));
//        setSleepListData(sleepDataList);
//        setSleepStartTime(sleepDataList.get(0).getStartTime());
//        setSleepEndTime(sleepDataList.get(sleepDataList.size() - 1).getStartTime());
//
//
//    }


    /**
     * 解析睡眠数据
     */
    public SleepModel(SleepInfo mSleepInfo) {
        super();
        String sleep_date = mSleepInfo.getDate();
        String sleep_data = mSleepInfo.getData();
        MyLog.i(TAG, "sleep_date = " + sleep_date);
        MyLog.i(TAG, "sleep_data= " + sleep_data);

        if (JavaUtil.checkIsNull(sleep_data)) {
            return;
        }

        byte[] data = AnalyticalUtils.StringToIntSuZu(sleep_data);

        int sleepTotalMin = 0;
        int deepTotalMin = 0;
        int ligntTotalMin = 0;
        int stayupTotalMin = 0;
        int soberTotalMin = 0;
        int wokeCount = 0;
        int allSleepMin = 0;

        List<SleepData> sleepDataList = new ArrayList<SleepData>();

//        int fast_hours = (int) (data[0] & 0xf8) >> 3;
//        int fast_min = (int) (data[0] & 0x7) << 3 | (int) (data[1] & 0xE0) >> 5;
//
//
//        if (fast_hours >= 23 || fast_hours < 8) {
//            sleepDataList.add(new SleepData("0", "23:00"));
//            stayupTotalMin = MyTime.getMyTotalTime("23:00", AnalyticalUtils.getStrTime(fast_hours, fast_min));
//        }

        for (int i = 0; i < data.length; i += 2) {

            int sleep_type = data[i + 1] & 0x0F;
            int start_hours = (int) (data[i] & 0xf8) >> 3;
            int start_min = (int) (data[i] & 0x7) << 3 | (int) (data[i + 1] & 0xE0) >> 5;
            int end_hours = 0;
            int end_min = 0;


            sleepDataList.add(new SleepData(String.valueOf(sleep_type), AnalyticalUtils.getStrTime(start_hours, start_min)));


            if (i < data.length - 2) {
                end_hours = (int) (data[i + 2] & 0xf8) >> 3;
                end_min = (int) (data[i + 2] & 0x7) << 3 | (int) (data[i + 3] & 0xE0) >> 5;
            } else {
                end_hours = start_hours;
                end_min = start_min;
            }

//            System.out.println("睡眠解析 ========== " + i + " ==========");
//            System.out.println("睡眠解析 = start = " + start_hours + ":" + start_min);
//            System.out.println("睡眠解析 = end = " + end_hours + ":" + end_min);
//            System.out.println("睡眠解析 = sleep_type = " + sleep_type);

            if (sleep_type == 0x02) { // 浅睡眠
                ligntTotalMin += MyTime.getMyTotalTime(AnalyticalUtils.getStrTime(start_hours, start_min), AnalyticalUtils.getStrTime(end_hours, end_min));
            } else if (sleep_type == 0x03) { // 熟睡
                deepTotalMin += MyTime.getMyTotalTime(AnalyticalUtils.getStrTime(start_hours, start_min), AnalyticalUtils.getStrTime(end_hours, end_min));
            } else if (sleep_type == 0x04) { // 睡醒
                soberTotalMin += MyTime.getMyTotalTime(AnalyticalUtils.getStrTime(start_hours, start_min), AnalyticalUtils.getStrTime(end_hours, end_min));
                wokeCount++;
            }

        }
        allSleepMin = deepTotalMin + ligntTotalMin;
        sleepTotalMin = allSleepMin + stayupTotalMin;

//        System.out.println("睡眠解析 ========== " + "统计" + " ==========");
//        System.out.println("睡眠解析 = sleep_date = " + sleep_date);
//        System.out.println("睡眠解析 = deepTotalMin = " + deepTotalMin);
//        System.out.println("睡眠解析 = ligntTotalMin = " + ligntTotalMin);
//        System.out.println("睡眠解析 = stayupTotalMin = " + stayupTotalMin);
//        System.out.println("睡眠解析 = sleepTotalMin = " + sleepTotalMin);
//        System.out.println("睡眠解析 = allSleepMin = " + allSleepMin);
//        System.out.println("睡眠解析 = wokeCount = " + wokeCount);
//        System.out.println("睡眠解析 = soberTotalMin = " + soberTotalMin);
//        System.out.println("睡眠解析 = sleepDataList = " + sleepDataList.toString());
//        System.out.println("睡眠解析 = start_time = " + sleepDataList.get(0).getStartTime());
//        System.out.println("睡眠解析 = end_time = " + sleepDataList.get(sleepDataList.size() - 1).getStartTime());

        setSleepDate(sleep_date);
        setSleepDeep(String.valueOf(deepTotalMin));
        setSleepLight(String.valueOf(ligntTotalMin));
        setSleepStayUpTime(String.valueOf(stayupTotalMin));
        setSleepTotalTime(String.valueOf(sleepTotalMin));
        setSleepSleepTime(String.valueOf(allSleepMin));
        setSleepWoke(String.valueOf(wokeCount));
        setSleepSoberTime(String.valueOf(soberTotalMin));
        setSleepListData(sleepDataList);
        setSleepStartTime(sleepDataList.get(0).getStartTime());
        setSleepEndTime(sleepDataList.get(sleepDataList.size() - 1).getStartTime());


    }

    /**
     * 解析睡眠数据
     */
    public SleepModel(String sleep_date, String sleep_data) {
        super();
        MyLog.i(TAG, "sleep_date = " + sleep_date);
        MyLog.i(TAG, "sleep_data= " + sleep_data);

        if (JavaUtil.checkIsNull(sleep_data)) {
            return;
        }

        byte[] data = AnalyticalUtils.StringToIntSuZu(sleep_data);

        int sleepTotalMin = 0;
        int deepTotalMin = 0;
        int ligntTotalMin = 0;
        int stayupTotalMin = 0;
        int wokeCount = 0;
        int allSleepMin = 0;

        List<SleepData> sleepDataList = new ArrayList<SleepData>();

        int fast_hours = (int) (data[0] & 0xf8) >> 3;
        int fast_min = (int) (data[0] & 0x7) << 3 | (int) (data[1] & 0xE0) >> 5;


        if (fast_hours >= 23 || fast_hours < 8) {
            sleepDataList.add(new SleepData("0", "23:00"));

            stayupTotalMin = MyTime.getMyTotalTime("23:00", AnalyticalUtils.getStrTime(fast_hours, fast_min));
        }

        for (int i = 0; i < data.length; i += 2) {

            int sleep_type = data[i + 1] & 0x0F;
            int start_hours = (int) (data[i] & 0xf8) >> 3;
            int start_min = (int) (data[i] & 0x7) << 3 | (int) (data[i + 1] & 0xE0) >> 5;
            int end_hours = 0;
            int end_min = 0;

            sleepDataList.add(new SleepData(String.valueOf(sleep_type), AnalyticalUtils.getStrTime(start_hours, start_min)));


            if (i < data.length - 2) {
                end_hours = (int) (data[i + 2] & 0xf8) >> 3;
                end_min = (int) (data[i + 2] & 0x7) << 3 | (int) (data[i + 3] & 0xE0) >> 5;
            } else {
                end_hours = start_hours;
                end_min = start_min;
            }


            if (sleep_type == 0x02) { // 浅睡眠
                ligntTotalMin += MyTime.getMyTotalTime(AnalyticalUtils.getStrTime(start_hours, start_min), AnalyticalUtils.getStrTime(end_hours, end_min));
            } else if (sleep_type == 0x03) { // 熟睡
                deepTotalMin += MyTime.getMyTotalTime(AnalyticalUtils.getStrTime(start_hours, start_min), AnalyticalUtils.getStrTime(end_hours, end_min));
            } else if (sleep_type == 0x04) { // 睡醒
                wokeCount++;
            }

        }

        allSleepMin = deepTotalMin + ligntTotalMin;
        sleepTotalMin = allSleepMin + stayupTotalMin;


        setSleepDate(sleep_date);
        setSleepDeep(String.valueOf(deepTotalMin));
        setSleepLight(String.valueOf(ligntTotalMin));
        setSleepStayUpTime(String.valueOf(stayupTotalMin));
        setSleepTotalTime(String.valueOf(sleepTotalMin));
        setSleepSleepTime(String.valueOf(allSleepMin));
        setSleepWoke(String.valueOf(wokeCount));
        setSleepListData(sleepDataList);
        setSleepStartTime(sleepDataList.get(0).getStartTime());
        setSleepEndTime(sleepDataList.get(sleepDataList.size() - 1).getStartTime());


    }

    @Override
    public String toString() {
        return "SleepModel{" +
                "TAG='" + TAG + '\'' +
                ", SleepDate='" + SleepDate + '\'' +
                ", SleepStartTime='" + SleepStartTime + '\'' +
                ", SleepEndTime='" + SleepEndTime + '\'' +
                ", SleepStayUpTime='" + SleepStayUpTime + '\'' +
                ", SleepDeep='" + SleepDeep + '\'' +
                ", SleepLight='" + SleepLight + '\'' +
                ", SleepTotalTime='" + SleepTotalTime + '\'' +
                ", SleepSleepTime='" + SleepSleepTime + '\'' +
                ", SleepWoke='" + SleepWoke + '\'' +
                ", SleepSoberTime='" + SleepSoberTime + '\'' +
                ", SleepListData=" + SleepListData +
                '}';
    }
}
